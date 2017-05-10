package rustic.common.entities;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.items.ModItems;
import rustic.common.potions.PotionsRustic;

public class EntityTomato extends EntityThrowable {

	public EntityTomato(World worldIn) {
		super(worldIn);
	}

	public EntityTomato(World worldIn, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn);
	}

	public EntityTomato(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 3) {
			Random rand = this.world.rand;
			for (int i = 0; i < rand.nextInt(8) + 4; ++i) {
				this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ,
						((rand.nextDouble() * 0.06) - 0.03), ((rand.nextDouble() * 0.06) - 0.03),
						((rand.nextDouble() * 0.06) - 0.03), Item.getIdFromItem(ModItems.TOMATO));
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entityHit != null) {
			int i = 0;

			result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float) i);
			if (!this.world.isRemote) {
				if (result.entityHit instanceof EntityLivingBase) {
					((EntityLivingBase) result.entityHit)
							.addPotionEffect(new PotionEffect(PotionsRustic.SHAME_POTION, 400, 0, false, false));
				}
			}
		}

		this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_SLIME_HURT,
				SoundCategory.NEUTRAL, 0.5F, 0.4F / (this.world.rand.nextFloat() * 0.4F + 0.8F));

		if (!this.world.isRemote) {
			this.world.setEntityState(this, (byte) 3);
			this.setDead();
		}
	}

}
