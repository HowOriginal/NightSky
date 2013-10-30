package com.example.androidworld;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;

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

public class GameSurfaceView extends GLSurfaceView {
	 
   // private GLRenderer glRenderer;
 
    public GameSurfaceView(Context context, AttributeSet atts) {
        super(context, atts);
        // TODO Auto-generated method stub
       //init(context);
    }
 
    public GameSurfaceView(Context context) {
        super(context);
        // TODO Auto-generated method stub
        //init(context);
    }
    
    
    /*
    private void init(Context context) {
        // TODO Auto-generated method stub
        glRenderer = new GLRenderer(context);
        setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(glRenderer);
    }*/
}