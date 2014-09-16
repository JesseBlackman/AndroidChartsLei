package com.androidchartslei;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import java.util.ArrayList;
import com.androidchartslei.LineView.Dot;
//import com.datepicker.DatePicker.DeliverDateListener;


/**
 * Created by Dacer on 11/11/13.
 * Edited by Lei gang on 16/9/14
 */
public class BarView extends View {
    private ArrayList<Float> percentList;
    private ArrayList<Float> targetPercentList;
    private ArrayList<Float> AllpercentList;
    private ArrayList<Float> AlltargetPercentList;
    private Paint textPaint;
    private Paint BlankNumPaint;
    private Paint AllNumPaint;
    private Paint bgPaint;
    private Paint fgPaint;
    private Paint AllfgPaint;
    private Rect rect;
    private int barWidth;
//    private boolean showSideMargin = true;
    private int bottomTextDescent;
    private boolean autoSetWidth = true;
    private int topMargin;
    private int bottomTextHeight;
    
    private int clickarea;            //点击坐标轴上的区域的数值
    public boolean clickaarea = false; 
    public String clickroom = null;  
    
    private ArrayList<String> bottomTextList;
    private ArrayList<String> BlankSeatList;
    private ArrayList<String> AllSeatList;
    private final int MINI_BAR_WIDTH;
    private final int BAR_SIDE_MARGIN;
    private final int TEXT_TOP_MARGIN;
    private final int TEXT_COLOR = Color.parseColor("#9B9A9B");
    private final int NUM_COLOR = Color.parseColor("#1E90FF");
    private final int BACKGROUND_COLOR = Color.parseColor("#F6F6F6");
    private final int FOREGROUND_COLOR = Color.parseColor("#00FF7F");
    private final int AllFOREGROUND_COLOR = Color.parseColor("#AFEEEE");
    
    public interface DeliverRoomListener {
		public void DeliverRoom(String classname);
	}
	
	private DeliverRoomListener mDeliverRoomListener;
//	
	public void setRoomClickedListener(DeliverRoomListener listener){
		mDeliverRoomListener = listener;
    }
    
        
    private Runnable animator = new Runnable() {
        @Override
        public void run() {
                boolean needNewFrame = false;
                for (int i=0; i<targetPercentList.size();i++) {
                    if (percentList.get(i) < targetPercentList.get(i)) {
                        percentList.set(i,percentList.get(i)+0.02f);
                        needNewFrame = true;
                    } else if (percentList.get(i) > targetPercentList.get(i)){
                        percentList.set(i,percentList.get(i)-0.02f);
                        needNewFrame = true;
                    }
                    if(Math.abs(targetPercentList.get(i)-percentList.get(i))<0.02f){
                        percentList.set(i,targetPercentList.get(i));
                    }
                }
                if (needNewFrame) {
                    postDelayed(this, 20);
                }
                invalidate();
        }
    };
    /*
     * 总值
     */
    private Runnable Allanimator = new Runnable() {
        @Override
        public void run() {
                boolean needNewFrame = false;
                for (int i=0; i<AlltargetPercentList.size();i++) {
                    if (AllpercentList.get(i) < AlltargetPercentList.get(i)) {
                    	AllpercentList.set(i,AllpercentList.get(i)+0.02f);
                        needNewFrame = true;
                    } else if (AllpercentList.get(i) > AlltargetPercentList.get(i)){
                    	AllpercentList.set(i,AllpercentList.get(i)-0.02f);
                        needNewFrame = true;
                    }
                    if(Math.abs(AlltargetPercentList.get(i)-AllpercentList.get(i))<0.02f){
                    	AllpercentList.set(i,AlltargetPercentList.get(i));
                    }
                }
                if (needNewFrame) {
                    postDelayed(this, 20);
                }
                invalidate();
        }
    };

