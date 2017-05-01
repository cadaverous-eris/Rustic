package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.core.Rustic;

public class BlockBase extends Block {

	public BlockBase(Material mat, String name) {
		super(mat);
		setRegistryName(name);
		setUnlocalizedName(Rustic.MODID + "." + name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		setHardness(1F);
		setCreativeTab(Rustic.decorTab);
	}
	
	public BlockBase(Material mat, String name, boolean register) {
		super(mat);
		setRegistryName(name);
		setUnlocalizedName(Rustic.MODID + "." + name);
		setHardness(1F);
		setCreativeTab(Rustic.decorTab);
		if (register) {
			GameRegistry.register(this);
			GameRegistry.register(new ItemBlock(this), getRegistryName());
		}
	}
	
	public void register(ItemBlock item) {
		GameRegistry.register(this);
		GameRegistry.register(item, getRegistryName());
	}
	
	public void register() {
		register(new ItemBlock(this));
	}
	
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName().toString(),"inventory"));
	}
	
	public BlockBase setBlockSoundType(SoundType soundType) {
		setSoundType(soundType);
		return this;
	}
	
}
