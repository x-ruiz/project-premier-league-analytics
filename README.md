# premier-league-stats

Data engineering project based on premier league stats

![premier-league-stats-architecture](https://github.com/user-attachments/assets/84c87b2e-c2cb-46b3-8201-e0af729e0003)

## Processing Flow

### Teams

Combine landing zone raw data of teams list with the teams/id endpoint to enhance teams tables with additional metadata
like coaches, players etc.

# Set Up

## GCP Authentication

In order for the java application and terraform configurations to have access to the GCP project,
Application Default Credentials (ADC) must be setup.

``gcloud auth application-default login`` -> "~/.config/gcloud/application_default_credentials.json"

``gcloud auth configure-docker `` -> To authenticate with artifact registry

1. Install Terraform

## Environment Variables

API_KEY | Authentication Key for the football api
