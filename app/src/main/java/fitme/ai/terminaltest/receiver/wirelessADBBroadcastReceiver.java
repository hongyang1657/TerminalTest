package fitme.ai.terminaltest.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fitme.ai.terminaltest.service.ADBService;
import fitme.ai.terminaltest.utils.L;


/**
 * Created by fez on 2017/3/4.
 */
public class wirelessADBBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction().toString();
        if (action.equals("action.start.adbConfig")) {
            L.i("onReceive: 开启远程ADB调试");
            Intent startIntent = new Intent(context, ADBService.class);
            //startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(startIntent);
        }
    }
}
