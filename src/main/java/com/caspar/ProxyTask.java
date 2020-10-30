package com.caspar;

import com.caspar.bean.HttpHeader;
import com.caspar.exception.HttpHeaderConstructException;
import com.caspar.util.HttpHeaderBuilder;
import com.caspar.util.ProxyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by casparhuan on 2016/12/3.
 */
public class ProxyTask implements Callable<Void> {
    private Socket inSocket;//使用代理的socket
    private Socket targetSocket;//目标访问网站的socket
    private Logger logger = LoggerFactory.getLogger(ProxyServer.class.getName());
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile AtomicLong targetFileSize = new AtomicLong(0);
    private volatile AtomicLong clintGetFileSize = new AtomicLong(0);


    private static final String AUTHORED = "HTTP/1.1 200 Connection established";

    public ProxyTask(Socket socket) {
        this.inSocket = socket;
    }

    /**
     * 代理
     *
     * @return
     * @throws Exception
     */
    @Override
    public Void call() {
        // System.getProperties().put("socksProxySet ", "true ");
        //System.getProperties().put("socksProxyHost ", "127.0.0.1");
        //System.getProperties().put("socksProxyPort ", 8882);

        logger.debug("ip：" + inSocket.getInetAddress() + "  ,port:" + inSocket.getPort() + "  使用代理");
        InputStream isClient = null;
        OutputStream osClient = null;
        HttpHeader httpHeader = null;
        InputStream isTarget = null;
        OutputStream osTarget = null;

        try {
            isClient = inSocket.getInputStream();
            osClient = inSocket.getOutputStream();
            httpHeader = new HttpHeaderBuilder(isClient).build();
            logger.info("get reporter ：" + httpHeader);

            //socket = new Socket(proxy);
            //socket.connect(new InetSocketAddress(ip, port));//服务器的ip及地址
            //连接目标网站
            //targetSocket = new Socket(httpHeader.getHost(), httpHeader.getPort());
            targetSocket = new Socket(ProxyUtil.getProxy());
            targetSocket.connect(new InetSocketAddress(httpHeader.getHost(), httpHeader.getPort()));
            targetSocket.setSoTimeout(5000);
            targetSocket.setKeepAlive(true);
            isTarget = targetSocket.getInputStream();
            osTarget = targetSocket.getOutputStream();

            //报文头部发送
            if (HttpHeader.METHOD_CONNECTION.equals(httpHeader.getMethod().trim().toUpperCase())) {
                //https访问
                osClient.write((AUTHORED + "\r\n\r\n").getBytes());
                osClient.flush();
            }
            else if(HttpHeader.METHOD_GET.equals(httpHeader.getMethod().trim().toUpperCase())){
                //发送报文的请求头还有header
                osTarget.write(httpHeader.getHttpHedaer().getBytes());
                osTarget.write("\r\n".getBytes());
                osTarget.flush();
            }
            else {
                osTarget.write(httpHeader.getHttpHedaer().getBytes());
                osTarget.flush();
            }


            //从目标网站服务器读取数据，传输给使用代理服务器的客户端
            InputStream finalIsTarget = isTarget;
            OutputStream finalOsClient = osClient;
            Callable<Void> readFromTargetToClientCallable = new Callable<Void>() {
                @Override
                public Void call() throws IOException {
                    logger.debug("从服务器端读取数据到客户端");
                    readFromTargetToClient(finalIsTarget, finalOsClient);
                    logger.info("结束");
                    return null;
                }
            };
            Future<Void> future = executor.submit(readFromTargetToClientCallable);

            //接受参数或者发送post正文内容
            logger.debug("发送数据到目标服务器");
            transferDataToTarget(isClient, osTarget);

            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            logger.info("ending...");
        } catch (HttpHeaderConstructException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            //返回固定格式
            if (osClient != null) {
                try {
                    osClient.write(badRequest().toString().getBytes());
                } catch (IOException e1) {
                    e1.printStackTrace();
                    logger.error(e1.getMessage());
                }
            }
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            logger.error(ioe.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        } finally {
            //关闭流和socket
            if (osClient != null) {
                try {
                    osClient.close();
                } catch (IOException ignored) {
                }
            }
            if (isClient != null) {
                try {
                    isClient.close();
                } catch (IOException ignored) {
                }
            }
            if (!inSocket.isClosed()) {
                try {
                    inSocket.close();
                } catch (IOException ignored) {
                }
            }
            if (isTarget != null) {
                try {
                    isTarget.close();
                } catch (IOException ignored) {
                }
            }
            if (osTarget != null) {
                try {
                    osTarget.close();
                } catch (IOException ignored) {
                }
            }
            if (!targetSocket.isClosed()) {
                try {
                    targetSocket.close();
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }

    /**
     * 从一端读取数据，传输到另外一端
     *
     * @param isFrom 内容获取
     * @param osOut  内容输出
     * @throws IOException
     */
    private void readDataToOther(InputStream isFrom, OutputStream osOut) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int len = 0;
        logger.debug("available:" + isFrom.available());
        while ((len = isFrom.read(buffer)) != -1) {
            osOut.write(buffer, 0, len);
            osOut.flush();
        }
    }

    /**
     * 从目标网站服务器读取数据，传输给使用代理服务器的客户端
     *
     * @param isTarget
     * @param osClient
     */
    private void readFromTargetToClient(InputStream isTarget, OutputStream osClient) throws IOException {
//        readDataToOther(isTarget,osClient);
        byte[] buffer = new byte[1024 * 8];
        int len = 0;
        logger.debug("available:" + isTarget.available());
        try {
            while ((len = isTarget.read(buffer)) != -1) {
                logger.debug("从目标网站读取到数据长度：" + len);
                osClient.write(buffer, 0, len);
                osClient.flush();
                if (targetSocket.isOutputShutdown() || inSocket.isClosed()) {
                    break;
                }
            }
        } catch (SocketTimeoutException e) {
        }
    }

    /**
     * 发送剩余的内容到目标服务器
     *
     * @param isClient 内容输入端
     * @param osTarget 内容接受端
     */
    private void transferDataToTarget(InputStream isClient, OutputStream osTarget) throws IOException {
//        readDataToOther(isClient,osTarget);

        byte[] buffer = new byte[1024 * 4];
        int len = 0;
        logger.debug("available:" + isClient.available());
        try {
            while ((len = isClient.read(buffer)) != -1) {
                osTarget.write(buffer, 0, len);
                osTarget.flush();
                if (inSocket.isOutputShutdown() || targetSocket.isClosed()) {
                    break;
                }
            }
        } catch (SocketTimeoutException e) {

        }

    }

    /**
     * 错误的请求的报文
     *
     * @return 报文内容
     */
    private String badRequest() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>\r\n")
                .append("<head>\r\rn")
                .append("<title>Bad Request</title>\r\n")
                .append("</head>\r\n")
                .append("<body>Bad Request</body>\r\n")
                .append("</html>\r\n");
        StringBuilder reporterBudiler = new StringBuilder();
        reporterBudiler.append("HTTP/1.1 400 Bad Request\r\n")
                .append("Content-Type:text/html;charset=ISO-8859-1\r\n")
                .append("Content-Length:" + htmlBuilder.toString().length() + "\r\n")
                .append("\r\n")
                .append(htmlBuilder.toString());
        return reporterBudiler.toString();
    }


}
