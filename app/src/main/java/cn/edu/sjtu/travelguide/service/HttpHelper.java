package cn.edu.sjtu.travelguide.service;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpHelper {
    private final static String TAG = HttpHelper.class.getSimpleName();
    ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public String sendGet(String url) {
        Callable<String> thread = new MyCallable(url);
        Future<String> future = threadPool.submit(thread);
        try {

            String result = future.get();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class MyCallable implements Callable<String> {
        private String url;

        MyCallable(String url) {
            this.url = url;
        }

        @Override
        public String call() {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            try {
                Response response = client.newCall(request).execute();
                ResponseBody result = response.body();
                if (result != null) {
                    return result.string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
