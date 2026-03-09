package com.socialplatform.core.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.socialplatform.core.application.dto.response.AdminActionResponse;
import com.socialplatform.core.domain.model.AdminAction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminActionMapper {

    @Mapping(source = "adminActionType", target = "actionType")
    AdminActionResponse toResponse(AdminAction entity);
}
