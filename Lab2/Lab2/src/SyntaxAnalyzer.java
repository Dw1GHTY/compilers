import java.io.IOException;
import java.util.*;

public class SyntaxAnalyzer {
	public final static int POP = -3;
	public final static int ACC = -2;
	public final static int ERR = -1;
	HashMap<String, Integer> rowDict = new HashMap<>();
	HashMap<String, Integer> colDict = new HashMap<>();

	int[][] syntaxTable = new int[20][13];
	String[] productionRules = new String[11];

	public SyntaxAnalyzer() {
		rowDict.put("R", 0);
		rowDict.put("E", 1);
		rowDict.put("E'", 2);
		rowDict.put("A", 3);
		rowDict.put("A'", 4);
		rowDict.put("T", 5);
		rowDict.put("S", 6);
		rowDict.put("loop", 7);
		rowDict.put("(", 8);
		rowDict.put(")", 9);
		rowDict.put("{", 10);
		rowDict.put("}", 11);
		rowDict.put("redo", 12);
		rowDict.put(";", 13);
		rowDict.put("||", 14);
		rowDict.put("&&", 15);
		rowDict.put("ID", 16);
		rowDict.put("CONST", 17);
		rowDict.put("=", 18);
		rowDict.put("#", 19);

		colDict.put("loop", 0);
		colDict.put("(", 1);
		colDict.put(")", 2);
		colDict.put("{", 3);
		colDict.put("}", 4);
		colDict.put("redo", 5);
		colDict.put(";", 6);
		colDict.put("||", 7);
		colDict.put("&&", 8);
		colDict.put("ID", 9);
		colDict.put("CONST", 10);
		colDict.put("=", 11);
		colDict.put("#", 12);

		productionRules[0] = "loop ( E ) { S redo ( E ) ; S }";
		productionRules[1] = "A E'";
		productionRules[2] = "|| A E'";
		productionRules[3] = "";// epsilon
		productionRules[4] = "T A'";
		productionRules[5] = "&& T A'";
		productionRules[6] = "";// epsilon
		productionRules[7] = "ID";
		productionRules[8] = "CONST";
		productionRules[9] = "R";
		productionRules[10] = "ID = E ;";

		for (int i = 0; i < 20; i++)
			for (int j = 0; j < 13; j++)
				syntaxTable[i][j] = ERR;
		for (int i = 7; i < 20; i++)
			syntaxTable[i][i - 7] = POP;

		syntaxTable[19][12] = ACC;

		syntaxTable[0][0] = 0;
		syntaxTable[1][9] = 1;
		syntaxTable[1][10] = 1;
		syntaxTable[2][2] = 3;
		syntaxTable[2][6] = 3;
		syntaxTable[2][7] = 2;
		syntaxTable[3][9] = 4;
		syntaxTable[3][10] = 4;
		syntaxTable[4][2] = 6;
		syntaxTable[4][6] = 6;
		syntaxTable[4][7] = 6;
		syntaxTable[4][8] = 5;
		syntaxTable[5][9] = 7;
		syntaxTable[5][10] = 8;
		syntaxTable[6][0] = 9;
		syntaxTable[6][9] = 10;

	}

	public int M(String top, Yytoken next) {
		if (next.m_index == sym.ID) // ID
		{
			if (top.equals("ID"))
				return POP;

			int rule = syntaxTable[rowDict.get(top)][colDict.get("ID")];
			System.out.println("Line " + next.m_line + ": Primenjujem smenu " + top + " -> " + productionRules[rule]
					+ " nad simbolom " + next.m_text);
			return rule;
		}
		if (next.m_index == sym.CONST) // CONST
		{
			if (top.equals("CONST"))
				return POP;

			int rule = syntaxTable[rowDict.get(top)][colDict.get("CONST")];
			System.out.println("Line " + next.m_line + ": Primenjujem smenu " + top + " -> " + productionRules[rule]
					+ " nad simbolom " + next.m_text);
			return rule;
		}
		if (top.equals("#") && next.m_text == null) // null jer u spec.flex vraca null za EOF
		{
			return ACC;
		}
		if (top.equals(next.m_text)) {
			System.out.println("Line " + next.m_line + ": Izbacujem sa steka " + next.m_text);
			return POP;
		}
		if (colDict.containsKey(next.m_text) && rowDict.containsKey(top)) {
			int rule = syntaxTable[rowDict.get(top)][colDict.get(next.m_text)];
			System.out.println("Line " + next.m_line + ": "
					+ (rule != ERR && rule != ACC && rule != POP
							? "Primenjujem smenu: " + top + " -> " + (productionRules[rule].equals("") ? "epsilon" : productionRules[rule])
							: "")
					+ " nad simbolom " + next.m_text);
			return rule;
		}

		return ERR;
	}

	public boolean SA_LL1(MPLexer lexer) {
		Stack<String> stack = new Stack<>();
		stack.push("#");
		stack.push("R");
		boolean detected = false;
		boolean error = false;
		int s;
		try {
			Yytoken next = lexer.next_token();
			do {
				s = M(stack.peek(), next);
				switch (s) {
				case POP:
					stack.pop();
					next = lexer.next_token();
					break;
				case ACC:
					detected = true;
					break;
				case ERR:
					error = true;
					System.out.println("Greska u liniji " + (next.m_line + 1) + ", simbol: " + next.m_text);
					break;
				default:
					stack.pop();
					String[] niz = productionRules[s].split(" ");
					for (int i = niz.length - 1; i >= 0; i--)
						if (!niz[i].equals(""))
							stack.push(niz[i]);
					break;
				}

			} while (!(detected || error));

			return detected;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return detected;

	}

	public static void main(String[] argv) {

		MPLexer scanner = null;
		try {
			java.io.FileInputStream stream = new java.io.FileInputStream(
					"C:\\Eclipse Workspace\\Lab2PP18026\\src\\testinput.txt");
			java.io.Reader reader = new java.io.InputStreamReader(stream, "UTF-8");
			scanner = new MPLexer(reader);
			SyntaxAnalyzer sa = new SyntaxAnalyzer();
			boolean correct = sa.SA_LL1(scanner);
			if (correct) {
				System.out.println("Analiza zavrsena - nema gresaka");
			}

		} catch (Exception e) {
			System.out.println("Unexpected exception:");
			e.printStackTrace();
		}

	}
}
