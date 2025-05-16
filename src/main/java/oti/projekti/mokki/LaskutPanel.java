package oti.projekti.mokki;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Map;

public class LaskutPanel extends Application {


    TextField nimiTextField = new TextField();
    TextField puhelinnumeroTextField = new TextField();
    TextField tilinumeroTextField = new TextField();

    ListView laskuListView = new ListView<>();

    private String asiakasID,nimi,puhelinnumero,tilinumero;

    public LaskutPanel(String asiakkaanID, String nimi, String puhelinnumero, String tilinumero){
        this.asiakasID = asiakkaanID;
        this.nimi = nimi;
        this.puhelinnumero = puhelinnumero;
        this.tilinumero = tilinumero;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        nimiTextField.setText(nimi);
        puhelinnumeroTextField.setText(puhelinnumero);
        tilinumeroTextField.setText(tilinumero);

        nimiTextField.setEditable(false);
        puhelinnumeroTextField.setEditable(false);
        tilinumeroTextField.setEditable(false);

        resetLasku();

        Label titleLabel = new Label("Laskut");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label nimiLabel = new Label("Varaajan Nimi:");
        Label puhelinnumeroLabel = new Label("Varaajan Puhelinnumero:");
        Label tilinumeroLabel = new Label("Varaajan Tilinumero:");

        GridPane infoGridPane = new GridPane();
        infoGridPane.setHgap(5);
        infoGridPane.setVgap(5);

        infoGridPane.add(nimiLabel, 0, 0);
        infoGridPane.add(puhelinnumeroLabel, 0, 1);
        infoGridPane.add(tilinumeroLabel, 0, 2);

        infoGridPane.add(nimiTextField, 1, 0);
        infoGridPane.add(puhelinnumeroTextField, 1, 1);
        infoGridPane.add(tilinumeroTextField, 1, 2);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(titleLabel, new Separator(), infoGridPane, new Separator(), laskuListView);

        Scene laskutScene = new Scene(root, 400, 400);
        primaryStage.setScene(laskutScene);
        primaryStage.setTitle("Laskut");
        primaryStage.show();
    }

    private void resetLasku(){

        laskuListView.getItems().clear();

        SQLDriver sqlDriver = new SQLDriver(MainWindow.connection, MainWindow.userName, MainWindow.userPassword);

        Map<String, ArrayList<String>> lista = sqlDriver.tableQuery
                ("Select l.* from lasku l Join varaus v ON l.varauksen_tunniste = v.varauksen_tunniste where v.asiakasID ='" + asiakasID + "'", "laskun_tunniste", new String[]{"summa", "erapaiva", "maksettu", "varauksen_tunniste"});


        for (Map.Entry<String, ArrayList<String>> i : lista.entrySet()) {
            String key = i.getKey();
            ArrayList<String> values = i.getValue();

            // Laskujen teksti
            String formatted = String.format(
                    "Laskun ID: %s\nSumma: %s\nErapaiva: %s\nOnko Maksettu: %s\nVarauksen ID: %s",
                    key,
                    values.get(0),
                    values.get(1),
                    values.get(2),
                    values.get(3)
            );
            //listView.getItems().add(formatted);

            //painikkeet
            Button buttonDeleteLasku = new Button("Poista Lasku");

            Text textLine = new Text(formatted);
            textLine.setWrappingWidth(200);

            HBox hboxLine = new HBox(20);

            VBox vBoxButtons = new VBox(10);
            vBoxButtons.getChildren().addAll(buttonDeleteLasku);

            // Lopullinen linja
            hboxLine.getChildren().addAll(textLine, vBoxButtons);

            //listView.getItems().add(buttonView);
            laskuListView.getItems().add(hboxLine);


            buttonDeleteLasku.setOnAction(actionEvent -> {
                try {

                    Connection conn = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);
                    String sql = "DELETE FROM lasku WHERE varauksen_tunniste = ?";

                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1,values.get(3));

                    stmt.executeUpdate();

                    stmt.close();
                    conn.close();

                    resetLasku();

                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            });

        }
    }
}
