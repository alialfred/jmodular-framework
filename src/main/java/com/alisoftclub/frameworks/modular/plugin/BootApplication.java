package com.alisoftclub.frameworks.modular.plugin;

public interface BootApplication
        extends Plugin {

    void initialize(String[] paramArrayOfString);

    void startApplication();

    void stopApplication();

    default boolean isHeadless() {
        return false;
    }
}
