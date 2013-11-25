package com.example.titlescreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
//import android.widget.RelativeLayout;
//import android.widget.TextView;

public class LibraryActivity extends Activity {
	private String jsonResult;
	private String url = "http://ezhang.myrpi.org/readstory.php";
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library_screen);
		ActionBar actionBar = getActionBar(); // getSupportActionBar()
		actionBar.setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listView1);
		accessWebService();

		final Button firstact = (Button) findViewById(R.id.button2);
		firstact.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				Intent activityChangeIntent = new Intent(LibraryActivity.this,
						FirstActivity.class);
				// activityChangeIntent.putExtra("text", user);
				// activityChangeIntent.putExtra("id", id);
				LibraryActivity.this.startActivity(activityChangeIntent);
			}
		});

		final Button secondact = (Button) findViewById(R.id.button1);
		secondact.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				Intent activityChangeIntent = new Intent(LibraryActivity.this,
						SecondActivity.class);
				// activityChangeIntent.putExtra("text", user);
				// activityChangeIntent.putExtra("id", id);
				LibraryActivity.this.startActivity(activityChangeIntent);
			}
		});

		final Button newstory = (Button) findViewById(R.id.button3);
		newstory.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

				Intent activityChangeIntent = new Intent(LibraryActivity.this,
						NewStoryScreen.class);
				// activityChangeIntent.putExtra("text", user);
				LibraryActivity.this.startActivity(activityChangeIntent);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.login:
			startActivity(new Intent(LibraryActivity.this, Login.class));
			;
			return true;
		case R.id.register:
			startActivity(new Intent(LibraryActivity.this, Register.class));
			;
			return true;
		case R.id.logout:
			AppVariables.setUser(null);
			startActivity(new Intent(LibraryActivity.this, LaunchScreen.class));
			;
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (AppVariables.getUser() != null) {
			getMenuInflater().inflate(R.menu.launch_screen, menu);
		} else {
			getMenuInflater().inflate(R.menu.login_menu, menu);
		}
		return true;
	}

	// Async Task to access the web
	private class JsonReadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			try {
				HttpResponse response = httpclient.execute(httppost);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
			}

			catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private StringBuilder inputStreamToString(InputStream is) {
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

	public void accessWebService() {
		JsonReadTask task = new JsonReadTask();
		// passes values for the urls string array
		task.execute(new String[] { url });
	}

	// build hash set for list view
	public void ListDrawer() {
		List<Map<String, String>> employeeList = new ArrayList<Map<String, String>>();

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			JSONArray jsonMainNode = jsonResponse
					.optJSONArray("users and stories");

			for (int i = 0; i < jsonMainNode.length(); i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				String name = jsonChildNode.optString("username");
				String title = jsonChildNode.optString("title");
				String outPut = title + " by: " + name;
				employeeList.add(createEmployee("employees", outPut));
			}
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Error" + e.toString(),
					Toast.LENGTH_SHORT).show();
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, employeeList,
				android.R.layout.simple_list_item_1,
				new String[] { "employees" }, new int[] { android.R.id.text1 });
		listView.setAdapter(simpleAdapter);
	}

	private HashMap<String, String> createEmployee(String name, String number) {
		HashMap<String, String> employeeNameNo = new HashMap<String, String>();
		employeeNameNo.put(name, number);
		return employeeNameNo;
	}
}
