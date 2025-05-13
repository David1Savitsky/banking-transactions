package com.savitsky.bankingtransactions.service.elastic;

import com.savitsky.bankingtransactions.mapper.UserMapper;
import com.savitsky.bankingtransactions.model.User;
import com.savitsky.bankingtransactions.repository.elastic.UserElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserElasticService {

    private final UserElasticsearchRepository userElasticsearchRepository;

    public void updateUserDocument(User user) {
        var document = UserMapper.mapUserToUserDocument(user);
        userElasticsearchRepository.save(document);
    }
}
