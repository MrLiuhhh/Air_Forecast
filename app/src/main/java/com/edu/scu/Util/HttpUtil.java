package com.edu.scu.Util;
//用于数据请求，联网等进行数据请求的耗时操作放到子线程中实现

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url=new URL(address);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    //设置httpurlconnection请求头里面的属性，比如cookie等
                    //connection.setRequestProperty("apikey","apikey值");
                    connection.connect();

                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in,"utf-8"));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) !=null){
                        response.append(line);
                    }
                    if(listener != null){
                        listener.onSuccess(response.toString());
                    }

                } catch (Exception e) {
                    if (listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
