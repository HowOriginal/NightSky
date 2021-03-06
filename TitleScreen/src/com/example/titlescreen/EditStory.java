package com.example.titlescreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditStory extends Activity implements OnClickListener {
	String jsonResult;
	String url = "http://ezhang.myrpi.org/getstory.php";
	String url2 = "http://ezhang.myrpi.org/editstory.php";

	public void onClick(View v) {
		new JsonReadTask2().execute();
		Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG)
				.show();
		Intent i = new Intent(this, LibraryActivity.class);
		startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editstory);
		Button mSubmit = (Button) findViewById(R.id.button1);
		mSubmit.setOnClickListener(this);

		accessWebService();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		JsonReadTask temp = new JsonReadTask();
		temp.execute();
		try {
			temp.get(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.login:
			startActivity(new Intent(EditStory.this, Login.class));
			;
			return true;
		case R.id.register:
			startActivity(new Intent(EditStory.this, Register.class));
			;
			return true;
		case R.id.logout:
			AppVariables.setUser(null);
			startActivity(new Intent(EditStory.this, LaunchScreen.class));
			;
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (AppVariables.getUser() != null) {
			getMenuInflater().inflate(R.menu.login_menu, menu);
		} else {
			getMenuInflater().inflate(R.menu.launch_screen, menu);
		}
		return true;
	}

	// Async Task to access the web
	class JsonReadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			AppVariables uclass = new AppVariables();

			try {
				List<NameValuePair> para = new ArrayList<NameValuePair>();
				// parameter here just replace with the id of the story
				para.add(new BasicNameValuePair("id", uclass.getStoryId()));
				httppost.setEntity(new UrlEncodedFormEntity(para));

				HttpResponse response = httpclient.execute(httppost);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
			}

			catch (ClientProtocolException e) {
				Log.i("CATCHRESULT", "2");
				e.printStackTrace();
			} catch (IOException e) {
				Log.i("CATCHRESULT", "3");
				e.printStackTrace();
			}
			return null;
		}

		StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				// e.printStackTrace();
				Log.i("CATCHRESULT", "4");
				Toast.makeText(getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			ListDrawer();
		}
	}// end async task

	// 2nd async task to submit
	class JsonReadTask2 extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url2);
			AppVariables uclass = new AppVariables();

			EditText a = (EditText) findViewById(R.id.editText1);
			String ntitle = a.getText().toString();
			EditText b = (EditText) findViewById(R.id.editText2);
			String nstory = b.getText().toString();

			try {
				List<NameValuePair> para = new ArrayList<NameValuePair>();
				// parameters here just replace with the passed in values
				para.add(new BasicNameValuePair("id", uclass.getStoryId()));
				para.add(new BasicNameValuePair("username", uclass.getUser()));
				para.add(new BasicNameValuePair("title", ntitle));
				para.add(new BasicNameValuePair("story", nstory));

				httppost.setEntity(new UrlEncodedFormEntity(para));

				HttpResponse response = httpclient.execute(httppost);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();

			}

			catch (ClientProtocolException e) {
				Log.i("CATCHRESULT", "2");
				e.printStackTrace();
			} catch (IOException e) {
				Log.i("CATCHRESULT", "3");
				e.printStackTrace();
			}
			return null;
		}

		StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				// e.printStackTrace();
				Log.i("CATCHRESULT", "4");
				Toast.makeText(getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}// end async task

	public void accessWebService() {
		JsonReadTask task = new JsonReadTask();
		// passes values for the urls string array
		task.execute(new String[] { url });
	}

	// build hash set for list view
	public void ListDrawer() {

		EditText gtitle = (EditText) findViewById(R.id.editText1);
		EditText gstory = (EditText) findViewById(R.id.editText2);

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			JSONArray jsonMainNode = jsonResponse
					.optJSONArray("users and stories");

			JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
			gtitle.setText(jsonChildNode.optString("title"));
			gstory.setText(jsonChildNode.optString("story"));

		} catch (JSONException e) {
			Log.i("CATCHRESULT", "5");
			Toast.makeText(getApplicationContext(), "Error" + e.toString(),
					Toast.LENGTH_SHORT).show();
		}
	}
}