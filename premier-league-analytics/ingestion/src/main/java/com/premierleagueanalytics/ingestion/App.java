package com.premierleagueanalytics.ingestion;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        SparkHttpToAvro httpToAvro = new SparkHttpToAvro();
        httpToAvro.getTeams();
    }
}
