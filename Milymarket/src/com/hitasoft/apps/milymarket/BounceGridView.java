package com.hitasoft.apps.milymarket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.GridView;

public class BounceGridView extends GridView{
	 private static final int MAX_Y_OVERSCROLL_DISTANCE = 150;
	    
	    private Context mContext;
		private int mMaxYOverscrollDistance;
		
		public BounceGridView(Context context) 
		{
			super(context);
			mContext = context;
			initBounceListView();
		}
		
		public BounceGridView(Context context, AttributeSet attrs) 
		{
			super(context, attrs);
			mContext = context;
			initBounceListView();
		}
		
		public BounceGridView(Context context, AttributeSet attrs, int defStyle) 
		{
			super(context, attrs, defStyle);
			mContext = context;
			initBounceListView();
		}
		
		private void initBounceListView()
		{
			//get the density of the screen and do some maths with it on the max overscroll distance
			//variable so that you get similar behaviors no matter what the screen size
			
			final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
	        	final float density = metrics.density;
	        
			mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
		}
		
		@SuppressLint("NewApi")
		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) 
		{ 
			//This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance; 
			return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);  
		}
}
