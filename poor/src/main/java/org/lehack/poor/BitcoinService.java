package org.lehack.poor;

import jakarta.annotation.PostConstruct;
import net.datafaker.Faker;
import org.lehack.poor.model.RentBitCoin;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class BitcoinService {

    private Map<UUID, RentBitCoin> bitcoins = new HashMap<UUID, RentBitCoin>();

    @PostConstruct
    public void init() {
        var faker = new Faker();
        for (int i = 0; i < 10; i++) {
            RentBitCoin rentBitCoin = new RentBitCoin(
                    faker.name().firstName(),
                    faker.name().lastName(),
                    faker.number().randomDigitNotZero(),
                    faker.number().randomDigitNotZero(),
                    Map.of(
                            "TXT", faker.aws().accountId(),
                            "VERIFY", faker.internet().macAddress(),
                            "POOR", faker.random().hex(32)
                    )
            );
            bitcoins.put(UUID.randomUUID(), rentBitCoin);
        }
    }

    public Map<UUID, RentBitCoin> getBitcoins() {
        return bitcoins;
    }

    public void save(RentBitCoin bitcoin) {
        Random generator = new Random();
        UUID randomKey = bitcoins.keySet().stream()
                .skip(generator.nextInt(bitcoins.size()))
                .findFirst().get();
        bitcoins.put(randomKey, bitcoin);
    }

    public Object getBitcoin(UUID id) {
        return bitcoins.get(id);
    }
}
