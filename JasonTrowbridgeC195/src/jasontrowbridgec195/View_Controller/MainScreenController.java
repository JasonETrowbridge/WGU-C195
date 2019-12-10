/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195.View_Controller;

import jasontrowbridgec195.util.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakev
 */
public class MainScreenController implements Initializable {

    @FXML
    private AnchorPane MainScreenLabel;
    @FXML
    private Button MainCustomersButton;
    @FXML
    private Button MainAppointmentsButton;
    @FXML
    private Button MainReportsButton;
    @FXML
    private Button MainExitButton;
    
    Parent root;
    Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void MainCustomersButtonHandler(ActionEvent event) throws IOException {
            root = FXMLLoader.load(getClass().getResource("CustomerScreen.fxml"));
            stage = (Stage)MainCustomersButton.getScene().getWindow();
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    private void MainAppointmentsButtonHandler(ActionEvent event) throws IOException {
            root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
            stage = (Stage)MainCustomersButton.getScene().getWindow();
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    private void MainReportsButtonHandler(ActionEvent event) throws IOException {
            root = FXMLLoader.load(getClass().getResource("Reports.fxml"));
            stage = (Stage)MainCustomersButton.getScene().getWindow();
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    private void MainExitButtonHandler(ActionEvent event) throws SQLException {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);            
            alert.setTitle("Confirmation Required");
            alert.setHeaderText("Confirm Exit");
            alert.setContentText("Are you sure you want to exit?");
            Optional<ButtonType> result = alert.showAndWait();
       
            if (result.get() == ButtonType.OK) {         
                ConnectDB.closeConnection();
                System.out.println("Program Exit.");
                System.exit(0);
            } 
            else{
                System.out.println("Exit canceled.");
            }        
    }
    
}
