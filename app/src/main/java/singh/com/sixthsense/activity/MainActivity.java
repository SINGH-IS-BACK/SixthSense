package singh.com.sixthsense.activity;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import singh.com.sixthsense.adapter.CheckpointRecyclerAdapter;
import singh.com.sixthsense.R;
import singh.com.sixthsense.manager.APIManager;
import singh.com.sixthsense.manager.SettingsManager;
import singh.com.sixthsense.model.Checkpoint;
import singh.com.sixthsense.model.EstimoteBeacon;
import singh.com.sixthsense.model.ServerResponse;

public class MainActivity extends AppCompatActivity {

    /*private static final Map<String, List<String>> ROUTES_BY_BEACONS;


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
        routesByBeacons.put("1:1004", new ArrayList<String>() {{
            add("Go to 5");
        }});
        routesByBeacons.put("1:1005", new ArrayList<String>() {{
            add("Go to 6");
        }});
        routesByBeacons.put("1:1006", new ArrayList<String>() {{
            add("Done");
        }});
        ROUTES_BY_BEACONS = Collections.unmodifiableMap(routesByBeacons);
    }

    private List<String> placesNearBeacon(EstimoteBeacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (ROUTES_BY_BEACONS.containsKey(beaconKey)) {
            return ROUTES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    private ArrayList<Checkpoint> remainingPath(int start){
        ArrayList<Checkpoint> route = new ArrayList<>();
        for(int i = start; i <= 1006; i++){
            Checkpoint checkpoint = new Checkpoint();
            EstimoteBeacon beacon = new EstimoteBeacon();
            beacon.setMinor(i);
            beacon.setLocation("Beacon "+ i);
            beacon.setMajor(1);
            checkpoint.setSource(beacon);
            String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
            if (ROUTES_BY_BEACONS.containsKey(beaconKey)) {
                checkpoint.setVoiceText(ROUTES_BY_BEACONS.get(beaconKey).get(0));
            }
            EstimoteBeacon beaconTwo = new EstimoteBeacon();
            beaconTwo.setMinor(i + 1);
            beaconTwo.setMajor(1);
            checkpoint.setDestination(beaconTwo);
            //checkpoint.set
            route.add(checkpoint);
        }
        return route;
    }*/



    //TextView currentLocationTV;
    TextView messageTV;
    //TextView finalLocationTV;
    int previousLocation = 0;
    TextToSpeech t1;
    Context mContext;
    int pointerCurrentLocation = 0;
    ArrayList<Checkpoint> mCheckpoints = new ArrayList<>();

    protected RecyclerView.Adapter adapter;
    protected RecyclerView mRecyclerView;
    TextView empty;

    private static final String API_URL = "http://54.69.39.220:8082/";

    private BeaconManager beaconManager;
    private com.estimote.sdk.Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        //currentLocationTV = (TextView) findViewById(R.id.currentLocation);
        messageTV = (TextView) findViewById(R.id.message);
        //finalLocationTV = (TextView) findViewById(R.id.finalDestination);
        beaconManager = new BeaconManager(this);



        FloatingActionButton clear = (FloatingActionButton) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GetDirectionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.location_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(layoutManager);

        empty = (TextView) findViewById(R.id.search_empty);
        empty.setVisibility(View.VISIBLE);


        final String finalLocation = SettingsManager.getInstance(this).getFinalLocation();
        if(finalLocation.isEmpty()){
            finish();
        }
        //finalLocationTV.setText(finalLocation);

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

                    final String currentUUID = nearestBeacon.toString();

