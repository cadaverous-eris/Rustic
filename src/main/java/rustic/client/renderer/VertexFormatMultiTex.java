package rustic.client.renderer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//OLDMODEL_POSITION_TEX_NORMAL
@SideOnly(Side.CLIENT)
public class VertexFormatMultiTex extends VertexFormat {
	
	public static VertexFormatMultiTex OLDMODEL_POSITION_MULTITEX_NORMAL;
	
	// for debugging
	public static boolean ENABLE_TEX_UNIT_3_UVS = true;
	
	private final VertexFormatElement TEX_3_UVS = new VertexFormatElement(3, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.UV, 2);
	
	private final List<VertexFormatElement> elementsOverride = new ArrayList<>();
	private boolean constructed = false;
	
	public VertexFormatMultiTex() {
		super(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
		
		this.elementsOverride.clear();
		this.elementsOverride.addAll(super.getElements());
		this.elementsOverride.add(TEX_3_UVS);
		
		constructed = true;
	}
	
	public static void init() {
		OLDMODEL_POSITION_MULTITEX_NORMAL = new VertexFormatMultiTex();
	}
	
	@Override
	public void clear() {
		super.clear();
    }
	
	@Override
	public VertexFormat addElement(VertexFormatElement element) {
		super.addElement(element);
		
		if (!constructed) return this;
		
		this.elementsOverride.clear();
		this.elementsOverride.addAll(super.getElements());
		this.elementsOverride.add(TEX_3_UVS);
		
		return this;
	}
	
	@Override
	public boolean hasUvOffset(int id) {
		//return (ENABLE_TEX_UNIT_3_UVS && (id == 3)) || super.hasUvOffset(id);
		if (ENABLE_TEX_UNIT_3_UVS && (id == 3)) return super.hasUvOffset(0);
		else return super.hasUvOffset(id);
    }

	@Override
    public int getUvOffsetById(int id) {
		if (ENABLE_TEX_UNIT_3_UVS && (id == 3)) return super.getUvOffsetById(0);
		else return super.getUvOffsetById(id);
    }
	
	@Override
	public List<VertexFormatElement> getElements() {
        return ENABLE_TEX_UNIT_3_UVS ? this.elementsOverride : super.getElements();
    }

	@Override
    public int getElementCount() {
        return super.getElementCount();
    }
	
	@Override
	public VertexFormatElement getElement(int index) {
		return ENABLE_TEX_UNIT_3_UVS ? this.elementsOverride.get(index): super.getElement(index);
    }

	@Override
    public int getOffset(int index) {
		//System.out.println("overridden " + ENABLE_TEX_UNIT_3_UVS + " index=" + index);
		if (ENABLE_TEX_UNIT_3_UVS && (index == (this.elementsOverride.size() - 1)))
			return getUvOffsetById(0);
		else
			return super.getOffset(index);
    }

}
