package guiPosts;

import java.sql.SQLException;
import java.util.List;
import entityClasses.Post;
import entityClasses.Reply;

public class ControllerPosts {
    
    // =====================POSTS PANEL METHODS=================== 
    protected static void performViewPosts() { // Show each post within the DB
        try {    
            List<Post> allPosts = ViewPosts.theDatabase.getAllPosts(); // grab all post in the db
            ViewPosts.list_Posts.getItems().clear(); // make sure there is nothing first before printing all posts
            for (Post post : allPosts) { 
                String displayText = ModelPosts.formatPostForDisplay(post); // format and display each post
                ViewPosts.list_Posts.getItems().add(displayText);
            }
            ViewPosts.showPostsPanel(); // show posts
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
 
    protected static void performCreatePost() { // show view of Post
        ViewPosts.text_PostContent.setText("");
        ViewPosts.showCreatePostsPanel();
    }
    
    protected static void performBack() { // go back to previous screen
    	if(ViewPosts.theUser.getAdminRole()) 
    		guiAdminHome.ViewAdminHome.displayAdminHome(ViewPosts.theStage, ViewPosts.theUser);
    	else if(ViewPosts.theUser.getNewRole1())
    		guiRole1.ViewRole1Home.displayRole1Home(ViewPosts.theStage, ViewPosts.theUser);
    	else
    		guiRole2.ViewRole2Home.displayRole2Home(ViewPosts.theStage, ViewPosts.theUser);
    }
    
    protected static void performDeletePost() { // delete Post!
        String p = ViewPosts.list_Posts.getSelectionModel().getSelectedItem(); // 
        
        if(p == null) { // if nothing is selected sysout error
            System.out.println("Need to select a Post");
            return;
        }
        
        int id = ModelPosts.getID(p); // get the ID from the post selected
        
        try {
            ViewPosts.theDatabase.deletePost(id); // delete that post with that post ID
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        performViewPosts();
    }
    
    protected static void performEditPost() { // edit a post
        String p = ViewPosts.list_Posts.getSelectionModel().getSelectedItem(); // string whatever is selected
        
        if(p == null || p.trim().isEmpty()) { // if nothing is selected return error
            System.out.println("Need to select a Post");
            return;
        }
        
        int id = ModelPosts.getID(p); // get the ID from post selected
       
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(); // create a textbox for new content to be inputed
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit the post content:");
        dialog.setContentText("New content:");
        
        java.util.Optional<String> result = dialog.showAndWait(); // show textbox and wait until something happens
        
        if (result.isPresent()) { //
            String newText = result.get().trim();
            
            if (newText.isEmpty()) { // if textbox is empty sysout error
                System.out.println("New content cannot be empty"); 
                return;
            }
            
            try {
                ViewPosts.theDatabase.updatePost(id, newText); // upddate the post with given id and new content
                performViewPosts();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error updating post");
            }
        }
    }
    
    // =====================CREATE POSTS PANEL METHODS=================== 
    
    // post get created and stored in the DB
    protected static void performSubmitPost() {
        String a = ViewPosts.theUser.getUserName(); // get the user who is creating the post and their content
        String c = ViewPosts.text_PostContent.getText();
        
        if(c.equals("")) { // if the content is empty sysout error
            System.out.println("Post Cannot be empty again");
            return;
        }
        
        try {
            ViewPosts.theDatabase.createPost(a, c); // create new post in DB
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        ControllerPosts.performViewPosts(); // go back to viewing posts
    }
    
    protected static void performCancel() { // cancel post creation
        ViewPosts.showPostsPanel();
    }
    
    // =====================REPLIES PANEL METHODS=================== 
    
    //create a reply
    protected static void performCreateReply() {
        ViewPosts.text_ReplyContent.setText(""); // set the text in the reply to empty before showing text box for input
        ViewPosts.showCreateReplyPanel();
    }
    
    // view all replies from selected post
    protected static void performViewReplies() {
        String p = ViewPosts.list_Posts.getSelectionModel().getSelectedItem(); // get selected post
        
        if(p == null || p.trim().isEmpty()) { // if no post is selected then sysout error
            System.out.println("Need to select a Post");
            return;
        }
        
        int id = ModelPosts.getID(p); // from selected post extract id from post
        
        try {
            // Get the post object
            Post post = ViewPosts.theDatabase.getPost(id);
            ViewPosts.text_PostInReply.setText(post.getContent());
           
            // Get replies as objects
            List<Reply> postReplies = ViewPosts.theDatabase.getRepliesByPost(id);
            ViewPosts.list_Replies.getItems().clear(); // clear the list of replies shown before 
            for (Reply reply : postReplies) {
                String displayText = ModelPosts.formatReplyForDisplay(reply); // format and display each reply
                ViewPosts.list_Replies.getItems().add(displayText);
            }
            
            ViewPosts.currentPostID = id; // update the currentPostID to keep track of what post your on
            ViewPosts.showRepliesPanel(); // show the replies!
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Delete selected reply
    protected static void performDeleteReply() {
        String r = ViewPosts.list_Replies.getSelectionModel().getSelectedItem(); // string the selected reply
        
        if(r == null || r.trim().isEmpty()) { // if no reply is selected sysout error
            System.out.println("Need to select a Reply");
            return;
        }
        
        int id = ModelPosts.getID(r); // extract id from select reply
        
        try {
            ViewPosts.theDatabase.deleteReply(id); // delete reply from DB
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        performViewReplies(); // go back to view replies
    }
    
    // Edit selected reply
    protected static void performEditReply() {
        String r = ViewPosts.list_Replies.getSelectionModel().getSelectedItem(); // string a selected a reply
        
        if(r == null || r.trim().isEmpty()) { // if nothing is selected sysout error
            System.out.println("Need to select a Reply");
            return;
        }
       
        int id = ModelPosts.getID(r); // extract id from selected reply
        
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(); // create text box to update reply
        dialog.setTitle("Edit Reply");
        dialog.setHeaderText("Edit the reply content:");
        dialog.setContentText("New content:");
        
        java.util.Optional<String> newContent = dialog.showAndWait(); // keep textbox shown until submited
        
        if (newContent.isPresent()) {
            String newText = newContent.get().trim();
            
            if (newText.isEmpty()) {	// check if textbox is empty
                System.out.println("New content cannot be empty");
                return;
            }
            
            try {
                ViewPosts.theDatabase.updateReply(id, newText); // update the reply in the DB with the new content
                performViewReplies(); // show new reply!
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error updating reply");
            }
        }
    }
    
    // Go back to posts
    protected static void performBackToPosts() {
        ViewPosts.showPostsPanel();
    }
    
    // =====================CREATE REPLIES PANEL METHODS=================== 
    
    // Panel to create a reply
    protected static void performSubmitReply() {
        String a = ViewPosts.theUser.getUserName();	// get author and the content within the textBox
        String c = ViewPosts.text_ReplyContent.getText();
        
        if(c.equals("")) { // if reply is empty Sysout an error
            System.out.println("Reply Cannot be empty again");
            return;
        }
        
        try {
            ViewPosts.theDatabase.createReply(ViewPosts.currentPostID, a, c); // create reply in DB
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        ControllerPosts.performViewReplies();
    }    
    
    // Cancel post
    protected static void performReplyCancel() {
        ControllerPosts.performViewReplies();
    }
    
    // BEGONE
    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewPosts.theStage);
    }
    
    // Bye Bye
    protected static void performQuit() {
        System.exit(0);
    }
}