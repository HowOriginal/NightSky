package com.example.titlescreen;

import com.example.reviewiteration.IntPair;
import com.example.reviewiteration.FloatPair;

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
 
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
 
public class ConstellationRead extends Activity {
 private String jsonResult;
 private String url = "http://ezhang.myrpi.org/getconstellation.php";
 private ListView listView;
 
 //THIS IS THE ARRAY WITH EVERYTHING
 ArrayList<Constellation> sky;
 
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.constellationread);
  listView = (ListView) findViewById(R.id.listView2);
  accessWebService();
 }
 
 public class Constellation {
		
		private String id;
		public ArrayList<FloatPair> stars = new ArrayList<FloatPair>();
		public ArrayList<IntPair> lines = new ArrayList<IntPair>();
		
		//Constructors
		public Constellation() {
			id = "No id";
		}
		public Constellation(String aid) {
			id = aid;
		}
		
		//Get functions
		public String getId(){return id;}
		public FloatPair getStar(int index) {return stars.get(index);}
		public int numStars() {return stars.size();}
		public ArrayList<IntPair> getLines() {return lines;}
		public IntPair getLine(int index) {return lines.get(index);}
		public int numLines() {return lines.size();}
		
		//Set functions
		public void setId(String aid) {id=aid;}
		public void setStar(int index, FloatPair astar) { stars.set(index, astar);}
		public void setLine(int index, IntPair aline) { lines.set(index, aline);}
		
		//Add and Delete functions
		public void addStar(FloatPair astar) {stars.add(astar);}
		public void deleteStar(int index) {
			IntPair ip;
			for (int i=0; i<lines.size(); ++i) {
				ip = lines.get(i);
				if (ip.first==index || ip.second==index) {
					lines.remove(i);
					--i;
				}
			}
			stars.remove(index);
			for (int i=0; i<lines.size(); ++i) {
				ip = lines.get(i);
				if (ip.first>index) {ip.first-=1;}
				if (ip.second>index) {ip.second-=1;}
			}
		}
		public void addLine(IntPair aline) {lines.add(aline);}
		public void deleteLine(int index) {lines.remove(index);}
		
		//Draw function
		public void draw() {
			//Need to implement.
		}
	}
 
 @Override
 public boolean onCreateOptionsMenu(Menu menu) {
  // Inflate the menu; this adds items to the action bar if it is present.
  getMenuInflater().inflate(R.menu.main, menu);
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
  List<Map<String, String>> starlist = new ArrayList<Map<String, String>>();
 
  try {
   JSONObject jsonResponse = new JSONObject(jsonResult);
   JSONArray jsonMainNode = jsonResponse.optJSONArray("constellations");
   
   //ArrayList of all stars
   ArrayList<Constellation>stars;
   
   for (int i = 0; i < jsonMainNode.length(); i++) {
    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i); 
    
    int id = jsonChildNode.optInt("id");
    float x1 = jsonChildNode.optInt("x1");
    float x2 = jsonChildNode.optInt("x2");
    float x3 = jsonChildNode.optInt("x3");
    float x4 = jsonChildNode.optInt("x4");
    float x5 = jsonChildNode.optInt("x5");
    float x6 = jsonChildNode.optInt("x6");
    float x7 = jsonChildNode.optInt("x7");
    float y1 = jsonChildNode.optInt("y1");
    float y2 = jsonChildNode.optInt("y2");
    float y3 = jsonChildNode.optInt("y3");
    float y4 = jsonChildNode.optInt("y4");
    float y5 = jsonChildNode.optInt("y5");
    float y6 = jsonChildNode.optInt("y6");
    float y7 = jsonChildNode.optInt("y7");
    
    int p1a = jsonChildNode.optInt("p1a");
    int p1b = jsonChildNode.optInt("p1b");
    int p2a = jsonChildNode.optInt("p2a");
    int p2b = jsonChildNode.optInt("p2b");
    int p3a = jsonChildNode.optInt("p3a");
    int p3b = jsonChildNode.optInt("p3b");
    int p4a = jsonChildNode.optInt("p4a");
    int p4b = jsonChildNode.optInt("p4b");
    int p5a = jsonChildNode.optInt("p5a");
    int p5b = jsonChildNode.optInt("p5a");
    int p6a = jsonChildNode.optInt("p6a");
    int p6b = jsonChildNode.optInt("p6b");
    int p7a = jsonChildNode.optInt("p7a");
    int p7b = jsonChildNode.optInt("p7b");
    
    int a=0, b=0;
    
    //use this constellation for information
    Constellation temp = new Constellation();
    
    temp.setId(Integer.toString(id));
    temp.addStar(new FloatPair(x1, y1));
    temp.addStar(new FloatPair(x2, y2));
    temp.addStar(new FloatPair(x3, y3));
    temp.addStar(new FloatPair(x4, y4));
    temp.addStar(new FloatPair(x5, y5));
    temp.addStar(new FloatPair(x6, y6));
    temp.addStar(new FloatPair(x7, y7));
    
    temp.addLine(new IntPair(p1a, p1b));
    temp.addLine(new IntPair(p2a, p2b));
    temp.addLine(new IntPair(p3a, p3b));
    temp.addLine(new IntPair(p4a, p4b));
    temp.addLine(new IntPair(p5a, p5b));
    temp.addLine(new IntPair(p6a, p6b));
    temp.addLine(new IntPair(p7a, p7b));
    
    //add to the sky object
    sky.add(temp);
    
    //clears it for the next object
    temp.stars.clear();
    temp.lines.clear();
    
    //dummy output to see how many stars in the sky
    String outPut = "Points ";
    starlist.add(createconst("points", outPut));
   }
  } catch (JSONException e) {
   Toast.makeText(getApplicationContext(), "Error" + e.toString(),
     Toast.LENGTH_SHORT).show();
  }
 
  SimpleAdapter simpleAdapter = new SimpleAdapter(this, starlist,
    android.R.layout.simple_list_item_1,
    new String[] { "points" }, new int[] { android.R.id.text1 });
  listView.setAdapter(simpleAdapter);
 }
 
 private HashMap<String, String> createconst(String name, String number) {
  HashMap<String, String> constellation = new HashMap<String, String>();
  constellation.put(name, number);
  return constellation;
 }
}