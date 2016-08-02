package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import entities.GeoLocation;
import entities.Goodybag;
import entities.User;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
