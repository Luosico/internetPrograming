package httpProxy;

/**
 * @Author: luo kai fa
 * @Date: 2020/4/17
 */
public class HttpMessage {
    String host;
    String content;

    public HttpMessage(String host, String content) {
        this.host = host;
        this.content = content;
    }

    public String getHost() {
        return host;
    }

    public String getContent() {
        return content;
    }
}
