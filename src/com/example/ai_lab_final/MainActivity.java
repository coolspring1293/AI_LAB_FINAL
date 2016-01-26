package com.example.ai_lab_final;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class MainActivity extends Activity implements SensorEventListener {
    //收集信息
	private double x, y, z, mfx, mfy, mfz, ox, oy, oz, pxt, lt;
	private int speed;
	private List<Sensor> sensors;
	private TextView gx, gy, gz, llo, lla, wf, spd;
	private Button start_bt, end_bt, hd_bt;
	private EditText etLabel, etInterval;
	private String filePath = "/sdcard/AI_Test/";
	private String fileName = "Not Write to SD card!";
	public static String FORMAT_FULL = "yyyy_MM_dd_HH_mm_ss_S";

	private final Timer timer = new Timer();  
	private String ssid;
	
	private Handler recordHandler;
	private TimerTask task;  
	
	private Sensor sensor;

	
	private String label, ipText;
	
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
		
		wf = (TextView) findViewById(R.id.tv_wifi);
		spd  = (TextView) findViewById(R.id.tv_spd);
		
		etLabel = (EditText) findViewById(R.id.et_label);
		etInterval = (EditText) findViewById(R.id.et_interval);
		

		
		start_bt = (Button)findViewById(R.id.bt_start);
		end_bt = (Button)findViewById(R.id.bt_end);
		hd_bt = (Button)findViewById(R.id.hiden);

		final Handler handler = new Handler() {
		    @Override
		    public void handleMessage(Message msg) {
		        // TODO Auto-generated method stub
		    	if (msg.what == 1) {		    		
					if (label == null) label = "NULL";
					Time t = new Time();
					t.setToNow();
					String singleSensorRecord = 
							x   + "," +  y  + "," +  z   + "," + 
							mfx + "," + mfy + "," + mfz  + "," + 
							ox  + "," + oy  + "," + oz   + "," + 
							pxt + "," + lt + "," + getWifiInfo() + "," +
							label + "," + getTimeString(); 
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
		
		end_bt.setEnabled(false);
		       
		start_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int interval = Integer.parseInt(etInterval.getText().toString());
				start_bt.setEnabled(false);
				end_bt.setEnabled(true);
				timer.schedule(task, 1000, interval);   
				Toast.makeText(getApplicationContext(), "START:1000毫秒后开始收集", Toast.LENGTH_SHORT).show();
				fileName = getTimeString() + "sensor_record.csv";
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
		
		
		hd_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(MainActivity.this) 
				.setTitle("人工智能实验组员信息")
				.setMessage("2013级计算机科学与技术\n刘思远\t13349073\n刘旺\t13349074\n谢智晖\t13349134\n左谭励\t????????\n郑晓钿\t????????")
				.setPositiveButton("返回", null)
				.show();
				//Toast.makeText(getApplicationCtext(), "I LOVE YOU", Toast.LENGTH_SHORT).show();
			}
		});
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			new AlertDialog.Builder(MainActivity.this) 
			.setTitle("About")
			.setMessage("AI_Lab_Final_Assignment_V1.0\nCreated by Liu Wang on " +
					"2016 Jan 26.\n Copyright (c) 2016 Liu Wang. All rights reserved.\n" +
					"Source Code on GitHub: https://github.com/coolspring1293/AI_LAB_FINAL\n" + 
					"Thanks a lot to Liu Siyuan and Xie Zhihui.")
			.setPositiveButton("返回", null)
			.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	
	private String getWifiInfo(){
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String maxText = info.getMacAddress();
		ipText = intToIp(info.getIpAddress());
		String status = "WIFI_STATE_DISABLED";
		if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			status = "WIFI_STATE_ENABLED";
		}
		ssid = info.getSSID();
		int networkID = info.getNetworkId();
		speed = info.getLinkSpeed();
		
		wf.setText("WiFi:" + ssid);
		spd.setText("IP:"+ ipText +"\tSpeed:" + speed);
		
		return maxText + ","+ ipText + "," + status + ","+ ssid + "," + networkID + "," + speed;
		}
	private String intToIp(int ip) {
		return  (ip & 0xFF) + "." + 
				((ip >> 8) & 0xFF) + "." + 
				((ip >> 16) & 0xFF) + "." + 
				((ip >> 24) & 0xFF);
	}
	

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
		sensors = manager.getSensorList(Sensor.TYPE_ALL);
		sensor = sensors.get(0);
		manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		for (Sensor s : sensors) {
			switch (s.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				x = event.values[0];
				y = event.values[1];
				z = event.values[2];
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				mfx = event.values[0];
				mfy = event.values[1];
				mfz = event.values[2];
				break;
			case Sensor.TYPE_ORIENTATION:
				ox = event.values[0];
				oy = event.values[1];
				oz = event.values[2];
				break;
			case Sensor.TYPE_PROXIMITY:
				pxt = event.values[0];
				break;
				
			case Sensor.TYPE_LIGHT:
				lt = event.values[0];
				break;
			}
			getWifiInfo();
		}
		
		gx.setText("X-axis-----------:" + x);
		gy.setText("Y-axis-----------:" + y);
		gz.setText("Z-axis-----------:" + z);
		lla.setText("Proximity--------:"+ pxt);
		llo.setText("Light------------:"+ lt);
		
		
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

	@SuppressLint("SimpleDateFormat")
	public static String getTimeString() {
	    SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL);
	    Calendar calendar = Calendar.getInstance();
	    return df.format(calendar.getTime());
	}


}
