/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jasontrowbridgec195.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Jason Trowbridge
 */
public class City {
    
    private IntegerProperty cityId;
    private StringProperty city;
    private IntegerProperty countryId;
    private DateFormat createDate;
    private StringProperty createdBy;
    private DateFormat lastUpdate;
    private StringProperty lastUpdateBy;
    
    City(int cityId, String city, int countryId, Date createDate, String createdBy, Date lastUpdate, String lastUpdateBy){
        this.cityId = new SimpleIntegerProperty(cityId);
        this.city = new SimpleStringProperty(city);
        this.countryId = new SimpleIntegerProperty(countryId);
       // this.createDate = new DateFormat(createDate);
        this.createdBy = new SimpleStringProperty(createdBy);
        this.lastUpdateBy = new SimpleStringProperty(lastUpdateBy);
        
    }
    
    //getters
    public IntegerProperty getCityIdProperty(){
        return cityId;
    }
    
}
