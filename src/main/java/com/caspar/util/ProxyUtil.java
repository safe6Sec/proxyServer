package com.caspar.util;

import com.alibaba.fastjson.JSON;
import com.caspar.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

/**
 * @Description: 二级代理工具类
 * @author: safe6
 * @date: 2020年07月20日 下午2:57
 */
public class ProxyUtil {

    private static Logger logger = LoggerFactory.getLogger(ProxyUtil.class.getName());


    /**
     * 获取二级代理
     * @return
     */
    public static Proxy getProxy(){

        Proxy proxy = null;
        try {
            //String url = "http://118.24.52.95/get/";
            String url = "http://localhost:5010/get/";

            String request = HttpClientUtil.httpGetRequest(url);

            if (!request.contains("proxy")){
                logger.error("获取代理失败,重试中。。。");
                getProxy();
            }

            Map<String,Object> map = JSON.parseObject(request,Map.class);
            String ip = map.get("proxy").toString();
            logger.info("使用代理:"+ip);
            String[] split = ip.split(":");

            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(split[0], Integer.parseInt(split[1])));

        } catch (Exception e) {
            e.printStackTrace();
            getProxy();
        }

        return proxy;
    }
}
