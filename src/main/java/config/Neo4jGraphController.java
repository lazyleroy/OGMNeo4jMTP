package config;

import Interfaces.DBController;
import com.vividsolutions.jts.geom.Coordinate;
import entities.*;
import org.neo4j.gis.spatial.SimplePointLayer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.pipes.GeoPipeFlow;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.exception.NotFoundException;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.service.Components;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.util.*;

/**
 * Created by Felix on 10.11.2016.
 * Package: config
 * Project: OGMNeo4jMTP
 */
public class Neo4jGraphController implements DBController {

    public static Main getMain() {
        return main;
    }

    private static Main main = new Main();
    private static SessionFactory sessionFactory = new MyConfiguration().getSessionFactory();
    public Session createNeo4JTemplate(){
        return sessionFactory.openSession();
    }

    @Override
    public void addSpot(Spot spot) {
        Date start_time = new Date();
        Session template = this.createNeo4JTemplate();

        String spotID = spot.getSpotID();
        ArrayList<Spot> neighbors = spot.getNeighbors();
        String spotNeighborsQuery = "CREATE (n:Spot{spotID:\'"+spotID+"\', longitude:"+spot.getLongitude()+", latitude:"+spot.getLatitude()+", " +
                "spotHeading:"+spot.getSpotHeading()+", intersection:"+spot.isIntersection()+", numberCenterCalcPoints:"+spot.getNumberCenterCalcPoints()+", " +
                "headSum:"+spot.getHeadSum()+", headCalcPoints:"+spot.getHeadCalcPoints()+", latitudeSum:"+spot.getLatitudeSum()+", " +
                "longitudeSum:"+spot.getLongitudeSum()+", numberOfNeighbours:"+spot.getNeighbors().size()+"}) ";
        for(int i = 0; i < neighbors.size(); i++){
            String neighborID = neighbors.get(i).getSpotID();
            spotNeighborsQuery+= "MERGE (t"+i+":Spot{spotID:\'"+neighborID+"\'})";
        }
        for(int i = 0; i < neighbors.size(); i++){
            spotNeighborsQuery+= "MERGE (n)-[:CONNECTED_WITH]-(t"+i+") ";
        }
        template.query(spotNeighborsQuery, Collections.EMPTY_MAP, false);
        Date stop_time = new Date();
        long time = stop_time.getTime() - start_time.getTime();
        main.setDatabaseTime(main.getDatabaseTime()+time);
    }

    @Override
    public void updateSpot(Spot spot) {
        Date start_time = new Date();
        Session template = this.createNeo4JTemplate();
        Collection<Spot> spots = template.loadAll(Spot.class, new Filter("spotID", spot.getSpotID()));
        Spot s = spots.iterator().next();
        ArrayList<Spot> neighbors = s.getNeighbors();
        s.setNeighbors(null);
        s.setLatitude(spot.getLatitude());
        s.setLongitude(spot.getLongitude());
        s.setSpotHeading(spot.getSpotHeading());
        s.setIntersection(spot.isIntersection());
        s.setNumberCenterCalcPoints(spot.getNumberCenterCalcPoints());
        s.setHeadSum(spot.getHeadSum());
        s.setHeadCalcPoints(spot.getHeadCalcPoints());
        s.setLatitudeSum(spot.getLatitudeSum());
        s.setLongitudeSum(spot.getLongitudeSum());
        s.setNumberOfNeighbours(spot.getNumberOfNeighbours());
        template.save(s);
        s.setNeighbors(neighbors);
        Date stop_time = new Date();
        long time = stop_time.getTime() - start_time.getTime();
        main.setDatabaseTime(main.getDatabaseTime()+(time));

    }

    @Override
    public Spot getSpot(String spotID) {
        Date start_time = new Date();
        Session template = this.createNeo4JTemplate();
        try{
            Spot spot = template.load(Spot.class, spotID);
            if(spot.getNeighbors() == null){
                spot.setNeighbors(new ArrayList<Spot>());
            }
           // System.out.println("getSpot QUERY");
            Date stop_time = new Date();
            long time = stop_time.getTime() - start_time.getTime();
            main.setDatabaseTime(main.getDatabaseTime()+time);
            return spot;
        }catch(NotFoundException nfe){
            Date stop_time = new Date();
            long time = stop_time.getTime() - start_time.getTime();
            main.setDatabaseTime(main.getDatabaseTime()+time);
            return null;
        }
    }

