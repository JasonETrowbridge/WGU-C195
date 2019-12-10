/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195.View_Controller;

import jasontrowbridgec195.Model.Appointment;
import jasontrowbridgec195.Model.Customer;
import jasontrowbridgec195.Model.User;
import jasontrowbridgec195.util.ConnectDB;
import jasontrowbridgec195.util.DateTime;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jason Trowbridge
 */
public class LoginController implements Initializable {

    @FXML
    private TextField UsernameTextField;
    @FXML
    private Label LoginUsernameLabel;
    @FXML
    private PasswordField PasswordTextField;
    @FXML
    private Label LoginPasswordLabel;
    @FXML
    private Button LoginButton;
    @FXML
    private Label LoginLabel;
    ObservableList<Appointment> appointmentReminderOL = FXCollections.observableArrayList();
    private DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private ZoneId localZoneId = ZoneId.systemDefault();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Before rb");
        try {
            rb = ResourceBundle.getBundle("jasontrowbridgec195.Properties.login", Locale.getDefault());
            System.out.println("After rb");
            LoginLabel.setText(rb.getString("title"));
            LoginUsernameLabel.setText(rb.getString("username"));
            UsernameTextField.setPromptText(rb.getString("username"));
            LoginPasswordLabel.setText(rb.getString("password"));
            PasswordTextField.setPromptText(rb.getString("password"));
            LoginButton.setText(rb.getString("signin"));
        } catch (MissingResourceException e) {
            System.out.println("Missing resource");
        }
    }

    //filters the reminder list and alerts if appointment is within 15 minutes
    private void appointmentAlert() {
        System.out.println("**** Being appointmentAlert ****");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus15Min = now.plusMinutes(15);
        System.out.println("Now: " + now);
        System.out.println("NowPlus15: " + nowPlus15Min);

        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentReminderOL);

        //lambda expression used to efficiently identify any appointment starting within the next 15 minutes
        filteredData.setPredicate(row -> {
            LocalDateTime rowDate = LocalDateTime.parse(row.getStart().substring(0, 16), datetimeDTF);
            return rowDate.isAfter(now.minusMinutes(1)) && rowDate.isBefore(nowPlus15Min);
        }
        );
        if (filteredData.isEmpty()) {
            System.out.println("No upcoming appointment alerts.");
        } else {
            String type = filteredData.get(0).getDescription();
            String customer = filteredData.get(0).getCustomerName();
            String start = filteredData.get(0).getStart().substring(0, 16);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upcoming Appointment Reminder");
            alert.setHeaderText("Reminder - You have an appointment scheduled within the next 15 minutes.");
            alert.setContentText("Your upcoming appointment with " + customer
                    + " is currently set to begin at " + start + ".");
            alert.showAndWait();
        }
        System.out.println("**** End appointmentAlert ****");

    }

    //creates reminder list to be checked with the appointment Alert
    private void createReminderList() {

        System.out.println("**** Begin createReminderList ****");
        System.out.println(User.getUsername());
        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement(
                    "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.description, "
                    + "appointment.`start`, appointment.`end`, customer.customerId, customer.customerName, appointment.createdBy "
                    + "FROM appointment, customer "
                    + "WHERE appointment.customerId = customer.customerId AND appointment.createdBy = ? "
                    + "ORDER BY `start`");
            ps.setString(1, User.getUsername());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {                           

                //pulls start time from database and converts it into local time zone
                Timestamp timestampStart = rs.getTimestamp("start");
                ZonedDateTime startUTC = timestampStart.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newLocalStart = startUTC.withZoneSameInstant(localZoneId);

                //pulls end time from database and converts it into local time zone
                Timestamp timestampEnd = rs.getTimestamp("end");
                ZonedDateTime endUTC = timestampEnd.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newLocalEnd = endUTC.withZoneSameInstant(localZoneId);

                //pulls select data fields for use in appointmentReminderOL observablelist
                int appointmentId = rs.getInt("appointmentId");   
                String title = rs.getString("title");
                String type = rs.getString("description");
                String customerName = rs.getString("customerName");
                int customerId = rs.getInt("customerId");
                String user = rs.getString("createdBy");

                //prints values of data fields prior to being inserted into observablelist
                System.out.println("AppointmentID: " + appointmentId);
                System.out.println("newLocalStart: " + newLocalStart.toString());
                System.out.println("newLocalEnd: " + newLocalEnd.toString());
                System.out.println("Title: " + title);
                System.out.println("Type: " + type);
                System.out.println("CustomerId: " + customerId);
                System.out.println("CustomerName: " + customerName);
                System.out.println("User: " + user);

                //inserts Appointment objects into observablelist
                appointmentReminderOL.add(new Appointment(appointmentId, newLocalStart.toString(), newLocalEnd.toString(), title, type, customerId, customerName, user));
            }

        } catch (SQLException sqe) {
            System.out.println("There is an error in your SQL preparedstatement");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("An error other than your SQL has occurred.");
            e.printStackTrace();
        }
        System.out.println("**** End create Reminder List ****");
    }

    @FXML
    private void UserNameTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void PasswordTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void LoginButtonHandler(ActionEvent event) throws SQLException, IOException {
        String usernameInput = UsernameTextField.getText();
        String passwordInput = PasswordTextField.getText();
        int userID = getUserID(usernameInput);
        Parent root;
        Stage stage;
        User user = new User();

        if (isValidPassword(userID, passwordInput)) {
            user.setUserID(userID);
            user.setUsername(usernameInput);

            //prints entered fields to terminal for troubleshooting
            //System.out.println("User ID: " + user.getUserID());
            //System.out.println("Username: " + user.getUsername());
            
            //calls method to write current user to the log
            loginLog(user.getUsername());
            createReminderList();
            appointmentAlert();

            //calls mainscreen scene after successful login
            root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            stage = (Stage) LoginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Incorrect Username and/or Password");
            alert.setContentText("Enter valid Username and Password");
            Optional<ButtonType> result = alert.showAndWait();
        }

    }

    //creates a new log file if one doesnt exist and inserts login information for current user
    public void loginLog(String user) {
        try {
            String fileName = "loginLog";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(DateTime.getTimeStamp() + " " + user + " " + "\n");
            System.out.println("New login recorded in log file.");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private boolean isValidPassword(int userID, String password) throws SQLException {

        //create statement object
        Statement statement = ConnectDB.conn.createStatement();

        //write SQL statement
        String sqlStatement = "SELECT password FROM user WHERE userId ='" + userID + "'";;

        //create resultset object
        ResultSet result = statement.executeQuery(sqlStatement);

        while (result.next()) {
            if (result.getString("password").equals(password)) {
                return true;
            }
        }
        return false;
    }

    //gets User ID for current user
    private int getUserID(String username) throws SQLException {
        int userID = -1;

        //create statement object
        Statement statement = ConnectDB.conn.createStatement();

        //write SQL statement
        String sqlStatement = "SELECT userID FROM user WHERE userName ='" + username + "'";

        //create resultset object
        ResultSet result = statement.executeQuery(sqlStatement);

        while (result.next()) {
            userID = result.getInt("userId");
        }
        return userID;
    }
}
