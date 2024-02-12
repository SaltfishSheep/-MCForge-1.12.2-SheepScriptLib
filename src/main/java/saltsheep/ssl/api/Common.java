package saltsheep.ssl.api;

import noppes.npcs.controllers.ScriptController;
import saltsheep.ssl.script.ScriptLoader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Common {
    public static Object station;
    static{
        try {
            ScriptEngine engine = ScriptLoader.engineFactory.getScriptEngine();
            engine.eval("var Common = Java.type(\"saltsheep.ssl.api.Common\");Common.station=new Object();");
        }catch (Throwable error){
            error.printStackTrace();
        }
    }
}
