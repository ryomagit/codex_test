package com.example.api.config;

public record DatabaseConfig(String jdbcUrl, String username, String password) {
  public static DatabaseConfig fromEnv() {
    String host = getenv("DB_HOST", "127.0.0.1");
    String port = getenv("DB_PORT", "3306");
    String database = getenv("DB_NAME", "book_app");
    String jdbcUrl =
        getenv(
            "DB_JDBC_URL",
            "jdbc:mysql://"
                + host
                + ":"
                + port
                + "/"
                + database
                + "?useUnicode=true&characterEncoding=utf8&connectionTimeZone=SERVER");
    return new DatabaseConfig(
        jdbcUrl, getenv("DB_USER", "book_app"), getenv("DB_PASSWORD", "book_app_password"));
  }

  private static String getenv(String key, String defaultValue) {
    String value = System.getenv(key);
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    return value;
  }
}
