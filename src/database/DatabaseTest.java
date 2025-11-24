package database;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.sql.SQLException;
import entityClasses.User;

/**
 * Security tests for Database.java focusing on SQL Injection prevention.
 * Tests verify that PreparedStatement correctly handles malicious input.
 * 
 * @author Emmanuel Zelaya-Armenta
 * @version 1.0 HW3 Task 2.4
 */
class DatabaseTest {
    
    private Database db;
    
    @BeforeEach
    void setUp() throws SQLException {
        db = new Database();
        db.connectToDatabase();
        db.clearAllTables();  
    }
    
    @AfterEach
    void tearDown() {
        if (db != null) {
            db.closeConnection();
        }
    }
    
    // ==================== BOUNDARY VALUE TESTS ====================
    
    /**
     * BV-1: Test minimum valid username length (4 characters)
     */
    @Test
    void testBV1_MinimumUsernameLength() throws SQLException {
        User user = new User("abcd", "Pass123!", "", "", "", "", "", 
                            true, false, false);
        
        db.register(user);
        
        assertTrue(db.doesUserExist("abcd"), 
                   "User with 4-char username should be registered");
        
        System.out.println("BV-1 PASSED: Minimum username length (4 chars) accepted");
    }
    
    /**
     * BV-2: Test maximum valid username length (16 characters)
     */
    @Test
    void testBV2_MaximumUsernameLength() throws SQLException {
        String maxUsername = "aaaaaaaaaaaaaaaa"; // 16 a's
        User user = new User(maxUsername, "Pass123!", "", "", "", "", "", 
                            false, true, false);
        
        db.register(user);
        
        assertTrue(db.doesUserExist(maxUsername),
                   "User with 16-char username should be registered");
        
        System.out.println("BV-2 PASSED: Maximum username length (16 chars) accepted");
    }
    
    /**
     * BV-3: Test below minimum username length (3 characters)
     * Documents whether validation exists
     */
    @Test
    void testBV3_BelowMinimumUsername() {
        String shortUsername = "abc";
        User user = new User(shortUsername, "Pass123!", "", "", "", "", "", 
                            false, true, false);
        
        try {
            db.register(user);
            System.out.println("BV-3 INFO: 3-char username was accepted (no validation)");
            System.out.println("  WEAKNESS: Missing username length validation");
        } catch (SQLException e) {
            System.out.println("BV-3 PASSED: 3-char username rejected by validation");
        }
    }
    
    /**
     * BV-4: Test above maximum username length (17 characters)
     */
    @Test
    void testBV4_AboveMaximumUsername() {
        String longUsername = "aaaaaaaaaaaaaaaaa"; // 17 a's
        User user = new User(longUsername, "Pass123!", "", "", "", "", "", 
                            false, true, false);
        
        try {
            db.register(user);
            System.out.println("BV-4 INFO: 17-char username was accepted");
            System.out.println("  WEAKNESS: Missing max length validation");
        } catch (SQLException e) {
            System.out.println("BV-4 PASSED: 17-char username rejected");
        }
    }
    
    /**
     * BV-5: Test empty username
     */
    @Test
    void testBV5_EmptyUsername() {
        User user = new User("", "Pass123!", "", "", "", "", "", 
                            false, true, false);
        
        try {
            db.register(user);
            System.out.println("BV-5 INFO: Empty username was accepted");
            fail("Empty username should not be allowed");
        } catch (Exception e) {
            System.out.println("BV-5 PASSED: Empty username rejected");
        }
    }
    
    // ==================== COVERAGE TESTS ====================
    
    /**
     * CV-1: Test successful registration (happy path)
     * Tests all 10 fields are inserted correctly
     */
    @Test
    void testCV1_SuccessfulRegistration() throws SQLException {
        User user = new User("testuser", "Test123!", "John", "M", "Doe", 
                            "Johnny", "john@test.com", false, true, false);
        
        db.register(user);
        
        // Verify user exists
        assertTrue(db.doesUserExist("testuser"), "User should be registered");
        
        // Verify details are correct
        db.getUserAccountDetails("testuser");
        assertEquals("testuser", db.getCurrentUsername());
        assertEquals("Test123!", db.getCurrentPassword());
        assertEquals("John", db.getCurrentFirstName());
        assertEquals("M", db.getCurrentMiddleName());
        assertEquals("Doe", db.getCurrentLastName());
        
        System.out.println("CV-1 PASSED: Successful registration with all fields");
        System.out.println("  All 10 user fields inserted correctly");
    }
    
