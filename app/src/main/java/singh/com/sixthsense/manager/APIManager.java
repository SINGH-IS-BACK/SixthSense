package singh.com.sixthsense.manager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import singh.com.sixthsense.model.DestinationResponse;
import singh.com.sixthsense.model.ServerResponse;

/**
 * Created by kthethi on 10/09/16.
 */

public interface APIManager {

    @GET("/SixthSense/beacon/info")
    Call<ServerResponse> getNextLocation(
            @Query("beaconId") String beaconId,
            @Query("currentLocation") int currentLocation,
            @Query("finalLocation") int finalLocation
    );

    @GET("/SixthSense/beacon/destinations")
    Call<DestinationResponse> getLocations(
            @Query("beaconUuid") String beaconUuid,
            @Query("minor") int minor,
            @Query("major") int major
    );
}
