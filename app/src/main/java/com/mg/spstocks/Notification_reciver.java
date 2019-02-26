package com.mg.spstocks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.mg.spstocks.api.classes.Api;
import com.mg.spstocks.api.classes.ApiClient;
import com.mg.spstocks.api.classes.ApiResponse;
import com.mg.spstocks.models.Coin;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notification_reciver extends BroadcastReceiver {
     public Coin coin;



    @Override
    public void onReceive(final Context context, Intent intent) {

        Api apiService = ApiClient.getClient().create(Api.class);
        Call<ApiResponse<List<Coin>>> call = apiService.getCoins();
        call.enqueue(new Callback<ApiResponse<List<Coin>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Coin>>> call, Response<ApiResponse<List<Coin>>> response) {
                List<Coin> temp = response.body().getData();
                coin =temp.get(0);
                getdata(context);
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Coin>>> call, Throwable t) {


            }
        });

    }

    public void getdata(Context context)
    {
        NotificationManager notificationManager = ( NotificationManager)  context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context,MainActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.spstocks)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(coin.getCoin_name()+"  "+context.getResources().getString(R.string.Buy)+" :   " +coin.getLog().get(0).getBuy()+"  " + context.getResources().getString(R.string.Sell)+" :   " +coin.getLog().get(0).getSell())
                .setAutoCancel(true);
        notificationManager.notify(100, builder.build());
    }
}
