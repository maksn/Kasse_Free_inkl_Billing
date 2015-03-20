package jz.apps.kassepro;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class KundenUebersichtAdapter extends BaseAdapter {

	  private ArrayList<Artikel> _data;
	    Context _c;
	    
	    KundenUebersichtAdapter (ArrayList<Artikel> data, Context c){
	        _data = data;
	        _c = c;
	    }
	   
	    @Override
		public int getCount() {
	        
	        return _data.size();
	    }
	    
	    @Override
		public Object getItem(int position) {
	        
	        return _data.get(position);
	    }
	 
	    @Override
		public long getItemId(int position) {
	        
	        return position;
	    }
	   
	    @Override
		public View getView(int position, View convertView, ViewGroup parent) {
	   
	    	View v = convertView;
	         if (v == null)
	         {
	            LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.kundenzeile, null);
	         }
	     
	           TextView tv_menge = (TextView)v.findViewById(R.id.tv_kue_anzahl);
	           TextView tv_bez = (TextView)v.findViewById(R.id.tv_kue_bez);
	           TextView tv_ep = (TextView)v.findViewById(R.id.tv_kue_ep);
	           TextView tv_gp = (TextView)v.findViewById(R.id.tv_kue_gp);
	          
	           
	 
	           Artikel artikel = _data.get(position);
	           tv_menge.setText(String.valueOf(artikel.anzahl));
	           tv_menge.setTextSize(20);
	           tv_bez.setText(artikel.getBezeichnung());
	           tv_bez.setTextSize(20);
	           tv_ep.setText(String.valueOf(artikel.getePreis()));
	           tv_ep.setTextSize(20);
	           tv_gp.setText(String.valueOf(artikel.getgPreis()));
	           tv_gp.setTextSize(20);
	           
	           
	                        
	        return v;
	}
	}