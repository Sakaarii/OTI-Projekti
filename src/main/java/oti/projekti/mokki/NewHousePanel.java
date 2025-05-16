package oti.projekti.mokki;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class NewHousePanel extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    Text textOsoite = new Text("Mökin Osoite:");
    Text textHinta = new Text("Mökin Hinta:");
    Text textKapasiteetti = new Text("Mökin max. kapasiteetti:");

    TextField textFieldOsoite = new TextField();
    TextField textFieldHinta = new TextField();
    TextField textFieldKapasiteetti = new TextField();

    @Override
    public void start(Stage primaryStage) {


        Button buttonOnly = new Button("Tallenna");

        GridPane infoGridPane = new GridPane();
        infoGridPane.setHgap(5);
        infoGridPane.setVgap(5);

        infoGridPane.add(textOsoite,0,0);
        infoGridPane.add(textHinta,0,1);
        infoGridPane.add(textKapasiteetti,0,2);

        infoGridPane.add(textFieldOsoite,1,0);
        infoGridPane.add(textFieldHinta,1,1);
        infoGridPane.add(textFieldKapasiteetti,1,2);

        VBox root = new VBox(10);
        root.getChildren().add(infoGridPane);
        root.getChildren().add(new Separator());
        root.getChildren().add(buttonOnly);
        root.setPadding(new Insets(15));


        //NAPPI JOKA LUO
        buttonOnly.setOnAction(actionEvent -> {

            String osoite = textFieldOsoite.getText();
            String varaustilanne = "vapaa";
            String hinta = textFieldHinta.getText();
            String kapasiteetti = textFieldKapasiteetti.getText();

            //tarkastaa että tekstikentät eivät ole tyhjiä
            if (osoite.isEmpty() || hinta.isEmpty() || varaustilanne.isEmpty() || kapasiteetti.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Tekstikentät ei voi olla tyhjiä");
                alert.showAndWait();
                return;
            }

            if (checkForOverlapping(osoite)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Osoitteen omaava mökki on jo olemassa");
                alert.showAndWait();
                return;
            }


            // tekee uuden mokin
            try {

                Connection conn = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);
                String sql = "INSERT INTO mokki (osoite, varaustilanne, varaushinta, kapasiteetti) VALUES (?, ?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, osoite);
                stmt.setString(2, varaustilanne);
                stmt.setString(3, hinta);
                stmt.setString(4, kapasiteetti);
                stmt.executeUpdate();

                stmt.close();
                conn.close();

                MainWindow.resetList();
                //MainWindow.listView.getItems().add(stringy);

            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }

            primaryStage.close();

        });

        Scene scene = new Scene(root,300,190);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Uuden mökin tiedot");
        primaryStage.show();
    }

    private boolean checkForOverlapping(String osoite){

        String query = "SELECT osoite from mokki";

        try {

            Connection conn = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);

            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()){

                String checkOsoite = resultSet.getString("osoite");

                if ( checkOsoite.equalsIgnoreCase(osoite) ){ // Katsoo kaikki mökit läpi, että ei ole päällekkäisyyksiä luonnissa

                    return true;
                }

                return false;
            }

            stmt.close();
            conn.close();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }

}
