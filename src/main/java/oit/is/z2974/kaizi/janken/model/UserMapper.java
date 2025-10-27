package oit.is.z2974.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Select("SELECT * from users")
  ArrayList<User> selectAllUsers();

  @Select("SELECT * FROM users WHERE id = #{id}")
  User selectById(int id); // ← このメソッドを追加

  @Select("SELECT * FROM users WHERE name = #{name}")
  User selectByName(String name);
}
