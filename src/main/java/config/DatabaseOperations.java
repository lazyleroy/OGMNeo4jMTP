package config;

import entities.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.neo4j.ogm.exception.NotFoundException;
import org.neo4j.ogm.model.Result;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.web.multipart.MultipartFile;
import requestAnswers.LoginAnswer;
import requestAnswers.RegisterAnswer;
import requestAnswers.SimpleAnswer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import static config.FileUploadController.ROOT;

/**
 * DatabaseOperations is used to execute operations related to a Database directly on the server.
 * The functions of this class or mostly called in JSON Controller and FileUploadController of this project
 * Created by Felix on 15.07.2016.
 */
public class DatabaseOperations {

    /**
     * Instance of the Main class to create Neo4JTemplates in different functions of the DatabaseOperations class.
     */
    private static Main main = new Main();

    /**
     * Types / Extensions that are allowed in the uploadProfilePicture function.
     * By default jpg, jpeg, bmp, png, gif and svg are allowed.
     */
    private static String[] mimeTypes = {"jpg", "jpeg", "bmp", "png", "gif", "svg"};
    /**
     * Number that is checked in different functions to verify the sender uses the correct application.
     * Always used with CLIENTSECRET
     */
    private static final int CLIENTID = 49911975;
    /**
     * String that is checked in different functions to verify the sender uses the correct application.
     * Always used with CLIENTID.
     */
    private static final String CLIENTSECRET = "ab02ndls82md9ak";


