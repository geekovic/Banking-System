package bank.system;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    private int changeAcc_no;

    @FXML
    TextField username_acc;

    @FXML
    TextField password;

    @FXML
    Button forgetpassword;

    @FXML
    Button loginbtn;

    @FXML
    TextField name;
    @FXML
    TextField passwd;
    @FXML
    TextField confirmpasswd;
    @FXML
    TextField date;
    @FXML
    TextField month;
    @FXML
    TextField year;
    @FXML
    TextField new_account_number;
    @FXML
    TextField new_passwd;
    @FXML
    TextField new_confirmpasswd;

    @FXML
    Label msg;
    @FXML
    Label forgetmsg;
    @FXML
    Label error;

    public void login(ActionEvent e) {
        String input = username_acc.getText();
        if (input == null || input.trim().isEmpty()) {
            error.setText("Account number cannot be empty");
            error.setTextFill(Color.RED);
            return;
        }

        try {
            int Acc_no = Integer.parseInt(input);
            String Password = password.getText();

            String sql = "SELECT * FROM Accounts WHERE account_no = ? AND password = ?";

            try (PreparedStatement stmt = Main.connection.prepareStatement(sql)) {
                stmt.setInt(1, Acc_no);
                stmt.setString(2, hashPassword(Password));

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Login Successful");
                    error.setText("");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/bank/system/dashboard.fxml"));
                    Parent root = loader.load();

                    DashboardController dashboardController = loader.getController();
                    dashboardController.setDetail(Acc_no);

                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();

                } else {
                    error.setText("Account number or Password incorrect");
                    error.setTextFill(Color.RED);
                }
            }
        } catch (NumberFormatException nfe) {
            error.setText("Account number must be a valid number");
            error.setTextFill(Color.RED);
        } catch (Exception err) {
            err.printStackTrace();
            error.setText("An error occurred during login");
            error.setTextFill(Color.RED);
        }
    }

    public void changeToLogin(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bank/system/login.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void changeToCreateAcc(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bank/system/createAcc.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void changeToForgetPassword(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bank/system/forgetPassword.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public boolean isDob(String date, String month, String year) {
        if (date == null || date.isEmpty())
            return false;
        if (month == null || month.isEmpty())
            return false;
        if (year == null || year.isEmpty())
            return false;

        try {
            int d = Integer.parseInt(date);
            int m = Integer.parseInt(month);
            int y = Integer.parseInt(year);

            LocalDate.of(y, m, d);
            return true;
        } catch (NumberFormatException | DateTimeException e) {
            return false;
        }
    }

    public void createAccount(ActionEvent e) {
        String Acc_name = name.getText();
        String Passwd = passwd.getText();
        String ConfirmPasswd = confirmpasswd.getText();

        String dob = date.getText() + "-" + month.getText() + "-" + year.getText();

        if (Acc_name.isEmpty()) {
            msg.setText("Name Should not be empty");
            msg.setTextFill(Color.RED);
            return;
        }
        if (Passwd.isEmpty()) {
            msg.setText("Password Should not be empty");
            msg.setTextFill(Color.RED);
            return;
        }
        if (!Passwd.equals(ConfirmPasswd)) {
            msg.setText("Password and Confirm Password not match");
            msg.setTextFill(Color.RED);
            return;
        }
        if (Passwd.length() < 6) {
            msg.setText("Password need to be atleast 6 characters long");
            msg.setTextFill(Color.RED);
            return;
        }
        if (!isDob(date.getText(), month.getText(), year.getText())) {
            msg.setText("Incorrect Date of Birth");
            msg.setTextFill(Color.RED);
            return;
        }
        int inputYear = Integer.parseInt(year.getText());
        int currentYear = java.time.LocalDate.now().getYear();
        int minYear = 1950;
        int maxYear = currentYear - 18;

        if (inputYear < minYear || inputYear > maxYear) {
            msg.setText("Year must be between " + minYear + " and " + maxYear);
            msg.setTextFill(Color.RED);
            return;
        }

        String sql = "INSERT INTO Accounts(name, dob,password) VALUES(?,?,?)";

        try (PreparedStatement stmt = Main.connection.prepareStatement(sql)) {
            stmt.setString(1, Acc_name);
            stmt.setString(2, dob);
            stmt.setString(3, hashPassword(Passwd));
            stmt.executeUpdate();

            int accountNo = -1;
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                accountNo = generatedKeys.getInt(1);
            }

            System.out.println("Account created for: " + name);

            msg.setText("Successfully created Account");
            msg.setTextFill(Color.GREEN);

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Account Detail ");
            alert.setHeaderText("Remember Your Account Detail unless You may loss! your account");
            alert.setContentText("Name : " + Acc_name + "\nDOB : " + dob + "\nAccount Number : " + accountNo
                    + "\nPassword : " + Passwd);

            alert.getButtonTypes().setAll(ButtonType.OK);

            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.setOnCloseRequest(event -> {
                event.consume();
            });

            if (alert.showAndWait().get() == ButtonType.OK) {
                name.clear();
                passwd.clear();
                confirmpasswd.clear();
                date.clear();
                month.clear();
                year.clear();

                changeToLogin(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void forgetPassword(ActionEvent e) {
        String Acc_name = name.getText();
        String new_Passwd = new_passwd.getText();
        String new_ConfirmPasswd = new_confirmpasswd.getText();

        String dob = date.getText() + "-" + month.getText() + "-" + year.getText();
        try {
            changeAcc_no = Integer.parseInt(new_account_number.getText());
        } catch (Exception err) {
            forgetmsg.setText("Invalid Account number");
            forgetmsg.setTextFill(Color.RED);
            return;
        }
        if (Acc_name.isEmpty()) {
            forgetmsg.setText("Name Should not be empty");
            forgetmsg.setTextFill(Color.RED);
            return;
        }
        if (new_Passwd.isEmpty()) {
            forgetmsg.setText("Password Should not be empty");
            forgetmsg.setTextFill(Color.RED);
            return;
        }
        if (!new_Passwd.equals(new_ConfirmPasswd)) {
            forgetmsg.setText("Password and Confirm Password not match");
            forgetmsg.setTextFill(Color.RED);
            return;
        }
        if (new_Passwd.length() < 6) {
            forgetmsg.setText("Password need to be atleast 6 characters long");
            forgetmsg.setTextFill(Color.RED);
            return;
        }
        if (!isDob(date.getText(), month.getText(), year.getText())) {
            forgetmsg.setText("Incorrect Date of Birth");
            forgetmsg.setTextFill(Color.RED);
            return;
        }

        String sql = "UPDATE Accounts set password =? WHERE account_no=? AND dob=? AND name=?";

        try (PreparedStatement stmt = Main.connection.prepareStatement(sql)) {
            stmt.setString(1, hashPassword(new_Passwd));
            stmt.setInt(2, changeAcc_no);
            stmt.setString(3, dob);
            stmt.setString(4, Acc_name);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Password Forget Successfully");
                forgetmsg.setText("Successfully Forget Password");
                forgetmsg.setTextFill(Color.GREEN);
                name.clear();
                new_passwd.clear();
                new_confirmpasswd.clear();
                date.clear();
                month.clear();
                year.clear();
                new_account_number.clear();
            } else {
                System.out.println("No matching account found");
                forgetmsg.setText("Incorrect account number, name, or date of birth");
                forgetmsg.setTextFill(Color.RED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1)
                    hexString.append("0");
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
