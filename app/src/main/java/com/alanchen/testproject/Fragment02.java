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
import android.widget.AdapterView;
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

public class Fragment02 extends Fragment {
    private static String TAG = "testproject";

    private ImageView imageView;
    private TextView e_info;
    private TextView e_memo;
    private TextView e_category;
    private TextView e_url;
    private JSONObject res;
    private Bitmap image;

    ListView mListView2;
    ArrayList<String> f_name_ch_list;
    JSONArray results;
    ArrayList<Bitmap> flowers_images;

    class ListAdapter2 extends ArrayAdapter<String> {
        ImageView list_image;
        TextView f_name_ch;
        TextView f_alsoknown;
        public ListAdapter2(@NonNull Context context, int resource, ArrayList<String> f_name_ch_list) {
            super(context, resource, f_name_ch_list);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item2, null);
            }
            try {
                JSONObject res = (JSONObject) results.get(position);
                list_image = convertView.findViewById(R.id.image);
                f_name_ch = convertView.findViewById(R.id.f_name_ch);
                f_alsoknown = convertView.findViewById(R.id.f_alsoknown);
                list_image.setImageBitmap(flowers_images.get(position));
                f_name_ch.setText(f_name_ch_list.get(position));
                f_alsoknown.setText(res.getString("F_AlsoKnown"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment02, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        imageView = getView().findViewById(R.id.image);
        e_info = getView().findViewById(R.id.e_info);
        e_memo = getView().findViewById(R.id.e_memo);
        e_category = getView().findViewById(R.id.e_category);
        e_url = getView().findViewById(R.id.e_url);
        mListView2 = getView().findViewById(R.id.listview2);
        try{
            if(image!=null)
                imageView.setImageBitmap(image);
            e_info.setText(res.getString("E_Info"));
            e_memo.setText(res.getString("E_Memo"));
            e_category.setText(res.getString("E_Category"));
            final String url = res.getString("E_URL");
            e_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    it.setData(Uri.parse(url));
                    startActivity(Intent.createChooser(it,""));
                }
            });
            ListAdapter2 listAdapter2 = new ListAdapter2(getActivity(), R.layout.list_item, f_name_ch_list);
            mListView2.setAdapter(listAdapter2);
            mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        JSONObject res = (JSONObject) results.get(i);
                        ((MainActivity)getActivity()).showFlowersDetail(res, flowers_images.get(i), f_name_ch_list.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {

        }
    }

    public void setResult(JSONObject res, Bitmap image)
    {
        this.res = res;
        this.image = image;
    }

    public void setFlowerInfo(ArrayList<String> f_name_ch_list, JSONArray results, ArrayList<Bitmap> flowers_images )
    {
        this.f_name_ch_list = f_name_ch_list;
        this.results = results;
        this.flowers_images = flowers_images;
    }

}
