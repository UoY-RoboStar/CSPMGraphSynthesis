package org.ai4math.graphgenerator.utils;

import org.ai4math.cspm.Keywords;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class NameGeneratorTest {

    @Test
    public void givenNameIsAcceptable_whenGenerateProcessName_thenReturnNameTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isProcessNameAcceptable(any())).thenReturn(true);

        String name = NameGenerator.generateProcessName(nameVerifier);

        assertNotNull(name, "name returned was null");
    }

    @Test
    public void givenNameIsNotInitiallyAcceptable_whenGenerateProcessName_thenReturnNameTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isProcessNameAcceptable(any())).thenReturn(false, true);

        String name = NameGenerator.generateProcessName(nameVerifier);

        assertNotNull(name, "name returned was null");
    }


    @Test
    public void givenNameIsAcceptable_whenGenerateCharTypedProcessName_thenReturnNameTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        Random r = mock(Random.class);
        when(r.nextInt(0,4)).thenReturn(1);
        when(nameVerifier.isProcessNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isParameterNameAcceptable(any())).thenReturn(true);

        Pair<String, Pair<String,String>> typedProcess = NameGenerator.generateTypedProcessName(r, nameVerifier);

        String processName = typedProcess.getKey();
        Pair<String,String> typePair = typedProcess.getValue();
        String parameterName = typePair.getKey();
        String parameterType = typePair.getValue();

        assertNotNull(processName, "name returned was null");
        assertNotNull(parameterName, "parameter name returned was null");
        assertEquals(Keywords.CHAR, parameterType, "parameter type was not Char");
    }

    @Test
    public void givenNameIsAcceptable_whenGenerateIntTypedProcessName_thenReturnNameTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        Random r = mock(Random.class);
        when(r.nextInt(0,4)).thenReturn(2);
        when(nameVerifier.isProcessNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isParameterNameAcceptable(any())).thenReturn(true);

        Pair<String, Pair<String,String>> typedProcess = NameGenerator.generateTypedProcessName(r, nameVerifier);

        String processName = typedProcess.getKey();
        Pair<String,String> typePair = typedProcess.getValue();
        String parameterName = typePair.getKey();
        String parameterType = typePair.getValue();

        assertNotNull(processName, "name returned was null");
        assertNotNull(parameterName, "parameter name returned was null");
        assertEquals(Keywords.INT, parameterType, "parameter type was not Int");
    }

    @Test
    public void givenNameIsAcceptable_whenGenerateBoolTypedProcessName_thenReturnNameTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        Random r = mock(Random.class);
        when(r.nextInt(0,4)).thenReturn(0);
        when(nameVerifier.isProcessNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isParameterNameAcceptable(any())).thenReturn(true);

        Pair<String, Pair<String,String>> typedProcess = NameGenerator.generateTypedProcessName(r, nameVerifier);

        String processName = typedProcess.getKey();
        Pair<String,String> typePair = typedProcess.getValue();
        String parameterName = typePair.getKey();
        String parameterType = typePair.getValue();

        assertNotNull(processName, "name returned was null");
        assertNotNull(parameterName, "parameter name returned was null");
        assertEquals(Keywords.BOOL, parameterType, "parameter type was not Boolean");
    }

    @Test
    public void givenNameIsAcceptable_whenGenerateEnumTypedProcessName_thenReturnNameTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        Random r = mock(Random.class);
        when(r.nextInt(0,4)).thenReturn(3);
        when(nameVerifier.isProcessNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isParameterNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true);

        Pair<String, Pair<String,String>> typedProcess = NameGenerator.generateTypedProcessName(r, nameVerifier);

        String processName = typedProcess.getKey();
        Pair<String,String> typePair = typedProcess.getValue();
        String parameterName = typePair.getKey();
        String parameterType = typePair.getValue();

        assertNotNull(processName, "name returned was null");
        assertNotNull(parameterName, "parameter name returned was null");
        assertNotNull(parameterType, "parameter type returned was null");
        assertNotEquals(Keywords.CHAR, parameterType, "parameter type was Char");
        assertNotEquals(Keywords.INT, parameterType, "parameter type was Int");
        assertNotEquals(Keywords.BOOL, parameterType, "parameter type was Boolean");
    }

    @Test
    public void givenParameterNameIsNotInitiallyAcceptable_whenGenerateTypedProcessName_thenReturnNameTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        Random r = mock(Random.class);
        when(r.nextInt(0,4)).thenReturn(0);
        when(nameVerifier.isProcessNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isParameterNameAcceptable(any())).thenReturn(false,false,true);

        Pair<String, Pair<String,String>> typedProcess = NameGenerator.generateTypedProcessName(r, nameVerifier);

        String processName = typedProcess.getKey();
        Pair<String,String> typePair = typedProcess.getValue();
        String parameterName = typePair.getKey();
        String parameterType = typePair.getValue();

        assertNotNull(processName, "name returned was null");
        assertNotNull(parameterName, "parameter name returned was null");
        assertEquals(Keywords.BOOL, parameterType, "parameter type was not Boolean");
    }

    @Test
    public void givenTypeNameIsNotInitiallyAcceptable_whenGenerateEnumTypedProcessName_thenReturnNameTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        Random r = mock(Random.class);
        when(r.nextInt(0,4)).thenReturn(3);
        when(nameVerifier.isProcessNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isParameterNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(false,false,true);

        Pair<String, Pair<String,String>> typedProcess = NameGenerator.generateTypedProcessName(r, nameVerifier);

        String processName = typedProcess.getKey();
        Pair<String,String> typePair = typedProcess.getValue();
        String parameterName = typePair.getKey();
        String parameterType = typePair.getValue();

        assertNotNull(processName, "name returned was null");
        assertNotNull(parameterName, "parameter name returned was null");
        assertNotNull(parameterType, "parameter type returned was null");
        assertNotEquals(Keywords.CHAR, parameterType, "parameter type was Char");
        assertNotEquals(Keywords.INT, parameterType, "parameter type was Int");
        assertNotEquals(Keywords.BOOL, parameterType, "parameter type was Boolean");
    }

    @Test
    public void givenNameIsAcceptableAndDecorated_whenGenerateMessages_thenReturnMessagesIncludesDecorationsTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isChannelNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isChannelNameTyped(any())).thenReturn(true);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true,false,true);
        Random r = mock(Random.class);
        when(r.nextInt(1,25)).thenReturn(6,14,26,12,4);
        when(r.nextInt(0,11)).thenReturn(5);

        List<String> names = NameGenerator.generateMessages(r, 5,  nameVerifier, true);

        assertNotNull(names, "name returned was null");
        assertEquals(5, names.size(), "Fewer names generated than expected");

        boolean decorated = false;
        for (String name: names){
            if (name.contains("!") || name.contains("?") || name.contains(".") || name.contains("$")){
                decorated = true;
            }
        }
        assertTrue(decorated, "No messages have decorations: "+names);
    }

    @Test
    public void givenNameIsNotInitiallyAcceptableAndDecorated_whenGenerateMessages_thenReturnMessagesIncludesDecorationsTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isChannelNameAcceptable(any())).thenReturn(false, false, true);
        when(nameVerifier.isChannelNameTyped(any())).thenReturn(true);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true,false,true);
        Random r = mock(Random.class);
        when(r.nextInt(1,25)).thenReturn(6,14,26,12,4);
        when(r.nextInt(0,11)).thenReturn(5);

        List<String> names = NameGenerator.generateMessages(r, 5,  nameVerifier, true);

        assertNotNull(names, "name returned was null");
        assertEquals(5, names.size(), "Fewer names generated than expected");

        boolean decorated = false;
        for (String name: names){
            if (name.contains("!") || name.contains("?") || name.contains(".") || name.contains("$")){
                decorated = true;
            }
        }
        assertTrue(decorated, "No messages have decorations: "+names);
    }

    @Test
    public void givenNameIsAcceptableAndDecoratedIsFalse_whenGenerateMessages_thenReturnMessagesHaveNoDecorationsTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isChannelNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isChannelNameTyped(any())).thenReturn(true);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true);
        Random r = mock(Random.class);
        when(r.nextInt(1,25)).thenReturn(6,14,26,12,4);
        when(r.nextInt(0,11)).thenReturn(5);

        List<String> names = NameGenerator.generateMessages(r, 5,  nameVerifier, false);

        assertNotNull(names, "name returned was null");
        assertEquals(5, names.size(), "Fewer names generated than expected");

        boolean decorated = false;
        for (String name: names){
            if (name.contains("!") || name.contains("?") || name.contains(".") || name.contains("$")){
                decorated = true;
            }
        }
        assertFalse(decorated, "Messages have decorations: "+names);
    }

    @Test
    public void givenNameIsAcceptableAndDecoratedAndSomeChannelsUntyped_whenGenerateMessages_thenReturnMessagesHaveDecorationsTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isChannelNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isChannelNameTyped(any())).thenReturn(true,false,true);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true,false,true);
        Random r = mock(Random.class);
        when(r.nextInt(1,25)).thenReturn(6,14,26,12,4);
        when(r.nextInt(0,11)).thenReturn(5);

        List<String> names = NameGenerator.generateMessages(r, 5,  nameVerifier, true);

        assertNotNull(names, "name returned was null");
        assertEquals(5, names.size(), "Fewer names generated than expected");

        boolean decorated = false;
        for (String name: names){
            if (name.contains("!") || name.contains("?") || name.contains(".") || name.contains("$")){
                decorated = true;
            }
        }
        assertTrue(decorated, "No messages have decorations: "+names);
    }

    @Test
    public void givenNameIsAcceptableAndDecoratedAndChannelsUntyped_whenGenerateMessages_thenReturnMessagesHaveNoDecorationsTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isChannelNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isChannelNameTyped(any())).thenReturn(false);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true,false,true);
        Random r = mock(Random.class);
        when(r.nextInt(1,25)).thenReturn(6,14,26,12,4);
        when(r.nextInt(0,11)).thenReturn(5);

        List<String> names = NameGenerator.generateMessages(r, 5,  nameVerifier, true);

        assertNotNull(names, "name returned was null");
        assertEquals(5, names.size(), "Fewer names generated than expected");

        boolean decorated = false;
        for (String name: names){
            if (name.contains("!") || name.contains("?") || name.contains(".") || name.contains("$")){
                decorated = true;
            }
        }
        assertFalse(decorated, "Messages have decorations: "+names);
    }

    @Test
    public void givenNameIsNotInitiallyAcceptableAndDecoratedIsFalse_whenGenerateMessages_thenReturnMessagesHaveNoDecorationsTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isChannelNameAcceptable(any())).thenReturn(false, false, true);
        when(nameVerifier.isChannelNameTyped(any())).thenReturn(false);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true,false,true);
        Random r = mock(Random.class);
        when(r.nextInt(1,25)).thenReturn(6,14,26,12,4);
        when(r.nextInt(0,11)).thenReturn(5);

        List<String> names = NameGenerator.generateMessages(r, 5, nameVerifier, false);

        assertNotNull(names, "name returned was null");
        assertEquals(5, names.size(), "Fewer names generated than expected");

        boolean decorated = false;
        for (String name: names){
            if (name.contains("!") || name.contains("?") || name.contains(".")){
                decorated = true;
            }
        }
        assertFalse(decorated, "Messages have decorations: "+names);
    }

    @Test
    public void givenNonEmptyList_whenGenerateGuardPairForBooleanParameter_thenGuardIsCreated() {
        String parameter = "l";
        String type = "Boolean";
        Random r = mock(Random.class);
        when(r.nextInt(0,3)).thenReturn(1);
        when(r.nextInt(0,2)).thenReturn(0);
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true, false, true);

        List<String> guards = NameGenerator.generateGuardPair(nameVerifier, r, parameter, type);

        assertEquals(2,guards.size(), "Incorrect number of guards generated");
        assertTrue(guards.getFirst().contains("=="),
                "Guard does not contain an equivalency check: "+ guards.getFirst());
        assertTrue(guards.getLast().contains("=="),
                "Guard does not contain an equivalency check: "+ guards.getLast());
        assertTrue(guards.getFirst().startsWith("("),
                "Guard does not start with a parenthesis: "+ guards.getFirst());
        assertTrue(guards.getLast().startsWith("("),
                "Guard does not start with a parenthesis: " + guards.getLast());
        assertTrue(guards.getFirst().endsWith(")"),
                "Guard does not start with a parenthesis: "+ guards.getFirst());
        assertTrue(guards.getLast().endsWith(")"),
                "Guard does not start with a parenthesis: " + guards.getLast());
        String[] guard1 = guards.getFirst().replace("(","")
                .replace(")","").split("==");
        String[] guard2 = guards.getLast().replace("(","")
                .replace(")","").split("==");
        assertNotEquals(guard1[1], guard2[1], "The values of guard1: "+guard1[1]+
                " and guard2: "+guard2[1]+" are equivalent");
        assertEquals("l", guard1[0], "The parameter of guard 1 is inaccurate");
        assertEquals("l", guard2[0], "The parameter of guard 2 is inaccurate");
    }

    @Test
    public void givenNonEmptyList_whenGenerateGuardPairForIntParameter_thenGuardIsCreated() {
        String parameter = "l";
        String type = "Int";
        Random r = mock(Random.class);
        when(r.nextInt(0,3)).thenReturn(1);
        when(r.nextInt(0,6)).thenReturn(3);
        when(r.nextInt(0,5)).thenReturn(4);
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true, false, true);

        List<String> guards = NameGenerator.generateGuardPair(nameVerifier, r, parameter, type);

        assertEquals(2,guards.size(), "Incorrect number of guards generated");
        assertTrue(guards.getFirst().contains("<"),
                "Guard does not contain an equivalency check: "+ guards.getFirst());
        assertTrue(guards.getLast().contains(">"),
                "Guard does not contain an equivalency check: "+ guards.getLast());
        assertTrue(guards.getFirst().startsWith("("),
                "Guard does not start with a parenthesis: "+ guards.getFirst());
        assertTrue(guards.getLast().startsWith("("),
                "Guard does not start with a parenthesis: " + guards.getLast());
        assertTrue(guards.getFirst().endsWith(")"),
                "Guard does not start with a parenthesis: "+ guards.getFirst());
        assertTrue(guards.getLast().endsWith(")"),
                "Guard does not start with a parenthesis: " + guards.getLast());
        String[] guard1 = guards.getFirst().replace("(","")
                .replace(")","").split("<");
        String[] guard2 = guards.getLast().replace("(","")
                .replace(")","").split(">");
        assertEquals(guard1[1], guard2[1], "The values of guard1: "+guard1[1]+
                " and guard2: "+guard2[1]+" are not equivalent");
        assertEquals("l", guard1[0], "The parameter of guard 1 is inaccurate");
        assertEquals("l", guard2[0], "The parameter of guard 2 is inaccurate");
    }

    @Test
    public void givenNonEmptyList_whenGenerateGuardPairForCharParameter_thenGuardIsCreated() {
        String parameter = "l";
        String type = "Char";
        Random r = mock(Random.class);
        when(r.nextInt(0,3)).thenReturn(1);
        when(r.nextInt(0,2)).thenReturn(1);
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true, false, true);

        List<String> guards = NameGenerator.generateGuardPair(nameVerifier, r, parameter, type);

        assertEquals(2,guards.size(), "Incorrect number of guards generated");
        assertTrue(guards.getFirst().contains("!="),
                "Guard does not contain an equivalency check: "+ guards.getFirst());
        assertTrue(guards.getLast().contains("!="),
                "Guard does not contain an equivalency check: "+ guards.getLast());
        assertTrue(guards.getFirst().startsWith("("),
                "Guard does not start with a parenthesis: "+ guards.getFirst());
        assertTrue(guards.getLast().startsWith("("),
                "Guard does not start with a parenthesis: " + guards.getLast());
        assertTrue(guards.getFirst().endsWith(")"),
                "Guard does not start with a parenthesis: "+ guards.getFirst());
        assertTrue(guards.getLast().endsWith(")"),
                "Guard does not start with a parenthesis: " + guards.getLast());
        String[] guard1 = guards.getFirst().replace("(","")
                .replace(")","").split("!=");
        String[] guard2 = guards.getLast().replace("(","")
                .replace(")","").split("!=");
        assertNotEquals(guard1[1], guard2[1], "The values of guard1: "+guard1[1]+
                " and guard2: "+guard2[1]+" are equivalent");
        assertEquals("l", guard1[0], "The parameter of guard 1 is inaccurate");
        assertEquals("l", guard2[0], "The parameter of guard 2 is inaccurate");
    }


    @Test
    public void givenNonEmptyList_whenGenerateGuardPairForEnumParameter_thenGuardIsCreated() {
        String parameter = "l";
        String type = "HoRmi";
        Random r = mock(Random.class);
        when(r.nextInt(0,3)).thenReturn(1);
        when(r.nextInt(0,2)).thenReturn(1);
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isTypeNameAcceptable(any())).thenReturn(true, false, true);

        List<String> guards = NameGenerator.generateGuardPair(nameVerifier, r, parameter, type);

        assertEquals(2,guards.size(), "Incorrect number of guards generated");
        assertTrue(guards.getFirst().contains("!="),
                "Guard does not contain an equivalency check: "+ guards.getFirst());
        assertTrue(guards.getLast().contains("!="),
                "Guard does not contain an equivalency check: "+ guards.getLast());
        assertTrue(guards.getFirst().startsWith("("),
                "Guard does not start with a parenthesis: "+ guards.getFirst());
        assertTrue(guards.getLast().startsWith("("),
                "Guard does not start with a parenthesis: " + guards.getLast());
        assertTrue(guards.getFirst().endsWith(")"),
                "Guard does not start with a parenthesis: "+ guards.getFirst());
        assertTrue(guards.getLast().endsWith(")"),
                "Guard does not start with a parenthesis: " + guards.getLast());
        String[] guard1 = guards.getFirst().replace("(","")
                .replace(")","").split("!=");
        String[] guard2 = guards.getLast().replace("(","")
                .replace(")","").split("!=");
        assertNotEquals(guard1[1], guard2[1], "The values of guard1: "+guard1[1]+
                " and guard2: "+guard2[1]+" are equivalent");
        assertEquals("l", guard1[0], "The parameter of guard 1 is inaccurate");
        assertEquals("l", guard2[0], "The parameter of guard 2 is inaccurate");
    }

}
