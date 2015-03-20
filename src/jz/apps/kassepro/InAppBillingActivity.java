package jz.apps.kassepro;

import de.jzapps.billingUtils.IabHelper;
import de.jzapps.billingUtils.IabResult;
import de.jzapps.billingUtils.Inventory;
import de.jzapps.billingUtils.Purchase;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


public class InAppBillingActivity extends Activity {
	
	public static final int APP_BONUS_MAX = 1;
	// Premium equals Pro-Version
	public static final String PREMIUM = "premium";
    public static final String BONUS = "bonus";
    public static final String ADFREE = "adfree";
    public static final int BONUS_1_KUNDENVERWALTUNG = 1;
    
    public static final int RC_REQUEST = 10001;
	
	boolean exampleSubscription = false;
	boolean isPremium = false;
	boolean subscribedToAdFree = false;
	int appBonus = 0;
	
	//Public-Key für verschlüsselte Signaturabfrage (Kommunikation mit Google Play Server = ID der Applikation)
	String base64EncodedPublicKey;
	
	ImageButton btn_buyPremium;
	ImageButton btn_buyBonus;
	ImageButton btn_buyAdFree;
	
	int imgResPremiumBought = R.drawable.kasse_pro_logo_lowq_bought_128_128;
	Drawable premiumBought = getResources().getDrawable(imgResPremiumBought);
	 
	int imgResKundenverwaltungBought = R.drawable.kasse_logo_kv_lowq_bought_128_128;
	Drawable kundenverwaltungBought = getResources().getDrawable(imgResKundenverwaltungBought);
	 
	int imgResAdFreeBought = R.drawable.kasse_logo_adfree_lowq_bought_128_128;
	Drawable adFreeBought = getResources().getDrawable(imgResAdFreeBought);
	
	//Hilfsobjekt zum vereinfachten In-App-Billing
	IabHelper billingHelper;
	IabHelper.QueryInventoryFinishedListener gotInventoryListener;
	IabHelper.OnConsumeFinishedListener consumeFinishedListener;
	IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener;
	
