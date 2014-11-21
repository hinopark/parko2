package com.plaxto.hino.mobile.park;

import static com.plaxto.hino.mobile.park.CommonUtilities.SENDER_ID;
import static com.plaxto.hino.mobile.park.CommonUtilities.displayMessage;
import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.util.Log;
import android.content.SharedPreferences;
import com.google.android.gcm.GCMBaseIntentService;
import com.plaxto.hino.mobile.park.MainActivity;
import com.plaxto.hino.mobile.park.R;

public class GCMIntentService extends GCMBaseIntentService {
	private static NotificationManager mNotificationManager;
	 private static int notificationID = 100;
	   private int numMessages = 0;
	private static final String TAG = "GCMIntentService";
	static String ada;
    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        StringBuilder sb = new StringBuilder();
		sb.append(LoginActivity.id);
		String idPetugas = sb.toString();
        ServerUtilities.registerGcm(context, idPetugas, registrationId);
        //Log.d("NAME", MainActivity.name);
      //  ServerUtilities.register(context, MainActivity.name, MainActivity.email, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
       // displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        ada = "ada gan";
        SharedPreferences settings = getSharedPreferences("push", 0);
 	   
	    settings.edit().putString("email", "ada gan").putString("pesan", message).putString("idPetugas", "as").putString("idParkir", "asd").commit();
       
	    displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
       // generateNotif(context, message);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = "Data Deleted";
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context,"Ada Error");
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, "Recovable Error");
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("akukiki", "ada");
        
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      

    }
    
    
    private static void generateNotif(Context context, String message){
    	Log.i("Start", "notification");

	      /* Invoking the default notification service */
	      NotificationCompat.Builder  mBuilder = 
	      new NotificationCompat.Builder(context);	

	      mBuilder.setContentTitle("Detecting Location...");
	      mBuilder.setContentText("");
	      mBuilder.setTicker("Detecting Your Location");
	      mBuilder.setSmallIcon(R.drawable.ic_launcher);
	      mBuilder.setOngoing(true);
	      /* Increase notification number every time a new notification arrives */
	      //mBuilder.setNumber(++numMessages);
	      
	      /* Creates an explicit intent for an Activity in your app */
	      Intent resultIntent = new Intent(context, MainActivity.class);

	      TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
	      stackBuilder.addParentStack(MainActivity.class);

	      /* Adds the Intent that starts the Activity to the top of the stack */
	      stackBuilder.addNextIntent(resultIntent);
	      PendingIntent resultPendingIntent =
	         stackBuilder.getPendingIntent(
	            0,
	            PendingIntent.FLAG_UPDATE_CURRENT
	         );

	      mBuilder.setContentIntent(resultPendingIntent);
	      //mBuilder.setAutoCancel(true); // On Click Close
	      
	      //mNotificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	      /* notificationID allows you to update the notification later on. */
	      mNotificationManager.notify(notificationID, mBuilder.build());
    }
}
