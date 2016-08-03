package config;

import entities.*;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;
import org.neo4j.ogm.exception.NotFoundException;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.web.multipart.MultipartFile;
import requestAnswers.RegisterAnswer;
import requestAnswers.SimpleAnswer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import static config.FileUploadController.ROOT;

/**
 * Created by Felix on 15.07.2016.
 */
public class DatabaseOperations {

    private static Main main = new Main();

    private static String[] mimeTypes = {"jpg", "jpeg", "bmp", "png", "gif", "svg"};
    public static final int CLIENTID = 49911975;
    public static final String CLIENTSECRET = "ab02ndls82md9ak";


    public static RegisterAnswer register(User u, String emailAddress, String firebaseToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        try {
            User t = template.loadByProperty(User.class, "emailAddress", emailAddress);
            return new RegisterAnswer(false, "Emailadress already exists");
        } catch (NotFoundException nfe) {
            template.purgeSession();
            template.clear();

            try{
                while(true){
                    User t = template.loadByProperty(User.class,"userID",u.getUserID());
                    u.changeID();
                    continue;
                }
            }catch(NotFoundException e){
                if(firebaseToken != null){
                    FirebaseToken fT = new FirebaseToken(firebaseToken, u);
                    template.save(fT);
                }
                UserSession uS = new UserSession(u);
                Cookie c = new Cookie(u);
                template.save(uS);
                template.save(c);
                String accessToken = uS.getAccessToken();
                String refreshToken = c.getRefreshToken();
                return new RegisterAnswer(true, accessToken, 86400000L, refreshToken, 15768000000L);
            }
        }
    }

    public static SimpleAnswer checkAccessToken(String accesstoken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        long timestamp = new Date().getTime();
        try {
            UserSession u = template.loadByProperty(UserSession.class, "accessToken", accesstoken);
            if (u.getExpiresAt() >= timestamp) {
                return new SimpleAnswer(true);
            } else {
                template.delete(u);
                return new SimpleAnswer(false, "Invalid accessToken, refreshTokenLogin required");
            }
        } catch (NotFoundException nfe) {
            return new SimpleAnswer(false, "Invalid accessToken, refreshTokenLogin required");
        }
    }

    public static RegisterAnswer emailLogin(String email, String password, String firebaseToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();

        try {
            User u = template.loadByProperty(User.class, "emailAddress", email);
            String hash = u.createMD5(password, u.getSalt());
            if (hash.equals(u.getPassword())) {
                if(firebaseToken != null){
                    FirebaseToken fT = new FirebaseToken(firebaseToken, u);
                    template.save(fT);
                }
                UserSession uS = new UserSession(u);
                Cookie c = new Cookie(u);
                template.save(uS);
                template.save(c);
                return new RegisterAnswer(true, uS.getAccessToken(), 86400000L, c.getRefreshToken(), 15768000000L);
            } else {
                return new RegisterAnswer(false, "Invalid password");
            }
        } catch (NotFoundException nfe) {
            return new RegisterAnswer(false, "Email does not exist.");
        }
    }

    public static RegisterAnswer refreshTokenLogin(String refreshToken, int clientID, String clientSecret) {
        if(clientID!= CLIENTID || clientSecret != CLIENTSECRET){
            return new RegisterAnswer(false, "Client - Credentials wrong.");
        }
        Neo4jTemplate template = main.createNeo4JTemplate();
        long timestamp = new Date().getTime();
        try {
            Cookie c = template.loadByProperty(Cookie.class, "refreshToken", refreshToken);
            if (c.getExpiresAt() >= timestamp) {
                UserSession uS = new UserSession(c.getUser());
                template.save(uS);
                return new RegisterAnswer(true, uS.getAccessToken(), 86400000L, c.getRefreshToken(), 15768000000L);
            } else {
                template.delete(c);
                return new RegisterAnswer(false, "Invalid refreshToken, emailLogin required");
            }
        } catch (NotFoundException nfe) {
            return new RegisterAnswer(false, "Invalid refreshToken, emailLogin required");
        }
    }

