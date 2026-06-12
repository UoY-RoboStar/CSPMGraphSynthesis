package org.ai4math.graphgenerator.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
        nameVerifier.setChannelNames(List.of(channelName));

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
        nameVerifier.setProcessNames(List.of(processName));

        boolean acceptable = nameVerifier.isProcessNameAcceptable(processName);
        List<String> processNames = nameVerifier.getProcessNames();

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
        nameVerifier.setChannelNames(List.of(channelName));

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
        nameVerifier.setProcessNames(List.of(processName));

        boolean acceptable = nameVerifier.isChannelNameAcceptable(processName);
        List<String> channelNames = nameVerifier.getChannelNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(channelNames.contains(processName), "Channels contains processName");
        assertEquals(0, nameVerifier.getChannelNames().size(), "More channel names than correct");
    }

    @Test
    public void givenAcceptableName_whenIsConstantNameAcceptable_thenReturnTrue(){
        String constantName = "constantName";
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        Map<String,String> constantNames = nameVerifier.getConstantNames();
        assertTrue(constantNames.isEmpty());
        boolean acceptable = nameVerifier.isConstantNameAcceptable(constantName,channelName);

        constantNames = nameVerifier.getConstantNames();
        assertTrue(acceptable, "Name not acceptable");
        assertTrue(constantNames.containsKey(constantName), "Constant do not contains constantName");
        assertEquals(channelName, constantNames.get(constantName),
                "The channel name and constant name are not matched.");
        assertEquals(1, nameVerifier.getConstantNames().size(), "More constant names than correct");

    }

    @Test
    public void givenExistingChannelName_whenIsConstantNameAcceptable_thenReturnTrue(){
        String constantName = "constantName";
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        Map<String,String> constantNames = nameVerifier.getConstantNames();
        assertTrue(constantNames.isEmpty());
        nameVerifier.setConstantNames(Map.of(constantName,channelName));
        boolean acceptable = nameVerifier.isConstantNameAcceptable(constantName,channelName);

        constantNames = nameVerifier.getConstantNames();
        assertTrue(acceptable, "Name not acceptable");
        assertTrue(constantNames.containsKey(constantName), "Constants do not contain constantName");
        assertEquals(channelName, constantNames.get(constantName),
                "The channel name and constant name are not matched.");
        assertEquals(1, nameVerifier.getConstantNames().size(), "More constant names than correct");

    }

    @Test
    public void givenKeywordName_whenIsConstantNameAcceptable_thenReturnFalse(){
        String constantName = "STOP";
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        boolean acceptable = nameVerifier.isConstantNameAcceptable(constantName,channelName);
        Map<String,String> constantNames = nameVerifier.getConstantNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(constantNames.containsKey(constantName), "Constants contains processName");
        assertEquals(0, nameVerifier.getConstantNames().size(), "More constant names than correct");

    }

    @Test
    public void givenProcessName_whenIsConstantNameAcceptable_thenReturnFalse(){
        String processName = "processName";
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setProcessNames(List.of(processName));

        boolean acceptable = nameVerifier.isConstantNameAcceptable(processName,channelName);
        Map<String,String> constantNames = nameVerifier.getConstantNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(constantNames.containsKey(processName), "Constants contains processName");
        assertEquals(0, nameVerifier.getConstantNames().size(), "More constant names than correct");
    }

    @Test
    public void givenChannelName_whenIsConstantNameAcceptable_thenReturnFalse(){
        String channelConstantName = "channelConstantName";
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setChannelNames(List.of(channelConstantName));

        boolean acceptable = nameVerifier.isConstantNameAcceptable(channelConstantName,channelName);
        Map<String,String> constantNames = nameVerifier.getConstantNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(constantNames.containsKey(channelConstantName), "Constants contains processName");
        assertEquals(0, nameVerifier.getConstantNames().size(), "More constant names than correct");
    }

    @Test
    public void givenTypedChannelName_whenIsChannelNameTyped_thenReturnTrue(){
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setChannelTyped(Map.of(channelName,true));

        boolean typed = nameVerifier.isChannelNameTyped(channelName);

        assertTrue(typed, "Name not typed");
    }

    @Test
    public void givenUntypedChannelName_whenIsChannelNameTyped_thenReturnFalse(){
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setChannelTyped(Map.of(channelName,false));

        boolean typed = nameVerifier.isChannelNameTyped(channelName);

        assertFalse(typed, "Name typed");
    }

    @Test
    public void givenUnknownChannelName_whenIsChannelNameTyped_thenReturnFalse(){
        String channelName = "channelName";
        String unknownName = "unknown";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setChannelTyped(Map.of(channelName,false));

        boolean typed = nameVerifier.isChannelNameTyped(unknownName);

        assertTrue(typed, "Name is not typed");
    }

    @Test
    public void givenChannelName_whenSetChannelNameTyped_thenChannelAddedToMap(){
        String channelConstantName = "channelConstantName";
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();

        nameVerifier.setChannelNameTyped(channelConstantName,true);
        nameVerifier.setChannelNameTyped(channelName,false);
        Map<String,Boolean> channelNames = nameVerifier.getChannelTyped();

        assertTrue(channelNames.containsKey(channelConstantName), "ChannelConstant not recorded");
        assertTrue(channelNames.get(channelConstantName), "ChannelConstant not recorded as typed");
        assertTrue(channelNames.containsKey(channelName), "Channel recorded");
        assertFalse(channelNames.get(channelName), "Channel recorded as typed");
        assertEquals(2, nameVerifier.getChannelTyped().size(), "More channel names than correct");
    }

    @Test
    public void givenRecordedChannelName_whenSetChannelNameTyped_thenChannelMapNotUpdated(){
        String channelConstantName = "channelConstantName";
        String channelName = "channelName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setChannelTyped(Map.of(channelConstantName, true, channelName,false));

        nameVerifier.setChannelNameTyped(channelName,true);
        Map<String,Boolean> channelNames = nameVerifier.getChannelTyped();

        assertTrue(channelNames.containsKey(channelConstantName), "ChannelConstant not recorded");
        assertTrue(channelNames.get(channelConstantName), "ChannelConstant not recorded as typed");
        assertTrue(channelNames.containsKey(channelName), "Channel recorded");
        assertFalse(channelNames.get(channelName), "Channel recorded as typed");
        assertEquals(2, nameVerifier.getChannelTyped().size(), "More channel names than correct");
    }
}
