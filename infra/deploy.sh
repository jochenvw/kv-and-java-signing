#!/bin/sh
# Provisions the resources in Azure to reproduce issue

az login --use-device-code
az account set --subscription "5fcb0d36-846f-4721-86e9-47f6c43494fd"

# Create a service principal with required parameter - you need to do this from your PC
# be sure to save the credentials in a safe place
# az ad sp create-for-rbac --scopes /subscriptions/5fcb0d36-846f-4721-86e9-47f6c43494fd --name "csu-nl-jvw-ravi-app" --role "Contributor"

az group create --name "csu-nl-jvw-ravi" --location "westeurope"
az deployment group create --resource-group "csu-nl-jvw-ravi" --template-file "infra.bicep"