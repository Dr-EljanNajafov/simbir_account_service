package com.github.simbir_account_service.auth.jwt;

import com.github.simbir_account_service.blacklist.JwtBlacklist;
import com.github.simbir_account_service.blacklist.JwtBlacklistRepository;
import com.github.simbir_account_service.account.response.RefreshResponse;
import com.github.simbir_account_service.controller.AccountController;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import java.util.*;
import java.security.Key;
import java.util.function.Function;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String SECRET_KEY = "9311eceb51e8a2f2d7a5825e6178dcf27d102a8173eb003cf4a0de6bc8e29df0878798a4de52de55ab32c4dcdb123ed35a0bd43c3ac2e499e95a1a95222c3947";
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);


    public <T> T accessUser(HttpServletRequest request, Function<String, T> userConsumer) {
        String username = extractClaim(token(request).orElseThrow(), Claims::getSubject);
        return userConsumer.apply(username);
    }

    public void accessUserVoid(HttpServletRequest request, Consumer<String> userConsumer) {
        accessUser(request, username -> {
            userConsumer.accept(username);  // Передаем строковое имя пользователя
            return null;
        });
    }

    public Optional<String> token(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        jwt = authHeader.substring("Bearer ".length());
        return Optional.of(jwt);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Объедините claims и роли
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("roles", roles);

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public void blacklistedToken(String token) {
        jwtBlacklistRepository.save(new JwtBlacklist(token));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Optional<JwtBlacklist> blacklistToken = jwtBlacklistRepository.findById(token);
        if (blacklistToken.isPresent()) {
            logger.warn("Токен черного списка: {}", token);
            if (isTokenExpired(token)) {
                jwtBlacklistRepository.delete(blacklistToken.get());
                logger.warn("Токен недействителен: {}", token);
            }
            return false;
        }
        // Check if username matches and token is not expired
        final String username = extractUsername(token);
        logger.error("Ошибка в процессе валидации токена");
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public RefreshResponse refreshTokens(String refreshToken) {
        if (validateRefreshToken(refreshToken)) {
            String username = extractUsername(refreshToken);
            String newAccessToken = generateAccessToken(username);
            String newRefreshToken = generateRefreshToken(username);

            RefreshResponse response = new RefreshResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);

            return response;
        } else {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        Optional<JwtBlacklist> blacklistToken = jwtBlacklistRepository.findById(token);
        if (blacklistToken.isPresent()) {
            return false;  // Token is blacklisted
        }
        try {
            String username = extractUsername(token);
            return username != null && !isTokenExpired(token);
        } catch (Exception e) {
            return false;  // Invalid token
        }
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
