package Tests;

import com.google.api.client.util.Data;
import config.DatabaseOperations;
import entities.User;
import junit.framework.TestCase;
import requestAnswers.LoginAnswer;

/**
 * Created by Felix on 04.09.2016.
 * Package: Tests
 * Project: OGMNeo4jMTP
 */
public class LoginTest extends TestCase{

    public LoginTest(String name){
        super(name);
    }

    public void testLogin(){
        DatabaseOperations db = new DatabaseOperations();
        db.register(new User("abc", "def","1234"),"def", "asdasd");
        LoginAnswer l;

        //Existing or not existing
        String email ="";
        // wrong or correct
        String password ="";

        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                switch(i){
                    case 1:{
                        email = "-1";
                        switch(j){
                            case 1: {
                                password = "-1";
                                l = db.emailLogin(email,password,"");
                                assertEquals(l.getReason(),"Email does not exist.");
                            }case 2:{
                                password ="1234";
                                l = db.emailLogin(email,password,"");
                                assertEquals(l.getReason(),"Email does not exist.");
                            }
                        }
                    }
                    case 2:{
                        email = "def";
                        switch(j){
                            case 1: {
                                password = "-1";
                                l = db.emailLogin(email,password,"");
                                assertEquals(l.getReason(),"Invalid password");
                            }case 2:{
                                password ="1234";
                                l = db.emailLogin(email,password,"");
                                assertEquals(l.isSuccess(), true);
                            }
                        }
                    }
                }
            }
        }


    }

    public static void main(String[] args){
        junit.textui.TestRunner.run(LoginTest.class);
    }


}
