package config;

import entities.*;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.web.multipart.MultipartFile;
import requestAnswers.RegisterAnswer;
import entities.UserSession;
import org.neo4j.ogm.exception.NotFoundException;
import org.springframework.data.neo4j.template.Neo4jTemplate;
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

    public String[] mimeTypes = {"jpg", "jpeg", "bmp", "png", "gif", "svg"};

    public static RegisterAnswer register(User u, String emailAddress, String firebaseToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        try {
            User t = new User();
            t = template.loadByProperty(User.class, "emailAddress", emailAddress);
            return new RegisterAnswer(false, "Emailadress already exists");
        } catch (NotFoundException nfe) {
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

    public static RegisterAnswer refreshTokenLogin(String refreshToken) {
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

    public SimpleAnswer uploadGoodybag(String creatorName, int creatorImage, String title, String status, String description,
                                       double tip, long creationTime, long deliverTime, GeoLocation deliverLocation,
                                       GeoLocation shopLocation, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            try {
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);

                while (true) {
                    Goodybag gB = new Goodybag(creatorName, creatorImage, title, status, description, tip, creationTime, deliverTime,
                            deliverLocation, shopLocation, uS.getUser());
                    try {
                        Goodybag goodyBag = template.loadByProperty(Goodybag.class, "goodyBagID", gB.getGoodyBagID());
                        continue;
                    } catch (NotFoundException nfe) {
                        template.save(gB);
                        return new SimpleAnswer(true);
                    }
                }
            } catch (NotFoundException nfe) {
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
            }
        } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
    }

    public SimpleAnswer uploadRoute(ArrayList<GeoLocation> routes, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            try {
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                Route r = new Route(routes, uS.getUser());
                template.save(r);
                return new SimpleAnswer(true, r.getRouteID());
            } catch (NotFoundException nfe) {
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");

            }
        } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
    }

    public SimpleAnswer uploadProfilePicture(MultipartFile file, String accessToken) {
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

    public SimpleAnswer changePassword(String password, String accessToken) {
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

    public SimpleAnswer storeFirebaseToken(String accessToken, String fireBaseToken){
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

}
