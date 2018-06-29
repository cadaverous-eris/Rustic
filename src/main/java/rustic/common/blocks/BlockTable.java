package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockTable extends BlockBase {
	
	public static final PropertyBool NW = PropertyBool.create("nw");
    public static final PropertyBool NE = PropertyBool.create("ne");
    public static final PropertyBool SE = PropertyBool.create("se");
    public static final PropertyBool SW = PropertyBool.create("sw");
	
	public BlockTable(String type) {
		super(Material.WOOD, "table_" + type);
		setHardness(1F);
		setSoundType(SoundType.WOOD);
		this.setDefaultState(this.blockState.getBaseState().withProperty(NW, true).withProperty(NE, true).withProperty(SE, true).withProperty(SW, true));
		
		Blocks.FIRE.setFireInfo(this, 5, 20);
	}
	
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess ba, BlockPos pos, EnumFacing side) {
		if (side == EnumFacing.UP) {
			return true;
		}
		return false;
	}
	
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }
    
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
    	IBlockState stateTemp = worldIn.getBlockState(pos.north());
        Block blockTemp = stateTemp.getBlock();
    	boolean blockNorth = blockTemp instanceof BlockTable;
    	stateTemp = worldIn.getBlockState(pos.south());
    	blockTemp = stateTemp.getBlock();
    	boolean blockSouth = blockTemp instanceof BlockTable;
    	stateTemp = worldIn.getBlockState(pos.east());
    	blockTemp = stateTemp.getBlock();
    	boolean blockEast = blockTemp instanceof BlockTable;
    	stateTemp = worldIn.getBlockState(pos.west());
    	blockTemp = stateTemp.getBlock();
    	boolean blockWest = blockTemp instanceof BlockTable;
    	
        return state.withProperty(NW, !blockNorth && !blockWest)
                .withProperty(NE, !blockNorth && !blockEast)
                .withProperty(SE, !blockSouth && !blockEast)
                .withProperty(SW, !blockSouth && !blockWest);
    }
    
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {NW, NE, SE, SW});
    }
    
    public static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0.875, 0, 1, 1, 1);
    
    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    	return AABB;
	}
    
    @Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		return (side == EnumFacing.UP) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

}
