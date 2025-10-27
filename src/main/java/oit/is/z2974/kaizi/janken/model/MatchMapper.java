package oit.is.z2974.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchMapper {
  @Select("SELECT * from matches")
  ArrayList<Match> selectAllMatches();

  @Insert("INSERT INTO matches (user1, user2, user1Hand, user2Hand) VALUES (#{user1}, #{user2}, #{user1Hand}, #{user2Hand});")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertMatch(Match match);
}
