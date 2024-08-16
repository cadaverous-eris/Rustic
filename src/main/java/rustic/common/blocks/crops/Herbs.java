package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.EnumPlantType;
import rustic.common.items.ItemHerbEdible;

public class Herbs {
	
	public static BlockHerbBase ALOE_VERA;
	public static BlockHerbBase BLOOD_ORCHID;
	public static BlockHerbBase CHAMOMILE;
	public static BlockHerbBase CLOUDSBLUFF_CROP;
	public static BlockHerbBase COHOSH;
	public static BlockHerbBase CORE_ROOT_CROP;
	public static BlockHerbBase DEATHSTALK;
	public static BlockHerbBase GINSENG_CROP;
	public static BlockHerbBase HORSETAIL;
	public static BlockHerbBase MARSH_MALLOW_CROP;
	public static BlockHerbBase MOONCAP;
	public static BlockHerbBase WIND_THISTLE;
	public static BlockHerbBase VANTA_LILY;
	
	public static ItemHerbEdible CLOUDSBLUFF;
	public static ItemHerbEdible CORE_ROOT;
	public static ItemHerbEdible GINSENG;
	public static ItemHerbEdible MARSH_MALLOW;

	public static void init() {
		ALOE_VERA = new BlockHerbBase("aloe_vera", false) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Desert;
			}

