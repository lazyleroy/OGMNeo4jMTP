package Tests;

import config.DatabaseOperations;
import entities.GeoLocation;
import junit.framework.TestCase;
import requestAnswers.SimpleAnswer;

import java.util.Date;

/**
 * Created by Felix on 08.09.2016.
 * Package: Tests
 * Project: OGMNeo4jMTP
 */
public class UploadGoodybagTest extends TestCase{
    DatabaseOperations db = new DatabaseOperations();
    SimpleAnswer simpleAnswer;

    //Possible values: random or empty
    String title;
    //Possible values: < 0 or >= 0
    double tip;
    // < current time or >= current Time
    long deliverTime;

    int checkOne = 1234;
    int checkTwo = 4321;
    //Correct or incorrect
    String accessToken;
    //REQUIRED VALUES FOR SUCCESSFUL UPLOAD!
    //Possible Values: null or Object
    GeoLocation deliverLocation;
    //Possible Values: null or Object
    GeoLocation shopLocation;
    //Possible Values: something or empty
    String description;


    //TESTED VALUES - May not be null or empty. Checks for null and empty values are performed automatically.
    String testedTitle = "Ein Titel";
    double testedTip = 15.42;
    long testedDeliverTime = new Date().getTime()+ 200000;
    String testedAccessToken = "gNybE0iWmZEvDilnZ96wQ1b75GIXNiwjOxPTLmTkZxu3prtDnZR9sbNMYdSS3a5P9Zf1jB";
    GeoLocation testedDeliverLocation = new GeoLocation(123.23, 12353.12, "Deliver","Location");
    GeoLocation testedShopLocation = new GeoLocation(213141.123, 12351.98, "Shop", "Location");
    String testedDescription = "Bring some Milk!";



    public UploadGoodybagTest(String name){
        super(name);
    }

    public void testUploadGoodybag() {

        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                for(int k = 0; k < 2; k++){
                    for(int h = 0; h < 2; h++){
                        for(int m = 0; m < 2; m++){
                            for (int n = 0; n < 2; n++){
                                for (int r = 0; r < 2; r++){
                                    switch(i){
                                        case 0:{
                                            title = "";
                                            switch(j){
                                                case 0:{
                                                    tip = -1;
                                                    switch(k){
                                                        case 0:{
                                                            deliverTime = 2;
                                                            switch(h){
                                                                case 0:{
                                                                    accessToken = "";
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                case 1:{
                                                                    accessToken = testedAccessToken;
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        case 1:{
                                                            deliverTime = testedDeliverTime;
                                                            switch(h){
                                                                case 0:{
                                                                    accessToken = "";
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                case 1:{
                                                                    accessToken = testedAccessToken;
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
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
                                                case 1:{
                                                    tip = testedTip;
                                                    switch(k){
                                                        case 0:{
                                                            deliverTime = 2;
                                                            switch(h){
                                                                case 0:{
                                                                    accessToken = "";
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                case 1:{
                                                                    accessToken = testedAccessToken;
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals(true, startUpload().getSuccess());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        case 1:{
                                                            deliverTime = testedDeliverTime;
                                                            switch(h){
                                                                case 0:{
                                                                    accessToken = "";
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                case 1:{
                                                                    accessToken = testedAccessToken;
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals(true, startUpload().getSuccess());
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
                                        case 1:{
                                            title = testedTitle;
                                            switch(j){
                                                case 0:{
                                                    tip = -1;
                                                    switch(k){
                                                        case 0:{
                                                            deliverTime = 2;
                                                            switch(h){
                                                                case 0:{
                                                                    accessToken = "";
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                case 1:{
                                                                    accessToken = testedAccessToken;
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        case 1:{
                                                            deliverTime = testedDeliverTime;
                                                            switch(h){
                                                                case 0:{
                                                                    accessToken = "";
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                case 1:{
                                                                    accessToken = testedAccessToken;
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
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
                                                case 1:{
                                                    tip = testedTip;
                                                    switch(k){
                                                        case 0:{
                                                            deliverTime = 2;
                                                            switch(h){
                                                                case 0:{
                                                                    accessToken = "";
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                case 1:{
                                                                    accessToken = testedAccessToken;
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals(true, startUpload().getSuccess());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        case 1:{
                                                            deliverTime = testedDeliverTime;
                                                            switch(h){
                                                                case 0:{
                                                                    accessToken = "";
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Invalid Accesstoken, refreshToken required", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                case 1:{
                                                                    accessToken = testedAccessToken;
                                                                    switch(m){
                                                                        case 0:{
                                                                            deliverLocation = null;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        case 1:{
                                                                            deliverLocation = testedDeliverLocation;
                                                                            switch (n){
                                                                                case 0:{
                                                                                    shopLocation = null;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                    }
                                                                                }
                                                                                case 1:{
                                                                                    shopLocation = testedShopLocation;
                                                                                    switch (r) {
                                                                                        case 0: {
                                                                                            description = "";
                                                                                            assertEquals("Important value missing (description / deliverLocation / shopLocation)", startUpload().getReason());
                                                                                        }
                                                                                        case 1: {
                                                                                            description = testedDescription;
                                                                                            assertEquals(true, startUpload().getSuccess());
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public SimpleAnswer startUpload(){
       return db.uploadGoodybag(title, "", description, tip, deliverTime, deliverLocation, shopLocation, checkOne, checkTwo, accessToken);
    }

    public static void main(String[] args){
        junit.textui.TestRunner.run(UploadGoodybagTest.class);
    }


}
