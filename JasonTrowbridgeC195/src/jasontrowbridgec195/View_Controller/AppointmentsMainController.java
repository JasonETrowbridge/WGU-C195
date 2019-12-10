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
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.ResourceBundle;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jason Trowbridge
 */
public class AppointmentsMainController implements Initializable {

    @FXML
    private Button AddNewAppointmentButton;
    @FXML
    private Button UpdateAppointmentButton;
    @FXML
    private Button DeleteAppointmentButton;

    @FXML
    private Label AppointmentScreenLabel;
    @FXML
    private TableView<Appointment> AppointmentTableView;
    @FXML
    private TableColumn<Appointment, String> AppointmentStartColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentEndColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentTypeColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentCustomerColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentConsultantColumn;
    @FXML
    private RadioButton AppointmentWeekRadioButton;
    @FXML
    private RadioButton AppointmentMonthRadioButton;
    @FXML
    private Button AppointmentsBackButton;

    private ToggleGroup RadioButtonToggleGroup;
    private boolean isWeekly;
    private static Appointment updateAppointment;
    private static int updateAppointmentIndex;
    public static int boxtype; //used to identify Add/Update label on AppointmentAddController
    private Customer customer = new Customer();
    private Appointment appointment = new Appointment();

    Parent root;
    Stage stage;

    ObservableList<Appointment> appointmentsOL = FXCollections.observableArrayList();
    private static Appointment selectedAppointment = new Appointment();

    //private User currentUser;    
    private final DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //private final ZoneId localZoneID = ZoneId.of("UTC-8");
    private final ZoneId localZoneID = ZoneId.systemDefault();
    private final ZoneId utcZoneID = ZoneId.of("UTC");

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Populate CustomerTable with values        
        PropertyValueFactory<Appointment, String> apptStartFactory = new PropertyValueFactory<>("Start");
        PropertyValueFactory<Appointment, String> apptEndFactory = new PropertyValueFactory<>("End");
        PropertyValueFactory<Appointment, String> apptTitleFactory = new PropertyValueFactory<>("Title");
        PropertyValueFactory<Appointment, String> apptTypeFactory = new PropertyValueFactory<>("Type");
        PropertyValueFactory<Appointment, String> apptCustomerFactory = new PropertyValueFactory<>("CustomerName");
        PropertyValueFactory<Appointment, String> apptConsultantFactory = new PropertyValueFactory<>("CreatedBy");

        AppointmentStartColumn.setCellValueFactory(apptStartFactory);
        AppointmentEndColumn.setCellValueFactory(apptEndFactory);
        AppointmentTitleColumn.setCellValueFactory(apptTitleFactory);
        AppointmentTypeColumn.setCellValueFactory(apptTypeFactory);
        AppointmentCustomerColumn.setCellValueFactory(apptCustomerFactory);
        AppointmentConsultantColumn.setCellValueFactory(apptConsultantFactory);

        //sets togglegroup
        RadioButtonToggleGroup = new ToggleGroup();
        AppointmentWeekRadioButton.setToggleGroup(RadioButtonToggleGroup);
        AppointmentMonthRadioButton.setToggleGroup(RadioButtonToggleGroup);
        AppointmentWeekRadioButton.setSelected(true);
        AppointmentMonthRadioButton.setSelected(false);

        isWeekly = true;

