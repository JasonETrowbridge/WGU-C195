/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195.View_Controller;

import jasontrowbridgec195.Model.Appointment;
import jasontrowbridgec195.Model.Customer;
import jasontrowbridgec195.Model.Reports;
import jasontrowbridgec195.Model.User;
import jasontrowbridgec195.util.ConnectDB;
import jasontrowbridgec195.util.DateTime;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Jason Trowbridge
 */
public class ReportsController implements Initializable {

    @FXML
    private AnchorPane Reports;
    @FXML
    private Tab ReportsByMonth;
    @FXML
    private TableView<Reports> ReportsByMonthTableView;
    @FXML
    private TableColumn<Reports, String> ReportsByMonthMonthColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsByMonthNewAccountColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsByMonthConsultationColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsByMonthFollowUpColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsByMonthCloseAccountColumn;

    @FXML
    private Tab ReportsSchedule;
    @FXML
    private TableView<Appointment> ReportsScheduleTableView;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleCustomerColumn;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleTitleColumn;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleTypeColumn;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleStartColumn;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleEndColumn;

    @FXML
    private Tab ReportsContactByMonth;
    @FXML
    private TableView<Reports> ReportsContactsByMonthTableView;
    @FXML
    private TableColumn<Reports, String> ReportsContactsMonthColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsContactEmailColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsContactPhoneColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsContactInpersonColumn;

    @FXML
    private Button ReportsMainMenuButton;

    Parent root;
    Stage stage;

    //creates observablelists used for the 3 available reports
    private ObservableList<Appointment> scheduleOL = FXCollections.observableArrayList();
    private ObservableList<Reports> typesByMonthOL = FXCollections.observableArrayList();
    private ObservableList<Reports> contactsByMonthOL = FXCollections.observableArrayList();
    private final DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //private final ZoneId localZoneID = ZoneId.of("UTC-8");
    private final ZoneId localZoneID = ZoneId.systemDefault();
    private final ZoneId utcZoneID = ZoneId.of("UTC");

