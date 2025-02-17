package saltsheep.ssl.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import saltsheep.ssl.script.ScriptLoader;

import java.util.Map;

public class SheepScriptLibPlugin
        implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass() {
        return new String[]{"saltsheep.ssl.puppet.asm.Trans", "saltsheep.ssl.core.Trans"};
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> map) {
        ScriptLoader.loader.loadCore();
    }

    public String getAccessTransformerClass() {
        return null;
    }
}