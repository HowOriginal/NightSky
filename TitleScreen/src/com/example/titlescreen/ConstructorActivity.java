package com.example.titlescreen;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("InlinedApi")
public class ConstructorActivity extends Activity {

	private ConstructorView cv;
	
	String LOGIN_URL = "http://ezhang.myrpi.org/addconstellation.php";
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
        cv.setBackgroundColor(Color.rgb(10, 10, 45));
        rl.addView(cv);

        ActionBar actionBar = getActionBar(); // getSupportActionBar()
        actionBar.setDisplayHomeAsUpEnabled(true);

        RelativeLayout.LayoutParams bp = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        Button savebutton = new Button(this);
        bp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        bp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        savebutton.setLayoutParams(bp);
        savebutton.setText("Save");
        savebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        // Retrieve the Constellation from the Constructor.
                        Constellation con = cv.getConstellation();

                        Create temp = new Create();
                        temp.s = con.stars;
                        temp.l = con.lines;

                        if (temp.s.size() < 1) {
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_LONG;
                                Toast toas = Toast.makeText(context,
                                                "Need at least 1 star to save.", duration);
                                toas.show();
                                return;
                        }

                        temp.execute();
                        Intent j = new Intent(ConstructorActivity.this, LaunchScreen.class);
                        startActivity(j);
                }
        });

        Button clearbutton = new Button(this);
        bp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        bp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        clearbutton.setLayoutParams(bp);
        clearbutton.setText("Clear");
        clearbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        cv.setConstellation(new Constellation());

                }
        });

        rl.addView(clearbutton);
        rl.addView(savebutton);

        setContentView(rl);
	}

	

    class Create extends AsyncTask<String, String, String> {

            ArrayList<FloatPair> s;
            ArrayList<IntPair> l;

            @Override
            protected void onPreExecute() {
                    super.onPreExecute();
                    pDialog = new ProgressDialog(ConstructorActivity.this);
                    pDialog.setMessage("Adding Constellation...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();
            }

            @Override
            protected String doInBackground(String... args) {
                    // TODO Auto-generated method stub
                    // Check for success tag
                    // int success;

                    try {
                            String[] starfieldnames = { "x1", "x2", "x3", "x4", "x5", "x6",
                                            "x7", "y1", "y2", "y3", "y4", "y5", "y6", "y7" };
                            String[] linefieldnames = { "p1a", "p1b", "p2a", "p2b", "p3a",
                                            "p3b", "p4a", "p4b", "p5a", "p5b", "p6a", "p6b", "p7a",
                                            "p7b" };
                            List<NameValuePair> params = new ArrayList<NameValuePair>();

                            for (int i = 0; i < 7; ++i) {
                                    if (s.size() > i) {
                                            params.add(new BasicNameValuePair(starfieldnames[i],
                                                            Float.toString(s.get(i).first)));
                                            params.add(new BasicNameValuePair(
                                                            starfieldnames[i + 7],
                                                            Float.toString(s.get(i).second)));
                                    } else {
                                            params.add(new BasicNameValuePair(starfieldnames[i],
                                                            Float.toString(s.get(0).first)));
                                            params.add(new BasicNameValuePair(
                                                            starfieldnames[i + 7],
                                                            Float.toString(s.get(0).second)));
                                    }
                                    if (l.size() > i) {
                                            params.add(new BasicNameValuePair(
                                                            linefieldnames[2 * i], Integer.toString(l
                                                                            .get(i).first)));
                                            params.add(new BasicNameValuePair(
                                                            linefieldnames[2 * i + 1], Integer.toString(l
                                                                            .get(i).second)));
                                    } else {
                                            params.add(new BasicNameValuePair(
                                                            linefieldnames[2 * i], "0"));
                                            params.add(new BasicNameValuePair(
                                                            linefieldnames[2 * i + 1], "0"));
                                    }
                            }

                            Log.d("request!", "starting");
                            pDialog.setMessage("Saving");

                            // Posting user data to script
                            jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);

                            // full json response
                            /*
                             * Log.d("Add attempt", json.toString()); success = 1; if
                             * (success == 1) { Log.d("Added!", json.toString()); finish();
                             * } else { Log.d("Addition Failed!",
                             * json.getString(TAG_MESSAGE)); }
                             */
                    } finally {

                    }

                    return null;

            }

            protected void onPostExecute(String file_url) {
                    pDialog.dismiss();
                    if (file_url != null) {
                            Toast.makeText(ConstructorActivity.this, file_url, Toast.LENGTH_LONG)
                                            .show();
                    }
            }
    }

  	 @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
      	case R.id.login:  startActivity(new Intent(ConstructorActivity.this, Login.class));;
      		return true;
      	case R.id.register:  startActivity(new Intent(ConstructorActivity.this, Register.class));;
      		return true; 
      	case R.id.logout: 
			AppVariables.setUser(null);
			startActivity(new Intent(ConstructorActivity.this, LaunchScreen.class));;
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
