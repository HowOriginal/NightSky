package com.example.reviewiteration;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Polyline;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;
//import com.threed.jpct.RGBColor;
import java.util.ArrayList;

public class Skyview extends Activity{

	// Used to handle pause and resume...
	private static Skyview master = null;

	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	private FrameBuffer fb = null;
	private World world = null;
	private RGBColor back = new RGBColor(50, 50, 100);
	
	//Finger touch variables
	private float touchTurn = 0;
	private float touchTurnUp = 0;
	private float xpos = -1;
	private float ypos = -1;
	private float x2pos = -1;
	private float y2pos = -1;

	private Object3D sky = null;
	private int fps = 0;
	private Light sun = null;
	
	private int ZoomLevel = 0;
	private int ZoomCap = 300;
	private int prevdist = 0;
	private String mode = "drag";
	private Camera cam;
	private boolean loadingFlag = true;
	private boolean loadingText = true;
	private TextView ld;
	private String ConstellationID;
	private long ZoomTimer;
	
	private ArrayList<Constellation> ConstellationDBList;
	private SkyTable SkySpace;

	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {

		Logger.log("onCreate");

		if (master != null) {
			copy(master);
		}
		
		//Create an OpenGL view for rendering the sky
		super.onCreate(savedInstanceState);
		renderer = new MyRenderer();
		mGLView = new GLSurfaceView(getApplication());
		mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});
		mGLView.setRenderer(renderer);
		mGLView.setZOrderMediaOverlay(true);
		

		RelativeLayout rl = new RelativeLayout(this);
	    rl.addView(mGLView);   
	    
	  //Add the loading text
	    ld = new TextView(this);
	    RelativeLayout.LayoutParams LD = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    LD.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    LD.addRule(RelativeLayout.CENTER_VERTICAL);
	    ld.setLayoutParams(LD);
	    ld.setId(22);
	    ld.setText("Loading, please wait.");
	    ld.setTextColor(Color.WHITE);
	    rl.addView(ld);
	    
	    
		//Add the zoom out button
	    Button zoomout = new Button(this);
	    RelativeLayout.LayoutParams ZO = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    ZO.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    ZO.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    zoomout.setId(1);
	    zoomout.setLayoutParams(ZO);
	    zoomout.setText("Zoom Out");
	    zoomout.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	if(!loadingFlag && ZoomLevel > 0)
	        	{
		        	TextView textView = (TextView) findViewById(12345);
		        	ZoomLevel -= 10;
		            textView.setText("Zoom: " + ZoomLevel);
		        	cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);
	        	}
	        }
	    });
	    zoomout.setOnTouchListener(new OnTouchListener()
	    {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        	if(!loadingFlag && ZoomLevel > 0 && System.currentTimeMillis() - ZoomTimer > 50)
		        	{
			        	TextView textView = (TextView) findViewById(12345);
			        	ZoomLevel -= 10;
			            textView.setText("Zoom: " + ZoomLevel);
			        	cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);
			        	ZoomTimer = System.currentTimeMillis ();
		        	}
				return true;
			}
	    });
	    rl.addView(zoomout);
		
	  //Add the zoom in button
	    Button zoomin = new Button(this);
	    RelativeLayout.LayoutParams ZI = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    ZI.addRule(RelativeLayout.ALIGN_LEFT, zoomout.getId());
	    ZI.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    ZI.addRule(RelativeLayout.ABOVE, zoomout.getId());
	    zoomin.setId(2);
	    zoomin.setLayoutParams(ZI);
	    zoomin.setText("Zoom In");
	    zoomin.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	if(!loadingFlag && ZoomLevel < 300)
	        	{
		        	TextView textView = (TextView) findViewById(12345);
		        	ZoomLevel += 10;
		            textView.setText("Zoom: " + ZoomLevel);
		        	cam.moveCamera(Camera.CAMERA_MOVEIN, 10);
	        	}
	        }
	    });
	    zoomin.setOnTouchListener(new OnTouchListener()
	    {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        	if(!loadingFlag && ZoomLevel < 300 && System.currentTimeMillis() - ZoomTimer > 50)
		        	{
			        	TextView textView = (TextView) findViewById(12345);
			        	ZoomLevel += 10;
			            textView.setText("Zoom: " + ZoomLevel);
			        	cam.moveCamera(Camera.CAMERA_MOVEIN, 10);
			        	ZoomTimer = System.currentTimeMillis ();
		        	}
				return true;
			}
	    });
	    rl.addView(zoomin);
	    
		//Add the zoom text
	    TextView tv = new TextView(this);
	    tv.setId(12345);
	    RelativeLayout.LayoutParams ZT = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    ZT.addRule(RelativeLayout.ABOVE, zoomin.getId());
	    ZI.addRule(RelativeLayout.ALIGN_LEFT, zoomout.getId());
	    ZT.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    tv.setLayoutParams(ZT);
	    tv.setText("Zoom: " + ZoomLevel);
	    tv.setTextColor(Color.GREEN);
	    rl.addView(tv);
	    
	  //Add the Constellation selection text
	    TextView CS = new TextView(this);
	    CS.setId(23456);
	    RelativeLayout.LayoutParams CSP = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    CSP.addRule(RelativeLayout.ALIGN_TOP);
	    CSP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    CSP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    CS.setLayoutParams(CSP);
	    CS.setText("Selection: (none)");
	    CS.setTextColor(Color.WHITE);
	    CS.setTextSize(20);
	    rl.addView(CS);
	   
	  //Add the Read Story button
	    Button RS = new Button(this);
	    RelativeLayout.LayoutParams RSP = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    RSP.addRule(RelativeLayout.BELOW, CS.getId());
	    RSP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    RS.setId(3);
	    RS.setLayoutParams(RSP);
	    RS.setText("Read Story");
	    RS.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	
	        }
	    });
	    rl.addView(RS);
	    
	    //Add the Go to Library button
	    Button LB = new Button(this);
	    RelativeLayout.LayoutParams LBP = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    LBP.addRule(RelativeLayout.BELOW, CS.getId());
	    LBP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    LB.setId(3);
	    LB.setLayoutParams(LBP);
	    LB.setText("Go to Library");
	    LB.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	
	        }
	    });
	    rl.addView(LB);
	    
	    //Remove title bar from my device
	    //requestWindowFeature(Window.FEATURE_NO_TITLE); 
	    
	    //Pull and set up constellation info
	    ConstellationDBList = new ArrayList<Constellation>();
	    
	    setContentView(rl);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void copy(Object src) {
		try {
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean onTouchEvent(MotionEvent me) {
		if(!loadingFlag)
		{
			if(loadingText)
			{
				ld.setVisibility(View.GONE);
				loadingText = false;
			}
		
		//Event manager for touchscreen presses
		TextView textView = (TextView) findViewById(12345);
		TextView selectionText = (TextView) findViewById(23456);
		
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			//Id1 = me.getActionIndex();
			xpos = me.getX(0);
			ypos = me.getY(0);
			
			SimpleVector dir = Interact2D.reproject2D3DWS(cam, fb, (int)Math.round(xpos), (int)Math.round(ypos)-60).normalize();
			Object[] res = world.calcMinDistanceAndObject3D(cam.getPosition(), dir, 1000 /*or whatever*/);
			
			//Text change events
            if(res[1] == null)
            {
            	ConstellationID = "null";
            	selectionText.setText("Selection: (none)");
            	//textView.setText("null");
            }
            else
            {
            	ConstellationID = ((Object3D) res[1]).getName();
            	selectionText.setText("Selection: " + ConstellationID);
            	//textView.setText(ConstellationID);
            }
			
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			touchTurn = 0;
			touchTurnUp = 0;
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			//If only one finger press, me.getX(1) == me.getX(0)
			x2pos = me.getX(1);
			y2pos = me.getY(1);
			/*float xdist = x2pos - xpos;
			float ydist = y2pos - ypos;
			int totaldist = (int)Math.sqrt(xdist*xdist + ydist*ydist);
			//textView.setText("xdist = " + xdist + "\nydist = " + ydist);
			//if the totaldist < 150, we are guaranteed that there are two finger presses detected. Thus we can perform zoom and pan.
			if(totaldist < 150)
			{
				//textView.setText("Distance = too small");
				totaldist = 150;
			}
			else
			{
				//textView.setText("Distance = " + totaldist);
			}
			int deltadist = totaldist - prevdist;
			deltadist = (int)Math.round(deltadist/1f);
			
				/*ZoomLevel += deltadist;
	            textView.setText("Zoom: \n" + ZoomLevel);
	        	cam.moveCamera(Camera.CAMERA_MOVEIN, deltadist);
			
			
        	
			prevdist = totaldist;*/
			/*
			if(mode == "zoom")
			{
				if(ZoomLevel < ZoomCap)
				{
					//TextView textView = (TextView) findViewById(12345);
		        	ZoomLevel += 1;
		            textView.setText("Zoom: \n" + ZoomLevel);
		        	cam.moveCamera(Camera.CAMERA_MOVEIN, 1);
				}
				return true;
			}
			*/
			//else if
			if(mode == "drag")
			{
				float xd = me.getX() - xpos;
				float yd = me.getY() - ypos;
				xpos = me.getX();
				ypos = me.getY();
				touchTurn = xd / -100f;
				touchTurnUp = yd / -100f;
			}
			
			return true;
		}
		/*
		if(me.getAction() == MotionEvent.ACTION_POINTER_DOWN)
		{
			Id2 = me.getActionIndex();
			float xd = me.getX(Id2);
			float yd = me.getY(Id2);
			float xdist = xd - xpos;
			float ydist = yd - ypos;
			mode = "zoom";
			if(xdist*xdist + ydist*ydist > 10)
			{
				//mode = "zoom";
			}
			return true;
		}
		
		if(me.getAction() == MotionEvent.ACTION_POINTER_UP)
		{
			mode = "drag";
			return true;
		}*/
		
		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// No need for this...
		}
		}
		return super.onTouchEvent(me);
	}
	
	protected boolean isFullscreenOpaque() {
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.login_menu,  menu);
		return true;
	}
		
		class MyRenderer implements GLSurfaceView.Renderer {

		private long time = System.currentTimeMillis();

		public MyRenderer() {
		}

		public void DrawConstellation(Constellation Con, World Sky)
		{
			// 0,0 coordinate on the sky
			SimpleVector Origin = new SimpleVector(0,0,500);
			
			//Retrieve constellation information
			ArrayList<FloatPair> StarList = new ArrayList<FloatPair>();
			ArrayList<IntPair> LineList = new ArrayList<IntPair>();
			LineList = Con.getLines();
			StarList = Con.getStars();
			
			//List of all objects created for constellation
			ArrayList<SimpleVector> StarVectors = new ArrayList<SimpleVector>(StarList.size());
			
			//Find rotation values for empty sky location
			IntPair RotValues = SkySpace.FindFirstEmptySlot();
			//Log.i("SKYSPACE", RotValues.first + " " + RotValues.second);
			int yAxisRotation = SkySpace.GetXrot(RotValues.first, RotValues.second);
			int xAxisRotation = SkySpace.GetYrot(RotValues.first, RotValues.second);
			
			//Build the Constellation clickable
			SimpleVector Point = new SimpleVector(0, 0, 450);
			Object3D Const = Primitives.getSphere(50);
			Const.calcTextureWrapSpherical();
			Const.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			Const.setTexture("SkyTexture");
			Const.setCulling(false);
			Const.setName(Con.getId());
			Const.strip();
			Const.build();
			Sky.addObject(Const);
			Point.rotateX((float)yAxisRotation * 3.1465f/180);
			Point.rotateY((float)xAxisRotation * 3.1465f/180);
			Const.setOrigin(Point);
			Const.setTransparency(0);
			Const.setLighting(Object3D.LIGHTING_NO_LIGHTS);
			
			//Builds the stars 
			for(int i = 0; i < StarList.size(); i++)
			{
				//Draw the star at the origin+location
				float x = Origin.x + StarList.get(i).first*50;
				float y = Origin.y + StarList.get(i).second*50;
				Point = new SimpleVector(x, y, 450);
				
				//Build and place star
				Object3D Star = Primitives.getSphere(10);
				Star.calcTextureWrapSpherical();
				Star.setTexture("stars");
				Star.setCulling(false);
				Star.setName(Con.getId());
				Star.strip();
				Star.build();
				Sky.addObject(Star);
				Point.rotateX((float)yAxisRotation * 3.1465f/180);
				Point.rotateY((float)xAxisRotation * 3.1465f/180);
				Star.setOrigin(Point);
				StarVectors.add(Point);
			}
            
			//Build the lines
			for(int i = 0; i < LineList.size(); i++)
			{
				SimpleVector[] LineTestPts = new SimpleVector[2];
				IntPair Members = LineList.get(i);
				LineTestPts[0] = StarVectors.get(Members.first);
				LineTestPts[1] = StarVectors.get(Members.second);
				RGBColor newcolor = new RGBColor(255,255,0);
				Polyline testline = new Polyline(LineTestPts, newcolor);
				testline.setWidth(2.5f);
				Sky.addPolyline(testline);
			}
		}
		
		public void BuildStars(World Sky)
		{
			//Get all of the constellations and iterate through them to draw
			
			//For each constellation, call DrawConstellation
			
			//Testcase code
			Constellation TestCase = new Constellation();
			IntPair tempI = new IntPair(0, 0);
			FloatPair tempF = new FloatPair(0f, 0f);
			TestCase.setId("Test constellation");
			TestCase.addStar(tempF);
			tempF = new FloatPair(.5f,.5f);
			TestCase.addStar(tempF);
			tempF = new FloatPair(.5f,-.5f);
			TestCase.addStar(tempF);
			tempF = new FloatPair(-.5f,.5f);
			TestCase.addStar(tempF);
			tempF = new FloatPair(-.5f,-.5f);
			TestCase.addStar(tempF);
			tempF = new FloatPair(-1f,-0f);
			TestCase.addStar(tempF);
			tempF = new FloatPair(1f,-0f);
			TestCase.addStar(tempF);
			
			tempI = new IntPair(0, 1);
			TestCase.addLine(tempI);
			tempI = new IntPair(1, 2);
			TestCase.addLine(tempI);
			tempI = new IntPair(2, 3);
			TestCase.addLine(tempI);
			tempI = new IntPair(3, 4);
			TestCase.addLine(tempI);
			tempI = new IntPair(4, 0);
			TestCase.addLine(tempI);
			tempI = new IntPair(5, 6);
			TestCase.addLine(tempI);
			tempI = new IntPair(3, 5);
			TestCase.addLine(tempI);
			tempF = null;
			tempI = null;
			for(int i = 0; i < 3; i++)
			{//15 is cap
				ConstellationDBList.add(TestCase);
			}
		
			for(int i = 0; i < ConstellationDBList.size(); i++)
			{
				DrawConstellation(ConstellationDBList.get(i), Sky);
			}
		}
		
		public void onSurfaceChanged(GL10 gl, int w, int h) {
			if (fb != null) {
				fb.dispose();
			}
			fb = new FrameBuffer(gl, w, h);

			if (master == null) {
				//Set up the world
				world = new World();
				world.setAmbientLight(100, 100, 100);
				world.setClippingPlanes(1, 100000);

				sun = new Light(world);
				sun.setIntensity(250, 250, 250);

				//Create textures
				Texture SkyTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.sky)), 64, 64));
				TextureManager.getInstance().addTexture("SkyTexture", SkyTexture);
				Texture StarTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.startexture)), 64, 64));
				TextureManager.getInstance().addTexture("stars", StarTexture);

				sky = Primitives.getSphere(500);
				sky.calcTextureWrapSpherical();
				sky.setTexture("SkyTexture");
				sky.setCulling(false);
				sky.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
				sky.strip();
				sky.build();
				
				SkySpace = new SkyTable();
				BuildStars(world);
				
				world.addObject(sky);
				
				//Set up camera
				cam = world.getCamera();
				cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);
				cam.lookAt(sky.getTransformedCenter());
				cam.moveCamera(Camera.CAMERA_MOVEIN, 10);
				
				//Position lighting
				SimpleVector sv = new SimpleVector();
				sv.set(sky.getTransformedCenter());
				sv.y -= 000;
				sv.z -= 1000;
				sun.setPosition(sv);
				MemoryHelper.compact();

				if (master == null) {
					Logger.log("Saving master Activity!");
					master = Skyview.this;
				}
			}
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			//Necessary empty function
			
		}

		public void onDrawFrame(GL10 gl) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
			
			//Screen Rotation Constant. This affects how fast the camera moves when sliding a touch across the screen.
			float SRC = (-1f/3f) * (Math.max(.1f, (500f-ZoomLevel)/500f));

			//If a meaningful touch event occurred, rotate the sky object
			if (touchTurn != 0) {
				cam.moveCamera(Camera.CAMERA_MOVEOUT, ZoomLevel);
				cam.rotateY(touchTurn*SRC);
				cam.moveCamera(Camera.CAMERA_MOVEIN, ZoomLevel);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
				cam.moveCamera(Camera.CAMERA_MOVEOUT, ZoomLevel);
				cam.rotateX(touchTurnUp*SRC);
				cam.moveCamera(Camera.CAMERA_MOVEIN, ZoomLevel);
				touchTurnUp = 0;
			}
			fb.clear(back);
			world.renderScene(fb);
			world.draw(fb);
			fb.display();
			//Enable button presses
			loadingFlag = false;

			if (System.currentTimeMillis() - time >= 1000) {
				Logger.log(fps + "fps");
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
		}
	}
}