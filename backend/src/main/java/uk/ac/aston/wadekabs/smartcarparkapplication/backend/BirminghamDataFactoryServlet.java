package uk.ac.aston.wadekabs.smartcarparkapplication.backend;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bhalchandrawadekar on 26/02/2017.
 */

public class BirminghamDataFactoryServlet extends HttpServlet {

    private static Logger Log = Logger.getLogger("uk.ac.aston.wadekabs.smartcarparkapplication.backend.BirminghamDataFactoryServlet");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        URL url = new URL("https://data.birmingham.gov.uk/dataset/53888bc3-6745-4454-8f03-3575668232fd/resource/bea04cd0-ea86-4d7e-ab3b-2da3368d1e01/download/parking.csv");

        CSVReader reader = new CSVReader(new InputStreamReader(url.openStream()));

        String[] nextLine;

        int i = 0;

        while ((nextLine = reader.readNext()) != null) {

            if (i == 0) {
                i++;
                continue;
            }

            String iString = Integer.toString(i);

            mDatabase.child("carParks").child(iString).child("systemCodeNumber").setValue(nextLine[0].trim());
            mDatabase.child("carParks").child(iString).child("capacity").setValue(Integer.parseInt(nextLine[1].trim()));
            mDatabase.child("carParks").child(iString).child("disabledCapacity").setValue(Integer.parseInt(nextLine[2].trim()));
            mDatabase.child("carParks").child(iString).child(/* "Short"  + */ "description").setValue(nextLine[3].trim());
            // mDatabase.child("carparks").child(iString).child("Northing").setValue(nextLine[4]);
            // mDatabase.child("carparks").child(iString).child("Easting").setValue(nextLine[5]);
            // mDatabase.child("carparks").child(iString).child("State").setValue(nextLine[6]);
            // mDatabase.child("carparks").child(iString).child("Fault").setValue(nextLine[7]);
            // mDatabase.child("carparks").child(iString).child("Occupancy").setValue(nextLine[8]);
            // mDatabase.child("carparks").child(iString).child("Occupancy_trend").setValue(nextLine[9]);
            // mDatabase.child("carparks").child(iString).child("OccupancyPercentage").setValue(nextLine[10]);
            // mDatabase.child("carparks").child(iString).child("FillRate").setValue(nextLine[11]);
            // mDatabase.child("carparks").child(iString).child("ExitRate").setValue(nextLine[12]);
            // mDatabase.child("carparks").child(iString).child("QueueTime").setValue(nextLine[13]);
            // mDatabase.child("carparks").child(iString).child("LastUpdated").setValue(nextLine[14]);

            JSONObject geom = new JSONObject(nextLine[15].trim());
            JSONArray coordinates = geom.getJSONArray("coordinates");

            mDatabase.child("carParks").child(iString).child("location").child("lng").setValue(coordinates.get(0));
            mDatabase.child("carParks").child(iString).child("location").child("lat").setValue(coordinates.get(1));

            i++;
        }
    }
}
