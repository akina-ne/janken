package oit.is.z2974.kaizi.janken.model;

public class Janken {
  private String myHand; // あなたの手
  private String cpuHand = "Gu"; // 相手の手
  private String result; // 結果

  // GetterとSetterを追加

  public Janken(String myHand) {
    this.myHand = myHand;
    this.judge();
  }

  // 勝敗判定ロジック
  private void judge() {
    if (myHand.equals(cpuHand)) {
      this.result = "Draw";
    } else if ((myHand.equals("Gu") && cpuHand.equals("Choki")) ||
        (myHand.equals("Choki") && cpuHand.equals("Pa")) ||
        (myHand.equals("Pa") && cpuHand.equals("Gu"))) {
      this.result = "You Win!";
    } else {
      this.result = "You Lose...";
    }
  }

  // Getter
  public String getCpuHand() {
    return this.cpuHand;
  }

  public String getresult() {
    return this.result;
  }
}
