package statistics;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import database.Database;
import entityClasses.Post;
import entityClasses.Reply;


public class ReplyStatisticsCalculator {
    
    /**
     * The database connection used to query posts and replies
     */
    private Database database;
    
    /**
     * The minimum number of unique students that must be replied to
     * in order to meet the requirement
     */
    private static final int REQUIRED_UNIQUE_REPLIES = 3;
    
    
    public ReplyStatisticsCalculator(Database database) {
        // Validate that database is not null to prevent NullPointerExceptions later
        if (database == null) {
            throw new IllegalArgumentException("Database cannot be null");
        }
        this.database = database;
    }
    
    
    public Map<String, Integer> analyzeStudent(String username) throws SQLException {
    	
    	// VALIDATION: Check for null username
    	if (username == null) {
    	    throw new IllegalArgumentException("Username cannot be null");
    	}

    	// VALIDATION: Check for empty/blank username
    	if (username.trim().isEmpty()) {
    	    throw new IllegalArgumentException("Username cannot be empty");
    	}
        // Create result map that will be returned
        Map<String, Integer> result = new HashMap<>();
        
        // Use a Set to store unique post authors
        // Why Set? Because it automatically prevents duplicates
        // If student replies to Alice 5 times, Alice only appears once in the Set
        Set<String> uniqueAuthors = new HashSet<>();
        
        // Step 1: Get ALL replies created by this student
        List<Reply> allReplies = database.getAllReplies();
        
        // Step 2: Filter to only replies by this specific student
        for (Reply reply : allReplies) {
            // Check if this reply was created by our target student
            if (reply.getAuthor().equals(username)) {
                
                // Step 3: Get the post that this reply responded to
                // We need to know WHO created the post (the post author)
                int postID = reply.getPostID();
                Post post = database.getPost(postID);
                
                // Step 4: Safety check - make sure post exists
                // (It should always exist due to foreign key constraints, but defensive programming)
                if (post != null) {
                    String postAuthor = post.getAuthor();
                    
                    // Step 5: Add the post author to our Set
                    // If postAuthor is "alice", and we add "alice" 5 times, Set still only has 1 "alice"
                    uniqueAuthors.add(postAuthor);
                }
            }
        }
        
        // Step 6: CRITICAL - Remove self-replies
        // If student replied to their own posts, those don't count
        // Example: alice replied to alice's post → doesn't count toward requirement
        uniqueAuthors.remove(username);
        
        // Step 7: Count how many unique authors remain
        int uniqueCount = uniqueAuthors.size();
        
        // Step 8: Determine if requirement is met
        // Requirement: Must reply to at least 3 DIFFERENT students
        // Use 1 for TRUE, 0 for FALSE (easier for database storage and UI display)
        int requirementMet = (uniqueCount >= REQUIRED_UNIQUE_REPLIES) ? 1 : 0;
        
        // Step 9: Build the result map
        result.put("uniqueCount", uniqueCount);
        result.put("requirementMet", requirementMet);
        
        // Return the results for use by Grading Dashboard
        return result;
    }
    
    
     // Analyzes ALL students in the system and returns their statistics.
    public Map<String, Map<String, Integer>> analyzeAllStudents() throws SQLException {
        // This will store results for ALL students
        Map<String, Map<String, Integer>> allResults = new HashMap<>();
        
        // Get the list of all usernames from the database
        List<String> userList = database.getUserList();
        
        // Iterate through each user
        // Note: First item in list is "<Select a User>" so we skip it
        for (String username : userList) {
            // Skip the placeholder text
            if (!username.equals("<Select a User>")) {
                
                // Analyze this student and store their results
                Map<String, Integer> studentStats = analyzeStudent(username);
                allResults.put(username, studentStats);
            }
        }
        
        return allResults;
    }
    
    
     //Description: Returns the minimum number of unique students that must be replied to in order to meet the requirement.
     
    public static int getRequiredUniqueReplies() {
        return REQUIRED_UNIQUE_REPLIES;
    }
}