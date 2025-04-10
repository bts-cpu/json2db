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
        createTables();
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

        int threadCount = Runtime.getRuntime().availableProcessors() * 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger processedCount = new AtomicInteger(0);
        int totalFiles = files.length;
        int batchSize = 1000;
        List<Word> wordBatch = new ArrayList<>(batchSize); // 单词批次
        List<Word> phraseBatch = new ArrayList<>(batchSize); // 词组批次
        int logInterval = 1000;
        ThreadLocal<String> lastEntry = new ThreadLocal<>();

        for (File file : files) {
            executor.submit(() -> {
                try {
                    Word word = objectMapper.readValue(file, Word.class);
                    // 清理特殊符号并判断是单词还是词组
                    String cleanedWord = cleanWord(word.getWord());
                    word.setWord(cleanedWord);
                    boolean isPhrase = isPhrase(cleanedWord);
                    lastEntry.set(cleanedWord);
                    int currentCount = processedCount.incrementAndGet();

                    synchronized (isPhrase ? phraseBatch : wordBatch) {
                        if (isPhrase) {
                            phraseBatch.add(word);
                            if (phraseBatch.size() >= batchSize) {
                                insertPhrases(new ArrayList<>(phraseBatch));
                                phraseBatch.clear();
                            }
                        } else {
                            wordBatch.add(word);
                            if (wordBatch.size() >= batchSize) {
                                insertWords(new ArrayList<>(wordBatch));
                                wordBatch.clear();
                            }
                        }
                    }

                    if (currentCount % logInterval == 0) {
                        logger.info("Successfully processed entry: {} ({}/{})", lastEntry.get(), currentCount, totalFiles);
                    }
                } catch (IOException e) {
                    logger.error("Failed to parse JSON file: {}", file.getName(), e);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        synchronized (wordBatch) {
            if (!wordBatch.isEmpty()) {
                insertWords(wordBatch);
            }
        }
        synchronized (phraseBatch) {
            if (!phraseBatch.isEmpty()) {
                insertPhrases(phraseBatch);
            }
        }

        long endTime = System.currentTimeMillis();
        logger.info("Processed {} files in {} seconds", totalFiles, (endTime - startTime) / 1000.0);
    }

    private void createTables() {
        // 单词表
        String wordSql = "CREATE TABLE IF NOT EXISTS words (" +
                "word VARCHAR(255) PRIMARY KEY, " +
                "phonetic_uk VARCHAR(50), " +
                "phonetic_us VARCHAR(50), " +
                "meanings TEXT, " +
                "exam_types TEXT, " +
                "forms TEXT, " +
                "web_translations TEXT, " +
                "special_translations TEXT, " +
                "phrases TEXT, " +
                "synonyms TEXT, " +
                "related_words TEXT, " +
                "examples TEXT, " +
                "authoritative_sentences TEXT, " +
                "media_sentences TEXT, " +
                "etymology TEXT, " +
                "collins TEXT, " +
                "collins_primary TEXT, " +
                "wikipedia_digest TEXT, " +
                "music_sentences TEXT)";
        jdbcTemplate.execute(wordSql);
        logger.info("Table 'words' created or already exists");

        // 词组表
        String phraseSql = "CREATE TABLE IF NOT EXISTS phrases (" +
                "phrase VARCHAR(255) PRIMARY KEY, " +
                "phonetic_uk VARCHAR(50), " +
                "phonetic_us VARCHAR(50), " +
                "meanings TEXT, " +
                "exam_types TEXT, " +
                "forms TEXT, " +
                "web_translations TEXT, " +
                "special_translations TEXT, " +
                "phrases TEXT, " +
                "synonyms TEXT, " +
                "related_words TEXT, " +
                "examples TEXT, " +
                "authoritative_sentences TEXT, " +
                "media_sentences TEXT, " +
                "etymology TEXT, " +
                "collins TEXT, " +
                "collins_primary TEXT, " +
                "wikipedia_digest TEXT, " +
                "music_sentences TEXT)";
        jdbcTemplate.execute(phraseSql);
        logger.info("Table 'phrases' created or already exists");
    }

    private String cleanWord(String word) {
        if (word == null) return "";
        // 保留字母、数字、空格和 -，移除其他特殊符号
        return word.replaceAll("[^a-zA-Z0-9\\s-]", "").trim();
    }

    private boolean isPhrase(String word) {
        if (word == null || word.trim().isEmpty()) return false;
        // 按空格或 - 分割
        String[] parts = word.split("[\\s-]+");
        // 移除空字符串
        List<String> nonEmptyParts = new ArrayList<>();
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                nonEmptyParts.add(part);
            }
        }
        // 如果有 2 个及以上单词，视为词组
        return nonEmptyParts.size() >= 2;
    }

    private void insertWords(List<Word> words) {
        String sql = "INSERT OR REPLACE INTO words (word, phonetic_uk, phonetic_us, meanings, exam_types, forms, " +
                "web_translations, special_translations, phrases, synonyms, related_words, examples, " +
                "authoritative_sentences, media_sentences, etymology, collins, collins_primary, wikipedia_digest, music_sentences) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();
        for (Word word : words) {
            try {
                batchArgs.add(new Object[]{
                        word.getWord(),
                        word.getPhonetics() != null ? word.getPhonetics().getUk() : "",
                        word.getPhonetics() != null ? word.getPhonetics().getUs() : "",
                        word.getMeanings() != null && !word.getMeanings().isEmpty() ? objectMapper.writeValueAsString(word.getMeanings()) : null,
                        word.getExam_types() != null && !word.getExam_types().isEmpty() ? objectMapper.writeValueAsString(word.getExam_types()) : null,
                        word.getForms() != null ? objectMapper.writeValueAsString(word.getForms()) : null,
                        word.getWeb_translations() != null && !word.getWeb_translations().isEmpty() ? objectMapper.writeValueAsString(word.getWeb_translations()) : null,
                        word.getSpecial_translations() != null && !word.getSpecial_translations().isEmpty() ? objectMapper.writeValueAsString(word.getSpecial_translations()) : null,
                        word.getPhrases() != null && !word.getPhrases().isEmpty() ? objectMapper.writeValueAsString(word.getPhrases()) : null,
                        word.getSynonyms() != null && !word.getSynonyms().isEmpty() ? objectMapper.writeValueAsString(word.getSynonyms()) : null,
                        word.getRelated_words() != null && !word.getRelated_words().isEmpty() ? objectMapper.writeValueAsString(word.getRelated_words()) : null,
                        word.getExamples() != null && !word.getExamples().isEmpty() ? objectMapper.writeValueAsString(word.getExamples()) : null,
                        word.getAuthoritative_sentences() != null && !word.getAuthoritative_sentences().isEmpty() ? objectMapper.writeValueAsString(word.getAuthoritative_sentences()) : null,
                        word.getMedia_sentences() != null && !word.getMedia_sentences().isEmpty() ? objectMapper.writeValueAsString(word.getMedia_sentences()) : null,
                        word.getEtymology() != null ? objectMapper.writeValueAsString(word.getEtymology()) : null,
                        word.getCollins() != null && !word.getCollins().isEmpty() ? objectMapper.writeValueAsString(word.getCollins()) : null,
                        word.getCollins_primary() != null && !word.getCollins_primary().isEmpty() ? objectMapper.writeValueAsString(word.getCollins_primary()) : null,
                        word.getWikipedia_digest() != null && !word.getWikipedia_digest().isEmpty() ? word.getWikipedia_digest() : null,
                        word.getMusic_sentences() != null && !word.getMusic_sentences().isEmpty() ? objectMapper.writeValueAsString(word.getMusic_sentences()) : null
                });
            } catch (Exception e) {
                logger.error("Failed to serialize word: {}", word.getWord(), e);
            }
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void insertPhrases(List<Word> phrases) {
        String sql = "INSERT OR REPLACE INTO phrases (phrase, phonetic_uk, phonetic_us, meanings, exam_types, forms, " +
                "web_translations, special_translations, phrases, synonyms, related_words, examples, " +
                "authoritative_sentences, media_sentences, etymology, collins, collins_primary, wikipedia_digest, music_sentences) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();
        for (Word phrase : phrases) {
            try {
                batchArgs.add(new Object[]{
                        phrase.getWord(),
                        phrase.getPhonetics() != null ? phrase.getPhonetics().getUk() : "",
                        phrase.getPhonetics() != null ? phrase.getPhonetics().getUs() : "",
                        phrase.getMeanings() != null && !phrase.getMeanings().isEmpty() ? objectMapper.writeValueAsString(phrase.getMeanings()) : null,
                        phrase.getExam_types() != null && !phrase.getExam_types().isEmpty() ? objectMapper.writeValueAsString(phrase.getExam_types()) : null,
                        phrase.getForms() != null ? objectMapper.writeValueAsString(phrase.getForms()) : null,
                        phrase.getWeb_translations() != null && !phrase.getWeb_translations().isEmpty() ? objectMapper.writeValueAsString(phrase.getWeb_translations()) : null,
                        phrase.getSpecial_translations() != null && !phrase.getSpecial_translations().isEmpty() ? objectMapper.writeValueAsString(phrase.getSpecial_translations()) : null,
                        phrase.getPhrases() != null && !phrase.getPhrases().isEmpty() ? objectMapper.writeValueAsString(phrase.getPhrases()) : null,
                        phrase.getSynonyms() != null && !phrase.getSynonyms().isEmpty() ? objectMapper.writeValueAsString(phrase.getSynonyms()) : null,
                        phrase.getRelated_words() != null && !phrase.getRelated_words().isEmpty() ? objectMapper.writeValueAsString(phrase.getRelated_words()) : null,
                        phrase.getExamples() != null && !phrase.getExamples().isEmpty() ? objectMapper.writeValueAsString(phrase.getExamples()) : null,
                        phrase.getAuthoritative_sentences() != null && !phrase.getAuthoritative_sentences().isEmpty() ? objectMapper.writeValueAsString(phrase.getAuthoritative_sentences()) : null,
                        phrase.getMedia_sentences() != null && !phrase.getMedia_sentences().isEmpty() ? objectMapper.writeValueAsString(phrase.getMedia_sentences()) : null,
                        phrase.getEtymology() != null ? objectMapper.writeValueAsString(phrase.getEtymology()) : null,
                        phrase.getCollins() != null && !phrase.getCollins().isEmpty() ? objectMapper.writeValueAsString(phrase.getCollins()) : null,
                        phrase.getCollins_primary() != null && !phrase.getCollins_primary().isEmpty() ? objectMapper.writeValueAsString(phrase.getCollins_primary()) : null,
                        phrase.getWikipedia_digest() != null && !phrase.getWikipedia_digest().isEmpty() ? phrase.getWikipedia_digest() : null,
                        phrase.getMusic_sentences() != null && !phrase.getMusic_sentences().isEmpty() ? objectMapper.writeValueAsString(phrase.getMusic_sentences()) : null
                });
            } catch (Exception e) {
                logger.error("Failed to serialize phrase: {}", phrase.getWord(), e);
            }
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
