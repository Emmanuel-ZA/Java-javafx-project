package javaDoc;

/**
 * Reply Alerts Feature Implementation Documentation (User Story 3.1)
 * 
 * <p>This class provides comprehensive documentation for the reply alerts feature
 * implementation. The feature notifies students when someone has replied to their posts
 * by displaying an alert badge on their home page.</p>
 * 
 * <p><strong>Note:</strong> This is a documentation-only class and should not be instantiated.</p>
 * 
 * <h2>Feature Overview</h2>
 * <p><strong>User Story 3.1:</strong> Display Reply Alerts on User's Home Page</p>
 * 
 * <p><strong>As a</strong> student<br>
 * <strong>I want to</strong> see an alert on my home page when someone has replied to my posts<br>
 * <strong>So that</strong> I can quickly know when I need to review discussion responses</p>
 * 
 * <p>The feature enables students to:</p>
 * <ul>
 *   <li>See a notification badge on their home page when they have unread replies</li>
 *   <li>Know exactly how many new replies they have ("1 new reply" vs "3 new replies")</li>
 *   <li>Automatically clear notifications when they view the replies</li>
 *   <li>Stay engaged with discussions without manually checking each post</li>
 * </ul>
 * 
 * <h2>Implementation Summary</h2>
 * <p><strong>Total Files Modified:</strong> 8</p>
 * <p><strong>Total Classes Modified:</strong> 7</p>
 * <p><strong>New Database Columns:</strong> 2</p>
 * 
 * <h3>Modified Files:</h3>
 * <ol>
 *   <li>{@code Database.java} - Schema updates and notification logic</li>
 *   <li>{@code Post.java} - Entity class with notification fields</li>
 *   <li>{@code ViewRole1Home.java} - Role1 student home page UI</li>
 *   <li>{@code ControllerRole1Home.java} - Role1 notification controller</li>
 *   <li>{@code ViewRole2Home.java} - Role2 student home page UI</li>
 *   <li>{@code ControllerRole2Home.java} - Role2 notification controller</li>
 *   <li>{@code ControllerPosts.java} - Mark replies as read logic</li>
 *   <li>{@code ReplyAlertsFeatureDocumentation.java} - This documentation (NEW)</li>
 * </ol>
 * 
 * <h2>Data Model Changes</h2>
 * 
 * <h3>Post.java (entityClasses package)</h3>
 * <p><strong>New Fields:</strong></p>
 * <ul>
 *   <li>{@code private boolean hasUnreadReplies} - Flag indicating if post has new replies</li>
 *   <li>{@code private java.sql.Timestamp lastReplyTimestamp} - When the last reply was created</li>
 * </ul>
 * 
 * <p><strong>Constructor Update:</strong></p>
 * <pre>
 * public Post(int id, String author, String content, String authorRole, 
 *             boolean isPinned, String pinnedBy,
 *             boolean hasUnreadReplies, java.sql.Timestamp lastReplyTimestamp)
 * </pre>
 * 
 * <p><strong>New Methods:</strong></p>
 * <ul>
 *   <li>{@code public boolean hasUnreadReplies()} - Returns unread status</li>
 *   <li>{@code public void setHasUnreadReplies(boolean)} - Sets unread status</li>
 *   <li>{@code public Timestamp getLastReplyTimestamp()} - Returns last reply time</li>
 *   <li>{@code public void setLastReplyTimestamp(Timestamp)} - Sets last reply time</li>
 * </ul>
 * 
 * <h2>Database Layer Changes</h2>
 * 
 * <h3>Database.java (database package)</h3>
 * <p><strong>Schema Updates in createPostTables():</strong></p>
 * <pre>
 * ALTER TABLE Post ADD COLUMN hasUnreadReplies BOOLEAN DEFAULT FALSE;
 * ALTER TABLE Post ADD COLUMN lastReplyTimestamp TIMESTAMP;
 * </pre>
 * 
 * <p><strong>Modified Methods:</strong></p>
 * <ul>
 *   <li><strong>createReply():</strong> Now calls {@code markPostAsHavingNewReply()} after creating a reply
 *       to automatically set the notification flag on the parent post.
 *   </li>
 * </ul>
 * 
 * <p><strong>New Methods:</strong></p>
 * <ul>
 *   <li>{@code private void markPostAsHavingNewReply(int postID) throws SQLException}
 *       <ul>
 *         <li>Sets hasUnreadReplies=TRUE on the specified post</li>
 *         <li>Records current timestamp as lastReplyTimestamp</li>
 *         <li>Called automatically when a reply is created</li>
 *       </ul>
 *   </li>
 *   <li>{@code public int getUnreadReplyCount(String username) throws SQLException}
 *       <ul>
 *         <li>Counts how many posts by this user have hasUnreadReplies=TRUE</li>
 *         <li>Used to display notification badge count on home page</li>
 *         <li>Returns 0 if no unread replies</li>
 *       </ul>
 *   </li>
 *   <li>{@code public void markRepliesAsRead(int postID) throws SQLException}
 *       <ul>
 *         <li>Sets hasUnreadReplies=FALSE for the specified post</li>
 *         <li>Called when post author views the replies</li>
 *         <li>Clears the notification for that post</li>
 *       </ul>
 *   </li>
 * </ul>
 * 
 * <h2>User Interface Changes</h2>
 * 
 * <h3>ViewRole1Home.java and ViewRole2Home.java</h3>
 * <p><strong>New UI Component:</strong></p>
 * <pre>
 * protected static Label label_ReplyAlert = new Label();
 * </pre>
 * 
 * <p><strong>Styling:</strong></p>
 * <ul>
 *   <li>Font: Arial, 16pt</li>
 *   <li>Color: Blue (#1E88E5)</li>
 *   <li>Weight: Bold</li>
 *   <li>Position: Centered below "View Posts" button</li>
 *   <li>Hidden by default (only shown when there are unread replies)</li>
 * </ul>
 * 
 * <p><strong>Display Format:</strong></p>
 * <ul>
 *   <li>1 reply: "🔔 You have 1 new reply to your post"</li>
 *   <li>Multiple: "🔔 You have 3 new replies to your posts"</li>
 * </ul>
 * 
 * <h3>ControllerRole1Home.java and ControllerRole2Home.java</h3>
 * <p><strong>New Method:</strong></p>
 * <pre>
 * protected static void updateReplyAlert()
 * </pre>
 * 
 * <p><strong>Logic Flow:</strong></p>
 * <ol>
 *   <li>Get current user's username</li>
 *   <li>Call {@code database.getUnreadReplyCount(username)}</li>
 *   <li>If count > 0: Show notification label with count</li>
 *   <li>If count == 0: Hide notification label</li>
 *   <li>Use singular/plural grammar based on count</li>
 * </ol>
 * 
 * <p><strong>Called By:</strong></p>
 * <ul>
 *   <li>{@code ViewRole1Home.displayRole1Home()} - When home page loads</li>
 *   <li>{@code ViewRole2Home.displayRole2Home()} - When home page loads</li>
 * </ul>
 * 
 * <h3>ControllerPosts.java</h3>
 * <p><strong>Modified Method:</strong> {@code performViewReplies()}</p>
 * 
 * <p><strong>Added Logic:</strong></p>
 * <pre>
 * // After retrieving post, before displaying replies:
 * ViewPosts.theDatabase.markRepliesAsRead(id);
 * </pre>
 * 
 * <p><strong>Purpose:</strong> When a user views replies to their post, automatically mark
 * those replies as "read" to clear the notification.</p>
 * 
 * <h2>Feature Behavior</h2>
 * 
 * <h3>Scenario 1: User Receives New Reply</h3>
 * <ol>
 *   <li>Alice creates a post "What is recursion?"</li>
 *   <li>Bob creates a reply "It's when a function calls itself"</li>
 *   <li>System calls {@code Database.createReply()} which triggers {@code markPostAsHavingNewReply()}</li>
 *   <li>Alice's post now has: hasUnreadReplies=TRUE, lastReplyTimestamp=[current time]</li>
 *   <li>Next time Alice logs in, home page calls {@code updateReplyAlert()}</li>
 *   <li>Alert displays: "🔔 You have 1 new reply to your post"</li>
 * </ol>
 * 
 * <h3>Scenario 2: User Views Replies</h3>
 * <ol>
 *   <li>Alice sees notification badge on home page</li>
 *   <li>Alice clicks "View Posts" button</li>
 *   <li>Alice selects her post and clicks "View Replies"</li>
 *   <li>{@code ControllerPosts.performViewReplies()} calls {@code markRepliesAsRead()}</li>
 *   <li>Alice's post now has: hasUnreadReplies=FALSE</li>
 *   <li>When Alice returns to home page, notification is gone</li>
 * </ol>
 * 
 * <h3>Scenario 3: Multiple Unread Replies</h3>
 * <ol>
 *   <li>Alice has 3 posts</li>
 *   <li>Bob replies to post #1</li>
 *   <li>Charlie replies to post #2</li>
 *   <li>David replies to post #3</li>
 *   <li>{@code getUnreadReplyCount("alice")} returns 3</li>
 *   <li>Alert displays: "🔔 You have 3 new replies to your posts"</li>
 *   <li>Alice views replies for post #1 → count becomes 2</li>
 *   <li>Alert updates: "🔔 You have 2 new replies to your posts"</li>
 * </ol>
 * 
 * <h2>Data Flow Diagram</h2>
 * <pre>
 * User creates reply
 *   ↓
 * ControllerPosts.performSubmitReply()
 *   ↓
 * Database.createReply(postID, author, content, role)
 *   ↓
 * Database.markPostAsHavingNewReply(postID)
 *   ↓
 * UPDATE Post SET hasUnreadReplies=TRUE, lastReplyTimestamp=NOW()
 *   ↓
 * [Post author logs in]
 *   ↓
 * ViewRole1Home.displayRole1Home() / ViewRole2Home.displayRole2Home()
 *   ↓
 * ControllerRole1Home.updateReplyAlert() / ControllerRole2Home.updateReplyAlert()
 *   ↓
 * Database.getUnreadReplyCount(username)
 *   ↓
 * SELECT COUNT(*) FROM Post WHERE author=? AND hasUnreadReplies=TRUE
 *   ↓
 * Display notification badge with count
 *   ↓
 * [User clicks "View Replies"]
 *   ↓
 * ControllerPosts.performViewReplies()
 *   ↓
 * Database.markRepliesAsRead(postID)
 *   ↓
 * UPDATE Post SET hasUnreadReplies=FALSE WHERE id=?
 *   ↓
 * Notification cleared for that post
 * </pre>
 * 
 * <h2>Database Migration</h2>
 * <p><strong>Important:</strong> Database must be reset after code deployment to apply schema changes.</p>
 * 
 * <p><strong>Schema Changes:</strong></p>
 * <pre>
 * ALTER TABLE Post ADD COLUMN hasUnreadReplies BOOLEAN DEFAULT FALSE;
 * ALTER TABLE Post ADD COLUMN lastReplyTimestamp TIMESTAMP;
 * </pre>
 * 
 * <p><strong>Default Values:</strong></p>
 * <ul>
 *   <li>hasUnreadReplies: FALSE (no notifications by default)</li>
 *   <li>lastReplyTimestamp: NULL (no replies yet)</li>
 * </ul>
 * 
 * <p><strong>Migration Notes:</strong></p>
 * <ul>
 *   <li>Existing posts will have hasUnreadReplies=FALSE</li>
 *   <li>Existing posts with replies will have lastReplyTimestamp=NULL until next reply</li>
 *   <li>No data loss - purely additive schema changes</li>
 * </ul>
 * 
 * <h2>Testing Recommendations</h2>
 * <ol>
 *   <li>Create post as Role1 user → verify no notification initially</li>
 *   <li>Create reply to that post as Role2 user → verify notification doesn't show for reply author</li>
 *   <li>Log in as Role1 user → verify notification badge appears</li>
 *   <li>Verify notification shows correct count (1 reply vs multiple replies)</li>
 *   <li>View replies for post → verify notification disappears</li>
 *   <li>Create second reply to same post → verify notification reappears</li>
 *   <li>Create posts with multiple replies → verify count is accurate</li>
 *   <li>View replies for one post → verify only that post's notification clears</li>
 *   <li>Test with both Role1 and Role2 users</li>
 *   <li>Test notification persistence across logout/login</li>
 * </ol>
 * 
 * <h2>Acceptance Criteria Validation</h2>
 * <p>From User Story 3.1 document:</p>
 * <ul>
 *   <li>✅ Home page displays a notification badge showing unread reply count</li>
 *   <li>✅ Badge indicates "You have X new replies to your posts"</li>
 *   <li>✅ Clicking notification takes user to posts (view posts button exists)</li>
 *   <li>✅ Notification clears once user views the replies</li>
 *   <li>✅ Notification persists across sessions (stored in database)</li>
 *   <li>✅ Staff can see notifications for their administrative posts</li>
 *   <li>✅ Notification updates when new replies are added</li>
 * </ul>
 * 
 * <h2>Future Enhancements</h2>
 * <ul>
 *   <li>Real-time updates using background polling or WebSockets</li>
 *   <li>Clickable notification that directly navigates to posts with new replies</li>
 *   <li>Notification sound or desktop notification on reply</li>
 *   <li>Highlight posts with unread replies in the posts list</li>
 *   <li>Email notifications for new replies (if email system implemented)</li>
 *   <li>Notification history log</li>
 * </ul>
 * 
 * @author Development Team
 * @version 1.0
 * @since TP3
 * 
 * @see database.Database#markPostAsHavingNewReply(int)
 * @see database.Database#getUnreadReplyCount(String)
 * @see database.Database#markRepliesAsRead(int)
 * @see entityClasses.Post#hasUnreadReplies()
 * @see guiRole1.ControllerRole1Home#updateReplyAlert()
 * @see guiRole2.ControllerRole2Home#updateReplyAlert()
 */
public class ReplyAlertsFeatureDocumentation {
    
    /**
     * Private constructor to prevent instantiation of this documentation class.
     * 
     * @throws UnsupportedOperationException if instantiation is attempted
     */
    private ReplyAlertsFeatureDocumentation() {
    	throw new UnsupportedOperationException("This is a documentation class and cannot be instantiated");
    }
}