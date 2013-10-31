package com.example.titlescreen;

import android.os.Bundle;
//import android.app.Activity;
import android.content.Intent;
//import android.view.Menu;
import android.view.View;

public class LibraryActivity extends MainActivity {


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

	public void firstact(View view) {
		Intent intent1 = new Intent(this, FirstActivity.class);
		startActivity(intent1);
	}
	
	public void secondact(View view) {
		Intent intent2 = new Intent(this, SecondActivity.class);
		startActivity(intent2);
	}
	
	public void editstory(View view) {
		Intent intent2 = new Intent(this, EditStoryScreen.class);
		startActivity(intent2);
	}
}