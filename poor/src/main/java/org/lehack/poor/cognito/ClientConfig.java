package org.lehack.poor.cognito;

import java.util.List;

public record ClientConfig(
        String usage,
        String clientId,
        String clientSecret,
        List<String> scopes
) {

}
