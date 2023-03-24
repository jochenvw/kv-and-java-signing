import java.util.Base64;
import java.security.MessageDigest;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
// import 

public class KeyvaultSignAndVerify {
    public static void main(String[] args) {
        try {
            String message = "Hello, world";
            String bearerToken = "<INSERT BEARER TOKEN>";

            // Generate digest using SHA-512 and base64-encode it
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(message.getBytes("UTF-8"));
            String encodedDigest = Base64.getEncoder().encodeToString(digest);

            // Sign the digest using RS512 algorithm and private key stored in the key vault
            String signUrl = "<INSERT SIGN URL>";
            // print the encoded digest
            System.out.println("Encoded digest: " + encodedDigest);
            // #CHECKED: Matches bash value

            String signRequest = String.format("{ \"alg\": \"RS512\", \"value\": \"%s\" }", encodedDigest);

            String response = sendPostRequest(signUrl, bearerToken, signRequest);
            String valueToVerify = getValueFromResponse(response);
            // print value to verify
            System.out.println("Value to verify: " + valueToVerify);

            // Verify the signature using public key stored in the key vault
            String verifyUrl = "<INSERT VERIFY URL>";
            String verifyRequest = String.format("{ \"alg\": \"RS512\", \"value\": \"%s\", \"digest\": \"%s\" }", valueToVerify, encodedDigest);
            String verifyResponse = sendPostRequest(verifyUrl, bearerToken, verifyRequest);

            System.out.println("Verification result: " + verifyResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sendPostRequest(String url, String bearerToken, String requestBody) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + bearerToken);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        OutputStream os = con.getOutputStream();
        os.write(requestBody.getBytes("UTF-8"));
        os.flush();
        os.close();

        int responseCode = con.getResponseCode();

        InputStream is;
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            is = con.getInputStream();
        } else {
            is = con.getErrorStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }


    private static String getValueFromResponse(String response) {
        String value = null;

        System.out.println("Response: " + response);

        try {
            String json = response.trim();
            int startIndex = json.indexOf("\"value\":") + 9;
            int endIndex = json.indexOf("}", startIndex);
            value = json.substring(startIndex, endIndex);
            value = value.replaceAll("\"", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }
}

