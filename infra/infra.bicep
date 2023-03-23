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
        objectId: '35e39bc7-dde3-49e0-a410-58ce9de9d32c'
        permissions: {
          certificates: [
            'get'
            'list'
            'delete'
            'create'
            'import'
            'update'
          ]
        }
        tenantId: subscription().tenantId
      }
    ]
  }
}
