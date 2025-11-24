package guiPostsAdmin;

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
 * <p> Title: ViewPostsAdmin Class </p>
 * 
 * <p> Description: This class manages all GUI scenes for the admin posts interface.
 * It uses a single-stage, multi-panel approach where different panels are shown/hidden
 * rather than creating multiple windows.</p>
 * 
 * <p>Panel Architecture:
 * - PostsPanel: List of all posts with action buttons
 * - CreatePostPanel: Text area for creating new posts
 * - RepliesPanel: Shows a post and all its replies
 * - CreateReplyPanel: Text area for creating new replies
 * </p>
 * 
 * <p>Why this design?
 * - Efficient memory use (one stage, reused components)
 * - Smooth transitions (no window closing/opening)
 * - Consistent window size and position
 * - Easier state management
 * </p>
 * 
 * <p>This is the VIEW in the MVC pattern - handles only display, no business logic.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Emmanuel Zelaya-Armenta, Lynn Robert Carter
 * @version 1.00 2025-10-26 Admin-specific post management interface
 */
public class ViewPostsAdmin {
	
	// ========== WINDOW DIMENSIONS ==========
	// Get standard window size from main application
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// ========== SHARED APPLICATION STATE ==========
	// These static variables are shared across all methods
	
	protected static User theUser;				// The currently logged-in admin user
	static Database theDatabase = applicationMain.FoundationsMain.database;  // Database reference
	static int currentPostID = -1; 				// ID of post currently being viewed (-1 = none)
	
	private static ViewPostsAdmin theView;		// Singleton instance of this view class
	protected static Stage theStage;			// The JavaFX Stage (window)
	
	// ========== GUI LABELS ==========
	// Labels provide text instructions/titles for each panel
	
	private static Label label_PostsTitle = new Label("Create Post Here!");
	private static Label label_ViewPostsTitle = new Label("View Posts Here!");
	private static Label label_ReplyTitle = new Label("Create Reply Here!");
	private static Label label_ViewReplyTitle = new Label("View Reply's Here!");
	
	// ========== GUI BUTTONS ==========
	// Buttons trigger actions via event handlers
	
	// Navigation buttons (appear on multiple panels)
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	protected static Button button_back = new Button("Go Back Home");
	
	// Posts panel buttons
	protected static Button button_viewReplies = new Button("View Replies");
	protected static Button button_CreatePost = new Button("Create Post");
	protected static Button button_DeletePost = new Button("Delete Post");
	protected static Button button_EditPost = new Button("Edit Post");
	
	// Create post panel buttons
	protected static Button button_SubmitPost = new Button("Submit Post");
	protected static Button button_CancelPost = new Button("Cancel");
	
	// Replies panel buttons
	protected static Button button_CreateReply = new Button("Create Reply");
	protected static Button button_DeleteReply = new Button("Delete Reply");
	protected static Button button_EditReply = new Button("Edit Reply");
	protected static Button button_BackToPosts = new Button("Back To Posts");
	
	// Create reply panel buttons
	protected static Button button_SubmitReply = new Button("Submit Reply");
	protected static Button button_CancelReply = new Button("Cancel");
	
	// ========== LIST VIEWS ==========
	// ListViews display scrollable lists of items (posts or replies)
	// String type means each list item is a formatted string
	
	protected static ListView<String> list_Posts = new ListView<>();      // List of all posts
	protected static ListView<String> list_Replies = new ListView<>();    // List of replies for one post
	
	// ========== TEXT AREAS ==========
	// TextAreas allow multi-line text input/display
	
	protected static TextArea text_PostContent = new TextArea();       // For creating new posts
	protected static TextArea text_PostInReply = new TextArea();       // Displays post being replied to
	protected static TextArea text_ReplyContent = new TextArea();      // For creating new replies
	
	// ========== SCENE AND PANELS ==========
	// The scene holds all panels, but only one panel is visible at a time
	
	private static Pane mainPane = new Pane();                          // Root pane holding all panels
	private static Scene mainScene = new Scene(mainPane, width, height); // The scene displayed on stage
	
	// The four different panels (sub-panes) that can be shown/hidden
	private static Pane postsPanel = new Pane();           // Panel 1: View all posts
	private static Pane createPostPanel = new Pane();      // Panel 2: Create new post
	private static Pane repliesPanel = new Pane();         // Panel 3: View post replies
	private static Pane createReplyPanel = new Pane();     // Panel 4: Create new reply
	
