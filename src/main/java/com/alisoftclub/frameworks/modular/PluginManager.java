package com.alisoftclub.frameworks.modular;

import com.alisoftclub.frameworks.modular.plugin.Plugin;
import com.alisoftclub.frameworks.modular.plugin.PluginListener;
import com.alisoftclub.frameworks.modular.plugin.PluginManifest;
import com.alisoftclub.frameworks.modular.plugin.impl.PluginVerifier;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.reflections.Reflections;

public class PluginManager {

    private static PluginManager instance;

    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    private static final Logger logger = Logger.getLogger(PluginManager.class.getCanonicalName());
    private final Map<String, PluginRegistory> pluginsMap = new HashMap<>();
    private final Map<String, PluginRegistory> plugins = new HashMap<>();
    private final Map<String, Plugin> pluginsByName = new HashMap<>();
    private final Map<String, PluginManifest> pluginsManifest = new HashMap<>();

    private final Map<Plugin, PluginRegistory> pluginsByPlugin = new HashMap<>();
    private final PluginLoader loader;
    private final EventListenerList listenerList = new EventListenerList();

    private PluginManager() {
        this.loader = new PluginLoader();

        Runtime.getRuntime().addShutdownHook(new Thread("shutdown-thread") {
            @Override
            public void run() {
                PluginManager.this.unloadPlugins();
            }
        });
    }

    void firePluginFound(PluginManifest pluginManifest) {
        for (PluginListener pl : (PluginListener[]) this.listenerList.<PluginListener>getListeners(PluginListener.class)) {
            pl.onPluginFound(pluginManifest);
        }
    }

    void firePluginLoad(Plugin plugin, PluginManifest pluginManifest) {
        for (PluginListener pl : (PluginListener[]) this.listenerList.<PluginListener>getListeners(PluginListener.class)) {
            pl.onPluginLoad(plugin, pluginManifest);
        }
    }

    void firePluginException(PluginManifest pluginManifest, Exception exception) {
        for (PluginListener pl : (PluginListener[]) this.listenerList.<PluginListener>getListeners(PluginListener.class)) {
            pl.onPluginException(pluginManifest, exception);
        }
    }

    void firePluginUnload(Plugin plugin, PluginManifest pluginManifest) {
        for (PluginListener pl : (PluginListener[]) this.listenerList.<PluginListener>getListeners(PluginListener.class)) {
            pl.onPluginUnload(plugin, pluginManifest);
        }
    }

    public void addPluginListener(PluginListener listener) {
        this.listenerList.add(PluginListener.class, listener);
    }

    public void removePluginListener(PluginListener listener) {
        this.listenerList.remove(PluginListener.class, listener);
    }

    public <T> Set<Class<? extends T>> findPluginsFor(Class<T> cls) {
        Reflections reflections = new Reflections(new Object[]{cls});

        Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(cls);
        return subTypes;
    }

    public Set<Class<?>> findAnnotatedPluginsFor(String pkg, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(new Object[0]);

        Set<Class<?>> subTypes = reflections.getTypesAnnotatedWith(annotation, true);
        return subTypes;
    }

    public void loadLibrary(String base, List<String> libs) throws Exception {
        List<URL> list = new ArrayList<>(0);
        for (String url : libs) {
            list.add(new URL(String.format("jar:%s%s!/", new Object[]{base, url})));
        }
        loadLibrary(list.<URL>toArray(new URL[0]));
    }

