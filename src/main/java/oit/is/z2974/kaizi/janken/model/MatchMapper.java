package oit.is.z2974.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchMapper {
  @Select("SELECT * from matches")
  ArrayList<Match> selectAllMatches();

  @Insert("INSERT INTO matches (user1, user2, user1Hand, user2Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{user2Hand}, #{isActive});")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertMatch(Match match);

  /**
   * 特定のユーザIDが関わるアクティブな試合を取得する
   *
   * @param userId ユーザID
   * @return Matchのリスト
   */
  @Select("SELECT * FROM matches WHERE (user1 = #{userId} OR user2 = #{userId}) AND isActive = true")
  ArrayList<Match> selectActiveMatchesByUserId(int userId);

  /**
   * 試合結果を表示したので、該当試合を非アクティブ化する
   *
   * @param id 更新するmatchesのID
   */
  @Update("UPDATE matches SET isActive = false WHERE id = #{id}")
  void updateMatchToInactive(int id);
}
