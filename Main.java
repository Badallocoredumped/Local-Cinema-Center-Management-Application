
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Local Cinema Center Management Application");

        Parent root = FXMLLoader.load(getClass().getResource("Controllers/loginscreen.fxml"));
        Scene loginScene = new Scene(root, 640, 400);
        loginScene.getStylesheets().add(getClass().getResource("Controllers/styles.css").toExternalForm());

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
}