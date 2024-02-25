package saltsheep.ssl;

import net.minecraftforge.common.config.Config;
import saltsheep.keylib.SheepKeyLib;

@Config(modid = SheepScriptLib.MODID)
public class SheepScriptLibConfig {

	@Config.Comment("是否启用SheepAI功能，如果不启用，添加AI将会无效（修改必须重启生效）")
	public static boolean sheepAI_enable = true;

	@Config.Comment("SheepAI线程池的常驻线程，过小会导致AI性能消耗高，过大会占用额外内存")
	public static int sheepAI_CoreThread = 16;

	@Config.Comment("SheepAI在运行时出问题的报错方式，0:直接输出日志，1:NPCsay(当AI属于NPC)/Player message(当AI属于玩家)")
	public static int sheepAI_PrintErrorUse = 0;

	@Config.Comment("SheepAI在运行末尾的work，部分人会由于未知原因丢失，预计是因为AI提前丢弃，启用该项可以解决这个问题")
	public static boolean sheepAI_endedWait = true;

	@Config.Comment("是否启用克隆的优化（开游戏多线程读数据+数据缓存），对于储存NPC较多的情况，会占用较多内存，如果没有使用API频繁克隆NPC的需求，建议关闭")
	public static boolean betterClone_Use = true;

	@Config.Comment("是否启用双端脚本（修改必须重启生效）")
	public static boolean sheepBothSideScript_Use = false;

	@Config.Comment("双端脚本文件使用的编码")
	public static String sheepBothSideScript_ScriptCoding = "UTF-8";

	@Config.Comment("是否启用Task功能，如果不启用，添加Task将会无效（修改必须重启生效）")
	public static boolean task_enable = true;

	@Config.Comment("是否启用新木偶框架，这会修改字节码，部分魔改版mod会因此冲突（修改必须重启生效）")
	public static boolean neoPuppet_enable = true;
	
}
