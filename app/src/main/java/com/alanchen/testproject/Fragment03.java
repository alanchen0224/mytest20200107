package com.alanchen.testproject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment03 extends Fragment {
    private static String TAG = "testproject";
    private LinearLayout container;
    private ImageView imageView;
    private TextView f_name_ch;
    private TextView f_name_latin;
    private TextView f_alsoknown;
    private TextView f_brief;
    private TextView f_feature;
    private TextView f_function_application;
    private TextView f_update;
    private JSONObject res;
    private Bitmap image;
    private String f_name_ch_str;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment03, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        imageView = getView().findViewById(R.id.image);
        f_name_ch = getView().findViewById(R.id.f_name_ch);
        f_name_latin = getView().findViewById(R.id.f_name_latin);
        f_alsoknown = getView().findViewById(R.id.f_alsoknown);
        f_brief = getView().findViewById(R.id.f_brief);
        f_feature = getView().findViewById(R.id.f_feature);
        f_function_application = getView().findViewById(R.id.f_function_application);
        f_update = getView().findViewById(R.id.f_update);

        if( res != null && image != null) {
            try {
                imageView.setImageBitmap(image);
                f_name_ch.setText(f_name_ch_str);
                f_name_latin.setText(res.getString("F_Name_Latin"));
                f_alsoknown.setText(res.getString("F_AlsoKnown"));
                f_brief.setText(res.getString("F_Brief"));
                f_feature.setText(res.getString("F_Feature"));
                f_function_application.setText(res.getString("F_Function＆Application"));
                f_update.setText(getResources().getString(R.string.last_update_time) + res.getString("F_Update"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void setInfo(JSONObject res, Bitmap image, String f_name_ch_str)
    {
        this.res = res;
        this.image = image;
        this.f_name_ch_str = f_name_ch_str;
    }
}
