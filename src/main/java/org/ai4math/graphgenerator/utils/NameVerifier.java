package org.ai4math.graphgenerator.utils;

import org.ai4math.cspm.Keywords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.ai4math.cspm.Keywords.*;

public class NameVerifier {

    private List<String> processNames = new ArrayList<>();
    private List<String> channelNames = new ArrayList<>();
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
            TRACES, FAILURES_DIVERGENCES);

    public boolean isProcessNameAcceptable(String name){
        if (this.channelNames.contains(name) || this.processNames.contains(name)
                || this.keywords.contains(name)){
            return false;
        }

        List<String> processes = new ArrayList<String>(this.processNames);
        processes.add(name);
        this.processNames = processes;
        return true;
    }

    public boolean isChannelNameAcceptable(String name){
        if (this.processNames.contains(name) || this.keywords.contains(name)){
            return false;
        }

        List<String> channels = new ArrayList<String>(this.channelNames);
        channels.add(name);
        this.channelNames = channels;
        return true;
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
}
