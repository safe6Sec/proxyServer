package com.caspar.someThing;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by casparhuan on 2016/12/4.
 */
public class TestSocket {
    @Test
    public void testViewBaidu() throws IOException {
//        System.getProperties().put("socksProxySet", "true");
//        System.getProperties().put("socksProxyHost", "127.0.0.1");
//        System.getProperties().put("socksProxyPort","8882");


        StringBuilder rp = new StringBuilder();
        rp.append("GET http://www.ifeng.com/ HTTP/1.1\r\n")
                .append(
                        //"User-Agent: Fiddler\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Content-Length: 0\r\n" +
                        "Content-Type: text/plain charset=utf-8\r\n" +
                        "Host: www.ifeng.com\r\n" +
                        "Proxy-Connection: Keep-Alive\r\n" +
                        "User-Agent: Apache-HttpClient/4.5.2 (Java/1.8.0_71)\r\n")
                .append("\r\n");
        System.out.println("------------");
        System.out.println(rp.toString());
        System.out.println("------------");
        Socket socket = new Socket("127.0.0.1",8002);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        out.write(rp.toString().getBytes());
        out.flush();

        byte[] buffer = new byte[1024*100];
        int len = 0;
        StringBuilder html = new StringBuilder();
        while( (len = in.read(buffer))!=-1){
//            System.out.println("读取："+len);
//            System.out.println(new String(buffer,"utf-8"));
//            if(in.available()==0){
//                break;
//            }
            html.append(new String(buffer,"utf-8"));
        }
        System.out.println(html);


    }
}
