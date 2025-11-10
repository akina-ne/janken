package oit.is.z2974.kaizi.janken.service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.z2974.kaizi.janken.model.Match;
import oit.is.z2974.kaizi.janken.model.MatchMapper;

@Service
public class AsyncKekka {

  @Autowired
  MatchMapper matchMapper;

  @Async
  public void asyncShowKekka(SseEmitter emitter, int userId) {
    try {
      while (true) {
        ArrayList<Match> activeMatches = matchMapper.selectActiveMatchesByUserId(userId);
        if (!activeMatches.isEmpty()) {
          emitter.send(activeMatches);
          // updateMatchToInactive の処理をここから削除！
          break;
        }
        TimeUnit.MILLISECONDS.sleep(500);
      }
    } catch (Exception e) {
      emitter.completeWithError(e);
    } finally {
      emitter.complete();
    }
  }

  /**
   * 指定された試合を、少し待ってから非アクティブ化する非同期メソッド
   *
   * @param matchId 非アクティブ化する試合のID
   */
  @Async
  public void deactivateMatchAfterDelay(int matchId) {
    try {
      // 2秒待つ (両方のプレイヤーが結果を取得するのに十分な時間を与える)
      TimeUnit.SECONDS.sleep(2);
      matchMapper.updateMatchToInactive(matchId);
    } catch (InterruptedException e) {
      // エラー処理
      System.err.println("Deactivation delay interrupted: " + e.getMessage());
    }
  }
}
