package guiPostsAdmin;

import java.sql.SQLException;
import java.util.List;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: ControllerPostsAdmin Class </p>
 * 
 * <p> Description: Controller for the Admin Posts view. Handles all user actions and
 * interactions with posts and replies from an admin perspective.</p>
 * 
 * <p>Key differences from regular ControllerPosts:
 * - NO permission checks - admins can edit/delete ANY post/reply
 * - Always tags new content with "Admin" role
 * - Full CRUD access to all content regardless of author
 * </p>
 * 
 * <p>This class follows the MVC (Model-View-Controller) pattern:
 * - Model: Post/Reply entity classes and Database
 * - View: ViewPostsAdmin (the GUI)
 * - Controller: This class (handles user actions)
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Emmanuel Zelaya-Armenta, Lynn Robert Carter
 * @version 1.00 2025-10-26 Initial creation for admin post management
 */
public class ControllerPostsAdmin {
    
    // ===================== POSTS PANEL METHODS ===================
    
    /*******
     * <p> Method: performViewPosts </p>
     * 
     * <p> Description: Retrieves ALL posts from the database and displays them in the ListView.
     * This is the first thing users see when they open the posts page.</p>
     * 
     * <p>Process:
     * 1. Query database for all posts
     * 2. Clear the ListView to remove old data
     * 3. Format each post for display (including role badge)
     * 4. Add formatted strings to ListView
     * 5. Show the posts panel
     * </p>
     * 
     * <p>Admin privilege: Gets ALL posts regardless of author.</p>
     */
    protected static void performViewPosts() {
        try {
            // Get all posts from the database as a List of Post objects
            List<Post> allPosts = ViewPostsAdmin.theDatabase.getAllPosts();
            
            // Clear any existing items in the ListView
            // This prevents duplicates if we're refreshing the view
            ViewPostsAdmin.list_Posts.getItems().clear();
            
            // Loop through each Post object in our list
            for (Post post : allPosts) {
                // Format the post as a display string (includes role badge)
                String displayText = ModelPostsAdmin.formatPostForDisplay(post);
                
                // Add the formatted string to the ListView
                // Users will see this in the GUI
                ViewPostsAdmin.list_Posts.getItems().add(displayText);
            }
            
            // Make the posts panel visible (hide other panels)
            ViewPostsAdmin.showPostsPanel();
            
        } catch (SQLException e) {
            // If database error occurs, print details for debugging
            e.printStackTrace();
        }
    }
 
    /*******
     * <p> Method: performCreatePost </p>
     * 
     * <p> Description: Switches to the create post panel. Clears the text area so it's
     * ready for new content.</p>
     * 
     * <p>This doesn't create the post yet - it just shows the UI where the admin can
     * type their post content.</p>
     */
    protected static void performCreatePost() {
        // Clear any previous text from the text area
        ViewPostsAdmin.text_PostContent.setText("");
        
        // Switch to the create post panel (hides posts list, shows text area)
        ViewPostsAdmin.showCreatePostsPanel();
    }
    
    /*******
     * <p> Method: performBack </p>
     * 
     * <p> Description: Returns the admin to their home page. This is the "exit" button
     * for the posts interface.</p>
     * 
     * <p>Since only admins can access this view, we always go to Admin Home.</p>
     */
    protected static void performBack() {
        // Navigate to the Admin Home page, passing the stage and user info
        guiAdminHome.ViewAdminHome.displayAdminHome(
            ViewPostsAdmin.theStage,   // The JavaFX Stage (window)
            ViewPostsAdmin.theUser     // The current logged-in user
        );
    }
    
