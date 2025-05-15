package oti.projekti.mokki;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    static Stage secondaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        checkReservedSituation();

        listView.setEditable(false);
        resetList();

        Text titleText = new Text("Mökkijärjestelmä");
        titleText.setFont(Font.font("Consolas", FontWeight.BOLD, 25));

        Separator titleSeparator = new Separator();
        titleSeparator.setMaxWidth(200);

        Button buttonRefreshList = new Button("Päivitä Lista");
        Button buttonCustomer = new Button("Asiakkaat");
        Button buttonNewHouse = new Button("Uusi Mökki");
        Button buttonNewReservation = new Button("Uusi Varaus");

        HBox hBoxNavigationButtons = new HBox(20);
        hBoxNavigationButtons.getChildren().addAll( buttonCustomer, buttonRefreshList);
        hBoxNavigationButtons.setAlignment(Pos.CENTER);

        VBox vBoxTopSection = new VBox(10);
        vBoxTopSection.getChildren().addAll(titleText, titleSeparator, hBoxNavigationButtons);
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

    public static void checkReservedSituation() {

        SQLDriver sqlDriver = new SQLDriver(MainWindow.connection, MainWindow.userName, MainWindow.userPassword);
        String table = "mokki";
        sqlDriver.updateTable(table,"varaustilanne = 'vapaa'","1=1");

        String query = "SELECT varauksen_tunniste, varauksen_alkamispaiva, varauksen_paattymispaiva, mokin_tunniste from varaus";

        LocalDate currentPaiva = LocalDate.now();

        try {

            Connection conn = DriverManager.getConnection(connection, userName, userPassword);

            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet resultSet = stmt.executeQuery();


            while (resultSet.next()) {

                //int varausID = resultSet.getInt("varauksen_tunniste");
                int mokkiID = resultSet.getInt("mokin_tunniste");

                String where = "mokin_tunniste = " + mokkiID;

                LocalDate alkuPaiva = LocalDate.parse(resultSet.getString("varauksen_alkamispaiva"));
                LocalDate loppuPaiva = LocalDate.parse(resultSet.getString("varauksen_paattymispaiva"));

                if (currentPaiva.isBefore(loppuPaiva) && (currentPaiva.isAfter(alkuPaiva) || currentPaiva.isEqual(alkuPaiva))){
                    sqlDriver.updateTable(table,"varaustilanne = 'varattu'",where);
                }
                else{
                    sqlDriver.updateTable(table,"varaustilanne = 'vapaa'",where);
                }

            }

            stmt.close();
            conn.close();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}


