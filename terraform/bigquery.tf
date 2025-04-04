locals {
  views_directory = "view_schemas"
  views_map       = {
    for filename in fileset(
      local.views_directory, "*.sql") :
    trimsuffix(filename, ".sql") => file(format("%s/%s", local.views_directory, filename))
  }
}

output "view_map_output" {
  value = local.views_map
}

###
# Landing Zone Setup
###
resource "google_bigquery_dataset" "pla_landing" {
  dataset_id = "pla_landing_us"
  location   = "us"
}

resource "google_bigquery_table" "pla_landing_raw" {
  for_each = local.ingestion_schedule_map

  dataset_id          = google_bigquery_dataset.pla_landing.dataset_id
  table_id            = "t_${each.key}_raw"
  deletion_protection = false

  external_data_configuration {
    autodetect = true
    source_uris = [
      "gs://pla-landing-zone-bkt-us/${each.key}/dt=*/data.avro"
      // this is a problem because ingestion pipelines are built in this same module
      // so the table won't get created until the pipeline is created and data exists in the bucket
    ]
    source_format = "AVRO"
    hive_partitioning_options {
      mode                     = "STRINGS"
      source_uri_prefix        = "gs://pla-landing-zone-bkt-us/${each.key}"
      require_partition_filter = true
    }
  }
}

###
# Curated Zone Setup
###
resource "google_bigquery_dataset" "pla_curated" {
  dataset_id = "pla_curated_us"
  location   = "us"
}

resource "google_bigquery_table" "pla_curated_view" {
  for_each = local.views_map

  dataset_id = google_bigquery_dataset.pla_curated.dataset_id
  table_id   = "v_${each.key}"
  project    = "premier-league-analytics"

  view {
    query          = each.value
    use_legacy_sql = false
  }

}