# Secret Management

We use Google Cloud Secret Manager. Create a file under terraform/secrets called api_key.txt with your api secret.

# BigQuery Resources

## View Creation

To create a view, add a sql file under the view_schemas directory (must end in .sql for terraform code to pick it up).
The name of the file will be the name of the view and it will be added to the curated zone dataset.