package HTTP.messages;

public class HTTPRequest extends HTTPMessage {

    public HTTPRequest(String message) {
        super(message);
    }

    public HTTPRequest(HTTPMessage httpMessage) {
        super(httpMessage.messageToString());
    }

    public String getHTTPRequestMethod() {
        String[] payload = getHeader().split("\r\n");
        String[] req = payload[0].split(" ");
        return req[0];
    }
}
