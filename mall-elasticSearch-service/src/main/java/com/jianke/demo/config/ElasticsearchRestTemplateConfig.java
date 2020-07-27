//package com.jianke.demo.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//import java.util.Arrays;
//import java.util.Objects;
//
///**
// * @author Shizhi Wu
// **/
//@Configuration
//@EnableScheduling
//@EnableAutoConfiguration(exclude = ElasticsearchDataAutoConfiguration.class)
//@Slf4j
//public class ElasticsearchRestTemplateConfig {
//    @Value("${mall-index-service.cluster-nodes:172.17.240.9:9200}")
//    private String esHost;
//
//    @Value("${mall-index-service.cluster-name:elasticsearch-dev}")
//    private String clusterName;
//
//    @Value("${mall-index-service.es-username:}")
//    private String esUsername;
//
//    @Value("${mall-index-service.es-password:}")
//    private String esPassword;
//
//    @Bean
//    public ElasticsearchRestTemplate elasticsearchRestTemplate(RestHighLevelClient client) {
//        return new ElasticsearchRestTemplate(client);
//    }
//
//
//    @Bean
//    public RestHighLevelClient client() {
//        String[] clustersSplit = esHost.split(",");
//        HttpHost[] hosts = Arrays.stream(clustersSplit)
//            .map(this::makeHttpHost)
//            .filter(Objects::nonNull)
//            .toArray(HttpHost[]::new);
//
//        RestClientBuilder restClientBuilder = RestClient.builder(hosts);
//        restClientBuilder.setRequestConfigCallback(builder -> {
//            builder.setConnectTimeout(5000);
//            builder.setSocketTimeout(60000);
//            builder.setConnectionRequestTimeout(1000);
//            return builder;
//        }).setMaxRetryTimeoutMillis(2 * 60 * 1000);
//        if (StringUtils.isNotBlank(esUsername) && StringUtils.isNotBlank(esPassword)) {
//            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//            credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(esUsername, esPassword));
//            restClientBuilder
//                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
//        }
//
//        return new RestHighLevelClient(restClientBuilder);
//    }
//
//    private HttpHost makeHttpHost(String s) {
//        assert StringUtils.isNotEmpty(s);
//        String[] address = s.split(":");
//        if (address.length == 2) {
//            String ip = address[0];
//            return new HttpHost(ip, Integer.parseInt(address[1]), "http");
//        } else {
//            return new HttpHost(address[0], 9200, "http");
//        }
//    }
//}
//
