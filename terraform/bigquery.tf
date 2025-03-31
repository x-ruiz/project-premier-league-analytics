resource "google_bigquery_dataset" "pla_landing" {
  dataset_id = "pla_landing_us"
  location   = "us"
}
resource "google_bigquery_table" "pla_landing_raw" {
  for_each = local.ingestion_schedule_map

  dataset_id          = "pla_landing_us"
  table_id            = "t_${each.key}_raw"
  deletion_protection = false

  external_data_configuration {
    autodetect = true
    source_uris = [
      "gs://pla-landing-zone-bkt-us/${each.key}/dt=*/data.avro"
    ]
    source_format = "AVRO"
    hive_partitioning_options {
      mode                     = "STRINGS"
      source_uri_prefix        = "gs://pla-landing-zone-bkt-us/${each.key}"
      require_partition_filter = true
    }
  }
}