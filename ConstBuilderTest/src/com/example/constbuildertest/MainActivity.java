package com.example.constbuildertest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("InlinedApi")
public class MainActivity extends Activity {

	private ConstructorView cv;
	
	String LOGIN_URL = "http://ezhang.myrpi.org/addminiconst.php";
	String TAG_SUCCESS = "success";
	String TAG_MESSAGE = "message";
	JSONParser jsonParser = new JSONParser();
	ProgressDialog pDialog;
	
	
	
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
	        	
	        	Create temp = new Create();
	        	temp.s = con.stars;
	        	temp.l = con.lines;
	        	
	        	temp.execute();
	            
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
	
	class Create extends AsyncTask<String, String, String> {
		
		ArrayList<FloatPair> s;
		ArrayList<IntPair> l;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Adding Constellation...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("x1", Float.toString(s.get(0).first)));
				
				/*params.add(new BasicNameValuePair("x3", Float.toString(s.get(2).first)));
				params.add(new BasicNameValuePair("x4", Float.toString(s.get(3).first)));
				params.add(new BasicNameValuePair("x5", Float.toString(s.get(4).first)));
				params.add(new BasicNameValuePair("x6", Float.toString(s.get(5).first)));
				params.add(new BasicNameValuePair("x7", Float.toString(s.get(6).first))); */
				params.add(new BasicNameValuePair("y1", Float.toString(s.get(0).second)));
				
				params.add(new BasicNameValuePair("x2", Float.toString(s.get(1).first)));
				
				params.add(new BasicNameValuePair("y2", Float.toString(s.get(1).second)));
				/*params.add(new BasicNameValuePair("y3", Float.toString(s.get(2).second)));
				params.add(new BasicNameValuePair("y4", Float.toString(s.get(3).second)));
				params.add(new BasicNameValuePair("y5", Float.toString(s.get(4).second)));
				params.add(new BasicNameValuePair("y6", Float.toString(s.get(5).second)));
				params.add(new BasicNameValuePair("y7", Float.toString(s.get(6).second))); */
				
				params.add(new BasicNameValuePair("p1a", Integer.toString(l.get(0).first)));
				params.add(new BasicNameValuePair("p1b", Integer.toString(l.get(0).second)));
				/*params.add(new BasicNameValuePair("p2a", Integer.toString(l.get(1).first)));
				params.add(new BasicNameValuePair("p2b", Integer.toString(l.get(1).second)));
				params.add(new BasicNameValuePair("p3a", Integer.toString(l.get(2).first)));
				params.add(new BasicNameValuePair("p3b", Integer.toString(l.get(2).second)));
				params.add(new BasicNameValuePair("p4a", Integer.toString(l.get(3).first)));
				params.add(new BasicNameValuePair("p4b", Integer.toString(l.get(3).second)));
				params.add(new BasicNameValuePair("p5a", Integer.toString(l.get(4).first)));
				params.add(new BasicNameValuePair("p5b", Integer.toString(l.get(4).second)));
				params.add(new BasicNameValuePair("p6a", Integer.toString(l.get(5).first)));
				params.add(new BasicNameValuePair("p6b", Integer.toString(l.get(5).second)));
				params.add(new BasicNameValuePair("p7a", Integer.toString(l.get(6).first)));
				params.add(new BasicNameValuePair("p7b", Integer.toString(l.get(6).second))); */
				
				
				Log.d("request!", "starting");
				pDialog.setMessage(Integer.toString(params.size()));
				
				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
						params);
				pDialog.setMessage("hi");
				// full json response
				Log.d("Add attempt", json.toString());
				success = 1;
				if (success == 1) {
					Log.d("Added!", json.toString());
					finish();
				} else {
					Log.d("Addition Failed!", json.getString(TAG_MESSAGE));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG)
						.show();
			}
		}
	}
	
}
