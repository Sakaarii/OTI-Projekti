package oti.projekti.mokki;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.InstantSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

// KÄYNNISTÄ MAINISTA
// AINA

public class MainWindow extends Application {

    // Yhteydet
    static String connection = "jdbc:mysql://localhost:3306/mokkikodit";
    static String userName = "root";
    static String userPassword = "Tammikuu2024";

    static ListView<Object> listView = new ListView<>();
    int x = 0;
    static Button delete;
    static Stage secondaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        listView.setEditable(false);

        resetList();

        Text text = new Text("Mökkijärjestelmä");
        text.setFont(Font.font("Arial",25));

        Button buttonNewReservation = new Button("Uusi Varaus");

        Button buttonNewHouse = new Button("Create new");
        /*
        Here goes the call to open the new window which is used to create a new mökki.
         */

        Button buttonMain = new Button("Pääikkuna");

        Button buttonCustomer = new Button("Asiakkaat");

        Rectangle rectangle1 = new Rectangle(0,0,200,50);
        rectangle1.setFill(Color.TRANSPARENT);
        rectangle1.setStroke(Color.BLACK);

        Rectangle rectangle2 = new Rectangle(0,0,100,100);
        rectangle2.setFill(Color.TRANSPARENT);
        rectangle2.setStroke(Color.BLACK);

        Rectangle rectangle3 = new Rectangle(0,0,100,100);
        rectangle2.setFill(Color.TRANSPARENT);
        rectangle2.setStroke(Color.BLACK);

        primaryStage.setTitle("Title");

        VBox vBoxCenterRoot = new VBox(10);
        vBoxCenterRoot.getChildren().add(listView);
        vBoxCenterRoot.setAlignment(Pos.CENTER);
        vBoxCenterRoot.setPadding(new Insets(20,20,20,20));

        StackPane stackPaneTop = new StackPane();
        stackPaneTop.getChildren().addAll(text,rectangle1);

        HBox hBoxMains = new HBox();
        hBoxMains.getChildren().addAll(buttonMain,buttonCustomer);
        hBoxMains.setAlignment(Pos.CENTER);

        VBox vBoxTopRoot = new VBox();
        vBoxTopRoot.getChildren().addAll(stackPaneTop,hBoxMains);
        vBoxTopRoot.setPadding(new Insets(10,10,10,10));

        StackPane stackPaneRightRoot = new StackPane();
        stackPaneRightRoot.getChildren().addAll(rectangle2,buttonNewHouse);
        stackPaneRightRoot.setPadding(new Insets(10,10,10,10));

        StackPane stackPaneRightRoot2 = new StackPane();
        stackPaneRightRoot2.getChildren().addAll(rectangle3,buttonNewReservation);
        stackPaneRightRoot2.setPadding(new Insets(10,10,10,10));

        VBox vBoxRightRoot = new VBox();
        vBoxRightRoot.getChildren().addAll(stackPaneRightRoot,stackPaneRightRoot2);

        BorderPane root = new BorderPane();
        root.setTop((vBoxTopRoot));
        root.setCenter(vBoxCenterRoot);
        root.setRight(vBoxRightRoot);

        primaryStage.setTitle("Title");
        Scene scene = new Scene(root,500,500);
        primaryStage.setScene(scene);
        primaryStage.show();


        // UUSIEN TASOJEN AVAAMISNAPIT
        buttonNewHouse.setOnAction(actionEvent -> {
            secondaryStage = new Stage();
            CreateNewHouse createHouse = new CreateNewHouse();
            createHouse.start(secondaryStage);
        });

        buttonCustomer.setOnAction(actionEvent -> {
            secondaryStage = new Stage();
            Customers customers = new Customers();
            customers.start(secondaryStage);
        });

        buttonNewReservation.setOnAction(actionEvent -> {
            secondaryStage = new Stage();
            CreateReservation createReservation = new CreateReservation();
            createReservation.start(secondaryStage);
        });

    }

    // hakee SQL databasesta mökit ja laittaa ne listViewiin.
    public static void resetList(){

        listView.getItems().clear();

        SQLDriver sqlDriver = new SQLDriver(connection,userName,userPassword);

        Map<String, ArrayList<String>> lista = sqlDriver.tableQuery("Select * from mokki","mokin_tunniste",new String[]{"osoite", "varaustilanne", "varaushinta", "kapasiteetti"});

        for (Map.Entry<String, ArrayList<String>> i:lista.entrySet()){
            String key = i.getKey();
            ArrayList<String> values = i.getValue();

            String formatted = String.format(
                    "Mökin tunnus: %s\nOsoite: %s\nVaraustilanne: %s\nHinta: %s\nKapasiteetti: %s",
                    key,
                    values.get(0),
                    values.get(1),
                    values.get(2),
                    values.get(3)
            );
            //listView.getItems().add(formatted);

            Button buttonView = new Button("Info");

            Text textLine = new Text(formatted);
            HBox hboxLine = new HBox(20);

            hboxLine.getChildren().addAll(textLine,buttonView);

            //listView.getItems().add(buttonView);
            listView.getItems().add(hboxLine);

            buttonView.setOnAction(actionEvent -> {

                System.out.println("\n"+formatted);
                secondaryStage = new Stage();
                TiedotPanel tiedotPanel = new TiedotPanel(key,
                        values.get(0),
                        values.get(1),
                        values.get(2),
                        values.get(3)
                );
                tiedotPanel.start(secondaryStage);

            });
            //listView.getItems().add(key + "\t" + values.get(0) + values.get(1));
        }
    }
}

/*
SQLDriver sqlDriver = new SQLDriver("jdbc:mysql://127.0.0.1:3306/mokkikodit","root","Tammikuu2024");

        Map<String, ArrayList<String>> lista = sqlDriver.tableQuery("Select * from mokki","mokin_tunniste",new String[]{"osoite", "varaustilanne", "varaushinta", "kapasiteetti"});

        for (Map.Entry<String, ArrayList<String>> i:lista.entrySet()){
            String key = i.getKey();
            ArrayList<String> values = i.getValue();

            String formatted = String.format(
                    "Mökin tunnus: %s\nOsoite: %s\nVaraustilanne: %s\nHinta: %s\nKapasiteetti: %s",
                    key,
                    values.get(0),
                    values.get(1),
                    values.get(2),
                    values.get(3)
            );
            //listView.getItems().add(formatted);

            Button buttonView = new Button("Info");

            Text textLine = new Text(formatted);
            HBox hboxLine = new HBox(20);

            hboxLine.getChildren().addAll(textLine,buttonView);

            //listView.getItems().add(buttonView);
            listView.getItems().add(hboxLine);

            buttonView.setOnAction(actionEvent -> {

                System.out.println("\n"+formatted);
                secondaryStage = new Stage();
                TiedotPanel tiedotPanel = new TiedotPanel(key,
                        values.get(0),
                        values.get(1),
                        values.get(2),
                        values.get(3)
                );
                tiedotPanel.start(secondaryStage);

            });
            //listView.getItems().add(key + "\t" + values.get(0) + values.get(1));
        }
 */
