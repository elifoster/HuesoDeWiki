package xbony2.huesodewiki.infobox.parameters;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import xbony2.huesodewiki.Utils;
import xbony2.huesodewiki.api.infobox.IInfoboxParameter;

import java.lang.reflect.Field;

public class MiningSpeedParameter implements IInfoboxParameter {
	
	@Override
	public boolean canAdd(ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemTool;
	}

	@Override
	public String getParameterName() {
		return "miningspeed";
	}

	@Override
	public String getParameterText(ItemStack itemstack) {
		return Utils.floatToString(((ItemTool) itemstack.getItem()).func_150913_i().getEfficiencyOnProperMaterial());
	}
}
