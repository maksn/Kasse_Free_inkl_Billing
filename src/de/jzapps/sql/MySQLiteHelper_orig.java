package de.jzapps.sql;

import java.util.Calendar;
import java.util.Locale;

import jz.apps.kassepro.Taste;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class MySQLiteHelper_orig extends SQLiteOpenHelper{

	final static String DATABASE_NAME = "kassepro_db";
	final static int DATABASE_VERSION = 23;
	final static String TABLE_KUNDENSELECTEDITEMS = "kundenselecteditems";
	final static String CLM_ANZAHL = "anzahl";
	final static String CLM_ARTIKEL = "artikel";
	
	
	public MySQLiteHelper_orig(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	/**
	 * Erstellt die benötigten Tabellen<br><br>
	 * tasten: beinhaltet alle erstellten Tasten<br>
	 * selecteditems: beinhaltet alle ausgewählten und noch nicht abgerechneten Artikel<br>
	 * umsaetze: beinhaltet alle abgerechneten Artikel<br>
	 * kunden: beinhaltet die eingegebenen Kundendaten<br>
	 * kundenselecteditems: beinhalten alle ausgewählten und noch nicht abgerechneten Artikel aller Kunden<br>
	 */
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Tabelle Tasten erstellen
		String createDB = "CREATE TABLE tasten ( "+
				"id INTEGER PRIMARY KEY AUTOINCREMENT, "+
				"btn_id INTEGER NOT NULL, " +
				"artikelname TEXT NOT NULL, "+
				"farbe STRING NOT NULL, "+
				"preis REAL NOT NULL"+
				" ) ";
		
		db.execSQL(createDB);
		String createDB2 = "CREATE TABLE selecteditems ( "+
				" id INTEGER PRIMARY KEY AUTOINCREMENT, "+
				" artikel TEXT NOT NULL, "+
				" ezpreis REAL NOT NULL, "+
				" anzahl INTEGER NOT NULL"+
				" )";
		db.execSQL(createDB2);
		
		String createDB3 = "CREATE TABLE umsaetze ( "+
				" id INTEGER PRIMARY KEY AUTOINCREMENT, "+
				" artikel TEXT NOT NULL, "+
				" ezpreis REAL NOT NULL, "+
				" anzahl INTEGER NOT NULL,"+
				" kunden_id INTEGER NOT NULL, "+
				" vdate REAL NOT NULL"+
				" )";
		db.execSQL(createDB3);
		
		String createDB4 = "CREATE TABLE kunden ( "+
				" id INTEGER PRIMARY KEY AUTOINCREMENT, "+
				" vorname TEXT NOT NULL, "+
				" nachname TEXT NOT NULL, "+
				" adresse TEXT,"+
				" betrag REAL NOT NULL"+
				" )";
		db.execSQL(createDB4);
		
		String createDB5 = "CREATE TABLE kundenselecteditems ( "+
				" id INTEGER PRIMARY KEY AUTOINCREMENT, "+
				" kunden_id INTEGER NOT NULL,"+
				" artikel TEXT NOT NULL, "+
				" ezpreis REAL NOT NULL, "+
				" anzahl INTEGER NOT NULL"+
				" )";
		db.execSQL(createDB5);
		
		
	}
	/**
	 * Bei neuer Datenbankversion in der AndroidManifest werden die angegebenen Tabellen gelöscht und neu erstellt
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS tasten");
        db.execSQL("DROP TABLE IF EXISTS selecteditems");
        db.execSQL("DROP TABLE IF EXISTS umsaetze");
        db.execSQL("DROP TABLE IF EXISTS kunden");
        db.execSQL("DROP TABLE IF EXISTS kundenselecteditems");
        
        
        // create fresh books table
        this.onCreate(db);
		
	}
	
	/**
	 * Schreibt abgerechnete Umsätze in die Tabelle umsaetze
	 * @param artikel
	 * Artikelbezeichnung
	 * @param anzahl
	 * Menge
	 * @param ezpreis
	 * Einzelpreis
	 * @param kid
	 * Kunden-Id
	 */
	public void umsaetzeEintragen(String artikel, int anzahl, double ezpreis, int kid){
		//int anzahl = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		Calendar cal = Calendar.getInstance();
		double vdatum = cal.getTimeInMillis();
						
		
		ContentValues values = new ContentValues();
						//Log.d("addButton", String.valueOf(btnid));
						values.put("artikel", artikel);
						values.put("anzahl", anzahl);
						values.put("ezpreis", ezpreis);
						values.put("vdate", vdatum);
						values.put("kunden_id", kid);
						
						long dbresult = db.insert("umsaetze", null, values);
						
						Log.d("DB", String.valueOf(dbresult));
						
					    anzahl = 1;
			
	
		
		
	}
	
	/**
	 * Löscht die Tabelle selecteditems. Diese Funtion wird nur während der Programmierphase benötigt.
	 */
	public void cleanDBSelectedItems(){
		SQLiteDatabase db = this.getWritableDatabase();
		 db.setLocale(Locale.getDefault());
		 db.setLockingEnabled(true);
		 String query = "DROP TABLE IF EXISTS selecteditems";
		 db.execSQL(query);
		 query = "CREATE TABLE selecteditems ( "+
					" id INTEGER PRIMARY KEY AUTOINCREMENT, "+
					" artikel TEXT NOT NULL, "+
					" ezpreis REAL NOT NULL, "+
					" anzahl INTEGER NOT NULL"+
					" )";;
		 db.execSQL(query);
		
	}
	
	/**
	 * Löscht die Taste mit der angegebenen Id aus der Tabelle <b>tasten</b>
	 * @param btnid
	 * Id der zu löschenden Taste
	 * 
	 */
	public void deleteButton(int btnid){
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		db.delete("tasten", //TABLE_NAME
				"btn_id = ?", 
				new String[] {String.valueOf(btnid)});
		db.close();
		String query = "";
		
	}
	
	/**
	 * Ermitelt die Anzahl wie oft ein Artikel aktuell ausgewählt ist (Mengenanzeige auf der Taste)
	 * @param btn_id
	 * Id der Taste, deren Anzahl geholt werden soll
	 * @return
	 * Integer-Wert der Anzahl der angegebenen Taste oder 0, wenn kein Eintrag in Tabelle <b>selecteditems</b> vorhanden
	 */
	public int getItemCount(int btn_id){
		int itemCount = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		String query = "SELECT selecteditems.anzahl FROM selecteditems,tasten WHERE tasten.artikelname = selecteditems.artikel AND btn_id = '"+btn_id+"'";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.getCount() > 0){
			cursor.moveToFirst();
			return cursor.getInt(0);
		} else {
			return 0;
		}
		
		
	}
	/**
	 * Erhöht die Anzahl eines bestimmten Artikels um 1
	 * @param artikel
	 * Artikelbezeichnung des zu erhöhenden Artikels
	 * @param ezpreis
	 * Einzelpreis des zu erhöhenden Artikels
	 * @return
	 * Anzahl nach der Erhöhung
	 */
	public int increaseItem(String artikel, float ezpreis){
		int anzahl = 0;
		
		String[] COLUMNS = {"artikel","anzahl"};
		String TABLE_NAME = "selecteditems";
		 // 1. get reference to readable DB
	    SQLiteDatabase db = this.getReadableDatabase();
	    
	    // 2. build query
	    Cursor cursor =
	            db.query(TABLE_NAME, // a. table
	            COLUMNS, // b. column names
	            " artikel = ?", // c. selections
	            new String[] { artikel }, // d. selections args
	            null, // e. group by
	            null, // f. having
	            null, // g. order by
	            null); // h. limit
		
		
		/*
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		String query = "SELECT artikel, anzahl FROM selecteditems WHERE artikel = '"+artikel+"'";
		Cursor cursor = db.rawQuery(query, null);
		*/
		if (!cursor.moveToFirst()){
			// Wenn Artikel noch nicht vorhanden dann erstellen und auf Anzahl auf 1 
						ContentValues values = new ContentValues();
						//Log.d("addButton", String.valueOf(btnid));
						values.put("artikel", artikel);
						values.put("anzahl", 1);
						values.put("ezpreis", ezpreis);
						long dbresult = db.insert("selecteditems", null, values);
						
						Log.d("DB", String.valueOf(dbresult));
						
					    anzahl = 1;
			
		} else {
		
			// Wenn Artikel bereits vorhanden dann nur Anzahl erhöhen
			anzahl = Integer.parseInt(cursor.getString(1));
			anzahl++;
			ContentValues values = new ContentValues();

			values.put("anzahl", anzahl);
			
			db.update("selecteditems", values, "artikel = ?",
			new String[] { artikel });
			Log.d("DB", "Item vorhanden, erhöht");
			
		}
		cursor.close();
		return anzahl;
		
		
	}
	
	/**
	 * Erhöht Anzahl einer Taste im Kundenmodus um 1
	 * @param artikel
	 * Artikelbezeichung der Taste bei der die Menge um 1 erhöht werden soll
	 * @param ezpreis
	 * Einzelpreis der gewünschten Taste
	 * @param kid
	 * Kunden-Id der Taste, die um 1 erhöht werden soll
	 * @return
	 * Gibt die Anzahl nach der Erhöhung zurück
	 */
	public int increaseKundenItem(String artikel, float ezpreis, int kid){
		int anzahl = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		String query = "SELECT artikel, anzahl FROM kundenselecteditems WHERE artikel = '"+artikel+"' AND kunden_id = '"+kid+"'";
		Cursor cursor = db.rawQuery(query, null);
		if (!cursor.moveToFirst()){
			// Wenn Artikel noch nicht vorhanden dann erstellen und auf Anzahl auf 1 
						ContentValues values = new ContentValues();
						values.put("artikel", artikel);
						values.put("anzahl", 1);
						values.put("ezpreis", ezpreis);
						values.put("kunden_id", kid);
						long dbresult = db.insert("kundenselecteditems", null, values);
						
						//Log.d("DB", String.valueOf(dbresult));
						
					    anzahl = 1;
			
		} else {
		
			// Wenn Artikel bereits vorhanden dann nur Anzahl erhöhen
			anzahl = Integer.parseInt(cursor.getString(1));
			anzahl++;
			
			Log.d("ANZAHL", String.valueOf(anzahl));
			
			String query2 = "UPDATE kundenselecteditems SET anzahl = '"+anzahl+"' WHERE artikel = '"+artikel+"' AND kunden_id = '"+kid+"'";
			//db.update("kundenselecteditems", values, "artikel = ? AND kunden_id = ?",
			//new String[] { artikel });
			db.execSQL(query2);
			
		}
		
		return anzahl;
		
		
	}
	
	/**
	 * Läschte alle Daten in der Tabelle <b>umsaetze</b>
	 */
	public void deleteAllUmsaetze(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		
		
		db.execSQL("DELETE FROM umsaetze");
		
		
		
	}
	
	/**
	 * Löscht alle Datensätze eines Kunden aus der Tabelle kundenselecteditems
	 * @param kid
	 * Kunden-Id, des Kunden, dessen Artikel gelöscht werden sollen
	 */
	public void deleteAllKundenItems(int kid){
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		
		
		db.execSQL("DELETE FROM kundenselecteditems WHERE kunden_id = '"+kid+"'");
		
		
		
	}
	
	/**
	 * Verringert die Anzahl eines ausgewählten Artikels um 1
	 * @param artikel
	 * Artikel, dess Anzahl um 1 verringert werden soll
	 * @return
	 * Gibt die Anzahl nach der Verringerung zurück
	 */
	public int decreaseItem(String artikel){
		int anzahl = 0;
		
		
		String[] COLUMNS = {"anzahl"};
		String TABLE_NAME = "selecteditems";
		 // 1. get reference to readable DB
	    SQLiteDatabase db = this.getReadableDatabase();
	    
	    // 2. build query
	    Cursor cursor =
	            db.query(TABLE_NAME, // a. table
	            COLUMNS, // b. column names
	            " artikel = ?", // c. selections
	            new String[] { artikel }, // d. selections args
	            null, // e. group by
	            null, // f. having
	            null, // g. order by
	            null); // h. limit
		
		
		
		/*
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		String query = "SELECT anzahl FROM selecteditems WHERE artikel = '"+artikel+"'";
		Cursor cursor = db.rawQuery(query, null);
		*/
		cursor.moveToFirst();
		
		/*if (!cursor.moveToFirst()){
			// Wenn Artikel noch nicht vorhanden dann erstellen und auf Anzahl auf 1 
						ContentValues values = new ContentValues();
						//Log.d("addButton", String.valueOf(btnid));
						values.put("artikel", artikel);
						values.put("anzahl", 1);
						values.put("ezpreis", ezpreis);
						long dbresult = db.insert("selecteditems", null, values);
						
						Log.d("DB", String.valueOf(dbresult));
						
					    anzahl = 1;
			
		} else {
		*/
			// Wenn Artikel bereits vorhanden dann nur Anzahl erhöhen
			anzahl = cursor.getInt(0);
			anzahl--;
			ContentValues values = new ContentValues();

			values.put("anzahl", anzahl);
			
			db.update("selecteditems", values, "artikel = ?",
			new String[] { artikel });
			Log.d("DB", "Item vorhanden, verringert");
			
		//}
		
		return anzahl;
		
		
	}
	/**
	 * Verringert die Anzahl eines Artikels eines Kunden um 1
	 * @param artikel
	 * Artikelbezeichung der Taste
	 * @param kid
	 * Kunden-Id, des Kunden
	 * @return
	 * Gibt die Anzahl nach der Verringerung zurück
	 */
	public int decreaseKundenItem(String artikel, int kid){
		int anzahl = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		String query = "SELECT SUM(anzahl) AS anzahl FROM kundenselecteditems WHERE artikel = '"+artikel+"' AND kunden_id = '"+kid+"' GROUP BY artikel";
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		/*if (!cursor.moveToFirst()){
			// Wenn Artikel noch nicht vorhanden dann erstellen und auf Anzahl auf 1 
						ContentValues values = new ContentValues();
						//Log.d("addButton", String.valueOf(btnid));
						values.put("artikel", artikel);
						values.put("anzahl", 1);
						values.put("ezpreis", ezpreis);
						long dbresult = db.insert("selecteditems", null, values);
						
						Log.d("DB", String.valueOf(dbresult));
						
					    anzahl = 1;
			
		} else {
		*/
			// Wenn Artikel bereits vorhanden dann nur Anzahl senken
			anzahl = cursor.getInt(0);
			anzahl--;
			//ContentValues values = new ContentValues();

			//values.put("anzahl", anzahl);
			String query2= "UPDATE kundenselecteditems SET anzahl = '"+anzahl+"' WHERE artikel = '"+artikel+"' AND kunden_id = '"+kid+"'";
			db.execSQL(query2);
			//db.update("kundenselecteditems", values, "artikel = ?",
			//new String[] { artikel });
			//Log.d("DB", "Item vorhanden, verringert");
			
		//}
		
		return anzahl;
		
		
	}
		
	/**
	 * Ermittelt die Anzahl eines gewählten Artikels	
	 * @param artikel
	 * Artikelname
	 * @return
	 * Gibt Anzahl zurück
	 */
	public int anzahlErmitteln(String artikel){
			
		String[] COLUMNS = {"anzahl"};
		String TABLE_NAME = "selecteditems";
		 // 1. get reference to readable DB
	    SQLiteDatabase db = this.getReadableDatabase();
	    int anzahl = 0;
	    // 2. build query
	    Cursor cursor =
	            db.query(TABLE_NAME, // a. table
	            COLUMNS, // b. column names
	            " artikel = ?", // c. selections
	            new String[] { artikel }, // d. selections args
	            null, // e. group by
	            null, // f. having
	            null, // g. order by
	            null); // h. limit
	   
	    if (cursor.moveToFirst()){
			 anzahl = cursor.getInt(0);
			 } else {
				anzahl = 0;
			 }
			 
			 cursor.close();
			return anzahl;
	}
		
		/*		
		
		SQLiteDatabase db = this.getReadableDatabase();
		 db.setLocale(Locale.getDefault());
		 db.setLockingEnabled(true);
		 int anzahl = 0;
		 String query = "SELECT anzahl FROM selecteditems WHERE artikel = "+artikel+"";
		 Cursor cursor = db.rawQuery(query, null);
		 
		 if (cursor.moveToFirst()){
		 anzahl = cursor.getInt(0);
		 } else {
			anzahl = 0;
		 }
		 
		 cursor.close();
		return anzahl;
	}
	*/
	    
	    
	/**
	 * Gibt Anzahl des Artikels in der <b>kundenselecteditems</b> Tabelle zurück
	 * @param artikel
	 * Artikelbezeichung
	 * @return
	 * Gibt Anzahl als Integer zurück
	 */
	public int anzahlInKundenItems(String artikel){
		String[] columns = {"SUM("+CLM_ANZAHL+")"};
		String selection = CLM_ARTIKEL;
		String[] selectionArgs = {artikel};
		String orderBy = CLM_ARTIKEL;
		
		SQLiteDatabase db = this.getReadableDatabase();
		 db.setLocale(Locale.getDefault());
		 db.setLockingEnabled(true);
		 int anzahl = 0;
		 
		 
		 Cursor cursor = db.query(TABLE_KUNDENSELECTEDITEMS, columns, selection+"=?", selectionArgs, null, null, orderBy);
		
		 
		 //String query = "SELECT SUM(anzahl) AS anzahl FROM kundenselecteditems WHERE artikel = '"+artikel+"' GROUP BY artikel";
		// Cursor cursor = db.rawQuery(query, null);
		 if (cursor.moveToFirst()){
		 anzahl = cursor.getInt(0);
		 } else {
			anzahl = 0;
		 }
		return anzahl;
	}
	
	/**
	 * Ermittelt die Anzahl des gewählten Artikels eines Kunden
	 * @param artikel
	 * Artikelbezeichnung
	 * @param kid
	 * Kunden-Id
	 * @return
	 * Gibt die Anzahl als Integer zurück
	 */
	public int anzahlKundeErmitteln(String artikel, int kid){
		/*
		 * Ermittelt die Anzahl der eingetragenen Artikel eines Kunden
		 */
		SQLiteDatabase db = this.getReadableDatabase();
		 db.setLocale(Locale.getDefault());
		 db.setLockingEnabled(true);
		 int anzahl = 0;
		 String query = "SELECT SUM(anzahl) AS anzahl FROM kundenselecteditems WHERE artikel = '"+artikel+"' AND kunden_id = '"+kid+"' GROUP BY artikel";
		 Cursor cursor = db.rawQuery(query, null);
		 if (cursor.moveToFirst()){
		 anzahl = cursor.getInt(0);
		 } else {
			anzahl = 0;
		 }
		return anzahl;
	}
	/**
	 * Die Methode <b>addButton</b> fügt der Tabelle <b>tasten</b> einen Eintrag hinzu mit den Werten der hier enthaltenen Parameter.
	 * @author Jens Zech
	 * @param btnid
	 * Eindeutige Id zur späteren leichteren Auffindbarkeit bei Aktionen
	 * @param artikelName
	 * Artikelname auf der entsprechenden Taste
	 * @param preis
	 * Preis, der auf der Taste angezeigt wird
	 * @param btnFarbe
	 * Farbe der Taste
	 * 
	 * 
	 * 
	 */
	public void addButton(int btnid, String artikelName, float preis, String btnFarbe){
		SQLiteDatabase db = this.getWritableDatabase();
		 db.setLocale(Locale.getDefault());
		 db.setLockingEnabled(true);
		    
		ContentValues values = new ContentValues();
		values.put("btn_id", btnid);
		values.put("artikelname", artikelName);
		values.put("preis", preis);
		values.put("farbe", btnFarbe);
		
	   
		long dbresult = db.insert("tasten", null, values);
		
		
		Log.d("db.insert result", String.valueOf(dbresult));
		
		
		
	}
	
	
	/**
	 * Fügt der Tabelle kunden einen neuen Kunden mit den angegebenen Parametern zu.
	 * In der aktuellen Version werden nur Vorname und Nachname benötigt. Die anderen Parameter sind noch nicht in Gebrauch.
	 * @param vorname
	 * Vorname des Kunden
	 * @param nachname
	 * Nachname des Kunden
	 * @param adresse
	 * Anschrift des Kunden
	 * @param betrag
	 * Betrag, der bereits auf dem Kundenkonto verfügbar sein soll
	 * @author Zensi
	 */
	public void addKunde(String vorname, String nachname, String adresse, double betrag){
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		ContentValues values = new ContentValues();
		values.put("vorname", vorname);
		values.put("nachname", nachname);
		values.put("adresse", adresse);
		values.put("betrag", betrag);
		long dbresult = db.insert("kunden", null, values);
		
	}
	
	
	/**
	 * Gibt alle Einträge der Tabelle <b>kunden</b> als Cursor zurück
	 * @return cursor
	 * @author Zensi
	 */
	public Cursor alleKunden(){
		SQLiteDatabase db = this.getReadableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		
		Cursor cursor = db.rawQuery("SELECT vorname, nachname, betrag, id FROM kunden ORDER BY nachname, vorname ASC", null);
   
		return cursor;
		
	}
	/**
	 * Noch nicht implementiert
	 */
	public void changeKunde(){
		
	}
	/**
	 * Löscht den Kunden mit der übergebenene Id aus der Tabelle <b>kunden</b>
	 * @param kid
	 * Kunden-Id zur Bestimmung des zu löschenden Kunden
	 */
	public void deleteKunde(int kid){
		
		SQLiteDatabase db = this.getWritableDatabase();
		//Erst prüfen, ob Kunde noch Items hat
		
			String query = "DELETE FROM kunden WHERE id = '"+kid+"'";
			db.execSQL(query);
			
	
		}
	
	/**
	 * Ermittelt die Anzahl der in die Tablle <b>tasten</b> eingetragenen Tasten und gibt diese als Integer-Wert zurück
	 * @return anzahl
	 * @author Zensi
	 */
	public int anzahlTasten(){
		int anzahl = 0; 
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT btn_id, artikelname, preis FROM tasten";
		Cursor cursor = db.rawQuery(query, null);
		anzahl = cursor.getCount();
		cursor.close();
		return anzahl;
		
	}
	
	/**
	 * Verändert einen Tasteneintrag in der Tabelle <b>tasten</b> auf die übergebenen Werte der Parameter
	 * @param btnid
	 * Id der zu ändernden Taste
	 * @param artikel
	 * Neuer Artikelname auf der Taste
	 * @param preis
	 * Neuer Preis auf der Taste
	 * @param btnFarbe
	 * Neue Farbe auf der Taste
	 * @author Zensi
	 */
	public void updateButton(int btnid, String artikel, float preis, String btnFarbe){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("btn_id", btnid);
		values.put("artikelname", artikel);
		values.put("preis", preis);
		values.put("farbe", btnFarbe);

		// updating row

		db.update("tasten", values, "btn_id = ?",
		new String[] { String.valueOf(btnid) });
	}
	
	/**
	 * Gibt die Artikelbezeichnung der Taste mit der durch den Parameter angegebenen Id zurück
	 * @param id
	 * Id der Taste, von der die Bezeichnung ermittelt werden soll
	 * @return artikel
	 */
	public String getButtonArtikel(int id){
		
		String query = "SELECT artikelname FROM tasten WHERE btn_id = "+id;
		String artikel = "";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		
		if (cursor.moveToFirst()){
			artikel = cursor.getString(0);
			
			
		}
		cursor.close();
		return artikel;
		
	}
	
	public Taste getButtonValues(Taste taste, int btnid){
		
		
		String query = "SELECT artikelname, preis, farbe FROM tasten WHERE btn_id = "+taste.getId();
		Log.d("TASTEN.ID", String.valueOf(taste.getId()));
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()){
			taste.setArtikel(cursor.getString(0));
			Log.d("BTNVALUES", cursor.getString(0));
			taste.setPreis(cursor.getFloat(1));
			taste.setFarbe(cursor.getString(2));
		}
		
		
		cursor.close();
		return taste;
	}
	
	/**
	 * Gibt die Farbe der Taste mit der übergebenen Id als String zurück
	 * @param btnid
	 * Id der Taste, von der die Farbe ermittelt werden soll
	 * @return farbe
	 */
    public String getButtonColor(int btnid){
		
		
		String query = "SELECT farbe FROM tasten WHERE btn_id = '"+btnid+"'";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		String farbe;
		if (cursor.moveToFirst()){
			farbe = cursor.getString(0);

		} else {
			farbe = "blue";
		}
	    cursor.close();
		return farbe;
	}
	
	
	
