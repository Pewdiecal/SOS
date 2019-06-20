package windows.nokialovers.sos;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity /**implements SensorEventListener**/ {
    private static final int START_STICKY = 0 ;
    LocationManager locationManager;
    GPSTracker gps1;
    SOSService sss;
    ImageView locationbtn;
    ImageView helpbtn;
    ImageView exit;
    ImageView settings;
    ImageView hbtn;
    TextView txtv;
    Context context;
    int count = 0;
    //final Counter2 timer2 = new Counter2(600000,1000);//10min 600000
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 60;
    ProgressDialog barProgressDialog;
    Handler updateBarHandler;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

       /** IntentFilter localIntentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(new Br(), localIntentFilter);**/

        String settingsTAG = "AppNameSettings";
        SharedPreferences prefs = getSharedPreferences(settingsTAG, MODE_PRIVATE);
        boolean rb0 = prefs.getBoolean("rb0", true);
        if(rb0 == true){
            // Do something
            prefs.edit().putBoolean("rb0",false).commit();
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }else if (rb0 == false){

        }

	     txtv = (TextView)findViewById(R.id.txt1);
         locationbtn = (ImageView)findViewById(R.id.imageView);
         helpbtn = (ImageView)findViewById(R.id.imageView2);
         exit = (ImageView)findViewById(R.id.imageView5);
         settings = (ImageView)findViewById(R.id.imageView3);
         hbtn = (ImageView)findViewById(R.id.imageView4);
      /**  senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);**/
        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        final String provider = locationManager.getBestProvider(new Criteria(), true);


        Intent i = new Intent(MainActivity.this, SOSService.class);
        i.putExtra("name", "SurvivingwithAndroid");
        MainActivity.this.startService(i);

        Intent in = getIntent();
        Intent intent = new Intent("MyCustomIntent");
        //Getting bundle
        Bundle b = in.getExtras();

        if(b == null){

        }else{
            String phnum1 = b.getString("phnum");
            String phnum2 = b.getString("phnum2");
            String phnum3 = b.getString("phnum3");

            savePreferences("StoredName12", phnum1);
            savePreferences("StoredName22", phnum2);
            savePreferences("StoredName32", phnum3);
        }

	     helpbtn.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 // TODO Auto-generated method stub
                 gps1 = new GPSTracker(MainActivity.this);

                 //count++;
                 //if (count == 2) {
                     //timer2.cancel();

                  //   count = 0;
                     //String time = "timer";
                     // SharedPreferences prefs = getSharedPreferences(time, MODE_PRIVATE);
                     // prefs.edit().putBoolean("acdec",true).commit();

                // }
                // if (count == 1) {
                     if (gps1.canGetLocation) {
                         // timer2.start();
                         loadSavedPreference();
                     } else {
                         gps1.showSettingsAlert();
                         // timer2.cancel();
                     }
                 //}

             }
         });

	     locationbtn.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 // TODO Auto-generated method stub


                 gps1 = new GPSTracker(MainActivity.this);
                 double latitude2 = gps1.getLatitude();
                 double longitude2 = gps1.getLongitude();

                 if (gps1.canGetLocation()) {

                     if (gps1.getLatitude() == 0.0 && gps1.getLongitude() == 0.0) {
                         Toast.makeText(getApplicationContext(), "No gps signal detected.", Toast.LENGTH_LONG).show();
                     } else {
                        //txtv.setText("Longitude: " + longitude2 + "\nLatiude" + latitude2);
                         Geocoder geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
                     try {
                             List<Address> addresses = geocoder.getFromLocation(latitude2, longitude2, 1);

                             if(addresses != null) {
                                 Address returnedAddress = addresses.get(0);
                                 final StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                                 for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                                     strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                                 }
                                 txtv.setText(strReturnedAddress.toString());
                             }
                             else{
                                 txtv.setText("Address not found! Please check or enable your internet connection and try again :)");
                             }
                         } catch (IOException e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                             txtv.setText("Address not found! Please check or enable your internet connection and try again :)");
                         }
                 }

                         }else {
                     gps1.showSettingsAlert();
                     }

                 }

         });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater1 = LayoutInflater.from(MainActivity.this);

                // Pss dialog
                final AlertDialog d1 = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Exit")
                        .setPositiveButton("Confirm exit", null) //Set to null. We override the onclick
                        .setNegativeButton("No", null)
                        .setMessage("Are you sure you want to exit ?")
                        .create();

                d1.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = d1.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // TODO Do something
                            System.exit(0);
                                d1.dismiss();


                            }
                        });

                        Button bt = d1.getButton(AlertDialog.BUTTON_NEGATIVE);
                        bt.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                d1.dismiss();

                            }
                        });

                    }
                });
                d1.show();
            }
        });

        hbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Pss dialog
                final AlertDialog d1 = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Help")
                        .setPositiveButton("OK", null) //Set to null. We override the onclick
                        .setMessage("To send the sos massages, you can send it by tapping the HELP! button or just shake your phone vigorously until you feel it vibrate.")
                        .create();

                d1.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = d1.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // TODO Do something

                                d1.dismiss();
                            }
                        });

                    }
                });
                d1.show();

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
                finish();
            }
        });

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

	/**@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}**/

	//@Override
	//public boolean onOptionsItemSelected(MenuItem item){
		
	//	switch (item.getItemId()) {
	//	case R.id.action_help:
			
	//		break;
	//	}
		
	//	switch (item.getItemId()) {
	//	case R.id.action_settings:
      //      Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
     //       startActivity(i);

           /** LayoutInflater inflater1 = LayoutInflater.from(MainActivity.this);

            // Pss dialog
            final AlertDialog d1 = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Authentication")
                    .setPositiveButton("Confirm Change", null) //Set to null. We override the onclick
                    .setNegativeButton("Cancel", null)
                    .setMessage("Please enter your password to access your settings.")
                    .create();
            final EditText input1 = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input1.setLayoutParams(lp1);
            d1.setView(input1);

            d1.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button b = d1.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something
                           // Intent in = getIntent();
                           // Bundle b = in.getExtras();
                            //Getting data from bundle
                            //String name = b.getString("input");

                            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(i);
                            //if(input1.getText().toString().equals(name)){
                              //  Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                                //startActivity(i);
                                d1.dismiss();
                                //Toast.makeText(getApplicationContext(),"Access Granted !", Toast.LENGTH_SHORT).show();
                            //}else{
                                //Toast.makeText(getApplicationContext(),"Access Denied !", Toast.LENGTH_SHORT).show();
                            //}


                        }
                    });

                    Button bt = d1.getButton(AlertDialog.BUTTON_NEGATIVE);
                    bt.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            d1.dismiss();
                            input1.setText("");
                        }
                    });

                }
            });
            d1.show();
**/
		//}
	//	return true;

	 //   }
   /** protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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

                    //loadSavedPreference();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
            }
        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }**/

   /** public class Counter2 extends CountDownTimer{
		 
        public Counter2(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
 
        @Override
        public void onFinish() {
        	//Do nothing
        }
 
        @Override
        public void onTick(long millisUntilFinished) {
            txt1.setText((millisUntilFinished/1000) + "");
            if ((millisUntilFinished/1000) == 599){
                loadSavedPreference();
            if((millisUntilFinished/1000) == 1){
            	timer2.start();
            }
            }
        }
    }**/

    public void loadSavedPreference(){
        gps1 = new GPSTracker(MainActivity.this);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        String phnumber1 = sp.getString("StoredName1", "");
        String phnumber2 = sp.getString("StoredName2", "");
        String phnumber3 = sp.getString("StoredName3", "");

        if(gps1.canGetLocation()){
            if(gps1.getLongitude() != 0.0 && gps1.getLatitude() != 0.0){
                SharedPreferences prefs = getSharedPreferences("text1", MODE_MULTI_PROCESS);
                String phnumber12 = prefs.getString("text", null);
                String phnumber22 = prefs.getString("text2",null);
                String phnumber32 = prefs.getString("text3",null);


                if(phnumber12.toString().equals("") && phnumber22.toString().equals("") && phnumber32.toString().equals("")){
                    Toast.makeText(getBaseContext(),
                            "You have not enter any of the phone number. No SMS alert will be sent out.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(),
                            "SOS message has been send",
                            Toast.LENGTH_SHORT).show();
                }


                double latitude = gps1.getLatitude();
                double longitude = gps1.getLongitude();

                String phoneNo = phnumber1;
                String message = "Need help !! Im in emergency.My current location is http://maps.google.com/?q=loc:" + latitude + "," + longitude + " .Please come at once.";
                if (phoneNo.length()>0 && message.length()>0){
                    sendSMS(phoneNo, message);
                }
                else{

                }


                String phoneNo2 = phnumber2;
                String message2 = "Need help !! Im in emergency.My current location is http://maps.google.com/?q=loc:" + latitude + "," + longitude + " Please come at once."  ;
                if (phoneNo2.length()>0 && message2.length()>0){
                    sendSMS(phoneNo2, message2);
                }
                else{

                }

                String phoneNo3 = phnumber3;
                String message3 = "Need help !! Im in emergency.My current location is http://maps.google.com/?q=loc:" + latitude + "," + longitude + " Please come at once."  ;
                if (phoneNo3.length()>0 && message3.length()>0){
                    sendSMS(phoneNo3, message3);
                }
                else{

                }

            }else{

                SharedPreferences prefs = getSharedPreferences("text1", MODE_MULTI_PROCESS);
                String phnumber12 = prefs.getString("text", null);
                String phnumber22 = prefs.getString("text2",null);
                String phnumber32 = prefs.getString("text3",null);

                if(phnumber12.toString().equals("") && phnumber22.toString().equals("") && phnumber32.toString().equals("")){
                    Toast.makeText(getBaseContext(),
                            "You have not enter any of the phone number. No SMS alert will be sent out",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(),
                            "SOS message has been sent" ,
                            Toast.LENGTH_SHORT).show();
                }



                String phoneNo = phnumber1;
                String message = "Need help !! Im in emergency. Please come at once."  ;
                if (phoneNo.length()>0 && message.length()>0){
                    sendSMS(phoneNo, message);
                }
                else{

                }

                String phoneNo2 = phnumber2;
                String message2 =  "Need help !! Im in emergency. Please come at once."  ;
                if (phoneNo2.length()>0 && message2.length()>0){
                    sendSMS(phoneNo2, message2);
                }
                else{

                }
                String phoneNo3 = phnumber3;
                String message3 =  "Need help !! Im in emergency. Please come at once."  ;
                if (phoneNo3.length()>0 && message3.length()>0){
                    sendSMS(phoneNo3, message3);
                }
                else{

                }
            }
        }else{
            gps1.showSettingsAlert();
        }

    }
    private void savePreferences(String key, String value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }


    }