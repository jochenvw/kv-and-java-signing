# Pre requisites

  - Install Azure CLI `curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash`
  - Ensure OpenSSL is installed [https://www.openssl.org/](https://www.openssl.org/)


# Get started
  - Create public/private key pair using [this](https://www.geeksforgeeks.org/how-to-create-a-public-private-key-pair/) tutorial:
    ```
    In OpenSSL:
    genrsa -out private.pem
    rsa -in private.pem -pubout -out public.pem ## Not needed actually because never used
    ```

  - Create service principal for application
    ```
    az ad sp create-for-rbac --scopes /subscriptions/5fcb0d36-846f-4721-86e9-47f6c43494fd --name "csu-nl-jvw-ravi-app" --role "Contributor"
    ```
    Store the credentials somewhere safe.

  - Import private key into keyvault
    ```
    az keyvault key import --vault-name jvw-ravi-vault --name jvw-key --pem-file private.pem
    ```

  - Get access token for the service principal - use values from the service principal created in step 2.:
    ```
    curl -X POST -H 'Content-Type: application/x-www-form-urlencoded' \
    https://login.microsoftonline.com/<tenant-id>/oauth2/v2.0/token \
    -d 'client_id=<client-id>' \
    -d 'grant_type=client_credentials' \
    -d 'scope=https://vault.azure.com/.default' \
    -d 'client_secret=<client-secret>'
    ```

# Sign and verifiction scripts
We've built two scripts to sign and verify a message:
  1. [/src/bash-sign-and-verify/sign-verify.sh](./src/bash-sign-and-verify/sign-verify.sh) Bash version that signs a message 'Hello, World' using the Azure KeyVault REST API endpoint, only using CURL commands
  2. [/src/java-sign-and-verify/KeyvaultSignAndVerify.java](./src/java-sign-and-verify/KeyvaultSignAndVerify.java) Quick-n-dirty JAVA version of the same.


