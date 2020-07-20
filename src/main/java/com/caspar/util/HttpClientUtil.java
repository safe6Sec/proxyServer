package com.caspar.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;


public class HttpClientUtil {
	private static PoolingHttpClientConnectionManager cm;
    private static String EMPTY_STR = "none";
    private static String UTF_8 = "UTF-8";
    public static String CURRENT = "";
    public static String CURRENT1 = "";
    public static int timeOut = 10;

    private static void init() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(2000);// 整个连接池最大连接数
            cm.setDefaultMaxPerRoute(500);// 每路由最大连接数，默认值是2
        }
    }

    /**
     * 通过连接池获取HttpClient
     * 
     * @return
     */
    public static CloseableHttpClient getHttpClient() {
        init();
        return HttpClients.custom().setConnectionManager(cm).build();
    }



//    public static void getSize(){
//        if(cm!=null){
//            System.out.println(cm.getDefaultConnectionConfig().getBufferSize());
//        }
//    }

    /**
     * 
     * @param url
     * @return
     */
    public static String httpGetRequest(String url) {
    	
    	
        HttpGet httpGet = new HttpGet(url);
        
        return getResult(httpGet);
    }

    /**
     * 获取get请求响应
     * @param url
     * @return
     * @throws IOException
     */

    public static CloseableHttpResponse get(String url) throws IOException {


        HttpGet httpGet = new HttpGet(url);
/*        SocketConfig socketConfig = SocketConfig.custom()
                .setSoKeepAlive(false)
                .setSoLinger(1)
                .setSoReuseAddress(true)
                .setSoTimeout(timeOut*100)
                .setTcpNoDelay(true).build();*/
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeOut*100).build();
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36" );
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader( "Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
        httpGet.setConfig(requestConfig);
        return getHttpClient().execute(httpGet);

    }

    /**
     * 获取head请求响应
     * @param url
     * @return
     * @throws IOException
     */


    public static CloseableHttpResponse head(String url) throws IOException {


        HttpHead httpHead = new HttpHead(url);
        httpHead.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36" );
        httpHead.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpHead.setHeader( "Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeOut*100).build();
        httpHead.setConfig(requestConfig);
        // response.getStatusLine().getStatusCode();
        return getHttpClient().execute(httpHead);

    }
    
    public static String httpGetRequest1(String url,Map<String, Object> headers) {
        HttpGet httpGet = new HttpGet(url);
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }
    
    public static String httpHeadRequest(String url) {
        HttpHead httpHead = new HttpHead(url);
        return getResult(httpHead);
    }

    public static String httpGetRequest(String url, Map<String, Object> params) throws URISyntaxException {
    	
    	
    	// URIBuilder 实用类来简化请求 URL的创建和修改.
        URIBuilder ub = new URIBuilder();
        
        //ub.setHost(url);
        
        //设置请求路径  e.g /index
        ub.setPath(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        
    /*    for (NameValuePair nameValuePair : pairs) {
			System.out.println("参数打印"+nameValuePair.getName()+"="+nameValuePair.getValue());
		}*/
        
        //设置参数
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
        
        
        //打印url
        //System.out.println(httpGet.getURI());
        CURRENT=httpGet.getURI().toString();
        return getResult(httpGet);
    }

    public static String httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws URISyntaxException {
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }

    public static String httpPostRequest(String url) {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }
    
    public static String httpPostRequest1(String url,Map<String, Object> headers) {
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> params) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);

        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));

        return getResult(httpPost);
    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        
        
        for (Map.Entry<String, Object> param : params.entrySet()) {
        	if(param.getKey().equals("avars[1][]")) {
        		pairs.add(new BasicNameValuePair("vars[1][]", String.valueOf(param.getValue())));
        	}else {
        		pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        	}
            
        }

        return pairs;
    }

    /**
     * 处理Http请求
     * 
     * @param request
     * @return
     */
    private static String getResult(HttpRequestBase request) {
        // CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getHttpClient();
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            // response.getStatusLine().getStatusCode();
            
            //响应实例
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // long len = entity.getContentLength();// -1 表示长度未知
                String result = EntityUtils.toString(entity,"utf-8");
                response.close();
                // httpClient.close();
                //System.out.println(result);
                return result;
            }
        } catch (ClientProtocolException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }

        return EMPTY_STR;
    }
    

}
