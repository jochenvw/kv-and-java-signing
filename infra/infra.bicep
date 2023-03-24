// Sets up a keyvault with access from the service principal created in the cli script
var location = 'westeurope'

resource kv 'Microsoft.KeyVault/vaults@2022-11-01' = {
  name: 'jvw-ravi-vault'
  location: location
  properties: {
    sku: {
      name: 'premium'
      family: 'A'
    }  
    tenantId: subscription().tenantId
    accessPolicies: [
      {
        objectId: '<TODO: INSERT YOUR SERVICE PRINCIPAL ID>' // service principal id
        permissions: {
          keys: [
            'sign'
            'verify'
          ]
        }
        tenantId: subscription().tenantId
      }
    ]
  }
}
