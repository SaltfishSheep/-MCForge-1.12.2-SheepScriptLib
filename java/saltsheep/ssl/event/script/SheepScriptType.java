package saltsheep.ssl.event.script;

import net.minecraftforge.common.util.EnumHelper;
import noppes.npcs.constants.EnumScriptType;

public class SheepScriptType {
    public static final EnumScriptType KEY_DOWN = EnumHelper.addEnum(EnumScriptType.class, "KEY_DOWN", new Class[]{String.class}, new Object[]{"keyDown"});
    public static final EnumScriptType KEY_UP = EnumHelper.addEnum(EnumScriptType.class, "KEY_UP", new Class[]{String.class}, new Object[]{"keyUp"});
}