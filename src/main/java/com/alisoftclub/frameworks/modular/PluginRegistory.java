package com.alisoftclub.frameworks.modular;

import com.alisoftclub.frameworks.modular.annotations.OnLoad;
import com.alisoftclub.frameworks.modular.annotations.OnShutdown;
import com.alisoftclub.frameworks.modular.annotations.TimerEvent;
import com.alisoftclub.frameworks.modular.plugin.Plugin;
import com.alisoftclub.frameworks.modular.plugin.PluginManifest;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

public class PluginRegistory {

    private static final Logger logger = Logger.getLogger(PluginRegistory.class.getCanonicalName());
    private final String id;
    private final PluginManifest pluginManifest;
    private final URL pluginFile;
    private boolean loaded = false;
    private Plugin plugin;
    private Class<Plugin> pluginClass;
    private final PluginManager pluginManager;
    private final PluginLoader loader;

    public PluginRegistory(PluginManager pluginManager, PluginLoader loader, PluginManifest pluginManifest, File pluginFile) throws Exception {
        this(pluginManager, loader, pluginManifest, pluginFile.toURI().toURL());
    }

    public PluginRegistory(PluginManager pluginManager, PluginLoader loader, PluginManifest pluginManifest, URL pluginFile) throws Exception {
        this.pluginManager = pluginManager;
        this.loader = loader;
        this.id = pluginManifest.getId();
        this.pluginManifest = pluginManifest;
        this.pluginFile = pluginFile;
        loader.addURLFile(this.id, pluginFile);
    }

    public void init() {
        if (this.pluginClass == null) {
            try {
                this.pluginClass = (Class) this.loader.loadClass(this.pluginManifest.getPluginClass());
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    public void loadPlugin() throws Exception {
        if (this.loaded) {
            return;
        }
        init();
        this.pluginManifest.getDependencies().stream().forEach(pm -> {
            try {
                this.pluginManager.getPluginRegistory(pm).loadPlugin();
            } catch (Exception ex) {
                logger.severe(ex.getMessage());
            }
        });
        this.plugin = this.pluginClass.getConstructor().newInstance();
        this.pluginManager.addPlugin(this.plugin, this);
        onLoad();
        this.loaded = true;
    }

    public boolean isClassType(Class type) {
        init();
        if (this.pluginClass == null || type == null) {
            return false;
        }
        return type.isAssignableFrom(this.pluginClass);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Class<Plugin> getPluginClass() {
        return this.pluginClass;
    }

    public String getId() {
        return this.id;
    }

    public PluginManifest getPluginManifest() {
        return this.pluginManifest;
    }

    public URL getPluginFile() {
        return this.pluginFile;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PluginRegistory other = (PluginRegistory) obj;
        return Objects.equals(this.id, other.id);
    }

    public void unloadPlugin() {
        this.pluginManager.firePluginUnload(this.plugin, this.pluginManifest);
        onShutdown();
        this.loader.unloadJarFiles(this.id);
    }

    private void onLoad() {
        for (Method m : this.plugin.getClass().getMethods()) {
            if (m.getAnnotation(OnLoad.class) != null) {
                try {
                    if (m.getParameterCount() == 0) {
                        m.invoke(this.plugin, new Object[0]);
                    } else if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == PluginManifest.class) {
                        m.invoke(this.plugin, new Object[]{this.pluginManager});
                    } else {
                        throw new IllegalArgumentException("unknown paramater type");
                    }
                } catch (IllegalAccessException | IllegalArgumentException | java.lang.reflect.InvocationTargetException ex) {
                    logger.severe(ex.getMessage());
                }
            }
            if (m.getAnnotation(TimerEvent.class) != null) {
                (new TimerEventRunner(this.plugin, m, m.<TimerEvent>getAnnotation(TimerEvent.class))).start();
            }
        }
        this.pluginManager.firePluginLoad(this.plugin, this.pluginManifest);
    }

    void onShutdown() {
        if (this.plugin == null) {
            return;
        }
        for (Method m : this.plugin.getClass().getMethods()) {
            if (m.getAnnotation(OnShutdown.class) != null)
        try {
                m.invoke(this.plugin, new Object[0]);
            } catch (IllegalAccessException | IllegalArgumentException | java.lang.reflect.InvocationTargetException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }
}

