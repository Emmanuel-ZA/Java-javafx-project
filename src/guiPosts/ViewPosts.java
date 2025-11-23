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

/**
 * <p><strong>Title:</strong> ViewPosts Class - Student Posts View</p>
 * 
 * <p><strong>Description:</strong> View component of MVC architecture for student discussion 
 * posts. Manages all GUI elements using a multi-panel design within a single scene for 
 * efficient navigation.</p>
 * 
 * <p><strong>MVC Role:</strong> View - Displays data and captures user input</p>
 * 
 * <p><strong>Architecture:</strong> Uses 4 panels in 1 scene instead of 4 separate scenes 
 * for better performance. Panels are shown/hidden based on user actions.</p>
 * 
 * <p><strong>Four Panel Structure:</strong></p>
 * <ol>
 * <li><strong>Posts Panel:</strong> View all posts (US-04), Edit/Delete buttons (US-02, US-03)</li>
 * <li><strong>Create Post Panel:</strong> Text area for new posts (US-01)</li>
 * <li><strong>Replies Panel:</strong> View replies to a post (US-05)</li>
 * <li><strong>Create Reply Panel:</strong> Text area for new replies (US-05)</li>
 * </ol>
 * 
 * <p><strong>Supported User Stories:</strong></p>
 * <ul>
 * <li><strong>US-01:</strong> Create posts - Create Post Panel</li>
 * <li><strong>US-02:</strong> Edit own posts - Edit button triggers dialog</li>
 * <li><strong>US-03:</strong> Delete own posts - Delete button in Posts Panel</li>
 * <li><strong>US-04:</strong> View all posts - ListView in Posts Panel</li>
 * <li><strong>US-05:</strong> Replies - Replies Panel + Create Reply Panel</li>
 * </ul>
 * 
 * <p><strong>Copyright:</strong> Lynn Robert Carter © 2025</p>
 * 
 * @author Team-25
 * @version 1.00 2025-10-12 Initial version with multi-panel architecture
 * 
 * @see guiPosts.ControllerPosts
 * @see guiPosts.ModelPosts
 */
public class ViewPosts {
	
	/**
	 * Window width from main application.
	 */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	
	/**
	 * Window height from main application.
	 */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	/**
	 * Currently logged-in user. Used for ownership checks and author attribution.
	 * 
	 * @see entityClasses.User
	 */
	protected static User theUser;
	
	/**
	 * Shared database instance for all CRUD operations.
	 * 
	 * @see database.Database
	 */
	static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**
	 * ID of post currently being viewed for replies. Used when creating new reply 
	 * to link it to parent post. Default -1 means no post selected.
	 * 
	 * <p><strong>US-05:</strong> Establishes parent-child relationship for replies</p>
	 */
	static int currentPostID = -1;
	
	/**
	 * Singleton instance.
	 */
	private static ViewPosts theView;
	
	/**
	 * JavaFX Stage container.
	 */
	protected static Stage theStage;
	
	// ==================== GUI COMPONENTS - LABELS ====================
	
	/**
	 * Title for Create Post Panel. US-01 (Create Posts)
	 */
	private static Label label_PostsTitle = new Label("Create Post Here!");
	
	/**
	 * Title for Posts Panel. US-04 (View Posts)
	 */
	private static Label label_ViewPostsTitle = new Label("View Posts Here!");
	
	/**
	 * Title for Create Reply Panel. US-05 (Replies)
	 */
	private static Label label_ReplyTitle = new Label("Create Reply Here!");
	
	/**
	 * Title for Replies Panel. US-05 (Replies)
	 */
	private static Label label_ViewReplyTitle = new Label("View Reply's Here!");
	
	// ==================== GUI COMPONENTS - BUTTONS ====================
	
	/**
	 * Logout button - ends session.
	 */
	protected static Button button_Logout = new Button("Logout");
	
	/**
	 * View Replies button - shows replies for selected post. US-05
	 */
	protected static Button button_viewReplies = new Button("View Replies");
	
	/**
	 * Create Post button - opens post creation interface. US-01
	 */
	protected static Button button_CreatePost = new Button("Create Post");
	
	/**
	 * Back button - returns to role-specific home.
	 */
	protected static Button button_back = new Button("Go Back Home");
	
	/**
	 * Quit button - exits application.
	 */
	protected static Button button_Quit = new Button("Quit");
	
	/**
	 * Submit Post button - creates new post. US-01
	 */
	protected static Button button_SubmitPost = new Button("Submit Post");
	
	/**
	 * Submit Reply button - creates new reply. US-05
	 */
	protected static Button button_SubmitReply = new Button("Submit Reply");
	
	/**
	 * Cancel Post button - discards post draft.
	 */
	protected static Button button_CancelPost = new Button("Cancel");
	
	/**
	 * Cancel Reply button - discards reply draft.
	 */
	protected static Button button_CancelReply = new Button("Cancel");
	
	/**
	 * Delete Post button - deletes selected post (ownership verified). US-03
	 */
	protected static Button button_DeletePost = new Button("Delete Post");
	
