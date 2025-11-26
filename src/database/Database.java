package database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
			createPostTables();
			createRepliesTables();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10))";
	    statement.execute(invitationCodesTable);
	}
	
	private void createPostTables() throws SQLException {
	    String postTable = "CREATE TABLE IF NOT EXISTS Post ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "author VARCHAR(255) NOT NULL, "
	            + "content VARCHAR(500) NOT NULL, "
	            + "authorRole VARCHAR(10), "
	            + "isPinned BOOLEAN DEFAULT FALSE, "
	            + "pinnedBy VARCHAR(255))"
	    		+ "hasUnreadReplies BOOLEAN DEFAULT FALSE, "
	    		+ "lastReplyTimestamp TIMESTAMP)";
	    
	    statement.execute(postTable);
	}
	
	private void createRepliesTables() throws SQLException {  
	    String replyTable = "CREATE TABLE IF NOT EXISTS Reply ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "postID INT NOT NULL, "
	            + "author VARCHAR(255) NOT NULL, "
	            + "content VARCHAR(500) NOT NULL, "
	            + "authorRole VARCHAR(10), " 
	            + "FOREIGN KEY (postID) REFERENCES Post(id))";
	    statement.execute(replyTable);
	}

	
	 // Method: createPost(String author, String content) </p>
	
	public int createPost(String author, String content, String authorRole) throws SQLException {
	    // SQL statement with ? placeholders for parameters (prevents SQL injection)
	    String insertPost = "INSERT INTO Post (author, content, authorRole) VALUES (?, ?, ?)";
	    
	    // try-with-resources ensures the PreparedStatement is automatically closed when done
	    // Statement.RETURN_GENERATED_KEYS tells database to give us back the auto-generated ID
	    try (PreparedStatement pstmt = connection.prepareStatement(insertPost, 
	            Statement.RETURN_GENERATED_KEYS)) {
	        
	        // Set the first ? to the author parameter (index starts at 1, not 0)
	        pstmt.setString(1, author);
	        
	        // Set the second ? to the content parameter
	        pstmt.setString(2, content);
	        
	        // Set the third ? to the authorRole parameter
	        pstmt.setString(3, authorRole);
	        
	        // Execute the INSERT statement and get back the number of rows affected
	        int rowsInserted = pstmt.executeUpdate();
	        
	        // If at least one row was inserted successfully
	        if (rowsInserted > 0) {
	            // Get the auto-generated keys (in this case, the post ID)
	            ResultSet generatedKeys = pstmt.getGeneratedKeys();
	            
	            // If there is a generated key available
	            if (generatedKeys.next()) {
	                // Return the generated post ID (column 1 is the first/only key)
	                return generatedKeys.getInt(1);
	            }
	        }
	    } catch (SQLException e) {
	        // If anything goes wrong, print the error details
	        e.printStackTrace();
	    }
	    
	    // If we get here, something went wrong - return -1 to indicate failure
	    return -1;
	}

	
	// return a Post object if found, null if not found
	 
	public Post getPost(int postID) throws SQLException {
	    // SQL query to select all columns from Post table where id matches
	    String query = "SELECT * FROM Post WHERE id = ?";
	    
	    // try-with-resources automatically closes PreparedStatement when done
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        // Set the ? placeholder to the postID we're looking for
	        pstmt.setInt(1, postID);
	        
	        // Execute the query and get back a ResultSet (contains the matching rows)
	        ResultSet rs = pstmt.executeQuery();
	        
	        // Check if we found a matching row (next() returns true if row exists)
	        if (rs.next()) {
	            return new Post(
	                rs.getInt("id"),
	                rs.getString("author"),
	                rs.getString("content"),
	                rs.getString("authorRole"),
	                rs.getBoolean("isPinned"),
	                rs.getString("pinnedBy")
	            );
	        }
	    } catch (SQLException e) {
	        // Print error details if something goes wrong
	        e.printStackTrace();
	    }
	    
	    // If we get here, no post was found or there was an error - return null
	    return null;
	}

	/*******
	 * <p> Method: getAllPosts() </p>
	 * 
	 * @return a List of Post objects for all posts
	 */
	public List<Post> getAllPosts() throws SQLException {
	    // Create a new empty ArrayList to store all the Post objects we'll find
	    List<Post> posts = new ArrayList<>();
	    
	    // SQL query to select ALL rows and columns from the Post table
	    // No WHERE clause means we get everything
	    String query = "SELECT * FROM Post ORDER BY isPinned DESC, id DESC";
	    
	    // try-with-resources ensures PreparedStatement is closed automatically
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        // Execute the query and get back all matching rows
	        ResultSet rs = pstmt.executeQuery();
	        
	        // Loop through each row in the ResultSet
	        // next() moves to the next row and returns false when no more rows exist
	        while (rs.next()) {
	            Post post = new Post(
	                rs.getInt("id"),
	                rs.getString("author"),
	                rs.getString("content"),
	                rs.getString("authorRole"),
	                rs.getBoolean("isPinned"),
	                rs.getString("pinnedBy")
	            );
	            posts.add(post);
	        }
	    } catch (SQLException e) {
	        // If anything goes wrong, print the error
	        e.printStackTrace();
	    }
	    
	    // Return the list of all posts (could be empty if no posts exist)
	    return posts;
	}

	

	
	/*******
	 * <p> Method: updatePost(int postID, String content) </p>
	 */
	public boolean updatePost(int postID, String content) throws SQLException {
	    // SQL UPDATE statement - sets content field to new value for specific post
	    String updatePost = "UPDATE Post SET content = ? WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(updatePost)) {
	        // Set first ? to the new content
	        pstmt.setString(1, content);
	        
	        // Set second ? to the ID of the post we're updating
	        pstmt.setInt(2, postID);
	        
	        // Execute the update and get number of rows affected
	        int rowsUpdated = pstmt.executeUpdate();
	        
	        // If at least one row was updated, return true (success)
	        // If rowsUpdated is 0, the post ID probably doesn't exist
	        return rowsUpdated > 0;
	    } catch (SQLException e) {
	        // Print error and fall through to return false
	        e.printStackTrace();
	    }
	    
	    // If we get here, update failed - return false
	    return false;
	}

	/*******
	 * <p> Method: deletePost(int postID) </p>
	 */
	public boolean deletePost(int postID) throws SQLException {
	    try {
	        // STEP 1: Delete all replies associated with this post
	        // This must happen first due to foreign key constraints
	        String deleteReplies = "DELETE FROM Reply WHERE postID = ?";
	        
	        try (PreparedStatement pstmt = connection.prepareStatement(deleteReplies)) {
	            // Set the ? to the post ID whose replies we want to delete
	            pstmt.setInt(1, postID);
	            
	            // Execute the delete - we don't care how many replies were deleted
	            pstmt.executeUpdate();
	        }
	        
	        // STEP 2: Now delete the post itself
	        // This is safe now because all its replies are gone
	        String deletePost = "DELETE FROM Post WHERE id = ?";
	        
	        try (PreparedStatement pstmt = connection.prepareStatement(deletePost)) {
	            // Set the ? to the post ID we want to delete
	            pstmt.setInt(1, postID);
	            
	            // Execute delete and get number of rows deleted
	            int rowsDeleted = pstmt.executeUpdate();
	            
	            // If at least one row (the post) was deleted, return true
	            return rowsDeleted > 0;
	        }
	    } catch (SQLException e) {
	        // If anything goes wrong, print error and return false
	        e.printStackTrace();
	    }
	    
	    // If we get here, deletion failed
	    return false;
	}

	// ========== REPLY CRUD OPERATIONS ==========

	/*******
	 * <p> Method: createReply(int postID, String author, String content) </p>
	 */
	
	public int createReply(int postID, String author, String content, String authorRole) throws SQLException {
	    String insertReply = "INSERT INTO Reply (postID, author, content, authorRole) VALUES (?, ?, ?, ?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(insertReply,
	            Statement.RETURN_GENERATED_KEYS)) {
	        
	        pstmt.setInt(1, postID);
	        pstmt.setString(2, author);
	        pstmt.setString(3, content);
	        pstmt.setString(4, authorRole);
	        
	        int rowsInserted = pstmt.executeUpdate();
	        
	        if (rowsInserted > 0) {
	            ResultSet generatedKeys = pstmt.getGeneratedKeys();
	            
	            if (generatedKeys.next()) {
	                int replyId = generatedKeys.getInt(1);
	                
	                // NEW: Mark the parent post as having unread replies
	                markPostAsHavingNewReply(postID);
	                
	                return replyId;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return -1;
	}
	
	/**
	 * Marks a post as having new unread replies.
	 * Called automatically when a reply is created.
	 * 
	 * @param postID the ID of the post that received a new reply
	 * @throws SQLException if database update fails
	 */
	private void markPostAsHavingNewReply(int postID) throws SQLException {
	    String updatePost = "UPDATE Post SET hasUnreadReplies = TRUE, lastReplyTimestamp = CURRENT_TIMESTAMP WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(updatePost)) {
	        pstmt.setInt(1, postID);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Gets the count of posts authored by a specific user that have unread replies.
	 * 
	 * @param username the username of the post author
	 * @return the number of posts with unread replies
	 * @throws SQLException if database query fails
	 */
	public int getUnreadReplyCount(String username) throws SQLException {
	    String query = "SELECT COUNT(*) FROM Post WHERE author = ? AND hasUnreadReplies = TRUE";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return 0;
	}
	
	/**
	 * Marks all replies for a specific post as read by clearing the unread flag.
	 * Called when the post author views the replies.
	 * 
	 * @param postID the ID of the post whose replies are being viewed
	 * @throws SQLException if database update fails
	 */
	public void markRepliesAsRead(int postID) throws SQLException {
	    String updatePost = "UPDATE Post SET hasUnreadReplies = FALSE WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(updatePost)) {
	        pstmt.setInt(1, postID);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/*******
	 * <p> Method: getReply(int replyID) </p>
	 * 
	 * @return a Reply object if found, null if not found
	 */
	public Reply getReply(int replyID) throws SQLException {
	    // SQL query to get all columns for a specific reply
	    String query = "SELECT * FROM Reply WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        // Set the reply ID we're looking for
	        pstmt.setInt(1, replyID);
	        
	        // Execute query
	        ResultSet rs = pstmt.executeQuery();
	        
	        // If we found the reply
	        if (rs.next()) {
	            // Create and return a Reply object with all the data including role
	            return new Reply(
	                rs.getInt("id"),              // Reply's unique ID
	                rs.getInt("postID"),          // ID of post this reply belongs to
	                rs.getString("author"),       // Username of reply author
	                rs.getString("content"),      // Reply text
	                rs.getString("authorRole")    // Role of author
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    // Reply not found or error occurred
	    return null;
	}

	/*******
	 * <p> Method: getRepliesByPost(int postID) </p>
	 * 
	 * @return a List of Reply objects for the post
	 */
	public List<Reply> getRepliesByPost(int postID) throws SQLException {
	    // Create empty list for replies
	    List<Reply> replies = new ArrayList<>();
	    
	    // SQL query with WHERE clause to filter by post ID
	    String query = "SELECT * FROM Reply WHERE postID = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        // Set which post's replies we want
	        pstmt.setInt(1, postID);
	        
	        // Execute query
	        ResultSet rs = pstmt.executeQuery();
	        
	        // Loop through all replies for this post
	        while (rs.next()) {
	            // Create Reply object from database row
	            Reply reply = new Reply(
	                rs.getInt("id"),
	                rs.getInt("postID"),
	                rs.getString("author"),
	                rs.getString("content"),
	                rs.getString("authorRole")
	            );
	            
	            // Add to list
	            replies.add(reply);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    // Return all replies for this post (empty list if post has no replies)
	    return replies;
	}


	/*******
	 * <p> Method: getAllReplies() </p>
	 * 
	 * @return a List of Reply objects for all replies
	 */
	public List<Reply> getAllReplies() throws SQLException {
		// Create empty list to store all replies
	    List<Reply> replies = new ArrayList<>();
	    
	    // SQL query to get ALL replies - no WHERE clause means get everything
	    String query = "SELECT * FROM Reply";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        // Execute the query - no parameters to set since we want all replies
	        ResultSet rs = pstmt.executeQuery();
	        
	        // Loop through every reply in the database
	        while (rs.next()) {
	            // Create a Reply object from the current row's data
	            Reply reply = new Reply(
	                rs.getInt("id"),              // Reply's unique ID
	                rs.getInt("postID"),          // Which post this reply belongs to
	                rs.getString("author"),       // Username of reply creator
	                rs.getString("content"),      // Reply text content
	                rs.getString("authorRole")    // Role of author when reply was created
	            );
	            
	            // Add this reply to our list
	            replies.add(reply);
	        }
	    } catch (SQLException e) {
	        // Print any errors that occur
	        e.printStackTrace();
	    }
	    
	    // Return the complete list of all replies
	    return replies;
	}


	/*******
	 * <p> Method: updateReply(int replyID, String content) </p>
	 */
	public boolean updateReply(int replyID, String content) throws SQLException {
	    // SQL UPDATE statement - changes content for a specific reply
	    String updateReply = "UPDATE Reply SET content = ? WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(updateReply)) {
	        // Set first ? to the new content text
	        pstmt.setString(1, content);
	        
	        // Set second ? to the ID of the reply we're updating
	        pstmt.setInt(2, replyID);
	        
	        // Execute the update and get number of rows affected
	        int rowsUpdated = pstmt.executeUpdate();
	        
	        // Return true if at least one row was updated
	        // If 0 rows updated, the reply ID probably doesn't exist
	        return rowsUpdated > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    // Update failed
	    return false;
	}

	/*******
	 * <p> Method: deleteReply(int replyID) </p>
	 */
	public boolean deleteReply(int replyID) throws SQLException {
	    // SQL DELETE statement - removes one reply by ID
	    String deleteReply = "DELETE FROM Reply WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteReply)) {
	        // Set the ? to the ID of reply we want to delete
	        pstmt.setInt(1, replyID);
	        
	        // Execute the delete and get number of rows deleted
	        int rowsDeleted = pstmt.executeUpdate();
	        
	        // Return true if at least one row (the reply) was deleted
	        return rowsDeleted > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    // Deletion failed
	    return false;
	}

	// ========== END OF CRUD OPERATIONS ==========

/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		 if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
		        throw new SQLException("Username cannot be empty");
		    }
	
		    String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
		            + "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
		            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);
			
			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
		System.out.println(userList);
		return userList;
	}

/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	        System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("preferredFirstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
	    String query = "SELECT * FROM userDB WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        // Check if user exists
	        if (!rs.next()) {
	            return false;  // User not found
	        }
	        
	        currentUsername = rs.getString(2);
	        currentPassword = rs.getString(3);
	        currentFirstName = rs.getString(4);
	        currentMiddleName = rs.getString(5);
	        currentLastName = rs.getString(6);
	        currentPreferredFirstName = rs.getString(7);
	        currentEmailAddress = rs.getString(8);
	        currentAdminRole = rs.getBoolean(9);
	        currentNewRole1 = rs.getBoolean(10);
	        currentNewRole2 = rs.getBoolean(11);
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();  // Add this for debugging
	        return false;
	    }
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role1") == 0) {
			String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole1 = true;
				else
					currentNewRole1 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Role2") == 0) {
			String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole2 = true;
				else
					currentNewRole2 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};

	
	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}
	
	/**
	 * Method to clear all data from all tables for testing
	 */
	public void clearAllTables() throws SQLException {
	    try {
	        statement.execute("DELETE FROM Reply");
	        statement.execute("DELETE FROM Post");
	        statement.execute("DELETE FROM userDB");
	        statement.execute("DELETE FROM InvitationCodes");
	    } catch (SQLException e) {
	        // Ignore errors if tables don't exist yet
	    }
	}
	
	public boolean pinPost(int postID, String pinnedBy) throws SQLException {
	    String countQuery = "SELECT COUNT(*) AS pinnedCount FROM Post WHERE isPinned = TRUE";
	    try (PreparedStatement countStmt = connection.prepareStatement(countQuery)) {
	        ResultSet rs = countStmt.executeQuery();
	        if (rs.next()) {
	            int pinnedCount = rs.getInt("pinnedCount");
	            if (pinnedCount >= 3) {
	                return false;
	            }
	        }
	    }
	    String update = "UPDATE Post SET isPinned = TRUE, pinnedBy = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(update)) {
	        pstmt.setString(1, pinnedBy);
	        pstmt.setInt(2, postID);
	        int rowsUpdated = pstmt.executeUpdate();
	        return rowsUpdated > 0;
	    }
	}

	public boolean unpinPost(int postID) throws SQLException {
	    String update = "UPDATE Post SET isPinned = FALSE, pinnedBy = NULL WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(update)) {
	        pstmt.setInt(1, postID);
	        int rowsUpdated = pstmt.executeUpdate();
	        return rowsUpdated > 0;
	    }
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
}
