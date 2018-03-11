package com.coursemis.view.activity;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.coursemis.view.myView.TitleView;
import com.coursemis.R;
import com.coursemis.model.LocationData;
import com.coursemis.util.HttpUtil;
import com.coursemis.util.P;
import com.coursemis.util.SubActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



public class TStartSignInActivity extends Activity{
	private String noticeInfo1=null;
	private String noticeInfo2=null;
	private String noticeInfo3=null;
	private Calendar dt = Calendar.getInstance();

	private Button btn_3=null;
	private Button btn_4=null;
	private Button btn_5=null;
	private Button btn_6=null;
	private int tid;
	private  AsyncHttpClient client =null;
	private String courseInfo=null;
	private String courseWeek=null;
	private String courseTime=null;
	private TextView textView_1=null;
	private int signInHour=0;
	private int signInMinute=0;
	private int cid;
	private TitleView mTitle=null;
	private Location currentLocation; 
	private String best;
	private TextView mTv = null;

	private Button   mStartBtn;
	private boolean  mIsStart;
	private static int count = 1;
	private Vibrator mVibrator01 =null;
	private LocationClient mLocClient;
	public static String TAG = "LocTestDemo";
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tstartsigninactivity);
		LocationData.latitude=0.0;
		LocationData.longitude=0.0;
		LocationData.radius=0.0f;
		
		
		mTitle = (TitleView) findViewById(R.id.tStart_signIn);
		mTitle.setTitle("课堂签到");
		mTitle.setLeftButton(R.string.back, new TitleView.OnLeftButtonClickListener(){

			public void onClick(View button) {
				finish();
			}
			
		});
		mTitle.setRightButton(R.string.signin, new TitleView.OnRightButtonClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(courseInfo==null||courseWeek==null||courseTime==null
						||(signInHour==0&&signInMinute==0)
						/*||LocationData.latitude==0.0||LocationData.longitude==0*/
						/*||LocationData.radius==0.0f  */) {
					Log.e("测试签到信息",(courseInfo==null)+"..."
							+(courseWeek==null)+"..."
							+(courseTime==null)+"..."
							+(signInHour==0&&signInMinute==0)+"..."
							+(LocationData.latitude==0.0)+"..."
							+(LocationData.longitude==0)+"..."
							+(LocationData.radius==0.0)) ;
					Toast.makeText(TStartSignInActivity.this,"签到信息没有设置完整!", Toast.LENGTH_SHORT).show();
				}else
				{
					RequestParams params = new RequestParams();
					params.put("cid", courseInfo.substring(0, courseInfo.indexOf(" ")));			
					params.put("signInHour", signInHour+"");
					params.put("signInMinute", signInMinute+"");
					params.put("latitude", LocationData.latitude+"");
					params.put("longitude", LocationData.longitude+"");
					//将教师Id上传上去
					params.put("tid",tid);
					client.post(HttpUtil.server_teacher_SignIn, params,
							new JsonHttpResponseHandler() {

								@Override
								public void onSuccess(int arg0, JSONObject arg1) {
								}
							});
					
					Toast.makeText(TStartSignInActivity.this,"签到已经开启", Toast.LENGTH_SHORT).show();
					finish();
					
					
				}
				
				
				
				
			}
		});
		
		
		mTv = (TextView)findViewById(R.id.TSignIn_11);

		mStartBtn = (Button)findViewById(R.id.TeacherStarLoc);
		mStartBtn.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mIsStart) {
					setLocationOption();
					mLocClient.start();
					Toast.makeText(TStartSignInActivity.this, "定位中请耐心等待", Toast.LENGTH_LONG);
					mStartBtn.setText("定位停止");
					mIsStart = true;

				} else {
					mLocClient.stop();
					mIsStart = false;
					mStartBtn.setText("定位开始");
					mTv.setText("定位完成");
				} 
				Log.d(TAG, "... mStartBtn onClick... pid="+Process.myPid()+" count="+count++);
			}
		});

		mIsStart = false;

		mLocClient = ((Location)getApplication()).mLocationClient;
		((Location)getApplication()).mTv = mTv;
		mVibrator01 =(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		((Location)getApplication()).mVibrator01 = mVibrator01;
		
		
		
		btn_3=(Button) findViewById(R.id.TSignIn_3);
		btn_4=(Button) findViewById(R.id.TSignIn_4);
		btn_5=(Button) findViewById(R.id.TSignIn_5);
		btn_6=(Button) findViewById(R.id.TSignIn_6);
		
		textView_1=(TextView)findViewById(R.id.TSignIn_7);
	
		client = new AsyncHttpClient();

		
	


		
		btn_3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SharedPreferences  sharedata=getSharedPreferences("courseMis", 0);
				tid = Integer.parseInt(sharedata.getString("userID",null));
				P.p(" tid     "+tid);
				RequestParams params = new RequestParams();
				params.put("tid", tid+"");
				params.put("action", "search_teacherCourse");
				client.post(HttpUtil.server_teacher_course, params,
						new JsonHttpResponseHandler() {

							@Override
							public void onSuccess(int arg0, JSONObject arg1) {
								JSONArray object = arg1.optJSONArray("result");
								P.p(object.toString()+1111);
								Log.e("选择课程",arg1.toString()) ;
								if(object.length()==0){
									Toast.makeText(TStartSignInActivity.this,"您还没教授任何课程!", Toast.LENGTH_SHORT).show();
								}else{
									ArrayList<String> list=new ArrayList<String>();
									for(int i=0;i<arg1.optJSONArray("result").length();i++){
										JSONObject object_temp = arg1.optJSONArray("result").optJSONObject(i);
										P.p(object_temp.toString()+2222);
										list.add(i, (object_temp.optInt("CId")+" "+(object_temp.optString("CName"))));
										}
									
									Intent intent = new Intent(TStartSignInActivity.this,TMentionNameActivity_1.class);
									intent.putStringArrayListExtra("courselist", list);
									
									startActivityForResult(intent,SubActivity.SUBACTIVITY_1);
								}
								
								super.onSuccess(arg0, arg1);
							}
						});
				
			}
			}
		);
		
		
		
		btn_4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
				if(courseInfo==null){
					Toast.makeText(TStartSignInActivity.this,"请先选择课程!", Toast.LENGTH_SHORT).show();
				}else{
					RequestParams params = new RequestParams();
					int k=courseInfo.indexOf(" ");
					String cid = "" ;
					for(int i=0;i<k;i++)
					{
						cid=courseInfo.charAt(i)+cid;
					}
					P.p(cid+"cid");
					params.put("cid", cid);	
					client.post(HttpUtil.server_teacher_course_week, params,
							new JsonHttpResponseHandler(){
						public void onSuccess(int arg0, JSONObject arg1) {
							JSONObject object = arg1.optJSONArray("result").optJSONObject(0);
							P.p(object.toString()+"1111");
							Log.e("课程周数",arg1.toString()) ;
							if(object.length()==0){
								Toast.makeText(TStartSignInActivity.this,"查询失败!", Toast.LENGTH_SHORT).show();
							}else{
								
								String weekchoose=object.optString("weekchoose");
								P.p(weekchoose+"weekchoose");
								String weeknumber=object.optString("weeknumber");
								P.p(weeknumber+"weeknumber");
								Intent intent = new Intent(TStartSignInActivity.this,TMentionNameActivity_2.class);
								
								intent.putExtra("weekchoose", weekchoose);
								intent.putExtra("weeknumber", weeknumber);
								startActivityForResult(intent,SubActivity.SUBACTIVITY_2);
								
								
							}
							
							super.onSuccess(arg0, arg1);
						}
						
						
						
						
					});
					
					
					
				}
				
				
				
				
			}
		});
		
		
		btn_5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(courseInfo==null||courseWeek==null){
					Toast.makeText(TStartSignInActivity.this,"请先选择课程再选择上课周!", Toast.LENGTH_SHORT).show();
				}else{
					RequestParams params = new RequestParams();
					int k=courseInfo.indexOf(" ");
					String cid = "" ;
					for(int i=0;i<k;i++)
					{
						cid=courseInfo.charAt(i)+cid;
					}
					P.p(cid+"cid");
					params.put("cid", cid);	
					client.post(HttpUtil.server_teacher_course_time, params,
							new JsonHttpResponseHandler(){
						public void onSuccess(int arg0, JSONObject arg1) {
							JSONArray object = arg1.optJSONArray("result");
							P.p(object.toString()+"1111");
							
							if(object.length()==0){
								Toast.makeText(TStartSignInActivity.this,"查询失败!", Toast.LENGTH_SHORT).show();
							}else{
								ArrayList<String> list=new ArrayList<String>();
								
								for(int i=0;i<arg1.optJSONArray("result").length();i++){
									JSONObject object_temp = arg1.optJSONArray("result").optJSONObject(i);
						
									list.add(i, object_temp.optInt("weekday")+" "+object_temp.optInt("startclass")+" "+object_temp.optInt("endclass"));
									}
								/*intent.putStringArrayListExtra("courselist", list);
								intent.putExtra("courelist1", list);*/
								Intent intent = new Intent(TStartSignInActivity.this,TMentionNameActivity_3.class);
								intent.putStringArrayListExtra("cousetime_list", list);
								intent.putExtra("courelist1", list);
								P.p("cousetime size"+list.size());
								startActivityForResult(intent,SubActivity.SUBACTIVITY_3);

							}
							
							super.onSuccess(arg0, arg1);
						}
						
						
						
						
					});
					
					
					
				}
			}
		});
		
		
		btn_6.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				showDialog(1);
				
				
				
			}
		});
		
	}
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case 0: // 返回DatePickerDialog对话框
     	  DatePickerDialog dDialog = new DatePickerDialog(this,
     	     new DatePickerDialog.OnDateSetListener() {
 			 public void onDateSet(DatePicker view, int year, 
 				  int monthOfYear,	int dayOfMonth) {						
 	    	   
 		   	 }							   	
 		  }, dt.get(Calendar.YEAR),
 			 dt.get(Calendar.MONTH),
 			 dt.get(Calendar.DAY_OF_MONTH));
     	  return dDialog;
        case 1: // 返回TimePickerDialog对话框
     	  TimePickerDialog tDialog = new TimePickerDialog(this,
     		 new TimePickerDialog.OnTimeSetListener() {
     		 @Override
     	  	 public void onTimeSet(TimePicker view,
     	  			 int hourOfDay, int minute) {
     			textView_1.setText(noticeInfo1+"\r\n"+noticeInfo2+"\r\n"+noticeInfo3+"\r\n"+"您设置了 "+hourOfDay+" 小时"+" "+minute+" 分钟");
     			signInHour=hourOfDay;
     			signInMinute=minute;
     			P.p("minute"+minute);
     			P.p("hourOfDay"+hourOfDay);
 	     	        					
              }
     	  }, dt.get(Calendar.HOUR),dt.get(Calendar.MINUTE),true);
     	 tDialog.setTitle("设置签到时间");
      	  return tDialog;
        }
        return null;
     }
	
