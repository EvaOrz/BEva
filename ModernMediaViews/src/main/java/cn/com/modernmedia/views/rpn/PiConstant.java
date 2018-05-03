package cn.com.modernmedia.views.rpn;

public class PiConstant extends Constant {
  public PiConstant(String value) {
    super(Math.PI);
  }
  
  static TokenMatcher getMatcher() {
    return new TokenMatcher("PiConstant", "PI");
  }
}