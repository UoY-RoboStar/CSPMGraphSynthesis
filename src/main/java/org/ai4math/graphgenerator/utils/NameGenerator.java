package org.ai4math.graphgenerator.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NameGenerator {

    public static String generateProcessName(NameVerifier nameVerifier){
        String name = RandomStringUtils.randomAlphabetic(1, 12);
        while (!nameVerifier.isProcessNameAcceptable(name)){
            name = RandomStringUtils.randomAlphabetic(1, 12);
        }
        return name;
    }

    public static List<String> generateMessages(int count, NameVerifier nameVerifier){
        List<String> messages = new ArrayList<>();
        int i = 0;
        Random r = new Random();

        while (i<count) {
            int length = r.nextInt(1, 25);
            String message = RandomStringUtils.random(length, true, false);
            if (!message.isEmpty() && nameVerifier.isChannelNameAcceptable(message)) {
                messages.add(message);
                i++;
            }
        }

        return messages;
    }
}
