package com.starfish.sphinx;

import static edu.cmu.pocketsphinx.Decoder.defaultConfig;
import static edu.cmu.pocketsphinx.Decoder.fileConfig;

import java.io.File;

import edu.cmu.pocketsphinx.*;

public class WordRecognizerSetup {

    static {
        System.loadLibrary("pocketsphinx_jni");
    }

    private final Config config;

    /**
     * Creates new speech recognizer builder with default configuration.
     */
    public static WordRecognizerSetup defaultSetup() {
        return new WordRecognizerSetup(defaultConfig());
    }

    /**
     * Creates new speech recognizer builder from configuration file.
     * Configuration file should consist of lines containing key-value pairs.
     *
     * @param configFile configuration file
     */
    public static WordRecognizerSetup setupFromFile(File configFile) {
        return new WordRecognizerSetup(fileConfig(configFile.getPath()));
    }

    private WordRecognizerSetup(Config config) {
        this.config = config;
    }

    public WordRecognizer getRecognizer() {
        return new WordRecognizer(config);
    }

    public WordRecognizerSetup setAcousticModel(File model) {
        return setString("-hmm", model.getPath());
    }

    public WordRecognizerSetup setDictionary(File dictionary) {
        return setString("-dict", dictionary.getPath());
    }

    public WordRecognizerSetup setSampleRate(int rate) {
        return setFloat("-samprate", rate);
    }

    public WordRecognizerSetup setRawLogDir(File dir) {
        return setString("-rawlogdir", dir.getPath());
    }

    public WordRecognizerSetup setKeywordThreshold(float threshold) {
        return setFloat("-kws_threshold", threshold);
    }

    public WordRecognizerSetup setBoolean(String key, boolean value) {
        config.setBoolean(key, value);
        return this;
    }

    public WordRecognizerSetup setInteger(String key, int value) {
        config.setInt(key, value);
        return this;
    }

    public WordRecognizerSetup setFloat(String key, float value) {
        config.setFloat(key, value);
        return this;
    }

    public WordRecognizerSetup setString(String key, String value) {
        config.setString(key, value);
        return this;
    }
}
