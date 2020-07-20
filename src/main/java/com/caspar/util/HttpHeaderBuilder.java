package com.caspar.util;

import com.caspar.bean.HttpHeader;
import com.caspar.exception.HttpHeaderConstructException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by casparhuan on 2016/12/3.
 */
public class HttpHeaderBuilder {
    private String requestLine;//请求头
    private List<String> headers ;//报文头部
    private String host;
    private int port;
    private String method;
    private String requestURL;
    private String[] leagalMehod = {"GET","POST","CONNECT"};

    private static final String METHOD_GET = HttpHeader.METHOD_GET;
    private static final String METHOD_POST = HttpHeader.METHOD_POST;
    private static final String METHOD_CONNECTION = HttpHeader.METHOD_CONNECTION;

    private final static int  MAX_REQUEST_LINE = 2048;
    private final static int  MAX_HEADER_LENGTH = 16;

    private final InputStream is;

    public HttpHeaderBuilder(InputStream is){
        this.is = is;
        this.method = "";
        this.port = 0;
        this.headers = new ArrayList<String>();
        this.requestLine ="";
        this.host = "";
    }

    public HttpHeader build() throws HttpHeaderConstructException {
        try {
            //1. 读取第一行如：GET http://baidu.com/ HTTP/1.1
            //1.1 读取请求头
            StringBuilder stringBuilder = new StringBuilder();
            int readTemp = -1;
            char readTempChar = 0;
            int requestLineLength = 0;
            while ((readTemp = is.read()) != -1) {
                readTempChar = (char) readTemp;
                if (readTempChar == '\n') {
                    break;
                }
                requestLineLength++;
                stringBuilder.append(readTempChar);
                if (requestLineLength > MAX_REQUEST_LINE) {
                    throw new HttpHeaderConstructException("read requestLine error");
                }
            }
            requestLine = stringBuilder.toString().replace("\r", "");
            //1.2 解析请求头
            parseRequestLine(requestLine);

            //2. 获取header
            boolean isNotHttpEntity = false;//是否是到了实体主体部分
            while (!isNotHttpEntity) {
                //2.1 清空stringbuilder
                stringBuilder.setLength(0);
                //2.2 循环读取header
                while ((readTemp = is.read()) != -1) {
                    readTempChar = (char) readTemp;
                    if (readTempChar == '\n') {
                        if (stringBuilder.toString().equals("\r")) {
                            isNotHttpEntity = true;//已经到了实体主体部分了
                            break;
                        }
                        break;
                    }
                    stringBuilder.append(readTempChar);
                }
                //2.3 添加头部
                String header = stringBuilder.toString().replace("\r", "");
                if(header.trim().length()==0){
                    break;
                }
                headers.add(header);
                //2.3 检查是否是host 的header(如：   Host: baidu.com)
                String[] temps = header.split(":");
                if (temps.length != 0 && temps[0].equalsIgnoreCase("host")) {
                    host = temps[1].trim();
                    if (temps.length > 3) {
                        throw new HttpHeaderConstructException("read host Hedaer error");
                    }
                    if (temps.length == 2) {
                        if (METHOD_CONNECTION.equals(temps)) {
                            //HTTPS 默认端口
                            port = 443;
                        } else {
                            //HTTP 默认端口
                            port = 80;
                        }
                    } else {
                        try {
                            port = Integer.parseInt(temps[2].trim());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            throw new HttpHeaderConstructException("read host port Hedaer error");
                        }
                    }
                }

                if (headers.size() >= MAX_HEADER_LENGTH) {
                    throw new HttpHeaderConstructException("headers too long");
                }

            }
        }catch (IOException ioe){
            ioe.printStackTrace();
            throw new HttpHeaderConstructException(ioe.getMessage());
        }finally {
            return new HttpHeader(host,port,method,requestLine,requestURL,headers);
        }
    }

    /**
     * 校验method
     * @param method
     * @return
     */
    private boolean validateMethod(String method){
        if(method ==null || method.trim().length()==0){
            return false;
        }
        for (int i = 0; i < leagalMehod.length; i++) {
            if(method.equals(leagalMehod[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * 解析请求头
     * @param requestLine 如：GET http://baidu.com/ HTTP/1.1
     */
    private void parseRequestLine(String requestLine) {
        int index1,index2;
        index1 = requestLine.indexOf(' ');
        if( index1 != -1 ){
            method = requestLine.substring(0,index1);
            if(!validateMethod(method)){
                method = "";
                return;
            }
            index2 = requestLine.substring(index1+1).indexOf(' ');
            if(index2 > index1){
                requestURL = requestLine.substring(index1+1,index1+1+index2);
            }
        }
    }

    public String getRequestLine() {
        return requestLine;
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

    public String getRequestURL() {
        return requestURL;
    }

    public String[] getLeagalMehod() {
        return leagalMehod;
    }

    @Override
    public String toString() {
        return "HttpHeaderBuilder{" +
                "leagalMehod=" + Arrays.toString(leagalMehod) +
                ", requestLine='" + requestLine + '\'' +
                ", headers=" + headers +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", method='" + method + '\'' +
                ", requestURL='" + requestURL + '\'' +
                '}';
    }
}
