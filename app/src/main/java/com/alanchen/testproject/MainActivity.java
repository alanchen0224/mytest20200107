package com.alanchen.testproject;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alanchen.testproject.WebService.WebService;
import com.alanchen.testproject.WebService.WebServiceCallBack;
import com.alanchen.testproject.WebService.WebServiceCallBackObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WebServiceCallBack {
    private Dialog progressDialog;
    private TextView title;
    private Fragment01 mFragment01;
    private Fragment02 mFragment02;
    private Fragment03 mFragment03;
    private FragmentManager mFragmentMgr;
    private ImageButton backbtn;
    private JSONArray fragment01Results = new JSONArray();
    private ArrayList<Bitmap> fragment01Images = new ArrayList<>();
    private ArrayList<String> e_url = new ArrayList<>();

    private JSONArray fragment02Results = new JSONArray();
    private ArrayList<Bitmap> fragment02Images = new ArrayList<>();
    private ArrayList<String> f_name_ch_list = new ArrayList<>();
    private static String TAG = "testproject";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showProgressDialog();
        title = findViewById(R.id.title);
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(backbtnOnClick);
        mFragment01 = new Fragment01();
        mFragment02 = new Fragment02();
        mFragment03 = new Fragment03();
        mFragmentMgr = getFragmentManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] params = { WebService.WsGetTotalInfo_API, WebService.WsGetTotalInfo_API_PATH, "GET" };
                new WebService(MainActivity.this).execute(params);
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void showResult(JSONObject res, Bitmap image, final String e_name)
    {
        showProgressDialog();
        mFragment02.setResult(res, image);
        title.setText(e_name);
        //mFragmentMgr.executePendingTransactions();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] params = { WebService.WsGetDetails_API, WebService.WsGetDetails_API_PATH + "&q=" + e_name, "GET" };
                new WebService(MainActivity.this).execute(params);
            }
        }).start();
    }

    public void showFlowersDetail(JSONObject res, Bitmap image, String f_name_ch )
    {
        mFragment03.setInfo(res, image, f_name_ch);
        mFragmentMgr.beginTransaction()
                .replace(R.id.mainLay, mFragment03, "TAG-mFragment03")
                .addToBackStack(null)
                .commit();
    }

    public void backToPrevFragment()
    {
        if(mFragmentMgr.getBackStackEntryCount()==1) {
            f_name_ch_list.clear();
            fragment02Results = new JSONArray();
            fragment02Images.clear();
        }
        mFragmentMgr.popBackStack();
    }

    private View.OnClickListener backbtnOnClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            backToPrevFragment();
        }
    };

    @Override
    public void onWebServiceCallBack(WebServiceCallBackObject webServiceCallBackObject) {
        String action = webServiceCallBackObject.action;
        int errorCode = webServiceCallBackObject.errorCode;
        String data = webServiceCallBackObject.data;
        Log.d(TAG, "--->onWebServiceCallBack, action="+action);
        if( action.equals(WebService.WsGetTotalInfo_API) )
        {
            if( errorCode == 0 )
            {
                try
                {
                    JSONObject dataJSON = new JSONObject(data);
                    fragment01Results = dataJSON.getJSONObject("result").getJSONArray("results");
                    getFragment01Images();
                }
                catch (JSONException e)
                {

                }
            }
        }
        else if ( action.equals(WebService.WsGetDetails_API) )
        {
            if( errorCode == 0 )
            {
                try
                {
                    JSONObject dataJSON = new JSONObject(data);
                    fragment02Results = dataJSON.getJSONObject("result").getJSONArray("results");
                    getFragment02Images();
                }
                catch (JSONException e)
                {

                }
            }
        }
    }
    private void getFragment01Images() {
        AsyncTask<Void, Void, Integer> downloadImages = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    for( int i=0; i<fragment01Results.length(); i++ )
                    {
                        JSONObject res = (JSONObject) fragment01Results.get(i);
                        String E_Pic_URL =  res.getString("E_Pic_URL");
                        e_url.add(res.getString("E_URL"));
                        URL url = new URL(E_Pic_URL);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        InputStream inputStream = connection.getInputStream();
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        fragment01Images.add( BitmapFactory.decodeStream(bufferedInputStream) );
                    }
                    return 0;
                }catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            }
            @Override
            protected void onPostExecute(Integer code) {
                Log.d(TAG, "--->fragment01Images.size()=" + fragment01Images.size());
                if(code == 0)
                {
                    mFragment01.setData(e_url, fragment01Results, fragment01Images);
                    mFragmentMgr.beginTransaction()
                            .replace(R.id.mainLay, mFragment01, "TAG-mFragment01")
                            .commit();
                    //backbtn.setVisibility(View.VISIBLE);
                    closeProgressDialog();
                }
                else
                {
                    showErrorAlert(getResources().getString(R.string.conntect_error));
                }
            }
        };
        downloadImages.execute();
    }

    private void getFragment02Images() {
        AsyncTask<Void, Void, Integer> downloadImages = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                String F_Pic_URL = "";
                try {
                    for( int i=0; i<fragment02Results.length(); i++ )
                    {
                        JSONObject res = (JSONObject) fragment02Results.get(i);
                        f_name_ch_list.add(res.getString("\uFEFFF_Name_Ch"));
                        URL url = null;
                        if( res.getString("F_Pic01_URL").length()>0 )
                            F_Pic_URL = res.getString("F_Pic01_URL").replace("?", "");
                        else if( res.getString("F_Pic02_URL").length()>0 )
                            F_Pic_URL = res.getString("F_Pic02_URL").replace("?", "");
                        else if( res.getString("F_Pic03_URL").length()>0 )
                            F_Pic_URL = res.getString("F_Pic03_URL").replace("?", "");
                        else if( res.getString("F_Pic04_URL").length()>0 )
                            F_Pic_URL = res.getString("F_Pic04_URL").replace("?", "");
                        else{
                            fragment02Images.add( BitmapFactory.decodeResource(getResources(), R.drawable.oops));
                            continue;
                        }
                        url = new URL(F_Pic_URL);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        int code = connection.getResponseCode();
                        if( code != 200){
                            fragment02Images.add(BitmapFactory.decodeResource(getResources(), R.drawable.oops));
                            continue;
                        }
                        InputStream inputStream = connection.getInputStream();
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        //fragment02Images.add( toByteArray(inputStream) );
                        fragment02Images.add( BitmapFactory.decodeStream(bufferedInputStream) );
                    }
                    return 0;
                }catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            }
            @Override
            protected void onPostExecute(Integer code) {
                closeProgressDialog();
                if(code == 0)
                {
                    mFragment02.setFlowerInfo(f_name_ch_list, fragment02Results, fragment02Images);
                    mFragmentMgr.beginTransaction()
                            .replace(R.id.mainLay, mFragment02, "TAG-mFragment02")
                            .addToBackStack(null)
                            .commit();
                }
                else
                {
                    showErrorAlert(getResources().getString(R.string.conntect_error));
                }
            }
        };
        downloadImages.execute();
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.progress, null);
        builder.setCancelable(false);
        builder.setView(layout);
        progressDialog = builder.create();
        progressDialog.show();
    }
    public void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    public void showErrorAlert(String msg){
        closeProgressDialog();
        android.support.v7.app.AlertDialog.Builder builder= new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.warning));
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton(getResources().getString(R.string.yes), null);
        builder.create().show();
    }
}
