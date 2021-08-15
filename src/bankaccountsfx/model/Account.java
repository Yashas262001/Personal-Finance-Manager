package bankaccountsfx.model;

public class Account {
	String accountNumber;
    String owner;

    //Account object with all of its data
    public Account( String accountNumber, String owner ){
    	this.accountNumber = accountNumber;
        this.owner = owner;
    }
    
    //Get the account number from Account object
    public String getAccountNumber() { 
   	 return this.accountNumber; 
   	 }
    

    //Get the owner from Account object
    public String getOwner() { 
   	 return this.owner; 
   	 }
    
    //Convert it to String with ";" as the field delimiter
    public String toString(){ 
        return this.accountNumber + ";" + this.owner;
     }
}
