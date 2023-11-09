package com.example.noteme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddCoordinate extends AppCompatActivity {

    // UI components
    private Toolbar tbar;
    private Coordinate changeCoordinate;
    private EditText txtLat, txtLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Set up the tbar
        tbar = findViewById(R.id.tbar);
        setSupportActionBar(tbar);
        getSupportActionBar().setTitle("New Coordinate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Connect UI elements to their respective views
        txtLat = findViewById(R.id.latitude);
        txtLon = findViewById(R.id.longitude);




        if(getIntent().hasExtra("changeNote")){
            changeCoordinate =(Coordinate) getIntent().getSerializableExtra("changeNote");
            setEditMode();
        } else{
        }

    }
    private void setEditMode() {

        txtLat.setText("" + changeCoordinate.getLatitude() + "");
        txtLon.setText("" + changeCoordinate.getLongitude() + "");

        getSupportActionBar().setTitle(changeCoordinate.getAddress());

        // Enable editing for latitude and description
        txtLat.setEnabled(true);
        txtLon.setEnabled(true);

        isEditMode = true;
    }

    // Pad single digit numbers with a leading zero
    private String pad(int i) {
        if (i < 10) {
            return "0" + i;
        }
        return String.valueOf(i);
    }

    // Inflate menu options for this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    private boolean isEditMode = false;

    // Handle actions when a menu item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            if(isEditMode){
                deleteExistingNote();
            }else {
                Toast.makeText(this, "Note Was Not Saved.", Toast.LENGTH_SHORT).show();
                goToMain();
            }
        } else if (item.getItemId() == R.id.save) {
            if(isEditMode){
                updateExistingNote();
            }else{
                saveNewNote();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateExistingNote(){
        if(changeCoordinate !=null){

            changeCoordinate.setLatitude(Float.parseFloat(txtLat.getText().toString()));
            changeCoordinate.setLongitude(Float.parseFloat(txtLon.getText().toString()));
            changeCoordinate.updateAddress(this);

            CoordinateDatabase db = new CoordinateDatabase(this);
            db.updateNote(changeCoordinate);
            isEditMode = false;

            Toast.makeText(this, "Note Has Been Updated.", Toast.LENGTH_SHORT).show();

            goToMain();
        }
    }

    private void deleteExistingNote() {
        if (changeCoordinate != null) {
            // Delete the note from database
            CoordinateDatabase db = new CoordinateDatabase(this);
            db.deleteNote(changeCoordinate);

            isEditMode = false;

            Toast.makeText(this, "Note Has Been Deleted.", Toast.LENGTH_SHORT).show();

            goToMain();
        }
    }

    private void saveNewNote() {

        // Create a new note
        String addy = getAddress();
        Coordinate coordinate = new Coordinate(addy, Float.parseFloat(txtLat.getText().toString()), Float.parseFloat(txtLon.getText().toString()));
        CoordinateDatabase db = new CoordinateDatabase(this);
        db.addNote(coordinate);

        Toast.makeText(this, "Note Has Been Saved.", Toast.LENGTH_SHORT).show();

        goToMain();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



    }



    // Navigate to the main activity
    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    // Handle the back button press event
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public String getAddress() {
        StringBuilder builder = new StringBuilder();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String result = "Not Found";
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    Float.parseFloat(txtLat.getText().toString()),
                    Float.parseFloat(txtLon.getText().toString()),
                    1);

            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {

                    String addy = " " + address.getAddressLine(i) + ",";
                    builder.append(addy);
                }

                builder.deleteCharAt(builder.length() - 1);

                result = builder.toString();
            }
        } catch (IOException e) {
            // Handle the exception
        }
        return result;
    }
}
