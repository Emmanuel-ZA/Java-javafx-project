package guiPosts;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewPosts Class </p>
 * 
 * <p> Description: This class manages all GUI scenes related to posts and replies.
 * It creates four separate scenes: PostsList, CreatePost, Replies, and CreateReply.
 * The controller switches between these scenes based on user actions. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Emmanuel Zelaya-Armenta
 * 
 * @version 1.00		2025-10-12 Initial version
 */
public class ViewPosts {
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	protected static User theUser;				// The current logged in User
	static Database theDatabase = applicationMain.FoundationsMain.database;
	static int currentPostID = -1; // current post it is on
	
	private static ViewPosts theView;		// Used to determine if instantiation of the class is needed
	protected static Stage theStage;			// The Stage that JavaFX has established for us
	
	// Labels for all 4 panels
	private static Label label_PostsTitle = new Label("Create Post Here!");
	private static Label label_ViewPostsTitle = new Label("View Posts Here!");
	private static Label label_ReplyTitle = new Label("Create Reply Here!");
	private static Label label_ViewReplyTitle = new Label("View Reply's Here!");
	
	//Buttons for all 4 panels
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_viewReplies = new Button("View Replies");
	protected static Button button_CreatePost = new Button("Create Post");
	protected static Button button_back = new Button("Go Back Home");
	protected static Button button_Quit = new Button("Quit");
	protected static Button button_SubmitPost = new Button("Submit Post");
	protected static Button button_SubmitReply = new Button("Submit Reply");
	protected static Button button_CancelPost = new Button("Cancel");
	protected static Button button_CancelReply = new Button("Cancel");
	protected static Button button_DeletePost = new Button("Delete Post");
	protected static Button button_DeleteReply = new Button("Delete Reply");
	protected static Button button_EditPost = new Button("Edit Post");
	protected static Button button_EditReply = new Button("Edit Reply");
	protected static Button button_CreateReply = new Button("Create Reply");
	protected static Button button_BackToPosts = new Button("Back To Posts");
	
	//List for all posts / replies
	protected static ListView<String> list_Posts = new ListView<>();
	protected static ListView<String> list_Replies = new ListView<>();
	
	// text areas to input post and replies
	protected static TextArea text_PostContent = new TextArea();
	protected static TextArea text_PostInReply = new TextArea();
	protected static TextArea text_ReplyContent = new TextArea();
	
	// MAIN SCENE
	private static Pane mainPane = new Pane();
	private static Scene mainScene = new Scene(mainPane, width, height);

	// Create 4 panels
	private static Pane postsPanel = new Pane();
	private static Pane createPostPanel = new Pane();
	private static Pane repliesPanel = new Pane();
	private static Pane createReplyPanel = new Pane();
	
	public static void displayPosts(Stage ps, User user) {
		theStage = ps;
		theUser = user;
	
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewPosts();		// Instantiate singleton if needed
		theStage.setScene(mainScene);
	    theStage.show();
	}
	
	// This is how the krabby patty is created here, instead of creating 4 whole stages we create 1 with 4 panels that hide when each page is presented
	protected static void hideAllPanels(){
		postsPanel.setVisible(false);
		createPostPanel.setVisible(false);
		repliesPanel.setVisible(false);
		createReplyPanel.setVisible(false);
	}
	
	

