package guiRole1;

public class ControllerRole1Home {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/
	
	protected static void performViewPost() {
	    guiPosts.ViewPosts.displayPosts(ViewRole1Home.theStage, ViewRole1Home.theUser);
	    guiPosts.ControllerPosts.performViewPosts();
	}
	
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
	}
	
	protected static void performQuit() {
		System.exit(0);
	}
}


