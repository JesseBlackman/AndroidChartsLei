package com.androidchartslei;
import  com.androidchartsleidemo.R;


import java.util.ArrayList;
import java.util.Collections;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.NinePatchDrawable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Dacer on 11/4/13.
 * Edited by Lee youngchan 21/1/14
 * Edited by Lei gang on 16/9/14
 */
public class LineView extends View {
    public int mViewHeight;
    //drawBackground
    private boolean autoSetDataOfGird = true;
    private boolean autoSetGridWidth = true;
    private int dataOfAGird = 10;
    private int bottomTextHeight = 0;
    private ArrayList<String> bottomTextList;
    private ArrayList<String> leftTextList;
    
    private ArrayList<ArrayList<Integer>> dataLists;
    private ArrayList<ArrayList<Boolean>> flagLists;
    private ArrayList<Integer> dataList;
    
    private ArrayList<Integer> xCoordinateList = new ArrayList<Integer>();
    private ArrayList<Integer> yCoordinateList = new ArrayList<Integer>();
    
    private ArrayList<ArrayList<Dot>> drawDotLists = new ArrayList<ArrayList<Dot>>();
    private ArrayList<Dot> drawDotList = new ArrayList<Dot>();
    
    private Paint bottomTextPaint = new Paint();
    private Paint ycoordTextPaint = new Paint();  
    private int bottomTextDescent;
    private int clickarea;            //点击坐标轴上的区域的数值
    public boolean clickaarea = false; 
    
    //popup
    private Paint popupTextPaint = new Paint();
    private final int bottomTriangleHeight = 12;
    public boolean showPopup = true; 

	private Dot selectedDot;

    private int topLineLength = MyUtils.dip2px(getContext(), 45);; // | | ←this
                                                                   //-+-+-
    private int sideLineLength = MyUtils.dip2px(getContext(),45)/3*2;// --+--+--+--+--+--+--
                                                                     //  ↑this
    private int backgroundGridWidth = MyUtils.dip2px(getContext(),25);
    
    private int backgroundGridLength = MyUtils.dip2px(getContext(),30); //控制纵轴单位高度
    //Constants
    private final int popupTopPadding = MyUtils.dip2px(getContext(),2);
    private final int popupBottomMargin = MyUtils.dip2px(getContext(),0);
    private final int bottomTextTopMargin = MyUtils.sp2px(getContext(),5);
    private final int bottomLineLength = MyUtils.sp2px(getContext(), 22);
    private final int DOT_INNER_CIR_RADIUS = MyUtils.dip2px(getContext(), 2);
    private final int DOT_OUTER_CIR_RADIUS = MyUtils.dip2px(getContext(),5);
    private final int MIN_TOP_LINE_LENGTH = MyUtils.dip2px(getContext(),30);
    private final int YCOORD_TEXT_LEFT_MARGIN = MyUtils.dip2px(getContext(), 10); 
    private final int MIN_VERTICAL_GRID_NUM = 4;
    private final int MIN_HORIZONTAL_GRID_NUM = 1;
    private final int BACKGROUND_LINE_COLOR = Color.parseColor("#EEEEEE");
    private final int BOTTOM_TEXT_COLOR = Color.parseColor("#9B9A9B");
    
    public static final int SHOW_POPUPS_All = 1;
    public static final int SHOW_POPUPS_MAXMIN_ONLY = 2;
    public static final int SHOW_POPUPS_NONE = 3;

    private int showPopupType = SHOW_POPUPS_NONE;
    
    public int holehight;
    public int holewidth;
    
    public void setShowPopup(int popupType) {
		this.showPopupType = popupType;
	}

	//점선표시
    private Boolean drawDotLine = false;
    //라인컬러
    private String[] colorArray = {"#e74c3c","#2980b9","#1abc9c"};
    //popup 컬러
    private int[] popupColorArray = {R.drawable.popup_red,R.drawable.popup_blue,R.drawable.popup_green};
	public void setDrawDotLine(Boolean drawDotLine) {
		this.drawDotLine = drawDotLine;
	}

