package HTTP.messages;

public class HTTPResponse extends HTTPMessage {

    public HTTPResponse(String message) {
        super(message);
    }

    public HTTPResponse(HTTPMessage httpMessage) {
        super(httpMessage.messageToString());
    }

    public Integer getStatusCode() {
        String[] linesOfMessage = getHeader().split("\r\n");
        String[] firstLine = linesOfMessage[0].split(" ");
        return Integer.parseInt(firstLine[1]);
    }
}