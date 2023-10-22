package test;

import java.net.ServerSocket;

import HTTP.messages.HTTPRequest;

import server.AggregationServer;

public class server_test {
    // test each call
    private static final Integer DEFAULT_PORT = 4567;

    private static final String GETMessage = "GET /get HTTP/1.1\nHost: httpbin.org\nContent-Length: 0\nUser-Agent: GETClient\nConnection: close";
    private static final HTTPRequest GETReq = new HTTPRequest(GETMessage);
    private static final String GETRes = "HTTP/1.1 200 OK";

    private static final HTTPRequest PUTReq = new HTTPRequest("");
    private static final String PUTRes = "HTTP/1.1 204 Empty Request Body";

    public static void main(String[] args) {
        try {
            AggregationServer server = new AggregationServer(new ServerSocket(DEFAULT_PORT));
            String res;

            res = server.doGETRequest(GETReq);
            assert GETRes == res;

            res = server.doPUTRequest(PUTReq);
            assert PUTRes == res;

            System.out.println("Aggregation server passed");
        } catch (Exception error) {
            System.out.println("Aggregation server failed");
        }
    }
}
