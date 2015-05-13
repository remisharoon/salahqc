package com.entedroid.salahqc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;


import com.appbrain.AppBrain;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.planetapps.qiblacompass.data.GlobalData;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import android.text.format.DateFormat;
import android.util.Log;

public class waktusolat extends Activity implements    OnClickListener, android.content.DialogInterface.OnClickListener {

	private static final String TAG = "arabsalah";
	public static final String PREFS_NAME = "MyPrefsFile";

	//Include your own AdMob publisher id
	private final static String ADMOB_PUBLISHER_ID = "a150a3c25576085";
	
    private final static int REFRESH_AD = 101;
	private final static long REFRESH_INTERVAL = 30000;
	

    private RelativeLayout layout;
     
	private ViewFlipper viewFlipper;
	private AdView adView;		

	private Handler refreshHandler;
	private Looper refreshLooper;
	
	
	
	
	private TextView calc_method, subuh_time, syuruk_time, zohor_time,
			asar_time, maghrib_time, isya_time,kawasan,negeri,kemaskini,qibla_dir;
	public static final String waktu_calc_method = "--";
	public static final String waktu_subuh = "waktu_subuh";
	public static final String waktu_syuruk = "waktu_syuruk";
	public static final String waktu_zohor = "waktu_zohor";
	public static final String waktu_asar = "waktu_asar";
	public static final String waktu_maghrib = "waktu_maghrib";
	public static final String waktu_isya = "waktu_isya";
	public static final String waktu_kemaskini = "kemaskini";	
	
	private EditText editTextShowLocation;
	private Button buttonGetLocation;
	private ProgressBar progress;
	public 	String	strCalcMethod = "";
	private LocationManager locManager;
	private LocationListener locListener = new MyLocationListener();
	public static Location currentLocation = null;
	
	private boolean gps_enabled = false;
	private boolean network_enabled = false;
	
	static final int pick_kawasan_request = 0;

	private Handler guiThread;
	private ExecutorService waktuThread;
	private Runnable updatewaktu;
	private Future waktupending;
	private SharedPreferences settings;
	
	private SolatService solatservice;
	private boolean servicebound;
	
	private ServiceConnection connection = 
			new ServiceConnection() {
		public void onServiceDisconnected(ComponentName className) {
	        solatservice = null;
	    }
		public void onServiceConnected(ComponentName name, IBinder service) {
			solatservice = ((SolatService.LocalBinder)service).getService();			
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		System.out.println("start onCreate");
		
		super.onCreate(savedInstanceState);
		System.out.println("in onCreate()");
		settings = getSharedPreferences(PREFS_NAME, 0);

        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		setContentView(R.layout.main);
		
		//layout = (RelativeLayout) findViewById(R.id.mobfoxContent);
		
		boolean includeLocation = true;
		boolean useAnimation = true;

        AppBrain.initApp(this);
		
        adView = new AdView(this, AdSize.BANNER, ADMOB_PUBLISHER_ID);
        
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.ad);
        // Add the adView to it
        layout.addView(adView);

        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest());
        

//		viewFlipper = new ViewFlipper(this) {
//			@Override
//			protected void onDetachedFromWindow() {
//				try {
//					super.onDetachedFromWindow();
//				} catch (IllegalArgumentException e) {
//					stopFlipping();
//				}
//			}
//		};



        Log.i(TAG, "Activity created");
		
		
		//turnGPSOn();

		//editTextShowLocation = (EditText) findViewById(R.id.editTextShowLocation);

		progress = (ProgressBar) findViewById(R.id.progressBar1);
		progress.setVisibility(View.GONE);

		locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		//Block Loc Finder
		try {
			Location gps = locManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			Location network = locManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			//onGetLocation();
			if (gps != null){
				System.out.println("gps not null");
				currentLocation = (gps); 
				startProcess();
			}
			else if (network != null){
				currentLocation = (network);
				System.out.println("network not null");
				startProcess();
			}
			else if(settings.getString(waktu_subuh, "--")=="--")
			{
			
				System.out.println("call onGetLocation()");
				onGetLocation();
			}else{
				System.out.println("call startProcess() without location");
				//currentLocation = settings.getString(waktu_subuh, "--");
				startProcess();
			}
		} catch (Exception ex2) {
			System.out.println("in exception > Block Loc Finder");
			Log.d(TAG, "Block Loc Finder exception", ex2);
			if(settings.getString(waktu_subuh, "--")=="--")
				onGetLocation();
		}
		
//		Location mLastKownLocation = null;

		
		
		
		ImageButton btn = (ImageButton)findViewById(R.id.select_method);
		btn.setAlpha(100);
		
		View selectmethodButton = findViewById(R.id.select_method);
		selectmethodButton.setOnClickListener(this);

		
	}
	
	private void startProcess()
	{

		if (currentLocation != null){
			System.out.println("startProcess>londitude >"+currentLocation.getLongitude());
			System.out.println("startProcess>Latitude >"+currentLocation.getLatitude());
			System.out.println("startProcess>Altitiude >"+currentLocation.getAltitude());
			System.out.println("startProcess>Accuracy >"+currentLocation.getAccuracy());
			System.out.println("startProcess>time >"+currentLocation.getTime());
			
			getAddressFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(), this, new GeocoderHandler());

		}
		
		if(settings.getString(waktu_subuh, "--")=="--" && !isNetworkAvailable())
		{		

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClassName("com.android.phone", "com.android.phone.Settings");
			startActivity(intent);
			Toast toast=Toast.makeText(this, " Enable Data Connectivity ..", 2000);
		    toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
		    toast.show();
		}
		
		
		initThreading();
		findViews();
		guiThread.post(updatewaktu);
		doBindService();
	}
	
	public void onGetLocation() {
		turnGPSOn();
		progress.setVisibility(View.VISIBLE);
		Toast toast=Toast.makeText(this, "Getting Current Location....", 2000);
	     toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
	     toast.show();
		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			System.out.println("gps_enabled >");
		} catch (Exception ex) {
		}
		try {
			network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			System.out.println("network_enabled >");
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		//if (!gps_enabled && !network_enabled) {
		if (!gps_enabled && !isNetworkAvailable()) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("Attention!");
			builder.setMessage("Sorry, location is not determined. Please enable location providers");
			builder.setPositiveButton("OK", this);
			builder.setNeutralButton("Cancel", this);
			builder.create().show();
			progress.setVisibility(View.GONE);
		}

		//if (gps_enabled) 
		{
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
		}
		if (network_enabled) {
			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
		}
	}

	class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			if (location != null) {
				// This needs to stop getting the location data and save the battery power.
				locManager.removeUpdates(locListener); 

				String londitude = "Londitude: " + location.getLongitude();
				String latitude = "Latitude: " + location.getLatitude();
				String altitiude = "Altitiude: " + location.getAltitude();
				String accuracy = "Accuracy: " + location.getAccuracy();
				String time = "Time: " + location.getTime();

				System.out.println("londitude >"+londitude);
				System.out.println("Latitude >"+latitude);
				System.out.println("Altitiude >"+altitiude);
				System.out.println("Accuracy >"+accuracy);
				System.out.println("time >"+time);
				//editTextShowLocation.setText(londitude + "\n" + latitude + "\n" + altitiude + "\n" + accuracy + "\n" + time);
		        String str = "\n CurrentLocation: "+
		                "\n Latitude: "+ location.getLatitude() + 
		                "\n Longitude: " + location.getLongitude() + 
		                "\n Accuracy: " + location.getAccuracy() + 
		                "\n CurrentTimeStamp "+ location.getTime();         
		                  Toast.makeText(waktusolat.this,str,Toast.LENGTH_LONG).show();
		                  //tv.append(str);               
				currentLocation = location;
				progress.setVisibility(View.GONE);
				turnGPSOff();
				startProcess();
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	public void onClick(DialogInterface dialog, int which) {
		if(which == DialogInterface.BUTTON_NEUTRAL){
			editTextShowLocation.setText("Sorry, location is not determined. To fix this please enable location providers");
		}else if (which == DialogInterface.BUTTON_POSITIVE) {
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.settings:
			startActivity(new Intent(this,Prefs.class));
			return true;
		case R.id.calc:
			Intent i = new Intent(this, ZonSolat.class);
			startActivityForResult(i,pick_kawasan_request);
			if(isNetworkAvailable()){
				System.out.println("NetworkAvailable>");
				startProcess();		//ckeck later
			}else
			{
				Toast toast=Toast.makeText(this, "Cannot Update Salah-Timings \n NO Internet Connectivity.... \n Please Connect to Network ", 2000);
			     toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
			     toast.show();
			}
			return true;			
		}
		return false;
	}
	
	private void doBindService() {
		if(!servicebound){			
		    bindService(new Intent(SolatService.class.getName()),connection,Context.BIND_AUTO_CREATE);
			servicebound = true;
		}
	}
	
	private void doUnbindService() {
		if(servicebound){
			unbindService(this.connection);
			servicebound = false;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (refreshHandler != null)
		{
			refreshHandler.removeMessages(REFRESH_AD);
		}

        Log.i(TAG, "OnPause");
	}
	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
		if (refreshLooper != null)
		{
			refreshLooper.quit();
			refreshLooper = null;
			refreshHandler = null;
		}
	    doUnbindService();
//        turnGPSOff();
//        finish();
//        System.runFinalizersOnExit(true);
//        System.exit(0);	    
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("in onResume()");
		if (refreshHandler != null)
		{
			refreshHandler.removeMessages(REFRESH_AD);
			refreshHandler.sendEmptyMessage(REFRESH_AD);
		}
        Log.i(TAG, "OnResume");		
		//startProcess();
		updatewaktuview(); //change later
		
		doBindService();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
      Log.i(TAG, "onConfigurationChanged");
	}










	
	
	private void findViews() {		
		
		calc_method = (TextView) findViewById(R.id.calc_method);
		subuh_time = (TextView) findViewById(R.id.subuh_time);
		syuruk_time = (TextView) findViewById(R.id.syuruk_time);
		zohor_time = (TextView) findViewById(R.id.zohor_time);
		asar_time = (TextView) findViewById(R.id.asar_time);
		maghrib_time = (TextView) findViewById(R.id.maghrib_time);
		isya_time = (TextView) findViewById(R.id.isya_time);
		negeri = (TextView) findViewById(R.id.negeri);
		kawasan = (TextView) findViewById(R.id.kawasan);
		kemaskini = (TextView) findViewById(R.id.kemaskini);
		qibla_dir = (TextView) findViewById(R.id.qibla_dir);
	}

	private void initThreading() {
		System.out.println("in initThreading :"+settings.getString("calcMethod", "--"));
		if(settings.getString("calcMethod", "--")=="--")
		{
			System.out.println("caling zonsolat intent");
			Intent i = new Intent(this, ZonSolat.class);
			startActivity(i);
		}
		strCalcMethod = settings.getString("calcMethod", "--");
		guiThread = new Handler();
		waktuThread = Executors.newSingleThreadExecutor();		
		updatewaktu = new Runnable() {
			public void run() {				
				String curdate = (String) android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date());		
				System.out.println("curdate :"+curdate);
				System.out.println("settings.getString(kemaskini, --) :"+settings.getString("kemaskini", "--"));
				
				if(settings.getBoolean("updatetime",false)||!curdate.equals(settings.getString("kemaskini", "--"))){ //change later
				//if(true == true) {
					System.out.println("in if :"+curdate);
					if (waktupending != null)
						waktupending.cancel(true);
					try {			
						System.out.println("calling  waktusolatTask:"+curdate);

						waktusolatTask waktutask = new waktusolatTask(waktusolat.this);
						waktupending = waktuThread.submit(waktutask);
					} catch (RejectedExecutionException e) {
						Log.d(TAG, "Rejectedexception", e);
					}
				}
				else{
					updatewaktuview();
				}
			}
		};
	}

	public void savewaktu(String nama_waktu, String waktu) {
		settings.edit().putString(nama_waktu, waktu).commit();
	}
	
	public void savekemaskini() {
		String curdate = (String) android.text.format.DateFormat.format("hh:mm a dd/MM/yyyy", new java.util.Date());
		settings.edit().putString("kemaskini", curdate).commit();
	}

	/**
	 * 
	 * Converts an angle in radians to degrees
	 */

	public static double radToBearing(double rad) {

		return (Math.toDegrees(rad) + 360) % 360;

	}
	
	public static double kaababearing() {
		try {
			double lat1 = currentLocation.getLatitude();
			double lon1 = currentLocation.getLongitude();

			Location kaaba = new Location("ATL");
			kaaba.setLatitude(21.422534);
			kaaba.setLongitude(39.826205);
			kaaba.setAltitude(1);

			double lat2 = 21.422534;
			double lon2 = 39.826205;
			System.out.println("currentLocation >" + lat1 + " long> " + lon1);
			System.out.println("Distance >" + currentLocation.distanceTo(kaaba));
			float kaabadistance = Math
					.round(currentLocation.distanceTo(kaaba) / 1000);
			GlobalData.setKaabadistance(kaabadistance);
			double lat1Rad = Math.toRadians(lat1);

			double lat2Rad = Math.toRadians(lat2);

			double deltaLonRad = Math.toRadians(lon2 - lon1);

			double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);

			double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad)
					* Math.cos(lat2Rad)

					* Math.cos(deltaLonRad);

			return radToBearing(Math.atan2(y, x));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}
	
	private static String updateText(float d) {
        int range = (int) (d / (360f / 16f)); 
        String  dirTxt = "";
        if (range == 15 || range == 0) dirTxt = "N"; 
        else if (range == 1 || range == 2) dirTxt = "NE"; 
        else if (range == 3 || range == 4) dirTxt = "E"; 
        else if (range == 5 || range == 6) dirTxt = "SE";
        else if (range == 7 || range == 8) dirTxt= "S"; 
        else if (range == 9 || range == 10) dirTxt = "SW"; 
        else if (range == 11 || range == 12) dirTxt = "W"; 
        else if (range == 13 || range == 14) dirTxt = "NW";
        System.out.println("dirTxt >"+dirTxt);
        return dirTxt;
    }
	
	public void updatewaktuview() {
		try {
			System.out.println("in void updatewaktuview() >");
			guiThread.post(new Runnable() {
				public void run() {
					try{				
						System.out.println("in run >");
						System.out.println("strsubuh >"+settings.getString("negeri", " -- "));
						String strcurdate = (String) android.text.format.DateFormat.format("hh:mm a dd/MM/yyyy", new java.util.Date());
								
						negeri.setText(settings.getString("negeri", " -- "));
						kawasan.setText(settings.getString("kawasan", " -- "));
						kemaskini.setText(settings.getString("kemaskini", strcurdate));
						calc_method.setText(calc_method_hash(settings.getString("calcMethod", "--")));
						strCalcMethod = settings.getString("calcMethod", "--");
						
						SimpleDateFormat cdf = new SimpleDateFormat("HH:mm");
						System.out.println("in updatewaktuview >");
						System.out.println("negeri >"+settings.getString("negeri", " -- "));
						System.out.println("kawasan >"+settings.getString("kawasan", " -- "));
						System.out.println("kemaskini >"+settings.getString("kemaskini", strcurdate));

						String curtime = (String) android.text.format.DateFormat.format("kk:mm", new java.util.Date());
						Date curdate = cdf.parse(curtime);
						//String strcalc_method = settings.getString(waktu_calc_method, "--").toUpperCase();
						String strsubuh = settings.getString(waktu_subuh, "--").toUpperCase();
						String strsyuruk = settings.getString(waktu_syuruk, "--").toUpperCase();
						String strzohor = settings.getString(waktu_zohor, "--").toUpperCase();
						String strasar = settings.getString(waktu_asar, "--").toUpperCase();
						String strmaghrib = settings.getString(waktu_maghrib, "--").toUpperCase();
						String strisya = settings.getString(waktu_isya, "--").toUpperCase();
						System.out.println("strisya >" + strisya);
						
						String kaabaoffset = settings.getString("kaabaoffset", "--").toUpperCase();
						
//						String strdir = updateText((float) kaababearing());
//						System.out.println("strdir >" + strdir);
//						String kaabaoffset = String.valueOf(Math.round(kaababearing())) + (char) 0x00B0  ;
//						System.out.println("kaabaoffset >" + kaabaoffset);
						
						
						
						//System.out.println("strcalc_method >"+strcalc_method+"  "+cdf.parse(strcalc_method));
						try {
							System.out.println("strsubuh >"+strsubuh+"  "+cdf.parse(strsubuh));
							System.out.println("strsyuruk >"+strsyuruk+"  "+cdf.parse(strsyuruk));
							System.out.println("strzohor >"+strzohor+"  "+cdf.parse(strzohor));
							System.out.println("strasar >"+strasar+"  "+cdf.parse(strasar));
							System.out.println("strmaghrib >"+strmaghrib+"  "+cdf.parse(strmaghrib));
							System.out.println("strisya >"+strisya+"  "+cdf.parse(strisya));
							System.out.println("curdate >"+curdate);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//calc_method.setText("Umm Ul Qura");
//					if(curdate.equals(cdf.parseObject(conv12to24(strcalc_method))) || (curdate.after(cdf.parse(conv12to24(strcalc_method))) && curdate.before(cdf.parse(conv12to24(strsubuh))))){
//						highlight(R.id.calc_method_title,calc_method,true);
//					}
//					else{
//						highlight(R.id.calc_method_title,calc_method,false);
//					}
						subuh_time.setText(strsubuh);
						if(curdate.equals(cdf.parseObject(conv12to24(strsubuh))) || (curdate.after(cdf.parse(conv12to24(strsubuh))) && curdate.before(cdf.parse(conv12to24(strsyuruk))))){
							highlight(R.id.subuh_title,subuh_time,true);
						}
						else{
							highlight(R.id.subuh_title,subuh_time,false);
						}
						syuruk_time.setText(strsyuruk);
						if(curdate.equals(cdf.parseObject(conv12to24(strsyuruk))) || (curdate.after(cdf.parse(conv12to24(strsyuruk))) && curdate.before(cdf.parse(conv12to24(strzohor))))){
							highlight(R.id.syuruk_title,syuruk_time,true);
						}
						else{
							highlight(R.id.syuruk_title,syuruk_time,false);
						}
						zohor_time.setText(strzohor);
						if(curdate.equals(cdf.parseObject(conv12to24(strzohor))) || (curdate.after(cdf.parse(conv12to24(strzohor))) && curdate.before(cdf.parse(conv12to24(strasar))))){
							highlight(R.id.zohor_title,zohor_time,true);
						}
						else{
							highlight(R.id.zohor_title,zohor_time,false);
						}
						asar_time.setText(strasar);
						if(curdate.equals(cdf.parseObject(conv12to24(strasar))) || (curdate.after(cdf.parse(conv12to24(strasar))) && curdate.before(cdf.parse(conv12to24(strmaghrib))))){
							highlight(R.id.asar_title,asar_time,true);
						}
						else{
							highlight(R.id.asar_title,asar_time,false);
						}
						maghrib_time.setText(strmaghrib);
						if(curdate.equals(cdf.parseObject(conv12to24(strmaghrib))) || (curdate.after(cdf.parse(conv12to24(strmaghrib))) && curdate.before(cdf.parse(conv12to24(strisya))))){
							highlight(R.id.maghrib_title,maghrib_time,true);
						}
						else{
							highlight(R.id.maghrib_title,maghrib_time,false);
						}
						isya_time.setText(strisya);
						if(curdate.equals(cdf.parseObject(conv12to24(strisya))) || curdate.after(cdf.parse(conv12to24(strisya))) || curdate.before(cdf.parse(conv12to24(strsubuh)))){
							highlight(R.id.isya_title,isya_time,true);
						}
						else{
							highlight(R.id.isya_title,isya_time,false);
						}
						qibla_dir.setText(kaabaoffset);
					}
					catch(Exception e){
						Log.d(TAG, "Rejectedexception", e);
					}
					String strcurdate = (String) android.text.format.DateFormat.format("hh:mm a dd/MM/yyyy", new java.util.Date());
					kemaskini.setText(settings.getString("kemaskini", strcurdate));
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected CharSequence calc_method_hash(String method_idx) {
		// TODO Auto-generated method stub
		String strcalcMethod = "--";
		System.out.println("in calc_method_hash: method_idx >"+method_idx);
		try {
			switch (Integer.parseInt(method_idx)) {
			case 1:
				strcalcMethod = "Egyptian General Authority Of Survey"; 
				break;
			case 2:
				strcalcMethod = "University Of Islamic Sciences (Shafi)";
				break;
			case 3:
				strcalcMethod = "University Of Islamic Sciences (Hanafi)";
				break;
			case 4:
				strcalcMethod = "Islamic Society Of North America";
				break;
			case 5:
				strcalcMethod = "Muslim World League";
				break;
			case 6:
				strcalcMethod = "Umm Al-Qurra (Middle East)";
				break;
			case 7:
				strcalcMethod = "Fixed Isha Interval";
				break;			
			}
			
			System.out.println("strcalcMethod >"+strcalcMethod);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strcalcMethod;
	}

	private String conv12to24(String str12date)
	{
		try {
			SimpleDateFormat cdf = new SimpleDateFormat("HH:mm");
			Date date12 = cdf.parse(str12date);
			
			System.out.println("in conv12to24 activity> "+str12date);
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(date12);
			
			Date date = cal.getTime();
			
			if(str12date.contains("PM"))
			{
				System.out.println("in PM cal HOUR ## >"+str12date.substring(0,2));
				if(str12date.substring(0,2).trim().equals("12")){
					//cal.add(Calendar.HOUR, 12);
					//Date date24 = cal.getTime();
					System.out.println("in serv PM return >"+str12date.substring(0,5));
					return str12date.substring(0,5);
				}else{
					cal.add(Calendar.HOUR, 12);
					Date date24 = cal.getTime();
					System.out.println("in serv PM non12 return >"+(String) android.text.format.DateFormat.format("kk:mm",date24));
					return (String) android.text.format.DateFormat.format("kk:mm",date24);
				}
				
			}
			else if(str12date.contains("AM"))
			{
				Date date24 = cal.getTime();
				return (String) android.text.format.DateFormat.format("kk:mm",date24);
			}
			
			return null;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	private void highlight(int titleint,TextView time,boolean hightlight){
		if(hightlight){
			TextView title = (TextView) findViewById(titleint);
			title.setBackgroundResource(R.color.hightlight);
			time.setBackgroundResource(R.color.hightlight);
		}
		else{
			TextView title = (TextView) findViewById(titleint);
			title.setBackgroundResource(R.color.green);
			time.setBackgroundResource(R.color.green);
		}
	}

	public String getkodkawasan() {
		return settings.getString("kod_kawasan", "sgr03");
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_method:
			Intent i = new Intent(this, ZonSolat.class);
			startActivityForResult(i,pick_kawasan_request);
			if(isNetworkAvailable()){
				System.out.println("NetworkAvailable>");
				startProcess();		//ckeck later
			}else
			{
				Toast toast=Toast.makeText(this, "Cannot Update Salah-Timings \n NO Internet Connectivity.... \n Please Connect to Network ", 2000);
			     toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
			     toast.show();
			}
			
			break;
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == pick_kawasan_request) {
            if (resultCode == RESULT_OK) {
            	if (data.hasExtra("kod_kawasan")) {
        			Bundle extras = data.getExtras();
        			
//        			String kod_kawasan = extras.getString("kod_kawasan");
//        			settings.edit().putString("kod_kawasan", kod_kawasan).commit();
        			
//        			String kawasan = extras.getString("kawasan");
//        			settings.edit().putString("kawasan", kawasan).commit();
        			
//        			String negeri = extras.getString("negeri");
//        			settings.edit().putString("negeri", negeri).commit();
//        			Log.d(TAG, "kod_kawasan:" + kod_kawasan);
        			
        			settings.edit().putBoolean("reset", true).commit();
        			ComponentName me=new ComponentName(this, SolatWidget.class);        			
        			AppWidgetManager mgr=AppWidgetManager.getInstance(this);
        			
        			mgr.updateAppWidget(me,SolatWidget.updateview(getApplicationContext()));
        			//settings.edit().putBoolean("updatetime", true).commit();  //change later
        			updatewaktu.run();
        		}
            }
        }
    }
	
	public static void getAddressFromLocation(final double lat,final double lon, final Context context, final Handler handler) {
	    Thread thread = new Thread() {
	        @Override public void run() {
	            Geocoder geocoder = new Geocoder(context, Locale.getDefault());   
	            String result = null;
	            String strAddress = null;
	            String strLocality = null;
	            String countryname = null;
	            String premise = null;
	            try {
	                List<Address> list = geocoder.getFromLocation(lat, lon, 1);
	                if (list != null && list.size() > 0) {
	                    Address address = list.get(0);
	                    // sending back first address line and locality
	                    result = address.getAddressLine(0);
	                    strAddress = address.getAddressLine(0);
	                    strLocality = address.getLocality();
	                    countryname = address.getCountryName();
	                    premise = address.getPremises();
	                }
	            } catch (IOException e) {
	                Log.e(TAG, "Impossible to connect to Geocoder", e);
	            } finally {
	                Message msg = Message.obtain();
	                msg.setTarget(handler);
	                if (result != null) {
	                    msg.what = 1;
	                    Bundle bundle = new Bundle();
	                    bundle.putString("address", result);
	                    bundle.putString("country", countryname);
	                    bundle.putString("premise", premise);
	                    bundle.putString("Address", strAddress);
	                    bundle.putString("Locality", strLocality);
	                    msg.setData(bundle);
	                } else 
	                    msg.what = 0;
	                msg.sendToTarget();
	            }
	            
	        }
	    };
	    thread.start();
	}
	
	private void turnGPSOn(){
	    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

	    if(!provider.contains("gps")){ //if gps is disabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        sendBroadcast(poke);
	    }
	}
    private void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            sendBroadcast(poke);
        }
    }	
	
	private class GeocoderHandler extends Handler {
	    @Override
	    public void handleMessage(Message message) {
            String result = null;
            String countryname = null;
            String premise = null;
            String strAddress = null;
            String strLocality = null;            
	        switch (message.what) {
	        case 1:
	            Bundle bundle = message.getData();
	            result = bundle.getString("address");
	            countryname = bundle.getString("country");
	            premise = bundle.getString("premise");
	            strAddress = bundle.getString("Address");
	            strLocality = bundle.getString("Locality");	            
           
	            break;
	        default:
	            result = null;
	        }
	        // replace by what you need to do
	        System.out.println("result  >"+result);
	        System.out.println("country  >"+countryname);
	        System.out.println("premise  >"+premise);
	        System.out.println("strAddress  >"+strAddress);
	        System.out.println("strLocality  >"+strLocality);	        
	        
	        if(result != null){
	        settings.edit().putString("negeri", result).commit();
	        }
	        if(countryname != null){
			settings.edit().putString("kawasan", countryname).commit();
	        }
	        //myLabel.setText(result);
	    }   
	}
	
	@Override
	public void onBackPressed() {
		AppBrain.getAds().showInterstitial(this);
		waktusolat.super.onBackPressed();
	}
	public void adClicked() {
		// TODO Auto-generated method stub
		
	}
}