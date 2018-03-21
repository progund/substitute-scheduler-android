package se.juneday.substitutescheduler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import se.juneday.substitutescheduler.domain.Assignment;
import se.juneday.substitutescheduler.storage.AssignmentStore;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");
        setContentView(R.layout.activity_main);
    }

    private void fillSpinner(Spinner spinner, List<String> elements) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item,
                        elements);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void fillDateSpinner() {
        List<String> dates = new ArrayList<>();
        dates.add("All");
        for (int i = 15; i < 20; i++) {
            dates.add("2018-01-" + i);
        }
        Spinner dateSpinner = findViewById(R.id.date_spinner);
        fillSpinner(dateSpinner, dates);
    }

    private void fillSubstituteSpinner() {
        List<String> subs = new ArrayList<>();
        subs.add("All");
        subs.add("Dr Bullshy T");
        subs.add("Dr B Ulsheet");

        Spinner substitutueSpinner = findViewById(R.id.substitute_spinner);
        fillSpinner(substitutueSpinner, subs);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(LOG_TAG, "onStart()");

        fillDateSpinner();
        fillSubstituteSpinner();

        // register listener
        AssignmentStore store = AssignmentStore.getInstance(this);
        store.registerAssignmentListener(data -> updateListView(data));

        // fetch data (arguments are ignored right now so don't bother about them
        store.fetch("", "");
    }

    private void updateListView(List<Assignment> data) {
        // Lookup ListView
        ListView listView = (ListView) findViewById(R.id.assignments);

        // Create Adapter
        ArrayAdapter<Assignment> adapter = new ArrayAdapter<Assignment>(this,
                android.R.layout.simple_list_item_1,
                data);

        // Set listView's adapter to the new adapter
        listView.setAdapter(adapter);
    }

}
