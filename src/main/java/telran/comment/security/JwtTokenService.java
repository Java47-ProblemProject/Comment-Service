package telran.comment.security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;
    private SecretKey jwtSecret;

    @PostConstruct
    public void init() {
        byte[] secretBytes = jwtSecretKey.getBytes();
        jwtSecret = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    public String extractEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
            String encryptedEmail = claims.getSubject();
            if (claims.getExpiration().before(new Date())) {
                return null;
            }
            return EmailEncryptionConfiguration.decryptAndDecodeUserId(encryptedEmail);
        } catch (Exception ex) {
            return null;
        }
    }

    public Set<String> extractRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
            String[] roles = claims.get("roles").toString().replace("[", "").replace("]", "").trim().split(",");
            return new HashSet<>(Arrays.asList(roles));
        } catch (Exception ex) {
            return Collections.emptySet();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

