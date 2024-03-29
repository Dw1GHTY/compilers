class Yytoken {
	public int m_index;
	public String m_text;
	public int m_line;
	public int m_column;
	public long m_charBegin;

	Yytoken(int index, String text, int line, int charBegin) {
		m_index = index;
		m_text = text;
		m_line = line;
		m_charBegin = charBegin;
	}

	Yytoken(int index, String text, int line, int column, long charBegin)
    {
        this.m_index = index;
        this.m_text = text;
        this.m_line = line;
        this.m_column = column;
        this.m_charBegin = charBegin;
    }
	
	public String toString() {
		return "Text : " + m_text 
				+ "\nindex : " + m_index 
				+ "\nline : " + m_line 
				+ "\ncBeg. : " + m_charBegin;
	}
}