package jz.apps.kassepro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class einstellungen extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.einstellungen);
	    
	    final CheckBox cbw = (CheckBox)findViewById(R.id.cb_wechselgeld);
	    final CheckBox cbz = (CheckBox)findViewById(R.id.cb_zusammenfassung);
	    
	    final SharedPreferences sp = getSharedPreferences("Einstellungen", Context.MODE_PRIVATE);
	    
	    boolean boolWechselgeld = sp.getBoolean("wechselgeld", true);
	    boolean boolZusammnefassung = sp.getBoolean("zusammenfassung", true);
	    cbw.setChecked(boolWechselgeld);
	    cbz.setChecked(boolZusammnefassung);
	    
	    
	    cbw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("wechselgeld", cbw.isChecked());
				editor.commit();
				
				
			}
		});
	    
	    cbz.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("zusammenfassung", cbz.isChecked());
				editor.commit();
				
				
			}
		});
	    
	    Button btnz = (Button)findViewById(R.id.btn_einstellungen_zurueck);
	    btnz.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText etEinlage = (EditText)findViewById(R.id.et_kassenbestand);
				Editor editor = sp.edit();
				float floatEinlage = 0;
				try {
					floatEinlage = Float.parseFloat(etEinlage.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(einstellungen.this, "Falscher Wert bei Einlage. Wert wird nun auf 0 gesetzt.", Toast.LENGTH_SHORT).show();
					
				}
				editor.putFloat("einlage", floatEinlage);
				editor.commit();
				Intent intent = new Intent(einstellungen.this, MainTabletActivity.class);
				finish();
				startActivity(intent);
				
			}
		});
	    
	    
	}
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(einstellungen.this, MainTabletActivity.class);
		startActivity(intent);
		finish();
	}

}