	//runable只是一个接口，如果没有开辟新的thread来实现，那么还是在主线程中运行。
	private Runnable animator = new Runnable() {
        @Override
        public void run() {
            boolean needNewFrame = false;
            for(ArrayList<Dot> data : drawDotLists){
            	for(Dot dot : data){
                    dot.update();
                    if(!dot.isAtRest()){
                        needNewFrame = true;
                    }
                }
            }
            if (needNewFrame) {
                postDelayed(this, 25);
            }
            invalidate();
        }
    };

    public LineView(Context context){
        this(context,null);
    }
//    在 AttributeSet 这种方法中,大概的步骤是这样的
//    1.我们的自定义控件和其他的控件一样,应该写成一个类,而这个类的属性是是有自己来决定的.
//    2.我们要在res/values目录下建立一个attrs.xml的文件,并在此文件中增加对控件的属性的定义.
//    3.使用AttributeSet来完成控件类的构造函数,并在构造函数中将自定义控件类中变量与attrs.xml中的属性连接起来.
//    4.在自定义控件类中使用这些已经连接的属性变量.
//    5.将自定义的控件类定义到布局用的xml文件中去.
//    6.在界面中生成此自定义控件类对象,并加以使用.
    public LineView(Context context, AttributeSet attrs){
        super(context, attrs);
        popupTextPaint.setAntiAlias(true);
        popupTextPaint.setColor(Color.WHITE);
        popupTextPaint.setTextSize(MyUtils.sp2px(getContext(), 12));
        popupTextPaint.setStrokeWidth(5);
        popupTextPaint.setTextAlign(Paint.Align.CENTER);

        bottomTextPaint.setAntiAlias(true);
        bottomTextPaint.setTextSize(MyUtils.sp2px(getContext(),12));
        bottomTextPaint.setTextAlign(Paint.Align.CENTER);
        bottomTextPaint.setStyle(Paint.Style.FILL);
        bottomTextPaint.setColor(BOTTOM_TEXT_COLOR);
        
        ycoordTextPaint.setAntiAlias(true);  
        ycoordTextPaint.setTextSize(MyUtils.sp2px(getContext(),12));  
        ycoordTextPaint.setTextAlign(Paint.Align.LEFT);  
        ycoordTextPaint.setStyle(Paint.Style.FILL);  
        ycoordTextPaint.setColor(BOTTOM_TEXT_COLOR);  
    }

    /**
     * dataList will be reset when called is method.
     * @param bottomTextList The String ArrayList in the bottom.
     */
    
    public void setifdotshow(ArrayList<ArrayList<Boolean>> inflagLists){	
    	    this.flagLists= inflagLists;
    }
    
    public void setLeftTextList(ArrayList<String> lefttextlist){	
    	leftTextList = lefttextlist;
    
    }
    public void setBottomTextList(ArrayList<String> bottomtextlist){
        //this.dataList.clear();
    	
        bottomTextList = bottomtextlist;

        Rect r = new Rect();
        int longestWidth = 0;
        String longestStr = "";
        bottomTextDescent = 0;
        for(String s:bottomTextList){
            bottomTextPaint.getTextBounds(s,0,s.length(),r);
            if(bottomTextHeight<r.height()){
                bottomTextHeight = r.height();
            }
            if(autoSetGridWidth&&(longestWidth<r.width())){
                longestWidth = r.width();
                longestStr = s;
            }
            if(bottomTextDescent<(Math.abs(r.bottom))){
                bottomTextDescent = Math.abs(r.bottom);
            }
        }

        if(autoSetGridWidth){
            if(backgroundGridWidth<longestWidth){
                backgroundGridWidth = longestWidth+(int)bottomTextPaint.measureText(longestStr,0,1);
            }
            if(sideLineLength<longestWidth/2){
                sideLineLength = longestWidth/2;
            }
        }

        refreshXCoordinateList(getHorizontalGridNum());
    }

