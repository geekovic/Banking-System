package bank.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {

    public static Connection connection;  // <-- add this!

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bank/system/login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            connectDB();
            createTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void connectDB() {
        try {
            String url = "jdbc:sqlite:bank.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Accounts(" +
                "account_no INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL,dob TEXT,"+
                "balance DOUBLE DEFAULT 0,password TEXT);";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created or already exists.");

            stmt.execute("INSERT OR IGNORE INTO sqlite_sequence(name, seq) VALUES('Accounts', 250000);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
