package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */



import org.neo4j.ogm.config.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.neo4j.ogm.session.SessionFactory;




/**
 * This class holds the complete configuration of the program. It configures the database IP + credentials + Driver mode.
 * In addition it enables Springs AutoConfiguration. From here on Spring takes over the configuration of FileUpload and
 * the RESTful WebService
 *
 */

@EnableWebMvc
@EnableAutoConfiguration
@EnableTransactionManagement
@PropertySources(value = {@PropertySource(value = "classpath:application.properties")})
@SpringBootApplication
public class MyConfiguration{


    @Bean
    protected org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config
                .driverConfiguration()

                  //.setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
                //.setURI("http://neo4j:mtp123456@localhost:7474");
                //.setURI("bolt://neo4j:password@localhost")
                //.setEncryptionLevel("NONE")
                //.setTrustStrategy("TRUST_ON_FIRST_USE")
                //.setTrustCertFile("/tmp/cert")
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
                .setURI("file:/home/ines/neo4j-community-3.1.1/data/databases/routes.db")
                ;

        return config;
    }

    @Bean
    public SessionFactory getSessionFactory(){
        //return new SessionFactory("entities");
        return new SessionFactory(getConfiguration(), "entities");
    }


/*
    @Bean
    protected EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory =
                new TomcatEmbeddedServletContainerFactory();
        return factory;
    }*/

}
