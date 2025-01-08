package saltsheep.ssl.api;

import saltsheep.ssl.script.ScriptLoader;

import javax.script.ScriptEngine;


public class Common {
    public static Object station;

    static {
        try {
            ScriptEngine engine = ScriptLoader.engineFactory.getScriptEngine();
            engine.eval("var Common = Java.type(\"saltsheep.ssl.api.Common\");Common.station=new Object();");
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }
}