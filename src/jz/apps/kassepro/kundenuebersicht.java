package jz.apps.kassepro;

import java.util.ArrayList;

import de.jzapps.sql.MySQLiteHelper;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;
import android.widget.TextView;

public class kundenuebersicht extends Activity {

	 private ArrayList<Artikel> alist ;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.kundenuebersicht);
	    
	    int kid = getIntent().getIntExtra("kundenId", 0);
		
		MySQLiteHelper sql = new MySQLiteHelper(this);
		
		String kundenName = sql.getKundenName(kid);
		
		TextView tv = (TextView)findViewById(R.id.tv_kundenheader);
		tv.setText(getString(R.string.kundenuebersicht_von) +" "+ kundenName);
		
		 DisplayMetrics metrics = new DisplayMetrics();
		    
		    getWindowManager().getDefaultDisplay().getMetrics(metrics);
		    
		    tv.setTextSize(metrics.heightPixels/50);
		    
		    ListView lv = (ListView)findViewById(R.id.lv_kue_artikel);
		   
		    alist = new ArrayList<Artikel>();
		    
		    //"SELECT anzahl, artikel, ezpreis, kunden_id, SUM (anzahl) as allanazahl FROM kundenselecteditems WHERE kunden_id = '"+id+"' GROUP BY artikel", null);
		    Cursor cursor = sql.getAllKundenItems(kid);
		 
		    if (cursor.moveToFirst()){
		    	Artikel artikel = new Artikel();
		    	artikel.setAnzahl(cursor.getInt(0));
		    	artikel.setBezeichnung(cursor.getString(1));
		    	artikel.setePreis(cursor.getDouble(2));
		    	alist.add(artikel);
		    	
		    	while (cursor.moveToNext()){
			    	Artikel artikel2 = new Artikel();
			    	artikel2.setAnzahl(cursor.getInt(0));
			    	artikel2.setBezeichnung(cursor.getString(1));
			    	artikel2.setePreis(cursor.getDouble(2));
			    	alist.add(artikel2);
			    }
		    
		    
		    
		    } else
		    {
		    	
		    }
		    
		    
		    
		    lv.setAdapter(new KundenUebersichtAdapter(alist , kundenuebersicht.this));
		    
		    
		
	}

}
