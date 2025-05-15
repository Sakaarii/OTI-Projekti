package oti.projekti.mokki;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Map;

public class TiedotPanel extends Application {

    String osoite,varaustilanne,kapasiteetti,hinta;
    static String mokkiID;
    static ListView varausListView = new ListView<>();

    public TiedotPanel(String mokkiID, String osoite, String varaustilanne, String hinta, String kapasiteetti){
        this.mokkiID=mokkiID;
        this.osoite=osoite;
        this.kapasiteetti=kapasiteetti;
        this.varaustilanne=varaustilanne;
        this.hinta=hinta;
    }

    VBox mainVBox = new VBox(20);
    Scene tiedotScene = new Scene(mainVBox, 450, 650);

    ToggleButton muokkaaButton = new ToggleButton("Muokkaa");
    Button tallennaButton = new Button("Tallenna");
    Button peruutaButton = new Button("Peruuta");
    Button poistaButton = new Button("Poista Mökki");

    HBox buttonHBox = new HBox(15);

    TextField mokkinroTextField = new TextField();
    TextField osoiteTextField = new TextField();
    TextField kapasiteettiTextField = new TextField();
    TextField varaustilanneTextField = new TextField();
    TextField hintaTextField = new TextField();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        mokkinroTextField.setText(mokkiID);
        osoiteTextField.setText(osoite);
        kapasiteettiTextField.setText(kapasiteetti);
        varaustilanneTextField.setText(varaustilanne);
        hintaTextField.setText(hinta);

        resetListView();

        mainVBox.setPadding(new Insets(15, 15, 0, 15));

        GridPane tietoGridPane = createTiedotSection();

        VBox varauksetSection = createVarauksetSection();

        buttonHBox.getChildren().addAll(tallennaButton, peruutaButton);

        mainVBox.getChildren().addAll(tietoGridPane, varauksetSection, new Separator(), buttonHBox);

        muokkaaButton.setOnAction(event -> muokkaaAction());

        tallennaButton.setOnAction(actionEvent -> muokkaaTietoja());

        poistaButton.setOnAction(actionEvent -> {
            poistaMokki();
            primaryStage.close();
        });

        peruutaButton.setOnAction(actionEvent -> {
            mokkinroTextField.setEditable(false);
            osoiteTextField.setEditable(false);
            kapasiteettiTextField.setEditable(false);
            varaustilanneTextField.setEditable(false);
            hintaTextField.setEditable(false);

            mokkinroTextField.setText(mokkiID);
            osoiteTextField.setText(osoite);
            kapasiteettiTextField.setText(kapasiteetti);
            varaustilanneTextField.setText(varaustilanne);
            hintaTextField.setText(hinta);
        });

