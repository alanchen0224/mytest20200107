package com.alanchen.testproject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Fragment01 extends Fragment {

    private ListView mListView;
    private static String TAG = "testproject";

    private JSONArray results = new JSONArray();
    private ArrayList<Bitmap> images = new ArrayList<>();
    private ArrayList<String> e_url = new ArrayList<>();

    class ListAdapter extends ArrayAdapter<String>{
        ImageView image;
        TextView e_name;
        TextView e_info;
        TextView e_memo;
        public ListAdapter(@NonNull Context context, int resource, ArrayList<String> e_url) {
            super(context, resource, e_url);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
            }
            try {
                JSONObject res = (JSONObject) results.get(position);
                image = convertView.findViewById(R.id.image);
                e_name = convertView.findViewById(R.id.e_name);
                e_info = convertView.findViewById(R.id.e_info);
                e_memo = convertView.findViewById(R.id.e_memo);
                image.setImageBitmap(images.get(position));
                e_name.setText(res.getString("E_Name"));
                e_info.setText(res.getString("E_Info"));
                if( res.getString("E_Memo").length() > 0 )
                    e_memo.setText(res.getString("E_Memo"));
                else
                    e_memo.setText(getResources().getString(R.string.noopen_info));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment01, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mListView = getView().findViewById(R.id.listview);
        ListAdapter listAdapter = new ListAdapter(getActivity(), R.layout.list_item, e_url);
        mListView.setAdapter(listAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject res = (JSONObject) results.get(i);
                    String e_name = res.getString("E_Name");
                    ((MainActivity)getActivity()).showResult(res, images.get(i), e_name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setData(ArrayList<String> e_url, JSONArray results, ArrayList<Bitmap> images )
    {
        this.results = results;
        this.images = images;
        this.e_url = e_url;
    }

}
