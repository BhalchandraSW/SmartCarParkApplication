package uk.ac.aston.wadekabs.smartcarparkapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.gson.Gson;
import com.google.maps.GaeRequestHandler;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import uk.ac.aston.wadekabs.smartcarparkapplication.backend.model.BirminghamDataFactoryCarPark;
import uk.ac.aston.wadekabs.smartcarparkapplication.backend.model.CarPark;
import uk.ac.aston.wadekabs.smartcarparkapplication.backend.model.parkright.ParkRightCarPark;

/**
 * An endpoint class for exposing following car park APIs:
 * 1. Google Places
 * 2. Transport for Greater Manchester
 * 3. Birmingham Data Factory
 * 4. ParkRight
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

    private static String GOOGLE_API_KEY = "AIzaSyA_AV3itrHOwfbPtS8ySQGe3L54883BWI8";
    private static GeoApiContext sContext = new GeoApiContext(new GaeRequestHandler())
            .setApiKey(GOOGLE_API_KEY);

    private static String PARK_RIGHT_API_KEY = "50c7b0895b794059b6b838b1b0c9a48b";
    private static String WEST_MINSTER_SITE_CODE = "UKLCYWC01";

    String city;

    @ApiMethod(name = "nearby")
    public List<CarPark> nearby(@Named("latitude") double latitude,
                                @Named("longitude") double longitude)
            throws InterruptedException, ApiException, IOException {

        List<CarPark> carParkList = new ArrayList<>();

        LatLng center = new LatLng(latitude, longitude);

        GeocodingApiRequest request = GeocodingApi.reverseGeocode(sContext, center);
        request.resultType(AddressType.POSTAL_TOWN);
        GeocodingResult[] results = request.await();

        outer:
        for (GeocodingResult result : results) {
            for (AddressComponent component : result.addressComponents) {
                for (AddressComponentType type : component.types) {
                    if (AddressComponentType.POSTAL_TOWN.equals(type)) {
                        city = component.shortName;
                        break outer;
                    }
                }
            }
        }

        /*
          Google Places API
         */
//        NearbySearchRequest nearbySearchRequest = PlacesApi.nearbySearchQuery(sContext, center);
//        nearbySearchRequest.radius(50000);
//        PlacesSearchResponse response = nearbySearchRequest.await();
//        for (PlacesSearchResult result : response.results) {
//            carParkList.add(new CarPark());
//        }

        /*
          Transport for Greater Manchester API
         */

        /*
          Birmingham Data Factory API
         */
        URL url = new URL("https://data.birmingham.gov.uk" +
                "/dataset/53888bc3-6745-4454-8f03-3575668232fd" +
                "/resource/bea04cd0-ea86-4d7e-ab3b-2da3368d1e01/download/parking.csv");

        CsvConfiguration config = new CsvConfiguration();
        config.setFieldDelimiter(',');
        config.setLineFilter(new HeaderAndFooterFilter(1, false, false));

        Deserializer deserializer =
                CsvIOFactory.createFactory(config, BirminghamDataFactoryCarPark.class)
                        .createDeserializer();
        deserializer.open(new InputStreamReader(url.openStream()));

        while (deserializer.hasNext()) {
            BirminghamDataFactoryCarPark birminghamDataFactoryCarPark = deserializer.next();
            CarPark carPark = new CarPark(birminghamDataFactoryCarPark);
            carParkList.add(carPark);
        }

        deserializer.close(true);

        /*
          ParkRight API
         */
        url = new URL("https://api.parkright.io/smartlot/v1/LotsByCoordinate/" +
                PARK_RIGHT_API_KEY + "/" +
                WEST_MINSTER_SITE_CODE + "/" +
                latitude + "/" +
                longitude + "/");

        InputStreamReader reader = new InputStreamReader(url.openStream());

        Gson gson = new Gson();
        ParkRightCarPark[] parkRightCarParks = gson.fromJson(reader, ParkRightCarPark[].class);

        for (ParkRightCarPark parkRightCarPark : parkRightCarParks) {
            CarPark carPark = new CarPark(parkRightCarPark);
            carParkList.add(carPark);
        }

        return carParkList;
    }
}
