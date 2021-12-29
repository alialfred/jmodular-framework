package com.alisoftclub.frameworks.modular;

import com.alisoftclub.frameworks.modular.plugin.BootApplication;
import com.alisoftclub.frameworks.modular.plugin.BootApplicationListener;
import com.alisoftclub.frameworks.modular.plugin.Plugin;
import com.alisoftclub.frameworks.modular.plugin.PluginListener;
import com.alisoftclub.frameworks.modular.plugin.PluginManifest;
import java.io.File;
import java.util.List;
import java.util.Properties;
import javax.swing.event.EventListenerList;

public class ApplicationLauncher {

    public static final String APPLICATION_PATH = "application.path";
    public static final String DEFAULT_APPLICATION_PATH = "applications";
    private static final ApplicationLauncher launcher = new ApplicationLauncher();
    private final PluginManager pmf = PluginManager.getInstance();
    private final EventListenerList listenerList = new EventListenerList();

    private Properties props;

    private String[] args;

    private void fireApplicationInit(BootApplication app, PluginManifest pluginManifest) {
        for (BootApplicationListener bal : (BootApplicationListener[]) this.listenerList.<BootApplicationListener>getListeners(BootApplicationListener.class)) {
            bal.onApplicationInitialized(app, pluginManifest);
        }
    }

    private void fireApplicationStart(BootApplication app, PluginManifest pluginManifest) {
        for (BootApplicationListener bal : (BootApplicationListener[]) this.listenerList.<BootApplicationListener>getListeners(BootApplicationListener.class)) {
            bal.onApplicationStart(app, pluginManifest);
        }
    }

    private void fireApplicationStop(BootApplication app, PluginManifest pluginManifest) {
        for (BootApplicationListener bal : (BootApplicationListener[]) this.listenerList.<BootApplicationListener>getListeners(BootApplicationListener.class)) {
            bal.onApplicationStop(app, pluginManifest);
        }
    }

    private void setParams(Properties props, String[] args) {
        this.props = (props == null) ? System.getProperties() : props;
        this.args = args;
    }

    private void launch() throws Exception {
        this.pmf.loadPluginsFrom(new File(this.props.getProperty(APPLICATION_PATH, DEFAULT_APPLICATION_PATH)));
        List<BootApplication> applications = this.pmf.getPlugins(BootApplication.class);
        applications.stream().forEach(app -> {
            app.initialize(this.args);

            fireApplicationInit(app, this.pmf.getPluginManifest((Plugin) app));
        });
        applications.stream().forEach(app -> {
            app.startApplication();
            fireApplicationStart(app, this.pmf.getPluginManifest((Plugin) app));
        });
    }

    private void launch(BootApplication app) {
        PluginManifest pluginManifest = this.pmf.getPluginManifest(app);
        System.out.println("Launching: " + pluginManifest);

        app.initialize(this.args);
        app.startApplication();
    }

    public void addPluginListener(BootApplicationListener listener) {
        this.listenerList.add(BootApplicationListener.class, listener);
        this.pmf.addPluginListener((PluginListener) listener);
    }

    public void removePluginListener(BootApplicationListener listener) {
        this.listenerList.remove(BootApplicationListener.class, listener);
        this.pmf.removePluginListener((PluginListener) listener);
    }

    public static void launch(String[] args) throws Exception {
        Properties props = System.getProperties();
        props.put(APPLICATION_PATH, DEFAULT_APPLICATION_PATH);
        launch(props, args, new BootApplicationListener[0]);
    }

    public static void launch(Properties props, String[] args, BootApplicationListener... listener) throws Exception {
        for (BootApplicationListener l : listener) {
            registerPluginListener(l);
        }
        launcher.setParams(props, args);
        launcher.launch();
    }

    public static void registerPluginListener(BootApplicationListener listener) {
        launcher.addPluginListener(listener);
    }
}
