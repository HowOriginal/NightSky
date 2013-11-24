package com.example.titlescreen;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
 
public class GetStory extends Activity {
 String jsonResult;
 String url = "http://ezhang.myrpi.org/getstory.php";
 ListView listView;
 
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.getstory);
  
  accessWebService();
  
  ActionBar actionBar = getActionBar(); //getSupportActionBar()
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
   	case R.id.login:  startActivity(new Intent(GetStory.this, Login.class));;
   		return true;
   	case R.id.register:  startActivity(new Intent(GetStory.this, Register.class));;
   		return true; 
   	case R.id.logout: 
   		AppVariables.setUser(null);
   		startActivity(new Intent(GetStory.this, LaunchScreen.class));;
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
 
 // Async Task to access the web
 class JsonReadTask extends AsyncTask<String, Void, String> {
  @Override
  protected String doInBackground(String... params) {
   HttpClient httpclient = new DefaultHttpClient();
   HttpPost httppost = new HttpPost(url);
   
   try {
   List<NameValuePair> para = new ArrayList<NameValuePair>();
   //parameter here just replace with the id of the story
   para.add(new BasicNameValuePair("id", AppVariables.getStoryId()));
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
 
 public void accessWebService() {
  JsonReadTask task = new JsonReadTask();
  // passes values for the urls string array
  task.execute(new String[] { url });
 }
 
 // build hash set for list view
 public void ListDrawer() {
  
  TextView guser = (TextView)findViewById(R.id.untext);
  TextView gscore = (TextView)findViewById(R.id.scoretext);
  TextView gtitle = (TextView)findViewById(R.id.titletext);
  TextView gstory = (TextView)findViewById(R.id.storytext);
  
  try {
   JSONObject jsonResponse = new JSONObject(jsonResult);
   JSONArray jsonMainNode = jsonResponse.optJSONArray("users and stories");
 
    JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
    gtitle.setText( jsonChildNode.optString("title"));
    gstory.setText( jsonChildNode.optString("story"));
    guser.setText( jsonChildNode.optString("username"));
    gscore.setText( String.valueOf(jsonChildNode.optInt("score")));
  } catch (JSONException e) {
	  Log.i("CATCHRESULT", "5");
   Toast.makeText(getApplicationContext(), "Error" + e.toString(),
     Toast.LENGTH_SHORT).show();
  }
}
}
