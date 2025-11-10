package oit.is.z2974.kaizi.janken.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import oit.is.z2974.kaizi.janken.model.Entry;
import oit.is.z2974.kaizi.janken.model.Janken;
import oit.is.z2974.kaizi.janken.model.UserMapper;
import oit.is.z2974.kaizi.janken.service.AsyncKekka;
import oit.is.z2974.kaizi.janken.model.User;
import oit.is.z2974.kaizi.janken.model.MatchMapper;
import oit.is.z2974.kaizi.janken.model.Match;
import oit.is.z2974.kaizi.janken.model.MatchInfo;
import oit.is.z2974.kaizi.janken.model.MatchInfoMapper;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class JankenController {

  @Autowired
  private Entry entry;

  @Autowired
  UserMapper userMapper;

  @Autowired
  MatchMapper matchMapper;

  @Autowired
  MatchInfoMapper matchInfoMapper;

  @Autowired
  AsyncKekka asyncKekka;

  @GetMapping("/janken")
  public String jankenPage(Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    this.entry.addUser(loginUser);
    model.addAttribute("username", loginUser);
    model.addAttribute("allUsers", this.entry.getUsers());
    ArrayList<User> users = this.userMapper.selectAllUsers();
    model.addAttribute("users", users);
    ArrayList<Match> matches = matchMapper.selectAllMatches();
    model.addAttribute("matches", matches);
    ArrayList<MatchInfo> activeMatches = matchInfoMapper.selectActiveMatches();
    model.addAttribute("activeMatches", activeMatches);

    return "janken.html";
  }

  @GetMapping("/match")
  public String match(@RequestParam int id, Principal prin, ModelMap model) {
    // ログインユーザの情報を取得
    String loginUserName = prin.getName();
    // 対戦相手のユーザ情報をIDで取得
    User opponent = userMapper.selectById(id);

    // Modelにテンプレートへ渡す情報を追加
    model.addAttribute("loginUserName", loginUserName);
    model.addAttribute("opponent", opponent);

    return "match.html";
  }

  @GetMapping("/fight")
  public String fight(@RequestParam int opponentId, @RequestParam String myHand, Principal prin, ModelMap model) {
    // 1. 自分のユーザ情報をDBから取得 (ID取得のため)
    String loginUserName = prin.getName();
    User myUser = userMapper.selectByName(loginUserName);

    // 2. 対戦相手のユーザ情報をIDで取得
    User opponent = userMapper.selectById(opponentId);

    // 3. じゃんけんロジックを実行
    Janken janken = new Janken(myHand);

    // 4. Matchオブジェクトを作成してDBにINSERT
    Match match = new Match();
    match.setUser1(myUser.getId());
    match.setUser2(opponent.getId());
    match.setUser1Hand(myHand); // user1(自分)の手
    match.setUser2Hand(janken.getOpponentHand()); // user2(相手)の手
    matchMapper.insertMatch(match);

    // 5. Modelに結果を詰めて画面遷移
    model.addAttribute("loginUserName", loginUserName);
    model.addAttribute("opponent", opponent);
    model.addAttribute("myHand", myHand);
    model.addAttribute("opponentHand", janken.getOpponentHand());
    model.addAttribute("result", janken.getresult());

    return "match.html";
  }

  @GetMapping("/wait")
  public String wait(@RequestParam int opponentId, @RequestParam String myHand, Principal prin, ModelMap model) {
    String loginUserName = prin.getName();
    User myUser = userMapper.selectByName(loginUserName);
    int myId = myUser.getId();

    // 相手が自分を待っているかDBで確認
    MatchInfo opponentMatchInfo = matchInfoMapper.selectActiveMatchByUsers(opponentId, myId);

    if (opponentMatchInfo == null) {
      // 相手は待っていない -> 自分が先に待つ
      MatchInfo myMatchInfo = new MatchInfo();
      myMatchInfo.setUser1(myId);
      myMatchInfo.setUser2(opponentId);
      myMatchInfo.setUser1Hand(myHand);
      myMatchInfo.setIsActive(true);
      matchInfoMapper.insertMatchInfo(myMatchInfo);
    } else {
      // 相手が待っていた -> 試合成立！
      Match match = new Match();
      match.setUser1(opponentMatchInfo.getUser1());
      match.setUser2(myId);
      match.setUser1Hand(opponentMatchInfo.getUser1Hand());
      match.setUser2Hand(myHand);
      match.setIsActive(true);
      matchMapper.insertMatch(match); // この時点でmatch.idがセットされる

      // ▼▼▼ この一行を追加 ▼▼▼
      // 試合が成立したので、2秒後にこの試合を非アクティブ化するよう予約する
      asyncKekka.deactivateMatchAfterDelay(match.getId());

      matchInfoMapper.updateMatchInfoToInactive(opponentMatchInfo.getId());
    }

    model.addAttribute("username", loginUserName);
    return "wait.html";
  }

  /**
   * 非同期で試合結果を返す
   */
  @GetMapping("/kekka")
  public SseEmitter kekka(Principal prin) {
    User myUser = userMapper.selectByName(prin.getName());
    final SseEmitter emitter = new SseEmitter();
    this.asyncKekka.asyncShowKekka(emitter, myUser.getId());
    return emitter;
  }

}
