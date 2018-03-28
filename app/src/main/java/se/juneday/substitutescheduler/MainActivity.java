package se.juneday.substitutescheduler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import se.juneday.substitutescheduler.domain.Assignment;
import se.juneday.substitutescheduler.domain.Substitute;
import se.juneday.substitutescheduler.storage.AssignmentStore;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    private AssignmentStore store;

    private String dateExpr = "";
    private String substituteExpr = "";

    private final static String ALL_DATES_FIELD = "All dates";
    private final static String ALL_SUBSTITUTES_FIELD = "All substitutes";

    private List<String> dates;
    private List<Substitute> substitutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        store = AssignmentStore.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(LOG_TAG, "onStart()");

        // register listener (using Lambda notation)
        store.registerAssignmentListener(code -> updateViews(code));

        // Fetch dates, substitutes and assignments
        store.fetchAll();
    }

    private void updateViews(List<Assignment> assignments) {
        Log.d(LOG_TAG, "updateViews()");
        if (assignments != null) {

            // Assignments
            updateListView(assignments);

            // Spinners
            updateSubstituteSpinner();
            updateDateSpinner();

        } else {
            Log.d(LOG_TAG, "Failure ... fetching assignments");
        }
    }

    private void updateListView(List<Assignment> assignments) {
        Log.d(LOG_TAG, "updateListView()");

        // Lookup ListView
        ListView listView = (ListView) findViewById(R.id.assignments);

        // Create Adapter
        ArrayAdapter<Assignment> adapter = new ArrayAdapter<Assignment>(this,
                android.R.layout.simple_list_item_1,
                assignments);

        // Set listView's adapter to the new adapter
        listView.setAdapter(adapter);
    }

    private void updateDateSpinner() {

        Log.d(LOG_TAG, "updateDateSpinner()");
        if (dates != null) {
            return;
        }
        dates = AssignmentStore.getInstance(this).dates();
        // Might be slow to add an item at pos 0 if the List is an ArrayList
        dates.add(0, ALL_DATES_FIELD);

        // Find spinner id (the spinner is defined in the XML file)
        Spinner dateSpinner = findViewById(R.id.date_spinner);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item,
                        dates);

        //  More space in spinner dropdown layout?, use
        //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set spinner's adapter (with data)
        dateSpinner.setAdapter(adapter);

        // Add listener to spinner items
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                Log.d(LOG_TAG, "onItemSelected " + this);
                String s = (String) adapterView.getItemAtPosition(pos);
                if (ALL_DATES_FIELD.equals(s)) {
                    dateExpr = "";
                } else {
                    dateExpr = s;
                }
                Log.d(LOG_TAG, " s: " + s);
                // fetch assignments
                store.fetchAssignments(substituteExpr, dateExpr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private void updateSubstituteSpinner() {
        Log.d(LOG_TAG, "updateSubstituteSpinner()");

        if (substitutes != null) {
            return;
        }

        // Find spinner id (the spinner is defined in the XML file)
        Spinner substitutueSpinner = findViewById(R.id.substitute_spinner);

        substitutes = store.substitutes();
        // Might be slow to add an item at pos 0 if the List is an ArrayList
        substitutes.add(0, new Substitute(ALL_SUBSTITUTES_FIELD, -1));

        ArrayAdapter<Substitute> adapter =
                new ArrayAdapter<Substitute>(this,
                        android.R.layout.simple_spinner_item,
                        substitutes);

        //  More space in spinner dropdown layout?, use
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set spinner's adapter (with data)
        substitutueSpinner.setAdapter(adapter);

        // Add listener to spinner items
        substitutueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                Log.d(LOG_TAG, "onItemSelected " + this);
                Substitute s = (Substitute) adapterView.getItemAtPosition(pos);
                if (s.name().equals(ALL_SUBSTITUTES_FIELD)) {
                    substituteExpr = "";
                } else {
                    substituteExpr = "" + s.id();
                }

                Log.d(LOG_TAG, " s: " + s + "  --> " + substituteExpr);
                // fetch assignments
                store.fetchAssignments(substituteExpr, dateExpr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

}
