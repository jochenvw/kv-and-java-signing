import java.util.Base64;
import java.security.MessageDigest;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.nio.charset.StandardCharsets;

public class KeyvaultSignAndVerify {
    public static void main(String[] args) {
        String message = "Hello, world";
        signAndVerifyUsingKeyvault(message);
        signAndVerifyUsingJAVA(message);
    }

    private static byte[] readKeyFile(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] privateKeyBytes = new byte[(int) file.length()];
            // Load the private key from file
            fis.read(privateKeyBytes);
            fis.close();

            String fileContent = new String(privateKeyBytes, StandardCharsets.UTF_8);

            // Inspired here:
            // https://www.javacodegeeks.com/2020/04/encrypt-with-openssl-decrypt-with-java-using-openssl-rsa-public-private-keys.html
            String cleanFileContent = fileContent
                    .replaceAll("\\n", "")
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .trim();



            return Base64.getDecoder().decode(cleanFileContent);
        } catch (Exception e) {
            System.out.println("ERROR while reading " + filePath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void signAndVerifyUsingJAVA(String message) {
        try {
            System.out.println("\r\n\r\nJAVA Signature verification ...");
            byte[] privateKeyBytes = readKeyFile("privatepkcs8.pem");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // Create a signature object and initialize it with the private key
            Signature signature = Signature.getInstance("SHA512withRSA");
            signature.initSign(privateKey);

            // Sign the message
            byte[] messageBytes = message.getBytes("UTF-8");
            signature.update(messageBytes);
            byte[] signatureBytes = signature.sign();

            // Print the signature
            String signatureString = Base64.getEncoder().encodeToString(signatureBytes);

            // Load the public key from file
            byte[] publicKeyBytes = readKeyFile("publicpkcs8.pem");
            X509EncodedKeySpec pubkeySpec = new X509EncodedKeySpec(publicKeyBytes);
            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(pubkeySpec);

            // Verify the signature
            signatureBytes = Base64.getDecoder().decode(signatureString);
            signature = Signature.getInstance("SHA512withRSA");
            signature.initVerify(publicKey);
            signature.update(messageBytes);
            boolean verified = signature.verify(signatureBytes);

            // Print the verification result
            System.out.println("Signature string: " + signatureString);
            System.out.println("Signature verification result: " + verified);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private static void signAndVerifyUsingKeyvault(String message) {
        try {
            System.out.println("\r\n\r\nAzure KeyVault Signature verification ...");
            /**
             * Sign and verify using the Azure KeyVault
             */

            String bearerToken = "<INSERT BEARER TOKEN>";

            // Generate digest using SHA-512 and base64-encode it
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(message.getBytes("UTF-8"));
            String encodedDigest = Base64.getEncoder().encodeToString(digest);

            // Sign the digest using RS512 algorithm and private key stored in the key vault
            String signUrl = "<INSERT SIGN URL>";
            // print the encoded digest
            //System.out.println("Encoded digest: " + encodedDigest);
            // #CHECKED: Matches bash value

            String signRequest = String.format("{ \"alg\": \"RS512\", \"value\": \"%s\" }", encodedDigest);

            String response = sendPostRequest(signUrl, bearerToken, signRequest);
            String valueToVerify = getValueFromResponse(response);

            // Verify the signature using public key stored in the key vault
            String verifyUrl = "<INSERT VERIFY URL>";
            String verifyRequest = String.format("{ \"alg\": \"RS512\", \"value\": \"%s\", \"digest\": \"%s\" }", valueToVerify, encodedDigest);
            String verifyResponse = sendPostRequest(verifyUrl, bearerToken, verifyRequest);

            System.out.println("Signature string: " + valueToVerify);
            System.out.println("Signature verification result: " + verifyResponse);
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