    public BarView(Context context){
        this(context,null);
    }
    public BarView(Context context, AttributeSet attrs){
        super(context, attrs);
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(BACKGROUND_COLOR);
        fgPaint = new Paint(bgPaint);
        fgPaint.setColor(FOREGROUND_COLOR);
        AllfgPaint = new Paint(bgPaint);
        AllfgPaint.setColor(AllFOREGROUND_COLOR);
        rect = new Rect();
        topMargin = MyUtils.dip2px(context, 5);
        int textSize = MyUtils.sp2px(context, 15);
        int numSize = MyUtils.sp2px(context, 13);
        barWidth = MyUtils.dip2px(context,22);
        MINI_BAR_WIDTH = MyUtils.dip2px(context,22);
        BAR_SIDE_MARGIN  = MyUtils.dip2px(context,22);
        TEXT_TOP_MARGIN = MyUtils.dip2px(context, 5);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        BlankNumPaint = new Paint();
        BlankNumPaint.setAntiAlias(true);
        BlankNumPaint.setColor(NUM_COLOR);
        BlankNumPaint.setTextSize(numSize);
        BlankNumPaint.setTextAlign(Paint.Align.CENTER);
        AllNumPaint = new Paint();
        AllNumPaint.setAntiAlias(true);
        AllNumPaint.setColor(NUM_COLOR);
        AllNumPaint.setTextSize(numSize);
        AllNumPaint.setTextAlign(Paint.Align.CENTER);
        percentList = new ArrayList<Float>();
        AllpercentList = new ArrayList<Float>();
    }

    /**
     * dataList will be reset when called is method.
     * @param bottomStringList The String ArrayList in the bottom.
     */
    public void setBottomTextList(ArrayList<String> bottomStringList){
//        this.dataList = null;
        this.bottomTextList = bottomStringList;
        Rect r = new Rect();
        bottomTextDescent = 0;
        barWidth = MINI_BAR_WIDTH;
        for(String s:bottomTextList){
            textPaint.getTextBounds(s,0,s.length(),r);
            BlankNumPaint.getTextBounds(s,0,s.length(),r);
            AllNumPaint.getTextBounds(s,0,s.length(),r);
            if(bottomTextHeight<r.height()){
                bottomTextHeight = r.height();
            }
            if(autoSetWidth&&(barWidth<r.width())){
                barWidth = r.width();
            }
            if(bottomTextDescent<(Math.abs(r.bottom))){
                bottomTextDescent = Math.abs(r.bottom);
            }
        }
        setMinimumWidth(2);
        postInvalidate();
    }

    public void setBlankSeatList(ArrayList<String> BlankSeatStringList){
      this.BlankSeatList = BlankSeatStringList;
    }
    
    public void setAllSeatList(ArrayList<String> AllSeatStringList){
      this.AllSeatList = AllSeatStringList;
    }
    
    
    /**
     *
     * @param list The ArrayList of Integer with the range of [0-max].
     */
    public void setDataList(ArrayList<Integer> list, int max){
        targetPercentList = new ArrayList<Float>();
        if(max == 0) max = 1;

        for(Integer integer : list){
            targetPercentList.add(1-(float)integer/(float)max);
        }

        // Make sure percentList.size() == targetPercentList.size()
        if(percentList.isEmpty() || percentList.size()<targetPercentList.size()){
            int temp = targetPercentList.size()-percentList.size();
            for(int i=0; i<temp;i++){
                percentList.add(1f);
            }
        } else if (percentList.size()>targetPercentList.size()){
            int temp = percentList.size()-targetPercentList.size();
            for(int i=0; i<temp;i++){
                percentList.remove(percentList.size()-1);
            }
        }
        setMinimumWidth(2);
        removeCallbacks(animator);
        post(animator);
    }
    /*
     * 同理，将每个横轴坐标对应的总值也传进来
     */
    public void setAllDataList(ArrayList<Integer> list, int max){
    	AlltargetPercentList = new ArrayList<Float>();
        if(max == 0) max = 1;

        for(Integer integer : list){
        	AlltargetPercentList.add(1-(float)integer/(float)max);
        }

        // Make sure percentList.size() == targetPercentList.size()
        if(AllpercentList.isEmpty() || AllpercentList.size()<AlltargetPercentList.size()){
            int temp = AlltargetPercentList.size()-AllpercentList.size();
            for(int i=0; i<temp;i++){
            	AllpercentList.add(1f);
            }
        } else if (AllpercentList.size()>AlltargetPercentList.size()){
            int temp = AllpercentList.size()-AlltargetPercentList.size();
            for(int i=0; i<temp;i++){
            	AllpercentList.remove(AllpercentList.size()-1);
            }
        }
        setMinimumWidth(2);
        removeCallbacks(Allanimator);
        post(Allanimator);
    }
    

