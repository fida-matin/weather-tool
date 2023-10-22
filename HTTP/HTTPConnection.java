package HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.Socket;

import HTTP.messages.HTTPMessage;

public class HTTPConnection implements AutoCloseable {
    private final Socket socket;
    protected BufferedReader reader;
    protected OutputStreamWriter writer;

    public HTTPConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    public void sendData(String request) throws IOException {
        writer.write(request, 0, request.length());
        writer.flush();
    }

    public HTTPMessage readRequest() throws IOException {
        StringBuilder message = new StringBuilder();
        String line;
        int contentLength = -1;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            message.append(line).append("\r\n");

            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }
        message.append("\r\n");

        if (contentLength > 0) {
            char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            message.append(body);
        }

        return new HTTPMessage(message.toString());
    }

}
