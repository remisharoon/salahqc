package com.entedroid.salahqc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;


public class SolatWidget extends AppWidgetProvider {
	public static final String PREFS_NAME = "MyPrefsFile";	
	static private SharedPreferences settings;
	public static final String waktu_calc_method = "waktu_calc_method";
	public static final String waktu_subuh = "waktu_subuh";
	public static final String waktu_syuruk = "waktu_syuruk";
	public static final String waktu_zohor = "waktu_zohor";
	public static final String waktu_asar = "waktu_asar";
	public static final String waktu_maghrib = "waktu_maghrib";
	public static final String waktu_isya = "waktu_isya";
	public static final String waktu_kemaskini = "kemaskini";	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){		
		RemoteViews updateViews = updateview(context);
		Intent intent = new Intent(context, waktusolat.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		updateViews.setOnClickPendingIntent(R.id.solatwidget, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds,updateViews);		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}	
	private static String conv12to24(String str12date)
	{
		try {
			SimpleDateFormat cdf = new SimpleDateFormat("HH:mm");
			Date date12 = cdf.parse(str12date);
			
			System.out.println("in widget conv12to24 >"+str12date);
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(date12);
			
			//Date date = cal.getTime();
			
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
				System.out.println("in widget return >"+(String) android.text.format.DateFormat.format("kk:mm",date24));
				return (String) android.text.format.DateFormat.format("kk:mm",date24);
			}
			
			return null;
		} catch (ParseException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	static public RemoteViews updateview(Context context){
		System.out.println("in remoteviews");
		settings = context.getSharedPreferences(PREFS_NAME, 0);
		RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		String curtime = (String) android.text.format.DateFormat.format("kk:mm", new java.util.Date());
		SimpleDateFormat cdf = new SimpleDateFormat("HH:mm");
		try{
		Date curdate = cdf.parse(curtime);
		//String strcalc_method = settings.getString(waktu_calc_method, "--").toUpperCase();
		String strsubuh = settings.getString(waktu_subuh, "--").toUpperCase();
		String strsyuruk = settings.getString(waktu_syuruk, "--").toUpperCase();
		String strzohor = settings.getString(waktu_zohor, "--").toUpperCase();
		String strasar = settings.getString(waktu_asar, "--").toUpperCase();
		String strmaghrib = settings.getString(waktu_maghrib, "--").toUpperCase();
		String strisya = settings.getString(waktu_isya, "--").toUpperCase();
		
		//System.out.println("strcalc_method >"+strcalc_method+"  "+cdf.parse(strcalc_method));
		System.out.println("strsubuh >"+strsubuh+"  "+cdf.parse(strsubuh));
		System.out.println("strsyuruk >"+strsyuruk+"  "+cdf.parse(strsyuruk));
		System.out.println("strzohor >"+strzohor+"  "+cdf.parse(strzohor));
		System.out.println("strasar >"+strasar+"  "+cdf.parse(strasar));
		System.out.println("strmaghrib >"+strmaghrib+"  "+cdf.parse(strmaghrib));
		System.out.println("strisya >"+strisya+"  "+cdf.parse(strisya));
		System.out.println("curdate >"+curdate);
		
//		updateViews.setTextViewText(R.id.calc_method, settings.getString(waktu_calc_method, "--"));
//		if(curdate.equals(cdf.parse(conv12to24(strcalc_method))) || (curdate.after(cdf.parse(conv12to24(strcalc_method))) && curdate.before(cdf.parse(conv12to24(strsubuh))))){
//			updateViews.setInt(R.id.calc_methodline, "setBackgroundResource", R.color.hightlight);
//		}
//		else{
//			updateViews.setInt(R.id.calc_methodline, "setBackgroundResource", 0);
//		}
				
		updateViews.setTextViewText(R.id.subuh, settings.getString(waktu_subuh, "--"));
		if(curdate.equals(cdf.parse(conv12to24(strsubuh))) || (curdate.after(cdf.parse(conv12to24(strsubuh))) && curdate.before(cdf.parse(conv12to24(strsyuruk))))){
			updateViews.setInt(R.id.subuhline, "setBackgroundResource", R.color.hightlight);
		}
		else{
			updateViews.setInt(R.id.subuhline, "setBackgroundResource", 0);
		}
		
		updateViews.setTextViewText(R.id.syuruk, settings.getString(waktu_syuruk, "--"));
		if(curdate.equals(cdf.parse(conv12to24(strsyuruk))) || (curdate.after(cdf.parse(conv12to24(strsyuruk))) && curdate.before(cdf.parse(conv12to24(strzohor))))){
			updateViews.setInt(R.id.syurukline, "setBackgroundResource", R.color.hightlight);
		}
		else{
			updateViews.setInt(R.id.syurukline, "setBackgroundResource", 0);
		}
		
		updateViews.setTextViewText(R.id.zohor, settings.getString(waktu_zohor, "--"));
		if(curdate.equals(cdf.parse(conv12to24(strzohor))) || (curdate.after(cdf.parse(conv12to24(strzohor))) && curdate.before(cdf.parse(conv12to24(strasar))))){
			updateViews.setInt(R.id.zohorline, "setBackgroundResource", R.color.hightlight);
		}
		else{
			updateViews.setInt(R.id.zohorline, "setBackgroundResource", 0);
		}
		
		updateViews.setTextViewText(R.id.asar, settings.getString(waktu_asar, "--"));
		if(curdate.equals(cdf.parse(conv12to24(strasar))) || (curdate.after(cdf.parse(conv12to24(strasar))) && curdate.before(cdf.parse(conv12to24(strmaghrib))))){
			updateViews.setInt(R.id.asarline, "setBackgroundResource", R.color.hightlight);
		}
		else{
			updateViews.setInt(R.id.asarline, "setBackgroundResource", 0);
		}
		
		updateViews.setTextViewText(R.id.maghrib, settings.getString(waktu_maghrib, "--"));
		if(curdate.equals(cdf.parse(conv12to24(strmaghrib))) || (curdate.after(cdf.parse(conv12to24(strmaghrib))) && curdate.before(cdf.parse(conv12to24(strisya))))){
			updateViews.setInt(R.id.maghribline, "setBackgroundResource", R.color.hightlight);
		}
		else{
			updateViews.setInt(R.id.maghribline, "setBackgroundResource", 0);
		}
		
		updateViews.setTextViewText(R.id.isya, strisya);
		if(curdate.equals(cdf.parse(conv12to24(strsubuh))) || curdate.after(cdf.parse(conv12to24(strisya))) || curdate.before(cdf.parse(conv12to24(strsubuh)))){
			updateViews.setInt(R.id.isyaline, "setBackgroundResource", R.color.hightlight);
		}
		else{
			updateViews.setInt(R.id.isyaline, "setBackgroundResource", 0);
		}
		

		}
		catch(Exception e){
			
		}
		updateViews.setTextViewText(R.id.tarikh, settings.getString(waktu_kemaskini, "--"));
		String kawasan = settings.getString("kawasan", "--");
		if(kawasan.length()>18){
			kawasan = kawasan.substring(0,18);
		}
		updateViews.setTextViewText(R.id.tempat, kawasan);
		return updateViews;
	}
}
