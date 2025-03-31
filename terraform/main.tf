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
resource "google_cloud_run_v2_job" "ingestion" {
  for_each = local.ingestion_schedule_map

  location            = "us-central1"
  name                = "ingestion-${each.key}"
  deletion_protection = false
  template {
    template {
      service_account = google_service_account.ingestion.email
      containers {
        image = "us-central1-docker.pkg.dev/premier-league-analytics/java/ingestion:latest"
        command = ["java"]
        args = [
          "-cp",
          "@/app/jib-classpath-file",
          "com.premierleagueanalytics.ingestion.${title(each.key)}"
        ]
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
resource "google_cloud_scheduler_job" "ingestion" {
  for_each    = local.ingestion_schedule_map
  name        = "ingestion_${each.key}"
  description = "Schedule for running the batch ingestion job for loading ${each.key} data"
  schedule    = each.value
  time_zone   = "America/Chicago"

  retry_config {
    retry_count = 3
  }

  http_target {
    uri         = "https://${google_cloud_run_v2_job.ingestion[each.key].location}-run.googleapis.com/apis/run.googleapis.com/v1/namespaces/${google_cloud_run_v2_job.ingestion[each.key].project}/jobs/${google_cloud_run_v2_job.ingestion[each.key].name}:run"
    http_method = "POST"

    oauth_token {
      service_account_email = google_service_account.ingestion.email
    }
  }
}