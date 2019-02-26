package com.mg.spstocks;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.mg.spstocks.fragments.CoinFragment;
import com.mg.spstocks.fragments.GoldFragment;
import com.mg.spstocks.fragments.TransferFragment;
import com.mg.spstocks.adapters.Coinadapter;
import com.mg.spstocks.adapters.Goldadapter;
import com.mg.spstocks.api.classes.Api;
import com.mg.spstocks.api.classes.ApiClient;
import com.mg.spstocks.api.classes.ApiResponse;
import com.mg.spstocks.models.Coin;
import com.mg.spstocks.models.gold;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private TextView mTextMessage;
    private  RecyclerView myRecyclerView;
    private Coinadapter mAdapter;
    private Goldadapter mAdapter2;
    public ArrayList<Coin> coinList;
    public   ArrayList<gold> goldList;
    ProgressDialog progressDialog;
    TextView textView;
    Toolbar toolbar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressDialog =ProgressDialog.getInstance();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        coinList= new ArrayList<Coin>();
        goldList=new ArrayList<gold>();
        progressDialog.show(this);
        getData();
        setNotification();
    }
   private void getData() {
        Api apiService = ApiClient.getClient().create(Api.class);
        Call<ApiResponse<List<Coin>>> call = apiService.getCoins();
        call.enqueue(new Callback<ApiResponse<List<Coin>>>() {
         @Override
         public void onResponse(Call<ApiResponse<List<Coin>>> call, Response<ApiResponse<List<Coin>>> response) {
             List<Coin> temp = response.body().getData();
             for (int i =0;i<temp.size();i++)
             {
                 coinList.add(temp.get(i));

             }

             getGold();
         }
         @Override
         public void onFailure(Call<ApiResponse<List<Coin>>> call, Throwable t) {
             Toast.makeText(MainActivity.this,  getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
         }
     });

    }
    public void getGold()
    {
        Api apiService = ApiClient.getClient().create(Api.class);
        Call<ApiResponse<List<gold>>> call = apiService.getGold();
        call.enqueue(new Callback<ApiResponse<List<gold>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<gold>>> call, Response<ApiResponse<List<gold>>> response) {
                List<gold> temp= response.body().getData();
                for (int i =0;i<temp.size();i++)
                {
                    goldList.add(temp.get(i));
                }
                progressDialog.cancel();

                CoinFragment fragment = new CoinFragment();
                loadFragment(fragment);
            }
            @Override
            public void onFailure(Call<ApiResponse<List<gold>>> call, Throwable t) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<gold> getGoldList() {  return  goldList;
    }

    public List<Coin> getCoinList() { return  coinList;
    }
    private  boolean loadFragment(Fragment fragment)
    {
        if (fragment  != null )
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fargment_container,fragment).commit();
                 return true;
        }
        return false;
    }
    public  void setNotification()
    {
                Calendar firingCal = Calendar.getInstance() ;
                Calendar calendar = Calendar.getInstance();

                firingCal.set(Calendar.HOUR_OF_DAY, 8);
                firingCal.set(Calendar.MINUTE, 0);
                firingCal.set(Calendar.SECOND, 0);
                long intendedTime= firingCal.getTimeInMillis();
                long currentTime= calendar.getTimeInMillis();

               Intent intent = new Intent(getApplicationContext(), Notification_reciver.class);
               PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
               AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (intendedTime >= currentTime) {
            alarmManager.setRepeating(AlarmManager.RTC,
                    intendedTime,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
        else
        {
            firingCal.add(Calendar.DAY_OF_MONTH, 1);
            intendedTime = firingCal.getTimeInMillis();
            alarmManager.setRepeating(AlarmManager.RTC,
                    intendedTime,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment= null;
        switch (menuItem.getItemId())
        {
            case R.id.navigation_coins:
                fragment = new CoinFragment();

                break;
            case R.id.navigation_gold:
                fragment = new GoldFragment();

                break;
            case R.id.navigation_tranfer:
                fragment = new TransferFragment();
                break;
        }
        return loadFragment(fragment);
    }
}
