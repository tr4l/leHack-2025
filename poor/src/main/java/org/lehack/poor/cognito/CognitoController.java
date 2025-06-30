package org.lehack.poor.cognito;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Hidden
@RestController
@RequestMapping("cognito-proxy")
public class CognitoController {

    @Autowired
    private CognitoClientService cognitoClientService;

    @Value("${poor.cognito.issuer}")
    private String issuer;

    @Value("${poor.cognito.kid}")
    private String kid;

    private HttpHeaders commonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-amz-cognito-request-id", UUID.randomUUID().toString());
        headers.add("cache-control", "no-cache, no-store, max-age=0, must-revalidate");
        headers.add("pragma", "no-cache");
        headers.add("expires", "0");
        headers.add("x-cache", "Miss from cloudfront");
        headers.add("via", "1.1 <redacted>.cloudfront.net (CloudFront)");
        headers.add("x-amz-cf-pop", "<redacted>");
        headers.add("x-amz-cf-id", "<redacted>");
        return headers;
    }

    @GetMapping("/oauth2/authorize")
    public ResponseEntity<?> authorize() {
        return ResponseEntity.badRequest().headers(commonHeaders()).body(Map.of("error", "not_implemented"));
    }

    @PostMapping("/oauth2/revoke")
    public ResponseEntity<?> revoke() {
        return ResponseEntity.badRequest().headers(commonHeaders()).body(Map.of("error", "not_implemented"));
    }

    @PostMapping("/saml2/idpresponse")
    public ResponseEntity<?> idpresponse() {
        return ResponseEntity.badRequest().headers(commonHeaders()).body(Map.of("error", "not_implemented"));
    }

    @GetMapping("/oauth2/userInfo")
    public ResponseEntity<?> userInfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null) {
            return ResponseEntity.badRequest().headers(commonHeaders()).body(Map.of("invalid_request", "Authorization header required, but missing from request"));
        }
        if (!authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().headers(commonHeaders()).body(Map.of("invalid_request", "Authorization header format is invalid"));
        }

        String token = authorization.substring(7);

        try {
            JwtParser jwtParser = Jwts.parser()
                    .verifyWith(cognitoClientService.getPublicKey())
                    .build();

            // Not used, but we need to parse it to verify the signature
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(commonHeaders()).body(Map.of("invalid_token", "Access token is expired or user has globally signed out, disabled, or been deleted"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(commonHeaders()).body(Map.of("invalid_token", "Value of username in token claims must be a String"));

    }

    @PostMapping("/oauth2/token")
    public ResponseEntity<?> token(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam(value = "scope", required = false) String scope
    ) {
        if (!"client_credentials".equals(grantType)) {
            return ResponseEntity.badRequest().headers(commonHeaders()).body(Map.of("error", "unsupported_grant_type"));
        }
        var clientConfig = cognitoClientService.getClientConfig(clientId);

        if (clientConfig == null) {
            return ResponseEntity.badRequest().headers(commonHeaders()).body(Map.of("error", "invalid_client"));
        }

        if (!clientConfig.clientSecret().equals(clientSecret)) {
            return ResponseEntity.badRequest().headers(commonHeaders()).body(Map.of("error", "invalid_client"));
        }

        //TODO: send invalid_scope if scope is not in the list of valid scopes and doesn't contains "/"
        // Not needed, but will look more realistic

        List<String> requestedScopes = scope != null ? List.of(scope.split(" ")) : clientConfig.scopes();
        List<String> validScopes = new ArrayList<>(requestedScopes);
        validScopes.retainAll(clientConfig.scopes());

        try {
            String token = Jwts.builder()
                    .header().keyId(kid).and()
                    .subject(clientId)
                    .claim("scope", String.join(" ", validScopes))
                    .claim("token_use", "access")
                    .claim("auth_time", new Date())
                    .claim("iss", issuer)
                    .claim("version", 2)
                    .claim("jti", UUID.randomUUID().toString())
                    .claim("client_id", clientId)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hour expiration
                    .signWith(cognitoClientService.getPrivateKey()/*, Jwts.SIG.RS256*/)
                    .compact();
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", token);
            response.put("token_type", "Bearer");
            response.put("expires_in", 3600);
            return ResponseEntity.ok().headers(commonHeaders()).body(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

}