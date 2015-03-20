package jz.apps.kassepro;

import java.util.ArrayList;
import de.jzapps.sql.MySQLiteHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class kundenverwaltung extends Activity {

	 private ArrayList<Kunde> alist ;
	 private Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.kundenverwaltung);
	    ListView liste;
	    alist = new ArrayList<Kunde>();
	    
	    /*
	     * Zur Anzeige der eingetragenen Kunden die Tabelle kunden auslesen und alphabetisch sortieren
	     * 
	     */
	    MySQLiteHelper sql = new MySQLiteHelper(this);
	    Cursor cursor = sql.alleKunden();
	    
	    Log.d("Kundenanzahl", String.valueOf(cursor.getCount()));
	    
	   double actKundenBetrag;
	    if(cursor.moveToFirst()){
	    	Kunde kunde = new Kunde();
		    kunde.setKundenName(cursor.getString(0),cursor.getString(1));
		    kunde.getKundenName();
		    kunde.setId(cursor.getInt(3));
		    
		    actKundenBetrag = sql.getActKundenGesmatBetrag(kunde.getId());
		    kunde.setBetrag(actKundenBetrag);
		    kunde.setFormattedBetrag(actKundenBetrag);
		    
		    alist.add(kunde);
		    
	    }
	    
	    while(cursor.moveToNext()){
	    	Kunde kunde2 = new Kunde();
		    kunde2.setKundenName(cursor.getString(0),cursor.getString(1));
		    kunde2.getKundenName();
		    kunde2.setId(cursor.getInt(3));
		    actKundenBetrag = sql.getActKundenGesmatBetrag(kunde2.getId());
		    
		    kunde2.setBetrag(actKundenBetrag);
		    kunde2.setFormattedBetrag(actKundenBetrag);
		    
		    alist.add(kunde2);
	    }
	    
	   
        liste = (ListView)findViewById(R.id.kundenliste);
	    
	    liste.setAdapter(new KundenAdapter(alist , kundenverwaltung.this));
	    
	    liste.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				TextView v=(TextView) view.findViewById(R.id.tv_kundenid);
				int kid = Integer.parseInt(v.getText().toString());
				
				Intent intent = new Intent(kundenverwaltung.this, MainTabletActivity.class);
				intent.putExtra("kundenId", kid);
				startActivity(intent);
				finish();
			}

		});
	    
	    liste.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView v=(TextView) view.findViewById(R.id.tv_kundenid);
				int kid = Integer.parseInt(v.getText().toString());
				kundenverwaltung.this.showDeleteKundenDialog(kid);
				return true;
			}
		});
	    
	    //Log.d("LISTE ANzahL", String.valueOf(alist.size()));
	
	    DisplayMetrics metrics = new DisplayMetrics();
	    
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    
	    String bildschirmModus="";
	    
	    // Bildschirmhöhe
	    int bsHoehe = metrics.heightPixels;
	    int bsBreite = metrics.widthPixels;
	    ImageView iv_addKunde = (ImageView)findViewById(R.id.iv_addKunde);
	    iv_addKunde.getLayoutParams().height = bsBreite/10;
	    iv_addKunde.getLayoutParams().height = bsHoehe/10;
	    iv_addKunde.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
				showKundenDialog(0);
				
			}
		});
	    
	   
	    
	}
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(kundenverwaltung.this, MainTabletActivity.class);
		startActivity(intent);
		finish();
	}

	
	public void showDeleteKundenDialog(final int kid){
		final Dialog dialog = new Dialog(kundenverwaltung.this);
		dialog.setTitle(R.string.kundeloeschen);
		dialog.setContentView(R.layout.deletekundedialog);
		
		Button btn_del = (Button)dialog.findViewById(R.id.btn_kundendialog_loeschen);
		Button btn_cancel = (Button)dialog.findViewById(R.id.btn_kundendialog_zurueck);
		btn_del.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			 MySQLiteHelper sql = new MySQLiteHelper(kundenverwaltung.this);
			 sql.deleteKunde(kid);
			 sql.deleteAllKundenItems(kid);
			 Intent intent = new Intent(kundenverwaltung.this, kundenverwaltung.class);
			 startActivity(intent);
			 finish();
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		
		dialog.show();
	}
	
	public void showKundenDialog(final int kid){
		
		final Dialog dialog = new Dialog(kundenverwaltung.this);
		dialog.setTitle(R.string.neuen_kunden_eintragen);
		dialog.setContentView(R.layout.neuerkundedialog);
		Button btn_add = (Button)dialog.findViewById(R.id.btn_kundendialog_speichern);
		
		final EditText et_vorname = (EditText)dialog.findViewById(R.id.et_kvorname);
		final EditText et_nachname = (EditText)dialog.findViewById(R.id.et_knachname);
		//EditText et_adresse = (EditText)dialog.findViewById(R.id.et_kadresse);
		//EditText et_telefon = (EditText)dialog.findViewById(R.id.et_ktelefon);
		//final EditText et_email  = (EditText)dialog.findViewById(R.id.et_mail);
		//EditText et_guthaben = (EditText)dialog.findViewById(R.id.et_guthaben);
		if (kid>0){
			// Wenn KundenId übergeben wird die Kundendaten aus Datenbank holen und anzeigen  
			MySQLiteHelper sql = new MySQLiteHelper(context);
			Cursor cursor = sql.getKundenNameAsCursor(kid);
			et_vorname.setText(cursor.getString(1));
			et_nachname.setText(cursor.getString(2));
			
		} 
		
		
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MySQLiteHelper sql = new MySQLiteHelper(kundenverwaltung.this);
				/*
				 * Prüfen, ob alle notwendigen Felder befüllt sind
				 */
				
				String vorname = et_vorname.getText().toString();
				String nachname = et_nachname.getText().toString();
				//String adresse = et_adresse.getText().toString();
				/*double guthaben = 0;
				try {
					guthaben = Double.parseDouble(et_guthaben.getText().toString());
				} catch (Exception e) {
					// Wenn kein Guthaben eingegeben wird dann automatisch auf 0 setzen
					guthaben = 0;
				}
				*/
				sql.addKunde(vorname, nachname, null, 0);
				Intent intent = new Intent(kundenverwaltung.this, kundenverwaltung.class);
				startActivity(intent);
				finish();
				dialog.dismiss();
				
			}
		});
		
		Button btn_canc = (Button)dialog.findViewById(R.id.btn_kundendialog_verwerfen);
		btn_canc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});

		dialog.show();
	}
}
