package com.example.titlescreen;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
//import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class LibraryActivity extends Activity {


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_screen);
    
	
	 final Button firstact = (Button) findViewById(R.id.button2);
     firstact.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
             // Perform action on click   

             Intent activityChangeIntent = new Intent(LibraryActivity.this, FirstActivity.class);

             // currentContext.startActivity(activityChangeIntent);

             LibraryActivity.this.startActivity(activityChangeIntent);
         }
     });
     
	 final Button secondact = (Button) findViewById(R.id.button1);
     secondact.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
             // Perform action on click   

             Intent activityChangeIntent = new Intent(LibraryActivity.this, SecondActivity.class);

             // currentContext.startActivity(activityChangeIntent);

             LibraryActivity.this.startActivity(activityChangeIntent);
         }
     });
     
	 final Button editstory = (Button) findViewById(R.id.button3);
     editstory.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
             // Perform action on click   

             Intent activityChangeIntent = new Intent(LibraryActivity.this, EditStoryScreen.class);

             // currentContext.startActivity(activityChangeIntent);

             LibraryActivity.this.startActivity(activityChangeIntent);
         }
     });
 }


}
