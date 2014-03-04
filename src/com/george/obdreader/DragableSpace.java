package com.george.obdreader;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Scroller;

public class DragableSpace extends ViewGroup {
	private Scroller mScroller;   
    private VelocityTracker mVelocityTracker;
	private int mScrollX = 0;     
    private int mScrollY = 0;      
    private int mCurrentScreen = 0;   
	private float mLastMotionX;    	     
    private float mLastMotionY;      
    private static final boolean debug = false;
    private static final String LOG_TAG = "DragableSpace";  
    private boolean bIsXOrientation = true;
    
    private static final int SNAP_VELOCITY = 1000;   
    private final static int TOUCH_STATE_REST = 0;   
    private final static int TOUCH_STATE_SCROLLING = 1;   
  
    private int mTouchState = TOUCH_STATE_REST;   
    private int mTouchSlop = 0;   
  
    public DragableSpace(Context context) {   
        super(context);   
        mScroller = new Scroller(context);    
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();  
        if(bIsXOrientation == true){
        	this.setLayoutParams(new ViewGroup.LayoutParams(   
                    ViewGroup.LayoutParams.WRAP_CONTENT,   
                    ViewGroup.LayoutParams.FILL_PARENT));
        }else{
        	this.setLayoutParams(new ViewGroup.LayoutParams(   
                    ViewGroup.LayoutParams.FILL_PARENT,   
                    ViewGroup.LayoutParams.WRAP_CONTENT));   
        }
    }   
  
    public DragableSpace(Context context, AttributeSet attrs) {   
        super(context, attrs);   
        mScroller = new Scroller(context);   	  
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();   
        
        TypedArray a=getContext().obtainStyledAttributes(attrs,R.styleable.DragableSpace);   
        mCurrentScreen = a.getInteger(R.styleable.DragableSpace_default_screen, 0);  
        bIsXOrientation= a.getBoolean(R.styleable.DragableSpace_xorientation,true); 
        
        if(debug == true)Log.i(LOG_TAG,this.hashCode()+ "DragableSpace current screen "+
        		mCurrentScreen+",bIsXOrientation="+bIsXOrientation); 
        
  		if(bIsXOrientation == true){
   			this.setLayoutParams(new ViewGroup.LayoutParams(   
        		ViewGroup.LayoutParams.WRAP_CONTENT,   
                ViewGroup.LayoutParams.FILL_PARENT)); 
  		}else{
        	this.setLayoutParams(new ViewGroup.LayoutParams(   
        		ViewGroup.LayoutParams.FILL_PARENT,   
                ViewGroup.LayoutParams.WRAP_CONTENT));   
		}
    }   
    
    public interface ICallback {
    	public void callback(int current, int total);
    }
    
