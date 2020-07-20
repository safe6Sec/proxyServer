package com.caspar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by casparhuan on 2016/12/3.
 */
public class ProxyServer {

    private static Logger logger = LoggerFactory.getLogger(ProxyServer.class.getName());
    private static boolean RUN_FLAG = true;

    public static void main(String[] args) throws IOException {
        logger.debug("starting proxyServer...");
        int listenPort = 8080;
        if(args.length!=0){
            try {
                listenPort = Integer.parseInt(args[0]);
            }catch (Exception e){
                e.printStackTrace();
                logger.error(e.getMessage());
                listenPort = 8080;
                logger.info("由于输入参数的端口错误，仍然监听端口"+listenPort);
            }
        }
        ServerSocket serverSocket = new ServerSocket(listenPort);
        final ExecutorService executor = Executors.newCachedThreadPool();

        logger.info("开始监听端口："+listenPort);

        while (RUN_FLAG) {
            try {
                Socket socket = serverSocket.accept();
                socket.setKeepAlive(true);
                socket.setSoTimeout(10000);
                executor.submit(new ProxyTask(socket));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }

        logger.info("服务结束");
    }

}
