package caprita.catalin.cityquestbackend.security.service.jwt;

import caprita.catalin.cityquestbackend.controllers.dto.user.LoginSuccessDto;
import caprita.catalin.cityquestbackend.controllers.dto.user.UserLoginDto;
import caprita.catalin.cityquestbackend.security.service.UserPrincipal;
import caprita.catalin.cityquestbackend.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenUtil jwtTokenUtil;
    public static final String RESPONSE_TEMPLATE_STRING = "id: %d";
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JwtTokenUtil jwtTokenUtil,
                                   UserService userService,
                                   ModelMapper modelMapper,
                                   ObjectMapper objectMapper) {
        super(authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/api/users/login");
    }

    /* Trigger when we issue POST request to /login
            We also need to pass in {"username":"dan", "password":"dan123"} in the request body
             */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // Grab credentials and map them to login viewmodel
        UserLoginDto credentials = null;
        try {
            credentials = new ObjectMapper().readValue(request.getInputStream(), UserLoginDto.class);
        } catch (IOException e) {
            LOGGER.error("Failed to perfomr auth: {}", e.getMessage());
        }

        // Create login token
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(),
                credentials.getPassword(),
                List.of());

        // Authenticate user
        Authentication auth = getAuthenticationManager().authenticate(authenticationToken);
        return auth;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // Grab principal
        UserPrincipal principal = ((UserPrincipal) authResult.getPrincipal());
        try {
            LoginSuccessDto loginDto = new LoginSuccessDto();

            loginDto.setId(principal.getId());
            loginDto.setUsername(principal.getUsername());
            // Create JWT Token
            String token = jwtTokenUtil.generateAccessToken(principal);
            // Add token in response
            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " +  token);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            loginDto.setToken(token);

            String detailsJson = objectMapper.writeValueAsString(loginDto);

            response.getOutputStream().write(detailsJson.getBytes(StandardCharsets.UTF_8));
            response.getOutputStream().flush();

        }catch (Exception ex ){
            LOGGER.error("Could not continue Successful Authentication : {}",ex.getMessage());
            throw new IOException();
        }

    }



}
