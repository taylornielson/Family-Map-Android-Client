package com.example.familymap.UI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.familymap.MainActivity;
import com.example.familymap.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

public class EventActivity extends AppCompatActivity {
    final public static String tag = "eventActivity";
    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(new IconDrawable(this, FontAwesomeIcons.fa_arrow_left).color(Color.LTGRAY).sizeDp(20));
        actionBar.setTitle("Family Map");
        if (savedInstanceState == null){
            Intent intent = getIntent();
            if (intent.hasExtra("eventID")) {
                eventID = (intent.getStringExtra("eventID"));
            }
            else {
                Log.e(tag, "No extra value"); return;}
        }
        else{
            eventID = (savedInstanceState.getString("eventID"));
        }

        Map eventMap = new Map();
        eventMap.setEventActivity(true);
        eventMap.setEventID(eventID);
        getSupportFragmentManager().beginTransaction().add(R.id.event_fragment, eventMap).commit();

    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        return true;
    }
}
