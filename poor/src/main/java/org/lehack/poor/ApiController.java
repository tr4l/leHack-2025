package org.lehack.poor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.lehack.poor.cognito.ClientConfig;
import org.lehack.poor.cognito.CognitoClientService;
import org.lehack.poor.exception.AuthException;
import org.lehack.poor.model.BitCoin;
import org.lehack.poor.model.RentBitCoin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@OpenAPIDefinition(tags = {
        @Tag(name = "me", description = "Get details about the caller"),
        @Tag(name = "poors", description = "Get public listing of existing poors"),
        @Tag(name = "poor", description = "Get details about one poor."),
        @Tag(name = "rent", description = "Call to rent your bitcoin. Will deliver a poor"),
        @Tag(name = "admin", description = "Get/set admin bitcoin master key")

})
@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired
    private CognitoClientService cognitoClientService;

    @Autowired
    private BitcoinService bitcoinService;

    @Value("${poor.cognito.flag}")
    private String flag;
    // Get proof/id

    @Hidden
    @GetMapping("/clients/")
    public ResponseEntity<List<ClientConfig>> getClientConfig() {
        return ResponseEntity.ok().body(cognitoClientService.getAllClientConfig());
    }

    @Tag(name = "admin")
    @GetMapping("/admin/bitcoin")
    public ResponseEntity<?> bitcoin(@RequestHeader Map<String, String> headers) {
        checkAuthAndScope(headers, "admin");
        if ("dev".equals(headers.get("x-env"))) {
            return ResponseEntity.ok(Map.of("key", "--demo--", "message", "In production, this will get the master bitcoin keys."));
        }
        return ResponseEntity.ok(Map.of("key", flag));
    }

    @Tag(name = "poors")
    @GetMapping("/poors/")
    public ResponseEntity<?> listPoor(@RequestHeader Map<String, String> headers) {
        checkAuthAndScope(headers, "read");
        return ResponseEntity.ok(bitcoinService.getBitcoins());
    }

    @Tag(name = "poor")
    @GetMapping("/poor/{id}")
    public ResponseEntity<?> listPoor(@RequestHeader Map<String, String> headers, @PathVariable UUID id) {
        checkAuthAndScope(headers, "read");
        return ResponseEntity.ok(bitcoinService.getBitcoin(id));
    }

    @Tag(name = "rent")
    @PostMapping("/rent/bitcoin")
    public ResponseEntity<?> rentBitcoin(@RequestHeader Map<String, String> headers, @RequestBody RentBitCoin bitcoin) {
        checkAuthAndScope(headers, "write");
        bitcoinService.save(bitcoin);
        if ("dev".equals(headers.get("x-env"))) {
            return ResponseEntity.ok(Map.of("message", "(demo) You'r poor hasbeen generated: " + bitcoin.firstname()));
        }
        return ResponseEntity.ok(Map.of(
                "message", "An email has been sent to you with the details of your rental.",
                "fr", "We haven't coded the rental process yet, so we do it manually then send an email."));
    }


    @Tag(name = "admin")
    @PutMapping("/admin/bitcoin")
    public ResponseEntity<?> putBitcoin(@RequestHeader Map<String, String> headers, @RequestBody BitCoin bitcoin) {
        checkAuthAndScope(headers, "admin");
        if ("dev".equals(headers.get("x-env"))) {
            return ResponseEntity.ok(Map.of("message", "(demo) Key save successfully: " + bitcoin.key()));
        }
        return ResponseEntity.internalServerError().body(Map.of("message", "Can't overwrite the key in production"));
    }

    @Tag(name = "me")
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader Map<String, String> headers) {
        checkAuthAndScope(headers, "read");
        return ResponseEntity.ok(Map.of("client", "demo"));
    }

    private void checkAuthAndScope(Map<String, String> headers, String scope) {
        String auth = headers.get("authorization");
        if (auth == null) {
            throw new AuthException("Authorization header required, but missing from request");
        }
        String[] parts = auth.split(" ");
        if (parts.length != 2 || !parts[0].equals("Bearer")) {
            throw new AuthException("Invalid Authorization header format");
        }
        String token = parts[1];
        String scopes = "";
        try {
            JwtParser jwtParser = Jwts.parser()
                    .verifyWith(cognitoClientService.getPublicKey())
                    .build();

            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            scopes = claims.get("scope", String.class);

        } catch (Exception e) {
            throw new AuthException("Shit happens: " + e.getMessage());
        }
        if (scopes == null || scopes.isEmpty()) {
            throw new AuthException("Should not happens, empty scope ? really ?!?");
        }

        if (scopes.contains("env/dev")) {
            // Scoped env token can only be used on dev
            if (!"dev".equals(headers.get("x-env"))) {
                throw new AuthException("Token emitted for dev env, but request header x-env is not dev");
            }
        }

        // F*cking ugly, but should do the work for the CTF
        if (scope != null && !scopes.contains(scope)) {
            // read will match read/all etc ...
            throw new AuthException("You don't have permission to make this API call");
        }

    }
}
