package com.mg.spstocks.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mg.spstocks.MainActivity;
import com.mg.spstocks.R;
import com.mg.spstocks.models.Coin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TransferFragment  extends Fragment {
    Spinner fromSpinner , toSpinner;
    ArrayAdapter<String> adapter;
    List<Coin> coinList;
    List <String> fromtolist;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    EditText editText;
    TextView toTxt;
    int from ,to ,type;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.transfer_fragment, null);
        fromSpinner = (Spinner) view.findViewById(R.id.fromspid);
        toSpinner = (Spinner) view.findViewById(R.id.tospid);
        coinList= ((MainActivity)getActivity()).getCoinList();
        fromtolist = new ArrayList<>();
        radioGroup = view.findViewById(R.id.radiogroup);
        radioButton1= view.findViewById(R.id.buyradio);
        radioButton2= view.findViewById(R.id.sellradio);
        editText= view.findViewById(R.id.fromedtxt);
        toTxt=view.findViewById(R.id.toedtxt);
        radioButton1.setChecked(true);
        initFacebookLogin(view);
        fromtolist.add("الليرة السورية");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioButton1.isChecked())
                {
                    type=0;
                }
                else
                {
                    type=1;
                }
                cal();
            }
        });
        for (int i=0;i<coinList.size();i++)
        {
            fromtolist.add(coinList.get(i).getCoin_name());
        }
        from =0;
        to = 0;
        type= 0;
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner ,fromtolist) {
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(getResources().getColor(R.color.grayDarkDark));

                return tv;
            }
        };
        adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (radioButton1.isChecked())
                {
                    type=0;
                }
                else
                {
                    type=1;
                }
                 from=fromSpinner.getSelectedItemPosition();
                 to = toSpinner.getSelectedItemPosition();
                cal();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (radioButton1.isChecked())
                {
                    type=0;
                }
                else
                {
                    type=1;
                }
                from=fromSpinner.getSelectedItemPosition();
                to = toSpinner.getSelectedItemPosition();
                cal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (radioButton1.isChecked())
                {
                    type=0;
                }
                else
                {
                    type=1;
                }
                cal();
            }
        });
        return view;
    }
    public  void cal()
    {
          double x=0;
           if (editText.getText().toString().equals(""))
           {
              x= 0;
           }
           else
           {
                String fromText= editText.getText().toString();
                x= Double.parseDouble(fromText);
           }

           double res =0;
           if (type == 0 ) {
               if (from == 0) {
                   if (to == 0) {
                       res = x;
                   } else {
                       res = x / coinList.get(to - 1).getLog().get(0).getBuy();
                   }
               } else {
                   if (to == 0) {
                       res = x * coinList.get(from - 1).getLog().get(0).getBuy();
                   } else {
                       res = x * coinList.get(from - 1).getLog().get(0).getBuy();
                       res = res / coinList.get(to - 1).getLog().get(0).getBuy();
                   }
               }
           }
           else
           {
               if (from == 0) {
                   if (to == 0) {
                       res = x;
                   } else {
                       res = x / coinList.get(to - 1).getLog().get(0).getSell();
                   }
               } else {
                   if (to == 0) {
                       res = x * coinList.get(from - 1).getLog().get(0).getSell();
                   } else {
                       res = x * coinList.get(from - 1).getLog().get(0).getSell();
                       res = res / coinList.get(to - 1).getLog().get(0).getSell();
                   }
               }
           }
           res = round(res,6);
         NumberFormat nf = NumberFormat.getNumberInstance(Locale.CANADA);
         nf.setMaximumFractionDigits(6);
         String rounded = nf.format(res);
           toTxt.setText(rounded);


    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private void initFacebookLogin(View container) {
       // LoginManager.getInstance().logOut();

        loginButton = container.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException exception) {
            }
        });
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
