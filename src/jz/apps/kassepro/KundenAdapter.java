package jz.apps.kassepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import de.jzapps.sql.MySQLiteHelper;

public class KundenAdapter extends BaseAdapter {
	
	
    
	 
    private ArrayList<Kunde> _data;
    Context _c;
    
    KundenAdapter (ArrayList<Kunde> data, Context c){
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
     
           final Kunde kunde = _data.get(position);
           TextView tv_name = (TextView)v.findViewById(R.id.tv_kunde_name);
           TextView tv_betrag = (TextView)v.findViewById(R.id.tv_kunde_betrag);
           TextView tv_kid = (TextView)v.findViewById(R.id.tv_kundenid);
           
 
           MySQLiteHelper sql = new MySQLiteHelper(_c);
           
           tv_kid.setText(String.valueOf(kunde.id));
           tv_kid.setTextSize(20);
           tv_name.setText(kunde.getKundenName());
           tv_name.setTextSize(20);
           tv_betrag.setText(kunde.getFormattetBetrag());
           tv_betrag.setTextSize(20);
           
                        
        return v;
}
}