package entityClasses;

import java.util.ArrayList;
import java.util.List;

public class PostCollection {
    
    private List<Post> posts;
    
    public PostCollection() {
        this.posts = new ArrayList<>();
    }

    public PostCollection(List<Post> posts) {
        this.posts = new ArrayList<>(posts);
    }
    
    public void addPost(Post post) {
        posts.add(post);
    }
    
    public boolean removePost(Post post) {
        return posts.remove(post);
    }
    
    public boolean removePostById(int postId) {
        return posts.removeIf(p -> p.getPostID() == postId);
    }
    
    public List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }
    
    public Post getPostById(int postId) {
        for (Post post : posts) {
            if (post.getPostID() == postId) {
                return post;
            }
        }
        return null;
    }
    
    public int size() {
        return posts.size();
    }
    
    public boolean isEmpty() {
        return posts.isEmpty();
    }
    
    public void clear() {
        posts.clear();
    }
    
    public String toString() {
        return "PostCollection [" + posts.size() + " posts]";
    }
}