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
//class used for creating reports
public class Reports {

    private String month;
    private int newAccount;
    private int consultation;
    private int followUp;
    private int closeAccount;
    private int email;
    private int phone;
    private int inperson;
    private int typesArray[];
    
    //constructors
    public Reports(String month, int typesArray[]) {
        setMonth(month);
        setTypesArray(typesArray);
    }
    
    public Reports(String month, int newAccount, int consultation, int followUp, int closeAccount){
        setMonth(month);
        setNewAccount(newAccount);
        setConsultation(consultation);
        setFollowUp(followUp);
        setCloseAccount(closeAccount);        
    }
    
    public Reports(String month, int email, int phone, int inperson){
        setMonth(month);
        setEmail(email);
        setPhone(phone);
        setInperson(inperson);
    }
    
    //getters
    public String getMonth() {
        return this.month;
    }

    public int[] getTypesArray() {
        return this.typesArray;
    }

    public int getNewAccount(){
        return this.newAccount;
    }
    
    public int getConsultation(){
        return this.consultation;
    }
    
    public int getFollowUp(){
        return this.followUp;
    }
    
    public int getCloseAccount(){
        return this.closeAccount;
    }
    
    public int getEmail(){
        return this.email;
    }
    
    public int getPhone(){
        return this.phone;
    }
    
    public int getInperson(){
        return this.inperson;
    }
    
    //setters
    private void setMonth(String month) {
        this.month = month;
    }

    private void setNewAccount(int newAccount){
        this.newAccount = newAccount;
    }
    
    private void setConsultation(int consultation){
        this.consultation = consultation;
    }
    
    private void setFollowUp(int followUp){
        this.followUp = followUp;
    }
    
    private void setCloseAccount(int closeAccount){
        this.closeAccount = closeAccount;
    }
    
    private void setEmail(int email){
        this.email = email;
    }
    
    private void setPhone(int phone){
        this.phone = phone;
    }
    
    private void setInperson(int inperson){
        this.inperson = inperson;
    }
    
    private void setTypesArray(int[] typesArray) {
        this.typesArray = typesArray;
    }
}