    /**
     *
     * @param dataLists The Integer ArrayLists for showing,
     *                 dataList.size() must < bottomTextList.size()
     */
    public void setDataList(ArrayList<ArrayList<Integer>> dataLists){
    	selectedDot = null;
        this.dataLists = dataLists;
        for(ArrayList<Integer> list : dataLists){
        	if(list.size() > bottomTextList.size()){
                throw new RuntimeException("dacer.LineView error:" +
                        " dataList.size() > bottomTextList.size() !!!");
            }
        }
        int biggestData = 0;
        for(ArrayList<Integer> list : dataLists){
        	if(autoSetDataOfGird){
                for(Integer i:list){
                    if(biggestData<i){
                        biggestData = i;
                    }
                }
        	}
        	dataOfAGird = 1;
        	while(biggestData/10 > dataOfAGird){
        		dataOfAGird *= 10;
        	}
        }
        
        refreshAfterDataChanged();                               //3
        showPopup = true;
        setMinimumWidth(0); // It can help the LineView reset the Width,
                                // I don't know the better way..
       
        //holehight = backgroundGridLength * getVerticalGridlNum() +topLineLength+bottomTextTopMargin + bottomTextHeight+bottomTextDescent;
        //holewidth = getWidth();
        //measure(holehight,holewidth);
        //this.measure(0,0);
        postInvalidate();
    }

    private void refreshAfterDataChanged(){
    	mViewHeight= backgroundGridLength * getVerticalGridlNum() +topLineLength+bottomTextTopMargin + bottomTextHeight+bottomTextDescent+50;
        int verticalGridNum = getVerticalGridlNum();
        
        System.out.println("lineview+++verticalGridNum："+verticalGridNum);
        refreshTopLineLength(verticalGridNum);
        refreshYCoordinateList(verticalGridNum);
        refreshDrawDotList(verticalGridNum);                         //2
        
    }

    private int getVerticalGridlNum(){
        int verticalGridNum = MIN_VERTICAL_GRID_NUM;
        if(dataLists != null && !dataLists.isEmpty()){
        	for(ArrayList<Integer> list : dataLists){
	        	for(Integer integer:list){
	        		if(verticalGridNum<integer){
	        			verticalGridNum = integer;
	        		}
	        	}
        	}
        }
        return verticalGridNum;
    }

    private int getHorizontalGridNum(){
    	int horizontalGridNum = 0;
    	if( bottomTextList != null)
          horizontalGridNum = bottomTextList.size()-1;
        if(horizontalGridNum<MIN_HORIZONTAL_GRID_NUM){
            horizontalGridNum = MIN_HORIZONTAL_GRID_NUM;
        }
        return horizontalGridNum;
    }

    private void refreshXCoordinateList(int horizontalGridNum){
        xCoordinateList.clear();
        for(int i=0;i<(horizontalGridNum+1);i++){
            xCoordinateList.add(sideLineLength + backgroundGridWidth*i);
        }

    }

    private void refreshYCoordinateList(int verticalGridNum){
        yCoordinateList.clear();
        for(int i=verticalGridNum;i>=0;i--){        //垂直方向，y轴坐标点数比verticalGridNum大1
//            yCoordinateList.add(topLineLength +
//                    ((mViewHeight-topLineLength-bottomTextHeight-bottomTextTopMargin-
//                            bottomLineLength-bottomTextDescent)*i/(verticalGridNum)));
        	yCoordinateList.add(mViewHeight-bottomTextHeight-bottomTextTopMargin-
                 bottomLineLength-bottomTextDescent-backgroundGridLength *i);
        }
    }

