package test;

import client.GETClient;

import java.net.Socket;

public class client_test {
    private static final String DEFAULT_SERVER = "localhost";
    private static final Integer DEFAULT_PORT = 4567;
    private static final String LOCATION = "client/resources/GETRequest.txt";

    public static void main(String[] args) {
        String exp_req = "GET /get HTTP/1.1\n" + //
                "Host: httpbin.org\n" + //
                "User-Agent: simpleGETClient\n" + //
                "Connection: close\n";

        try (GETClient client = new GETClient(new Socket(DEFAULT_SERVER, DEFAULT_PORT))) {
            String req = client.buildHTTP(LOCATION);
            assert exp_req == req;
        } catch (Exception error) {
            System.out.println("Client build unsucessful");
        }

        System.out.println("Client build sucessful");
    }
}
