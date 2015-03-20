package jz.apps.kassepro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.jzapps.kassef.InAppBillingActivity;
import de.jzapps.sql.MySQLiteHelper;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class umsatzstatistik extends Activity {

	double gPreis;
	int textSize;
	Context context;
	Uri uri;
	float floatEinlage;
	
	SharedPreferences bonusInventar;
	Editor bonusInventarEditor;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.umsatzstatistik);
	    
	    bonusInventar = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    
	    final MySQLiteHelper db = new MySQLiteHelper(this);
	    final Cursor cursor = db.getAllUmsaetzeSumme();
	    final long startdate = db.getStartUmsaetze();
	    final long enddate = db.getEndUmsaetze();
	    
        final NumberFormat formatter = NumberFormat.getCurrencyInstance();
        final NumberFormat formatter2 = NumberFormat.getNumberInstance();
        // Daten aus der Tabelle selectedItems holen
        final SharedPreferences sp = getSharedPreferences("Einstellungen", Context.MODE_PRIVATE);
        floatEinlage = sp.getFloat("einlage", 0);
	    
	    
	    
        Button umsatz_zurueck = (Button)findViewById(R.id.btn_umsatz_zurueck);
        umsatz_zurueck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(umsatzstatistik.this, MainTabletActivity.class);
				startActivity(intent);
				finish();
				
			}
		});
        
        Button btn_reset = (Button)findViewById(R.id.btn_umsatz_reset);
        btn_reset.setOnClickListener(new OnClickListener() {
        	
		
		
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(umsatzstatistik.this);
				dialog.setTitle(R.string.sicherheitsabfrage);
				dialog.setContentView(R.layout.umsatz_sicherheitsabfrage);
				TextView tv = (TextView)dialog.findViewById(R.id.tv_delumsatz);
				tv.setTextColor(Color.RED);
				tv.setTextSize(getResources().getDimension(R.dimen.TxtSizeInfoLeiste));
				Button btn_ok = (Button)dialog.findViewById(R.id.delumsatz_ok);
				btn_ok.setBackgroundResource(R.drawable.bluebutton);
				btn_ok.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
				Button btn_canc = (Button)dialog.findViewById(R.id.delumsatz_abbr);
				btn_canc.setBackgroundResource(R.drawable.bluebutton);
				btn_canc.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
				btn_canc.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
					dialog.dismiss();	
						
					}
				});
				
				btn_ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						db.deleteAllUmsaetze();
						Intent intent = new Intent(umsatzstatistik.this,umsatzstatistik.class);
						startActivity(intent);
						finish();
						
							
					}
				});
				
				dialog.show();
			
				
			}
		});
        
        
        Button btn_export = (Button)findViewById(R.id.tv_export);
        btn_export.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				
				/*
				 * Auslagerung des Export auf eigene Seite wäre sinnvoll
				 * Max: Definitiv! -> xls-Export wäre zusätzlich nice-to-have
				 * ,
				 * desweiteren kann man über Charts und Statistiken nachdenken
				 * 
				 */
				
				if(bonusInventar.getBoolean("IS_PREMIUM", false) == true) {
				
					final Dialog dialog = new Dialog(umsatzstatistik.this);
					dialog.setContentView(R.layout.dialog_export);
					final EditText et_mail = (EditText)dialog.findViewById(R.id.et_exportmail);
					et_mail.setText(umsatzstatistik.this.getExportMail());
					Button btn_exportnow = (Button)dialog.findViewById(R.id.btn_export_now);
					
					btn_exportnow.setOnClickListener(new OnClickListener() {
					
						// Aktuelle Mail speichern
						
						
						SimpleDateFormat dformatter = new SimpleDateFormat("dd.MM.yyyy"); 
					    String dateString = dformatter.format(new Date(startdate));
					    SimpleDateFormat dformatter2 = new SimpleDateFormat("dd.MM.yyyy"); 
					    String dateString2 = dformatter2.format(new Date(enddate));
						
						@Override
						public void onClick(View v) {
							String strMail = et_mail.getText().toString();
							umsatzstatistik.this.saveExportMail(strMail);
							
						File root = Environment.getExternalStorageDirectory();
						final File file = new File(Environment.getExternalStorageDirectory(),"kasse_export.csv");
						
						try {
							
							if (file.exists()){
								
								Log.d("DATEI", "bereits vorhanden wird gelöscht");
								file.delete();
							}
							
							file.createNewFile();
							
							BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
							writer.write(getResources().getString(R.string.datenexport_von_kassepro)+"\n");
							writer.write("Datum vom "+dateString+" bis "+dateString2+"\n\n");
							
							writer.write(getResources().getString(R.string.bezeichnung)+";"+(getResources().getString(R.string.ep)+";"+getResources().getString(R.string.menge))+";"+getResources().getString(R.string.gp)+"\n");
							
							if (cursor.moveToFirst()){
								String strPreis = formatter2.format(cursor.getDouble(2)*cursor.getInt(3));
								writer.write(cursor.getString(0)+";"+formatter2.format(cursor.getDouble(2))+";"+String.valueOf(cursor.getInt(3))+";"+strPreis+"\n");
							}
							while (cursor.moveToNext()){
								String strPreis = formatter2.format(cursor.getDouble(2)*cursor.getInt(3));
								writer.write(cursor.getString(0)+";"+formatter2.format(cursor.getDouble(2))+";"+String.valueOf(cursor.getInt(3))+";"+strPreis+"\n");
							}
						
							writer.flush();
							writer.close();
							
	
							if (file.exists()){
								
								Log.d("DATEI", "bereits vorhanden wird gelöscht");
								
							} else {
								Log.d("DATEI", "nicht vorhanden");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						      
					
							
							uri = Uri.fromFile(file);
							//Uri.parse("file:///"+Environment.getExternalStorageDirectory()+"/tmp/kasse_exort.csv"));
					
							Log.d("URI", String.valueOf(Uri.fromFile(file)));
							
							/*
							Intent gmail = new Intent(Intent.ACTION_VIEW);
			                gmail.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
			                gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { strMail });
			                //gmail.setData(Uri.parse("jckdsilva@gmail.com"));
			                gmail.putExtra(Intent.EXTRA_SUBJECT, "KassePro Export");
			                gmail.setType("plain/text");
			                //gmail.putExtra(Intent.EXTRA_TEXT, "hi android jack!");
			                startActivity(gmail);
							*/
			                /*
			                Intent email = new Intent(Intent.ACTION_SEND);
			                email.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ strMail });     
			                email.putExtra(Intent.EXTRA_SUBJECT, R.string.export);
			                email.putExtra(Intent.EXTRA_TEXT, R.string.export);
			                email.setType("message/rfc822");
			                startActivity(Intent.createChooser(email, "Choose an Email client :"));
			               */
			                
							
							Intent i = new Intent(Intent.ACTION_SEND);
							i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ strMail });
							i.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.export);
							i.putExtra(android.content.Intent.EXTRA_TEXT, R.string.export);
							i.setType("text/csv");
							 Log.i(getClass().getSimpleName(),
								        "logFile=" + uri);
								    i.putExtra(Intent.EXTRA_STREAM, uri);
	
							
							startActivity(Intent.createChooser(i, "Send email"));
							
							dialog.dismiss();
						
						}
						
					});
					dialog.setTitle(R.string.export);
					dialog.show();
					/*
					 * Falls schon eine Mailadresse eingegeben wurde, dann diese in Textfeld schreiben
					 */
				}
				
				
				else {
					
					Toast.makeText(getApplicationContext(), "Export is a Premium-Feature", Toast.LENGTH_LONG);
					
					Intent billingActivity = new Intent(getApplicationContext(), InAppBillingActivity.class);
					startActivity(billingActivity);
					finish();
				}
				
			}	
			
		});
        
        
        TextView tv_date = (TextView)findViewById(R.id.tv_umsatz_start);
        TextView tv_dEnde = (TextView)findViewById(R.id.tv_umsatz_ende);
	    
	    
	    
	    
	    final Calendar sDate = Calendar.getInstance();
	    sDate.setTimeInMillis(startdate);
	    
	    
	    //Log.d("START", String.valueOf(startdate));
	    //Log.d("ENDE", String.valueOf(enddate));
	    SimpleDateFormat dformatter = new SimpleDateFormat("dd.MM.yyyy"); 
	    String dateString = dformatter.format(new Date(startdate));
	    
	    
	    SimpleDateFormat dformattery = new SimpleDateFormat("yyyy"); 
	    final String year =dformattery.format(new Date(startdate));
	    SimpleDateFormat dformatterm = new SimpleDateFormat("MM"); 
	    final String monthOfYear =dformatterm.format(new Date(startdate));
	    SimpleDateFormat dformatterd = new SimpleDateFormat("dd"); 
	    final String dayOfMonth =dformatterd.format(new Date(startdate));
	    
	    //Log.d("STARTD", dateString);
	    final String dateString2 = dformatter.format(new Date(enddate));
	    //Log.d("ENDED", dateString2);
	    DisplayMetrics metrics = new DisplayMetrics();
	    tv_date.setText(dateString);
	    tv_dEnde.setText(dateString2);
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    //double ergebnis = (Math.sqrt(metrics.heightPixels*metrics.heightPixels+metrics.widthPixels*metrics.widthPixels))/metrics.densityDpi;
	    String bildschirmModus="";
	   	    // Bildschirmhöhe
	    int bsHoehe = metrics.heightPixels;
	    int bsBreite = metrics.widthPixels;
	    final int dialogTextSize = bsHoehe/100;
	    Log.d("TextSize", String.valueOf(dialogTextSize));
	   
	  
	    if (metrics.widthPixels > metrics.heightPixels){
		     
	    	bildschirmModus = "Landscape";
	    	
	    } else {
	    	bildschirmModus = "Portrait";
	    	
	    }
	    
	    TextView tv_menge1 = (TextView)findViewById(R.id.tv_hmenge);
	    TextView tv_bez1 = (TextView)findViewById(R.id.tv_hbez);
	    TextView tv_ep1 = (TextView)findViewById(R.id.tv_hep);
	    TextView tv_gp1 = (TextView)findViewById(R.id.tv_hgp);
	    tv_menge1.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
	    tv_menge1.setText(R.string.Anz);
	    tv_bez1.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
	    tv_bez1.setText(R.string.bezeichnung);
	    tv_ep1.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
	    tv_ep1.setText(R.string.ep);
	    tv_gp1.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
	    tv_gp1.setText(R.string.gp);
	    
	    TableLayout table = (TableLayout)findViewById(R.id.tablelay);
		  
		   double zahlBetrag = 0;
		   
		   if(!cursor.moveToFirst()){
		    	
		    } else {
		   
		   cursor.moveToFirst();
		   
		   TableRow tr1 = new TableRow(this);
		
		   TextView tv1_menge = new TextView(this);
		   tv1_menge.setWidth(metrics.widthPixels/5);
		   tv1_menge.setTextColor(getResources().getColor(R.color.weiss));
		   tv1_menge.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		   tv1_menge.setGravity(Gravity.CENTER);
		   tv1_menge.setBackgroundResource(R.drawable.blackbutton);
		   
		   TextView tv1_bez = new TextView(this);
		   tv1_bez.setWidth((int) (metrics.widthPixels/2.85));
		   tv1_bez.setTextColor(getResources().getColor(R.color.weiss));
		   tv1_bez.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		   tv1_bez.setGravity(Gravity.LEFT);
		   tv1_bez.setBackgroundResource(R.drawable.blackbutton);
		   
		   TextView tv1_ep = new TextView(this);
		   tv1_ep.setWidth(metrics.widthPixels/5);
		   tv1_ep.setTextColor(getResources().getColor(R.color.weiss));
		   tv1_ep.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		   tv1_ep.setGravity(Gravity.RIGHT);
		   tv1_ep.setBackgroundResource(R.drawable.blackbutton);
		   
		   TextView tv1_gp = new TextView(this);
		   tv1_gp.setWidth(metrics.widthPixels/4);
		   tv1_gp.setTextColor(getResources().getColor(R.color.weiss));
		   tv1_gp.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		   tv1_gp.setGravity(Gravity.RIGHT);
		   tv1_gp.setBackgroundResource(R.drawable.blackbutton);
		   
		  
		   tv1_bez.setText(cursor.getString(0));
		  
		   tv1_menge.setText(cursor.getString(3));
		   tv1_ep.setText(formatter.format(cursor.getFloat(2)));
		   gPreis = cursor.getInt(3)*cursor.getFloat(2);
		   tv1_gp.setText(formatter.format(gPreis));
		   tr1.addView(tv1_menge);
		   tr1.addView(tv1_bez);
		   tr1.addView(tv1_ep);
		   tr1.addView(tv1_gp);
		   table.addView(tr1);
		   Log.d("DB-Umsatze","Bezeichung:"+cursor.getString(0));
		   Log.d("DB-Umsatze","Menge:"+cursor.getString(3));
		   
		   
		 
		   while (cursor.moveToNext()) {
			   
			   TableRow tr = new TableRow(this);
			   TextView tv_menge = new TextView(this);
			   TextView tv_bez = new TextView(this);
			   TextView tv_ep = new TextView(this);
			   TextView tv_gp = new TextView(this);
			   
			   tv_menge.setText(cursor.getString(3));
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
			   
			   
			   tv_gp.setWidth(metrics.widthPixels/4);
			   float gPreis2 = cursor.getInt(3)*cursor.getFloat(2);
			   tv_gp.setText(formatter.format(gPreis2));
			   tv_gp.setTextColor(getResources().getColor(R.color.weiss));
			   tv_gp.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
			   tv_gp.setGravity(Gravity.RIGHT);
			   tv_gp.setBackgroundResource(R.drawable.blackbutton);
			   gPreis += gPreis2;
			   tr.addView(tv_menge);
			   tr.addView(tv_bez);
			   tr.addView(tv_ep);
			   tr.addView(tv_gp);
			   table.addView(tr);
		}
	    }  
		   TextView tvgpr = (TextView)findViewById(R.id.tv_gUmsatz);
		   gPreis += floatEinlage;
		   tvgpr.setText(formatter.format(gPreis));
		   
		   
		   
		
	}
	
	
	public void schreibeDatei(FileOutputStream out) throws IOException{
		OutputStreamWriter osw = new OutputStreamWriter(out);
		try {
			osw.write("Das ist ein Text");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (osw != null){
				osw.close();
			}
		}
	}
	
	public void saveExportMail(String strMail){
		
		SharedPreferences prefs = getSharedPreferences("MailExport", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("mail", strMail);
		editor.commit();
	}
	
	public String getExportMail(){
		
		SharedPreferences prefs = getSharedPreferences("MailExport", Context.MODE_PRIVATE);
		String strMail = prefs.getString("mail", null);
		
		return strMail;
		
	}
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(umsatzstatistik.this, MainTabletActivity.class);
		startActivity(intent);
		finish();
	}
	
	

}
