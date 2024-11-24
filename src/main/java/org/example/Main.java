package org.example;


import java.time.Instant;


public class Main {
    public static void main(String[] args) {
        DocumentManager manager = new DocumentManager();

        final DocumentManager.Document document = new DocumentManager.Document(
                null,
                "t1",
                "c1",
                new DocumentManager.Author("i1", "n1"),
                Instant.now()
        );

        System.out.println(manager.save(document));
    }
}
