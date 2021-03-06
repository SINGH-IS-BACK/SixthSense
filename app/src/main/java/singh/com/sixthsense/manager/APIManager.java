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

    @GET("/SixthSense/route/user")
    Call<ServerResponse> getUserRoute(
            @Query("sourceBeaconUuid") String sourceBeaconUuid,
            @Query("minor") int minor,
            @Query("major") int major,
            @Query("destinationBeaconId") String destinationBeaconId
    );

    @GET("/SixthSense/beacon/destinations")
    Call<DestinationResponse> getLocations(
            @Query("sourceBeaconUuid") String beaconUuid,
            @Query("minor") int minor,
            @Query("major") int major
    );
}
