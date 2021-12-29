package com.alisoftclub.frameworks.modular.plugin.impl;

import com.alisoftclub.frameworks.modular.license.Version;
import com.alisoftclub.frameworks.modular.plugin.PluginManifest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.jar.JarFile;

public class PluginVerifier {

    private static final String[] MANIFEST = new String[]{"META-INF/plugin.properties" /* "META-INF/plugin.json", "META-INF/plugin.manifest"*/};
    private String manifest;
    private JarFile jarFile;

    public PluginVerifier(URL file) throws IOException {
        this(((JarURLConnection) file.openConnection()).getJarFile());
    }

    public PluginVerifier(String file) throws IOException {
        this(new JarFile(file));
    }

    public PluginVerifier(JarFile file) throws IOException {
        this.jarFile = file;
        for (String m : MANIFEST) {
            if (this.jarFile.getJarEntry(m) != null) {
                this.manifest = m;
                break;
            }
        }
    }

    public PluginVerifier(File file) throws IOException {
        this(file.getAbsolutePath());
    }

    public boolean isPlugin() {
        return (this.manifest != null);
    }

    public boolean hasEntry(String name) {
        return (this.jarFile.getEntry(name) != null);
    }

    public PluginManifest getPluginManifest() {
        try {
            Properties props = new Properties();
            props.load(new InputStreamReader(getEntry(this.manifest)));
            PluginManifest pm = new PluginManifest();
            pm.setId(props.getProperty("id"));
            pm.setDescription(props.getProperty("description"));
            pm.setName(props.getProperty("name"));
            pm.setPluginClass(props.getProperty("pluginClass"));
            pm.setRequired(Boolean.valueOf(props.getProperty("pluginClass", "false")));
            pm.setUrl(props.getProperty("url"));
            pm.setVendor(props.getProperty("vendor"));
            pm.setDependencies(Arrays.asList(props.getProperty("dependencies", "").split(",")));
            pm.setVersion(
                    new Version(
                            Integer.valueOf(props.getProperty("version.major", "0")),
                            Integer.valueOf(props.getProperty("version.minor", "0")),
                            Integer.valueOf(props.getProperty("version.build", "0")),
                            props.getProperty("version.description", "")
                    ));

            return pm;
        } catch (IOException ex) {
            return null;
        }
    }

    public InputStream getEntry(String name) throws IOException {
        return this.jarFile.getInputStream(this.jarFile.getJarEntry(name));
    }

    public void close() throws IOException {
        this.jarFile.close();
    }

}
