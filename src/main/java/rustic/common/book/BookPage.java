package rustic.common.book;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.gui.book.GuiBook;

public abstract class BookPage {
	
	protected BookEntry entry;
	
	public BookPage(BookEntry entry) {
		this.entry = entry;
	}
	
	@SideOnly(Side.CLIENT)
	public abstract void drawScreen(GuiBook gui, int mouseX, int mouseY, float partialTicks);
	
	@SideOnly(Side.CLIENT)
	public void update() {};
	
	@SideOnly(Side.CLIENT)
	public void onOpened(GuiBook gui) {}
	
	@SideOnly(Side.CLIENT)
	public void onClosed(GuiBook gui) {
		gui.getButtonList().clear();
	}
	
	@SideOnly(Side.CLIENT)
	public void actionPerformed(GuiBook gui, GuiButton button) {}
	
	@SideOnly(Side.CLIENT)
	public void keyPressed(char c, int key) {}

}
