package saltsheep.ssl.event.script;

import net.minecraftforge.common.util.EnumHelper;
import noppes.npcs.constants.EnumScriptType;

public class SheepScriptType {

	public static final EnumScriptType KEY_DOWN;
	public static final EnumScriptType KEY_UP;
	
	static {
		KEY_DOWN = EnumHelper.addEnum(EnumScriptType.class, "KEY_DOWN", new Class[]{String.class}, "keyDown");
		KEY_UP = EnumHelper.addEnum(EnumScriptType.class, "KEY_UP", new Class[]{String.class}, "keyUp");
	}
	
}
