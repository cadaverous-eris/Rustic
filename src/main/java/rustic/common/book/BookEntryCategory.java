package rustic.common.book;

import java.util.ArrayList;
import java.util.List;

import rustic.common.book.pages.BookPageCategory;

public class BookEntryCategory extends BookEntry {

	private static int entries_per_page = 12;
	
	private BookCategory entryCategory;
	
	public BookEntryCategory(String name, BookCategory category) {
		super(name, null);
		this.entryCategory = category;
	}
	
	@Override
	public String getName() {
		return entryCategory.getName();
	}
	
	@Override
	public List<BookPage> getPages() {
		List<BookPage> pages = new ArrayList<BookPage>();
		int numEntries = this.entryCategory.getEntries().size();
		int numPages = numEntries / entries_per_page;
		if (numEntries % entries_per_page != 0) {
			numPages++;
		}
		for (int i = 0; i < numPages; i++) {
			BookPageCategory page = new BookPageCategory(this.entryCategory, this);
			for (int j = 0; j < entries_per_page && (j + (i * entries_per_page)) < numEntries; j++) {
				page.addEntry(this.entryCategory.getEntries().get(j + (i * entries_per_page)));
			}
			pages.add(page);
		}
		
		return pages;
	}

}
