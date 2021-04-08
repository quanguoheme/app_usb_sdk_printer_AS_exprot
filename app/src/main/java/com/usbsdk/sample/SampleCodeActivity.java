package com.usbsdk.sample;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import comon.error.Common;
import github.com.debug;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.format.Time;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.posin.filebrowser.FileBrowser;
import com.usbsdk.ASBStatus;
import com.usbsdk.CallbackInfo;
import com.usbsdk.CallbackInterface;
import com.usbsdk.Device;
import com.usbsdk.DeviceParameters;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Editable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.format.Time;


import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;
import android.view.View;
import java.io.*;

import android.widget.CheckBox;
import java.lang.String;

import java.io.IOException;
import android.app.ProgressDialog;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

 

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.view.Gravity;

public class SampleCodeActivity extends Activity implements CallbackInterface {

     
	//GpCom objects
    Device m_Device;
    DeviceParameters m_DeviceParameters;
    ASBStatus m_ASBData;
    //String FILE_NAME = "/mnt/internal_sd/quck.bin";
    String FILE_NAME = "quck.bin";
    //Status Thread
 	Thread m_statusThread;
 	boolean m_bStatusThreadStop;
 	public OutputStream mOutputStream =new OutputStream();
    //progress dialog
 	 
 	//static int test=0;
    //progress dialog
 	ProgressDialog m_progressDialog;

	private final int ENABLE_BUTTON = 2;
	private final int SHOW_VERSION = 3;
	private final int UPDATE_FW = 4;
	private final int SHOW_PROGRESS = 5;
	private final int DISABLE_BUTTON = 6;	
	private final int HIDE_PROGRESS=7;	
	private final int REFRESH_PROGRESS=8;	
	private final int SHOW_FONT_UPTAE_INFO=9;
	private final int SHOW_PRINTER_INFO_WHEN_INIT=10;
	private final byte  HDX_ST_NO_PAPER1 = (byte)(1<<0);     // 1 缺纸
	//private final byte  HDX_ST_BUF_FULL  = (byte)(1<<1);     // 1 缓冲满
	//private final byte  HDX_ST_CUT_ERR   = (byte)(1<<2);     // 1 打印机切刀错误
	private final byte  HDX_ST_HOT       = (byte)(1<<4);     // 1 打印机太热
	private final byte  HDX_ST_WORK      = (byte)(1<<5);     // 1 打印机在工作状态
	
	private boolean stop = false;
	public static int BinFileNum = 0;
	public static boolean ver_start_falg = false;
	boolean Status_Start_Falg = false;
	byte [] Status_Buffer=new byte[300];
	int Status_Buffer_Index = 0;
	public static int update_ver_event = 0;
	public static boolean update_ver_event_err = false;
	public static StringBuilder strVer=new StringBuilder("922");
	public static StringBuilder oldVer=new StringBuilder("922");
	public static File BinFile;
	// EditText mReception;
	private static final String TAG = "SampleCodeActivity";
	private static   String Error_State = "";
	Time time = new Time();
	int TimeSecond;
	public CheckBox myCheckBox;
	public ProgressDialog myDialog = null;
	private int iProgress   = 0;
	String Printer_Info =new String();
	
	public static boolean flow_start_falg = false;	
	byte [] flow_buffer=new byte[300];
	
	public TextView TextViewSerialRx;
	public static Context context;
	private  static int get_ver_count = 0;
	MyHandler handler;
	EditText Emission;
	Button ButtonCodeDemo;
	Button ButtonImageDemo;
	Button ButtonGetVersion;
	Button ButtonUpdateVersion;
	Button ButtonGetStatus;
	Button ButtonUpdateFontLib;
	String debug_str="";
	String debug_strX="";
	ExecutorService pool = Executors.newSingleThreadExecutor();
	//WakeLock lock;
	int printer_status = 0;
	private ProgressDialog m_pDialog;

    private Bitmap mBitmap ;
    private Canvas mCanvas;	
    private int lcd_width;
    private int lcd_height;



	private class MyHandler extends Handler {
		public void handleMessage(Message msg) {
			if (stop == true)
				return;
			switch (msg.what) {
			case DISABLE_BUTTON:
				//Close_Button();
				debug.d(TAG,"DISABLE_BUTTON");
				break;			
			case ENABLE_BUTTON:
				//ButtonCodeDemo.setEnabled(true);
				//ButtonImageDemo.setEnabled(true);
				ButtonGetVersion.setEnabled(true);
				//ButtonCharacterDemo.setEnabled(true);
				if(get_ver_count>1)
				{
					ButtonUpdateVersion.setEnabled(true);
					//ButtonUpdateFontLib.setEnabled(true);			
				}
	
				debug.d(TAG,"ENABLE_BUTTON");
				break;
			case SHOW_FONT_UPTAE_INFO:
				TextView tv3 = new TextView(SampleCodeActivity.this);
				tv3.setText((String)msg.obj);
				tv3.setGravity(Gravity.CENTER);
				tv3.setTextSize(25);
				tv3.findFocus();
				new AlertDialog.Builder(SampleCodeActivity.this)
						 
						.setView(tv3)
						.setCancelable(false)
						.setPositiveButton("OK", new OnClickListener() {

							public void onClick(DialogInterface arg0, int arg1) {
								handler.sendMessage(handler.obtainMessage(ENABLE_BUTTON, 1,0, null));	
							}
						}).show();
				break;
			case SHOW_VERSION:
				TextView tv2 = new TextView(SampleCodeActivity.this);
				tv2.setText(getString(R.string.currentFWV)
						+ SampleCodeActivity.strVer.toString());
				tv2.setGravity(Gravity.CENTER);
				tv2.setTextSize(25);
				tv2.findFocus();
				new AlertDialog.Builder(SampleCodeActivity.this)
						.setTitle(getString(R.string.getV))
						.setIcon(R.drawable.icon)
						.setView(tv2)
						.setCancelable(false)
						.setPositiveButton("OK", null).show();
				break;				
			case UPDATE_FW:
				m_pDialog.hide();
				TextView tv4 = new TextView(SampleCodeActivity.this);
				// if(!SampleCodeActivity.oldVer.toString().isEmpty())
				{
					tv4.setText(getString(R.string.previousFWV)
							+ SampleCodeActivity.oldVer.toString() + "\n"
							+ getString(R.string.currentFWV)
							+ SampleCodeActivity.strVer.toString());
				 
				}
				// else
				{
					// tv3.setText("update firmware version failed ");
				}
				tv4.setGravity(Gravity.CENTER);
				tv4.setTextSize(22);
				tv4.findFocus();
				
				new AlertDialog.Builder(SampleCodeActivity.this)
						.setTitle(getString(R.string.updateFWFinish))
						.setIcon(R.drawable.icon).setView(tv4)
						.setCancelable(true)
						.setPositiveButton("OK", null).show();
				break;
			case SHOW_PROGRESS:
				m_pDialog = new ProgressDialog(SampleCodeActivity.this);
				m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				m_pDialog.setMessage((String)msg.obj);
				m_pDialog.setIndeterminate(false);
				m_pDialog.setCancelable(false);
				m_pDialog.show();
				break;
			case  HIDE_PROGRESS:	
				m_pDialog.hide();
				break;
			case   REFRESH_PROGRESS :	
				m_pDialog.setProgress(iProgress);		
				break;	
			case     SHOW_PRINTER_INFO_WHEN_INIT:	
				TextViewSerialRx.setText(Printer_Info+strVer.toString());
				break;				
			default:
				break;
			}
		}
	}
  