    public static SimpleAnswer updateProfile(String userName, String occupation, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            try {
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                User u = uS.getUser();
                u.setUserName(userName);
                u.setOccupation(occupation);
                template.save(u);
                return new SimpleAnswer(true, "values updated: " + userName + " " + occupation);
            } catch (NotFoundException nfe) {
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
            }
        } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");

    }

    public static SimpleAnswer uploadGoodybag(String title, String status, String description,
                                       double tip, long deliverTime, GeoLocation deliverLocation,
                                       GeoLocation shopLocation, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            try {
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                while (true) {
                    Goodybag gB = new Goodybag(title, status, description, tip, deliverTime, deliverLocation, shopLocation, uS.getUser());

                    try {
                        Goodybag goodyBag = template.loadByProperty(Goodybag.class, "goodyBagID", gB.getGoodyBagID());
                        continue;
                    } catch (NotFoundException nfe) {
                        uS.getUser().getGoodybags().add(gB);
                        template.save(gB);
                        return new SimpleAnswer(true);
                    }
                }
            } catch (NotFoundException nfe) {
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
            }
        } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
    }

   public static SimpleAnswer uploadRoute(ArrayList<GeoLocation> routes, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
       template.purgeSession();
       template.clear();

        if (checkAccessToken(accessToken).getSuccess()) {
            try {
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                for(int i = 0; i<routes.size(); i++){
                    routes.get(i).setGeoLocationID(Double.toString(routes.get(i).getLatitude())+
                    Double.toString(routes.get(i).getLongitude()));
                    try{
                        GeoLocation gL1 = template.loadByProperty(GeoLocation.class, "geoLocationID",routes.get(i).getGeoLocationID());
                        try{
                            if(i != routes.size()-1) {
                                GeoLocation gL2 = template.loadByProperty(GeoLocation.class, "geoLocationID", routes.get(i + 1).getGeoLocationID());
                                if(!(gL1.getConnectedSpots().contains(gL2))){
                                    gL1.getConnectedSpots().add(gL2);
                                    gL2.getConnectedSpots().add(gL1);
                                }
                            }
                        }catch(NotFoundException nfe){
                            gL1.getConnectedSpots().add(routes.get(i+1));
                            routes.get(i+1).getConnectedSpots().add(gL1);
                        }

                    }catch(NotFoundException nfe){
                        if(i==0){
                            if(uS.getUser().getStartedAt()==null){
                                ArrayList<GeoLocation> startedAt = new ArrayList<GeoLocation>();
                                startedAt.add(routes.get(i));
                                uS.getUser().setStartedAt(startedAt);
                            }else {
                                uS.getUser().getStartedAt().add(routes.get(i));
                            }
                        }else{
                            System.out.println(routes.size());
                            if(i != routes.size()-1)
                                if(!(routes.get(i).getConnectedSpots().contains(routes.get(i+1)))) {
                                    routes.get(i).getConnectedSpots().add(routes.get(i+1));
                            }
                        }
                    }
                }
                template.save(uS.getUser(),-1);




                return new SimpleAnswer(true, "Spots updated");
            } catch (NotFoundException nfe) {
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");

            }
        } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
    }

    public static SimpleAnswer uploadProfilePicture(MultipartFile file, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            if (!file.isEmpty()) {
                try {
                    Date d = new Date();
                    long t = d.getTime();
                    UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                    User u = uS.getUser();
                    Files.deleteIfExists(Paths.get(ROOT, u.getProfilePicture()));
                    String[] extension = file.getOriginalFilename().split("\\.");
                    for (int i = 0; i < mimeTypes.length; i++) {
                        if (mimeTypes[i].equals(extension[extension.length - 1])) {
                            u.setProfilePicture(t + u.getEmailAddress() + "." + extension[extension.length - 1]);
                            Files.copy(file.getInputStream(), Paths.get(ROOT, u.getProfilePicture()));
                            template.save(uS);
                            template.purgeSession();
                            template.clear();
                            return new SimpleAnswer(true);
                        }
                    }
                    return new SimpleAnswer(false, "Bad Filetype");
                } catch (IOException | RuntimeException nfe) {
                    return new SimpleAnswer(false, "Some Exception");
                }
            } else {
                return new SimpleAnswer(false, "Empty File");
            }
        } else {
            return new SimpleAnswer(false, "Invalid accessToken, refreshToken required");
        }
    }

    public static SimpleAnswer changePassword(String password, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            try {
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                User u = uS.getUser();
                Date d = new Date();
                Long t = d.getTime();
                u.setSalt(Long.toString(t));
                u.setPassword(u.createMD5(password, u.getSalt()));
                template.save(u);
                return new SimpleAnswer(true);
            } catch (NotFoundException nfe) {
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
            }
        } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
    }

    public static SimpleAnswer storeFirebaseToken(String accessToken, String fireBaseToken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        if(checkAccessToken(accessToken).getSuccess()){
            try{
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                FirebaseToken fT= new FirebaseToken(fireBaseToken, uS.getUser());
                template.save(fT);
                return new SimpleAnswer(true);
            }catch (NotFoundException nfe){
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
            }
        }else return new SimpleAnswer(false, "Invalid AccessToken, refreshToken required");
    }

    public static void retrieveAllGoodybags(String accessToken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        if(checkAccessToken(accessToken).getSuccess()){
            UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
            User u = uS.getUser();
            Filters fl = new Filters();
            Filter f = new Filter();
            fl.add("creatorName","felix");
            //System.out.println(template.loadAllByProperties(Goodybag.class, f));
            System.out.println(template.loadAllByProperties(Goodybag.class, fl,2));

        }
    }

}
