package xbony2.huesodewiki.infobox.parameters;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import xbony2.huesodewiki.api.infobox.IInfoboxParameter;

public class LuminanceParameter implements IInfoboxParameter {
	@Override
	public boolean canAdd(ItemStack itemstack){
		if(itemstack.getItem() instanceof ItemBlock){
			Block block = ((ItemBlock) itemstack.getItem()).field_150939_a;
			return block.getLightValue() > 0;
		}
		
		return false;
	}

	@Override
	public String getParameterName(){
		return "luminance";
	}

	@Override
	public String getParameterText(ItemStack itemstack){
		Block block = ((ItemBlock) itemstack.getItem()).field_150939_a;
		return Integer.toString(block.getLightValue());
	}
}
