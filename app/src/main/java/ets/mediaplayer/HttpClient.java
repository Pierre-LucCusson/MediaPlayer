package ets.mediaplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import okhttp3.Address;
import okhttp3.Authenticator;
import okhttp3.CertificatePinner;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Gabriel on 2017-06-15.
 */

public class HttpClient {

    String urlHead = "http://192.168.2.31:5000";

    public String PLAYLIST = "/playlist";
    public String ID = "/id/";
    public String PLAY = "/play/";
    public String PAUSE = "/pause";
    public String RESUME = "/resume";
    public String NEXT = "/next";
    public String BACK = "/back";
    public String SHUFFLE = "/shuffle";
    public String REPEAT = "/repeat";
    public String VOLUME = "/volume/";
    public String STOP = "/stop";

    OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {

        Request request = new Request.Builder()
                .url(urlHead + url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


}
