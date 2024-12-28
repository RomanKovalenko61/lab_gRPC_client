package org.example;

import org.example.grpc.BookOuterClass;

import java.nio.charset.StandardCharsets;

public class LibraryUtils {
    public static String formatBook(BookOuterClass.Book book) {
        String title = formatTitle(book);
        return "Название: \"" + title + "\"\n" +
                "год: " + book.getYear() + "\n" +
                "жанр: " + translateGenre(book.getGenre());
    }

    public static String formatTitle(BookOuterClass.Book book) {
        byte[] titleBytes = book.getTitle().getBytes();
        return new String(titleBytes, StandardCharsets.UTF_8);
    }

    public static String translateGenre(BookOuterClass.Genre genre) {
        switch (genre) {
            case FICTION:
                return "Художественная литература";
            case NON_FICTION:
                return "Документальная литература";
            case SCIENCE:
                return "Научная литература";
            case HISTORY:
                return "Историческая литература";
            case FANTASY:
                return "Фэнтези";
            default:
                throw new IllegalArgumentException("Unknown genre: " + genre);
        }
    }
}