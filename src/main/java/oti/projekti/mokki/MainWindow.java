package oti.projekti.mokki;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    static Stage secondaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        listView.setEditable(false);
        resetList();

        Text titleText = new Text("Mökkijärjestelmä");
        titleText.setFont(Font.font("Arial", 25));

        Button buttonRefreshList = new Button("Päivitä Lista");
        Button buttonCustomer = new Button("Asiakkaat");
        Button buttonNewHouse = new Button("Uusi Mökki");
        Button buttonNewReservation = new Button("Uusi Varaus");

        Rectangle titleRectangle = new Rectangle(0, 0, 200, 50);
        titleRectangle.setFill(Color.TRANSPARENT);
        titleRectangle.setStroke(Color.BLACK);

        StackPane stackPaneTitle = new StackPane();
        stackPaneTitle.getChildren().addAll(titleRectangle, titleText);

        HBox hBoxNavigationButtons = new HBox(20);
        hBoxNavigationButtons.getChildren().addAll( buttonCustomer, buttonRefreshList);
        hBoxNavigationButtons.setAlignment(Pos.CENTER);

        VBox vBoxTopSection = new VBox(10);
        vBoxTopSection.getChildren().addAll(stackPaneTitle, hBoxNavigationButtons);
        vBoxTopSection.setPadding(new Insets(10, 10, 0, 10));
        vBoxTopSection.setAlignment(Pos.CENTER);

        VBox vBoxCenterSection = new VBox(10);
        vBoxCenterSection.getChildren().add(listView);
        vBoxCenterSection.setAlignment(Pos.CENTER);
        vBoxCenterSection.setPadding(new Insets(20, 20, 20, 20));

        HBox hBoxButtonSection = new HBox(20);
        hBoxButtonSection.getChildren().addAll(buttonNewHouse, buttonNewReservation);
        hBoxButtonSection.setPadding(new Insets(0, 10, 20, 20));
        hBoxButtonSection.setAlignment(Pos.CENTER_LEFT);

        BorderPane rootPane = new BorderPane();
        rootPane.setTop(vBoxTopSection);
        rootPane.setCenter(vBoxCenterSection);
        rootPane.setBottom(hBoxButtonSection);

        Scene scene = new Scene(rootPane, 500, 500);
        primaryStage.setTitle("Mökkivarausjärjestelmä");
        primaryStage.setScene(scene);
        primaryStage.show();


        // UUSIEN TASOJEN AVAAMISNAPIT
        buttonNewHouse.setOnAction(actionEvent -> {
            secondaryStage = new Stage();
            NewHousePanel createHouse = new NewHousePanel();
            createHouse.start(secondaryStage);
        });

        buttonCustomer.setOnAction(actionEvent -> {
            secondaryStage = new Stage();
            CustomersPanel customersPanel = new CustomersPanel();
            customersPanel.start(secondaryStage);
        });

        buttonNewReservation.setOnAction(actionEvent -> {
            secondaryStage = new Stage();
            NewReservationPanel newReservationPanel = new NewReservationPanel();
            newReservationPanel.start(secondaryStage);
        });

        buttonRefreshList.setOnAction((actionEvent -> resetList()));

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
