package com.usbsdk.sample;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

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
import com.usbsdk.CallbackInterface;
import com.usbsdk.ASBStatus;
import com.usbsdk.CallbackInfo;
import com.usbsdk.Device;
import com.usbsdk.DeviceParameters;
import com.usbsdk.sample.R;
import comon.error.Common;
import comon.error.Common.ALIGNMENT;
import comon.error.Common.ERROR_CODE;
import comon.error.Common.FONT;
import comon.error.Common.PORT_TYPE;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
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
		UsbManager um = ((UsbManager) context.getSystemService("usb"));

		UsbDevice usbdev = getUsbDevice(um);

		if (usbdev != null) {

			// get requestPermission
			if (!um.hasPermission(usbdev)) {
				postRequestPermission(context, um, usbdev);

				return Common.ERROR_CODE.ERROR_OR_NO_ACCESS_PERMISSION;
			}

			return Common.ERROR_CODE.SUCCESS;
		}

		return Common.ERROR_CODE.NO_USB_DEVICE_FOUND;
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
			
			debug.d(TAG, "usb device : " + String.format("%1$04X:%2$04X", dev.getVendorId(), dev.getProductId()));

/*
			if (dev.getVendorId() == 0x0485|| 0x0BDA ==dev.getVendorId()   ) {
				debug.d(TAG, "usb device : " + String.format("%1$04X:%2$04X", dev.getVendorId(), dev.getProductId()));

				return dev;
			}
			if (dev.getVendorId() == 0xB000 ) {
				 
				return dev;
			}*/
			if (dev.getVendorId() == 0x28E9 ) {

				return dev;
			}
		}
		
		return null;
	}
	
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
		/*	ButtonCodeDemo = (Button) findViewById(R.id.ButtonCodeDemo);
			ButtonImageDemo = (Button) findViewById(R.id.ButtonImageDemo);
			
			
			ButtonCharacterDemo = (Button) findViewById(R.id.ButtonCharacterDemo);
			ButtonUpdateFontLib = (Button) findViewById(R.id.UpdateFontLib);*/
	 	ButtonGetVersion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boolean result;
				ButtonGetVersion.setEnabled(false);
				//result = get_fw_version();
				sleep(300);
				ButtonGetVersion.setEnabled(true);
			}
		});
	 	
	 	ButtonGetStatus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boolean result;
				ButtonGetVersion.setEnabled(false);
			//	result = get_status();
			 
				ButtonGetVersion.setEnabled(true);
			}
		});
	 	ButtonGetVersion.setVisibility(0x00000004);
	 	ButtonGetStatus.setVisibility(0x00000004);
	 	ButtonUpdateVersion.setVisibility(0x00000004);
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
									BinFileNum=R.raw.test32;
									break;
								case 1:
									SampleCodeActivity.strVer = new StringBuilder("967");	
									BinFileNum=R.raw.par966a;
									break;
								case 2:
									SampleCodeActivity.strVer = new StringBuilder("967");	
									BinFileNum=R.raw.par966t;
									/*Resources r =getResources();;
									InputStream is = r.openRawResource(BinFileNum);
									SendFileToUart(is);	
									dialog.dismiss();
									return;*/
									break;
									
				
								default:
									return;
								}
							
							
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
	        
        	//initialize GpCom
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
    	ERROR_CODE err = ERROR_CODE.SUCCESS;
    	debug_str="";
    	EditText editTextIPAddress = (EditText)findViewById(R.id.editTextIPAddress);
    	Spinner spinnerPortType = (Spinner)findViewById(R.id.spinnerPortType);
    	String selectedItem = (String)spinnerPortType.getSelectedItem();
    	/*
    	if(selectedItem.equals("Ethernet"))
    	{
    	   	//fill in some parameters
	    	m_DeviceParameters.PortType = PORT_TYPE.USB;
	    	m_DeviceParameters.PortName = editTextIPAddress.getText().toString(); 
	    	m_DeviceParameters.ApplicationContext = this;
    	}
    	else if(selectedItem.equals("USB"))
    	{*/
	    	//fill in some parameters
	    	m_DeviceParameters.PortType = PORT_TYPE.USB;
	    	m_DeviceParameters.PortName = editTextIPAddress.getText().toString(); 
	    	m_DeviceParameters.ApplicationContext = this;
    	/*}
    	else
    	{
			err = ERROR_CODE.INVALID_PORT_TYPE;
    	}*/
    	
    	if(err==ERROR_CODE.SUCCESS)
    	{
	    	//set the parameters to the device 
	    	err = m_Device.setDeviceParameters(m_DeviceParameters);
	    	if(err!=ERROR_CODE.SUCCESS)
	    	{
				String errorString = Common.getErrorText(err);
	    		messageBox(this, errorString, "SampleCode: setDeviceParameters Error");
	    	}
	    	
	    	if(err==ERROR_CODE.SUCCESS)
	    	{
	    		//open the device
	    		err = m_Device.openDevice();
	        	if(err!=ERROR_CODE.SUCCESS)
	        	{
	    			String errorString = Common.getErrorText(err);
			   		debug.d("SampleCode", "Error from openDevice: " + errorString);
	        		messageBox(this, errorString, "SampleCode 0: openDevice Error");
	        	}
	    	}
	    	
	    	if(err==ERROR_CODE.SUCCESS)
	    	{
	    		//activate ASB sending
	    		err = m_Device.activateASB(true, true, true, true, true, true);
	        	if(err!=ERROR_CODE.SUCCESS)
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
    	ERROR_CODE err = m_Device.closeDevice();
    	if(err!=ERROR_CODE.SUCCESS)
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
    
    /**************************************************************************************
     * printStringButtonClicked
     * 
     * @param view
     **************************************************************************************/
    public void printStringButtonClicked(View view)
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

    /**************************************************************************************
     * curPaperButtonClicked
     * 
     * @param view
     **************************************************************************************/
    public void cutPaperButtonClicked(View view)
    {
    	ERROR_CODE err = ERROR_CODE.SUCCESS;
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
    	
       	debug.e(" quck", "  printTextFileButtonClicked" );
     new WriteThread().start();
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
    	ERROR_CODE err = ERROR_CODE.SUCCESS;

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

	/**************************************************************************************
	 * CallbackMethod
	 * This, method will be called by the GpCom library when new data arrives from the device
	 * @param cbInfo object of type CallbackInfo to convey information into the callback
	 * @return GpCom.ERROR_CODE
     **************************************************************************************/
	public ERROR_CODE CallbackMethod(CallbackInfo cbInfo)
	{
    	Common.ERROR_CODE retval = Common.ERROR_CODE.SUCCESS;
		
    	try
    	{
    		debug.e(" on CallbackMethod(GpComCallbackInfo cbInfo)"+cbInfo.receiveCount );
   		 
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
	    			debug.e(" on CallbackMethod(GpComCallbackInfo cbInfo)" );
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


	void  sendCommand(byte[] bs)
	{
		//byte bs[]=new byte[command.length];
		Vector<Byte> data = new Vector<Byte>(bs.length);
		for(int i=0; i<bs.length; i++) {

			data.add((byte)( bs[i]&0xff) );
		}

		m_Device.sendData(data);
	}
//------------------------------------
   	private class WriteThread extends Thread {
   		int  action_code;

   		public WriteThread( ) 
   		{
   		 
   		} 
   		
   		public void run() 
   		{
            byte[] bytes={};

   	//	String str ="好德芯电子科技有限公司，成立于2008年，位于中国的“硅谷”－深圳市, 好德芯电子是《深圳市高新技术企业》，《国家高新技术企业》，是一家二维码验证/支付终端/手机支付终端/4G无线智能终端/O2O智能终端/云POS等产品整机及整体解决方案提供商。好德芯是中国二维码验证/支付终端市场的后起之秀。7年来我们始终专注于二维码验证/支付终端产品的设计、优化与完善，坚定不移地潜心研发。正是由于这样一份执着以及强大的持续创新能力，我们厚积薄发，以飞跑的速度占据中国二维码验证/支付终端“行业领跑者”的地位，是目前国内服务最专业，市场占有量最大，地域覆盖范围最广，性价比最高的二维码验证/支付终端提供商-----500多家签约客户（包括：中国移动、中国电信、中国联通三大运营商；窝窝团、大众点评、拉手网、银河联动、巨群网络、支付宝、微信支付等大型互联网品牌及公司；南航、厦航等航空公司、麦当劳、哈根达斯等餐饮连锁企业；携程网、北京旅游在线、驴妈妈、去哪儿、同程网、酷秀网等知名旅游电子门票网站；以及中国石油、石化等大型国企等）。几十万个基于二维码电子凭证的业务应用项目的实施、数亿次的二维码电子凭证业务使用，不停地产品技术改良换代，积累了丰富的二维码验证/支付终端的技术研发和售后服务经验分布在全国16个省的上万个二维码验证/支付终端业务（覆盖金融保险、交通运输、百货零售、文化娱乐、餐饮美食、旅游休闲、医疗卫生等行业），为客户提供了强有力的技术保障。7年的时间、持续的优化升级，我们已经打磨出稳定的产品核心平台、强大的制造中心、极具个性化的定制系统开发等，构成了开放的、极具竞争力的、能够保证各类二维码验证/支付业务应用实现与正常运行的硬件环境。18个专业代理商、分销商覆盖了国内大部分的一线城市，强大的售前和售后服务，实现了完善的 “一站式”与“本土化”，完全能做到快速响应、服务周到。 专业的客户服务中心、业务咨询热线、400和企业级QQ客服、微信公众平台、企业微博、旺旺客服、客服邮箱、客户自服务网站等多客服通道，可以随时受理用户关于二维码验证/支付终端及业务应用的咨询与服务，满足客户的需求。 好德芯公司一贯注重 “以人才为基础、以科技创新求发展” 的企业宗旨，沿袭多年的技术和知识沉淀融合通信/物联网/互联网+产品的新思路、新概念不断推陈出新，细致而全面地满足客户的设计需求二维码智能验证/支付终端有：二维码扫描器、二维码扫描手持机、二维码扫一体机、二维码智能识别终端、二维码柜式设备等系列产品;云POS设备有： 12.1英寸、13.3英寸、15英寸系列，主要用于餐饮系统，美发美容行业、便利店、大型超市、粮油店、福利彩票系统、各行业连锁经营主体、信仰传播、银行券商保险业、物流、安防、智能家居、广告发布系统、家庭娱乐、民航娱乐、个人娱乐、文化传播等各行各业；好德芯公司的发展依赖科技的进步，得益于广大客户的支持。今后我们仍将一如继往地专注于全球无线通信/手机支付/物联网/互联网+行业的发展，贯彻  以客户为导向、以技术为依托、以人才为根本  的宗旨，研发新技术，提供新产品，全力以赴为客户创造价值和                                                                                               "  ;

			String str ="printer test";
 		try {
			bytes=str.getBytes("cp936");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

   		Byte[] byteList = toBytes(bytes);
         Vector<Byte> vector = new Vector<Byte>(Arrays.asList(byteList));
    
          m_Device.sendData(vector);

			sendCommand(0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 39); // for alabo language
			try {
				sendCommand("يضصثقثقغنهفهخغعفهخغتخهتنميبتسينمبتسيمنبت".getBytes("cp864"));
				sendCommand("يضصثقثقغنهفهخغعفهخغتخهتنميبتسينمبتسيمنبت".getBytes("cp1256"));
				sendCommand("يضصثقثقغنهفهخغعفهخغتخهتنميبتسينمبتسيمنبت".getBytes("cp1256"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			SendRawFileToUart(R.raw.alabo );



		}
   			
   	}
	public void SendRawFileToUart(int fileID  )
	{
		int i;
		int temp;
		Log.e(TAG,"  SendRawFileToUart fileID"+fileID);

		try {
			Resources r =getResources();;
			InputStream is = r.openRawResource(fileID);
			int count = is.available();
			byte[] b = new byte[count];
			is.read(b);
			//byte SendBuf[] = new byte[count  +1023];
			//Arrays.fill(SendBuf,(byte)0);
 			sendCommand(b);

			Log.e("quck2", "all data have send!!  "   );
			sleep(400);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{


		}

	}

	  class OutputStream  {
		  

		public OutputStream( ) {
			 
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
    
    

	 
	
	
	
	 
		private void sleep(int ms) {
			// debug.d(TAG,"start sleep "+ms);
			try {
				Thread.sleep(ms);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// debug.d(TAG,"end sleep "+ms);
		}	
	
		 
		  
				
	  
	 	
}

 
   
   	
   	
	 