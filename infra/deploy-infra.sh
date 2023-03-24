#!/bin/sh
# Provisions the resources in Azure to reproduce issue

az login --use-device-code
az account set --subscription "[subscription-id]"

az group create --name "csu-nl-jvw-ravi" --location "westeurope"
az deployment group create --resource-group "csu-nl-jvw-ravi" --template-file "infra.bicep"