	@Override
	protected void onDestroy() {
		   if(m_Device.isDeviceOpen())
			   m_Device.closeDevice();
		   super.onDestroy();
	}
	public Common.ERROR_CODE requestPermission(Context context) {
		/*UsbManager um = ((UsbManager) context.getSystemService("usb"));

		UsbDevice usbdev = getUsbDevice(um);
		
		if (usbdev != null) {

			// get requestPermission
			if (!um.hasPermission(usbdev)) {
				postRequestPermission(context, um, usbdev);

				return Common.ERROR_CODE.ERROR_OR_NO_ACCESS_PERMISSION;
			}

			return Common.ERROR_CODE.SUCCESS;
		}

		return Common.ERROR_CODE.NO_USB_DEVICE_FOUND;*/
		return Common.ERROR_CODE.SUCCESS;
	}
	static String ACTION_USB_PERMISSION = "com.usbsdk.USBPort.USB_PERMISSION";
	private static void postRequestPermission(Context context, UsbManager um,
			UsbDevice ud) {
		final BroadcastReceiver receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				debug.d(TAG, intent.getAction());
				context.unregisterReceiver(this);
			}
		};

		IntentFilter ifilter = new IntentFilter(ACTION_USB_PERMISSION);
		context.registerReceiver(receiver, ifilter);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		um.requestPermission(ud, pi);
	}
	
	
	static UsbDevice getUsbDevice(UsbManager um) {
		HashMap<String, UsbDevice> lst = um.getDeviceList();

		Iterator<UsbDevice> deviceIterator = lst.values().iterator();
		while (deviceIterator.hasNext()) {
			UsbDevice dev = (UsbDevice) deviceIterator.next();
			
			Log.d(TAG, "usb device : " + String.format("%1$04X:%2$04X", dev.getVendorId(), dev.getProductId()));

			
			if (dev.getVendorId() == 0x0485 ) {
				 
				return dev;
			}
			if (dev.getVendorId() == 0xB000 ) {
				 
				return dev;
			}	
			
		}
		
		return null;
	}
	
	@SuppressLint("InvalidWakeLockTag")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_code);
		PowerManager pm = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
		handler = new MyHandler();
		 strVer = new StringBuilder();
	     oldVer = new StringBuilder();
	     ButtonGetVersion = (Button) findViewById(R.id.GetVersion);
	     ButtonUpdateVersion = (Button) findViewById(R.id.UpdateVersion);
	     
	     ButtonGetStatus = (Button) findViewById(R.id.GetStatus);
			WindowManager wm = (WindowManager) getBaseContext()
                    .getSystemService(Context.WINDOW_SERVICE);
			 lcd_width = wm.getDefaultDisplay().getWidth();
		/*	 ButtonCodeDemo = (Button) findViewById(R.id.ButtonCodeDemo);
			B uttonImageDemo = (Button) findViewById(R.id.ButtonImageDemo);
			
			
			ButtonCharacterDemo = (Button) findViewById(R.id.ButtonCharacterDemo);
			ButtonUpdateFontLib = (Button) findViewById(R.id.UpdateFontLib);*/
	 	ButtonGetVersion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boolean result;
				ButtonGetVersion.setEnabled(false);
				result = get_fw_version();
				sleep(300);
				ButtonGetVersion.setEnabled(true);
			}
		});
	 	
	 	ButtonGetStatus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boolean result;
				ButtonGetVersion.setEnabled(false);
				result = get_status();
			 
				ButtonGetVersion.setEnabled(true);
			}
		});
	 	
		ButtonUpdateVersion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				/*boolean result = get_fw_version();
				if (!result) {

					return;
				}*/
				oldVer=strVer;
				String[] TransactionType = new String[] {
						getResources().getString(R.string.TransactionType1),
						getResources().getString(R.string.TransactionType2),
						getResources().getString(R.string.TransactionType3),
			

				};
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SampleCodeActivity.this);
				builder.setTitle(getString(R.string.selectFW));
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setSingleChoiceItems(TransactionType, 0,
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								System.out.println("whichwhichwhichwhich == "
										+ which);
								switch (which) {
								case 0:
									SampleCodeActivity.strVer = new StringBuilder("966");
									BinFileNum=R.raw.hdx80pt9012;
									break;
								case 1:
									SampleCodeActivity.strVer = new StringBuilder("967");	
									BinFileNum=R.raw.hdx9012pt170519;
									break;
								case 2:
									SampleCodeActivity.strVer = new StringBuilder("pt98mhdx9016");	
									BinFileNum=R.raw.hdx_80_065_9019;
									/*Resources r =getResources();;
									InputStream is = r.openRawResource(BinFileNum);
									SendFileToUart(is);	
									dialog.dismiss();
									return;*/
									break;
									
				
								default:
									return;
								}
								new UpdateFWThread(0).start();
							
								dialog.dismiss();
							}
						});
				builder.setCancelable(true);
				builder.setNegativeButton(
						"从其他地方选择固件",
						new OnClickListener() {
							public void onClick(
									DialogInterface arg0,int arg1) {
								//SDFileExplorer.FileType=0;
								//Intent intent = new Intent(SampleCodeActivity.this,SDFileExplorer.class);
								//startActivity(intent);
								//startActivityForResult(intent,0);
								debug.e("quck2", "finish->从其他地方选择固件  "); 
								
							}
						});				
				builder.setPositiveButton(R.string.cancel, null);
				builder.show();
			}
		});	     
        try
        {
        	m_progressDialog=null;
        	
        	//fill spinner with data
	        Spinner spinner = (Spinner) findViewById(R.id.spinnerPortType);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.porttypes_array, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        
	        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long id) {
					debug.d(TAG, "select "+position);
					//if(position == 1) {
						((EditText)findViewById(R.id.editTextIPAddress)).setText("");
				    	requestPermission(SampleCodeActivity.this);
					//}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
	        	
	        });
	        
        	//initialize Common
        	m_Device = new Device();
        	m_DeviceParameters = new DeviceParameters();
        	
	        //register myself as callback
	        m_Device.registerCallback(this);
        	
        	//create and run status thread
        	createAndRunStatusThread(this);

