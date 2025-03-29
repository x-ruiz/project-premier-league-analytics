# premier-league-stats

Data engineering project based on premier league stats

![Premier League Stats Dashboard](https://github.com/user-attachments/assets/c238e0c9-0eb1-4101-bb22-8034b186612e)

# Set Up

## GCP Authentication

In order for the java application and terraform configurations to have access to the GCP project,
Application Default Credentials (ADC) must be setup.

``gcloud auth application-default login`` -> "~/.config/gcloud/application_default_credentials.json"

``gcloud auth configure-docker `` -> To authenticate with artifact registry

1. Install Terraform

## Environment Variables

API_KEY | Authentication Key for the football api
