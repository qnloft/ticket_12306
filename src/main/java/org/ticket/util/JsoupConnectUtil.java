package org.ticket.util;

import com.alibaba.fastjson.JSON;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * @author User: R&M www.rmworking.com/blog
 *         Date: 2017/7/9
 *         Time: 11:25
 *         jobFetching
 *         com.job.util
 */
public class JsoupConnectUtil {

    private static final Logger logger = Logger.getLogger(JsoupConnectUtil.class.getName());
    /**
     * 请求传参方式
     */
    public enum RequestDataType {

        urlParam(false), body(true);

        private final boolean hasBody;

        RequestDataType(boolean hasBody) {
            this.hasBody = hasBody;
        }

        /**
         * Check if this HTTP method has/needs a request body
         *
         * @return if body needed
         */
        public final boolean hasBody() {
            return hasBody;
        }
    }

    /**
     * 超时时间默认3秒
     */
    private static final Integer TIME_OUT = 3000;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String DEFAULT_CT = "application/json;charset=UTF-8";
    private static final String ACCEPT = "accept";
    private static final String DEFAULT_AC = "application/json, text/plain, */*";

    /**
     * 请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static Document connectionUrl(String url, Connection.Method method) throws IOException {
        return connectionUrl(url, null, null, null, null, null, method, null, null);
    }

    /**
     * 请求指定的url
     *
     * @param url
     * @param timeout
     * @return
     */
    public static Document connectionUrl(String url, Integer timeout, Connection.Method method) throws IOException {
        return connectionUrl(url, null, timeout, null, null, null, method, null, null);
    }

    /**
     * 请求指定的url,携带cookie
     *
     * @param url
     * @param timeout
     * @return
     */
    public static Document connectionUrl(String url, Integer timeout, Connection.Method method, Map<String, String> cookieMap) throws IOException {

        return connectionUrl(url, null, timeout, null, null, null, method, null, cookieMap);
    }

    /**
     * 请求指定的url,修改header
     *
     * @param url
     * @param timeout
     * @return
     */
    public static Document connectionUrl(String url, Integer timeout,  String ip, Integer port ,Connection.Method method, Map<String, String> header) throws IOException {

        return connectionUrl(url, null, timeout, ip, port, header, method, null, null);
    }

    /**
     * 请求使用代理访问指定url
     *
     * @param url
     * @param timeout
     * @param ip
     * @param port
     * @return
     * @throws IOException
     */
    public static Document connectionUrl(String url, Integer timeout, String ip, Integer port, Connection.Method method) throws IOException {
        return connectionUrl(url, null, timeout, ip, port, null, method, null, null);
    }

    /**
     * 请求
     *
     * @param url
     * @param paramMap        请求传参
     * @param timeout         超时时间
     * @param ip              代理IP
     * @param port            代理端口
     * @param headers         请求头设置
     * @param method          请求类型
     * @param requestDataType 传参方式
     * @return
     * @throws IOException
     */
    public static Document connectionUrl(String url, Map<String, String> paramMap,
                                         Integer timeout, String ip, Integer port,
                                         Map<String, String> headers, Connection.Method method,
                                         RequestDataType requestDataType, Map<String, String> cookieMap) throws IOException {
        Connection connection = Jsoup.connect(url);
        getConnection(paramMap, timeout, ip, port, headers, method, connection, requestDataType, cookieMap);
        return connectionUrl(connection);
    }

