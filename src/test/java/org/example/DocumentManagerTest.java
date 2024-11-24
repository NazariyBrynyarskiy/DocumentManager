package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();

        documentManager.save(DocumentManager.Document.builder()
                .id("1")
                .title("Java Basics")
                .content("Learn Java programming")
                .author(DocumentManager.Author.builder().id("A1").name("Author1").build())
                .created(Instant.parse("2024-11-01T10:00:00Z"))
                .build());

        documentManager.save(DocumentManager.Document.builder()
                .id("2")
                .title("Advanced Java")
                .content("Deep dive into Java")
                .author(DocumentManager.Author.builder().id("A2").name("Author2").build())
                .created(Instant.parse("2024-11-05T15:00:00Z"))
                .build());

        documentManager.save(DocumentManager.Document.builder()
                .id("3")
                .title("Spring Boot Guide")
                .content("Learn Spring Boot")
                .author(DocumentManager.Author.builder().id("A1").name("Author1").build())
                .created(Instant.parse("2024-11-10T20:00:00Z"))
                .build());
    }

    @Test
    void testFilterByTitlePrefix() {
        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Java"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(2, results.size());
    }

    @Test
    void testFilterByAuthorIds() {
        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .authorIds(List.of("A1"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(2, results.size());
    }

    @Test
    void testFilterByDates() {
        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .createdFrom(Instant.parse("2024-11-01T00:00:00Z"))
                .createdTo(Instant.parse("2024-11-05T23:59:59Z"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(2, results.size());
    }

    @Test
    void testSearch() {
        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Spring"))
                .authorIds(List.of("A1"))
                .containsContents(List.of("Boot"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("Spring Boot Guide", results.get(0).getTitle());
    }

    @Test
    void save() throws NoSuchFieldException, IllegalAccessException {
        final DocumentManager.Document document = new DocumentManager.Document(
                null,
                "t1",
                "c1",
                new DocumentManager.Author("i1", "n1"),
                Instant.now()
        );
        documentManager.save(document);

        final Class<?> clazz = documentManager.getClass();
        final Field documentsField = clazz.getDeclaredField("documents");
        documentsField.setAccessible(true);
        final Map<String, DocumentManager.Document> documents =
                (Map<String, DocumentManager.Document>) documentsField.get(documentManager);

        assertTrue(documents.containsKey(document.getId()));
        assertTrue(documents.size() == 4);
        assertNotNull(document.getId());
    }
}