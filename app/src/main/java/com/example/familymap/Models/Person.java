package com.example.familymap.Models;

import com.example.familymap.Client.Cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import Model.Event;

public class Person extends Model.Person {
    private HashSet<String> events;
    private boolean onMotherSide = false;
    private boolean onFatherSide = false;
    private boolean isVisible = true;

    public Person(){
        super();
        events = new HashSet<String>();
    }

    public void addEvent(String id)
    {
        events.add(id);
    }

    public ArrayList<Event> getSortedEvents()
    {
        ArrayList<Event> result = new ArrayList<Event>();
        for (String id: events)
        {
            result.add(Cache.getEvent(id));
        }
        Collections.sort(result);
        return result;
    }

    public boolean isOnFatherSide() {
        return onFatherSide;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean getVisible(){
        return this.isVisible;
    }

    public boolean isOnMotherSide() {
        return onMotherSide;
    }

    public void setOnMotherSide(boolean onMotherSide) {
        this.onMotherSide = onMotherSide;
    }

    public boolean onFatherSide() {
        return onFatherSide;
    }

    public void setOnFatherSide(boolean onFatherSide) {
        this.onFatherSide = onFatherSide;
    }

    public boolean containsString(String search)
    {
        search = search.toLowerCase();
        return (getFirstName().toLowerCase().contains(search) || getLastName().toLowerCase().contains(search));
    }
    public Person getMother()
    {
        return Cache.getPerson(getMotherID());
    }
    public Event getEarliestEvent()
    {
        ArrayList<Event> sorted = getSortedEvents();
        return (sorted.size() > 0) ? sorted.get(0) : null;
    }

    public Person getFather()
    {
        return Cache.getPerson(getFatherID());
    }

}
