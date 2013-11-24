package com.example.titlescreen;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
//import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class LaunchScreen extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
	
		 final Button library = (Button) findViewById(R.id.button2);
	     library.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             // Perform action on click   
	
	             Intent activityChangeIntent = new Intent(LaunchScreen.this, LibraryActivity.class);
	             //activityChangeIntent.putExtra("text", user);
	             LaunchScreen.this.startActivity(activityChangeIntent);
	         }
	     });
	     
		 final Button skyview = (Button) findViewById(R.id.button1);
	     skyview.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             // Perform action on click   
	
	             Intent activityChangeIntent = new Intent(LaunchScreen.this, SkyView.class);
	             //activityChangeIntent.putExtra("text", user);
	             LaunchScreen.this.startActivity(activityChangeIntent);
	         }
	     });

  }  

	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	      	case R.id.login:  startActivity(new Intent(LaunchScreen.this, Login.class));;
	      		return true;
	      	case R.id.register:  startActivity(new Intent(LaunchScreen.this, Register.class));;
	      		return true; 
	      	case R.id.logout: 
	      		AppVariables.setUser(null);
	      		startActivity(new Intent(LaunchScreen.this, LaunchScreen.class));;
	      		return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {

		 if (AppVariables.getUser() != null) {
			  getMenuInflater().inflate(R.menu.launch_screen, menu);
		 }
		 else {
			 getMenuInflater().inflate(R.menu.login_menu, menu);
		 }
		 return true;
	 }

}