    private void refreshDrawDotList(int verticalGridNum){
        if(dataLists != null && !dataLists.isEmpty()){
    		//if(drawDotLists.size() == 0){
        	    drawDotLists.clear();   			
    			for(int k = 0; k < dataLists.size(); k++){
    				drawDotLists.add(new ArrayList<LineView.Dot>());
    			//}
    		}
    		     System.out.println("LINEVIEW:dataLists.size() ="+ dataLists.size());
    		     System.out.println("LINEVIEW:drawDotLists.size() ="+ drawDotLists.size());
        	for(int k = 0; k < dataLists.size(); k++){
        		int drawDotSize = drawDotLists.get(k).isEmpty()? 0:drawDotLists.get(k).size();  //1
        		
        		for(int i=0;i<dataLists.get(k).size();i++){
                    int x = xCoordinateList.get(i);
                    int y = yCoordinateList.get(verticalGridNum - dataLists.get(k).get(i));
                    if(i>drawDotSize-1){
                    	//도트리스트를 추가한다.Add to the list dots
                        drawDotLists.get(k).add(new Dot(x, 0, x, y, dataLists.get(k).get(i),k));
                    }else{
                    	//도트리스트에 타겟을 설정한다.Sets the list of target dots
                        drawDotLists.get(k).set(i, drawDotLists.get(k).get(i).setTargetData(x,y,dataLists.get(k).get(i),k));
                    }
                }
        		//如果点的个数比数据个数多，则从后往前删掉多的个数
        		int temp = drawDotLists.get(k).size() - dataLists.get(k).size();
        		for(int i=0; i<temp; i++){
        			drawDotLists.get(k).remove(drawDotLists.get(k).size()-1);
        		}
        	}
        }
        //移除runable
        removeCallbacks(animator);
        //将runable添加到消息队列中，在user interface thread中运行
        post(animator);
    }

