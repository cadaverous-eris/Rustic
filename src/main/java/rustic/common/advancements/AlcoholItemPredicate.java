package rustic.common.advancements;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.oredict.OreDictionary;
import rustic.common.blocks.fluids.FluidBooze;

public class AlcoholItemPredicate extends ItemPredicate {
	
	private final FluidBooze fluid;
	private final MinMaxBounds quality;

	public AlcoholItemPredicate(@Nullable FluidBooze fluid, MinMaxBounds quality) {
		this.fluid = fluid;
        this.quality = quality;
    }
	
	@Override
    public boolean test(ItemStack stack) {
		if (stack.isEmpty() || !stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
			return false;
		
		FluidStack fluidStack = FluidUtil.getFluidContained(stack);
		if ((fluidStack == null) || (fluidStack.getFluid() == null) || (fluidStack.amount <= 0))
			return false;
		
		Fluid fluid = fluidStack.getFluid();
		if (!(fluid instanceof FluidBooze))
			return false;
		
		if ((this.fluid != null) && (this.fluid != fluid))
			return false;
		
		float quality = ((FluidBooze) fluid).getQuality(fluidStack);
		if ((this.quality != null) && !this.quality.test(quality))
			return false;
		
        return true;
    }
	
	public static AlcoholItemPredicate deserialize(JsonObject jsonObject) {
		FluidBooze fluid = null;
		if (JsonUtils.hasField(jsonObject, "fluid")) {
			String fluidName = JsonUtils.getString(jsonObject, "fluid");
			Fluid registeredFluid = FluidRegistry.getFluid(fluidName);
			if (registeredFluid == null)
				throw new JsonSyntaxException("Unknown fluid id '" + fluidName + "'");
			if (registeredFluid instanceof FluidBooze)
				fluid = (FluidBooze) registeredFluid;
			else
				throw new JsonSyntaxException("Fluid '" + fluidName + "' is not an instance of rustic.common.blocks.fluids.FluidBooze");
		}
		MinMaxBounds quality = MinMaxBounds.deserialize(jsonObject.get("quality"));

		return new AlcoholItemPredicate(fluid, quality);
	}

}
