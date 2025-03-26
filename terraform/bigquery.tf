resource "google_bigquery_dataset" "pla_landing" {
  dataset_id = "pla_landing_us"
  location   = "us"
}
resource "google_bigquery_table" "pla_raw" {
  dataset_id = "pla_landing_us"
  table_id   = "pla_teams_raw"

  external_data_configuration {
    autodetect = true
    source_uris = [
      "gs://pla-landing-zone-bkt-us/avro/dt=*/teams.avro"
    ]
    source_format = "AVRO"
    hive_partitioning_options {
      mode                     = "STRINGS"
      source_uri_prefix        = "gs://pla-landing-zone-bkt-us/avro"
      require_partition_filter = true
    }
  }
}