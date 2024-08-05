package com.cornershop.ecommerce.service;

import static org.springframework.security.config.Elements.JWT;

import com.cornershop.ecommerce.dto.LoginDto;
import com.cornershop.ecommerce.model.Customer;
import com.cornershop.ecommerce.repository.CustomerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final CustomerRepository customerRepository;

    public static final String SECRET = "404D635166546A576E5A7234753778214125442A472D4B6150645267556B5870";

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKEy())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public LoginDto generateToken(Authentication authentication) {
        LoginDto loginDto = new LoginDto();
        Optional<Customer> customerOptional = customerRepository.findByEmail(authentication.getName());

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authentication.getAuthorities());
        claims.put("name", authentication.getName());
        if(customerOptional.isPresent()) {
            loginDto.setCustomerId(customerOptional.get().getId());
        }
        loginDto.setToken(createToken(claims, authentication.getName()));

        return loginDto;
    }

    private String createToken(Map<String, Object> claims, String name) {
        return Jwts.builder()
                .claims(claims)
                .subject(name)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSignKEy())
                .compact();
    }

    private SecretKey getSignKEy() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
