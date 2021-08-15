package bankaccountsfx.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction extends Account {

    Date date;
    String description;
    float amount;
    
   //Transaction object with all of its data
    public Transaction(String accountNumber,String owner,Date date,String description,float amount) {
    	super(accountNumber,owner);
        this.date = date;
        this.description = description;
        this.amount = amount;
    }
    
    //Save a new transaction data in Transaction object
    public void setDate( Date newDate ){ 
    	this.date = newDate; 
    }
    
    
     //Get the transaction data in format (DD/MM/YYYY) from Transaction object
     public String getDate() { 
        DateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        return dateFormat.format( this.date ); 
     } 
    
    //Save a new transaction description in Transaction object
    public void setDescription( String newDescription ){ 
    	this.description = newDescription; 
    }
    
    //Get the transaction description from Transaction object
    public String getDescription() { 
    	return this.description; 
    }
    
    //Save a new transaction amount in Transaction object
     public void setAmount( Float newAmount ){ 
    	 this.amount = newAmount; 
     }
    
    //Get the transaction amount from Transaction object
     public Float getAmount() { 
    	 return this.amount; 
     }
    
    //Convert it to String with ";" as the field delimiter
    @Override
    public String toString(){ 
       return this.accountNumber +  ";" + this.owner + ";" + getDate() + ";" + this.description +  ";" +  this.amount;
    }
    
}


