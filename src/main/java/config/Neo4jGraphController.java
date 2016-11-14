package config;

import Interfaces.DBController;
import entities.*;
import org.neo4j.ogm.model.Result;
import org.springframework.data.neo4j.template.Neo4jTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Felix on 10.11.2016.
 * Package: config
 * Project: OGMNeo4jMTP
 */
public class Neo4jGraphController implements DBController {

    private static Main main = new Main();

    @Override
    public void addSpot(Spot spot) {
        Neo4jTemplate template = main.createNeo4JTemplate();

        template.query("match(n:Spot)", Collections.EMPTY_MAP,false);
    }

    @Override
    public void removeSpot(Spot spot) {
        Neo4jTemplate template = main.createNeo4JTemplate();


    }

    @Override
    public ArrayList<Spot> getSpots(float latitude, float longitude) {
        Neo4jTemplate template = main.createNeo4JTemplate();

        Result r = template.query("match(n:Spot) where distance(point(n),point({latitude:"+latitude+", longitude:"+longitude+"}))<50 return n", Collections.EMPTY_MAP, false);
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
            return null;
        }
        return spots;
    }

    @Override
    public Spot getSpot(int spotID, GPS_plus point) {
        Neo4jTemplate template = main.createNeo4JTemplate();

        return null;
    }

    @Override
    public Spot getSpot(long spotID, float latitude, float longitude) {
        Neo4jTemplate template = main.createNeo4JTemplate();


        return null;
    }

    @Override
    public ArrayList<Spot> getSpots(int arg1, int arg2, double radius) {
        Neo4jTemplate template = main.createNeo4JTemplate();

        return null;
    }
}
