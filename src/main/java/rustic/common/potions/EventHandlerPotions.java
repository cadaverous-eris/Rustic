package rustic.common.potions;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.network.MessageFirePowerAttack;
import rustic.common.network.PacketHandler;

public class EventHandlerPotions {

	private Random rand = new Random();

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onWaterBottleUse(LivingEntityUseItemEvent.Finish event) {
		EntityLivingBase entity = event.getEntityLiving();
		PotionEffect effect = entity.getActivePotionEffect(PotionsRustic.TIPSY);
		if (effect != null && !entity.world.isRemote) {
			ItemStack stack = event.getItem();
			if (stack.getItem() == Items.POTIONITEM && PotionUtils.getPotionFromItem(stack) == PotionTypes.WATER) {
				int duration = effect.getDuration();
				int amplifier = effect.getAmplifier();
				if (rand.nextFloat() < 0.1F) {
					amplifier--;
				} else {
					duration -= (rand.nextInt(800) + 200);
				}
				entity.removePotionEffect(PotionsRustic.TIPSY);
				if (amplifier >= 0 && duration > 0) {
					entity.addPotionEffect(new PotionEffect(PotionsRustic.TIPSY, duration, amplifier, false, false));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onJumpEvent(LivingEvent.LivingJumpEvent event) {
		if (event.getEntityLiving() == null) return;
		
		EntityLivingBase entity = event.getEntityLiving();
		if (entity.getActivePotionEffect(PotionsRustic.FULLMETAL_POTION) != null) {
			entity.motionY = 0;
		}
	}
	
	@SubscribeEvent
	public void onMountEvent(EntityMountEvent event) {
		if (!event.isMounting()) return;
		if ((event.getEntityMounting() == null) || !(event.getEntityMounting() instanceof EntityLivingBase)) return;
		if ((event.getEntityBeingMounted() == null) || !(event.getEntityBeingMounted() instanceof EntityLivingBase)) return;
		EntityLivingBase top = (EntityLivingBase) event.getEntityMounting();
		//EntityLivingBase bottom = (EntityLivingBase) event.getEntityBeingMounted();
		
		if (top.getActivePotionEffect(PotionsRustic.FULLMETAL_POTION) != null) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onSleepEvent(PlayerSleepInBedEvent event) {
		if ((event.getEntityPlayer() != null) && (event.getEntityPlayer().getActivePotionEffect(PotionsRustic.FULLMETAL_POTION) != null)) {
			event.setResult(SleepResult.OTHER_PROBLEM);
		}
	}
	
	@SubscribeEvent
	public void onFallEvent(LivingFallEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		float d = event.getDistance();
		if (entity.isPotionActive(PotionsRustic.FULLMETAL_POTION) && (d >= 0.6F) && !entity.isInWater() && !entity.isInLava()) {
			entity.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
			
			// TODO: damage anything fallen on?
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerAttack(PlayerInteractEvent event) {
		if (!event.getEntityPlayer().isPotionActive(PotionsRustic.FIRE_POWER_POTION)) return;
		
		boolean leftClick = false;
		if (event instanceof PlayerInteractEvent.LeftClickEmpty) {
			leftClick = true;
		} else if (event instanceof PlayerInteractEvent.LeftClickBlock) {
			leftClick = true;
		}
		
		EntityPlayer p = event.getEntityPlayer();
		if (leftClick && !p.isSwingInProgress && !p.isSneaking()) {
			PacketHandler.INSTANCE.sendToServer(new MessageFirePowerAttack());
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerAttack(AttackEntityEvent event) {
		if (!event.getEntityPlayer().isPotionActive(PotionsRustic.FIRE_POWER_POTION)) return;
		
		EntityPlayer p = event.getEntityPlayer();
		
		System.out.println(p.isSwingInProgress);
		
		
		if (!p.isSwingInProgress && !p.isSneaking()) {
			PacketHandler.INSTANCE.sendToServer(new MessageFirePowerAttack());
		}
	}
	
	
	public static void doFirePowerAttack(EntityPlayer p) {
		if (p == null) return;
		if (p.world.isRemote) return;
		
		PotionEffect effect = p.getActivePotionEffect(PotionsRustic.FIRE_POWER_POTION);
		if (effect == null) return;
		
		
		if (p.world.getBlockState(new BlockPos(p.posX, p.posY + p.getEyeHeight(), p.posZ)).getMaterial() == Material.WATER) {
        	return;
		}
		
		double f = 0.005;
		
		double d1 = p.getLookVec().x * 40d;
        double d2 = p.getLookVec().y * 40d;
        double d3 = p.getLookVec().z * 40d;
		
        EntityFireball fb;
        if (effect.getAmplifier() > 0) {
        	fb = new EntityLargeFireball(p.world, p, d1 + p.getRNG().nextGaussian() * (double) f, d2, d3 + p.getRNG().nextGaussian() * (double) f);
        	((EntityLargeFireball) fb).explosionPower = effect.getAmplifier();
        } else {
        	fb = new EntitySmallFireball(p.world, p, d1 + p.getRNG().nextGaussian() * (double) f, d2, d3 + p.getRNG().nextGaussian() * (double) f);
        }
        fb.posY = p.posY + (double) (p.getEyeHeight());
        
        //double h = Math.sqrt((p.getLookVec().x * p.getLookVec().x) + (p.getLookVec().z * p.getLookVec().z) + (p.getLookVec().y * p.getLookVec().y));
        fb.posX += p.getLookVec().x * 1.0;
        fb.posZ += p.getLookVec().z * 1.0;
        fb.posY += p.getLookVec().y * 1.0;
        
        fb.motionX += p.motionX;
        fb.motionZ += p.motionZ;
        if (!p.onGround) {
        	fb.motionY += p.motionY;
        }
        
        fb.setPosition(fb.posX,  fb.posY,  fb.posZ);
        
        if ((fb.getEntityBoundingBox() != null) && p.world.checkBlockCollision(fb.getEntityBoundingBox().grow(0.001, 0.001, 0.001))) {
        	return;
        }
        
        p.world.playSound((EntityPlayer) null, p.posX + p.getLookVec().x, p.posY + p.getEyeHeight() + p.getLookVec().y, p.posZ + p.getLookVec().z,
				SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 1.0F,
				0.4F / (p.getRNG().nextFloat() * 0.4F + 1.2F));
        
        p.world.spawnEntity(fb);
	}
	

	// FULL, MAGIC RESISTANCE, WITHER WARD

	@SubscribeEvent
	public void onEntityDamage(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		EntityLivingBase entity = event.getEntityLiving();
		
		if (entity.isPotionActive(PotionsRustic.FULLMETAL_POTION) && (source != DamageSource.OUT_OF_WORLD)) {
			event.setAmount(0);
			return;
		}
		
		if (source == DamageSource.STARVE) {
			PotionEffect effect = entity.getActivePotionEffect(PotionsRustic.FULL_POTION);
			if (effect != null) {
				float newAmount = Math.max(event.getAmount() - (effect.getAmplifier() + 1), 0);
				event.setAmount(newAmount);
			}
		} else if (source == DamageSource.MAGIC) {
			PotionEffect effect = entity.getActivePotionEffect(PotionsRustic.MAGIC_RESISTANCE_POTION);
			if (effect != null) {
				float newAmount = event.getAmount() / (2F * (effect.getAmplifier() + 1F));
				event.setAmount(newAmount);
			}
		} else if (source == DamageSource.WITHER) {
			PotionEffect effect = entity.getActivePotionEffect(PotionsRustic.WITHER_WARD_POTION);
			if (effect != null) {
				float newAmount = event.getAmount() / (2F * (effect.getAmplifier() + 1F));
				event.setAmount(newAmount);
			}
		}
	}

}
