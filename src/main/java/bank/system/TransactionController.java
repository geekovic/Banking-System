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

public class TransactionController {

    private int accountNo;
    private double actual_balance;

    @FXML
    private TextField account_no;
    @FXML
    private TextField tran_acc_no;
    @FXML
    private TextField password;
    @FXML
    private TextField transaction_amount;
    @FXML
    private Label transactionmsg;

    public void setDetail(int acc) {
        this.accountNo = acc;
    }

    public void transaction(ActionEvent e) {
        double bal;
        String passwd;
        int acc_no;
        int tran_no;
        try {
            acc_no = Integer.parseInt(account_no.getText());
        } catch (NumberFormatException err) {
            transactionmsg.setText("Invalid account number format");
            transactionmsg.setTextFill(Color.RED);
            return;
        }
        try {
            tran_no = Integer.parseInt(tran_acc_no.getText());
        } catch (NumberFormatException err) {
            transactionmsg.setText("Invalid account number format");
            transactionmsg.setTextFill(Color.RED);
            return;
        }

        passwd = password.getText();
        try {
            bal = Double.parseDouble(transaction_amount.getText());
        } catch (NumberFormatException err) {
            transactionmsg.setText("Invalid transaction amount");
            transactionmsg.setTextFill(Color.RED);
            return;
        }

        if (acc_no != accountNo) {
            transactionmsg.setText("Wrong Account Number");
            transactionmsg.setTextFill(Color.RED);
            return;
        }
        if (bal < 0) {
            transactionmsg.setText("Transaction Amount Cannot be Negative");
            transactionmsg.setTextFill(Color.RED);
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
                transactionmsg.setText("Check Account no. or Password ");
                transactionmsg.setTextFill(Color.RED);
            }
        } catch (Exception err) {
            err.printStackTrace();
            transactionmsg.setText("Transaction Error");
            transactionmsg.setTextFill(Color.RED);
        }
        if (bal > actual_balance) {
            transactionmsg.setText(
                    "Transaction amount is insufficient.\nYou only have " + actual_balance + " in your account.");
            transactionmsg.setTextFill(Color.RED);
            return;
        }
        String sql = "UPDATE Accounts SET balance = balance - ? WHERE account_no = ? AND password = ?";
        try (PreparedStatement stmt = Main.connection.prepareStatement(sql)) {
            stmt.setDouble(1, bal);
            stmt.setInt(2, accountNo);
            stmt.setString(3, LoginController.hashPassword(passwd));
            int rows = stmt.executeUpdate();
            if (rows < 0) {
                transactionmsg.setText("Transaction Failed: Check password");
                transactionmsg.setTextFill(Color.RED);
            }
        } catch (Exception err) {
            err.printStackTrace();
            transactionmsg.setText("Transaction Error");
            transactionmsg.setTextFill(Color.RED);
        }
        String sql2 = "UPDATE Accounts SET balance = balance + ? WHERE account_no = ?";
        try (PreparedStatement stmt = Main.connection.prepareStatement(sql2)) {
            stmt.setDouble(1, bal);
            stmt.setInt(2, tran_no);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                transactionmsg.setText("Transaction Successful");
                transactionmsg.setTextFill(Color.GREEN);
                account_no.setText("");
                tran_acc_no.setText("");
                transaction_amount.setText("");
                password.setText("");

            } else {
                transactionmsg.setText("Transaction Failed: Check password or Account Number");
                transactionmsg.setTextFill(Color.RED);
            }
        } catch (Exception err) {
            err.printStackTrace();
            transactionmsg.setText("Transaction Error");
            transactionmsg.setTextFill(Color.RED);
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
