package space.fedosenko.parkingtimer;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import space.fedosenko.parkingtimer.GsonClassesForLocation.Location;
import space.fedosenko.parkingtimer.Model.Coordinate;
import space.fedosenko.parkingtimer.Model.Model;

public class BackgroundLocationWorker extends Worker {

    private static final String TAG = "BackgroundLocationWorker";
    public static final String TIME_TO_RETURN_IN_SECONDS = "time left";
    private static final int REQUEST_LOCATION = 1;
    private Context mContext;
    private Coordinate currentCoordinate, savedCoordinate;
    private long timeToReturn, timeLeft; //in seconds
    //delete after test
    int status = 0;

    public BackgroundLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        timeLeft= inputData.getLong(TIME_TO_RETURN_IN_SECONDS, -1);
        timeToReturn = 0;
        if (timeLeft != -1) {
            while (timeLeft-120>timeToReturn){

                getCurrentLocation();
                try {
                    sendNotification("time left=" +
                                    timeToReturn +
                                    " min. GD used" +
                            status +
                            ". Sleep for " +(timeLeft-timeToReturn)/2+
                            "min");

                    Thread.sleep(1000*(timeLeft-timeToReturn)/2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timeLeft=timeLeft-(timeLeft-timeToReturn)/2;

                status++;


                //sendNotification();
            }
            sendNotification("Job is done! We've used "+status+" distance researches");

        }
        Log.d(TAG, "doWork: done!!!");
        return Result.success();
    }

    private void sendNotification(String notificationText){


        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                        + savedCoordinate
                        +"&travelmode=walking"));
        PendingIntent pendingIntent=PendingIntent.getActivity(mContext,2,intent,0);

        String stStatus;
        //testing code

        //end of testing code
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext,"channel1")
                .setContentTitle("Hello")
                .setSmallIcon(R.drawable.ic_action_name)
                .setContentText(notificationText)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager=NotificationManagerCompat.from(mContext);
        manager.notify(1,builder.build());
    }
    private void getCurrentLocation() {


        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.getFusedLocationProviderClient(mContext)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(mContext)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int lastLocationIndex = locationResult.getLocations().size() - 1;
                            Log.d(TAG, "onLocationResult: current location is:"
                                    +locationResult.getLocations().get(lastLocationIndex).getLatitude()
                                    +", "
                                    +locationResult.getLocations().get(lastLocationIndex).getLongitude()
                            );
                            if (savedCoordinate==null){
                                savedCoordinate=new Coordinate(
                                        locationResult.getLocations().get(lastLocationIndex).getLatitude(),
                                        locationResult.getLocations().get(lastLocationIndex).getLongitude());
                            } else{
                                currentCoordinate= new Coordinate(
                                        locationResult.getLocations().get(lastLocationIndex).getLatitude(),
                                        locationResult.getLocations().get(lastLocationIndex).getLongitude());
                                getData();
                            }
                        }
                    }
                    }, Looper.getMainLooper());

    }
    private void getData() {
        final Gson gson = new Gson();



        String coord1 = savedCoordinate.toString();
        String coord2 = currentCoordinate.toString();
        StringRequest request = new StringRequest(Request.Method.GET,
                "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="
                        +coord1
                        +"&destinations="
                        +coord2
                        +"&mode=walking&key="
                /*google developer key*/, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Location location = gson.fromJson(response, space.fedosenko.parkingtimer.GsonClassesForLocation.Location.class);

                Log.d(TAG, "onResponse: distance to return"+location.getDistance());
                timeToReturn=location.getTimeToReturn();
                Log.d(TAG, "onResponse: time to return"+location.getTimeToReturn());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

            }
        });
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
        queue.start();
    }
}