	private ViewPosts(){
		// Create main pane Add ALL panels to main pane
		mainPane.getChildren().addAll(postsPanel, createPostPanel, repliesPanel, createReplyPanel);
		// At start, hide everything except posts
		hideAllPanels();
		postsPanel();
		createPostPanel();
		repliesPanel();
		createReplyPanel();
		postsPanel.setVisible(true);
	}
	
	
	private void postsPanel() { // First Panel gui that shows all posts and option to create post
		
		setupLabelUI(label_ViewPostsTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		ControllerPosts.performViewPosts();
		setupListViewUI(list_Posts, "Dialog", 18, 450, 300, 20, 150 );
		
		setupButtonUI(button_CreatePost, "Dialog", 18, 250, Pos.CENTER, 500, 150);
        button_CreatePost.setOnAction((event) -> {ControllerPosts.performCreatePost(); });
        
        setupButtonUI(button_viewReplies, "Dialog", 18, 250, Pos.CENTER, 500, 200);
        button_viewReplies.setOnAction((event) -> {ControllerPosts.performViewReplies(); });
        
        setupButtonUI(button_DeletePost, "Dialog", 18, 250, Pos.CENTER, 500, 250);
        button_DeletePost.setOnAction((event) -> {ControllerPosts.performDeletePost(); });
        
        setupButtonUI(button_EditPost, "Dialog", 18, 250, Pos.CENTER, 500, 300);
        button_EditPost.setOnAction((event) -> {ControllerPosts.performEditPost(); });
        
        setupButtonUI(button_back, "Dialog", 18, 250, Pos.CENTER, 500, 350);
        button_back.setOnAction((event) -> {ControllerPosts.performBack(); });
        
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {ControllerPosts.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 500, 540);
        button_Quit.setOnAction((event) -> {ControllerPosts.performQuit(); });
        
        postsPanel.getChildren().addAll(button_Logout, button_Quit, button_viewReplies,button_DeletePost,
        		button_EditPost, button_CreatePost,list_Posts, label_ViewPostsTitle, button_back);
		
	}
	
	private void createPostPanel() { // Panel gui for creating a Post
		
		setupLabelUI(label_PostsTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		setupTextAreaUI(text_PostContent, "Dialog", 18, 300, 200, 40, 150);
		
		setupButtonUI(button_SubmitPost, "Dialog", 18, 250, Pos.CENTER, 500, 150);
		button_SubmitPost.setOnAction((event) -> {ControllerPosts.performSubmitPost(); });

        setupButtonUI(button_CancelPost, "Dialog", 18, 250, Pos.CENTER, 500, 250);
        button_CancelPost.setOnAction((event) -> {ControllerPosts.performCancel(); });
        
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {ControllerPosts.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 500, 540);
        button_Quit.setOnAction((event) -> {ControllerPosts.performQuit(); });
        
        createPostPanel.getChildren().addAll(button_Logout, button_Quit,button_CancelPost,
        		label_PostsTitle,text_PostContent, button_SubmitPost);
		
	}
	
	private void repliesPanel() { // First view of all replies in a given post
	    
	    setupLabelUI(label_ViewReplyTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
	   
	    setupTextAreaUI(text_PostInReply, "Dialog", 14, 450, 80, 20, 60);
	    text_PostInReply.setEditable(false);  
	    
	    setupListViewUI(list_Replies, "Dialog", 18, 450, 280, 20, 160);
	    
	    setupButtonUI(button_CreateReply, "Dialog", 18, 250, Pos.CENTER, 500, 150);
	    button_CreateReply.setOnAction((event) -> {ControllerPosts.performCreateReply(); });
	   
	    setupButtonUI(button_DeleteReply, "Dialog", 18, 250, Pos.CENTER, 500, 200);
        button_DeleteReply.setOnAction((event) -> {ControllerPosts.performDeleteReply(); });
        
        setupButtonUI(button_EditReply, "Dialog", 18, 250, Pos.CENTER, 500, 250);
        button_EditReply.setOnAction((event) -> {ControllerPosts.performEditReply(); });
        
        setupButtonUI(button_BackToPosts, "Dialog", 18, 250, Pos.CENTER, 500, 300);
	    button_BackToPosts.setOnAction((event) -> {ControllerPosts.performBackToPosts(); });
        
	   
	    setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
	    button_Logout.setOnAction((event) -> {ControllerPosts.performLogout(); });
	    
	    setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 500, 540);
	    button_Quit.setOnAction((event) -> {ControllerPosts.performQuit(); });
	   
	    repliesPanel.getChildren().addAll(button_Logout, button_Quit, button_BackToPosts, button_CreateReply, 
	            list_Replies, text_PostInReply, label_ViewReplyTitle,button_EditReply, button_DeleteReply);
	}
	
	
	private void createReplyPanel() { // Panel gui for creating a Reply
		
		setupLabelUI(label_ReplyTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		setupTextAreaUI(text_ReplyContent, "Dialog", 18, 300, 200, 40, 150);
		
		setupButtonUI(button_SubmitReply, "Dialog", 18, 250, Pos.CENTER, 500, 150);
		button_SubmitReply.setOnAction((event) -> {ControllerPosts.performSubmitReply(); });

        setupButtonUI(button_CancelReply, "Dialog", 18, 250, Pos.CENTER, 500, 250);
        button_CancelReply.setOnAction((event) -> {ControllerPosts.performReplyCancel(); });
        
        
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {ControllerPosts.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 500, 540);
        button_Quit.setOnAction((event) -> {ControllerPosts.performQuit(); });
        
        createReplyPanel.getChildren().addAll(button_Logout, button_Quit,button_CancelReply,
        		button_SubmitReply, label_ReplyTitle, text_ReplyContent);
	}
	
	
	
	protected static void showPostsPanel(){	// reveal Posts Panel hide everything else
	    ViewPosts.hideAllPanels();
	    ViewPosts.postsPanel.setVisible(true);
	 
	}
	
	protected static void showCreatePostsPanel(){	// reveal Create Posts Panel hide everything else
	    ViewPosts.hideAllPanels();
	    ViewPosts.createPostPanel.setVisible(true);
	}
	
	protected static void showRepliesPanel(){	// reveal Replies Panel hide everything else
	    ViewPosts.hideAllPanels();
	    ViewPosts.repliesPanel.setVisible(true);
	}
	
	protected static void showCreateReplyPanel(){	// reveal Create Reply Panel hide everything else
	    ViewPosts.hideAllPanels();
	    ViewPosts.createReplyPanel.setVisible(true);
	}
	
	
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
	
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
	
	private static void setupTextAreaUI(TextArea t, String ff, double f, double w, double h, double x, double y) {
		t.setFont(Font.font(ff, f));
		t.setPrefWidth(w);
		t.setPrefHeight(h);
		t.setLayoutX(x);
		t.setLayoutY(y);
	}
	
	private static void setupListViewUI(ListView<String> l, String ff, double f, double w, double h, double x, double y) {
		l.setStyle("-fx-font-family: '" + ff + "'; -fx-font-size: " + f + ";");
		l.setPrefWidth(w);
		l.setPrefHeight(h);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	
	
}