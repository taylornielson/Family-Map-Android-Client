package com.example.familymap.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymap.Client.Cache;
import com.example.familymap.MainActivity;
import com.example.familymap.Models.Person;
import com.example.familymap.R;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Model.Event;

public class SearchActivity extends AppCompatActivity {
    private static final int EVENT_GROUP_POSITION = 1;
    private static final int RELATIVE_GROUP_POSITION = 0;
    private static final String tag="SearchActivity";
    public String searchString;
    EditText searchValue;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchString = "";
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(new IconDrawable(this, FontAwesomeIcons.fa_arrow_left).color(Color.LTGRAY).sizeDp(20));
        actionBar.setTitle("Family Map: Search");
        searchValue = (EditText) SearchActivity.this.findViewById(R.id.searchField);
        Log.e(tag, searchValue.toString());
        recyclerView = findViewById(R.id.searchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        List<Person> people = new ArrayList<Person>();
        List<Event> events = new ArrayList<Event>();
        searchValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               executeSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        //people.add(Cache.getPerson("Sheila_Parker"));
        //events.add(Cache.getEvent("Sheila_Birth"));
        SearchAdapter adapter = new SearchAdapter(people, events);
        recyclerView.setAdapter(adapter);
    }



    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Person> people;
        private final List<Event> events;

        SearchAdapter(List<Person> people, List<Event> events) {
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < people.size() ? RELATIVE_GROUP_POSITION : EVENT_GROUP_POSITION;
        }


        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if(viewType == RELATIVE_GROUP_POSITION) {
                view = getLayoutInflater().inflate(R.layout.relative_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_item, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < people.size()) {
                holder.bind(people.get(position));
            } else {
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView image;
        private final TextView name;
        private final TextView location;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == RELATIVE_GROUP_POSITION) {
                image = itemView.findViewById(R.id.listImage);
                name = itemView.findViewById(R.id.relativeName);
                location = null;
            } else {
                image = itemView.findViewById(R.id.listImage);
                name = itemView.findViewById(R.id.eventTitle);
                location = itemView.findViewById(R.id.eventPersonName);
            }
        }

        private void bind(Person person) {
            this.person = person;
            String gender = person.getGender();
            if (gender.equals("m")){
                image.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).color(Color.BLUE));
            }
            else{
                image.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).color(Color.rgb(255,192,203)));
            }
            String personName = person.getFirstName() + " " + person.getLastName();
            name.setText(personName);
        }

        private void bind(Event event) {
            this.event = event;
            image.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).color(Color.RED));
            name.setText(event.getEventType() + ": " + event.getCity() + ", " + event.getCountry() + " (" + Integer.toString(event.getYear()) + ")");
            location.setText(Cache.getPerson(event.getPersonID()).getFirstName() + " " + Cache.getPerson(event.getPersonID()).getLastName());
        }

        @Override
        public void onClick(View view) {
            if(viewType == RELATIVE_GROUP_POSITION) {
                startPerson(SearchActivity.this, person.getPersonID());

                //Toast.makeText(SearchActivity.this, String.format("Hello %s!",
                  //      person.getFirstName()), Toast.LENGTH_SHORT).show();
            } else {
                startEvent(SearchActivity.this, event.getEventID());

                //Toast.makeText(SearchActivity.this, String.format("Wow you %s.",
                  //      event.getEventType()), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startPerson(Activity activity, String currentPersonID)
    {
        Log.e(tag, "Starting person activity");
        Intent intent = new Intent(activity, PersonActivity.class);
        intent.putExtra("personID", currentPersonID);
        startActivity(intent);
    }

    private void startEvent(Activity activity, String currentEventID){
        Intent intent = new Intent(activity, EventActivity.class);
        intent.putExtra("eventID", currentEventID);
        startActivity(intent);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("map", true);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        return true;
    }

    public void executeSearch()
    {
        searchString = searchValue.getText().toString();
        //clearResults();
        if (searchString.length() <= 0) return;

        ArrayList<Person> peopleResults = new ArrayList<Person>();
        ArrayList<Event> eventsResults = new ArrayList<Event>();

        for (Person p : Cache.people.values())
        {
            if (p.containsString(searchString))
            {
                peopleResults.add(p);
            }
        }

        for (Event e : Cache.events.values())
        {
            if (e.containsString(searchString))
            {
                eventsResults.add(e);
            }
        }
        if (eventsResults.size() > 0){
            for (int i = 0; i < eventsResults.size(); ++i){
                if (!eventsResults.get(i).getIsVisible()){
                    eventsResults.remove(i);
                    --i;
                }
            }
        }
        SearchAdapter adapter = new SearchAdapter(peopleResults, eventsResults);
        recyclerView.setAdapter(adapter);
    }


}
