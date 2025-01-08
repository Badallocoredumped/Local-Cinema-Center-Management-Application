module help {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens help to javafx.fxml;
    exports help;
}
