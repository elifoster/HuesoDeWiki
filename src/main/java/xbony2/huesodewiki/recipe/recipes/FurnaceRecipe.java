package xbony2.huesodewiki.recipe.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import xbony2.huesodewiki.Utils;
import xbony2.huesodewiki.api.IWikiRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FurnaceRecipe implements IWikiRecipe {
	@Override
	public String getRecipes(ItemStack itemstack){
		List<ItemStack> inputs = new ArrayList<>();

		((Iterable<Map.Entry<ItemStack, ItemStack>>)FurnaceRecipes.smelting().getSmeltingList().entrySet()).forEach((entry) -> {
			ItemStack output = entry.getValue();
			
			if(output.isItemEqual(itemstack))
				inputs.add(entry.getKey());
		});
		
		if(inputs.isEmpty())
			return null;

		StringBuilder ret = new StringBuilder("{{Cg/Furnace\n");
		ret.append("|I=");

		inputs.forEach((input) -> ret.append(Utils.outputItem(input)));
		
		ret.append('\n');

		ret.append("|O=").append(Utils.outputItemOutput(itemstack)).append('\n');
		ret.append("}}\n");
		return ret.toString();
	}
}
