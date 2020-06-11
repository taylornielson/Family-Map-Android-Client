package com.example.familymap.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListAdapter;
import android.widget.Toast;

import com.example.familymap.Client.Cache;
import com.example.familymap.MainActivity;
import com.example.familymap.Models.Person;
import com.example.familymap.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import Model.Event;

public class PersonActivity extends AppCompatActivity {
    public static final String tag ="Person Activity";
    private String personId;
    private Person person;
    TextView firstName;
    TextView lastName;
    TextView gender;
    String firstNameString;
    String lastNameString;
    Drawable pin;
    Drawable genderPin;
    ArrayList<Event> events;
    ArrayList<Pair<String, String>> relatives;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(new IconDrawable(this, FontAwesomeIcons.fa_arrow_left).color(Color.LTGRAY).sizeDp(20));
        actionBar.setTitle("Family Map: Person Details");
        if (savedInstanceState == null){
            Intent intent = getIntent();
            if (intent.hasExtra("personID")) {
                setPersonId(intent.getStringExtra("personID"));
            }
            else {Log.e(tag, "No extra value"); return;}
        }
        else{
            setPersonId(savedInstanceState.getString("personID"));
        }
        Log.e(tag, personId);
        firstNameString = Cache.getPerson(personId).getFirstName();
        lastNameString = Cache.getPerson(personId).getLastName();
        firstName = (TextView) findViewById(R.id.firstName);
        lastName = (TextView) findViewById(R.id.lastName);
        gender = (TextView) findViewById(R.id.gender);
        fillView();
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        return true;
    }

    public void setPersonId(String id)
    {
        this.personId = id;
        this.person = Cache.getPerson(id);
    }

    private void fillView(){
        firstName.setText(this.person.getFirstName());
        lastName.setText(this.person.getLastName());
        gender.setText(this.person.getGender().toUpperCase());
        this.events = this.person.getSortedEvents();
        this.relatives = Cache.getImmediateRelatives(personId);
        ExpandableListView expandableListView = findViewById(R.id.ELV);
        if (!this.person.getVisible()){
            events.clear();
        }
        expandableListView.setAdapter(new ExpandableListAdapter(events, relatives));
    }

    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("personId", this.personId);
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENT_GROUP_POSITION = 0;
        private static final int RELATIVE_GROUP_POSITION = 1;

        private final List<Event> events;
        private final List<Pair<String,String>> relatives;

        ExpandableListAdapter(List<Event> events, List<Pair<String,String>> relatives) {
            this.events = events;
            this.relatives = relatives;

        }
        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.size();
                case RELATIVE_GROUP_POSITION:
                    return relatives.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return (R.string.eventsTitle);
                case RELATIVE_GROUP_POSITION:
                    return (R.string.relativesTitle);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.get(childPosition);
                case RELATIVE_GROUP_POSITION:
                    return relatives.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText(R.string.eventsTitle);
                    break;
                case RELATIVE_GROUP_POSITION:
                    titleView.setText(R.string.relativesTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case RELATIVE_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.relative_item, parent, false);
                    initializeRelativeView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }


        private void initializeEventView(View eventView, final int childPosition) {
            ImageView eventPin = eventView.findViewById(R.id.listImage);
            eventPin.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).color(Color.GREEN));
            TextView event = eventView.findViewById(R.id.eventTitle);
            Event tempEvent = events.get(childPosition);
            event.setText(tempEvent.getEventType() + ": " + tempEvent.getCity() + ", " + tempEvent.getCountry() + " (" + Integer.toString(tempEvent.getYear()) + ")");
            TextView eventPersonName = eventView.findViewById(R.id.eventPersonName);
            eventPersonName.setText(firstNameString + " " + lastNameString);

            eventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEvent(childPosition);
                    // Toast.makeText(PersonActivity.this, getString(R.string.eventToastText, events.get(childPosition).getEventType()), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void initializeRelativeView(View relativeView, final int childPosition) {
            ImageView relativePin = relativeView.findViewById(R.id.listImage);
            final Person relative = Cache.getPerson(relatives.get(childPosition).first);
            if (relative.getGender().equals("m")){
                relativePin.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).color(Color.BLUE));
            }
            else
            {
                Log.e(tag, "Entering the death zone");
                relativePin.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).color(Color.rgb(255,192,203)));
            }
            TextView relativeName = relativeView.findViewById(R.id.relativeName);
            String relativeFirstName = relative.getFirstName();
            String relativeLastName = relative.getLastName();
            relativeName.setText(relativeFirstName + " " + relativeLastName);



            TextView relativeType = relativeView.findViewById(R.id.relativeType);
            relativeType.setText(relatives.get(childPosition).second);

           relativeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startPerson(PersonActivity.this, relative.getPersonID());


                   // Toast.makeText(PersonActivity.this, getString(R.string.relativeToastText, relatives.get(childPosition).second), Toast.LENGTH_SHORT).show();
                }
            });


        }
        private void startPerson(Activity activity, String currentPersonID)
        {
            Log.e(tag, "Starting person activity");
            Intent intent = new Intent(activity, PersonActivity.class);
            intent.putExtra("personID", currentPersonID);
            startActivity(intent);
        }

        private void startEvent(int childPosition){
            Intent intent = new Intent(getApplicationContext(), EventActivity.class);
            intent.putExtra("eventID", events.get(childPosition).getEventID());
            startActivity(intent);
        }



        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
