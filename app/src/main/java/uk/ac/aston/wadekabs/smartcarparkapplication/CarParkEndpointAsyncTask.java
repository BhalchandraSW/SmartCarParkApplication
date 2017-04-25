package uk.ac.aston.wadekabs.smartcarparkapplication;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

import uk.ac.aston.wadekabs.smartcarparkapplication.backend.carParkApi.CarParkApi;
import uk.ac.aston.wadekabs.smartcarparkapplication.backend.carParkApi.model.CarPark;

class CarParkEndpointAsyncTask extends AsyncTask<LatLng, Void, List<CarPark>> {

    private static CarParkApi service;

    @Override
    protected List<CarPark> doInBackground(LatLng... params) {

        if (service == null) {
            CarParkApi.Builder builder = new CarParkApi.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),
                    null)
                    .setRootUrl("https://smart-car-park-application.appspot.com/_ah/api/");
//                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                        @Override
//                        public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
//                            request.setDisableGZipContent(true);
//                        }
//                    });
            service = builder.build();
        }

        LatLng location = params[0];

        try {
            return service.nearby(location.latitude, location.longitude).execute().getItems();
        } catch (IOException ignored) {
            System.out.println("ignored.print");
            ignored.printStackTrace();
        }

        return null;
    }
}
