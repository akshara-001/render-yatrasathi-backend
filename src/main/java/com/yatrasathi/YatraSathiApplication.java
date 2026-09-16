package com.yatrasathi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class YatraSathiApplication {

    public static void main(String[] args) {
        SpringApplication.run(YatraSathiApplication.class, args);
    }

    @Bean
    CommandLineRunner startupDebug(MongoTemplate mongoTemplate) {
        return args -> {
            System.out.println("========================================");
            System.out.println("[DEBUG] YatraSathi Backend Starting...");
            System.out.println("========================================");

            // Check MongoDB connection
            try {
                String dbName = mongoTemplate.getDb().getName();
                System.out.println("[DEBUG] ✅ MongoDB CONNECTED successfully!");
                System.out.println("[DEBUG] Database name: " + dbName);
            } catch (Exception e) {
                System.out.println("[DEBUG] ❌ MongoDB CONNECTION FAILED!");
                System.out.println("[DEBUG] Error: " + e.getMessage());
                e.printStackTrace();
            }

            // Print env var status (masked for security)
            String mongoUri = System.getenv("MONGODB_URI");
            String mongoUri2 = System.getenv("MONGO_URI");
            System.out.println("[DEBUG] ENV MONGODB_URI is " + (mongoUri != null ? "SET (length=" + mongoUri.length() + ")" : "NOT SET ❌"));
            System.out.println("[DEBUG] ENV MONGO_URI is " + (mongoUri2 != null ? "SET (length=" + mongoUri2.length() + ")" : "NOT SET ❌"));
            System.out.println("[DEBUG] ENV JWT_SECRET is " + (System.getenv("JWT_SECRET") != null ? "SET ✅" : "NOT SET ❌"));
            System.out.println("[DEBUG] ENV MAIL_USERNAME is " + (System.getenv("MAIL_USERNAME") != null ? "SET ✅" : "NOT SET ❌"));
            System.out.println("[DEBUG] ENV PORT is " + (System.getenv("PORT") != null ? System.getenv("PORT") : "NOT SET (using default)"));

            System.out.println("========================================");
            System.out.println("[DEBUG] ✅ Application started successfully!");
            System.out.println("========================================");
        };
    }
}
