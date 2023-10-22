package HTTP;

import java.net.ServerSocket;
import java.net.Socket;

import HTTP.messages.HTTPRequest;

import java.io.IOException;

public abstract class HTTPServer implements AutoCloseable {
    private final ServerSocket serverSocket;

    // dedicated functions for each call within HTTP Protocol
    public abstract String doPOSTRequest(HTTPRequest request);

    public abstract String doPUTRequest(HTTPRequest request);

    public abstract String doGETRequest(HTTPRequest request);

    public abstract String doDELETERequest(HTTPRequest request);

    public HTTPServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        System.out.println("Server running on port " + serverSocket.getLocalPort());
    }

    public void receiveClientRequest(Socket clientSocket) {
        try (HTTPConnection connect = new HTTPConnection(clientSocket)) {
            HTTPRequest request = new HTTPRequest(connect.readRequest());
            String response = null;

            // create protocol for HTTP - Each call is handled by their own dedicated
            // function
            switch (request.getHTTPRequestMethod()) {
                case "POST":
                    System.out.println("Server Recieved a POST request");
                    response = this.doPOSTRequest(request);
                    break;

                case "PUT":
                    System.out.println("Server Recieved a PUT request");
                    response = this.doPUTRequest(request);
                    break;

                case "GET":
                    System.out.println("Server Recieved a GET request");
                    response = this.doGETRequest(request);
                    break;

                case "DELETE":
                    System.out.println("Server Recieved a DELETE request");
                    response = this.doDELETERequest(request);
                    break;
            }

            connect.sendData(response);

        } catch (IOException error) {
            System.out.println("Server unable to receive request");
            error.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try (Socket clientSocket = serverSocket.accept();) {
                System.out.println("Received a connection from " + clientSocket.getInetAddress());
                this.receiveClientRequest(clientSocket);
            } catch (IOException error) {
                System.out.println("Unable to make connection with client");
                error.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }

}
