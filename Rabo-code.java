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