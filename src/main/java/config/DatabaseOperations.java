package config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import entities.*;
import entities.Cookie;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.*;
import org.json.JSONObject;
import org.neo4j.ogm.exception.NotFoundException;
import org.neo4j.ogm.json.*;
import org.neo4j.ogm.model.Result;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.web.multipart.MultipartFile;
import requestAnswers.LoginAnswer;
import requestAnswers.RegisterAnswer;
import requestAnswers.SimpleAnswer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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
     * @param accesstoken token to verify the user
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
     *                      process it will be stored in this function call. If the firebaseToken already exists it will be bound
     *                      to the newly created refreshToken.
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
                    try{
                        FirebaseToken fT0 = template.loadByProperty(FirebaseToken.class, "token", firebaseToken);
                        fT0.setUser(c);
                        template.save(fT0);
                    }catch(NotFoundException nfe){
                        FirebaseToken fT = new FirebaseToken(firebaseToken, c);
                        template.save(fT);
                    }

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
     * Verifies a user via his refreshtoken + clientID + clientSecret.
     * @param refreshToken token to verify the user
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
        String name = "No changes";
        String mail = "No changes";
        Neo4jTemplate template = main.createNeo4JTemplate();

            if (checkAccessToken(accessToken).getSuccess()) {
                try {
                    UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                    User u = uS.getUser();
                    if (userName != null && !userName.equals("")&&!u.getUserName().equals(userName)) {
                        u.setUserName(userName);
                        name = userName;
                    }
                    if (email != null && !email.equals("")&& !u.getEmailAddress().equals(email)) {
                            try{
                                User user = template.loadByProperty(User.class, "emailAddress", email);
                                return new SimpleAnswer(false, "Email already exists. Choose another one");
                            }catch(NotFoundException nfe){
                                u.setEmailAddress(email);
                                mail = email;
                            }
                    }
                    template.save(u);
                    return new SimpleAnswer(true, "Update complete:  Username: " + name + " - Email: " + mail);
                } catch (NotFoundException nfe2) {
                    return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
                }
            } else return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");

    }

    /**
     * Core function of the application. This function creates a goodybag and links it to a user on the database.
     * @param title the title of the goodybag
     * @param status the status of a goodybag. Represents if a goodybag is already taken, done, matched,...
     * @param description holds information about what the creator of the goodybag needs
     * @param tip amount of money the creator is ready to pay for the delivery of the described goods
     * @param deliverTime point of time until when the creator wants the goodybag to be delivered
     * @param deliverLocation the location the creator wants the goodybag to be delivered to
     * @param shopLocation the location of the shop from where the goods shall be bought
     * @param accessToken the token to verify a user and enable the creationg of a goodybag
     * @return depending on missing or wrong input this method will return a SimpleAnswer holding false + reason
     * what went wrong on the creation process. The SimpleAnswer holds true if the process worked.
     */
    public static SimpleAnswer uploadGoodybag(String title, String status, String description,
                                              double tip, long deliverTime, GeoLocation deliverLocation,
                                              GeoLocation shopLocation, int checkOne, int checkTwo, String accessToken) {
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
                    deliverTime = -1;
                }
                if (description.equals("") || deliverLocation == null || tip < 0 || shopLocation == null) {
                    return new SimpleAnswer(false, "Important value missing (description / deliverLocation / shopLocation)");
                }
                while (true) {
                    Goodybag gB = new Goodybag(title, status, description, tip, deliverTime, deliverLocation, shopLocation, uS.getUser(), checkOne, checkTwo);
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

    /**
     * A simple function to upload a picture and store it in the database. Only files with a size of 10MB will be accepted.
     * In addition this method will only accept files with the following file-extensions: "jpg", "jpeg", "bmp", "png", "gif", "svg".
     * @param file the file that is to be uploaded to the database
     * @param accessToken token to verify the user
     * @return The returned SimpleAnswer holds true and the filename of the uploaded picture if the process is successful.
     * The returned SimpleAnswer holds wrong and a reason if something went wrong.
     */
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

    /**
     * This function changes the password of a user.
     * @param password the new password of the user
     * @param accessToken token to verify the password
     * @return returns a SimpleAnswer with true or a SimpleAnswer holding false + a reason in case of an invalid refreshToken
     */
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

    /**
     * Explicitly stores a firebasetoken of a user if it has not already been uploaded during the login or refreshtokenlogin.
     * @param refreshToken token to verify a user.
     * @param fireBaseToken the token that is to be saved on the database. Will be linked to the cookie of a user.
     * @return A SimpleAnswer holding false + reason if a firebasetoken already exists or if the given refreshtoken does not exist.
     * A SimpleAnwer holding true if everything worked.
     */
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


    /**
     * Function to return all goodybags that are linked to a unique user.
     * @param accessToken token to verfiy the unique user
     * @return returns an ArrayList of Goodybags.
     */
    public static ArrayList<Goodybag> myGoodybags(String accessToken) {
        if(checkAccessToken(accessToken).getSuccess()) {
            Neo4jTemplate template = main.createNeo4JTemplate();
            Date d = new Date();
            //Timestamp from now + 12 hours (extra time if goodybag is overdue)
            long timestamp = d.getTime()+43200000;
            Result result = template.query("MATCH(n:UserSession{accessToken:\'" + accessToken + "\'})-[:USER]-(m:User)-[:OWNS]-(t:Goodybag)" +
                    "MATCH(t:Goodybag)-[:DELIVER_LOCATION]-(k:GeoLocation) " +
                    "MATCH(t:Goodybag)-[:SHOP_LOCATION]-(r:GeoLocation)return t,k,r", Collections.EMPTY_MAP, true);

            Iterator<Map<String, Object>> iterator = result.iterator();
            ArrayList<Goodybag> goodybags = new ArrayList<>();
            int i = 0;
            while (iterator.hasNext()) {
                boolean j = true;
                Map<String, Object> map = iterator.next();
                //noinspection Convert2streamapi
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getValue() instanceof Goodybag) {
                        if (((Goodybag) entry.getValue()).getDeliverTime() < timestamp || ((Goodybag) entry.getValue()).getStatus().equals("Done")) {
                            template.delete(map.get("k"));
                            template.delete(map.get("r"));
                            template.delete((Goodybag) entry.getValue());
                            break;
                        }
                        goodybags.add((Goodybag) entry.getValue());
                    }
                    if (entry.getValue() instanceof GeoLocation && j) {
                        goodybags.get(i).setDeliverLocation((GeoLocation) entry.getValue());
                        j = false;
                        continue;
                    }
                    if (entry.getValue() instanceof GeoLocation && !j) {
                        goodybags.get(i).setShopLocation((GeoLocation) entry.getValue());
                    }
                }
            }
            return goodybags;
        }
        return new ArrayList<>();
    }


    /**
     * Function to rate a user and to set a goodybags status to "Done".
     * @param goodybagID the ID of the finished goodybag
     * @param rating number between 1 and 5 to rate the shopper of the goodybag.
     * @return A SimpleAnswer holding false + reason if the goodybags status is already set on "Done" or if the goodybag does not exist
     * A Simple Answer holding true if everything worked.
     */
    public static SimpleAnswer finishGoodybag(long goodybagID, int rating, String accessToken) {
        if (checkAccessToken(accessToken).getSuccess()) {
            Neo4jTemplate template = main.createNeo4JTemplate();
            try {
                Goodybag gB = template.loadByProperty(Goodybag.class, "goodybagID", goodybagID);
                if (gB.getStatus().equals("Done")) {
                    return new SimpleAnswer(false, "Goodybag aready done. Cannot rate twice.");
                }
                if (rating == -1){
                    gB.setStatus("Done");
                    return new SimpleAnswer(true);
                }
                gB.setStatus("Done");
                User u = gB.getUser();
                u.setNumberOfRatings(u.getNumberOfRatings() + 1);
                u.setCumulatedRatings(u.getCumulatedRatings() + rating);
                u.setRating((double) (u.getCumulatedRatings()) / u.getNumberOfRatings());
                template.save(gB);
                return new SimpleAnswer(true, String.valueOf(u.getRating()));
            } catch (NotFoundException nfe) {
                return new SimpleAnswer(false, "Goodybag does not exist");
            }
        }else {
            return new SimpleAnswer(false, "Invalid Accesstoken");
        }
    }

    public static Goodybag getGoodybagbyID(long goodybagID, String accessToken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        Goodybag gB = new Goodybag();
        if (checkAccessToken(accessToken).getSuccess()){
            Result r = template.query("match (u:UserSession)-[:USER]-(n:User)-[:MATCHED_TO]-(m:Goodybag) where m.goodybagID = "+goodybagID+" and u.accessToken = \'"+accessToken+"\' return m", Collections.EMPTY_MAP, false);
            Iterator<Map<String, Object>> iterator = r.iterator();
            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                Map<String, Object> i = iterator.next();
                for (Map.Entry<String, Object> entry : i.entrySet()) {
                    if(entry.getValue() instanceof  Goodybag){
                         gB = (Goodybag)entry.getValue();
                    }
                }
            }
            return gB;
        }
        return null;
    }

    public static ArrayList<Goodybag> matchedGoodybags(String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        Date d = new Date();
        long timestamp = d.getTime();
        Result result = template.query("MATCH(n:UserSession{accessToken:\'" + accessToken + "\'})-[:USER]-(m:User)-[:MATCHED_TO]-(t:Goodybag)-[:OWNS]-(q:User)" +
                "MATCH(t:Goodybag)-[:DELIVER_LOCATION]-(k:GeoLocation) " +
                "MATCH(t:Goodybag)-[:SHOP_LOCATION]-(r:GeoLocation)return t,k,r,q", Collections.EMPTY_MAP, true);

        Iterator<Map<String, Object>> iterator = result.iterator();
        ArrayList<Goodybag> goodybags = new ArrayList<>();
        int i = 0;
        while (iterator.hasNext()) {
            boolean j = true;
            Map<String, Object> map = iterator.next();
            //noinspection Convert2streamapi
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof Goodybag) {
                    if (((Goodybag) entry.getValue()).getDeliverTime() < timestamp || ((Goodybag) entry.getValue()).getStatus().equals("Done")) {
                        template.delete(map.get("k"));
                        template.delete(map.get("r"));
                        template.delete((Goodybag) entry.getValue());
                        break;
                    }
                    goodybags.add((Goodybag) entry.getValue());
                }
                if (entry.getValue() instanceof GeoLocation && j) {
                    goodybags.get(i).setDeliverLocation((GeoLocation) entry.getValue());
                    j = false;
                    continue;
                }
                if (entry.getValue() instanceof GeoLocation && !j) {
                    goodybags.get(i).setShopLocation((GeoLocation) entry.getValue());
                }
                if (entry.getValue() instanceof User) {
                    goodybags.get(i).setUser((User) entry.getValue());
                }
            }
        }
        return goodybags;
    }

    public static SimpleAnswer acceptGoodybag(long goodybagID, String accessToken){
        if(checkAccessToken(accessToken).getSuccess()){
            Neo4jTemplate template = main.createNeo4JTemplate();
            Goodybag gB = new Goodybag();
            long userID = 0;

            Result result = template.query("match(u:UserSession)-[:USER]-(n:User)-[:MATCHED_TO]-(gb:Goodybag)-[:USER]-(x:User)-[:USER]-" +
                    "(c:Cookie)-[:COOKIE]-(ft:FirebaseToken) where u.accessToken=\'"+accessToken+"\' and gb.goodybagID="+goodybagID +" return gb, ft,n", Collections.EMPTY_MAP, true);

            Iterator<Map<String, Object>> iterator = result.iterator();
            while (iterator.hasNext()) {
                boolean j = true;
                Map<String, Object> map = iterator.next();
                //noinspection Convert2streamapi
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getValue() instanceof Goodybag) {
                        if(gB.getGoodybagID() == ((Goodybag) entry.getValue()).getGoodybagID()){
                            continue;
                        }
                        if ((((Goodybag) entry.getValue()).getStatus().equals("Accepted"))) {
                            return new SimpleAnswer(false, "Goodybag has already been accepted");
                        }
                        gB = (Goodybag)entry.getValue();
                        gB.setStatus("Accepted");
                    }
                    if (entry.getValue() instanceof FirebaseToken) {

                        org.json.JSONObject notification = new org.json.JSONObject();
                        org.json.JSONObject message = new org.json.JSONObject();
                        org.json.JSONObject body = new org.json.JSONObject();
                        body.put("body", "Goodybag Accepted");
                        message.put("title", goodybagID);
                        notification.put("notification", body);
                        notification.put("data", message);
                        notification.put("to", ((FirebaseToken) entry.getValue()).getToken());

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
                    if(entry.getValue() instanceof User){
                        userID = ((User) entry.getValue()).getUserID();
                    }
                }
            }
            template.query("match(n:User)-[p:MATCHED_TO]-(g:Goodybag)where g.goodybagID="+goodybagID+" and " +
                    "NOT n.userID = "+userID+" delete p", Collections.EMPTY_MAP, false);
            template.query("match(g:Goodybag) where g.goodybagID="+goodybagID+" set g.status = \'Accepted\' ",Collections.EMPTY_MAP,false);
            return new SimpleAnswer(true);

        }
    return new SimpleAnswer(false, "Invalid Accesstoken, refreshToken required");
    }


    /**
     * Function to add a relationship between Users and their matched Goodybags on the database. This method will
     * also automatically send a goodybagID to all users who have been matched via a push notification using their
     * firebaseTokens.
     * @param userIDs the IDs of the users who shall be notificated
     * @param goodybagID the ID of the matched goodybag
     */
    public static void matching(ArrayList<String> userIDs, long goodybagID){
        Neo4jTemplate template = main.createNeo4JTemplate();
        String query = "";
        int counter = 0;
        HashSet<Long> tempSet = new HashSet<>();
        Goodybag gB;
        try{
            gB = template.loadByProperty(Goodybag.class, "goodybagID", goodybagID);

        }catch(NotFoundException nfe){
            System.out.println("Goodybag not found. Wrong ID or its deliverTime might have expired during the matching process");
            return;
        }
        for(int i = 0; i < userIDs.size(); i++){
            if(i == 0){
                query+= userIDs.get(i);
                continue;
            }
            query += ","+userIDs.get(i);
        }
        String firebaseToken = "";
        Result r = template.query("match (x:FirebaseToken)-[:COOKIE]->(y:Cookie)-[:USER]->(n:User) where n.userID IN["+query+"] return x,y,n", Collections.EMPTY_MAP, true);
        Iterator<Map<String, Object>> iterator = r.iterator();
        //noinspection WhileLoopReplaceableByForEach
        String query2= "merge(gb:Goodybag{goodybagID:"+goodybagID+"})";
        while (iterator.hasNext()) {
            Map<String, Object> i = iterator.next();
            for (Map.Entry<String, Object> entry : i.entrySet()) {
                if (entry.getValue() instanceof FirebaseToken) {
                    firebaseToken = ((FirebaseToken) entry.getValue()).getToken();
                }
                if (entry.getValue() instanceof Cookie) {

                    gB.setMatchedUsers(null);
                    gB.getUser().setGoodybags(null);
                    gB.getUser().setEmailAddress(null);
                    gB.getUser().setCumulatedRatings(0);
                    gB.getUser().setPassword(null);
                    gB.getUser().setSalt(null);
                    gB.getUser().setRoutes(null);
                    gB.getUser().setSalt(null);
                    gB.setStatus(null);

                    org.json.JSONObject notification = new org.json.JSONObject();
                    org.json.JSONObject goodybag = new org.json.JSONObject(gB);
                    org.json.JSONObject body = new org.json.JSONObject();
                    body.put("body", "Matched Goodybag");
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
                }if(entry.getValue() instanceof User){
                    if(tempSet.contains(((User) entry.getValue()).getUserID())){
                        continue;
                    }
                    tempSet.add(((User) entry.getValue()).getUserID());
                    query2+= "merge(u"+counter+":User{userID:"+userIDs.get(counter)+"})merge(gb)-[:MATCHED_TO]-(u"+counter+")";
                    counter++;
                }
                }
            }
        template.query(query2, Collections.EMPTY_MAP, false);
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