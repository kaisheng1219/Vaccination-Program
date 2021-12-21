import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.msjth.dbHelper.DbUtil;

import java.io.IOException;
import java.sql.SQLException;

@SuppressWarnings("all")

public class App extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            DbUtil.getConnection();
        } catch (SQLException e) {
            System.out.println("DB Connection Error!");
        }
        Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
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
            System.out.println("Did not manage to close DB Connection properly.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}