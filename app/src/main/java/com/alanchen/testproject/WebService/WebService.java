package com.alanchen.testproject.WebService;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class WebService extends
        AsyncTask<String, Integer, WebServiceCallBackObject> {

    private static String TAG = "WebService";
    private WebServiceCallBack webServiceCallBack;
    private WebServiceCallBackObject webServiceCallBackObject = null;
    private HttpURLConnection connection = null;
    private BufferedReader reader = null;

    public static String WsGetTotalInfo_API = "WsGetTotalInfo_API";
    public static String WsGetDetails_API = "WsGetDetails_API";
    public static String WsGetDetails_API_PATH = "https://data.taipei/api/v1/dataset/f18de02f-b6c9-47c0-8cda-50efad621c14?scope=resourceAquire";
    public static String WsGetTotalInfo_API_PATH = "https://data.taipei/api/v1/dataset/5a0e5fbb-72f8-41c6-908e-2fb25eff9b8a?scope=resourceAquire";

    public static String JSON_data = "data";
    public static String JSON_status = "status";
    public static String JSON_statusText = "statusText";
    public static String JSON_headers = "headers";
    public static String JSON_config = "config";
    public static String JSON_request = "request";

    public WebService(WebServiceCallBack webServiceCallBack) {
        this.webServiceCallBack = webServiceCallBack;
    }

    @Override
    protected WebServiceCallBackObject doInBackground(String... params) {
        Log.d(TAG,"doInBackground");
        postOrGetData(params[0], params[1], params[2]); //action, url, method, body
        return webServiceCallBackObject;
    }

    @Override
    protected void onPostExecute(WebServiceCallBackObject data) {
        Log.d(TAG,"onPostExecute");
        webServiceCallBack.onWebServiceCallBack(data);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.d(TAG,"onProgressUpdate");
    }

    public void postOrGetData(String action, String path, String method) {
        final StringBuilder response = new StringBuilder();
        URL url = null;
        try {
            url = new URL(path);
            int status_code = -1;
            boolean connectfail = false;
            try {
                connection = (HttpURLConnection) url.openConnection();
                try {
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(15000);
                    InputStream inputStream = null;
                    try {
                        int code = connection.getResponseCode();
                        status_code = code;
                        if( status_code == 404){
                            return;
                        }
                        inputStream = connection.getErrorStream();
                        if (inputStream == null && code == 200) {
                            inputStream = connection.getInputStream();
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e) {
                        connectfail = true;
                        e.printStackTrace();
                    }
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if(response != null && response.toString().length() > 0) {
                    webServiceCallBackObject = new WebServiceCallBackObject(action, 0, response.toString());
                }else if( status_code == 404 ){
                    webServiceCallBackObject = new WebServiceCallBackObject(action, 404, null);
                } else if( connectfail == true){
                    webServiceCallBackObject = new WebServiceCallBackObject(action, -1, null);
                }else {
                    webServiceCallBackObject = new WebServiceCallBackObject(action, 500, null);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
