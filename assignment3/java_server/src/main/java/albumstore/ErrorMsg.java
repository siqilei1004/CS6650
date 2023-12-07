package albumstore;

public class ErrorMsg {
  private String msg;

  public ErrorMsg() {
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "ErrorMsg{" +
        "msg='" + msg + '\'' +
        '}';
  }
}
