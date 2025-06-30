package org.lehack.poor.model;

import java.util.Map;

public record RentBitCoin(
        String firstname,
        String name,
        int amount,
        int duration,
        Map<String, String> customData
) {}
