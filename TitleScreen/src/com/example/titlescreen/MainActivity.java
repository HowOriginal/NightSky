package com.example.titlescreen;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
//import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

	public void gotolib(View view) {
		Intent intent = new Intent(this, LibraryActivity.class);
		startActivity(intent);
	}
	
  //  public void gotoskyview(View view){
  //  	Intent intent = new Intent(this, Skyview.class);
  //  }
}
