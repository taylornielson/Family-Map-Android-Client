package com.example.familymap.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.familymap.Client.Cache;
import com.example.familymap.MainActivity;
import com.example.familymap.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String tag ="Settings";
    Button LogOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Family Map: Settings");
        }
        LogOut = (Button) findViewById(R.id.logoutButton);
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogOut();
            }
        });

    }
    private void doLogOut(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }



    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Cache.changed = true;
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        return true;
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }


        /*
        private void setSwitchListeners() {

            lifeLines = (SwitchPreferenceCompat) findPreference("LifeStory");
            familyLines = (SwitchPreferenceCompat) findPreference("FamilyTree");
            spouseLines = (SwitchPreferenceCompat) findPreference("Spouse");
            male = (SwitchPreferenceCompat) findPreference("MaleEvents");
            female = (SwitchPreferenceCompat) findPreference("FemaleEvents");
            mothersSide = (SwitchPreferenceCompat) findPreference("MotherSide");
            fathersSide = (SwitchPreferenceCompat) findPreference("FatherSide");
        }*/



    }
}