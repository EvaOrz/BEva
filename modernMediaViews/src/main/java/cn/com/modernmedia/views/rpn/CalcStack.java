package cn.com.modernmedia.views.rpn;

import java.util.ArrayList;

class CalcStack {
	private ArrayList<Token> buffer;

	public CalcStack() {
		buffer = new ArrayList<Token>();
	}

	public void push(Token t) {
		buffer.add(t);
	}

	public Token pop() {
		int last = buffer.size() - 1;
		Token t = buffer.get(last);
		buffer.remove(last);
		return t;
	}

	// Fixit !!!!!!
	public Number[] pop(int toPop) throws Exception {
		Number[] ret = new Number[toPop];
		if (toPop > buffer.size()) {
			return ret;
		}
		while ((toPop--) > 0) {
			ret[toPop] = (Number) pop();
		}
		return ret;
	}

	public Token peek() {
		int size = buffer.size();
		if (size == 0) {
			return null;
		} else {
			return buffer.get(size - 1);
		}
	}

	public boolean empty() {
		return buffer.isEmpty();
	}

	public String toString() {
		return buffer.toString();
	}
}