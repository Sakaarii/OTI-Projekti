package oti.projekti.mokki;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class NewHousePanel extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    Text textOsoite = new Text("Mökin Osoite:");
    Text textId = new Text("Mökin ID:");
    Text textHinta = new Text("Mökin Hinta:");
    Text textKapasiteetti = new Text("Mökin max. kapasiteetti:");

    TextField textFieldOsoite = new TextField();
    TextField textFieldId = new TextField();
    TextField textFieldHinta = new TextField();
    TextField textFieldKapasiteetti = new TextField();

    @Override
    public void start(Stage primaryStage) {


        Button buttonOnly = new Button("Tallenna");

        VBox vBox = new VBox();
        VBox vBox1 = new VBox();
        HBox hBox = new HBox();

        hBox.getChildren().addAll(vBox1,vBox);

        GridPane infoGridPane = new GridPane();
        infoGridPane.setHgap(5);
        infoGridPane.setVgap(5);

        infoGridPane.add(textOsoite,0,0);
        infoGridPane.add(textHinta,0,1);
        infoGridPane.add(textId,0,2);
        infoGridPane.add(textKapasiteetti,0,3);

        infoGridPane.add(textFieldOsoite,1,0);
        infoGridPane.add(textFieldHinta,1,1);
        infoGridPane.add(textFieldId,1,2);
        infoGridPane.add(textFieldKapasiteetti,1,3);

        VBox root = new VBox(10);
        root.getChildren().add(hBox);
        root.getChildren().add(infoGridPane);
        root.getChildren().add(buttonOnly);
        root.setPadding(new Insets(10));


        //NAPPI JOKA LUO
        buttonOnly.setOnAction(actionEvent -> {

            String osoite = textFieldOsoite.getText();
            String Varaustilanne = textFieldId.getText();
            String hinta = textFieldHinta.getText();
            String kapasiteetti = textFieldKapasiteetti.getText();


            // tekee uuden mokin
            try {

                Connection conn = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);
                String sql = "INSERT INTO mokki (osoite, varaustilanne, varaushinta, kapasiteetti) VALUES (?, ?, ?, ?)";

                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, osoite);
                stmt.setString(2, Varaustilanne);
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

        });

        Scene scene = new Scene(root,300,185);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Uuden mökin tiedot");
        primaryStage.show();
    }
}
