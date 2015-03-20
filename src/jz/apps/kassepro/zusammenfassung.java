package jz.apps.kassepro;

import java.text.NumberFormat;

import de.jzapps.sql.MySQLiteHelper;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class zusammenfassung extends Activity {

	
	float textSize;
	int kid;
	boolean kundenModus;
	boolean boolWechselgeld;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.zusammnefassung);
	    
	    final SharedPreferences sp = getSharedPreferences("Einstellungen", Context.MODE_PRIVATE);
		boolWechselgeld = sp.getBoolean("wechselgeld", true);
		
	    
	    
	    kid = getIntent().getIntExtra("kundenId", 0);
	    /*
	     * Wenn KundenNr 0 dann wurde nicht aus dem KundenModus diese Seite aufgerufen
	     */
	    if (kid > 0) {
	    	
	    	kundenModus = true;
	    } else {
	    	kundenModus = false;
	    }
	    
	    
	    
	    final NumberFormat formatter = NumberFormat.getCurrencyInstance();
	    
	    // Daten aus der Tabelle selectedItems holen
	    final MySQLiteHelper db = new MySQLiteHelper(this);
	    
	    
	    Cursor cursor;
	    if (kundenModus){
	    	cursor = db.getAllSelectedKundenItemsSumme(kid);
		    
	    } else {
	    	cursor = db.getAllSelectedItemsSumme();
		    
	    }
	    
	    
	    
	    
	    if (cursor.getCount()<1){
	    	
	    } else {
	    
	    
	    DisplayMetrics metrics = new DisplayMetrics();
	    
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    //double ergebnis = (Math.sqrt(metrics.heightPixels*metrics.heightPixels+metrics.widthPixels*metrics.widthPixels))/metrics.densityDpi;
	    String bildschirmModus="";
	    
	    // Bildschirmhöhe
	    final int bsHoehe = metrics.heightPixels;
	    final int bsBreite = metrics.widthPixels;

	    final float dialogTextSize = (float) (getResources().getDimension(R.dimen.text_size));

	      if (metrics.widthPixels > metrics.heightPixels){
	     
	    	bildschirmModus = "Landscape";
	    	
	    	
	    } else {
	    	bildschirmModus = "Portrait"; 
	    	
	    }
	    
	       
	        TextView tv_menge1 = (TextView)findViewById(R.id.tv_hmenge);
		    TextView tv_bez1 = (TextView)findViewById(R.id.tv_hbezeichnung);
		    TextView tv_ep1 = (TextView)findViewById(R.id.tv_hep);
		    TextView tv_gp1 = (TextView)findViewById(R.id.tv_hgp);
		    tv_menge1.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
		    tv_menge1.setText(R.string.Anz);
		    tv_bez1.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
		    tv_bez1.setText(R.string.bezeichnung);
		    tv_ep1.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
		    tv_ep1.setText(R.string.ep);
		    tv_gp1.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
		    tv_gp1.setText(R.string.gp);
		   
	  
	    
	   TableLayout table = (TableLayout)findViewById(R.id.tablelay);
	  
	   float zahlBetrag = 0;
	   
	   cursor.moveToFirst();
	   
	   TableRow tr1 = new TableRow(this);
	
	   TextView tv1_menge = new TextView(this);
	   tv1_menge.setWidth(metrics.widthPixels/5);
	   tv1_menge.setTextColor(getResources().getColor(R.color.weiss));
	   tv1_menge.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
	   tv1_menge.setGravity(Gravity.CENTER);
	   tv1_menge.setBackgroundResource(R.drawable.blackbutton);
	   
	   TextView tv1_bez = new TextView(this);
	   tv1_bez.setWidth((int) (metrics.widthPixels/2.85));
	   tv1_bez.setTextColor(getResources().getColor(R.color.weiss));
	   tv1_bez.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
	   tv1_bez.setGravity(Gravity.LEFT);
	   tv1_bez.setBackgroundResource(R.drawable.blackbutton);
	   
	   TextView tv1_ep = new TextView(this);
	   tv1_ep.setWidth(metrics.widthPixels/5);
	   tv1_ep.setTextColor(getResources().getColor(R.color.weiss));
	   tv1_ep.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
	   tv1_ep.setGravity(Gravity.RIGHT);
	   tv1_ep.setBackgroundResource(R.drawable.blackbutton);
	   
	   TextView tv1_gp = new TextView(this);
	   tv1_gp.setWidth(metrics.widthPixels/4);
	   tv1_gp.setTextColor(getResources().getColor(R.color.weiss));
	   tv1_gp.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
	   tv1_gp.setGravity(Gravity.RIGHT);
	   tv1_gp.setBackgroundResource(R.drawable.blackbutton);
	   
	  
	   tv1_bez.setText(cursor.getString(0));
	   tv1_menge.setText(cursor.getString(1));
	   tv1_ep.setText(formatter.format(cursor.getFloat(2)));
	   float gPreis = cursor.getInt(1)*cursor.getFloat(2);
	   tv1_gp.setText(formatter.format(gPreis));
	   zahlBetrag = zahlBetrag + gPreis;
	   tr1.addView(tv1_menge);
	   tr1.addView(tv1_bez);
	   tr1.addView(tv1_ep);
	   tr1.addView(tv1_gp);
	   table.addView(tr1);
	   
	   while (cursor.moveToNext()) {
		   
		   TableRow tr = new TableRow(this);
		   TextView tv_menge = new TextView(this);
		   TextView tv_bez = new TextView(this);
		   TextView tv_ep = new TextView(this);
		   TextView tv_gp = new TextView(this);
		   
		   tv_menge.setText(cursor.getString(1));
		   tv_menge.setWidth(metrics.widthPixels/5);
		   tv_menge.setTextColor(getResources().getColor(R.color.weiss));
		   tv_menge.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		   tv_menge.setGravity(Gravity.CENTER);
		   tv_menge.setBackgroundResource(R.drawable.blackbutton);
		   
		   tv_bez.setText(cursor.getString(0));
		   tv_bez.setWidth((int) (metrics.widthPixels/2.85));
		   tv_bez.setTextColor(getResources().getColor(R.color.weiss));
		   tv_bez.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		   tv_bez.setGravity(Gravity.LEFT);
		   tv_bez.setBackgroundResource(R.drawable.blackbutton);
		   
		   tv_ep.setText(formatter.format(cursor.getFloat(2)));
		   tv_ep.setWidth(metrics.widthPixels/5);
		   tv_ep.setTextColor(getResources().getColor(R.color.weiss));
		   tv_ep.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		   tv_ep.setGravity(Gravity.RIGHT);
		   tv_ep.setBackgroundResource(R.drawable.blackbutton);
		   
		   
		   tv_gp.setText(cursor.getString(3));
		   tv_gp.setWidth(metrics.widthPixels/4);
		   float gPreis2 = cursor.getInt(1)*cursor.getFloat(2);
		   tv_gp.setText(formatter.format(gPreis2));
		   tv_gp.setTextColor(getResources().getColor(R.color.weiss));
		   tv_gp.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		   tv_gp.setGravity(Gravity.RIGHT);
		   tv_gp.setBackgroundResource(R.drawable.blackbutton);
		   zahlBetrag += gPreis2;
		   tr.addView(tv_menge);
		   tr.addView(tv_bez);
		   tr.addView(tv_ep);
		   tr.addView(tv_gp);
		   table.addView(tr);
	}
	   
	   
	  TextView tv_zzt = (TextView)findViewById(R.id.tv_zuzahlentext);
	  tv_zzt.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
	   
	   TextView tv_zahlBetrag = (TextView)findViewById(R.id.tv_zahlBetrag);
	   tv_zahlBetrag.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
	   
	   
	   
	   
	   final String strZahlbetrag = formatter.format(zahlBetrag);
	   final float zahlGeld = zahlBetrag;
	   tv_zahlBetrag.setText(strZahlbetrag);
	   
	   Button btn_geldAbbruch = (Button)findViewById(R.id.btn_geldabbruch);
	   btn_geldAbbruch.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(zusammenfassung.this, MainTabletActivity.class);
			startActivity(intent);
			finish();
			
		}
	});
	   
	   Button btn_erhalten = (Button)findViewById(R.id.btn_geldErhalten);
	   btn_erhalten.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			
			if (boolWechselgeld){			
			
				/*
				 * Wenn Wechselgeld checked dann Dialog öffnen
				 */
				final Dialog dialog = new Dialog(zusammenfassung.this);
				dialog.setContentView(R.layout.wechselgelddialog);
				dialog.getWindow().getAttributes().width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = WindowManager.LayoutParams.MATCH_PARENT;
				dialog.getWindow().setAttributes(lp);
				TextView tva = (TextView)dialog.findViewById(R.id.tv_wechselgeld);
				tva.setTextSize(getResources().getDimension(R.dimen.TxtSizeGesBetrag));
			
				dialog.setTitle("Wechselgeld");
			
				TextView tv1 = (TextView)dialog.findViewById(R.id.tv_wgelddialog_1);
				tv1.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
				TextView tv2 = (TextView)dialog.findViewById(R.id.tv_wgelddialog_2);
				tv2.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
				TextView tv3 = (TextView)dialog.findViewById(R.id.tv_wgelddialog_3);
				tv3.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
				TextView tv_zzb = (TextView)dialog.findViewById(R.id.tv_zuzahlenderBetragDialog);
				tv_zzb.setText(strZahlbetrag);
				tv_zzb.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
				final EditText et_eingabe = (EditText)dialog.findViewById(R.id.et_betragEingabe);
				et_eingabe.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
				et_eingabe.setWidth(bsBreite/10);
				
				Button btn_ok = (Button)dialog.findViewById(R.id.btn_wgeldok);
				btn_ok.setBackgroundResource(R.drawable.bluebutton);
				btn_ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// Wechselgeld berechnen
						TextView tvw = (TextView)dialog.findViewById(R.id.tv_wechselgeld);
						tvw.setTextSize(getResources().getDimension(R.dimen.TxtSizeGesBetrag));
						double eingabe;
						try {
							eingabe = Float.parseFloat(et_eingabe.getText().toString());
						} catch (NumberFormatException e) {
							eingabe = 0.00;
						}
						
						double rueckgabe = eingabe - zahlGeld;
						tvw.setText(formatter.format(rueckgabe));
					}
				});
				
			
				Button btn_wga = (Button)dialog.findViewById(R.id.btn_wgeld_abbrechen);
				btn_wga.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
			Button btn_wge = (Button)dialog.findViewById(R.id.btn_wgeld_erhalten);
			btn_wge.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					/*
					 * Daten in verkaufshistorie eintragen
					 * Daten holen und in Tabelle umsaetze eintragen
					 * 
					 */
					Cursor cursor;
					if (kundenModus) {
						cursor = db.getAllSelectedKundenItemsSumme(kid);
					} else {
						cursor = db.getAllSelectedItemsSumme();
					}
					while (cursor.moveToNext()){
					
						String artikel = cursor.getString(0);
						int anzahl = cursor.getInt(3);
						double ezpreis = cursor.getDouble(2);
						
						db.umsaetzeEintragen(artikel, anzahl, ezpreis, kid);
						//Log.d("IN UMSÄTZE-DB",anzahl+"x "+artikel+" zu je "+ezpreis);
						
					}
					
					if (kundenModus) {
							db.deleteAllKundenItems(kid);
						} else {
							db.cleanDBSelectedItems();
							
						}
					
					
					
					dialog.dismiss();
					Intent intent = new Intent(zusammenfassung.this, MainTabletActivity.class);
					startActivity(intent);
					finish();
					
					
					
				}
			});
			
			dialog.show();
			
			} else {
				Cursor cursor;
				if (kundenModus) {
					cursor = db.getAllSelectedKundenItemsSumme(kid);
				} else {
					cursor = db.getAllSelectedItemsSumme();
				}
				while (cursor.moveToNext()){
				
					String artikel = cursor.getString(0);
					int anzahl = cursor.getInt(3);
					double ezpreis = cursor.getDouble(2);
					
					db.umsaetzeEintragen(artikel, anzahl, ezpreis, kid);
					//Log.d("IN UMSÄTZE-DB",anzahl+"x "+artikel+" zu je "+ezpreis);
					
				}
				
				if (kundenModus) {
						db.deleteAllKundenItems(kid);
					} else {
						db.cleanDBSelectedItems();
						
					}
				
				
				Intent intent = new Intent(zusammenfassung.this, MainTabletActivity.class);
				startActivity(intent);
				finish();
				
			}
		}
	});
	   
	    }
	}
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(zusammenfassung.this, MainTabletActivity.class);
		startActivity(intent);
		finish();
	}

}
