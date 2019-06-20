package windows.nokialovers.sos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;
import android.os.Vibrator;

/**
 * Created by Windows EP on 10/26/14.
 */
public class Br extends BroadcastReceiver {
    Handler h;
    int press_count = 0;
    Runnable r;
    Context context;




    @Override
    public void onReceive(Context paramContext, Intent paramIntent) {

        while ((!paramIntent.getAction().equals("android.intent.action.SCREEN_ON")) && (!paramIntent.getAction().equals("android.intent.action.SCREEN_OFF")));

                this.press_count = (1 + this.press_count);
                if (this.press_count == 1)
                {
                    this.h = new Handler();
                    this.r = new Runnable()
                    {
                        public void run()
                        {
                           press_count = 0;

                        }
                    };
                    this.h.postDelayed(this.r, 5000L);

                    return;
                }
                if ((this.press_count == 3))
                {
                    Toast.makeText(context, "Done",Toast.LENGTH_LONG).show();

                }
    }

}
