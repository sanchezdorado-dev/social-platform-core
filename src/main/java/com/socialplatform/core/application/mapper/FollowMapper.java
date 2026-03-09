package com.socialplatform.core.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.socialplatform.core.application.dto.response.FollowResponse;
import com.socialplatform.core.domain.model.Follow;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        UserMapper.class })
public interface FollowMapper {

    @Mapping(target = "user", source = "follower")
    FollowResponse toFollowerResponse(Follow follow);

    @Mapping(target = "user", source = "followed")
    FollowResponse toFollowingResponse(Follow follow);
}
