package com.example.familymap.Client;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.example.familymap.MainActivity;
import com.example.familymap.Models.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import Model.Event;

public class Cache {
    public static String authToken;
    public static String userName;
    public static String personID;
    public static String firstName;
    public static String lastName;
    private static String tag = "Cache";
    public static HashMap<String, com.example.familymap.Models.Person> people;
    public static HashMap<String, Event> events;
    public static TreeSet<String> eventType = new TreeSet<String>();
    private static Cache instance;
    public static Boolean changed = false;


    public static Cache getInstance() {
        if(instance == null) {
            instance = new Cache();
        }
        return instance;
    }
    private Cache() {
        authToken = "";
        userName = "";
        personID = "";
        firstName = "";
        lastName = "";
        people = new HashMap<>();
        events = new HashMap<String, Event>();
    }
        public static Person getPerson (String id)
        {
            if (!people.containsKey(id)) return null;
            return people.get(id);
        }

        public static void clear ()
        {
            authToken = "";
            userName = "";
            personID = "";
            people.clear();
            events.clear();
        }

    public static String getFirstName() {
        if (!people.containsKey(personID)) return null;
        return people.get(personID).getFirstName();
    }
    public static String getLastName(){
        if (!people.containsKey(personID)) return null;
        return people.get(personID).getLastName();
    }
    public static void setVisibles(Boolean male, Boolean female, Boolean mother, Boolean father){
        for (Person p: people.values()) {
            Boolean visible = true;
            if (p.getGender().equals("m")) {
                if (!male) {
                    visible = false;
                } else if (p.isOnFatherSide() && !father) {
                    visible = false;
                } else if (p.isOnMotherSide() && !mother) {
                    visible = false;
                }
            } else {
                if (!female) {
                    visible = false;
                } else if (p.isOnFatherSide() && !father) {
                    visible = false;
                } else if (p.isOnMotherSide() && !mother) {
                    visible = false;
                }
            }
            p.setVisible(visible);
        }
        for (Event e: events.values()){
            Boolean visible = true;
            if(!(getPerson(e.getPersonID()).isVisible())){
                visible = false;
            }
            e.setIsVisible(visible);
        }
    }

    public static Boolean loadCache () {
            try {
                people.clear();
                events.clear();
                loadPeople();
                loadEvents();
                loadEventTypes();
                computeFamilySides();
                changed = false;
                return true;
            } catch (Exception e) {
                Log.e(tag, "loading Error: " + e.getMessage());
                return false;
            }
        }


    public static void loadPeople() throws JSONException
        {
            Log.e(tag, "starting to load");
            JSONObject result = Proxy.doAuthenticationAction("person", authToken);
            //JSONObject result = Proxy.doAuthenticationAction("person", authToken);
            Log.e(tag, "authenticated person stuff");
            JSONArray data = result.getJSONArray("data");
            Log.e(tag, "About to enter for loop");
            for (int i = 0; i < data.length(); i++) {
                JSONObject person_obj = data.getJSONObject(i);
                String id = person_obj.getString("personID");
                String motherId = JSONGetErrorChecking(person_obj, "motherID");
                String fatherId = JSONGetErrorChecking(person_obj, "fatherID");
                com.example.familymap.Models.Person person = new com.example.familymap.Models.Person();
                person.setFirstName(person_obj.getString("firstName"));
                person.setLastName(person_obj.getString("lastName"));
                person.setSpouseID(JSONGetErrorChecking(person_obj, "spouseID"));
                person.setGender(person_obj.getString("gender"));
                person.setFatherID(fatherId);
                person.setMotherID(motherId);
                person.setPersonID(id);
                Log.e(tag, "right before loading a person");
                addPerson(id, person);

            }
            Log.e(tag, "Loaded People");
        }

