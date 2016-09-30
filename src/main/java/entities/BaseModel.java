package entities;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by Felix Hambrecht on 11.07.2016.
 */
@NodeEntity
public class BaseModel {

    public BaseModel(){
    }

    @GraphId
    private Long id;

    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
