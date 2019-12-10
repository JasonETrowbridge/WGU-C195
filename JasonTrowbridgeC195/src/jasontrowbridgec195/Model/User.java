/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195.Model;



/**
 *
 * @author Jason Trowbridge
 */

//class is used to store current user data
public class User {

    private static int userID; //auto incremented in database
    private static String username;
    private static String password;
    

    public User() {
        userID = 0;
        username = null;
        password = null;

    }

    
    //constructor
    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    //getters
    public static int getUserID() {
        return userID;
    }
    
    public static String  getUsername() {
        return username;
    }
     
    public String getPassword() {
        return this.password;
    }

    //setters
    public static void setUserID(int userID) {
        User.userID = userID;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static void setPassword(String password) {
        User.password = password;
    }
}
