package windows.nokialovers.sos;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by Windows EP on 10/20/14.
 */
public class SOSService extends Service implements SensorEventListener{
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 5000;
    GPSTracker gps1;
    Context context;
    Intent intent;
    private static final String TAG = "SOSService";
    public Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {


                   //Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    //v.vibrate(3000);
                    //Set the pattern for vibration
                    //long pattern[]={0,200,100,300,400};
                    //Start the vibration
                    vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    //start vibration with repeated count, use -1 if you don't want to repeat the vibration
                    vibrator.vibrate(5000);
                    loadSavedPreference();
                   // String time = "timer";
                   // SharedPreferences prefs = getSharedPreferences(time, MODE_PRIVATE);
                   // prefs.edit().putBoolean("acdec",true).commit();
                }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    protected void onResume() {

        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause()
    {
        // unregister listener
        senSensorManager.unregisterListener(this);

    }

    private void sendSMS(String phoneNo, String message) {
        // TODO Auto-generated method stub
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        /**   registerReceiver(new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        switch (getResultCode())
        {
        case Activity.RESULT_OK:

        Toast.makeText(getBaseContext(), "SMS sent" ,
        Toast.LENGTH_SHORT).show();
        break;
        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
        Toast.makeText(getBaseContext(), "Generic failure",
        Toast.LENGTH_SHORT).show();
        break;
        case SmsManager.RESULT_ERROR_NO_SERVICE:
        Toast.makeText(getBaseContext(), "No service",
        Toast.LENGTH_SHORT).show();
        break;
        case SmsManager.RESULT_ERROR_NULL_PDU:
        Toast.makeText(getBaseContext(), "Null PDU",
        Toast.LENGTH_SHORT).show();
        break;
        case SmsManager.RESULT_ERROR_RADIO_OFF:
        Toast.makeText(getBaseContext(), "Radio off",
        Toast.LENGTH_SHORT).show();
        break;
        }
        }
        }, new IntentFilter(SENT));

         //---when the SMS has been delivered---
         registerReceiver(new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        switch (getResultCode())
        {
        case Activity.RESULT_OK:
        Toast.makeText(getBaseContext(), "SMS delivered",
        Toast.LENGTH_SHORT).show();
        break;
        case Activity.RESULT_CANCELED:
        Toast.makeText(getBaseContext(), "SMS not delivered",
        Toast.LENGTH_SHORT).show();
        break;
        }
        }
        }, new IntentFilter(DELIVERED));    **/

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNo, null, message, sentPI, deliveredPI);
    }
    private void loadSavedPreference(){

        SharedPreferences prefs = getSharedPreferences("text1", MODE_MULTI_PROCESS);
        String phnumber1 = prefs.getString("text", null);
        String phnumber2 = prefs.getString("text2",null);
        String phnumber3 = prefs.getString("text3",null);

        gps1 = new GPSTracker(SOSService.this);

        if(gps1.canGetLocation()){
            if(gps1.getLongitude() != 0.0 && gps1.getLatitude() != 0.0){
                if(phnumber1.toString().equals(null) && phnumber2.toString().equals(null) && phnumber3.toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "You have not enter any of the phone number. No SMS alert will be sent out.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "SOS message has been sent" ,
                            Toast.LENGTH_SHORT).show();
                }

                double latitude = gps1.getLatitude();
                double longitude = gps1.getLongitude();

                String phoneNo = phnumber1;
                String message = "Need help !! Im in emergency.My current location is http://maps.google.com/?q=loc:" + longitude + latitude + " Please come at once.";
                if (phoneNo.length()>0 && message.length()>0){
                    sendSMS(phoneNo, message);
                }else{

                }

                String phoneNo2 = phnumber2;
                String message2 = "Need help !! Im in emergency.My current location is http://maps.google.com/?q=loc:" + longitude + latitude + " Please come at once.";
                if (phoneNo2.length()>0 && message2.length()>0){
                    sendSMS(phoneNo2, message2);
                }else{

                }

                String phoneNo3 = phnumber3;
                String message3 = "Need help !! Im in emergency.My current location is http://maps.google.com/?q=loc:" + longitude + latitude + " Please come at once.";
                if (phoneNo3.length()>0 && message3.length()>0){
                    sendSMS(phoneNo3, message3);
                }else{

                }

            }else{

                SharedPreferences prefs1 = getSharedPreferences("text1", MODE_MULTI_PROCESS);
                String phnumber12 = prefs1.getString("text", null);
                String phnumber22 = prefs1.getString("text2",null);
                String phnumber32 = prefs1.getString("text3",null);

                if(phnumber12.toString().equals("") && phnumber22.toString().equals("") && phnumber32.toString().equals("")){
                    Toast.makeText(getBaseContext(),
                            "You have not enter any of the phone number. No SMS alert will be sent out.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(),
                            "SOS message has been sent",
                            Toast.LENGTH_SHORT).show();
                }

                String phoneNo = phnumber1;
                String message = "Need help !! Im in emergency. Please come at once."  ;
                if (phoneNo.length()>0 && message.length()>0){
                    sendSMS(phoneNo, message);
                }else{

                }

                String phoneNo2 = phnumber2;
                String message2 =  "Need help !! Im in emergency. Please come at once."  ;
                if (phoneNo2.length()>0 && message2.length()>0){
                    sendSMS(phoneNo2, message2);
                }else{

                }

                String phoneNo3 = phnumber3;
                String message3 =  "Need help !! Im in emergency. Please come at once."  ; ;
                if (phoneNo3.length()>0 && message3.length()>0){
                    sendSMS(phoneNo3, message3);
                }else{

                }
            }
        }else{
            gps1.showSettingsAlert();
        }

    }

}
