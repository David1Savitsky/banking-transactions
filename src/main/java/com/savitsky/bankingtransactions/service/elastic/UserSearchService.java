package com.savitsky.bankingtransactions.service.elastic;

import com.savitsky.bankingtransactions.model.elastic.UserDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<UserDocument> search(String name, String phone, String email, LocalDate dob, Pageable pageable) {
        Criteria criteria = new Criteria();

        if (name != null && !name.isBlank()) {
            criteria = criteria.and("name").startsWith(name.toLowerCase());
        }
        if (phone != null && !phone.isBlank()) {
            criteria = criteria.and("phones").is(phone);
        }
        if (email != null && !email.isBlank()) {
            criteria = criteria.and("emails").is(email);
        }
        if (dob != null) {
            criteria = criteria.and("dateOfBirth").greaterThan(dob);
        }

        CriteriaQuery query = new CriteriaQuery(criteria, pageable);
        SearchHits<UserDocument> hits = elasticsearchOperations.search(query, UserDocument.class);

        List<UserDocument> documents = hits.stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(documents, pageable, hits.getTotalHits());
    }
}
