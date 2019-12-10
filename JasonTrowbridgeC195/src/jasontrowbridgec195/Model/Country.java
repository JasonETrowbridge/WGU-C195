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
public class Country {
    private int countryID = 0;//auto generated
    private String country;
    private Date createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    
    public Country(int countryID, String country, Date createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy){
        setCountryID(countryID);//auto generated
        setCountry(country);
        setCreateDate(createDate);
        setCreatedBy(createdBy);
        setLastUpdate(lastUpdate);
        setLastUpdatedBy(lastUpdatedBy);
    }
    
    //getters
    
    public int getCountryID(){
        return countryID;
    }
    
    public String getCountry(){
        return country;        
    }
    
    public Date getCreateDate(){
        return createDate;
    }
    
    public String getCreatedBy(){
        return createdBy;
    }
    
    public Timestamp getLastUpdate(){
        this.lastUpdate = Timestamp.valueOf(LocalDateTime.of(lastUpdate.toLocalDateTime().toLocalDate(), lastUpdate.toLocalDateTime().toLocalTime()));
        return this.lastUpdate;
    }
    
    public String getLastUpdatedBy(){
        return lastUpdatedBy;
    }
    
    //setters
    
    private void setCountryID(int countryID){
        this.countryID = 0;//auto generated
    }
    
    private void setCountry(String country){
        this.country = country;
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
