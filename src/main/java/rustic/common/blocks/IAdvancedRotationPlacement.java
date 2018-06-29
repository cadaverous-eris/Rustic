package rustic.common.blocks;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public interface IAdvancedRotationPlacement {
	
	public default IBlockState getStateForAdvancedRotationPlacement(IBlockState defaultState, PropertyDirection facingProperty, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = defaultState;
		
		if (facing.getAxis() == Axis.Y) {
			float hitXFromCenter = hitX - 0.5F;
			float hitZFromCenter = hitZ - 0.5F;
			
			if (Math.max(Math.abs(hitXFromCenter), Math.abs(hitZFromCenter)) == Math.abs(hitXFromCenter)) {
				state = state.withProperty(facingProperty, (hitXFromCenter > 0) ? EnumFacing.EAST : EnumFacing.WEST);
			} else {
				state = state.withProperty(facingProperty, (hitZFromCenter > 0) ? EnumFacing.SOUTH : EnumFacing.NORTH);
			}
		} else {
			state = state.withProperty(facingProperty, facing.getOpposite());
		}
		
		return state;
	}
	
}
