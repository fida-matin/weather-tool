package HTTP.messages;

public class HTTPMessage {
    private String header;
    private String body;

    public HTTPMessage(String message) {
        String[] split = message.split("\r\n\r\n");
        this.header = split[0];

        if (split.length > 1) {
            this.body = split[1];
        }

    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public String messageToString() {
        return header + "\r\n\r\n" + body;
    }
}
