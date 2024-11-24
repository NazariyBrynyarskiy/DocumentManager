package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> documents;

    public DocumentManager() {
        documents = new HashMap<>();
    }


    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.id == null) {
            final String id = generateId();
            document.setId(id);
        }
        documents.put(document.id, document);

        return document;
    }

    private String generateId() {
        String id = UUID.randomUUID().toString();

        while (documents.containsKey(id)) {
            id = UUID.randomUUID().toString();
        }

        return id;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        Collection<Document> documents = this.documents.values();

        documents = filterByTitlePrefix(request, documents);
        documents = filterByContents(request, documents);
        documents = filterByAuthorIds(request, documents);
        documents = filterByDates(request, documents);

        return new ArrayList<>(documents);
    }

    private Collection<Document> filterByTitlePrefix(SearchRequest request,
                                                     Collection<Document> documents) {
        if (request.titlePrefixes != null) {
            for (String title : request.titlePrefixes) {
                documents = documents
                        .stream()
                        .filter(document -> document.title.contains(title))
                        .toList();
            }
        }
        return documents;
    }

    private Collection<Document> filterByContents(SearchRequest request,
                                                  Collection<Document> documents) {
        if (request.containsContents != null) {
            for (String contents : request.containsContents) {
                documents = documents
                        .stream()
                        .filter(document -> document.title.contains(contents))
                        .toList();
            }
        }
        return documents;
    }

    private Collection<Document> filterByAuthorIds(SearchRequest request,
                                                   Collection<Document> documents) {
        if (request.authorIds != null) {
            for (String authorId : request.authorIds) {
                documents = documents
                        .stream()
                        .filter(document -> {
                            final String documentAuthorId = document.author.id;
                            return documentAuthorId.equals(authorId);
                        })
                        .toList();
            }
        }
        return documents;
    }

    private Collection<Document> filterByDates(SearchRequest request,
                                               Collection<Document> documents) {
        final Instant createdFrom = request.createdFrom;
        final Instant createdTo = request.createdTo;

        if (createdFrom != null || createdTo != null) {
            if (createdFrom != null) {
                documents = documents
                        .stream()
                        .filter(document -> document.created.isAfter(createdFrom))
                        .toList();
            }
            if (createdTo != null) {
                documents = documents
                        .stream()
                        .filter(document -> document.created.isBefore(createdTo))
                        .toList();
            }
        }
        return documents;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        final Document document = documents.get(id);

        return Optional.ofNullable(document);
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}