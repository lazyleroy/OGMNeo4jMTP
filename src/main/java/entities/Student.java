package entities;

/**
 * Created by Felix on 05.07.2016.
 */
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Student extends BaseModel{

    private String name;

    @Relationship(type = "ENROLLED", direction = Relationship.OUTGOING)
    private Set<Course> courses = new HashSet<Course>();
    @Relationship(type = "KNOWS", direction = Relationship.UNDIRECTED)
    private Set<Student> knownStudents = new HashSet<Student>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    public Set<Student> getknownStudents(){ return knownStudents;}

    public void setKnownStudents(Set<Student> knownStudents){this.knownStudents = knownStudents; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Student other = (Student) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