    /*******
     * <p> Method: performDeletePost </p>
     * 
     * <p> Description: Deletes the selected post from the database. ADMIN PRIVILEGE: Can
     * delete ANY post, regardless of who created it. No permission check needed.</p>
     * 
     * <p>Process:
     * 1. Get the selected item from ListView
     * 2. Validate that something was selected
     * 3. Extract the post ID from the display string
     * 4. Delete from database (which also deletes all replies)
     * 5. Refresh the view
     * </p>
     * 
     * <p>Safety: Database.deletePost() handles cascading delete of replies.</p>
     */
    protected static void performDeletePost() {
        // Get the currently selected item from the ListView
        // Returns null if nothing is selected
        String p = ViewPostsAdmin.list_Posts.getSelectionModel().getSelectedItem();
        
        // Check if user actually selected something
        if(p == null) {
            // Print error message to console for debugging
            System.out.println("Need to select a Post");
            return;  // Exit the method early - can't delete nothing
        }
        
        // Extract the post ID from the display string
        // Uses ModelPostsAdmin.getID() to parse "id: 5 author..." → 5
        int id = ModelPostsAdmin.getID(p);
        
        try {
            // Delete the post from the database
            // This also deletes all associated replies (cascading delete)
            ViewPostsAdmin.theDatabase.deletePost(id);
            
        } catch (SQLException e) {
            // Print error if database operation fails
            e.printStackTrace();
        }
        
        // Refresh the posts list to show the post is gone
        performViewPosts();
    }
    
    /*******
     * <p> Method: performEditPost </p>
     * 
     * <p> Description: Allows admin to edit ANY post's content. Shows a dialog box where
     * the admin can type new content.</p>
     * 
     * <p>Process:
     * 1. Validate a post is selected
     * 2. Extract post ID
     * 3. Show input dialog for new content
     * 4. Validate new content isn't empty
     * 5. Update database
     * 6. Refresh view
     * </p>
     * 
     * <p>ADMIN PRIVILEGE: No ownership check - can edit anyone's post.</p>
     */
    protected static void performEditPost() {
        // Get the selected post string from the ListView
        String p = ViewPostsAdmin.list_Posts.getSelectionModel().getSelectedItem();
        
        // Validate selection exists and isn't empty/whitespace
        if(p == null || p.trim().isEmpty()) {
            System.out.println("Need to select a Post");
            return;  // Exit early if nothing selected
        }
        
        // Extract the post ID number from the display string
        int id = ModelPostsAdmin.getID(p);
       
        // Create a dialog box for text input
        // TextInputDialog is a JavaFX popup with a text field
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        
        // Set the dialog's title (shows in window title bar)
        dialog.setTitle("Edit Post");
        
        // Set the header text (large text at top of dialog)
        dialog.setHeaderText("Edit the post content:");
        
        // Set the label for the input field
        dialog.setContentText("New content:");
        
        // Show the dialog and wait for user response
        // Returns an Optional<String> - a container that may or may not have a value
        // This is Java's way of handling "maybe null" situations safely
        java.util.Optional<String> result = dialog.showAndWait();
        
        // Check if user entered something (clicked OK vs Cancel)
        if (result.isPresent()) {
            // Get the text the user entered and remove leading/trailing whitespace
            String newText = result.get().trim();
            
            // Validate that the new content isn't empty
            if (newText.isEmpty()) {
                System.out.println("New content cannot be empty"); 
                return;  // Exit without saving - empty posts not allowed
            }
            
            try {
                // Update the post in the database with the new content
                // Note: Only content changes - author, role, ID stay the same
                ViewPostsAdmin.theDatabase.updatePost(id, newText);
                
                // Refresh the view so user sees the updated content
                performViewPosts();
                
            } catch (SQLException e) {
                // Print error if database update fails
                e.printStackTrace();
            }
        }
        // If result is not present (user clicked Cancel), do nothing
    }
    
    // ===================== CREATE POSTS PANEL METHODS ===================
    