	SharedPreferences bonusInventar;
	Editor bonusInventarEditor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_billing);
        
        bonusInventar = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	bonusInventarEditor = bonusInventar.edit();
        
        btn_buyPremium = (ImageButton) findViewById(R.id.button_buy_premium);
        btn_buyBonus = (ImageButton) findViewById(R.id.button_buy_bonus);
        btn_buyAdFree = (ImageButton) findViewById(R.id.button_buy_adfree);
        
        initButtonListeners();
        
        base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk3+su+D0JhHWG78n2snHRqGftuT0TBBk/gkpIRtt4VaNtkjU33GmabmxcjyCLkDIO7wymkpJWESB92WVmKf6Ac3mRqGHCEFzZb00FbMlD3LfVLa2kzxJs1+4DFH+NBBCPPBHCdDfVrjJ3mq3XqXy3451RJnS2DQg/THV0dd/SumT58ItZgcC9Ik+kLc+mEir8fdnyHXHzSVA+jPPPnfTov9NrW2gtg5nk7mhS49h5IA9Xyw1u3QTbY/2E803aLLOLk3BUExaXhcu3RQOHbAhVtuh266YBiX43Uz4PXfKfzkeRCgSclEQ7oiUFRmHtdg3Gq86r6mhDRugSbaLNLBZxQIDAQAB";
        billingHelper = new IabHelper(this, base64EncodedPublicKey);
        
        billingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Toast.makeText(getApplicationContext(), "billingerHelper Setup finished", Toast.LENGTH_SHORT).show();

                if (!result.isSuccess()) {
                    // Fehler-Behandlung des Helper-Objekts
                    Toast.makeText(getApplicationContext(), "Problem setting up billingHelper: " + result, Toast.LENGTH_LONG);
                    return;
                }

                // Falls Helper-Objekt geschlossen wurde -> Setup-Abbruch
                if (billingHelper == null) return;

                // billingHelper wurde aufgesetzt 
                // Abfrage nach vorhandenem Inventar (Bonus, Abos, Premium-Modus) wird gestartet
                Toast.makeText(getApplicationContext(), "Setup successful. Querying inventory.", Toast.LENGTH_SHORT).show();
                billingHelper.queryInventoryAsync(gotInventoryListener);
            }
        });
    
        
     // Listener that's called when we finish querying the items and subscriptions we own
    gotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory bonusInventory) {
            Toast.makeText(getApplicationContext(), "Query inventory finished.", Toast.LENGTH_SHORT).show();

            // Falls Helper-Objekt geschlossen wurde -> Abbruch
            if (billingHelper == null) return;

            // Inventar-Abfrage fehlerhaft? -> Abbruch
            if (result.isFailure()) {
                Toast.makeText(getApplicationContext(), "Failed to query inventory: " + result, Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getApplicationContext(), "Query inventory successful!", Toast.LENGTH_SHORT).show();
            

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Existiert das Premium-Upgrade?
            Purchase premiumPurchase = bonusInventory.getPurchase(PREMIUM);
            // TO DO: Premium-Kauf verifizieren in Methode verifyDeveloperPayload()
            // true (Default atm) -> besitzt Premium - false -> kein Premium-User
            isPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Toast.makeText(getApplicationContext(), "User is " + (isPremium ? "PREMIUM" : "NOT PREMIUM"), Toast.LENGTH_LONG).show();

            // Steht unendlicher Bonus zur Verfügung?
            Purchase infiniteBonusPurchase = bonusInventory.getPurchase(ADFREE);
            subscribedToAdFree = (infiniteBonusPurchase != null &&
                    verifyDeveloperPayload(infiniteBonusPurchase));
            
            Toast.makeText(getApplicationContext(), "User " + (subscribedToAdFree ? "HAS" : "DOES NOT HAVE")
                        + " an ad-free subscription.", Toast.LENGTH_LONG).show();
            
            if (subscribedToAdFree) appBonus = APP_BONUS_MAX;

            // Check for bonus delivery -- if we own bonus, we should fill up the bonus-level immediately
            Purchase bonusPurchase = bonusInventory.getPurchase(BONUS);
            if (bonusPurchase != null && verifyDeveloperPayload(bonusPurchase)) {
                Toast.makeText(getApplicationContext(), "We have bonus. Consuming it.", Toast.LENGTH_SHORT);
                billingHelper.consumeAsync(bonusInventory.getPurchase(BONUS), consumeFinishedListener);
                return;
            }
            
            //updateUi();
            //setWaitScreen(false);
            Toast.makeText(getApplicationContext(), "Initial inventory query finished; enabling main UI.", Toast.LENGTH_SHORT).show();
        }
    };
        
    purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        	Toast.makeText(getApplicationContext(), "Purchase finished: " + result + ", purchase: " + purchase, Toast.LENGTH_LONG).show();

            // if we were disposed of in the meantime, quit.
            if (billingHelper == null) return;

            if (result.isFailure()) {
            	Toast.makeText(getApplicationContext(), "Error purchasing: " + result, Toast.LENGTH_LONG).show();
                //setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
            	Toast.makeText(getApplicationContext(), "Error purchasing. Authenticity verification failed.", Toast.LENGTH_LONG).show();
                //setWaitScreen(false);
                return;
            }

            Toast.makeText(getApplicationContext(), "Purchase successful.", Toast.LENGTH_SHORT).show();

            if (purchase.getSku().equals(BONUS)) {
                // bought bonus. So consume it.
            	Toast.makeText(getApplicationContext(), "Purchase is bonus. Starting bonus" +
            			" consumption.", Toast.LENGTH_SHORT);
                billingHelper.consumeAsync(purchase, consumeFinishedListener);
                btn_buyBonus.setImageDrawable(kundenverwaltungBought);
            }
            else if (purchase.getSku().equals(PREMIUM)) {
                // bought the premium upgrade!
            	
            	Toast.makeText(getApplicationContext(), "Purchase is premium upgrade. Congratulating user.", Toast.LENGTH_SHORT);
                Toast.makeText(getApplicationContext(), "Thank you for upgrading to premium!", Toast.LENGTH_SHORT);
                
                isPremium = true;
                bonusInventarEditor.putBoolean("IS_PREMIUM", isPremium);
                bonusInventarEditor.commit();
                btn_buyPremium.setImageDrawable(premiumBought);
                
                //TO DO
                
                //setWaitScreen(true);
                //updateUi();
                //setWaitScreen(false);
            }
            else if (purchase.getSku().equals(ADFREE)) {
                // bought the infinite gas subscription
               
                Toast.makeText(getApplicationContext(), "ad-free subscription purchased.", Toast.LENGTH_SHORT);
                Toast.makeText(getApplicationContext(), "Thank you for subscribing to ad-free version!",
                		Toast.LENGTH_SHORT).show();
                subscribedToAdFree = true;
                bonusInventarEditor.putBoolean("IS_AD_FREE", subscribedToAdFree);
                bonusInventarEditor.commit();
                btn_buyAdFree.setImageDrawable(adFreeBought);
                //appBonus = APP_BONUS_MAX;
                
                //TO DO
                //setWaitScreen(true);
                //updateUi();
                //setWaitScreen(false);
            }
        }
    };

    // Called when consumption is complete
    consumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
        	Toast.makeText(getApplicationContext(), "Consumption finished. Purchase: " + purchase + ", result: " + result, Toast.LENGTH_SHORT).show();

            // if we were disposed of in the meantime, quit.
            if (billingHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
            	Toast.makeText(getApplicationContext(), "Consumption successful. Provisioning.", Toast.LENGTH_SHORT).show();
                appBonus = appBonus == APP_BONUS_MAX ? APP_BONUS_MAX : appBonus + 1;
                saveBonusData();
                Toast.makeText(getApplicationContext(), "You filled 1/5 bonus. Your bonus-level is now " + String.valueOf(appBonus) + "/5!", Toast.LENGTH_SHORT).show();
            }
            
            else {
            	Toast.makeText(getApplicationContext(), "Error while consuming: " + result, Toast.LENGTH_LONG).show();
            }
            
            //setWaitScreen(true);
            //updateUi();
            //setWaitScreen(false);
            
            Toast.makeText(getApplicationContext(), "End consumption flow.", Toast.LENGTH_SHORT).show();
        }
	};
    }

    
    public void onBuyBonusButtonClicked(View arg0) {
    	Toast.makeText(getApplicationContext(), "Buy bonus button clicked.", Toast.LENGTH_SHORT).show();
    	
    	/*
        if (subscribedToAdFree) {
        	Toast.makeText(getApplicationContext(), "No need! You're subscribed to infinite bonus. Isn't that awesome?",
        			Toast.LENGTH_SHORT).show();
            return;
        }*/
    	

        if (appBonus >= APP_BONUS_MAX) {
        	Toast.makeText(getApplicationContext(), "You already reached the final bonus level!",
        			Toast.LENGTH_SHORT).show();
            return;
        }
        
        //TODO
        // launch the bonus purchase UI flow.
        // We will be notified of completion via purchaseFinishedListener
        // setWaitScreen(true);
        Toast.makeText(getApplicationContext(), "Launching purchase flow for bonus.", Toast.LENGTH_SHORT).show();

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        billingHelper.launchPurchaseFlow(this, BONUS, RC_REQUEST,
                purchaseFinishedListener, payload);
    }

    // User clicked the "Upgrade to Premium" button.
    public void onBuyPremiumButtonClicked(View arg0) {
    	Toast.makeText(getApplicationContext(), "Upgrade button clicked; launching purchase flow for upgrade.", Toast.LENGTH_SHORT);
        
    	//TODO
    	//setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        billingHelper.launchPurchaseFlow(this, PREMIUM, RC_REQUEST,
                purchaseFinishedListener, payload);
    }

    // "Subscribe to infinite bonus" button clicked. Explain to user, then start purchase
    // flow for subscription.
    public void onBuyAdFreeButtonClicked(View arg0) {
        if (!billingHelper.subscriptionsSupported()) {
        	Toast.makeText(getApplicationContext(), "Subscriptions not supported on your device yet. Sorry!",
        			Toast.LENGTH_SHORT).show();
            return;
        }

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";
        
        //setWaitScreen(true);
        Toast.makeText(getApplicationContext(), "Launching purchase flow for ad-free subscription.",
        		Toast.LENGTH_SHORT).show();
        billingHelper.launchPurchaseFlow(this,
                ADFREE, IabHelper.ITEM_TYPE_SUBS,
                RC_REQUEST, purchaseFinishedListener, payload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Toast.makeText(getApplicationContext(), "onActivityResult(" + requestCode + "," + resultCode + "," + data,
    			Toast.LENGTH_SHORT).show();
        if (billingHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!billingHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
        	//TODO (optional)
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
        	Toast.makeText(getApplicationContext(), "onActivityResult handled by billingHandler.",
        			Toast.LENGTH_SHORT).show();
        }
    }
    
    public void saveBonusData() {

    	//keine sichere Speicherung in SharedPreferences-Objekt, nur für den Übergang

        //SharedPreferences.Editor bonusInventary = getPreferences(MODE_PRIVATE).edit();
        bonusInventarEditor.putInt("BONUS_LEVEL", appBonus);
        bonusInventarEditor.commit();
        Toast.makeText(getApplicationContext(), "Saved data: App-Bonus = " + String.valueOf(appBonus), Toast.LENGTH_SHORT).show();
    }

    public void loadBonusData() {
    	
        //SharedPreferences bonusInventary = getPreferences(MODE_PRIVATE);
        
        //Bei Nicht-Existenz des Keys AppBonusLevel, wird 0 als Default-Value gesetzt
        
        appBonus = bonusInventar.getInt("BONUS_LEVEL", 0);
        Toast.makeText(getApplicationContext(), "Loaded data: App-Bonus = " + String.valueOf(appBonus), Toast.LENGTH_SHORT);
    }
    
    public void initButtonListeners() {
    	
    	btn_buyPremium.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				onBuyPremiumButtonClicked(v);
			}
    	});
    	
    	btn_buyBonus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				onBuyBonusButtonClicked(v);
			}
    	});
    	
    	btn_buyAdFree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				onBuyAdFreeButtonClicked(v);
			}
    	});
    }
    
    /** Verifies the developer payload of a purchase. */
    public boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.in_app_billing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //if (id == R.id.action_settings) {
          //  return true;
       // }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
    	Toast.makeText(getApplicationContext(), "Destroying billingHelper!", Toast.LENGTH_SHORT).show();
        if (billingHelper != null) {
            billingHelper.dispose();
            billingHelper = null;
        }
    }
}
