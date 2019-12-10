/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195;

import jasontrowbridgec195.Model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jasontrowbridgec195.util.ConnectDB;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jdk.nashorn.internal.objects.NativeDate.getTime;

/**
 *
 * @author Jason Trowbridge
 */
public class JasonTrowbridgeC195 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Start...");
        Parent root = FXMLLoader.load(getClass().getResource("View_Controller/Login.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
        System.out.println("End of Start section");
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws SQLException, Exception {

        ConnectDB.makeConnection();
        launch(args);
        ConnectDB.closeConnection();

    }

}