    @Override
    public ArrayList<Spot> getSpots(float latitude, float longitude) {
        Session template = this.createNeo4JTemplate();
        Date start_time = new Date();

        /*EmbeddedDriver embeddedDriver = (EmbeddedDriver) Components.driver();
        GraphDatabaseService databaseService = embeddedDriver.getGraphDatabaseService();

        SpatialDatabaseService spatialDatabaseService = new SpatialDatabaseService(databaseService);

        SimplePointLayer layer = (SimplePointLayer) spatialDatabaseService.getLayer("test-layer");
        System.out.println(layer);
        List<GeoPipeFlow> results = layer.findClosestPointsTo(new Coordinate(longitude, latitude), 0.05);
        for(int i = 0; i < results.size(); i++){
            System.out.println(results.get(i).getGeomNode());
        }*/

        Result r = template.query("match(n:Spot) where distance(point(n),point({latitude:"+latitude+", longitude:"+longitude+"}))<50 return n", Collections.EMPTY_MAP, false);
        //sendQuery("CALL spatial.withinDistance('test-layer',{longitude:"+longitude+",latitude:"+latitude+"},0.05)");
        //Result r = template.query("CALL spatial.withinDistance('spot-layer',{longitude:"+longitude+",latitude:"+latitude+"},0.05)", Collections.EMPTY_MAP, false);
        //Result r = template.query("CALL spatial.procedures", Collections.EMPTY_MAP, false);
        Iterator<Map<String, Object>> result = r.iterator();
        ArrayList<Spot> spots = new ArrayList<>();
        while(result.hasNext()){
            Map<String, Object> map = result.next();
            for(Map.Entry<String, Object> entry : map.entrySet()){
                if(entry.getValue() instanceof Spot){
                    spots.add((Spot)entry.getValue());
                }
            }
        }
        if(spots.isEmpty()){
            Date stop_time = new Date();
            long time = stop_time.getTime() - start_time.getTime();
            main.setDatabaseTime(main.getDatabaseTime()+time);
            return null;
        }

        Date stop_time = new Date();
        long time = stop_time.getTime() - start_time.getTime();
        main.setDatabaseTime(main.getDatabaseTime()+time);

        return spots;
    }

    @Override
    public void addGPSPoints(ArrayList<GPS_plus> gpspoints, String username, ArrayList<String> intersectionSpots) {

        Date start_time = new Date();
        Session template = this.createNeo4JTemplate();
        HashSet<String> intersectionSpotStrings = new HashSet<>();
        Date date = new Date();


        String inList = "p.spotID IN [";
        for(int i = 0; i < intersectionSpots.size(); i++){
            if(i == 0){
                inList += "\'"+intersectionSpots.get(i)+"\' ";
            }else {
                inList += ",\'"+intersectionSpots.get(i)+"\'";
            }
        }
        inList += "]";

        String finalizeQuery = "MATCH (p:Spot)-[:CONNECTED_WITH]-(c:Spot) WITH p,count(c) as rels WHERE rels > 2 AND "+inList+" set p.intersection = true return p";
        Result result = template.query(finalizeQuery, Collections.EMPTY_MAP, false);
        Iterator<Map<String, Object>> iterator = result.iterator();
        while(iterator.hasNext()){
            Map<String, Object> map = iterator.next();
            for(Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof Spot) {
                    intersectionSpotStrings.add(((Spot) entry.getValue()).getSpotID());
                }
            }
        }

        boolean repeatedGPSinSpot = true;
        String gpsPlusID;
        String gpsPlusIDcheck = "";
        Random random = new Random();

        String waypointID ="";

