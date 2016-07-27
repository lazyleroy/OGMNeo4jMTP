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
        SpringApplication.run(Main.class, args);



    }


}
