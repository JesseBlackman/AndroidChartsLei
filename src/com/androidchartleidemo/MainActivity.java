package com.androidchartleidemo;
import  com.androidchartslei.LineView;
import  com.androidchartslei.BarView;
import com.androidchartslei.PieHelper;
import com.androidchartslei.PieView;
import  com.androidchartsleidemo.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements BarView.DeliverRoomListener{
	
	int randomint = 15;	
	int Year_=0; 
	int Month_=0;
	int Day_=0;
	String area_info;
	String build_info;
	String date_info;
	String today_date_info;
	String roomclicked;


	public static ArrayList<String> roomListtoday = new ArrayList<String>(); 
	public static ArrayList<String> eachroomListtoday = new ArrayList<String>();
	public static String[] DataList; 

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		Calendar today = Calendar.getInstance();	
		today_date_info =Integer.toString(today.get(Calendar.YEAR))+"-"+Integer.toString(today.get(Calendar.MONTH)+1)+"-"+Integer.toString(today.get(Calendar.DAY_OF_MONTH)); 

		final LineView lineView = (LineView)this.findViewById(R.id.line_view);
		final BarView barView = (BarView)this.findViewById(R.id.bar_view);
		final PieView pieView = (PieView)this.findViewById(R.id.pie_view);   
		
		barView.setRoomClickedListener(this);
	    //must*
		
		ArrayList<String> test = new ArrayList<String>();
		ArrayList<String> test1 = new ArrayList<String>();
	        
		for (int i=0; i<15; i++)
	            test.add(String.valueOf(i+1));
	    test1.add("001");
	    test1.add("101");
	    test1.add("201");
	    test1.add("301");
	    test1.add("401");
	    test1.add("501");
	    test1.add("601");
	    test1.add("701");
	    test1.add("801");
	     	        
	    lineView.setBottomTextList(test);
	    lineView.setDrawDotLine(true);
        lineView.setShowPopup(LineView.SHOW_POPUPS_NONE);
	    lineView.setLeftTextList(test1);
	    //设置LineView初始数据
	    SetInitialData(lineView);	    
	        
	    //刷新加载新数据
	    TextView todayBtn1 = (TextView) findViewById(R.id.btn_refresh1);
	    todayBtn1.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View view) {
	    			gettodaydata1();	
	    			lineView.setLeftTextList(roomListtoday);	
	    			SetLineView(lineView);
	    			SetBarView(barView);
	    			Setpie(pieView); 
	    	            		
	    	}
	    });       
	    
	  //刷新加载新数据
	    TextView todayBtn2 = (TextView) findViewById(R.id.btn_refresh2);
	    todayBtn2.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View view) {
	    			gettodaydata2();	
	    			lineView.setLeftTextList(roomListtoday);	
	    			SetLineView(lineView);
	    			SetBarView(barView);
	    			Setpie(pieView); 
	    	            		
	    	}
	    });    
	}


 	//重写BarView.DeliverRoom接口
 	 @Override
 	public void DeliverRoom(String classroom)
 		{
 		    roomclicked = classroom;
 		    System.out.println("Got clicked room："+roomclicked);  		   	
 		    //启动WebView
 		   
 		} 	 
	
	 //初始数据
	 public void SetInitialData(LineView lineView){
	        ArrayList<Integer> dataList = new ArrayList<Integer>();
	        ArrayList<Boolean> flagList = new ArrayList<Boolean>();
	        for (int i=0; i<randomint; i++){
	        	dataList.add((int)2);
//	        	0_1 2 3_4_5_6 7_8_9 10 11 12 13_14  
	        	if(i==1 ||i==2 ||i==6 ||i==9||i==10||i==11||i==12||i==14)
	        		flagList.add(false);
	        	else
	        		flagList.add(true);     	              
	        }  
	        ArrayList<Integer> dataList2 = new ArrayList<Integer>();
	        ArrayList<Boolean> flagList2 = new ArrayList<Boolean>();
	        for (int i=0; i<randomint; i++){
	        	dataList2.add((int)7);
//	        	0 1 2_3_4_5 6 7_8_9 10_11_12 13_14  
	        	if(i==0 ||i==1 ||i==5 ||i==6||i==9||i==12||i==14)
	        		flagList2.add(false);
	        	else
	        		flagList2.add(true);
	        }
	        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<ArrayList<Integer>>();
	        ArrayList<ArrayList<Boolean>> flagLists = new ArrayList<ArrayList<Boolean>>();
	        dataLists.add(dataList);
	        dataLists.add(dataList2);
	        flagLists.add(flagList);
	        flagLists.add(flagList2);
	        lineView.setDataList(dataLists);
	        lineView.setifdotshow(flagLists);	       
	    }
	
	 //显示当天的数据 lineview
	 public void SetLineView(LineView lineView){
		    ArrayList<ArrayList<Integer>> dataList = new ArrayList<ArrayList<Integer>>();
	        ArrayList<ArrayList<Boolean>> flagList = new ArrayList<ArrayList<Boolean>>();
	          
	        for(int j=0; j<roomListtoday.size(); j++)
	        {
	           ArrayList<Integer> singledata = new ArrayList<Integer>();
		       ArrayList<Boolean> singleflag = new ArrayList<Boolean>();
	        	
	            for (int i=0; i<14; i++)
	            {
	        	   singledata.add((int)j);
	        	  
	        	   if (eachroomListtoday.get(j).charAt(i) == '1')
	        		  singleflag.add(true);
	        	   else
	        		  singleflag.add(false); 
	            }
	            singledata.add(j);
	            singleflag.add(false);
	            dataList.add(singledata);
	            flagList.add(singleflag);
	        }
     
	        lineView.setDataList(dataList);                 
	        lineView.setifdotshow(flagList);
	       
	    }
	 
	//set barview
	private void SetBarView(BarView barView){
	        int r = DataList.length;
	        int max = 0;
	        ArrayList<String> bottomList = new ArrayList<String>();
	        ArrayList<String> BlankNumList = new ArrayList<String>();
	        ArrayList<String> AllNumList = new ArrayList<String>();
	        ArrayList<Integer> barDataList = new ArrayList<Integer>();
	        ArrayList<Integer> AllbarDataList = new ArrayList<Integer>();
	        for (int i=0; i<r/3; i++){
	        	bottomList.add(DataList[0+i*3]);
	        	barDataList.add(Integer.parseInt(DataList[1+i*3]));
	        	BlankNumList.add(DataList[1+i*3]);
	        	//产生一个最大值，因为至少要留出一个数字的高度。
	        	max = (Integer.parseInt(DataList[2+i*3])>max) ? Integer.parseInt(DataList[2+i*3]) : max;
	        	AllbarDataList.add(Integer.parseInt(DataList[2+i*3]));
	        	AllNumList.add(DataList[2+i*3]);
	        }
	        barView.setBottomTextList(bottomList);	     //横轴area名称  
	        barView.setBlankSeatList(BlankNumList);	 	//空座位数目 string  
	        barView.setAllSeatList(AllNumList);	 		//总座位数目 string  
	        barView.setDataList(barDataList,max);		//绘制上层bar的数据 int  
	        barView.setAllDataList(AllbarDataList,max+10);		//绘制底层bar的数据 int  
	    }
	 
	//set pieview
	 private void Setpie(PieView pieView){
		 	int totalpieblanknum =0;
		 	ArrayList<String> pienameList = new ArrayList<String>();
		    for (int i=0; i<DataList.length/3; i++){
		    	pienameList.add(DataList[0+i*3]);
                totalpieblanknum = totalpieblanknum + Integer.parseInt(DataList[1+i*3]);  
		    }
		 
	        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
	        int SHour =0;
	        int SMin =0;
	        int EHour =0;
	        int EMin =0;
	        int degree =0;
	        int degreenew =0;
	        int r = DataList.length;
	        for (int i=0; i<r/3; i++){
	        	degree = Integer.parseInt(DataList[1+i*3])*360/totalpieblanknum ;
	        	degreenew = degree+ degreenew;
	        	EHour = degreenew/30;
	        	EMin = degreenew%30*2; 
	        	if(i+1 == r/3)
	        	{
	        		EHour = 12;
		        	EMin = 0; 
	        	}
	        	pieHelperArrayList.add(new PieHelper(SHour,SMin,EHour,EMin));
	        	SHour =EHour;
	        	SMin =EMin;
	        }
	        pieView.setareanameList(pienameList); //传进area名称
	        pieView.setDate(pieHelperArrayList);
	    }
	 
	
	
	//获取数据，这里取本地数据。 
	public void gettodaydata1(){				

				String json = "\"101\",\"11110111100000\",\"102\",\"10010111100110\",\"103\",\"11001101100000\",\"104\",\"11001001011010\",\"105\",\"10100010001001\",\"106\",\"11010111111000\"";					
				String regex = "\"[0-9]{3}\"";
				String regex2 ="\"[0-9]{14}\"";					
				roomListtoday= getStringArray(json, regex) ;															
				eachroomListtoday= getStringArray(json, regex2) ;													
				for (int i=0;i<roomListtoday.size();i++){
					roomListtoday.set(i,roomListtoday.get(i).replaceAll("\"",""));
					eachroomListtoday.set(i,eachroomListtoday.get(i).replaceAll("\"",""));
				}
				String json2 = "101,45,60,102,38,60,103,12,60,104,50,60,105,40,120,106,11,60";
				DataList = json2.split(",");
		}
	//获取数据，这里取本地数据。 
		public void gettodaydata2(){	
			
					String json = "\"101\",\"10100100001010\",\"103\",\"10010110100110\",\"201\",\"01100111101110\",\"202\",\"10001110011111\",\"205\",\"11100001001100\",\"301\",\"00111000111010\",\"303\",\"10110010000111\",\"306\",\"11001101100110\"";					
					String regex = "\"[0-9]{3}\"";
					String regex2 ="\"[0-9]{14}\"";					
					roomListtoday= getStringArray(json, regex) ;															
					eachroomListtoday= getStringArray(json, regex2) ;													
					for (int i=0;i<roomListtoday.size();i++){
						roomListtoday.set(i,roomListtoday.get(i).replaceAll("\"",""));
						eachroomListtoday.set(i,eachroomListtoday.get(i).replaceAll("\"",""));
					}
					String json2 = "101,20,60,103,50,60,201,33,60,202,47,80,205,76,200,301,26,70,303,16,60,306,29,70";
					DataList = json2.split(",");
			}
		
		
		private void showMsg(String text) {
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}

		public String convertStreamToString(InputStream is) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		}	
		
		public static ArrayList<String> getStringArray(String ary,String regex)
		{
			Pattern pt = Pattern.compile(regex) ;
			Matcher mr = pt.matcher(ary) ;
			ArrayList<String> list = new ArrayList<String>() ;
			while(mr.find()){
				list.add(mr.group()) ;
			}
			return list;
		}
}