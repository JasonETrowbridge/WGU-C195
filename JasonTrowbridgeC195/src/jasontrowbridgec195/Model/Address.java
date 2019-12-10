/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195.Model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

        
/**
 *
 * @author Jason Trowbridge
 */
public class Address {
    private int addressID = 0;//auto generated
    private String address;
    private String address2;
    private int cityID = 0;//auto generated
    private String postalCode;
    private String phone;
    private Date createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    
    public Address(int addressID, String address, String address2, int cityID, String postalCode, String phone, Date createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy){
        
        setAddressID(addressID);//auto generated
        setAddress(address);
        setAddress2(address2);
        setCityID(cityID);//auto generated
        setPostalCode(postalCode);
        setPhone(phone);
        setCreateDate(createDate);
        setCreatedBy(createdBy);
        setLastUpdate(lastUpdate);
        setLastUpdatedBy(lastUpdatedBy);
                
    }
    
    //getters
    
    public int getAddressID(){
        return this.addressID;
    }
    
    public String getAddress(){
        return this.address;
    }
    
    public String getAddress2(){
        return this.address2;
    }
    
    public int getCityID(){
        return this.cityID;        
    }
    
    public String getPostalCode(){
        return this.postalCode;
    }
    
    public String getPhone(){
        return this.phone;
    }
    
    public Date getCreateDate(){
        return this.createDate;                
    }  
    
    public String getCreatedBy(){
        return this.createdBy;
    }
    
    public Timestamp getLastUpdate(){
        this.lastUpdate = Timestamp.valueOf(LocalDateTime.of(lastUpdate.toLocalDateTime().toLocalDate(), lastUpdate.toLocalDateTime().toLocalTime()));
        return this.lastUpdate;
    }
    
    public String getLastUpdatedBy(){
        return this.lastUpdatedBy;
    }
    
    //setters
    
    private void setAddressID(int addressID){
        this.addressID = 0;//auto generated
    }
    
    private void setAddress(String address){
        this.address = address;
    }
    
    private void setAddress2(String address2){
        this.address2 = address2;
    }
    
    private void setCityID(int cityID){
        this.cityID = 0; //auto generated
    }
    
    private void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }
    
    private void setPhone(String phone){
        this.phone = phone;
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
}
