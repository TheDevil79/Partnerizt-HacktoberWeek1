package com.partnerizt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@SpringBootApplication
public class PartneriztApplication {

    private static final Logger log = LoggerFactory.getLogger(PartneriztApplication.class);

    public static void main(String[] args) {
        loadDotEnvFiles();
        SpringApplication.run(PartneriztApplication.class, args);
    }

    /**
     * Automatically loads .env files from project root or current directory
     * so API keys and environment variables work effortlessly.
     */
    private static void loadDotEnvFiles() {
        String[] potentialPaths = {
                ".env",
                "../.env",
                "backend/.env",
                System.getProperty("user.dir") + "/.env",
                System.getProperty("user.dir") + "/../.env"
        };

        for (String path : potentialPaths) {
            File envFile = new File(path);
            if (envFile.exists() && envFile.isFile()) {
                log.info("Loading environment variables from: {}", envFile.getAbsolutePath());
                try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) {
                            continue;
                        }
                        int idx = line.indexOf('=');
                        String key = line.substring(0, idx).trim();
                        String value = line.substring(idx + 1).trim();
                        // Remove surrounding quotes if present
                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        if (System.getProperty(key) == null && System.getenv(key) == null) {
                            System.setProperty(key, value);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Could not parse .env file at {}: {}", path, e.getMessage());
                }
                break; // Load first valid .env found
            }
        }
    }
}
