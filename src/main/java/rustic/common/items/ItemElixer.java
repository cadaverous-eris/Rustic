package rustic.common.items;

import java.util.List;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.crafting.CondenserRecipe;
import rustic.common.crafting.Recipes;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public class ItemElixer extends ItemBase implements IColoredItem {

	public ItemElixer() {
		super("elixer");
		setCreativeTab(Rustic.alchemyTab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (CondenserRecipe recipe : Recipes.condenserRecipes) {
			if (!subItems.contains(recipe.getResult())) {
				subItems.add(recipe.getResult());
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getDefaultInstance() {
		return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), PotionTypes.HEALING);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;

		if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
			stack.shrink(1);
		}

		if (!worldIn.isRemote) {
			for (PotionEffect potioneffect : PotionUtils.getEffectsFromStack(stack)) {
				if (potioneffect.getPotion().isInstant()) {
					potioneffect.getPotion().affectEntity(entityplayer, entityplayer, entityLiving,
							potioneffect.getAmplifier(), 1.0D);
				} else {
					entityLiving.addPotionEffect(new PotionEffect(potioneffect));
				}
			}
		}

		if (entityplayer != null) {
			entityplayer.addStat(StatList.getObjectUseStats(this));
		}

		if (entityplayer != null && !entityplayer.capabilities.isCreativeMode) {
			if (!entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE))) {
				entityplayer.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
			}
		}

		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 24;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String name = I18n.translateToLocal("rustic.elixer.prefix");
		int i = 0;
		List<PotionEffect> effects = PotionUtils.getEffectsFromStack(stack);
		for (PotionEffect effect : effects) {
			name += I18n.translateToLocal(effect.getEffectName());
			if (i < effects.size() - 1) {
				name += I18n.translateToLocal("rustic.elixer.separator");
			}
			i++;
		}
		return name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return new IItemColor() {
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				if (tintIndex == 0) {
					return PotionUtils.getColor(stack);
				}
				return 0xFFFFFF;
			}
		};
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		super.initModel();
		ClientProxy.addColoredItem(this);
	}

}
