package oit.is.z2974.kaizi.janken.model;

public class Janken {
  private String userName;
  private String myHand; // あなたの手
  private String cpuHand; // 相手の手
  private String result; // 結果

  // GetterとSetterを追加
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getMyHand() {
    return myHand;
  }

  public void setMyHand(String myHand) {
    this.myHand = myHand;
  }

  public String getCpuHand() {
    return cpuHand;
  }

  public void setCpuHand(String cpuHand) {
    this.cpuHand = cpuHand;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
