package com.example.reviewiteration;

import java.lang.reflect.Field;
//import java.util.Vector;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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
	private int ZoomMin = 0;
	private float TouchDist = 0;
	//private int ZoomChange = 0;
	private String mode = "drag";
	private Camera cam;
	
	private int Id1;
	private int Id2;

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
	        	if(ZoomLevel > 0)
	        	{
		        	TextView textView = (TextView) findViewById(12345);
		        	ZoomLevel -= 10;
		            textView.setText("Zoom: " + ZoomLevel);
		        	cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);
	        	}
	        }
	    });
	    rl.addView(zoomout);
		
	  //Add the zoom in button
	    Button zoomin = new Button(this);
	    RelativeLayout.LayoutParams ZI = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    //ZI = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    ZI.addRule(RelativeLayout.ALIGN_LEFT, zoomout.getId());
	    ZI.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    //ZI.addRule(RelativeLayout.ALIGN_BOTTOM);
	    ZI.addRule(RelativeLayout.ABOVE, zoomout.getId());
	    zoomin.setId(2);
	    zoomin.setLayoutParams(ZI);
	    zoomin.setText("Zoom In");
	    zoomin.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	if(ZoomLevel < 300)
	        	{
		        	TextView textView = (TextView) findViewById(12345);
		        	ZoomLevel += 10;
		            textView.setText("Zoom: " + ZoomLevel);
		        	cam.moveCamera(Camera.CAMERA_MOVEIN, 10);
	        	}
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
		//Event manager for touchscreen presses
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			Id1 = me.getActionIndex();
			xpos = me.getX(Id1);
			ypos = me.getY(Id1);
			
			SimpleVector dir = Interact2D.reproject2D3DWS(cam, fb, (int)Math.round(xpos), (int)Math.round(ypos)-60).normalize();
			Object[] res = world.calcMinDistanceAndObject3D(cam.getPosition(), dir, 1000 /*or whatever*/);
			
			TextView textView = (TextView) findViewById(12345);
            
            if(res[1] == null)
            {
            	textView.setText("null");
            }
            else
            {
            	textView.setText(((Object3D) res[1]).getName());
            	//textView.setText((res[0]).toString() + "\n" + ((Object3D) res[1]).getName());
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

			//xpos = me.getX(0);
			//ypos = me.getY(0);
			//x2pos = me.getX(1);
			//y2pos = me.getY(1);
			
			//float xdist = x2pos - xpos;
			//float ydist = y2pos - ypos;
			
			if(mode == "zoom")
			{
				if(ZoomLevel < ZoomCap)
				{
					TextView textView = (TextView) findViewById(12345);
		        	ZoomLevel += 1;
		            textView.setText("Zoom: \n" + ZoomLevel);
		        	cam.moveCamera(Camera.CAMERA_MOVEIN, 1);
				}
				return true;
			}
			else if(mode == "drag")
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

		return super.onTouchEvent(me);
	}
	
	protected boolean isFullscreenOpaque() {
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
			ArrayList<IntPair> StarList = new ArrayList<IntPair>();
			ArrayList<IntPair> LineList = new ArrayList<IntPair>();
			LineList = Con.getLines();
			StarList = Con.getStars();
			
			//List of all objects created for constellation
			ArrayList<SimpleVector> StarVectors = new ArrayList<SimpleVector>(StarList.size());
			
			//Texture StarTexture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.ic_launcher)), 64, 64));
			//TextureManager.getInstance().addTexture("stars", StarTexture);
			
			//Build the Constellation clickable
			SimpleVector Point = new SimpleVector(0, 0, 450);
			Object3D Const = Primitives.getSphere(30);
			Const.calcTextureWrapSpherical();
			Const.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			Const.setTexture("SkyTexture");
			Const.setCulling(false);
			Const.setName(Con.getId());
			Const.strip();
			Const.build();
			Sky.addObject(Const);
			Point.rotateY(0.5f);
			Const.setOrigin(Point);
			Const.setTransparency(0);
			Const.setLighting(Object3D.LIGHTING_NO_LIGHTS);
			//Builds the stars 
			for(int i = 0; i < StarList.size(); i++)
			{
				//Draw the star at the origin+location
				float x = Origin.x + StarList.get(i).first;
				float y = Origin.y + StarList.get(i).second;
				Point = new SimpleVector(x, y, 450);
				//Apply rotation to origin+star position; for now none applicable
				
				//Build and place star
				Object3D Star = Primitives.getSphere(10);
				Star.calcTextureWrapSpherical();
				//Star.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
				Star.setTexture("stars");
				Star.setCulling(false);
				Star.setName(Con.getId());
				Star.strip();
				Star.build();
				Sky.addObject(Star);
				Point.rotateY(0.5f);
				Star.setOrigin(Point);
				StarVectors.add(Point);
			}
            
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
			TestCase.setId("Test constellation");
			TestCase.addStar(new IntPair(0,0));
			TestCase.addStar(new IntPair(20,20));
			TestCase.addStar(new IntPair(20,-20));
			TestCase.addStar(new IntPair(-20,20));
			TestCase.addStar(new IntPair(-20,-20));
			TestCase.addLine(new IntPair(0,1));
			TestCase.addLine(new IntPair(1,2));
			TestCase.addLine(new IntPair(2,3));
			TestCase.addLine(new IntPair(3,4));
			TestCase.addLine(new IntPair(0,4));
			DrawConstellation(TestCase, Sky);
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

			if (System.currentTimeMillis() - time >= 1000) {
				Logger.log(fps + "fps");
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
		}
	}
}