package rustic.common.blocks.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyBool implements IUnlistedProperty<Boolean> {

	private String name;
	
	public UnlistedPropertyBool(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isValid(Boolean value) {
		return true;
	}

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}

	@Override
	public String valueToString(Boolean value) {
		return value.toString();
	}
	
}
