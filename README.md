# pre-req

  - Install Azure CLI `curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash`
  - Ensure OpenSSL is installed [https://www.openssl.org/](https://www.openssl.org/)
  - Create public/private key pair using [this](https://www.geeksforgeeks.org/how-to-create-a-public-private-key-pair/) tutorial:
    ```
    In OpenSSL:
    genrsa -out private.pem
    rsa -in private.pem -pubout -out public.pem
    ```
  - Import key into keyvault
    ```
    az keyvault key import --vault-name jvw-ravi-vault --name jvw-key --pem-file private.pem
    ```
  - Get access token for the service principal
    ```
    curl -X POST -H 'Content-Type: application/x-www-form-urlencoded' \
    https://login.microsoftonline.com/<tenant-id>/oauth2/v2.0/token \
    -d 'client_id=<client-id>' \
    -d 'grant_type=client_credentials' \
    -d 'scope=2ff814a6-3304-4ab8-85cb-cd0e6f879c1d%2F.default' \

    -d 'client_secret=<client-secret>'
    ```

  - Docs here: https://learn.microsoft.com/en-us/rest/api/keyvault/keys/sign/sign?tabs=HTTP 
    ```
    curl -X POST -H "Authorization: Bearer [TOKEN]" -H "Content-Type: application/json" https://jvw-ravi-vault.vault.azure.net/keys/jvw-key/943283d300364f2f810c55af07f19141/sign?api-version=7.3 -d "{ \"alg\": \"RS512\", \"value\": \"hello world\" }"
    ```