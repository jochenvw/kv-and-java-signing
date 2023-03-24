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