    private static void getConnection(Map<String, String> paramMap, Integer timeout,
                                      String ip, Integer port, Map<String, String> headers,
                                      Connection.Method method, Connection connection,
                                      RequestDataType requestDataType, Map<String, String> cookieMap) {
        Validate.notNull(method, "Method is not null! Method is get or post...");
        connection.method(method);
        if (!StringUtil.isEmpty(paramMap)) {
            if (requestDataType == null) {
                connection.requestBody(paramMap.get("text"));
            } else {
                if (!requestDataType.hasBody()) {
                    connection.data(paramMap);
                } else {
                    connection.requestBody(JSON.toJSONString(paramMap));
                    connection.header(CONTENT_TYPE, DEFAULT_CT);
                }
            }
        }
        if (!StringUtil.isEmpty(ip) && !StringUtil.isEmpty(port)) {
            //设置代理
            SocketAddress proxyAddress = new InetSocketAddress(ip, port);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
            connection.proxy(proxy);
        }
        //设置链接属性
        connection.timeout(timeout == null ? TIME_OUT : timeout);
        if (!StringUtil.isEmpty(headers)) {
            connection.headers(headers);
        }
        // 设置cookie属性
        if (!StringUtil.isEmpty(cookieMap)) {
            for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
                connection.cookie(entry.getKey(), entry.getValue());
            }
        }
    }

    private static Document connectionUrl(Connection connection) throws IOException {
        return connection.execute().parse();
    }

    /**
     * get请求 json接口
     *
     * @param url
     * @param method
     * @param charset
     * @return
     * @throws IOException
     */
    public static String connectionJson(String url, Connection.Method method, String charset) throws IOException {
        return connectionJson(url, null, null, null, null, null, method, null, null, charset);
    }


    /**
     * get请求 json接口
     *
     * @param url
     * @param timeout
     * @param method
     * @param cookieMap
     * @param charset
     * @return
     * @throws IOException
     */
    public static String connectionJson(String url, Integer timeout, Connection.Method method, Map<String, String> cookieMap, String charset) throws IOException {
        return connectionJson(url, null, timeout, null, null, null, method, null, cookieMap, charset);
    }

    /**
     * get请求 json接口
     *
     * @param url
     * @param timeout
     * @param ip
     * @param port
     * @param method
     * @param charset
     * @return
     * @throws IOException
     */
    public static String connectionJson(String url, Integer timeout, String ip, int port, Connection.Method method, String charset) throws IOException {
        return connectionJson(url, null, timeout, ip, port, null, method, null, null, charset);
    }

    /**
     * 请求json接口
     *
     * @param url
     * @param paramMap
     * @param timeout
     * @param ip
     * @param port
     * @param headers
     * @param method
     * @param requestDataType
     * @param cookieMap
     * @param charset
     * @return
     * @throws IOException
     */
    public static String connectionJson(String url, Map<String, String> paramMap,
                                        Integer timeout, String ip, Integer port,
                                        Map<String, String> headers, Connection.Method method,
                                        RequestDataType requestDataType, Map<String, String> cookieMap, String charset) throws IOException {

        Connection connection = Jsoup.connect(url).ignoreContentType(true);
        getConnection(paramMap, timeout, ip, port, headers, method, connection, requestDataType, cookieMap);
        return connectionJson(connection, charset);
    }

    private static String connectionJson(Connection connection, String charset) throws IOException {
        if (StringUtil.isEmpty(charset)) {
            charset = "GBK";
        }
        String result = null;
        Connection.Response response = null;
        try {
            response = connection.execute().charset(charset);
        } catch (NoSuchElementException ne) {
            logger.info(ne.getMessage());
        }
        if (response != null) {
            try {
                result = response.body();
            } catch (org.jsoup.UncheckedIOException une) {
                logger.info(une.getMessage());
            }
        }
        return result;
//        return connection.execute().charset(charset).body();
    }

    /**
     * 获取cookie
     * @param url
     * @param timeout
     * @param ip
     * @param port
     * @param method
     * @return
     * @throws IOException
     */
    public static Map<String, String> getCookie(String url ,Integer timeout, String ip, Integer port ,Connection.Method method) throws IOException {
        Connection connection = Jsoup.connect(url);
        getConnection(null, timeout, ip, port, null, method, connection, null, null);
        return connection.execute().cookies();
    }

    public static void main(String[] args) {
//        Map<String, String> paramMap = new HashMap<>(16);
//        paramMap.put("logonid", "qnloft");
//        Document document = connectionUrl("http://127.0.0.1:7766/userInfo/1", paramMap, null, null, null, null, Connection.Method.POST);
//        System.out.println(document.html());

//        System.out.println(connectionJson("http://127.0.0.1:7766/doc", Connection.Method.GET));
        try {
            System.out.println(connectionUrl("http://www.66ip.cn/nmtq.php?getnum=100&anonymoustype=3", 3000, Connection.Method.GET).html());
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

}
