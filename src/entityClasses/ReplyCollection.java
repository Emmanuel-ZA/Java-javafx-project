package entityClasses;

import java.util.ArrayList;
import java.util.List;

public class ReplyCollection {
    
    private List<Reply> replies;
    
    public ReplyCollection() {
        this.replies = new ArrayList<>();
    }
 
    public ReplyCollection(List<Reply> replies) {
        this.replies = new ArrayList<>(replies);
    }
    
    public void addReply(Reply reply) {
        replies.add(reply);
    }
    
    public boolean removeReply(Reply reply) {
        return replies.remove(reply);
    }
    
    public boolean removeReplyById(int replyId) {
        return replies.removeIf(r -> r.getReplyID() == replyId);
    }
    
    public List<Reply> getAllReplies() {
        return new ArrayList<>(replies);
    }
    
    public Reply getReplyById(int replyId) {
        for (Reply reply : replies) {
            if (reply.getReplyID() == replyId) {
                return reply;
            }
        }
        return null;
    }
    
    public ReplyCollection getRepliesByPost(int postId) {
        ReplyCollection subset = new ReplyCollection();
        for (Reply reply : replies) {
            if (reply.getPostID() == postId) {
                subset.addReply(reply);
            }
        }
        return subset;
    }
    
    
    public int size() {
        return replies.size();
    }
    
    public boolean isEmpty() {
        return replies.isEmpty();
    }
    
    public void clear() {
        replies.clear();
    }
    
    public String toString() {
        return "ReplyCollection [" + replies.size() + " replies]";
    }
}