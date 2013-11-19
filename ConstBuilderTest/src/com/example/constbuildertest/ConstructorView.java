package com.example.constbuildertest;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class ConstructorView extends View {
    Paint paint = new Paint();
    private Constellation con = new Constellation();
    private int startX,startY,stopX,stopY;

    public ConstructorView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
    }

    //Drawing constellation is implemented here
    @Override
    public void onDraw(Canvas canvas) {
    	IntPair ip, ip2, ip3;
    	for (int i=0; i<con.numStars(); ++i) {
    		ip = con.getStar(i);
    		canvas.drawCircle(ip.first, ip.second, 10, paint);
    	}
    	for (int i=0; i<con.numLines(); ++i) {
    		ip = con.getLine(i);
    		ip2 = con.getStar(ip.first);
    		ip3 = con.getStar(ip.second);
    		canvas.drawLine(ip2.first, ip2.second, ip3.first, ip3.second, paint);
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
    			if (startstar.first==-1 || startstar.second>40) {
    				con.addStar(new IntPair(startX,startY));
    			}
    			//If there is a star, delete it
    			else if (startstar.second<=40) {
    				con.deleteStar(startstar.first);
    			}
    		}
    		
    		//If the user drags a line from a star, either move it or create a connection
    		else {
    			if (startstar.first!=-1 && startstar.second<=40) {
    				//If a star exists near where the user lifted, draw a connection
    				if (stopstar.first!=-1 && stopstar.second<=40) {
    					con.addLine(new IntPair(startstar.first,stopstar.first));
    				}
    				//If no star exists at the end point, move the star there
    				else {
    					con.setStar(startstar.first, new IntPair(stopX,stopY));
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
    
    private int calculateDistance(int x1, int y1, int x2, int y2) {
    	double result = (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
    	result = Math.sqrt(result);
    	return (int) result;
    }
    
    //Returns an IntPair where:
    //	first is the index of the closest star
    //	second is the distance to the star from x1,y1
    private IntPair findClosestStar(int x1, int y1) {
    	IntPair ip;
    	int closest=-1,closestdis=-1,dis;
    	for (int i=0; i<con.numStars(); ++i) {
    		ip = con.getStar(i);
    		dis = calculateDistance(x1,y1,ip.first,ip.second);
    		if (dis < closestdis || closestdis==-1) {
    			closestdis=dis;
    			closest = i;
    		}
    	}
    	return new IntPair(closest, closestdis);
    }

}