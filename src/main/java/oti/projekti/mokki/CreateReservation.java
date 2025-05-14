package oti.projekti.mokki;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class CreateReservation extends Application {

    Text textMokkiTunnus = new Text("Mökin Tunnus");
    Text textEtunimi = new Text("Etunimi");
    Text textSukunimi = new Text("Sukunimi");
    Text textSposti = new Text("Sähköposti");
    Text textTilinumero = new Text("Tilinumero");

    TextField textFieldMokkiTunnus = new TextField();
    TextField textFieldEtunimi = new TextField();
    TextField textFieldSukunimi = new TextField();
    TextField textFieldSposti = new TextField();
    TextField textFieldTilinumero = new TextField();

    static int x = -1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        DatePicker datePickerStart = new DatePicker();
        DatePicker datePickerEnd = new DatePicker();

        Button buttonOnly = new Button("Create");

        Text text1 = new Text("Start Date:");
        Text text2 = new Text("End Date:");

        RadioButton buttonNew = new RadioButton("Uusi Asiakas");
        RadioButton buttonOld = new RadioButton("Vanha asiakas");

        HBox hBoxRadioButtons1 = new HBox(10);
        hBoxRadioButtons1.getChildren().addAll(buttonNew,buttonOld);

        ToggleGroup radioButtonToggle1 = new ToggleGroup();
        radioButtonToggle1.getToggles().addAll(buttonNew,buttonOld);

        RadioButton buttonCustomer = new RadioButton("Asiakas");
        RadioButton buttonBusiness = new RadioButton("Yritys");
        HBox hBoxRadioButtons2 = new HBox(10);
        hBoxRadioButtons2.getChildren().addAll(buttonCustomer,buttonBusiness);

        ToggleGroup radioButtonToggle2 = new ToggleGroup();
        radioButtonToggle2.getToggles().addAll(buttonBusiness,buttonCustomer);

        VBox vBox = new VBox();
        VBox vBox1 = new VBox();
        HBox hBox = new HBox();

        vBox1.getChildren().addAll(text1,text2);
        vBox.getChildren().addAll(datePickerStart,datePickerEnd);
        hBox.getChildren().addAll(vBox1,vBox);

        GridPane gridPane = new GridPane();
        gridPane.add(textEtunimi,0,0);
        gridPane.add(textSukunimi,0,1);
        gridPane.add(textSposti,0,2);
        gridPane.add(textTilinumero,0,3);
        gridPane.add(textMokkiTunnus,0,4);

        gridPane.add(textFieldEtunimi,1,0);
        gridPane.add(textFieldSukunimi,1,1);
        gridPane.add(textFieldSposti,1,2);
        gridPane.add(textFieldTilinumero,1,3);
        gridPane.add(textFieldMokkiTunnus,1,4);

        VBox root = new VBox(10);
        root.getChildren().add(hBox);
        root.getChildren().add(hBoxRadioButtons1);
        root.getChildren().add(hBoxRadioButtons2);
        root.getChildren().add(gridPane);
        root.getChildren().add(buttonOnly);

        buttonOnly.setOnAction(actionEvent -> {

            int mokkiID = Integer.parseInt(textFieldMokkiTunnus.getText());
            String etunimi = textFieldEtunimi.getText();
            String sukunimi = textFieldSukunimi.getText();
            String nimi = etunimi + " " + sukunimi;
            String sahkoposti = textFieldSposti.getText();
            String tilinumero = textFieldTilinumero.getText();
            LocalDate startDate = datePickerStart.getValue();
            LocalDate endDate = datePickerEnd.getValue();
            long length = ChronoUnit.DAYS.between(startDate,endDate);

            //tekee uuden asiakkaan. Jos ei paina uusi asiakas painiketta, ei yritä luoda uutta asiakasta, vaan hakee vaan asiakkaan databasesta
            if (buttonNew.isSelected()){
                try {
                    Connection connection = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);
                    String sqlAsiakas = "INSERT INTO asiakas(nimi,asiakastunnus,sahkoposti,tilinumero) VALUES (?, ?, ?, ?)";

                    PreparedStatement stmt = connection.prepareStatement(sqlAsiakas);

                    stmt.setString(1, nimi);
                    stmt.setString(2, "xxx");
                    stmt.setString(3, sahkoposti);
                    stmt.setString(4,tilinumero);

                    stmt.executeUpdate();

                    stmt.close();
                    connection.close();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            // nimi ja sähköposti on tällä hetkellä miten erittelee asiakkaat

            SQLDriver sqlDriver = new SQLDriver(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);
            ArrayList<ArrayList<String>> listOfIDs = sqlDriver.returnAllQuery( "SELECT asiakasId FROM asiakas WHERE nimi='" + nimi + "' AND sahkoposti='" + sahkoposti + "'");
            x = Integer.parseInt(listOfIDs.get(0).get(0));


            // tekee uuden varauksen
            try {

                Connection conn = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);
                String sql = "INSERT INTO varaus(varauksen_alkamispaiva, varauksen_paattymispaiva, varauksen_kesto, mokin_tunniste, asiakasID) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, startDate.toString());
                stmt.setString(2, endDate.toString());
                stmt.setInt(3, (int) length);
                stmt.setInt(4, mokkiID);
                stmt.setInt(5, x);
                stmt.executeUpdate();

                stmt.close();
                conn.close();

                MainWindow.resetList();
                //MainWindow.listView.getItems().add(stringy);

            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        Scene scene = new Scene(root,300,300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
