package guiNewAccount;

import validators.UserNameRecognizer;
import validators.Model;
import java.util.List;
import database.Database;
import javafx.scene.paint.Color;

/*******
 * <p> Title: ModelNewAccount Class. </p>
 * 
 * <p> Description: The NewAccount Page Model.  This class is not used as there is no
 * data manipulated by this MVC beyond accepting role information and saving it in the
 * database.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */
public class ModelNewAccount {

    /*
     * This attribute stores the last error message for validation purposes.
     */
    public static String lastErrorMessage = "";

    
    /*****
     * <p> Method: validateUsername(String username) </p>
     * 
     * <p> Description: This method validates the username using FSM rules from the 
     * UserNameRecognizer class. It updates the View with error or success messages. </p>
     * 
     * @param username the input string to be validated
     * 
     * @return true if the username is valid, false otherwise
     */
    
    
    protected static boolean validateUsername(String username) {
        String error = UserNameRecognizer.checkForValidUserName(username);
        if (!error.isEmpty()) {
            lastErrorMessage = error;
            ViewNewAccount.label_UsernameValidation.setTextFill(Color.RED);
            ViewNewAccount.label_UsernameValidation.setText(error);
            return false;
        }
        ViewNewAccount.label_UsernameValidation.setTextFill(Color.GREEN);
        ViewNewAccount.label_UsernameValidation.setText("Valid username.");
        return true;  //remove check to double check DB userName
    }
    
    // TP1 Start ****************************************
    
    protected static boolean validateDatabaseUsername(String username) {
    	if(MatchingUsername(username)) { //check if userName is already in the Data Base and if so print error message
    		lastErrorMessage = "Cannot have matching Username as another user";
        	ViewNewAccount.label_UsernameValidation.setTextFill(Color.RED); 	
        	ViewNewAccount.label_UsernameValidation.setText(lastErrorMessage);
            return false;
        }
        return true;   
    }
    
    protected static boolean MatchingUsername(String username) {
        // Use the existing database connection, not a new one
        Database DB = applicationMain.FoundationsMain.database;
        
        try {
            List<String> check = DB.getUserList();
            if (check != null && check.contains(username)) {
                return true; // Username already exists
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //	TP1 END ***************************************


    
    /*****
     * <p> Method: passwordsMatch(String pw1, String pw2) </p>
     * 
     * <p> Description: This method checks if the two entered passwords match. </p>
     * 
     * @param pw1 first password entry
     * @param pw2 second password entry
     * 
     * @return true if the two passwords match, false otherwise
     */
    protected static boolean passwordsMatch(String pw1, String pw2) {
        if (!pw1.equals(pw2)) {
            lastErrorMessage = "Passwords do not match.";
            ViewNewAccount.label_PasswordsDoNotMatch.setTextFill(Color.RED);
            ViewNewAccount.label_PasswordsDoNotMatch.setText(lastErrorMessage);
            return false;
        }
        ViewNewAccount.label_PasswordsDoNotMatch.setText("");
        return true;
    }

    
    /*****
     * <p> Method: validatePassword(String password) </p>
     * 
     * <p> Description: This method validates the password using the FSM rules implemented 
     * in the Model class from the PasswordEvaluatorTestbed. </p>
     * 
     * @param password the input string to be validated
     * 
     * @return true if the password is valid, false otherwise
     */
    protected static boolean validatePassword(String password) {
        String error = Model.evaluatePassword(password);
        if (!error.isEmpty()) {
            lastErrorMessage = error;
            ViewNewAccount.label_PasswordValidation.setTextFill(Color.RED);
            ViewNewAccount.label_PasswordValidation.setText(error);
            return false;
        }
        ViewNewAccount.label_PasswordValidation.setTextFill(Color.GREEN);
        ViewNewAccount.label_PasswordValidation.setText("Valid password.");
        return true;
    }
    
    
    

    
    /*****
     * <p> Method: validateAll(String username, String pw1, String pw2) </p>
     * 
     * <p> Description: This method validates the username and password entries together. 
     * It ensures that the username is valid, both passwords match, and that the password 
     * meets FSM rules. </p>
     * 
     * @param username the user username to validate
     * @param pw1 the first user password entry
     * @param pw2 the second user password entry
     * 
     * @return true if all validation checks pass, false otherwise
     */
    protected static boolean validateAll(String username, String pw1, String pw2) {
        ViewNewAccount.resetValidation();
        if (!validateDatabaseUsername(username)) return false; // added to the things to check TP1*********************
        if (!validateUsername(username)) return false;
        if (!passwordsMatch(pw1, pw2)) return false;
        if (!validatePassword(pw1)) return false;
        lastErrorMessage = "";
        return true;
    }
}
