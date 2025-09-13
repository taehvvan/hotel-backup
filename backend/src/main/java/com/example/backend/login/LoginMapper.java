package com.example.backend.login;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.example.backend.register.UserEntity;

@Mapper
public interface LoginMapper {

    @Select("SELECT * FROM users WHERE email = #{email}")
    UserEntity findByEmail(@Param("email") String email);

}
