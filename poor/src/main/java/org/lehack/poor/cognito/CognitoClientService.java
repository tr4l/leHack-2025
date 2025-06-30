package org.lehack.poor.cognito;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CognitoClientService {

    private final Map<String, ClientConfig> clients = new HashMap<>();
    private final SignatureAlgorithm alg = Jwts.SIG.RS256;
    KeyPair pair = alg.keyPair().build();

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        Resource[] resources = resourcePatternResolver.getResources("classpath:/clients/**.json");

        for (Resource clientResource : resources) {
            ClientConfig client = jsonMapper.readValue(clientResource.getURL(), new TypeReference<>() {});
            clients.put(client.clientId(), client);
        }
    }

    public ClientConfig getClientConfig(String clientId) {
        return clients.get(clientId);
    }

    public PrivateKey getPrivateKey() throws Exception {
        /*Resource resource = resourcePatternResolver.getResource("classpath:/keypair.pem");

        String privateKeyPEM = resource.getContentAsString(StandardCharsets.US_ASCII)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .replace("-----END PRIVATE KEY-----", "");
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);*/
        return pair.getPrivate();
    }


    public PublicKey getPublicKey() throws Exception {
        /*RSAPrivateKey rsaCrtKey = readPKCS8PrivateKey();
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(rsaCrtKey.getModulus(), rsaCrtKey.getPrivateExponent());
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(rsaPublicKeySpec);*/
        return pair.getPublic();
    }

    public List<ClientConfig> getAllClientConfig() {
        return new ArrayList<>(clients.values());
    }
}