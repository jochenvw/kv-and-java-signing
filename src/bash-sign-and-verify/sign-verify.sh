# 01 - Use sha512 to generate a digest of the message string - base4 encode it
# Replace bearer token with value using readme.md instructions
digest=$(echo -n "Hello, world" | openssl dgst -sha512 -binary | base64)
BEARER="<INSERT YOUR BEARER TOKEN HERE>"
SIGN_URL="<INSERT YOUR SIGN URL HERE>" # example: https://jvw-ravi-vault.vault.azure.net/keys/jvw-key/943283d300364f2f810c55af07f19141/sign?api-version=7.3
VERIFY_URL="<INSERT YOUR VERIFY URL HERE>" # example: https://jvw-ravi-vault.vault.azure.net/keys/jvw-key/943283d300364f2f810c55af07f19141/verify?api-version=7.3

# Sign the digest using RS512 alg using the private key stored in the key vault
response=$(curl -X POST \
     -H "Authorization: Bearer $BEARER" \
     -H "Content-Type: application/json" \
     -d "{ \"alg\": \"RS512\", \"value\": \"$digest\" }" \
          "$SIGN_URL")


# Extract the "value"-key from the response object
value_to_verify=$(echo $response | jq -r '.value')

# Verify the signature using the public key stored in the key vault
curl -X POST \
     -H "Authorization: Bearer $BEARER" \
     -H "Content-Type: application/json" \
     -d "{ \"alg\": \"RS512\", \"value\": \"$value_to_verify\", \"digest\": \"$digest\" }" \
          "$VERIFY_URL"