    /**
     * Method to register a user on the database. Returns false + reason if the chosen email is already bound to
     * another user on the database.
     * @param user the user that is to be stored
     * @param emailAddress email of the user. Is checked against duplicates on the database
     * @param firebaseToken Token that is required for pushNotifications later on. Stored together with the user.
     * @return RegisterAnswer consists of false and a reason if the registration fails. If the registration process
     * succeeds the RegisterAnswer will consist of true, refreshToken, accessToken + their expiry dates.
     */
    public static RegisterAnswer register(User user, String emailAddress, String firebaseToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        try {
            User t = template.loadByProperty(User.class, "emailAddress", emailAddress);
            return new RegisterAnswer(false, "Emailadress already exists");
        } catch (NotFoundException nfe) {
            template.purgeSession();
            template.clear();

            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    User t = template.loadByProperty(User.class, "userID", user.getUserID());
                    user.changeID();
                }
            } catch (NotFoundException e) {
                UserSession uS = new UserSession(user);
                Cookie c = new Cookie(user);
                if (firebaseToken != null) {
                    FirebaseToken fT = new FirebaseToken(firebaseToken, c);
                    c.setFirebaseToken(fT);
                    template.save(user);
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

    /**
     * Checks the expiry date and the correctness of an accesstoken of a user. This function is used in other functions
     * to verify a user and grant rights for further database operations.
     * @param accesstoken The token sent by the user to verify himself
     * @return SimpleAnswer will either return false, "Invalid AccessToken, refreshToken required" if the accessToken
     * is wrong or expired, or simply holds true if everything is ok.
     */
    @SuppressWarnings("WeakerAccess")
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

    /**
     * This function uses the credentials of a user to login and to create an accesstoken and a refreshtoken for the user
     * for further verification.
     * @param email the email of the user
     * @param password the password of the user
     * @param firebaseToken token that will be stored if the user exists. If the token was not stored during the registration
     *                      process it will be stored in this function call.
     * @return returns false + reason if email or password are incorrect. Returns true, accesstoken refreshtoken + their
     * expiry dates if the login was successful.
     */

    public static LoginAnswer emailLogin(String email, String password, String firebaseToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();

        try {
            User u = template.loadByProperty(User.class, "emailAddress", email);
            String hash = u.createSHA1(password, u.getSalt());
            UserSession uS = new UserSession(u);
            Cookie c = new Cookie(u);
            if (hash.equals(u.getPassword())) {
                if (firebaseToken != null) {
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

    /**
     * Verifies a user via his refreshtoken + clientID + clientSecret
     * @param refreshToken token that verifies the user
     * @param clientID ID of the app. Verifies that the request was sent from the correct application
     * @param clientSecret ID of the app. Verifies that the request was sent from the correct application
     * @return RegisterAnswer consists of false + a reason if one or multiple of the params are incorrect
     * RegisterAnswer returns true, + a new accesstoken + old refreshtoken + their expiry dates if the call was successful.
     */

    public static RegisterAnswer refreshTokenLogin(String refreshToken, int clientID, String clientSecret) {
        if (clientID != CLIENTID || !(clientSecret.equals(CLIENTSECRET))) {
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

    /**
     * Changes username and / or email of a User.
     * @param userName String that shall be the new username of the user
     * @param email String that shall be the new username of the user. Email is a unique value and can only exist once in the database
     * @param accessToken token to verify the user
     * @return SimpleAnswer consists of false + reason if the new email is already present on the database or if the accesstoken is
     * invalid (due to expiry date or wrong spelling). SimpleAnswer consists of true + the updated values if everything was ok.
     *
     */
    public static SimpleAnswer updateProfile(String userName, String email, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        try{
            User u = template.loadByProperty(User.class, "emailAddress", email);
            return new SimpleAnswer(false, "Email already exists. Chose another one");
        }catch(NotFoundException nfe) {
            if (checkAccessToken(accessToken).getSuccess()) {
                try {
                    UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                    User u = uS.getUser();
                    if (userName != null) {
                        u.setUserName(userName);
                    }
                    if (email != null) {
                        u.setEmailAddress(email);
                    }
                    template.save(u);
                    return new SimpleAnswer(true, "values updated: " + userName + " " + email);
                } catch (NotFoundException nfe2) {
                    return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
                }
            } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
        }
    }


    public static SimpleAnswer uploadGoodybag(String title, String status, String description,
                                              double tip, long deliverTime, GeoLocation deliverLocation,
                                              GeoLocation shopLocation, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            try {
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                Date d = new Date();
                if (title == null) {
                    title = "Goodybag";
                }
                if (status == null) {
                    status = "Not Accepted";
                }
                if (deliverTime <= d.getTime()) {
                    deliverTime = 0;
                }
                if (description.equals("") || deliverLocation == null || tip < 0 || shopLocation == null) {
                    return new SimpleAnswer(false, "Important value missing (description / deliverLocation / shopLocation)");
                }
                while (true) {
                    Goodybag gB = new Goodybag(title, status, description, tip, deliverTime, deliverLocation, shopLocation, uS.getUser());
                    gB.changeID();
                    try {
                        Goodybag goodyBag = template.loadByProperty(Goodybag.class, "goodyBagID", gB.getGoodybagID());
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




    public static SimpleAnswer uploadProfilePicture(MultipartFile file, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            if (!file.isEmpty()) {
                try {

                    UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                    User u = uS.getUser();
                    Files.deleteIfExists(Paths.get(ROOT, u.getProfilePicture()));
                    String[] extension = file.getOriginalFilename().split("\\.");
                    //noinspection ForLoopReplaceableByForEach
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
                u.setSalt(Long.toString(d.getTime()));
                u.setPassword(u.createSHA1(password, u.getSalt()));
                template.save(u);
                return new SimpleAnswer(true);
            } catch (NotFoundException nfe) {
                return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
            }
        } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
    }

           public static SimpleAnswer storeFirebaseToken(String refreshToken, String fireBaseToken){
            Neo4jTemplate template = main.createNeo4JTemplate();
                try{
                    Cookie c = template.loadByProperty(Cookie.class, "refreshToken", refreshToken);
                    FirebaseToken fT= new FirebaseToken(fireBaseToken, c);
                    if(c.getFirebaseToken()!=null){
                        return new SimpleAnswer(false, "A FirebaseToken for this Cookie is already stored");
                    }
                    c.setFirebaseToken(fT);
                    template.save(fT);
                    return new SimpleAnswer(true);
                }catch (NotFoundException nfe){
                    return new SimpleAnswer(false, "Invalid RefreshToken, Login required");
                }
        }

    public static ArrayList<Goodybag> retrieveAllGoodybags(String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
       Result result = template.query("MATCH(n:UserSession{accessToken:\'"+accessToken+"\'})-[:USER]-(m:User)-[:OWNS]-(t:Goodybag) return t",Collections.EMPTY_MAP, true );
       Iterator<Map<String, Object>> iterator = result.iterator();
        ArrayList<Goodybag> goodybags = new ArrayList<>();
        while(iterator.hasNext()){
            Map<String, Object> map = iterator.next();
            //noinspection Convert2streamapi
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if(entry.getValue() instanceof Goodybag){
                    goodybags.add((Goodybag)entry.getValue());
                }
            }
        }
        return goodybags;
    }

    public static void sendGoodybagToUsers(ArrayList<Long> userIDs, String goodybagID) {

        Neo4jTemplate template = main.createNeo4JTemplate();
        String query = "";
        for (int i = 0; i < userIDs.size(); i++) {
            if (i == userIDs.size() - 1) {
                query += userIDs.get(i).toString();
            } else {
                query += userIDs.get(i).toString() + ",";
            }
        }
        ArrayList<String> firebaseTokens = new ArrayList<>();
        String firebaseToken = "";
        String refreshToken;
        Result r = template.query("match (x:FirebaseToken)-[:COOKIE]->(y:Cookie)-[:USER]->(n:User) where n.userID IN[" + query + "] return x,y", Collections.EMPTY_MAP, true);
        Iterator<Map<String, Object>> iterator = r.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (iterator.hasNext()) {
            Map<String, Object> i = iterator.next();
            for (Map.Entry<String, Object> entry : i.entrySet()) {
                if (entry.getValue() instanceof FirebaseToken) {
                    firebaseToken = ((FirebaseToken) entry.getValue()).getToken();
                }
                if (entry.getValue() instanceof Cookie) {
                    refreshToken = ((Cookie) entry.getValue()).getRefreshToken();
                    org.json.JSONObject notification = new org.json.JSONObject();
                    org.json.JSONObject goodybag = new org.json.JSONObject();
                    org.json.JSONObject body = new org.json.JSONObject();
                    body.put("body", "Matched Goodybag");
                    goodybag.put("goodybagID", goodybagID);
                    goodybag.put("refreshToken", refreshToken);
                    notification.put("notification", body);
                    notification.put("data", goodybag);
                    notification.put("to", firebaseToken);

                    HttpClient httpClient = HttpClientBuilder.create().build();
                    HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
                    try {
                        StringEntity se = new StringEntity(notification.toString());
                        post.setEntity(se);
                        post.setHeader("Content-type", "application/json");
                        post.addHeader("Authorization", "key=AIzaSyCwYZ4ddd2Ue0DcRCJkhdHSuX1x6AoMC8Q");
                        HttpResponse response = httpClient.execute(post);
                        System.out.println(EntityUtils.toString(response.getEntity()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public static SimpleAnswer finishGoodybag(long goodybagID, int rating){
        Neo4jTemplate template = main.createNeo4JTemplate();
        try{
            Goodybag gB = template.loadByProperty(Goodybag.class, "goodybagID", goodybagID);
            if(gB.getStatus().equals("Done")){
                return new SimpleAnswer(false, "Goodybag aready done. Cannot rate twice.");
            }
            gB.setStatus("Done");
            User u = gB.getUser();
            u.setNumberOfRatings(u.getNumberOfRatings()+1);
            u.setCumulatedRatings(u.getCumulatedRatings()+rating);
            u.setRating((double)(u.getCumulatedRatings())/u.getNumberOfRatings());
            template.save(gB);
            return new SimpleAnswer(true, String.valueOf(u.getRating()));
        }catch(NotFoundException nfe){
            return new SimpleAnswer(false, "Goodybag does not exist");
        }
    }

    public static SimpleAnswer test(ArrayList<Waypoint> uploadedWaypoints, String accessToken) {
        long startTime = System.nanoTime();
        if (checkAccessToken(accessToken).getSuccess()) {
            Neo4jTemplate template = main.createNeo4JTemplate();
            try {
                User u = template.loadByProperty(UserSession.class, "accessToken", accessToken).getUser();

                String query = "";
                query += "MERGE(U:User{userID:"+u.getUserID()+"})";
                int j = 0;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < uploadedWaypoints.size(); i++) {
                    j++;
                    Spot spot = uploadedWaypoints.get(i).getSpot();
                    query += "MERGE(W" + j + ":Waypoint{waypointID:" + uploadedWaypoints.get(i).getWaypointID() + "})";
                    query += "MERGE(S" + j + ":Spot{spotID:" + spot.getSpotID() + "})";
                    query += "MERGE(U)<-[:USER]-(W"+ j+")-[:LOCATED_IN]-(S"+j+")";
                    System.out.println(query);
                    Iterator<Spot> spotIterator = spot.getConnectedSpots().iterator();
                    int t = 1;
                    while (spotIterator.hasNext()) {
                        j++;
                        Spot connectedSpot = spotIterator.next();

                        query += "MERGE(S" + j + ":Spot{spotID:" + connectedSpot.getSpotID() + "})";
                        query += "MERGE(S" + (j - t) + ")-[:CONNECTED_WITH]-(S" + j + ")";
                        t++;
                    }
                }
                System.out.println(query);
                template.query(query, Collections.EMPTY_MAP);
                long stopTime = System.nanoTime();
                System.out.println(stopTime - startTime);
                return new SimpleAnswer(true);
            }catch(NotFoundException nfe){
                return new SimpleAnswer(false, "User could not be found");
            }
        }else {
            return new SimpleAnswer(false, "Invalid AccessToken, refreshToken required");
        }
    }


}