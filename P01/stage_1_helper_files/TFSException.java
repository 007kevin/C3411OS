import java.io.*;

class TFSException extends IOException {
  public TFSException(String msg){
    super("TFSException: " + msg);
  }
}