        public static void loadEvents () throws JSONException
        {
            Log.e(tag, "Starting to load events");
            JSONObject result = Proxy.doAuthenticationAction("event", authToken);
            //JSONObject result = Proxy.doAuthenticationAction("event", authToken);
            Log.e(tag, "Made it past authentication");
            JSONArray data = result.getJSONArray("data");
            Log.e(tag,data.toString());
            Log.e(tag, "starting event loop");
            for (int i = 0; i < data.length(); i++) {
                JSONObject event_obj = data.getJSONObject(i);
                Event event = new Event();
                Log.e(tag, "about to start adding events to event model");
                event.setCity(event_obj.getString("city"));
                event.setCountry(event_obj.getString("country"));
                event.setLatitude(event_obj.getDouble("latitude"));
                event.setLongitude(event_obj.getDouble("longitude"));
                event.setYear(event_obj.getInt("year"));
                event.setEventType(event_obj.getString("eventType"));
                String eventId = event_obj.getString("eventID");
                String personId = event_obj.getString("personID");
                String associatedUsername = event_obj.getString("associatedUsername");
                event.setPersonID(personId);
                event.setEventID(eventId);
                event.setUserName(associatedUsername);
                Log.e(tag, "Calling add event at end of loop");
                addEvent(personId, eventId, event);
            }
        }

    public static void setChanged(Boolean changed) {
        Cache.changed = changed;
    }

    public static String JSONGetErrorChecking (JSONObject obj, String property)
        {
            try {
                return (obj.has(property)) ? obj.getString(property) : "";
            } catch (JSONException e) {
                return "";
            }
        }

        public static void addPerson (String id, com.example.familymap.Models.Person person)
        {
            people.put(id, person);
        }


        public static void addEvent (String personId, String eventID, Event event)
        {
            if (people.containsKey(personId)) {
                Log.e(tag,people.get(personId).toString());
                people.get(personId).addEvent(eventID);
                events.put(eventID, event);
            }
        }

        public static void computeFamilySides(){
            Person person = getPerson(personID);
            if (person == null)
            {
                Log.e(tag, "Uh-Oh, the current user is null!");
                return;
            }
            recSetFatherSide(person.getFather());
            recSetMotherSide(person.getMother());
        }

    private static void recSetFatherSide(Person person)
    {
        if (person == null) return;
        person.setOnFatherSide(true);
        recSetFatherSide(person.getMother());
        recSetFatherSide(person.getFather());
        return;
    }

    private static void recSetMotherSide(Person person)
    {
        if (person == null) return;
        person.setOnMotherSide(true);
        recSetMotherSide(person.getMother());
        recSetMotherSide(person.getFather());
        return;

    }


        public static Event getEvent(String eventID){
            if(!events.containsKey(eventID)) return null;
            return events.get(eventID);
        }
        private static void loadEventTypes() {
            eventType.clear();
            for (Event e: events.values()){
                String eventTypeString = e.getEventType().toLowerCase();
                Log.e(tag, eventTypeString);
                eventType.add(eventTypeString);
            }
            Log.e(tag, eventType.toString());
        }

    public static ArrayList<Pair<String, String>> getImmediateRelatives(String personId)
    {
        ArrayList<Pair<String, String>> relatives = new ArrayList<Pair<String, String>>();

        Person p = getPerson(personId);
        Log.e(tag, personId);
        String motherId = p.getMotherID();
        Log.e(tag,motherId);
        if (personExists(motherId)) relatives.add(new Pair<String, String>(motherId, "Mother"));

        String fatherId = p.getFatherID();
        if (personExists(fatherId)) relatives.add(new Pair<String, String>(fatherId, "Father"));

        String spouseId = p.getSpouseID();
        if (personExists(spouseId)) relatives.add(new Pair<String, String>(spouseId, "Spouse"));

        for (String childId : people.keySet())
        {
            Person child = getPerson(childId);
            if (child.getMotherID().equals(personId) || child.getFatherID().equals(personId) ) {
                relatives.add(new Pair<String, String>(childId, "Child"));
            }

        }
        return relatives;
    }
    public static boolean personExists(String personId)
    {
        if (personId == null) return false;
        return people.containsKey(personId);
    }
}