	/**
	 * Delete Reply button - deletes selected reply (ownership verified). US-05
	 */
	protected static Button button_DeleteReply = new Button("Delete Reply");
	
	/**
	 * Edit Post button - edits selected post (ownership verified). US-02
	 */
	protected static Button button_EditPost = new Button("Edit Post");
	
	/**
	 * Edit Reply button - edits selected reply (ownership verified). US-05
	 */
	protected static Button button_EditReply = new Button("Edit Reply");
	
	/**
	 * Create Reply button - opens reply creation interface. US-05
	 */
	protected static Button button_CreateReply = new Button("Create Reply");
	
	/**
	 * Back to Posts button - returns from replies to posts view.
	 */
	protected static Button button_BackToPosts = new Button("Back To Posts");
	
	// ==================== GUI COMPONENTS - LISTS ====================
	
	/**
	 * ListView displaying all posts. Format: "id: X author: Y [Role] content: Z"
	 * US-04 (View Posts)
	 */
	protected static ListView<String> list_Posts = new ListView<>();
	
	/**
	 * ListView displaying replies for current post. Format: "id: X author: Y [Role] content: Z"
	 * US-05 (Replies)
	 */
	protected static ListView<String> list_Replies = new ListView<>();
	
	// ==================== GUI COMPONENTS - TEXT AREAS ====================
	
	/**
	 * TextArea for creating new post content. US-01 (Create Posts)
	 */
	protected static TextArea text_PostContent = new TextArea();
	
	/**
	 * TextArea displaying original post (read-only context for replies). US-05
	 */
	protected static TextArea text_PostInReply = new TextArea();
	
	/**
	 * TextArea for creating new reply content. US-05 (Replies)
	 */
	protected static TextArea text_ReplyContent = new TextArea();
	
	// ==================== SCENE AND PANEL STRUCTURE ====================
	
	
	private static Pane mainPane = new Pane();
	private static Scene mainScene = new Scene(mainPane, width, height); // Main Scene!!!!!
	 
	private static Pane postsPanel = new Pane();		//Panel 1: View and manage posts
	private static Pane createPostPanel = new Pane(); 	//Panel 2: Create new posts
	private static Pane repliesPanel = new Pane();		//Panel 3: View and manage replies.
	private static Pane createReplyPanel = new Pane();	//Panel 4: Create new replies
	
	/**
	 * Entry point to display posts interface.
	 * 
	 * <p>Initializes View (singleton pattern) and shows Posts Panel by default.</p>
	 * 
	 * @param ps JavaFX Stage
	 * @param user Currently logged-in user
	 */
	public static void displayPosts(Stage ps, User user) {
		theStage = ps;
		theUser = user;
	
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewPosts();		// Instantiate singleton if needed
		theStage.setScene(mainScene);
	    theStage.show();
	}
	
	/**
	 * Hides all four panels. Called before showing a specific panel to ensure only 
	 * one is visible at a time.
	 */
	protected static void hideAllPanels(){
		postsPanel.setVisible(false);
		createPostPanel.setVisible(false);
		repliesPanel.setVisible(false);
		createReplyPanel.setVisible(false);
	}
	
