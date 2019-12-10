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
import static jasontrowbridgec195.View_Controller.AppointmentsMainController.getUpdateAppointment;
import static jasontrowbridgec195.View_Controller.AppointmentsMainController.getUpdateAppointmentIndex;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author Jason Trowbridge
 */
public class AppointmentAddController implements Initializable {

    @FXML
    private Label AppointmentLabel;
    @FXML
    private Label AppointmentCustomerLabel;
    @FXML
    private Label AppointmentTitleLabel;
    @FXML
    private Label ApointmentDescriptionLabel;
    @FXML
    private Label AppointmentContactLabel;
    @FXML
    private Label AppointmentTypeLabel;
    @FXML
    private Label AppointmentDateLabel;
    @FXML
    private Label AppointmentLocationLabel;
    @FXML
    private TextField AppointmentTitleTextField;
    @FXML
    private TextField AppointmentDescriptionTextField;
    @FXML
    private DatePicker AppointmentDatePicker;
    @FXML
    private Label AppointmentStartTimeLabel;
    @FXML
    private Label AppointmentUrlLabel;
    @FXML
    private TextField AppointmentUrlTextField;
    @FXML
    private Label AppointmentEndTimeLabel;
    @FXML
    private ComboBox<String> AppointmentTypeComboBox;
    @FXML
    private ComboBox<String> AppointmentContactComboBox;
    @FXML
    private ComboBox<String> AppointmentLocationComboBox;
    @FXML
    private ComboBox<String> AppointmentStartComboBox;
    @FXML
    private ComboBox<String> AppointmentEndComboBox;
    @FXML
    private Button AppointmentSaveButton;
    @FXML
    private Button AppointmentCancelButton;
    @FXML
    private TableView<Customer> AppointmentCustomerTable;
    @FXML
    private TableColumn<Customer, Integer> AppointmentCustomerTableCustomerIDColumn;
    @FXML
    private TableColumn<Customer, String> AppointmentCustomerTableCustomerNameColumn;
    @FXML
    private Label AppointmentCustomerTableLabel;
    @FXML
    private TextField AppointmentCustomerTextField;

    Parent root;
    Stage stage;

    Customer selectedCustomer = new Customer();

    
    private Appointment selectedAppointment;
    private final ZoneId localZoneID = ZoneId.systemDefault(); //local zoneId

    private ObservableList<Customer> customerOL = FXCollections.observableArrayList();
    private final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofPattern("HH:mm:ss");//ISO standard time formaat
    private final DateTimeFormatter dateDTF = DateTimeFormatter.ofPattern("yyyy-mm-dd");//ISO standard date format
    private final DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ZoneId utcZoneID = ZoneId.of("UTC");
    ObservableList<Appointment> apptTimeList;
    @FXML
    private Button AppointmentBackButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PropertyValueFactory<Customer, Integer> customerIDFactory = new PropertyValueFactory<>("CustomerID");
        PropertyValueFactory<Customer, String> customerNameFactory = new PropertyValueFactory<>("CustomerName");

        AppointmentCustomerTableCustomerIDColumn.setCellValueFactory(customerIDFactory);
        AppointmentCustomerTableCustomerNameColumn.setCellValueFactory(customerNameFactory);
        AppointmentCustomerTextField.setEditable(false);
      
        try {
            updateCustomerTable();
        } catch (SQLException ex) {
            System.out.println("Something is wrong with your SQL code!");
        }

        //populates data into respective ComboBox's
        fillTypeList();
        fillContactList();
        fillLocationList();
        fillStartTimesList();