    @Override
    protected void onDraw(Canvas canvas) {
        int i = 1,j = 1,k = 1;
        if(percentList != null && !percentList.isEmpty()){
            for(Float f:percentList){
                rect.set(BAR_SIDE_MARGIN*i+barWidth*(i-1),
                        topMargin,
                        (BAR_SIDE_MARGIN+barWidth)* i,
                        getHeight()-bottomTextHeight-TEXT_TOP_MARGIN);
                canvas.drawRect(rect,bgPaint);
                rect.set(BAR_SIDE_MARGIN*i+barWidth*(i-1),
                        topMargin+(int)((getHeight()-topMargin)*AllpercentList.get(i-1)),
                        (BAR_SIDE_MARGIN+barWidth)* i,
                        getHeight()-bottomTextHeight-TEXT_TOP_MARGIN);
                canvas.drawRect(rect,AllfgPaint);
                rect.set(BAR_SIDE_MARGIN*i+barWidth*(i-1),
                        topMargin+(int)((getHeight()-topMargin)*percentList.get(i-1)),
                        (BAR_SIDE_MARGIN+barWidth)* i,
                        getHeight()-bottomTextHeight-TEXT_TOP_MARGIN);
                canvas.drawRect(rect,fgPaint);
                i++;
            }
        }

        if(bottomTextList != null && !bottomTextList.isEmpty()){
            i = 1;j = 1;k = 1;
            //底部数字
            for(String s:bottomTextList){
                canvas.drawText(s,BAR_SIDE_MARGIN*i+barWidth*(i-1)+barWidth/2,
                        getHeight()-bottomTextDescent,textPaint);
                i++;
            }
            //有效值
            for(String s:BlankSeatList){
            	float y=( topMargin+(int)((getHeight()-topMargin)*percentList.get(j-1)) + getHeight()-bottomTextHeight-TEXT_TOP_MARGIN)/2;
            	if (y> (getHeight()-bottomTextHeight-TEXT_TOP_MARGIN-topMargin-15))
            	   y=getHeight()-bottomTextHeight-TEXT_TOP_MARGIN-topMargin;
                canvas.drawText(s,BAR_SIDE_MARGIN*j+barWidth*(j-1)+barWidth/2,
                        y,BlankNumPaint);
                j++;
            }
            //总值
            for(String s:AllSeatList){
            	float y=topMargin+(int)((getHeight()-topMargin)*AllpercentList.get(k-1));
                canvas.drawText(s,BAR_SIDE_MARGIN*k+barWidth*(k-1)+barWidth/2,
                        y,AllNumPaint);
                k++;
            }
        }
   
      
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mViewWidth = measureWidth(widthMeasureSpec)+10;
        int mViewHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mViewWidth,mViewHeight);
    }

    private int measureWidth(int measureSpec){
        int preferred = 0;
        if(bottomTextList != null){
            preferred = bottomTextList.size()*(barWidth+BAR_SIDE_MARGIN);
        }
        return getMeasurement(measureSpec, preferred);
    }

    private int measureHeight(int measureSpec){
        int preferred = 222;
        return getMeasurement(measureSpec, preferred);
    }

    private int getMeasurement(int measureSpec, int preferred){
        int specSize = MeasureSpec.getSize(measureSpec);
        int measurement;
        switch(MeasureSpec.getMode(measureSpec)){
            case MeasureSpec.EXACTLY:
                measurement = specSize;
                break;
            case MeasureSpec.AT_MOST:
                measurement = Math.min(preferred, specSize);
                break;
            default:
                measurement = preferred;
                break;
        }
        return measurement;
    }
    
 //处理点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();      
        Region rv = new Region();
        int a,b,c,d;

        if(bottomTextList != null || !bottomTextList.isEmpty()){
	       	        
	        for(int i=0;i<bottomTextList.size();i++)
	        {
	        	a=BAR_SIDE_MARGIN*(i+1)+barWidth*i-BAR_SIDE_MARGIN/2; //L
	        	b=a+BAR_SIDE_MARGIN+barWidth;                         //R
	        	c=getHeight()-bottomTextHeight-TEXT_TOP_MARGIN-20;    //T
	        	d=getHeight()+20;                                      //B
	        	rv.set(a,c,b,d);
	        	if (rv.contains(point.x,point.y) && event.getAction() == MotionEvent.ACTION_DOWN){
                    clickarea = i;               
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    if (rv.contains(point.x,point.y)){
                        //把点击的教室名传进接口
                    	 clickroom = bottomTextList.get(clickarea); 
                         if (mDeliverRoomListener != null)
                         	mDeliverRoomListener.DeliverRoom(clickroom);   
                    	 System.out.println("click:"+clickarea);
                    }
                }
	        }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_UP){
            postInvalidate();
        }
        return true;
    }
    
}
