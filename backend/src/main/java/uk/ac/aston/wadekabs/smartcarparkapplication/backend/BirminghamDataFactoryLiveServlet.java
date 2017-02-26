package uk.ac.aston.wadekabs.smartcarparkapplication.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Bhalchandra Wadekar on 26/02/2017.
 */

public class BirminghamDataFactoryLiveServlet extends HttpServlet {

    private static Logger Log = Logger.getLogger("uk.ac.aston.wadekabs.smartcarparkapplication.backend.BirminghamDataFactoryServlet");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);

        // Note: Ensure that the [PRIVATE_KEY_FILENAME].json has read
        // permissions set.
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(getServletContext().getResourceAsStream("/WEB-INF/Smart Car Park Application-a993eacfcd35.json"))
                .setDatabaseUrl("https://smart-car-park-application.firebaseio.com/")
                .build();


        try {
            FirebaseApp.getInstance();
        } catch (Exception error) {
            Log.info("doesn't exist...");
        }

        try {
            FirebaseApp.initializeApp(options);
        } catch (Exception error) {
            Log.info("already exists...");
        }

        // As an admin, the app has access to read and write all data, regardless of Security Rules
//        DatabaseReference ref = FirebaseDatabase
//                .getInstance()
//                .getReference("todoItems");

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

            // mDatabase.child("carParks").child(iString).child("systemCodeNumber").setValue(nextLine[0]);
            // mDatabase.child("carParks").child(iString).child("capacity").setValue(Integer.parseInt(nextLine[1]));
            // mDatabase.child("carParks").child(iString).child("disabledCapacity").setValue(Integer.parseInt(nextLine[2]));
            // mDatabase.child("carParks").child(iString).child(/* "Short"  + */ "description").setValue(nextLine[3]);
            // mDatabase.child("carparks").child(iString).child("Northing").setValue(nextLine[4]);
            // mDatabase.child("carparks").child(iString).child("Easting").setValue(nextLine[5]);
            mDatabase.child("carParksLive").child(iString).child("state").setValue(nextLine[6].trim());
            mDatabase.child("carParksLive").child(iString).child("fault").setValue(nextLine[7].trim());
            mDatabase.child("carParksLive").child(iString).child("occupancy").setValue(Integer.parseInt(nextLine[8].trim()));
            mDatabase.child("carParksLive").child(iString).child("occupancyTrend").setValue(nextLine[9].trim());
            mDatabase.child("carParksLive").child(iString).child("occupancyPercentage").setValue(Double.parseDouble(nextLine[10].trim()));
            mDatabase.child("carParksLive").child(iString).child("fillRate").setValue(Double.parseDouble(nextLine[11].trim()));
            mDatabase.child("carParksLive").child(iString).child("exitRate").setValue(Double.parseDouble(nextLine[12].trim()));
            mDatabase.child("carParksLive").child(iString).child("queueTime").setValue(Double.parseDouble(nextLine[13].trim()));
            mDatabase.child("carParksLive").child(iString).child("lastUpdated").setValue(nextLine[14].trim());

//            JSONObject geom = new JSONObject(nextLine[15]);
//            JSONArray coordinates = geom.getJSONArray("coordinates");
//
//            mDatabase.child("carParks").child(iString).child("location").child("lng").setValue(coordinates.get(0));
//            mDatabase.child("carParks").child(iString).child("location").child("lat").setValue(coordinates.get(1));

            i++;
        }
    }
}
