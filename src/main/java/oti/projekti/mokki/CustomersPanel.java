package oti.projekti.mokki;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Map;

public class CustomersPanel extends Application {

    //Asiakasta ei voi poistaa jos hänellä on varauksia.

    static ListView<Object> listView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        resetCustomer();

        Scene scene = new Scene(listView, 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Asiakkaat");
        primaryStage.show();

    }


    //tekee listan asiakkaista ja varauksista

    private static void resetCustomer(){

        listView = new ListView<>();

        listView.getItems().clear();

        SQLDriver sqlDriver = new SQLDriver(MainWindow.connection, MainWindow.userName, MainWindow.userPassword);

        Map<String, ArrayList<String>> lista = sqlDriver.tableQuery("Select * from asiakas", "asiakasID", new String[]{"nimi", "sahkoposti", "tilinumero"});

        // Lista asiakkaista

        for (Map.Entry<String, ArrayList<String>> i : lista.entrySet()) {
            String key = i.getKey();
            ArrayList<String> values = i.getValue();

            // asiakkaan teksti
            String formatted = String.format(
                    "ID: %s\nNimi: %s\nSahkoposti: %s\nTilinumero: %s",
                    key,
                    values.get(0),
                    values.get(1),
                    values.get(2)
            );
            //listView.getItems().add(formatted);

            //painikkeet
            Button buttonView = new Button("Naytä varaukset");
            Button buttonDeleteAsiakas = new Button("Poista asiakas");

            // varauksien teksti
            Map<String, ArrayList<String>> kaikkiVaraukset = sqlDriver.tableQuery("SELECT * FROM varaus WHERE asiakasID ='" + key + "'", "varauksen_tunniste", new String[]{"varauksen_alkamispaiva", "varauksen_paattymispaiva", "varauksen_kesto","mokin_tunniste"});

            VBox vBox = new VBox();
            vBox.setVisible(false);


            // Lista asiakkaiden varauksista
            for (Map.Entry<String, ArrayList<String>> varaus : kaikkiVaraukset.entrySet()) {
                String avain = varaus.getKey();
                ArrayList<String> arvot = varaus.getValue();

                String alustettu = String.format(
                        "Varaus ID: %-5s\t Alkamispäivä: %-10s\t Päättymispäivä: %-10s\t Kesto(päivää): %-2s\t Mökin tunnus: %-5s",
                        avain,
                        arvot.get(0),
                        arvot.get(1),
                        arvot.get(2),
                        arvot.get(3)
                );

                // varauksen teksti
                Text textVaraus = new Text(alustettu);
                vBox.getChildren().add(textVaraus);
            }

            // Asiakkaan teksti
            Text textLine = new Text(formatted);
            textLine.setWrappingWidth(200);

            HBox hboxLine = new HBox(20);

            VBox vBoxButtons = new VBox(10);
            vBoxButtons.getChildren().addAll(buttonView,buttonDeleteAsiakas);

            // Lopullinen linja
            hboxLine.getChildren().addAll(textLine, vBoxButtons,vBox);

            //listView.getItems().add(buttonView);
            listView.getItems().add(hboxLine);

            buttonView.setOnAction(actionEvent -> {

                if (vBox.isVisible()) vBox.setVisible(false);
                else vBox.setVisible(true);
                System.out.println("\n" + formatted);

            });

            buttonDeleteAsiakas.setOnAction(actionEvent -> {
                try {

                    Connection conn = DriverManager.getConnection(MainWindow.connection,MainWindow.userName,MainWindow.userPassword);
                    String sql = "DELETE FROM asiakas WHERE asiakasID = ?";

                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1,i.getKey());

                    stmt.executeUpdate();

                    stmt.close();
                    conn.close();

                    //resettaa listviewin jotta näyttää oikealta
                    resetCustomer();

                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            });
        }
    }
}