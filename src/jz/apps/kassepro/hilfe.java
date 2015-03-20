package jz.apps.kassepro;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class hilfe extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.hilfe);
	    
	    WebView wv = (WebView)findViewById(R.id.webView1);
	    wv.loadUrl("http://www.asapp-it.de/leistungen/apps/");
	   
	   
	   
	}
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(hilfe.this, MainTabletActivity.class);
		finish();
		startActivity(intent);
	}	

}
