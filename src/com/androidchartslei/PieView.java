package com.androidchartslei;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;


/**
 * Created by Dacer on 11/13/13.
 * Edited by Lei gang on 16/9/14
 */
public class PieView extends View {

    private Paint textPaint;
    private Paint redPaint;
    private Paint linePaint;
    private Paint whitePaint;
    private int mViewWidth;
    private int mViewHeight;
    private int textSize;
    private int pieRadius;
    private Point pieCenterPoint;
    private Point tempPoint;
    private Point tempPointRight;
    private int lineLength;
    private float leftTextWidth;
    private float rightTextWidth;
    private float topTextHeight;
    private int lineThickness;
    private RectF cirRect;
    private Rect textRect;
    private ArrayList<String> areanameList;
    
    private ArrayList<PieHelper> pieArrayList = new ArrayList<PieHelper>();
    private ArrayList<PieHelper> pieArrayList_ = new ArrayList<PieHelper>();  //单纯备份传进的角度
    
    private final int TEXT_COLOR = Color.parseColor("#9B9A9B");
    private final int GRAY_COLOR = Color.parseColor("#D4D3D4");
    private final int RED_COLOR = Color.argb(50, 255, 0, 51);
    private String[] colorArray = {"#e74c3c","#2980b9","#1abc9c"};

    private Runnable animator = new Runnable() {
        @Override
        public void run() {
            boolean needNewFrame = false;
            for(PieHelper pie : pieArrayList){
                pie.update();
                if(!pie.isAtRest()){
                    needNewFrame = true;
                }
            }
            if (needNewFrame) {
                postDelayed(this, 10);
            }
            invalidate();
        }
    };

    public PieView(Context context){
        this(context,null);
    }
    public PieView(Context context, AttributeSet attrs){
        super(context, attrs);
        textSize = MyUtils.sp2px(context, 15);
        lineThickness = MyUtils.dip2px(context, 1);
        lineLength = MyUtils.dip2px(context, 10);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = new Paint.FontMetrics();
        textPaint.getFontMetrics(fm);
        textRect = new Rect();
        textPaint.getTextBounds("18",0,1,textRect);
        redPaint = new Paint(textPaint);
        redPaint.setColor(RED_COLOR);
        linePaint = new Paint(textPaint);
        linePaint.setColor(GRAY_COLOR);
        linePaint.setStrokeWidth(lineThickness);
        whitePaint = new Paint(linePaint);
        whitePaint.setColor(Color.WHITE);
        tempPoint = new Point();
        pieCenterPoint = new Point();
        tempPointRight = new Point();
        cirRect = new RectF();
        leftTextWidth = textPaint.measureText("18");
        rightTextWidth = textPaint.measureText("6");
        topTextHeight = textRect.height();
    }
    
    //传进画图需要的数据
    public void setDate(ArrayList<PieHelper> helperList){
        if(helperList != null && !helperList.isEmpty()){
        	this.pieArrayList_ = helperList;
            int pieSize = pieArrayList.isEmpty()? 0:pieArrayList.size();
            for(int i=0;i<helperList.size();i++){
                if(i>pieSize-1){
//                    float mStart = helperList.get(i).getStart();
                    pieArrayList.add(new PieHelper(0,0,helperList.get(i)));
                }else{
                    pieArrayList.set(i, pieArrayList.get(i).setTarget(helperList.get(i)));
                }
            }
            int temp = pieArrayList.size() - helperList.size();
            for(int i=0; i<temp; i++){
                pieArrayList.remove(pieArrayList.size()-1);
            }
        }else {
            pieArrayList.clear();
        }
        
        removeCallbacks(animator);
        post(animator);
    }
    
    //传进要画在图上的数据
    public void setareanameList(ArrayList<String> AreaNameList){
        this.areanameList = AreaNameList;
      }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        if(pieArrayList != null){
        	//涂色
        	for(int k=0; k<pieArrayList.size(); k++){
        		redPaint.setColor(Color.parseColor(colorArray[k%3]));
        		canvas.drawArc(cirRect,pieArrayList.get(k).getStart(),pieArrayList.get(k).getSweep(),true,redPaint);	   
        	}
        	
        	//画数据
        	for(int k=0; k< pieArrayList_.size(); k++){
        		//angle:12点方向起始，顺时针的角度
                double angle = pieArrayList_.get(k).getStart()-270+pieArrayList_.get(k).getSweep()/2;

                //90-angle: 360-(angle-90) 转化成平面坐标系中角度，3点起始，逆时针
   	            float x = (float) (pieCenterPoint.x+Math.cos(Math.toRadians(90-angle))*pieRadius*0.8);
   	            float y = (float) (pieCenterPoint.y-Math.sin(Math.toRadians(90-angle))*pieRadius*0.8);
   	            canvas.drawText(areanameList.get(k),x, y, textPaint);
           }
        }
    }

    //背景，主要是外圈圆
    private void drawBackground(Canvas canvas){
    	
        //外圈白
        //canvas.drawCircle(pieCenterPoint.x,pieCenterPoint.y,pieRadius+lineLength/2, whitePaint);
        //圆
        canvas.drawCircle(pieCenterPoint.x,pieCenterPoint.y,pieRadius+lineThickness,linePaint);
        //内圈白
        canvas.drawCircle(pieCenterPoint.x,pieCenterPoint.y,pieRadius,whitePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = measureWidth(widthMeasureSpec);
        mViewHeight = measureHeight(heightMeasureSpec);
        pieRadius = mViewWidth*5/12-lineLength*2-(int)(textPaint.measureText("18")/2);
        pieCenterPoint.set(mViewWidth/2-(int)rightTextWidth/2+(int)leftTextWidth/2,
                mViewHeight/2+textSize/2-(int)(textPaint.measureText("18")/2));
        cirRect.set(pieCenterPoint.x-pieRadius,
                pieCenterPoint.y-pieRadius,
                pieCenterPoint.x+pieRadius,
                pieCenterPoint.y+pieRadius);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    private int measureWidth(int measureSpec){
        int preferred = 3;
        return getMeasurement(measureSpec, preferred);
    }

    private int measureHeight(int measureSpec){
        int preferred = mViewWidth;
        return getMeasurement(measureSpec, preferred);
    }

    private int getMeasurement(int measureSpec, int preferred){
        int specSize = View.MeasureSpec.getSize(measureSpec);
        int measurement;

        switch(View.MeasureSpec.getMode(measureSpec)){
            case View.MeasureSpec.EXACTLY:
                measurement = specSize;
                break;
            case View.MeasureSpec.AT_MOST:
                measurement = Math.min(preferred, specSize);
                break;
            default:
                measurement = preferred;
                break;
        }
        return measurement;
    }
}
