package com.cezarmoisa.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.math.BigDecimal;


public class ActivityBMI extends AppCompatActivity {

    EditText height;
    EditText heightin;
    EditText weight;
    Button Calculate;
    //InterstitialAd declaration
    private InterstitialAd Interstitial1;
    // Banner ad declaration
    private AdView Banner1;
    String AdsDisabled_str;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_m_i);

        // editText
        height = findViewById(R.id.height_edittext);
        heightin = findViewById(R.id.heightin_edittext);
        heightin.setVisibility(View.GONE);

        weight = findViewById(R.id.weight_edittext);

        //switch
        final Switch heightsw = findViewById(R.id.height_switch);
        heightsw.setChecked(false);
        heightsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //switch on(ft+in)
                    height.setHint("FT");
                    height.getText().clear();
                    heightin.setVisibility(View.VISIBLE);
                    heightin.getText().clear();
                }
                else{
                    //switch off(cm)
                    height.setHint("CM");
                    height.getText().clear();
                    heightin.setVisibility(View.GONE);
                }
            }
        });

        final Switch weightsw = findViewById(R.id.weight_switch);
        weightsw.setChecked(false);
        weightsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //switch on (lbs)
                    weight.setHint("LBS");
                    weight.getText().clear();
                }
                else{
                    //switch off (kg)
                    weight.setHint("KG");
                    weight.getText().clear();
                }
            }
        });

        //item Purchased
        AdsDisabled_str = getIntent().getStringExtra("AdsDisabled");

        //Interstital Ad
        Interstitial1 = new InterstitialAd(this);
        Interstitial1.setAdUnitId("ca-app-pub-4574445756996951/4516300634");
        Interstitial1.loadAd(new AdRequest.Builder().build());

        //Calculate button
        Calculate = findViewById(R.id.calculate_button);
        Calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //no particular cases broke
                boolean particularcases = false;
                //toast duration
                int duration = Toast.LENGTH_SHORT;
                //Toast.makeText(getApplicationContext(),"TEXT",duration);
                double height_value = 301;
                boolean incm = false;
                double height_ver = 0;
                if (heightsw.isChecked()) {
                    incm = false;
                    //get height text
                    String height_stringft = height.getText().toString();
                    String height_stringin = heightin.getText().toString();
                    if (height_stringft.equals("") || height_stringin.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please, insert your height", duration).show();
                        particularcases = true;
                    }
                    if(particularcases==false){
                        //convert text to double number
                        double height_valueft = Double.parseDouble(height_stringft);
                        double height_valuein = Double.parseDouble(height_stringin);
                        if(height_valueft>9||(height_valueft==9&&height_valuein>10)||height_valueft<1){
                            Toast.makeText(getApplicationContext(), "Please, insert a height from 1 feet to 9 feet 10 inches", duration).show();
                            particularcases=true;
                        }
                        if(particularcases==false){
                            if(height_valuein>11){
                                Toast.makeText(getApplicationContext(), "Please, insert a value from 0 to 11 for inches", duration).show();
                                particularcases=true;
                            }
                            if(particularcases==false){
                                //convert in to ft
                                double intoftconverter = 0.08333333;
                                // convert height from ft+in in ft
                                height_value = height_valueft + height_valuein * intoftconverter;
                                //convert ft in m
                                double ftinmconverter = 3.2808399;
                                // convert height from ft to m
                                height_value = height_value / ftinmconverter;
                                height_ver = height_value * 100;
                                //convert m to m^2
                                height_value = height_value * height_value;
                            }
                        }
                    }
                }
                else {
                    incm = true;
                    //get height text
                    String height_string = height.getText().toString();
                    if (height_string.equals("") && particularcases == false) {
                        Toast.makeText(getApplicationContext(), "Please, insert your height", duration).show();
                        particularcases = true;
                    }
                    if(particularcases==false){
                        //convert text to double number
                        height_value = Double.parseDouble(height_string);
                        if(height_value>300 || height_value<1){
                            Toast.makeText(getApplicationContext(), "Please, insert a height from 1 to 300 centimeters", duration).show();
                            particularcases = true;
                        }
                        if(particularcases==false){
                            //convert cm in m
                            height_ver = height_value;
                            height_value = height_value / 100;
                            //convert m in m^2
                            height_value = height_value * height_value;
                        }
                    }
                }
                if(particularcases==false) {
                    if (height_ver > 300 || height_ver < 1) {
                        if (incm == true) {
                            Toast.makeText(getApplicationContext(), "Please, insert a height from 1 to 300 centimeters", duration).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Please, insert a height from 1 feet to 9 feet 10 inches", duration).show();
                        }
                        particularcases = true;
                    }
                    //get weight text
                    String weight_string = weight.getText().toString();
                    //if no weight is inserted
                    if (weight_string.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please, insert your weight", duration).show();
                        particularcases = true;
                    }
                    if(particularcases==false){
                        //convert text to double number
                        double weight_value = Double.parseDouble(weight_string);
                        boolean inlbs = false;
                        if (weightsw.isChecked()) {
                            inlbs = true;
                            //lbs to kg converter
                            double lbstokgconverter = 2.20462262;
                            // convert weight from lbs to kg
                            weight_value = weight_value / lbstokgconverter;
                        }
                        if ((weight_value > 1000 || weight_value < 1 ) && particularcases == false) {
                            if (inlbs == true) {
                                Toast.makeText(getApplicationContext(), "Please, insert a weight from 2.2 to 2200 pounds", duration).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Please, insert a weight from 1 to 1000 kilograms", duration).show();
                            }
                            particularcases = true;
                        }
                        if(particularcases==false){
                            double BMI = weight_value / height_value;
                            BMI = new BigDecimal(BMI).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                            //ideal weight calculation with BMI = 21.75
                            double idealWeight = 21.75 * height_value;
                            idealWeight = new BigDecimal(idealWeight).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                            //unit measurement
                            String measurement = "LBS";
                            if(inlbs==false)
                                measurement = "KG";
                            openBMI_done(BMI,idealWeight,measurement);
                            //Show interstitial ad
                            if(AdsDisabled_str.equals("false")){
                                if (Interstitial1.isLoaded()) {
                                    Log.d("tgaa", "The interstitial is showed");
                                    Interstitial1.show();
                                }
                                else {
                                    Log.d("tgaa", "The interstitial wasn't loaded yet.");
                                }
                                Interstitial1.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdClosed() {
                                        //Load next interstitial ad
                                        Interstitial1.loadAd(new AdRequest.Builder().build());
                                    }

                                });
                            }
                        }
                    }
                }
            }
        });

        // Ad Banner implementation
        Banner1 = findViewById(R.id.Banner1);
        AdRequest adRequest = new AdRequest.Builder().build();
        Banner1.loadAd(adRequest);

        //item Purchased
        if(AdsDisabled_str.equals("true")){
            //disable ads
            ConstraintLayout adscontainer = findViewById(R.id.bmi_act);
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return true;
    }
    //open done page
    public void openBMI_done(double BMI, double idealWeight, String measurement){
        //declare Intent for next activity
        Intent intent = new Intent(this, ActivityBMI_done.class);
        //pass BMI value to ActivityBMI_done
        intent.putExtra("BMI_VALUE",String.valueOf(BMI));
        //pass Ideal Weight value to ActivityBMI_done
        intent.putExtra("IDWEIGHT_VALUE",String.valueOf(idealWeight));
        //pass LBS or KG
        intent.putExtra("measurement",measurement);
        //pass ads disable value
        intent.putExtra("AdsDisabled", AdsDisabled_str);
        //start next activity
        startActivity(intent);
    }
}