module help {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens help to javafx.fxml;
    exports help;
}
