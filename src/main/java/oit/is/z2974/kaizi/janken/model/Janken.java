package oit.is.z2974.kaizi.janken.model;

public class Janken {
  private String myHand; // あなたの手
  private String opponentHand; // 相手の手
  private String result; // 結果

  // GetterとSetterを追加

  public Janken(String myHand) {
    this.myHand = myHand;
    this.opponentHand = "Gu"; // CPUの手は固定
    this.judge();
  }

  public Janken(String user1Hand, String user2Hand) {
    this.myHand = user1Hand;
    this.opponentHand = user2Hand; // 2つ目の文字列を相手の手にセット
    this.judge(); // 勝敗判定ロジックはそのまま使える
  }

  // 勝敗判定ロジック
  private void judge() {
    if (myHand.equals(this.opponentHand)) {
      this.result = "Draw";
    } else if ((myHand.equals("Gu") && this.opponentHand.equals("Choki")) ||
        (myHand.equals("Choki") && this.opponentHand.equals("Pa")) ||
        (myHand.equals("Pa") && this.opponentHand.equals("Gu"))) {
      this.result = "You Win!";
    } else {
      this.result = "You Lose...";
    }
  }

  // Getterメソッド
  public String getOpponentHand() {
    return this.opponentHand;
  }

  public String getresult() {
    return this.result;
  }
}
