package content;

import HTTP.HTTPClient;
import HTTP.messages.HTTPResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.Socket;

import java.nio.charset.StandardCharsets;

import java.util.stream.Collectors;

public class ContentServer extends HTTPClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "4567";
    private static final String DEFAULT_FILE = "content/data/WeatherData.txt";

    private static boolean isNum(String payload) {
        return payload.matches("-?\\d+(\\.\\d+)?");
    }

    public ContentServer(Socket socket) {
        super(socket);
    }

    @Override
    public String buildHTTP(String location, String... weatherData) {

        String req = "", payload = "";

        try (InputStream stream = getClass().getResourceAsStream(location);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            req = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception error) {
            System.out.println("Unable to connect to get request location for HTTP");
        }

        try (InputStream stream = getClass().getResourceAsStream(weatherData[0]);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            payload = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception error) {
            System.out.println("Unable to connect to get weather data for HTTP");
        }

        req = req.replace("{{body_length}}", Integer.toString(payload.length()));
        req = req.replace("{{body}}", payload);

        return req;
    }

    public static String[] doCLI(String[] args) {
        String[] cli = new String[] { DEFAULT_HOST, DEFAULT_PORT, DEFAULT_FILE };

        if (args.length == 0) {
            System.out.println("Content Server received no arugments");
            return cli;
        }

        String[] split = args[0].split(":");

        if (split.length != 3) {
            System.out.println("Content server format must be 'servername:portnumber'");
            return cli;
        }

        if (isNum(split[0]) && isNum(split[1])) {
            cli[0] = split[0];
            cli[1] = split[1];
            cli[2] = split[2];
        } else {
            System.out.println("Non numeric values for server and port");
        }

        return cli;
    }

    public static void main(String[] args) {
        String[] cli = doCLI(args);

        if (cli == null)
            return;

        try (ContentServer content = new ContentServer(new Socket(cli[0], Integer.parseInt(cli[1])))) {
            HTTPResponse res = content.sendHTTPRequest("data/PUTRequest.txt", "\n");
            System.out.println("Server Response: " + res.toString() + "\n");
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

}
