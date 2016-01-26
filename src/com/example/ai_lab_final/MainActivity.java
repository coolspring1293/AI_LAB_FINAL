package com.example.ai_lab_final;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.Settings;


public class MainActivity extends Activity implements SensorEventListener {
    //收集信息
	private double x, y, z, locTime, locLongitude, locLatitude, locAltitude;
	
	private List<Sensor> sensors;
	private TextView gx, gy, gz, llo, lla;
	private Button start_bt, end_bt;
	private EditText etLabel, etInterval;
	private String filePath = "/sdcard/AI_Test/";
	private String fileName = "Not Write to SD card!.txt";
	private final Timer timer = new Timer();  
	
	
	private Handler recordHandler;
	private TimerTask task;  
	
	private Sensor sensor;
	private LocationListener locationListener;
	private LocationManager lm;
	
	private String label;
	
	private GpsStatus.Listener listener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		gx = (TextView) findViewById(R.id.tv_gx);
		gy = (TextView) findViewById(R.id.tv_gy);
		gz = (TextView) findViewById(R.id.tv_gz);
		
		llo = (TextView) findViewById(R.id.tv_loc_lon);
		lla = (TextView) findViewById(R.id.tv_loc_la);
		
		etLabel = (EditText) findViewById(R.id.et_label);
		etInterval = (EditText) findViewById(R.id.et_interval);
		

		
		start_bt = (Button)findViewById(R.id.bt_start);
		end_bt = (Button)findViewById(R.id.bt_end);
		/*
		//为获取地理位置信息时设置查询条件
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        //获取位置信息
        //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location= lm.getLastKnownLocation(bestProvider);    
        updateLocation(location);
        //监听状态
        lm.addGpsStatusListener(listener);
        
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
		
		
		if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
            startActivityForResult(intent,0); 
            return;
        }
        
        locationListener = new LocationListener() {
	        
	        /**
	         * 位置信息变化时触发
	         *
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				updateLocation(location);
				
			}
			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub
				updateLocation(null);
			}

			@Override
			public void onProviderEnabled(String provider) {
	            Location location = lm.getLastKnownLocation(provider);
	            updateLocation(location);
	        }
	    

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
	            switch (status) {
	            //GPS状态为可见时
	            case LocationProvider.AVAILABLE:
	            	Toast.makeText(getApplicationContext(), "当前GPS状态为可见状态", Toast.LENGTH_SHORT).show();

	                break;
	            //GPS状态为服务区外时
	            case LocationProvider.OUT_OF_SERVICE:
	            	Toast.makeText(getApplicationContext(), "当前GPS状态为服务区外状态", Toast.LENGTH_SHORT).show();
	                break;
	            //GPS状态为暂停服务时
	            case LocationProvider.TEMPORARILY_UNAVAILABLE:
	            	Toast.makeText(getApplicationContext(), "当前GPS状态为暂停服务状态", Toast.LENGTH_SHORT).show();
	            	break;
	            }
	        }
	};
	
		listener = new GpsStatus.Listener() {
	        public void onGpsStatusChanged(int event) {
	            

	        };
	    };
		*/
		final Handler handler = new Handler() {
		    @Override
		    public void handleMessage(Message msg) {
		        // TODO Auto-generated method stub
		    	if (msg.what == 1) {		    		
					if (label == null) label = "NULL";
					Time t = new Time();
					t.setToNow();
					String curTime = t.toString();
					String singleSensorRecord = x + "," + y + "," + z + "," + locTime + "," + locTime + "," + locLatitude + ","+ locAltitude +","+ label + "," + curTime;  
					writeTxtToFile(singleSensorRecord, filePath, fileName);			
		    	}		    	
		        super.handleMessage(msg);
		    }
		};
		
