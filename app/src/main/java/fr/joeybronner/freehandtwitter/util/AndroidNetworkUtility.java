package fr.joeybronner.freehandtwitter.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AndroidNetworkUtility {

    public boolean isConnected(Context ctx){
        boolean flag = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            flag = true;
        }
        return flag;
    }
}