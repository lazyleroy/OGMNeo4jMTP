package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class is used to start the program. It also creates the Neo4JTemplates which is responsible for loading and storing nodes from and to
 * the database.
 */
@SpringBootApplication
public class Main {

    // Create SessionFactory. Pass the package name of the entity classes as the argument. Pass
    //Configuration as first Argument --> Both done in MyConfiguration

    private static Neo4jGraphController neo4jGraphController = new Neo4jGraphController();

    public long getDatabaseTime() {
        return databaseTime;
    }

    public void setDatabaseTime(long databaseTime) {
        this.databaseTime = databaseTime;
    }

    private long databaseTime = 0;



    public static void main(String[] args){


        SpringApplication.run(Main.class, args);
        if(!Files.exists(Paths.get(FileUploadController.ROOT))){
            try {
                Files.createDirectory(Paths.get(FileUploadController.ROOT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        neo4jGraphController.sendQuery("CREATE INDEX ON :Spot(spotID)");
        neo4jGraphController.sendQuery("CREATE INDEX ON :Spot(longitude)");
        neo4jGraphController.sendQuery("CREATE INDEX ON :Spot(latitude)");
        neo4jGraphController.sendQuery("CREATE INDEX ON :GPS_Plus(gpsPlusID)");
        neo4jGraphController.sendQuery("CREATE INDEX ON :Waypoint(waypointID)");



    }





}