        primaryStage.setScene(tiedotScene);
        primaryStage.setTitle("Tiedot");
        primaryStage.show();

    }

    //Creates a gridpane that shows all the information
    private GridPane createTiedotSection() {
        GridPane tietoGridPane = new GridPane();
        tietoGridPane.setHgap(5);
        tietoGridPane.setVgap(5);

        Label mokkinroLabel = new Label("Mökkinro:");
        Label osoiteLabel = new Label("Osoite:");
        Label kapasiteettiLabel = new Label("Kapasiteetti:");
        Label varaustilanneLabel = new Label("Varaustilanne:");
        Label hintaLabel = new Label("Hinta (€):");
        Label tiedotLabel = new Label("Tiedot:");

        tietoGridPane.add(tiedotLabel, 0, 0);
        tietoGridPane.add(mokkinroLabel, 0, 1);
        tietoGridPane.add(mokkinroTextField, 1, 1);
        tietoGridPane.add(osoiteLabel, 0, 2);
        tietoGridPane.add(osoiteTextField, 1, 2);
        tietoGridPane.add(kapasiteettiLabel, 0, 3);
        tietoGridPane.add(kapasiteettiTextField, 1, 3);
        tietoGridPane.add(varaustilanneLabel, 0, 4);
        tietoGridPane.add(varaustilanneTextField, 1, 4);
        tietoGridPane.add(hintaLabel, 0, 5);
        tietoGridPane.add(hintaTextField, 1, 5);
        tietoGridPane.add(muokkaaButton, 1, 6);
        tietoGridPane.add(poistaButton,2,6);

        mokkinroTextField.setEditable(false);
        osoiteTextField.setEditable(false);
        kapasiteettiTextField.setEditable(false);
        varaustilanneTextField.setEditable(false);
        hintaTextField.setEditable(false);

        tiedotLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        return tietoGridPane;
    }

    //Creates the varaukset section of the panel.
    private VBox createVarauksetSection() {
        VBox reservationsBox = new VBox(10);
        reservationsBox.setPadding(new Insets(10, 0, 0, 0));

        Label reservationsTitle = new Label("Varaukset:");
        reservationsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        reservationsBox.getChildren().add(reservationsTitle);

        varausListView.setMaxSize(600, 300);

        reservationsBox.getChildren().add(varausListView);
        return reservationsBox;
    }

    //Muokkaa button action
    private void muokkaaAction(){
        if (muokkaaButton.isSelected()){
            osoiteTextField.setEditable(true);
            kapasiteettiTextField.setEditable(true);
            varaustilanneTextField.setEditable(true);
            hintaTextField.setEditable(true);
        } else {
            osoiteTextField.setEditable(false);
            kapasiteettiTextField.setEditable(false);
            varaustilanneTextField.setEditable(false);
            hintaTextField.setEditable(false);
        }
    }

    // Tallenna nappia painaessa
    private void muokkaaTietoja(){
        SQLDriver sqlDriver = new SQLDriver(MainWindow.connection, MainWindow.userName, MainWindow.userPassword);

        String table = "mokki";
        String newOsoite = osoiteTextField.getText();
        String newKapasiteetti = kapasiteettiTextField.getText();
        String newVaraustilanne = varaustilanneTextField.getText();
        String newHinta = hintaTextField.getText();

        //Tarkastaa että tekstikentät eivät ole tyhjiä
        if (newOsoite.isEmpty() || newKapasiteetti.isEmpty() || newVaraustilanne.isEmpty() || newHinta.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Tekstikentät ei voi olla tyhjiä");
            alert.showAndWait();
            return;
        }

        String updatedInfo = String.format(
                "osoite='%s', kapasiteetti=%s, varaustilanne='%s', varaushinta=%s",
                newOsoite, newKapasiteetti, newVaraustilanne, newHinta
        );
        String where = "mokin_tunniste = " + mokkiID;

        //        driver.updateTable("customer", "first_name = \"ADAM\"", "first_name = \"BETTY\"");
        sqlDriver.updateTable(table,updatedInfo,where);

        MainWindow.resetList();
    }

    // poistaa mökin, sulkee TiedotPanel ikkunan

    private void poistaMokki(){
        try {

            Connection conn = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);
            String sql = "DELETE FROM mokki WHERE mokin_tunniste = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, mokkiID);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

            MainWindow.resetList();
            //MainWindow.listView.getItems().add(stringy);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    public static void resetListView(){

        varausListView.getItems().clear();

        SQLDriver sqlDriver = new SQLDriver(MainWindow.connection, MainWindow.userName, MainWindow.userPassword);
        Map<String, ArrayList<String>> kaikkiVaraukset = sqlDriver.tableQuery("SELECT * FROM varaus WHERE mokin_tunniste ='" + mokkiID + "'", "varauksen_tunniste", new String[]{"varauksen_alkamispaiva", "varauksen_paattymispaiva", "varauksen_kesto"});


        for (Map.Entry<String, ArrayList<String>> varaus : kaikkiVaraukset.entrySet()) {
            String avain = varaus.getKey();
            ArrayList<String> arvot = varaus.getValue();

            String alustettu = String.format(
                    "ID: %s\tAlku: %s\tLoppu: %s\tKesto: %s",
                    avain,
                    arvot.get(0),
                    arvot.get(1),
                    arvot.get(2)
            );

            Button buttonVarausInfo = new Button("Info");
            Text textVaraus = new Text(alustettu);
            HBox hBoxLine = new HBox(20);
            hBoxLine.getChildren().addAll(textVaraus,buttonVarausInfo);

            varausListView.getItems().add(hBoxLine);

            buttonVarausInfo.setOnAction(actionEvent -> {
                System.out.println("\n"+alustettu);
                VarauksetPanel varauksetPanel = new VarauksetPanel(avain);
                Stage secondaryStage = new Stage();
                varauksetPanel.start(secondaryStage);
            });

        }
    }

}

/*
SQLDriver sqlDriver = new SQLDriver("jdbc:mysql://127.0.0.1:3306/mokkikodit", "root", "");
        Map<String, ArrayList<String>> kaikkiVaraukset = sqlDriver.tableQuery("SELECT * FROM varaus WHERE mokin_tunniste ='" + mokkiID + "'", "varauksen_tunniste", new String[]{"varauksen_alkamispaiva", "varauksen_paattymispaiva", "varauksen_kesto"});


        for (Map.Entry<String, ArrayList<String>> varaus : kaikkiVaraukset.entrySet()) {
            String avain = varaus.getKey();
            ArrayList<String> arvot = varaus.getValue();

            String alustettu = String.format(
                    "ID: %s\tAlku: %s\tLoppu: %s\tKesto: %s",
                    avain,
                    arvot.get(0),
                    arvot.get(1),
                    arvot.get(2)
            );

            Button buttonVarausInfo = new Button("Info");
            Text textVaraus = new Text(alustettu);
            HBox hBoxLine = new HBox(20);
            hBoxLine.getChildren().addAll(textVaraus,buttonVarausInfo);

            varausListView.getItems().add(hBoxLine);

            buttonVarausInfo.setOnAction(actionEvent -> {
                System.out.println("\n"+alustettu);
                VarauksetPanel varauksetPanel = new VarauksetPanel(avain);
                Stage secondaryStage = new Stage();
                varauksetPanel.start(secondaryStage);
            });

        }
 */