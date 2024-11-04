package ReFreSH.JMobileSuit.ObjectModel;

import ReFreSH.JMobileSuit.BuildInCommandServer;
import ReFreSH.JMobileSuit.CommonSuitConfiguration;
import ReFreSH.JMobileSuit.IO.ColorSetting;
import ReFreSH.JMobileSuit.IO.CommonPromptServer;
import ReFreSH.JMobileSuit.IO.IOServer;
import ReFreSH.JMobileSuit.IO.PromptServer;
import ReFreSH.JMobileSuit.SuitConfiguration;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public class SuitConfigurator {
    public Class<? extends BuildInCommandServer> BuildInCommandServerType = BuildInCommandServer.class;
    public ColorSetting ColorSetting = ReFreSH.JMobileSuit.IO.ColorSetting.getInstance();
    public Class<? extends PromptServer> PromptServerType = CommonPromptServer.class;
    public Logger logger = null;
    public Class<? extends IOServer> IOServerType = IOServer.class;
    public Class<? extends SuitConfiguration> ConfigurationType = CommonSuitConfiguration.class;

    SuitConfigurator() {

    }

    private static boolean hasInterface(Class<?> c, Class<?> i) {
        while (!c.equals(Object.class)) {
            if (Arrays.asList(c.getInterfaces()).contains(i)) return true;
            c = c.getSuperclass();
        }
        return false;
    }

    private static boolean hasBaseClass(Class<?> c, Class<?> b) {
        if (b.equals(Object.class)) return true;
        while (!c.equals(Object.class)) {
            if (c.equals(b)) return true;
            c = c.getSuperclass();
        }
        return false;
    }

    public static SuitConfigurator of(Class<?> s) {
        SuitConfigurator r = new SuitConfigurator();
        if (hasBaseClass(s, IOServer.class))
            r.IOServerType = (Class<? extends IOServer>) s;
        else if (hasInterface(s, PromptServer.class))
            r.PromptServerType = (Class<? extends PromptServer>) s;
        else if (hasBaseClass(s, BuildInCommandServer.class))
            r.BuildInCommandServerType = (Class<? extends BuildInCommandServer>) s;
        else if (hasInterface(s, SuitConfiguration.class))
            r.ConfigurationType = (Class<? extends SuitConfiguration>) s;

        return r;
    }

    public static SuitConfigurator of(Logger s) {
        SuitConfigurator r = new SuitConfigurator();
        r.logger = s;
        return r;
    }

    public static SuitConfigurator ofDefault() {
        return new SuitConfigurator();
    }

    public static SuitConfigurator of(ColorSetting s) {
        SuitConfigurator r = new SuitConfigurator();
        r.ColorSetting = s;
        return r;
    }

    public SuitConfigurator use(Class<?> s) {
        if (hasBaseClass(s, IOServer.class))
            this.IOServerType = (Class<? extends IOServer>) s;
        else if (hasInterface(s, PromptServer.class))
            this.PromptServerType = (Class<? extends PromptServer>) s;
        else if (hasBaseClass(s, BuildInCommandServer.class))
            this.BuildInCommandServerType = (Class<? extends BuildInCommandServer>) s;
        else if (hasInterface(s, SuitConfiguration.class))
            this.ConfigurationType = (Class<? extends SuitConfiguration>) s;


        return this;
    }

    public SuitConfigurator use(Logger s) {
        this.logger = s;
        return this;
    }

    public SuitConfigurator use(ColorSetting s) {
        this.ColorSetting = s;
        return this;
    }

    public SuitConfiguration getConfiguration()// throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        try {
            PromptServer promptServer = PromptServerType.getConstructor().newInstance();
            IOServer ioServer = IOServerType.getConstructor(PromptServer.class, Logger.class, ColorSetting.class)
                    .newInstance(promptServer, logger, ColorSetting);
            promptServer.setIO(ioServer);
            return ConfigurationType.getConstructor(Class.class, IOServer.class, PromptServer.class, ColorSetting.class, Logger.class).newInstance(
                    BuildInCommandServerType,
                    ioServer,
                    promptServer,
                    ColorSetting,
                    logger
            );
        } catch (Exception e) {
            return null;
        }

    }
}
