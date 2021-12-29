package com.alisoftclub.frameworks.modular.plugin;

public interface BootApplicationListener extends PluginListener {

    void onApplicationInitialized(BootApplication paramBootApplication, PluginManifest paramPluginManifest);

    void onApplicationStart(BootApplication paramBootApplication, PluginManifest paramPluginManifest);

    void onApplicationStop(BootApplication paramBootApplication, PluginManifest paramPluginManifest);
}
