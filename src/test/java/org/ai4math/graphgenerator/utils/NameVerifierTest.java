package org.ai4math.graphgenerator.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NameVerifierTest {

    @Test
    public void givenAcceptableName_whenIsProcessNameAcceptable_thenReturnTrue(){
        String processName = "processName";
        NameVerifier nameVerifier = new NameVerifier();
        List<String> processNames = nameVerifier.getProcessNames();
        assertTrue(processNames.isEmpty());
        boolean acceptable = nameVerifier.isProcessNameAcceptable(processName);

        processNames = nameVerifier.getProcessNames();
        assertTrue(acceptable, "Name not acceptable");
        assertTrue(processNames.contains(processName), "Processes don't contain processName");
        assertEquals(1, processNames.size(), "More process names than correct");
    }

    @Test
    public void givenKeywordName_whenIsProcessNameAcceptable_thenReturnFalse(){
        String processName = "if";
        NameVerifier nameVerifier = new NameVerifier();
        boolean acceptable = nameVerifier.isProcessNameAcceptable(processName);
        List<String> processNames = nameVerifier.getProcessNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(processNames.contains(processName), "Processes contains if");
        assertEquals(0, processNames.size(), "More process names than correct");

    }

    @Test
    public void givenChannelName_whenIsProcessNameAcceptable_thenReturnFalse(){
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.isChannelNameAcceptable(channelName);
        List<String> channelNames = nameVerifier.getChannelNames();
        assertTrue(channelNames.contains(channelName), "Channels don't contain channelName");

        boolean acceptable = nameVerifier.isProcessNameAcceptable(channelName);
        List<String> processNames = nameVerifier.getProcessNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(processNames.contains(channelName), "Processes contains channelName");
        assertEquals(0, nameVerifier.getProcessNames().size(), "More process names than correct");

    }

    @Test
    public void givenProcessName_whenIsProcessNameAcceptable_thenReturnFalse(){
        String processName = "processName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.isProcessNameAcceptable(processName);
        List<String> processNames = nameVerifier.getProcessNames();
        assertTrue(processNames.contains(processName), "Processes don't contain processName");

        boolean acceptable = nameVerifier.isProcessNameAcceptable(processName);

        assertFalse(acceptable, "Name not acceptable");
        assertEquals(1, processNames.size(), "More process names than correct");
    }

    @Test
    public void givenAcceptableName_whenIsChannelNameAcceptable_thenReturnTrue(){
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        List<String> channelNames = nameVerifier.getChannelNames();
        assertTrue(channelNames.isEmpty());
        boolean acceptable = nameVerifier.isChannelNameAcceptable(channelName);

        channelNames = nameVerifier.getChannelNames();
        assertTrue(acceptable, "Name not acceptable");
        assertTrue(channelNames.contains(channelName), "Channels don't contain channelName");
        assertEquals(1, nameVerifier.getChannelNames().size(), "More channel names than correct");

    }

    @Test
    public void givenExistingChannelName_whenIsChannelNameAcceptable_thenReturnTrue(){
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        List<String> channelNames = nameVerifier.getChannelNames();
        assertTrue(channelNames.isEmpty());
        nameVerifier.isChannelNameAcceptable(channelName);
        channelNames = nameVerifier.getChannelNames();
        assertTrue(channelNames.contains(channelName));
        boolean acceptable = nameVerifier.isChannelNameAcceptable(channelName);

        channelNames = nameVerifier.getChannelNames();
        assertTrue(acceptable, "Name not acceptable");
        assertTrue(channelNames.contains(channelName), "Channels don't contain channelName");
        assertEquals(1, nameVerifier.getChannelNames().size(), "More channel names than correct");

    }

    @Test
    public void givenKeywordName_whenIsChannelNameAcceptable_thenReturnFalse(){
        String channelName = "STOP";
        NameVerifier nameVerifier = new NameVerifier();
        boolean acceptable = nameVerifier.isChannelNameAcceptable(channelName);
        List<String> channelNames = nameVerifier.getChannelNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(channelNames.contains(channelName), "Channels contains STOP");
        assertEquals(0, nameVerifier.getChannelNames().size(), "More channel names than correct");

    }

    @Test
    public void givenProcessName_whenIsChannelNameAcceptable_thenReturnFalse(){
        String processName = "processName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.isProcessNameAcceptable(processName);
        List<String> processNames = nameVerifier.getProcessNames();
        assertTrue(processNames.contains(processName), "Channels doesn't contain processName");

        boolean acceptable = nameVerifier.isChannelNameAcceptable(processName);
        List<String> channelNames = nameVerifier.getChannelNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(channelNames.contains(processName), "Channels contains processName");
        assertEquals(0, nameVerifier.getChannelNames().size(), "More channel names than correct");
    }
}
