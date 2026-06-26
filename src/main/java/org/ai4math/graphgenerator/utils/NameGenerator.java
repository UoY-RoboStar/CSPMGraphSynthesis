package org.ai4math.graphgenerator.utils;

import org.ai4math.cspm.Keywords;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class NameGenerator {

    public static String generateProcessName(NameVerifier nameVerifier){
        String name;
        do {
            name = RandomStringUtils.randomAlphabetic(1, 12);
        } while (!nameVerifier.isProcessNameAcceptable(name));
        return name;
    }

    public static Pair<String, Pair<String,String>> generateTypedProcessName(
            Random r, NameVerifier nameVerifier) {
        return generateTypedProcessName(r, nameVerifier, null);
    }

    public static Pair<String, Pair<String,String>> generateTypedProcessName(
            Random r, NameVerifier nameVerifier, String type){
        Pair<String, Pair<String,String>> typedProcess;
        String name = generateProcessName(nameVerifier);

        type = type==null?generateType(r, nameVerifier):type;

        String paramName;
        do {
            paramName = RandomStringUtils.randomAlphabetic(1, 12);
        } while (!nameVerifier.isParameterNameAcceptable(paramName));

        Pair<String,String> parameter = Pair.of(paramName,type);
        typedProcess = Pair.of(name,parameter);

        return typedProcess;
    }

    public static String generateType(Random r, NameVerifier nameVerifier) {
        int choice = r.nextInt(0,4);
        if (choice == 0){
            return Keywords.BOOL;
        } else if (choice == 1){
            return Keywords.CHAR;
        } else if (choice == 2){
            return Keywords.INT;
        } else {
            return generateEnum(r, nameVerifier);
        }
    }

    private static String generateEnum(Random r, NameVerifier nameVerifier) {
        int length = r.nextInt(1, 10);
        String constant;
        do {
            constant = RandomStringUtils.random(1, true, false) +
                    RandomStringUtils.random(length, true, true);
        } while (!nameVerifier.isTypeNameAcceptable(constant));

        return constant;
    }

    public static List<String> generateMessages(Random r, int count, NameVerifier nameVerifier, boolean decorated){
        List<String> messages = new ArrayList<>();
        int i = 0;

        while (i<count) {
            int length = r.nextInt(1, 25);
            String message = RandomStringUtils.random(length, true, false);
            if (!message.isEmpty() && nameVerifier.isChannelNameAcceptable(message)) {
                if (r.nextInt(0,11) == 5 && decorated && nameVerifier.isChannelNameTyped(message)){
                    nameVerifier.setChannelNameTyped(message,true);
                    message = generateMessageWithDecoration(nameVerifier, message);
                } else {
                    nameVerifier.setChannelNameTyped(message,false);
                }
                messages.add(message);
                i++;
            }
        }

        return messages;
    }

    private static String generateMessageWithDecoration(NameVerifier nameVerifier, String message){
        Random r = new Random();
        int choice = r.nextInt(0,4);
        if (choice==0){
            message += "!";
            message += generateParameter(nameVerifier, r);
            //message+= generateExpression();
        } else if (choice==1){
            message+="?";
            message += generateParameter(nameVerifier, r);
        } else if (choice==2){
            message+=".";
            message += generateParameter(nameVerifier, r);
            //message += r.nextBoolean()?generateParameter(r):generateExpression();
        } else if (choice==3){
            message+="$";
            message += generateParameter(nameVerifier, r);
        }

        return message;
    }

    private static String generateParameter(NameVerifier nameVerifier, Random r){
        int choice = r.nextInt(0,4);
        return generateParameter(nameVerifier, choice, r);
    }

    private static String generateParameter(NameVerifier nameVerifier, Integer choice, Random r){
        if (choice == 0){
            return r.nextBoolean()?Keywords.TRUE:Keywords.FALSE;
        } else if (choice == 1){
            String character = RandomStringUtils.random(1, true,false);
            return "'"+character+"'";
        } else if (choice == 2){
            return Integer.toString(r.nextInt(0,150));
        } else {
            return generateEnum(r, nameVerifier);
        }
    }

    public static List<String> generateGuardPair(NameVerifier nameVerifier, Random r, String parameter, String type){
        List<String> guardPair = new ArrayList<>();
        String value, value2 = null, comparison1, comparison2;
        List<String> mathComparison = new ArrayList<>(List.of("==","!=","<","<=",">",">="));
        List<String> comparisons = new ArrayList<>(List.of("==","!="));

        if (Objects.equals(type, Keywords.INT)){
            value = generateParameter(nameVerifier, 2, r);
            comparison1 = mathComparison.get(r.nextInt(0,mathComparison.size()));
            mathComparison.remove(comparison1);
            comparison2 = mathComparison.get(r.nextInt(0,mathComparison.size()));
        } else if (Objects.equals(type, Keywords.BOOL)){
            value = generateParameter(nameVerifier, 0, r);
            value2 = Objects.equals(value, Keywords.TRUE) ?Keywords.FALSE:Keywords.TRUE;
            comparison1 = comparisons.get(r.nextInt(0,2));
            comparisons.remove(comparison1);
            comparison2 = comparisons.getFirst();
        } else if (Objects.equals(type, Keywords.CHAR)){
            value = generateParameter(nameVerifier, 1, r);
            do {
                value2 = generateParameter(nameVerifier, 1, r);
            } while (Objects.equals(value2, value));
            comparison1 = comparisons.get(r.nextInt(0,2));
            comparisons.remove(comparison1);
            comparison2 = comparisons.get(0);
        } else {
            value = generateParameter(nameVerifier, 3, r);
            value2 = generateParameter(nameVerifier, 3, r);
            comparison1 = comparisons.get(r.nextInt(0,2));
            comparisons.remove(comparison1);
            comparison2 = comparisons.get(0);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("(").append(parameter).append(comparison1).append(value).append(")");
        guardPair.add(sb.toString());

        sb = new StringBuilder();
        if (value2!=null) {
            sb.append("(").append(parameter).append(comparison1).append(value2).append(")");
        } else {
            sb.append("(").append(parameter).append(comparison2).append(value).append(")");
        }
        guardPair.add(sb.toString());

        return guardPair;
    }

    private static String generateExpression(){
        return "";
    }
}
