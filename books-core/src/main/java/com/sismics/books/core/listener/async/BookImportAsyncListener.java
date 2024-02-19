package com.sismics.books.core.listener.async;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sismics.books.core.dao.jpa.UserBookTagDao;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.sismics.books.core.dao.jpa.BookDao;
import com.sismics.books.core.dao.jpa.TagDao;
import com.sismics.books.core.dao.jpa.UserBookDao;
import com.sismics.books.core.dao.jpa.dto.TagDto;
import com.sismics.books.core.event.BookImportedEvent;
import com.sismics.books.core.model.context.AppContext;
import com.sismics.books.core.model.jpa.Book;
import com.sismics.books.core.model.jpa.Tag;
import com.sismics.books.core.model.jpa.UserBook;
import com.sismics.books.core.util.TransactionUtil;
import com.sismics.books.core.util.math.MathUtil;

/**
 * Listener on books import request.
 * 
 * @author bgamard
 */
public class BookImportAsyncListener {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(BookImportAsyncListener.class);
    private static final int isbn10Field = 6;
    private static final int bookIdField = 0;
    private static final int isbn13Field = 5;
    private static final int readDateField = 14;
    private static final int createDateField = 15;
    private static final int tagsField = 16;

    /**
     * Process the event.
     * 
     * @param bookImportedEvent Book imported event
     * @throws Exception
     */
    @Subscribe
    public void on(final BookImportedEvent bookImportedEvent) throws Exception {
        if (log.isInfoEnabled()) {
            log.info(MessageFormat.format("Books import requested event: {0}", bookImportedEvent.toString()));
        }

        // Create books and tags
        TransactionUtil.handle(new Runnable() {
            @Override
            public void run() {
                CSVReader reader = null;
                try {
                    reader = new CSVReader(new FileReader(bookImportedEvent.getImportFile()));
                } catch (FileNotFoundException e) {
                    log.error("Unable to read CSV file", e);
                }



                String [] line;
                try {

                    while ((line = reader.readNext()) != null) {
                        if (line[bookIdField].equals("Book Id")) {
                            // Skip header
                            continue;
                        }

                        // Retrieve ISBN number
                        String isbn = Strings.isNullOrEmpty(line[isbn10Field]) ? line[isbn13Field] : line[isbn10Field];
                        if (Strings.isNullOrEmpty(isbn)) {
                            log.warn("No ISBN number for Goodreads book ID: " + line[bookIdField]);
                            continue;
                        }

                        Book book = getOrCreateBook(isbn);
                        UserBook userBook = getOrCreateUserBook(book.getId(),bookImportedEvent.getUser().getId(),line);
                        Set<String> tagIdSet = createTags(line,bookImportedEvent.getUser().getId());
                        createUserBookTags(tagIdSet, userBook.getId());

                        TransactionUtil.commit();
                    }
                } catch (Exception e) {
                    log.error("Error parsing CSV line", e);
                }
            }
        });
    }

    private Book getOrCreateBook(String isbn) throws Exception{
        BookDao bookDao = new BookDao();
        // Fetch the book from database if it exists
        Book book = bookDao.getByIsbn(isbn);
        if (book == null) {
            // Try to get the book from a public API
            try {
                book = AppContext.getInstance().getBookDataService().searchBook(isbn);
            } catch (Exception e) {
                throw e;
            }

            // Save the new book in database
            bookDao.create(book);
        }
        return book;
    }

    private UserBook getOrCreateUserBook(String bookId,String userId, String[] line){
        UserBookDao userBookDao = new UserBookDao();

        // Goodreads date format
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd");

        // Create a new user book if needed
        UserBook userBook = userBookDao.getByBook(bookId, userId);
        if (userBook == null) {
            userBook = new UserBook();
            userBook.setUserId(userId);
            userBook.setBookId(bookId);
            userBook.setCreateDate(new Date());
            if (!Strings.isNullOrEmpty(line[readDateField])) {
                userBook.setReadDate(formatter.parseDateTime(line[readDateField]).toDate());
            }
            if (!Strings.isNullOrEmpty(line[createDateField])) {
                userBook.setCreateDate(formatter.parseDateTime(line[createDateField]).toDate());
            }
            userBookDao.create(userBook);
        }
        return userBook;
    }

    private Set<String> createTags(String[] line, String userId){
        TagDao tagDao = new TagDao();

        // Create tags
        String[] bookshelfArray = line[tagsField].split(",");
        Set<String> tagIdSet = new HashSet<String>();
        for (String bookshelf : bookshelfArray) {
            bookshelf = bookshelf.trim();
            if (Strings.isNullOrEmpty(bookshelf)) {
                continue;
            }

            Tag tag = tagDao.getByName(userId, bookshelf);
            if (tag == null) {
                tag = new Tag();
                tag.setName(bookshelf);
                tag.setColor(MathUtil.randomHexColor());
                tag.setUserId(userId);
                tagDao.create(tag);
            }

            tagIdSet.add(tag.getId());
        }

        return tagIdSet;
    }

    private void createUserBookTags(Set<String> tagIdSet, String userBookId){
        UserBookTagDao userBookTagDao = new UserBookTagDao();

        // Add tags to the user book
        if (tagIdSet.size() > bookIdField) {
            List<TagDto> tagDtoList = userBookTagDao.getByUserBookId(userBookId);
            for (TagDto tagDto : tagDtoList) {
                tagIdSet.add(tagDto.getId());
            }
            userBookTagDao.updateTagList(userBookId, tagIdSet);
        }
    }


}
