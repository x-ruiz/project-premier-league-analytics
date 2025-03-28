###
# GCS Setup
###
resource "google_storage_bucket" "pla_landing_zone" {
  location = "us"
  name     = "pla-landing-zone-bkt-us"
}

###
# Secret Management Setup
###
resource "google_secret_manager_secret" "football_api_key" {
  secret_id = "football-api-key"
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_version" "football_api_key_version" {
  secret = google_secret_manager_secret.football_api_key.id
  secret_data_wo = file("secrets/api_key.txt")
}

###
# Cloud Run Job Setup
###
resource "google_cloud_run_v2_job" "ingestion_teams" {
  location            = "us-central1"
  name                = "ingestion-teams"
  deletion_protection = false
  template {
    template {
      service_account = google_service_account.ingestion.email
      containers {
        image = "us-central1-docker.pkg.dev/premier-league-analytics/java/ingestion:latest"
        env {
          name = "API_KEY"
          value_source {
            secret_key_ref {
              secret  = google_secret_manager_secret.football_api_key.secret_id
              version = google_secret_manager_secret_version.football_api_key_version.version
            }
          }
        }
      }
    }
  }
}

###
# Cloud Scheduler Job Setup
###
resource "google_cloud_scheduler_job" "ingestion_teams" {
  name        = "ingestion_teams"
  description = "Schedule for running the batch ingestion job for loading teams data"
  schedule    = "0 1 * * *"
  time_zone   = "America/Chicago"

  retry_config {
    retry_count = 3
  }

  http_target {
    uri         = "https://${google_cloud_run_v2_job.ingestion_teams.location}-run.googleapis.com/apis/run.googleapis.com/v1/namespaces/${google_cloud_run_v2_job.ingestion_teams.project}/jobs/${google_cloud_run_v2_job.ingestion_teams.name}:run"
    http_method = "POST"

    oauth_token {
      service_account_email = google_service_account.ingestion.email
    }
  }
}