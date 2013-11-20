package com.example.constbuildertest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class ConstructorView extends View {
    Paint paint = new Paint();
    private Constellation con = new Constellation();
    private int startX,startY,stopX,stopY,screenwidth,screenheight;

    @SuppressLint("NewApi")
	public ConstructorView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenwidth = size.x;
        screenheight = size.y;
    }

    //Drawing constellation is implemented here
    @Override
    public void onDraw(Canvas canvas) {
    	IntPair ip;
    	FloatPair fp, fp2;
    	for (int i=0; i<con.numStars(); ++i) {
    		fp = con.getStar(i);
    		canvas.drawCircle(fp.first*screenwidth, fp.second*screenheight, 10, paint);
    	}
    	for (int i=0; i<con.numLines(); ++i) {
    		ip = con.getLine(i);
    		fp = con.getStar(ip.first);
    		fp2 = con.getStar(ip.second);
    		canvas.drawLine(fp.first*screenwidth, fp.second*screenheight, fp2.first*screenwidth, fp2.second*screenheight, paint);
    	}
    	canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
    
    //Touch logic, basically everything is calculated on ACTION_UP
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN:
    		startX = (int) event.getX();
    		startY = (int) event.getY();
    		return true;
    	case MotionEvent.ACTION_MOVE:
    		stopX = (int)event.getX();
    		stopY = (int)event.getY();
    		break;
    	case MotionEvent.ACTION_UP:    
    		stopX = (int)event.getX();
    		stopY = (int)event.getY();
    		int dis = calculateDistance(startX,startY,stopX,stopY);
    		IntPair startstar = findClosestStar(startX,startY);
    		IntPair stopstar = findClosestStar(stopX,stopY);
    		
    		//Create or delete a star if the user does not drag a outside a star
    		if (dis < 20) {
    			//If no star exists at this location, add a new one
    			if (startstar.first==-1 || startstar.second>20) {
    				con.addStar(new FloatPair((float)startX/screenwidth,(float)startY/screenheight));
    			}
    			//If there is a star, delete it
    			else if (startstar.second<=20) {
    				con.deleteStar(startstar.first);
    			}
    		}
    		
    		//If the user drags a line from a star, either move it or create a connection
    		else {
    			if (startstar.first!=-1 && startstar.second<=20) {
    				//If a star exists near where the user lifted, draw a connection
    				if (stopstar.first!=-1 && stopstar.second<=20) {
    					con.addLine(new IntPair(startstar.first,stopstar.first));
    				}
    				//If no star exists at the end point, move the star there
    				else {
    					con.setStar(startstar.first, new FloatPair((float)stopX/screenwidth,(float)stopY/screenheight));
    				}
    			}
    		}
    		stopX = startX;
    		stopY = startY;
    		break;
    	default:
    		return false;
    	}
    	
    	//Redraw
    	invalidate();
    	return true;
    }
    
    //Return the distance between two points
    private int calculateDistance(float x1, float y1, float x2, float y2) {
    	double result = (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
    	result = Math.sqrt(result);
    	return (int) result;
    }
    
    //Returns an IntPair where:
    //	first is the index of the closest star
    //	second is the distance to the star from x1,y1
    private IntPair findClosestStar(int x1, int y1) {
    	FloatPair fp;
    	int closest=-1,closestdis=-1,dis;
    	for (int i=0; i<con.numStars(); ++i) {
    		fp = con.getStar(i);
    		dis = calculateDistance(x1,y1,fp.first*screenwidth,fp.second*screenheight);
    		if (dis < closestdis || closestdis==-1) {
    			closestdis=dis;
    			closest = i;
    		}
    	}
    	return new IntPair(closest, closestdis);
    }

}