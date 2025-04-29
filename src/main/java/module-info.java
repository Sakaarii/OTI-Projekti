module oti.projekti.mokki {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens oti.projekti.mokki to javafx.fxml;
    exports oti.projekti.mokki;
}