package com.alisoftclub.frameworks.modular.plugin;

import java.util.EventListener;

public interface PluginListener extends EventListener {

    void onPluginFound(PluginManifest paramPluginManifest);

    void onPluginLoad(Plugin paramPlugin, PluginManifest paramPluginManifest);

    void onPluginUnload(Plugin paramPlugin, PluginManifest paramPluginManifest);

    void onPluginException(PluginManifest paramPluginManifest, Exception paramException);
}
