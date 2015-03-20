package de.jzapps.tools;

import jz.apps.kassepro.MainTabletActivity;
import jz.apps.kassepro.R;
import jz.apps.kassepro.zusammenfassung;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

public class DeviceChekcer extends Activity {

	
	private int intWidth;
	private int intHeight;
	private String strDeviceName;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.devicechecker);
	    
	    
	    TextView tv1 = (TextView)findViewById(R.id.textView1);
	    tv1.setText(this.getDeviceName());
	    
	    
	    DisplayMetrics metrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);

	    intHeight = metrics.heightPixels;
	    intWidth = metrics.widthPixels;
	    TextView tv2 = (TextView)findViewById(R.id.textView2);
	    tv2.setText("Auflösung: "+intWidth+" X "+intHeight);
	    
	    TextView tv3 = (TextView)findViewById(R.id.textView3);
	    tv3.setText("Pixeldichte in dpi: "+metrics.densityDpi);

	    
	}
	
	public String getDeviceName() {
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  if (model.startsWith(manufacturer)) {
		    return model;
		  } else {
		    return manufacturer + " " + model;
		  }
		}
	
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(DeviceChekcer.this, MainTabletActivity.class);
		startActivity(intent);
		finish();
	}

}
