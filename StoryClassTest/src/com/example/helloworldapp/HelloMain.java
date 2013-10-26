package com.example.helloworldapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HelloMain extends Activity {
	EditText thetext;
	Story thestory = new Story("theauthor", "theid", "thetitle");
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hello_main);
		// Capture our button from layout
		Button[] buttons = new Button[8];
	    buttons[0] = (Button)findViewById(R.id.Gauthor);
		buttons[1] = (Button)findViewById(R.id.Gid);
		buttons[2] = (Button)findViewById(R.id.Gtitle);
		buttons[3] = (Button)findViewById(R.id.Gtext);
		buttons[4] = (Button)findViewById(R.id.Sauthor);
		buttons[5] = (Button)findViewById(R.id.Sid);
		buttons[6] = (Button)findViewById(R.id.Stitle);
		buttons[7] = (Button)findViewById(R.id.Stext);
		
		//Register the onClick listener with the implementation above
		buttons[0].setOnClickListener(mAddListener1);
		buttons[1].setOnClickListener(mAddListener2);
		buttons[2].setOnClickListener(mAddListener3);
		buttons[3].setOnClickListener(mAddListener4);
		buttons[4].setOnClickListener(mAddListener5);
		buttons[5].setOnClickListener(mAddListener6);
		buttons[6].setOnClickListener(mAddListener7);
		buttons[7].setOnClickListener(mAddListener8);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hello_main, menu);
		return true;
	}

	
	// Create an anonymous implementation of OnClickListener
	private OnClickListener mAddListener1 = new OnClickListener()
	{
		public void onClick(View v)
		{
			long id = 0;
			// do something when the button is clicked
			try
			{
				Context context = getApplicationContext();
				String text = "Author = " + thestory.getAuthor() + "!";
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
			catch (Exception ex)
			{
				Context context = getApplicationContext();
				String text = ex.toString() + "ID = " + id;
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		}
	};
	
	
	private OnClickListener mAddListener2 = new OnClickListener()
	{
		public void onClick(View v)
		{
			Context context = getApplicationContext();
			String text = "ID = " + thestory.getId() + "!";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	};
	
	private OnClickListener mAddListener3 = new OnClickListener()
	{
		public void onClick(View v)
		{
			Context context = getApplicationContext();
			String text = "Title = " + thestory.getTitle() + "!";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	};
	
	private OnClickListener mAddListener4 = new OnClickListener()
	{
		public void onClick(View v)
		{
			Context context = getApplicationContext();
			String text = "Text = " + thestory.getText() + "!";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	};
	
	private OnClickListener mAddListener5 = new OnClickListener()
	{
		public void onClick(View v)
		{
			try {
				thetext = (EditText)findViewById(R.id.thetext);
				Context context = getApplicationContext();
				String str = "" + thetext.getText();
				thestory.setAuthor(str);
				String text = "Author = " + thestory.getAuthor() + "!";
				thetext.setText("");
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
			catch (Exception ex)
			{
				Context context = getApplicationContext();
				String text = ex.toString();
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		}
	};
	
	private OnClickListener mAddListener6 = new OnClickListener()
	{
		public void onClick(View v)
		{
			thetext = (EditText)findViewById(R.id.thetext);
			Context context = getApplicationContext();
			String str = "" + thetext.getText();
			thestory.setId(str);
			String text = "ID = " + thestory.getId() + "!";
			thetext.setText("");
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	};
	
	private OnClickListener mAddListener7 = new OnClickListener()
	{
		public void onClick(View v)
		{
			thetext = (EditText)findViewById(R.id.thetext);
			Context context = getApplicationContext();
			String str = "" + thetext.getText();
			thestory.setTitle(str);
			String text = "Title = " + thestory.getTitle() + "!";
			thetext.setText("");
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	};
	
	private OnClickListener mAddListener8 = new OnClickListener()
	{
		public void onClick(View v)
		{
			thetext = (EditText)findViewById(R.id.thetext);
			Context context = getApplicationContext();
			String str = "" + thetext.getText();
			thestory.setText(str);
			String text = "Text = " + thestory.getText() + "!";
			thetext.setText("");
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	};
}
