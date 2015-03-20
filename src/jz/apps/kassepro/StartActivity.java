package jz.apps.kassepro;


import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.Policy;
import com.android.vending.licensing.ServerManagedPolicy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends Activity {
	
	Context context;
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk3+su+D0JhHWG78n2snHRqGftuT0TBBk/gkpIRtt4VaNtkjU33GmabmxcjyCLkDIO7wymkpJWESB92WVmKf6Ac3mRqGHCEFzZb00FbMlD3LfVLa2kzxJs1+4DFH+NBBCPPBHCdDfVrjJ3mq3XqXy3451RJnS2DQg/THV0dd/SumT58ItZgcC9Ik+kLc+mEir8fdnyHXHzSVA+jPPPnfTov9NrW2gtg5nk7mhS49h5IA9Xyw1u3QTbY/2E803aLLOLk3BUExaXhcu3RQOHbAhVtuh266YBiX43Uz4PXfKfzkeRCgSclEQ7oiUFRmHtdg3Gq86r6mhDRugSbaLNLBZxQIDAQAB";

	// Generate 20 random bytes, and put them here.
    private static final byte[] SALT = new byte[] {
     -66, 36, 19, -116, -103, -77, 78, -22, 35, 88, -99,
     -45, 71, -127, -39, -113, -44, 98, -29, 46
     };

    
    private TextView mStatusText;  
    private Button mCheckLicenseButton;
    private Button btn_weiter;
    
    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    // A handler on the UI thread.
    private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		SharedPreferences prefs = this.getSharedPreferences("Versionsmodus", Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = prefs.edit();
		
		
		Button btn_tablet = (Button)findViewById(R.id.btn_tabletstart);
		btn_tablet.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		Button btn_smart = (Button)findViewById(R.id.btn_smartstart);
		btn_smart.setTextSize(getResources().getDimension(R.dimen.TxtSizeSysBtn));
		
		//btn_smart.setEnabled(true);
		//btn_tablet.setEnabled(true);
		
		btn_tablet.setOnClickListener(new OnClickListener() {
		
			
			
			@Override
			public void onClick(View v) {
				editor.putInt("modus", 0);
				editor.commit(); 
				Intent intent = new Intent(StartActivity.this, MainTabletActivity.class);
				startActivity(intent);
				finish();
				
			}
		});
		
		btn_smart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, MainTabletActivity.class);
				editor.putInt("modus", 1);
				editor.commit();
				startActivity(intent);
				finish();
				
			}
		});
		
		
		 mHandler = new Handler();

	        // Try to use more data here. ANDROID_ID is a single point of attack.
	        String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

	        // Library calls this when it's done.
	        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
	        // Construct the LicenseChecker with a policy.
	        mChecker = new LicenseChecker(
	            this, new ServerManagedPolicy(this,
	                new AESObfuscator(SALT, getPackageName(), deviceId)),
	            BASE64_PUBLIC_KEY);
	        doCheck();
		
		
		/*
		 *  Beim Start dieser Activity wird versucht im Hintergrund eine Lizenzprüfung durchzuführen.
		 *  Beim allersten Start darf die Lizenzprüfung nicht fehlschlagen. Danach können 10 Prüfungen fehlschlagen.
		 *  Wenn noch 3 Sarts möglich sind, wird dies per DialogFenster beim Start angezeigt.
		 *  
		 */
		
		
		
	}
	
	private void doCheck() {
       // mCheckLicenseButton.setEnabled(false);
        //setProgressBarIndeterminateVisibility(true);
        //mStatusText.setText(R.string.checking_license);
        mChecker.checkAccess(mLicenseCheckerCallback);
    }


    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        public void allow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.
          // displayResult(getString(R.string.allow));
            if(licenseCheckOK()){
            	
            	runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Button btnt = (Button)findViewById(R.id.btn_tabletstart);
						Button btns = (Button)findViewById(R.id.btn_smartstart);
		            	btnt.setEnabled(true);
		            	btns.setEnabled(true);
						
					}
				});
            	
            }
            
           
        }

        public void dontAllow(int policyReason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
           // displayResult(getString(R.string.dont_allow));
           // displayDialog(policyReason == Policy.RETRY); 
            
            fehlVersuch();
            
            // Should not allow access. In most cases, the app should assume
            // the user has access unless it encounters this. If it does,
            // the app should inform the user of their unlicensed ways
            // and then either shut down the app or limit the user to a
            // restricted set of features.
            // In this example, we show a dialog that takes the user to Market.
            // If the reason for the lack of license is that the service is
            // unavailable or there is another problem, we display a
            // retry button on the dialog and a different message.
            
        }

        public void applicationError(int errorCode) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            
            fehlVersuch();
            // This is a polite way of saying the developer made a mistake
            // while setting up or calling the license checker library.
            // Please examine the error code and fix the error.
            //String result = String.format(getString(R.string.application_error), errorCode);
           // displayResult(result);
        }
    }

    public void fehlVersuch(){
    	
   	 SharedPreferences pLizenz = this.getSharedPreferences("LizenzCheck", MODE_PRIVATE);
   	 int intAnzahl = pLizenz.getInt("Anzahl", 99);
   	 intAnzahl++;
   	 Log.d("Anzahl", "Versuch Nr.: "+pLizenz.getInt("Anzahl", 99));
   	 SharedPreferences.Editor editor = pLizenz.edit();
   	 if (intAnzahl > 10) intAnzahl = 10;
   	 editor.putInt("Anzahl", intAnzahl);
   	 editor.commit();
   	 
 	
	
	/*
	 * Wenn die Anzahl der Versuche > 5 ist dann Dialog anzeigen
	 */
	
	final Dialog dLizenz = new Dialog(StartActivity.this);
	dLizenz.setContentView(R.layout.dialog_lizenzpruefung);
	dLizenz.setTitle(R.string.lizenzpruefung);
	Button btn_manTest = (Button)dLizenz.findViewById(R.id.btn_test);
	btn_manTest.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dLizenz.dismiss();
			doCheck();
		}
	});
	
	
	Button btn_ok = (Button)dLizenz.findViewById(R.id.btn_ok);
	btn_ok.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Button btnt = (Button)findViewById(R.id.btn_tabletstart);
					Button btns = (Button)findViewById(R.id.btn_smartstart);
	            	btnt.setEnabled(true);
	            	btns.setEnabled(true);
					
				}
			});
			dLizenz.dismiss();
			
		}
	});
	
	
		int intRestVersuche = (10 - intAnzahl);
		if (intRestVersuche < 0) {
			intRestVersuche = 0; 
		}
		
		if (intRestVersuche == 0) {
			btn_ok.setEnabled(false);
		}
		
		String strRestVersuche = String.valueOf(intRestVersuche);
		
		TextView tv_verusche = (TextView)dLizenz.findViewById(R.id.tv_liz_verusche);
		tv_verusche.setTextColor(Color.RED);
		tv_verusche.setText("Sie haben noch "+strRestVersuche+" von 10 Versuchen, danach wird die App gesperrt");
		
		
 
	
	dLizenz.show();
   	 
   }

	public boolean licenseCheckOK() {
		SharedPreferences prefs = this.getSharedPreferences("LizenzCheck",MODE_PRIVATE);
		final SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("Anzahl", 1);
		editor.commit(); 
		
		Log.d("Anzahl", "Versuch Nr.: "+prefs.getInt("Anzahl", 99));
		
		return true;
		

		
	}
	}


