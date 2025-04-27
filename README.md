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

# Ingestion Flow

## Adding a new endpoint to ingest

1. Add avsc AVRO schema file based on endpoint
2. run ``make build-ingestion`` -> generates avro class to be used
3. Make java code changes to deconstruct api response to avro generated class
4. run ``make build-ingestion`` -> update java jar locally
5. run ``make run-ingestion`` -> Uploads first file to gcs bucket to initialize ingestion process
6. run ``make publish-ingestion`` -> publishes new docker image through JIB
7. Add the schedule for the new endpoint in terraform/locals.tf
8. run ``terraform plan``
9. run ``terraform apply``
10. Validate ingestion pipelines are set up

# Processing Flow

## Teams

Combine landing zone raw data of teams list with the teams/id endpoint to enhance teams tables with additional metadata
like coaches, players etc.
