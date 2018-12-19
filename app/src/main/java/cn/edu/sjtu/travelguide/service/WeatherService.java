package cn.edu.sjtu.travelguide.service;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by wanglei on 2018/12/11.
 */

public class WeatherService {
    private final static String TAG = WeatherService.class.getSimpleName();
    private static WeatherService instance;
    private OkHttpClient client;
    private static final String BASE_URL = "http://t.weather.sojson.com/api/weather/city/";

    public static WeatherService getInstance(){
        if(instance == null){
            instance = new WeatherService();
        }
        return instance;
    }

    private WeatherService() { client = new OkHttpClient(); }

    /**
     * 获取天气状况
     *
     * @param cityCode
     * @return
     */
    public void getWeatherCondition(String cityCode, final AsyncTask task){
        String url = null;
        if(cityCode == null){
            url = BASE_URL + "101020100";//上海
        }else{
            url = BASE_URL + cityCode;
        }

        Request request = new Request.Builder().url(url)
                .get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                task.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null){
                    task.onFailure();
                } else {
                    String jsonStr = body.toString();
                    task.onSuccess(body);
                }
            }
        });
    }
}
