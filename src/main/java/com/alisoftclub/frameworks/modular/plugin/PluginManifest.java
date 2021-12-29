package com.alisoftclub.frameworks.modular.plugin;

import com.alisoftclub.frameworks.modular.license.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PluginManifest
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String description;
    private String vendor;
    private String url;
    private String pluginClass;
    private Version version;
    private boolean required;
    private List<String> dependencies = new ArrayList<>(0);

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return this.vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Version getVersion() {
        return this.version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPluginClass() {
        return this.pluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<String> getDependencies() {
        return this.dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return this.name + " [Version: " + this.version + ']';
    }
}
