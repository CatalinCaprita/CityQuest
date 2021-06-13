package caprita.catalin.cityquestbackend.security.service.jwt;

import caprita.catalin.cityquestbackend.security.service.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@ConditionalOnProperty("cityquest.jwt.secret")
public class JwtTokenUtil {

    @Value("${cityquest.jwt.secret}")
    private String JWT_SECRET;
    @Value("${cityquest.jwt.issuer}")
    private String ISSUER;
    @Value("#{T(java.lang.Long).parseLong(${cityquest.jwt.duration}) * 60 * 60 * 1000}")
    private Long jwtDuration;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    public static final String TOKEN_PREFIX = "Bearer ";

    public JwtTokenUtil(@Value("${cityquest.jwt.secret}")  String JWT_SECRET,
                        @Value("${cityquest.jwt.issuer}") String ISSUER,
                        @Value("#{T(java.lang.Long).parseLong(${cityquest.jwt.duration}) * 60 * 60 * 1000}") Long jwtDuration
                        ) {
        this.JWT_SECRET = JWT_SECRET;
        this.ISSUER = ISSUER;
        this.jwtDuration = jwtDuration;
        Assert.notNull(jwtDuration, "jwtDuration is null!");
        Assert.isTrue(JWT_SECRET != null, "JWT_SECRET is wrong!");
        Assert.isTrue(ISSUER != null && !ISSUER.isEmpty(), "JWT_SECRET is wrong!");

    }

    public String generateAccessToken(UserPrincipal user) {
        return Jwts.builder()
                .setSubject(String.format("%s", user.getUsername()))
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtDuration))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Date getExpirationDate(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            LOGGER.error("Invalid JWT signature - {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("JWT claims string is empty - {}", ex.getMessage());
        }
        return false;
    }
}