/**
 * Ermittelt den Preis auf der Taste mit der angegebenen Id und gibt diesen als float Wert zurück	
 * @param id
 * Tasten Id von der der Preis ermittelt werden soll
 * @return preis als float
 */
public float getButtonPreis(int id){
		
		String query = "SELECT preis FROM tasten WHERE btn_id = "+id;
		float preis = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		
		if (cursor.moveToFirst()){
			preis = cursor.getFloat(0);
			
			
		}
		cursor.close();
		return preis;
		
	}
	
/**
 * Ermittelt den aktuellen Gesamtbetrag aller ausgewählten Artikel und gibt den Gesamtbetrag als cursor zurück
 * @return cursor mit dem Gesamtbetrag
 */
public Cursor getActGesmatBetrag(){
	String[] COLUMNS = {"anzahl","ezpreis"};
	String TABLE_NAME = "selecteditems";
	 
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor =
            db.query(TABLE_NAME, // a. table
            COLUMNS, // b. column names
            null, // c. selections
            null, // d. selections args
            null, // e. group by
            null, // f. having
            null, // g. order by
            null); // h. limit
    
	return cursor;
	
}

/**
 * Gibt den aktuellen Gesamtbetrag der gewählten Artikel eines Kunden mit der übergebenen Id als Double-Wert zurück
 * @param kid
 * Kunden Id von dem der Gesamtbetrag ermittelt werden soll
 * @return wert = Gesamtbetrag als Double
 * 
 */
