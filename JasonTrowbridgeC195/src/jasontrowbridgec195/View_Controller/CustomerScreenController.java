/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195.View_Controller;

import jasontrowbridgec195.Model.City;
import jasontrowbridgec195.Model.Country;
import jasontrowbridgec195.Model.Customer;
import jasontrowbridgec195.Model.User;
import jasontrowbridgec195.util.ConnectDB;
import static jasontrowbridgec195.util.ConnectDB.conn;
import java.io.IOException;
import static java.lang.System.load;
import java.net.URL;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jason Trowbridge
 */
public class CustomerScreenController implements Initializable {

    @FXML
    private AnchorPane CustomerAddLabel;
    @FXML
    private Label CustomerLabel;
    @FXML
    private TableView<Customer> CustomerTable;
    @FXML
    private TableColumn<Customer, Integer> CustomerCustomerIDColumn;
    @FXML
    private TableColumn<Customer, String> CustomerCustomerNameColumn;
    @FXML
    private TableColumn<Customer, String> CustomerPhoneColumn;
    @FXML
    private TextField CustomerCustomerIDTextField;
    @FXML
    private Label CustomerCustomerIDLabel;
    @FXML
    private Label CustomerCustomerNameLabel;
    @FXML
    private Label CustomerAddressLabel;
    @FXML
    private Label CustomerAddress2Label;
    @FXML
    private Label CustomerCityLabel;
    @FXML
    private Label CustomerCountryLabel;
    @FXML
    private Label CustomerPostalCodeLabel;
    @FXML
    private Label CustomerPhoneLabel;
    @FXML
    private ComboBox<String> CustomerCityComboBox;
    @FXML
    private ComboBox<String> CustomerCountryComboBox;
    @FXML
    private Button CustomerSaveButton;
    @FXML
    private Button CustomerCancelButton;
    @FXML
    private Button CustomerAddButton;    
    @FXML
    private Button CustomerDeleteButton;
    @FXML
    private TextField CustomerCustomerNameTextField;
    @FXML
    private TextField CustomerAddressTextField;
    @FXML
    private TextField CustomerAddress2TextField;
    @FXML
    private TextField CustomerPostalCodeTextField;
    @FXML
    private TextField CustomerPhoneTextField;
    @FXML
    private RadioButton CustomerActiveRadioButton;
    @FXML
    private RadioButton CustomerInactiveRadioButton;
    @FXML
    private ToggleGroup RadioButtonToggleGroup;
    @FXML
    private Button CustomerBackButton;

    Parent root;
    Stage stage;

    //create ObservableLists
    ObservableList<Customer> customerOL = FXCollections.observableArrayList();
    ObservableList<String> cityOptions = FXCollections.observableArrayList();
    ObservableList<String> countryOptions = FXCollections.observableArrayList();

    private static Customer selectedCustomer = new Customer();
    private boolean customerUpdate = false; //used to determine whether to UPDATE customer in the database
    private boolean customerAdd = false; //used to determine whether to INSERT customer in the database
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Populate CustomerTable with values
        PropertyValueFactory<Customer, String> custNameFactory = new PropertyValueFactory<>("CustomerName");
        PropertyValueFactory<Customer, String> custPhoneFactory = new PropertyValueFactory<>("CustomerPhone"); //String value "CustomerPhone" calls getCustomerPhone method
        PropertyValueFactory<Customer, Integer> custCustomerIDFactory = new PropertyValueFactory<>("CustomerID");
        CustomerCustomerIDColumn.setCellValueFactory(custCustomerIDFactory);
        CustomerCustomerNameColumn.setCellValueFactory(custNameFactory);
        CustomerPhoneColumn.setCellValueFactory(custPhoneFactory);

        CustomerCustomerIDTextField.setText("Auto Generated");

        //disable input for CustomerID since its auto-generated
        disableCustomerFields();

