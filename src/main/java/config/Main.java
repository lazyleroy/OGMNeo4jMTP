package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */
import java.util.Collections;

import entities.User;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import entities.Course;
import entities.Student;

public class Main {

    public static void main(String[] args) {
        MyConfiguration x = new MyConfiguration();
        // Create SessionFactory. Pass the package name of the entity classes as the argument. Pass
        //Configuration as first Argument --> Both done in MyConfiguration
        SessionFactory sessionFactory = x.getSessionFactory();
        // Create the session
        Session session = sessionFactory.openSession();

        // Create a User

        User u = new User("Leroy", "leroy@gmx.de","testPasswort");

        // Create few courses
        Course oop = new Course();
        oop.setName("Object Oriented Programming");
        oop.setCredits(2.0f);

        Course algo = new Course();
        algo.setName("Advanced Algorithm");
        algo.setCredits(3.0f);

        Course db = new Course();
        db.setName("Database Internals");
        db.setCredits(3.0f);

        // Create few students
        Student alice = new Student();
        alice.setName("Alice");

        Student bob = new Student();
        bob.setName("Bob");

        Student carol = new Student();
        carol.setName("Carol");

        // Add the courses
        alice.getCourses().add(oop);
        alice.getCourses().add(algo);
        alice.getCourses().add(db);

        alice.getknownStudents().add(bob);

        bob.getCourses().add(oop);
        bob.getCourses().add(algo);
        bob.getknownStudents().add(alice);

        carol.getCourses().add(algo);
        carol.getCourses().add(db);



        // Persist the objects. Persisting students persists courses as well.
        session.save(alice);
        session.save(bob);
        session.save(carol);

        // Retrieve Students who enrolled for Advanced Algorithm
        Iterable<Course> courses = session.loadAll(Course.class);
        Iterable<Student> students = session.query(Student.class,
                "MATCH (c:Course)<-[:ENROLLED]-(student) WHERE c.name = 'Advanced Algorithm' RETURN student",
                Collections.<String, Object> emptyMap());

        // Print all the Students
        for (Student stu : students) {
            System.out.println(stu.getName());
        }
        for (Course cts : courses){
            System.out.println(cts.getName());
        }

    }


}
