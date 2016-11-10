package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.template.Neo4jTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        db.saveDataEntity("Felix",1,2,3,4,2,100);
        db.saveDataEntity("Felix",1,3,4,5,2,120);
        db.saveDataEntity("Felix",1,4,5,6,3,140);
        db.saveDataEntity("Felix",1,5,6,7,3,160);
        db.saveDataEntity("Felix",1,6,7,8,3,180);
        db.saveDataEntity("Felix",1,7,8,9,3,200);
        db.saveDataEntity("Felix",1,8,9,1,2,200);

    }




}
