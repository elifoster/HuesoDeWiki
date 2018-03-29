package xbony2.huesodewiki.infobox;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import xbony2.huesodewiki.Utils;
import xbony2.huesodewiki.api.infobox.BasicInstanceOfParameter;
import xbony2.huesodewiki.api.infobox.IInfoboxParameter;
import xbony2.huesodewiki.infobox.parameters.*;

public class InfoboxCreator {
	public static List<IInfoboxParameter> parameters = new ArrayList<>();
	
	static {
		parameters.add(new NameParameter());
		parameters.add(new ImageIconParameter());
		parameters.add(new ModParameter());
		parameters.add(new TypeParameter());
		parameters.add(new OreDictNameParameter());
		//parameters.add(new UnlocalizedNameParameter()); // Disabled until issue resolved
		parameters.add(new BasicInstanceOfParameter("blastresistance", (itemstack) -> {
			String ret;
			
			try{
				ret = Utils.floatToString(((ItemBlock)itemstack.getItem()).field_150939_a.getExplosionResistance(null) * 5); //Minecraft is weird with it, don't ask
			}catch(Exception e){ //In case of a null pointer
				ret = "?";
			}
			
			return ret;
		}, ItemBlock.class));
		
		parameters.add(new BasicInstanceOfParameter("hardness", (itemstack) -> {
			String ret;
			
			try{
				// No position, not sure what to put in place of a null BlockPos.
				ret = Utils.floatToString(((ItemBlock)itemstack.getItem()).field_150939_a.getBlockHardness(null, 0, 0, 0));
			}catch(Exception e){ //In case of a null pointer
				ret = "?";
			}
			
			return ret;
		}, ItemBlock.class));
		
		parameters.add(new BasicInstanceOfParameter("foodpoints", (itemstack) -> Integer.toString(((ItemFood)itemstack.getItem()).func_150905_g(itemstack)), ItemFood.class));
		parameters.add(new BasicInstanceOfParameter("saturation", (itemstack) -> Utils.floatToString(((ItemFood)itemstack.getItem()).func_150906_h(itemstack) * ((ItemFood)itemstack.getItem()).func_150905_g(itemstack)), ItemFood.class));
		parameters.add(new BasicInstanceOfParameter("hunger", (itemstack) -> {
			ItemFood food = (ItemFood)itemstack.getItem();
			return "{{Shanks|" + Integer.toString(food.func_150905_g(itemstack)) + "|" + Utils.floatToString(food.func_150906_h(itemstack)) + "}}";
		}, ItemFood.class));
		
		parameters.add(new BasicInstanceOfParameter("armorrating", (itemstack) -> Integer.toString(((ItemArmor)itemstack.getItem()).damageReduceAmount), ItemArmor.class));
		parameters.add(new ToughnessParameter());
		parameters.add(new BasicInstanceOfParameter("damage", (itemstack) -> {
			Item item = itemstack.getItem();
			if(item instanceof ItemTool){
				try{
					Field field = Utils.getField(ItemTool.class, "attackDamage", "field_77865_bY");
					if(field != null){
						field.setAccessible(true);
						return Utils.floatToString(field.getFloat((ItemTool)item) + 1.0f);
					}
				}catch(IllegalArgumentException | IllegalAccessException e){//Do not complain or you will be smote.
					e.printStackTrace();
				}
			}else if(item instanceof ItemSword){
				Multimap<String, AttributeModifier> multimap = ((ItemSword)item).getItemAttributeModifiers();
				float damage = 1.0f; //default
				for(String name : multimap.keySet())
					if(name == SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName())
						for(AttributeModifier modifier : multimap.get(name))
							damage += modifier.getAmount();
				return Utils.floatToString(damage);
			}
			return "?";
		}, ItemTool.class, ItemSword.class));
		parameters.add(new BasicInstanceOfParameter("aspeed", (itemstack) -> {
			Item item = itemstack.getItem();
			if(item instanceof ItemTool){
				try{
					Field field = Utils.getField(ItemTool.class, "attackSpeed", "field_185065_c");
					if(field != null){
						field.setAccessible(true);
						return String.format("%.2g", field.getFloat((ItemTool)item) + 4.0f);
					}
				}catch(IllegalArgumentException | IllegalAccessException e){//Do not complain or you will be smote.
					e.printStackTrace();
				}
			}else if(item instanceof ItemSword){
				Multimap<String, AttributeModifier> multimap = ((ItemSword)item).getItemAttributeModifiers();
				float speed = 4.0f; //default
				for(String name : multimap.keySet())
					if(name == SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName())
						for(AttributeModifier modifier : multimap.get(name))
							speed += modifier.getAmount();
				
				return String.format("%.2g", speed);
			}
			return "?";
		}, ItemTool.class, ItemSword.class));
		parameters.add(new BasicInstanceOfParameter("durability", (itemstack) -> Utils.floatToString(((ItemTool)itemstack.getItem()).getMaxDamage(itemstack) + 1), ItemTool.class));
		parameters.add(new EnchantabilityParameter());
		parameters.add(new MiningLevelParameter());
		parameters.add(new MiningSpeedParameter());
		parameters.add(new StackableParameter());
		parameters.add(new FlammableParameter());
		parameters.add(new LuminanceParameter());
	}
	
	public static String createInfobox(ItemStack itemstack){
		StringBuilder ret = new StringBuilder("{{Infobox\n");
		
		parameters.stream().filter((parameter) -> parameter.canAdd(itemstack)).forEach((parameter) -> ret.append('|').append(parameter.getParameterName()).append('=').append(parameter.getParameterText(itemstack)).append('\n'));
		
		ret.append("}}\n");
		return ret.toString();
	}
}
