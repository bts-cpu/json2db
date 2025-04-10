package com.btscpu.json2db;

import com.btscpu.json2db.service.DictionaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Json2dbApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Json2dbApplication.class);
    private final DictionaryService dictionaryService;

    public Json2dbApplication(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Json2dbApplication.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info("Starting JSON to database import...");
        try {
            dictionaryService.importJsonFiles("dicdata");
            logger.info("JSON to database import completed successfully.");
        } catch (Exception e) {
            logger.error("Failed to import JSON data", e);
        }
    }
}