		task = new TimerTask() {
		    @Override
		    public void run() {
		        // TODO Auto-generated method stub
		        Message message = new Message();
		        message.what = 1;
		        handler.sendMessage(message);
		    }
		}; 
		
		
		
		        
		start_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int interval = Integer.parseInt(etInterval.getText().toString());
				
				
				timer.schedule(task, 1000, interval);   
				Toast.makeText(getApplicationContext(), "START:1000毫秒后开始收集", Toast.LENGTH_SHORT).show();
				Time t = new Time();
				t.setToNow();
				int year = t.year;
				int day = t.yearDay;
				int hour = t.hour;
				int min = t.minute;
				int sec = t.second;
				fileName = year + "_" + day + "_"+ hour + "_"+ min + "_" + sec + "sensor_record.csv";
				label = etLabel.getText().toString();
			}
			
		});
		
		end_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				timer.cancel();  
				Toast.makeText(getApplicationContext(), "写文件到" + filePath + fileName, Toast.LENGTH_SHORT).show();
				Toast.makeText(getApplicationContext(), "收集结束", Toast.LENGTH_SHORT).show();
				start_bt.setText("Exit");
				start_bt.setEnabled(false);
			}
		});
		
	}
	
	
	public void updateLocation(Location location) {
        if(location != null) {
        	locLongitude = (double)location.getLongitude();
        	locLatitude = (double)location.getLatitude();
        	locTime = (double)location.getTime();
        	locAltitude = (double)location.getAltitude();
        	
        	llo.setText("Longitude--" + locLongitude);
            lla.setText("Latitude--" + locLatitude);
        } else{
        	llo.setText("Longitude--" + "Invalid");
            lla.setText("Latitude--" + "Invalid");
            
        }
    }
	/*{
		 
        
        
	}*/
	
	public void writeFileSdcardFile(String fileName,String write_str) throws IOException{ 
		 try{ 
		       FileOutputStream fout = new FileOutputStream(fileName); 
		       byte [] bytes = write_str.getBytes(); 

		       fout.write(bytes); 
		       fout.close(); 
		     }
		      catch(Exception e){ 
		        e.printStackTrace(); 
		       } 
		   } 
	
	@Override
	protected void onResume() {
		super.onResume();
		SensorManager manager = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		sensor = sensors.get(0);
		manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		x = event.values[0];
		y = event.values[1];
		z = event.values[2];
		
		gx.setText("X-axis-----------:" + x);
		gy.setText("Y-axis-----------:" + y);
		gz.setText("Z-axis-----------:" + z);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	

	// 将字符串写入到文本文件中
	public void writeTxtToFile(String strcontent, String filePath, String fileName) {
	    //生成文件夹之后，再生成文件，不然会出错
	    makeFilePath(filePath, fileName);
	    
	    String strFilePath = filePath+fileName;
	    // 每次写入时，都换行写
	    String strContent = strcontent + "\r\n";
	    try {
	        File file = new File(strFilePath);
	        if (!file.exists()) {
	            Log.d("TestFile", "Create the file:" + strFilePath);
	            file.getParentFile().mkdirs();
	            file.createNewFile();
	        }
	        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
	        raf.seek(file.length());
	        raf.write(strContent.getBytes());
	        raf.close();
	    } catch (Exception e) {
	        Log.e("TestFile", "Error on write File:" + e);
	    }
	}
	 
	// 生成文件
	public File makeFilePath(String filePath, String fileName) {
	    File file = null;
	    makeRootDirectory(filePath);
	    try {
	        file = new File(filePath + fileName);
	        if (!file.exists()) {
	            file.createNewFile();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return file;
	}
	 
	// 生成文件夹
	public static void makeRootDirectory(String filePath) {
	    File file = null;
	    try {
	        file = new File(filePath);
	        if (!file.exists()) {
	            file.mkdir();
	        }
	    } catch (Exception e) {
	        Log.i("error:", e+"");
	    }
	}

	private Criteria getCriteria(){
        Criteria criteria=new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细 
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费  
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求  
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

}