	/*******
	 * <p> Method: displayPostsAdmin </p>
	 * 
	 * <p> Description: Entry point for displaying the admin posts interface. This is called
	 * from other parts of the application (like Admin Home) to show this view.</p>
	 * 
	 * <p>Singleton Pattern:
	 * - First call creates the view (theView == null)
	 * - Subsequent calls reuse existing view
	 * - Saves memory and maintains state
	 * </p>
	 * 
	 * @param ps the JavaFX Stage to display on
	 * @param user the currently logged-in admin user
	 */
	public static void displayPostsAdmin(Stage ps, User user) {
		// Store references for use throughout this class
		theStage = ps;
		theUser = user;
	
		// Singleton check: create view only if it doesn't exist
		if (theView == null) {
			theView = new ViewPostsAdmin();		// Create and initialize the GUI
		}
		
		// Set the scene on the stage and show it
		theStage.setScene(mainScene);
	    theStage.show();
	}
	
	/*******
	 * <p> Method: hideAllPanels </p>
	 * 
	 * <p> Description: Hides all four panels. This is called before showing a specific panel
	 * to ensure only one panel is visible at a time.</p>
	 * 
	 * <p>Why hide all first?
	 * - Prevents overlapping panels
	 * - Clean transition between views
	 * - Simpler than tracking which panel was previously visible
	 * </p>
	 */
	protected static void hideAllPanels(){
		// Set visible property to false for all panels
		postsPanel.setVisible(false);
		createPostPanel.setVisible(false);
		repliesPanel.setVisible(false);
		createReplyPanel.setVisible(false);
	}
	
	/*******
	 * <p> Constructor: ViewPostsAdmin </p>
	 * 
	 * <p> Description: Private constructor that initializes all GUI components. Called only
	 * once (singleton pattern). Sets up all four panels and adds them to the main pane.</p>
	 * 
	 * <p>Initialization sequence:
	 * 1. Add all panels to mainPane
	 * 2. Hide everything initially
	 * 3. Initialize each panel's components
	 * 4. Show posts panel by default
	 * </p>
	 */
	private ViewPostsAdmin(){
		// Add all four panels to the main pane
		// They're all added at once but only one is visible at a time
		mainPane.getChildren().addAll(postsPanel, createPostPanel, repliesPanel, createReplyPanel);
		
		// Hide everything initially for clean setup
		hideAllPanels();
		
		// Initialize each panel's GUI components
		postsPanel();          // Set up posts list panel
		createPostPanel();     // Set up create post panel
		repliesPanel();        // Set up replies view panel
		createReplyPanel();    // Set up create reply panel
		
		// Show the posts panel as the default starting view
		postsPanel.setVisible(true);
	}
	
	/*******
	 * <p> Method: postsPanel </p>
	 * 
	 * <p> Description: Initializes the posts panel GUI - the main view showing all posts.
	 * This panel allows admins to view, create, edit, delete posts, and view their replies.</p>
	 * 
	 * <p>Layout:
	 * - Title at top
	 * - ListView of posts (left side, scrollable)
	 * - Action buttons (right side)
	 * - Navigation buttons (bottom)
	 * </p>
	 */
	private void postsPanel() {
		// Set up the title label at the top of the panel
		setupLabelUI(label_ViewPostsTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		// Load and display all posts from the database
		// This populates the ListView with current post data
		ControllerPostsAdmin.performViewPosts();
		
		// Set up the ListView that displays all posts
		// Parameters: widget, font, font size, width, height, x position, y position
		setupListViewUI(list_Posts, "Dialog", 18, 450, 300, 20, 150);
		
		// Set up "Create Post" button and attach event handler
		setupButtonUI(button_CreatePost, "Dialog", 18, 250, Pos.CENTER, 500, 150);
        button_CreatePost.setOnAction((event) -> {
        	// When clicked, switch to create post panel
        	ControllerPostsAdmin.performCreatePost();
        });
        
        // Set up "View Replies" button
        setupButtonUI(button_viewReplies, "Dialog", 18, 250, Pos.CENTER, 500, 200);
        button_viewReplies.setOnAction((event) -> {
        	// When clicked, show replies for selected post
        	ControllerPostsAdmin.performViewReplies();
        });
        
        // Set up "Delete Post" button
        setupButtonUI(button_DeletePost, "Dialog", 18, 250, Pos.CENTER, 500, 250);
        button_DeletePost.setOnAction((event) -> {
        	// When clicked, delete the selected post
        	ControllerPostsAdmin.performDeletePost();
        });
        
        // Set up "Edit Post" button
        setupButtonUI(button_EditPost, "Dialog", 18, 250, Pos.CENTER, 500, 300);
        button_EditPost.setOnAction((event) -> {
        	// When clicked, allow editing of selected post
        	ControllerPostsAdmin.performEditPost();
        });
        
        // Set up "Go Back Home" button
        setupButtonUI(button_back, "Dialog", 18, 250, Pos.CENTER, 500, 350);
        button_back.setOnAction((event) -> {
        	// When clicked, return to Admin Home page
        	ControllerPostsAdmin.performBack();
        });
        
        // Set up "Logout" button at bottom left
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {
        	// When clicked, log out and go to login page
        	ControllerPostsAdmin.performLogout();
        });
        
        // Set up "Quit" button at bottom right
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 500, 540);
        button_Quit.setOnAction((event) -> {
        	// When clicked, exit the application
        	ControllerPostsAdmin.performQuit();
        });
        
