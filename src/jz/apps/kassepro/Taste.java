package jz.apps.kassepro;


import android.graphics.Color;

public class Taste {
	
	private int id;
	private String artikel;
	private float preis;
	private String farbe;
	private int mengeVerkauf;
	private boolean btnIsEnabled;
		
	public void taste(int id, String artikel, float preis, Color farbe){
		
	}
	
	public void enableBtn(int id){
		this.btnIsEnabled = true;
	}
	
	public void disableBtn(int id){
		this.btnIsEnabled = false;
	}
	
	public boolean isBtnEnabled(int id){
		return this.btnIsEnabled;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setArtikel(String artikel){
		this.artikel = artikel;
	}
	
	public String getArtikel(){
		return artikel;
	}
	
	public void setPreis(float preis){
		this.preis = preis;
	}
	
	public float getPreis(){
		return preis;
	}
	
	public void setFarbe(String btnFarbe){
		this.farbe = btnFarbe;
	}
	
	public String getFarbe(){
		return farbe;
	}
	
	public int getMengeVerkauf(){
		return mengeVerkauf;
	}
	
	public void incrMengeVerkauf(){
		mengeVerkauf++;
	}

}
