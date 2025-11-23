package guiPosts;

import java.sql.SQLException;
import java.util.List;
import entityClasses.Post;
import entityClasses.Reply;

/**
 * <p><strong>Title:</strong> ControllerPosts Class - Student Posts Controller</p>
 * 
 * <p><strong>Description:</strong> Controller component of the MVC architecture for the 
 * student discussion posts system. This class handles all user actions and orchestrates 
 * communication between the View (ViewPosts) and Model (ModelPosts, Database) layers.</p>
 * 
 * <p><strong>MVC Role:</strong> Controller - Processes user input, enforces business logic 
 * and security rules, and coordinates updates between View and Model.</p>
 * 
 * <p><strong>Key Security Features:</strong></p>
 * <ul>
 * <li><strong>Ownership Verification:</strong> Users can only edit/delete their OWN content</li>
 * <li><strong>Permission Checks:</strong> All edit/delete operations verify author matches current user</li>
 * <li><strong>User-Friendly Error Dialogs:</strong> Clear feedback when permission denied</li>
 * <li><strong>Role Tracking:</strong> Automatically tags all new content with user's role</li>
 * </ul>
 * 
 * <p><strong>Supported User Stories:</strong></p>
 * <ul>
 * <li><strong>US-01 (Create Posts):</strong> {@link #performSubmitPost()} - Creates posts with role tracking</li>
 * <li><strong>US-02 (Edit Posts):</strong> {@link #performEditPost()} - Edits own posts with ownership verification</li>
 * <li><strong>US-03 (Delete Posts):</strong> {@link #performDeletePost()} - Deletes own posts with ownership verification</li>
 * <li><strong>US-04 (View Posts):</strong> {@link #performViewPosts()} - Displays all posts with role badges</li>
 * <li><strong>US-05 (Replies):</strong> {@link #performViewReplies()}, {@link #performSubmitReply()} - Full reply functionality</li>
 * </ul>
 * 
 * <p><strong>Design Pattern:</strong> This class follows the <strong>Controller pattern</strong> 
 * from MVC architecture where:</p>
 * <ul>
 * <li><strong>View (ViewPosts):</strong> Displays GUI and captures user input</li>
 * <li><strong>Controller (This Class):</strong> Processes input, enforces rules, coordinates actions</li>
 * <li><strong>Model (ModelPosts + Database):</strong> Manages data formatting and persistence</li>
 * </ul>
 * 
 * <p><strong>Security Architecture:</strong></p>
 * <pre>
 * User Action → View → Controller Security Check → Database
 * 
 * Example: Edit Post Flow
 * 1. User clicks "Edit Post" → ViewPosts captures event
 * 2. ViewPosts calls performEditPost() → Controller takes control
 * 3. Controller retrieves post from Database
 * 4. Controller compares post.author with current user
 * 5. If match: allow edit → Database.updatePost()
 * 6. If no match: show error dialog → block operation
 * 7. Controller refreshes View
 * </pre>
 * 
 * <p><strong>Difference from Admin Controller:</strong> Unlike ControllerPostsAdmin (which 
 * allows admins to edit/delete ANY content), this controller enforces strict ownership 
 * rules - regular users can only modify their own posts and replies.</p>
 * 
 * <p><strong>Copyright:</strong> Lynn Robert Carter © 2025</p>
 * 
 * @author Emmanuel Zelaya-Armenta
 * @author Lynn Robert Carter
 * @version 2.00 2025-10-26 Added permission checks and role tracking for TP2
 * @version 1.00 2025-10-12 Initial implementation for TP1
 * 
 * @see guiPosts.ViewPosts
 * @see guiPosts.ModelPosts
 * @see database.Database
 * @see entityClasses.Post
 * @see entityClasses.Reply
 */
public class ControllerPosts {
    
    // ===================== POSTS PANEL METHODS ===================
    
