package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import uk.ac.aston.wadekabs.smartcarparkapplication.model.CarParkItem;

public class CarParkListActivity extends AppCompatActivity implements CarParkFragment.OnListFragmentInteractionListener {

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_park_list);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get the RequestQueue.
        queue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        String url = "https://api.parkright.io/smartlot/v1/OccupanciesByCoordinate/e0c50d6bc5fc4223a37f3d893e0b7d27/UKLCYWC01/51.5209/-0.1741/";

        // Request a json response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Display the response array as string
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue accessed through singleton class.
        MySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(this);
        }
    }

    @Override
    public void onListFragmentInteraction(CarParkItem item) {
        Toast.makeText(getApplicationContext(), item.details, Toast.LENGTH_LONG).show();
    }
}
