package jz.apps.kassepro;

import java.io.File;
import java.text.NumberFormat;



import de.jzapps.billingUtils.IabHelper;
import de.jzapps.billingUtils.IabResult;
import de.jzapps.billingUtils.Inventory;
import de.jzapps.billingUtils.Purchase;
import de.jzapps.kassef.InAppBillingActivity;
import de.jzapps.kassef.R;
import de.jzapps.sql.MySQLiteHelper;
import de.jzapps.tools.DeviceChekcer;


import android.app.Activity;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainTabletActivity extends Activity implements OnClickListener, OnLongClickListener {

		public static final String APPLICATIONS_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk3+su+D0JhHWG78n2snHRqGftuT0TBBk/gkpIRtt4VaNtkjU33GmabmxcjyCLkDIO7wymkpJWESB92WVmKf6Ac3mRqGHCEFzZb00FbMlD3LfVLa2kzxJs1+4DFH+NBBCPPBHCdDfVrjJ3mq3XqXy3451RJnS2DQg/THV0dd/SumT58ItZgcC9Ik+kLc+mEir8fdnyHXHzSVA+jPPPnfTov9NrW2gtg5nk7mhS49h5IA9Xyw1u3QTbY/2E803aLLOLk3BUExaXhcu3RQOHbAhVtuh266YBiX43Uz4PXfKfzkeRCgSclEQ7oiUFRmHtdg3Gq86r6mhDRugSbaLNLBZxQIDAQAB";
		public static final String PREMIUM = "premium";
		public static final String ADFREE = "adfree";
		public static final String BONUS = "bonus";
		public static final int APP_BONUS_MAX = 1;
	    
	    TextView tv, tvi;
		boolean tabletVersion = false;
		int z=0;
		int s=0;
		String btnFarbe;
		float gesamtBetrag;
		double btnTextSize;
		String strInfoleiste;
		String kundenName;
		int anzahl;
		boolean kundenModus;
		int kid;
		int modus; // 0 = tablet , 1 = smartphone
		Context context;
		boolean boolLandscape = false;
		boolean boolWechselgeld = true;
		boolean boolZusammenfassung = true;
		
		int appBonus = 0;
		
		boolean isPremium = false;
		boolean isAdFree = false;
		
		IabHelper billingHelperMain;
		IabHelper.QueryInventoryFinishedListener gotInventoryListener;
		IabHelper.OnConsumeFinishedListener consumeFinishedListener;
		
		SharedPreferences bonusInventar;
		Editor bonusInventarEditor;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.tablet_main);
			
			bonusInventar = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			bonusInventarEditor = bonusInventar.edit();
			
			setUpBillingHelper(APPLICATIONS_PUBLIC_KEY);
			
			final SharedPreferences sp = getSharedPreferences("Einstellungen", Context.MODE_PRIVATE);
			boolWechselgeld = sp.getBoolean("wechselgeld", true);
			boolZusammenfassung = sp.getBoolean("zusammenfassung", true);
			
			SharedPreferences prefs = this.getSharedPreferences("Versionsmodus", Context.MODE_PRIVATE);
			
			
			modus = prefs.getInt("modus", 0);
			
			//Log.d("MODUS",String.valueOf(modus));
			
			kid = getIntent().getIntExtra("kundenId", 0);
			if (kid>0){
				kundenModus = true;
			} else {
				kundenModus = false;
			}
			
			/*
			 * Was passiert, wenn eine Kundennr übermittelt wird
			 * Name des Kunden aus Tabelle holen
			 * Alle Artikel für den Kunden aus der Tabell holen und anzeigen
			 */
						 
			NumberFormat formatter = NumberFormat.getCurrencyInstance();
			
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			final int bsHoehe = metrics.heightPixels;
			final int bsBreite = metrics.widthPixels;
			    
			    
			   
			    if (metrics.widthPixels > metrics.heightPixels){
			    	boolLandscape = true;
			    	
			    	
			    } else {
			    	boolLandscape = false;
			    	
			    }
			    
			    
			    //Toast.makeText(this, "Grï¿½ï¿½e in Zoll: "+String.valueOf(ergebnis), Toast.LENGTH_SHORT).show();
			    final LinearLayout linlayRoot = (LinearLayout)findViewById(R.id.linlayroot);
			    final LinearLayout linlayhead = new LinearLayout(this);
			    double maxHeight = metrics.heightPixels*0.1;
			    
			   /*
			    * Zeile mit der Preisanzeige
			    */
			    
			    /*
			     * Button für Umsatzstatistik
			     */
			    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (metrics.widthPixels*0.1), android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
			    
			    ImageView iv = new ImageView(this);
			    iv.setLayoutParams(layoutParams);
			    iv.setImageResource(R.drawable.summe);
			    iv.setBackgroundResource(R.drawable.infoleiste);
			    iv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainTabletActivity.this, umsatzstatistik.class);
						startActivity(intent);
						finish();
						
					}
				});
			    /*
			     * Button für Kundenverwaltung
			     */
			    ImageView ivk = new ImageView(this);
			    ivk.setLayoutParams(layoutParams);
			    ivk.setImageResource(R.drawable.kundenverwaltung);
			    ivk.setBackgroundResource(R.drawable.infoleiste);
			    ivk.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						if(isPremium || appBonus >= 1) {
							
							Intent intent = new Intent(MainTabletActivity.this, kundenverwaltung.class);
							startActivity(intent);
							finish();
							
						}
						
						else {
							
							Toast.makeText(getApplicationContext(), "Kundenverwaltung ist ein Premium-Feature",
									Toast.LENGTH_LONG).show();
							Intent billingActivity = new Intent(getApplicationContext(), InAppBillingActivity.class);
							startActivity(billingActivity);
							finish();
						}
						
					}
				});
			    /*
			     * Button für Hilfe
			     */
			    ImageView ivh = new ImageView(this);
			    ivh.setLayoutParams(layoutParams);
			    ivh.setImageResource(R.drawable.img_hilfe);
			    ivh.setBackgroundResource(R.drawable.infoleiste);
			    ivh.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						
						Intent intent = new Intent(MainTabletActivity.this, hilfe.class);
						startActivity(intent);
						finish();
						
						
					}
				});
			    
			    ivh.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						Intent intent = new Intent(MainTabletActivity.this, DeviceChekcer.class);
						startActivity(intent);
						finish();
						return false;
					}
				});
			    
			    //TODO
			    
			    ImageView ivc = new ImageView(this); // ImageView erzeugen für Icon mit dem Summenzeichen
			    ivc.setLayoutParams(layoutParams);
			    ivc.setImageResource(R.drawable.kasse_shopping_cart_45_45);
			    ivc.setBackgroundResource(R.drawable.blackbutton);
			    ivc.setOnClickListener(new OnClickListener() {
					
					@Override // Bei Klick auf den Einkaufswagen wird die In-App-Billing Activity aufgerufen und diese geschlossen (finish())
					public void onClick(View v) {
						Intent billingActivity = new Intent(getApplicationContext(), InAppBillingActivity.class);
						startActivity(billingActivity);
						finish();
					}
				});
			 
			    
			    /*
			     * Anzuzeigender GesamtBetrag aus Tabelle selectedItems holen
			     */
			    gesamtBetrag = 0;
			    
			    final MySQLiteHelper db = new MySQLiteHelper(this);
			    Cursor cursor;
			    
			    
			    
			    if (kundenModus==true) {
			    	kundenName = db.getKundenName(kid);
			    	gesamtBetrag = (float) db.getActKundenGesmatBetrag(kid);
			    	
			    } else {
			    	cursor = db.getActGesmatBetrag();
			    	if (cursor.moveToFirst()){
				    	gesamtBetrag = cursor.getInt(0) * cursor.getFloat(1);
				    	
				    	while (cursor.moveToNext()){
				    		gesamtBetrag += cursor.getInt(0) * cursor.getFloat(1);
				    	}
				    	
				    }
			    	 
			    }
			    
			    linlayhead.addView(iv);
			    linlayhead.addView(ivk);
			    linlayhead.addView(ivh);
			    linlayhead.addView(ivc);
			    
			    /*
			     * Preisanzeige
			     */
			    linlayRoot.addView(linlayhead);
			    tv = new TextView(this);
			    //LayoutParams params = new LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.RIGHT|Gravity.CENTER_HORIZONTAL));
			    tv.setHeight((int) maxHeight);
			    tv.setWidth((int) (metrics.widthPixels*0.8));
			    tv.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
			    tv.setTextSize(getResources().getDimension(R.dimen.TxtSizeGesBetrag));
			    tv.setText(formatter.format(gesamtBetrag));
			    tv.setTextColor(getResources().getColor(R.color.weiss));
			    tv.setBackgroundResource(R.drawable.infoleiste);
			    linlayhead.addView(tv);
			    /*
			     * Infoleiste
			     */
			    final LinearLayout linlayinfo = new LinearLayout(this);
			    tvi = new TextView(this);
			    tvi.setHeight((int) (maxHeight/1.2));
			    tvi.setWidth(metrics.widthPixels);
			    tvi.setGravity(Gravity.CENTER);
			    tvi.setTextSize(getResources().getDimension(R.dimen.TxtSizeInfoLeiste));
			    tvi.setText(R.string.infoleiste);
			    tvi.setTextColor(getResources().getColor(R.color.weiss));
			    tvi.setBackgroundResource(R.drawable.infoleiste);
			    if (kid>0) tvi.setText(kundenName);
			    
			    
			    linlayinfo.addView(tvi);
			    linlayRoot.addView(linlayinfo);
			    //final LinearLayout linlay = (LinearLayout)findViewById(R.id.tastenbereich);
			    /*
			     *  Wenn Bildschirmmodus Portrait, dann in der nichtTabletVersion 3x4 Felder, in der TabletVersion 6x5
			     *  Wenn Bildschirmmodus Landscape, dann in der nichtTabletVersion 4x3 Felder, in der TabletVersion 5x6
			     */
			    
			   
			   
			    if (!boolLandscape){
			    	s = 5;
			    	z = 6;
			    	if (modus == 1){
			    		s = 3;
			    		z = 4;
			    	}
			    	
			    }
			    if (boolLandscape){
			    	s=6;
			    	z=5;
			    	
			    	if (modus == 1){
			    		s = 4;
			    		z = 3; 
			    	}
			    }
			   
			    int btnzaehler = 1;
			    for (int i = 1; i <= z;i++){
			    	LinearLayout linlaynew = new LinearLayout(this);
			    	linlaynew.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			    	linlaynew.setOrientation(LinearLayout.HORIZONTAL);
			    	linlayRoot.addView(linlaynew);
			    	
			    	for (int j = 1; j <= s; j++){
			    		final int btnid = 100 + btnzaehler; 
			    		btnzaehler++;
			    		final Button btn = new Button(this);
			    		Taste taste = new Taste();
			    		taste.setId(btnid);
			    		btn.setTextColor(getResources().getColor(R.color.weiss));
			    		btn.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
			    		
			    		/*
			    		 * Datenbank prï¿½fen, ob dort eine Taste mit der passenden Id eingetragen ist, wenn ja dann diese laden
			    		 * ansonsten leere Taste erstellen
			    		 */
			    		
			    		NumberFormat instance = NumberFormat.getInstance();
			    	    instance.setMaximumFractionDigits(2);
			    		
			    	    
			    		
			    		if (db.checkButton(btnid) == true){
			    			
			    			//Log.d("BTNVALUES", String.valueOf(taste)+" "+String.valueOf(taste.getId()));
			    			try {
			    				db.getButtonValues(taste, btnid);
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(MainTabletActivity.this, "Problem mit der Datenbank", Toast.LENGTH_SHORT).show();
							}
			    			
			    			try {
			    				if (taste.getFarbe().equals("blue")) btn.setBackgroundResource(R.drawable.bluebutton);
				    			if (taste.getFarbe().equals("red")) btn.setBackgroundResource(R.drawable.redbutton);
				    			if (taste.getFarbe().equals("brown")) btn.setBackgroundResource(R.drawable.brownbutton);
				    			if (taste.getFarbe().equals("yellow")) btn.setBackgroundResource(R.drawable.yellowbutton);
				    			if (taste.getFarbe().equals("green")) btn.setBackgroundResource(R.drawable.greenbutton);
				    			if (taste.getFarbe().equals("orange")) btn.setBackgroundResource(R.drawable.orangebutton);
				    			if (taste.getFarbe().equals("pink")) btn.setBackgroundResource(R.drawable.pinkbutton);
				    			if (taste.getFarbe().equals("black")) btn.setBackgroundResource(R.drawable.greybutton);
							} catch (Exception e) {
								e.printStackTrace();
								
							}
			    			
			    			
			    			
			    			if (kundenModus){
			    						anzahl = db.anzahlKundeErmitteln(taste.getArtikel(),kid);
			    			} else {
			    						anzahl = db.anzahlErmitteln(taste.getArtikel());
			    			    		
			    			}
			    			
			    			
			    			btn.setText(taste.getArtikel()+"\n"+formatter.format(taste.getPreis())+" ("+anzahl+")");
			    		
			    		} else {
			    			
			    			btn.setText(" \n ");
			    			btn.setBackgroundResource(R.drawable.bluebutton);
			    		}
			    		
			    		btn.setId(btnid);
			    		btn.setWidth(metrics.widthPixels/s);
			    		if (boolLandscape){
			    			btn.setHeight((int) (metrics.heightPixels*0.5/4.2));
			    		} else {
			    			btn.setHeight((int) (metrics.heightPixels*0.5/4.5));
			    		}
				    	
				    	btn.setOnClickListener(this);
				    	btn.setOnLongClickListener(this);
				    	linlaynew.addView(btn);
				    	
			    	}
			    	
			    }
			    
			    /*
			     * letzte Zeile hinzufï¿½gen
			     */
			    LinearLayout linlaynew = new LinearLayout(this);
		    	linlaynew.setOrientation(LinearLayout.HORIZONTAL);
		    	linlayRoot.addView(linlaynew);
		    	
		    	Button btn = new Button(this);
		    	btn.setId(99);
		    	if (kundenModus){
		    		btn.setBackgroundResource(R.drawable.greenbutton);
					
		    	} else {
		    		btn.setBackgroundResource(R.drawable.redbutton);
		    	}
		    	
				btn.setWidth(metrics.widthPixels/3);
		    	btn.setHeight((int) (metrics.heightPixels*0.6/4.2));
		    	btn.setText(R.string.kundenmodus);
		    	btn.setTextColor(getResources().getColor(R.color.weiss));
		    	btn.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		    	if (kundenModus) btn.setOnClickListener(this);
		    	linlaynew.addView(btn);
		    	
		    	Button btn2 = new Button(this);
		    	btn2.setId(88);
		    	
				btn2.setBackgroundResource(R.drawable.greybutton);
				btn2.setWidth(metrics.widthPixels/3);
				btn2.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
				btn2.setTextColor(getResources().getColor(R.color.weiss));
		    	btn2.setHeight((int) (metrics.heightPixels*0.6/4.2));
		    	btn2.setOnClickListener(this);
		    	btn2.setText(R.string.zur_kasse);
		    	
		    	
		    	linlaynew.addView(btn2);
		    	
		    	Button btn3 = new Button(this);
		    	btn3.setId(77);
				btn3.setBackgroundResource(R.drawable.greybutton);
				btn3.setWidth(metrics.widthPixels/3);
				btn3.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
				btn3.setTextColor(getResources().getColor(R.color.weiss));
		    	btn3.setHeight((int) (metrics.heightPixels*0.6/4.2));
		    	btn3.setText(R.string.extra);
		    	btn3.setOnClickListener(this);
		    	linlaynew.addView(btn3);
		    	
		    
	    
	}

		@SuppressWarnings("deprecation")
		@Override
		public boolean onLongClick(View v) {
			MySQLiteHelper sql = new MySQLiteHelper(this);
			final int btn_id = v.getId();
			
			String kartikel = sql.getButtonArtikel(btn_id);
			//Log.d("ItemCount",String.valueOf(sql.getItemCount(btn_id)));
			//Log.d("KundItem",String.valueOf(sql.anzahlInKundenItems(kartikel)));
			if (sql.getItemCount(btn_id)>0 || sql.anzahlInKundenItems(kartikel)>0){
				String strArtikel = sql.getButtonArtikel(btn_id);
				//Log.d("Artiekl",kartikel+"="+strArtikel);
				float flEZPreis = sql.getButtonPreis(btn_id);
				int anzahl;
				if (kundenModus){
					if (sql.anzahlKundeErmitteln(strArtikel, kid)>0) {
						anzahl = sql.decreaseKundenItem(strArtikel, kid);
						tvi.setText("Storno -> 1x "+strArtikel);
						gesamtBetrag -= flEZPreis;
					} else {
						anzahl = sql.anzahlKundeErmitteln(strArtikel, kid);
					}
					
				} else {
					
					if (sql.anzahlErmitteln(strArtikel)>0){
						anzahl = sql.decreaseItem(strArtikel);
						tvi.setText("Storno -> 1x "+strArtikel);
						gesamtBetrag -= flEZPreis;
						
					} else {
						anzahl = sql.anzahlErmitteln(strArtikel);
					}
					
				}
				
				Button btnde = (Button)findViewById(btn_id);
				NumberFormat formatter = NumberFormat.getCurrencyInstance();
				btnde.setText(strArtikel+"\n"+formatter.format(flEZPreis)+" ("+anzahl+")");
				tv.setText(formatter.format(gesamtBetrag));
				
				
			} else {
				
				// Wenn alles auf 0 dann öffnet sich der Dialog für die Tastenwerte
				
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_tastenwert);
			dialog.getWindow().getAttributes().width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		    lp.copyFrom(dialog.getWindow().getAttributes());
		    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		    dialog.getWindow().setAttributes(lp);
			
			dialog.setTitle(R.string.tastenzuweisung);
			TextView tv_bez = (TextView)dialog.findViewById(R.id.tv_twbez);
			tv_bez.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
			TextView tv_preis = (TextView)dialog.findViewById(R.id.tv_twpreis);
			tv_preis.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
			final Button btnvorschau = (Button) dialog.findViewById(R.id.dialogvorschaubtn);
			final EditText artikel = (EditText) dialog.findViewById(R.id.artikelname);
			artikel.setBackgroundColor(Color.WHITE);
			artikel.setTextColor(Color.BLACK);
			final EditText preis = (EditText) dialog.findViewById(R.id.preis);
			preis.setBackgroundColor(Color.WHITE);
			preis.setTextColor(Color.BLACK);
			
			DisplayMetrics metrics = new DisplayMetrics();
		    getWindowManager().getDefaultDisplay().getMetrics(metrics);
			Button btn = (Button) dialog.findViewById(R.id.dialog_speichern);
			Button btnv = (Button) dialog.findViewById(R.id.dialog_verwerfen);
			Button btnd = (Button) dialog.findViewById(R.id.dialog_taste_loeschen);
			
			if (sql.getButtonColor(btn_id).equals("blue")) btnvorschau.setBackgroundResource(R.drawable.bluebutton);
			if (sql.getButtonColor(btn_id).equals("red")) btnvorschau.setBackgroundResource(R.drawable.redbutton);
			if (sql.getButtonColor(btn_id).equals("brown")) btnvorschau.setBackgroundResource(R.drawable.brownbutton);
			if (sql.getButtonColor(btn_id).equals("yellow")) btnvorschau.setBackgroundResource(R.drawable.yellowbutton);
			if (sql.getButtonColor(btn_id).equals("green")) btnvorschau.setBackgroundResource(R.drawable.greenbutton);
			if (sql.getButtonColor(btn_id).equals("orange")) btnvorschau.setBackgroundResource(R.drawable.orangebutton);
			if (sql.getButtonColor(btn_id).equals("pink")) btnvorschau.setBackgroundResource(R.drawable.pinkbutton);
			if (sql.getButtonColor(btn_id).equals("black")) btnvorschau.setBackgroundResource(R.drawable.greybutton);
			//Log.d("BTNCOLOR",sql.getButtonArtikel(btn_id));
			
			ImageButton btn_blue = (ImageButton) dialog.findViewById(R.id.clrbtn_blue);
			btn_blue.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				btnvorschau.setBackgroundResource(R.drawable.bluebutton);	
				btnFarbe = "blue";
				}
			});
			ImageButton btn_yellow = (ImageButton) dialog.findViewById(R.id.clrbtn_yellow);
	        btn_yellow.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				btnvorschau.setBackgroundResource(R.drawable.yellowbutton);	
				btnFarbe = "yellow";
				}
			});
	        ImageButton btn_green = (ImageButton) dialog.findViewById(R.id.clrbtn_green);
	        btn_green.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				btnvorschau.setBackgroundResource(R.drawable.greenbutton);	
				btnFarbe = "green";
				}
			});
	        ImageButton btn_red = (ImageButton) dialog.findViewById(R.id.clrbtn_red);
	        btn_red.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				btnvorschau.setBackgroundResource(R.drawable.redbutton);	
				btnFarbe = "red";
				}
			});
	        ImageButton btn_brown = (ImageButton) dialog.findViewById(R.id.clrbtn_brown);
	        btn_brown.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				btnvorschau.setBackgroundResource(R.drawable.brownbutton);	
				btnFarbe = "brown";
				}
			});
	        ImageButton btn_pink = (ImageButton) dialog.findViewById(R.id.clrbtn_pink);
	        btn_pink.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				btnvorschau.setBackgroundResource(R.drawable.pinkbutton);	
				btnFarbe = "pink";
				}
			});
	        ImageButton btn_org = (ImageButton) dialog.findViewById(R.id.clrbtn_orange);
	        btn_org.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				btnvorschau.setBackgroundResource(R.drawable.orangebutton);	
				btnFarbe = "orange";
				}
			});
	        ImageButton btn_black = (ImageButton) dialog.findViewById(R.id.clrbtn_black);
	        btn_black.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				btnvorschau.setBackgroundResource(R.drawable.blackbutton);	
				btnFarbe = "black";
				
				}
			});
			
			double maxHeight = metrics.heightPixels*0.1;
			btnvorschau.setTextSize((float) (getResources().getDimension(R.dimen.TxtSizeSysBtn)));
			
			// Je nach ausgewï¿½hlter Farbe die entsprechende Drawable laden
			
		
			btnvorschau.setWidth(metrics.widthPixels/s);
	    	btnvorschau.setHeight((int) (metrics.heightPixels*0.6/4));
			MySQLiteHelper db = new MySQLiteHelper(this);
			if (db.checkButton(btn_id)){
				artikel.setText(db.getButtonArtikel(btn_id));
				 String strPreis = String.format("%.2f",db.getButtonPreis(btn_id));
				 strPreis = strPreis.replace(",",".");
				
				preis.setText(strPreis);
				btnvorschau.setText(db.getButtonArtikel(btn_id)+"\n"+strPreis);
			}
			artikel.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					((Button) dialog.findViewById(R.id.dialogvorschaubtn)).setText(s+"\n"+preis.getText().toString());
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
				}
			});		
			
			btnv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					
				}
			});
			preis.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					((Button) dialog.findViewById(R.id.dialogvorschaubtn)).setText(artikel.getText().toString()+"\n"+s);
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
				}
			});
			
			btnd.setOnLongClickListener(new OnLongClickListener() {
				
				
				@Override
				public boolean onLongClick(View v) {
					
					MySQLiteHelper sql = new MySQLiteHelper(MainTabletActivity.this);
					if (sql.checkButton(btn_id)){
						sql.deleteButton(btn_id);
						
					}
					
					dialog.dismiss();
					Intent intent = new Intent(MainTabletActivity.this, MainTabletActivity.class);
					finish();
					startActivity(intent);
					return false;
				}
			});
			
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					/*
					 * Neuen Tastenwert in die Datenbank eintragen wenn noch nicht vorhanden und mainscreen neu laden, damit die neue Taste angezeigt wird
					 */
					
					
					
					
					 if (btnFarbe == null) btnFarbe = "blue";
					// Log.d("ButtonFarbe", btnFarbe);
					 
					MySQLiteHelper sql = new MySQLiteHelper(MainTabletActivity.this);
					
					String strPreis = preis.getText().toString();
					float floatPreis = 0;
				try {
					floatPreis = Float.parseFloat(strPreis);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(MainTabletActivity.this, "Preis im falschen Format angegeben X.XX", Toast.LENGTH_SHORT).show();
				}
					
					
					String strArtikel = artikel.getText().toString();
					
					/*
					 * Es wird geprüft, ob der Artikelname die Zeichen ' oder " enthält, wenn ja, wird diesem ein \ vorangestellt.
					 */
					
					if (strPreis == "" || strArtikel == "" || floatPreis == 0){
						
					} else {
						if(sql.checkButton(btn_id)==false){ 
							try {
								
								sql.addButton(btn_id, artikel.getText().toString(), floatPreis, btnFarbe);
								dialog.dismiss();
								Intent intent = new Intent(MainTabletActivity.this, MainTabletActivity.class);
								finish();
								startActivity(intent);
								
							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
								
								preis.setBackgroundResource(R.drawable.redbutton);
								
								Toast.makeText(MainTabletActivity.this, "Fehler bei Tastenerstellung", Toast.LENGTH_LONG).show();
							} catch (SQLException se){
								Toast.makeText(MainTabletActivity.this, "FEHLER/ERROR", Toast.LENGTH_SHORT).show();
								se.printStackTrace();
								dialog.dismiss();
								Intent intent = new Intent(MainTabletActivity.this, MainTabletActivity.class);
								finish();
								startActivity(intent);
							}
							
							
							} else {
								
								try {
									sql.updateButton(btn_id, strArtikel, floatPreis, btnFarbe) ;
									//Log.d("BTN SCHON VORHANDEN","UPDATE ERFOLGREICH");
								} catch (SQLException se){
									se.printStackTrace();
									Toast.makeText(MainTabletActivity.this, "Fehler bei Tastenerstellung", Toast.LENGTH_LONG).show();
									//Log.d("BTN SCHON VORHANDEN","FEHLER BEI UPDATE");
								}
									
								dialog.dismiss();
								Intent intent = new Intent(MainTabletActivity.this, MainTabletActivity.class);
								finish();
								startActivity(intent);
								
								}
						
					}
					
					
				}
			});
			dialog.show();
			
			}
			return true;
		}

		@Override
		public void onClick(View v) {
			final NumberFormat formatter = NumberFormat.getCurrencyInstance();
			if (v.getId()<100){
				switch (v.getId()) {
				case 77: // Unterste Zeile "EXTRA": Dialog anzeigen
					final Dialog dialog = new Dialog(this);
					 DisplayMetrics metrics = new DisplayMetrics();
					    getWindowManager().getDefaultDisplay().getMetrics(metrics);
					    //double ergebnis = (Math.sqrt(metrics.heightPixels*metrics.heightPixels+metrics.widthPixels*metrics.widthPixels))/metrics.densityDpi;
					    final int bsHoehe = metrics.heightPixels;
					    final int bsBreite = metrics.widthPixels;
					dialog.setContentView(R.layout.extra_dialog);
					dialog.getWindow().setLayout((int) (bsBreite*0.9), (int) (bsHoehe*0.9));
					dialog.setTitle(R.string.extra);
					
					
					final EditText et_eartikel = (EditText)dialog.findViewById(R.id.et_extra_bez);
					final EditText et_ePreis = (EditText)dialog.findViewById(R.id.et_extra_preis2);
					final EditText et_eMenge = (EditText)dialog.findViewById(R.id.et_extra_menge);
					Button btn_ab = (Button)dialog.findViewById(R.id.btn_extra_abbruch);
					Button btn_ok = (Button)dialog.findViewById(R.id.btn_extra_ok);
					btn_ok.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							
							String streArtikel = et_eartikel.getText().toString();
							double ePreis;
							try {
								 ePreis = Double.parseDouble(et_ePreis.getText().toString());
								 
							} catch (Exception e) {
								e.printStackTrace();
								ePreis = 0;
							}
							
							Log.d("ePreis", String.valueOf(ePreis));
							
							int eMenge;
							try {
								eMenge = Integer.parseInt(et_eMenge.getText().toString());
							} catch (Exception e) {
								e.printStackTrace();
								eMenge = 0;
							}
							
							
							
							gesamtBetrag +=  ePreis*eMenge;
							if (kundenModus){
								strInfoleiste = kundenName + " -> " + streArtikel;
							} else {
								strInfoleiste = streArtikel;	
							}
							tv.setText(formatter.format(gesamtBetrag));
							tvi.setText(strInfoleiste);
							MySQLiteHelper db = new MySQLiteHelper(MainTabletActivity.this);
							Log.d("KUNDENMODUS", String.valueOf(kundenModus));
							if (kundenModus){
								anzahl = db.increaseValueKundenItem(streArtikel, ePreis, eMenge , kid);
							} else {
								anzahl = db.increaseValueItem(streArtikel, ePreis, eMenge);
							}
						dialog.dismiss();	
						}
						
					});
					
					
					btn_ab.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							
						}
					});
					
					dialog.show();
					
					break;
				case 88: // Unterste Zeile "Zur Kasse"
					// Anzahl selected Items prüfen bei 0 alert dialog ausgeben
					/*
					 * 
					 */
					MySQLiteHelper db = new MySQLiteHelper(this);
					db.getReadableDatabase();
					Cursor cursor;
					
					
					if (kundenModus==true){
						cursor = db.getAllKundenItems(kid);
					} else {
						cursor = db.getAllSelectedItems();
					} 
					
					if (cursor.getCount() < 1 || gesamtBetrag == 0){
						
					} else {
						
					if (boolZusammenfassung){
					Intent intent = new Intent(MainTabletActivity.this, zusammenfassung.class);
					if (kundenModus) intent.putExtra("kundenId", kid);
					startActivity(intent);
					finish();
					
					} else {
						
						// Kauf automatisch abwickeln und alles auf Null stellen
						Cursor cursor2;
						if (kundenModus) {
							cursor2 = db.getAllSelectedKundenItemsSumme(kid);
						} else {
							cursor2 = db.getAllSelectedItemsSumme();
						}
						while (cursor2.moveToNext()){
						
							String artikel = cursor2.getString(0);
							int anzahl = cursor2.getInt(3);
							double ezpreis = cursor2.getDouble(2);
							
							db.umsaetzeEintragen(artikel, anzahl, ezpreis, kid);
							//Log.d("IN UMSÄTZE-DB",anzahl+"x "+artikel+" zu je "+ezpreis);
							
						}
						cursor2.close();
						if (kundenModus) {
								db.deleteAllKundenItems(kid);
							} else {
								db.cleanDBSelectedItems();
								
							}
						
						
						Intent intent = new Intent(MainTabletActivity.this, MainTabletActivity.class);
						startActivity(intent);
						finish();
						
					}
					
					}
					cursor.close();
					break;
				case 99: 
					
					Intent intent = new Intent(MainTabletActivity.this, MainTabletActivity.class);
					startActivity(intent);
					finish();
					
					break;

				default:
					break;
				}
				
			} else {
			int btn_id = v.getId();
			Button btn = (Button)findViewById(btn_id);
			MySQLiteHelper db = new MySQLiteHelper(this);
			
			if (db.checkButton(btn_id)){
			String strArtikel = db.getButtonArtikel(btn_id);
			float flEZPreis = db.getButtonPreis(btn_id); 
			gesamtBetrag +=  flEZPreis;
			if (kundenModus){
				strInfoleiste = kundenName + " -> " + strArtikel;
			} else {
				strInfoleiste = strArtikel;	
			}
			tv.setText(formatter.format(gesamtBetrag));
			tvi.setText(strInfoleiste);
			if (kundenModus){
				anzahl = db.increaseKundenItem(strArtikel, flEZPreis, kid);
			} else {
				anzahl = db.increaseItem(strArtikel, flEZPreis);
			}
			if (anzahl < 0 ) anzahl = 0;
			int strWeg = 2;
			if (anzahl > 9 && anzahl < 100){
				strWeg = 3;
			}
			if (anzahl > 99){
				strWeg = 4;
			}
			
			btn.setText(strArtikel+"\n"+formatter.format(flEZPreis)+" ("+anzahl+")");
			
			} else {
				Toast.makeText(MainTabletActivity.this, R.string.taste_hat_keinen_wert, Toast.LENGTH_LONG).show();
			}
			}
		}
		
		public void setUpBillingHelper(String appsPublicKey) {
			
	        billingHelperMain = new IabHelper(this, APPLICATIONS_PUBLIC_KEY);
	        
	        billingHelperMain.startSetup(new IabHelper.OnIabSetupFinishedListener() {
	            public void onIabSetupFinished(IabResult result) {
	                Toast.makeText(getApplicationContext(), "billingerHelper Setup finished", Toast.LENGTH_SHORT).show();

	                if (!result.isSuccess()) {
	                    // Fehler-Behandlung des Helper-Objekts
	                    Toast.makeText(getApplicationContext(), "Problem setting up billingHelper: " + result, Toast.LENGTH_LONG);
	                    return;
	                }

	                // Falls Helper-Objekt geschlossen wurde -> Setup-Abbruch
	                if (billingHelperMain == null) return;

	                // billingHelper wurde aufgesetzt 
	                // Abfrage nach vorhandenem Inventar (Bonus, Abos, Premium-Modus) wird gestartet
	                Toast.makeText(getApplicationContext(), "Setup successful. Querying inventory.", Toast.LENGTH_SHORT).show();
	                billingHelperMain.queryInventoryAsync(gotInventoryListener);
	            }
	        });
	        
	     // Listener that's called when we finish querying the items and subscriptions we own
	        gotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
	            public void onQueryInventoryFinished(IabResult result, Inventory bonusInventory) {
	                Toast.makeText(getApplicationContext(), "Query inventory finished.", Toast.LENGTH_SHORT).show();

	                // Falls Helper-Objekt geschlossen wurde -> Abbruch
	                if (billingHelperMain == null) return;

	                // Inventar-Abfrage fehlerhaft? -> Abbruch
	                if (result.isFailure()) {
	                    Toast.makeText(getApplicationContext(), "Failed to query inventory: " + result, Toast.LENGTH_SHORT).show();
	                    return;
	                }

	                Toast.makeText(getApplicationContext(), "Query inventory successful!", Toast.LENGTH_SHORT).show();
	                

	                /*
	                 * Check for items we own. Notice that for each purchase, we check
	                 * the developer payload to see if it's correct! See
	                 * verifyDeveloperPayload().
	                 */

	                // Existiert das Premium-Upgrade?
	                Purchase premiumPurchase = bonusInventory.getPurchase(PREMIUM);
	                // TO DO: Premium-Kauf verifizieren in Methode verifyDeveloperPayload()
	                // true (Default atm) -> besitzt Premium - false -> kein Premium-User
	                isPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
	                Toast.makeText(getApplicationContext(), "User is " + (isPremium ? "PREMIUM" : "NOT PREMIUM"), Toast.LENGTH_LONG).show();
	           
	                bonusInventarEditor.putBoolean("IS_PREMIUM", isPremium);
	                bonusInventarEditor.commit();

	                // Steht Werbefreiheit zur Verfügung?
	                Purchase adFreePurchase = bonusInventory.getPurchase(ADFREE);
	                isAdFree = (adFreePurchase != null &&
	                        verifyDeveloperPayload(adFreePurchase));
	                
	                bonusInventarEditor.putBoolean("IS_ADD_FREE", isAdFree);
	                bonusInventarEditor.commit();
	                
	                Toast.makeText(getApplicationContext(), "User " + (isAdFree ? "HAS" : "DOES NOT HAVE")
	                            + " an ad-free version.", Toast.LENGTH_LONG).show();
	                
	                if (isPremium) appBonus = APP_BONUS_MAX;

	                // Check for bonus delivery -- if we own bonus, we should fill up the bonus-level immediately
	                Purchase bonusPurchase = bonusInventory.getPurchase(BONUS);
	                
	                
	                if (bonusPurchase != null && verifyDeveloperPayload(bonusPurchase)) {
	                    Toast.makeText(getApplicationContext(), "User have bonus. Consuming it.", Toast.LENGTH_SHORT);
	                    billingHelperMain.consumeAsync(bonusInventory.getPurchase(BONUS), consumeFinishedListener);
	                    return;
	                }
	                
	                
	                //updateUi();
	                //setWaitScreen(false);
	                Toast.makeText(getApplicationContext(), "Initial inventory query finished; enabling main UI.", Toast.LENGTH_SHORT).show();
	            }
	        };
	        
	        // Called when consumption is complete
	        consumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
	            public void onConsumeFinished(Purchase purchase, IabResult result) {
	            	Toast.makeText(getApplicationContext(), "Consumption finished. Purchase: " + purchase + ", result: " + result, Toast.LENGTH_SHORT).show();

	                // if we were disposed of in the meantime, quit.
	                if (billingHelperMain == null) return;

	                // We know this is the "gas" sku because it's the only one we consume,
	                // so we don't check which sku was consumed. If you have more than one
	                // sku, you probably should check...
	                if (result.isSuccess()) {
	                    // successfully consumed, so we apply the effects of the item in our
	                    // game world's logic, which in our case means filling the gas tank a bit
	                	Toast.makeText(getApplicationContext(), "Consumption successful. Provisioning.", Toast.LENGTH_SHORT).show();
	                    appBonus = appBonus == APP_BONUS_MAX ? APP_BONUS_MAX : appBonus + 1;
	                    saveBonusData();
	                    Toast.makeText(getApplicationContext(), "Your bonus-level is now " + String.valueOf(appBonus) + "/5!", Toast.LENGTH_SHORT).show();
	                }
	                
	                else {
	                	Toast.makeText(getApplicationContext(), "Error while consuming: " + result, Toast.LENGTH_LONG).show();
	                }
	                
	                //setWaitScreen(true);
	                //updateUi();
	                //setWaitScreen(false);
	                
	                Toast.makeText(getApplicationContext(), "End consumption flow.", Toast.LENGTH_SHORT).show();
	            }
	    	};
		}
		
		/** Verifies the developer payload of a purchase. */
	    public boolean verifyDeveloperPayload(Purchase p) {
	        String payload = p.getDeveloperPayload();

	        /*
	         * TODO: verify that the developer payload of the purchase is correct. It will be
	         * the same one that you sent when initiating the purchase.
	         *
	         * WARNING: Locally generating a random string when starting a purchase and
	         * verifying it here might seem like a good approach, but this will fail in the
	         * case where the user purchases an item on one device and then uses your app on
	         * a different device, because on the other device you will not have access to the
	         * random string you originally generated.
	         *
	         * So a good developer payload has these characteristics:
	         *
	         * 1. If two different users purchase an item, the payload is different between them,
	         *    so that one user's purchase can't be replayed to another user.
	         *
	         * 2. The payload must be such that you can verify it even when the app wasn't the
	         *    one who initiated the purchase flow (so that items purchased by the user on
	         *    one device work on other devices owned by the user).
	         *
	         * Using your own server to store and verify developer payloads across app
	         * installations is recommended.
	         */

	        return true;
	    }
	    
	    public void saveBonusData() {

	    	//keine sichere Speicherung in SharedPreferences-Objekt, nur für den Übergang

	        bonusInventarEditor.putInt("AppBonusLevel", appBonus);
	        bonusInventarEditor.commit();
	        Toast.makeText(getApplicationContext(), "Saved data: App-Bonus = " + String.valueOf(appBonus), Toast.LENGTH_SHORT).show();
	    }

	    public void loadBonusData() {
	    	
	        SharedPreferences bonusInventary = getPreferences(MODE_PRIVATE);
	        
	        //Bei Nicht-Existenz des Keys AppBonusLevel, wird 0 als Default-Value gesetzt
	        
	        appBonus = bonusInventary.getInt("AppBonusLevel", 0);
	        Toast.makeText(getApplicationContext(), "Loaded data: App-Bonus = " + String.valueOf(appBonus), Toast.LENGTH_SHORT).show();
	    }
	    
			
		@Override
		public void onBackPressed(){
			
		}	
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu){
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.einstellungen, menu);
			return super.onCreateOptionsMenu(menu);
			
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item){
			switch (item.getItemId()){
			case R.id.einstellungen:
				Intent intent = new Intent(MainTabletActivity.this, einstellungen.class);
				startActivity(intent);
				finish();
				return true;
				
			case R.id.beenden:
				MainTabletActivity.this.finish();
				return true;
			}
			
			return super.onOptionsItemSelected(item);
		}
		
		

}
