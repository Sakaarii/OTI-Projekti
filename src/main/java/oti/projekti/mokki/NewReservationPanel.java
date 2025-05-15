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

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class NewReservationPanel extends Application {

    Text textMokkiTunnus = new Text("Mökin ID:");
    Text textEtunimi = new Text("Etunimi:");
    Text textSukunimi = new Text("Sukunimi:");
    Text textSposti = new Text("Sähköposti:");
    Text textTilinumero = new Text("Tilinumero:");

    TextField textFieldMokkiTunnus = new TextField();
    TextField textFieldEtunimi = new TextField();
    TextField textFieldSukunimi = new TextField();
    TextField textFieldSposti = new TextField();
    TextField textFieldTilinumero = new TextField();

    DatePicker datePickerStart = new DatePicker();
    DatePicker datePickerEnd = new DatePicker();

    static int x = -1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        datePickerStart.setMaxWidth(150);
        datePickerEnd.setMaxWidth(150);

        Button buttonOnly = new Button("Tallenna");

        Text startDateText = new Text("Alku:");
        Text endDateText = new Text("Loppu:");

        RadioButton buttonNew = new RadioButton("Uusi Asiakas");
        RadioButton buttonOld = new RadioButton("Vanha asiakas");

        HBox hBoxRadioButtons1 = new HBox(10);
        hBoxRadioButtons1.getChildren().addAll(buttonNew,buttonOld);

        ToggleGroup radioButtonToggle1 = new ToggleGroup();
        radioButtonToggle1.getToggles().addAll(buttonNew,buttonOld);

        VBox vBox = new VBox(5);
        VBox vBox1 = new VBox(15);
        vBox1.setPadding(new Insets(3,0,0,0));
        HBox hBox = new HBox(30);

        vBox1.getChildren().addAll(startDateText,endDateText);
        vBox.getChildren().addAll(datePickerStart,datePickerEnd);
        hBox.getChildren().addAll(vBox1,vBox);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.add(textEtunimi,0,1);
        gridPane.add(textSukunimi,0,2);
        gridPane.add(textSposti,0,3);
        gridPane.add(textTilinumero,0,4);
        gridPane.add(textMokkiTunnus,0,0);

        gridPane.add(textFieldEtunimi,1,1);
        gridPane.add(textFieldSukunimi,1,2);
        gridPane.add(textFieldSposti,1,3);
        gridPane.add(textFieldTilinumero,1,4);
        gridPane.add(textFieldMokkiTunnus,1,0);

        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.getChildren().add(hBoxRadioButtons1);
        root.getChildren().add(new Separator());
        root.getChildren().add(hBox);
        root.getChildren().add(new Separator());
        root.getChildren().add(gridPane);
        root.getChildren().add(new Separator());
        root.getChildren().add(buttonOnly);

        buttonOld.setOnAction(event -> {
                textTilinumero.setVisible(false);
                textFieldTilinumero.setVisible(false);
        });

        buttonNew.setOnAction(event -> {
            textTilinumero.setVisible(true);
            textFieldTilinumero.setVisible(true);
        });

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

            //tarkistaa että tekstikentät eivät ole tyhjät, mutta mökintunnus voi vielä olla
            if (etunimi.isEmpty() || sukunimi.isEmpty() || sahkoposti.isEmpty() || (tilinumero.isEmpty() && buttonNew.isSelected())
                    || startDate == null || endDate == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Tekstikentät ei voi olla tyhjiä");
                alert.showAndWait();
                return;
            }

            if (startDate.isAfter(endDate)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Alkupäivämäärä ei voi olla loppupäivämäärän jälkeen");
                alert.showAndWait();
                return;
            }

            if (startDate.isBefore(LocalDate.now())){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Alkupäivämäärä ei voi olla menneisyydessä");
                alert.showAndWait();
                return;
            }

            if (checkForOverlapping(mokkiID)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Päällekkäisyys toisen varauksen kanssa");
                alert.showAndWait();
                return;
            }


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

                MainWindow.checkReservedSituation();
                MainWindow.resetList();
                //MainWindow.listView.getItems().add(stringy);

            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }

            //ikkuna sulkeutuu kun painaa "Tallenna nappia"

            primaryStage.close();

        });

        Scene scene = new Scene(root,250,370);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Uusi varaus");
        primaryStage.show();
    }

    private boolean checkForOverlapping(int mokkiID){

        String query = "SELECT varauksen_tunniste, varauksen_alkamispaiva, varauksen_paattymispaiva, mokin_tunniste from varaus";

        try {

            Connection conn = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);

            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()){
                int checkMokkiID = resultSet.getInt("mokin_tunniste");
                LocalDate alkuPaiva = LocalDate.parse(resultSet.getString("varauksen_alkamispaiva"));
                LocalDate loppuPaiva = LocalDate.parse(resultSet.getString("varauksen_paattymispaiva"));

                LocalDate alkuPaivaNew = datePickerStart.getValue();
                LocalDate loppuPaivaNew = datePickerEnd.getValue();

                if ( checkMokkiID == mokkiID ){ // se varauksen mökki id

                    if (alkuPaivaNew.isBefore(alkuPaiva) && loppuPaivaNew.isAfter(alkuPaiva)){ // Loppupäivä keskellä varausta
                        return true;
                    }
                    else if (alkuPaivaNew.isBefore(loppuPaiva) && loppuPaivaNew.isAfter(alkuPaiva)){ // Alkupäivä keskellä varausta
                        return true;
                    }
                    else if ((alkuPaivaNew.isAfter(alkuPaiva) && alkuPaivaNew.isBefore(loppuPaiva)) || (loppuPaivaNew.isAfter(alkuPaiva) && loppuPaivaNew.isBefore(loppuPaiva))) { // "Varaus syö varauksen"
                        return true;
                    }
                }
            }

            stmt.close();
            conn.close();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }
}
