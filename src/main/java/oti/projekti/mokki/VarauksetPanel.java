package oti.projekti.mokki;

import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class VarauksetPanel extends Application {

    String varausID;
    TextField alkupvmTextField = new TextField();
    TextField loppupvmTextField = new TextField();
    TextField kestoTextField = new TextField();
    TextField nimiTextField = new TextField();
    TextField spostiTextField = new TextField();

    public VarauksetPanel(String varausID) {
        this.varausID = varausID;
    }

    GridPane varausGridPane = new GridPane();
    Scene varauksetScene = new Scene(varausGridPane, 325, 220);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        SQLmethod();

        Button buttonPoistaVaraus = new Button("Poista Varaus");

        buttonPoistaVaraus.setOnAction(actionEvent -> {
            poistaVaraus();
            TiedotPanel.resetListView();
            primaryStage.close();
        });

        varausGridPane.setHgap(5);
        varausGridPane.setVgap(5);

        Label alkupvmLabel = new Label("Alku pvm:");
        Label loppupvmLabel = new Label("Loppu pvm:");
        Label kestoLabel = new Label("Kesto (pv):");
        Label nimiLabel = new Label("Varaajan nimi:");
        Label spostiLabel = new Label("Varaajan Sähköposti:");

        alkupvmTextField.setEditable(false);
        loppupvmTextField.setEditable(false);
        kestoTextField.setEditable(false);
        nimiTextField.setEditable(false);
        spostiTextField.setEditable(false);

        varausGridPane.add(alkupvmLabel, 0, 0);
        varausGridPane.add(alkupvmTextField, 1, 0);
        varausGridPane.add(loppupvmLabel, 0, 1);
        varausGridPane.add(loppupvmTextField, 1, 1);
        varausGridPane.add(kestoLabel, 0, 2);
        varausGridPane.add(kestoTextField, 1, 2);
        varausGridPane.add(nimiLabel, 0, 3);
        varausGridPane.add(nimiTextField, 1, 3);
        varausGridPane.add(spostiLabel, 0, 4);
        varausGridPane.add(spostiTextField, 1, 4);
        varausGridPane.add(buttonPoistaVaraus,0,5);

        varausGridPane.setPadding(new Insets(15));
        varausGridPane.setHgap(10);
        varausGridPane.setVgap(10);

        primaryStage.setScene(varauksetScene);
        primaryStage.setTitle("Varauksen tiedot");
        primaryStage.show();
    }

    private void SQLmethod() {

        SQLDriver sqlDriver = new SQLDriver(MainWindow.connection, MainWindow.userName, MainWindow.userPassword);

        String sqlQuery = "SELECT varaus.varauksen_alkamispaiva, varaus.varauksen_paattymispaiva, " +
                "varaus.varauksen_kesto, asiakas.nimi, asiakas.sahkoposti " +
                "FROM varaus " +
                "JOIN asiakas ON varaus.asiakasID = asiakas.asiakasID " +
                "where varaus.varauksen_tunniste =" + varausID;

        ArrayList<ArrayList<String>> tiedot = sqlDriver.returnAllQuery(sqlQuery);

        alkupvmTextField.setText(tiedot.get(0).get(0));
        loppupvmTextField.setText(tiedot.get(0).get(1));
        kestoTextField.setText(tiedot.get(0).get(2));
        nimiTextField.setText(tiedot.get(0).get(3));
        spostiTextField.setText(tiedot.get(0).get(4));

    }

    private void poistaVaraus() {
        try {

            Connection conn = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);
            String sql = "DELETE FROM varaus WHERE varauksen_tunniste = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, varausID);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

            MainWindow.checkReservedSituation();
            MainWindow.resetList();
            //MainWindow.listView.getItems().add(stringy);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}

