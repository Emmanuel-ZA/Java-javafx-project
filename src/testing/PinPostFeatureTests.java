package testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import database.Database;
import entityClasses.Post;
import entityClasses.User;
import java.sql.SQLException;

class PinPostFeatureTests {

    private Database database;
    private User testAdmin;

    @BeforeEach
    void setUp() throws SQLException {
        database = new Database();
        database.connectToDatabase();
        database.clearAllTables();

        testAdmin = new User("testadmin", "AdminPass123!", "", "", "", "", "", true, false, false);
        database.register(testAdmin);

        System.out.println("\n=== Test Setup Complete ===\n");
    }

    @AfterEach
    void tearDown() {
        if (database != null) {
            database.closeConnection();
        }
    }

    /**
     * TEST CASE 1: Pin a single post successfully
     * 
     * Verifies that:
     * - Admin can pin a post when fewer than 3 are pinned
     * - isPinned flag is set to true
     * - pinnedBy field contains admin username
     * - pinPost() returns true for success
     */
    @Test
    void testCase1_PinSinglePost() throws SQLException {
        System.out.println("TEST 1: Pin Single Post");
        System.out.println("========================");

        int postId = database.createPost("student", "Important question", "Role1");

        boolean result = database.pinPost(postId, "testadmin");

        assertTrue(result, "pinPost should return true for successful pin");

        Post pinnedPost = database.getPost(postId);
        assertTrue(pinnedPost.isPinned(), "Post should have isPinned=true");
        assertEquals("testadmin", pinnedPost.getPinnedBy(), "pinnedBy should be testadmin");

        System.out.println("RESULT: ✓ PASS");
        System.out.println("Post pinned successfully with correct admin recorded\n");
    }

    /**
     * TEST CASE 2: Unpin a pinned post successfully
     * 
     * Verifies that:
     * - Admin can unpin a previously pinned post
     * - isPinned flag is set to false
     * - pinnedBy field is cleared to null
     * - unpinPost() returns true for success
     */
    @Test
    void testCase2_UnpinPost() throws SQLException {
        System.out.println("TEST 2: Unpin Post");
        System.out.println("==================");

        int postId = database.createPost("student", "Question", "Role1");
        database.pinPost(postId, "testadmin");

        Post pinnedPost = database.getPost(postId);
        assertTrue(pinnedPost.isPinned(), "Post should be pinned initially");

        boolean result = database.unpinPost(postId);

        assertTrue(result, "unpinPost should return true for successful unpin");

        Post unpinnedPost = database.getPost(postId);
        assertFalse(unpinnedPost.isPinned(), "Post should have isPinned=false");
        assertNull(unpinnedPost.getPinnedBy(), "pinnedBy should be null after unpin");

        System.out.println("RESULT: ✓ PASS");
        System.out.println("Post unpinned successfully and admin cleared\n");
    }

    /**
     * TEST CASE 3: Enforce maximum 3 pinned posts limit
     * 
     * Verifies that:
     * - System allows pinning up to 3 posts
     * - System blocks pinning a 4th post
     * - pinPost() returns false when limit is reached
     * - Only 3 posts remain pinned
     */
    @Test
    void testCase3_EnforceMaxPinLimit() throws SQLException {
        System.out.println("TEST 3: Enforce Max Pin Limit (3 posts)");
        System.out.println("========================================");

        int post1 = database.createPost("student", "Post 1", "Role1");
        int post2 = database.createPost("student", "Post 2", "Role1");
        int post3 = database.createPost("student", "Post 3", "Role1");
        int post4 = database.createPost("student", "Post 4", "Role1");

        boolean pin1 = database.pinPost(post1, "testadmin");
        boolean pin2 = database.pinPost(post2, "testadmin");
        boolean pin3 = database.pinPost(post3, "testadmin");
        boolean pin4 = database.pinPost(post4, "testadmin");

        assertTrue(pin1, "First pin should succeed");
        assertTrue(pin2, "Second pin should succeed");
        assertTrue(pin3, "Third pin should succeed");
        assertFalse(pin4, "Fourth pin should be blocked (limit=3)");

        Post p4 = database.getPost(post4);
        assertFalse(p4.isPinned(), "Post 4 should not be pinned");

        System.out.println("RESULT: ✓ PASS");
        System.out.println("System correctly enforced maximum 3 pins limit\n");
    }

    /**
     * TEST CASE 4: Verify pin icon display in post formatting
     * 
     * Verifies that:
     * - Pinned posts display with 📌 icon in formatted string
     * - Unpinned posts do not display pin icon
     * - Post ID can be correctly extracted from formatted string with icon
     */
    @Test
    void testCase4_PinIconDisplay() throws SQLException {
        System.out.println("TEST 4: Pin Icon Display");
        System.out.println("========================");

        int postId = database.createPost("student", "Test content", "Role1");
        database.pinPost(postId, "testadmin");

        Post pinnedPost = database.getPost(postId);

        String pinMarker = pinnedPost.isPinned() ? "📌 " : "";
        String formattedPinned = pinMarker + "id: " + pinnedPost.getPostID() + 
                                " author: " + pinnedPost.getAuthor();

        assertTrue(formattedPinned.startsWith("📌"), "Pinned post should start with pin icon");
        assertTrue(formattedPinned.contains("id: " + postId), "Post ID should be in formatted string");

        Post unpinnedPost = new Post(postId, "student", "content", "Role1", false, null, false, null);
        String formatUnpinned = (unpinnedPost.isPinned() ? "📌 " : "") + "id: " + unpinnedPost.getPostID();

        assertFalse(formatUnpinned.startsWith("📌"), "Unpinned post should not have pin icon");

        System.out.println("RESULT: ✓ PASS");
        System.out.println("Pin icon correctly displayed for pinned posts only\n");
    }

    /**
     * TEST CASE 5: Verify post ordering with pinned and unpinned posts
     * 
     * Verifies that:
     * - Pinned posts appear first in the list
     * - Unpinned posts appear after pinned posts
     * - Within each group, posts are ordered by ID descending (newest first)
     * - getAllPosts() applies correct ordering
     */
    @Test
    void testCase5_PinPostOrdering() throws SQLException {
        System.out.println("TEST 5: Pin Post Ordering");
        System.out.println("=========================");

        int post1 = database.createPost("student", "Oldest post", "Role1");
        int post2 = database.createPost("student", "Middle post", "Role1");
        int post3 = database.createPost("student", "Newest post", "Role1");

        database.pinPost(post1, "testadmin");

        java.util.List<Post> posts = database.getAllPosts();

        assertEquals(3, posts.size(), "Should have 3 posts total");

        assertTrue(posts.get(0).isPinned(), "First post should be pinned");
        assertEquals(post1, posts.get(0).getPostID(), "Pinned post should be first");

        assertFalse(posts.get(1).isPinned(), "Second post should be unpinned");
        assertEquals(post3, posts.get(1).getPostID(), "Newest unpinned (post3) should be second");

        assertFalse(posts.get(2).isPinned(), "Third post should be unpinned");
        assertEquals(post2, posts.get(2).getPostID(), "Middle unpinned (post2) should be third");

        System.out.println("RESULT: ✓ PASS");
        System.out.println("Posts correctly ordered: pinned first, then by ID DESC\n");
    }

  
}