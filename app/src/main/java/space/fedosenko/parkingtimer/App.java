package space.fedosenko.parkingtimer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationsChannel();
    }

    private void createNotificationsChannel(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("channel1","Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This channel is important for the Timer app");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
