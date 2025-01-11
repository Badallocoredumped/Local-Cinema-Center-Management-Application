module help {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires org.apache.pdfbox;
    requires org.apache.fontbox;
    requires java.desktop;

    opens help to javafx.fxml;
    exports help;
}
