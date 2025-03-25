provider "google" {
  project = "premier-league-analytics"
  region  = "us-central1"
  zone    = "us-central1-c"
}

terraform {
  backend "gcs" {
    bucket = "tf-state-dv"
    prefix = "terraform/state"
  }
}
