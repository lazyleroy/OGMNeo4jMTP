package config;

import entities.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.neo4j.ogm.cypher.BooleanOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;
import org.neo4j.ogm.exception.NotFoundException;
import org.neo4j.ogm.json.JSONException;
import org.neo4j.ogm.json.JSONObject;
import org.neo4j.ogm.model.Result;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.web.multipart.MultipartFile;
import requestAnswers.LoginAnswer;
import requestAnswers.RegisterAnswer;
import requestAnswers.SimpleAnswer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
                UserSession uS = new UserSession(u);
                Cookie c = new Cookie(u);
                if(firebaseToken != null){
                    FirebaseToken fT = new FirebaseToken(firebaseToken, c);
                    c.setFirebaseToken(fT);
                    template.save(u);
                    template.save(fT);
                }

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

    public static LoginAnswer emailLogin(String email, String password, String firebaseToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();

        try {
            User u = template.loadByProperty(User.class, "emailAddress", email);
            String hash = u.createSHA3(password, u.getSalt());
            UserSession uS = new UserSession(u);
            Cookie c = new Cookie(u);
            if (hash.equals(u.getPassword())) {
                if(firebaseToken != null){
                    FirebaseToken fT = new FirebaseToken(firebaseToken, c);
                    template.save(fT);
                }

                template.save(uS);
                template.save(c);
                return new LoginAnswer(true, uS.getAccessToken(), 86400000L, c.getRefreshToken(), 15768000000L, u.getProfilePicture(), u.getUserName());
            } else {
                return new LoginAnswer(false, "Invalid password");
            }
        } catch (NotFoundException nfe) {
            return new LoginAnswer(false, "Email does not exist.");
        }
    }

    public static RegisterAnswer refreshTokenLogin(String refreshToken, int clientID, String clientSecret) {
        if(clientID!= CLIENTID || !(clientSecret.equals(CLIENTSECRET))){
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

    public static SimpleAnswer updateProfile(String userName, String email, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            try {
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                User u = uS.getUser();
                if(userName != null){
                    u.setUserName(userName);
                }
                if (email != null){
                    u.setEmailAddress(email);
                }
                template.save(u);
                return new SimpleAnswer(true, "values updated: " + userName + " " + email);
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
                if(title == null){
                    title = "Goodybag";
                }
                if(status == null){
                    status = "Not Accepted";
                }
                if(deliverTime == 0) {
                    deliverTime = 0;
                }
                if(description == null||deliverLocation == null || tip < 0 || shopLocation == null){
                    return new SimpleAnswer(false, "Important value missing (description / deliverLocation / shopLocation)");
                }
                while (true) {
                    Goodybag gB = new Goodybag(title, status, description, tip, deliverTime, deliverLocation, shopLocation, uS.getUser());
                    gB.changeID();
                    try {
                        Goodybag goodyBag = template.loadByProperty(Goodybag.class, "goodyBagID", gB.getGoodybagID());
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
                long startTime = System.nanoTime();

                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);

                Collection<GeoLocation> collection = template.loadAllByProperty(GeoLocation.class, "userID", uS.getUser().getUserID());
                ArrayList<GeoLocation> gL = new ArrayList<>(collection);
                long stopTime = System.nanoTime();
                System.out.println(stopTime - startTime);
                for(int i = 0; i < gL.size(); i++){
                    System.out.println(gL.get(i).getGeoLocationID());
                }

                for(int j = 0; j < routes.size(); j++){
                    routes.get(j).setGeoLocationID(Double.toString(routes.get(j).getLatitude())+
                            Double.toString(routes.get(j).getLongitude()));
                    routes.get(j).setUserID(uS.getUser().getUserID());
                    routes.get(j).setAddress("default");
                    routes.get(j).setTitle("default");
                }
                for(int i = 0; i<routes.size(); i++){

                    try{

                        GeoLocation gL1 = template.loadByProperty(GeoLocation.class, "geoLocationID",routes.get(i).getGeoLocationID());
                        try{
                            if(i != routes.size()-1) {

                                GeoLocation gL2 = template.loadByProperty(GeoLocation.class, "geoLocationID", routes.get(i + 1).getGeoLocationID());
                                if(!(gL1.getConnectedSpots().contains(gL2))){
                                    System.out.println("Knoten nicht vorhanden Füge Knoten "+ " " +i+1+ " hinzu");
                                    gL2.getConnectedSpots().add(gL1);
                                    gL1.getConnectedSpots().add(gL2);
                                    template.save(gL2, 1);
                                }
                            }
                        }catch(NotFoundException nfe){
                            routes.get(i+1).getConnectedSpots().add(gL1);
                            gL1.getConnectedSpots().add(routes.get(i+1));
                            template.save(gL1, 1);
                        }

                    }catch(NotFoundException nfe){
                        if(i==0){
                            if(uS.getUser().getStartedAt()==null){
                                ArrayList<GeoLocation> startedAt = new ArrayList<GeoLocation>();
                                startedAt.add(routes.get(i));
                                uS.getUser().setStartedAt(startedAt);
                                template.save(uS.getUser());
                            }else {
                                uS.getUser().getStartedAt().add(routes.get(i));
                                template.save(uS.getUser());
                            }
                        }else{
                            try{

                                    GeoLocation gL2 = template.loadByProperty(GeoLocation.class, "geoLocationID", routes.get(i - 1).getGeoLocationID());
                                    gL2.getConnectedSpots().add(routes.get(i));
                                    template.save(gL2, 1);
                            }catch(NotFoundException nf){
                                System.out.println(routes.get(i-1).getGeoLocationID());
                            }
                         }
                    }
                }

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

                    UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                    User u = uS.getUser();
                    Files.deleteIfExists(Paths.get(ROOT, u.getProfilePicture()));
                    String[] extension = file.getOriginalFilename().split("\\.");
                    for (int i = 0; i < mimeTypes.length; i++) {
                        if (mimeTypes[i].equals(extension[extension.length - 1])) {
                            u.setProfilePicture(u.getUserID() + "." + extension[extension.length - 1]);
                            Files.copy(file.getInputStream(), Paths.get(ROOT, u.getProfilePicture()));
                            template.save(uS);
                            template.purgeSession();
                            template.clear();
                            return new SimpleAnswer(true, u.getProfilePicture());
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
                u.setPassword(u.createSHA3(password, u.getSalt()));
                template.save(u);
                return new SimpleAnswer(true);
            } catch (NotFoundException nfe) {
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
            }
        } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
    }

/*       public static SimpleAnswer storeFirebaseToken(String accessToken, String fireBaseToken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        if(checkAccessToken(accessToken).getSuccess()){
            try{
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                FirebaseToken fT= new FirebaseToken(fireBaseToken, uS.getUser());
                ArrayList<FirebaseToken> fbT = uS.getUser().getFirebaseToken();
                if(fbT == null){
                    fbT = new ArrayList<>();
                    fbT.add(fT);
                    uS.getUser().setFirebaseToken(fbT);
                }else{
                    uS.getUser().getFirebaseToken().add(fT);
                }
                template.save(fT);
                return new SimpleAnswer(true);
            }catch (NotFoundException nfe){
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
            }
        }else return new SimpleAnswer(false, "Invalid AccessToken, refreshToken required");
    }
*/
    public static void retrieveAllGoodybags(String accessToken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        if(checkAccessToken(accessToken).getSuccess()){
            UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
            List<Goodybag> u = uS.getUser().getGoodybags();
        }
    }

    public static void sendGoodybagToUsers(ArrayList<Long> userIDs, String goodybagID){

        Neo4jTemplate template = main.createNeo4JTemplate();
        String query ="";
        for(int i = 0; i < userIDs.size(); i++){
            if(i == userIDs.size()-1){
                query += userIDs.get(i).toString();
            }else{
                query += userIDs.get(i).toString()+",";
            }
        }
        ArrayList<String> firebaseTokens = new ArrayList<String>();
        String firebaseToken ="";
        String refreshToken;
        Result r = template.query("match (x:FirebaseToken)-[:COOKIE]->(y:Cookie)-[:USER]->(n:User) where n.userID IN["+query+"] return x,y", Collections.EMPTY_MAP, true);
        Iterator<Map<String, Object>> iterator = r.iterator();
        while(iterator.hasNext()){
            Map<String, Object> i = iterator.next();
            for(Map.Entry<String, Object> entry: i.entrySet()){
                if(entry.getValue() instanceof FirebaseToken){
                    firebaseToken = ((FirebaseToken) entry.getValue()).getToken();
                }
                if(entry.getValue()instanceof Cookie){
                    refreshToken = ((Cookie) entry.getValue()).getRefreshToken();
                    org.json.JSONObject notification = new org.json.JSONObject();
                    org.json.JSONObject goodybag = new org.json.JSONObject();
                    org.json.JSONObject body = new org.json.JSONObject();
                    body.put("body","Matched Goodybag");
                    goodybag.put("goodybagID", goodybagID);
                    goodybag.put("refreshToken", refreshToken);
                    notification.put("notification", body);
                    notification.put("data", goodybag);
                    notification.put("to", firebaseToken);

                    HttpClient httpClient    = HttpClientBuilder.create().build();
                    HttpPost post          = new HttpPost("https://fcm.googleapis.com/fcm/send");
                    try {
                        StringEntity se = new StringEntity(notification.toString());
                        post.setEntity(se);
                        post.setHeader("Content-type", "application/json");
                        post.addHeader("Authorization", "key=AIzaSyCwYZ4ddd2Ue0DcRCJkhdHSuX1x6AoMC8Q");
                        HttpResponse response = httpClient.execute(post);
                        System.out.println(EntityUtils.toString(response.getEntity()));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }







    }

}
