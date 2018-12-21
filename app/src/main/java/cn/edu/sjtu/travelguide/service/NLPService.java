package cn.edu.sjtu.travelguide.service;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NLPService {
    private static NLPService instance;
    private final static String TAG = NLPService.class.getSimpleName();
    private OkHttpClient client;
    private final Gson gson = new Gson();
    private static final String BASE_URL = "https://aip.baidubce.com/rpc/2.0/nlp/v2/word_emb_sim?access_token=24.f9ba9c5241b67688bb4adbed8bc91dec.2592000.1485570332.282335-8574074";

    private NLPService() {
    }

    public static NLPService getInstance() {
        if (instance == null) {
            instance = new NLPService();
        }
        return instance;
    }

    /**
     * 判断两个字符串语义是否相似
     *
     * @param str1
     * @param str2
     * @return
     */
    public boolean isSimilar(final String str1, final String str2) {
        String data = " {\"word_1\":\"+" + str1 + "\",\n" +
                "    \"word_2\":\"" + str2 + "\"}";
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data);
        Request request = new Request.Builder().url(BASE_URL)
                .post(body).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String jsonStr = response.body().string();
            Map<String, Object> map = gson.fromJson(jsonStr, Map.class);
            double score = (double) map.get("score");
            return score > 0.67;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

}
