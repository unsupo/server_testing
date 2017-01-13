package examples;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

/**
 * Created by jarndt on 1/11/17.
 */
public class HttpRequest {
//    public static void main(String[] args) throws IOException {
//        String token = "EAAI10tXkMXwBANwlhM8JJ355EgjU8giAiu6PoaDpTGjhBwcbUfejbhBvEinRylNJrUYDGy2eNS091et9LYWB4DZBF2HeLJpF32iiKqus586W1TG5bm5ZB6dTfDOtmfZCpSBRynWQgglQwcsTkytFZAv0sz2ACRWnXwnZCrgUZBdQZDZD";
//        String access_token = "622129601327484|SR8Fs6oT4wV5TayZopEQ9IP4tWY";
//        FacebookClient.DebugTokenInfo v = new DefaultFacebookClient(access_token).debugToken(token);
//        System.out.println(v);
//
//        //access token
////        System.out.println(post("https://graph.facebook.com/v2.8/oauth/access_token","client_id=622129601327484&client_secret=0f808c9fbc9f3ceab73d41339702063e&grant_type=client_credentials"));
//////
//////        access_token=access_token.split("=")[1].trim();
//////        System.out.println(access_token);
////        String input_token = "EAAI10tXkMXwBANwlhM8JJ355EgjU8giAiu6PoaDpTGjhBwcbUfejbhBvEinRylNJrUYDGy2eNS091et9LYWB4DZBF2HeLJpF32iiKqus586W1TG5bm5ZB6dTfDOtmfZCpSBRynWQgglQwcsTkytFZAv0sz2ACRWnXwnZCrgUZBdQZDZD";
////        String access_token = "622129601327484|SR8Fs6oT4wV5TayZopEQ9IP4tWY";
//////        System.out.println(post("https://graph.facebook.com/debug_token",
//////                String.format("input_token=%s&access_token=%s", input_token, access_token)));
////        System.out.println(post("https://graph.facebook.com/debug_token","input_token=EAAI10tXkMXwBANwlhM8JJ355EgjU8giAiu6PoaDpTGjhBwcbUfejbhBvEinRylNJrUYDGy2eNS091et9LYWB4DZBF2HeLJpF32iiKqus586W1TG5bm5ZB6dTfDOtmfZCpSBRynWQgglQwcsTkytFZAv0sz2ACRWnXwnZCrgUZBdQZDZD&access_token=622129601327484|SR8Fs6oT4wV5TayZopEQ9IP4tWY"));
//////                "input_token="+ URLEncoder.encode(input_token,"UTF-8")+
////                "&access_token="+URLEncoder.encode(access_token,"UTF-8")+"","POST"));
//    }

    private static final String USER_AGENT = "Mozilla/5.0";

    public static String post(String url, String urlParameters) throws IOException {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("accept", "*/*");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in;
        if(con.getResponseCode()>= 400)
            in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        else
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }



    public static String executePost(String targetURL, String urlParameters) {
        HttpsURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent",USER_AGENT);
            connection.setRequestProperty("accept","*/*");
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"); // Do as if you're using Chrome 41 on Windows 7.
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Connection", "keep-alive");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is;
            if (connection.getResponseCode() >= 400) {
                is = connection.getErrorStream();
            } else {
                is = connection.getInputStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
