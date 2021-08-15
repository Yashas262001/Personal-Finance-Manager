package bankaccountsfx;

import bankaccountsfx.model.*;
import bankaccountsfx.utils.*;

//import static accountsfx.utils.MessageUtils.showError;
import static bankaccountsfx.utils.FileUtils.*;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class FXMLDocumentController implements Initializable{    //Initializable and the method  are used when you want to interact with stuff injected with @FXML.
		
		//The @FXML annotation enables an FXMLLoader to inject values defined in an FXML file into references in the controller class.
	 	@FXML
	    private TextField accountNumber;
	    @FXML
	    private TextField owner;
	    @FXML
	    private Label totalSalary;
	    @FXML
	    private Button btnAccountAdd;
	    @FXML
	    private ComboBox <Account> cmbAccount;
	    @FXML
	    private TextField transactionDescription;
	    @FXML
	    private TextField transactionAmount;
	    @FXML
	    private DatePicker transactionDate;
	    @FXML
	    private Button btnTransactionAdd;
	    @FXML
	    private Button btnTransactionChart;
	    @FXML
	    private ComboBox <String> cmbFilter;
	    @FXML
	    private TableView <Transaction> tableTransactions;
	    public List <Account> listAccounts;
	    public List <Transaction> listTransactions;
	    @FXML
	    private Label textTotal;
	    @FXML
	    private AnchorPane paneData;
	    @FXML
	    private AnchorPane panePie;
	    @FXML
	    private VBox vBox;
	    @FXML
	    private PieChart balanceChartPositive;
	    @FXML
	    private PieChart balanceChartNegative;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {        
		try {
			vBox.setStyle("-fx-background-color: grey");
            btnAccountAdd.setStyle("-fx-text-fill: white; -fx-background-color: black ");
            btnTransactionAdd.setStyle("-fx-text-fill: white; -fx-background-color: black");
            btnTransactionChart.setStyle("-fx-text-fill: white; -fx-background-color: black");
            btnTransactionAdd.setDisable( true );
            btnTransactionChart.setDisable( true );
            transactionDate.setDisable( true );
            transactionDescription.setDisable( true );
            transactionAmount.setDisable( true );
            cmbFilter.setDisable( true );
            
            cmbAccount.setPromptText("Account Details");
            
            //Add filters
            cmbFilter.getItems().add( "Options" );
            cmbFilter.getItems().add( "Date" );
            cmbFilter.getItems().add( "Description" );
            cmbFilter.getItems().add( "Income" );
            cmbFilter.getItems().add( "Expenses" );
            cmbFilter.getSelectionModel().selectFirst();
             
          //Save a new account with form data
            btnAccountAdd.setOnAction( ( ActionEvent e ) -> {
                saveNewAccount();
            });
            
           //Shows all of Accounts in a combo box
            listAccounts = loadAccounts();
            cmbAccount.getItems().addAll( listAccounts );
            
          //Save a new transaction with form data
            btnTransactionAdd.setOnAction( ( ActionEvent e ) -> {
                if( transactionDate.getValue() == null ) {
                	showError( "Error!", "Transaction's date is required." );

                }
                else if( transactionDescription.getText().trim().equals( "" ) ){
                	showError( "Error!", "Transaction's description is required." );

                }
                else if( transactionAmount.getText().trim().equals( "" ) ){
                	showError( "Error!", "Transaction's amount is required." );

                }
                else{
                	
                    try {
						saveNewTransaction( cmbAccount.getValue(), transactionDate, transactionDescription.getText().trim(), Float.parseFloat( transactionAmount.getText().trim() ) );
					} 
                    catch (NumberFormatException | IOException ex) {
                    	 ex.printStackTrace();
                    	 showError( FileUtils.class.getName(), ex.getMessage() );
                    }
                }
            });
           
            //Filter Transaction according to the Option selected
            cmbFilter.setOnAction((ActionEvent e) ->{
            	try {
            		if(cmbAccount.getValue() == null || cmbAccount.getValue().toString().trim().equals("") && !cmbFilter.getValue().equals("Options")) {
            			showError("Error!!","An account has to be selected");
            		}
            		else {
            			filterTransaction();
            		}
            	}
            	catch(Exception ex) {
            		ex.printStackTrace();
            	}
            });
            
            
          //Filter Transactions according to the Option selected(Date)
            transactionDate.setOnAction((ActionEvent e) ->{
            	try {
            		if(cmbAccount.getValue()!=null && cmbFilter.getValue().trim().equals("Date")) {
            			filterTransaction();
            		}
            	}
            	catch(Exception ex) {
            		ex.printStackTrace();
            	}
            });
            
            //Filter transactions according to the Option selected(Description)
            transactionDescription.setOnAction((ActionEvent e) -> {
            	try {
            		if(cmbAccount.getValue()!=null && cmbFilter.getValue().trim().equals("Description")) {
            			filterTransaction();
            		}
            	}
            	catch(Exception ex) {
            		ex.printStackTrace();
            	}
            });
		  
          //Shows all of Transactions for an account in a combo box and a table
            cmbAccount.setOnAction((ActionEvent e) -> {
               try {
                   listTransactions = loadTransactionsForAccount( cmbAccount.getValue() ,null,null);
                   createTable();
                   btnTransactionAdd.setDisable( false );
                   btnTransactionChart.setDisable( false );
                   transactionDate.setDisable( false );
                   transactionDescription.setDisable( false );
                   transactionAmount.setDisable( false );
                   cmbFilter.setDisable( false );
                   
               } 
               catch ( IOException | NullPointerException ex ) {
            	   ex.printStackTrace();
              	 showError( FileUtils.class.getName(), ex.getMessage() );
                   
               }
           });
            
        //When the Chart button is pressed, two Pie-Charts will be displayed, one for Income and other for Expense    
		btnTransactionChart.setOnAction( ( ActionEvent e ) -> {
        	try{
                ObservableList<PieChart.Data> pieChartDataPositive = FXCollections.observableArrayList();
                ObservableList<PieChart.Data> pieChartDataNegative = FXCollections.observableArrayList();
                HashMap<String,Float> hashChart = new HashMap<>();
                listTransactions.forEach( transaction -> {
                    if( !hashChart.isEmpty() && hashChart.containsKey( transaction.getDescription() ) ){
                        hashChart.replace( transaction.getDescription() , hashChart.get( transaction.getDescription() ) , hashChart.get( transaction.getDescription() ) + transaction.getAmount() );
                       
                    }
                    else{
                        hashChart.put(transaction.getDescription(), transaction.getAmount() );
                    }
                });
                hashChart.forEach( ( keyHash, valueHash ) -> {
                    float amount = valueHash;
                    if( amount < 0 ){ 
                        amount = amount * -1; 
                        pieChartDataNegative.add( new PieChart.Data( keyHash, amount ) );
                    }
                    else{
                        pieChartDataPositive.add( new PieChart.Data( keyHash, amount ) );
                    }
                });
                
                balanceChartPositive.setTitle( "Income " );
                balanceChartNegative.setTitle( "Expenses" );
                balanceChartPositive.setData(pieChartDataPositive);
                balanceChartNegative.setData(pieChartDataNegative);
                
            }
        	catch(NullPointerException ex){
                Logger
                .getLogger( FileUtils.class.getName() )
                .log( Level.SEVERE, null, e );
                showError( FileUtils.class.getName(), ex.getMessage() );
            }
        });
        
    }
    catch( Exception e ){
        Logger
        .getLogger( FileUtils.class.getName() )
        .log( Level.SEVERE, null, e );
        showError( FileUtils.class.getName(), e.getMessage() );
                
    }
}    	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createTable(){
        TableColumn<Transaction, String> dateColumn = new TableColumn( "Date" );
        dateColumn.setPrefWidth( 120 );
        dateColumn.setStyle("-fx-font-size:14");
        dateColumn.setCellValueFactory(new PropertyValueFactory( "date" ));
        TableColumn<Transaction, Float> descriptionColumn = new TableColumn( "Description" );
        descriptionColumn.setPrefWidth( 340 );
        descriptionColumn.setStyle("-fx-font-size:14");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory( "description" ));
        TableColumn amountColumn = new TableColumn( "Amount" );
        amountColumn.setPrefWidth( 135 );
        amountColumn.setStyle("-fx-font-size:14");
        amountColumn.setCellValueFactory(new PropertyValueFactory( "amount" ));
        tableTransactions.getColumns().clear();
        tableTransactions.getItems().clear();
        tableTransactions.getColumns().addAll( dateColumn, descriptionColumn, amountColumn );
        tableTransactions.getItems().addAll( listTransactions );
         
        totalSalary.setText( getTotalAmount( listTransactions ).toString() + " Rs " );  // Set new balance
        
        amountColumn.setCellFactory(e -> new TableCell<Transaction, Float>() {   //Set colors to the transaction distinguishing between income and expense
            
        	@Override
            public void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } 
                else {
                    setText(item.toString());
                    if(item >= 0 ){
                	   this.setStyle( "-fx-text-fill: white; -fx-background-color: green; -fx-alignment: center;" );
                    }
                   else{
                	   this.setStyle( "-fx-text-fill: white; -fx-background-color: red; -fx-alignment: center-right;" );
                    }
                }
            }
        });
	}
		
		private void saveNewAccount(){
	        if( accountNumber.getText().trim().equals( "" ) ){
	            showError( "Error!", "Account number is required." );

	        }
	        else if( owner.getText().trim().equals( "" ) ){
	        	showError( "Error!", "Owner is required." );

	        }
	        else{
	        	try {
	                Account newAccount = new Account(accountNumber.getText(),owner.getText());
	                listAccounts.add(newAccount);
	                saveAccounts( listAccounts );
	                cmbAccount.getItems().add( newAccount );
	                showMessage( "Succesful!", "New account was registered successfully." );

	            } 
	        	catch ( IOException | NullPointerException ex ) {
	        		ex.printStackTrace();
	    			showError( FileUtils.class.getName(), ex.getMessage() );
	        	}
	        }
	    }
		
		private void saveNewTransaction( Account account, DatePicker date, String description, float amount ) throws IOException{
	    	try {
	    		Instant instant = Instant.from(date.getValue().atStartOfDay( ZoneId.systemDefault() ));
	    		Transaction newTransaction = new Transaction(account.getAccountNumber(),account.getOwner(),Date.from( instant ),description,amount);
	    		listTransactions.add( newTransaction);
	    		saveTransactions(newTransaction);
	            showMessage( "Succesful!", "New transaction was registered successfully." );
	            createTable();

	        } 
	    	catch ( NullPointerException | NumberFormatException ex ) {
	    		ex.printStackTrace();
           	 showError( FileUtils.class.getName(), ex.getMessage() );
	    	}
	        
	    }
		
		 //Get total amount from transactions list
		public static Float getTotalAmount( List<Transaction> listTransactions ){
	         float result = 0;
	         
	         for( Transaction dataTransaction: listTransactions ){

	             result += dataTransaction.getAmount();

	         }
	         if(result<0) {
	         	showError("Caution!!!","Your Expense has exceeded your Income" );
	         }
	         return result;
	         
	     }
		
		//Filter Transactions according to the Option selected from the Combo Box
		private void filterTransaction() throws IOException,NullPointerException{
			
			switch(cmbFilter.getValue()) {
			case "Date":
				if(transactionDate.getValue()==null || transactionDate.getValue().toString().trim().equals( "" )) {
					showError("Error!!","A Date has to be selected");
				}
				else {
					DateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                    String date = dateFormat.format( Date.from( Instant.from(transactionDate.getValue().atStartOfDay(ZoneId.systemDefault())) ) );
                    listTransactions=loadTransactionsForAccount(cmbAccount.getValue(),"date",date);
				}
				break;
				
			case "Description":
				if(transactionDescription.getText().trim().equals("")){
					showError("Error!!","A Description has to be given");
				}
				else {
					listTransactions = loadTransactionsForAccount(cmbAccount.getValue(),"description",transactionDescription.getText().trim());
				}
				break;
				
				case "Income":
					listTransactions = loadTransactionsForAccount(cmbAccount.getValue(),"+","");
					break;
					
				case "Expenses":
					listTransactions = loadTransactionsForAccount(cmbAccount.getValue(),"-","");
					break;
					
				default:
					listTransactions = loadTransactionsForAccount(cmbAccount.getValue(),null,null);
				}
			createTable();
			}
			
		 public static void showError(String header, String message){
		        Alert alert = new Alert( AlertType.ERROR );
		        alert.setTitle( "Error Dialog" );
		        alert.setHeaderText( header );
		        alert.setContentText( message );
		        alert.showAndWait();
		    }
		 
		 public static void showMessage(String header, String message){
		        Alert alert = new Alert( AlertType.INFORMATION );
		        alert.setTitle( "Information Dialog" );
		        alert.setHeaderText( header );
		        alert.setContentText( message );
		        alert.showAndWait();
		    }
     }


