package Tests;

import config.DatabaseOperations;
import junit.framework.TestCase;
import requestAnswers.SimpleAnswer;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Felix Hambrecht on 04.09.2016.
 * Package: Tests
 * Project: OGMNeo4jMTP
 */
public class UpdateProfileTest extends TestCase {

    //Choose the Values of a User you want to test
    public static String testAccessToken = "au6xXNUlRNlF1P3qqQWuSRSmSp38o3SiONTzhWDxfXye2ISuhBoObflNHnMT31AJRD8PsW";
    // username of the user
    public static String testUsername = "Mustermann";
    //email of the user
    public static String testEmail ="Mustermann@gmx.de";
    //email of another existing user (testing for duplicate mails)
    public static String staticExistingEmail = "FelixHambrecht@gmx.de";

    public UpdateProfileTest(String name){
        super(name);
    }

    public void testUpdateProfile() {
        DatabaseOperations db = new DatabaseOperations();
        SecureRandom random = new SecureRandom();
        SimpleAnswer simpleAnswer;
        //Tested Values: Empty, existing, random
        String username;
        //Tested Values: Empty, existing, random
        String email;
        //Tested Values: existing, not existing
        String accessToken;



            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    for (int h = 0; h < 2; h++) {
                        switch (i) {
                            case 0: {
                                username = "";
                                switch (j) {
                                    case 0: {
                                        email = "";
                                        switch (h) {
                                            case 0: {
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                        }
                                            case 1: {
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Update complete:  Username: No changes - Email: No changes", simpleAnswer.getReason());
                                            }
                                        }
                                    }
                                    case 1: {
                                        email = testEmail;
                                        switch (h) {
                                            case 0: {
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Update complete:  Username: No changes - Email: No changes", simpleAnswer.getReason());
                                            }
                                        }
                                    }
                                    case 2: {
                                        email = staticExistingEmail;
                                        switch (h) {
                                            case 0: {
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Email already exists. Choose another one", simpleAnswer.getReason());
                                            }
                                        }
                                    }
                                    case 3: {
                                        switch (h) {
                                            case 0: {
                                                email = new BigInteger(130, random).toString(32);
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                email = new BigInteger(130, random).toString(32);
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Update complete:  Username: No changes - Email: " + email, simpleAnswer.getReason());
                                                email = testEmail;
                                                db.updateProfile(username, email, accessToken);
                                            }
                                        }
                                    }
                                }
                            }
                            case 1: {
                                username = testUsername;
                                switch (j) {
                                    case 0: {
                                        email = "";
                                        switch (h) {
                                            case 0: {
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Update complete:  Username: No changes - Email: No changes", simpleAnswer.getReason());
                                            }
                                        }
                                    }
                                    case 1: {
                                        email = testEmail;
                                        switch (h) {
                                            case 0: {
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Update complete:  Username: No changes - Email: No changes", simpleAnswer.getReason());
                                            }
                                        }
                                    }
                                    case 2: {
                                        email = staticExistingEmail;
                                        switch (h) {
                                            case 0: {
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Email already exists. Choose another one", simpleAnswer.getReason());
                                            }
                                        }
                                    }
                                    case 3: {
                                        switch (h) {
                                            case 0: {
                                                email = new BigInteger(130, random).toString(32);
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                email = new BigInteger(130, random).toString(32);
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Update complete:  Username: No changes" + " - Email: " + email, simpleAnswer.getReason());
                                                email = testEmail;
                                            }
                                            db.updateProfile(username, email, accessToken);
                                        }
                                    }
                                }
                            }
                            case 2: {
                                switch (j) {
                                    case 0: {
                                        email = "";
                                        switch (h) {
                                            case 0: {
                                                accessToken = "";
                                                username = new BigInteger(130, random).toString(32);
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                username = new BigInteger(130, random).toString(32);
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Update complete:  Username: " + username + " - Email: No changes", simpleAnswer.getReason());
                                                username = testUsername;
                                                db.updateProfile(username, email, accessToken);
                                            }
                                        }
                                    }
                                    case 1: {
                                        email = testEmail;
                                        switch (h) {
                                            case 0: {
                                                username = new BigInteger(130, random).toString(32);
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                username = new BigInteger(130, random).toString(32);
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Update complete:  Username: " + username + " - Email: No changes", simpleAnswer.getReason());
                                                username = testUsername;
                                                db.updateProfile(username, email, accessToken);
                                            }
                                        }
                                    }
                                    case 2: {
                                        email = staticExistingEmail;
                                        switch (h) {
                                            case 0: {
                                                username = new BigInteger(130, random).toString(32);
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                username = new BigInteger(130, random).toString(32);
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Email already exists. Choose another one", simpleAnswer.getReason());
                                                username = testUsername;
                                                db.updateProfile(username, email, accessToken);
                                            }
                                        }
                                    }
                                    case 3: {
                                        switch (h) {
                                            case 0: {
                                                email = new BigInteger(130, random).toString(32);
                                                username = new BigInteger(130, random).toString(32);
                                                accessToken = "";
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Invalid Accesstoken, refreshToken required", simpleAnswer.getReason());
                                            }
                                            case 1: {
                                                email = new BigInteger(130, random).toString(32);
                                                username = new BigInteger(130, random).toString(32);
                                                accessToken = testAccessToken;
                                                simpleAnswer = db.updateProfile(username, email, accessToken);
                                                assertEquals("Update complete:  Username: " + username + " - Email: " + email, simpleAnswer.getReason());
                                                email = testEmail;
                                                username = testUsername;
                                                db.updateProfile(username, email, accessToken);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }

    }
    public static void main(String[] args){
        junit.textui.TestRunner.run(UpdateProfileTest.class);
    }
}
