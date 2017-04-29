package rustic.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import rustic.client.gui.GuiApiary;
import rustic.client.gui.GuiBarrel;
import rustic.client.gui.GuiCabinet;
import rustic.client.gui.GuiCabinetDouble;
import rustic.client.gui.GuiCondenser;
import rustic.client.gui.GuiCondenserAdvanced;
import rustic.client.gui.GuiVase;
import rustic.common.blocks.BlockCabinet;
import rustic.common.tileentity.ContainerApiary;
import rustic.common.tileentity.ContainerBarrel;
import rustic.common.tileentity.ContainerCabinet;
import rustic.common.tileentity.ContainerCabinetDouble;
import rustic.common.tileentity.ContainerCondenser;
import rustic.common.tileentity.ContainerCondenserAdvanced;
import rustic.common.tileentity.ContainerVase;
import rustic.common.tileentity.TileEntityApiary;
import rustic.common.tileentity.TileEntityBarrel;
import rustic.common.tileentity.TileEntityCabinet;
import rustic.common.tileentity.TileEntityCondenser;
import rustic.common.tileentity.TileEntityCondenserAdvanced;
import rustic.common.tileentity.TileEntityVase;

public class GuiProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityApiary) {
			return new ContainerApiary(player.inventory, (TileEntityApiary) te);
		} else if (te instanceof TileEntityVase) {
			return ((TileEntityVase) te).createContainer(player.inventory, player);
		} else if (te instanceof TileEntityBarrel) {
			return ((TileEntityBarrel) te).createContainer(player.inventory, player);
		} else if (te instanceof TileEntityCabinet) {
			return ((TileEntityCabinet) te).createContainer(player.inventory, player);
		} else if (te instanceof TileEntityCondenser) {
			return new ContainerCondenser(player.inventory, (TileEntityCondenser) te);
		} else if (te instanceof TileEntityCondenserAdvanced) {
			return new ContainerCondenserAdvanced(player.inventory, (TileEntityCondenserAdvanced) te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityApiary) {
			TileEntityApiary apiaryTileEntity = (TileEntityApiary) te;
			return new GuiApiary(new ContainerApiary(player.inventory, apiaryTileEntity), player.inventory);
		} else if (te instanceof TileEntityVase) {
			return new GuiVase((ContainerVase) ((TileEntityVase) te).createContainer(player.inventory, player),
					player.inventory);
		} else if (te instanceof TileEntityBarrel) {
			return new GuiBarrel((ContainerBarrel) ((TileEntityBarrel) te).createContainer(player.inventory, player),
					player.inventory);
		} else if (te instanceof TileEntityCabinet) {
			if (world.getBlockState(pos).getValue(BlockCabinet.TOP)) {
				return new GuiCabinetDouble(
						(ContainerCabinetDouble) ((TileEntityCabinet) te).createContainer(player.inventory, player),
						player.inventory);
			} else if (world.getBlockState(pos.up()).getBlock() instanceof BlockCabinet
					&& world.getBlockState(pos.up()).getValue(BlockCabinet.TOP)) {
				return new GuiCabinetDouble(
						(ContainerCabinetDouble) ((TileEntityCabinet) te).createContainer(player.inventory, player),
						player.inventory);
			} else {
				return new GuiCabinet(
						(ContainerCabinet) ((TileEntityCabinet) te).createContainer(player.inventory, player),
						player.inventory);
			}
		} else if (te instanceof TileEntityCondenser) {
			return new GuiCondenser(new ContainerCondenser(player.inventory, (TileEntityCondenser) te), player.inventory);
		} else if (te instanceof TileEntityCondenserAdvanced) {
			return new GuiCondenserAdvanced(new ContainerCondenserAdvanced(player.inventory, (TileEntityCondenserAdvanced) te), player.inventory);
		}
		return null;
	}

}