        try {
            setAppointmentsTable();
        } catch (SQLException ex) {
            System.out.println("SQL error when 'setAppointmentTable' was called.");
        }

    }

    //method returns updateAppointment object that was selected for update
    public static Appointment getUpdateAppointment() {
        return updateAppointment;
    }

    public static int getUpdateAppointmentIndex() {
        return updateAppointmentIndex;
    }

    //populates table view with appointments and applies filter
    public void setAppointmentsTable() throws SQLException {
        System.out.println("**** Start Set Appointment Table ****");
        PreparedStatement ps;
        try {
            ps = ConnectDB.makeConnection().prepareStatement(
                    "SELECT appointment.appointmentId, appointment.customerId, appointment.userId, appointment.title, appointment.description, "
                    + "appointment.location, appointment.contact, appointment.type, appointment.url, appointment.start, appointment.end, "
                    + "appointment.createdBy, customer.customerId, customer.customerName "
                    + "FROM appointment, customer "
                    + "WHERE appointment.customerId = customer.customerId "
                    + "ORDER BY `start`");

            System.out.println("PreparedStament: " + ps);
            ResultSet rs = ps.executeQuery();
            System.out.println("Appointment Table query worked");
            appointmentsOL.clear();

            while (rs.next()) {

                //assigns variables with data from db for insertion into appointments observablelist
                int appointmentID = rs.getInt("appointmentId");
                int customerID = rs.getInt("customerId");
                int userID = rs.getInt("userId");
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String url = rs.getString("url");

                //get database start time stored as UTC
                String startUTC = rs.getString("start").substring(0, 19);

                //get database end time stored as UTC
                String endUTC = rs.getString("end").substring(0, 19);

                //convert database UTC to LocalDateTime
                LocalDateTime utcStartDT = LocalDateTime.parse(startUTC, datetimeDTF);
                LocalDateTime utcEndDT = LocalDateTime.parse(endUTC, datetimeDTF);

                //convert times UTC zoneId to local zoneId
                ZonedDateTime localZoneStart = utcStartDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                ZonedDateTime localZoneEnd = utcEndDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);

                //convert ZonedDateTime to a string for insertion into AppointmentsTableView
                String localStartDT = localZoneStart.format(datetimeDTF);
                String localEndDT = localZoneEnd.format(datetimeDTF);

                //get title from appointment
                String title = rs.getString("title");

                //get type from appointment
                String type = rs.getString("type");

                //put Customer data into Customer object
                Customer customer = new Customer(rs.getInt("customerId"), rs.getString("customerName"));
                String customerName = customer.getCustomerName();

                //System.out.println("Customer Name: " + customerName);
                String user = rs.getString("createdBy");

                //insert appointments into observablelist for AppointmentTableView if userName = createdBy
                appointmentsOL.add(new Appointment(appointmentID, customerID, userID, title, description, location, contact, type, url, localStartDT, localEndDT, customerName, user));
            }

            //filter appointments by week or month
            if (isWeekly) {
                filterAppointmentsByWeek(appointmentsOL);
            } else {
                filterAppointmentsByMonth(appointmentsOL);
            }

        } catch (SQLException sqe) {
            System.out.println("Update Appointment Table SQL error!");
        } catch (Exception e) {
            System.out.println("Something other than SQL has caused an error!");
        }
        System.out.println("**** End Set Appointment Table ****");
    }

    @FXML
    private void AddNewAppointmentButtonHandler(ActionEvent event) throws IOException {
        boxtype = 1;
        root = FXMLLoader.load(getClass().getResource("AppointmentAdd.fxml"));
        stage = (Stage) AddNewAppointmentButton.getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void UpdateAppointmentButtonHandler(ActionEvent event) throws IOException {
        //Check that a part has been selected
        if (AppointmentTableView.getSelectionModel().getSelectedItem() != null) {
            updateAppointment = AppointmentTableView.getSelectionModel().getSelectedItem();
            System.out.println("AppointmentID: " + updateAppointment.getAppointmentID());
            updateAppointmentIndex = appointmentsOL.indexOf(updateAppointment);
            boxtype = 2;

            //get reference to the button's stage
            root = FXMLLoader.load(getClass().getResource("AppointmentAdd.fxml"));
            stage = (Stage) UpdateAppointmentButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            System.out.println("No appointment has been selected to modify.");
        }
    }

    private void deleteAppointment(Appointment appointment) throws Exception {
        Appointment appt = appointment;
        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("DELETE appointment.* FROM appointment WHERE appointment.appointmentId = ? ");
            System.out.println("Delete appointmentID " + appt.getAppointmentID());
            ps.setInt(1, appt.getAppointmentID());
            int result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL statement contains an error!");
        }
        setAppointmentsTable();
    }

    @FXML
    private void DeleteAppointmentButtonHandler(ActionEvent event) throws Exception {
        if (AppointmentTableView.getSelectionModel().getSelectedItem() != null) {

            Appointment appt = AppointmentTableView.getSelectionModel().getSelectedItem();
            int appointmentID = appt.getAppointmentID();
            System.out.println("AppointmentID : " + appointmentID);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Required");
            alert.setHeaderText("Confirm DELETE");
            alert.setContentText("Are you sure you want to DELETE appointmentID " + appt.getAppointmentID() + " ?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                System.out.println("Deleting appointment...");
                deleteAppointment(appt);
                System.out.println("AppointmentID " + appt.getAppointmentID() + " has been deleted!");
                setAppointmentsTable();
            } else {
                System.out.println("DELETE was canceled.");
            }
        } else {
            System.out.println("No appointment was selected to delete!");
        }
    }

    @FXML
    private void AppointmentWeekRadioButtonHandler(ActionEvent event) throws SQLException, Exception {
        isWeekly = true;
        setAppointmentsTable();
    }

    @FXML
    private void AppointmentMonthRadioButtonHandler(ActionEvent event) throws SQLException, Exception {
        isWeekly = false;
        setAppointmentsTable();
    }

    public void filterAppointmentsByMonth(ObservableList appointmentsOL) throws SQLException {

        //filter appointments for month
        LocalDate now = LocalDate.now();
        LocalDate nowPlus1Month = now.plusMonths(1);

        //lambda expression used to efficiently filter appointments by month
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentsOL);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = LocalDate.parse(row.getStart(), datetimeDTF);

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Month);
        });

        AppointmentTableView.setItems(filteredData);
    }

    public void filterAppointmentsByWeek(ObservableList appointmentsOL) {
        //filter appointments for week
        LocalDate now = LocalDate.now();
        LocalDate nowPlus1Week = now.plusWeeks(1);

        //lambda expression used to efficiently filter appointments by week
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentsOL);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = LocalDate.parse(row.getStart(), datetimeDTF);

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Week);
        });
        AppointmentTableView.setItems(filteredData);
    }

    @FXML
    private void AppointmentsBackButtonHandler(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        stage = (Stage) UpdateAppointmentButton.getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

}