	/**
	 * Private constructor implementing Singleton pattern.
	 * Initializes all four panels and shows Posts Panel by default.
	 */
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
		ControllerPosts.performViewPosts();
	}
	
	/**
	 * Initializes Posts Panel (main view).
	 * 
	 * <p><strong>User Stories:</strong></p>
	 * <ul>
	 * <li>US-02: Edit Post button</li>
	 * <li>US-03: Delete Post button</li>
	 * <li>US-04: ListView displays all posts with role badges</li>
	 * <li>US-05: View Replies button</li>
	 * </ul>
	 * 
	 * <p><strong>Layout:</strong> Title, ListView, action buttons (Create, View Replies, 
	 * Delete, Edit, Back), Logout, Quit</p>
	 */
	private void postsPanel() { // First Panel gui that shows all posts and option to create post
		
		setupLabelUI(label_ViewPostsTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		// US-04: Load all posts
		ControllerPosts.performViewPosts();
		setupListViewUI(list_Posts, "Dialog", 18, 450, 300, 20, 150 );
		
		// US-01: Create Post
		setupButtonUI(button_CreatePost, "Dialog", 18, 250, Pos.CENTER, 500, 150);
        button_CreatePost.setOnAction((event) -> {ControllerPosts.performCreatePost(); });
        
        // US-05: View Replies
        setupButtonUI(button_viewReplies, "Dialog", 18, 250, Pos.CENTER, 500, 200);
        button_viewReplies.setOnAction((event) -> {ControllerPosts.performViewReplies(); });
        
        // US-03: Delete Post (with ownership check)
        setupButtonUI(button_DeletePost, "Dialog", 18, 250, Pos.CENTER, 500, 250);
        button_DeletePost.setOnAction((event) -> {ControllerPosts.performDeletePost(); });
        
        // US-02: Edit Post (with ownership check)
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
	
	/**
	 * Initializes Create Post Panel.
	 * 
	 * <p><strong>US-01 (Create Posts):</strong> TextArea for content, Submit/Cancel buttons</p>
	 * 
	 * <p><strong>Layout:</strong> Title, TextArea, Submit, Cancel, Logout, Quit</p>
	 */
	private void createPostPanel() { // Panel gui for creating a Post
		
		setupLabelUI(label_PostsTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		// US-01: TextArea for post content
		setupTextAreaUI(text_PostContent, "Dialog", 18, 300, 200, 40, 150);
		
		// US-01: Submit creates post with role tracking
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
	
	/**
	 * Initializes Replies Panel.
	 * 
	 * <p><strong>US-05 (Replies):</strong> Shows original post (context) + list of replies</p>
	 * 
	 * <p><strong>Layout:</strong> Title, read-only post TextArea, replies ListView, 
	 * Create/Delete/Edit Reply buttons, Back to Posts, Logout, Quit</p>
	 */
	private void repliesPanel() { // First view of all replies in a given post
	    
	    setupLabelUI(label_ViewReplyTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
	   
	    // US-05: Show original post for context (read-only)
	    setupTextAreaUI(text_PostInReply, "Dialog", 14, 450, 80, 20, 60);
	    text_PostInReply.setEditable(false);  
	    
	    // US-05: ListView for replies
	    setupListViewUI(list_Replies, "Dialog", 18, 450, 280, 20, 160);
	    
	    // US-05: Create Reply
	    setupButtonUI(button_CreateReply, "Dialog", 18, 250, Pos.CENTER, 500, 150);
	    button_CreateReply.setOnAction((event) -> {ControllerPosts.performCreateReply(); });
	   
	    // US-05: Delete Reply (with ownership check)
	    setupButtonUI(button_DeleteReply, "Dialog", 18, 250, Pos.CENTER, 500, 200);
        button_DeleteReply.setOnAction((event) -> {ControllerPosts.performDeleteReply(); });
        
        // US-05: Edit Reply (with ownership check)
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
	
	/**
	 * Initializes Create Reply Panel.
	 * 
	 * <p><strong>US-05 (Replies):</strong> TextArea for reply content, Submit/Cancel buttons</p>
	 * 
	 * <p><strong>Layout:</strong> Title, TextArea, Submit, Cancel, Logout, Quit</p>
	 */
	private void createReplyPanel() { // Panel gui for creating a Reply
		
		setupLabelUI(label_ReplyTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		// US-05: TextArea for reply content
		setupTextAreaUI(text_ReplyContent, "Dialog", 18, 300, 200, 40, 150);
		
		// US-05: Submit creates reply linked to currentPostID
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
	
	/**
	 * Shows Posts Panel, hides all others. Used after post operations.
	 */
	protected static void showPostsPanel(){	// reveal Posts Panel hide everything else
	    ViewPosts.hideAllPanels();
	    ViewPosts.postsPanel.setVisible(true);
	 
	}
	
	/**
	 * Shows Create Post Panel, hides all others. US-01 (Create Posts)
	 */
	protected static void showCreatePostsPanel(){	// reveal Create Posts Panel hide everything else
	    ViewPosts.hideAllPanels();
	    ViewPosts.createPostPanel.setVisible(true);
	}
	
	/**
	 * Shows Replies Panel, hides all others. US-05 (Replies)
	 */
	protected static void showRepliesPanel(){	// reveal Replies Panel hide everything else
	    ViewPosts.hideAllPanels();
	    ViewPosts.repliesPanel.setVisible(true);
	}
	
	/**
	 * Shows Create Reply Panel, hides all others. US-05 (Replies)
	 */
	protected static void showCreateReplyPanel(){	// reveal Create Reply Panel hide everything else
	    ViewPosts.hideAllPanels();
	    ViewPosts.createReplyPanel.setVisible(true);
	}
	
	/**
	 * Configures Label with standard styling.
	 * 
	 * @param l Label to configure
	 * @param ff font family
	 * @param f font size
	 * @param w minimum width
	 * @param p alignment
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
	
	/**
	 * Configures Button with standard styling.
	 * 
	 * @param b Button to configure
	 * @param ff font family
	 * @param f font size
	 * @param w minimum width
	 * @param p alignment
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
	
	/**
	 * Configures TextArea with standard styling.
	 * 
	 * @param t TextArea to configure
	 * @param ff font family
	 * @param f font size
	 * @param w preferred width
	 * @param h preferred height
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	private static void setupTextAreaUI(TextArea t, String ff, double f, double w, double h, double x, double y) {
		t.setFont(Font.font(ff, f));
		t.setPrefWidth(w);
		t.setPrefHeight(h);
		t.setLayoutX(x);
		t.setLayoutY(y);
	}
	
	/**
	 * Configures ListView with standard styling. Used for displaying posts and replies 
	 * with format: "id: X author: Y [Role] content: Z"
	 * 
	 * @param l ListView to configure
	 * @param ff font family
	 * @param f font size
	 * @param w preferred width
	 * @param h preferred height
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	private static void setupListViewUI(ListView<String> l, String ff, double f, double w, double h, double x, double y) {
		l.setStyle("-fx-font-family: '" + ff + "'; -fx-font-size: " + f + ";");
		l.setPrefWidth(w);
		l.setPrefHeight(h);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
}