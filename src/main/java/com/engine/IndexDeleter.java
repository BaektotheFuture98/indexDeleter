package com.engine;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Elasticsearch 인덱스 삭제 클래스
 * 인덱스 이름을 받아 직접 삭제합니다.
 */
public class IndexDeleter {
    private final RestClient restClient;
    private final String hosts;
    private final String user;
    private final String password;

    public IndexDeleter(String hosts, String user, String password) {
        this.hosts = hosts;
        this.user = user;
        this.password = password;
        this.restClient = createRestClient();
    }

    /**
     * RestClient 생성
     * 다중 호스트와 Base64 인증 방식 사용
     */
    private RestClient createRestClient() {
        try {
            List<String> list = Arrays.asList(hosts.split(","));
            HttpHost[] hostList = list.stream().map(host ->
                    new HttpHost(host.trim(), 9200, "http")
            ).toArray(HttpHost[]::new);

            String CREDENTIALS_STRING = user + ":" + password;
            String encodedBytes = Base64.getEncoder().encodeToString(CREDENTIALS_STRING.getBytes());
            Header[] headers = {
                    new BasicHeader("Authorization", "Basic " + encodedBytes)
            };

            RestClient client = RestClient.builder(hostList)
                    .setDefaultHeaders(headers)
                    .build();

            System.out.println("[IndexDeleter] ES client initialized. hosts=" + list + " user=" + user);
            return client;
        } catch (Exception e) {
            System.err.println("[IndexDeleter] ES client initialization error: " + e.getMessage());
            throw new RuntimeException("ElasticsearchRepo Initialization Error", e);
        }
    }

    /**
     * 특정 인덱스를 삭제합니다.
     */
    public boolean deleteIndex(String indexName) {
        try {
            Request request = new Request("DELETE", "/" + indexName);
            Response response = restClient.performRequest(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println("✓ 삭제됨: " + indexName);
                return true;
            } else {
                System.err.println("✗ 삭제 실패: " + indexName + " (상태 코드: " + response.getStatusLine().getStatusCode() + ")");
                return false;
            }
        } catch (Exception e) {
            System.err.println("✗ 삭제 중 오류: " + indexName + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 리소스를 해제합니다.
     */
    public void close() throws IOException {
        if (restClient != null) {
            restClient.close();
        }
    }
}

