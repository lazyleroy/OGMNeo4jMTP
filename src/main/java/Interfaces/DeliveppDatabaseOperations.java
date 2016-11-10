package Interfaces;

import entities.GeoLocation;
import entities.Goodybag;
import entities.User;
import org.springframework.web.multipart.MultipartFile;
import requestAnswers.*;

import java.util.ArrayList;

/**
 * Created by Felix on 10.11.2016.
 * Package: Interfaces
 * Project: OGMNeo4jMTP
 */
public interface DeliveppDatabaseOperations {


    RegisterAnswer register(User user, String emailAddress, String firebaseToken);

    SimpleAnswer checkAccessToken(String accesstoken);

    LoginAnswer emailLogin(String email, String password, String firebaseToken);

    RegisterAnswer refreshTokenLogin(String refreshToken, int clientID, String clientSecret);

    SimpleAnswer updateProfile(String userName, String email, String accessToken);

    SimpleAnswer uploadGoodybag(String title, String status, String description,
                                double tip, long deliverTime, GeoLocation deliverLocation,
                                GeoLocation shopLocation, int checkOne, int checkTwo, String accessToken);

    SimpleAnswer uploadProfilePicture(MultipartFile file, String accessToken);

    SimpleAnswer changePassword(String oldPassword, String newPassword, String accessToken);

    SimpleAnswer storeFirebaseToken(String refreshToken, String fireBaseToken);

    ArrayList<Goodybag> myGoodybags(String accessToken);

    int getNumberOfFinishedGoodybags(String accessToken);

    SimpleAnswer finishGoodybag(String goodybagID, int rating, boolean creatorRates, String accessToken);

    Goodybag getGoodybagbyID(String goodybagID, String accessToken);

    ArrayList<Goodybag> matchedGoodybags(String accessToken);

    SimpleAnswer acceptGoodybag(String goodybagID, String accessToken);

    void matching(ArrayList<String> userIDs, String goodybagID);

}
