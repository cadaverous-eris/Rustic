package rustic.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElixirUtils {

	/*
	 * Custom Effect NBT Tag Format:
	 * 
	 * {ElixirEffects:[{ Effect:"[effect registry name]", Duration:[insert
	 * duration here], Amplifier:[insert amplifier here] }]}
	 */

	public static List<PotionEffect> deserializeEffects(NBTTagCompound tag) {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();

		if (tag != null && tag.hasKey("ElixirEffects", 9)) {
			NBTTagList tagList = tag.getTagList("ElixirEffects", 10);

			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound effectTag = tagList.getCompoundTagAt(i);

				ResourceLocation effectName = new ResourceLocation(effectTag.getString("Effect"));
				int duration = effectTag.getInteger("Duration");
				int amplifier = effectTag.getInteger("Amplifier");

				if (ForgeRegistries.POTIONS != null && ForgeRegistries.POTIONS.containsKey(effectName)) {
					effects.add(new PotionEffect(ForgeRegistries.POTIONS.getValue(effectName), duration, amplifier));
				}
			}
		}

		return effects;
	}

	public static List<PotionEffect> getEffects(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return deserializeEffects(stack.getTagCompound());
		}
		return new ArrayList<PotionEffect>();
	}

	public static void addEffect(PotionEffect effect, ItemStack stack) {
		NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
		if (!tag.hasKey("ElixirEffects", 9)) {
			NBTTagList tagList = new NBTTagList();
			tag.setTag("ElixirEffects", tagList);
		}

		NBTTagCompound effectTag = new NBTTagCompound();
		effectTag.setString("Effect", effect.getPotion().getRegistryName().toString());
		effectTag.setInteger("Duration", effect.getDuration());
		effectTag.setInteger("Amplifier", effect.getAmplifier());

		tag.getTagList("ElixirEffects", 10).appendTag(effectTag);
		stack.setTagCompound(tag);
	}

	public static int getColor(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();

		if (tag == null || !tag.hasKey("ElixirEffects", 9) || tag.getTagList("ElixirEffects", 10).tagCount() <= 0) {
			return 16253176;
		}

		ResourceLocation effectName = new ResourceLocation(
				tag.getTagList("ElixirEffects", 10).getCompoundTagAt(0).getString("Effect"));
		if (ForgeRegistries.POTIONS != null && ForgeRegistries.POTIONS.containsKey(effectName)) {
			return ForgeRegistries.POTIONS.getValue(effectName).getLiquidColor();
		}

		return 16253176;
	}

	@SideOnly(Side.CLIENT)
	public static void addPotionTooltip(ItemStack itemIn, List<String> lores, float durationFactor) {
		List<PotionEffect> list = getEffects(itemIn);
		List<Tuple<String, AttributeModifier>> list1 = Lists.<Tuple<String, AttributeModifier>>newArrayList();

		if (list.isEmpty()) {
			String s = I18n.translateToLocal("effect.none").trim();
			lores.add(TextFormatting.GRAY + s);
		} else {
			for (PotionEffect potioneffect : list) {
				String s1 = I18n.translateToLocal(potioneffect.getEffectName()).trim();
				Potion potion = potioneffect.getPotion();
				Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

				if (!map.isEmpty()) {
					for (Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(),
								potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier),
								attributemodifier.getOperation());
						list1.add(new Tuple(((IAttribute) entry.getKey()).getName(), attributemodifier1));
					}
				}

				if (potioneffect.getAmplifier() > 0) {
					s1 = s1 + " " + I18n.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
				}

				if (potioneffect.getDuration() > 20) {
					s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, durationFactor) + ")";
				}

				if (potion.isBadEffect()) {
					lores.add(TextFormatting.RED + s1);
				} else {
					lores.add(TextFormatting.BLUE + s1);
				}
			}
		}

		if (!list1.isEmpty()) {
			lores.add("");
			lores.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("potion.whenDrank"));

			for (Tuple<String, AttributeModifier> tuple : list1) {
				AttributeModifier attributemodifier2 = tuple.getSecond();
				double d0 = attributemodifier2.getAmount();
				double d1;

				if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2) {
					d1 = attributemodifier2.getAmount();
				} else {
					d1 = attributemodifier2.getAmount() * 100.0D;
				}

				if (d0 > 0.0D) {
					lores.add(TextFormatting.BLUE + I18n.translateToLocalFormatted(
							"attribute.modifier.plus." + attributemodifier2.getOperation(),
							ItemStack.DECIMALFORMAT.format(d1),
							I18n.translateToLocal("attribute.name." + (String) tuple.getFirst())));
				} else if (d0 < 0.0D) {
					d1 = d1 * -1.0D;
					lores.add(TextFormatting.RED + I18n.translateToLocalFormatted(
							"attribute.modifier.take." + attributemodifier2.getOperation(),
							ItemStack.DECIMALFORMAT.format(d1),
							I18n.translateToLocal("attribute.name." + (String) tuple.getFirst())));
				}
			}
		}
	}

}
