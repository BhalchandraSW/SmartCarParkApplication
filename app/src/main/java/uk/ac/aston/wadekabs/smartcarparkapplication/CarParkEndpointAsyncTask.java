package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import uk.ac.aston.wadekabs.smartcarparkapplication.backend.carParkApi.CarParkApi;

/**
 * Created by bhalchandrawadekar on 24/04/2017.
 */

public class CarParkEndpointAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {

    private static CarParkApi service;
    private Context context;

    @Override
    protected String doInBackground(Pair<Context, String>... params) {

        if (service == null) {
            CarParkApi.Builder builder = new CarParkApi.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),
                    null)
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                            request.setDisableGZipContent(true);
                        }
                    });
            service = builder.build();
        }

        context = params[0].first;
        String name = params[0].second;

        try {
            return service.sayHi(name).execute().getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }
}
