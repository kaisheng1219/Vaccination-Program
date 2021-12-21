import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.msjth.dbHelper.DbUtil;

import java.io.IOException;
import java.sql.SQLException;

@SuppressWarnings("all")

public class App extends javafx.application.Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        launch();
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("select-hall.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("MySuperSejahtera!");
        primaryStage.show();
    }

    @Override
    public void stop() {
        try {
            DbUtil.closeConnection();
        } catch (SQLException e) {
            System.out.println("Db Connection did not close properly.");
        }
    }
}