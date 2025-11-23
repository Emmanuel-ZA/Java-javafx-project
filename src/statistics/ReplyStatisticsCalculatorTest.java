package statistics;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import database.Database;
import entityClasses.User;
import java.sql.SQLException;
import java.util.Map;


class ReplyStatisticsCalculatorTest {
    
    private static Database database;
    private ReplyStatisticsCalculator calculator;
    
    /**
     * Set up test database once before all tests.
     * Creates users, posts, and replies with specific patterns.
     */
    @BeforeAll
    static void setUpDatabase() throws SQLException {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Setting up test database");
        System.out.println("═══════════════════════════════════════════════════════");
        
        database = new Database();
        database.connectToDatabase();
        database.clearAllTables();
        
        // Create test users
        createTestUser("alice");
        createTestUser("bob");
        createTestUser("charlie");
        createTestUser("david");
        createTestUser("emma");
        createTestUser("frank");
        createTestUser("lurker");
        createTestUser("narcissist");
        createTestUser("mixed");
        
        // Create posts
        int post_alice = database.createPost("alice", "Alice's question", "Role1");
        int post_bob = database.createPost("bob", "Bob's question", "Role1");
        int post_charlie = database.createPost("charlie", "Charlie's question", "Role1");
        int post_david = database.createPost("david", "David's question", "Role1");
        int post_emma = database.createPost("emma", "Emma's question", "Role1");
        int post_frank = database.createPost("frank", "Frank's question", "Role1");
        int post_narcissist = database.createPost("narcissist", "Narcissist's question", "Role1");
        int post_mixed1 = database.createPost("mixed", "Mixed's first question", "Role1");
        int post_mixed2 = database.createPost("mixed", "Mixed's second question", "Role1");
        
        // TEST CASE 1 data: alice replies to exactly 3 different students
        database.createReply(post_bob, "alice", "Alice replies to Bob", "Role1");
        database.createReply(post_charlie, "alice", "Alice replies to Charlie", "Role1");
        database.createReply(post_david, "alice", "Alice replies to David", "Role1");
        
        // TEST CASE 2 data: bob replies to 2 unique (with duplicates)
        database.createReply(post_alice, "bob", "Bob's first reply to Alice", "Role1");
        database.createReply(post_alice, "bob", "Bob's second reply to Alice", "Role1");
        database.createReply(post_alice, "bob", "Bob's third reply to Alice", "Role1");
        database.createReply(post_charlie, "bob", "Bob's first reply to Charlie", "Role1");
        database.createReply(post_charlie, "bob", "Bob's second reply to Charlie", "Role1");
        
        // TEST CASE 3 data: charlie replies to 5 different students
        database.createReply(post_alice, "charlie", "Charlie replies to Alice", "Role1");
        database.createReply(post_bob, "charlie", "Charlie replies to Bob", "Role1");
        database.createReply(post_david, "charlie", "Charlie replies to David", "Role1");
        database.createReply(post_emma, "charlie", "Charlie replies to Emma", "Role1");
        database.createReply(post_frank, "charlie", "Charlie replies to Frank", "Role1");
        
        // TEST CASE 4 data: lurker has zero replies (no replies created)
        
        // TEST CASE 5 data: narcissist only replies to own posts
        database.createReply(post_narcissist, "narcissist", "First self-reply", "Role1");
        database.createReply(post_narcissist, "narcissist", "Second self-reply", "Role1");
        database.createReply(post_narcissist, "narcissist", "Third self-reply", "Role1");
        
        // TEST CASE 6 data: mixed has self-replies and replies to others
        database.createReply(post_mixed1, "mixed", "Mixed replies to self 1", "Role1");
        database.createReply(post_mixed2, "mixed", "Mixed replies to self 2", "Role1");
        database.createReply(post_alice, "mixed", "Mixed replies to Alice", "Role1");
        database.createReply(post_bob, "mixed", "Mixed replies to Bob", "Role1");
        database.createReply(post_charlie, "mixed", "Mixed replies to Charlie", "Role1");
        
        System.out.println("Test database setup complete\n");
    }
    
