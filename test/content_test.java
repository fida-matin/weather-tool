package test;

import content.ContentServer;

import java.net.Socket;

public class content_test {
    private static final String DEFAULT_SERVER = "localhost";
    private static final Integer DEFAULT_PORT = 4567;
    private static final String LOCATION = "content/data/PUTRequest.txt";
    private static final String[] FILE = { "content/data/WeatherData.txt" };

    public static void main(String[] args) {
        String exp_req = "PUT /weather.json HTTP/1.1\n" +
                "User-Agent: ATOMClient/1/0\n" +
                "Content-Type: text/html;\n" +
                "Content-Length: {{length}}\n" +
                "\n" +
                "{{payload}}\n";

        try (ContentServer server = new ContentServer(new Socket(DEFAULT_SERVER, DEFAULT_PORT))) {
            String req = server.buildHTTP(LOCATION, FILE);
            assert exp_req == req;
            System.out.println("Content server build sucessful");
        } catch (Exception error) {
            System.out.println("Content server build unsucessful");
        }
    }
}
