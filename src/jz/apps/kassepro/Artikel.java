package jz.apps.kassepro;

import java.text.NumberFormat;

public class Artikel {

	int anzahl;
	double ePreis;
	double gPreis;
	String bezeichnung;
	final NumberFormat formatter = NumberFormat.getCurrencyInstance();
	
	public void setAnzahl(int anzahl){
		this.anzahl = anzahl;
	}
	
	public int getAnzahl(){
		return this.anzahl;
	}
	
	public void setePreis(double ePreis){
		this.ePreis = ePreis;
	}
	
	public double getePreis(){
		return this.ePreis;
	}
	
	public double getgPreis(){
		return anzahl * gPreis;
	}
	
	public void setBezeichnung(String bezeichnung){
		this.bezeichnung = bezeichnung;
	}
	
	public String getBezeichnung(){
		return this.bezeichnung;
	}
	
}
