package org.ai4math.graphgenerator.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void givenNameIsAcceptable_whenGenerateMessages_thenReturnMessagesTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isChannelNameAcceptable(any())).thenReturn(true);

        List<String> names = NameGenerator.generateMessages(5, nameVerifier);

        assertNotNull(names, "name returned was null");
        assertEquals(5, names.size(), "Fewer names generated than expected");
    }

    @Test
    public void givenNameIsNotInitiallyAcceptable_whenGenerateMessages_thenReturnMessagesTest(){
        NameVerifier nameVerifier = mock(NameVerifier.class);
        when(nameVerifier.isChannelNameAcceptable(any())).thenReturn(false, false, true);

        List<String> names = NameGenerator.generateMessages(5, nameVerifier);

        assertNotNull(names, "name returned was null");
        assertEquals(5, names.size(), "Fewer names generated than expected");
    }

}