			@Override
			public Item getHerb() {
				return Item.getItemFromBlock(this);
			}
		};
		BLOOD_ORCHID = new BlockHerbBase("blood_orchid", false) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Plains;
			}

			@Override
			public Item getHerb() {
				return Item.getItemFromBlock(this);
			}
		};
		Blocks.FIRE.setFireInfo(BLOOD_ORCHID, 60, 100);
		CHAMOMILE = new BlockHerbBase("chamomile", false) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Plains;
			}

			@Override
			public Item getHerb() {
				return Item.getItemFromBlock(this);
			}
		};
		Blocks.FIRE.setFireInfo(CHAMOMILE, 60, 100);
		CLOUDSBLUFF_CROP = new BlockHerbBase("cloudsbluff", true) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Plains;
			}

			@Override
			public Item getHerb() {
				return CLOUDSBLUFF;
			}
		};
		Blocks.FIRE.setFireInfo(CLOUDSBLUFF_CROP, 60, 100);
		COHOSH = new BlockHerbBase("cohosh", false) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Plains;
			}

			@Override
			public Item getHerb() {
				return Item.getItemFromBlock(this);
			}
		};
		Blocks.FIRE.setFireInfo(COHOSH, 60, 100);
		CORE_ROOT_CROP = new BlockHerbBase("core_root", true) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Cave;
			}

			@Override
			public Item getHerb() {
				return CORE_ROOT;
			}
		};
		Blocks.FIRE.setFireInfo(CORE_ROOT_CROP, 60, 100);
		DEATHSTALK = new BlockHerbBase("deathstalk_mushroom", false) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Cave;
			}

			@Override
			public Item getHerb() {
				return Item.getItemFromBlock(this);
			}
		};
		GINSENG_CROP = new BlockHerbBase("ginseng", true){
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Plains;
			}

			@Override
			public Item getHerb() {
				return GINSENG;
			}
		};
		Blocks.FIRE.setFireInfo(GINSENG_CROP, 60, 100);
		HORSETAIL = new BlockHerbBase("horsetail", false) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Plains;
			}

			@Override
			public Item getHerb() {
				return Item.getItemFromBlock(this);
			}
		};
		Blocks.FIRE.setFireInfo(HORSETAIL, 60, 100);
		MARSH_MALLOW_CROP = new BlockHerbBase("marsh_mallow", true) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Plains;
			}

			@Override
			public Item getHerb() {
				return MARSH_MALLOW;
			}
		};
		Blocks.FIRE.setFireInfo(MARSH_MALLOW_CROP, 60, 100);
		MOONCAP = new BlockHerbBase("mooncap_mushroom", false) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Cave;
			}

			@Override
			public Item getHerb() {
				return Item.getItemFromBlock(this);
			}
			
			@Override
			 public int getLightValue(IBlockState state) {
				return 8;
			}
		};
		WIND_THISTLE = new BlockHerbBase("wind_thistle", false) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Plains;
			}

			@Override
			public Item getHerb() {
				return Item.getItemFromBlock(this);
			}
		};
		Blocks.FIRE.setFireInfo(WIND_THISTLE, 60, 100);
		VANTA_LILY = new BlockHerbBase("vanta_lily", false) {
			@Override
			public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
				return EnumPlantType.Plains;
			}

			@Override
			public Item getHerb() {
				return Item.getItemFromBlock(this);
			}
		};
		Blocks.FIRE.setFireInfo(VANTA_LILY, 60, 100);
				
		CLOUDSBLUFF = new ItemHerbEdible(CLOUDSBLUFF_CROP, 2, 0.2F) {
			@Override
			protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
				if (!worldIn.isRemote) {
					player.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 400));
				}
			}
		};
		CORE_ROOT = new ItemHerbEdible(CORE_ROOT_CROP, 2, 0.3F);
		GINSENG = new ItemHerbEdible(GINSENG_CROP, 2, 0.3F);
		MARSH_MALLOW = new ItemHerbEdible(MARSH_MALLOW_CROP, 3, 0.3F);
	}
	
	public static void initModels() {
		ALOE_VERA.initModel();
		BLOOD_ORCHID.initModel();
		CHAMOMILE.initModel();
		COHOSH.initModel();
		CORE_ROOT_CROP.initModel();
		DEATHSTALK.initModel();
		GINSENG_CROP.initModel();
		HORSETAIL.initModel();
		MARSH_MALLOW_CROP.initModel();
		MOONCAP.initModel();
		WIND_THISTLE.initModel();
		VANTA_LILY.initModel();
		
		CLOUDSBLUFF.initModel();
		CORE_ROOT.initModel();
		GINSENG.initModel();
		MARSH_MALLOW.initModel();
	}
	
	public static BlockHerbBase getRandomHerbForBiome(Biome biome, Random rand) {
		List<BlockHerbBase> herbs = new ArrayList<BlockHerbBase>();
		
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE)) {
			herbs.add(BLOOD_ORCHID);
			herbs.add(HORSETAIL);
			herbs.add(MARSH_MALLOW_CROP);
			herbs.add(MOONCAP);
		} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)) {
			herbs.add(ALOE_VERA);
		} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SAVANNA)) {
			herbs.add(ALOE_VERA);
		} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.MESA)) {
			herbs.add(ALOE_VERA);
		} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.MOUNTAIN) && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)) {
			herbs.add(WIND_THISTLE);
			herbs.add(CLOUDSBLUFF_CROP);
		} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP) && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)) {
			herbs.add(CHAMOMILE);
			herbs.add(HORSETAIL);
			herbs.add(MARSH_MALLOW_CROP);
		} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST) && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)) {
			herbs.add(CHAMOMILE);
			herbs.add(COHOSH);
			herbs.add(GINSENG_CROP);
			herbs.add(HORSETAIL);
			herbs.add(VANTA_LILY);
		} else if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.PLAINS) && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)) {
			herbs.add(CHAMOMILE);
			herbs.add(GINSENG_CROP);
			herbs.add(HORSETAIL);
			herbs.add(WIND_THISTLE);
			herbs.add(VANTA_LILY);
		} else {
			return null;
		}
		
		return herbs.get(rand.nextInt(herbs.size()));
	}
	
	public static BlockHerbBase getRandomCaveHerb(Random rand) {
		BlockHerbBase[] herbs = new BlockHerbBase[] {
				CORE_ROOT_CROP,
				MOONCAP
		};
		return herbs[rand.nextInt(herbs.length)];
	}
	
	public static BlockHerbBase getRandomNetherHerb(Random rand) {
		return DEATHSTALK;
	}
	
}
