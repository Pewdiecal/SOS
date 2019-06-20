package windows.nokialovers.sos;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Array;

public class SettingsActivity extends Activity {
	public static final int PICK_CONTACT_REQUEST_1 = 1;
	public static final int PICK_CONTACT_REQUEST_2 = 2;
	public static String message;
	public static String name1;
	public static String name2;
	public static String name3;
	public static String number1;
	public static String number2;
	public static String number3;
	private boolean e = true;
	EditText ctctxt1;
	EditText ctctxt2;
	EditText ctctxt3;
	ImageButton addctcbtn1;
	ImageButton addctcbtn2;
	ImageButton addctcbtn3;
    ImageView ok;
    ImageView reset;
	CheckBox shake;
	CheckBox gps;
	LocationManager locationManager;
    GPSTracker gps1;
    Context context;

    @Override
    public void onBackPressed() {
        //Include the code here
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
        return;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		 ctctxt1 = (EditText)findViewById(R.id.ctc1);
		 ctctxt2 = (EditText)findViewById(R.id.ctctxt2);
		 ctctxt3 = (EditText)findViewById(R.id.ctctxt3);
		 addctcbtn1 = (ImageButton)findViewById(R.id.addctcbtn1);
		 addctcbtn2 = (ImageButton)findViewById(R.id.addctcbtn2);
		 addctcbtn3 = (ImageButton)findViewById(R.id.addctcbtn3);
        ok = (ImageView)findViewById(R.id.okbtn1);
         reset = (ImageView)findViewById(R.id.resetbtn);
        gps1 = new GPSTracker(this);
       loadSavedPreference();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedata();
                finish();
            }
       });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctctxt1.setText("");
                ctctxt2.setText("");
                ctctxt3.setText("");

                SharedPreferences.Editor editor = getSharedPreferences("text1",MODE_MULTI_PROCESS).edit();
                editor.putString("text", "");
                editor.apply();

                SharedPreferences.Editor editor1 = getSharedPreferences("text1",MODE_MULTI_PROCESS).edit();
                editor1.putString("text2", "");
                editor1.apply();

                SharedPreferences.Editor editor2 = getSharedPreferences("text1",MODE_MULTI_PROCESS).edit();
                editor2.putString("text3", "");
                editor2.apply();
            }
        });

		addctcbtn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent localIntent = new Intent("android.intent.action.PICK", Uri.parse("content://contacts"));
		          localIntent.setType("vnd.android.cursor.dir/phone_v2");
		          SettingsActivity.this.startActivityForResult(localIntent, 1);
			}
		});

		addctcbtn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent localIntent = new Intent("android.intent.action.PICK", Uri.parse("content://contacts"));
		          localIntent.setType("vnd.android.cursor.dir/phone_v2");
		          SettingsActivity.this.startActivityForResult(localIntent, 2);
			}
		});

		addctcbtn3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent localIntent = new Intent("android.intent.action.PICK", Uri.parse("content://contacts"));
		          localIntent.setType("vnd.android.cursor.dir/phone_v2");
		          SettingsActivity.this.startActivityForResult(localIntent, 3);
			}
		});

	}

	@Override
	protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
	  {
	    super.onActivityResult(paramInt1, paramInt2, paramIntent);
	    if (paramInt2 == -1)
	    {
	      Uri localUri = paramIntent.getData();
	      String[] arrayOfString = { "data1", "display_name", "_id" };
	      Cursor localCursor = getContentResolver().query(localUri, arrayOfString, null, null, null);
	      localCursor.moveToFirst();
	      int i = localCursor.getColumnIndex("data1");
	      int j = localCursor.getColumnIndex("display_name");
	      String str1 = localCursor.getString(i);
	      String str2 = localCursor.getString(j);
	      if (paramInt1 == 1)
	      {
	          this.ctctxt1.setText(str1 + " [" + str2 + "]");
	          number1 = str1;
	          name1 = str2;

              SharedPreferences.Editor editor = getSharedPreferences("text1",MODE_MULTI_PROCESS).edit();
              editor.putString("text", number1.toString());
              editor.apply();
	      }
	      if (paramInt1 == 2)
	      {
	          this.ctctxt2.setText(str1 + " [" + str2 + "]");
	          number2 = str1;
	          name2 = str2;

              SharedPreferences.Editor editor = getSharedPreferences("text1",MODE_MULTI_PROCESS).edit();
              editor.putString("text2", number2.toString());
              editor.apply();
	      }
	      if (paramInt1 == 3){
	    	  this.ctctxt3.setText(str1 + " " + "[" + str2 + "]" );
	    	  number3 = str1;
	    	  name3 = str2;

              SharedPreferences.Editor editor = getSharedPreferences("text1",MODE_MULTI_PROCESS).edit();
              editor.putString("text3", number3.toString());
              editor.apply();

	      }
	    }
	  }

/**	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}**/

    private void loadSavedPreference(){
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        String phnumber1 = sp.getString("StoredName1","");
        String phnumber2 = sp.getString("StoredName2", "");
        String phnumber3 = sp.getString("StoredName3", "");

        ctctxt1.setText(phnumber1);
        ctctxt2.setText(phnumber2);
        ctctxt3.setText(phnumber3);
    }
    private void savePreferences(String key, String value){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private void savedata(){

        if(ctctxt1.getText().toString().equals("")){
            SharedPreferences.Editor editor = getSharedPreferences("text1",MODE_MULTI_PROCESS).edit();
            editor.putString("text", "");
            editor.apply();
        }
        if(ctctxt2.getText().toString().equals("")){
            SharedPreferences.Editor editor = getSharedPreferences("text1",MODE_MULTI_PROCESS).edit();
            editor.putString("text2", "");
            editor.apply();
        }
        if(ctctxt3.getText().toString().equals("")){
            SharedPreferences.Editor editor = getSharedPreferences("text1",MODE_MULTI_PROCESS).edit();
            editor.putString("text3", "");
            editor.apply();
        }

        savePreferences("StoredName1", ctctxt1.getText().toString());
        savePreferences("StoredName2", ctctxt2.getText().toString());
        savePreferences("StoredName3", ctctxt3.getText().toString());

        String phnumber = ctctxt1.getText().toString();
        String phnumber2 = ctctxt2.getText().toString();
        String phnumber3 = ctctxt3.getText().toString();

        //Creating Bundle object
        Bundle b = new Bundle();

        //Storing data into bundle

        b.putString("phnum", phnumber);
        b.putString("phnum2", phnumber2);
        b.putString("phnum3", phnumber3);

        //Creating Intent object
        Intent in = new Intent(getApplicationContext(), MainActivity.class);

        //Storing bundle object into intent
        in.putExtras(b);

        startActivity(in);



        if (ctctxt1.getText().toString().equals("")&& ctctxt2.getText().toString().equals("") && ctctxt3.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "You have not enter any of the phone number. No SMS alert will be sent out.", Toast.LENGTH_LONG).show();
        }
    }


}
