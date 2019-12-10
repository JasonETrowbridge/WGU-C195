/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195.Model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author Jason Trowbridge
 */
public class Appointment {
    
    private int appointmentID;//auto generated
    private int customerID;//auto generated
    private int userID;//auto generated    
    private Customer customer;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String url;
    private String startTime;
    private String endTime;
    private String customerName;    
    private Date createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    
    
    public Appointment(int appointmentID, int customerID, int userID, String title, String description, String location, 
                String contact, String type, String url, String startTime, String endTime, Date createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy){
        
        setAppointmentID(appointmentID);//auto generated
        setCustomerID(customerID);//auto generated
        setUserID(userID);//auto generated
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setContact(contact);
        setType(type);
        setUrl(url);
        setStart(startTime);
        setEnd(endTime);
        setCreateDate(createDate);
        setCreatedBy(createdBy);
        setLastUpdate(lastUpdate);
        setLastUpdatedBy(lastUpdatedBy);        
    }
    
    public Appointment(int appointmentID, String startTime, String endTime, String title, String type, int customerId, String customerName, String user){
        setAppointmentID(appointmentID);
        setStart(startTime);
        setEnd(endTime);
        setTitle(title);
        setType(type);
        setCustomerID(customerId);
        setCustomerName(customerName);
        setCreatedBy(user);
    }
    
    public Appointment(int appointmentID, int customerID, int userID, String title, String description, String location, String contact, String type, 
                String url, String startTime, String endTime, String customerName, String user){
        
        setAppointmentID(appointmentID);//auto generated
        setCustomerID(customerID);//auto generated
        setUserID(userID);//auto generated
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setContact(contact);
        setType(type);
        setUrl(url);
        setStart(startTime);
        setEnd(endTime);
        setCustomerName(customerName);
        setCreatedBy(user);
         
    }
    public Appointment(String startTime, String endTime, String title, String type, String customer, String consultant) {
        setStart(startTime); 
        setEnd(endTime);
        setTitle(title);
        setType(type);
        setCustomerName(customer);
        setCreatedBy(consultant); //consultant equals User
    }
   
    public Appointment(){
    }
        
    //getters
    public int getAppointmentID(){
        return this.appointmentID;
    }
    
    public int getCustomerID(){
        return this.customerID;
    }
    
    public int getUserID(){
        return this.userID;
    }
    
    public String getTitle(){
        return this.title;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public String getLocation(){
        return this.location;
    }
    
    public String getContact(){
        return this.contact;
    }
    
    public String getType(){
        return this.type;
    }
    
    public String getUrl(){
        return this.url;
    }
    
    public String getStart(){        
        return this.startTime;
    }
    
    public String getEnd(){       
        return this.endTime;
    }
    
    public Date getCreateDate(){
        return this.createDate;
    }
    
    public String getCreatedBy(){
        return this.createdBy;
    }
    
    public Timestamp getLastUpdate(){
        return this.lastUpdate;
    }
    
    public String getLastUpdatedBy(){
        return this.lastUpdatedBy;
    }
    
    public String getCustomerName(){
        return this.customerName;
    }
    
    public String getConsultantName(){
        return this.createdBy;
    }
     public Customer getCustomer() {
        return customer;
    }
    
    //setters
    
    private void setCustomer(Customer customer){
        this.customer = customer;
    }
    private void setAppointmentID(int appointmentID){
        this.appointmentID = appointmentID;//auto generated
    }
    
    private void setCustomerID(int customerID){
        this.customerID = customerID;//auto generated
    }
    
    private void setUserID(int userID){
        this.userID = userID;//auto generated        
    }
    
    private void setTitle(String title){
        this.title = title;
    }
    
    private void setDescription(String description){
        this.description = description;
    }
    
    private void setLocation(String location){
        this.location = location;
    }
    
    private void setContact(String contact){
        this.contact = contact;
    }
    
    private void setType(String type){
        this.type = type;
    }
    
    private void setUrl(String url){
        this.url = url;
    }
    
   
    private void setStart(String startTime){
        this.startTime = startTime;
    }
    
    private void setEnd(String endTime){
        this.endTime = endTime;
    }
    
    private void setCreateDate(Date createDate){
        this.createDate = createDate;
    }
    
    private void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }
    
    private void setLastUpdate(Timestamp lastUpdate){
        this.lastUpdate = lastUpdate;
    }
    
    private void setLastUpdatedBy(String lastUpdatedBy){
        this.lastUpdatedBy = lastUpdatedBy;
    }
    
    public void setCustomerName(String customerName){
       this.customerName = customerName;
       
    }
    
    public void setConsultant(String consultant){
        this.createdBy = consultant;
        
    }   
}