    /**
     * Helper method to register a test user.
     */
    private static void createTestUser(String username) throws SQLException {
        User user = new User(username, "Pass123!", "", "", "", "", "", false, true, false);
        database.register(user);
    }
    
    @BeforeEach
    void setUp() {
        calculator = new ReplyStatisticsCalculator(database);
    }
    
    @AfterEach
    void tearDown() {
        calculator = null;
    }
    
    // ==================== TEST CASES FROM TESTING DETAILS PDF ====================
    
    /**
     * TEST CASE 1: Student Replied to Exactly 3 Different Students
     * Requirement: REQ-1, REQ-3
     * Type: POSITIVE TEST - Boundary value (exact minimum)
     */
    @Test
    void testCase1_ExactlyThreeUniqueStudents() throws SQLException {
        Map<String, Integer> result = calculator.analyzeStudent("alice");
        
        int uniqueCount = result.get("uniqueCount");
        int requirementMet = result.get("requirementMet");
        
        assertEquals(3, uniqueCount, "Alice should have replied to 3 unique students");
        assertEquals(1, requirementMet, "Alice should meet requirement (3 >= 3)");
        
        System.out.println("TEST 1 PASSED: Exactly 3 different students");
        System.out.println("  uniqueCount=3, requirementMet=1");
    }
    
    /**
     * TEST CASE 2: Student Replied to Only 2 Different Students
     * Requirement: REQ-1, REQ-3
     * Type: NEGATIVE TEST - Tests duplicate removal
     */
    @Test
    void testCase2_OnlyTwoUniqueStudents() throws SQLException {
        Map<String, Integer> result = calculator.analyzeStudent("bob");
        
        int uniqueCount = result.get("uniqueCount");
        int requirementMet = result.get("requirementMet");
        
        assertEquals(2, uniqueCount, "Bob replied 5 times but only to 2 unique students");
        assertEquals(0, requirementMet, "Bob should NOT meet requirement (2 < 3)");
        
        System.out.println("TEST 2 PASSED: Only 2 different students (duplicates removed)");
        System.out.println("  uniqueCount=2, requirementMet=0");
        System.out.println("  HashSet correctly removed duplicates");
    }
    
    /**
     * TEST CASE 3: Student Replied to 5 Different Students
     * Requirement: REQ-1, REQ-3
     * Type: POSITIVE TEST - Exceeds requirement
     */
    @Test
    void testCase3_FiveUniqueStudents() throws SQLException {
        Map<String, Integer> result = calculator.analyzeStudent("charlie");
        
        int uniqueCount = result.get("uniqueCount");
        int requirementMet = result.get("requirementMet");
        
        assertEquals(5, uniqueCount, "Charlie should have replied to 5 unique students");
        assertEquals(1, requirementMet, "Charlie should meet requirement (5 > 3)");
        
        System.out.println("TEST 3 PASSED: Five different students (exceeds requirement)");
        System.out.println("  uniqueCount=5, requirementMet=1");
    }
    
    /**
     * TEST CASE 4: Student Has Zero Replies
     * Requirement: REQ-1, REQ-3, REQ-4
     * Type: BOUNDARY VALUE TEST - Minimum case (zero)
     */
    @Test
    void testCase4_ZeroReplies() throws SQLException {
        Map<String, Integer> result = calculator.analyzeStudent("lurker");
        
        int uniqueCount = result.get("uniqueCount");
        int requirementMet = result.get("requirementMet");
        
        assertEquals(0, uniqueCount, "Lurker has no replies");
        assertEquals(0, requirementMet, "Lurker should NOT meet requirement (0 < 3)");
        
        System.out.println("TEST 4 PASSED: Zero replies (boundary case)");
        System.out.println("  uniqueCount=0, requirementMet=0");
        System.out.println("  System handles empty case without crashing");
    }
    
    
}

