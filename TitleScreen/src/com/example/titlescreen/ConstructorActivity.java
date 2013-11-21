package com.example.titlescreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

@SuppressLint("InlinedApi")
public class ConstructorActivity extends Activity {

	private ConstructorView cv;
	
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RelativeLayout rl = new RelativeLayout(this);
		
		cv = new ConstructorView(this);
		cv.setBackgroundColor(Color.WHITE);
		rl.addView(cv);
        
        
		
        Button backbutton = new Button(this);
	    RelativeLayout.LayoutParams bp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    bp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    bp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    backbutton.setLayoutParams(bp);
	    backbutton.setText("Back");
	    backbutton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            //Implement
	        }
	    });
	    rl.addView(backbutton);
	    
	    Button savebutton = new Button(this);
	    bp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    bp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    bp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    savebutton.setLayoutParams(bp);
	    savebutton.setText("Save");
	    savebutton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	//Retrieve the Constellation from the Constructor.
	        	Constellation con = cv.getConstellation();
	        	
	            //Implement
	        }
	    });
	    rl.addView(savebutton);
	    
	    setContentView(rl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
