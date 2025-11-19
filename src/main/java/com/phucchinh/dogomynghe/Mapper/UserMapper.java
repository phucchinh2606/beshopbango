package com.phucchinh.dogomynghe.Mapper;

import com.phucchinh.dogomynghe.dto.response.UserResponse;
import com.phucchinh.dogomynghe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper extends EntityMapper<User, UserResponse>{
}
