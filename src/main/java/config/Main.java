package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */

import entities.GPS_plus;
import entities.Spot;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.template.Neo4jTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class is used to start the program. It also creates the Neo4JTemplates which is responsible for loading and storing nodes from and to
 * the database.
 */
@SpringBootApplication
public class Main {

    // Create SessionFactory. Pass the package name of the entity classes as the argument. Pass
    //Configuration as first Argument --> Both done in MyConfiguration
    private static SessionFactory sessionFactory = new MyConfiguration().getSessionFactory();
    private static Session session = sessionFactory.openSession();
    private static Neo4jTemplate template = new Neo4jTemplate(session);
    private static DatabaseOperations db = new DatabaseOperations();
    private static Neo4jGraphController neo4jGraphController = new Neo4jGraphController();




    public Neo4jTemplate createNeo4JTemplate(){
        return new Neo4jTemplate(sessionFactory.openSession());
    }

    public static void main(String[] args){


        SpringApplication.run(Main.class, args);
        if(!Files.exists(Paths.get(FileUploadController.ROOT))){
            try {
                Files.createDirectory(Paths.get(FileUploadController.ROOT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Spot s1 = new Spot();
        s1.setSpotID("1");
        s1.setNeighbors(new ArrayList<>());
        Spot s2 = new Spot();
        s2.setSpotID("2");
        s2.setNeighbors(new ArrayList<>());
        Spot s3 = new Spot();
        s3.setSpotID("3");
        s3.setNeighbors(new ArrayList<>());
        Spot s4 = new Spot();
        s4.setSpotID("4");
        s4.setNeighbors(new ArrayList<>());
        Spot s5 = new Spot();
        s5.setSpotID("5");
        s5.setNeighbors(new ArrayList<>());
        Spot s6 = new Spot();
        s6.setSpotID("6");
        s6.setNeighbors(new ArrayList<>());
        Spot s7 = new Spot();
        s7.setSpotID("7");
        s7.setNeighbors(new ArrayList<>());
        Spot s8 = new Spot();
        s8.setSpotID("8");
        s8.setNeighbors(new ArrayList<>());

        s1.getNeighbors().add(s2);
        s1.getNeighbors().add(s3);
        s1.getNeighbors().add(s6);
        s1.getNeighbors().add(s7);
        s1.getNeighbors().add(s8);
        s2.getNeighbors().add(s4);
        s3.getNeighbors().add(s5);
        s4.getNeighbors().add(s5);

        neo4jGraphController.addSpot(s8);
        neo4jGraphController.addSpot(s7);
        neo4jGraphController.addSpot(s6);
        neo4jGraphController.addSpot(s5);
        neo4jGraphController.addSpot(s4);
        neo4jGraphController.addSpot(s3);
        neo4jGraphController.addSpot(s2);
        neo4jGraphController.addSpot(s1);

        s1.setLatitude(1.234f);
        s1.setLongitude(1.456f);
        s1.setIntersection(true);
        s1.setLongitudeSum(9999.9f);
        //neo4jGraphController.updateSpot(s1);

        Date date = new Date();
        GPS_plus gp1 = new GPS_plus();
        gp1.setSpot(s1);
        GPS_plus gp2 = new GPS_plus();
        gp2.setSpot(s1);
        GPS_plus gp3 = new GPS_plus();
        gp3.setSpot(s3);
        GPS_plus gp4 = new GPS_plus();
        gp4.setSpot(s3);
        GPS_plus gp5 = new GPS_plus();
        gp5.setSpot(s3);
        GPS_plus gp6 = new GPS_plus();
        gp6.setSpot(s5);
        GPS_plus gp7 = new GPS_plus();
        gp7.setSpot(s5);
        GPS_plus gp8 = new GPS_plus();
        gp8.setSpot(s5);
        GPS_plus gp9 = new GPS_plus();
        gp9.setSpot(s4);
        GPS_plus gp10 = new GPS_plus();
        gp10.setSpot(s4);
        GPS_plus gp11 = new GPS_plus();
        gp11.setSpot(s4);
        GPS_plus gp12 = new GPS_plus();
        gp12.setSpot(s2);

        gp1.setTime(date);
        gp2.setTime(date);
        gp3.setTime(date);
        gp4.setTime(date);
        gp5.setTime(date);
        gp6.setTime(date);
        gp7.setTime(date);
        gp8.setTime(date);
        gp9.setTime(date);
        gp10.setTime(date);
        gp11.setTime(date);
        gp12.setTime(date);



        ArrayList<GPS_plus> points = new ArrayList<>();

        points.add(gp1);
        points.add(gp2);
        points.add(gp3);
        points.add(gp4);
        points.add(gp5);
        points.add(gp6);
        points.add(gp7);
        points.add(gp8);
        points.add(gp9);
        points.add(gp10);
        points.add(gp11);
        points.add(gp12);

        //neo4jGraphController.addGPSPoints(points, "Felix");
        //neo4jGraphController.addNeighbour("2","5");


    }




}
