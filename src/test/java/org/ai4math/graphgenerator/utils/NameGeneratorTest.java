package org.ai4math.graphgenerator.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

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

        List<String> names = NameGenerator.generateMessages(40, nameVerifier, true);

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

        List<String> names = NameGenerator.generateMessages(40, nameVerifier, true);

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

        List<String> names = NameGenerator.generateMessages(40, nameVerifier, false);

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

        List<String> names = NameGenerator.generateMessages(40, nameVerifier, false);

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

}
