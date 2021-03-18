package com.cezarmoisa.bmicalculator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hotchemi.android.rate.AppRate;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    Button bmi_button;
    Button bfp_button;
    // Banner ad declaration
    private AdView Banner1;
    //go back to the main page from alert dialog
    Context mcontext = this;
    //billing client declaration
    private BillingClient billingClient;
    //item to purchase
    public static final String PREF_FILE= "MyPref";
    public static final String PURCHASE_KEY= "Remove Ads";
    public static final String PRODUCT_ID= "YOUR ID";
    //billing buttons test
    public boolean AdsDisabled = false;
    String AdsDisabled1 = "false";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //billing client
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases().setListener(this).build();

        //billing
        // Establish connection to billing client
        //check purchase status from google play store cache
        //to check if item already Purchased previously or refunded
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(INAPP);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if(queryPurchases!=null && queryPurchases.size()>0){
                        handlePurchases(queryPurchases);
                    }
                    //if purchase list is empty that means item is not purchased
                    //Or purchase is refunded or canceled
                    else{
                        savePurchaseValueToPref(false);
                        AdsDisabled = false;
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });

        //social media toolbar
        //Toolbar toolbar = findViewById(R.id.toolbar1);
        //setSupportActionBar(toolbar);

        //Initialize Mobile Ads doar pe mainActivity trebuie
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //if user pays disable the ad
        //disableAds();

        //rating reminder v1
        AppRate.with(this)
                //first apearence on day 0(first day)
                .setInstallDays(0)
                //after 3 launches
                .setLaunchTimes(3)
                //after 2 days
                .setRemindInterval(2)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);

        bmi_button = findViewById(R.id.bmi_button);
        bfp_button = findViewById(R.id.bfp_button);

        bmi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBMI();
            }
        });

        bfp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBFP();
            }
        });

        // Ad Banner implementation
        Banner1 = findViewById(R.id.Banner1);
        AdRequest adRequest = new AdRequest.Builder().build();
        Banner1.loadAd(adRequest);

        //item Purchased
        if(getPurchaseValueFromPref()){
            //disable ads
            ConstraintLayout adscontainer = findViewById(R.id.main_act);
            View Banner1 = (View) findViewById(R.id.Banner1);
            adscontainer.removeView(Banner1);
            AdsDisabled = true;
            AdsDisabled1 = String.valueOf(AdsDisabled);
        }
        //item not Purchased
        else{
            //don't do anything(let the ads roll)
        }

    }
    //3 dots menu, custom for social media, remove ads and more apps
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //Remove Ads
        if(id == R.id.remove_ads_btn) {
            if(getPurchaseValueFromPref()){
                Toast.makeText(this, "Thank you for buying!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Remove Ads", Toast.LENGTH_SHORT).show();
                //purchase remove ads item
                purchase();
            }
        }

        //More apps
        if(id == R.id.more_apps_btn){
            Toast.makeText(this,"Check these out!",Toast.LENGTH_LONG).show();
            //Sascom page google play store
            openlink("https://play.google.com/store/apps/dev?id=6847342940063847928");
        }

        //Credits/Social media/Contact/Follow
        if(id == R.id.credits_btn){
            //Toast.makeText(this,"Follow",Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
            final View view2 = layoutInflaterAndroid.inflate(R.layout.dialog, null);
            builder.setView(view2);
            //true to cancel alert dialog by touching anywhere
            //false to cancel only by a button
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            view2.findViewById(R.id.emailButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Email
                    sendemail();
                }
            });
            view2.findViewById(R.id.ytbutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Youtube Sascom Software Development
                    openlink("https://www.youtube.com/channel/UCMAa_pspjeeyzzisCuHGvsQ");
                }
            });
            view2.findViewById(R.id.instabutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Instagram Sascom Software Development
                    openlink("https://www.instagram.com/sascom_dev/");
                }
            });
            view2.findViewById(R.id.fbbutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Facebook Sascom Software Development
                    openlink("https://www.facebook.com/Sascom-Software-Development-107731334696368");
                }
            });
            //cancel button(decomment if you want, and set builder.setCancelable(false)
            view2.findViewById(R.id.cancelbutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go back to the current page
                    alertDialog.cancel();
                }
            });
        }
        return true;
    }
    public void openBMI(){
        Intent intent = new Intent(this, ActivityBMI.class);
        intent.putExtra("AdsDisabled", String.valueOf(AdsDisabled1));
        startActivity(intent);
    }
    public void openBFP() {
        Intent intent = new Intent(this, ActivityBFP.class);
        intent.putExtra("AdsDisabled", String.valueOf(AdsDisabled1));
        startActivity(intent);
    }
    //Send email
    public void sendemail(){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","cezarmoisa13@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "cezarmoisa13@gmail.com"); // String[] addresses
    }
    //open a link
    public void openlink(String link){
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    //billing functions
    private SharedPreferences getPreferenceObject() {
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
    }
    private SharedPreferences.Editor getPreferenceEditObject() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        return pref.edit();
    }
    private boolean getPurchaseValueFromPref(){
        return getPreferenceObject().getBoolean( PURCHASE_KEY,false);
    }
    private void savePurchaseValueToPref(boolean value){
        getPreferenceEditObject().putBoolean(PURCHASE_KEY,value).commit();
    }

    //initiate purchase on button click
    public void purchase() {
        //check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase();
        }
        //else reconnect service
        else{
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase();
                    } else {
                        Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                }
            });
        }
    }
    private void initiatePurchase() {
        List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetailsList.get(0))
                                        .build();
                                billingClient.launchBillingFlow(MainActivity.this, flowParams);
                            }
                            else{
                                //try to add item/product id "purchase" inside managed product in google play console
                                Toast.makeText(getApplicationContext(),"Purchase Item not Found",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    " Error "+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //if item newly purchased
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
        //if item already purchased then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(INAPP);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                handlePurchases(alreadyPurchases);
            }
        }
        //if purchase cancelled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }
        // Handle any other error msgs
        else {
            //Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    void handlePurchases(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            //if item is purchased
            if (PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(getApplicationContext(), "Error : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
                }
                //else item is purchased and also acknowledged
                else {
                    // Grant entitlement to the user on item purchase
                    // restart activity
                    if(!getPurchaseValueFromPref()){
                        savePurchaseValueToPref(true);
                        Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                        this.recreate();
                    }
                }
            }
            //if purchase is pending
            else if( PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
            {
                //Toast.makeText(getApplicationContext(),
                        //"Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            }
            //if purchase is unknown
            else if(PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            {
                savePurchaseValueToPref(false);
                //purchaseStatus.setText("Purchase Status : Not Purchased");
                //Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }
    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                //if purchase is acknowledged
                // Grant entitlement to the user. and restart activity
                savePurchaseValueToPref(true);
                Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                MainActivity.this.recreate();
            }
        }
    };

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkHJizZvSNwjqI/TkMV6fNvQEULURzoAx5L8z9DYSV7fLWAWctBRDpUa2T0s5cHtIyxIoDWOb6QG/XNyI4nTeaHWd2jVEpriRtyapTAqQoL5pwh1REVA4beBNNFamtubK7QWhRqCQPIrbLKUh/i+kaQYVkjg8yJuzlzRTOw7zKFZz+UBk7AF2zYrlB8RXW3erZjtszEQIyfa6RiC7PLnvzx/qtKAVgJ2hbNrqLlkmgPE0GkWenKbFyBO89/gskX9c+MpbA7hUmZ8latsa+lBZBe9WJvgCRtoHCBj0a8z10416h08Hig48pBzeJslhUe2hsXgiWUIFPzac+QcjEeUqxwIDAQAB";
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(billingClient!=null){
            billingClient.endConnection();
        }
    }
    //remove preferences
    /*public static void removeDataPref(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        //editor.remove(PREF_FILE);
        editor.clear();
        editor.apply();
    }
    public void resetPrefs(View view){
        removeDataPref(this);
    }*/
}
