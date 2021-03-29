package space.fedosenko.parkingtimer;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

import space.fedosenko.parkingtimer.Model.Coordinate;
import space.fedosenko.parkingtimer.Model.Model;

import static space.fedosenko.parkingtimer.BackgroundLocationWorker.TIME_TO_RETURN_IN_SECONDS;

//import space.fedosenko.parkingtimer.GsonClassesForLocation.Location;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    private boolean parkingPositionSaved;
    private TextView parkingTime, timeToReturn, textView, textView2;
    private Button btSaveLocation, btNavigateHome;
    private ProgressBar progressBar;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Context context;
    private long waitingTimeInSec;
    private int t1Hour, t1Minute, t2Hour, t2Minute;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btNavigateHome = findViewById(R.id.bt_navigate);

        parkingTime = findViewById(R.id.parking_time);
        timeToReturn = findViewById(R.id.time_to_return);
        textView = findViewById(R.id.tv_enter);
        textView2 = findViewById(R.id.tv_or);


        timeToReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                                Calendar calendar = Calendar.getInstance();
                                t1Hour = hourOfDay -calendar.get(Calendar.HOUR_OF_DAY);
                                t1Minute = minute -calendar.get(Calendar.MINUTE);

                                if (t1Minute<0){
                                    t1Minute+=60;
                                    t1Hour--;
                                }

                                waitingTimeInSec = (t1Hour*60+t1Minute)*60;

                                String time;
                                if (t1Hour==0){
                                    time = t1Minute+" min";
                                } else {
                                    time = t1Hour +" hour(s) "+ t1Minute+" min";
                                }

                                Calendar currentCalendar = Calendar.getInstance();

                                currentCalendar.add(Calendar.HOUR, t1Hour);
                                currentCalendar.add(Calendar.MINUTE, t1Minute);
                                //calendar.set(0,0,0,t1Hour,t1Minute);
                                timeToReturn.setText("You will be back by "+DateFormat.format("HH:mm",currentCalendar));
                                parkingTime.setText("Parking time: " +time);
                            }
                        },24,0,true
                );
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY,t1Hour);
                calendar.add(Calendar.MINUTE,t1Minute);
                timePickerDialog.updateTime(calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));

                timePickerDialog.show();
                textView.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.INVISIBLE);
            }
        });
        parkingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                waitingTimeInSec = (hourOfDay*60+minute)*60;


                                t1Hour = hourOfDay;
                                t1Minute = minute;
                                String time;
                                if (hourOfDay==0){
                                    time = minute+" min";
                                } else {
                                    time = hourOfDay +" hour(s) "+ minute+" min";
                                }
                                //Calendar calendar = Calendar.getInstance();

                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.HOUR,t1Hour);
                                calendar.add(Calendar.MINUTE,t1Minute);
                                //calendar.set(0,0,0,t1Hour,t1Minute);

                                timeToReturn.setText("You will be back by "+DateFormat.format("HH:mm", calendar));
                                parkingTime.setText("Parking time: " +time);

                            }
                        },24,0,true
                );
                timePickerDialog.updateTime(t1Hour,t1Minute);

                timePickerDialog.show();
                textView.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.INVISIBLE);
            }
        });
        progressBar = findViewById(R.id.progressBar);

        btSaveLocation = findViewById(R.id.bt_saveLocation);
        btSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLocation();

                Toast.makeText(MainActivity.this, "help", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION
                    );
                } else {
                initWorker();

                }
            }
        });

        btNavigateHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                cancelAllWork();

                if(!(Model.getInstance().getParkingCoordinate() ==null)){
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                                    + Model.getInstance().getParkingCoordinate().toString()
                                    +"&travelmode=walking"));
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Current Location is not up to date. Please wait",
                            Toast.LENGTH_LONG).show();
                }


            }
        });





    }


    private void cancelAllWork(){
        WorkManager.getInstance(this).cancelAllWork();
    }

    private void saveLocation(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION
            );
        }
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int lastLocationIndex = locationResult.getLocations().size() - 1;
                            Model.getInstance().setParkingCoordinate(new Coordinate(
                                    locationResult.getLocations().get(lastLocationIndex).getLatitude(),
                                    locationResult.getLocations().get(lastLocationIndex).getLongitude())
                            );


                            progressBar.setVisibility(View.GONE); }
                    }
                }, Looper.getMainLooper());

    }


    private void initWorker() {
        if (parkingPositionSaved){
            cancelAllWork();
        }
        Data data = new Data.Builder()
                .putLong(TIME_TO_RETURN_IN_SECONDS, waitingTimeInSec)
                .build();

        Constraints constraints = new Constraints.Builder()
                .build();
        OneTimeWorkRequest downloadRequest = new OneTimeWorkRequest.Builder(BackgroundLocationWorker.class)
                .setInputData(data)
                .setConstraints(constraints)
                .addTag("download")
                .build();
        parkingPositionSaved=true;
        WorkManager.getInstance(this).enqueue(downloadRequest);

    }
}