        for(int i = 0; i < gpspoints.size(); i++){
            GPS_plus tempGPS = gpspoints.get(i);
            gpsPlusID = username+tempGPS.getTime()+tempGPS.getLatitude()+tempGPS.getLongitude();
            String gpsPlusQuery = "MERGE (n:User{username:\'"+username+"\'}) \n";

            if(gpspoints.get(i) == null){
                continue;
            }
            if(gpspoints.get(i).getSpot() == null){
                System.out.println("FEHLENDER SPOT");
                continue;
            }

            String spotID = gpspoints.get(i).getSpot().getSpotID();
            gpsPlusQuery +=  "CREATE (GPS_Plus0:GPS_Plus{date:\'"+tempGPS.getTime()+"\', latitude:"+tempGPS.getLatitude()
                    +", longitude:"+tempGPS.getLongitude()+", head:"+tempGPS.getHead()+", gpsPlusID:\'"+gpsPlusID+"\', " +
                    //"speed:"+tempGPS.getSpeed()+"," +
                    " timeDiffToNextPoint:" +
                    tempGPS.getTimediffToNextPoint()+", distanceToNextPoint:"+tempGPS.getTimediffToNextPoint()+", dataID:"+tempGPS.getDataID()+"}) \n";


            gpsPlusQuery += "MERGE (spot:Spot{spotID:\'" + spotID + "\'}) \n";
            //gpsPlusQuery += "call spatial.addNode(\"spot-layer\", spot) YIELD node \n";

            gpsPlusQuery += "CREATE (GPS_Plus0)-[:MAPPED_TO_SPOT]->(spot)";

            if(i==0){
                waypointID =  String.valueOf(date.getTime())+String.valueOf(random.nextLong())+username;
                gpsPlusQuery += "CREATE (n)-[:STARTING_POINT]->(GPS_Plus0) \n";
                gpsPlusQuery += "CREATE (waypoint:Waypoint{waypointID:\'"+waypointID+"\'}) \n CREATE (n)-[:ROUTE_START]->(waypoint) \n";
                gpsPlusQuery += "CREATE (GPS_Plus0)-[:WAYPOINT]->(waypoint) \n";
                //System.out.println("START - WAYPOINT ERSTELLT!");
            }
            else if(intersectionSpotStrings.contains(spotID)){
                if(!repeatedGPSinSpot){
                    String waypointID1 = String.valueOf(date.getTime())+String.valueOf(random.nextLong())+username;

                    gpsPlusQuery += "MERGE (waypoint1:Waypoint{waypointID:'"+waypointID1+"\'}) \n CREATE (GPS_Plus0)-[:WAYPOINT]->(waypoint1) \n";
                    gpsPlusQuery += "MERGE(waypoint0:Waypoint{waypointID:\'" + waypointID + "\'})";
                    gpsPlusQuery += "CREATE (waypoint0)-[:NEXT_WAYPOINT]->(waypoint1) \n ";
                    repeatedGPSinSpot = true;
                    waypointID = waypointID1;

                    //System.out.println("WAYPOINT ERSTELLT!");
                    //System.out.println(waypointID1);
                }
            }else{
                repeatedGPSinSpot = false;
            }
            if(i == gpspoints.size()-1){
                String waypointID2 = String.valueOf(date.getTime())+String.valueOf(random.nextLong())+username;

                gpsPlusQuery += "MERGE (waypoint1:Waypoint{waypointID:'"+waypointID2+"\'}) \n CREATE (GPS_Plus0)-[:WAYPOINT]->(waypoint1) \n";
                gpsPlusQuery += "MERGE(waypoint0:Waypoint{waypointID:\'" + waypointID + "\'})";
                gpsPlusQuery += "CREATE (waypoint0)-[:NEXT_WAYPOINT]->(waypoint1) \n ";

                //System.out.println("ROUTEN ENDE");
            }
            if(i >0){
                gpsPlusQuery += "MERGE (GPS_Plus1:GPS_Plus{gpsPlusID:\'"+gpsPlusIDcheck+"\'})";
                gpsPlusQuery += "CREATE (GPS_Plus0)-[:NEXT_GPS]->(GPS_Plus1) \n";
            }
            gpsPlusIDcheck = gpsPlusID;
            //System.out.println(gpsPlusQuery);

            template.query(gpsPlusQuery, Collections.EMPTY_MAP, false);
            Date stop_time = new Date();
            long time = stop_time.getTime() - start_time.getTime();
            main.setDatabaseTime(main.getDatabaseTime()+time);



        }



    }

    @Override
    public void addNeighbour(String spotID, String updatedSpotID, boolean intersectionCheck, boolean updatedIntersectionCheck){
        Date start_time = new Date();
        Session template = this.createNeo4JTemplate();
        String addQuery = "MATCH (n:Spot{spotID:\'"+spotID+"\'}) MATCH (r:Spot{spotID:\'" +updatedSpotID +"\'}) MERGE (n)-[:CONNECTED_WITH]-(r)";

        template.query(addQuery, Collections.EMPTY_MAP, false);

        Date stop_time = new Date();
        long time = stop_time.getTime() - start_time.getTime();
        main.setDatabaseTime(main.getDatabaseTime()+time);
    }

    @Override
    public void setIntersections(ArrayList<String> spots){
        Date start_time = new Date();
        Session template = this.createNeo4JTemplate();

        String inList = "p.spotID IN [";
        for(int i = 0; i < spots.size(); i++){
            if(i == 0){
                inList += "\'"+spots.get(i)+"\' ";
            }else {
                inList += ",\'"+spots.get(i)+"\' ";
            }
        }
        inList += "]";


        String finalizeQuery = "MATCH (p:Spot)-[:CONNECTED_WITH]-(c:Spot) WITH p,count(c) as rels WHERE rels > 2 AND "+inList+" set p.intersection = true return p";
        Result result = template.query(finalizeQuery, Collections.EMPTY_MAP, false);

        Date stop_time = new Date();
        long time = stop_time.getTime() - start_time.getTime();
        main.setDatabaseTime(main.getDatabaseTime()+time);

    }

    @Override
    public Result sendQuery(String query) {
        Date start_time = new Date();
        Session template = this.createNeo4JTemplate();
        Result r = template.query(query, Collections.EMPTY_MAP, false);
        Date stop_time = new Date();
        long time = stop_time.getTime() - start_time.getTime();
        main.setDatabaseTime(main.getDatabaseTime()+time);
        return r;
    }

}
