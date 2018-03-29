package xbony2.huesodewiki;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import xbony2.huesodewiki.compat.Compat;
import xbony2.huesodewiki.recipe.RecipeCreator;

@Mod(modid = HuesoDeWiki.MODID, version = HuesoDeWiki.VERSION)
public class HuesoDeWiki {
	public static final String MODID = "huesodewiki";
	public static final String VERSION = "1.3.4a";
	
	public static KeyBinding copyPageKey;
	private boolean isCopyPageKeyDown = false;
	
	public static KeyBinding copyNameKey;
	private boolean isCopyNameKeyDown = false;
	
	public static boolean use2SpaceStyle;
	public static boolean useStackedCategoryStyle;
	
	public static Map<String, String> nameCorrections = new HashMap<>();
	public static Map<String, String> linkCorrections = new HashMap<>();
	
	public static final String[] DEFAULT_NAME_CORRECTIONS = new String[]{"Iron Chest", "Iron Chests", "Minecraft", "Vanilla", "Thermal Expansion", "Thermal Expansion 5", "Pressurized Defense", "Pressurized Defence"};
	public static final String[] DEFAULT_LINK_CORRECTIONS = new String[]{"Esteemed Innovation", "Esteemed Innovation (mod)"};

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		copyPageKey = new KeyBinding("key.copybasepage", Keyboard.KEY_SEMICOLON, "key.categories.huesodewiki");
		ClientRegistry.registerKeyBinding(copyPageKey);
		copyNameKey = new KeyBinding("key.copyname", Keyboard.KEY_APOSTROPHE, "key.categories.huesodewiki");
		ClientRegistry.registerKeyBinding(copyNameKey);
		FMLCommonHandler.instance().bus().register(new RenderTickEventEventHanlder());
		
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), "HuesoDeWiki.cfg"));
		config.load();
		use2SpaceStyle = config.getBoolean("Use2SpaceStyle", "Main", false, "Use \"2spacestyle\"- put an extra space in headers (like \"== Recipe ==\", as vs \"==Recipe==\").");
		useStackedCategoryStyle = config.getBoolean("UseStackedCategoryStyle", "Main", false, "Use \"stacked\" category style– put each category on its own line.");
		String[] nameCorrections = config.getStringList("NameCorrections", "Main", DEFAULT_NAME_CORRECTIONS, "Name fixes. Is a map- first entry is the mod's internal name, second is the FTB Wiki's name.");
		String[] linkCorrections = config.getStringList("LinkCorrections", "Main", DEFAULT_LINK_CORRECTIONS, "Link fixes. Is a map- first entry is the mod's name, second is the FTB Wiki's page.");
		
		for(int i = 0; i < nameCorrections.length - 1; i += 2)
			this.nameCorrections.put(nameCorrections[i], nameCorrections[i + 1]);
		
		for(int i = 0; i < linkCorrections.length - 1; i += 2)
			this.nameCorrections.put(linkCorrections[i], linkCorrections[i + 1]);
		
		config.save();
		
		Compat.preInit();
	}
	
	public class RenderTickEventEventHanlder {
		@SubscribeEvent
		public void renderTickEvent(TickEvent.RenderTickEvent event){
			if(event.phase == TickEvent.Phase.START){
				if(Keyboard.isKeyDown(copyPageKey.getKeyCode())){
					if(!isCopyPageKeyDown){
						isCopyPageKeyDown = true;
						ItemStack itemstack = Utils.getHoveredItemStack();
						if(itemstack != null){
							if(GuiScreen.isCtrlKeyDown()){
								Utils.copyString(RecipeCreator.createRecipes(itemstack));
								Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("msg.copiedrecipe", itemstack.getDisplayName()));
							}else{
								Utils.copyString(PageCreator.createPage(itemstack));
								Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("msg.copiedpage", itemstack.getDisplayName()));
							}
						}else
							return;
					}
				}else
					isCopyPageKeyDown = false;
				
				if(Keyboard.isKeyDown(copyNameKey.getKeyCode())){
					if(!isCopyNameKeyDown){
						isCopyNameKeyDown = true;
						ItemStack itemstack = Utils.getHoveredItemStack();
						if(itemstack != null)
							Utils.copyString(itemstack.getDisplayName());
					}
				}else
					isCopyNameKeyDown = false;
			}
		}
	}
}
