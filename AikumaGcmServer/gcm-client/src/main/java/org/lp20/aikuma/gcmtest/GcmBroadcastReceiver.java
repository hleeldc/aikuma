/* Most of the code was taken from an example on the android web site.
 * https://developer.android.com/google/gcm/client.html
 */
package org.lp20.aikuma.gcmtest;

import android.content.*;
import android.app.*;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}