    private ICallback mCallback = null;
    public void setCallBack(ICallback callback){
    	mCallback = callback;
    }
  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {   
    	 final int action = ev.getAction();      
         if ((action == MotionEvent.ACTION_MOVE)      
             && (mTouchState != TOUCH_STATE_REST)) {      
             return true;      
         }      
      
	     final float x = ev.getX();  
         final float y = ev.getY();
         switch (action) {      
             case MotionEvent.ACTION_MOVE:    
			 	if(bIsXOrientation == true){    
					 final int xDiff = (int) Math.abs(x - mLastMotionX);      
	                 boolean xMoved = xDiff > mTouchSlop;      
	                 if (xMoved) {      
	                     // Scroll if the user moved far enough along the X axis      
	                     mTouchState = TOUCH_STATE_SCROLLING;      
	                 } 
				}else{     
	                 final int yDiff = (int) Math.abs(y - mLastMotionY);      
	                 boolean yMoved = yDiff > mTouchSlop;      
	                 if (yMoved) {      
	                     // Scroll if the user moved far enough along the X axis      
	                     mTouchState = TOUCH_STATE_SCROLLING;      
	                 }  
				 }    
                 break;      
      
             case MotionEvent.ACTION_DOWN:      
                 // Remember location of down touch  
				 if(bIsXOrientation == true){ 
				 	 mLastMotionX = x;  
				 }else{    
                	 mLastMotionY = y;     
				 }    
                 mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;      
                 break;      
      
             case MotionEvent.ACTION_CANCEL:      
             case MotionEvent.ACTION_UP:      
                 // Release the drag      
                 mTouchState = TOUCH_STATE_REST;      
                 break;      
         }      

         return mTouchState != TOUCH_STATE_REST;    
    }   
	  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {     
    	 if (mVelocityTracker == null) {      
             mVelocityTracker = VelocityTracker.obtain();      
         }      
         mVelocityTracker.addMovement(event);      
         mScrollX = this.getScrollX();
         mScrollY = this.getScrollY(); 
         
         final int action = event.getAction();   
		 final float x = event.getX();    
         final float y = event.getY();          
         switch (action) {      
             case MotionEvent.ACTION_DOWN:      
//                 Log.i(LOG_TAG, "event : down");      	                   
                 if (!mScroller.isFinished()) {      
                     mScroller.abortAnimation();      
                 }      
    
                 // Remember where the motion event started      
				 mLastMotionX = x; 
                 mLastMotionY = y;      
                 break;     
                 
             case MotionEvent.ACTION_MOVE:      
//                 Log.i(LOG_TAG,"event : move");      
//                 if (mTouchState == TOUCH_STATE_SCROLLING) {      
//                 Scroll to follow the motion event      
				   final int deltaX = (int) (mLastMotionX - x);    
                   final int deltaY = (int) (mLastMotionY - y);  
				   mLastMotionX = x;     
                   mLastMotionY = y;      
                   if(debug == true)Log.i(LOG_TAG, "event : move, deltaX " + deltaX + ", mScrollX " + mScrollX+
                		", deltaY " + deltaY + ", mScrollY " + mScrollY);              
				   if(bIsXOrientation == true){
						if (deltaX < 0) {      
		                     if (mScrollX > 0) {      
		                         scrollBy(Math.max(-mScrollX, deltaX), 0);      
		                     }      
		                 } else if (deltaX > 0) {      
		                     final int availableToScroll = getChildAt(getChildCount() - 1)      
		                         .getRight()      
		                         - mScrollX - getWidth();      
		                     if (availableToScroll > 0) {      
		                         scrollBy(Math.min(availableToScroll, deltaX), 0);      
		                     }      
		                 }
					}else{   	 
	                 	if (deltaY <= 0) {    
		                     if (mScrollY > 0) {      
		                         scrollBy(0,Math.max(-mScrollY, deltaY));      
		                     }else{
//		                    	 scrollBy(0,getChildAt(getChildCount() - 1).getBottom()); 
//		                    	 snapToScreen(getChildCount()); 
		                    	 final VelocityTracker velocityTracker = mVelocityTracker;      
		                         velocityTracker.computeCurrentVelocity(1000); 
		                    	 int velocityY = (int) velocityTracker.getYVelocity(); 
		                    	 if(velocityY>100){
		                    		 setToScreen(getChildCount()-1);
		                    	 }
		                     }      
	                 	} else if (deltaY > 0) {      
		                     final int availableToScroll = getChildAt(getChildCount() - 1).getBottom()      
		                         - mScrollY - getHeight();  
		                     if(debug == true)Log.i(LOG_TAG, "event : move, availableToScroll " + availableToScroll + 
		                    		 ", getBottom() " + getChildAt(getChildCount() - 1).getBottom()+
		                			 ", deltaY " + deltaY + ", mScrollY " + mScrollY); 
		                     if(availableToScroll <= 0){
		                    	 mScrollY = 0;
		                    	 final VelocityTracker velocityTracker = mVelocityTracker;      
		                         velocityTracker.computeCurrentVelocity(1000); 
		                    	 int velocityY = (int) velocityTracker.getYVelocity();
		                    	 if(debug == true)Log.i(LOG_TAG, "event : velocityY "+velocityY);
		                    	 if(velocityY<-100){
		                    		 setToScreen(0);
		                    	 }
		                     }else if (availableToScroll > 0) {      
		                         scrollBy(0,Math.min(availableToScroll, deltaY));      
		                     }      
	                 }    
				 }       
                 break;   
                 
             case MotionEvent.ACTION_UP:
            	 if(debug == true)Log.i(LOG_TAG, "event : up");
                 // if (mTouchState == TOUCH_STATE_SCROLLING) {      
                 final VelocityTracker velocityTracker = mVelocityTracker;      
                 velocityTracker.computeCurrentVelocity(1000);      
				 int velocityX = (int) velocityTracker.getXVelocity();
                 int velocityY = (int) velocityTracker.getYVelocity();      
	      		if(bIsXOrientation == true){
					 if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {      
	                     // Fling hard enough to move left      
	                     snapToScreen(mCurrentScreen - 1);      
	                 } else if (velocityX < -SNAP_VELOCITY      
	                         && mCurrentScreen < getChildCount() - 1) {      
	                     // Fling hard enough to move right      
	                     snapToScreen(mCurrentScreen + 1);      
	                 } else {      
	                     snapToDestination();      
	                 } 
				 }else{
	                 if (velocityY > SNAP_VELOCITY) {      
	                     // Fling hard enough to move left  
	                	 if(mCurrentScreen > 0){
	                		 snapToScreen(mCurrentScreen - 1);   
	                	 }else{
	                		 snapToScreen(getChildCount());
	                	 }
	                 } else if (velocityY < -SNAP_VELOCITY) {      
	                     // Fling hard enough to move right    
	                	 if(mCurrentScreen < getChildCount() - 1){
	                		 snapToScreen(mCurrentScreen + 1); 
	                	 }else{
	                		 snapToScreen(0);
	                	 }
	                 } else {      
	                     snapToDestination(); 
	                 }     
				 } 
      
                 if (mVelocityTracker != null) {      
                     mVelocityTracker.recycle();      
                     mVelocityTracker = null;      
                 }         
                 mTouchState = TOUCH_STATE_REST;      
                 break;     
                 
             case MotionEvent.ACTION_CANCEL:   
            	 if(debug == true)Log.i(LOG_TAG, "event : cancel"); 
                 mTouchState = TOUCH_STATE_REST;    
                 break;
         }      
		          
         return true;   
    }   
  
