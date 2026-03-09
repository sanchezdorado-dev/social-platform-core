package com.socialplatform.core.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.socialplatform.core.application.dto.response.UserResponse;
import com.socialplatform.core.application.dto.response.UserSummaryResponse;
import com.socialplatform.core.domain.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        ProfileMapper.class })
public interface UserMapper {

    @Mapping(target = "roleName", source = "role.name")
    UserResponse toResponse(User user);

    @Mapping(target = "profilePictureUrl", source = "profile.profilePictureUrl")
    @Mapping(target = "occupation", source = "profile.occupation")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    UserSummaryResponse toSummaryResponse(User user);
}
