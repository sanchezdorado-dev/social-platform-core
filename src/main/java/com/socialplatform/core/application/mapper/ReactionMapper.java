package com.socialplatform.core.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.socialplatform.core.application.dto.request.ReactionRequest;
import com.socialplatform.core.application.dto.response.ReactionResponse;
import com.socialplatform.core.domain.model.Reaction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        UserMapper.class })
public interface ReactionMapper {

    @Mapping(target = "postId", source = "post.id")
    ReactionResponse toResponse(Reaction reaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Reaction toEntity(ReactionRequest request);
}
