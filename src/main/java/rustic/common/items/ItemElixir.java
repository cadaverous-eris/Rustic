package rustic.common.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;
import rustic.common.util.ElixirUtils;
import rustic.common.util.RusticUtils;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public class ItemElixir extends ItemBase implements IColoredItem {

	public ItemElixir() {
		super("elixir");
		setCreativeTab(Rustic.alchemyTab);
		setMaxStackSize(16);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (ICondenserRecipe recipe : Recipes.condenserRecipes) {
			if (!subItems.contains(recipe.getResult())) {
				if(isInCreativeTab(tab)) {
					subItems.add(recipe.getResult());
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getDefaultInstance() {
		ItemStack stack = super.getDefaultInstance();
		ElixirUtils.addEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 0), stack);
		return stack;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;

		if (!worldIn.isRemote) {
			for (PotionEffect potioneffect : ElixirUtils.getEffects(stack)) {
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
			
			if (entityplayer instanceof EntityPlayerMP) {
	            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
	        }
		}

		if ((entityplayer == null) || !entityplayer.capabilities.isCreativeMode) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				return new ItemStack(Items.GLASS_BOTTLE);
			} else if (entityplayer != null) {
				RusticUtils.givePlayerItem(entityplayer, new ItemStack(Items.GLASS_BOTTLE));
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
		String name = I18n.translateToLocal("rustic.elixir.prefix");
		int i = 0;
		List<PotionEffect> effects = ElixirUtils.getEffects(stack);
		for (PotionEffect effect : effects) {
			name += I18n.translateToLocal(effect.getEffectName());
			if (i < effects.size() - 1) {
				name += I18n.translateToLocal("rustic.elixir.separator");
			}
			i++;
		}
		return name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ElixirUtils.addPotionTooltip(stack, tooltip, 1.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				if (tintIndex == 0) {
					return ElixirUtils.getColor(stack);
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
