package oti.projekti.mokki;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LaskutPanel extends Application {

    TextField nimiTextField = new TextField();
    TextField puhelinnumeroTextField = new TextField();
    TextField tilinumeroTextField = new TextField();

    ListView laskuListView = new ListView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        nimiTextField.setEditable(false);
        puhelinnumeroTextField.setEditable(false);
        tilinumeroTextField.setEditable(false);

        Label titleLabel = new Label("Laskut");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label nimiLabel = new Label("Varaajan Nimi:");
        Label puhelinnumeroLabel = new Label("Varaajan Puhelinnumero:");
        Label tilinumeroLabel = new Label("Varaajan tilinumero:");

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
}