                    if(currentLocation == previousLocation){

                    }else {


                        previousLocation = currentLocation;
                        Toast.makeText(getApplicationContext(), "Current Beacon : " + (currentLocation - 1000), Toast.LENGTH_SHORT);

                        //currentLocationTV.setText("Current Location : " + currentLocation);
                        /*
                        ArrayList<Checkpoint> route = remainingPath(currentLocation);
                        adapter = new CheckpointRecyclerAdapter(mContext, route, currentLocation);
                        mRecyclerView.setAdapter(adapter);
                        EstimoteBeacon beacon = new EstimoteBeacon();
                        beacon.setMinor(currentLocation);
                        beacon.setMajor(1);
                        t1.speak(placesNearBeacon(beacon).get(0), TextToSpeech.QUEUE_FLUSH, null);
*/
                        boolean flag = false;
                        if(mCheckpoints.size() > pointerCurrentLocation && mCheckpoints.get(pointerCurrentLocation).getDestination()==null){
                            //empty.setText("Final Location Reached");
                            //empty.setVisibility(View.VISIBLE);
                            t1.speak("Final location reached", TextToSpeech.QUEUE_FLUSH, null);
                            flag = true;
                            messageTV.setText("");
                            return;
                        }
                        else if (mCheckpoints.size() > pointerCurrentLocation && mCheckpoints.get(pointerCurrentLocation).getSource().getMinor()== currentLocation) {
                            adapter = new CheckpointRecyclerAdapter(mContext, mCheckpoints, currentLocation);
                            //mRecyclerView.setAdapter(adapter);
                            t1.speak(mCheckpoints.get(pointerCurrentLocation).getVoiceText(), TextToSpeech.QUEUE_FLUSH, null);
                            adapter = new CheckpointRecyclerAdapter(mContext, mCheckpoints, currentLocation);
                            mRecyclerView.setAdapter(adapter);
                            pointerCurrentLocation++;
                            flag = true;
                            messageTV.setText("");
                            return;
                        }

                        /*if((mCheckpoints.size() > pointerCurrentLocation && pointerCurrentLocation>0)) {
                            if (mCheckpoints.get(pointerCurrentLocation).getSource().getId().equals(mCheckpoints.get(pointerCurrentLocation -1).getDestination().getId())) {
                                empty.setText("Final Location Reached");
                                empty.setVisibility(View.VISIBLE);
                                t1.speak("Final location reached", TextToSpeech.QUEUE_FLUSH, null);

                                if(mCheckpoints.get(pointerCurrentLocation).getSource().getId().equals(finalLocation))

                                return;
                            } else if (mCheckpoints.size() > pointerCurrentLocation + 1 && mCheckpoints.get(pointerCurrentLocation + 1).getSource().getMinor() == currentLocation) {
                                adapter = new CheckpointRecyclerAdapter(mContext, mCheckpoints, currentLocation);
                                //mRecyclerView.setAdapter(adapter);
                                t1.speak(mCheckpoints.get(pointerCurrentLocation + 1).getVoiceText(), TextToSpeech.QUEUE_FLUSH, null);
                                adapter = new CheckpointRecyclerAdapter(mContext, mCheckpoints, currentLocation);
                                mRecyclerView.setAdapter(adapter);
                                return;
                            }
                        }*/


                        /*if((mCheckpoints.size() > pointerCurrentLocation)){
                            if(mCheckpoints.get(pointerCurrentLocation).getDestination().getId().equals(finalLocation)){
                                empty.setText("Final Location Reached");
                                empty.setVisibility(View.VISIBLE);
                                t1.speak("Final location reached", TextToSpeech.QUEUE_FLUSH, null);
                                return;
                            }
                            else if(mCheckpoints.size() > pointerCurrentLocation+1 && mCheckpoints.get(pointerCurrentLocation+1).getSource().getMinor() == currentLocation){
                                adapter = new CheckpointRecyclerAdapter(mContext, mCheckpoints, currentLocation);
                                //mRecyclerView.setAdapter(adapter);
                                t1.speak(mCheckpoints.get(pointerCurrentLocation+1).getVoiceText(), TextToSpeech.QUEUE_FLUSH, null);
                                adapter = new CheckpointRecyclerAdapter(mContext, mCheckpoints, currentLocation);
                                mRecyclerView.setAdapter(adapter);
                                return;
                            }
                        }*/

                        if(flag == false) {

                            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(API_URL)
                                    .client(client)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            APIManager service = retrofit.create(APIManager.class);

                            Call<ServerResponse> call = service.getUserRoute("B9407F30-F5F8-466E-AFF9-25556B57FE6D", currentLocation, 1, finalLocation);
                            call.enqueue(new Callback<ServerResponse>() {
                                @Override
                                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                    // handle success
                                    ArrayList<Checkpoint> route = response.body().getRoute();
                                    mCheckpoints = route;
                                    empty.setVisibility(View.GONE);
                                    String nextMessage = "";
                                    if (route.size() == 0) {
                                        //nextMessage = "Final Location Reached";
                                        return;
                                    } else {
                                        nextMessage = route.get(0).getVoiceText();
                                    }
                                    String str = "Currently at   " + currentLocation + "   next location " + nextMessage;
                                    //currentLocationTV.setText(nextMessage + "//  " + currentUUID);
                                    messageTV.setText("");//str);
                                    adapter = new CheckpointRecyclerAdapter(mContext, route, currentLocation);
                                    mRecyclerView.setAdapter(adapter);

                                    t1.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                                    pointerCurrentLocation = 1;
                                }

                                @Override
                                public void onFailure(Call<ServerResponse> call, Throwable t) {
                                    // handle failure
                                    messageTV.setText("Failed to retrieve Final Location" + t.toString());
                                }
                            });
                        }

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
        //beaconManager.stopRanging(region);
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