        try {
            RadioButtonToggleGroup = new ToggleGroup();
            CustomerActiveRadioButton.setToggleGroup(RadioButtonToggleGroup);
            CustomerInactiveRadioButton.setToggleGroup(RadioButtonToggleGroup);
            CustomerActiveRadioButton.setSelected(false);
            CustomerInactiveRadioButton.setSelected(false);
            selectedCustomer.setCustomerActive(1);

            System.out.println("Current userID: " + User.getUserID());
            System.out.println("Current Username: " + User.getUsername());
            updateCustomerTable();
            try {
                fillCityComboBox();
            } catch (Exception ex) {
                Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                fillCountryComboBox();
            } catch (Exception ex) {
                Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CustomerScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Listen for mouse click on item in Customer Table
        CustomerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        customerListener(newValue);
                    } catch (Exception ex) {
                        System.out.println("Customer Listener had an error!");
                    }
                });
    }

    public void updateCustomerTable() throws SQLException {
        System.out.println("***** Begin Update Customer Table *****");
        customerOL.clear();
        //create statement object
        Statement stmt = ConnectDB.conn.createStatement();

        //Write SQL statement (columns from two tables)
        String sqlStatement = "SELECT customerId, customerName, phone,  active FROM customer, address WHERE customer.addressId = address.addressId ORDER BY customer.customerName";

        //execute statement and create resultset object
        ResultSet result = stmt.executeQuery(sqlStatement);

        //get all records from resultset object
        while (result.next()) {
            Customer cust = new Customer();
            cust.setCustomerID(result.getInt("customerId"));
            cust.setCustomerName(result.getString("customerName"));
            cust.setCustomerPhone(result.getString("phone"));            
            customerOL.addAll(cust);
        }
        CustomerTable.setItems(customerOL);
        System.out.println("***** End Update Customer Table *****");
    }

    public void clearTextFields() {
        CustomerLabel.setText("");
        CustomerCustomerIDTextField.setText("");
        CustomerCustomerNameTextField.setText("");
        CustomerAddressTextField.setText("");
        CustomerAddress2TextField.setText("");
        CustomerCityComboBox.setValue("");
        CustomerCountryComboBox.setValue("");
        CustomerPostalCodeTextField.setText("");
        CustomerPhoneTextField.setText("");
        CustomerInactiveRadioButton.setSelected(false);
        CustomerActiveRadioButton.setSelected(false);
    }

    //populate CityComboBox with all available cities
    public void fillCityComboBox() throws SQLException, Exception {
        //Write SQL statement 
        String sqlStatement = "SELECT city FROM city";

        //create statement object
        PreparedStatement pst = ConnectDB.makeConnection().prepareStatement(sqlStatement);

        //execute statement and create resultset object
        ResultSet result = pst.executeQuery(sqlStatement);

        //get all records from resultset obj
        while (result.next()) {
            Customer cust = new Customer();
            cust.setCustomerCity(result.getString("city"));
            cityOptions.add(cust.getCustomerCity());
            CustomerCityComboBox.setItems(cityOptions);

        }
        pst.close();
        result.close();
    }

    //populate CountryComboBox with all available countries
    public void fillCountryComboBox() throws SQLException, Exception {
        //create statement object
        Statement stmt = ConnectDB.makeConnection().createStatement();

        //Write SQL statement (columns from two tables)
        String sqlStatement = "SELECT country FROM country";

        //execute statement and create resultset object
        ResultSet result = stmt.executeQuery(sqlStatement);

        //get all records from resultset obj
        while (result.next()) {
            Customer cust = new Customer();
            cust.setCustomerCountry(result.getString("country"));
            countryOptions.add(cust.getCustomerCountry());
            CustomerCountryComboBox.setItems(countryOptions);
        }
        stmt.close();
        result.close();
    }

    @FXML
    private void CustomerCustomerIDTextFieldHandler(ActionEvent event) {

    }

    @FXML
    private void CustomerCustomerNameTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerAddressTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerAddress2TextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerCityComboBoxHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerCountryComboBoxHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerPostalCodeTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerPhoneTextFieldHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerSaveButtonHandler(ActionEvent event) throws Exception {
        System.out.println("CustomerAdd: " + customerAdd);
        System.out.println("CustomerUpdate: " + customerUpdate);
        if (CustomerCustomerNameTextField.getText() != null && customerAdd || customerUpdate) {
            if (validCustomer()) {
                if (customerAdd) {
                    saveCustomer();
                    clearTextFields();
                    updateCustomerTable();
                } else if (customerUpdate) {
                    updateCustomer();
                    clearTextFields();
                    updateCustomerTable();
                }
            }
        } else {
            System.out.println("No customer selected to save!");
        }

    }

    //saves new customer
    private void saveCustomer() throws Exception {

        System.out.println("***** Begin Save Customer *****");
        try {
            int newAddressID = -1;
            //Insert new address into DB
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, CustomerAddressTextField.getText());
            ps.setString(2, CustomerAddress2TextField.getText());
            ps.setInt(3, getCityID(CustomerCityComboBox.getValue()));
            ps.setString(4, CustomerPostalCodeTextField.getText());
            ps.setString(5, CustomerPhoneTextField.getText());
            ps.setString(6, User.getUsername());
            ps.setString(7, User.getUsername());
            int result = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                newAddressID = rs.getInt(1);
                System.out.println("Generated AddressId: " + newAddressID);
            }
            System.out.println("Address section saved successfully!");
            System.out.println("Customer Active if value is 1: " + selectedCustomer.getCustomerActive());

            //Insert new customer into DB 
            PreparedStatement psc = ConnectDB.makeConnection().prepareStatement("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");

            psc.setString(1, CustomerCustomerNameTextField.getText());
            psc.setInt(2, newAddressID);
            psc.setInt(3, selectedCustomer.getCustomerActive());
            psc.setString(4, User.getUsername());
            psc.setString(5, User.getUsername());
            int results = psc.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL statement has an error!");
        }
        clearTextFields();
        disableCustomerFields();
        updateCustomerTable();
        customerAdd = false;
        customerUpdate = false;
        System.out.println("***** End Save Customer *****");
    }

    private void deleteCustomer(Customer customer) throws Exception {
        System.out.println("***** Begin Delete Customer *****");
        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("DELETE customer.*, address.* from customer, address WHERE customer.customerId = ? AND customer.addressId = address.addressId");
            System.out.println("Delete CustomerID: " + customer.getCustomerID());
            ps.setInt(1, customer.getCustomerID());
            int result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete Customer SQL statement contains an error!");
        }
        clearTextFields();
        disableCustomerFields();
        updateCustomerTable();
        System.out.println("***** End Delete Customer *****");
    }
    
    //updates current customer with all changes
    private void updateCustomer() throws Exception {

        System.out.println("***** Begin Update Customer *****");
        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("UPDATE address, customer, city, country "
                    + "SET address = ?, address2 = ?, address.cityId = ?, postalCode = ?, phone = ?, address.lastUpdate = CURRENT_TIMESTAMP, address.lastUpdateBy = ? "
                    + "WHERE customer.customerId = ? AND customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId");

            ps.setString(1, CustomerAddressTextField.getText());
            ps.setString(2, CustomerAddress2TextField.getText());
            ps.setInt(3, getCityID(CustomerCityComboBox.getValue()));
            ps.setString(4, CustomerPostalCodeTextField.getText());
            ps.setString(5, CustomerPhoneTextField.getText());
            ps.setString(6, User.getUsername());
            ps.setString(7, CustomerCustomerIDTextField.getText());
            System.out.println("CustomerID for Update: " + CustomerCustomerIDTextField.getText());

            int result = ps.executeUpdate();

            PreparedStatement psc = ConnectDB.makeConnection().prepareStatement("UPDATE customer, address, city "
                    + "SET customerName = ?, customer.active = ?, customer.lastUpdate = CURRENT_TIMESTAMP, customer.lastUpdateBy = ? "
                    + "WHERE customer.customerId = ? AND customer.addressId = address.addressId AND address.cityId = city.cityId");

            psc.setString(1, CustomerCustomerNameTextField.getText());
            psc.setInt(2, selectedCustomer.getCustomerActive());
            System.out.println("Customer Active (0 for false, 1 for true): " + selectedCustomer.getCustomerActive());
            psc.setString(3, User.getUsername());
            psc.setString(4, CustomerCustomerIDTextField.getText());
            int results = psc.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Update Customer SQL statement has an error!");
        }
        clearTextFields();
        disableCustomerFields();
        updateCustomerTable();
        customerAdd = false;
        customerUpdate = false;
        System.out.println("***** End Update Customer *****");
    }

    //checks to see if Customer data fields are valid before save/update
    private boolean validCustomer() {

        String customerName = CustomerCustomerNameTextField.getText().trim();
        String address = CustomerAddressTextField.getText().trim();
        String address2 = CustomerAddress2TextField.getText().trim();
        String city = CustomerCityComboBox.getValue().trim();
        String country = CustomerCountryComboBox.getValue().trim();
        String postalCode = CustomerPostalCodeTextField.getText().trim();
        String phone = CustomerPhoneTextField.getText().trim();
        boolean active = CustomerActiveRadioButton.isSelected();
        boolean inactive = CustomerInactiveRadioButton.isSelected();

        String errorMessage = "";
        //first checks to see if inputs are null
        if (!active & !inactive) {
            errorMessage += "You must select Active or Inactive.\n";
        }
        if (customerName == null || customerName.length() == 0) {
            errorMessage += "Please enter Customer Name.\n";
        }
        if (address == null || address.length() == 0) {
            errorMessage += "Please enter an address.\n";
        }
        if (address2.trim() == null || address2.length() == 0) {
            errorMessage += "Please enter N/A if there is no additional address.\n";
        }
        if (city == null) {
            errorMessage += "Please Select a City.\n";
        }
        if ((city.equals("Phoenix") || city.equals("New York") || city.equals("Tampa") || city.equals("Orlando") || city.equals("Dallas")) && (country.equals("United Kingdom"))){
            errorMessage += "These cities are located in the United States, not the United Kingdom.";
        }
        if ((city.equals("London") || city.equals("Liverpool")) && (country.equals("United States"))){
            errorMessage += "These cities are located in the United Kingdom, not the United States.";
        }
        if (country == null) {
            errorMessage += "Please Select a Country.\n";
        }
        if (postalCode == null || postalCode.length() == 0) {
            errorMessage += "Please enter a valid Postal Code.\n";
        } else if (postalCode.length() > 10 || postalCode.length() < 5) {
            errorMessage += "Postal Code must be between 5 and 10 characters.\n";
        }
        if (phone == null || phone.length() == 0) {
            errorMessage += "Please enter a Phone Number (including Area Code).";
        } else if (phone.length() < 10 || phone.length() > 15) {
            errorMessage += "Please enter a valid phone number (including Area Code).\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Invalid Customer");
            alert.setContentText(errorMessage);
            Optional<ButtonType> result = alert.showAndWait();

            return false;
        }
    }

    private int getCityID(String city) throws SQLException, Exception {
        int cityID = -1;

        //create statement object
        Statement statement = ConnectDB.makeConnection().createStatement();

        //write SQL statement
        String sqlStatement = "SELECT cityID FROM city WHERE city.city ='" + city + "'";

        //create resultset object
        ResultSet result = statement.executeQuery(sqlStatement);

        while (result.next()) {
            cityID = result.getInt("cityId");
        }
        return cityID;
    }

    //prevents inputs in customer fields when not adding or updating a customer
    public void disableCustomerFields(){
        CustomerActiveRadioButton.setDisable(true);
        CustomerInactiveRadioButton.setDisable(true);
        CustomerCustomerIDTextField.setDisable(true);
        CustomerCustomerNameTextField.setDisable(true);
        CustomerAddressTextField.setDisable(true);
        CustomerAddress2TextField.setDisable(true);
        CustomerCityComboBox.setDisable(true);
        CustomerCountryComboBox.setDisable(true);
        CustomerPostalCodeTextField.setDisable(true);
        CustomerPhoneTextField.setDisable(true);
        CustomerSaveButton.setDisable(true);
        CustomerCancelButton.setDisable(true);
        CustomerDeleteButton.setDisable(true);
    }
    
    //allows inputs to add/update customer
    public void enableCustomerFields(){
        CustomerActiveRadioButton.setDisable(false);
        CustomerInactiveRadioButton.setDisable(false);
        CustomerCustomerIDTextField.setDisable(false);
        CustomerCustomerIDTextField.setEditable(false);
        CustomerCustomerNameTextField.setDisable(false);
        CustomerAddressTextField.setDisable(false);
        CustomerAddress2TextField.setDisable(false);
        CustomerCityComboBox.setDisable(false);
        CustomerCountryComboBox.setDisable(false);
        CustomerPostalCodeTextField.setDisable(false);
        CustomerPhoneTextField.setDisable(false);
        CustomerSaveButton.setDisable(false);
        CustomerCancelButton.setDisable(false);
        CustomerDeleteButton.setDisable(false);
    }
    
    //method is called when customer table listener detects customer selection to update customer
    public void customerListener(Customer customer) throws SQLException, Exception {

        System.out.println("***** Begin Customer Listener *****");
        
        Customer cust = new Customer();
        cust = customer;
        String custName = cust.getCustomerName();
        int custId = cust.getCustomerID();
        ObservableList<Customer> customerOL = FXCollections.observableArrayList();

        CustomerLabel.setText("Update");
        customerUpdate = true;
        customerAdd = false;
        enableCustomerFields();

        //create statement object
        PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("SELECT * FROM customer, address, city, country "
                + "WHERE customer.customerId = ? AND customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId");        
        
        //execute statement and create resultset object
        ps.setInt(1, custId);
        ResultSet result = ps.executeQuery();
        System.out.println("SQL Statement: " + ps);
        while (result.next()) {
            System.out.println("CustomerID: " + result.getInt("customerID"));

            CustomerCustomerIDTextField.setText(Integer.toString(result.getInt("customerId")));
            CustomerCustomerNameTextField.setText(result.getString("customerName"));
            CustomerAddressTextField.setText(result.getString("address"));
            CustomerAddress2TextField.setText(result.getString("address2"));
            CustomerCityComboBox.setValue(result.getString("city"));
            CustomerCountryComboBox.setValue(result.getString("country"));
            CustomerPostalCodeTextField.setText(result.getString("postalCode"));
            CustomerPhoneTextField.setText(result.getString("phone"));
            if (result.getInt("active") == 0) {
                System.out.println("Active: " + result.getInt("active"));
                CustomerActiveRadioButton.setSelected(false);
                CustomerInactiveRadioButton.setSelected(true);
            } else {
                System.out.println("Active: " + result.getInt("active"));
                CustomerActiveRadioButton.setSelected(true);
                CustomerInactiveRadioButton.setSelected(false);
            }

            System.out.println("***** End Customer Listener *****");
        }
    }

    @FXML
    private void CustomerCancelButtonHandler(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Required");
        alert.setHeaderText("Confirm Cancel");
        alert.setContentText("Are you sure you want to cancel?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {

            customerUpdate = false;
            customerAdd = false;
            clearTextFields();
            disableCustomerFields();
        } else {
            System.out.println("Cancel canceled.");
        }
    }

    @FXML
    private void CustomerAddButtonHandler(ActionEvent event) throws SQLException {
        clearTextFields();
        CustomerLabel.setText("Add");
        CustomerCustomerIDTextField.setText("Auto Generated");
        customerAdd = true;
        customerUpdate = false;
        enableCustomerFields();

    }

    @FXML
    private void CustomerDeleteButtonHandler(ActionEvent event
    ) throws Exception {
        if (CustomerTable.getSelectionModel().getSelectedItem() != null) {

            Customer cust = CustomerTable.getSelectionModel().getSelectedItem();
            String custName = cust.getCustomerName();
            String custPhone = cust.getCustomerPhone();
            System.out.println("Name: " + custName);
            System.out.println("Phone: " + custPhone);
            System.out.println("CustomerID : " + cust.getCustomerID());

            //String customerName = customer.getCustomerName();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Required");
            alert.setHeaderText("Confirm DELETE");
            alert.setContentText("Are you sure you want to DELETE customerID: " + cust.getCustomerID() + " ?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                System.out.println("Deleting customer...");
                deleteCustomer(cust);
                System.out.println("CustomerID: " + cust.getCustomerID() + " has been deleted!");

                clearTextFields();
                disableCustomerFields();
                updateCustomerTable();
            } else {
                System.out.println("DELETE was canceled.");
            }
        } else {
            System.out.println("No customer was selected to delete!");
        }
    }

    
    @FXML
    private void CustomerActiveRadioButtonHandler(ActionEvent event
    ) {
        selectedCustomer.setCustomerActive(1);
        System.out.println("Active: " + selectedCustomer.getCustomerActive());
    }

    @FXML
    private void CustomerInactiveRadioButtonHandler(ActionEvent event
    ) {
        selectedCustomer.setCustomerActive(0);
        System.out.println("Active: " + selectedCustomer.getCustomerActive());
    }

    @FXML
    private void CustomerBackButtonHandler(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        stage = (Stage) CustomerBackButton.getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

}
