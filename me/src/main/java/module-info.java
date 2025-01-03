module help {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens help to javafx.fxml;
    exports help;
}