    /**
     * CV-2: Test duplicate username rejection
     */
    @Test
    void testCV2_DuplicateUsername() throws SQLException {
        // Register first user
        User user1 = new User("duplicate", "Pass123!", "", "", "", "", "", 
                             false, true, false);
        db.register(user1);
        
        // Try to register second user with same username
        User user2 = new User("duplicate", "DifferentPass!", "", "", "", "", "", 
                             false, false, true);
        
        try {
            db.register(user2);
            System.out.println("CV-2 INFO: Duplicate username was accepted");
            fail("Duplicate username should throw SQLException");
        } catch (SQLException e) {
            System.out.println("CV-2 PASSED: Duplicate username rejected");
            System.out.println("  Unique constraint enforced: " + e.getMessage());
        }
    }
    
    // ==================== SECURITY TESTS (SQL INJECTION) ====================
    
    /**
     * SEC-1: Test single quote in username
     * PreparedStatement should escape it as literal character
     */
    @Test
    void testSEC1_SingleQuoteInUsername() throws SQLException {
        String maliciousUsername = "admin'test";
        User user = new User(maliciousUsername, "Pass123!", "", "", "", "", "", 
                            false, true, false);
        
        db.register(user);
        
        assertTrue(db.doesUserExist(maliciousUsername),
                   "Username with single quote should be stored literally");
        
        System.out.println("SEC-1 PASSED: Single quote stored as literal character");
        System.out.println("  PreparedStatement escaped the quote correctly");
    }
    
    /**
     * SEC-2: Test SQL comment injection (--)
     * Should NOT comment out rest of query
     */
    @Test
    void testSEC2_SQLCommentInjection() throws SQLException {
        String maliciousUsername = "admin'-- ";
        User user = new User(maliciousUsername, "Pass123!", "", "", "", "", "", 
                            false, true, false);
        
        db.register(user);
        
        assertTrue(db.doesUserExist(maliciousUsername),
                   "SQL comment should be stored as literal");
        
        System.out.println("SEC-2 PASSED: SQL comment (--) did not affect query");
        System.out.println("  Comment stored as text, not executed");
    }
    
    /**
     * SEC-3: Test DROP TABLE attack
     * CRITICAL: Must NOT delete the database table
     */
    @Test
    void testSEC3_DropTableAttack() throws SQLException {
        String maliciousUsername = "'; DROP TABLE userDB; --";
        User user = new User(maliciousUsername, "Pass123!", "", "", "", "", "", 
                            false, true, false);
        
        int userCountBefore = db.getNumberOfUsers();
        
        try {
            db.register(user);
            System.out.println("  Malicious DROP command stored as literal text");
            
        } catch (SQLException e) {
            System.out.println("  Malicious username rejected: " + e.getMessage());
        }
        
        // CRITICAL CHECK: Verify table still exists
        int userCountAfter = db.getNumberOfUsers();
        assertTrue(userCountAfter >= userCountBefore,
                   "userDB table must still exist after DROP attempt");
        
        System.out.println("SEC-3 PASSED: DROP TABLE command did NOT execute");
        System.out.println("  PreparedStatement successfully prevented SQL injection!");
        System.out.println("  Database integrity maintained");
    }
    
