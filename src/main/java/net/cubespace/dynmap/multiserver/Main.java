package net.cubespace.dynmap.multiserver;

import net.cubespace.dynmap.multiserver.Config.Dynmap;
import net.cubespace.dynmap.multiserver.GSON.ComponentDeserializer;
import net.cubespace.dynmap.multiserver.GSON.Components.PlayerMarkers;
import net.cubespace.dynmap.multiserver.GSON.Components.Spawn;
import net.cubespace.dynmap.multiserver.HTTP.HTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);

    private static ArrayList<DynmapServer> dynmapServers = new ArrayList<>();

    public static void main(String[] args) {
        //Init the Logger
        logger.info("Booting up Dynmap-MultiServer v0.1.0");
        logger.info("Running on Java Version: " + System.getProperty("java.version") + " " + System.getProperty("os.arch"));
        logger.info("Running on OS: " + System.getProperty("os.name"));

        //Init the Config
        logger.info("Getting the config...");
        net.cubespace.dynmap.multiserver.Config.Main config = new net.cubespace.dynmap.multiserver.Config.Main();
        config.init();

        //Normalize the WebPath
        File file = new File(System.getProperty("user.dir"), config.Webserver_webDir);

        if (!file.exists()) {
            file.mkdirs();

            logger.error("Please install a stripped Web Directory from Dynmap into the Webdir");
            System.exit(-1);
        }

        logger.info("Config holds Information for " + config.DynMap.size() + " DynMap(s)");

        //Register all Components
        ComponentDeserializer.addComponent(new Spawn());
        ComponentDeserializer.addComponent(new PlayerMarkers());

        //Load up the Dynmaps
        logger.info("Loading the Dynmaps");

        for (Dynmap dynmap : config.DynMap) {
            logger.info("Booting up Dynmap " + dynmap.Folder);

            try {
                DynmapServer dynmapServer = new DynmapServer(dynmap);
                dynmapServers.add(dynmapServer);
            } catch (DynmapInitException e) {
                logger.error("Could not boot up this Dynmap", e);
                System.exit(-1);
            }
        }

        //Start up the Webserver
        HTTPServer httpServer = new HTTPServer(config);
        httpServer.start();
    }

    public static ArrayList<DynmapServer> getDynmapServers() {
        return new ArrayList<>(dynmapServers);
    }
}