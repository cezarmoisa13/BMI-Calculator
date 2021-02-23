package com.cezarmoisa.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.math.BigDecimal;

public class ActivityBMI_done extends AppCompatActivity {

    TextView bmi_result1;
    TextView idealweight1;
    // Banner ad declaration
    private AdView Banner1;
    String AdsDisabled_str;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_m_i_done);

        //get the BMI Value
        String BMI = getIntent().getStringExtra("BMI_VALUE");
        bmi_result1 = findViewById(R.id.bmi_result);
        bmi_result1.setText(BMI);

        //get the Ideal Weight Value
        String idealWeight = getIntent().getStringExtra("IDWEIGHT_VALUE");
        double idealWeight_db = Double.parseDouble(idealWeight);
        idealweight1 = findViewById(R.id.idealweightvalue);
        //get the measurement kg/lbs
        String measurement =getIntent().getStringExtra("measurement");
        if(measurement.equals("LBS")){
            double lbstokgconverter = 2.20462262;
            idealWeight_db=idealWeight_db*lbstokgconverter;
            idealWeight_db = new BigDecimal(idealWeight_db).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        idealweight1.setText(String.valueOf(idealWeight_db) + " " + measurement);

        // Ad Banner implementation
        Banner1 = findViewById(R.id.Banner1);
        AdRequest adRequest = new AdRequest.Builder().build();
        Banner1.loadAd(adRequest);

        //item Purchased
        AdsDisabled_str = getIntent().getStringExtra("AdsDisabled");
        if(AdsDisabled_str.equals("true")){
            //disable ads
            ConstraintLayout adscontainer = findViewById(R.id.bmidone_act);
            View Banner1 = (View) findViewById(R.id.Banner1);
            adscontainer.removeView(Banner1);
            Log.d("adssds", "Ads Disabled succesfully " + AdsDisabled_str);
        }
        //item not Purchased
        else{
            //don't do anything(let the ads roll)
            Log.d("adssds", "Ads Disabled not succesfully " + AdsDisabled_str);
        }

        //back button action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //back button action bar
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), ActivityBMI.class);
        //pass ads disable value
        intent.putExtra("AdsDisabled", AdsDisabled_str);
        startActivity(intent);
        return true;
    }
}