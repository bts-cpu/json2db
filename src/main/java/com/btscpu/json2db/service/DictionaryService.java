package com.btscpu.json2db.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.btscpu.json2db.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DictionaryService {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public DictionaryService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void importJsonFiles(String folderPath) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        createTable();
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            logger.error("Directory does not exist: {}", folderPath);
            throw new IllegalArgumentException("Invalid directory: " + folderPath);
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            logger.warn("No JSON files found in directory: {}", folderPath);
            return;
        }

        logger.info("Found {} JSON files to process", files.length);

        // 使用线程池并行处理
        int threadCount = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger processedCount = new AtomicInteger(0);
        int totalFiles = files.length;
        int batchSize = 1000;
        List<Word> batch = new ArrayList<>(batchSize);

        for (File file : files) {
            executor.submit(() -> {
                try {
                    Word word = objectMapper.readValue(file, Word.class);
                    int currentCount = processedCount.incrementAndGet();
                    synchronized (batch) {
                        batch.add(word);
                        if (batch.size() >= batchSize) {
                            insertWords(new ArrayList<>(batch));
                            batch.clear();
                        }
                    }
                    logger.info("Successfully processed word: {} ({}/{})", word.getWord(), currentCount, totalFiles);
                } catch (IOException e) {
                    logger.error("Failed to parse JSON file: {}", file.getName(), e);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        synchronized (batch) {
            if (!batch.isEmpty()) {
                insertWords(batch);
            }
        }

        long endTime = System.currentTimeMillis();
        logger.info("Processed {} files in {} seconds", totalFiles, (endTime - startTime) / 1000.0);
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS words (" +
                "word VARCHAR(255) PRIMARY KEY, " +
                "phonetic_uk VARCHAR(50), " +
                "phonetic_us VARCHAR(50), " +
                "meanings TEXT, " +
                "exam_types TEXT, " +
                "forms TEXT, " +
                "web_trans TEXT, " +
                "special_trans TEXT, " +
                "ee_trans TEXT, " +
                "phrs TEXT, " +
                "examples TEXT, " +
                "syno_phrases TEXT, " +
                "synonyms TEXT, " +
                "similar_words TEXT, " +
                "etymology TEXT, " +
                "auth_sents TEXT, " +
                "media_sents TEXT)";
        jdbcTemplate.execute(sql);
        logger.info("Table 'words' created or already exists");
    }

    private void insertWords(List<Word> words) {
        String sql = "INSERT OR REPLACE INTO words (word, phonetic_uk, phonetic_us, meanings, exam_types, forms, " +
                "web_trans, special_trans, ee_trans, phrs, examples, syno_phrases, synonyms, similar_words, " +
                "etymology, auth_sents, media_sents) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();
        for (Word word : words) {
            try {
                batchArgs.add(new Object[]{
                        word.getWord(),
                        word.getPhoneticUk(),
                        word.getPhoneticUs(),
                        objectMapper.writeValueAsString(word.getMeanings()),
                        objectMapper.writeValueAsString(word.getExamTypes()),
                        objectMapper.writeValueAsString(word.getForms()),
                        objectMapper.writeValueAsString(word.getWebTrans()),
                        objectMapper.writeValueAsString(word.getSpecialTrans()),
                        objectMapper.writeValueAsString(word.getEeTrans()),
                        objectMapper.writeValueAsString(word.getPhrs()),
                        objectMapper.writeValueAsString(word.getExamples()),
                        objectMapper.writeValueAsString(word.getSynoPhrases()),
                        objectMapper.writeValueAsString(word.getSynonyms()),
                        objectMapper.writeValueAsString(word.getSimilarWords()),
                        objectMapper.writeValueAsString(word.getEtymology()),
                        objectMapper.writeValueAsString(word.getAuthSents()),
                        objectMapper.writeValueAsString(word.getMediaSents())
                });
            } catch (Exception e) {
                logger.error("Failed to serialize word: {}", word.getWord(), e);
            }
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}