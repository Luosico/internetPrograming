package httpProxy;

/**
 * @Author: luo kai fa
 * @Date: 2020/4/17
 */
public class RequestInfo {
    String host;
    int port;
    byte[] content;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
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
