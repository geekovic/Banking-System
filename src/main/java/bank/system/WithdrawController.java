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

public class WithdrawController {

    private int accountNo;
    private double actual_balance;

    @FXML
    private TextField account_no;
    @FXML
    private TextField password;
    @FXML
    private TextField withdraw_amount;
    @FXML
    private Label withdrawmsg;

    public void setDetail(int acc) {
        this.accountNo = acc;
    }

    public void withdraw(ActionEvent e) {
        double bal;
        String passwd;
        int acc_no;
        try {
            acc_no = Integer.parseInt(account_no.getText());
        } catch (NumberFormatException err) {
            withdrawmsg.setText("Invalid account number format");
            withdrawmsg.setTextFill(Color.RED);
            return;
        }

        passwd = password.getText();
        try {
            bal = Double.parseDouble(withdraw_amount.getText());
        } catch (NumberFormatException err) {
            withdrawmsg.setText("Invalid withdraw amount");
            withdrawmsg.setTextFill(Color.RED);
            return;
        }

        if (acc_no != accountNo) {
            withdrawmsg.setText("Wrong Account Number");
            withdrawmsg.setTextFill(Color.RED);
            return;
        }
        if (bal < 0) {
            withdrawmsg.setText("Withdraw Amount Cannot be Negative");
            withdrawmsg.setTextFill(Color.RED);
            return;
        }

        String getBalance = "Select balance from Accounts WHERE account_no = ? AND password = ?";
        try (PreparedStatement stmt = Main.connection.prepareStatement(getBalance)) {
            stmt.setInt(1, accountNo);
            stmt.setString(2, LoginController.hashPassword(passwd));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                actual_balance = rs.getDouble("balance");
            } else {
                withdrawmsg.setText("Check Account no. or Password ");
                withdrawmsg.setTextFill(Color.RED);
            }
        } catch (Exception err) {
            err.printStackTrace();
            withdrawmsg.setText("Withdraw Error");
            withdrawmsg.setTextFill(Color.RED);
        }
        if (bal > actual_balance) {
            withdrawmsg.setText(
                    "Withdrawal amount is insufficient.\nYou only have " + actual_balance + " in your account.");
            withdrawmsg.setTextFill(Color.RED);
            return;
        }
        String sql = "UPDATE Accounts SET balance = balance - ? WHERE account_no = ? AND password = ?";
        try (PreparedStatement stmt = Main.connection.prepareStatement(sql)) {
            stmt.setDouble(1, bal);
            stmt.setInt(2, accountNo);
            stmt.setString(3, LoginController.hashPassword(passwd));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                withdrawmsg.setText("Withdraw Successful");
                withdrawmsg.setTextFill(Color.GREEN);
                account_no.setText("");
                withdraw_amount.setText("");
                password.setText("");
            } else {
                withdrawmsg.setText("Withdraw Failed: Check password");
                withdrawmsg.setTextFill(Color.RED);
            }
        } catch (Exception err) {
            err.printStackTrace();
            withdrawmsg.setText("Withdraw Error");
            withdrawmsg.setTextFill(Color.RED);
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
