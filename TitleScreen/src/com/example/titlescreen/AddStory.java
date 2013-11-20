package com.example.titlescreen;

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

public class AddStory extends Activity implements OnClickListener {

	private EditText storytitle, storytext;
	private TextView user;
	private Button mRegister;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	private static final String LOGIN_URL = "http://ezhang.myrpi.org/addstory.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addstory);
		
		TextView t = (TextView)findViewById(R.id.textname);
		t.setText(getIntent().getExtras().getString("un"));
		
		user = t;
		storytitle = (EditText) findViewById(R.id.title);
		storytext = (EditText) findViewById(R.id.story);

		mRegister = (Button) findViewById(R.id.addstory);
		mRegister.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		new Create().execute();
		
	}

	class Create extends AsyncTask<String, String, String> {

		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddStory.this);
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
			String username = user.getText().toString();
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
				Toast.makeText(AddStory.this, file_url, Toast.LENGTH_LONG)
						.show();
			}
		}
	}
}
