package uk.ac.aston.wadekabs.smartcarparkapplication.backend;

/**
 * Created by bhalchandrawadekar on 14/04/2017.
 */

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.maps.GaeRequestHandler;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import uk.ac.aston.wadekabs.smartcarparkapplication.backend.model.CarPark;

/**
 * An endpoint class for exposing car park APIs
 */
@Api(
        name = "carParkApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.smartcarparkapplication.wadekabs.aston.ac.uk",
                ownerName = "backend.smartcarparkapplication.wadekabs.aston.ac.uk",
                packagePath = ""
        )
)
public class CarParkEndpoint {

    private static String API_KEY = "AIzaSyA_AV3itrHOwfbPtS8ySQGe3L54883BWI8";
    private static GeoApiContext sContext = new GeoApiContext(new GaeRequestHandler()).setApiKey(API_KEY);

    @ApiMethod(name = "nearby")
    public List<CarPark> nearby(@Named("latitude") double latitude, @Named("longitude") double longitude) throws InterruptedException, ApiException, IOException {

        List<CarPark> carParkList = new ArrayList<>();

        LatLng center = new LatLng(latitude, longitude);

        GeocodingApiRequest request = GeocodingApi.reverseGeocode(sContext, center);
        request.resultType(AddressType.POSTAL_TOWN);
        GeocodingResult[] results = request.await();

        for (GeocodingResult result : results) {
            for (AddressComponent component : result.addressComponents) {

                for (AddressComponentType type : component.types) {
                    if (AddressComponentType.POSTAL_TOWN.equals(type)) {
                        System.out.println(component);
                    }
                }
            }
        }

        /*
          Google Places API
         */
        NearbySearchRequest nearbySearchRequest = PlacesApi.nearbySearchQuery(sContext, new LatLng(latitude, longitude));
        PlacesSearchResponse response = nearbySearchRequest.await();
        for (PlacesSearchResult result : response.results) {
            carParkList.add(new CarPark());
        }

        /*
          Transport for Greater Manchester API
         */

        /*
          Birmingham Data Factory API
         */

        /*
          ParkRight API
         */

        return carParkList;
    }

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }

}
