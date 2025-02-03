package com.rahul.journal_app.utils;

import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    private UserService userService;

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.expiration_time}")
    private Long jwtExpirationTime; // in minute

    public String generateToken(String username) {
        Map<String, Object> claims = setClaims(username);
        return createJwtToken(claims, username);
    }

    private Map<String, Object> setClaims(String username) {
        Map<String, Object> claims = new HashMap<>(); // you can send your payload here, like username, email, role etc
        User user = userService.findByUserName(username);
        if(user != null){
            if(user.getUserName() !=null && !user.getUserName().equals("")){
                claims.put("email", user.getUserName());
            }

            Boolean isUserAdmin=user.getRoles().stream()
                    .anyMatch(role->"ADMIN".equals(role));
            if(isUserAdmin){
                claims.put("role", "ADMIN");
            }else{
                claims.put("role", "USER");
            }
        }
        return claims;
    }

    private String createJwtToken(Map<String, Object> claims, String username) {

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .header().add("typ", "JWT").and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * jwtExpirationTime))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String extractUsername(String jwtToken) {
         Claims allClaims =extractAllClaims(jwtToken);
         return allClaims.getSubject();
    }


    // Get payloads
    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    public boolean validateToken(String jwtToken) {
        return (!isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken) {
        Date expiredDate = extractExpirationDate(jwtToken);
        return expiredDate.before(new Date()); // check expired time(set in jwt token) with current date time
    }

    private Date extractExpirationDate(String jwtToken) {
        return extractAllClaims(jwtToken).getExpiration();
    }
}
