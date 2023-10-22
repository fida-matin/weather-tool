package client;

// import local
import util.JSONObject;

import HTTP.HTTPClient;
import HTTP.messages.HTTPResponse;

// import java
import java.net.Socket;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

public class GETClient extends HTTPClient {
    // set Macros
    private static final String REQ_LOCATION = "client/resources/GETRequest.txt";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "4567";

    public GETClient(Socket socket) {
        super(socket);
    }

    @Override
    public String buildHTTP(String reqLocation, String... payload) throws IOException {
        String request = new String(Files.readAllBytes(Paths.get(reqLocation)));
        return request;
    }

    private static boolean isNum(String payload) {
        return payload.matches("-?\\d+(\\.\\d+)?");
    }

    //
    public static String[] doCLI(String[] args) {
        String host;
        String port;

        // set to default is hosts

        if (args.length == 0) {
            System.out.println("No arguments found");
            return new String[] { DEFAULT_HOST, DEFAULT_PORT };
        }

        String[] payload = args[0].split(":");

        if (payload.length != 2) {
            System.out.println(
                    "Format does not match withc expected format 'servername:portnumber'");
            return new String[] { DEFAULT_HOST, DEFAULT_PORT };
        }

        if (isNum(payload[0]) && isNum(payload[1])) {
            host = payload[0];
            port = payload[1];
        } else {
            System.out.println("invalid (non numeric) arguments provided, using default server and port");
            host = DEFAULT_HOST;
            port = DEFAULT_PORT;
        }

        return new String[] { host, port };
    }

    public static void main(String[] args) throws Exception {
        String[] cli = doCLI(args);

        String host = cli[0];
        Integer port = Integer.parseInt(cli[1]);

        try (GETClient client = new GETClient(new Socket(host, port))) {

            HTTPResponse res = client.sendHTTPRequest(REQ_LOCATION);
            JSONObject updatedObject = new JSONObject(res.getBody());
            System.out.println("Aggregate server has GET request, the response: " + res.toString() + "\n");
            System.out.println(updatedObject.toListString());

        } catch (IOException error) {
            System.out.println("Unable to create client");
            error.printStackTrace();
        }
    }
}
