package windows.nokialovers.sos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Windows EP on 10/21/14.
 */
public class BootBreceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SOSService.class));

    }
}