    //create array for storing how many of each contacts are in each month
    private int monthContacts[][] = new int[][]{
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0}
    };
    
    //create array for storing how many of each appointment types are in each month
    private int monthTypes[][] = new int[][]{
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0}
    };
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Populate Consultant Schedule        
        PropertyValueFactory<Appointment, String> apptStartFactory = new PropertyValueFactory<>("Start");
        PropertyValueFactory<Appointment, String> apptEndFactory = new PropertyValueFactory<>("End");
        PropertyValueFactory<Appointment, String> apptTitleFactory = new PropertyValueFactory<>("Title");
        PropertyValueFactory<Appointment, String> apptTypeFactory = new PropertyValueFactory<>("Type");
        PropertyValueFactory<Appointment, String> apptCustomerFactory = new PropertyValueFactory<>("CustomerName");
        PropertyValueFactory<Appointment, String> apptConsultantFactory = new PropertyValueFactory<>("CreatedBy");

        //assign cell values to Schedule Report   
        ReportsScheduleStartColumn.setCellValueFactory(apptStartFactory);
        ReportsScheduleEndColumn.setCellValueFactory(apptEndFactory);
        ReportsScheduleCustomerColumn.setCellValueFactory(apptCustomerFactory);
        ReportsScheduleTitleColumn.setCellValueFactory(apptTitleFactory);
        ReportsScheduleTypeColumn.setCellValueFactory(apptTypeFactory);

        //Populate Types By Month
        PropertyValueFactory<Reports, String> monthFactory = new PropertyValueFactory<>("Month");
        PropertyValueFactory<Reports, Integer> newFactory = new PropertyValueFactory<>("NewAccount");
        PropertyValueFactory<Reports, Integer> consultationFactory = new PropertyValueFactory<>("Consultation");
        PropertyValueFactory<Reports, Integer> followUpFactory = new PropertyValueFactory<>("FollowUp");
        PropertyValueFactory<Reports, Integer> closeFactory = new PropertyValueFactory<>("CloseAccount");

        //assign cell values to Types By Month
        ReportsByMonthMonthColumn.setCellValueFactory(monthFactory);
        ReportsByMonthNewAccountColumn.setCellValueFactory(newFactory);
        ReportsByMonthConsultationColumn.setCellValueFactory(consultationFactory);
        ReportsByMonthFollowUpColumn.setCellValueFactory(followUpFactory);
        ReportsByMonthCloseAccountColumn.setCellValueFactory(closeFactory);

        //Populate Contacts By Moonth
        PropertyValueFactory<Reports, String> monthContactFactory = new PropertyValueFactory<>("Month");
        PropertyValueFactory<Reports, Integer> emailFactory = new PropertyValueFactory<>("Email");
        PropertyValueFactory<Reports, Integer> phoneFactory = new PropertyValueFactory<>("Phone");
        PropertyValueFactory<Reports, Integer> inpersonFactory = new PropertyValueFactory<>("Inperson");
        
        //assign cell values to Contacts By Month
        ReportsContactsMonthColumn.setCellValueFactory(monthContactFactory);
        ReportsContactEmailColumn.setCellValueFactory(emailFactory);
        ReportsContactPhoneColumn.setCellValueFactory(phoneFactory);
        ReportsContactInpersonColumn.setCellValueFactory(inpersonFactory);
        
        setReportsScheduleTable();
        try {
            setReportsTypeByMonthTable();
        } catch (Exception ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            setReportsContactsByMonthTable();
        } catch (Exception ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        ReportsScheduleTableView.setItems(scheduleOL);
        ReportsByMonthTableView.setItems(typesByMonthOL);
        ReportsContactsByMonthTableView.setItems(contactsByMonthOL);

    }

    @FXML
    private void ReportsMainMenuButtonHandler(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        stage = (Stage) ReportsMainMenuButton.getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    private void setReportsTypeByMonthTable() throws SQLException, Exception {
        System.out.println("**** Begin Report Type By Month ****");
        PreparedStatement ps;
        try {
            ps = ConnectDB.makeConnection().prepareStatement(
                    "SELECT * "
                    + "FROM appointment");

            System.out.println("PreparedStament: " + ps);
            ResultSet rs = ps.executeQuery();
            System.out.println("Reports By Month query worked");
            typesByMonthOL.clear();
            System.out.println("Entering While Loop");
            while (rs.next()) {
                System.out.println("Inside While Loop");
                //get database start time stored as UTC
                String startUTC = rs.getString("start").substring(0, 19);
                System.out.println("UTC Start: " + startUTC);

                //get database end time stored as UTC
                String endUTC = rs.getString("end").substring(0, 19);
                System.out.println("UTC End: " + endUTC);

                //convert database UTC to LocalDateTime
                LocalDateTime utcStartDT = LocalDateTime.parse(startUTC, datetimeDTF);
                LocalDateTime utcEndDT = LocalDateTime.parse(endUTC, datetimeDTF);

                //convert times UTC zoneId to local zoneId
                ZonedDateTime localZoneStart = utcStartDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                ZonedDateTime localZoneEnd = utcEndDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                System.out.println("localZoneStart: " + localZoneStart);
                System.out.println("localZoneEnd: " + localZoneEnd);

                //convert ZonedDateTime to a string for insertion into AppointmentsTableView
                String localStartDT = localZoneStart.format(datetimeDTF);
                String localEndDT = localZoneEnd.format(datetimeDTF);
                System.out.println("localStartDT: " + localStartDT);
                System.out.println("localEndDT: " + localEndDT);

                String monthParse = localStartDT.substring(5, 7);
                int month = Integer.parseInt(monthParse);
                System.out.println("Month parsed to Int: " + month);
                month = month - 1;
                String type = rs.getString("type");
                System.out.println("Month: " + month);
                System.out.println("Type: " + type);

                //increment array values of each type for each month
                if (month == 0) {
                    if (type.equals("New Account")) {
                        monthTypes[0][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[0][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[0][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[0][3]++;
                    }

                } else if (month == 1) {
                    if (type.equals("New Account")) {
                        monthTypes[1][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[1][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[1][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[1][3]++;
                    }
                } else if (month == 2) {
                    if (type.equals("New Account")) {
                        monthTypes[2][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[2][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[2][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[2][3]++;
                    }
                } else if (month == 3) {
                    if (type.equals("New Account")) {
                        monthTypes[3][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[3][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[3][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[3][3]++;
                    }
                } else if (month == 4) {
                    if (type.equals("New Account")) {
                        monthTypes[4][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[4][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[4][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[4][3]++;
                    }
                } else if (month == 5) {
                    if (type.equals("New Account")) {
                        monthTypes[5][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[5][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[5][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[5][3]++;
                    }
                } else if (month == 6) {
                    if (type.equals("New Account")) {
                        monthTypes[6][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[6][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[6][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[6][3]++;
                    }
                } else if (month == 7) {
                    if (type.equals("New Account")) {
                        monthTypes[7][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[7][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[7][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[7][3]++;
                    }
                } else if (month == 8) {
                    if (type.equals("New Account")) {
                        monthTypes[8][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[8][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[8][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[8][3]++;
                    }
                } else if (month == 9) {
                    if (type.equals("New Account")) {
                        monthTypes[9][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[9][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[9][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[9][3]++;
                    }
                } else if (month == 10) {
                    if (type.equals("New Account")) {
                        monthTypes[10][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[10][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[10][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[10][3]++;
                    }
                } else if (month == 11) {
                    if (type.equals("New Account")) {
                        monthTypes[11][0]++;
                    } else if (type.equals("Consultation")) {
                        monthTypes[11][1]++;
                    } else if (type.equals("Follow-Up")) {
                        monthTypes[11][2]++;
                    } else if (type.equals("Close Account")) {
                        monthTypes[11][3]++;
                    }
                }
            }
            System.out.println("Exited While Loop");
        } catch (SQLException sqe) {
            System.out.println("Reports By Month has SQL error!");
        } catch (Exception e) {
            System.out.println("Something other than SQL has caused an error!");
        }
        for (int i = 0; i < 12; i++) {
            //assign variables for insertion into typesByMonthOL
            int newAccount = monthTypes[i][0];
            int consultation = monthTypes[i][1];
            int followUp = monthTypes[i][2];
            int closeAccount = monthTypes[i][3];
            
            //prints variable contents to terminal for troubleshooting
            System.out.println("newAccount: " + newAccount);            
            System.out.println("consultation: " + consultation);            
            System.out.println("followUp: " + followUp);            
            System.out.println("closeAccount: " + closeAccount);
            
            typesByMonthOL.add(new Reports(getAbbreviatedMonth(i), newAccount, consultation, followUp, closeAccount));
            
        }
        System.out.println("**** End Report Type By Month ****");
    }
    
    private void setReportsContactsByMonthTable() throws SQLException, Exception {
        System.out.println("**** Begin Report Contacts By Month ****");
        PreparedStatement ps;
        try {
            ps = ConnectDB.makeConnection().prepareStatement(
                    "SELECT * "
                    + "FROM appointment");

            System.out.println("PreparedStament: " + ps);
            ResultSet rs = ps.executeQuery();
            System.out.println("Reports Contacts By Month query worked");
            contactsByMonthOL.clear();            
            while (rs.next()) {
                
                //get database start time stored as UTC
                String startUTC = rs.getString("start").substring(0, 19);
                System.out.println("UTC Start: " + startUTC);

                //get database end time stored as UTC
                String endUTC = rs.getString("end").substring(0, 19);
                System.out.println("UTC End: " + endUTC);

                //convert database UTC to LocalDateTime
                LocalDateTime utcStartDT = LocalDateTime.parse(startUTC, datetimeDTF);
                LocalDateTime utcEndDT = LocalDateTime.parse(endUTC, datetimeDTF);

                //convert times UTC zoneId to local zoneId
                ZonedDateTime localZoneStart = utcStartDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                ZonedDateTime localZoneEnd = utcEndDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                System.out.println("localZoneStart: " + localZoneStart);
                System.out.println("localZoneEnd: " + localZoneEnd);

                //convert ZonedDateTime to a string for insertion into AppointmentsTableView
                String localStartDT = localZoneStart.format(datetimeDTF);
                String localEndDT = localZoneEnd.format(datetimeDTF);
                System.out.println("localStartDT: " + localStartDT);
                System.out.println("localEndDT: " + localEndDT);
                
                //parse two digit value for month
                String monthParse = localStartDT.substring(5, 7);
                int month = Integer.parseInt(monthParse);               
                month = month - 1;
                String contact = rs.getString("contact");
                System.out.println("Month: " + month);
                System.out.println("Contact: " + contact);

                //increment array values of each type for each month
                if (month == 0) {
                    if (contact.equals("Email")) {
                        monthContacts[0][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[0][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[0][2]++;
                    } 

                } else if (month == 1) {
                     if (contact.equals("Email")) {
                        monthContacts[1][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[1][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[1][2]++;
                    } 
                } else if (month == 2) {
                     if (contact.equals("Email")) {
                        monthContacts[2][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[2][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[2][2]++;
                    } 
                } else if (month == 3) {
                    if (contact.equals("Email")) {
                        monthContacts[3][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[3][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[3][2]++;
                    } 
                } else if (month == 4) {
                     if (contact.equals("Email")) {
                        monthContacts[4][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[4][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[4][2]++;
                    } 
                } else if (month == 5) {
                     if (contact.equals("Email")) {
                        monthContacts[5][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[5][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[5][2]++;
                    } 
                } else if (month == 6) {
                     if (contact.equals("Email")) {
                        monthContacts[6][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[6][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[6][2]++;
                    } 
                } else if (month == 7) {
                    if (contact.equals("Email")) {
                        monthContacts[7][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[7][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[7][2]++;
                    } 
                } else if (month == 8) {
                     if (contact.equals("Email")) {
                        monthContacts[7][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[7][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[7][2]++;
                    } 
                } else if (month == 9) {
                     if (contact.equals("Email")) {
                        monthContacts[7][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[7][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[7][2]++;
                    } 
                } else if (month == 10) {
                     if (contact.equals("Email")) {
                        monthContacts[10][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[10][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[10][2]++;
                    } 
                } else if (month == 11) {
                     if (contact.equals("Email")) {
                        monthContacts[11][0]++;
                    } else if (contact.equals("Phone")) {
                        monthContacts[11][1]++;
                    } else if (contact.equals("In-person")) {
                        monthContacts[11][2]++;
                    } 
                }
            }            
        } catch (SQLException sqe) {
            System.out.println("Reports Contacts By Month SQL error!");
        } catch (Exception e) {
            System.out.println("Something other than SQL has caused an error!");
        }
        for (int i = 0; i < 12; i++) {
            //variables to insert into observablelist
            int email = monthTypes[i][0];
            int phone = monthTypes[i][1];
            int inperson = monthTypes[i][2];
            
            //print data to console prior to insertion into contactsByMonth observablelist
            System.out.println("Email: " + email);            
            System.out.println("Phone: " + phone);            
            System.out.println("In-person: " + inperson);
            
            contactsByMonthOL.add(new Reports(getAbbreviatedMonth(i), email, phone, inperson));            
        }
        System.out.println("**** End Report Contacts By Month ****");
    }

    //converts two digit month code into abbreviated month string
    private String getAbbreviatedMonth(int month) {
        String abbreviatedMonth = null;
        if (month == 0) {
            abbreviatedMonth = "JAN";
        }
        if (month == 1) {
            abbreviatedMonth = "FEB";
        }
        if (month == 2) {
            abbreviatedMonth = "MAR";
        }
        if (month == 3) {
            abbreviatedMonth = "APR";
        }
        if (month == 4) {
            abbreviatedMonth = "MAY";
        }
        if (month == 5) {
            abbreviatedMonth = "JUN";
        }
        if (month == 6) {
            abbreviatedMonth = "JUL";
        }
        if (month == 7) {
            abbreviatedMonth = "AUG";
        }
        if (month == 8) {
            abbreviatedMonth = "SEP";
        }
        if (month == 9) {
            abbreviatedMonth = "OCT";
        }
        if (month == 10) {
            abbreviatedMonth = "NOV";
        }
        if (month == 11) {
            abbreviatedMonth = "DEC";
        }
        return abbreviatedMonth;
    }

    //shows current consultant's schedule
    private void setReportsScheduleTable() {
        System.out.println("**** Begin Report Schedule Table ****");
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
            System.out.println("Report Schedule query worked");
            scheduleOL.clear();

            while (rs.next()) {

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
                System.out.println("Title: " + title);

                //get type from appointment
                String type = rs.getString("type");
                System.out.println("Type: " + type);

                //put Customer data into Customer object
                Customer customer = new Customer(rs.getInt("customerId"), rs.getString("customerName"));
                String customerName = customer.getCustomerName();

                //System.out.println("Customer Name: " + customerName);
                String user = rs.getString("createdBy");
                System.out.println("User: " + user);

                //insert appointments into observablelist for AppointmentTableView if userName = createdBy
                if (User.getUserID() == userID) {
                    scheduleOL.add(new Appointment(appointmentID, customerID, userID, title, description, location, contact, type, url, localStartDT, localEndDT, customerName, user));
                    System.out.println("Schedule add: " + scheduleOL);
                } else {
                    System.out.println("Appointment was not for current User!");
                }

            }

            //filter appointments by week or month
        } catch (SQLException sqe) {
            System.out.println("Report Schedule Table SQL error!");
        } catch (Exception e) {
            System.out.println("Something other than SQL has caused an error!");
        }
        System.out.println("**** End Report Schedule Table ****");
    }
}
