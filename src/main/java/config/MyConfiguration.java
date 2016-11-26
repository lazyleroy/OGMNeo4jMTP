package config;

/**
 * Created by Felix Hambrecht on 05.07.2016.
 */

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.neo4j.ogm.session.SessionFactory;


/**
 * This class holds the complete configuration of the program. It configures the database IP + credentials + Driver mode.
 * In addition it enables Springs AutoConfiguration. From here on Spring takes over the configuration of FileUpload and
 * the RESTful WebService
 *
 */

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@EnableTransactionManagement
@PropertySources(value = {@PropertySource(value = "classpath:application.properties")})
public class MyConfiguration extends Neo4jConfiguration {


    @Bean
    protected org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config
                .driverConfiguration()
                //.setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
                //.setURI("http://neo4j:mtp123456@134.155.48.48:7474");
                .setURI("file:/C:/Users/Hambe//Documents/Neo4j/default.graphdb");

        return config;
    }
    
    @Bean
    public SessionFactory getSessionFactory(){
        return new SessionFactory(getConfiguration(), "entities");
    }

    @Bean
    protected EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory =
                new TomcatEmbeddedServletContainerFactory();
        return factory;
    }

}
