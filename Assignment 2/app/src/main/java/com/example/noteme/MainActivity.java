package com.example.noteme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity implements Adapter.OnDeleteListener, Adapter.OnEditListener {

    // UI components
    private Toolbar tbar;
    private RecyclerView recyclerView;
    private SearchView searchView;

    // Adapter for RecyclerView and list to hold notes data
    private Adapter adapter;
    private List<Coordinate> coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Comment this line out to no longer insert coordinates from file
        readFromFileIntoDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbar = findViewById(R.id.tbar);
        searchView = findViewById(R.id.searchBar);

        setSupportActionBar(tbar);

        CoordinateDatabase db = new CoordinateDatabase(this);
        coordinates = db.getNotes();

        recyclerView = findViewById(R.id.listOfNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, coordinates, this::onDelete, this::onEdit);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options for the activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Intent i = new Intent(this, AddCoordinate.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    // Filter the notes
    private void filter(String query) {
        List<Coordinate> filteredCoordinates = new ArrayList<>();

        for (Coordinate coordinate : coordinates) {
            if (coordinate.getAddress().toLowerCase().contains(query.toLowerCase())) {
                filteredCoordinates.add(coordinate);
            }
        }

        adapter.updateNotes(filteredCoordinates);
    }

    @Override
    public void onDelete(Coordinate coordinate) {

        // Callback method to handle note deletion

        CoordinateDatabase db = new CoordinateDatabase(this);
        db.deleteNote(coordinate);

        coordinates = db.getNotes();
        adapter.updateNotes(coordinates);
    }

    @Override
    public void onEdit(Coordinate coordinate) {

        Intent editIntent = new Intent(this, AddCoordinate.class);
        editIntent.putExtra("changeNote", coordinate);
        startActivity(editIntent);
    }

    public void readFromFileIntoDatabase() {

        CoordinateDatabase db = new CoordinateDatabase(this);
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.coordinate_50)));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    float latitude = Float.parseFloat(parts[0].trim());
                    float longitude = Float.parseFloat(parts[1].trim());
                    Coordinate c = new Coordinate(latitude, longitude);
                    c.updateAddress(this);

                    // Add that to db.
                    db.addNote(c);

                }
            }
            br.close();
        } catch (IOException e) {
            // Handle exceptions
        }

        db.close();
    }
}

