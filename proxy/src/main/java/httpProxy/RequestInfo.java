package httpProxy;

import java.util.Arrays;

/**
 * @Author: luo kai fa
 * @Date: 2020/4/17
 */
public class RequestInfo {
    String host;
    String method;
    int port;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public RequestInfo() {
        this.host = null;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