        // Add all components to the posts panel
        // The order here doesn't affect visual layering (z-index is automatic)
        postsPanel.getChildren().addAll(
        		button_Logout, button_Quit, button_viewReplies, button_DeletePost,
        		button_EditPost, button_CreatePost, list_Posts, label_ViewPostsTitle, button_back
        );
	}
	
	/*******
	 * <p> Method: createPostPanel </p>
	 * 
	 * <p> Description: Initializes the create post panel - where admins type new post content.</p>
	 * 
	 * <p>Layout:
	 * - Title at top
	 * - Large text area for post content (center)
	 * - Submit and Cancel buttons (right side)
	 * - Navigation buttons (bottom)
	 * </p>
	 */
	private void createPostPanel() {
		// Set up title label
		setupLabelUI(label_PostsTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		// Set up the text area where admin types the post
		// Parameters: widget, font, font size, width, height, x, y
		setupTextAreaUI(text_PostContent, "Dialog", 18, 300, 200, 40, 150);
		
		// Set up "Submit Post" button
		setupButtonUI(button_SubmitPost, "Dialog", 18, 250, Pos.CENTER, 500, 150);
		button_SubmitPost.setOnAction((event) -> {
			// When clicked, create the post in database
			ControllerPostsAdmin.performSubmitPost();
		});

		// Set up "Cancel" button
        setupButtonUI(button_CancelPost, "Dialog", 18, 250, Pos.CENTER, 500, 250);
        button_CancelPost.setOnAction((event) -> {
        	// When clicked, discard typed content and go back
        	ControllerPostsAdmin.performCancel();
        });
        
        // Set up "Logout" button
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {
        	ControllerPostsAdmin.performLogout();
        });
        
        // Set up "Quit" button
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 500, 540);
        button_Quit.setOnAction((event) -> {
        	ControllerPostsAdmin.performQuit();
        });
        
        // Add all components to the create post panel
        createPostPanel.getChildren().addAll(
        		button_Logout, button_Quit, button_CancelPost,
        		label_PostsTitle, text_PostContent, button_SubmitPost
        );
	}
	
	/*******
	 * <p> Method: repliesPanel </p>
	 * 
	 * <p> Description: Initializes the replies panel - shows a post and all its replies.
	 * This creates a "thread" view.</p>
	 * 
	 * <p>Layout:
	 * - Title at top
	 * - Read-only text area showing the original post
	 * - ListView of all replies to that post
	 * - Action buttons for replies (right side)
	 * - Navigation buttons (bottom)
	 * </p>
	 */
	private void repliesPanel() {
	    // Set up title label
	    setupLabelUI(label_ViewReplyTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
	   
	    // Set up text area displaying the post being replied to
	    // This is read-only (set in setupTextAreaUI) so user can't edit the original post
	    setupTextAreaUI(text_PostInReply, "Dialog", 14, 450, 80, 20, 60);
	    text_PostInReply.setEditable(false);  // Make it read-only
	    
	    // Set up ListView showing all replies
	    setupListViewUI(list_Replies, "Dialog", 18, 450, 280, 20, 160);
	    
	    // Set up "Create Reply" button
	    setupButtonUI(button_CreateReply, "Dialog", 18, 250, Pos.CENTER, 500, 150);
	    button_CreateReply.setOnAction((event) -> {
	    	// When clicked, switch to create reply panel
	    	ControllerPostsAdmin.performCreateReply();
	    });
	   
	    // Set up "Delete Reply" button
	    setupButtonUI(button_DeleteReply, "Dialog", 18, 250, Pos.CENTER, 500, 200);
        button_DeleteReply.setOnAction((event) -> {
        	// When clicked, delete selected reply
        	ControllerPostsAdmin.performDeleteReply();
        });
        
        // Set up "Edit Reply" button
        setupButtonUI(button_EditReply, "Dialog", 18, 250, Pos.CENTER, 500, 250);
        button_EditReply.setOnAction((event) -> {
        	// When clicked, edit selected reply
        	ControllerPostsAdmin.performEditReply();
        });
        
        // Set up "Back To Posts" button
        setupButtonUI(button_BackToPosts, "Dialog", 18, 250, Pos.CENTER, 500, 300);
	    button_BackToPosts.setOnAction((event) -> {
	    	// When clicked, return to posts list view
	    	ControllerPostsAdmin.performBackToPosts();
	    });
        
	    // Set up "Logout" button
	    setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
	    button_Logout.setOnAction((event) -> {
	    	ControllerPostsAdmin.performLogout();
	    });
	    
	    // Set up "Quit" button
	    setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 500, 540);
	    button_Quit.setOnAction((event) -> {
	    	ControllerPostsAdmin.performQuit();
	    });
	   
	    // Add all components to the replies panel
	    repliesPanel.getChildren().addAll(
	    		button_Logout, button_Quit, button_BackToPosts, button_CreateReply, 
	            list_Replies, text_PostInReply, label_ViewReplyTitle, button_EditReply, 
	            button_DeleteReply
	    );
	}
	
	/*******
	 * <p> Method: createReplyPanel </p>
	 * 
	 * <p> Description: Initializes the create reply panel - where admins type new reply content.</p>
	 * 
	 * <p>Layout is similar to createPostPanel but for replies.</p>
	 */
	private void createReplyPanel() {
		// Set up title label
		setupLabelUI(label_ReplyTitle, "Arial", 32, width, Pos.CENTER, 0, 10);
		
		// Set up text area for typing reply content
		setupTextAreaUI(text_ReplyContent, "Dialog", 18, 300, 200, 40, 150);
		
		// Set up "Submit Reply" button
		setupButtonUI(button_SubmitReply, "Dialog", 18, 250, Pos.CENTER, 500, 150);
		button_SubmitReply.setOnAction((event) -> {
			// When clicked, create reply in database
			ControllerPostsAdmin.performSubmitReply();
		});

		// Set up "Cancel" button
        setupButtonUI(button_CancelReply, "Dialog", 18, 250, Pos.CENTER, 500, 250);
        button_CancelReply.setOnAction((event) -> {
        	// When clicked, discard typed content and go back
        	ControllerPostsAdmin.performReplyCancel();
        });
        
        // Set up "Logout" button
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {
        	ControllerPostsAdmin.performLogout();
        });
        
        // Set up "Quit" button
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 500, 540);
        button_Quit.setOnAction((event) -> {
        	ControllerPostsAdmin.performQuit();
        });
        
        // Add all components to the create reply panel
        createReplyPanel.getChildren().addAll(
        		button_Logout, button_Quit, button_CancelReply,
        		button_SubmitReply, label_ReplyTitle, text_ReplyContent
        );
	}
	
	// ========== PANEL VISIBILITY CONTROL METHODS ==========
	// These methods control which panel is visible
	
	/*******
	 * <p> Method: showPostsPanel </p>
	 * 
	 * <p> Description: Hides all panels then shows only the posts panel.
	 * This is the "home" view of the posts interface.</p>
	 */
	protected static void showPostsPanel(){
	    ViewPostsAdmin.hideAllPanels();           // Hide everything first
	    ViewPostsAdmin.postsPanel.setVisible(true);  // Show only posts panel
	}
	
	/*******
	 * <p> Method: showCreatePostsPanel </p>
	 * 
	 * <p> Description: Shows the create post panel for typing new post content.</p>
	 */
	protected static void showCreatePostsPanel(){
	    ViewPostsAdmin.hideAllPanels();
	    ViewPostsAdmin.createPostPanel.setVisible(true);
	}
	
	/*******
	 * <p> Method: showRepliesPanel </p>
	 * 
	 * <p> Description: Shows the replies panel displaying a post and its replies.</p>
	 */
	protected static void showRepliesPanel(){
	    ViewPostsAdmin.hideAllPanels();
	    ViewPostsAdmin.repliesPanel.setVisible(true);
	}
	
	/*******
	 * <p> Method: showCreateReplyPanel </p>
	 * 
	 * <p> Description: Shows the create reply panel for typing new reply content.</p>
	 */
	protected static void showCreateReplyPanel(){
	    ViewPostsAdmin.hideAllPanels();
	    ViewPostsAdmin.createReplyPanel.setVisible(true);
	}
	
	// ========== UI HELPER METHODS ==========
	// These methods reduce code duplication by standardizing component setup
	
	/*******
	 * <p> Method: setupLabelUI </p>
	 * 
	 * <p> Description: Helper method to configure a Label widget with consistent styling.</p>
	 * 
	 * <p>Why use helper methods?
	 * - Reduces repetitive code (DRY principle - Don't Repeat Yourself)
	 * - Ensures consistent styling across all labels
	 * - Makes it easy to change styling in one place
	 * - More readable code in the panel setup methods
	 * </p>
	 * 
	 * @param l the Label to configure
	 * @param ff font family name (e.g., "Arial")
	 * @param f font size in points
	 * @param w width of the label
	 * @param p alignment position (LEFT, CENTER, RIGHT)
	 * @param x x-coordinate (distance from left edge)
	 * @param y y-coordinate (distance from top edge)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		// Set the font family and size
		l.setFont(Font.font(ff, f));
		
		// Set minimum width (label won't shrink below this)
		l.setMinWidth(w);
		
		// Set text alignment within the label
		l.setAlignment(p);
		
		// Set position on the pane
		l.setLayoutX(x);  // Horizontal position
		l.setLayoutY(y);  // Vertical position
	}
	
	/*******
	 * <p> Method: setupButtonUI </p>
	 * 
	 * <p> Description: Helper method to configure a Button widget with consistent styling.</p>
	 * 
	 * <p>Parameters are similar to setupLabelUI - consistency makes code easier to understand.</p>
	 * 
	 * @param b the Button to configure
	 * @param ff font family name
	 * @param f font size in points
	 * @param w width of the button
	 * @param p alignment position
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		// Set font
		b.setFont(Font.font(ff, f));
		
		// Set minimum width (button won't shrink below this)
		b.setMinWidth(w);
		
		// Set text alignment within button
		b.setAlignment(p);
		
		// Set position on pane
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
	
	/*******
	 * <p> Method: setupTextAreaUI </p>
	 * 
	 * <p> Description: Helper method to configure a TextArea widget.</p>
	 * 
	 * <p>TextArea differences from TextField:
	 * - Supports multiple lines of text
	 * - Has scrollbars if content exceeds size
	 * - Better for longer content (posts, replies)
	 * </p>
	 * 
	 * @param t the TextArea to configure
	 * @param ff font family name
	 * @param f font size in points
	 * @param w preferred width
	 * @param h preferred height
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	private static void setupTextAreaUI(TextArea t, String ff, double f, double w, double h, double x, double y) {
		// Set font
		t.setFont(Font.font(ff, f));
		
		// Set preferred width (can be resized by user)
		t.setPrefWidth(w);
		
		// Set preferred height
		t.setPrefHeight(h);
		
		// Set position on pane
		t.setLayoutX(x);
		t.setLayoutY(y);
	}
	
	/*******
	 * <p> Method: setupListViewUI </p>
	 * 
	 * <p> Description: Helper method to configure a ListView widget.</p>
	 * 
	 * <p>ListView Notes:
	 * - Displays a scrollable list of items
	 * - Items are displayed as strings
	 * - User can select one item at a time (by default)
	 * - Selected item can be retrieved with getSelectionModel().getSelectedItem()
	 * </p>
	 * 
	 * <p>The setStyle() method uses CSS-like syntax to set the font. This is necessary
	 * because ListView doesn't have a setFont() method like other controls.</p>
	 * 
	 * @param l the ListView to configure
	 * @param ff font family name
	 * @param f font size in points
	 * @param w preferred width
	 * @param h preferred height
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	private static void setupListViewUI(ListView<String> l, String ff, double f, double w, double h, double x, double y) {
		// Set font using CSS-style syntax
		// Format: -fx-font-family: 'FontName'; -fx-font-size: SizePx;
		l.setStyle("-fx-font-family: '" + ff + "'; -fx-font-size: " + f + ";");
		
		// Set preferred width
		l.setPrefWidth(w);
		
		// Set preferred height
		l.setPrefHeight(h);
		
		// Set position on pane
		l.setLayoutX(x);
		l.setLayoutY(y);
	}
}