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

    public static List<String> generateMessages(Random r, int count, NameVerifier nameVerifier, boolean decorated){
        List<String> messages = new ArrayList<>();
        int i = 0;

        while (i<count) {
            int length = r.nextInt(1, 25);
            String message = RandomStringUtils.random(length, true, false);
            if (!message.isEmpty() && nameVerifier.isChannelNameAcceptable(message)) {
                if (r.nextInt(0,10) == 5 && decorated && nameVerifier.isChannelNameTyped(message)){
                    message = generateMessageWithDecoration(message);
                    nameVerifier.setChannelNameTyped(message,true);
                } else {
                    nameVerifier.setChannelNameTyped(message,false);
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
        int choice = r.nextInt(0,3);
        if (choice == 0){
            return Boolean.toString(r.nextBoolean());
        } else if (choice == 1){
            String character = RandomStringUtils.random(1, true,false);
            return "'"+character+"'";
        } else if (choice == 2){
            return Integer.toString(r.nextInt(0,150));
        } else {
            int length = r.nextInt(1, 10);
            return RandomStringUtils.random(1, true, false) +
                    RandomStringUtils.random(length, true, true);
        }
    }

    public static String generateGuard(Random r, NameVerifier nameVerifier, List<String> messages){
        List<String> decoratedMessages = messages.stream()
               .filter(s -> s.matches("[a-zA-Z]*[!?$.]'?[a-zA-Z0-9]*'?"))
               .toList();

        List<String> parameters = new ArrayList<>();
        List<String> channels = new ArrayList<>();
        for (String message : decoratedMessages) {
            String[] components = message.split("[!?$.]");
            if (components.length > 1) {
                parameters.add(components[1]);
                channels.add(components[0]);
            }
        }

        if (parameters.size() > 0){
            int choice = r.nextInt(0,parameters.size());
            String parameter = parameters.get(choice);
            int length = r.nextInt(1, 10);
            String constantName = RandomStringUtils.random(1, true, false) +
                    RandomStringUtils.random(length, true, true);

            while (!nameVerifier.isConstantNameAcceptable(constantName, channels.get(choice))){
                constantName = RandomStringUtils.random(1, true, false) +
                        RandomStringUtils.random(length, true, true);
            }

            if (parameter.equals("true")||parameter.equals("false")){
                return constantName;
            } else {
                return constantName+"=="+parameter;
            }
        }

        return null;
    }

    private static String generateExpression(){
        return "";
    }
}
