package com.entedroid.salahqc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.planetapps.qiblacompass.data.GlobalData;


import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.Html;
import android.util.Log;

public class waktusolatTask implements Runnable {

	private static final String TAG = "waktusolatTask";	 
	private final waktusolat waktu;
	
	waktusolatTask(waktusolat waktu) {
		this.waktu = waktu;
	}

	public void run() {
		HttpURLConnection con = null;
		try {
			if (Thread.interrupted())
				throw new InterruptedException();
			
//			URL url = new URL("http://www.e-solat.gov.my/solat.php?"
//					+ "kod=" + waktu.getkodkawasan() + "&lang=Eng"
//					+ "&url=http://blog.abdullahsolutions.com");
			
			//String url = "http://salah.com/rss?q=loc:12.5097254,74.9844009&method=4";
			System.out.println("waktu.currentLocation.getLatitude() >"+waktu.currentLocation.getLatitude());
			System.out.println("waktu.currentLocation.getLongitude() >"+waktu.currentLocation.getLongitude());
			System.out.println("waktu.currentLocation.strCalcMethod() >"+waktu.strCalcMethod);
			String url = "http://salah.com/rss?q=loc:"+waktu.currentLocation.getLatitude()+","+waktu.currentLocation.getLongitude()+"&method="+waktu.strCalcMethod;
			System.out.println("url  -->"+url);
			
			Calendar c = Calendar.getInstance(); 
			
			PrayTime prayertimes = new PrayTime();
//			prayertimes.getPrayerTimes(c, (double) waktu.currentLocation.getLatitude() ,(double) waktu.currentLocation.getLongitude(), 4);
			
			TimeZone tz = TimeZone.getDefault();
			System.out.println("tz  -->"+tz.getOffset(0));
			System.out.println("TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID());
			
			TimeZone tz2 = c.getTimeZone();
			System.out.println("tz2  -->"+tz2.getOffset(0));
			System.out.println("TimeZone2   "+tz2.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz2.getID());
			
			double timezoneoffset = (double)tz2.getOffset(0)/3600000;
			
			String calcmethod = "3";  //Muslim World League (MWL)
			String isShafii = "0";  //Shafii - 0, Hanafi - 1
			
			if ( waktu.strCalcMethod == "1" )
			{
				calcmethod = "5";
			}else if ( waktu.strCalcMethod == "2" )
			{
				calcmethod = "1";
				isShafii = "0";
			}else if ( waktu.strCalcMethod == "3" )
			{
				calcmethod = "1";
				isShafii = "1";
			}else if ( waktu.strCalcMethod == "4"  )
			{
				calcmethod = "2";
			}else if ( waktu.strCalcMethod == "5"  )
			{
				calcmethod = "3";
			}else if ( waktu.strCalcMethod == "6"  )
			{
				calcmethod = "4";
			}
			
			prayertimes.setLat((double) waktu.currentLocation.getLatitude());
			prayertimes.setLng((double) waktu.currentLocation.getLongitude());
//			prayertimes.setTimeZone ((double) tz);
			prayertimes.setCalcMethod(3);
			
			String[] args = {
								Double.toString(waktu.currentLocation.getLatitude()),
								Double.toString(waktu.currentLocation.getLongitude()),
								Double.toString(timezoneoffset),
								calcmethod,
								isShafii
							};
			
			ArrayList<String> prayerTimes = prayertimes.main(args);
//			prayertimes.setJDate(jDate);
			ArrayList<String> prayerNames = prayertimes.getTimeNames();
			
	        for (int i = 0; i < prayerTimes.size(); i++) {
	            System.out.println(prayerNames.get(i) + " -------- >> " + prayerTimes.get(i));
	        }
			
			System.out.println("DhuhrMinutes  -->"+prayertimes.getDhuhrMinutes());
			
//			ArrayList<String> prayertimeslist = prayertimes.getPrayerTimesCurrent();
			
			
			
//			System.out.println("DhuhrMinutes  -->"+prayertimeslist.);
//			
//			XMLParser parser = new XMLParser(); 
//			String xml = parser.getXmlFromUrl(url); // getting XML
//			Document doc = parser.getDomElement(xml); // getting DOM element
//			
//			Element root = dom.getDocumentElement();
//			NodeList channel = root.getElementsByTagName("rss");
//			Node nchannel = channel.item(0);
//			NodeList items = nchannel.getChildNodes();
			
			
			
//			NodeList nl = doc.getElementsByTagName("item");
//			NodeList nl = (NodeList) n2.item(0);
			System.out.println("prayerTimes.size() >"+prayerTimes.size());
			// looping through all item nodes <item>
//			for (int i = 0; i < nl.getLength(); i++) {
			for (int i = 0; i < prayerTimes.size(); i++) {
				// creating new HashMap
//				HashMap<String, String> map = new HashMap<String, String>();
//				Element e = (Element) nl.item(i); 
				// adding each child node to HashMap key => value
//				map.put(KEY_ID, parser.getValue(e, KEY_ID));
				
				System.out.println("value e  -->"+prayerTimes.get(i));
				switch(i){
				case 0:
					waktu.savewaktu(waktusolat.waktu_subuh,prayerTimes.get(i));
					break;
				case 1:
					waktu.savewaktu(waktusolat.waktu_syuruk,prayerTimes.get(i));
					break;
				case 2:
					waktu.savewaktu(waktusolat.waktu_zohor,prayerTimes.get(i));
					break;
				case 3:
					waktu.savewaktu(waktusolat.waktu_asar,prayerTimes.get(i));
					break;
				case 4:
					waktu.savewaktu(waktusolat.waktu_maghrib,prayerTimes.get(i));
					break;
				case 6:
					waktu.savewaktu(waktusolat.waktu_isya,prayerTimes.get(i));
					break;				
				}	
			}
			
			
			
			
			double lat1 = waktu.currentLocation.getLatitude();
			double lon1 = waktu.currentLocation.getLongitude();

			Location kaaba = new Location("ATL");
			kaaba.setLatitude(21.422534);
			kaaba.setLongitude(39.826205);
			kaaba.setAltitude(1);

			double lat2 = 21.422534;
			double lon2 = 39.826205;
			System.out.println("currentLocation >" + lat1 + " long> " + lon1);
			System.out.println("Distance >" + waktu.currentLocation.distanceTo(kaaba));
			float kaabadistance = Math.round(waktu.currentLocation.distanceTo(kaaba) / 1000);
			GlobalData.setKaabadistance(kaabadistance);
			double lat1Rad = Math.toRadians(lat1);

			double lat2Rad = Math.toRadians(lat2);

			double deltaLonRad = Math.toRadians(lon2 - lon1);

			double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);

			double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad)
					* Math.cos(lat2Rad)

					* Math.cos(deltaLonRad);
			double bearing = Math.atan2(y, x);
			
			double d	=	(Math.toDegrees(bearing) + 360) % 360;
//			return (Math.toDegrees(rad) + 360) % 360;
			
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
			
			
			String kaabaoffset = String.valueOf(Math.round(d)) + (char) 0x00B0  + " " + dirTxt;
			System.out.println("kaabaoffset >" + kaabaoffset);
			
		
			waktu.savewaktu("kaabaoffset",kaabaoffset);
			
			
//			  try {
//				Geocoder geocoder = new Geocoder(waktu, Locale.getDefault());
//				  List<Address> addresses = geocoder.getFromLocation(12.5097254,74.9844009, 1);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
//			URL url = new URL("http://salah.com/rss?q=loc:12.5097254,74.9844009&method=4");
//			con = (HttpURLConnection) url.openConnection();
//			con.setReadTimeout(1000);
//			con.setConnectTimeout(1000);
//			con.setRequestMethod("GET");
////			con.addRequestProperty("Referer",					"http://blog.abdullahsolutions.com");
//			con.setDoInput(true);
//			con.connect();
//			
//			Log.d(TAG, "Trying to get latest data");
//			
//			if(Thread.interrupted())
//				throw new InterruptedException();
//			BufferedReader reader = new BufferedReader(
//					new InputStreamReader(con.getInputStream(),"UTF-8"));
//			
//			String payload;
//			String total="";
//			int i=0;
//			while((payload=reader.readLine())!=null){
//				System.out.println("payload :"+payload);
//				total+=payload;				
//			}			
//			
//			String htmlTextStr = Html.fromHtml(total).toString();			
//			Pattern waktupattern = Pattern.compile("(\\d+:\\d+)");			
//			Matcher waktumatch = waktupattern.matcher(htmlTextStr);			
//			int pos=0;
//			while(waktumatch.find()){				
//				switch(pos){
//				case 0:					
//					waktu.savewaktu(waktusolat.waktu_calc_method,waktumatch.group());
//					break;
//				case 1:
//					waktu.savewaktu(waktusolat.waktu_subuh,waktumatch.group());
//					break;
//				case 2:
//					waktu.savewaktu(waktusolat.waktu_syuruk,waktumatch.group());
//					break;
//				case 3:
//					waktu.savewaktu(waktusolat.waktu_zohor,waktumatch.group());
//					break;
//				case 4:
//					waktu.savewaktu(waktusolat.waktu_asar,waktumatch.group());
//					break;
//				case 5:
//					waktu.savewaktu(waktusolat.waktu_maghrib,waktumatch.group());
//					break;
//				case 6:
//					waktu.savewaktu(waktusolat.waktu_isya,waktumatch.group());
//					break;				
//				}				
//				pos++;				
//			}			
			waktu.savekemaskini();
			waktu.updatewaktuview();

//			reader.close();

		} catch (InterruptedException e) {
			Log.d(TAG, "Interrupted exception ", e);
//		} catch (MalformedURLException e) {
//			Log.d(TAG, "Malformed url exception ", e);
//		} catch (IOException e) {
//			Log.d(TAG, "IO exception ", e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}
}
