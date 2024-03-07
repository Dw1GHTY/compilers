// import sekcija

%%

// sekcija opcija i deklaracija
%class MPLexer
%function next_token
%line
%column
%char
%debug
%type Yytoken

%eofval{
return new Yytoken( sym.EOF, null, yyline, yycolumn, yychar);
%eofval}

%{
//dodatni clanovi generisane klase
KWTable kwTable = new KWTable();
Yytoken getKW()
{
	return new Yytoken( kwTable.find( yytext() ),
	yytext(), yyline, yycolumn, yychar );
}
%}

//stanja
%xstate COMMENT

//makroi
slovo = [a-zA-Z]
cifra = [0-9]
//octa_cifra=[0-7]
//hexa_cifra=[0-9A-F]
niz_cifara = (0|[1-9]{cifra}*)
//znak = ({cifra}|{slovo})
znak='[^]'

%%

// pravila
"%" { yybegin( COMMENT ); }
<COMMENT>~("%") { yybegin( YYINITIAL ); }

[\t\n\r ] { ; }
\( { return new Yytoken( sym.OPEN_BRACKET, yytext(), yyline, yycolumn, yychar ); }
\) { return new Yytoken( sym.CLOSE_BRACKET, yytext(), yyline, yycolumn, yychar ); }
\{ { return new Yytoken( sym.OPEN_CURLY_BRACKET, yytext(), yyline, yycolumn, yychar ); }
\} { return new Yytoken( sym.CLOSE_CURLY_BRACKET, yytext(), yyline, yycolumn, yychar ); }

//operatori
"<" { return new Yytoken( sym.LESS, yytext(), yyline, yycolumn, yychar ); }
"<=" { return new Yytoken( sym.LESSOREQUAL, yytext(), yyline, yycolumn, yychar ); }
"==" { return new Yytoken( sym.EQUAL, yytext(), yyline, yycolumn, yychar ); }
"!=" { return new Yytoken( sym.NOTEQUAL, yytext(), yyline, yycolumn, yychar ); }
">" { return new Yytoken( sym.GREATER, yytext(), yyline, yycolumn, yychar ); }
">=" { return new Yytoken( sym.GREATEROREQUAL, yytext(), yyline, yycolumn, yychar ); }
"&&" { return new Yytoken( sym.AND, yytext(), yyline, yycolumn, yychar ); }
"||" { return new Yytoken( sym.OR, yytext(), yyline, yycolumn, yychar ); }
"=" { return new Yytoken( sym.ASSIGNMENT, yytext(), yyline, yycolumn, yychar ); }

//separatori
; { return new Yytoken( sym.SEMICOLON, yytext(), yyline, yycolumn ); }
, { return new Yytoken( sym.COMMA, yytext(), yyline, yycolumn ); }


//kljucne reci
{slovo}+ { return getKW(); }

// int
0#o[0-7]+|0#x[0-9a-fA-F]+|0#d{cifra}+|{cifra}+  { return new Yytoken( sym.CONST, yytext(), yyline, yycolumn, yychar ); }

// bool
true|false  { return new Yytoken( sym.CONST, yytext(), yyline, yycolumn, yychar ); }

// char
 {znak}  { return new Yytoken( sym.CONST, yytext(), yyline, yycolumn, yychar ); }

//float
0\.{cifra}*(E[+-]?{cifra}+)?  { return new Yytoken( sym.CONST, yytext(), yyline, yycolumn, yychar ); }

//identifikatori
({slovo}|_)({slovo}|{cifra}|_)* { return new Yytoken(sym.ID, yytext(),yyline, yycolumn, yychar ); }

//obrada gresaka
. { if (yytext() != null && yytext().length() > 0) System.out.println( "ERROR: " + yytext() ); }
