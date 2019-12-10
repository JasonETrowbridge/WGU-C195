/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author Jason Trowbridge
 */
public class ConnectDB {
    
    //variables used to connect to database
    private static final String databaseName = "U04NZH";
    private static final String DB_URL = "jdbc:mysql://52.206.157.109:3306/" + databaseName;
    private static final String username = "U04NZH";
    private static final String password = "53688291096";
    private static final String driver = "com.mysql.jdbc.Driver";
    public static Connection conn;
    
    //establishes connection to the database 
    public static Connection makeConnection() throws ClassNotFoundException, SQLException, Exception{
        Class.forName(driver);
        conn = DriverManager.getConnection(DB_URL, username, password);
        System.out.println("Connection successful.");
        return conn;
    }
    
    //closes database connection
    public static void closeConnection() throws SQLException{
        conn.close();
        System.out.println("Connection closed.");
    }
}