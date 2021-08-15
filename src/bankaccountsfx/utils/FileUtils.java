package bankaccountsfx.utils;

import java.util.List;
import bankaccountsfx.model.*;
import static bankaccountsfx.utils.MessageUtils.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;


public class FileUtils {
	//Save a list of new accounts to DB (Accounts.txt)
    public static void saveAccounts(List<Account> listAccounts) throws FileNotFoundException, IOException,NullPointerException{
	   String accountsFile = "C:\\Users\\Yashas Acharya\\eclipse-workspace\\MiniProjectPersonalFinanceManager\\Accounts.txt";
       FileWriter file=new FileWriter( accountsFile);  									//creates FileWriter object
       BufferedWriter bufferedWriter = new BufferedWriter(file);						//creates bufferwriter object	
       bufferedWriter.flush();

       listAccounts.forEach( ( Account dataAccount ) -> {     							//loops through all accounts of listAccounts
    	   try {
    		   bufferedWriter.write(dataAccount.toString());							//writes the acc to dataAccount
               bufferedWriter.newLine();
    	   } 
           catch (Exception ex) {
        	   ex.printStackTrace();
             	 showError( FileUtils.class.getName(), ex.getMessage() );
           }
       });

       bufferedWriter.close();
       
   }
    
  //Get all of accounts from DB (Accounts.txt)
  	//Generic type Account
      public static List <Account> loadAccounts() throws FileNotFoundException, IOException,NullPointerException{
          String accountsFile = "C:\\Users\\Yashas Acharya\\eclipse-workspace\\MiniProjectPersonalFinanceManager\\Accounts.txt";  
          FileReader file=new FileReader( accountsFile );   							//Creates a file reader object to read the file
          BufferedReader bufferedReader=new BufferedReader( file );                     //creates bufferreader object
          String[] splitDataAccount;
          List<Account> listAccount = new ArrayList<>();								//creates an array list of type account
          String lineRead=bufferedReader.readLine();									//lineRead object initialized to line read from bufferreader 	
          
          try {
          	do{
          		if(lineRead==null) {
          			break;
          		}
          		splitDataAccount = lineRead.split( ";" );
          		listAccount.add( new Account( splitDataAccount[ 0 ],splitDataAccount[ 1 ]) );  		//appends first and second fields of account to listAccounts
          		lineRead = bufferedReader.readLine();  												//reads nextline in buffer
          	}
          	while(lineRead!=null);
          }
          catch(Exception ex) {
        	 ex.printStackTrace();
  			showError( FileUtils.class.getName(), ex.getMessage() );
          	
          }
          bufferedReader.close();
          return listAccount;
          
      }
      
    //Save a list of new transactions to DB (Transactions.txt)
      public static void saveTransactions(Transaction newTransactions) throws IOException,NullPointerException {
        String transactionsFile = "C:\\Users\\Yashas Acharya\\eclipse-workspace\\MiniProjectPersonalFinanceManager\\Transaction.txt";
        FileWriter file=new FileWriter( transactionsFile,true );
        BufferedWriter bufferedWriter=new BufferedWriter( file );
        bufferedWriter.flush();
        	try {
                bufferedWriter.write(newTransactions.toString());   //writes the newTransactions to buffer and adds new line
                bufferedWriter.newLine();
        	} 
            catch (Exception ex) {
            	ex.printStackTrace();
           	 showError( FileUtils.class.getName(), ex.getMessage() );
            }
        	 bufferedWriter.close();
        } 
      
    //Get all of transactions for an account from DB (Transactions.txt)
      @SuppressWarnings("deprecation")
	public static List<Transaction> loadTransactionsForAccount( Account account,String typeparams,String params ) throws FileNotFoundException, IOException,NullPointerException{
        String transactionsFile = "C:\\Users\\Yashas Acharya\\eclipse-workspace\\MiniProjectPersonalFinanceManager\\Transaction.txt";
        FileReader file=new FileReader( transactionsFile );
        BufferedReader bufferedReader=new BufferedReader( file );
        String[] splitDataTransaction;
        String[] splitData;
        List<Transaction> listTransaction = new ArrayList<>();
        String lineRead=bufferedReader.readLine();
        boolean flag = false;
        try {
        	do{
        		if(lineRead==null) {
        			break;
        		}
        		splitDataTransaction = lineRead.split( ";" );
        		if( account.getAccountNumber().equals( splitDataTransaction[ 0 ] ) ){
        			splitData = splitDataTransaction[ 2 ].split( "/" );
        			
        			if(typeparams!=null && params!=null) {
        				switch(typeparams) {
        				case "date":
        					flag = splitDataTransaction[2].equals(params);
        					break;
        					
        				case "description":
        					flag = splitDataTransaction[3].equals(params);
        					break;
        					
        				case "+":
        					flag = Float.parseFloat(splitDataTransaction[4]) >= 0;
        					break;
        					
        				case "-":
        					flag = Float.parseFloat(splitDataTransaction[4]) < 0;
        					break;
        				}
        				
        				if(flag) {
        					splitDataTransaction[ 2 ] = splitData[ 1 ] + "/" + splitData[ 0 ] + " / " + splitData[ 2 ];
        					listTransaction.add( new Transaction( splitDataTransaction[ 0 ],splitDataTransaction[1],new Date( splitDataTransaction[ 2 ] ),splitDataTransaction[ 3 ],Float.parseFloat( splitDataTransaction[ 4 ]))  );
        				}
        				flag=false;
        			}
        			else {
        				splitDataTransaction[ 2 ] = splitData[ 1 ] + "/" + splitData[ 0 ] + " / " + splitData[ 2 ];
        				listTransaction.add( new Transaction( splitDataTransaction[ 0 ],splitDataTransaction[1],new Date( splitDataTransaction[ 2 ] ),splitDataTransaction[ 3 ],Float.parseFloat( splitDataTransaction[ 4 ]))  );
        			}
        		}
        		
            
            lineRead = bufferedReader.readLine();
        		
        }
        while( lineRead != null );
        }
        
        catch(Exception ex) {
        	ex.printStackTrace();
          	showError( FileUtils.class.getName(), ex.getMessage() );
        }
        bufferedReader.close();
        return listTransaction;
        
      }
  }
      


