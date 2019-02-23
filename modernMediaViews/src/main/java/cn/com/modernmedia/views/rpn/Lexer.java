package cn.com.modernmedia.views.rpn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;

import android.text.TextUtils;

public class Lexer {
	private String expression;
	private ArrayList<TokenMatcher> registeredTokens;
	private ArrayList<Token> tokenObjects;
	@SuppressWarnings("rawtypes")
	private Class[] proto;

	public Lexer(String exp) throws Exception {
		expression = exp.replaceAll("(?i)\\s+", "$1");

		if (TextUtils.isEmpty(expression)) {
			return;
		}

		tokenObjects = new ArrayList<Token>();

		registeredTokens = new ArrayList<TokenMatcher>();
		registeredTokens.add(Number.getMatcher());
		registeredTokens.add(L_Brace.getMatcher());
		registeredTokens.add(R_Brace.getMatcher());

		registeredTokens.add(PlusOperator.getMatcher());
		registeredTokens.add(MinusOperator.getMatcher());
		registeredTokens.add(MultiplyOperator.getMatcher());
		registeredTokens.add(DivideOperator.getMatcher());
		registeredTokens.add(PowerOperator.getMatcher());

		registeredTokens.add(SinFunction.getMatcher());
		registeredTokens.add(CosFunction.getMatcher());
		registeredTokens.add(TgFunction.getMatcher());
		registeredTokens.add(CtgFunction.getMatcher());

		registeredTokens.add(PiConstant.getMatcher());

		proto = new Class[1];
		proto[0] = String.class;
	}

	private Token tokenFactory(TokenMatcher tr, String value) throws Exception {
		Token klass = null;
		try {
			String path = this.getClass().getPackage().getName() + "."
					+ tr.getKlass();
			klass = (Token) Class.forName(path).getConstructor(proto)
					.newInstance(value);
		} catch (Exception e) {
			// FIXIT:
			e.printStackTrace();
		}

		return klass;
	}

	void tokenize() throws Exception {
		boolean isMatch = false;

		while (expression.length() > 0) {
			isMatch = false;
			for (TokenMatcher tr : registeredTokens) {
				Matcher m = tr.getRegexp().matcher(expression);
				if (!isMatch && m.find()) {
					isMatch = true;
					tokenObjects.add(tokenFactory(tr, m.group()));
					expression = expression.substring(m.group().length());
					break;
				}
			}
			if (!isMatch) {
				break;
			}
		}
	}

	public Iterator<Token> getIterator() {
		return tokenObjects.iterator();
	}
}
