package com.example.reviewiteration;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
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
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

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

	private Object3D sky = null;
	private int fps = 0;
	private Light sun = null;
	
	private int ZoomLevel = 150;
	private Camera cam;

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
		
		//Add the zoom text
		RelativeLayout rl = new RelativeLayout(this);
	    rl.addView(mGLView);        
	    TextView tv = new TextView(this);
	    tv.setId(12345);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    lp.addRule(RelativeLayout.ALIGN_TOP);
	    tv.setLayoutParams(lp);
	    tv.setText("Zoom: " + ZoomLevel);
	    tv.setTextColor(Color.GREEN);
	    rl.addView(tv);
	    
	    //Add the zoom in button
	    Button zoomin = new Button(this);
	    RelativeLayout.LayoutParams bp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    bp.addRule(RelativeLayout.CENTER_VERTICAL);
	    zoomin.setLayoutParams(bp);
	    zoomin.setText("Zoom In");
	    zoomin.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	if(ZoomLevel < 500)
	        	{
		        	TextView textView = (TextView) findViewById(12345);
		        	ZoomLevel += 10;
		            textView.setText("Zoom: \n" + ZoomLevel);
		        	cam.moveCamera(Camera.CAMERA_MOVEIN, 10);
	        	}
	        }
	    });
	    rl.addView(zoomin);
	    
	    //Add the zoom out button
	    Button zoomout = new Button(this);
	    bp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    bp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    zoomout.setLayoutParams(bp);
	    zoomout.setText("Zoom Out");
	    zoomout.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	if(ZoomLevel > 0)
	        	{
		        	TextView textView = (TextView) findViewById(12345);
		        	ZoomLevel -= 10;
		            textView.setText("Zoom: \n" + ZoomLevel);
		        	cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);
	        	}
	        }
	    });
	    rl.addView(zoomout);
	    
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
			xpos = me.getX();
			ypos = me.getY();
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
			float xd = me.getX() - xpos;
			float yd = me.getY() - ypos;

			xpos = me.getX();
			ypos = me.getY();

			touchTurn = xd / -100f;
			touchTurnUp = yd / -100f;
			return true;
		}

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

				//Create texture for sky
				Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.dots)), 64, 64));
				TextureManager.getInstance().addTexture("texture", texture);

				sky = Primitives.getSphere(500);
				sky.calcTextureWrapSpherical();
				sky.setTexture("texture");
				sky.setCulling(false);
				sky.strip();
				sky.build();

				world.addObject(sky);
				
				//Set up camera
				cam = world.getCamera();
				cam.moveCamera(Camera.CAMERA_MOVEOUT, -10);
				cam.lookAt(sky.getTransformedCenter());
				cam.moveCamera(Camera.CAMERA_MOVEOUT, 10);
				
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
			//Screen Rotation Constant. This affects how fast the camera moves when sliding a touch across the screen.
			float SRC = (1f/3f) * (Math.max(.1f, (500f-ZoomLevel)/500f));
			
			//If a meaningful touch event occurred, rotate the sky object
			if (touchTurn != 0) {
				sky.rotateY(-touchTurn*SRC);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
				sky.rotateX(touchTurnUp*SRC);
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