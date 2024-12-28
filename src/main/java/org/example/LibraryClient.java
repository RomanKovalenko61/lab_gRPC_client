package org.example;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.grpc.BookOuterClass;
import org.example.grpc.LibraryServiceGrpc;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryClient {
    private static final Logger logger = Logger.getLogger(LibraryClient.class.getName());

    private final ManagedChannel channel;
    private final LibraryServiceGrpc.LibraryServiceBlockingStub blockingStub;

    public LibraryClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    LibraryClient(ManagedChannel channel) {
        this.channel = channel;
        this.blockingStub = LibraryServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
    }

    public void getAllBooks() {
        Iterator<BookOuterClass.Book> allBooks = blockingStub.getAllBooks(Empty.getDefaultInstance());
        while (allBooks.hasNext()) {
            System.out.println("---");
            System.out.println(LibraryUtils.formatBook(allBooks.next()));
            System.out.println("***");
        }
    }

    public void addBook(String title, int year, BookOuterClass.Genre genre) {
        BookOuterClass.Book book = BookOuterClass.Book.newBuilder()
                .setTitle(title)
                .setYear(year)
                .setGenre(genre)
                .build();
        blockingStub.addBook(book);
        System.out.println("Книга " + LibraryUtils.formatTitle(book) + " добавлена успешно.");
    }

    public void updateBook(String title, int year, BookOuterClass.Genre genre) {
        BookOuterClass.Book book = BookOuterClass.Book.newBuilder()
                .setTitle(title)
                .setYear(year)
                .setGenre(genre)
                .build();
        try {
            blockingStub.updateBook(book);
            System.out.println("Книга обновлена успешно.");
        } catch (Throwable t) {
            logger.log(Level.WARNING, "Ошибка обновления книги: {0}", t.getMessage());
        }
    }

    public void deleteBook(String title) {
        BookOuterClass.Book book = BookOuterClass.Book.newBuilder()
                .setTitle(title)
                .build();
        try {
            blockingStub.deleteBook(book);
            System.out.println("Книга удалена успешно.");
        } catch (Throwable t) {
            logger.log(Level.WARNING, "Ошибка удаления книги: {0}", t.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        LibraryClient client = new LibraryClient(System.getenv("SERVER_ADDRESS"), 8080);

        System.out.println("Имитация взаимодействия с сервером");
        System.out.println("Добавляем пять книг ...");

        client.addBook("Властелин колец", 1954, BookOuterClass.Genre.FANTASY);
        client.addBook("История государства Российского", 1818, BookOuterClass.Genre.HISTORY);
        client.addBook("Происхождение видов", 1850, BookOuterClass.Genre.SCIENCE);
        client.addBook("Чёрный лебедь. Под знаком непредсказуемости", 2007, BookOuterClass.Genre.NON_FICTION);
        client.addBook("Мастер и Маргарита", 1967, BookOuterClass.Genre.FICTION);

        System.out.println("=========================");
        System.out.println("Запрос дай нам все книги");
        client.getAllBooks();
        System.out.println("=========================");
        System.out.println("Кажется у нас ошибка. Книга Происхождение видов была издана в 1859. Давай исправим");
        System.out.println("Отправляю запрос на корректировку");
        client.updateBook("Происхождение видов", 1859, BookOuterClass.Genre.SCIENCE);
        System.out.println("=========================");
        System.out.println("Запрос дай нам все книги. Проверим исправление");
        client.getAllBooks();
        System.out.println("=========================");
        System.out.println("РКН говорит надо убрать книгу Черный лебедь");
        client.deleteBook("Чёрный лебедь. Под знаком непредсказуемости");
        System.out.println("=========================");
        System.out.println("Проверяем список всех книг");
        client.getAllBooks();
        client.shutdown();
    }
}
