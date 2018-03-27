package se.juneday.substitutescheduler.storage;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.juneday.substitutescheduler.domain.Assignment;
import se.juneday.substitutescheduler.domain.School;
import se.juneday.substitutescheduler.domain.Substitute;

public class AssignmentStore {

    private static final String LOG_TAG = AssignmentStore.class.getName() ;

    private static AssignmentStore instance;

    private AssignmentListener listener;
    private Context context;
    private Map<String, Integer> substituteMap;
    private List<Substitute> subs;
    private List<Assignment> assignments;


    public enum FETCH_RETURN_CODES {
        FETCH_SUCCEEDED,
        FETCH_PARSE_ERROR,
        FETCH_NETWORK_ERROR
    };

    public static AssignmentStore getInstance(Context context) {
        if (instance==null) {
            instance = new AssignmentStore(context);
        }
        return instance;
    }

    private AssignmentStore(Context context) {
        this.context = context;

        // used to look up id for sub
        substituteMap = new HashMap<>();

        // Substitute teacher storage
        subs = new ArrayList<>();
        addFakeSubstitutes();
    }  // private to prevent instance creation

    private void addFakeSubstitutes() {
        subs.add(createSubstitute("Rikard", 1));
        subs.add(createSubstitute("Henrik", 2));
        subs.add(createSubstitute("Anders", 3));
        subs.add(createSubstitute("Nahid", 4));
        subs.add(createSubstitute("Conny", 5));
        subs.add(createSubstitute("Svante", 6));
        subs.add(createSubstitute("Elisabeth", 7));
        subs.add(createSubstitute("Eva", 8));
        subs.add(createSubstitute("Kristina", 9));
        subs.add(createSubstitute("Bengt", 10));
    }

    public List<String> dates() {
        List<String> dates = new ArrayList<>();
        for (int i = 15; i < 20; i++) {
            dates.add("2018-01-" + i);
        }
        return dates;
    }

    public List<Assignment> assignments() {
        return assignments;
    }

    public interface AssignmentListener {
        public void assignmentsReceived(FETCH_RETURN_CODES code);
    }

    public void registerAssignmentListener(AssignmentListener listener) {
        this.listener = listener;
    };

    /* fetchAssignments - with faked data and no network

    public void fetchAssignments(int id, String date) {
        List<Assignment> assignments = new ArrayList<>();

        Log.d(LOG_TAG, "fetchAssignments()");

        // Create some fake assignments
        School school = new School("Bullshit Academy", "at home");
        Substitute substitute = new Substitute("Dr. Bullshy T");
        for (int i=10;i<30;i++) {
            Assignment assignment = new Assignment(substitute, "2018-04-" + i, school);
            assignments.add(assignment);
        }

        // fake waiting for data from the web api
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // notify listener that new data has arrived
        if (listener!=null) {
            Log.d(LOG_TAG, "informing listener()");
            listener.assignmentsReceived(assignments);
        } else {
            Log.d(LOG_TAG, "No listener registered");
        }
    }
    */

    // This method is compensating for the lack of id lookup
    private int idForSubstituteName(String name) {
        return substituteMap.get(name);
    }

    private Substitute createSubstitute(String name, int id) {
        substituteMap.put(name,id);
        return new Substitute(name, id);
    }

    private Substitute createSubstitute(String name) {
        return new Substitute(name, idForSubstituteName(name));
    }


    public List<Substitute> substitutes() {
        return subs;
    }

    private List<Assignment> jsonToAssignments(JSONArray array) {
        List<Assignment> assignments = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Log.d(LOG_TAG, " JSON parse " + i + " of " + array.length());
            try {
                JSONObject row = array.getJSONObject(i);

                JSONObject schoolJson = row.getJSONObject("school");
                School school = new School(schoolJson.getString("school_name"), schoolJson.getString("address"));

                JSONObject substituteJson = row.getJSONObject("substitute");
                Substitute substitute = createSubstitute(substituteJson.getString("name"));

                Log.d(LOG_TAG, " * " + substitute);

                String date = row.getString("date");
                Assignment assignment = new Assignment(substitute, date, school);

                assignments.add(assignment);
            } catch (JSONException e) {
                Log.d(LOG_TAG, " JSON parse exception " + e);
            }
        }
        return assignments;
    }

    public void fetchAll() {
        // no need to fetch subs and dates ... server does not have 'em
        fetchAssignments("","");
    }

    public void fetchAssignments(String id, String date) {
        assignments = new ArrayList<>();

        Log.d(LOG_TAG, "fetchAssignments()");

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ServerSettings.serverUrl(id, date);
        Log.d(LOG_TAG, " url: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray array) {
                        Log.d(LOG_TAG, "onResponse() " + array);
                        assignments = jsonToAssignments(array);
                        listener.assignmentsReceived(FETCH_RETURN_CODES.FETCH_SUCCEEDED);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, " cause: " + error.getCause());
                listener.assignmentsReceived(FETCH_RETURN_CODES.FETCH_NETWORK_ERROR);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);

    }

}
