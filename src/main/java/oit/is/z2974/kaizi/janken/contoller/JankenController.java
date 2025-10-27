package oit.is.z2974.kaizi.janken.contoller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Modelをインポート
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import oit.is.z2974.kaizi.janken.model.Entry;
import oit.is.z2974.kaizi.janken.model.Janken;
import oit.is.z2974.kaizi.janken.model.UserMapper;
import oit.is.z2974.kaizi.janken.model.User;
import oit.is.z2974.kaizi.janken.model.MatchMapper;
import oit.is.z2974.kaizi.janken.model.Match;

@Controller
public class JankenController {

  @Autowired
  private Entry entry;

  @Autowired
  UserMapper userMapper;

  @Autowired
  MatchMapper matchMapper;

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
    match.setUser2Hand(janken.getCpuHand()); // user2(相手)の手
    matchMapper.insertMatch(match);

    // 5. Modelに結果を詰めて画面遷移
    model.addAttribute("loginUserName", loginUserName);
    model.addAttribute("opponent", opponent);
    model.addAttribute("myHand", myHand);
    model.addAttribute("cpuHand", janken.getCpuHand());
    model.addAttribute("result", janken.getresult());

    return "match.html";
  }

}
