package rustic.client.gui.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.book.BookEntry;
import rustic.common.book.BookEntryCategory;
import rustic.common.book.BookManager;
import rustic.common.book.BookPage;
import rustic.common.network.PacketHandler;
import rustic.core.Rustic;

@SideOnly(Side.CLIENT)
public class GuiBook extends GuiScreen {

	public static final int WIDTH = 146;
	public static final int HEIGHT = 180;

	public static final ResourceLocation BOOK_BACKGROUND = new ResourceLocation(Rustic.MODID, "textures/gui/book/book.png");

	public int guiLeft, guiTop;

	private BookEntry currentEntry = null, prevEntry = null;
	private int currentPageNum = 0, prevPageNum = 0;
	private BookPage currentPage = null;
	
	private BookLocation backLocation = null;
	private List<BookLocation> history = new ArrayList<BookLocation>();

	public GuiBook() {

	}

	@Override
	public void initGui() {
		this.guiTop = (this.height - HEIGHT) / 2;
		this.guiLeft = (this.width - WIDTH) / 2;

		if (this.currentEntry == null || this.currentPage == null) {
			this.currentEntry = BookManager.categories_entry;
			this.currentPage = currentEntry.getPages().get(currentPageNum);
			this.pageOpened();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawBackgroundLayer(partialTicks, mouseX, mouseY);
		super.drawScreen(mouseX, mouseY, partialTicks);

		this.currentPage.drawScreen(this, mouseX, mouseY, partialTicks);
	}

	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(BOOK_BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
	}

	@Override
	public void updateScreen() {
		prevEntry = currentEntry;
		prevPageNum = currentPageNum;
		
		if (!this.mc.player.isEntityAlive() || this.mc.player.isDead) {
			this.mc.player.closeScreen();
		}

		currentPage.update();
	}

	public void goToPage(int pageNum) {
		if (pageNum >= 0 && this.currentEntry.getPages().size() > pageNum && prevPageNum == currentPageNum) {
			this.currentPage.onClosed(this);
			this.currentPageNum = pageNum;
			this.currentPage = currentEntry.getPages().get(currentPageNum);
			this.pageOpened();
		}
	}

	public void goToEntry(BookEntry entry) {
		if (entry != null && entry.getPages().size() > 0 && prevEntry == currentEntry) {
			if (currentEntry instanceof BookEntryCategory) {
				if (!(entry instanceof BookEntryCategory) && entry != BookManager.categories_entry) {
					this.backLocation = new BookLocation(this.currentEntry, this.currentPageNum);
				}
			}
			if (entry instanceof BookEntryCategory || entry == BookManager.categories_entry) {
				this.backLocation = null;
			}
			
			if (entry instanceof BookEntryCategory || entry == BookManager.categories_entry) {
				this.history.clear();
			} else if (!(this.currentEntry instanceof BookEntryCategory) && this.currentEntry != BookManager.categories_entry) {
				this.history.add(new BookLocation(this.currentEntry, this.currentPageNum));
			}
			
			this.currentPage.onClosed(this);
			this.currentEntry = entry;
			this.currentPageNum = 0;
			this.currentPage = currentEntry.getPages().get(currentPageNum);
			this.pageOpened();
		}
	}
	
	private void goToHistoryLocation(int index) {
		if (index >= 0 && index < this.history.size() && prevEntry == currentEntry && prevPageNum == currentPageNum) {
			BookEntry entry = this.history.get(index).getEntry();
			int page = this.history.get(index).getPage();
			
			for (int i = index; i < this.history.size();) {
				history.remove(i);
			}
			
			this.currentPage.onClosed(this);
			this.currentEntry = entry;
			this.currentPageNum = page;
			this.currentPage = currentEntry.getPages().get(currentPageNum);
			this.pageOpened();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof PageTurnButton) {
			if (((PageTurnButton) button).isForward()) {
				this.goToPage(currentPageNum + 1);
			} else {
				this.goToPage(currentPageNum - 1);
			}
		} else if (button instanceof BackButton) {
			if (currentEntry.getCategory() == null) {
				this.goToEntry(BookManager.categories_entry);
			} else if (backLocation != null) {
				this.backLocation.goTo(this);
			} else {
				this.goToEntry(currentEntry.getCategory().getCategoryEntry());
			}
		} else if (button instanceof LastHistoryButton) {
			this.goToHistoryLocation(this.history.size() - 1);
		} else {
			this.currentPage.actionPerformed(this, button);
		}
	}

	@Override
	protected void keyTyped(char c, int key) throws IOException {
		currentPage.keyPressed(c, key);

		if (mc.gameSettings.keyBindInventory.getKeyCode() == key) {
			if (currentEntry == BookManager.categories_entry) {
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
			} else if (currentEntry.getCategory() == null) {
				this.goToEntry(BookManager.categories_entry);
			} else if (backLocation != null) {
				this.backLocation.goTo(this);
			} else {
				this.goToEntry(currentEntry.getCategory().getCategoryEntry());
			}
		} else if (1 == key) {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}

	@Override
	public void onGuiClosed() {
		this.currentPage.onClosed(this);
	}
	
	protected void pageOpened() {
		this.currentPage.onOpened(this);
	}

	public void addNavButtons() {
		int numPages = this.currentEntry.getPages().size();
		if (this.currentPageNum > 0) {
			this.addButton(new PageTurnButton(this.buttonList.size(), guiLeft, guiTop + HEIGHT - 10, false));
		}
		if (this.currentPageNum < numPages - 1) {
			this.addButton(
					new PageTurnButton(nextButtonID(), guiLeft + WIDTH - 18, guiTop + HEIGHT - 10, true));
		}
		boolean backButton = this.currentEntry != BookManager.categories_entry;
		boolean histButton = this.history.size() > 0 && this.currentEntry != BookManager.categories_entry;
		if (backButton) {
			this.addButton(new BackButton(nextButtonID(), (histButton) ? guiLeft + (WIDTH / 2) - (30 / 2) : guiLeft + (WIDTH / 2) - 8, guiTop + HEIGHT + 0));
		}
		if (histButton) {
			this.addButton(new LastHistoryButton(nextButtonID(), (backButton) ? guiLeft + (WIDTH / 2) + (30 / 2) - 10 : guiLeft + (WIDTH / 2) - 5, guiTop + HEIGHT - 3));
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void onResize(Minecraft mcIn, int w, int h) {
		this.setWorldAndResolution(mcIn, w, h);
		this.currentPage.onClosed(this);
		this.pageOpened();
	}

	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}

	public List<GuiButton> getButtonList() {
		return this.buttonList;
	}
	
	public int nextButtonID() {
		return this.buttonList.size();
	}

	public void drawCenteredText(String text, int x, int y, int color) {
		int cX = x - (this.fontRenderer.getStringWidth(text) / 2);
		this.fontRenderer.drawString(text, cX, y, color);
	}
	
	@Override
	public void drawHorizontalLine(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }
        drawRect(startX, y, endX + 1, y + 1, color);
    }

	@Override
    public void drawVerticalLine(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }
        drawRect(x, startY + 1, x + 1, endY, color);
    }
	
	@Override
	public void renderToolTip(ItemStack stack, int x, int y) {
		super.renderToolTip(stack, x, y);
	}

	@SideOnly(Side.CLIENT)
	public class PageTurnButton extends GuiButton {

		private final boolean isForward;

		public PageTurnButton(int buttonId, int x, int y, boolean isForward) {
			super(buttonId, x, y, 18, 10, "book.rustic.button." + (isForward ? "next" : "prev"));
			this.isForward = isForward;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
						&& mouseY < this.y + this.height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
				int i = 0;
				int j = 180;

				if (flag) {
					i += 18;
				}

				if (!this.isForward) {
					j += 10;
				}

				this.drawTexturedModalRect(this.x, this.y, i, j, 18, 10);
			}
		}

		public boolean isForward() {
			return this.isForward;
		}

	}

	@SideOnly(Side.CLIENT)
	public class BackButton extends GuiButton {

		public BackButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 16, 10, "book.rustic.button.back");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
						&& mouseY < this.y + this.height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
				int i = 0;
				int j = 200;

				if (flag) {
					i += 16;
				}

				this.drawTexturedModalRect(this.x, this.y, i, j, 16, 10);
			}
		}

	}
	
	@SideOnly(Side.CLIENT)
	public class LastHistoryButton extends GuiButton {

		public LastHistoryButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 10, 16, "book.rustic.button.last_history");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
						&& mouseY < this.y + this.height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
				int i = 36;
				int j = 193;

				if (flag) {
					i += 10;
				}

				this.drawTexturedModalRect(this.x, this.y, i, j, 10, 16);
			}
		}

	}
	
	private class BookLocation {
		
		private BookEntry entry;
		private int page;
		
		public BookLocation(BookEntry entry, int page) {
			this.entry = entry;
			this.page = page;
		}
		
		public void goTo(GuiBook gui) {
			gui.goToEntry(this.entry);
			gui.goToPage(this.page);
		}
		
		public BookEntry getEntry() {
			return this.entry;
		}
		
		public int getPage() {
			return this.page;
		}
		
	}
}
