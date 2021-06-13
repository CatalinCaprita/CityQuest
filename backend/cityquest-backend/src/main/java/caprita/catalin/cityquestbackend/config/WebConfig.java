package caprita.catalin.cityquestbackend.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig{

    @Value("${SELF_PORT}")
    private String port;

    @Bean
    @Primary
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

//    @Bean
//    public ServletWebServerFactory servletContainer(){
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory(){
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint contstraint = new SecurityConstraint();
//                contstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                contstraint.addCollection(collection);
//                context.addConstraint(contstraint);
////                super.postProcessContext(context);
//            }
//        };
//        factory.addAdditionalTomcatConnectors(httpToHttpsConnector());
//        return factory;
//    }
//
//    /*Basic Tomcat connector that will enable the HTTP port mapping to HTTPS. Any request intercepted by
//    * this container for port 8081 will be redirected to app's port
//    * */
//    @Bean
//    public Connector httpToHttpsConnector(){
//        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
//        connector.setScheme("http");
//        connector.setPort(8081);
//        connector.setSecure(false);
//        try {
//            int port = Integer.parseInt(this.port);
//            connector.setRedirectPort(port);
//        }catch (NumberFormatException e){
//            throw e;
//        }
//        return connector;
//    }
}
