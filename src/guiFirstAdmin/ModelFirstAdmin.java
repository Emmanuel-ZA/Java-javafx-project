package guiFirstAdmin;

import validators.UserNameRecognizer;
import validators.Model;
import javafx.scene.paint.Color;

/*******
 * <p> Title: ModelFirstAdmin Class. </p>
 * 
 * <p> Description: The First System Startup Page Model.  This class is not used as there is no
 * data manipulated by this MVC beyond accepting a username and password and then saving it in the
 * database.  When the code is enhanced for input validation, this model may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-15 Initial version
 *  
 */

public class ModelFirstAdmin {

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
            ViewFirstAdmin.label_UsernameValidation.setTextFill(Color.RED);
            ViewFirstAdmin.label_UsernameValidation.setText(error);
            return false;
        }
        ViewFirstAdmin.label_UsernameValidation.setTextFill(Color.GREEN);
        ViewFirstAdmin.label_UsernameValidation.setText("Valid username.");
        return true;
    }

    
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
            ViewFirstAdmin.label_PasswordsDoNotMatch.setTextFill(Color.RED);
            ViewFirstAdmin.label_PasswordsDoNotMatch.setText(lastErrorMessage);
            return false;
        }
        ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
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
            ViewFirstAdmin.label_PasswordValidation.setTextFill(Color.RED);
            ViewFirstAdmin.label_PasswordValidation.setText(error);
            return false;
        }
        ViewFirstAdmin.label_PasswordValidation.setTextFill(Color.GREEN);
        ViewFirstAdmin.label_PasswordValidation.setText("Valid password.");
        return true;
    }

    
    /*****
     * <p> Method: validateAll(String username, String pw1, String pw2) </p>
     * 
     * <p> Description: This method validates the username and password entries together. 
     * It ensures that the username is valid, both passwords match, and that the password 
     * meets FSM rules. </p>
     * 
     * @param username the Admin username to validate
     * @param pw1 the first Admin password entry
     * @param pw2 the second Admin password entry
     * 
     * @return true if all validation checks pass, false otherwise
     */
    
    protected static boolean validateAll(String username, String pw1, String pw2) {
        ViewFirstAdmin.resetValidation();
        if (!validateUsername(username)) return false;
        if (!passwordsMatch(pw1, pw2)) return false;
        if (!validatePassword(pw1)) return false;
        lastErrorMessage = "";
        return true;
    }
}
