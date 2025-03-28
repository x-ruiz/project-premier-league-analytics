INGESTION_JAR_NAME = ingestion/target/ingestion-1.0-SNAPSHOT-jar-with-dependencies.jar

install:
	mvn clean install

gen-avro:
	mvn clean generate-sources compile

build-ingestion:
	mvn clean package assembly:single -pl ingestion # includes dependencies

run-ingestion:
	java -jar $(INGESTION_JAR_NAME)

publish-ingestion:
	mkdir -p $(HOME)/jib-caches/java-app
	mvn compile jib:build -Djib.applicationCache=$(HOME)/jib-caches/java-app -pl ingestion