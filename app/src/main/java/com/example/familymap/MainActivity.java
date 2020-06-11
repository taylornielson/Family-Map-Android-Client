package com.example.familymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.familymap.Client.Cache;
import com.example.familymap.UI.Login;
import com.example.familymap.UI.Map;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


public class MainActivity extends AppCompatActivity {
    Boolean showMap = true;
    public final static String tag = "familyMainActivity";
    public static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {
            showLogin();
        }
        else
        {
            if (savedInstanceState.containsKey("map"))
            {
                boolean showMap = savedInstanceState.getBoolean("map");
                if (showMap) showMap();
                else showLogin();
            }
            else showLogin();
        }
    }

    public void showLogin()
    {
        getSupportFragmentManager().beginTransaction().add(R.id.main_fragment , new Login()).commit();
        Log.e(tag, "added loginfragment");
    }
    public void showMap()
    {
        Log.e(tag,"called show map");
        if (showMap) getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new Map()).commit();
        Log.e(tag, "showed map");
    }

}
