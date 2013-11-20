package com.example.constbuildertest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	private ConstructorView cv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		cv = new ConstructorView(this);
		cv.setBackgroundColor(Color.WHITE);
        setContentView(cv);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
