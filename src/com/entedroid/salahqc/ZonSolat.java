package com.entedroid.salahqc;

//import com.abdullahsolutions.solatmalaysia.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ZonSolat extends Activity implements OnClickListener {
	
	String calcMethod = "0";
	private static final String TAG = "ZonSolat";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zon_solat);

		View selectNegeriButton = findViewById(R.id.EGAS);
		selectNegeriButton.setOnClickListener(this);

		View selectUISKSButton = findViewById(R.id.UISKS);
		selectUISKSButton.setOnClickListener(this);
		
		View selectUISKHButton = findViewById(R.id.UISKH); 
		selectUISKHButton.setOnClickListener(this);
		
		View selectISNAButton = findViewById(R.id.ISNA);
		selectISNAButton.setOnClickListener(this);
		
		View selectMWLButton = findViewById(R.id.MWL);
		selectMWLButton.setOnClickListener(this);
		
		View selectUAQButton = findViewById(R.id.UAQ);
		selectUAQButton.setOnClickListener(this);
		
		View selectFIIButton = findViewById(R.id.FII);
		selectFIIButton.setOnClickListener(this);		

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.EGAS:
			calcMethod = "1";
			break;
		case R.id.UISKS:
			calcMethod = "2";
			break;
		case R.id.UISKH:
			calcMethod = "3";
			break;
		case R.id.ISNA:
			calcMethod = "4";
			break;
		case R.id.MWL:
			calcMethod = "5";
			break;
		case R.id.UAQ:
			calcMethod = "6";
			break;
		case R.id.FII:
			calcMethod = "7";
			break;			
		}
		
		saveCalcMethod(calcMethod);
		finish();
	}

	
	
	
//	private void openNegeriDialog() {
//		new AlertDialog.Builder(this).setTitle("Calculation Method Selected")
//				.setItems(negeri, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialoginterface, int i) {
//						selectnegeri(i);
//					}
//				}).show();
//	}

//	private void selectnegeri(int i) {
//		Log.d(TAG, "selected negeri " + i);
//		getPreferences(MODE_PRIVATE).edit().putLong("negeri", i).commit();
//		final Button button = (Button) findViewById(R.id.select_negeri);
//		button.setText(negeri[i]);
//	}
	
	private void saveCalcMethod(String calcMethod2) {
		Log.d(TAG, "selected Method " + calcMethod2);
		//getPreferences(MODE_PRIVATE).edit().putString("calcMethod", calcMethod2).commit();
		getSharedPreferences("MyPrefsFile", 0).edit().putString("calcMethod", calcMethod2).commit();

//		final Button button = (Button) findViewById(R.id.select_method);
//		button.setText(negeri[i]);
	}	




}
