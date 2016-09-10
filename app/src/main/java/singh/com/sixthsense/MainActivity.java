package singh.com.sixthsense;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import singh.com.sixthsense.manager.APIManager;
import singh.com.sixthsense.model.ServerResponse;

public class MainActivity extends AppCompatActivity {

    private static final Map<String, List<String>> ROUTES_BY_BEACONS;
    TextView textView;
    int previousLocation = 0;
    TextToSpeech t1;
    private static final String API_URL = "http://54.69.39.220:8082/";

    static {
        Map<String, List<String>> routesByBeacons = new HashMap<>();

        //hardcoded values for temporary purpose
        routesByBeacons.put("1:1001", new ArrayList<String>() {{
            add("Go to 2");
        }});
        routesByBeacons.put("1:1002", new ArrayList<String>() {{
            add("Go to 3");
            add("Warning");
        }});
        routesByBeacons.put("1:1003", new ArrayList<String>() {{
            add("Go to 4");
        }});
        ROUTES_BY_BEACONS = Collections.unmodifiableMap(routesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (ROUTES_BY_BEACONS.containsKey(beaconKey)) {
            return ROUTES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    private BeaconManager beaconManager;
    private com.estimote.sdk.Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        beaconManager = new BeaconManager(this);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);

                    final int currentLocation = nearestBeacon.getMinor();

                    if(currentLocation == previousLocation){

                    }else {
                        textView.setText("Current Location : " + currentLocation);

                        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(API_URL)
                                .client(client)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        APIManager service = retrofit.create(APIManager.class);

                        Call<ServerResponse> call = service.getNextLocation("asd", currentLocation, 1008);
                        call.enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                // handle success
                                String nextMessage = response.body().getMessage();
                                String str = "Currently at   " + currentLocation + "   next location " + nextMessage;
                                textView.setText(str);
                                t1.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                // handle failure
                                textView.setText("Failed to retrieve Final Location");
                            }
                        });
                        previousLocation = currentLocation;
                    }

                }
            }
        });
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

    }


    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        //beaconManager.stopRanging(region);

        super.onPause();
    }

    @Override
    protected void onStop() {
        beaconManager.stopRanging(region);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