    public void loadLibrary(URL... urls) throws Exception {
        for (URL url : urls) {
            if (url.toExternalForm().startsWith("jar")) {

                this.loader.addURLFile(url.getFile(), url);
            }
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    public void addClassPath(URL u) throws IOException {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;

        try {
            u.openConnection();
            Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{u});
        } catch (Throwable ex) {
            logger.severe(ex.getMessage());
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    public void loadPlugins(String... urls) throws Exception {
        loadPlugins("", urls);
    }

    public void loadPlugins(String base, List<String> urls) throws Exception {
        loadPlugins(base, urls.<String>toArray(new String[0]));
    }

    public void loadPlugins(String base, String... urls) throws Exception {
        List<URL> list = new ArrayList<>(0);
        for (String url : urls) {
            list.add(new URL(String.format("jar:%s%s!/", new Object[]{base, url})));
        }
        loadPlugins(list.<URL>toArray(new URL[0]));
    }

    public void loadPlugins(URL... urls) throws Exception {
        for (URL url : urls) {
            if (url.toExternalForm().startsWith("jar")) {
                loadPrePlugin(url);
            }
        }
        this.pluginsMap.values().forEach(registry -> {
            if (resolveDependencies(registry.getPluginManifest())) {
                addPlugin(registry.getPlugin(), registry);
            }
        });
        this.plugins.values().forEach(pr -> this.pluginsMap.remove(pr.getId()));
        resolvePlugins();
    }

    public void loadPluginsFrom(File dir) throws Exception {
        loadPluginsFrom(dir, null);
    }

    public void loadPluginsFrom(File dir, List<File> restrictions) throws Exception {
        try {
            if (dir.isFile()) {
                loadPrePlugin(dir, restrictions);
            } else if (dir.isDirectory()) {
                for (File f : dir.listFiles()) {
                    if (f.isFile()) {
                        loadPrePlugin(f, restrictions);
                    } else if (f.isDirectory()) {
                        loadPluginsFrom(f, restrictions);
                    }
                }
            }
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        }
        this.pluginsMap.values().forEach(registry -> {
            if (resolveDependencies(registry.getPluginManifest())) {
                addPlugin(registry.getPlugin(), registry);
            }
        });
        this.plugins.values().forEach(pr -> this.pluginsMap.remove(pr.getId()));
        resolvePlugins();
    }

    public void loadPrePlugin(File file) throws Exception {
        loadPrePlugin(file, null);
    }

    public void loadPrePlugin(File file, List<File> restrictions) throws Exception {
        if (restrictions != null && !restrictions.isEmpty()
                && !restrictions.stream().anyMatch(f -> f.equals(file))) {
            return;
        }

        if (!file.getName().endsWith("jar") && !file.getName().endsWith("module")) {
            return;
        }
        String jarFileUri = file.toURI().toString() + "!/";
        loadPrePlugin(new URL("jar", "", -1, jarFileUri));
    }

    public void loadPrePlugin(URL file) throws Exception {
        try {
            PluginVerifier verifier = new PluginVerifier(file);
            if (!verifier.isPlugin()) {
                loadLibrary(new URL[]{file});
                verifier.close();
                return;
            }
            PluginManifest manifest = verifier.getPluginManifest();
            try {
//                System.out.println(manifest);
                if (this.pluginsMap.containsKey(manifest.getId())) {
                    logger.warning(String.format("Ignoring duplicate plugin [id: %s, name: %s]", manifest.getId(), manifest.getName()));
                    verifier.close();
                    return;
                }
                PluginRegistory registory = new PluginRegistory(this, this.loader, manifest, file);
                this.pluginsMap.put(registory.getId(), registory);
                this.pluginsManifest.put(manifest.getId(), manifest);
            } catch (Exception ex) {
                logger.severe(ex.getMessage());
            }
            verifier.close();
        } catch (Exception exc) {
            logger.severe(exc.getMessage());
            throw exc;
        }
    }

    private void resolvePlugins() {
        List<String> resolved = new ArrayList<>(0);
        this.pluginsMap.values().stream().forEach(pr -> {
            if (this.plugins.containsValue(pr)) {
                resolved.add(pr.getId());
            } else if (resolveDependencies(pr.getPluginManifest())) {
                addPlugin(pr.getPlugin(), pr);
                resolved.add(pr.getId());
            }
        });
        resolved.stream().forEach(id -> this.pluginsMap.remove(id));
    }

    private boolean resolveDependencies(PluginManifest pm) {
        if (pm == null) {
            return false;
        }
        boolean b = (this.plugins.containsKey(pm.getId()) || this.pluginsMap.containsKey(pm.getId()) || !pm.isRequired());
        if (!pm.getDependencies().isEmpty()) {
            for (String p : pm.getDependencies()) {
                b = (b && resolveDependencies(getPluginManifest(p)));
            }
        }
        return b;
    }

    public void loadPlugins() {
        resolvePlugins();
        this.plugins.values().stream().forEach(pr -> {
            try {
                pr.loadPlugin();
            } catch (Exception ex) {
                logger.severe(ex.getMessage());
                firePluginException(pr.getPluginManifest(), ex);
            }
        });
    }

    public int getPluginCount() {
        resolvePlugins();
        return this.plugins.size();
    }

    public <P extends Plugin> List<P> getPlugins(Class<P> type) {
        List<P> list = new ArrayList<>(0);
        this.plugins.values().stream().forEach(pr -> {
            if (pr.isClassType(type)) {
                try {
                    pr.loadPlugin();
                    list.add(type.cast(pr.getPlugin()));
                } catch (Exception ex) {
                    logger.severe(ex.getMessage());
                    firePluginException(pr.getPluginManifest(), ex);
                }
            } else {
                System.out.println(type + "!=" + pr.getPluginClass());
            }
        });
        return list;
    }

    public void addPlugin(Plugin plugin, PluginRegistory registory) {
        this.pluginsByPlugin.put(plugin, registory);
        this.plugins.put(registory.getId(), registory);
        this.pluginsByName.put(registory.getPluginManifest().getName(), plugin);
        firePluginFound(registory.getPluginManifest());
    }

    public void removePlugin(PluginRegistory registory) {
        this.plugins.remove(registory.getId());
        this.pluginsByPlugin.remove(registory.getPlugin());
        this.pluginsMap.remove(registory.getId());
    }

    public void unloadPlugins() {
        List<PluginRegistory> list = new ArrayList<>(this.plugins.values());
        list.stream().forEach(pr -> pr.unloadPlugin());
    }

    public void unloadPlugin(String id) {
        ((PluginRegistory) this.plugins.get(id)).onShutdown();
    }

    public void unloadPlugin(Plugin plugin) {
        ((PluginRegistory) this.pluginsByPlugin.get(plugin)).onShutdown();
    }

    public PluginManifest getPluginManifest(String id) {
        return this.pluginsManifest.get(id);
    }

    public PluginManifest getPluginManifest(Plugin plugin) {
        return ((PluginRegistory) this.pluginsByPlugin.get(plugin)).getPluginManifest();
    }

    public Plugin getPlugin(String id) {
        return ((PluginRegistory) this.plugins.get(id)).getPlugin();
    }

    public Plugin getPluginByName(String name) {
        return this.pluginsByName.get(name);
    }

    public boolean isPluginExists(String id) {
        return (this.plugins.get(id) != null);
    }

    public boolean isPluginByNameExists(String name) {
        return (getPluginByName(name) != null);
    }

    PluginRegistory getPluginRegistory(String id) {
        return this.plugins.get(id);
    }

    public Class<?> loadClass(String className) throws Exception {
        return this.loader.loadClass(className);
    }
}


/* Location:              E:\java\reverse-engineered\reconciler\lib\modular-framework-1.0.0.1.jar!\com\alisoftclub\amf\PluginManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
