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


### README by ChatGPT
## Readme
This repository contains a set of files related to signing and verifying data using public and private key pairs stored in Azure Key Vault.

### Files
KeyvaultSignAndVerify.java
This file contains a Java implementation of signing and verifying data using public and private key pairs stored in Azure Key Vault.

The main method of the KeyvaultSignAndVerify class signs a message using SHA-512 and the RS512 algorithm with a private key stored in the key vault. It then verifies the signature using the corresponding public key. The bearer token, sign and verify URLs need to be provided by the user.

### sign-verify.sh
This file contains a Bash implementation of signing and verifying data using public and private key pairs stored in Azure Key Vault.

The script generates a digest of the message string using SHA-512, base64 encodes it, and then signs the digest using the RS512 algorithm with a private key stored in the key vault. It then verifies the signature using the corresponding public key. The bearer token, sign and verify URLs need to be provided by the user.

### build-n-run.sh
This file compiles and runs the Java implementation of signing and verifying data using public and private key pairs stored in Azure Key Vault.

deploy-infra.sh
This file provisions the necessary resources in Azure to reproduce the issue. The script logs in to Azure using device code and creates a resource group and deploys an ARM template.

Instructions
Before running the Java or Bash implementation, the bearer token, sign and verify URLs need to be updated in the code. These values can be obtained from Azure Key Vault.

To reproduce the issue, the Azure resources need to be provisioned using the deploy-infra.sh script. After the resources have been provisioned, the Java or Bash implementation can be run.