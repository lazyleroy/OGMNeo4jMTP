package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */

import entities.Spot;
import entities.Waypoint;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.template.Neo4jTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public static void main(String[] args){




        SpringApplication.run(Main.class, args);
        if(!Files.exists(Paths.get(FileUploadController.ROOT))){
            try {
                Files.createDirectory(Paths.get(FileUploadController.ROOT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*ArrayList<Long> x = new ArrayList<Long>();
        x.add(1712652937495941L);
        x.add(919967042209181L);
        x.add(2635972791352644L);
        DatabaseOperations db = new DatabaseOperations();
        //db.sendGoodybagToUsers(x, "19905L12");*/

        /*ArrayList<Spot> arrayList = new ArrayList<>();
        ArrayList<Waypoint> waypointArrayList = new ArrayList<>();

        arrayList.add(new Spot(1, new HashSet<>()));
        arrayList.add(new Spot(2, new HashSet<>()));
        arrayList.add(new Spot(3, new HashSet<>()));
        arrayList.add(new Spot(4, new HashSet<>()));
        arrayList.add(new Spot(5, new HashSet<>()));
        arrayList.add(new Spot(6, new HashSet<>()));
        arrayList.add(new Spot(7, new HashSet<>()));
        arrayList.add(new Spot(8, new HashSet<>()));
        arrayList.add(new Spot(9, new HashSet<>()));
        arrayList.add(new Spot(10, new HashSet<>()));
        arrayList.add(new Spot(11, new HashSet<>()));
        arrayList.add(new Spot(12, new HashSet<>()));
        arrayList.add(new Spot(13, new HashSet<>()));
        arrayList.add(new Spot(14, new HashSet<>()));
        arrayList.add(new Spot(15, new HashSet<>()));
        arrayList.add(new Spot(16, new HashSet<>()));
        arrayList.add(new Spot(17, new HashSet<>()));
        arrayList.add(new Spot(18, new HashSet<>()));
        arrayList.add(new Spot(19, new HashSet<>()));
        arrayList.add(new Spot(20, new HashSet<>()));
        arrayList.add(new Spot(21, new HashSet<>()));
        arrayList.add(new Spot(22, new HashSet<>()));
        /*arrayList.add(new Spot(12, new HashSet<>()));
        arrayList.add(new Spot(13, new HashSet<>()));
        arrayList.add(new Spot(14, new HashSet<>()));
        arrayList.get(0).getConnectedSpots().add(arrayList.get(1));
        arrayList.get(0).getConnectedSpots().add(arrayList.get(2));
        arrayList.get(1).getConnectedSpots().add(arrayList.get(11));
        arrayList.get(2).getConnectedSpots().add(arrayList.get(3));
        arrayList.get(3).getConnectedSpots().add(arrayList.get(4));
        arrayList.get(4).getConnectedSpots().add(arrayList.get(5));
        arrayList.get(5).getConnectedSpots().add(arrayList.get(6));
        arrayList.get(5).getConnectedSpots().add(arrayList.get(7));
        arrayList.get(7).getConnectedSpots().add(arrayList.get(8));
        arrayList.get(8).getConnectedSpots().add(arrayList.get(9));
        arrayList.get(9).getConnectedSpots().add(arrayList.get(11));
        arrayList.get(10).getConnectedSpots().add(arrayList.get(11));
        /*arrayList.get(10).getConnectedSpots().add(arrayList.get(11));
        arrayList.get(11).getConnectedSpots().add(arrayList.get(12));
        arrayList.get(12).getConnectedSpots().add(arrayList.get(13));
        arrayList.get(13).getConnectedSpots().add(arrayList.get(14));
        arrayList.get(14).getConnectedSpots().add(arrayList.get(15));
        arrayList.get(14).getConnectedSpots().add(arrayList.get(6));
        arrayList.get(14).getConnectedSpots().add(arrayList.get(4));
        arrayList.get(14).getConnectedSpots().add(arrayList.get(9));
        arrayList.get(15).getConnectedSpots().add(arrayList.get(16));
        arrayList.get(16).getConnectedSpots().add(arrayList.get(17));
        arrayList.get(17).getConnectedSpots().add(arrayList.get(18));
        arrayList.get(18).getConnectedSpots().add(arrayList.get(19));
        arrayList.get(19).getConnectedSpots().add(arrayList.get(20));
        arrayList.get(20).getConnectedSpots().add(arrayList.get(21));
        waypointArrayList.add(new Waypoint(arrayList.get(0), 1L));
        waypointArrayList.add(new Waypoint(arrayList.get(1), 2L));
        waypointArrayList.add(new Waypoint(arrayList.get(2), 3L));
        waypointArrayList.add(new Waypoint(arrayList.get(3), 4L));
        waypointArrayList.add(new Waypoint(arrayList.get(4), 5L));
        waypointArrayList.add(new Waypoint(arrayList.get(5), 6L));*/


        //db.test(arrayList,waypointArrayList,"MJeXUrf9cv63X5PBWetsvcP98GiTQ6UAwQRgpgdkrMmfNNMdKibWG7NYfCE0BWoWZKovy8");
        //db.register(new User("felix","abc@gmx.de","1234"),"abc@gmx.de",null);
        ArrayList<String> userIDs = new ArrayList<>();
        userIDs.add("3015889670584973");
        userIDs.add("2609054028101385");
        userIDs.add("3298671247787562");
        userIDs.add("9455440145628192");
        //DatabaseOperations.matching(userIDs, 4726992368837059L);




        //DatabaseOperations.retrieveAllGoodybags("ltoRqohjno51v3CKbJQaSBOhVUt42n68m6lzMJUHT0fMeXV6qY6Pgr2QLqKmP9HxFVKQctxJSfcOKG0bWaFL45izwFYiVV8ERB5SagZHdEkcyaR18uzM7TUYEhrTN2qRt5MtF8FM5eT8NOR6ILx7ep7DsSFvHAAEgPsXR7tccNercQgGG4R4AVmDOko7cTb5DR1WqlApOOwzxpikRHfUOj7VBgs94tcUpmBMUba6q3olDzE9qltsFe7RH3JUW6H");

    }



}
