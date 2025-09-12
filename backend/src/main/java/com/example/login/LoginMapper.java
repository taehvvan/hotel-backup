package com.example.login;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.example.register.UserEntity;

@Mapper
public interface LoginMapper {

    @Select("SELECT u_id, name, email, password, phone, birth, social, role FROM users WHERE email = #{email}")
    UserEntity findByEmail(@Param("email") String email);

}
