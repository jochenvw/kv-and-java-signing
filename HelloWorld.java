public class HelloWorld {
    public static void main(String[] args) {
    
        // Call an Azure Keyvault REST API endpoint to sign a message 'abc'
        String response = HttpUtils.sendGet("https://<your-keyvault-name>.vault.azure.net/secrets/<your-secret-name>/sign?api-version=7.0&value=abc");
        System.out.println(response);

    }
}
