package util;

import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.lang.StringBuilder;

public class JSONObject {
    private final Map<String, String> JSONdata = new HashMap<>();

    public JSONObject(String input) throws Exception {
        try {
            if (input.contains("{")) {
                parseJSONobject(input);
            } else {
                parseListFormatting(input);
            }

        } catch (Exception error) {
            System.out.println("Unable to handle JSON Object");
            error.printStackTrace();
            throw error;
        }
    }

    public String get(String key) {
        return JSONdata.get(key);
    }

    public void put(String key, String value) {
        JSONdata.put(key, value);
    }

    public String toJSONString() {
        try {
            StringBuilder build = new StringBuilder();
            build.append("   ");

            for (Map.Entry<String, String> keyPair : JSONdata.entrySet()) {
                build.append("    ");
                build.append('"').append(keyPair.getKey()).append('"');
                build.append(": ");

                if (isNum(keyPair.getValue())) {
                    build.append(keyPair.getValue());
                } else {
                    build.append('"').append(keyPair.getValue()).append('"');
                }

                build.append(",\n");
            }

            build.delete(build.length() - 2, build.length());
            build.append("\n}");
            return build.toString();

        } catch (Exception error) {
            System.out.print("Unable to change JSON Object data to JSON string");
            error.printStackTrace();
            throw error;
        }
    }

    public String toListString() {
        try {
            StringBuilder build = new StringBuilder();

            for (Map.Entry<String, String> keyPair : JSONdata.entrySet()) {
                build.append(keyPair.getKey()).append(": ").append(keyPair.getValue()).append("\n");
            }
            return build.toString().trim();
        } catch (Exception error) {
            System.out.print("Unable to change JSON Object data to string");
            error.printStackTrace();
            throw error;
        }
    }

    private static boolean isNum(String payload) {
        return payload.matches("-?\\d+(\\.\\d+)?");
    }

    private void parseJSONobject(String JSONstring) {
        try {
            Pattern pattern = Pattern.compile("\"(.*?)\"\\s*:\\s*(\".*?\"|[-+]?[0-9]*\\.?[0-9]+)");
            Matcher match = pattern.matcher(JSONstring);

            while (match.find()) {
                String key = match.group(1);
                String val = match.group(2).replaceAll("\"", "");
                // put key, val pair into hashmap
                JSONdata.put(key, val);
            }

        } catch (Exception error) {
            System.out.print("Unable to parse JSON");
            error.printStackTrace();
            throw error;
        }
    }

    private void parseListFormatting(String list) throws Exception {
        try {
            String[] keyPairs = list.split("\n");

            for (String keyPair : keyPairs) {
                String[] pairArr = keyPair.split(":");
                // put values into hashmap
                JSONdata.put(pairArr[0].trim(), pairArr[1].trim());
            }

        } catch (Exception error) {
            System.out.print("Unable to format list");
            error.printStackTrace();
            throw error;
        }
    }

}
