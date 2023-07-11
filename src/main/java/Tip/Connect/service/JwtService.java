package Tip.Connect.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

//    @Value("@{application.security.jwt.serect-key}")
//    private String serectKey;
//    @Value("@{application.security.jwt.expiration}")
//    private Long jwtExpiration;
//    @Value("@{application.security.jwt.refresh-token.expiration}")
//    private Long refreshExpiration;

    private String serectKey = "2949e145c15445a20cc0d0d569e070cdcabfa18bb049fbf4eb1c7c75023a1fb1";
    private Long jwtExpiration = 86400000L;
    private Long refreshExpiration = 604800000L;

    public String generateRefreshToken(UserDetails userDetails){
        return buildToken(new HashMap<>(),userDetails,refreshExpiration);
    }
    public String generateToken(UserDetails userDetails){
        //hashMap is entry Map
        return buildToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    private String generateToken(Map<String,Object> extraClaims, UserDetails userDetails){
        return buildToken(extraClaims,userDetails,jwtExpiration);
    }

    private String buildToken(Map<String,Object> extraClaims, UserDetails userDetails, Long expiration){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSerectKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSerectKey(){
        byte[] keyBytes = Decoders.BASE64.decode(serectKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //extract belong <T>
    private <T> T extractClaim(String token, Function< Claims, T> claimResolver){
        final Claims claims = extractAllClaim(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaim(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSerectKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return (!isTokenExpire(token)&&extractUsername(token).equals(userDetails.getUsername()));

    }

    public boolean isTokenExpire(String token){
        return extractExpiration(token).before(new Date());
    }



}
