package guiRole2;


public class ControllerRole2Home {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/
	
	protected static void performViewPost() {
	    guiPosts.ViewPosts.displayPosts(ViewRole2Home.theStage, ViewRole2Home.theUser);
	    guiPosts.ControllerPosts.performViewPosts();
	    
	}
	
	protected static void updateReplyAlert() {
	    try {
	        String username = ViewRole2Home.theUser.getUserName();
	        int unreadCount = applicationMain.FoundationsMain.database.getUnreadReplyCount(username);
	        
	        if (unreadCount > 0) {
	            String message = (unreadCount == 1) 
	                ? "🔔 You have 1 new reply to your post" 
	                : "🔔 You have " + unreadCount + " new replies to your posts";
	            
	            ViewRole2Home.label_ReplyAlert.setText(message);
	            ViewRole2Home.label_ReplyAlert.setVisible(true);
	        } else {
	            ViewRole2Home.label_ReplyAlert.setVisible(false);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole2Home.theStage);
	}
	
	protected static void performQuit() {
		System.exit(0);
	}

}
