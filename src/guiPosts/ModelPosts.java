package guiPosts;

import entityClasses.Post;
import entityClasses.Reply;

public class ModelPosts {
	
	//return the ID of the given string from either a Post or  Reply 
    protected static int getID(String s) {
        String[] stuff1 = s.split(" author: ");
        String temp = stuff1[0];
        String[] stuff2 = temp.split(" ");
        int id = Integer.parseInt(stuff2[1]);
        return id;
    }
    
    // print out the format of a Post
    protected static String formatPostForDisplay(Post post) {
        return "id: " + post.getPostID() + " author: " + post.getAuthor() + 
               " content: " + post.getContent();
    }
    
    // print out the format of a Reply
    protected static String formatReplyForDisplay(Reply reply) {
        return "id: " + reply.getReplyID() + " author: " + reply.getAuthor() + 
               " content: " + reply.getContent();
    }
}