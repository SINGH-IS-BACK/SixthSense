package singh.com.sixthsense;

import android.content.Context;
import android.opengl.Visibility;
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

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import singh.com.sixthsense.manager.APIManager;
import singh.com.sixthsense.model.Checkpoint;
import singh.com.sixthsense.model.ServerResponse;

public class MainActivity extends AppCompatActivity {

    //private static final Map<String, List<String>> ROUTES_BY_BEACONS;
    TextView currentLocationTV;
    TextView messageTV;
    TextView finalLocationTV;
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
        currentLocationTV = (TextView) findViewById(R.id.currentLocation);
        messageTV = (TextView) findViewById(R.id.message);
        finalLocationTV = (TextView) findViewById(R.id.finalDestination);
        beaconManager = new BeaconManager(this);


        



        FloatingActionButton clear = (FloatingActionButton) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        finalLocationTV.setText(finalLocation);

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
                        currentLocationTV.setText("Current Location : " + currentLocation);

                        if((mCheckpoints.size() > pointerCurrentLocation)){
                            if(mCheckpoints.get(pointerCurrentLocation).getDestination().getId().equals(finalLocation)){
                                t1.speak("Final location reached", TextToSpeech.QUEUE_FLUSH, null);
                                return;
                            }
                            else if(mCheckpoints.size() > pointerCurrentLocation+1 && mCheckpoints.get(pointerCurrentLocation+1).getSource().getMinor() == currentLocation){
                                adapter = new CheckpointRecyclerAdapter(mContext, mCheckpoints, 22);//currentLocation);
                                //mRecyclerView.setAdapter(adapter);
                                t1.speak(mCheckpoints.get(pointerCurrentLocation+1).getVoiceText(), TextToSpeech.QUEUE_FLUSH, null);
                                return;
                            }
                        }


                        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(API_URL)
                                .client(client)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        APIManager service = retrofit.create(APIManager.class);

                        Call<ServerResponse> call = service.getUserRoute("asd", 1, currentLocation, finalLocation);
                        call.enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                // handle success
                                ArrayList<Checkpoint> route = response.body().getRoute();
                                mCheckpoints = route;
                                empty.setVisibility(View.GONE);
                                String nextMessage = "";
                                if(route.size() == 0){
                                    nextMessage = "Final Location Reached";
                                    return;
                                }else {
                                    nextMessage = route.get(0).getVoiceText();
                                }
                                String str = "Currently at   " + currentLocation + "   next location " + nextMessage;
                                currentLocationTV.setText(nextMessage + "//  " + currentUUID);
                                messageTV.setText(str);
                                adapter = new CheckpointRecyclerAdapter(mContext, route, 22);//currentLocation);
                                mRecyclerView.setAdapter(adapter);

                                t1.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                // handle failure
                                currentLocationTV.setText("Failed to retrieve Final Location");
                            }
                        });

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