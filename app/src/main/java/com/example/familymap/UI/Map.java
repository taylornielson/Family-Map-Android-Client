package com.example.familymap.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.familymap.Client.Cache;
import com.example.familymap.Models.Person;
import com.example.familymap.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import Model.Event;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.YELLOW;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;


public class Map extends Fragment implements OnMapReadyCallback {
    GoogleMap gMap;
    Boolean eventActivity = false;
    private View view;
    private TextView textView;
    private ImageView gender_image_view;
    private MapView mapView;
    private String currentPersonID;
    private String eventID;
    SharedPreferences sharedPreferences;
    private ArrayList<Polygon> lifeStory;
    private Polygon spouseLine;
    private ArrayList<Polygon> familyHistory;
    private HashMap<String, Float> pinColors;
    String tag = "familymapfragment";
    public static final float DEFAULT_MARKER_COLOR = HUE_AZURE;
    public static final int LIFE_STORY_WIDTH = 10;
    public static final int LIFE_STORY_COLOR = GREEN;

    public static final int SPOUSE_LINE_WIDTH = 10;
    public static final int SPOUSE_LINE_COLOR = BLUE;
    public static final int FAMILY_HISTORY_WIDTH = 35;
    public static final int FAMILY_HISTORY_COLOR = YELLOW;

