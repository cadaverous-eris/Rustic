package rustic.common.tileentity;

public interface ITileEntitySyncable {
	
	public void markForUpdate();
	
	public boolean needsUpdate();
	
	public void clean();

}
