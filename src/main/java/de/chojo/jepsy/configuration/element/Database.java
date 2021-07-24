package de.chojo.jepsy.configuration.element;

public class Database {
    private String host = "localhost";
    private String port = "5432";
    private String user = "root";
    private String password = "passy";
    private String database = "db";
    private String schema = "jepsy";

    public String host() {
        return host;
    }

    public String port() {
        return port;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }

    public String database() {
        return database;
    }

    public String schema() {
        return schema;
    }
}
