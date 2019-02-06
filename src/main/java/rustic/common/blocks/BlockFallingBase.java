package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.core.Rustic;

public class BlockFallingBase extends BlockFalling {

	public BlockFallingBase(Material mat, String name) {
		this(mat, name, true);
	}
	
	public BlockFallingBase(Material mat, String name, boolean register) {
		super(mat);
		setRegistryName(name);
		setUnlocalizedName(Rustic.MODID + "." + name);
		setHardness(1F);
		setCreativeTab(Rustic.decorTab);
		if (register) {
			GameRegistry.findRegistry(Block.class).register(this);
			GameRegistry.findRegistry(Item.class).register(new ItemBlock(this).setRegistryName(getRegistryName()));
		}
	}
	
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName().toString(),"inventory"));
	}
	
}