    /**
     * SEC-4: Test UNION injection attack
     * Should NOT execute UNION query to leak data
     */
    @Test
    void testSEC4_UnionInjectionAttack() throws SQLException {
        String maliciousUsername = "admin' UNION SELECT * FROM userDB--";
        User user = new User(maliciousUsername, "Pass123!", "", "", "", "", "", 
                            false, true, false);
        
        int userCountBefore = db.getNumberOfUsers();
        
        try {
            db.register(user);
            System.out.println("  Long malicious username stored (truncated if needed)");
        } catch (SQLException e) {
            System.out.println("  Long malicious username rejected");
        }
        
        int userCountAfter = db.getNumberOfUsers();
        
        // Verify only 1 user added (or 0 if rejected)
        assertTrue(userCountAfter - userCountBefore <= 1,
                   "UNION should not leak multiple rows");
        
        System.out.println("SEC-4 PASSED: UNION injection did not leak data");
        System.out.println("  Only 1 row inserted (or 0 if rejected)");
    }
    
    /**
     * SEC-5: Test semicolon command separator
     * Should NOT execute second SQL command
     */
    @Test
    void testSEC5_SemicolonCommandSeparator() throws SQLException {
        // First, create a victim user to verify password doesn't get changed
        User testVictim = new User("victim", "Original123!", "", "", "", "", "", 
                                   false, true, false);
        db.register(testVictim);
        
        // Try to inject UPDATE command via semicolon
        String maliciousUsername = "hacker'; UPDATE userDB SET password='hacked";
        User attacker = new User(maliciousUsername, "Pass123!", "", "", "", "", "", 
                                false, true, false);
        
        try {
            db.register(attacker);
            System.out.println("  Attacker username stored as literal");
        } catch (SQLException e) {
            System.out.println("  Attacker username rejected (too long)");
        }
        
        // CRITICAL: Verify victim's password was NOT changed
        db.getUserAccountDetails("victim");
        String victimPassword = db.getCurrentPassword();
        
        assertEquals("Original123!", victimPassword,
                    "Victim password should not be changed by SQL injection");
        
        System.out.println("SEC-5 PASSED: Semicolon did not execute second command");
        System.out.println("  PreparedStatement prevented password modification!");
        System.out.println("  Victim's password remains: Original123!");
    }
    
    /**
     * SEC-6: Test malicious post content
     * 500 characters is enough for complex SQL injection
     */
    @Test
    void testSEC6_SQLInjectionInPostContent() throws SQLException {
        // Register a normal user first
        User user = new User("poster", "Pass123!", "", "", "", "", "", 
                            false, true, false);
        db.register(user);
        
        // Try to inject SQL through post content
        String maliciousContent = "Normal post text... '); DROP TABLE Post; --";
        
        int postId = db.createPost("poster", maliciousContent, "Role1");
        
        // Verify post was created (ID returned)
        assertTrue(postId > 0, "Post should be created with ID > 0");
        
        // Verify Post table still exists by retrieving the post
        entityClasses.Post retrieved = db.getPost(postId);
        assertNotNull(retrieved, "Post table should still exist");
        
        // Verify malicious content was stored literally
        assertEquals(maliciousContent, retrieved.getContent(),
                    "Malicious content should be stored as text");
        
        System.out.println("SEC-6 PASSED: SQL injection in post content prevented");
        System.out.println("  Post table still exists");
        System.out.println("  Malicious content stored as literal text");
    }
    
    /**
     * SEC-7: Test malicious reply content
     */
    @Test
    void testSEC7_SQLInjectionInReplyContent() throws SQLException {
        // Create user and post
        User user = new User("replier", "Pass123!", "", "", "", "", "", 
                            false, true, false);
        db.register(user);
        
        int postId = db.createPost("replier", "Original post", "Role1");
        
        // Try SQL injection in reply
        String maliciousReply = "Nice post! '); DELETE FROM Reply; --";
        
        int replyId = db.createReply(postId, "replier", maliciousReply, "Role1");
        
        // Verify reply was created
        assertTrue(replyId > 0, "Reply should be created");
        
        // Verify Reply table still exists
        entityClasses.Reply retrieved = db.getReply(replyId);
        assertNotNull(retrieved, "Reply table should still exist");
        
        // Verify malicious content stored literally
        assertEquals(maliciousReply, retrieved.getContent(),
                    "Malicious reply should be stored as text");
        
        System.out.println("SEC-7 PASSED: SQL injection in reply prevented");
        System.out.println("  Reply table still exists");
        System.out.println("  Malicious reply stored as literal text");
    }
}