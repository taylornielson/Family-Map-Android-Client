package com.example.familymap;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import androidx.test.runner.AndroidJUnit4;

import com.example.familymap.Client.Cache;
import com.example.familymap.Client.Proxy;
import com.example.familymap.Models.Person;
import com.example.familymap.UI.SearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import Model.Event;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleInstrumentedTest {

    @Test
    public void testLogin() throws JSONException {
        Proxy.setIP("10.0.2.2");
        Proxy.setPort("8080");
        JSONObject body = new JSONObject();
        body.put("userName", "user");
        body.put("password", "pass");
        JSONObject returnObject = Proxy.doPostAction("user/login", body);
        assert(returnObject != null);


    }

    @Test
    public void testLoginFail() {
        int val;
        try {
            val = 1;
            Proxy.setIP("10.0.2.2");
            Proxy.setPort("8080");
            JSONObject body = new JSONObject();
            body.put("blagh", "user");
            body.put("password", "pass");
            JSONObject returnObject = Proxy.doPostAction("user/login", body);
            assert (returnObject == null);
        } catch(JSONException e){
            val = 0;
        }
    }

    @Test
    public void testRegister() throws JSONException {
        JSONObject body = new JSONObject();
        try {
            body.put("userName", "user");
            body.put("password", "pass");
            body.put("email", "email");
            body.put("firstName", "first");
            body.put("lastName", "last");
            body.put("gender", "gend");
        } catch (JSONException e) {

        }
        Proxy.setIP("10.0.2.2");
        Proxy.setPort("8080");
        JSONObject returnObject = Proxy.doPostAction("user/register", body);
        assert (returnObject != null);


    }

    @Test
    public void testRegisterFail() {
        JSONObject body = new JSONObject();
        try {
            body.put("blagh", "user");
            body.put("password", "pass");
            body.put("email", "email");
            body.put("firstName", "first");
            body.put("ighkl", "last");
            body.put("gender", "gend");
        } catch (JSONException e) {

        }
        Proxy.setIP("10.0.2.2");
        Proxy.setPort("8080");
        JSONObject returnObject = Proxy.doPostAction("user/register", body);
        assert (returnObject == null);
    }

    @Test
    public void testGetEvent() {
        Proxy.setPort("8080");
        Proxy.setIP("10.0.2.2");
        try {
            JSONObject result = Proxy.doAuthenticationAction("event", "0e1240d3-ae08-441d-b96f-3b19fb50961b");
            JSONArray data = result.getJSONArray("data");
            assert (data != null);
        }catch (JSONException e){ }

    }
    @Test
    public void testGetEventFail() {
        Proxy.setPort("8080");
        Proxy.setIP("10.0.2.2");
        try {
            JSONObject result = Proxy.doAuthenticationAction("event", "thisisfake!");
            JSONArray data = result.getJSONArray("data");
            assert (data == null);
        }catch (JSONException e){ }

    }

    @Test
    public void testGetPerson() {
        Proxy.setPort("8080");
        Proxy.setIP("10.0.2.2");
        try {
            JSONObject result = Proxy.doAuthenticationAction("person", "0e1240d3-ae08-441d-b96f-3b19fb50961b");
            JSONArray data = result.getJSONArray("data");
            assert (data != null);
        }catch (JSONException e){ }

    }
    @Test
    public void testGetPersonFail() {
        Proxy.setPort("8080");
        Proxy.setIP("10.0.2.2");
        try {
            JSONObject result = Proxy.doAuthenticationAction("person", "thisisfake!");
            JSONArray data = result.getJSONArray("data");
            assert (data == null);
        }catch (JSONException e){ }
    }

    @Test
    public void testFamilyRelationships(){
        ArrayList<Pair<String, String>> aList = new ArrayList<Pair<String, String>>();
        Cache.people = new HashMap<String, Person>();
        Cache.events = new HashMap<String, Event>();
        Proxy.setIP("10.0.2.2");
        Proxy.setPort("8080");
        Cache.authToken = "0e1240d3-ae08-441d-b96f-3b19fb50961b";
        Cache.loadCache();
        aList = Cache.getImmediateRelatives("Sheila_Parker");
        assert(aList.get(0).second.contains("Mother"));
        assert(aList.get(0).second.contains("Father"));
        assert(aList.get(0).second.contains("Spouse"));
    }

    @Test
    public void testFamilyRelationshipsFail(){
        try{
            ArrayList<Pair<String, String>> aList = new ArrayList<Pair<String, String>>();
            Cache.people = new HashMap<String, Person>();
            Cache.events = new HashMap<String, Event>();
            Proxy.setIP("10.0.2.2");
            Proxy.setPort("8080");
            Cache.authToken = "0e1240d3-ae08-441d-b96f-3b19fb50961b";
            Cache.loadCache();
            aList = Cache.getImmediateRelatives("Bob");
        }catch(NullPointerException e){
            assert (e != null);
        }
    }

    @Test
    public void testOrder(){
        Cache.people = new HashMap<String, Person>();
        Cache.events = new HashMap<String, Event>();
        Proxy.setIP("10.0.2.2");
        Proxy.setPort("8080");
        Cache.authToken = "0e1240d3-ae08-441d-b96f-3b19fb50961b";
        Cache.loadCache();
        Person tempP = Cache.getPerson("Sheila_Parker");
        assert(tempP.getSortedEvents().get(0).getYear() < tempP.getSortedEvents().get(1).getYear());

    }

    @Test
    public void testOrderFail(){
        Cache.people = new HashMap<String, Person>();
        Cache.events = new HashMap<String, Event>();
        Proxy.setIP("10.0.2.2");
        Proxy.setPort("8080");
        Cache.authToken = "0e1240d3-ae08-441d-b96f-3b19fb50961b";
        Cache.loadCache();
        Person tempP = Cache.getPerson("Sheila_Parker");
        assert(!(tempP.getSortedEvents().get(0).getYear() > tempP.getSortedEvents().get(1).getYear()));
    }
}
