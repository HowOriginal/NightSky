package com.example.titlescreen;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
//import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class LaunchScreen extends Activity {


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
    //}
	
	 final Button library = (Button) findViewById(R.id.button2);
     library.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
             // Perform action on click   

             Intent activityChangeIntent = new Intent(LaunchScreen.this, LibraryActivity.class);

             // currentContext.startActivity(activityChangeIntent);

             LaunchScreen.this.startActivity(activityChangeIntent);
         }
     });
     
	 final Button skyview = (Button) findViewById(R.id.button1);
     skyview.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
             // Perform action on click   

             Intent activityChangeIntent = new Intent(LaunchScreen.this, LibraryActivity.class);

             // currentContext.startActivity(activityChangeIntent);

             LaunchScreen.this.startActivity(activityChangeIntent);
         }
     });

  }  

}