    public Map() {
        lifeStory = new ArrayList<Polygon>();
        familyHistory = new ArrayList<Polygon>();
        spouseLine = null;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        textView = (TextView) view.findViewById(R.id.text);
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        Drawable andIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).color(Color.GREEN);
        gender_image_view = (ImageView)view.findViewById(R.id.map_gender_image);
        gender_image_view.setImageDrawable(andIcon);
        setHasOptionsMenu(true);
        setPersonEvent();
        if (eventActivity){
            Drawable femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).color(Color.rgb(255,192,203));
            Drawable maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).color(BLUE);
            currentPersonID = Cache.getEvent(eventID).getPersonID();
            String eventType = Cache.getEvent(eventID).getEventType();
            String city = Cache.getEvent(eventID).getCity();
            String country = Cache.getEvent(eventID).getCountry();
            int year = Cache.getEvent(eventID).getYear();
            String firstName = Cache.getPerson(Cache.getEvent(eventID).getPersonID()).getFirstName();
            String lastName = Cache.getPerson(Cache.getEvent(eventID).getPersonID()).getLastName();
            String gender = Cache.getPerson(Cache.getEvent(eventID).getPersonID()).getGender();
            //updateLines();
            if (gender.equals("f")){
                gender_image_view.setImageDrawable(femaleIcon);
            }
            else{
                gender_image_view.setImageDrawable(maleIcon);
            }
            textView.setText(firstName + " " + lastName + "\n" + eventType + ": " + city + ", " + country + " (" + Integer.toString(year) + ")");
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                initMap();
            }
        });

        return view;
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Log.e(tag, "calling create options menu in fragment");
        if (!eventActivity){
            inflater.inflate(R.menu.action_menu, menu);
            ;
            setMenuItem(menu, R.id.action_settings, FontAwesomeIcons.fa_gear);
            setMenuItem(menu, R.id.action_search, FontAwesomeIcons.fa_search);
        }
        else
        {
            /*
            inflater.inflate(R.menu.back_menu, menu);
            setMenuItem(menu, R.id.go_to_top, Iconify.IconValue.fa_angle_double_up);

             */
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if(!eventActivity) {
            if (Cache.changed) {
                refreshPageAfterFilter();
            }
        }
    }

    private void refreshPageAfterFilter(){
        Cache.setVisibles(sharedPreferences.getBoolean("MaleEvents", true), sharedPreferences.getBoolean("FemaleEvents", true), sharedPreferences.getBoolean("MotherSide", true), sharedPreferences.getBoolean("FatherSide", true));
        gMap.clear();
        addMarkers();
        drawLines();
    }

    private void clearMarkers(GoogleMap googleMap){
        googleMap.clear();
    }

    void initMap() {

        setMapType();
        Cache.setVisibles(sharedPreferences.getBoolean("MaleEvents", true), sharedPreferences.getBoolean("FemaleEvents", true), sharedPreferences.getBoolean("MotherSide", true), sharedPreferences.getBoolean("FatherSide", true));
        setClickListener();
        chooseMarkerColors();
        addMarkers();
        //setBounds();
        setMarkerListener();
        drawLines();
        centerMap();
        zoomMap(2);

    }
    private void setMenuItem(Menu menu, int id, FontAwesomeIcons value)
    {

        MenuItem item = menu.findItem(id);
        if (item == null)
        {
            Log.e(tag, "menu item is null");
        }
        else item.setIcon(new IconDrawable(getActivity().getApplicationContext(), value).color(Color.LTGRAY).sizeDp(40));
    }

    void centerMap() {
        CameraUpdate update;
        if (eventActivity){
            Double lat = Cache.getEvent(eventID).getLatitude();
            Double lon = Cache.getEvent(eventID).getLongitude();
            LatLng event = new LatLng(lat, lon);
            update = CameraUpdateFactory.newLatLng(event);
        }
        else {
            LatLng byu = new LatLng(40.2518, -111.6493);
            update = CameraUpdateFactory.newLatLng(byu);
        }
        gMap.moveCamera(update);
    }

    void zoomMap(float amount) {
        CameraUpdate update = CameraUpdateFactory.zoomTo(amount);
        gMap.moveCamera(update);
    }

    //    static final int mapType = MAP_TYPE_NORMAL;
    static final int mapType = MAP_TYPE_TERRAIN;

    void setMapType() {
        gMap.setMapType(mapType);
    }


    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        Intent intent;
        if (id == android.R.id.home){
            getActivity().finish();
        }
        else if (id == R.id.action_search){
            intent = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_settings){
            intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    void setPersonEvent(){
        gender_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(tag, "Clicked person starter");
                startPerson();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(tag, "Clicked person starter");
                startPerson();
            }
        });

    }

    private void startPerson()
    {
        Log.e(tag, "Starting person activity");
        Intent intent = new Intent(getActivity(), PersonActivity.class);
        intent.putExtra("personID", currentPersonID);
        startActivity(intent);
    }
    void setClickListener() {
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                textView.setText(latLng.toString());
            }
        });
    }

    LatLng getLatLng(String[] strings) {
        return new LatLng(Double.valueOf(strings[1]), Double.valueOf(strings[2]));
    }

    void addMarkers() {
        for (String eventID : Cache.events.keySet()) {
            addMarker(eventID);
        }
    }


    void addMarker(String eventID) {
        Boolean maleEventsAllowed = sharedPreferences.getBoolean("MaleEvents", true);
        Boolean femaleEventsAllowed = sharedPreferences.getBoolean("FemaleEvents", true);
        Event e = Cache.getEvent(eventID);
        if (!maleEventsAllowed){
            if(Cache.getPerson(e.getPersonID()).getGender().equals("m")){
                return;
            }
        }
        if(!femaleEventsAllowed){
            if(Cache.getPerson(e.getPersonID()).getGender().equals("f")){
                return;
            }
        }
        if (e != null) {
            LatLng position = new LatLng(e.getLatitude(), e.getLongitude());
            String eventType = e.getEventType().toLowerCase();
            Log.e(tag, e.getEventType());
            float pinColor = (pinColors.containsKey(eventType)) ? pinColors.get(eventType.toLowerCase()) : DEFAULT_MARKER_COLOR;
            gMap.addMarker(new MarkerOptions()
                    .position(position)
                    .snippet(eventID)
                    .icon(BitmapDescriptorFactory.defaultMarker(pinColor)));
        }
    }


    void setMarkerListener() {
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                Drawable femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).color(Color.rgb(255,192,203));
                Drawable maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).color(BLUE);
                eventID = marker.getSnippet();
                currentPersonID = Cache.getEvent(marker.getSnippet()).getPersonID();
                String eventType = Cache.getEvent(eventID).getEventType();
                String city = Cache.getEvent(eventID).getCity();
                String country = Cache.getEvent(eventID).getCountry();
                int year = Cache.getEvent(eventID).getYear();
                String firstName = Cache.getPerson(Cache.getEvent(eventID).getPersonID()).getFirstName();
                String lastName = Cache.getPerson(Cache.getEvent(eventID).getPersonID()).getLastName();
                String gender = Cache.getPerson(Cache.getEvent(eventID).getPersonID()).getGender();
                if (gender.equals("f")){
                    gender_image_view.setImageDrawable(femaleIcon);
                }
                else{
                    gender_image_view.setImageDrawable(maleIcon);
                }
                textView.setText(firstName + " " + lastName + "\n" + eventType + ": " + city + ", " + country + " (" + Integer.toString(year) + ")");
                drawLines();
                return false;
            }
        });
    }

    void drawLines() {
        if (sharedPreferences.getBoolean("Spouse", true)){
            drawSpouseLine();
        }
        else{
            clearSpouseLine();
        }
        if(sharedPreferences.getBoolean("LifeStory", true)){
            drawLifeStoryLines();
        }
        else{
            for (Polygon pol : lifeStory) {
                pol.remove();
            }
        }
        if(sharedPreferences.getBoolean("FamilyTree", true)){
            for (int i = 0; i < familyHistory.size(); i++)
            {
                familyHistory.get(i).remove();
            }
            drawFamilyHistoryLines(Cache.getEvent(eventID), FAMILY_HISTORY_WIDTH);
        }
        else{
            for (int i = 0; i < familyHistory.size(); i++)
            {
                familyHistory.get(i).remove();
            }
        }
    }

    private void drawSpouseLine() {
        Person spouse;
        Person p;
        try {
            spouse = Cache.getPerson(Cache.getPerson(Cache.getEvent(eventID).getPersonID()).getSpouseID());
            p = Cache.getPerson(Cache.getEvent(eventID).getPersonID());
        }catch(NullPointerException e){
            Log.e(tag, "null");
            return;
        }
        if(!spouse.getVisible() || !p.getVisible()) {return;}
        clearSpouseLine();
        Log.e(tag, spouse.getFirstName());
        Log.e(tag, Cache.getEvent(eventID).getEventType());
        Log.e(tag, Cache.getEvent(eventID).getCity());
        Double lat = Cache.getEvent(eventID).getLatitude();
        Double lon = Cache.getEvent(eventID).getLongitude();
        LatLng origin = new LatLng(lat,lon);
        lat = spouse.getEarliestEvent().getLatitude();
        lon = spouse.getEarliestEvent().getLongitude();
        Log.e(tag, spouse.getEarliestEvent().getCity());
        LatLng spouseCoords = new LatLng(lat, lon);
        spouseLine = drawLine(origin, spouseCoords,SPOUSE_LINE_WIDTH,SPOUSE_LINE_COLOR) ;
    }

    private void drawLifeStoryLines(){
        Person p;
        for (Polygon pol : lifeStory) {
                pol.remove();
            }
        try {
            p = Cache.getPerson(Cache.getEvent(eventID).getPersonID());
        }catch(NullPointerException e){
            return;
        }
            if(!p.getVisible()){return;}
        ArrayList<Event> sorted = p.getSortedEvents();
        for(int i = 0; i < sorted.size()-1; ++i){
            Double lat = sorted.get(i).getLatitude();
            Double lon = sorted.get(i).getLongitude();
            LatLng first = new LatLng(lat,lon);
            lat = sorted.get(i+1).getLatitude();
            lon = sorted.get(i+1).getLongitude();
            LatLng second = new LatLng(lat,lon);
            lifeStory.add(drawLine(first,second, LIFE_STORY_WIDTH, LIFE_STORY_COLOR));
        }
    }

    private void drawFamilyHistoryLines(Event event, int width) {
        Log.e(tag, "Entering Family History Lines");
        Person p;
        try {
            p = Cache.getPerson(Cache.getEvent(event.getEventID()).getPersonID());
        }catch(NullPointerException e){
            return;
        }
        Log.e(tag,"Family History visibility: " + p.getVisible());
        Log.e(tag,"Family History visibility: " + p.isOnMotherSide());
        Log.e(tag,"Family History visibility: " + p.isOnFatherSide());
        if (!p.getVisible()){return;}
        Person mother;
        Person father;
        try {
            mother = Cache.getPerson(p.getMotherID());
        } catch (NullPointerException e) {
            mother = null;
        }
        try {
            father = Cache.getPerson(p.getFatherID());
        } catch (NullPointerException e) {
            father = null;
        }
        if (mother != null && mother.getVisible()) {
            Log.e(tag, "Entering MotherLines");
            Event firstMotherEvent = mother.getEarliestEvent();
            drawFamilyStoryLine(event, firstMotherEvent, width);
            drawFamilyHistoryLines(firstMotherEvent, (width - 10));
        }

        if (father != null && father.getVisible()) {
            Log.e(tag, "Entering FatherLines");
            Event firstFatherEvent = father.getEarliestEvent();
            drawFamilyStoryLine(event, firstFatherEvent, width);
            drawFamilyHistoryLines(firstFatherEvent, (width - 10));
        }
        return;
    }


    private void drawFamilyStoryLine(Event first, Event second, int width) {
        if (first == null || second == null) return;
        Log.e(tag, first.getCity());
        Log.e(tag, second.getCity());
        LatLng source = new LatLng(first.getLatitude(), first.getLongitude());
        LatLng next = new LatLng(second.getLatitude(), second.getLongitude());
        familyHistory.add(drawLine(source, next, width, FAMILY_HISTORY_COLOR));
    }


    private void clearSpouseLine(){
        if (spouseLine != null){
            spouseLine.remove();
        }
    }

    static final float WIDTH = 10;  // in pixels
    static final int color = BLUE;
//    static final int color = BLACK;

    private Polygon drawLine(LatLng point1, LatLng point2, int width, int color) {
        zoomMap(1);
        PolygonOptions options =
                new PolygonOptions().add(point1, point2)
                        .strokeWidth(width)
                        .strokeColor(color);
        return gMap.addPolygon(options);
    }
    private void chooseMarkerColors()
    {
        pinColors = new HashMap<String, Float>();
        int num_colors = Cache.eventType.size();
        Iterator<String> it = Cache.eventType.iterator();
        int i = 0;
        float color_increment = ((float)360) / (num_colors+1);
        while (it.hasNext()) {
            i++;
            pinColors.put(it.next(), i * color_increment);
        }
    }

    public void setEventActivity(Boolean b){
        eventActivity = b;
    }
    public void setEventID(String s){
        this.eventID = s;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }
}


