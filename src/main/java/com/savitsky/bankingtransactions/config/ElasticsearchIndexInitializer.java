package com.savitsky.bankingtransactions.config;

import com.savitsky.bankingtransactions.model.elastic.UserDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void init() {
        Class<?> clazz = UserDocument.class;

        if (!elasticsearchOperations.indexOps(clazz).exists()) {
            elasticsearchOperations.indexOps(clazz).create();
            elasticsearchOperations.indexOps(clazz).putMapping(
                    elasticsearchOperations.indexOps(clazz).createMapping(clazz)
            );
        }
    }
}
