package com.savitsky.bankingtransactions.repository.elastic;

import com.savitsky.bankingtransactions.model.elastic.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserElasticsearchRepository extends ElasticsearchRepository<UserDocument, Long> {
}
