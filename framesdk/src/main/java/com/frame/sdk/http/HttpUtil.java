package com.frame.sdk.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.frame.sdk.app.FrameApplication;
import com.frame.sdk.app.FrameConstant;
import com.frame.sdk.async.HttpTaskListener;
import com.frame.sdk.model.Response;
import com.frame.sdk.util.LogUtils;
import com.frame.sdk.util.NetworkManager;
import com.frame.sdk.util.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 网络请求工具类
 */
public class HttpUtil {
    private static AsyncHttpClient client;

    /**
     * http get请求
     *
     * @return 成功返回相应字符串，否则返回""
     */
    public static String httpGet(UrlString urlString) {
        return StringUtil.UTF8Wrapper(httpGet(urlString.getFullUrl()));
    }

    /**
     * http get请求
     *
     * @return 成功返回相应字符串，否则返回""
     */
    public static String httpGet(String url) {
        LogUtils.i("url=" + url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
        HttpClient client = new DefaultHttpClient();
        String result = "";
        try {
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * http post请求
     *
     * @return 成功返回相应字符串，否则返回""
     */
    public static String httpPost(UrlString urlString) {
        return StringUtil.UTF8Wrapper(httpPost(urlString.getPostUrl(), urlString.getParame()));
    }

    /**
     * http post请求
     *
     * @return 成功返回相应字符串，否则返回""
     */
    public static String httpPost(String url, Map<String, String> parameters) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return httpPost(url, nvps);
    }

    /**
     * http post请求
     *
     * @return 成功返回相应字符串，否则返回""
     */
    public static String httpPost(String url, List<NameValuePair> nvps) {
        LogUtils.i("url==" + url);
        LogUtils.i("parame==" + nvps);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
        String result = "";
        HttpClient client = new DefaultHttpClient();
        try {
            if (nvps != null && !nvps.isEmpty())
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String uploadImg(UrlString urlString, UploadFile uploadFile) {
        LogUtils.i("url==" + urlString.getPostUrl());
        LogUtils.i("parame==" + urlString.getParame());

        HttpClient httpclient = new DefaultHttpClient();
        //设置通信协议版本
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(urlString.getPostUrl());

        MultipartEntity mpEntity = new MultipartEntity(); //文件传输
        ContentBody cbFile = new FileBody(new File(uploadFile.getPath()));
        mpEntity.addPart("img", cbFile); // <input type="file" name="userfile" />  对应的
        Map<String, String> parames = urlString.getParame();
        for (Map.Entry<String, String> entry : parames.entrySet()) {
            try {
                mpEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        httppost.setEntity(mpEntity);

        String result = "";
        try {
            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 上传文件
     *
     * @return 成功返回相应字符串，否则返回""
     */

    public static String uploadFile(String urlStr, Map<String, String> parameters, UploadFile uploadFile) {
        List<UploadFile> files = new ArrayList<UploadFile>();
        files.add(uploadFile);
        return StringUtil.UTF8Wrapper(uploadFile(urlStr, parameters, files));
    }

    /**
     * 上传文件
     *
     * @return 成功返回相应字符串，否则返回""
     */
    public static String uploadFile(UrlString urlString, UploadFile uploadFile) {
        return StringUtil.UTF8Wrapper(uploadFile(urlString.getPostUrl(), urlString.getParame(), uploadFile));
    }

    /**
     * 多个文件上传
     *
     * @return 成功返回相应字符串，否则返回""
     */
    public static String uploadFile(UrlString urlString, List<UploadFile> files) {
        return StringUtil.UTF8Wrapper(uploadFile(urlString.getPostUrl(), urlString.getParame(), files));
    }

    /**
     * 多个文件上传,参数值传到服务器少了最后一位，不知道什么情况
     *
     * @return 成功返回相应字符串，否则返回""
     */
    public static String uploadFile(String urlStr, Map<String, String> parameters, List<UploadFile> files) {
        LogUtils.i("url==" + urlStr);
        LogUtils.i("parame==" + parameters);
        String result = "";
        final String BOUNDARY = "---------------------------7da2137580612";  // 边界标识 随机生成
        final String PREFIX = "--", LINE_END = System.getProperty("line.separator");
        final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        final int TIME_OUT = 100 * 1000;
        final String CHARSET = "utf-8";
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            // 参数内容
            StringBuilder textEntity = new StringBuilder();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {// 构造文本类型参数的实体数据
                textEntity.append(PREFIX);
                textEntity.append(BOUNDARY);
                textEntity.append(LINE_END);
                textEntity.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                textEntity.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                textEntity.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                textEntity.append(LINE_END);
                textEntity.append(entry.getValue());
                textEntity.append(LINE_END);
            }

            LogUtils.i("textEntity.toString()=========" + textEntity.toString());

            OutputStream dos = new DataOutputStream(conn.getOutputStream());
            // 字段内容.
            dos.write(textEntity.toString().getBytes());

            // 上传文件
            for (UploadFile file : files) {
                if (!file.exists()) {
                    LogUtils.e("文件" + file.getAbsolutePath() + "不存在");
                    continue;
                }
                LogUtils.i("file==" + file.getAbsolutePath());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"" + file.getFormName() + "\"; filename=\"" + file.getFileName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream" + LINE_END + LINE_END);
                dos.write(sb.toString().getBytes());

                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
            }

            // 结束字符
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            if (res == 200) {
                InputStream input = conn.getInputStream();
                result = readStream(input);
                return result;
            }
            LogUtils.i("ResponseCode==" + res);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void uploadFile(final HttpTaskListener taskListener, final UrlString urlString, RequestParams params) {
        LogUtils.i("url==" + urlString.getPostUrl());
        LogUtils.i("urlString parame==" + urlString.getParame());
        LogUtils.i("RequestParams parame==" + params);
        Map<String, String> pa = urlString.getParame();
        for (Map.Entry<String, String> entry : pa.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        if (client == null) {
            client = new AsyncHttpClient();
        }
        client.post(urlString.getPostUrl(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                parseResult(taskListener, new String(bytes));
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                taskListener.onError(i, new String(bytes));
            }

            @Override
            public void onRetry(int retryNo) {
                LogUtils.i("onRetry retryNo=" + retryNo);
                super.onRetry(retryNo);
            }
        });
    }

    private static void parseResult(final HttpTaskListener httpTaskListener, final String result) {
        if (httpTaskListener != null) {
            httpTaskListener.onComplete(result);
        }
        if (result == null) {
            if (httpTaskListener != null) {
                if (NetworkManager.getInstance(FrameApplication.getInstance()).isNetworkConnected()) {
                    httpTaskListener.onError(FrameConstant.NET_NULL, FrameConstant.NET_NULL_STR);
                } else {
                    httpTaskListener.onError(FrameConstant.NET_ERROR, FrameConstant.NET_ERROR_STR);
                }
            }
            return;
        }
        String resStr = (String) result;
        if (TextUtils.isEmpty(resStr)) {
            if (httpTaskListener != null) {
                if (NetworkManager.getInstance(FrameApplication.getInstance()).isNetworkConnected()) {
                    httpTaskListener.onError(FrameConstant.NET_EMPTY, FrameConstant.NET_EMPTY_STR);
                } else {
                    httpTaskListener.onError(FrameConstant.NET_ERROR, FrameConstant.NET_ERROR_STR);
                }
            }
            return;
        }
        Response response = Response.parse(resStr);
        if (response.isSuccess()) {
            if (httpTaskListener != null)
                httpTaskListener.onSuccess(response.msg);
        } else {
            if (httpTaskListener != null) {
                String errMsg = response.error;
                httpTaskListener.onError(response.code, errMsg);
            }
        }
    }

    /**
     * 文件下载
     */
    public static boolean downloadFile(String url, File file) {
        LogUtils.i("url==" + url);
        LogUtils.i("file==" + file.getAbsolutePath());
        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists())
                parentDir.mkdirs();
            if (file.exists())
                file.delete();
            file.createNewFile();
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream ins = connection.getInputStream();
            FileOutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            boolean gotdata = false;
            int readlen = 0;
            while ((readlen = ins.read(buffer)) > 0) {
                output.write(buffer, 0, readlen);
                gotdata = true;
            }
            output.flush();
            output.close();
            if (!gotdata) {
                file.delete();
                return false;
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件下载
     */
    public static boolean downloadFile(String url, String filePath) {
        return downloadFile(url, new File(filePath));
    }

    /**
     * 下载图片
     *
     * @param url 图片的全路径
     * @return 成功返回bitmap，失败返回null
     */
    public static Bitmap downloadImage(String url) {
        LogUtils.i("url==" + url);
        Bitmap bitmap = null;
        URL imgUrl;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setConnectTimeout(1000);
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String readStream(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int len = -1;
        StringBuilder sb = new StringBuilder();
        while ((len = in.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len));
        }
        in.close();
        return sb.toString();
    }
}
