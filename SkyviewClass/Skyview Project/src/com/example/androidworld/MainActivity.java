package com.example.androidworld;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

public class MainActivity extends Activity  implements SensorEventListener{

	// Used to handle pause and resume...
	private static MainActivity master = null;

	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	private FrameBuffer fb = null;
	private World world = null;
	private RGBColor back = new RGBColor(50, 50, 100);

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	private Object3D cube = null;
	private int fps = 0;

	private Light sun = null;
	
	private int testingInt = 0;
	private SensorManager mSensorManager;
	private Sensor mSensor;

	protected void onCreate(Bundle savedInstanceState) {

		Logger.log("onCreate");

		if (master != null) {
			copy(master);
		}
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
		    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		  }

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
	    TextView tv = new TextView(this);
	    tv.setId(12345);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    lp.addRule(RelativeLayout.ALIGN_TOP);
	    tv.setLayoutParams(lp);
	    tv.setText("TEST: " + testingInt);
	    tv.setTextColor(Color.WHITE);
	    rl.addView(tv);
	    
	    Button mybutton = new Button(this);
	    RelativeLayout.LayoutParams bp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    bp.addRule(RelativeLayout.CENTER_VERTICAL);
	    mybutton.setLayoutParams(bp);
	    mybutton.setText("Button!");
	    tv.setTextColor(Color.WHITE);
	    mybutton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	TextView textView = (TextView) findViewById(12345);
	        	testingInt++;
	            textView.setText("TEST: \n" + testingInt);
	        }
	    });
	    
	    rl.addView(mybutton);
	    setContentView(rl);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
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

	public void onSensorChanged(SensorEvent event)
    {
         // alpha is calculated as t / (t + dT)
         // with t, the low-pass filter's time-constant
         // and dT, the event delivery rate

         final float alpha = (float) 0.8;

         float[]  gravity = new float[3];
		 gravity [0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
         gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
         gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

         float[] linear_acceleration = new float[3];
		 linear_acceleration[0] = event.values[0] - gravity[0];
         linear_acceleration[1] = event.values[1] - gravity[1];
         linear_acceleration[2] = event.values[2] - gravity[2];
         TextView textView = (TextView) findViewById(12345);
         textView.setText("value A:" + linear_acceleration[0] + "\n" +
        		 "value B:" + linear_acceleration[1] + "\n" +
        		 "value C:" + linear_acceleration[2]);
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

				world = new World();
				world.setAmbientLight(0, 0, 0);

				sun = new Light(world);
				sun.setIntensity(250, 250, 250);

				// Create a texture out of the icon...:-)
				Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.dots)), 64, 64));
				TextureManager.getInstance().addTexture("texture", texture);

				cube = Primitives.getSphere(50);
				cube.calcTextureWrapSpherical();
				cube.setTexture("texture");
				cube.setCulling(false);
				cube.strip();
				cube.build();

				world.addObject(cube);

				Camera cam = world.getCamera();
				cam.moveCamera(Camera.CAMERA_MOVEOUT, -10);
				cam.lookAt(cube.getTransformedCenter());

				SimpleVector sv = new SimpleVector();
				sv.set(cube.getTransformedCenter());
				sv.y -= 000;
				sv.z -= 100;
				sun.setPosition(sv);
				MemoryHelper.compact();

				if (master == null) {
					Logger.log("Saving master Activity!");
					master = MainActivity.this;
				}
			}
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}

		public void onDrawFrame(GL10 gl) {
			if (touchTurn != 0) {
				cube.rotateY(-touchTurn/3);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
				cube.rotateX(touchTurnUp/3);
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

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		//Function to adjust sensor if the accuracy si compromised.
	}
	
	
}