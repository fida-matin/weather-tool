package server;

// a1798239 - Fida Matin

import HTTP.messages.HTTPRequest;
import HTTP.HTTPServer;

import util.JSONObject;

import java.util.LinkedList;
import java.util.Deque;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.net.ServerSocket;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.time.ZonedDateTime;

public class AggregationServer extends HTTPServer {

    private static final String DEFAULT_PORT = "4567";
    private static final String WEATHER_FILE = "aggregationServer/resources/WeatherData";

    // Protocols
    // 200 codes
    private static final String OK = "200 OK";
    private static final String HTTP_CREATED = "201 HTTP_CREATED";
    private static final String EMPTY_REQUEST_BODY = "204 Empty Request Body";

    // Others
    private static final String METHOD_NOT_IMPLEMENTED = "400 Not an implemented method";
    private static final String INTERNAL_SERVER_ERROR = "500 Internal server error";

    private WeatherData_Aggregated WeatherData_aggregated;

    // storage
    private static class WeatherData_Aggregated implements Serializable {
        private static final int MAX_UPDATES = 20;
        private Deque<Weather> updates = new LinkedList<>();

        private static class Weather implements Serializable {
            ZonedDateTime updateTime;
            String weatherData;

            public Weather(String weatherData, ZonedDateTime lastUpdated) {
                this.updateTime = lastUpdated;
                this.weatherData = weatherData;
            }
        }

        public WeatherData_Aggregated() {
            try {
                readFromFile();
            } catch (Exception error) {
                System.out.println("Unable to read file");
                error.printStackTrace();
            }

            if (updates == null) {
                updates = new LinkedList<>();
            }
        }

        public void update(String newData) {
            updates.addLast(new Weather(newData, ZonedDateTime.now()));

            if (updates.size() > MAX_UPDATES) {
                updates.removeFirst();
            }
        }

        public String getRecentUpdate() {
            if (updates.isEmpty()) {
                return "Unable to find data";
            }
            return updates.getLast().weatherData;
        }

        public ZonedDateTime getLastTime() {
            if (updates.isEmpty()) {
                return ZonedDateTime.now();
            }
            return updates.getLast().updateTime;
        }

        private void saveUpdatesToFile() throws IOException {
            try (
                    FileOutputStream fileOut = new FileOutputStream(WEATHER_FILE);
                    ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

                out.writeObject(updates);
                updates.getLast().updateTime = ZonedDateTime.now();

            } catch (IOException i) {
                System.out.println("Unable to write data to file");
                throw i;
            }
        }

        @SuppressWarnings("unchecked")
        public void readFromFile() throws IOException, ClassNotFoundException {
            System.out.println("Reading data file");

            try (FileInputStream fileIn = new FileInputStream(WEATHER_FILE);
                    ObjectInputStream in = new ObjectInputStream(fileIn)) {

                // Cast the deserialized object back to Deque<WeatherData>
                updates = (Deque<Weather>) in.readObject();
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
    }

    // constructor
    public AggregationServer(ServerSocket serverSocket) {
        super(serverSocket);
        WeatherData_aggregated = new WeatherData_Aggregated();
    }

    private static boolean isNum(String payload) {
        return payload.matches("-?\\d+(\\.\\d+)?");
    }

    private String buildErrorResponse(String statusCode, String message) {
        return "HTTP/1.1 " + statusCode + "\r\nContent-Length:" + message.length() + "\r\n\r\n" + message;
    }

    private String buildPUTResponse(String status) {
        String body = "Server received request successfully: " + ZonedDateTime.now() + "\r\n";
        return String.format("HTTP/1.1 " + status + "\r\nContent-Length: %d\r\n\r\n%s\r\n", body.length(), body);
    }

    private String buildGETResponse(String weatherUpdate) {
        return String.format("HTTP/1.1 200 OK\r\nContent-Length: %d\r\n\r\n%s\r\n", weatherUpdate.length(),
                weatherUpdate);
    }

    /**
     * Abstract class implementations from HTTP Server
     */

    @Override
    public String doPOSTRequest(HTTPRequest httpRequest) {
        return buildErrorResponse(METHOD_NOT_IMPLEMENTED, "POST not supported");
    }

    public String doPUTRequest(HTTPRequest request) {
        try {
            WeatherData_aggregated.update(request.getBody());

            // error checking for content of request
            if (request.getBody().isEmpty() || request.getBody().equals("null")) {
                System.out.println("Empty request body");
                return buildPUTResponse(EMPTY_REQUEST_BODY);
            }

            try {
                JSONObject update = new JSONObject(request.getBody());

                if (request.getBody().equals("{}")) {
                    System.out.println("Empty JSON object");
                    return buildPUTResponse(EMPTY_REQUEST_BODY);

                } else if (update.get("id") == null || update.get("id").isEmpty()) {
                    System.out.println("Invalid location ID");
                    return buildErrorResponse(INTERNAL_SERVER_ERROR, "Invalid location");
                }

            } catch (Exception error) {
                System.out.println("Couldn't parse JSON in aggregation server");
                return buildErrorResponse(INTERNAL_SERVER_ERROR, "Couldn't parse JSON in aggregation server");
            }

            Path filePath = Paths.get(WEATHER_FILE);
            if (!Files.exists(filePath)) {
                System.out.println("No data file exists, creating one");
                WeatherData_aggregated.saveUpdatesToFile();
                return buildPUTResponse(HTTP_CREATED);
            }
            return buildPUTResponse(OK);

        } catch (Exception e) {
            return buildErrorResponse(INTERNAL_SERVER_ERROR, "Couldn't write data");
        }
    }

    @Override
    public String doGETRequest(HTTPRequest request) {
        try {
            JSONObject update = new JSONObject(WeatherData_aggregated.getRecentUpdate());
            ZonedDateTime currTime = WeatherData_aggregated.getLastTime();
            System.out.println(currTime);
            return buildGETResponse(update.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(INTERNAL_SERVER_ERROR, "Failed to read data");
        }
    }

    @Override
    public String doDELETERequest(HTTPRequest request) {
        return buildErrorResponse(METHOD_NOT_IMPLEMENTED, "DELETE not supported");
    }

    public static String doCLI(String[] args) {
        String port;

        if (args.length == 0) {
            System.out.println("No arguments (port number) provided for starting agg server, defaulting to 4567");
            port = DEFAULT_PORT;

        } else if (args.length == 1 && isNum(args[0])) {
            port = args[0];

        } else {
            System.out.println("Invalid arguments provided for starting agg server, defaulting to 4567");
            port = DEFAULT_PORT;
        }

        return port;
    }

    public static void main(String[] args) {
        String port = doCLI(args);

        try {
            AggregationServer aggregationServer = new AggregationServer(new ServerSocket(Integer.parseInt(port)));
            aggregationServer.run();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

}
