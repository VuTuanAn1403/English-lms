package com.englishlms.ai.mapper;

import com.englishlms.ai.dto.ChatHistoryResponse;
import com.englishlms.ai.entity.ChatHistory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AiMapper {

    ChatHistoryResponse toChatHistoryResponse(ChatHistory chatHistory);

    List<ChatHistoryResponse> toChatHistoryResponseList(List<ChatHistory> chatHistoryList);
}