    /**
     * Retrieves and displays all posts from the database.
     * 
     * <p><strong>Implements:</strong> US-04 (View All Posts)</p>
     * 
     * <p><strong>Process Flow:</strong></p>
     * <ol>
     * <li>Query database for all posts via {@link database.Database#getAllPosts()}</li>
     * <li>Clear existing ListView contents</li>
     * <li>Format each post with role badge via {@link ModelPosts#formatPostForDisplay(Post)}</li>
     * <li>Add formatted posts to ListView</li>
     * <li>Switch View to posts panel</li>
     * </ol>
     * 
     * <p><strong>Display Format:</strong> Each post shows as:
     * <code>"id: X author: username [Role] content: post text"</code></p>
     * 
     * <p><strong>Role Badges:</strong> Posts display [Admin], [Role1], or [Role2] based on 
     * author's role at time of posting. This helps users quickly identify post context.</p>
     * 
     * <p><strong>User Story Satisfaction:</strong> US-04 requires students to view all posts 
     * from classmates. This method retrieves ALL posts (no filtering) and displays them in 
     * a formatted list with clear attribution and role identification.</p>
     * 
     * <p><strong>Error Handling:</strong> SQLException caught and printed to console. In 
     * production, should display user-friendly error dialog.</p>
     * 
     * @see ViewPosts#list_Posts
     * @see ViewPosts#showPostsPanel()
     * @see database.Database#getAllPosts()
     */
    public static void performViewPosts() {
        try {    
            // US-04: Get all posts from database
            List<Post> allPosts = ViewPosts.theDatabase.getAllPosts();
            
            // Clear existing items in ListView
            ViewPosts.list_Posts.getItems().clear();
            
            // Format and add each post to the display
            for (Post post : allPosts) { 
                // Format includes role badge [Admin], [Role1], or [Role2]
                String displayText = ModelPosts.formatPostForDisplay(post);
                ViewPosts.list_Posts.getItems().add(displayText);
            }
            
            // Switch to posts panel view
            ViewPosts.showPostsPanel();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * Prepares the create post interface for user input.
     * 
     * <p><strong>Implements:</strong> US-01 (Create Posts) - Step 1: Show creation interface</p>
     * 
     * <p><strong>Process:</strong></p>
     * <ol>
     * <li>Clear text area to ensure blank slate for new post</li>
     * <li>Switch View to create post panel</li>
     * </ol>
     * 
     * <p><strong>User Journey:</strong> User clicks "Create Post" button → this method 
     * shows empty text area → user types content → clicks Submit → 
     * {@link #performSubmitPost()} handles creation</p>
     * 
     * @see ViewPosts#text_PostContent
     * @see ViewPosts#showCreatePostsPanel()
     * @see #performSubmitPost()
     */
    protected static void performCreatePost() {
        // Clear text area for new post
        ViewPosts.text_PostContent.setText("");
        
        // Show create post panel
        ViewPosts.showCreatePostsPanel();
    }
    
    /**
     * Returns user to their role-specific home page.
     * 
     * <p><strong>Navigation Logic:</strong> Since both Role1 and Role2 users access this 
     * posts interface, this method checks the current user's role and navigates to the 
     * appropriate home page.</p>
     * 
     * <p><strong>Role-Based Routing:</strong></p>
     * <ul>
     * <li><strong>Admin:</strong> Should not occur (admins use ViewPostsAdmin interface)</li>
     * <li><strong>Role1:</strong> Navigate to guiRole1.ViewRole1Home</li>
     * <li><strong>Role2:</strong> Navigate to guiRole2.ViewRole2Home</li>
     * </ul>
     * 
     * <p><strong>Design Rationale:</strong> Rather than a generic "back" button, this 
     * method ensures users return to the correct context based on their role, maintaining 
     * proper navigation flow.</p>
     * 
     * @see ViewPosts#theUser
     * @see entityClasses.User#getAdminRole()
     * @see entityClasses.User#getNewRole1()
     * @see entityClasses.User#getNewRole2()
     */
    protected static void performBack() {
    	// Check user's role and navigate to appropriate home page
    	if(ViewPosts.theUser.getAdminRole()) {
    		// Should not happen - admins use ViewPostsAdmin
    		guiAdminHome.ViewAdminHome.displayAdminHome(ViewPosts.theStage, ViewPosts.theUser);
    	}
    	else if(ViewPosts.theUser.getNewRole1()) {
    		// Role1 user - go to Role1 home
    		guiRole1.ViewRole1Home.displayRole1Home(ViewPosts.theStage, ViewPosts.theUser);
    	}
    	else {
    		// Role2 user - go to Role2 home
    		guiRole2.ViewRole2Home.displayRole2Home(ViewPosts.theStage, ViewPosts.theUser);
    	}
    }
    
    /**
     * Deletes a post ONLY if the current user is the author.
     * 
     * <p><strong>Implements:</strong> US-03 (Delete Own Posts) with security enforcement</p>
     * 
     * <p><strong>Security Process:</strong></p>
     * <ol>
     * <li>Get selected post ID from ListView</li>
     * <li>Retrieve full Post object from database</li>
     * <li><strong>CRITICAL:</strong> Compare post.author with current user's username</li>
     * <li><strong>If match:</strong> Delete post from database</li>
     * <li><strong>If no match:</strong> Show error dialog and deny deletion</li>
     * <li>Refresh posts view</li>
     * </ol>
     * 
     * <p><strong>Why This Security Check is Essential:</strong> Per your assignment 
     * requirements, "Students keep removing each other's posts and replies" was a problem. 
     * This ownership verification prevents unauthorized deletions by ensuring only the 
     * post author can delete their own content.</p>
     * 
     * <p><strong>User Story Mapping:</strong></p>
     * <ul>
     * <li><strong>US-03:</strong> "Students can delete their own posts if posted in error"</li>
     * <li><strong>Key Word:</strong> "their own" - This method enforces ownership</li>
     * <li><strong>Prevention:</strong> Users cannot delete others' posts (security requirement)</li>
     * </ul>
     * 
     * <p><strong>Error Messages:</strong></p>
     * <ul>
     * <li><strong>No Selection:</strong> Console message "Need to select a Post"</li>
     * <li><strong>Permission Denied:</strong> Dialog popup "You can only delete your own posts"</li>
     * </ul>
     * 
     * <p><strong>Validation:</strong> Checks for null selection before attempting database 
     * operation to prevent NullPointerException.</p>
     * 
     * @see ViewPosts#list_Posts
     * @see ModelPosts#getID(String)
     * @see database.Database#getPost(int)
     * @see database.Database#deletePost(int)
     */
    protected static void performDeletePost() {
        // Get selected post display string
        String p = ViewPosts.list_Posts.getSelectionModel().getSelectedItem();
        
        // Validate selection
        if(p == null) {
            System.out.println("Need to select a Post");
            return;
        }
        
        // Extract post ID from display string
        int id = ModelPosts.getID(p);
        
        try {
            // US-03 SECURITY CHECK: Get the full post object to check ownership
            Post post = ViewPosts.theDatabase.getPost(id);
            
            // Compare post author with current user's username
            if (!post.getAuthor().equals(ViewPosts.theUser.getUserName())) {
                // NOT the owner - deny access
                
                // Print to console for debugging
                System.out.println("ERROR: You can only delete your own posts!");
                
                // Show user-friendly error dialog
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Permission Denied");
                alert.setHeaderText("Cannot Delete Post");
                alert.setContentText("You can only delete your own posts.");
                
                // Show dialog and wait for user to click OK
                alert.showAndWait();
                
                // Exit method without deleting
                return;
            }
            
            // If we get here, user IS the owner - allow delete
            ViewPosts.theDatabase.deletePost(id);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Refresh the posts view
        performViewPosts();
    }
    
    /**
     * Edits a post ONLY if the current user is the author.
     * 
     * <p><strong>Implements:</strong> US-02 (Edit Own Posts) with security enforcement</p>
     * 
     * <p><strong>Security Process:</strong></p>
     * <ol>
     * <li>Verify user has selected a post</li>
     * <li>Extract post ID from selection</li>
     * <li>Retrieve post from database</li>
     * <li><strong>CRITICAL:</strong> Verify post.author matches current user</li>
     * <li><strong>If authorized:</strong> Show edit dialog with current content</li>
     * <li>Validate new content (not empty)</li>
     * <li>Update database with new content</li>
     * <li>Refresh view</li>
     * </ol>
     * 
     * <p><strong>User Story Satisfaction:</strong></p>
     * <ul>
     * <li><strong>US-02:</strong> "Students can edit their own posts to fix typos or clarify content"</li>
     * <li><strong>Ownership:</strong> "their own" enforced by author comparison</li>
     * <li><strong>Use Cases:</strong> Fix typos, clarify statements, add context, correct errors</li>
     * </ul>
     * 
     * <p><strong>What Can Be Edited:</strong> ONLY the post content. Author, ID, and role 
     * are immutable. This design preserves post identity and historical accuracy.</p>
     * 
     * <p><strong>Validation Rules:</strong></p>
     * <ul>
     * <li>Selection must not be null or empty</li>
     * <li>User must be the post author</li>
     * <li>New content must not be empty/blank</li>
     * </ul>
     * 
     * <p><strong>Dialog Interface:</strong> Uses TextInputDialog to capture new content. 
     * Pre-fills with current content for convenience. User can modify and submit, or 
     * cancel to abort the edit.</p>
     * 
     * @see ViewPosts#list_Posts
     * @see database.Database#updatePost(int, String)
     */
    protected static void performEditPost() {
        String p = ViewPosts.list_Posts.getSelectionModel().getSelectedItem();
        
        if(p == null || p.trim().isEmpty()) {
            System.out.println("Need to select a Post");
            return;
        }
        
        int id = ModelPosts.getID(p);
        
        try {
            // US-02 SECURITY CHECK: Verify ownership before allowing edit
            Post post = ViewPosts.theDatabase.getPost(id);
            
            // Compare authors
            if (!post.getAuthor().equals(ViewPosts.theUser.getUserName())) {
                // NOT the owner - deny access
                System.out.println("ERROR: You can only edit your own posts!");
                
                // Show error dialog
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Permission Denied");
                alert.setHeaderText("Cannot Edit Post");
                alert.setContentText("You can only edit your own posts.");
                alert.showAndWait();
                
                // Exit without editing
                return;
            }
            
            // Owner verified - proceed with edit
            
        } catch (SQLException e) {
            e.printStackTrace();
            return;  // Exit on database error
        }
       
        // Show edit dialog
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit the post content:");
        dialog.setContentText("New content:");
        
        java.util.Optional<String> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            String newText = result.get().trim();
            
            if (newText.isEmpty()) {
                System.out.println("New content cannot be empty"); 
                return;
            }
            
            try {
                // Update post with new content
                ViewPosts.theDatabase.updatePost(id, newText);
                performViewPosts();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // ===================== CREATE POSTS PANEL METHODS ===================
    
    /**
     * Creates a new post with automatic role tracking.
     * 
     * <p><strong>Implements:</strong> US-01 (Create Posts) - Complete post creation</p>
     * 
     * <p><strong>Process Flow:</strong></p>
     * <ol>
     * <li>Get author from current logged-in user</li>
     * <li>Get content from text area</li>
     * <li>Determine user's current role (Admin/Role1/Role2)</li>
     * <li>Validate content is not empty</li>
     * <li>Create post in database with role tag</li>
     * <li>Return to posts view</li>
     * </ol>
     * 
     * <p><strong>Role Detection Logic:</strong></p>
     * <pre>
     * if (user.getAdminRole()) → role = "Admin"
     * else if (user.getNewRole1()) → role = "Role1"
     * else if (user.getNewRole2()) → role = "Role2"
     * else → role = "Unknown" (fallback, shouldn't happen)
     * </pre>
     * 
     * <p><strong>Why Role Tracking Matters:</strong></p>
     * <ul>
     * <li><strong>Display:</strong> Shows [Role] badge in post listings for visual identification</li>
     * <li><strong>TP3 Analytics:</strong> Enables future instructor features to filter/analyze by role</li>
     * <li><strong>Context:</strong> Helps students understand who is posting (peer vs instructor)</li>
     * </ul>
     * 
     * <p><strong>User Story Satisfaction:</strong> US-01 requires students to create posts 
     * to ask questions or share knowledge. This method captures author, content, and role, 
     * then persists to database.</p>
     * 
     * <p><strong>Validation:</strong> Content must not be empty. If empty, error message 
     * printed to console and creation aborted.</p>
     * 
     * <p><strong>Future Enhancement:</strong> Could add character limit validation (500 chars) 
     * before database submission to provide immediate feedback.</p>
     * 
     * @see ViewPosts#theUser
     * @see ViewPosts#text_PostContent
     * @see database.Database#createPost(String, String, String)
     */
    protected static void performSubmitPost() {
        String a = ViewPosts.theUser.getUserName();
        String c = ViewPosts.text_PostContent.getText();
        
        // US-01: Determine user's role for tagging the post
        String role = "Unknown";  // Default fallback
        
        if (ViewPosts.theUser.getAdminRole()) {
            role = "Admin";
        }
        else if (ViewPosts.theUser.getNewRole1()) {
            role = "Role1";
        }
        else if (ViewPosts.theUser.getNewRole2()) {
            role = "Role2";
        }
        
        if(c.equals("")) {
            System.out.println("Post Cannot be empty");
            return;
        }
        
        try {
            ViewPosts.theDatabase.createPost(a, c, role);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        ControllerPosts.performViewPosts();
    }
    
    /**
     * Cancels post creation and returns to posts view.
     * 
     * <p><strong>Purpose:</strong> Allows user to abort post creation without submitting. 
     * Discards any typed content and returns to posts list.</p>
     * 
     * <p><strong>User Journey:</strong> User clicks "Create Post" → starts typing → 
     * changes mind → clicks "Cancel" → this method discards content and shows posts list</p>
     * 
     * @see ViewPosts#showPostsPanel()
     */
    protected static void performCancel() {
        // Discard typed content and return to posts view
        ViewPosts.showPostsPanel();
    }
    
    // ===================== REPLIES PANEL METHODS ===================
    
    /**
     * Prepares the create reply interface.
     * 
     * <p><strong>Implements:</strong> US-05 (Replies) - Step 1: Show reply creation interface</p>
     * 
     * <p><strong>Process:</strong></p>
     * <ol>
     * <li>Clear reply text area</li>
     * <li>Switch View to create reply panel</li>
     * </ol>
     * 
     * <p><strong>Context:</strong> Called when user is viewing replies to a post and clicks 
     * "Create Reply" button. The parent post ID is already stored in ViewPosts.currentPostID.</p>
     * 
     * @see ViewPosts#text_ReplyContent
     * @see ViewPosts#showCreateReplyPanel()
     * @see #performSubmitReply()
     */
    protected static void performCreateReply() {
        // Clear reply text area
        ViewPosts.text_ReplyContent.setText("");
        
        // Show create reply panel
        ViewPosts.showCreateReplyPanel();
    }
    
    /**
     * Displays all replies for the selected post.
     * 
     * <p><strong>Implements:</strong> US-05 (Replies) - View replies to posts</p>
     * 
     * <p><strong>Process Flow:</strong></p>
     * <ol>
     * <li>Validate user has selected a post</li>
     * <li>Extract post ID from selection</li>
     * <li>Retrieve and display original post (read-only) for context</li>
     * <li>Retrieve all replies for this post</li>
     * <li>Format and display replies with role badges</li>
     * <li>Store post ID for reply creation</li>
     * <li>Switch to replies panel</li>
     * </ol>
     * 
     * <p><strong>Thread View:</strong> Shows original post at top (non-editable) with all 
     * replies listed below. This provides context for the discussion thread.</p>
     * 
     * <p><strong>User Story Satisfaction:</strong> US-05 requires students to reply to posts 
     * to continue discussions. This method shows existing conversation and enables reply creation.</p>
     * 
     * <p><strong>Display Format:</strong> Each reply shows:
     * <code>"id: X author: username [Role] content: reply text"</code></p>
     * 
     * @see ViewPosts#text_PostInReply
     * @see ViewPosts#list_Replies
     * @see ViewPosts#currentPostID
     * @see database.Database#getRepliesByPost(int)
     */
    protected static void performViewReplies() {
        String p = ViewPosts.list_Posts.getSelectionModel().getSelectedItem();
        
        if(p == null || p.trim().isEmpty()) {
            System.out.println("Need to select a Post");
            return;
        }
        
        int id = ModelPosts.getID(p);
        
        try {
            // US-05: Get and display the original post for context
            Post post = ViewPosts.theDatabase.getPost(id);
            ViewPosts.text_PostInReply.setText(post.getContent());
           
            // Get all replies for this post
            List<Reply> postReplies = ViewPosts.theDatabase.getRepliesByPost(id);
            
            // Clear existing replies from ListView
            ViewPosts.list_Replies.getItems().clear();
            
            // Format and display each reply with role badge
            for (Reply reply : postReplies) {
                String displayText = ModelPosts.formatReplyForDisplay(reply);
                ViewPosts.list_Replies.getItems().add(displayText);
            }
            
            // Store current post ID for reply creation
            ViewPosts.currentPostID = id;
            
            // Show replies panel
            ViewPosts.showRepliesPanel();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes a reply ONLY if the current user is the author.
     * 
     * <p><strong>Implements:</strong> US-05 (Replies) - Delete own replies with security</p>
     * 
     * <p><strong>Security Process:</strong> Same ownership verification as posts. Compares 
     * reply.author with current user. Only allows deletion if they match.</p>
     * 
     * <p><strong>Permission Model:</strong> Identical to {@link #performDeletePost()} but 
     * operates on Reply objects instead of Post objects. Prevents users from deleting 
     * others' replies.</p>
     * 
     * @see database.Database#getReply(int)
     * @see database.Database#deleteReply(int)
     */
    protected static void performDeleteReply() {
        String r = ViewPosts.list_Replies.getSelectionModel().getSelectedItem();
        
        if(r == null || r.trim().isEmpty()) {
            System.out.println("Need to select a Reply");
            return;
        }
        
        int id = ModelPosts.getID(r);
        
        try {
            // US-05 SECURITY CHECK: Verify ownership
            Reply reply = ViewPosts.theDatabase.getReply(id);
            
            // Compare authors
            if (!reply.getAuthor().equals(ViewPosts.theUser.getUserName())) {
                // NOT the owner - deny
                System.out.println("ERROR: You can only delete your own replies!");
                
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Permission Denied");
                alert.setHeaderText("Cannot Delete Reply");
                alert.setContentText("You can only delete your own replies.");
                alert.showAndWait();
                
                return;
            }
            
            // Owner verified - allow delete
            ViewPosts.theDatabase.deleteReply(id);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Refresh replies view
        performViewReplies();
    }
    
    /**
     * Edits a reply ONLY if the current user is the author.
     * 
     * <p><strong>Implements:</strong> US-05 (Replies) - Edit own replies with security</p>
     * 
     * <p><strong>Security Process:</strong> Same ownership verification as posts. Compares 
     * reply.author with current user before allowing edit.</p>
     * 
     * <p><strong>Permission Model:</strong> Identical to {@link #performEditPost()} but 
     * operates on Reply objects. Users can only edit their own replies.</p>
     * 
     * @see database.Database#updateReply(int, String)
     */
    protected static void performEditReply() {
        String r = ViewPosts.list_Replies.getSelectionModel().getSelectedItem();
        
        if(r == null || r.trim().isEmpty()) {
            System.out.println("Need to select a Reply");
            return;
        }
       
        int id = ModelPosts.getID(r);
        
        try {
            // US-05 SECURITY CHECK: Verify ownership
            Reply reply = ViewPosts.theDatabase.getReply(id);
            
            // Compare authors
            if (!reply.getAuthor().equals(ViewPosts.theUser.getUserName())) {
                // NOT the owner - deny
                System.out.println("ERROR: You can only edit your own replies!");
                
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Permission Denied");
                alert.setHeaderText("Cannot Edit Reply");
                alert.setContentText("You can only edit your own replies.");
                alert.showAndWait();
                
                return;
            }
            
            // Owner verified - proceed with edit
            
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        // Show edit dialog
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Edit Reply");
        dialog.setHeaderText("Edit the reply content:");
        dialog.setContentText("New content:");
        
        java.util.Optional<String> newContent = dialog.showAndWait();
        
        if (newContent.isPresent()) {
            String newText = newContent.get().trim();
            
            if (newText.isEmpty()) {
                System.out.println("New content cannot be empty");
                return;
            }
            
            try {
                // Update reply with new content
                ViewPosts.theDatabase.updateReply(id, newText);
                performViewReplies();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Returns to the posts list view from the replies view.
     * 
     * <p><strong>Navigation:</strong> Simple back navigation that returns user to the main 
     * posts list after viewing/managing replies.</p>
     * 
     * @see ViewPosts#showPostsPanel()
     */
    protected static void performBackToPosts() {
        // Return to posts list view
        ViewPosts.showPostsPanel();
    }
    
    // ===================== CREATE REPLIES PANEL METHODS ===================
    
    /**
     * Creates a new reply with automatic role tracking.
     * 
     * <p><strong>Implements:</strong> US-05 (Replies) - Complete reply creation</p>
     * 
     * <p><strong>Process Flow:</strong></p>
     * <ol>
     * <li>Get author from current logged-in user</li>
     * <li>Get reply content from text area</li>
     * <li>Determine user's current role (Role1/Role2)</li>
     * <li>Validate content is not empty</li>
     * <li>Create reply in database linked to current post</li>
     * <li>Return to replies view</li>
     * </ol>
     * 
     * <p><strong>Role Detection:</strong> Similar to post creation, determines if user is 
     * Role1 or Role2 and tags the reply accordingly. Admin check not typically needed as 
     * admins use separate interface.</p>
     * 
     * <p><strong>Parent Post Linking:</strong> Uses {@link ViewPosts#currentPostID} to link 
     * this reply to the correct parent post. This ID was set when user clicked "View Replies" 
     * on a post.</p>
     * 
     * <p><strong>User Story Satisfaction:</strong> US-05 requires students to reply to posts 
     * to continue discussions. This method captures author, content, role, and parent post ID, 
     * then persists the reply.</p>
     * 
     * @see ViewPosts#currentPostID
     * @see ViewPosts#text_ReplyContent
     * @see database.Database#createReply(int, String, String, String)
     */
    protected static void performSubmitReply() {
        // Get author (current user)
        String a = ViewPosts.theUser.getUserName();
        
        // Get reply content
        String c = ViewPosts.text_ReplyContent.getText();
        
        // US-05: Determine user's role
        String role = "Unknown";
        if (ViewPosts.theUser.getNewRole1()) {
            role = "Role1";
        }
        else if (ViewPosts.theUser.getNewRole2()) {
            role = "Role2";
        }
        
        // Validate not empty
        if(c.equals("")) {
            System.out.println("Reply Cannot be empty again");
            return;
        }
        
        try {
            // Create reply with role tag
            // Uses currentPostID to link reply to post
            ViewPosts.theDatabase.createReply(ViewPosts.currentPostID, a, c, role);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Return to replies view
        ControllerPosts.performViewReplies();
    }    
    
    /**
     * Cancels reply creation and returns to replies view.
     * 
     * <p><strong>Purpose:</strong> Allows user to abort reply creation. Discards typed 
     * content and returns to viewing existing replies.</p>
     * 
     * @see #performViewReplies()
     */
    protected static void performReplyCancel() {
        // Discard and return to replies view
        ControllerPosts.performViewReplies();
    }
    
    // ===================== NAVIGATION METHODS ===================
    
    /**
     * Logs out the current user and returns to login screen.
     * 
     * <p><strong>Security:</strong> Ends current user session and requires re-authentication 
     * to access the system again.</p>
     * 
     * @see guiUserLogin.ViewUserLogin#displayUserLogin(javafx.stage.Stage)
     */
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewPosts.theStage);
    }
    
    /**
     * Exits the application completely.
     * 
     * <p><strong>Note:</strong> Uses System.exit(0) for clean application termination.</p>
     */
    protected static void performQuit() {
        System.exit(0);
    }
}