/*        		if(usbManager.hasPermission(usbDevice)){
        		    //Other code
        		}else{ 
        		    //没有权限询问用户是否授予权限
        		    usbManager.requestPermission(usbDevice, pendingIntent); //该代码执行后，系统弹出一个对话框，
        		   //询问用户是否授予程序操作USB设
        		}
*/       		
        }
        catch(Exception e)
        {
			messageBox(this, "Exception: " + e.toString() + " - " + e.getMessage(), "onCreate Error");
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.sample_code, menu);
		return true;
	}

    /**************************************************************************************
     * onConfigurationChanged
     * This is called when something changes, e.g. the orientation of the tablet 
     * @param newConfig
     * @see Activity#onConfigurationChanged(Configuration)
     **************************************************************************************/
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
      super.onConfigurationChanged(newConfig);
      //setContentView(R.layout.main);
      setContentView(R.layout.activity_sample_code);
      showASBStatus();
    }
    
    
    /**************************************************************************************
     * createAndRunStatusThread
     * 
     * @param act main activity
     **************************************************************************************/
    public void createAndRunStatusThread(final Activity act)
    {
        m_bStatusThreadStop=false;
		m_statusThread = new Thread(new Runnable() 
	   	{
		   	public void run() 
		   	{
		   		while(m_bStatusThreadStop==false)
		   		{
		   			try
		   			{
		   				//anything touching the GUI has to run on the Ui thread
		   		    	act.runOnUiThread(new Runnable()
	   			    	{
	   			    		public void run()
	   			    		{
	   			    	    	//set indicator background color
	   			    	        TextView tvOpen =(TextView)findViewById(R.id.textViewOpen);
	   			    	        TextView tvClosed =(TextView)findViewById(R.id.textViewClosed);
	   			    	        TextView tvdebug =(TextView)findViewById(R.id.textViewDebug);
	   			    	        TextView tvdebugX =(TextView)findViewById(R.id.textViewDebugX);
	   			    	      
	   			    	        if(m_Device.isDeviceOpen())
	   			    	        {
	   			    	        	tvOpen.setBackgroundColor(0xFF00E000);		//green
	   			    	        	tvClosed.setBackgroundColor(0xFF707070);	//gray
	   			    	        	tvdebug.setBackgroundColor(0xF000E020);	
	   			    	        	
	   			    	        }
	   			    	        else
	   			    	        {
	   			    	        	tvOpen.setBackgroundColor(0xFF707070);		//gray
	   			    	        	tvClosed.setBackgroundColor(0xFFE00000);	//red
	   			    	        	tvdebug.setBackgroundColor(0xFF707070);	 
	   			    	        }
	   			    	        tvdebugX.setBackgroundColor(0xFF707070);	 
	   			    	        tvdebug.setText(debug_str);
	   			    	        tvdebugX.setText(debug_strX);
	   			    	       //sleep(20000);
	   			    	        
	   			    		}
	   			    	});

		   				Thread.sleep(200);
		   			}
		   			catch(InterruptedException e)
		   			{
		   				m_bStatusThreadStop = true;
		   				messageBox(act, "Exception in status thread: " + e.toString() + " - " + e.getMessage(), "createAndRunStatusThread Error");
		   			}
		   		}
		   	}
	   	});
		m_statusThread.start();
    }
    
    
    /**************************************************************************************
     * openButtonClicked
     * 
     * @param view
     **************************************************************************************/
    public void openButtonClicked(View view)
    {
		Common.ERROR_CODE err = Common.ERROR_CODE.SUCCESS;
    	debug_str="";
    	EditText editTextIPAddress = (EditText)findViewById(R.id.editTextIPAddress);
    	Spinner spinnerPortType = (Spinner)findViewById(R.id.spinnerPortType);
    	String selectedItem = (String)spinnerPortType.getSelectedItem();
    	
    	if(selectedItem.equals("Ethernet"))
    	{
    	   	//fill in some parameters
	    	m_DeviceParameters.PortType = Common.PORT_TYPE.USB;
	    	m_DeviceParameters.PortName = editTextIPAddress.getText().toString(); 
	    	m_DeviceParameters.ApplicationContext = this;
    	}
    	else if(selectedItem.equals("USB"))
    	{
	    	//fill in some parameters
	    	m_DeviceParameters.PortType = Common.PORT_TYPE.USB;
	    	m_DeviceParameters.PortName = editTextIPAddress.getText().toString(); 
	    	m_DeviceParameters.ApplicationContext = this;
    	}
    	else
    	{
			err = Common.ERROR_CODE.INVALID_PORT_TYPE;
    	}
    	
    	if(err== Common.ERROR_CODE.SUCCESS)
    	{
	    	//set the parameters to the device 
	    	err = m_Device.setDeviceParameters(m_DeviceParameters);
	    	if(err!= Common.ERROR_CODE.SUCCESS)
	    	{
				String errorString = Common.getErrorText(err);
	    		messageBox(this, errorString, "SampleCode: setDeviceParameters Error");
	    	}
	    	
	    	if(err== Common.ERROR_CODE.SUCCESS)
	    	{
	    		//open the device
	    		err = m_Device.openDevice();
	        	if(err!= Common.ERROR_CODE.SUCCESS)
	        	{
	    			String errorString = Common.getErrorText(err);
			   		debug.d("SampleCode", "Error from openDevice: " + errorString);
	        		messageBox(this, errorString, "SampleCode 0: openDevice Error");
	        	}
	    	}
	    	
	    	if(err== Common.ERROR_CODE.SUCCESS)
	    	{
	    		//activate ASB sending
	    		err = m_Device.activateASB(true, true, true, true, true, true);
	        	if(err!= Common.ERROR_CODE.SUCCESS)
	        	{
	    			String errorString = Common.getErrorText(err);
			   		debug.d("SampleCode", "Error from activateASB: " + errorString);
	        		messageBox(this, errorString, "SampleCode 1: openDevice Error");
	        	}
	    	}
    	}    	
    }

    
    /**************************************************************************************
     * closeButtonClicked
     * 
     * @param view
     **************************************************************************************/
    public void closeButtonClicked(View view)
    {
    	Common.ERROR_CODE err = m_Device.closeDevice();
    	if(err!= Common.ERROR_CODE.SUCCESS)
    	{
			String errorString = Common.getErrorText(err);
    		messageBox(this, errorString, "closeDevice Error");
    	}
    	
    	//reset the ASB indicators
        TextView textViewASBOnline =(TextView)findViewById(R.id.textViewASBOnline);
        TextView textViewASBCover =(TextView)findViewById(R.id.textViewASBCover);
        TextView textViewASBPaper =(TextView)findViewById(R.id.textViewASBPaper);
        TextView textViewASBSlip =(TextView)findViewById(R.id.textViewASBSlip);
        textViewASBOnline.setBackgroundColor(0xFF707070);	//gray
        textViewASBCover.setBackgroundColor(0xFF707070);	//gray
        textViewASBPaper.setBackgroundColor(0xFF707070);	//gray
		textViewASBSlip.setBackgroundColor(0xFF707070);		//gray
    	
    }
    
    byte [] getTestData(int len)
    {
    	byte [] data= new byte[len];
    	for(int i=0;i<len;i++)
    	{
    		data[i]=(byte) (i%10);
    		
    	}
    	return data;
    }
    
    byte [] getTestData2( byte [] data2)
    {
    	int len= data2.length;
    	
    	byte [] data= new byte[len];
    	
    	byte[] start3 =  { 0x1b,0x23,0x23,0x55,0x50,0x50,0x50,0x0,0x0,0x0,0x2,0x1,0x0,0x0,0x0,0x2 };
    	
    	
    	for(int i=0;i<len;i++)
    	{
    		data[i]=(byte) (i%10);
    		
    	}
    	return data;
    }
      
    public void printStringButtonClicked(View view)
    {
    	
    	if(m_Device.isDeviceOpen()==false)
		{
 
    		messageBox(this, "Device is not open", "printString Error");
    		return  ;
    		
    	}
    	try {
			test_qr6() ;
			sleep(300);
			new BmpThread().start();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sleep(200);
		sleep(50);
		return   ;
    }
    void test_qr6() throws UnsupportedEncodingException
    {  
    	

    	//code 128
    	//sendCommand(0x1d,0x6b,0x49,0x05,0x31,0x32,0x33,0x34,0x35);
    	byte []data1  ="功能测试：根据 n 的值来设置字符打印方式".getBytes("cp936");
    	
    	//qr code,not supported by all platform
    	//sendCommand(0x1B,0x23,0x23,0x51,0x50,0x49,0x58,0xa);
    	byte []data2  ={0x1d,(byte)0x28,(byte)0x6b,(byte)0x9a,(byte)0x00,(byte)0x31,(byte)0x50,(byte)0x30,(byte)
    			0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)
    			0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)
    			0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)
    			0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)
    			0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)
    			0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)
    			0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)
    			0x30,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x34,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)
    			0x51,(byte)0x64,(byte)0x69,(byte)0x6E,(byte)0x47,(byte)0x35,(byte)0x36,(byte)0x37,(byte)0x38,(byte)0x39,(byte)
    			0xB4,(byte)0x38,(byte)0x52,(byte)0x16,(byte)0x55,(byte)0x73,(byte)0x63,(byte)0x15,(byte)0x15,(byte)0xA3,(byte)
    			0x4B,(byte)0x00,(byte)0xC8,(byte)0xA1,(byte)0x35,(byte)0x34,(byte)0xDC,(byte)0x36,(byte)0x99,(byte)0x18,(byte)
    			0x89,(byte)0x76,(byte)0xD1,(byte)0x3C,(byte)0xF5,(byte)0x70,(byte)0x89,(byte)0xA8,(byte)0x5F,(byte)0xE6,(byte)
    			0x2C,(byte)0x42,(byte)0x5C,(byte)0xE6,(byte)0x5C,(byte)0x20,(byte)0x5A,(byte)0xEB,(byte)0xF2,(byte)0x1A,(byte)
    			0x5D,(byte)0xE2,(byte)0x55,(byte)0x74,(byte)0x59,(byte)0xE1,(byte)0x50,(byte)0x63,(byte)0x5A,(byte)0xE4,(byte)
    			0x8C,(byte)0x48,(byte)0x54,(byte)0x92,(byte)0x23,(byte)0xB9,(byte)0x2D,(byte)0xE3,(byte)0x55,(byte)0xA9,(byte)
    			0x6D,(byte)0x1d,(byte)0x28,(byte)0x6b,(byte)0x9a,(byte)0x00,(byte)0x31,(byte)0x51,(byte)0x30,0x1b,0x4a,0x30,0x1b,0x4a,0x30,0x1b,0x4a,0x30,0x1b,0x4a,0x30,0x1b,0x61,1};
    	
    	byte []data3=byteMerger(data1,data2);
    	data3=byteMerger(data3,data3);
     
    	mOutputStream.write(data3);
    }

    /**************************************************************************************
     * printStringButtonClicked
     * 
     * @param view
     **************************************************************************************/
  /*  public void printStringButtonClicked(View view)
    {
    	ERROR_CODE err = ERROR_CODE.SUCCESS;
    	
    	FONT font = FONT.FONT_A;
    	Boolean bold = false;
    	Boolean underlined = false;
    	Boolean doubleHeight = false;
    	Boolean doubleWidth = false;
     
    	sleep(1000);
    	try
    	{
    		//get UI elements
    		EditText editTextPrintString = (EditText)findViewById(R.id.editTextPrintString);
            RadioGroup radioGroupFont = (RadioGroup)findViewById(R.id.radioGroupFont);
            RadioGroup radioGroupDoublePrint = (RadioGroup)findViewById(R.id.radioGroupDoublePrint);
            RadioGroup radioGroupAlignment = (RadioGroup)findViewById(R.id.radioGroupAlignment);
            CheckBox checkBoxBold  = (CheckBox)findViewById(R.id.checkBoxBold);
            CheckBox checkBoxUnderlined  = (CheckBox)findViewById(R.id.checkBoxUnderlined);
    		
			//set font
	    	switch(radioGroupFont.getCheckedRadioButtonId())
	    	{
	    		case R.id.radioFontA:
	    	    	font = FONT.FONT_A;
	    	    	break;
	    		case R.id.radioFontB:
	    	    	font = FONT.FONT_B;
	    	    	break;
	    	}
	    	
	    	//set boldness
	    	if(checkBoxBold.isChecked())
	    	{
	            bold = true;
	    	}
	    	else
	    	{
	            bold = false;
	    	}
	    		
	    	//set underlined
	    	if(checkBoxUnderlined.isChecked())
	    	{
	            underlined = true;
	    	}
	    	else
	    	{
	            underlined = false;
	    	}
	    	
			//set double print
	    	switch(radioGroupDoublePrint.getCheckedRadioButtonId())
	    	{
	    		case R.id.radioX1Y1:
	    	        doubleHeight = false;
	    	        doubleWidth = false;
	    	    	break;
	    		case R.id.radioX2Y1:
	    	        doubleHeight = false;
	    	        doubleWidth = true;
	    	    	break;
	    		case R.id.radioX1Y2:
	    	        doubleHeight = true;
	    	        doubleWidth = false;
	    	    	break;
	    		case R.id.radioX2Y2:
	    	        doubleHeight = true;
	    	        doubleWidth = true;
	    	    	break;
	    	}
	    	
	    	if(m_Device.isDeviceOpen()==true)
	    	{
	    		//set print alignment
	        	switch(radioGroupAlignment.getCheckedRadioButtonId())
	        	{
	        		case R.id.radioLeft:
	        			err = m_Device.selectAlignment(ALIGNMENT.LEFT);
	    	    		if(err!=ERROR_CODE.SUCCESS)
	    	    		{
	    	    			String errorString = Common.getErrorText(err);
	    	        		messageBox(this, errorString, "printString Error");
	    	    		}
	        			break;
	        		case R.id.radioCenter:
	        			err = m_Device.selectAlignment(ALIGNMENT.CENTER);
	    	    		if(err!=ERROR_CODE.SUCCESS)
	    	    		{
	    	    			String errorString = Common.getErrorText(err);
	    	        		messageBox(this, errorString, "printString Error");
	    	    		}
	        			break;
	        		case R.id.radioRight:
	        			err = m_Device.selectAlignment(ALIGNMENT.RIGHT);
	    	    		if(err!=ERROR_CODE.SUCCESS)
	    	    		{
	    	    			String errorString = Common.getErrorText(err);
	    	        		messageBox(this, errorString, "printString Error");
	    	    		}
	        			break;
	        	}	        	
	        	
	        	if(err==ERROR_CODE.SUCCESS)
	        	{
		        	//print string
		    		String sendString = editTextPrintString.getText().toString();
		    		err = m_Device.printString(sendString, font, bold, underlined, doubleHeight, doubleWidth);
		    		if(err!=Common.ERROR_CODE.SUCCESS)
		    		{
		    			String errorString = Common.getErrorText(err);
		        		messageBox(this, errorString, "printString Error");
		    		}
	        	}
	    	}
	    	else
	    	{
	    		messageBox(this, "Device is not open", "printString Error");
	    	}
	    }
	    catch(Exception e)
	    {
			messageBox(this, "Exception: " + e.toString() + " - " + e.getMessage(), "printString Error");
	    }
    	
    }    
*/
    /**************************************************************************************
     * curPaperButtonClicked
     * 
     * @param view
     **************************************************************************************/
    public void cutPaperButtonClicked(View view)
    {
    	Common.ERROR_CODE err = Common.ERROR_CODE.SUCCESS;
    	//test=32;
    	try
        {
	    	if(m_Device.isDeviceOpen()==true)
	    	{
	    		err = m_Device.cutPaper();
	    		if(err!=Common.ERROR_CODE.SUCCESS)
	    		{
	    			String errorString = Common.getErrorText(err);
	        		messageBox(this, errorString, "cutPaper Error");
	    		}	    		
	    	}
	    	else
	    	{
	    		messageBox(this, "Device is not open", "cutPaper Error");
	    	}
        }
        catch(Exception e)
        {
    		messageBox(this, "Exception:" + e.getMessage(), "cutPaper Error");
        }
    }    
    
    /**************************************************************************************
     * printTextFileButtonClicked
     * 
     * @param view
     **************************************************************************************/
    public void printTextFileButtonClicked(View view) 
    {
    	try {
			test_qr6() ;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
       	debug.e(" quck", "  printTextFileButtonClicked" );
    // new WriteThread().start();
    }
    
    String readTextFile(String filename) throws IOException 
    {
    	final StringBuilder sb = new StringBuilder();
    	final FileInputStream fs = new FileInputStream(new File(filename));
    	final BufferedReader br = new BufferedReader(new InputStreamReader(fs));
    	String s;
    	try {
	    	while((s = br.readLine()) != null)
	    	{
	    		sb.append(s);
	    		sb.append("\n");
	    	}
    	} finally {
    		if(br != null)
    			br.close();
    		if(fs != null)
    			fs.close();
    	}
    	return sb.toString();
    }
     
    void printTextFile(String filename) 
    {
    	Common.ERROR_CODE err = Common.ERROR_CODE.SUCCESS;

    	try
        {
    		String text = readTextFile(filename);
    		byte[] bs = text.getBytes("GB2312");

			if(m_Device.isDeviceOpen()==true)
	    	{
	    		Vector<Byte> data = new Vector<Byte>(bs.length);
	    		for(int i=0; i<bs.length; i++) {
	    			data.add(bs[i]);
	    		}
	    		
	    		err = m_Device.sendData(data);
	    		if(err!=Common.ERROR_CODE.SUCCESS)
	    		{
	    			String errorString = Common.getErrorText(err);
	        		messageBox(this, errorString, "cutPaper Error");
	    		}	    
	    		else {
		    		err = m_Device.cutPaper();
		    		if(err!=Common.ERROR_CODE.SUCCESS)
		    		{
		    			String errorString = Common.getErrorText(err);
		        		messageBox(this, errorString, "cutPaper Error");
		    		}
	    		}
	    	}
	    	else
	    	{
	    		messageBox(this, "Device is not open", "cutPaper Error");
	    	}
        }
        catch(Exception e)
        {
    		messageBox(this, "Exception:" + e.getMessage(), "cutPaper Error");
        }
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	if(requestCode == FileBrowser.ACTIVITY_CODE && resultCode == Activity.RESULT_OK) {
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				if (!bundle.containsKey("file")) {
					return;
				}

				String fpath = bundle.getString("file");

				File f = new File(fpath);

				if (!f.isDirectory()) {
					printTextFile(f.getAbsolutePath());
				}
			}
    	}
		super.onActivityResult(requestCode, resultCode, data);
	}


	public Common.ERROR_CODE CallbackMethod(CallbackInfo cbInfo)
	{
    	Common.ERROR_CODE retval = Common.ERROR_CODE.SUCCESS;
		
    	try
    	{
    		debug.e(" on CallbackMethod(CommonCallbackInfo cbInfo)"+cbInfo.receiveCount );
   		 
			//do nothing, ignore any general incoming data 
			
			debug.d(TAG,"Receiving data: "+ Integer.toString(cbInfo.receiveCount)+ " bytes");		 
			StringBuilder str = new StringBuilder();
			StringBuilder strBuild = new StringBuilder();
			for (int i = 0; i < cbInfo.receiveCount; i++) 
			{
				//USBPort.this.m_receiveBuffer.add(Byte.valueOf(USBPort.this.m_receiveData[i]));
				str.append(String.format(" %x", cbInfo.m_receiveData[i]));
				strBuild.append(String.format("%c", (char) cbInfo.m_receiveData[i]));
			}
			debug.d(TAG, "onC= " + strBuild.toString());
			debug.d(TAG, "onx= " + str.toString()); 

			debug_str=debug_str+strBuild.toString();
			debug_strX=debug_strX+str.toString();
			if(debug_str.length()> (lcd_width*8/10))
			{
				debug_str=debug_str+"\n";
				
			}
			if(debug_strX.length()> (lcd_width*8/10))
			{
				debug_strX=debug_strX+"\n";
				
			}			
    		/*
	    	switch(cbInfo.ReceivedDataType)
	    	{
	    		case GENERAL:
	    			debug.e(" on CallbackMethod(CommonCallbackInfo cbInfo)" );
	    			//do nothing, ignore any general incoming data 
	    			
					debug.d(TAG,"Receiving data: "+ Integer.toString(cbInfo.receiveCount)+ " bytes");		 
					StringBuilder str = new StringBuilder();
					StringBuilder strBuild = new StringBuilder();
					for (int i = 0; i < cbInfo.receiveCount; i++) 
					{
						//USBPort.this.m_receiveBuffer.add(Byte.valueOf(USBPort.this.m_receiveData[i]));
						str.append(String.format(" %x", cbInfo.m_receiveData[i]));
						strBuild.append(String.format("%c", (char) cbInfo.m_receiveData[i]));
					}
					debug.d(TAG, "onReceivedC= " + strBuild.toString());
					debug.d(TAG, "onReceivedx= " + str.toString()); 	
					
				
	    			break;
	    		case ASB:	//new ASB data came in
	    			debug.d("Sample", "new ASB data came in");
	    			//receiveAndShowASBData();
	    			break;
	    	}	*/
    	}
    	catch(Exception e)
    	{
    		messageBox(this, "callback method threw exception: " + e.toString() + " - " + e.getMessage(), "Callback Error");
    	}
    	
    	return retval;
	}
    
    
    /**************************************************************************************
     * receiveAndShowASBData
     * 
     **************************************************************************************/
    private void receiveAndShowASBData()
    {
		//retrieve current ASB data and show it
		m_ASBData = m_Device.getASB();
		showASBStatus();
    }

    
    /**************************************************************************************
     * showASBStatus
     * 
     **************************************************************************************/
    private void showASBStatus()
    {
		try
		{
	    	this.runOnUiThread(
				new Runnable()
				{
					public void run()
					{
						//get the status indicators from the GUI
		    	        TextView textViewASBOnline =(TextView)findViewById(R.id.textViewASBOnline);
		    	        TextView textViewASBCover =(TextView)findViewById(R.id.textViewASBCover);
		    	        TextView textViewASBPaper =(TextView)findViewById(R.id.textViewASBPaper);
		    	        TextView textViewASBSlip =(TextView)findViewById(R.id.textViewASBSlip);

						if(m_ASBData!=null)
						{
							//light up indicators
							if(m_ASBData.Online)
							{
			    	        	textViewASBOnline.setBackgroundColor(0xFF00E000);	//green
							}
							else
							{
			    	        	textViewASBOnline.setBackgroundColor(0xFFE00000);	//red
							}
							if(m_ASBData.CoverOpen)
							{
								textViewASBCover.setBackgroundColor(0xFFE00000);	//red
							}
							else
							{
			    	        	textViewASBCover.setBackgroundColor(0xFF00E000);	//green
							}
							if(m_ASBData.PaperOut==true)
							{
								textViewASBPaper.setBackgroundColor(0xFFE00000);	//red
							}
							else
							{
								if(m_ASBData.PaperNearEnd==true)
								{
									textViewASBPaper.setBackgroundColor(0xFFE0E000);	//yellow
								}
								else
								{
									textViewASBPaper.setBackgroundColor(0xFF00E000);	//green
								}
							}
							if(m_ASBData.SlipSelectedAsActiveSheet)
							{
								textViewASBSlip.setBackgroundColor(0xFF00E000);	//green
							}
							else
							{
								textViewASBSlip.setBackgroundColor(0xFF707070);	//gray
							}
						}
					} //public void run()
				}); //this.runOnUiThread(
    	}
		catch(Exception e)
		{
    		messageBox(this, "receiveAndShowASBData threw exception: " + e.toString() + " - " + e.getMessage(), "ReceiveAndShowASBData Error");
		}
    }
    
    /**************************************************************************************
     * messageBox
     * Shows a standard message box with OK button. 
     * Note: the program execution does not stop!
     * @param context
     * @param message
     * @param title
     **************************************************************************************/
    public void messageBox(final Context context, final String message, final String title)
    {
    	this.runOnUiThread(
			new Runnable()
			{
				public void run()
				{
					final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
					//alertDialog.setTitle(title);
					alertDialog.setMessage(message);
					alertDialog.setButton("OK", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{               
							alertDialog.cancel();    	
						}
					});    	
					alertDialog.show();    	
				}
			}
    	);
    }
    
    
    
