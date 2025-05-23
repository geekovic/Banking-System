package bank.system;

import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DepositController {

    private int accountNo;

    @FXML
    private TextField account_no;
    @FXML
    private TextField password;
    @FXML
    private TextField deposit_amount;
    @FXML
    private Label depositmsg;

    public void setDetail(int acc) {
        this.accountNo = acc;
    }

    public void deposit(ActionEvent e) {
        double bal;
        String passwd;
        int acc_no;
        try {
            acc_no = Integer.parseInt(account_no.getText());
        } catch (NumberFormatException err) {
            depositmsg.setText("Invalid account number format");
            depositmsg.setTextFill(Color.RED);
            return;
        }
        passwd = password.getText();
        try {
            bal = Double.parseDouble(deposit_amount.getText());
        } catch (NumberFormatException err) {
            depositmsg.setText("Invalid deposit amount");
            depositmsg.setTextFill(Color.RED);
            return;
        }

        if (acc_no != accountNo) {
            depositmsg.setText("Wrong Account Number");
            depositmsg.setTextFill(Color.RED);
            return;
        }
        if (bal < 0) {
            depositmsg.setText("Deposit Amount Cannot be Negative");
            depositmsg.setTextFill(Color.RED);
            return;
        }

        String sql = "UPDATE Accounts SET balance = balance + ? WHERE account_no = ? AND password = ?";
        try (PreparedStatement stmt = Main.connection.prepareStatement(sql)) {
            stmt.setDouble(1, bal);
            stmt.setInt(2, accountNo);
            stmt.setString(3, LoginController.hashPassword(passwd));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                depositmsg.setText("Deposit Successful");
                depositmsg.setTextFill(Color.GREEN);
                account_no.setText("");
                deposit_amount.setText("");
                password.setText("");
            } else {
                depositmsg.setText("Deposit Failed: Check password");
                depositmsg.setTextFill(Color.RED);
            }
        } catch (Exception err) {
            err.printStackTrace();
            depositmsg.setText("Deposit Error");
            depositmsg.setTextFill(Color.RED);
        }
    }

    public void changeToDashboard(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/system/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setDetail(accountNo);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
