package com.alisoftclub.frameworks.modular;

import java.io.File;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginLoader
        extends URLClassLoader {

    private final Map<String, JarURLConnection> cachedJarFiles = new HashMap<>();

    public PluginLoader() {
        super(new URL[0], findParentClassLoader());
    }

    public PluginLoader(URL url) {
        super(new URL[]{url}, findParentClassLoader());
    }

    public PluginLoader(URL[] url) {
        super(url, findParentClassLoader());
    }

    public Map<String, JarURLConnection> getCachedJarFiles() {
        return Collections.unmodifiableMap(this.cachedJarFiles);
    }

    public void loadPlugin(String id, File module) {
        if (module != null && module.isFile()) {
            String jarFileUri = module.toURI().toString() + "!/";
            loadPlugin(id, jarFileUri);
        }
    }

    public void loadPlugin(String id, String moduleURI) {
        try {
            addURLFile(id, new URL("jar", "", -1, moduleURI));
        } catch (MalformedURLException malformedURLException) {
        }
    }

    public void addURLFile(String id, URL file) {
        try {
            if (this.cachedJarFiles.containsKey(id)) {
                unloadJarFiles(id);
            }

            URLConnection uc = file.openConnection();
            if (uc instanceof JarURLConnection) {
                uc.setUseCaches(true);
                this.cachedJarFiles.put(id, (JarURLConnection) uc);
            }
        } catch (Exception exception) {
        }

        addURL(file);
    }

    public void unloadJarFiles(String id) {
        try {
            JarURLConnection url = this.cachedJarFiles.remove(id);
            if (url == null) {
                return;
            }

            url.getJarFile().close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void unloadJarFiles() {
        this.cachedJarFiles.values().stream().forEach(url -> {

            try {
                url.getJarFile().close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    private static ClassLoader findParentClassLoader() {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = PluginLoader.class.getClassLoader();
        }
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        return parent;
    }
}
