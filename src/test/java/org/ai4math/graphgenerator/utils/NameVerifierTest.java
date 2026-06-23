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
    public void givenAcceptableName_whenIsTypeNameAcceptable_thenReturnTrue(){
        String typeName = "typeName";
        NameVerifier nameVerifier = new NameVerifier();
        List<String> typeNames = nameVerifier.getTypeNames();
        assertTrue(typeNames.isEmpty());
        boolean acceptable = nameVerifier.isTypeNameAcceptable(typeName);

        typeNames = nameVerifier.getTypeNames();
        assertTrue(acceptable, "Name not acceptable");
        assertTrue(typeNames.contains(typeName), "Type do not contains typeName");
        assertEquals(1, nameVerifier.getTypeNames().size(), "More Type names than correct");

    }

    @Test
    public void givenExistingTypeName_whenIsTypeNameAcceptable_thenReturnTrue(){
        String typeName = "typeName";
        NameVerifier nameVerifier = new NameVerifier();
        List<String> typeNames = nameVerifier.getTypeNames();
        assertTrue(typeNames.isEmpty());
        nameVerifier.setTypeNames(List.of(typeName));
        boolean acceptable = nameVerifier.isTypeNameAcceptable(typeName);

        typeNames = nameVerifier.getTypeNames();
        assertTrue(acceptable, "Name not acceptable");
        assertTrue(typeNames.contains(typeName), "Type do not contain typeName");
        assertEquals(1, nameVerifier.getTypeNames().size(), "More Type names than correct");

    }

    @Test
    public void givenKeywordName_whenIsTypeNameAcceptable_thenReturnFalse(){
        String typeName = "STOP";
        NameVerifier nameVerifier = new NameVerifier();
        boolean acceptable = nameVerifier.isTypeNameAcceptable(typeName);
        List<String> typeNames = nameVerifier.getTypeNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(typeNames.contains(typeName), "Type contains processName");
        assertEquals(0, nameVerifier.getTypeNames().size(), "More Type names than correct");

    }

    @Test
    public void givenProcessName_whenIsTypeNameAcceptable_thenReturnFalse(){
        String typeName = "processName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setProcessNames(List.of(typeName));

        boolean acceptable = nameVerifier.isTypeNameAcceptable(typeName);
        List<String> typeNames = nameVerifier.getTypeNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(typeNames.contains(typeName), "Type contains processName");
        assertEquals(0, nameVerifier.getTypeNames().size(), "More Type names than correct");
    }

    @Test
    public void givenChannelName_whenIsTypeNameAcceptable_thenReturnFalse(){
        String typeName = "channelConstantName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setChannelNames(List.of(typeName));

        boolean acceptable = nameVerifier.isTypeNameAcceptable(typeName);
        List<String> typeNames = nameVerifier.getTypeNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(typeNames.contains(typeName), "Type contains processName");
        assertEquals(0, nameVerifier.getTypeNames().size(), "More Type names than correct");
    }

    @Test
    public void givenParameterName_whenIsTypeNameAcceptable_thenReturnFalse(){
        String typeName = "parameterName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setParameterNames(List.of(typeName));

        boolean acceptable = nameVerifier.isTypeNameAcceptable(typeName);
        List<String> typeNames = nameVerifier.getTypeNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(typeNames.contains(typeName), "Types contains processName");
        assertEquals(0, nameVerifier.getTypeNames().size(), "More type names than correct");
    }


    @Test
    public void givenAcceptableName_whenIsParameterNameAcceptable_thenReturnTrue(){
        String parameterName = "parameterName";
        NameVerifier nameVerifier = new NameVerifier();
        List<String> parameterNames = nameVerifier.getParameterNames();
        assertTrue(parameterNames.isEmpty());
        boolean acceptable = nameVerifier.isParameterNameAcceptable(parameterName);

        parameterNames = nameVerifier.getParameterNames();
        assertTrue(acceptable, "Name not acceptable");
        assertTrue(parameterNames.contains(parameterName), "Parameters do not contains parameterName");
        assertEquals(1, nameVerifier.getParameterNames().size(), "More Parameters names than correct");

    }

    @Test
    public void givenExistingParameterName_whenIsParameterNameAcceptable_thenReturnTrue(){
        String parameterName = "parameterName";
        NameVerifier nameVerifier = new NameVerifier();
        List<String> parameterNames = nameVerifier.getParameterNames();
        assertTrue(parameterNames.isEmpty());
        nameVerifier.setParameterNames(List.of(parameterName));
        boolean acceptable = nameVerifier.isParameterNameAcceptable(parameterName);

        parameterNames = nameVerifier.getParameterNames();
        assertTrue(acceptable, "Name not acceptable");
        assertTrue(parameterNames.contains(parameterName), "Parameters do not contain parameterName");
        assertEquals(1, nameVerifier.getParameterNames().size(), "More Parameters names than correct");

    }

    @Test
    public void givenKeywordName_whenIsParameterNameAcceptable_thenReturnFalse(){
        String parameterName = "STOP";
        NameVerifier nameVerifier = new NameVerifier();
        boolean acceptable = nameVerifier.isParameterNameAcceptable(parameterName);
        List<String> parameterNames = nameVerifier.getParameterNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(parameterNames.contains(parameterName), "Parameters contains processName");
        assertEquals(0, nameVerifier.getParameterNames().size(), "More Parameters names than correct");

    }

    @Test
    public void givenProcessName_whenIsParameterNameAcceptable_thenReturnFalse(){
        String parameterName = "processName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setProcessNames(List.of(parameterName));

        boolean acceptable = nameVerifier.isParameterNameAcceptable(parameterName);
        List<String> parameterNames = nameVerifier.getParameterNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(parameterNames.contains(parameterName), "Parameters contains processName");
        assertEquals(0, nameVerifier.getParameterNames().size(), "More Parameters names than correct");
    }

    @Test
    public void givenChannelName_whenIsParameterNameAcceptable_thenReturnFalse(){
        String parameterName = "channelConstantName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setChannelNames(List.of(parameterName));

        boolean acceptable = nameVerifier.isParameterNameAcceptable(parameterName);
        List<String> parameterNames = nameVerifier.getParameterNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(parameterNames.contains(parameterName), "Parameters contains processName");
        assertEquals(0, nameVerifier.getParameterNames().size(), "More Parameters names than correct");
    }

    @Test
    public void givenTypeName_whenIsParameterNameAcceptable_thenReturnFalse(){
        String parameterName = "parameterName";
        NameVerifier nameVerifier = new NameVerifier();
        nameVerifier.setTypeNames(List.of(parameterName));

        boolean acceptable = nameVerifier.isParameterNameAcceptable(parameterName);
        List<String> parameterNames = nameVerifier.getParameterNames();

        assertFalse(acceptable, "Name not acceptable");
        assertFalse(parameterNames.contains(parameterName), "Parameters contains processName");
        assertEquals(0, nameVerifier.getParameterNames().size(), "More Parameters names than correct");
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
