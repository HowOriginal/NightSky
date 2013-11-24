package com.example.titlescreen;

import android.app.Application;

public class AppVariables extends Application {
	    //using singleton yaaayy
        private static String user = null;
        private static String storyid = null;
        
        public static String getUser(){
        	return user;
        }
        
        public static String getStoryId() {
        	return storyid;
        }
        
        public static void setUser(String u) {
        	user = u;
        }
        
        public static void setStoryId(String sid) {
        	storyid = sid;
        }
        
        @Override
        public void onCreate() {
                //reinitialize variables
            user = null;
            storyid = null;
        }
}
