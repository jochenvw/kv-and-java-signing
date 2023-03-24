ISSUE
 
CX is creating a signature for data using the private key stored in the key vault using key vault  REST API 
sign - REST API (Azure Key Vault) | Microsoft Learn
verify - REST API (Azure Key Vault) | Microsoft Learn
and this works as expected (it gets verified correctly), however - when they want to verify the signature using pure java client code using the same algorithm and public key, the signature is not verified.
 
CX believe since they're are using the standard algorithm and valid public key combination it should work in Java client code as well.
 
 
TROUBLESHOOTING DETAILS

Based on the CX description - the sender application is using REST API of Azure Key Vault for signing the payload and sends the payload and signature. The sender also shared the public key to the receiver.
Receiver application is in SAP which uses the shared public key to verify the signature offline using standard Java libraries.
 
using the Rest API to generate the signature and verify
https://az-lena-keyvault-demo.vault.azure.net/keys/ravinewcertificate/3eb3da63315f42fc8b4f5af4cf79b718/sign?api-version=7.3
https://az-lena-keyvault-demo.vault.azure.net/keys/ravinewcertificate/3eb3da63315f42fc8b4f5af4cf79b718/verify?api-version=7.3
 
CX has an app registered and getting the token for that app
 
curl -X POST -d 'grant_type=client_credentials&client_id=c7ced622-d045-480b-819d-1bec5d838788&client_secret=BnX8Q~LMEagUHzYo5e9o33kPi0tqmmZbVHKz4bT1&resource=https%3A%2F%2Fvault.azure.net' https://login.microsoftonline.com/6e93a626-8aca-4dc1-9191-ce291b4b75a1/oauth2/token
 
CX shared a JAVA code used to verify the signature:
 
import java.io.File;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
public class VerifySign_BC {
    public static void main(String[] args) throws Exception {
    
        //Get Public Key
        PublicKey publicKey = readPublicKey(new File("C:\\VerifySign\\keypublic.pem"));
        
        //Get Signature & Payload
        String signatureString = "OVNRFYkm4bZFpQ-T5VDWUiDd0d63XXwstzXcW_mkbgulj4WkL9MsxnazdcKKegQPNpTDvgg_-WpqIPtEyxrg3m_QhQ0M4999GfHMb4cUSWa6xE_cfv_e5toD2-R8qrSowXmP-4bm4H_Is-81_yHTe-L74UMFcHBCiuGVCTJpFVI0miB90_Z20SHzAnVpjkxr7fFiKuyH_61QYKf5LiVj_pDAElWqWP4YkFsa_NeOn4XNV0MOCb1EXp1rPM-7WzUV5rYLlWbFPsNZshffMK-vKA6B6f5PZqXkHLud_PuROvIoJvgFflPEIQk_3lzqL3NTCeW8uWKDOL0tfDTFhMPZ_A";    
        byte[] encSign = Base64.getUrlDecoder().decode(signatureString);
        byte[] dataBytes = "YmU2YjY2Nzk5MDZhNzNiZDNmZjU0MjcyMTQ3ZjNlMTg=".getBytes();

        //Verify Signature
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataBytes);
        
        boolean verifies = signature.verify(encSign);
        System.out.println("signature verifies: " + verifies);
    }
    public static RSAPublicKey readPublicKey(File file) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        try (FileReader keyReader = new FileReader(file);
            PemReader pemReader = new PemReader(keyReader)) 
        {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
            return (RSAPublicKey) factory.generatePublic(pubKeySpec);
        }
    }
}
 
Discussed the case with TA (Marco), confirmed that the above inquiry is out of AKV teams support scope
(Customer may need a CXE engineer, familiar with JAVA code development / troubleshooting in order to assist with the application development - as required by CX).
 
 
Engaged IM + CSAM, provided the above update
 
 
Cloud Solution Architect (Jochen van Wylick) proposed a meeting with CX on Wednesday.
 
(pending for the further update / meeting outcome)