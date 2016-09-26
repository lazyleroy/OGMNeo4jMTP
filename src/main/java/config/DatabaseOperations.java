package config;

import entities.*;
import entities.Cookie;
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

import java.io.*;
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
        if(user.getUserName().equals("")|| emailAddress.equals("")){
            return new RegisterAnswer(false, "Empty username or email");
        }
        try {
            User t = template.loadByProperty(User.class, "emailAddress", emailAddress);
            return new RegisterAnswer(false, "Emailadress already exists");
        } catch (NotFoundException nfe) {
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
                    try{
                        FirebaseToken token = template.loadByProperty(FirebaseToken.class, "token", firebaseToken);
                        token.setUser(c);
                        template.save(user);
                        template.save(token);
                    }
                    catch (NotFoundException nfe2) {
                        FirebaseToken fT = new FirebaseToken(firebaseToken, c);
                        c.setFirebaseToken(fT);
                        template.save(user);
                        template.save(fT);
                    }
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
            if(u.getLoginCounter() >= 10){
                return new LoginAnswer(false, "Entered invalid password too often");
            }
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
                        template.save(fT,1);
                    }
                }
                u.setLoginCounter(0);
                u.setChangePasswordCounter(0);
                template.save(uS,1);
                template.save(c,1);
                return new LoginAnswer(true, uS.getAccessToken(), 86400000L, c.getRefreshToken(), 15768000000L, u.getProfilePicture(), u.getUserName(), u.getRating());
            } else {
                u.setLoginCounter(u.getLoginCounter()+1);
                template.save(u);
                return new LoginAnswer(false, "Invalid password. Number of tries left: "+ (10-u.getLoginCounter()));
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
                    if (userName != null && !userName.equals("")&& !u.getUserName().equals(userName)) {
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
                if (deliverTime <= d.getTime()) {
                    deliverTime = -1;
                }
                if (description.equals("") || deliverLocation == null || tip < 0 || shopLocation == null) {
                    return new SimpleAnswer(false, "Important value missing (description / deliverLocation / shopLocation)");
                }
                while (true) {
                    Goodybag gB = new Goodybag(title, "Not Accepted", description, tip, deliverTime, deliverLocation, shopLocation, uS.getUser(), checkOne, checkTwo);
                    gB.changeID();
                    try {
                        Goodybag goodyBag = template.loadByProperty(Goodybag.class, "goodyBagID", gB.getGoodybagID());
                    } catch (NotFoundException nfe) {

                        uS.getUser().getGoodybags().add(gB);
                        template.save(gB);
                        if(uS.getUser().getUserName().equals("Felix")){
                        dummyMatching(gB.getGoodybagID());
                        }
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
                    Date d = new Date();
                    long timestamp = d.getTime();

                    UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                    User u = uS.getUser();
                    Files.deleteIfExists(Paths.get(ROOT, u.getProfilePicture()));
                    String[] extension = file.getOriginalFilename().split("\\.");
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0; i < mimeTypes.length; i++) {
                        if (mimeTypes[i].equals(extension[extension.length - 1].toLowerCase())) {
                            u.setProfilePicture(timestamp+file.getOriginalFilename());
                            File convFile = new File(timestamp+file.getOriginalFilename());
                            convFile.createNewFile();
                            FileOutputStream fos = new FileOutputStream(ROOT+"/"+convFile);
                            fos.write(file.getBytes());
                            fos.close();
                            template.save(uS);
                            return new SimpleAnswer(true, timestamp+file.getOriginalFilename());
                        }
                    }
                    return new SimpleAnswer(false, "Bad Filetype");
                } catch (IOException | RuntimeException nfe) {
                    nfe.printStackTrace();
                    CharArrayWriter cw = new CharArrayWriter();
                    PrintWriter w = new PrintWriter(cw);
                    nfe.printStackTrace(w);
                    w.close();
                    String test = cw.toString();
                    return new SimpleAnswer(false, "Exception on the Server");
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
     * @param newPassword the new password of the user
     * @param accessToken token to verify the password
     * @return returns a SimpleAnswer with true or a SimpleAnswer holding false + a reason in case of an invalid refreshToken
     */
    public static SimpleAnswer changePassword(String oldPassword, String newPassword, String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        if (checkAccessToken(accessToken).getSuccess()) {
            try {
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                User u = uS.getUser();
                if(u.getLoginCounter() >= 10){
                    return new SimpleAnswer(false, "Entered invalid password too often");
                }
                if(u.createSHA1(oldPassword, u.getSalt()).equals(u.getPassword())){
                Date d = new Date();
                u.setSalt(Long.toString(d.getTime()));
                u.setPassword(u.createSHA1(newPassword, u.getSalt()));
                u.setChangePasswordCounter(0);
                template.save(u);
                return new SimpleAnswer(true);
                }
                u.setChangePasswordCounter(u.getChangePasswordCounter()+1);
                template.save(u);
                return new SimpleAnswer(false, "Invalid old password. Number of tries lieft: "+ (10-u.getChangePasswordCounter()));
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
                    try{
                        FirebaseToken token = template.loadByProperty(FirebaseToken.class, "token", fireBaseToken);
                        token.setUser(c);
                        template.save(token);
                        return new SimpleAnswer(true);
                    }catch (NotFoundException nfe ) {


                        FirebaseToken fT = new FirebaseToken(fireBaseToken, c);
                        if (c.getFirebaseToken() != null) {
                            return new SimpleAnswer(false, "A FirebaseToken for this Cookie is already stored");
                        }
                        template.save(fT);
                        return new SimpleAnswer(true);
                    }
                }catch (NotFoundException nfe2){
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
            //Timestamp to check for expired Goodybags
            long timestamp = d.getTime();
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
                        if (((Goodybag) entry.getValue()).getDeliverTime()+43200000 < timestamp &&( ((Goodybag) entry.getValue()).getStatus().equals("Not Accepted")|| ((Goodybag) entry.getValue()).getStatus().equals("Accepted"))) {
                            template.query("match (g:Goodybag) where g.goodybagID = \'"+((Goodybag)entry.getValue()).getGoodybagID()+"\' set g.status = \'Expired\'", Collections.EMPTY_MAP, false);
                            i--;
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
                i++;
            }
            return goodybags;
        }
        return new ArrayList<>();
    }

    /**
     * Function to return the number of finished Goodybags. User is identified by the accessToken.
     * @param accessToken token to verfiy the unique user
     * @return returns the number of overall finished Goodybags
     */
    public static int getNumberOfFinishedGoodybags(String accessToken){
        if(checkAccessToken(accessToken).getSuccess()){
            Neo4jTemplate template = main.createNeo4JTemplate();
            int counter = 0;
            ArrayList<Goodybag> gBs = template.loadByProperty(UserSession.class,"accessToken", accessToken, 3).getUser().getGoodybags();
            for (int i = 0; i < gBs.size();i++){
                if (gBs.get(i).getStatus().equals("Done")){
                    counter++;
                }
            }
            return counter;
        }
    return -1;
    }


    /**
     * Function to rate a user and to set a goodybags status to "Done".
     * @param goodybagID the ID of the finished goodybag
     * @param rating number between 1 and 5 to rate the shopper of the goodybag.
     * @return A SimpleAnswer holding false + reason if the goodybags status is already set on "Done" or if the goodybag does not exist
     * A Simple Answer holding true if everything worked.
     */
    public static SimpleAnswer finishGoodybag(String goodybagID, int rating, boolean creatorRates, String accessToken) {
        if (checkAccessToken(accessToken).getSuccess()) {
            Neo4jTemplate template = main.createNeo4JTemplate();
            Result result;
            double userRating = 0;
            int cumulatedRating = 0;
            int numberOfRatings = 0;
            long userID = 0;

            String query1 =" match (ft:FirebaseToken)-[:COOKIE]-(c:Cookie)-[:USER]-(u:User)-[:MATCHED_TO]-(gb:Goodybag{goodybagID:\'"+goodybagID+"\'})-[:USER]-(n:User)-[:USER]-(us:UserSession{accessToken:\'" + accessToken + "\'}) set gb.status = \'Done\' return u, ft";
            String query2 =" match (ft:FirebaseToken)-[:COOKIE]-(c:Cookie)-[:USER]-(u:User)-[:OWNS]-(gb:Goodybag{goodybagID:\'"+goodybagID+"\'})-[:MATCHED_TO]-(n:User)-[:USER]-(us:UserSession{accessToken:\'" + accessToken + "\'}) set gb.status = \'Done\' return u, ft";


            if(creatorRates){
                 result = template.query(query1, Collections.EMPTY_MAP, false);
            }else{
                 result = template.query(query2, Collections.EMPTY_MAP, false);
            }

            Iterator<Map<String, Object>> iterator = result.iterator();
            while (iterator.hasNext()) {
                boolean j = true;
                Map<String, Object> map = iterator.next();
                //noinspection Convert2streamapi
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getValue() instanceof User){
                        if(rating ==-1){
                            continue;
                        }
                        cumulatedRating = ((User) entry.getValue()).getCumulatedRatings()+rating;
                        numberOfRatings = ((User) entry.getValue()).getNumberOfRatings()+1;
                        userRating = (double)(cumulatedRating)/numberOfRatings;
                        userID = ((User) entry.getValue()).getUserID();
                    }
                    if(entry.getValue() instanceof FirebaseToken){
                        org.json.JSONObject notification = new org.json.JSONObject();
                        org.json.JSONObject message = new org.json.JSONObject();
                        org.json.JSONObject body = new org.json.JSONObject();
                        body.put("body", "Du wurdest bewertet");
                        message.put("overallRating", userRating);
                        message.put("rating", rating);
                        notification.put("notification", body);
                        notification.put("data", message);
                        notification.put("to", ((FirebaseToken) entry.getValue()).getToken());
                        HttpClient httpClient = HttpClientBuilder.create().build();
                        System.out.println(notification.toString());
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
            if(rating >= 0 && rating <= 5) {
                template.query("match(n:User) where n.userID=" + userID + " set n.rating = " + userRating + " set n.cumulatedRatings =" + cumulatedRating + " set n.numberOfRatings = " + numberOfRatings, Collections.EMPTY_MAP, false);
            }
            return new SimpleAnswer(true);
        }else {
            return new SimpleAnswer(false, "Invalid Accesstoken");
        }
    }

    /**
     * Function to get a single Goodybag from the Database.
     * @param goodybagID ID of the Goodybag that shall be returned. Should consist of 5 characters / numbers.
     * @param accessToken toke to verify the unique user
     * @return returns a single Goodybag identified by its ID.
     */
    public static Goodybag getGoodybagbyID(String goodybagID, String accessToken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        Goodybag gB = new Goodybag();
        if (checkAccessToken(accessToken).getSuccess()){
            Result r = template.query("match (u:User)-[:OWNS]-(m:Goodybag) where m.goodybagID = \'"+goodybagID+"\' and m.status = \'Not Accepted\' MATCH(m:Goodybag)-[:DELIVER_LOCATION]-(k:GeoLocation) MATCH(m:Goodybag)-[:SHOP_LOCATION]-(r:GeoLocation) return m, k,r, u", Collections.EMPTY_MAP, false);
            Iterator<Map<String, Object>> iterator = r.iterator();
            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                boolean j = true;
                Map<String, Object> i = iterator.next();
                for (Map.Entry<String, Object> entry : i.entrySet()) {
                    if(entry.getValue() instanceof  Goodybag){
                         gB = (Goodybag)entry.getValue();
                    }
                    if (entry.getValue() instanceof GeoLocation && j) {
                        gB.setDeliverLocation((GeoLocation) entry.getValue());
                        j = false;
                        continue;
                    }
                    if (entry.getValue() instanceof GeoLocation && !j) {
                        gB.setShopLocation((GeoLocation) entry.getValue());
                    }
                    if(entry.getValue() instanceof  User){
                        ((User) entry.getValue()).setPassword("");
                        ((User) entry.getValue()).setSalt("");
                        ((User) entry.getValue()).setEmailAddress("");
                        gB.setUser((User)entry.getValue());
                    }
                }
            }
            return gB;
        }
        return null;
    }

    /**
     * Function to get all Goodybags that are matched to a specific user
     * @param accessToken token to verify the unique user.
     * @return returns all Goodybags that are matched to the verified user.
     */
    public static ArrayList<Goodybag> matchedGoodybags(String accessToken) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        Date d = new Date();
        long timestamp = d.getTime();
        Result result = template.query("MATCH(n:UserSession{accessToken:\'" + accessToken + "\'})-[:USER]-(m:User)-[:MATCHED_TO]-(t:Goodybag)-[:OWNS]-(q:User) where t.status in [\'Accepted\',\'Not Accepted\']" +
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
                    if (((Goodybag) entry.getValue()).getDeliverTime()+43200000 < timestamp) {
                        template.query("match (g:Goodybag)-[m:MATCHED_TO]-(n:User) where g.goodybagID = \'"+((Goodybag)entry.getValue()).getGoodybagID()+"\' set g.status = \'Expired\' delete m", Collections.EMPTY_MAP, false);
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
                    ((User) entry.getValue()).setPassword("");
                    ((User) entry.getValue()).setSalt("");
                    ((User) entry.getValue()).setEmailAddress("");
                    goodybags.get(i).setUser((User) entry.getValue());
                }
            }
            i++;
        }
        return goodybags;
    }

    /**
     * Function to accept a single Goodybag after it has been matched to a user.
     * @param goodybagID ID of the single Goodybag that is to be accepted
     * @param accessToken token to verify the unique user.
     * @return changes the state of the Goodybag in the database and returns a SimpleAnswer holding true or a SimpleAnswer holding
     * false + the reason (Incorrect AccessToken).
     */
    public static SimpleAnswer acceptGoodybag(String goodybagID, String accessToken){
        if(checkAccessToken(accessToken).getSuccess()){
            Neo4jTemplate template = main.createNeo4JTemplate();
            Goodybag gB = new Goodybag();
            long userID = 0;

            Result result = template.query("match(u:UserSession)-[:USER]-(n:User)-[:MATCHED_TO]-(gb:Goodybag)-[:USER]-(x:User)-[:USER]-" +
                    "(c:Cookie)-[:COOKIE]-(ft:FirebaseToken) where u.accessToken=\'"+accessToken+"\' and gb.goodybagID=\'"+goodybagID +"\' return gb, ft,n", Collections.EMPTY_MAP, true);

            Iterator<Map<String, Object>> iterator = result.iterator();
            while (iterator.hasNext()) {
                boolean j = true;
                Map<String, Object> map = iterator.next();
                int i = 0;
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
                    }
                    if (entry.getValue() instanceof FirebaseToken) {

                        org.json.JSONObject notification = new org.json.JSONObject();
                        org.json.JSONObject message = new org.json.JSONObject();
                        org.json.JSONObject body = new org.json.JSONObject();
                        body.put("body", "Goodybag akzeptiert");
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


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(entry.getValue() instanceof User){
                        userID = ((User) entry.getValue()).getUserID();
                    }
                }
            }
            template.query("match(n:User)-[p:MATCHED_TO]-(g:Goodybag)where g.goodybagID=\'"+goodybagID+"\' and " +
                    "NOT n.userID = "+userID+" delete p", Collections.EMPTY_MAP, false);
            template.query("match(g:Goodybag) where g.goodybagID=\'"+goodybagID+"\' set g.status = \'Accepted\' ",Collections.EMPTY_MAP,false);
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
    public static void matching(ArrayList<String> userIDs, String goodybagID){
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
        String query2= "merge(gb:Goodybag{goodybagID:\'"+goodybagID+"\'})";
        while (iterator.hasNext()) {
            Map<String, Object> i = iterator.next();
            for (Map.Entry<String, Object> entry : i.entrySet()) {
                if (entry.getValue() instanceof FirebaseToken) {
                    firebaseToken = ((FirebaseToken) entry.getValue()).getToken();
                }
                if (entry.getValue() instanceof Cookie) {

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
                    body.put("body", "Passender Goodybag gefunden");
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
                        System.out.println(notification.toString());
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

    public static void dummyMatching(String goodybagID){
        Neo4jTemplate template = main.createNeo4JTemplate();
        template.query("match(m:User)-[:OWNS]-(g:Goodybag) where g.goodybagID = \'"+goodybagID+"\' match(r:User) where not r = m merge (g)-[p:MATCHED_TO]->(r)", Collections.EMPTY_MAP, false);

    }


}