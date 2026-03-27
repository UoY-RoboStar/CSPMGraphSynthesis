package org.ai4math.graphgenerator.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NameVerifierTest {

    @Test
    public void givenAcceptableName_whenIsProcessNameAcceptable_thenReturnTrue(){
        String processName = "processName";
        NameVerifier nameVerifier = new NameVerifier();
        boolean acceptable = nameVerifier.isProcessNameAcceptable(processName);

        assertTrue(acceptable, "Name not acceptable");
        assertTrue(nameVerifier.getProcessNames().contains(processName), "Processes don't contain processName");
        assertEquals(1, nameVerifier.getProcessNames().size(), "More process names than correct");
    }

    @Test
    public void givenKeywordName_whenIsProcessNameAcceptable_thenReturnFalse(){
        String processName = "if";
        NameVerifier nameVerifier = new NameVerifier();
        boolean acceptable = nameVerifier.isProcessNameAcceptable(processName);

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(nameVerifier.getProcessNames().contains(processName), "Processes contains if");
        assertEquals(0, nameVerifier.getProcessNames().size(), "More process names than correct");

    }

    @Test
    public void givenChannelName_whenIsProcessNameAcceptable_thenReturnFalse(){
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.isChannelNameAcceptable(channelName);
        assertTrue(nameVerifier.getChannelNames().contains(channelName), "Channels don't contain channelName");

        boolean acceptable = nameVerifier.isProcessNameAcceptable(channelName);

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(nameVerifier.getProcessNames().contains(channelName), "Processes contains channelName");
        assertEquals(0, nameVerifier.getProcessNames().size(), "More process names than correct");

    }

    @Test
    public void givenProcessName_whenIsProcessNameAcceptable_thenReturnFalse(){
        String processName = "processName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.isProcessNameAcceptable(processName);
        assertTrue(nameVerifier.getProcessNames().contains(processName), "Processes don't contain processName");

        boolean acceptable = nameVerifier.isProcessNameAcceptable(processName);

        assertFalse(acceptable, "Name not acceptable");
        assertEquals(1, nameVerifier.getProcessNames().size(), "More process names than correct");
    }

    @Test
    public void givenAcceptableName_whenIsChannelNameAcceptable_thenReturnTrue(){
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        boolean acceptable = nameVerifier.isChannelNameAcceptable(channelName);

        assertTrue(acceptable, "Name not acceptable");
        assertTrue(nameVerifier.getChannelNames().contains(channelName), "Channels don't contain channelName");
        assertEquals(1, nameVerifier.getChannelNames().size(), "More channel names than correct");

    }

    @Test
    public void givenKeywordName_whenIsChannelNameAcceptable_thenReturnFalse(){
        String channelName = "STOP";
        NameVerifier nameVerifier = new NameVerifier();
        boolean acceptable = nameVerifier.isChannelNameAcceptable(channelName);

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(nameVerifier.getChannelNames().contains(channelName), "Channels contains STOP");
        assertEquals(0, nameVerifier.getChannelNames().size(), "More channel names than correct");

    }

    @Test
    public void givenProcessName_whenIsChannelNameAcceptable_thenReturnFalse(){
        String processName = "processName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.isProcessNameAcceptable(processName);
        assertTrue(nameVerifier.getProcessNames().contains(processName), "Channels doesn't contain processName");

        boolean acceptable = nameVerifier.isChannelNameAcceptable(processName);

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(nameVerifier.getChannelNames().contains(processName), "Channels contains processName");
        assertEquals(0, nameVerifier.getChannelNames().size(), "More channel names than correct");
    }
}
