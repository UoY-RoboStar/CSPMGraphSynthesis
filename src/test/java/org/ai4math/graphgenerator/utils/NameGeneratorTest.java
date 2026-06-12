package org.ai4math.graphgenerator.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
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
    public void givenNameIsAcceptableAndDecorated_whenGenerateMessages_thenReturnMessagesIncludesDecorationsTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isChannelNameAcceptable(any())).thenReturn(true);
        when(nameVerifier.isChannelNameTyped(any())).thenReturn(true);

        List<String> names = NameGenerator.generateMessages(new Random(), 40, nameVerifier, true);

        assertNotNull(names, "name returned was null");
        assertEquals(40, names.size(), "Fewer names generated than expected");

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

        List<String> names = NameGenerator.generateMessages(new Random(), 40, nameVerifier, true);

        assertNotNull(names, "name returned was null");
        assertEquals(40, names.size(), "Fewer names generated than expected");

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

        List<String> names = NameGenerator.generateMessages(new Random(), 40, nameVerifier, false);

        assertNotNull(names, "name returned was null");
        assertEquals(40, names.size(), "Fewer names generated than expected");

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

        List<String> names = NameGenerator.generateMessages(new Random(), 40, nameVerifier, true);

        assertNotNull(names, "name returned was null");
        assertEquals(40, names.size(), "Fewer names generated than expected");

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

        List<String> names = NameGenerator.generateMessages(new Random(), 40, nameVerifier, true);

        assertNotNull(names, "name returned was null");
        assertEquals(40, names.size(), "Fewer names generated than expected");

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

        List<String> names = NameGenerator.generateMessages(new Random(), 40, nameVerifier, false);

        assertNotNull(names, "name returned was null");
        assertEquals(40, names.size(), "Fewer names generated than expected");

        boolean decorated = false;
        for (String name: names){
            if (name.contains("!") || name.contains("?") || name.contains(".")){
                decorated = true;
            }
        }
        assertFalse(decorated, "Messages have decorations: "+names);
    }

    @Test
    public void givenNonEmptyList_whenGenerateGuardForBooleanChannel_thenGuardIsCreated() {
        List<String> messages = List.of("one!false","two?'a'","three.4","four");
        Random r = mock(Random.class);
        when(r.nextInt(0,3)).thenReturn(0);
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isConstantNameAcceptable(any(),any())).thenReturn(true, false, true);


        String guard = NameGenerator.generateGuard(r, nameVerifier, messages);

        assertFalse(messages.contains(guard), "Guard is contained in the messages list: "+guard);
        assertFalse(guard.contains("=="), "Guard contains an equivalency check: "+guard);
        assertFalse(guard.equals("true")||guard.equals("false"), "Guard defined as boolean: "+guard);
    }

    @Test
    public void givenNonEmptyList_whenGenerateGuardForIntChannel_thenGuardIsCreated() {
        List<String> messages = List.of("one!false","two?'a'","three.4","four");
        Random r = mock(Random.class);
        when(r.nextInt(0,3)).thenReturn(2);
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isConstantNameAcceptable(any(),any())).thenReturn(true, false, true);

        String guard = NameGenerator.generateGuard(r, nameVerifier, messages);

        assertFalse(messages.contains(guard), "Guard is contained in the messages list: "+guard);
        assertTrue(guard.contains("==4"), "Guard does not contain an equivalency check: "+guard);
        assertTrue(guard.length()>3, "Guard length is too short: "+guard);
    }

    @Test
    public void givenNonEmptyList_whenGenerateGuardForCharChannel_thenGuardIsCreated() {
        List<String> messages = List.of("one!false","two?'a'","three.4","four");
        Random r = mock(Random.class);
        when(r.nextInt(0,3)).thenReturn(1);
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isConstantNameAcceptable(any(),any())).thenReturn(true, false, true);


        String guard = NameGenerator.generateGuard(r, nameVerifier, messages);

        assertFalse(messages.contains(guard), "Guard is contained in the messages list: "+guard);
        assertTrue(guard.contains("=='a'"), "Guard does not contain an equivalency check: "+guard);
        assertTrue(guard.length()>5, "Guard length is too short: "+guard);
    }

}