    private void refreshTopLineLength(int verticalGridNum){
        // For prevent popup can't be completely showed when backgroundGridHeight is too small.
        // But this code not so good.
        if((mViewHeight-topLineLength-bottomTextHeight-bottomTextTopMargin)/
                (verticalGridNum+2)<getPopupHeight()){
            topLineLength = getPopupHeight()+DOT_OUTER_CIR_RADIUS+DOT_INNER_CIR_RADIUS+2;
        }else{
            topLineLength = MIN_TOP_LINE_LENGTH;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackgroundLines(canvas);
        drawLines(canvas);
        drawDots(canvas);

        //在点上方显示数据，依赖于参数 showPopupType 选择
        for(int k=0; k < drawDotLists.size(); k++){
        	int MaxValue = Collections.max(dataLists.get(k));
        	int MinValue = Collections.min(dataLists.get(k));
        	for(Dot d: drawDotLists.get(k)){
        		if(showPopupType == SHOW_POPUPS_All)
        			drawPopup(canvas, String.valueOf(d.data), d.getPoint(),popupColorArray[k%3]);
        		else if(showPopupType == SHOW_POPUPS_MAXMIN_ONLY){
        			if(d.data == MaxValue)
        				drawPopup(canvas, String.valueOf(d.data), d.getPoint(),popupColorArray[k%3]);
        			if(d.data == MinValue)
        				drawPopup(canvas, String.valueOf(d.data), d.getPoint(),popupColorArray[k%3]);
        		}
        	}
        }
      // 显示点击的点的数据       
        if(showPopup && selectedDot != null &&  selectedDot.iffollowshowed)
        {
            String name = leftTextList.get(selectedDot.data);    //String.valueOf(selectedDot.data)
            System.out.println("selectedroom:"+name);
        	drawPopup(canvas,
                    name,
                    selectedDot.getPoint(),popupColorArray[selectedDot.linenumber%3]);
        }
      //显示点击坐标轴上的点对应的区间的所有数据
        if(clickaarea)
        {
        	 
        	for(int k=0; k < drawDotLists.size(); k++){	
        		for(int m=0; m<drawDotLists.get(k).size(); m++)  
        		{
        			if(m==clickarea &&  drawDotLists.get(k).get(m).iffollowshowed)
        			{
        				String name=leftTextList.get(drawDotLists.get(k).get(m).data);  
        				drawPopup(canvas,
        	                    name,
        	                    drawDotLists.get(k).get(m).getPoint(),popupColorArray[drawDotLists.get(k).get(m).linenumber%3]);	
        			}
        		}
        	}
       	}
    }

    /**
     *
     * @param canvas  The canvas you need to draw on.
     * @param point   The Point consists of the x y coordinates from left bottom to right top.
     *                Like is
     *                
     *                3
     *                2
     *                1
     *                0 1 2 3 4 5
     */
    private void drawPopup(Canvas canvas,String num, Point point,int PopupColor){
        boolean singularNum = (num.length() == 1);
        int sidePadding = MyUtils.dip2px(getContext(),singularNum? 8:5);
        int x = point.x;
        int y = point.y-MyUtils.dip2px(getContext(),5);
        Rect popupTextRect = new Rect();
        popupTextPaint.getTextBounds(num,0,num.length(),popupTextRect);
        Rect r = new Rect(x-popupTextRect.width()/2-sidePadding,
                y - popupTextRect.height()-bottomTriangleHeight-popupTopPadding*2-popupBottomMargin,
                x + popupTextRect.width()/2+sidePadding,
                y+popupTopPadding-popupBottomMargin);
//NinePatchDrawable 绘画的是一个可以伸缩的位图图像,Android会自动调整大小来容纳显示的内容
        NinePatchDrawable popup = (NinePatchDrawable)getResources().getDrawable(PopupColor);
        popup.setBounds(r);
        popup.draw(canvas);
        canvas.drawText(num, x, y-bottomTriangleHeight-popupBottomMargin, popupTextPaint);
    }

    private int getPopupHeight(){
        Rect popupTextRect = new Rect();
        popupTextPaint.getTextBounds("9",0,1,popupTextRect);
        Rect r = new Rect(-popupTextRect.width()/2,
                 - popupTextRect.height()-bottomTriangleHeight-popupTopPadding*2-popupBottomMargin,
                 + popupTextRect.width()/2,
                +popupTopPadding-popupBottomMargin);
        return r.height();
    }

    //  画点
    private void drawDots(Canvas canvas){
    	int x;
    	int y;
    	boolean f1;
    	boolean f2;
        Paint bigCirPaint = new Paint();
        bigCirPaint.setAntiAlias(true);
        Paint smallCirPaint = new Paint(bigCirPaint);
        smallCirPaint.setAntiAlias(true);
        smallCirPaint.setColor(Color.parseColor("#FFFFFF"));
        if(drawDotLists!=null && !drawDotLists.isEmpty()){       		
        	for(int k=0; k < drawDotLists.size(); k++){	
        		bigCirPaint.setColor(Color.parseColor(colorArray[k%3]));
        		
        		//获取这个点之后的一个单位时段是否要画出
        		 for(int m=0; m<drawDotLists.get(k).size(); m++)        			
         			drawDotLists.get(k).get(m).iffollowshowed=flagLists.get(k).get(m);  
             	
        		 for(int i=0; i<drawDotLists.get(k).size(); i++){
        			f1= drawDotLists.get(k).get(i).iffollowshowed;
        			x=drawDotLists.get(k).get(i).x;
        			y=drawDotLists.get(k).get(i).y;
        			if (i==0)     //起始点，若为-1 则不画
        			{
        				drawDotLists.get(k).get(i).showed =(f1)? true : false;       				
        			}
        			else         //前有后无为右端点 前无后有为左端点 ，左右端点画点 
        			{
        				f2= drawDotLists.get(k).get(i-1).iffollowshowed;
        				if (!f1 && f2 || f1 && !f2)
        					drawDotLists.get(k).get(i).showed =true;
        				else
        					drawDotLists.get(k).get(i).showed =false;
        			}
        			
        			if(drawDotLists.get(k).get(i).showed)
        			{
                	  canvas.drawCircle(x,y,DOT_INNER_CIR_RADIUS,bigCirPaint);
                	  //canvas.drawCircle(x,y,DOT_INNER_CIR_RADIUS,smallCirPaint);
        			}
            	}
        	}
        }
    }

    //根据showed属性 连点成线  ：showed为真的点的个数是偶数
    private void drawLines(Canvas canvas){
    	int b=0,e=0;
    	boolean f1=false;
    	boolean f2=false;
        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(MyUtils.dip2px(getContext(), 2));
        for(int k = 0; k<drawDotLists.size(); k ++){
        	linePaint.setColor(Color.parseColor(colorArray[k%3]));
	        for(int i=0; i<drawDotLists.get(k).size(); i++){
	        	
	        	if (drawDotLists.get(k).get(i).showed )
	        	{
	        		if (!f1)     //f1假 ：更新f1，将f1置真
	        		{
	        		    b=i;
	        		    f1=true;
	        		}
	        		else if(!f2)  //f1真 f2假 ：更新f2，将f2置真
	        		{
	        			 e=i;
		        		 f2=true;	        			
	        		}
	        		if(f1 && f2)  //f1真 f2真：划线，重置f1 f2为假
	        		{
	                     canvas.drawLine(drawDotLists.get(k).get(b).x,
	                                     drawDotLists.get(k).get(b).y,
	                                     drawDotLists.get(k).get(e).x,
	                                     drawDotLists.get(k).get(e).y,linePaint);
	                     f1 =false;
	                     f2= false;
	        		}
	        	}
	        }
        }
    }
    
//    背景线 包括：1.竖线（实线）
//               2.横线（虚线，描有点的线）
//               3.横坐标
     private void drawBackgroundLines(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(MyUtils.dip2px(getContext(),1f));
        paint.setColor(BACKGROUND_LINE_COLOR);
        PathEffect effects = new DashPathEffect(
                new float[]{10,5,10,5}, 1);

        //draw vertical lines
        for(int i=0;i<xCoordinateList.size();i++){
            canvas.drawLine(xCoordinateList.get(i),
            		mViewHeight-topLineLength-bottomTextTopMargin - bottomTextHeight-bottomTextDescent-backgroundGridLength * getVerticalGridlNum(),
                    xCoordinateList.get(i),
                    mViewHeight - bottomTextTopMargin - bottomTextHeight-bottomTextDescent,
                    paint);
        }
        
        //draw dotted lines
       paint.setPathEffect(effects);
       Path dottedPath = new Path();
       for(int i=0;i<yCoordinateList.size();i++){
           if((yCoordinateList.size()-1-i)%dataOfAGird == 0){
                int y = yCoordinateList.get(i); 
                dottedPath.moveTo(55, y);
                dottedPath.lineTo(getWidth(), y);
                canvas.drawPath(dottedPath, paint);
            }
        }
       
       //draw left text
       if(leftTextList != null){
    	   int n = yCoordinateList.size();   
            for(int i=0;i<n;i++){
    	        canvas.drawText(leftTextList.get(i), 0, yCoordinateList.get(n-i-1), ycoordTextPaint);
            }
       }
      //draw bottom text
      if(bottomTextList != null){  	  
    	  for(int i=0;i<bottomTextList.size()-1;i++){
    		  if (i==whatstime())    //获得当前时间并计算出是第几节
    			  bottomTextPaint.setColor(Color.parseColor("#0000FF"));
    		  else
    			  bottomTextPaint.setColor(Color.parseColor("#696969"));
    		  canvas.drawText(bottomTextList.get(i), sideLineLength+backgroundGridWidth*i+25, mViewHeight-bottomTextDescent-10, bottomTextPaint);
    	  }
      }
      
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		System.out.println(this.getMeasuredWidth()+","+this.getMeasuredHeight()) ;
    	
        int mViewWidth = measureWidth(widthMeasureSpec);
        mViewHeight = measureHeight(heightMeasureSpec);
        refreshAfterDataChanged();
        setMeasuredDimension(mViewWidth,mViewHeight); //调整view 大小
    }
//计算宽度
    private int measureWidth(int measureSpec){
        int horizontalGridNum = getHorizontalGridNum();
        int preferred = backgroundGridWidth*horizontalGridNum+sideLineLength*2;
        return getMeasurement(measureSpec, preferred);
    }
//计算高度
    private int measureHeight(int measureSpec){
        int preferred = 0;
        return getMeasurement(measureSpec, preferred);
    }
//计算规则
    private int getMeasurement(int measureSpec, int preferred){
    	//根据提供的测量值(格式)提取大小值(这个大小也就是我们通常所说的大小)
        int specSize = MeasureSpec.getSize(measureSpec);
        int measurement;
        //根据提供的测量值(格式)提取模式(上述三个模式之一)
        switch(MeasureSpec.getMode(measureSpec)){
            case MeasureSpec.EXACTLY:        //父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小
                measurement = specSize;
                break;
            case MeasureSpec.AT_MOST:       //子元素至多达到指定大小的值
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
        Region r = new Region();
        Region rv = new Region();
        int a,b;
        int width = backgroundGridWidth/2;
        if(drawDotLists != null || !drawDotLists.isEmpty()){
	        for(ArrayList<Dot> data : drawDotLists){
	        	for(Dot dot : data){
	        		r.set(dot.x-width,dot.y-width,dot.x+width,dot.y+width);
	                if (r.contains(point.x,point.y) && event.getAction() == MotionEvent.ACTION_DOWN){
	                    selectedDot = dot;
	                }else if (event.getAction() == MotionEvent.ACTION_UP){
	                    if (r.contains(point.x,point.y)){
	                        showPopup = true;
	                        clickaarea = false;
	                    }
	                }
	            }
	        }
	        
	        for(int i=0;i<bottomTextList.size()-1;i++)
	        {
	        	a=sideLineLength+backgroundGridWidth*i+25;
	        	b=mViewHeight-bottomTextDescent-10;
	        	rv.set(a-width,b-width,a+width,b+width);
	        	if (rv.contains(point.x,point.y) && event.getAction() == MotionEvent.ACTION_DOWN){
                    clickarea = i;
                    
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    if (rv.contains(point.x,point.y)){
                    	clickaarea = true;
                    	showPopup = false;
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
   
    private int whatstime()
    {
    	int c=50;
    	Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
    	t.setToNow(); // 取得系统时间。
    	int hour = t.hour; // 0-23
    	int minute = t.minute;
    	if (hour == 8 && 0 <minute && minute < 45)
    	   c=0;
    	if ((hour ==  8 && 50 <minute)||(hour ==  9 && minute < 35))
    	   c=1;
    	if ((hour ==  9 && 50 <minute)||(hour == 10 && minute < 35))
     	   c=2;
    	if ((hour == 10 && 40 <minute)||(hour == 11 && minute < 25))
      	   c=3;
    	if ((hour == 11 && 30 <minute)||(hour == 12 && minute < 15))
       	   c=4;
    	
    	if ((hour == 13 && 15 <minute)||(hour == 14 && minute <  0))
           c=5;
    	if ((hour == 14 &&  5 <minute)||(hour == 14 && minute < 50))
            c=6;
    	if ((hour == 14 && 55 <minute)||(hour == 15 && minute < 40))
            c=7;
    	if ((hour == 15 && 45 <minute)||(hour == 16 && minute < 30))
            c=8;
    	if ((hour == 16 && 40 <minute)||(hour == 17 && minute < 25))
            c=9;
    	if ((hour == 17 && 30 <minute)||(hour == 18 && minute < 15))
            c=10;
    	if ((hour == 19 &&  0 <minute)||(hour == 19 && minute < 45))
            c=11;
    	if ((hour == 19 && 50 <minute)||(hour == 20 && minute < 35))
            c=12;
    	if ((hour == 20 && 40 <minute)||(hour == 21 && minute < 25))
            c=13;
    	return c;
    }

    
    class Dot{
        int x;
        int y;
        int data;
        int targetX;
        int targetY;
        int linenumber;
        int velocity = MyUtils.dip2px(getContext(),18);
        boolean showed =true;
        boolean iffollowshowed= true;

        
        Dot(int x,int y,int targetX,int targetY,Integer data,int linenumber){
            this.x = x;
            this.y = y;
            this.linenumber = linenumber;
            setTargetData(targetX, targetY,data,linenumber);
        }

        Point getPoint(){
            return new Point(x,y);
        }

        Dot setTargetData(int targetX,int targetY,Integer data,int linenumber){
            this.targetX = targetX;
            this.targetY = targetY;
            this.data = data;
            this.linenumber = linenumber;
            return this;
        }

        boolean isAtRest(){
            return (x==targetX)&&(y==targetY);
        }

        void update(){
            x = updateSelf(x, targetX, velocity);
            y = updateSelf(y, targetY, velocity);
        }

        private int updateSelf(int origin, int target, int velocity){
            if (origin < target) {
                origin += velocity;
            } else if (origin > target){
                origin-= velocity;
            }
            if(Math.abs(target-origin)<velocity){
                origin = target;
            }
            return origin;
        }
    }
}
