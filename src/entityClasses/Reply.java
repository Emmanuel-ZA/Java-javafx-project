package entityClasses;

public class Reply {
    private int id;
    private int postID;
    private String author;
    private String content;
    private String authorRole;

    public Reply(int id, int postID, String author, String content, String authorRole) {
        this.id = id;
        this.postID = postID;
        this.author = author;
        this.content = content;
        this.authorRole = authorRole;
    }

    public int getReplyID() {  
        return id;
    }
    
    public int getPostID() { 
        return postID;
    }
    
    public String getAuthor() {  
        return author;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getAuthorRole() {
        return authorRole;
    }
    
    // Setters
    public void setReplyId(int id) {
        this.id = id;
    }
    
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "id: " + id + " author: " + author + " content: " + content;
    }
}