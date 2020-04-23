package com.fmm.checkapp.task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fmm.checkapp.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class TimeAsyncTask extends AsyncTask<URL, Void, String> {

    public interface OnFinishTask{
        void onFinish(String hora, String min);
    }

    OnFinishTask listener;

    public TimeAsyncTask(OnFinishTask listener){
        this.listener = listener;
    }


    private static final String TAG = "TimeAsyncTask";

    @Override
    protected String doInBackground(URL... urls) {
        URL url = urls[0];
        String json = null;

        try {
            json = NetworkUtil.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "json: "+json);

        return json;
    }

    @Override
    protected void onPostExecute(String s) {
        String dateTime;

        try {
            if(s==null){
                Date date = new Date();
                String hora = Integer.toString(date.getHours());
                String min = Integer.toString(date.getMinutes());
                listener.onFinish(hora, min);
            }else{
                JSONObject jsonObject = new JSONObject(s);
                dateTime = jsonObject.getString("datetime");
                Log.d(TAG, "datetime: "+ dateTime);
                String[] data = dateTime.substring(dateTime.indexOf("T")+1).split(":");
                String hora = data[0];
                String min = data[1];
                listener.onFinish(hora, min);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
