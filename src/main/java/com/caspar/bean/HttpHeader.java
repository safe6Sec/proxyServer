package com.caspar.bean;
import java.util.List;

/**
 * 报文解析
 * Created by casparhuan on 2016/12/3.
 */
public final class HttpHeader {
    private final String requestLine;//请求头
    private final List<String> headers ;//报文头部

    private final String host;
    private final int port;
    private final String method;
    private final String requestURL;

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_CONNECTION = "CONNECT";


    public HttpHeader(String host,int port,String method,String requestLine,String requestURL,List<String> headers){
        this.headers = headers;
        this.host = host;
        this.port = port;
        this.requestLine = requestLine;
        this.method = method;
        this.requestURL =requestURL;
    }

    /**
     * 返回整个报文头部
     * @return
     */
    public String getHttpHedaer(){
        StringBuilder sb = new StringBuilder();
        sb.append(requestLine)
                .append("\r\n");
        headers.forEach(val->{
            sb.append(val).append("\r\n");
        });
        sb.append("\r\n");
//        System.out.println("----------");
//        System.out.println(sb.toString()+"post date:");
//        System.out.println("----------");
        return sb.toString();
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getRequestURL() {
        return requestURL;
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "requestLine='" + requestLine + '\'' +
                ", headers=" + headers +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", method='" + method + '\'' +
                ", requestURL='" + requestURL + '\'' +
                '}';
    }
}