//------------------------------------
   	private class WriteThread extends Thread {
   		int  action_code;

   		public WriteThread( ) 
   		{
   		 
   		} 
   		
   		public void run() 
   		{
            byte[] bytes;
            
    		/*test=33;
			if(test==33)
			{
				return; 
				//sendCharacterDemo();
			}*/	
 		/*	test++;
   		if(test==5)
 		{
			
			//sendCharacterDemo();
 		}
   		else if(test >=6)
   		{
			return;
			
   		}*/
			// new BmpThread().start();
			 /*
   		String str = "深圳市好德芯电子科技有限公司公司简介"+	
				"好德芯电子科技有限公司， 成立于 2008 年， 位于中国的“硅谷”－深圳市, 好德芯电子是 《深圳市高新技术企业》 ."+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"机及整体解决方案提供商。"+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"《国家高新技术企业》 ，是一家二维码验证/支付终端/手机支付终端/4G 无线智能终端/O2O 智能终端/云 POS 等产品整  "+
				"机及整体解决方案提供商。";
 		//bytes = "1 2 3 4 5 6 7 8 9 a b c d e f g h i j k l m n o p q r stuvwxyz".getBytes("cp936");
 		bytes=str.getBytes("cp936");
   		//sendCharacterDemo();
   		
   		bytes=new byte[3];
   		bytes[0]=0x1b;    bytes[0]=0x4a;  bytes[0]=0x30;      
   		Byte[] byteList0 = toBytes(bytes);
         Vector<Byte> vector0 = new Vector<Byte>(Arrays.asList(byteList0));
         // m_Device.sendData(vector0);
        //  m_Device.sendData(vector0);
         
     	//sendCharacterDemo();
         new BmpThread().start();
     	// m_Device.sendData(vector0);
     	// m_Device.sendData(vector0);
     	 
   		//bytes = "我们希望并培养每一位员工与公".getBytes("cp936");
   		Byte[] byteList = toBytes(bytes);
         Vector<Byte> vector = new Vector<Byte>(Arrays.asList(byteList));
    
         // m_Device.sendData(vector);
  */
            
            byte [] xx={0x1d, 0x56, 0x30}; 
            while (true)
            {
            	
            	
               try {
				bytes = "我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公我们希望并培养每一位员工与公".getBytes("cp936");
				bytes =byteMerger(xx,bytes);
				SendLongDataToUart(bytes,bytes.length);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       
               
            	try {
					sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

  
           
    		
   		}
   			
   	}
   	
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }  
	  class OutputStream  {
		  

		public OutputStream( ) {
			 
		} 
	 void write(byte[] bytes)
	 {
		 
         //	bytes = "倍高命令倍高命令倍高命令".getBytes("cp936");
			Byte[] byteList = toBytes(bytes);
		   Vector<Byte> vector = new Vector<Byte>(Arrays.asList(byteList));
		//   Vector<Byte> vector = new Vector<Byte>();

		    // m_Device.sendData(vector);  
		 m_Device.sendData(vector);
		 //test(bytes);
 				 
	 }
	 
	 void write(int[] bytes)
	 {
		 
         //	bytes = "倍高命令倍高命令倍高命令".getBytes("cp936");
			Byte[] byteList = toBytes(bytes);
		     Vector<Byte> vector = new Vector<Byte>(Arrays.asList(byteList));
		  //   Vector<Byte> vector = new Vector<Byte>();

		     m_Device.sendData(vector);  		 
	 }		
	 
	 void write(int data)
	 {
		   int[] bytes =new int[1];
		   bytes[0]=data;
         //	bytes = "倍高命令倍高命令倍高命令".getBytes("cp936");
			Byte[] byteList = toBytes(bytes);
		     Vector<Byte> vector = new Vector<Byte>(Arrays.asList(byteList));
		  //   Vector<Byte> vector = new Vector<Byte>();

		     m_Device.sendData(vector);  		 
	 }		 
	} 	
   	
    Byte[]   toBytes(byte[] bytes)
    {
    	 Byte[] byteList =new Byte[bytes.length];
    	int i;
    	 for(i=0;i<bytes.length;i++)
    	 {
    		 
    		 byteList[i]= bytes[i];
    	 }
    	return    byteList; 
    }
    Byte[]   toBytes(int[] bytes)
    {
    	 Byte[] byteList =new Byte[bytes.length];
    	int i;
    	 for(i=0;i<bytes.length;i++)
    	 {
    		 
    		 byteList[i]= (byte)bytes[i];
    	 }
    	return    byteList; 
    } 
    void sendCommand(int... command) {
		for (int i = 0; i < command.length; i++) {
			mOutputStream.write(command[i]);
			// debug.e(TAG,"command["+i+"] = "+Integer.toHexString(command[i]));
		}
	}   
    
    
	private void sendCharacterDemo() {

	debug.e(TAG, "#########sendCharacterDemo##########");//,0x1B,0x23,0x46
	sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x0e ); // taiwan
	try {
		mOutputStream.write("撥出受固定撥號限制".getBytes("Big5"));
		mOutputStream.write("目前無法連上這個網路".getBytes("Big5"));
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		sendCommand(0x0a);
	
	try {
 
		sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 39); //  阿拉伯语
		//mOutputStream.write("يضصثقثقغنهفهخغعفهخغتخهتنميبتسينمبتسيمنبت".getBytes("cp864"));
		mOutputStream.write("يضصثقثقغنهفهخغعفهخغتخهتنميبتسينمبتسيمنبت".getBytes("cp1256"));
		mOutputStream.write("يضصثقثقغنهفهخغعفهخغتخهتنميبتسينمبتسيمنبت".getBytes("cp1256"));
		sendCommand(0x0a);
		sendCommand(0x0a);
		sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x0f); // china
		sendCommand(0x1D, 0x21, 0x01); // double height
		mOutputStream.write("倍高命令".getBytes("cp936"));
		sendCommand(0x0a);
		sendCommand(0x1D, 0x21, 0x00); // cancel double height
		mOutputStream.write("取消倍高命令".getBytes("cp936"));
		sendCommand(0x0a);
		sendCommand(0x1D, 0x21, 0x10); // double width
		mOutputStream.write("倍宽命令".getBytes("cp936"));
		sendCommand(0x0a);
		sendCommand(0x1D, 0x21, 0x00); // cancel double width
		mOutputStream.write("取消倍宽命令".getBytes("cp936"));
		sendCommand(0x0a);
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
		mOutputStream.write("english test".getBytes());
	sendCommand(0x0a);

	sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x20); // thailand
	
	try {
		mOutputStream.write("แต่ถ้าหากเธอไม่สามารถช่วยพี่ชแต่ถ้าหากเธอไม่สามารถช่วยพี่ชแต่ถ้าหากเธอไม่สามารถช่วยพี่ชแต่ถ้าหากเธอไม่สามารถช่วยพี่ช"
				.getBytes("cp874"));
		int size,i;
		String strd="แต่ถ้าหากเธอไม่สามารถช่วยพี่ชแต่ถ้";
		byte []buffer  =strd.getBytes("cp874");
		size=buffer.length;
		StringBuilder str = new StringBuilder();
		StringBuilder strBuild = new StringBuilder();
		for (i = 0; i < size; i++) {

			str.append(String.format("%02x", buffer[i]));
			strBuild.append(String.format("%c", (char) buffer[i]));
		}
		debug.e(TAG, "oxxxC= " + strBuild.toString());
		debug.e(TAG, "oxxxX= " + str.toString());
		
		sendCommand(0x0a);
		//40个泰文
		mOutputStream.write("แต่ถ้าหากเธอแต่ถ้าหากเธอแต่ถ้าหากเธอแต่ถ้าหากเธอ".getBytes("cp874"));
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		sendCommand(0x0a);

	sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x22); // russia
	try {
		mOutputStream
				.write("У этого сайта проблемы с сертификатом безопасности."
						.getBytes("CP1251"));
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	sendCommand(0x0a);
	//sleep(200);
	sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x0f); // china
	//sleep(200);
	sendCommand(0x1D, 0x42, 0x1 ); //使能反白 
	try {
		mOutputStream.write("反白打印测试反白打印测试反白打印测试反白打印测试反白打印测试反白打印测试反白打印测试反白打印测试 ".getBytes("cp936"));
		sendCommand(0x0a);
		//mOutputStream.write("反白打印测试   ".getBytes("cp936"));
		sendCommand(0x0a);
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	sendCommand(0x1D, 0x42, 0x0 ); //关闭反白
}
	
	final static int Max_Dot=576;
	public void PrintBmp(int startx, Bitmap bitmap) throws IOException {
		// byte[] start1 = { 0x0d,0x0a};
		byte[] start2 = { 0x1D, 0x76, 0x30, 0x30, 0x00, 0x00, 0x01, 0x00 };

		int width = bitmap.getWidth() + startx;
		int height = bitmap.getHeight();
		Bitmap.Config m =bitmap.getConfig();
		// 332  272  ARGB_8888
		debug.e(TAG,"width:  "+width+" height :"+height+"   m:"+ m);
		if (width > Max_Dot)
			width = Max_Dot;
		int tmp = (width + 7) / 8;
		byte[] data = new byte[tmp];
		byte xL = (byte) (tmp % 256);
		byte xH = (byte) (tmp / 256);
		start2[4] = xL;
		start2[5] = xH;
		start2[6] = (byte) (height % 256);
		;
		start2[7] = (byte) (height / 256);
		;
		byte SendBuf[] = new byte[start2.length+data.length*height];
		Arrays.fill(SendBuf,(byte)0);	
		System.arraycopy(start2,0,SendBuf,0, start2.length); 
		//mOutputStream.write(start2);
		 try { Thread.sleep(1); } catch (InterruptedException e) { }
		//System.arraycopy(src,0,byteNumCrc,0,4); 
		//System.arraycopy(b,0,SendBuf,0, count); 
		
		
		for (int i = 0; i < height; i++) {

			for (int x = 0; x < tmp; x++)
				data[x] = 0;
			
			for (int x = startx; x < width; x++) {
				int pixel = bitmap.getPixel(x - startx, i);
				if (Color.red(pixel) == 0 || Color.green(pixel) == 0
						|| Color.blue(pixel) == 0) {
					// 高位在左，所以使用128 右移
					data[x / 8] += 128 >> (x % 8);// (byte) (128 >> (y % 8));
				}
			}
			
			
			//mOutputStream.write(data);
			System.arraycopy(data,0,SendBuf,(start2.length+data.length*i), data.length); 
			//  try { Thread.sleep(50); } catch (InterruptedException e) { }
			  
		}
		mOutputStream.write(SendBuf);
	}
	
	
	
	private class BmpThread extends Thread {
		public BmpThread() {
		}

		public void run() {
			super.run();
			 
	 
			 
		 
			try {
				Resources r = getResources();
				// 以数据流的方式读取资源
				@SuppressLint("ResourceType") InputStream is = r.openRawResource(R.drawable.test2);
				BitmapDrawable bmpDraw = new BitmapDrawable(is);
				Bitmap bmp = bmpDraw.getBitmap();
				 PrintBmp(0, bmp);
			 

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			 
			}
		 
		}

		
	}

	
		void Time_Check_Start() {
			time.setToNow(); // ȡ��ϵͳʱ�䡣
			TimeSecond = time.second;
			

		}

		boolean TimeIsOver(int second) {

			time.setToNow(); // ȡ��ϵͳʱ�䡣
			int t = time.second;
			if (t < TimeSecond) {
				t += 60;
			}

			if (t - TimeSecond > second) {
				return true;
			}
			return false;
		}
		
		
		void flow_begin()
		{
			
		 
			debug.i(TAG,"flow_begin ");
			
		}
		void flow_end()
		{
			
		 
			debug.i(TAG,"flow_end ");
		}
				
		
		boolean  flow_check_and_Wait(int timeout) 
		{
			
			 
					 
			debug.e(TAG,"Get_Printer flow timeout");
			return false;
			
 

		}	
	
		private void sleep(int ms) {
			// debug.d(TAG,"start sleep "+ms);
			try {
				Thread.sleep(ms);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// debug.d(TAG,"end sleep "+ms);
		}	
	
		 
		// get current fw version
		public boolean get_fw_version() {
 
			if(m_Device.isDeviceOpen()==true)
			{


			}
	    	else
	    	{
	    		messageBox(this, "Device is not open", "printString Error");
	    		return false;
	    		
	    	}
			debug_str="";
			debug_strX="";
			//SampleCodeActivity.strVer = new StringBuilder();
			//SampleCodeActivity.ver_start_falg = true;
			byte[] start3 = { 0x1B, 0x23, 0x56,  0x1D , 0x67 , 0x66 };
			//byte[] start3 = { 0x1b,0x76 };
			mOutputStream.write(start3);
			//sendCommand(0x1b,0x76);
			
			/*
			byte[] start2 = { 0x1D, 0x67, 0x66 };
			try {
				mOutputStream.write(start2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//sendCommand(0x1b, 0x4a, 0x30,0x1b, 0x4a, 0x30,0x1b, 0x4a, 0x30,0x1b, 0x4a, 0x30,0x1b, 0x4a, 0x30); // line feed

			sleep(200);
			strVer = new StringBuilder("900");
			oldVer = new StringBuilder("900");
			return true ;
		}
		public boolean get_status() {
			 
			if(m_Device.isDeviceOpen()==true)
			{


			}
	    	else
	    	{
	    		messageBox(this, "Device is not open", "printString Error");
	    		return false;
	    		
	    	}
			//debug_str="";
			//debug_strX="";
			//SampleCodeActivity.strVer = new StringBuilder();
			//SampleCodeActivity.ver_start_falg = true;
			//byte[] start3 = { 0x1B, 0x23, 0x56,  0x1D , 0x67 , 0x66 };
			//byte[] start3 = { 0x10,0x04,4 };
			byte[] start3 = { 0x1d,0x61,4 };
			mOutputStream.write(start3);
			//sendCommand(0x1b,0x76);
			
			/*
			byte[] start2 = { 0x1D, 0x67, 0x66 };
			try {
				mOutputStream.write(start2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//sendCommand(0x1b, 0x4a, 0x30,0x1b, 0x4a, 0x30,0x1b, 0x4a, 0x30,0x1b, 0x4a, 0x30,0x1b, 0x4a, 0x30); // line feed

			//sleep(200);
			sleep(50);
			return true ;
		}
				
		void int2ByteAtr(int pData, byte sumBuf[]) {
			for (int ix = 0; ix < 4; ++ix) {
				int offset = ix * 8;
				sumBuf[ix] = (byte) ((pData >> offset) & 0xff);
			}

		}
		
		void Get_Buf_Sum(byte dataBuf[], int dataLen, byte sumBuf[]) {

			int i;
			long Sum = 0;
			// byte[] byteNum = new byte[8];
			long temp;

			for (i = 0; i < dataLen; i++) {
				if (dataBuf[i] < 0) {
					temp = dataBuf[i] & 0x7f;
					temp |= 0x80L;

				} else {
					temp = dataBuf[i];
				}
				Sum += temp;
				temp = dataBuf[i];

			}

			for (int ix = 0; ix < 4; ++ix) {
				int offset = ix * 8;
				sumBuf[ix] = (byte) ((Sum >> offset) & 0xff);
			}

		}
		
		
		private class SendThread extends Thread {
			InputStream is;
			public SendThread(InputStream is ) {
				this.is=is;
			}
			
	 
			public void run() {
				try {
					 
					int count = is.available();
					debug.e("SendFileToUart "+count );
					byte[] b = new byte[count];
					is.read(b);
					mOutputStream.write(b);
				 
					debug.e("quck2", "all data have send!!  "   );
					sleep(3000);
					  
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
			 
			 
				//HdxUtil.SetPrinterPower(0);	
			}
			 
			}
		}	
		public void SendFileToUart(InputStream is ) 
		{
			new SendThread(is).start();

			//handler.sendMessage(handler.obtainMessage(ENABLE_BUTTON, 1, 0,null));
		}
	

		private class UpdateFWThread extends Thread {
			int type;
			public UpdateFWThread(int type) {
				this.type=type;
			}
		
			@SuppressLint("Wakelock")
			
			public void run() {
				FILE_NAME=FILE_NAME+"2";	
				
		
				
				byte[] command_head = { 0x1B, 0x23, 0x23, 0x55, 0x50, 0x50, 0x47 };
				int temp;
				super.run();

		 
			  
		
				
				Message message = new Message();
				handler.sendMessage(handler.obtainMessage(SHOW_PROGRESS, 1, 0,getResources().getString(R.string.itpw)  ));
				try {
					if(type == 0 )
					{
						 //SendLongDataToUart(BinFileNum,start2,100,50);
							Resources r =getResources();;
							InputStream is = r.openRawResource(BinFileNum);
							int count = is.available();
							
							byte[] b = new byte[count];
							is.read(b);
							byte[] byteNum = new byte[4];
							byte[] byteNumCrc =  new byte[4];
							byte[] byteNumLen =  new byte[4];
							 
							
							//get crc 
							Get_Buf_Sum(b,count,byteNum);// 17	01 7E 00   CRC
							System.arraycopy(byteNum,0,byteNumCrc,0,4); 
							Log.e("quck2", "crc0  "+ String.format("0x%02x", byteNum[0] )  );	
							Log.e("quck2", "crc1  "+ String.format("0x%02x", byteNum[1] )	);	
							Log.e("quck2", "crc2  "+ String.format("0x%02x", byteNum[2] )	);	
							Log.e("quck2", "crc3  "+ String.format("0x%02x", byteNum[3] )  );	
							 
							
							//get len
							int2ByteAtr(count,byteNum); //58 54 01 00	LEN
							System.arraycopy(byteNum,0, byteNumLen,0,4);
							Log.e("quck2", "len0  "+ String.format("0x%02x", byteNum[0] )  );	
							Log.e("quck2", "len1  "+ String.format("0x%02x", byteNum[1] )	);	
							Log.e("quck2", "len2  "+ String.format("0x%02x", byteNum[2] )	);	
							Log.e("quck2", "len3  "+ String.format("0x%02x", byteNum[3] )  );
							
							//send command_head
							print(command_head);
							//send crc
							print(byteNumCrc);
							//send len
							print(byteNumLen);
							//send bin file							
							SendLongDataToUart(b, b.length);
							Log.e("","update fw  ," +b.length );
					}
					else
					{
					//	SendLongDataToUart(BinFile,start2,100,50);
					}
					debug.e("quck2", "all data have send!!  ");
					sleep(3000);
					//get_fw_version();
					message = new Message();
					message.what = UPDATE_FW;
					handler.sendMessage(message);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				
					try {
						sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// HdxUtil.SetPrinterPower(0);
				}
				
				handler.sendMessage(handler.obtainMessage(ENABLE_BUTTON, 1, 0, null));
				
			}
		}		
	
		public void SendLongDataToUart(int fileID,byte [] command_head,int delay_time,int delay_time2 ) 
		{
			byte[] byteNum = new byte[4]; 
			byte[] byteNumCrc = new byte[4];
			byte[] byteNumLen = new byte[4];
		 	int i;
			int temp;
			debug.e(TAG,"  SendLongDataToUart");
			flow_begin();
			try {
					Resources r =getResources();;
					InputStream is = r.openRawResource(fileID);
					int count = is.available();
					byte[] b = new byte[count];
					is.read(b);


					debug.e("quck2", " read file is .available()= "+ count  );	
					//get command HEAD
				  
					
					//get crc 
					Get_Buf_Sum(b,count,byteNum);// 17	01 7E 00   CRC
					System.arraycopy(byteNum,0,byteNumCrc,0,4); 
					debug.e("quck2", "crc0  "+ String.format("0x%02x", byteNum[0] )  );	
					debug.e("quck2", "crc1  "+ String.format("0x%02x", byteNum[1] )	);	
					debug.e("quck2", "crc2  "+ String.format("0x%02x", byteNum[2] )	);	
					debug.e("quck2", "crc3   "+ String.format("0x%02x", byteNum[3] )  );	
					 
					int block_size=64;
					//get len
					//count=block_size;
					int2ByteAtr(count,byteNum); //58 54 01 00	LEN
					System.arraycopy(byteNum,0, byteNumLen,0,4);
					debug.e("quck2", "len0  "+ String.format("0x%02x", byteNum[0] )  );	
					debug.e("quck2", "len1  "+ String.format("0x%02x", byteNum[1] )	);	
					debug.e("quck2", "len2  "+ String.format("0x%02x", byteNum[2] )	);	
					debug.e("quck2", "len3  "+ String.format("0x%02x", byteNum[3] )  );
					
					//send command_head
					mOutputStream.write(command_head);
					//send crc
					mOutputStream.write(byteNumCrc);
					//send len
					mOutputStream.write(byteNumLen);

					//mOutputStream.write(b);
					
					
					byte SendBuf[] = new byte[count  +block_size-1];
					Arrays.fill(SendBuf,(byte)0);
					//send bin file
					System.arraycopy(b,0,SendBuf,0, count); 					
					//temp= (count +63)/64;
					temp= (count )/block_size;
					byte[] databuf= new byte[block_size]; 
					//Arrays.fill(SendBuf,(byte)0Xff);
					//sleep(delay_time);
					for(i=0;i<temp-1;i++)
					{
						System.arraycopy(SendBuf,i*block_size,databuf,0,block_size); 
						
						//if((i%2) == 0)
						{
							//sleep(delay_time2);
							
						}
						debug.i("quck2", " updating ffont finish:"  +((i+1)*100)/temp +"%");	
						iProgress=((i+1)*100)/temp;
						handler.sendMessage(handler.obtainMessage(REFRESH_PROGRESS, 1, 0,null));
						mOutputStream.write(databuf);
						//flow_check_and_Wait(10);
						//sleep(delay_time2);
						
					}
					
					debug.i("quck2", "all data have send!!  "   );
					sleep(3000);
					  
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
			 
				flow_end();
				//HdxUtil.SetPrinterPower(0);	
			}

			handler.sendMessage(handler.obtainMessage(ENABLE_BUTTON, 1, 0,null));
		}
		
		
		private void test(byte[] content)
		{
			try
			{
				
				// 如果手机插入了SD卡，而且应用程序具有访问SD的权限
				if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED))
				{
					// 获取SD卡的目录
					File sdCardDir = Environment.getExternalStorageDirectory();
					File targetFile = new File(sdCardDir
						.getCanonicalPath() + FILE_NAME);
					// 以指定文件创建 RandomAccessFile对象
					RandomAccessFile raf = new RandomAccessFile(
						targetFile, "rw");
					// 将文件记录指针移动到最后
					raf.seek(targetFile.length());
					// 输出文件内容
					raf.write(content);
					// 关闭RandomAccessFile				
					raf.close();
				}	
				
				
				
	 
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		

		void  print(byte [] bs)
		{
			Vector<Byte> data = new Vector<Byte>(bs.length);
			for(int i=0; i<bs.length; i++) {
 
			data.add(bs[i]);
			}
			
			m_Device.sendData(data);
		}
		

		public void SendLongDataToUart(byte[] b ,	int count  ) 
		{
 
			 
	 
			int block_size=0x1000;
		 	int i;
			int temp;
		    int delay_time =50;
		    int delay_time2=0;
		    count=b.length;
		  
			try {
				 
					if(b.length<=block_size)
					{
						
						print(b);
						return;
					}
					
					byte[] databuf= new byte[block_size]; 
					temp= (count )/block_size;
					//handler.sendMessage(handler.obtainMessage(SHOW_PROGRESS, 1, 0,null));
					for(i=0;i<temp;i++)
					{
						System.arraycopy(b,i*block_size,databuf,0,block_size); 
 
						debug.i("quck2", " updating ffont finish:"  +((i+1)*100)/temp +"%");	
						iProgress=((i+1)*100)/temp;
						handler.sendMessage(handler.obtainMessage(REFRESH_PROGRESS, 1, 0,null));
						print(databuf);
			 
						sleep(delay_time2);
						
					}						
					databuf= new byte[(b.length &(block_size -1) )] ; 
					int dd = b.length &(block_size -1)  ;
					System.arraycopy(b,i*block_size,databuf,0,dd); 
					print(databuf); 
	 
	
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
			 
				 
				//HdxUtil.SetPrinterPower(0);	
			}
			sleep(delay_time);
		// handler.sendMessage(handler.obtainMessage(HIDE_PROGRESS, 1, 0,null));
  
					  
		}		
	 
		
	 		
	 	
}

 
   
   	
   	
	 