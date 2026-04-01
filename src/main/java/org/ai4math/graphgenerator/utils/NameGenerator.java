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
                if (r.nextInt(0,10) == 5){
                    message = generateMessageWithDecoration(message);
                }
                messages.add(message);
                i++;
            }
        }

        return messages;
    }

    private static String generateMessageWithDecoration(String message){
        Random r = new Random();
        int choice = r.nextInt(0,4);
        if (choice==0){
            message += "!";
            message += generateParameter(r);
            //message+= generateExpression();
        } else if (choice==1){
            message+="?";
            message += generateParameter(r);
        } else if (choice==2){
            message+=".";
            message += generateParameter(r);
            //message += r.nextBoolean()?generateParameter(r):generateExpression();
        } else if (choice==3){
            message+="$";
            message += generateParameter(r);
        }

        return message;
    }

    private static String generateParameter(Random r){
        int length = r.nextInt(1, 10);
        return RandomStringUtils.random(1, true, false) +
                RandomStringUtils.random(length, true, true);
    }

    private static String generateExpression(){
        return "";
    }
}
