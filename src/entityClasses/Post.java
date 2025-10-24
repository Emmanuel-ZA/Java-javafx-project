package entityClasses;

public class Post {
    private int id;
    private String author;
    private String content;

    public Post(int id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public int getPostID() {  
        return this.id;
    }
    
    public String getAuthor() {  
        return this.author;
    }
    
    public String getContent() {
        return this.content;
    }

    // Setters
    public void setPostId(int id) {
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