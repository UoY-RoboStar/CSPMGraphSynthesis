package org.ai4math.cspmtransformer.utils;

import static org.ai4math.cspm.Keywords.LAMBDA;

import static org.ai4math.cspm.Keywords.TICK;


public class StringConstants {

    public static String processDeclaration() {
        return "processName = processDefinition";
    }

    public static String channelDeclaration() {
        return "channel name\n";
    }

    public static String channelTypedDeclaration() {
        return "channel name : type\n";
    }

    public static String variableDeclaration() {
        return "channel setVariableName : type\nchannel getVariableName : type\n";
    }

    public static String constantDeclaration() {
        return "name = value\n";
    }

    public static String include(){
        return "include \"importURI\"\n";
    }
    public static String transparent(){
        return "transparent name\n";
    }
    public static String exports(){
        return "external name\n";
    }

    ///*****************************************
    ///   ASSERTIONS
    ///*****************************************

    public static String assertDeclaration(){
        return "assert process assertion\n";
    }

    public static String deadlockAssertion(){
        return ":[deadlock free]";
    }

    /// graph to CSP constants
    public static  String sigmaEdge() {
        return TICK+"(processName)";
    }
    public static  String lambdaEdge() {
        return LAMBDA;
    }

}
