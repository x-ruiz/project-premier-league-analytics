###
# Ingestion Service Account Set Up
###
resource "google_service_account" "ingestion" {
  display_name = "pla-igst-gsa"
  account_id   = "pla-igst-gsa"
  description  = "Ingestion service account for uploading data to gcs bucket"
  project      = "premier-league-analytics"
}

resource "google_storage_bucket_iam_member" "ingestion_pla_landing_zone_object_user" {
  bucket = google_storage_bucket.pla_landing_zone.name
  member = "serviceAccount:${google_service_account.ingestion.email}"
  role   = "roles/storage.objectUser"
}

resource "google_secret_manager_secret_iam_member" "ingestion_football_api_key_accessor" {
  secret_id = google_secret_manager_secret.football_api_key.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.ingestion.email}"
}

resource "google_project_iam_member" "ingestion_ar_reader" {
  project = "premier-league-analytics"
  role    = "roles/artifactregistry.reader"
  member  = "serviceAccount:${google_service_account.ingestion.email}"
}

resource "google_project_iam_member" "ingestion_run_invoker" {
  project = "premier-league-analytics"
  role    = "roles/run.invoker"
  member  = "serviceAccount:${google_service_account.ingestion.email}"
}