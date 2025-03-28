terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "6.27.0"
    }
  }

  backend "gcs" {
    bucket = "tf-state-dv"
    prefix = "terraform/state"
  }
}

provider "google" {
  project = "premier-league-analytics"
  region  = "us-central1"
  zone    = "us-central1-c"
}