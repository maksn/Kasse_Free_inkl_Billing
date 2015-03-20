package jz.apps.kassepro;

import java.text.NumberFormat;

public class Kunde {
	
	final NumberFormat formatter = NumberFormat.getCurrencyInstance();
	int id;
	String vorname;
	String nachname;
	String anschrift;
	int plz;
	String wohnort;
	String telefon;
	String emailAdresse;
	double betrag;
	String formattedBetrag;
	
	public void neuerKunde(String vorname, String nachname, String anschrift, int plz, String wohnort, String telefon, String emailAdresse, double guthaben){
		
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getVorname(){
		return this.vorname;
		
	}
	
	public String getNachname(){
		return this.nachname;
	}
	
	public String getKundenName(){
		String kundenName = this.nachname+", "+this.vorname;
		return kundenName;
	}
	
	public void setKundenName(String vorname, String nachname){
		this.vorname = vorname;
		this.nachname = nachname;
	}
	
	public void setBetrag(double betrag){
		this.betrag = betrag;
	}
	
	public double getBetrag(){
		return this.betrag;
	}
	
	public String getFormattetBetrag(){
		return formatter.format(betrag);
	}
	
	public void setFormattedBetrag(double wert){
		this.formattedBetrag = formatter.format(wert);
	}

}
