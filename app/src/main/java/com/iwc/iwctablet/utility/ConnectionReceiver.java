package com.iwc.iwctablet.utility;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.iwc.iwctablet.R;
import com.google.android.material.button.MaterialButton;

public class ConnectionReceiver extends BroadcastReceiver {
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (isConnected(context)) {
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
        } else {
            showDialog();
        }
    }

    public boolean isConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showDialog() {
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.no_connection_dialog);
        dialog.setCanceledOnTouchOutside(false);
        MaterialButton btnRetry = dialog.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(view1 -> dialog.dismiss());
        dialog.show();
    }

}
