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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
//import android.view.View;
//import android.widget.Button;

public class FirstActivity extends Activity {
	private String jsonResult;
	private String url = "http://ezhang.myrpi.org/readstory.php";
	private ListView listView;

	List<Integer> nums = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_layout);
		listView = (ListView) findViewById(R.id.listView1);

		// Variables.user = getIntent().getStringExtra("text");
		// id = getIntent().getStringExtra("id");

		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				// Here you can do something when items are
				// selected/de-selected,
				// such as update the title in the CAB
				// AppVariables.setStoryId((String)
				// (listView.getItemAtPosition(position)));
				AppVariables uclass = new AppVariables();
				uclass.setStoryId(Integer.toString(nums.get(position)));
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// Respond to clicks on the actions in the CAB

				switch (item.getItemId()) {
				case R.id.editstory:

					Intent activityChangeIntent = new Intent(
							FirstActivity.this, EditStory.class);
					FirstActivity.this.startActivity(activityChangeIntent);
					mode.finish(); // Action picked, so close the CAB
					return true;
				case R.id.readstory:
					Intent activityChangeIntent2 = new Intent(
							FirstActivity.this, GetStory.class);
					FirstActivity.this.startActivity(activityChangeIntent2);
					// activityChangeIntent2.putExtra("id", id);
					mode.finish(); // Action picked, so close the CAB
					return true;
				default:
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate the menu for the CAB
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.my_stories, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// Here you can make any necessary updates to the activity when
				// the CAB is removed. By default, selected items are
				// deselected/unchecked.
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// Here you can perform updates to the CAB due to
				// an invalidate() request
				return false;
			}

		});
		accessWebService();

		ActionBar actionBar = getActionBar(); // getSupportActionBar()
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.login:
			startActivity(new Intent(FirstActivity.this, Login.class));
			;
			return true;
		case R.id.register:
			startActivity(new Intent(FirstActivity.this, Register.class));
			;
			return true;
		case R.id.logout:
			AppVariables.setUser(null);
			startActivity(new Intent(FirstActivity.this, LaunchScreen.class));
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
	private class JsonReadTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			// Variables uclass = new Variables();
			// String temp = uclass.user;
			String temp = AppVariables.getUser();

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);

			try {
				List<NameValuePair> para = new ArrayList<NameValuePair>();
				// parameter here just replace with the id of the story
				para.add(new BasicNameValuePair("username", temp));
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
		List<Map<String, String>> uList = new ArrayList<Map<String, String>>();
		// Variables uclass = new Variables();
		// String temp = uclass.user;
		String temp = AppVariables.getUser();
		// makes the list
		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);

			JSONArray jsonMainNode = jsonResponse
					.optJSONArray("users and stories");
			//
			for (int i = 0; i < jsonMainNode.length(); i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				String name = jsonChildNode.optString("username");

				if (name.equals(temp)) {
					String title = jsonChildNode.optString("title");
					String outPut = "Title: " + title;
					uList.add(createM("e", outPut));
					nums.add(jsonChildNode.optInt("id"));
				}
			}

			if (temp == null || jsonMainNode.length() == 0) {
				String outPut = "You have not created any stories yet";
				uList.add(createM("e", outPut));
			}

		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Error" + e.toString(),
					Toast.LENGTH_SHORT).show();
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, uList,
				android.R.layout.simple_list_item_1, new String[] { "e" },
				new int[] { android.R.id.text1 });
		listView.setAdapter(simpleAdapter);
	}

	private HashMap<String, String> createM(String name, String number) {
		HashMap<String, String> NameNo = new HashMap<String, String>();
		NameNo.put(name, number);
		return NameNo;
	}
}
