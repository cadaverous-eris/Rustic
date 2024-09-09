package rustic.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.resources.I18n;
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

	public static Potion getPotionById(ResourceLocation potionId) {
		return ((ForgeRegistries.POTIONS != null) && ForgeRegistries.POTIONS.containsKey(potionId))
				? ForgeRegistries.POTIONS.getValue(potionId) : null;
	}
	
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
	
	
	public static NBTTagCompound getVantaOilTag(ItemStack stack) {
		return !stack.isEmpty() ? stack.getSubCompound("vanta_oil") : null;
	}
	
	public static PotionEffect getVantaOilEffect(NBTTagCompound vantaTag) {
		if (vantaTag == null)
			return null;
		
		if (!vantaTag.hasKey("Effect", 8) || !vantaTag.hasKey("Duration", 3) || !vantaTag.hasKey("Amplifier", 3))
			return null;
				
		int duration = vantaTag.getInteger("Duration");
		if (duration < 1)
			return null;
		int amplifier = vantaTag.getInteger("Amplifier");
		if (amplifier < 0)
			return null;
		Potion potion = getPotionById(new ResourceLocation(vantaTag.getString("Effect")));
		if (potion == null)
			return null;

		return new PotionEffect(potion, duration, amplifier);
	}
	public static PotionEffect getVantaOilEffect(ItemStack stack) {
		return getVantaOilEffect(getVantaOilTag(stack));
	}
	
	public static ItemStack setVantaOilTag(ItemStack stack, NBTTagCompound vantaTag) {
		if (!stack.isEmpty()) {
			if (vantaTag != null) {
				stack.setTagInfo("vanta_oil", vantaTag);
			} else if (stack.hasTagCompound()) {
				if (stack.getTagCompound().getSize() > 1) {
					stack.removeSubCompound("vanta_oil");
				} else {
					stack.setTagCompound(null);
				}
			}
		}
		return stack;
	}
	public static NBTTagCompound setVantaOilEffect(NBTTagCompound vantaTag, PotionEffect effect) {
		if ((effect == null) || (effect.getPotion() == null) || (effect.getDuration() < 1) || (effect.getAmplifier() < 0)) {			
			return null;
		}
		if (vantaTag == null) vantaTag = new NBTTagCompound();
		vantaTag.setString("Effect", effect.getPotion().getRegistryName().toString());
		vantaTag.setInteger("Duration", effect.getDuration());
		vantaTag.setInteger("Amplifier", effect.getAmplifier());
		return vantaTag;
	}
	public static ItemStack setVantaOilEffect(ItemStack stack, PotionEffect effect) {
		return setVantaOilTag(stack, setVantaOilEffect(getVantaOilTag(stack), effect));
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
			String s = I18n.format("effect.none").trim();
			lores.add(TextFormatting.GRAY + s);
		} else {
			for (PotionEffect potioneffect : list) {
				String s1 = I18n.format(potioneffect.getEffectName()).trim();
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
					s1 = s1 + " " + I18n.format("potion.potency." + potioneffect.getAmplifier()).trim();
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
			lores.add(TextFormatting.DARK_PURPLE + I18n.format("potion.whenDrank"));

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
					lores.add(TextFormatting.BLUE + I18n.format(
							"attribute.modifier.plus." + attributemodifier2.getOperation(),
							ItemStack.DECIMALFORMAT.format(d1),
							I18n.format("attribute.name." + (String) tuple.getFirst())));
				} else if (d0 < 0.0D) {
					d1 = d1 * -1.0D;
					lores.add(TextFormatting.RED + I18n.format(
							"attribute.modifier.take." + attributemodifier2.getOperation(),
							ItemStack.DECIMALFORMAT.format(d1),
							I18n.format("attribute.name." + (String) tuple.getFirst())));
				}
			}
		}
	}
	
	
	public static final int VANTA_OIL_EFFECT_DURATION = 15 * 20;
	public static final int VANTA_OIL_EFFECT_MAX_DURATION = VANTA_OIL_EFFECT_DURATION + (5 * 20);
	
	public static int getRemainingVantaUses(PotionEffect effect) {
		int duration = effect.getDuration();
		if (duration <= 0)
			return 0;
		
		if (effect.getPotion().isInstant())
			return duration;
		
		int hits = duration / VANTA_OIL_EFFECT_DURATION;
		int remainderDuration = duration % VANTA_OIL_EFFECT_DURATION;
		
		if ((remainderDuration > (VANTA_OIL_EFFECT_MAX_DURATION - VANTA_OIL_EFFECT_DURATION)) || (hits == 0))
			hits++;
		
		return hits;
	}
	
	public static int getNextVantaHitDuration(int totalDuration) {
		return (totalDuration > VANTA_OIL_EFFECT_MAX_DURATION) ? VANTA_OIL_EFFECT_DURATION : totalDuration;
	}
	
	@SideOnly(Side.CLIENT)
	public static void addVantaOilTooltip(ItemStack itemIn, PotionEffect vantaEffect, List<String> lores) {
		
		// TODO: implement
		
		if (!lores.isEmpty()) lores.add("");
		lores.add(TextFormatting.DARK_PURPLE + I18n.format("tooltip.rustic.vanta_oil", vantaEffect.getDuration()).trim());
		
		
		String effectName = I18n.format(vantaEffect.getEffectName()).trim();
		Potion potion = vantaEffect.getPotion();

		if (vantaEffect.getAmplifier() > 0) {
			effectName = effectName + " " + I18n.format("potion.potency." + vantaEffect.getAmplifier()).trim();
		}
		if (!potion.isInstant()) {
			int duration = getNextVantaHitDuration(vantaEffect.getDuration());
			//if (duration > 20)
			effectName = effectName + " (" + StringUtils.ticksToElapsedTime(duration) + ")";
			//else
			//effectName = effectName + " (" + StringUtils.ticksToElapsedTime(duration) + ")";
		}
		
		if (potion.isBadEffect()) {
			lores.add(" " + TextFormatting.RED + effectName);
		} else {
			lores.add(" " + TextFormatting.BLUE + effectName);
		}
		
		int uses = getRemainingVantaUses(vantaEffect);
		lores.add(" " + I18n.format((uses == 1) ? "tooltip.rustic.vanta_oil_use" : "tooltip.rustic.vanta_oil_uses", uses));
	}

}
