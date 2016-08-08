package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */

import entities.GeoLocation;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.template.Neo4jTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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
        if(!Files.exists(Paths.get(FileUploadController.ROOT))){
            try {
                Files.createDirectory(Paths.get(FileUploadController.ROOT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        //DatabaseOperations.retrieveAllGoodybags("ltoRqohjno51v3CKbJQaSBOhVUt42n68m6lzMJUHT0fMeXV6qY6Pgr2QLqKmP9HxFVKQctxJSfcOKG0bWaFL45izwFYiVV8ERB5SagZHdEkcyaR18uzM7TUYEhrTN2qRt5MtF8FM5eT8NOR6ILx7ep7DsSFvHAAEgPsXR7tccNercQgGG4R4AVmDOko7cTb5DR1WqlApOOwzxpikRHfUOj7VBgs94tcUpmBMUba6q3olDzE9qltsFe7RH3JUW6H");

    }



}
