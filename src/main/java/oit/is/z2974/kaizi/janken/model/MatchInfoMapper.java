package oit.is.z2974.kaizi.janken.model;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MatchInfoMapper {

  @Insert("INSERT INTO matchinfo (user1, user2, user1Hand, isActive) VALUES (#{user1}, #{user2}, #{user1Hand}, #{isActive})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertMatchInfo(MatchInfo matchInfo);

  @Select("SELECT * FROM matchinfo WHERE isActive = true")
  ArrayList<MatchInfo> selectActiveMatches();

  /**
   * user1とuser2を指定して、アクティブな試合を取得する
   * (相手がすでに待っているかを確認するために使用)
   *
   * @param user1 ユーザ1のID
   * @param user2 ユーザ2のID
   * @return MatchInfoオブジェクト
   */
  @Select("SELECT * FROM matchinfo WHERE user1 = #{user1} AND user2 = #{user2} AND isActive = true")
  MatchInfo selectActiveMatchByUsers(int user1, int user2);

  /**
   * 試合が終わったので、該当レコードを非アクティブ化する
   *
   * @param id 更新するmatchinfoのID
   */
  @Update("UPDATE matchinfo SET isActive = false WHERE id = #{id}")
  void updateMatchInfoToInactive(int id);
}