//    @Override
//	protected void onResume() {
//		super.onResume();
//		// 取得最佳的定位提供者
//		Criteria criteria = new Criteria();
//		best = manager.getBestProvider(criteria, true);
//		// 更新位置频率的条件 
//        int minTime = 5000; // 毫秒
//        float minDistance = 5; // 公尺
//		if (best != null) { // 取得快取的最后位置,如果有的话 
//	       currentLocation = manager.getLastKnownLocation(best); 
//	       manager.requestLocationUpdates(best, minTime,
//	    		                     minDistance, listener);
//		}
//		else { // 取得快取的最后位置,如果有的话 
//           currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
//           manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
//                   minTime, minDistance, listener);
//		}
//	    updatePosition(); // 更新位置 
//	}
//	@Override
//	protected void onPause() {
//		super.onPause();
//		manager.removeUpdates(listener); 
//	}
//	// 更新现在的位置 
//    private void updatePosition() { 
//    	TextView output;
//    	output = (TextView) findViewById(R.id.TSignIn_11);
//        if (currentLocation == null) { 
//            output.setText("取得定位信息中..."); 
//        } else { 
//            output.setText(getLocationInfo(currentLocation)); 
//        } 
//    } 
//    // 创建定位服务的监听者对象 
//    private LocationListener listener = new LocationListener() { 
//        @Override 
//        public void onLocationChanged(Location location) { 
//            currentLocation = location; 
//            updatePosition(); 
//        }  
//        @Override 
//        public void onProviderDisabled(String provider) { } 
//        @Override 
//        public void onProviderEnabled(String provider) { } 
//        @Override 
//        public void onStatusChanged(String provider, int status, Bundle extras) { } 
//    }; 
//    // 取得定位信息
//	public String getLocationInfo(Location location) {
//		StringBuffer str = new StringBuffer();
//		str.append("定位提供者(Provider): "+location.getProvider());
//		str.append("\n纬度(Latitude): " + Double.toString(location.getLatitude()));
//		str.append("\n经度(Longitude): " + Double.toString(location.getLongitude()));
//		str.append("\n高度(Altitude): " + Double.toString(location.getAltitude()));
//		latitude = Double.toString(location.getLatitude())+"";
//		longitude=Double.toString(location.getLongitude())+"";
//		return str.toString();
//	}
//	// 启动Google地图
//    public void button1_Click(View view) {
//    	// 取得经纬度坐标
//    	float latitude = (float) currentLocation.getLatitude();
//    	float longitude = (float) currentLocation.getLongitude();   
//    	// 创建URI字符串
//    	String uri = String.format("geo:%f,%f?z=18", latitude, longitude);
//    	// 创建Intent对象
//    	Intent geoMap = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
//    	startActivity(geoMap);  // 启动活动   	
//    }
	
	
    
	//设置相关参数
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
    	option.setPoiNumber(10);
		option.disableCache(true);		
		mLocClient.setLocOption(option);
	}
    
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case SubActivity.SUBACTIVITY_1:
			if(resultCode==RESULT_OK) 
			{
				noticeInfo1=null;
				Uri uriData=data.getData();
				courseInfo=uriData.toString();
				courseWeek=null;
				courseTime=null;
				noticeInfo1="选择"+uriData.toString().substring(uriData.toString().indexOf(" ")+1,uriData.toString().length())+"课程";
				textView_1.setText(noticeInfo1);
			}
			break;
		case SubActivity.SUBACTIVITY_2:
			if(resultCode==RESULT_OK) {
				noticeInfo2=null;
				Uri uriData=data.getData();
				courseWeek=uriData.toString();
				courseTime=null;
				noticeInfo2="选择课程第"+uriData.toString()+"周";
				textView_1.setText(noticeInfo1+"\r\n"+noticeInfo2); 
			}break;
		case SubActivity.SUBACTIVITY_3:
			if(resultCode==RESULT_OK) {
				noticeInfo3=null;
				Uri uriData=data.getData();
				courseTime=uriData.toString();
				noticeInfo3="时间"+uriData.toString().substring(2,uriData.toString().length());
				textView_1.setText(noticeInfo1+"\r\n"+noticeInfo2+"\r\n"+noticeInfo3); 
			}
			break;
		
		}
		
	}
	
	

}