        //Listen for mouse click on item in Customer Table and populate customer text field with selected customer name
        AppointmentCustomerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCustomerName(newValue));
        System.out.println("Current User is: " + User.getUsername());
        
        //disables weekends from being selected for appointments
        AppointmentDatePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY );
            }
        });
        
        //this section populates appointment fields only if updating
        if (AppointmentsMainController.boxtype == 2) {
            selectedAppointment = AppointmentsMainController.getUpdateAppointment();
            selectedCustomer.setCustomerID(selectedAppointment.getCustomerID());
            System.out.println("selectedCustomerID: " + selectedCustomer.getCustomerID());
            AppointmentCustomerTable.getSelectionModel().select(selectedCustomer);
            updateAppointmentFields();
        }
    }

    private void showCustomerName(Customer newValue) {
        AppointmentCustomerTextField.setText(newValue.getCustomerName());
        selectedCustomer = newValue;
    }

    //fills appointment fields with data from selected appointment for update
    private void updateAppointmentFields() {
        System.out.println("**** Start Update Appointment Fields ****");
        AppointmentLabel.setText("Update Appointment");
        
        System.out.println("Selected Appointment CustomerID: " + selectedAppointment.getCustomerID());
        selectedCustomer.setCustomerID(selectedAppointment.getCustomerID());
       
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //get local start time
        String startLocal = selectedAppointment.getStart();
        System.out.println("Local Start: " + startLocal);

        //get local end time
        String endLocal = selectedAppointment.getEnd();
        System.out.println("Local End: " + endLocal);
       
        LocalDateTime localDateTimeStart = LocalDateTime.parse(startLocal, datetimeDTF);
        LocalDateTime localDateTimeEnd = LocalDateTime.parse(endLocal, datetimeDTF);
        
        LocalDate localDate = localDateTimeStart.toLocalDate();
        System.out.println("localDate: " + localDate);
       
        LocalTime localTimeStart = localDateTimeStart.toLocalTime();
      
        System.out.println("localTimeStart: " + localTimeStart);
        //System.out.println("localTimeEnd: " + localTimeEnd);

        //populates appointment fields with the selected appointment to be updated
        AppointmentCustomerTextField.setText(selectedAppointment.getCustomerName());
        AppointmentTitleTextField.setText(selectedAppointment.getTitle());
        AppointmentDescriptionTextField.setText(selectedAppointment.getDescription());
        AppointmentTypeComboBox.setValue(selectedAppointment.getType());
        AppointmentContactComboBox.setValue(selectedAppointment.getContact());
        AppointmentLocationComboBox.setValue(selectedAppointment.getLocation());
        AppointmentDatePicker.setValue(localDate);
        AppointmentStartComboBox.getSelectionModel().select(localDateTimeStart.toLocalTime().format(timeDTF));
        AppointmentEndComboBox.getSelectionModel().select(localDateTimeEnd.toLocalTime().format(timeDTF));
        AppointmentUrlTextField.setText(selectedAppointment.getUrl());
        System.out.println("**** End Update Appointment Fields ****");

    }

    //updates customer table with any recent changes
    public void updateCustomerTable() throws SQLException {
        System.out.println("**** Start update Customer Table ****");

        //create statement object
        PreparedStatement ps = ConnectDB.conn.prepareStatement("SELECT customerId, customerName FROM customer");

        //execute statement and create resultset object
        ResultSet result = ps.executeQuery();
       

        //get all records from resultset object
        while (result.next()) {
            Customer cust = new Customer();
            cust.setCustomerName(result.getString("customerName"));
            cust.setCustomerID(result.getInt("customerId"));
            customerOL.addAll(cust);
        }
        AppointmentCustomerTable.setItems(customerOL);
        System.out.println("**** End update Customer Table ****");
    }

    private void clearAppointmentFields() {
        System.out.println("**** Start Clear Appointment Fields ****");
        AppointmentCustomerTextField.setText("");
        AppointmentTitleTextField.setText("");
        AppointmentDescriptionTextField.setText("");
        AppointmentTypeComboBox.getSelectionModel().clearSelection();
        AppointmentContactComboBox.getSelectionModel().clearSelection();
        AppointmentLocationComboBox.getSelectionModel().clearSelection();
        AppointmentDatePicker.setValue(null);
        AppointmentStartComboBox.getSelectionModel().clearSelection();
        AppointmentEndComboBox.getSelectionModel().clearSelection();
        AppointmentUrlTextField.setText("");
        System.out.println("**** End Clear Appointment Fields ****");
    }

    private void fillStartTimesList() {
        /**
         * Limits Start and End times to hours between 8am-5pm
         * Appointment intervals are set at 30min increments
         */
        LocalTime time = LocalTime.of(8, 0, 0);
        do {
            startTimes.add(time.format(timeDTF));
            endTimes.add(time.format(timeDTF));
            time = time.plusMinutes(30);
        } while (!time.equals(LocalTime.of(17, 30, 0)));
        startTimes.remove(startTimes.size() - 1);
        endTimes.remove(0);
        if (AppointmentsMainController.boxtype != 2) {
            AppointmentDatePicker.setValue(LocalDate.now());
        }
        AppointmentStartComboBox.setItems(startTimes);
        AppointmentEndComboBox.setItems(endTimes);
        AppointmentStartComboBox.getSelectionModel().select(LocalTime.of(8, 0, 0).format(timeDTF));
        AppointmentEndComboBox.getSelectionModel().select(LocalTime.of(8, 30, 0).format(timeDTF));
    }

    private void fillContactList() {
        /**
         * populates contact list with type of contact used to set appointment
         */
        ObservableList<String> contactList = FXCollections.observableArrayList();
        contactList.addAll("Email", "Phone", "In-person");
        AppointmentContactComboBox.setItems(contactList);
    }

    private void fillTypeList() {
        /**
         * populates list with four categories of appointment types
         */
        ObservableList<String> typeList = FXCollections.observableArrayList();
        typeList.addAll("New Account", "Consultation", "Follow-Up", "Close Account");
        AppointmentTypeComboBox.setItems(typeList);
    }

    private void fillLocationList() {
        /**
         * populates list with all the available appointment locations
         */
        ObservableList<String> locationList = FXCollections.observableArrayList();
        locationList.addAll("Phoenix", "New York", "Tampa", "Orlando", "Dallas", "London", "Liverpool");
        AppointmentLocationComboBox.setItems(locationList);
    }

    private ObservableList<Customer> fillCustomerList() throws SQLException, Exception {
        /**
         * populates customer table with all available customers
         */
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        int customerID;
        String customerName;
        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("SELECT customerId, CustomerName FROM customer");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                customerID = result.getInt(selectedCustomer.getCustomerID());
                customerName = result.getString(selectedCustomer.getCustomerName());
                customerList.add(new Customer(customerID, customerName));
            }
        } catch (SQLException e) {
            System.out.println("fillCustomerList SQL statement has an error!");
        }
        return customerList;
    }

    private boolean validAppointment() {
        System.out.println("****** Begin Appointment Validation *****");
        Customer customer = AppointmentCustomerTable.getSelectionModel().getSelectedItem();
        String title = AppointmentTitleTextField.getText();
        String description = AppointmentDescriptionTextField.getText();
        String type = AppointmentTypeComboBox.getValue();
        String contact = AppointmentContactComboBox.getValue();
        String location = AppointmentLocationComboBox.getValue();
        String url = AppointmentUrlTextField.getText();
        LocalDate localDate = AppointmentDatePicker.getValue();
        LocalTime startTime = LocalTime.parse(AppointmentStartComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalTime endTime = LocalTime.parse(AppointmentEndComboBox.getSelectionModel().getSelectedItem(), timeDTF);

        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));

        String errorMessage = "";
        //first checks to see if inputs are null
        if (customer == null) {
            errorMessage += "Please Select a Customer.\n";
        }
        if (title == null || title.length() == 0) {
            errorMessage += "You must enter an Appointment title.\n";
        }
        if (description == null || description.length() == 0) {
            errorMessage += "You must enter an appointment description.\n";
        }
        if (type == null || type.length() == 0) {
            errorMessage += "You must select an Appointment type.\n";
        }
        if (contact == null || contact.length() == 0) {
            errorMessage += "You must select an Appointment contact.\n";
        }
        if (location == null || location.length() == 0) {
            errorMessage += "You must select an Appointment location.\n";
        }
        if (url == null || url.length() == 0) {
            errorMessage += "You must enter an Appointment URL.\n";
        }
        if (startUTC == null) {
            errorMessage += "You must select a Start time";
        }
        if (endUTC == null) {
            errorMessage += "You must select an End time.\n";
            //checks to make sure Start and End times are not the same
        } else if (endUTC.equals(startUTC) || endUTC.isBefore(startUTC)) {
            errorMessage += "End time must be after Start time.\n";
        } else {
            try {
                //checks user's existing appointments for time conflicts
                if (hasConflict(startUTC, endUTC)) {
                    errorMessage += "Appointment times conflict with Consultant's other appointments.\n";
                }
            } catch (SQLException ex) {
                Logger.getLogger(AppointmentAddController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Invalid Appointment");
            alert.setContentText(errorMessage);
            Optional<ButtonType> result = alert.showAndWait();
            System.out.println("***** End Appointment Validation *****");
            return false;
        }

    }
    
    //checks to make sure current appointment being set doesnt conflict with consultant's other appointments
    private boolean hasConflict(ZonedDateTime newStart, ZonedDateTime newEnd) throws SQLException {
        int appointmentID = -1;
        String consultant;
        if (AppointmentsMainController.boxtype == 2) {
            //edited appointment
            appointmentID = selectedAppointment.getAppointmentID();
            consultant = selectedAppointment.getCreatedBy();
        } else {
            //new appointment            
            consultant = User.getUsername();
        }
        System.out.println("AppointmentID: " + appointmentID);

        try {

            PreparedStatement pst = ConnectDB.makeConnection().prepareStatement(
                    "SELECT * FROM appointment "
                    + "WHERE (? BETWEEN start AND end OR ? BETWEEN start AND end OR ? < start AND ? > end) "
                    + "AND (createdBy = ? AND appointmentID != ?)");
            pst.setTimestamp(1, Timestamp.valueOf(newStart.toLocalDateTime()));
            pst.setTimestamp(2, Timestamp.valueOf(newEnd.toLocalDateTime()));
            pst.setTimestamp(3, Timestamp.valueOf(newStart.toLocalDateTime()));
            pst.setTimestamp(4, Timestamp.valueOf(newEnd.toLocalDateTime()));
            pst.setString(5, consultant);
            pst.setInt(6, appointmentID);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (SQLException sqe) {
            System.out.println("SQL contains errors for 'hasConflict' method.");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something other than the SQL has an error.");
            e.printStackTrace();
        }
        return false;
    }

    //saves new appointments
    private void saveAppointment() throws Exception {
        System.out.println("**** Start Save Apppointment ****");
        //capture dates and time in local time
        LocalDate localDate = AppointmentDatePicker.getValue(); //returns date value without time
        LocalTime localStartTime = LocalTime.parse(AppointmentStartComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalTime localEndTime = LocalTime.parse(AppointmentEndComboBox.getSelectionModel().getSelectedItem(), timeDTF);

        //combine date and start/end times together       
        LocalDateTime startDT = LocalDateTime.of(localDate, localStartTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, localEndTime);
        System.out.println("localStartDT: " + startDT);
        System.out.println("localEndDT: " + endDT);

        //convert startDT and endDT to UTC
        ZonedDateTime startUTC = startDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println("startUTC: " + startUTC);
        System.out.println("endUTC: " + endUTC);

        //convert UTC time to a timestamp for database insertion
        Timestamp sqlStartTS = Timestamp.valueOf(startUTC.toLocalDateTime());
        Timestamp sqlEndTS = Timestamp.valueOf(endUTC.toLocalDateTime());
        System.out.println("sqlStartTime: " + sqlStartTS);
        System.out.println("sqlEndTime: " + sqlEndTS);

        try {
            int newAppointmentID = -1;
            //Insert new address into DB
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, AppointmentCustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
            selectedCustomer.setCustomerID(AppointmentCustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
            //System.out.println("CustomerID: " + selectedCustomer.getCustomerID());
            ps.setInt(2, User.getUserID());
            ps.setString(3, AppointmentTitleTextField.getText());
            ps.setString(4, AppointmentDescriptionTextField.getText());
            ps.setString(5, AppointmentLocationComboBox.getSelectionModel().getSelectedItem());
            ps.setString(6, AppointmentContactComboBox.getSelectionModel().getSelectedItem());
            ps.setString(7, AppointmentTypeComboBox.getSelectionModel().getSelectedItem());
            ps.setString(8, AppointmentUrlTextField.getText());
            ps.setTimestamp(9, sqlStartTS);
            ps.setTimestamp(10, sqlEndTS);
            ps.setString(11, User.getUsername());
            ps.setString(12, User.getUsername());
            System.out.println("PS: " + ps);
            int result = ps.executeUpdate();
            //System.out.println("After SQL execute");
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                newAppointmentID = rs.getInt(1);
                System.out.println("Generated AppointmentID: " + newAppointmentID);
            }
            System.out.println("Appointment section saved successfully!");
            clearAppointmentFields();
        } catch (SQLException e) {
            System.out.println("SQL statement has an error.");
        }
        System.out.println("**** End Save Appointment ****");
    }

    //updates existing appointments with any changes
    private void updateAppointment() throws Exception {
        System.out.println("**** Start Update Appointment Save ****");
        //capture dates and time in local time
        System.out.println(">>> Begin LocalDate <<<<");
        LocalDate localDate = AppointmentDatePicker.getValue(); //returns date value without time
        System.out.println(">>> End LocalDate <<<<");
        LocalTime localStartTime = LocalTime.parse(AppointmentStartComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalTime localEndTime = LocalTime.parse(AppointmentEndComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        System.out.println(">>> End LocalTime <<<<");

        //combine date and start/end times together       
        LocalDateTime startDT = LocalDateTime.of(localDate, localStartTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, localEndTime);
        System.out.println("localStartDT: " + startDT);
        System.out.println("localEndDT: " + endDT);

        //convert startDT and endDT to UTC
        ZonedDateTime startUTC = startDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println("startUTC: " + startUTC);
        System.out.println("endUTC: " + endUTC);

        //convert UTC time to a timestamp for database insertion
        Timestamp sqlStartTS = Timestamp.valueOf(startUTC.toLocalDateTime());
        Timestamp sqlEndTS = Timestamp.valueOf(endUTC.toLocalDateTime());
        
        //displays values of fields in console used for troubleshooting
        System.out.println("sqlStartTime: " + sqlStartTS);
        System.out.println("sqlEndTime: " + sqlEndTS);
        System.out.println("****************************************");
        System.out.println("CustomerID: " + selectedCustomer.getCustomerID());
        System.out.println("UserId: " + User.getUserID());
        System.out.println("Title: " + AppointmentTitleTextField.getText());
        System.out.println("Description: " + AppointmentDescriptionTextField.getText());
        System.out.println("Location: " + AppointmentLocationComboBox.getSelectionModel().getSelectedItem());
        System.out.println("Contact: " + AppointmentContactComboBox.getSelectionModel().getSelectedItem());
        System.out.println("Type: " + AppointmentTypeComboBox.getSelectionModel().getSelectedItem());
        System.out.println("URL: " + AppointmentUrlTextField.getText());
        System.out.println("****************************************");

        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("UPDATE appointment "
                    + "SET customerId = ?, userId = ?, title = ?, description = ?, "
                    + "location = ?, contact = ?, type = ?, url = ?, start = ?, end = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? "
                    + "WHERE appointmentId = ?");
            System.out.println("CustomerID before check CustomerTable: " + selectedCustomer.getCustomerID());
            if (selectedCustomer.getCustomerID() <= 0) {
                selectedCustomer.setCustomerID(AppointmentCustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
            }
            System.out.println("CustomerID after check CustomberTable: " + selectedCustomer.getCustomerID());
            ps.setInt(1, selectedCustomer.getCustomerID());
            System.out.println("CustomerID: " + selectedCustomer.getCustomerID());
            ps.setInt(2, User.getUserID());
            ps.setString(3, AppointmentTitleTextField.getText());
            ps.setString(4, AppointmentDescriptionTextField.getText());
            ps.setString(5, AppointmentLocationComboBox.getSelectionModel().getSelectedItem());
            ps.setString(6, AppointmentContactComboBox.getSelectionModel().getSelectedItem());
            ps.setString(7, AppointmentTypeComboBox.getSelectionModel().getSelectedItem());
            ps.setString(8, AppointmentUrlTextField.getText());
            ps.setTimestamp(9, sqlStartTS);
            ps.setTimestamp(10, sqlEndTS);
            ps.setString(11, User.getUsername());
            ps.setInt(12, selectedAppointment.getAppointmentID());
            System.out.println("PS SQL: " + ps);
            int result = ps.executeUpdate();
            System.out.println("Appointment UPDATED successfully!");
            clearAppointmentFields();
        } catch (SQLException e) {
            System.out.println("Update Appointment method SQL preparedstatement has an error.");
        }
        System.out.println("**** End Update Appointment Save ****");

    }

    @FXML
    private void AppointmentCancelButtonHandler(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Required");
        alert.setHeaderText("Confirm Cancel");
        alert.setContentText("Are you sure you want to cancel?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            System.out.println("Returning to Main Appointments Screen.");
            root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
            stage = (Stage) AppointmentSaveButton.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
            //customerUpdate = false;
            //customerAdd = false;
            //clearTextFields();
        } else {
            System.out.println("Cancel canceled.");
        }
    }

    @FXML
    private void AppointmentTitleTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void AppointmentDescriptionTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void AppointmentDatePickerHandler(ActionEvent event) {
    }

    @FXML
    private void AppointmentUrlTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void AppointmentSaveButtonHandler(ActionEvent event) throws Exception {
        if (validAppointment()) {
            if (AppointmentsMainController.boxtype == 1) {
                saveAppointment();
                AppointmentLabel.setText("Add Appointment");
                root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
                stage = (Stage) AppointmentBackButton.getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.show();
            } else if (AppointmentsMainController.boxtype == 2) {
                updateAppointment();
                AppointmentLabel.setText("Add Appointment");
                root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
                stage = (Stage) AppointmentBackButton.getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.show();
            }
        }
    }

    @FXML
    private void AppointmentCustomerTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void AppointmentBackButtonHandler(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
        stage = (Stage) AppointmentBackButton.getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
