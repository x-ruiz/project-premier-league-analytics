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

## Adding a new endpoint to ingest

1. Add avsc AVRO schema file based on endpoint
2. run ``make build-ingestion`` -> generates avro class to be used
3. Make java code changes to deconstruct api response to avro generated class
4. run ``make build-ingestion`` -> update java jar locally
5. run ``make run-ingestion`` -> Uploads first file to gcs bucket to initialize ingestion process
6. Add the schedule for the new endpoint in terraform/locals.tf
7. run ``terraform plan``
8. run ``terraform apply``
9. Validate ingestion pipelines are set up
