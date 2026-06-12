package org.ai4math.graphgenerator.utils;

import org.ai4math.cspm.Keywords;

import java.util.*;

import static org.ai4math.cspm.Keywords.*;

/**
 *
 */
public class NameVerifier {

    private List<String> processNames = new ArrayList<>();
    private List<String> channelNames = new ArrayList<>();
    private Map<String, Boolean> channelTyped = new HashMap<>();
    private Map<String,String> constantNames = new HashMap<>();
    private final List<String> keywords = Arrays.asList(CHAOS, SKIP, STOP, DIV, LOOP, RUN, WAIT, MODULE,
            END_MODULE, EXTERNAL, EXPORTS, TRANSPARENT, INCLUDE, CHANNEL, DATATYPE, NAMETYPE, PRINT,
            ASSERT, NOT, AND, OR, IF, THEN, ELSE, LET, WITHIN, INSTANCE, TIMED, TYPE, LITTLE_FALSE,
            LITTLE_TRUE, TRUE, FALSE, BOOL, CHAR, EVENTS, INT, PROC, CARD, DIFF, EMPTY, LITTLE_INTER,
            INTER, MEMBER, SEQ, SET, LITTLE_UNION, UNION, CONCAT, ELEM, HEAD, LENGTH, NULL, LITTLE_SET,
            TAIL, EMPTY_MAP, MAP_DELETE, MAP_FROM_LIST, MAP_LOOKUP, MAP_MEMBER, MAP_TO_LIST,
            MAP_UPDATE, MAP_UPDATE_MULTIPLE, MAP, ERROR, SHOW, CHASE, CHASE_NO_CACHE, DETER,
            DIAMOND, DBISIM, LAZY_ENUMERATE, EXPLICATE, FAILURE_WATCHDOG, NORMAL, PRIORITISE,
            PRIORITISE_NO_CACHE, PRIORITISE_PO, SBISIM, TAU_LOOP_FACTOR, TRACE_WATCHDOG, TIMED_PRIORITY,
            WBISIM, MTRANSCLOSE, RELATIONAL_IMAGE, RELATIONAL_INVERSE_IMAGE, TRANSPOSE,
            TICK, LAMBDA, TAU, ONESTEP, DEADLOCK, DIVERGENCE, DETERMINISTIC, HAS, FREE, FAILURES,
            TRACES, FAILURES_DIVERGENCES, TYPE_PLACEHOLDER);


    public boolean isProcessNameAcceptable(String name) {
        if (this.channelNames.contains(name) || this.processNames.contains(name)
                || this.keywords.contains(name)){
            return false;
        }

        List<String> processes = new ArrayList<String>(this.processNames);
        processes.add(name);
        this.processNames = processes;
        return true;
    }

    public boolean isChannelNameAcceptable(String name) {
        if (this.processNames.contains(name) || this.keywords.contains(name)){
            return false;
        } else if (this.channelNames.contains(name)) {
            return true;
        }

        List<String> channels = new ArrayList<String>(this.channelNames);
        channels.add(name);
        this.channelNames = channels;
        return true;
    }

    public boolean isConstantNameAcceptable(String name, String channel) {
        if (this.processNames.contains(name) || this.keywords.contains(name) || this.channelNames.contains(name)){
            return false;
        } else if (this.constantNames.containsKey(name)
                &&this.constantNames.get(name).equals(channel)){
            return true;
        }

        Map<String,String> constants = new HashMap<>(this.constantNames);
        constants.put(name,channel);
        this.constantNames = constants;
        return true;
    }

    public boolean isChannelNameTyped(String channel) {
        if (this.channelTyped.containsKey(channel)){
            return this.channelTyped.get(channel);
        }

        // used to indicate that the channel *can* be typed
        return true;
    }

    public void setChannelNameTyped(String channel, Boolean typed) {
        if(!this.channelTyped.containsKey(channel)){
            Map<String, Boolean> typedChannels = new HashMap<>(this.channelTyped);
            typedChannels.put(channel, typed);
            this.channelTyped = typedChannels;
        }
    }

    public List<String> getChannelNames() {
        return channelNames;
    }

    public void setChannelNames(List<String> channelNames) {
        this.channelNames = channelNames;
    }

    public List<String> getProcessNames() {
        return processNames;
    }

    public void setProcessNames(List<String> processNames) {
        this.processNames = processNames;
    }

    public Map<String,String> getConstantNames() {
        return constantNames;
    }

    public void setConstantNames(Map<String,String> constantNames) {
        this.constantNames = constantNames;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setChannelTyped(Map<String, Boolean> channelTyped) {
        this.channelTyped = channelTyped;
    }

    public Map<String, Boolean> getChannelTyped() {
        return channelTyped;
    }
}
