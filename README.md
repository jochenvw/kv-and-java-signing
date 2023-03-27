# Pre requisites

  - Install Azure CLI `curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash`
  - Ensure OpenSSL is installed [https://www.openssl.org/](https://www.openssl.org/)
  - Ensure you're logged into the Azure CLI `az login --use-device-code`


# Get started
  - Create public/private key pair using [this](https://www.javacodegeeks.com/2020/04/encrypt-with-openssl-decrypt-with-java-using-openssl-rsa-public-private-keys.html) tutorial:
    ```
    In OpenSSL:
    genrsa -out private.pem
    pkcs8 -topk8 -in private.pem -out privatepkcs8.pem -nocrypt
    rsa -pubout -outform pem -in privatepkcs8.pem -out publicpkcs8.pem
    ```

  - Create service principal for application
    ```
    az ad sp create-for-rbac --scopes /subscriptions/5fcb0d36-846f-4721-86e9-47f6c43494fd --name "csu-nl-jvw-ravi-app" --role "Contributor"
    ```
    Store the credentials somewhere safe.

  - Import private key into keyvault
    ```
    az keyvault key import --vault-name jvw-ravi-vault --name jvw-key --pem-file privatepkcs8.pem
    ```

  - Get access token for the service principal - use values from the service principal created in step 2.:
    ```
    curl -X POST -H 'Content-Type: application/x-www-form-urlencoded' \
    https://login.microsoftonline.com/<tenant-id>/oauth2/v2.0/token \
    -d 'client_id=<client-id>' \
    -d 'grant_type=client_credentials' \
    -d 'scope=https://vault.azure.net/.default' \
    -d 'client_secret=<client-secret>'
    ```

# Sign and verifiction scripts
We've built two scripts to sign and verify a message:
  1. [/src/bash-sign-and-verify/sign-verify.sh](./src/bash-sign-and-verify/sign-verify.sh) Bash version that signs a message 'Hello, World' using the Azure KeyVault REST API endpoint, only using CURL commands
  2. [/src/java-sign-and-verify/KeyvaultSignAndVerify.java](./src/java-sign-and-verify/KeyvaultSignAndVerify.java) Quick-n-dirty JAVA version of the same.


