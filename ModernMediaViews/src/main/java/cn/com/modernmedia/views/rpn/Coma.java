package cn.com.modernmedia.views.rpn;

class Coma extends Token {
	public Coma(String value) {
		super("coma", value);
	}

	static TokenMatcher getMatcher() {
		return new TokenMatcher("Coma", "\\,");
	}
}