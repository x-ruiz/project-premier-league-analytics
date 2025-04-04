locals {
  ingestion_schedule_map = {
    teams : "0 1 * * 6"
    competitions : "0 2 1 * *"
    areas : "0 1 1 1 *"
  }
}