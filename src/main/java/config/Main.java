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


        neo4jGraphController.sendQuery("CREATE INDEX ON :Spot(spotID)");
        neo4jGraphController.sendQuery("CREATE INDEX ON :Spot(longitude)");
        neo4jGraphController.sendQuery("CREATE INDEX ON :Spot(latitude)");
        neo4jGraphController.sendQuery("CREATE INDEX ON :GPS_Plus(gpsPlusID)");
        neo4jGraphController.sendQuery("CREATE INDEX ON :Waypoint(waypointID)");
        //neo4jGraphController.sendQuery("CALL spatial.addPointLayer(\'SpotIndex\')");

        long start_time1 = new Date().getTime();
/*
       for(int i = 0; i < 100; i++){
            neo4jGraphController.sendQuery("CREATE (GPS_Plus0:GPS_Plus)");
            System.out.println(i);
        }
        long stop_time = new Date().getTime();
        double time = (double)(stop_time-start_time1);
        System.out.println("Durchschnittliche Zeit pro kleinem Knoten: "+time/1000);

        start_time1 = new Date().getTime();

        for(int r = 1; r < 101; r++) {
            for (int i = 0; i < 100; i++) {
                neo4jGraphController.sendQuery("CREATE (GPS_Plus" + i + ":GPS_Plus" + "{date:\'" + new Date().getTime() + "\', latitude:1234.45"+i*10+", longitude:5678.98, head:\'zahl\', gpsPlusID:\'" + i + "\', " +
                        " timeDiffToNextPoint:13"+i/r+", distanceToNextPoint:\'blah\', dataID:99"+r+"})");
            }
            System.out.println(r);
        }

        stop_time = new Date().getTime();
        time = (double)(stop_time-start_time1);
        System.out.println("Durchschnittliche Zeit pro großem Knoten: "+time/100);
*/

    }





}
