package com.engine;

/**
 * Elasticsearch 인덱스 삭제 유틸리티
 * 사용법: java -jar IndexDelete.jar <indexName>
 * 예시: java -jar IndexDelete.jar "lucy_main_v1_2044"
 */
public class IndexDeleteMain {
    private static final String ELASTICSEARCH_HOSTS = "localhost:9200";
    private static final String ELASTICSEARCH_USER = "elastic";
    private static final String ELASTICSEARCH_PASSWORD = "changeme";

    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        String indexName = args[0];

        try {
            IndexDeleter deleter = new IndexDeleter(ELASTICSEARCH_HOSTS, ELASTICSEARCH_USER, ELASTICSEARCH_PASSWORD);
            boolean deleted = deleter.deleteIndex(indexName);

            if (deleted) {
                System.out.println("✓ 성공: " + indexName + " 인덱스가 삭제되었습니다.");
            } else {
                System.out.println("✗ 실패: " + indexName + " 인덱스 삭제에 실패했습니다.");
                System.exit(1);
            }

            deleter.close();
        } catch (Exception e) {
            System.err.println("✗ 오류: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Elasticsearch 인덱스 삭제 도구");
        System.out.println();
        System.out.println("사용법:");
        System.out.println("  java -jar IndexDelete.jar <indexName>");
        System.out.println();
        System.out.println("예시:");
        System.out.println("  java -jar IndexDelete.jar lucy_main_v1_2044");
        System.out.println("  java -jar IndexDelete.jar logs-2024-01-18");
    }
}