    private void snapToDestination() {  
    	final int whichScreen;
    	if(bIsXOrientation == true){
			final int screenWidth = getWidth();      
	        whichScreen = (mScrollX + (screenWidth / 2)) / screenWidth; 
		}else{
	    	final int screenHeight = getHeight();      
	        whichScreen = (mScrollY + (screenHeight / 2)) / screenHeight;  
		}    
    	if(debug == true)Log.i(LOG_TAG, "from des whichScreen "+whichScreen+",mScrollY "+mScrollY);    
 
    	if(whichScreen >= getChildCount()){
    		snapToScreen(getChildCount()-1);
    	}else{
    		snapToScreen(whichScreen);  
    	}
    }   
  
    public void snapToScreen(int whichScreen) {     
        mCurrentScreen = whichScreen;      
		final int newX = whichScreen * getWidth();      
        final int delta;
        final int newY = whichScreen * getHeight();    
        if(debug == true)Log.i(LOG_TAG,this.hashCode()+ "snap To Screen " + whichScreen);

		if(bIsXOrientation == true){   
			delta = newX - mScrollX;
			mScroller.startScroll(mScrollX, 0, delta, 0, Math.abs(delta) * 2); 
		}else{
			delta = newY - mScrollY;
        	mScroller.startScroll(0,mScrollY, 0,delta,Math.abs(delta) * 2);  
		}                 
        invalidate();
        if(mCallback != null){
        	mCallback.callback(whichScreen,getChildCount());
        }
    }   

	public void setToScreen(int whichScreen) {   
		if(debug == true)Log.i(LOG_TAG,this.hashCode()+  "set To Screen " + 
				whichScreen+",bIsXOrientation="+bIsXOrientation);   
        mCurrentScreen = whichScreen;  
		final int newX = whichScreen * getWidth();  
        final int newY = whichScreen * getHeight();  
		if(bIsXOrientation == true){ 
			scrollTo(newX, 0);
		}else{
         	scrollTo(0,newY); 
		} 
        invalidate();   
    }   
	
	public int getCurrentScreen() {   
      return mCurrentScreen;   
	} 
  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) { 
    	if(debug == true)Log.i(LOG_TAG,this.hashCode()+ "onLayout screen "+mCurrentScreen +
          	",bIsXOrientation="+bIsXOrientation);
		int childLeft = 0;  
        int childTop = 0;      
        final int count = getChildCount();      
        for (int i = 0; i < count; i++) {      
        	final View child = getChildAt(i);      
            if (child.getVisibility() != View.GONE) { 
				if(bIsXOrientation == true){  
					final int childWidth = child.getMeasuredWidth();      
	                child.layout(childLeft, 0, childLeft + childWidth, child      
	                        .getMeasuredHeight());      
	                childLeft += childWidth;  
				}else{ 
	                final int childHeight = child.getMeasuredHeight();      
	                child.layout(0,childTop,child.getMeasuredWidth(), childTop + childHeight);      
	                childTop += childHeight;  
				}    
            }      
        }  
    }   
	  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
  
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);   
//        if (widthMode != MeasureSpec.EXACTLY) {   
//            throw new IllegalStateException("error mode.");   
//        }   
  
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);   
//        if (heightMode != MeasureSpec.EXACTLY) {   
//            throw new IllegalStateException("error mode.");   
//        }   
  
        // The children are given the same width and height as the workspace   
        final int count = getChildCount();   
        for (int i = 0; i < count; i++) {   
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);   
        }   
        if(debug == true)Log.i(LOG_TAG,this.hashCode()+ "moving to screen "+mCurrentScreen +
        	",bIsXOrientation="+bIsXOrientation);
		if(bIsXOrientation == true){ 
			scrollTo(mCurrentScreen * width, 0);
		}else{
         	scrollTo(0,mCurrentScreen * height); 
		}     
    }     
	  
    @Override  
    public void computeScroll() {   
    	if (mScroller.computeScrollOffset()) {  
			if(bIsXOrientation == true){   
				mScrollX = mScroller.getCurrX();      
	            scrollTo(mScrollX, 0); 
			}else{
	            mScrollY = mScroller.getCurrY();      
	            scrollTo(0,mScrollY);
			}      
            postInvalidate();      
        }   
    }   
    
    public static int getGestrueViewValue(int startId,int size,Window window){
		StringBuffer value=new StringBuffer();	
		for(int i = 0;i< size;i++){
			value.append(((DragableSpace)window.findViewById(startId+i)).getCurrentScreen());
		}
		return Integer.parseInt(value.toString());
	}
    
    public static void setGestrueViewValue(int startId,int size,String value,Window window){		
		for(int i = 0;i< size;i++){
			if(i<value.length()){
				((DragableSpace)window.findViewById(startId+size-1-i)).setToScreen(value.charAt(value.length()-1-i)-'0');
			}else{
				((DragableSpace)window.findViewById(startId+size-1-i)).setToScreen(0);
			}
		}
	}
}