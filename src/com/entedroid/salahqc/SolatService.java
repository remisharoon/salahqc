package com.entedroid.salahqc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore.Audio;
import android.util.Log;

public class SolatService extends Service {
    private NotificationManager mNM;
    public static final String PREFS_NAME = "MyPrefsFile";	
    static private SharedPreferences settings;
    private String curwaktu;
    
    private Timer timer;
    
    //public static final String waktu_calc_method = "waktu_calc_method";
	public static final String waktu_subuh = "waktu_subuh";
	public static final String waktu_syuruk = "waktu_syuruk";
	public static final String waktu_zohor = "waktu_zohor";
	public static final String waktu_asar = "waktu_asar";
	public static final String waktu_maghrib = "waktu_maghrib";
	public static final String waktu_isya = "waktu_isya";
	int mat =1;
	private static final long alert_interval = 6000;
	
	private TimerTask waitSolat = new TimerTask() {
		public void run() {
			settings = getSharedPreferences(PREFS_NAME, 0);
    	  	String curtime = (String) android.text.format.DateFormat.format("kk:mm", new java.util.Date());
    	  	//System.out.println("in waitSolat , curtime>"+curtime);
			SimpleDateFormat cdf = new SimpleDateFormat("HH:mm");
			try{
				Date curdate = cdf.parse(curtime);
		    	//String strcalc_method = settings.getString(waktu_calc_method, "--");		    	
				String strsubuh = settings.getString(waktu_subuh, "--");
				String strsyuruk = settings.getString(waktu_syuruk, "--");
				String strzohor = settings.getString(waktu_zohor, "--");
				String strasar = settings.getString(waktu_asar, "--");
				String strmaghrib = settings.getString(waktu_maghrib, "--");
				String strisya = settings.getString(waktu_isya, "--");
			
			
				//strzohor = "11:40 am";
				
				//System.out.println("in waitSolat , strsubuh>"+strsubuh);
				//System.out.println("in waitSolat , strsyuruk>"+strsyuruk);
				//System.out.println("in waitSolat , strzohor>"+strzohor);
				//System.out.println("in waitSolat , strasar>"+strasar);
				//System.out.println("in waitSolat , strmaghrib>"+strmaghrib);
				//System.out.println("in waitSolat , strisya>"+strisya);
//				if(curdate.equals(cdf.parseObject(strcalc_method)) && !curwaktu.equals("calc_method")){
//					notifySolat("calc_method");
//					updatewidget();
//					curwaktu="calc_method";					
//				}
//				else 
				//notifySolat("subuh"); //change later	
				
				//System.out.println("in waitSolat , curwaktu>"+curwaktu);
				
				if(curdate.equals(cdf.parseObject(conv12to24(strsubuh.toUpperCase()))) && !curwaktu.equals("subuh")){
					notifySolat("subuh");
					updatewidget();
					curwaktu="subuh";
				}
				else if(curdate.equals(cdf.parseObject(conv12to24(strsyuruk.toUpperCase()))) && !curwaktu.equals("syuruk")){
					notifySolat("syuruk");
					updatewidget();
					curwaktu="syuruk";
				}
				else if(curdate.equals(cdf.parseObject(conv12to24(strzohor.toUpperCase()))) && !curwaktu.equals("zohor")){
					notifySolat("zohor");
					updatewidget();
					curwaktu="zohor";
				}
				else if(curdate.equals(cdf.parseObject(conv12to24(strasar.toUpperCase()))) && !curwaktu.equals("asar")){
					notifySolat("asar");
					updatewidget();
					curwaktu="asar";
				}
				else if(curdate.equals(cdf.parseObject(conv12to24(strmaghrib.toUpperCase()))) && !curwaktu.equals("maghrib")){
					notifySolat("maghrib");
					updatewidget();
					curwaktu="maghrib";
				}
				else if(curdate.equals(cdf.parseObject(conv12to24(strisya.toUpperCase()))) && !curwaktu.equals("isya")){
					//System.out.println("call  notifySolat isha>");
					notifySolat("isya");
					updatewidget();
					curwaktu = "isya";
				}
			}
			catch(Exception e){
				//System.out.println("in waitSolat , exception>");
				e.printStackTrace();
			}   
		}
    };

	private static String conv12to24(String str12date) //check later 12:37 pm conversion problem
	{
		try {
			SimpleDateFormat cdf = new SimpleDateFormat("HH:mm");
			Date date12 = cdf.parse(str12date);
			
			//System.out.println("in service conv12to24 >"+str12date);
			GregorianCalendar cal = new GregorianCalendar(); 
			cal.setTime(date12);
			
			//Date date = cal.getTime();
			
			if(str12date.contains("PM"))
			{
				//System.out.println("in PM cal HOUR ## >"+str12date.substring(0,2));
				if(str12date.substring(0,2).trim().equals("12")){
					//cal.add(Calendar.HOUR, 12);
					//Date date24 = cal.getTime();
					//System.out.println("in serv PM return >"+str12date.substring(0,5));
					return str12date.substring(0,5);
				}else{
					cal.add(Calendar.HOUR, 12);
					Date date24 = cal.getTime();
					//System.out.println("in serv PM non12 return >"+(String) android.text.format.DateFormat.format("kk:mm",date24));
					return (String) android.text.format.DateFormat.format("kk:mm",date24);
				}
				
			}
			else if(str12date.contains("AM"))
			{
				Date date24 = cal.getTime();
				//System.out.println("in widget return >"+(String) android.text.format.DateFormat.format("kk:mm",date24));
				return (String) android.text.format.DateFormat.format("kk:mm",date24);
			}
			
			return null;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}    
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.solatnotification;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	SolatService getService() {
            return SolatService.this;
        }
    }

    @Override
    public void onCreate() {
    	curwaktu = "Tiada";
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        timer = new Timer();
        timer.schedule(waitSolat,1000,SolatService.alert_interval);  
        //timer.schedule(waitSolat,1000,1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	//System.out.println("in waitSolat , onStartCommand>");
        Log.d("SolatNotification", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        waitSolat.run();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
    }

    @Override
    public IBinder onBind(Intent intent) {  
    	
    	try {
			waitSolat.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    
    
    public void notifySolat(String waktu){
    	//System.out.println("in waitSolat , notifySolat>"+waktu);
    	// In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Time for Salah " + waktu;

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.icon, text, System.currentTimeMillis());
        
        if(Prefs.getAzan(this, waktu)){
        	notification.sound = Uri.parse("android.resource://com.entedroid.salahqc/" + R.raw.azan);  //change later
        }

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, waktusolat.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "Time for Salah",text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
    
    public void updatewidget(){
    	//System.out.println("in waitSolat , updatewidget>");
    	ComponentName me=new ComponentName(this, SolatWidget.class);        			
		AppWidgetManager mgr=AppWidgetManager.getInstance(this);
		
		mgr.updateAppWidget(me,SolatWidget.updateview(getApplicationContext()));   	
    }   
    
}
