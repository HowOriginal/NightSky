package com.example.titlescreen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTabHost;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
import android.app.Activity;
//import android.widget.TextView;
import android.content.Intent;

public class FirstActivity extends Activity {

    protected void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.first_layout);
   
   	 final Button editstory = (Button) findViewById(R.id.button1);
     editstory.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
             // Perform action on click   

             Intent activityChangeIntent = new Intent(FirstActivity.this, EditStoryScreen.class);

             // currentContext.startActivity(activityChangeIntent);

             FirstActivity.this.startActivity(activityChangeIntent);
         }
     });
     
	 final Button newstory = (Button) findViewById(R.id.button2);
     newstory.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
             // Perform action on click   

             Intent activityChangeIntent = new Intent(FirstActivity.this, NewStoryScreen.class);

             // currentContext.startActivity(activityChangeIntent);

             FirstActivity.this.startActivity(activityChangeIntent);
         }
     });
    }
    
    

}
