package rustic.common.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.renderer.CabinetRenderer;
import rustic.client.renderer.LiquidBarrelRenderer;
import rustic.common.tileentity.TileEntityBarrel;
import rustic.common.tileentity.TileEntityCabinet;
import rustic.common.tileentity.TileEntityLiquidBarrel;

public class BlockLiquidBarrel extends BlockBase implements ITileEntityProvider {
	
	protected static final AxisAlignedBB BARREL_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1D, 0.875D);

	public BlockLiquidBarrel() {
		super(Material.WOOD, "liquid_barrel");
		this.setHardness(1.5F);
		setSoundType(SoundType.WOOD);
	}
	
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		NBTTagCompound nbttagcompound = stack.getTagCompound();
		
		if (nbttagcompound != null) {
            if (nbttagcompound.hasKey("FluidName", Constants.NBT.TAG_STRING) && nbttagcompound.hasKey("Amount", Constants.NBT.TAG_INT)) {
            	Fluid fluid = FluidRegistry.getFluid(nbttagcompound.getString("FluidName"));
            	int amount = nbttagcompound.getInteger("Amount");
            	FluidStack fluidStack = new FluidStack(fluid, amount);
                tooltip.add(amount + "mb of " + fluid.getLocalizedName(fluidStack));
            }    
        }
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityLiquidBarrel();
	}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
		((TileEntityLiquidBarrel)world.getTileEntity(pos)).breakBlock(world,pos,state,player);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		return ((TileEntityLiquidBarrel)world.getTileEntity(pos)).activate(world,pos,state,player,hand,side,hitX,hitY,hitZ);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		return items;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack){
		super.onBlockPlacedBy(world, pos, state, player, stack);
		if (stack.hasTagCompound()){
			TileEntityLiquidBarrel tile = (TileEntityLiquidBarrel)createNewTileEntity(world, getMetaFromState(state));
			world.setTileEntity(pos, tile);
			tile.getTank().readFromNBT(stack.getTagCompound());
			tile.markDirty();
		}
	}
	
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BARREL_AABB;
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLiquidBarrel.class, new LiquidBarrelRenderer());
    }

}
