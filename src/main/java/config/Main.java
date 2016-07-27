package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */
import java.util.ArrayList;
import java.util.Random;

import entities.*;
import javafx.application.Application;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.template.Neo4jTemplate;


@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages={"jsonTests"})

public class Main {


    // Create SessionFactory. Pass the package name of the entity classes as the argument. Pass
    //Configuration as first Argument --> Both done in MyConfiguration
    private static SessionFactory sessionFactory = new MyConfiguration().getSessionFactory();
    private static Session session = sessionFactory.openSession();
    private static Neo4jTemplate template = new Neo4jTemplate(session);


    public Neo4jTemplate createNeo4JTemplate(){
        return new Neo4jTemplate(sessionFactory.openSession());
    }

    public static void main(String[] args) {
        DatabaseOperations db = new DatabaseOperations();


      /*  // Create a User

        User u = new User("Leroy", "leroy@gmx.de", "testPasswort");

        // Create a few GeoLocations

        GeoLocation g0 = new GeoLocation(123.42,136.2);
        GeoLocation g1 = new GeoLocation(443.42,936.2);
        GeoLocation g2 = new GeoLocation(3.42,1036.2);
        GeoLocation g3 = new GeoLocation(123124,123124);
        GeoLocation g4 = new GeoLocation(3574567,13462);
        GeoLocation g5 = new GeoLocation(121111,22222);


        //Create a Route
        ArrayList<GeoLocation> gl0 = new ArrayList<GeoLocation>();
        ArrayList<GeoLocation> gl1 = new ArrayList<GeoLocation>();
        ArrayList<GeoLocation> gl2 = new ArrayList<GeoLocation>();

        gl0.add(g0);
        gl0.add(g1);
        gl0.add(g2);
        gl1.add(g1);
        gl1.add(g2);
        gl2.add(g3);
        gl2.add(g4);
        gl2.add(g5);
        Route r0 = new Route(gl0);
        Route r1 = new Route(gl1);
        Route r2 = new Route(gl2);
        u.getRoutes().add(r0);
        u.getRoutes().add(r1);


       // session.save(u);
*/
/*
        System.out.println(db.loadUserName("Leroy"));
        System.out.println(db.loadEmailAddress("Leroy"));
        System.out.println(db.loadIsTrackingActivated("Leroy"));
        System.out.println(db.loadSalt("Leroy"));
        System.out.println(db.loadPassWord("Leroy"));
        System.out.println(db.loadRating("Leroy"));*/



        //db.saveEmailAddress("Leroy","12345");
        //System.out.println(db.emailLogin("12345","testPasswort"));
        SpringApplication.run(Main.class, args);



    }


}
