package com.jiebao.baqiang;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpCon {

	private static final String TAG = "HttpCon";
	private Handler serverHandler;
	private int what ;

	/** 
     * POST请求
     *  
     * @param data 传输数据
     * @param address 地址 
     */  
    public  void sendByPost(Handler handler ,int flag, String data, String address) {
        try {
            serverHandler = handler;
        	what = flag;
            // 根据 地址url创建URL对象
            URL url = new URL(address);  
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 这只请求的方式
            urlConnection.setRequestMethod("POST");  
            // 设置请求的超时时间 
            urlConnection.setReadTimeout(10000);  
            urlConnection.setConnectTimeout(10000);

            Log.d(TAG, "sendByPost data " + data);
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();

            os.write(data.getBytes());

            os.flush();
            if (urlConnection.getResponseCode() == 200) {  
                // 获取相应的输入流对象
                InputStream is = urlConnection.getInputStream();  
                // 创建字节输出流对象 
                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
                // 定义读取的长度  
                int len = 0;  
                // 定义读取的长度
                byte buffer[] = new byte[5000];  
                // 按照缓冲区的大小,循环读取  
                while ((len = is.read(buffer)) != -1) {  
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);  
                }  
                // 释放资源
                is.close();  
                baos.close();
                buffer=null;
                // 返回字符串
               // final String result = new String(baos.toByteArray());  
                returnMessage(new String(baos.toByteArray(), "UTF-8").trim());
            } else {
                Log.v(TAG, "connect getResponseCode " + urlConnection.getResponseCode());
            }
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
   /* 
    * @steps read();
    * @effect 服务端得到返回数据并发送到主界面
    * 
    * */
   public void returnMessage(String response){
	   Log.v(TAG, "returnMessage " + response);

       Message msg = new Message ();
       msg.what = what ;
       msg.obj = response;

       if(response.length() > 0){
           serverHandler.sendMessage (msg);
       }

   }

    public void sendByHttpClient(Handler handler ,int flag, List<NameValuePair> params, String address) {

        try {
            serverHandler = handler;
            what = flag;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(address);

            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("username", username));
            //params.add(new BasicNameValuePair("password", password));

            //DefaultHttpClient:
            //请求超时
            //httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
            //读取超时
            //httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);

            //HttpClient
            //HttpClient httpClient=new HttpClient();
            //链接超时
            //httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
            //读取超时
            //httpClient.getHttpConnectionManager().getParams().setSoTimeout(60000)

            final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);

            Log.d(TAG, "sendByHttpClient " + httpPost.toString());

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity1 = httpResponse.getEntity();
                String response = EntityUtils.toString(entity1, "utf-8");
                returnMessage(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
