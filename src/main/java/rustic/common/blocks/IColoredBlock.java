package rustic.common.blocks;

import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IColoredBlock {
	
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor();
	
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor();

}
