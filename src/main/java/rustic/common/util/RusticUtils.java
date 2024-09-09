package rustic.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreDictionary;
import rustic.common.Config;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.items.ItemFluidBottle;
import rustic.common.items.ModItems;
import rustic.core.Rustic;

public class RusticUtils {
	
	public static boolean isVantaOilableWeapon(ItemStack stack) {
		if (stack.isEmpty()) return false;
		
		Item item = stack.getItem();
		
		if ((item instanceof ItemSword) || (item instanceof ItemAxe))
			return true;
		
		if ((item.getHarvestLevel(stack, "sword", null, null) >= 0) || (item.getHarvestLevel(stack, "axe", null, null) >= 0))
			return true;
		
		String itemName = Objects.requireNonNull(item.getRegistryName()).toString();
		
		
		for (String entry : Config.VANTA_OIL_WHITELIST) {
			if (entry.startsWith("#")) {
				String oreName = entry.substring(1);
				if (OreDictionary.doesOreNameExist(oreName)) {
					NonNullList<ItemStack> ores = OreDictionary.getOres(oreName, false);
					for (ItemStack ore : ores) {
						if (OreDictionary.itemMatches(ore, stack, false)) {
							return true;
						}
					}
				}
				continue;
			}
			int damageSeparatorIndex = entry.indexOf("@");
			if (damageSeparatorIndex >= 0) {
				String entryName = entry.substring(0, damageSeparatorIndex);
				if (itemName.equals(entryName)) {
					String dmgString = entry.substring(damageSeparatorIndex + 1);
					if (!dmgString.isEmpty()) {
						try {
							int dmg = Integer.parseInt(dmgString);
							if (stack.getItemDamage() == dmg) {
								return true;
							}
						} catch (NumberFormatException e) {}
					} else {
						return true;
					}
				}
			} else if (itemName.equals(entry)) {
				return true;
			}
		}
		//if (Config.VANTA_OIL_WHITELIST.contains(itemName)) return true;
		
		return false;
	}

	public static boolean isVantaOilBottle(ItemStack stack) {
		if (!stack.isEmpty() && (stack.getItem().equals(ModItems.FLUID_BOTTLE))) {
			FluidStack fluidStack = FluidUtil.getFluidContained(stack);
			if ((fluidStack != null) && (fluidStack.getFluid() != null) && fluidStack.getFluid().equals(ModFluids.VANTA_OIL)) {
				return true;
			}
			
		}
		return false;
	}
	
	public static boolean isBottleOf(ItemStack stack, Fluid fluid) {
		if (stack.isEmpty() || (fluid == null)) return false;
		if (fluid == FluidRegistry.WATER) {
			Item item = stack.getItem();
			if (item == Items.POTIONITEM) {
				NBTTagCompound stackTag = stack.getTagCompound();
				if ((stackTag != null) && (PotionUtils.getPotionTypeFromNBT(stackTag) == PotionTypes.WATER)) {
					if (!stackTag.hasKey("CustomPotionEffects", 9)) return true;
					NBTTagList tagList = stackTag.getTagList("CustomPotionEffects", 10);
					if (tagList.tagCount() == 0) return true;
					for (int i = 0; i < tagList.tagCount(); ++i) {
						NBTTagCompound effectTag = tagList.getCompoundTagAt(i);
						PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(effectTag);
						if (effect != null) return false;
					}
					return true;
				}
			}
			return false;
		}
		if (stack.getItem() instanceof ItemFluidBottle) {
			FluidStack fluidStack = FluidUtil.getFluidContained(stack);
			if ((fluidStack != null) && (fluidStack.getFluid() != null) && fluidStack.getFluid().equals(fluid)) {
				return true;
			}
			
		}
		return false;
	}
	
	public static void givePlayerItem(EntityPlayer player, ItemStack stack) {
		if ((player == null) || stack.isEmpty()) return;
		if (!player.inventory.addItemStackToInventory(stack)) {
			player.dropItem(stack, false);
		}
	}
	
	public static List<ItemStack> getSubItems(Item item) {
		List<ItemStack> list = new ArrayList<>();
		int l = list.size();
		for (CreativeTabs itemTab : item.getCreativeTabs()) {
			if (itemTab != null) {
				addSubtypesFromCreativeTabToList(list, item, 1, itemTab);
			}
		}
		if (list.size() == l) {
			list.add(new ItemStack(item));
		}
		return list;
	}
	
	public static void addSubtypesFromCreativeTabToList(List<ItemStack> subtypeList, Item item, final int stackSize, CreativeTabs itemTab) {
		NonNullList<ItemStack> subItems = NonNullList.create();
		try {
			item.getSubItems(itemTab, subItems);
		} catch (RuntimeException | LinkageError e) {
			Rustic.logger.warn("Caught a crash while getting sub-items of {}", item, e);
		}
		for (ItemStack subItem : subItems) {
			if (!subItem.isEmpty() && (subItem.getMetadata() != OreDictionary.WILDCARD_VALUE)) {
				if (subItem.getCount() != stackSize) {
					ItemStack subItemCopy = subItem.copy();
					subItemCopy.setCount(stackSize);
					subtypeList.add(subItemCopy);
				} else {
					subtypeList.add(subItem);
				}
			}
		}
	}
	
	
	public static RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
		float f = playerIn.rotationPitch;
		float f1 = playerIn.rotationYaw;
		double d0 = playerIn.posX;
		double d1 = playerIn.posY + (double) playerIn.getEyeHeight();
		double d2 = playerIn.posZ;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = 5.0D;
		if (playerIn instanceof net.minecraft.entity.player.EntityPlayerMP) {
			d3 = ((net.minecraft.entity.player.EntityPlayerMP) playerIn).interactionManager.getBlockReachDistance();
		}
		Vec3d vec3d1 = vec3d.addVector((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
		return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
	}

}
