###
# Ingestion Service Account Set Up
###
resource "google_service_account" "ingestion" {
  display_name = "pla-igst-gsa"
  account_id   = "pla-igst-gsa"
  description  = "Ingestion service account for uploading data to gcs bucket"
  project      = "premier-league-analytics"
}

resource "google_storage_bucket_iam_member" "landing_zone" {
  bucket = google_storage_bucket.pla_landing_zone.name
  member = "serviceAccount:${google_service_account.ingestion.email}"
  role   = "roles/storage.objectUser"
}