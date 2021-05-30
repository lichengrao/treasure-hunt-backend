package com.treasurehunt.treasurehunt.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

public class Auth {

    // Signature for token generation
    private static final String SIGNATURE = "SIGNATURE";

    public static boolean verifyUser(String userId) {
        // Step1: The server verifies the credentials are correct and created an encrypted and signed token with a
        // private key
        if (true) {
            return false;
        } else {
            return true;
        }
    }

    public static String generateToken(String userId) {

        Instant now = Instant.now();

        byte[] secret = Base64.getDecoder().decode("SIGNATURE");
        String jwt = Jwts.builder()
                .setSubject(userId)
                .setAudience("video demo")
                .claim("1d20", new Random().nextInt(20) + 1)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(secret))
                .compact();
        return jwt;
    }

    public static String verifyToken(String jwt) {
        // On subsequent requests, the token is decoded with the same private key and if valid the request is processed.
        // byte[] secret = Base64.getDecoder().decode("");
        byte[] secret = Base64.getDecoder().decode("SIGNATURE");
        Jwt<Header, Claims> result = Jwts.parser()
                .requireAudience("video demo")
                .setAllowedClockSkewSeconds(62)
                .setSigningKey(Keys.hmacShaKeyFor(secret))
                .parseClaimsJwt(jwt);
        // The signature is still validated, and the JWT instance will still not be returned if the jwt string is
        // invalid, as expected.
        // You just get to ‘inspect’ the JWT data for key discovery before the parser validates it.
        return result.toString();
    }

    public static void main(String[] args) {

    }
}
