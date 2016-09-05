package Tests;

import config.DatabaseOperations;
import entities.User;
import junit.framework.TestCase;
import requestAnswers.RegisterAnswer;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Felix on 04.09.2016.
 * Package: Tests
 * Project: OGMNeo4jMTP
 */
public class RegisterTest extends TestCase {

    public RegisterTest(String name){
        super(name);
    }

    public void testRegister(){
        DatabaseOperations db = new DatabaseOperations();
        db.register(new User("abc", "def","1234"),"def", "asdasd");
        SecureRandom random = new SecureRandom();
        User u = new User("","","");
        RegisterAnswer registerAnswer;
        //Empty, existing, random
        String username="";
        //Empty, existing, random
        String email="";
        //Empty, some
        String password="";
        //Empty, some
        String firebaseToken = "";

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                for(int h = 0; h < 2; h++){
                    for (int r = 0; r < 2; r++){
                        switch (i){
                            case 0: {
                                username = "";
                                switch (j) {
                                    case 0: {
                                        email = "";
                                        switch (h) {
                                            case 0: {
                                                password = "";
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                    case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                }
                                            }
                                            case 1: {
                                                password = new BigInteger(130, random).toString(32);
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                    case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                }
                                            }
                                        }
                                    }case 1: {
                                        email = "def";
                                        switch (h) {
                                            case 0: {
                                                password = "";
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                    case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }

                                                }
                                            }
                                            case 1: {
                                                password = new BigInteger(130, random).toString(32);
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                    case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                }
                                            }
                                        }
                                    }case 2:{
                                        email = new BigInteger(130, random).toString(32);
                                        switch (h) {
                                            case 0: {
                                                password = "";
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                }
                                            }case 1: {
                                                password = new BigInteger(130, random).toString(32);
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                    case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }

                            }case 1: {
                                username = "abc";
                                switch (j) {
                                    case 0: {
                                        email = "";
                                        switch (h) {
                                            case 0: {
                                                password = "";
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }
                                                }
                                            }case 1: {
                                                password = new BigInteger(130, random).toString(32);
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Empty username or email");
                                                    }

                                                }
                                            }
                                        }
                                    }case 1: {
                                        email = "def";
                                        switch (h) {
                                            case 0: {
                                                password = "";
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Emailadress already exists");
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Emailadress already exists");
                                                    }
                                                }
                                            }case 1: {
                                                password = new BigInteger(130, random).toString(32);
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Emailadress already exists");
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Emailadress already exists");
                                                    }
                                                }
                                            }
                                        }
                                    }case 2:{
                                        switch (h) {
                                            case 0: {
                                                password = "";
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }case 1: {
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }
                                                }
                                            }case 1: {
                                                password = new BigInteger(130, random).toString(32);
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                            }case 2: {
                                username = new BigInteger(130, random).toString(32);
                                switch (j) {
                                    case 0: {
                                        email = "";
                                        switch (h) {
                                            case 0: {
                                                password = "";
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }
                                                    case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }
                                                }
                                            }
                                            case 1: {
                                                password = new BigInteger(130, random).toString(32);
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }
                                                }
                                            }
                                        }
                                    }case 1: {
                                        email = "def";
                                        switch (h) {
                                            case 0: {
                                                password = "";
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Emailadress already exists");
                                                    }
                                                    case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Emailadress already exists");
                                                    }
                                                }
                                            }
                                            case 1: {
                                                password = new BigInteger(130, random).toString(32);
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Emailadress already exists");
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getReason(), "Emailadress already exists");
                                                    }
                                                }
                                            }
                                        }
                                    }case 2: {
                                        switch (h) {
                                            case 0: {
                                                password = "";
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }
                                                }
                                            }case 1: {
                                                password = new BigInteger(130, random).toString(32);
                                                switch (r) {
                                                    case 0: {
                                                        firebaseToken = "";
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
                                                    }case 1: {
                                                        firebaseToken = new BigInteger(130, random).toString(32);
                                                        email = new BigInteger(130, random).toString(32);
                                                        u = new User(username, email, password);
                                                        registerAnswer = db.register(u, email, firebaseToken);
                                                        assertEquals(registerAnswer.getSuccess(), true);
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
            }
        }

    }

    public static void main(String[] args){
        junit.textui.TestRunner.run(RegisterTest.class);
    }

}
