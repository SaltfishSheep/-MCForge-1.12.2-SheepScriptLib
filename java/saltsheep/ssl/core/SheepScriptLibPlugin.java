package saltsheep.ssl.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

public class SheepScriptLibPlugin
        implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass() {
        return new String[]{"saltsheep.ssl.puppet.asm.Trans"};
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> map) {
    }

    public String getAccessTransformerClass() {
        return null;
    }
}