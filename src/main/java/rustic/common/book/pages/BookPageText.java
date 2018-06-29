package rustic.common.book.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.gui.book.GuiBook;
import rustic.common.book.BookEntry;
import rustic.common.book.BookPage;

public class BookPageText extends BookPage {

	protected List<String> textKeys = new ArrayList<String>();
	protected Set<BookEntry> relatedEntries = new HashSet<BookEntry>();
	
	public BookPageText(BookEntry entry, String... textKeys) {
		super(entry);
		for (String textKey : textKeys) {
			this.textKeys.add("book.rustic.text." + textKey);
		}
	}

	@Override
	public void drawScreen(GuiBook gui, int mouseX, int mouseY, float partialTicks) {
		int y = gui.guiTop + 12;
		int x = gui.guiLeft + (GuiBook.WIDTH / 2);
		
		int maxY = gui.guiTop + GuiBook.HEIGHT - 19;
		
		gui.drawCenteredText(TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + I18n.translateToLocal(entry.getName()), x, y, 0x000000);
		y += gui.getFontRenderer().FONT_HEIGHT + 4;
		x = gui.guiLeft + 16;
		
		float textScale = 0.75F;
		GlStateManager.pushMatrix();
		GlStateManager.scale(textScale, textScale, textScale);
		for (int i = 0; i < this.textKeys.size(); i++) {
			String text = I18n.translateToLocal(this.textKeys.get(i));
			int increment = (int) ((gui.getFontRenderer().FONT_HEIGHT + 2) * textScale);
			List<String> lines = gui.getFontRenderer().listFormattedStringToWidth(text, (int) ((GuiBook.WIDTH - 32) / textScale));
			for (String line : lines) {
				if (y + increment > maxY) {
					break;
				}
				gui.getFontRenderer().drawString(line, (int) (x / textScale), (int) (y / textScale), 0x000000);
				y += increment;
			}
			y += (int) (4 * textScale);
		}
		GlStateManager.popMatrix();
		
		if (relatedEntries.size() > 0) {
			String text = TextFormatting.UNDERLINE + I18n.translateToLocal("book.rustic.label.related_entries");
			int textWidth = gui.getFontRenderer().getStringWidth(text);
			GlStateManager.color(1F, 1F, 1F, 1F);
			gui.mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
			gui.drawTexturedModalRect(gui.guiLeft + GuiBook.WIDTH, gui.guiTop + 10, (146 - 10) - (textWidth + 2), 210, (textWidth + 2) + 10, 16);
			
			gui.getFontRenderer().drawString(text, gui.guiLeft + GuiBook.WIDTH + 2, gui.guiTop + 10 + 4, 0x000000);
		}
	}
	
	@Override
	public void onOpened(GuiBook gui) {
		super.onOpened(gui);
		
		gui.addNavButtons();
		
		int x = gui.guiLeft + GuiBook.WIDTH;
		int y = gui.guiTop + 10 + 16 + 2;
		
		int i = 0;
		for (BookEntry entry : this.relatedEntries) {
			if (i < 9) {
				gui.getButtonList().add(new RelatedEntryButton(gui.nextButtonID(), x, y, gui.getFontRenderer(), entry));
				
				y += 16;
				i++;
			}
		}
		
	}
	
	@Override
	public void onClosed(GuiBook gui) {
		super.onClosed(gui);
	}
	
	@Override
	public void actionPerformed(GuiBook gui, GuiButton button) {
		if (button instanceof RelatedEntryButton) {
			gui.goToEntry(((RelatedEntryButton) button).getEntry());
		}
	}
	
	public BookPageText addRelatedEntry(BookEntry entry) {
		if (entry != null) {
			this.relatedEntries.add(entry);
		}
		return this;
	}
	
	public BookPageText addRelatedEntries(BookEntry... entries) {
		for (BookEntry entry : entries)
			if (entry != null) {
				this.relatedEntries.add(entry);
			}
		return this;
	}
	
	@SideOnly(Side.CLIENT)
	public class RelatedEntryButton extends GuiButton {

		private static final int BASE_WIDTH = 24;
		private static final int BASE_HEIGHT = 16;
		
		private int ticksHovered = 0;
		
		private FontRenderer fontRenderer;
		private final BookEntry entry;
		
		public RelatedEntryButton(int buttonId, int x, int y, FontRenderer fontRenderer, BookEntry entry) {
			super(buttonId, x, y, BASE_WIDTH, BASE_HEIGHT, entry.getName());
			this.fontRenderer = fontRenderer;
			this.entry = entry;
		}
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				String text = I18n.translateToLocal(this.displayString);
				int textWidth = this.fontRenderer.getStringWidth(text);
				
				boolean flag = mouseX >= this.x && mouseY >= this.y
						&& mouseX < this.x + this.width && mouseY < this.y + this.height;
				if (flag) {
					if (this.width < 140 && this.width < textWidth + BASE_WIDTH + 2) {
						this.ticksHovered++;
						this.width = Math.min(BASE_WIDTH + (ticksHovered * 8), Math.min(textWidth + BASE_WIDTH + 2, 144));
					}
				} else {
					this.ticksHovered = 0;
					if (this.width > BASE_WIDTH) {
						this.width = Math.max(this.width - 12, BASE_WIDTH);
					}
				}
				mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
				Gui.drawModalRectWithCustomSizedTexture(x, y, 146 - this.width, 226, this.width, this.height, 256F, 256F);
				
				text = this.fontRenderer.trimStringToWidth(text, this.width - BASE_WIDTH, true);
				textWidth = this.fontRenderer.getStringWidth(text);
				this.fontRenderer.drawString(text, x + (this.width - BASE_WIDTH) - textWidth, y + 4, 0x000000);
				
				if (this.entry != null && !this.entry.getIcon().isEmpty()) {
					GlStateManager.pushMatrix();
					GlStateManager.color(1F, 1F, 1F, 1F);
					RenderHelper.enableGUIStandardItemLighting();
					
					mc.getRenderItem().renderItemIntoGUI(this.entry.getIcon(), x + width - 22, y);
					
					RenderHelper.disableStandardItemLighting();
					GlStateManager.popMatrix();
				}
			}
		}
		
		public BookEntry getEntry() {
			return this.entry;
		}
		
	}

}
