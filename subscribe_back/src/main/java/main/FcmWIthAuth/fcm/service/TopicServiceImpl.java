package main.FcmWIthAuth.fcm.service;

import jakarta.transaction.Transactional;
import main.FcmWIthAuth.apiPayload.code.status.ErrorStatus;
import main.FcmWIthAuth.apiPayload.exception.GeneralException;
import main.FcmWIthAuth.fcm.converter.TopicConverter;
import main.FcmWIthAuth.fcm.dto.TopicResponseDto;
import main.FcmWIthAuth.fcm.entity.Topic;
import main.FcmWIthAuth.fcm.repository.TopicRepository;
import main.FcmWIthAuth.page.PostPagingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    @Transactional
    public void createTopic(String topicName) {
        if (topicRepository.existsByTopicName(topicName)) {
            throw new GeneralException(ErrorStatus._ALREADY_EXIST_TOPIC);
        }

        Topic topic = Topic.builder()
                .topicName(topicName)
                .build();

        topicRepository.save(topic);
    }

    @Override
    public TopicResponseDto.SearchTopicDto getTopics(PostPagingDto.PagingDto pagingDto) {

        Sort sort = Sort.by(Sort.Direction.fromString(pagingDto.getSort()),"id");
        Pageable pageable = PageRequest.of(pagingDto.getPage(), pagingDto.getSize(), sort);

        Page<Topic> topics = topicRepository.findAll(pageable);

        return TopicConverter.toSearchDto(topics);
    }
}
