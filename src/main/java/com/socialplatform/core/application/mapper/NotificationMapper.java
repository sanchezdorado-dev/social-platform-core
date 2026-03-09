package com.socialplatform.core.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.socialplatform.core.application.dto.response.NotificationResponse;
import com.socialplatform.core.domain.model.Notification;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        UserMapper.class })
public interface NotificationMapper {

    @Mapping(target = "actor", source = "actorUser")
    @Mapping(target = "isRead", source = "read")
    NotificationResponse toResponse(Notification notification);
}