public double getActKundenGesmatBetrag(int kid){
	String[] COLUMNS = {"anzahl","ezpreis","kunden_id"};
	String TABLE_NAME = "kundenselecteditems";
	 
    SQLiteDatabase db = this.getReadableDatabase();
    
    String query = "SELECT SUM (anzahl * ezpreis) AS alles FROM kundenselecteditems WHERE kunden_id = '"+kid+"'";
    Cursor cursor = db.rawQuery(query, null);
    double wert = 0;        
    if (cursor.moveToFirst()){
    	wert = cursor.getDouble(0);
    } else {
    	wert = 5;
    }
    
    cursor.close();
	return wert;
	
}


	/**
	 * Gibt alle ausgewählten Artikel mit Einzelpreis und Anzahl als Cursor zurück
	 * @return
	 * Cursor mit den Werten artikel, anzahl, ezpreis
	 */
	public Cursor getAllSelectedItems(){
		String[] COLUMNS = {"artikel","anzahl","ezpreis"};
		String TABLE_NAME = "selecteditems";
		 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor =
	            db.query(TABLE_NAME, // a. table
	            COLUMNS, // b. column names
	            null, // c. selections
	            null, // d. selections args
	            null, // e. group by
	            null, // f. having
	            null, // g. order by
	            null); // h. limit
	    
		return cursor;
		
	}
	
	/**
	 * Gibt alle ausgewählten Artikel mit Einzelpreis und Anzahl als Cursor zusammnegefasst nach Artikelbezeichung zurück
	 * @return
	 * Cursor mit o.g. Werten
	 */
	public Cursor getAllSelectedItemsSumme(){
		//String[] COLUMNS = {"artikel","anzahl","ezpreis"};
		//String[] GROUPBY = {"artikel"};
		String TABLE_NAME = "selecteditems";
		 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT artikel, anzahl, ezpreis, SUM (anzahl) AS allanzahl FROM selecteditems WHERE anzahl > 0 GROUP BY artikel", null);
	          
	    
		return cursor;
		
	}
	
	
	public Cursor getAllSelectedKundenItemsSumme(int kid){
		//String[] COLUMNS = {"artikel","anzahl","ezpreis"};
		//String[] GROUPBY = {"artikel"};
		String TABLE_NAME = "kundenselecteditems";
		 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT artikel, anzahl, ezpreis, SUM (anzahl) AS allanzahl FROM kundenselecteditems WHERE anzahl > 0 AND kunden_id = '"+kid+"' GROUP BY artikel", null);
	    
		return cursor;
		
	}
	
	
	
	public Cursor getAllUmsaetzeSumme(){
		//String[] COLUMNS = {"artikel","anzahl","ezpreis"};
		//String[] GROUPBY = {"artikel"};
		String TABLE_NAME = "umsaetze";
		 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT artikel, anzahl, ezpreis, SUM (anzahl) AS alleanzahl FROM umsaetze WHERE anzahl > 0 GROUP BY artikel", null);
	          
	    
		return cursor;
		
	}

	/**
	 * Prüft, ob die Taste mit der Id bereits vorhanden und gibt true zurück, wenn die Taste bereits vorhanden ist, ansonsten false
	 * @param id
	 * Id der Taste, die geprüft werden soll
	 * @return true bei bereits vorhanden, false bei noch nicht vorhanden
	 */
	public boolean checkButton(int id){
		
		Log.d("SQLSUCHW","ENDE CHECKBUTTON");
		
		String[] COLUMNS = {"btn_id","artikelname","preis"};
		String TABLE_NAME = "tasten";
		 // 1. get reference to readable DB
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    // 2. build query
	    Cursor cursor =
	            db.query(TABLE_NAME, // a. table
	            COLUMNS, // b. column names
	            " btn_id = ?", // c. selections
	            new String[] { String.valueOf(id) }, // d. selections args
	            null, // e. group by
	            null, // f. having
	            null, // g. order by
	            null); // h. limit
	    Log.d("SQLSUCHW","ENDE CHECKBUTTON");
	    if (cursor.getCount() == 1){
	    	return true;
	    } else {
	        cursor.close();
	    return false;
	    }
	    
	}
	

	public Cursor getAllKundenItems(int id) {
		String[] COLUMNS = {"artikel","anzahl","ezpreis","kunden_id"};
		String TABLE_NAME = "kundenselecteditems";
		 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT anzahl, artikel, ezpreis, kunden_id, SUM (anzahl) as allanazahl FROM kundenselecteditems WHERE kunden_id = '"+id+"' GROUP BY artikel", null);
	            
		return cursor;
		
	}

	public Cursor getKundenNameAsCursor(int kid) {
		SQLiteDatabase sqldb = this.getReadableDatabase();
		
	    Cursor cursor = sqldb.rawQuery("SELECT vorname, nachname FROM kunden WHERE id = '"+kid+"'", null);
	    
	    Log.d("Cursor Kunde",String.valueOf(cursor.getCount()));
		return cursor;
		
	}
	
	/**
	 * Ermitteln den Kundennamen als Nachname, Vorname und gibt diesen als String zurück
	 * @param kid
	 * Id des Kunden, dessen Name ermittelt werden soll
	 * @return String kundenName 
	 */
	public String getKundenName(int kid) {
		SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT vorname, nachname FROM kunden WHERE id = '"+kid+"'", null);
	    
	    cursor.moveToFirst();
	    
	    String kundenName = cursor.getString(1)+", "+cursor.getString(0);
	    cursor.close();
	    return kundenName;
		
	}

	/**
	 * Ermittelt das Datum des ersten Umsatzeintrages
	 * @return long startdate
	 */
	public long getStartUmsaetze() {
		long startdate;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT vdate FROM umsaetze ORDER BY vdate ASC LIMIT 0,1", null);
		if (cursor.moveToFirst()) {
			startdate = cursor.getLong(0);
		} else {
			startdate = 0;
		}
		cursor.close();
		return startdate;
	}
	
	/**
	 * Ermittelt das Datum des letzten Eintrages
	 * @return long endate
	 */
	public long getEndUmsaetze() {
		long enddate;
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("SELECT vdate FROM umsaetze ORDER BY vdate DESC ", null);
		if (cursor.moveToFirst()) {
			enddate = cursor.getLong(0);
		} else {
			enddate = 0;
		}
		cursor.close();
		return enddate;
	}

	public int increaseValueKundenItem(String streArtikel, double ePreis, int eMenge, int kid) {
		int anzahl = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		String query = "SELECT artikel, anzahl FROM kundenselecteditems WHERE artikel = '"+streArtikel+"' AND kunden_id = '"+kid+"'";
		Cursor cursor = db.rawQuery(query, null);
		if (!cursor.moveToFirst()){
			// Wenn Artikel noch nicht vorhanden dann erstellen und auf Anzahl auf 1 
						ContentValues values = new ContentValues();
						values.put("artikel", streArtikel);
						values.put("anzahl", eMenge);
						values.put("ezpreis", ePreis);
						values.put("kunden_id", kid);
						long dbresult = db.insert("kundenselecteditems", null, values);
						
						//Log.d("DB", String.valueOf(dbresult));
						
					    anzahl = 1;
			
		} else {
		
			// Wenn Artikel bereits vorhanden dann nur Anzahl erhöhen
			anzahl = Integer.parseInt(cursor.getString(1));
			anzahl += eMenge;
			
			String query2 = "UPDATE kundenselecteditems SET anzahl = '"+anzahl+"' WHERE artikel = '"+streArtikel+"' AND kunden_id = '"+kid+"'";
			//db.update("kundenselecteditems", values, "artikel = ? AND kunden_id = ?",
			//new String[] { artikel });
			db.execSQL(query2);
			
		}
		cursor.close();
		return anzahl;
		
		
	}

	public int increaseValueItem(String streArtikel, double ePreis, int eMenge) {
		int anzahl = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		String query = "SELECT artikel, anzahl FROM selecteditems WHERE artikel = '"+streArtikel+"'";
		Cursor cursor = db.rawQuery(query, null);
		if (!cursor.moveToFirst()){
			// Wenn Artikel noch nicht vorhanden dann erstellen und auf Anzahl auf 1 
						ContentValues values = new ContentValues();
						//Log.d("addButton", String.valueOf(btnid));
						values.put("artikel", streArtikel);
						values.put("anzahl", eMenge);
						values.put("ezpreis", ePreis);
						long dbresult = db.insert("selecteditems", null, values);
						
						Log.d("DB", String.valueOf(dbresult));
						
					    anzahl = eMenge;
			
		} else {
		
			// Wenn Artikel bereits vorhanden dann nur Anzahl erhöhen
			anzahl = Integer.parseInt(cursor.getString(1));
			anzahl += eMenge;
			ContentValues values = new ContentValues();

			values.put("anzahl", anzahl);
			
			db.update("selecteditems", values, "artikel = ?",
			new String[] { streArtikel });
			
			
		}
		cursor.close();
		return anzahl;
		
		
	}

}
