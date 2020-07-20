package com.caspar.util;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by casparhuan on 2016/12/3.
 */
public class TestHttpHeaderBuilder {

    @Test
    public void testParseRequestLine() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HttpHeaderBuilder httpHeaderBuilder = new HttpHeaderBuilder(null);
        Method method = HttpHeaderBuilder.class.getDeclaredMethod("parseRequestLine", String.class);
        method.setAccessible(true);
        method.invoke(httpHeaderBuilder, "GET http://baidu.com/ HTTP/1.1");
        Assert.assertEquals("method eqauls","GET",httpHeaderBuilder.getMethod());
        Assert.assertEquals("requestURL","http://baidu.com/",httpHeaderBuilder.getRequestURL());
        System.out.println(httpHeaderBuilder.toString());
    }

}
