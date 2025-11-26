package guiPostsAdmin;

import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: ModelPostsAdmin Class </p>
 * 
 * <p> Description: Model class for the Admin Posts view. This class contains utility methods
 * for formatting and extracting data from Post and Reply objects for display in the GUI.</p>
 * 
 * <p>Why a separate Admin model?
 * - Admins see ALL posts/replies, not just their own
 * - Display format includes role badges for better visibility
 * - Keeps admin-specific logic separate from regular user logic
 * - Makes code easier to maintain and modify
 * </p>
 * 
 * <p>This is a utility class with only static methods - no need to create instances.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Emmanuel Zelaya-Armenta, Lynn Robert Carter
 * @version 1.00 2025-10-26 Initial creation for admin post management
 */
public class ModelPostsAdmin {
    
    /*******
     * <p> Method: getID </p>
     * 
     * <p> Description: Extracts the ID number from a formatted post or reply string.
     * This is a parsing method that pulls out the numeric ID from display text.</p>
     * 
     * <p>How it works:
     * 1. Input looks like: "id: 5 author: john [Admin] content: Hello world"
     * 2. Split by " author: " → ["id: 5", "john [Admin] content: Hello world"]
     * 3. Take first part: "id: 5"
     * 4. Split by space → ["id:", "5"]
     * 5. Take second part and convert to integer: 5
     * </p>
     * 
     * <p>Why do we need this?
     * - ListView stores display strings, not Post objects
     * - When user selects a post to edit/delete, we need its ID
     * - This extracts the ID from the display string
     * </p>
     * 
     * @param s the formatted string containing "id: X author: Y content: Z"
     * 
     * @return the extracted ID as an integer
     */
	protected static int getID(String s) {
	    if (s == null) {
	        throw new NumberFormatException("Post string is null");
	    }
	    String[] stuff1 = s.split(" author: ");
	    if (stuff1.length == 0) {
	        throw new NumberFormatException("Unable to split on ' author: ' in: " + s);
	    }
	    String temp = stuff1[0].trim();
	    String[] tokens = temp.split("\\s+");
	    for (String token : tokens) {
	        String cleaned = token.replace(":", "").trim();
	        if (cleaned.matches("\\d+")) {
	            return Integer.parseInt(cleaned);
	        }
	    }
	    throw new NumberFormatException("Unable to extract numeric ID from: " + s);
	}
    
    /*******
     * <p> Method: formatPostForDisplay </p>
     * 
     * <p> Description: Formats a Post object into a human-readable string for display
     * in the GUI ListView. Includes role information in brackets for easy identification.</p>
     * 
     * <p>Display format: "id: X author: username [RoleName] content: post text"</p>
     * 
     * <p>Why format posts?
     * - JavaFX ListView displays strings, not objects
     * - Users need to see all relevant information at a glance
     * - Role badges help identify who posted what
     * - Consistent formatting makes the interface easier to scan
     * </p>
     * 
     * <p>The null check for role prevents crashes if old posts don't have role data.</p>
     * 
     * @param post the Post object to format
     * 
     * @return a formatted String ready for display
     */
	protected static String formatPostForDisplay(Post post) {
	    String role = post.getAuthorRole() != null ? post.getAuthorRole() : "Unknown";
	    String pinMarker = post.isPinned() ? "📌 " : "";
	    return pinMarker +
	           "id: " + post.getPostID() +
	           " author: " + post.getAuthor() +
	           " [" + role + "] " +
	           "content: " + post.getContent();
	}
    
    /*******
     * <p> Method: formatReplyForDisplay </p>
     * 
     * <p> Description: Formats a Reply object into a human-readable string for display.
     * Similar to formatPostForDisplay but for replies.</p>
     * 
     * <p>Display format: "id: X author: username [RoleName] content: reply text"</p>
     * 
     * <p>Note: We don't include postID in the display string because when viewing replies,
     * we're already in the context of a specific post, so showing postID would be redundant.</p>
     * 
     * @param reply the Reply object to format
     * 
     * @return a formatted String ready for display
     */
    protected static String formatReplyForDisplay(Reply reply) {
        // Get the role, defaulting to "Unknown" if null
        String role = reply.getAuthorRole() != null ? reply.getAuthorRole() : "Unknown";
        
        // Build and return the formatted string
        // Same format as posts but uses Reply data
        return "id: " + reply.getReplyID() + 
               " author: " + reply.getAuthor() + 
               " [" + role + "] " +
               "content: " + reply.getContent();
    }
}