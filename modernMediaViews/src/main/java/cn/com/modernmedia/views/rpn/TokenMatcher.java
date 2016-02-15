package cn.com.modernmedia.views.rpn;

import java.util.regex.Pattern;

public class TokenMatcher {
	private String regexp;
	private String klass;

	public TokenMatcher(String klass, String regexp) {
		this.klass = klass;
		this.regexp = regexp;
	}

	public Pattern getRegexp() {
		return Pattern.compile("^(" + regexp + ")", Pattern.CASE_INSENSITIVE);
	}

	public String getKlass() {
		return klass;
	}
}