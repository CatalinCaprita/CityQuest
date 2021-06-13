package caprita.catalin.cityquestbackend.config;

import caprita.catalin.cityquestbackend.security.service.JPAUserDetailsService;
import caprita.catalin.cityquestbackend.security.service.jwt.JwtAuthenticationFilter;
import caprita.catalin.cityquestbackend.security.service.jwt.JwtAuthorizationFilter;
import caprita.catalin.cityquestbackend.security.service.jwt.JwtTokenUtil;
import caprita.catalin.cityquestbackend.services.user.UserService;
import caprita.catalin.cityquestbackend.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JPAUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public SecurityConfig(JPAUserDetailsService userDetailsService,
                          JwtTokenUtil jwtTokenUtil,
                          UserService userService,
                          ModelMapper modelMapper,
                          ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }


    @Bean
    @Lazy
    public PasswordEncoder passwordEncoder(){
        int strength = 10; // work factor of bcrypt
        return new BCryptPasswordEncoder(strength, new SecureRandom());
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(this.userDetailsService);
        return provider;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());

    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
//        Enable CORS and DISABLE csrf
        http = http.cors().and().csrf().disable();
//        Set the session to stateless
        http = http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
//        Add Authentication and Authorization filters
        http = http
                .addFilter(new JwtAuthenticationFilter(authenticationManager(),jwtTokenUtil,userService,modelMapper, objectMapper))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(),jwtTokenUtil,userDetailsService))
                .authorizeRequests()
                .antMatchers("/api/users/login").permitAll()
                .antMatchers(HttpMethod.POST,"/api/users/register/**").permitAll()
                .antMatchers(HttpMethod.PATCH,"/api/users/register/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/users/register/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
        .formLogin().disable();
    }

    @Bean
    public CorsFilter corsFilter() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }


//    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }


}
