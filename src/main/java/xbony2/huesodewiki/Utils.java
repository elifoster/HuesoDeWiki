package xbony2.huesodewiki;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Field;
import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Utils {
	public static String getModName(ItemStack itemstack){
		ModContainer container = Loader.instance().getIndexedModList().get(itemstack.getItem().getCreatorModId(itemstack));
		
		if(container == null)
			return "Vanilla";
		else{
			String modName = container.getName();
			return HuesoDeWiki.nameCorrections.get(modName) != null ? HuesoDeWiki.nameCorrections.get(modName) : modName;
		}
	}
	
	public static String getModAbbrevation(String modName){
		return "{{subst:#invoke:Mods|getAbbrv|" + modName + "}}";
	}
	
	public static String getModAbbrevation(ItemStack itemstack){
		return getModAbbrevation(getModName(itemstack));
	}
	
	public static String outputItem(ItemStack itemstack){
		return "{{Gc|mod=" + getModAbbrevation(itemstack) + "|dis=false|" + itemstack.getDisplayName() + "}}";
	}
	
	public static String outputIngredient(Ingredient ingredient){
		StringBuilder ret = new StringBuilder();
		
		for(ItemStack itemstack : ingredient.getMatchingStacks()){
			ret.append(outputItem(itemstack));
		}
		
		return ret.toString();
	}
	
	public static String outputItemOutput(ItemStack itemstack){
		return "{{Gc|mod=" + getModAbbrevation(itemstack) + "|link=none|" + itemstack.getDisplayName() + (itemstack.getCount() != 1 ? "|" + itemstack.getCount() : "") + "}}";
	}
	
	/**
	 * @return null if nothing can be found.
	 */
	public static String outputOreDictionaryEntry(ItemStack[] list){
		try{
			ItemStack stack = list[0];
		
			int[] ids = OreDictionary.getOreIDs(stack);
			
			for(int i = 0; i < ids.length; i++){
				String potentialEntry = OreDictionary.getOreName(ids[i]);
				List<ItemStack> potentialCognate = OreDictionary.getOres(potentialEntry);
				
				boolean isEqual = potentialCognate.size() == list.length;
				
				if(isEqual) //so far, that is
					for(int j = 0; j < list.length; j++)
						if(potentialCognate.get(j).getItem() != list[j].getItem() && potentialCognate.get(j).getItemDamage() != list[j].getItemDamage())
							isEqual = false;
				
				if(isEqual)
					return "{{O|" + potentialEntry + "}}";
			}
		}catch(ArrayIndexOutOfBoundsException e){
			return null;
		}
		
		return null;
	}
	
	public static String doubleToString(double d){
		String ret = Double.toString(d);
		if(ret.endsWith(".0"))
				ret = ret.replaceAll(".0$", "");
		return ret;
	}
	
	public static String floatToString(float f){
		String ret = Float.toString(f);
		if(ret.endsWith(".0"))
				ret = ret.replaceAll(".0$", "");
		return ret;
	}

	/**
	 * Formats lists in infobox parameters.
	 * @param strings The list of strings to format.
	 * @return A formatted string containing the entries in the strings parameter.
	 */
	public static String formatInfoboxList(Iterable<String> strings){
		return String.join("<br />", strings);
	}

	/**
	 * @param clazz The class in which the Field is stored
	 * @param mappedName The name of the field with MCP mappings
	 * @param obfuscatedName The name of the field when obfuscated
	 * @return The Field, or null if it couldn't find it. It will print the stacktrace if it is unable to find either.
	 */
	@Nullable
	public static Field getField(Class clazz, String mappedName, String obfuscatedName){
		Field field = null;
		try{
			field = clazz.getDeclaredField(obfuscatedName);
		}catch(NoSuchFieldException obfException){
			try{
				field = clazz.getDeclaredField(mappedName);
			}catch(NoSuchFieldException mappedNameException){
				mappedNameException.printStackTrace();
			}
		}
		return field;
	}

	/**
	 * @return The ItemStack that the player is currently hovering over. If they are hovering over an empty slot,
	 * 		   are not hovering over a slot or they are hovering over a slot in a non-supported Gui, returns an
	 * 		   empty ItemStack.
	 */
	@Nonnull
	public static ItemStack getHoveredItemStack(){
		GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
		if(currentScreen instanceof GuiContainer){
			Slot hovered = ((GuiContainer)currentScreen).getSlotUnderMouse();
			if(hovered != null)
				return hovered.getStack();
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Adds the provided string to the system clipboard
	 * @param toCopy The string to add to the clipboard
	 */
	public static void copyString(String toCopy){
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(toCopy), null);
	}
}
