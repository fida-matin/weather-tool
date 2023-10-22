package HTTP;

import java.io.IOException;
import java.net.Socket;

import HTTP.messages.HTTPResponse;

public abstract class HTTPClient implements AutoCloseable {
    private final Socket socket;

    // functions
    public HTTPClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    protected abstract String buildHTTP(String requestLocation, String... payload) throws IOException;

    public HTTPResponse sendHTTPRequest(String requestLocation, String... payload) {
        try (HTTPConnection connection = new HTTPConnection(socket)) {
            String request;

            if (payload.length == 0) {
                request = buildHTTP(requestLocation);
            } else {
                request = buildHTTP(requestLocation, payload[0]);
            }

            connection.sendData(request);
            HTTPResponse response = new HTTPResponse(connection.readRequest());
            return response;

        } catch (IOException error) {
            System.out.println("Couldn't send HTTP request");
            error.printStackTrace();
            return null;
        }
    }
}
