package cn.com.modernmedia.views.rpn;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import android.util.DisplayMetrics;
import cn.com.modernmedia.views.ViewsApplication;

public class Calc {
	private Lexer lexer;
	private CalcStack stack;
	private Queue<Token> rpnNotation;

	public Calc(String expression, DisplayMetrics metrics) {
		try {
			float density = metrics == null ? 2 : metrics.density;
			expression = expression.replaceAll(" ", "");
			expression = expression.replaceAll("dp", "*" + density);
			expression = expression.replaceAll("width", ViewsApplication.width
					+ "");
			expression = expression.replaceAll("height",
					ViewsApplication.height + "");
			lexer = new Lexer(expression);
			stack = new CalcStack();
			rpnNotation = new LinkedList<Token>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int evaluate() {
		try {
			convertToRPN();
			int result = (int) process();
			return Math.max(result, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private double process() throws Exception {
		CalcStack tempStack = new CalcStack();
		Token token = null;
		while (!rpnNotation.isEmpty()) {
			token = rpnNotation.poll();
			// while((Token token = rpnNotation.poll()) != null) {
			if (token instanceof Number) {
				tempStack.push(token);
			} else if (token instanceof Operator) { // FIXIT !!
				Number[] arg = tempStack.pop(((Operator) token).numOfArgs());
				tempStack.push(((Operator) token).execute(arg));
			} else if (token instanceof Function) { // FIXIT !!
				Number[] arg = tempStack.pop(((Function) token).numOfArgs());
				tempStack.push(((Function) token).execute(arg));
			}
		}
		return ((Number) tempStack.pop()).getValue();
	}

	private void convertToRPN() throws Exception {
		lexer.tokenize();
		Iterator<Token> iterator = lexer.getIterator();
		while (iterator.hasNext()) {
			Token token = (Token) iterator.next();
			if (token instanceof Number) {
				rpnNotation.add(token);
			} else if (token instanceof Function) {
				stack.push(token);
			} else if (token instanceof Coma) {
				while (!(stack.peek() instanceof L_Brace)) {
					rpnNotation.add((Token) stack.pop());
				}
				if (!(stack.peek() instanceof L_Brace)) {
				}
			} else if (token instanceof Operator) {
				Operator t = (Operator) token;
				Object stackTop = stack.peek();
				if (stackTop != null && stackTop instanceof Operator) {
					int priority = ((Operator) stackTop).priority();
					boolean test1 = (t.associativity() == "both" || t
							.associativity() == "left")
							&& (t.priority() <= priority);
					boolean test2 = (t.associativity() == "right")
							&& (t.priority() < priority);
					if (test1 || test2) {
						rpnNotation.add((Token) stack.pop());
					}
				}
				stack.push(token);
			} else if (token instanceof L_Brace) {
				stack.push(token);
			} else if (token instanceof R_Brace) {
				boolean leftBracketExists = false;
				Object operator;
				while (!stack.empty()) {
					operator = stack.pop();
					if (operator instanceof L_Brace) {
						leftBracketExists = true;
						break;
					} else {
						rpnNotation.add((Token) operator);
					}
				}
				if (stack.peek() instanceof Function) {
					rpnNotation.add((Token) stack.pop());
				}
				if (stack.empty() && !leftBracketExists) {
				}
			}
		}
		Object operator;
		while (!stack.empty()) {
			operator = stack.pop();
			if (operator instanceof Brace) {
			}
			rpnNotation.add((Token) operator);
		}
	}
}