    /*******
     * <p> Method: performSubmitPost </p>
     * 
     * <p> Description: Creates a new post in the database with the content from the text area.
     * Automatically tags the post with "Admin" role since this is the admin view.</p>
     * 
     * <p>Process:
     * 1. Get current admin's username
     * 2. Get content from text area
     * 3. Set role to "Admin"
     * 4. Validate content isn't empty
     * 5. Insert into database
     * 6. Return to posts list view
     * </p>
     * 
     * <p>The role is hardcoded to "Admin" here because only admins use this interface.</p>
     */
    protected static void performSubmitPost() {
        String a = ViewPostsAdmin.theUser.getUserName();
        String c = ViewPostsAdmin.text_PostContent.getText();
       
        String role = "Admin";
        
        if(c.equals("")) {
            System.out.println("Post Cannot be empty");
            return;
        }
        
        try {
            ViewPostsAdmin.theDatabase.createPost(a, c, role);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        ControllerPostsAdmin.performViewPosts();
    }
    
    /*******
     * <p> Method: performCancel </p>
     * 
     * <p> Description: Cancels post creation and returns to the posts list.
     * Any text typed in the text area is discarded.</p>
     * 
     * <p>This gives users a way to back out without posting.</p>
     */
    protected static void performCancel() {
        // Switch back to the posts panel (discards any typed content)
        ViewPostsAdmin.showPostsPanel();
    }
    
    // ===================== REPLIES PANEL METHODS ===================
    
    /*******
     * <p> Method: performCreateReply </p>
     * 
     * <p> Description: Switches to the create reply panel. Clears the text area for
     * new reply content.</p>
     * 
     * <p>This is similar to performCreatePost but for replies.</p>
     */
    protected static void performCreateReply() {
        // Clear any previous text
        ViewPostsAdmin.text_ReplyContent.setText("");
        
        // Show the create reply panel
        ViewPostsAdmin.showCreateReplyPanel();
    }
    
    /*******
     * <p> Method: performViewReplies </p>
     * 
     * <p> Description: Shows all replies for the selected post. This creates a "thread"
     * view where you see the original post plus all its replies.</p>
     * 
     * <p>Process:
     * 1. Validate a post is selected
     * 2. Extract post ID
     * 3. Get post content and display it (read-only)
     * 4. Get all replies for this post
     * 5. Format and display replies
     * 6. Track current post ID for reply creation
     * 7. Show replies panel
     * </p>
     */
    protected static void performViewReplies() {
        // Get the selected post from the ListView
        String p = ViewPostsAdmin.list_Posts.getSelectionModel().getSelectedItem();
        
        // Validate that something was selected
        if(p == null || p.trim().isEmpty()) {
            System.out.println("Need to select a Post");
            return;  // Exit if no post selected
        }
        
        // Extract the post ID from the display string
        int id = ModelPostsAdmin.getID(p);
        
        try {
            // Get the Post object from the database using the ID
            Post post = ViewPostsAdmin.theDatabase.getPost(id);
            
            // Display the post's content in a read-only text area
            // This shows users which post they're replying to
            ViewPostsAdmin.text_PostInReply.setText(post.getContent());
           
            // Get all replies associated with this post ID
            List<Reply> postReplies = ViewPostsAdmin.theDatabase.getRepliesByPost(id);
            
            // Clear any existing replies from the ListView
            ViewPostsAdmin.list_Replies.getItems().clear();
            
            // Loop through each Reply object
            for (Reply reply : postReplies) {
                // Format the reply for display (includes role badge)
                String displayText = ModelPostsAdmin.formatReplyForDisplay(reply);
                
                // Add formatted reply to the ListView
                ViewPostsAdmin.list_Replies.getItems().add(displayText);
            }
            
            // Store the current post ID so we know which post to reply to
            // This is used when user creates a new reply
            ViewPostsAdmin.currentPostID = id;
            
            // Switch to the replies panel view
            ViewPostsAdmin.showRepliesPanel();
            
        } catch (SQLException e) {
            // Print error if database operations fail
            e.printStackTrace();
        }
    }
    
    /*******
     * <p> Method: performDeleteReply </p>
     * 
     * <p> Description: Deletes the selected reply from the database. ADMIN PRIVILEGE:
     * Can delete any reply regardless of who created it.</p>
     * 
     * <p>Process:
     * 1. Validate a reply is selected
     * 2. Extract reply ID
     * 3. Delete from database
     * 4. Refresh replies view
     * </p>
     */
    protected static void performDeleteReply() {
        // Get the selected reply from the ListView
        String r = ViewPostsAdmin.list_Replies.getSelectionModel().getSelectedItem();
        
        // Validate selection
        if(r == null || r.trim().isEmpty()) {
            System.out.println("Need to select a Reply");
            return;  // Exit if nothing selected
        }
        
        // Extract the reply ID from the display string
        int id = ModelPostsAdmin.getID(r);
        
        try {
            // Delete the reply from the database
            // Note: This is simpler than deleting posts because replies have no children
            ViewPostsAdmin.theDatabase.deleteReply(id);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Refresh the replies view to show the reply is gone
        performViewReplies();
    }
    
    /*******
     * <p> Method: performEditReply </p>
     * 
     * <p> Description: Allows admin to edit any reply's content. Shows a dialog box
     * for entering new content.</p>
     * 
     * <p>ADMIN PRIVILEGE: No ownership check - can edit anyone's reply.</p>
     * 
     * <p>Process is identical to performEditPost but operates on replies.</p>
     */
    protected static void performEditReply() {
        // Get selected reply string
        String r = ViewPostsAdmin.list_Replies.getSelectionModel().getSelectedItem();
        
        // Validate selection
        if(r == null || r.trim().isEmpty()) {
            System.out.println("Need to select a Reply");
            return;
        }
       
        // Extract reply ID
        int id = ModelPostsAdmin.getID(r);
        
        // Create and configure the input dialog
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Edit Reply");
        dialog.setHeaderText("Edit the reply content:");
        dialog.setContentText("New content:");
        
        // Show dialog and get user input
        java.util.Optional<String> newContent = dialog.showAndWait();
        
        // If user entered something (clicked OK)
        if (newContent.isPresent()) {
            // Get and trim the new text
            String newText = newContent.get().trim();
            
            // Validate not empty
            if (newText.isEmpty()) {
                System.out.println("New content cannot be empty");
                return;
            }
            
            try {
                // Update the reply in the database
                ViewPostsAdmin.theDatabase.updateReply(id, newText);
                
                // Refresh the replies view
                performViewReplies();
                
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error updating reply");
            }
        }
    }
    
    /*******
     * <p> Method: performBackToPosts </p>
     * 
     * <p> Description: Returns from the replies view back to the posts list view.
     * This is the "back" button when viewing replies.</p>
     */
    protected static void performBackToPosts() {
        // Switch to the posts panel
        ViewPostsAdmin.showPostsPanel();
    }
    
    // ===================== CREATE REPLIES PANEL METHODS ===================
    
    /*******
     * <p> Method: performSubmitReply </p>
     * 
     * <p> Description: Creates a new reply to the current post. Automatically tags
     * the reply with "Admin" role.</p>
     * 
     * <p>Process:
     * 1. Get admin's username
     * 2. Get reply content from text area
     * 3. Set role to "Admin"
     * 4. Validate content isn't empty
     * 5. Insert into database with current post ID
     * 6. Return to replies view
     * </p>
     * 
     * <p>Uses ViewPostsAdmin.currentPostID to know which post to reply to.</p>
     */
    protected static void performSubmitReply() {
        // Get the admin's username (reply author)
        String a = ViewPostsAdmin.theUser.getUserName();
        
        // Get the reply content from the text area
        String c = ViewPostsAdmin.text_ReplyContent.getText();
        
        // Hardcode role to "Admin" for this interface
        String role = "Admin";
        
        // Validate content isn't empty
        if(c.equals("")) {
            System.out.println("Reply Cannot be empty");
            return;
        }
        
        try {
            // Create the reply in the database
            // Parameters: postID, author, content, role
            // currentPostID was set when we viewed the post's replies
            ViewPostsAdmin.theDatabase.createReply(ViewPostsAdmin.currentPostID, a, c, role);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Return to the replies view (which now includes the new reply)
        ControllerPostsAdmin.performViewReplies();
    }    
    
    /*******
     * <p> Method: performReplyCancel </p>
     * 
     * <p> Description: Cancels reply creation and returns to the replies view.
     * Any typed text is discarded.</p>
     */
    protected static void performReplyCancel() {
        // Go back to viewing replies (discard typed content)
        ControllerPostsAdmin.performViewReplies();
    }
    
    // ===================== NAVIGATION METHODS ===================
    
    /*******
     * <p> Method: performLogout </p>
     * 
     * <p> Description: Logs out the current admin and returns to the login page.
     * This is a security feature - always provide a way to log out.</p>
     */
    protected static void performLogout() {
        // Navigate to the login page
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewPostsAdmin.theStage);
    }
    
    /*******
     * <p> Method: performQuit </p>
     * 
     * <p> Description: Exits the entire application. Closes all windows and
     * terminates the program.</p>
     * 
     * <p>System.exit(0) tells the operating system to end the process.
     * The parameter 0 indicates normal termination (vs error exit).</p>
     */
    protected static void performQuit() {
        // Terminate the application with exit code 0 (success)
        System.exit(0);
    }
}