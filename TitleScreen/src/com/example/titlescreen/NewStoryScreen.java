package com.example.titlescreen;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTabHost;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewStoryScreen extends Activity {
	
	private EditText storytitle, storytext;
	private String user;
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	private static final String LOGIN_URL = "http://ezhang.myrpi.org/addstory.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

    protected void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.new_screen);
        
        user = getIntent().getStringExtra("text");
        
		storytitle = (EditText) findViewById(R.id.story_title);
		storytext = (EditText) findViewById(R.id.new_story);
        
        ActionBar actionBar = getActionBar(); //getSupportActionBar()
        actionBar.setDisplayHomeAsUpEnabled(true);
    
    //upon clicking button, add story to database
      	 final Button savestory = (Button) findViewById(R.id.save_story);
         savestory.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 // Perform action on click
//            	 Create temp = new Create();
//            	 temp.execute();
            	 new Create().execute();
            	 //if not temp failure...
                 Intent activityChangeIntent = new Intent(NewStoryScreen.this, ConstructorActivity.class);
                 activityChangeIntent.putExtra("text", user);
                 NewStoryScreen.this.startActivity(activityChangeIntent);
             }
         });
    }
    
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case android.R.id.home:
	            this.finish();
	            return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {

		 if (user != "") {
			  getMenuInflater().inflate(R.menu.launch_screen, menu);
		 }
		 else {
			 getMenuInflater().inflate(R.menu.login_menu, menu);
		 }
		// getMenuInflater().inflate(R.menu.launch_screen, menu);
		 return true;
	 }
	 
	 
	 class Create extends AsyncTask<String, String, String> {

			boolean failure = false;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(NewStoryScreen.this);
				pDialog.setMessage("Adding Story...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
			}

			@Override
			protected String doInBackground(String... args) {
				// TODO Auto-generated method stub
				// Check for success tag
				int success;
				String username = user;
				String title = storytitle.getText().toString();
				String story = storytext.getText().toString();
				try {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("username", username));
					params.add(new BasicNameValuePair("title", title));
					params.add(new BasicNameValuePair("story", story));

					Log.d("request!", "starting");

					// Posting user data to script
					JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
							params);

					// full json response
					Log.d("Add attempt", json.toString());

					// json success element
					success = json.getInt(TAG_SUCCESS);
					if (success == 1) {
						Log.d("Story Added!", json.toString());
						finish();
						return json.getString(TAG_MESSAGE);
					} else {
						Log.d("Addition Failed!", json.getString(TAG_MESSAGE));
						return json.getString(TAG_MESSAGE);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return null;

			}

			protected void onPostExecute(String file_url) {
				pDialog.dismiss();
				if (file_url != null) {
					Toast.makeText(NewStoryScreen.this, file_url, Toast.LENGTH_LONG)
							.show();
				}
			}
		}


}

    

