package com.client9.libinjection;

import java.util.*;
import java.util.regex.*;

//@SuppressWarnings( { "rawtypes", "unchecked" } )
public class SQLParse
{
public static boolean debug = false;
public static final Set charSet1 = new HashSet();
public static final Set charSet2 = new HashSet();
public static final Map map = new HashMap( 500 );
public static final Map smap = new HashMap( 1024 * 10 );
public static final Set setOfChar = new HashSet();
public static final Set setOfOperator1 = new HashSet();
public static final Set setOfOperator2 = new HashSet();
public static final Set setOfOperator = new HashSet();
public static final Set setOf2byteOperator = new HashSet();
public static final Set unaryOperatorSet = new HashSet();
public static final Set arithmeticOperatorSet = new HashSet();
public static final Map keywordMergeMap = new HashMap();
public static final Set keywordOrFunctionSet = new HashSet();

public static final int QUOTE_NONE	= 1<<0;
public static final int QUOTE_SINGLE	= 1<<1;
public static final int QUOTE_DOUBLE	= 1<<2;
public static final int SQL_ANSI	= 1<<3;
public static final int SQL_MYSQL	= 1<<4;

public static final int LIBINJECTION_SQLI_MAX_TOKENS = 5;
public static int maxMergedKeywordLength = 0;

public static boolean initialized = false;

static
{
arithmeticOperatorSet.add( "*" );
arithmeticOperatorSet.add( "/" );
arithmeticOperatorSet.add( "-" );
arithmeticOperatorSet.add( "+" );
arithmeticOperatorSet.add( "%" );

setOfChar.add( "(" );
setOfChar.add( ")" );
setOfChar.add( "{" );
setOfChar.add( "}" );
setOfChar.add( "." );
setOfChar.add( "," );
setOfChar.add( ";" );

setOfOperator1.add( "%" );
setOfOperator1.add( "+" );
setOfOperator1.add( "^" );
setOfOperator1.add( "~" );

setOfOperator2.add( "!" );
setOfOperator2.add( "&" );
setOfOperator2.add( "*" );
setOfOperator2.add( ":" );
setOfOperator2.add( "<" );
setOfOperator2.add( "=" );
setOfOperator2.add( ">" );
setOfOperator2.add( "|" );

setOfOperator.addAll( setOfOperator1 );
setOfOperator.addAll( setOfOperator2 );

setOf2byteOperator.add("!!" );
setOf2byteOperator.add("!<" );
setOf2byteOperator.add("!=" );
setOf2byteOperator.add("!>" );
setOf2byteOperator.add("%=" );
setOf2byteOperator.add("&=" );
setOf2byteOperator.add("*=" );
setOf2byteOperator.add("+=" );
setOf2byteOperator.add("-=" );
setOf2byteOperator.add("/=" );
setOf2byteOperator.add("::" );
setOf2byteOperator.add(":=" );
setOf2byteOperator.add("<<" );
setOf2byteOperator.add("<=" );
setOf2byteOperator.add("<>" );
setOf2byteOperator.add("<@" );
setOf2byteOperator.add(">=" );
setOf2byteOperator.add(">>" );
setOf2byteOperator.add("@>" );
setOf2byteOperator.add("^=" );
setOf2byteOperator.add("|/" );
setOf2byteOperator.add("|=" );
setOf2byteOperator.add("~*" );

unaryOperatorSet.add( "-" );
unaryOperatorSet.add( "+" );
unaryOperatorSet.add( "!" );
unaryOperatorSet.add( "~" );
unaryOperatorSet.add( "!!" );
unaryOperatorSet.add( "NOT" );

//21	33	   &parse_operator2, /* 33 */		!
//26	38	   &parse_operator2, /* 38 */		&
//2A	42	   &parse_operator2, /* 42 */		*
//3A	58	   &parse_operator2, /* 58 */		:
//3C	60	   &parse_operator2, /* 60 */		<
//3D	61	   &parse_operator2, /* 61 */		=
//3E	62	   &parse_operator2, /* 62 */		>
//7C	124	   &parse_operator2, /* 124 */		|
 

/*
charSet1.addAll( Arrays.asList( new String[]{ "B","E","N","Q","U","X" } ) );

int k = 65;
for( int i = 0; i < 26; ++i )
	{
	char c = ( char )( k + i );
	String s = c + "";
	if( !charSet1.contains( s ) )
		{
		charSet2.add( s );
		}
	}
*/

initialize();
}
//--------------------------------------------------------------------------------
public static void initialize()
{
if( initialized )
	{
	return;
	}
else
	{
	initialized = true;
	}
map.put("!!", "o");
map.put("!<", "o");
map.put("!=", "o");
map.put("!>", "o");
map.put("%=", "o");
map.put("&&", "&");
map.put("&=", "o");
map.put("*=", "o");
map.put("+=", "o");
map.put("-=", "o");
map.put("/=", "o");
smap.put("&(1)O", "F");
smap.put("&(1)U", "F");
smap.put("&(1O(", "F");
smap.put("&(1OF", "F");
smap.put("&(1OS", "F");
smap.put("&(1OV", "F");
smap.put("&(F()", "F");
smap.put("&(F(1", "F");
smap.put("&(F(F", "F");
smap.put("&(F(N", "F");
smap.put("&(F(S", "F");
smap.put("&(F(V", "F");
smap.put("&(N)O", "F");
smap.put("&(N)U", "F");
smap.put("&(NO(", "F");
smap.put("&(NOF", "F");
smap.put("&(NOS", "F");
smap.put("&(NOV", "F");
smap.put("&(S)O", "F");
smap.put("&(S)U", "F");
smap.put("&(SO(", "F");
smap.put("&(SO1", "F");
smap.put("&(SOF", "F");
smap.put("&(SON", "F");
smap.put("&(SOS", "F");
smap.put("&(SOV", "F");
smap.put("&(V)O", "F");
smap.put("&(V)U", "F");
smap.put("&(VO(", "F");
smap.put("&(VOF", "F");
smap.put("&(VOS", "F");
smap.put("&1O(1", "F");
smap.put("&1O(F", "F");
smap.put("&1O(N", "F");
smap.put("&1O(S", "F");
smap.put("&1O(V", "F");
smap.put("&1OF(", "F");
smap.put("&1OS(", "F");
smap.put("&1OS1", "F");
smap.put("&1OSF", "F");
smap.put("&1OSU", "F");
smap.put("&1OSV", "F");
smap.put("&1OV(", "F");
smap.put("&1OVF", "F");
smap.put("&1OVO", "F");
smap.put("&1OVS", "F");
smap.put("&1OVU", "F");
smap.put("&1UE(", "F");
smap.put("&1UE1", "F");
smap.put("&1UEF", "F");
smap.put("&1UEK", "F");
smap.put("&1UEN", "F");
smap.put("&1UES", "F");
smap.put("&1UEV", "F");
smap.put("&F()O", "F");
smap.put("&F()U", "F");
smap.put("&F(1)", "F");
smap.put("&F(1O", "F");
smap.put("&F(F(", "F");
smap.put("&F(N)", "F");
smap.put("&F(NO", "F");
smap.put("&F(S)", "F");
smap.put("&F(SO", "F");
smap.put("&F(V)", "F");
smap.put("&F(VO", "F");
smap.put("&NO(1", "F");
smap.put("&NO(F", "F");
smap.put("&NO(N", "F");
smap.put("&NO(S", "F");
smap.put("&NO(V", "F");
smap.put("&NOF(", "F");
smap.put("&NOS(", "F");
smap.put("&NOS1", "F");
smap.put("&NOSF", "F");
smap.put("&NOSU", "F");
smap.put("&NOSV", "F");
smap.put("&NOV(", "F");
smap.put("&NOVF", "F");
smap.put("&NOVO", "F");
smap.put("&NOVS", "F");
smap.put("&NOVU", "F");
smap.put("&NUE(", "F");
smap.put("&NUE1", "F");
smap.put("&NUEF", "F");
smap.put("&NUEK", "F");
smap.put("&NUEN", "F");
smap.put("&NUES", "F");
smap.put("&NUEV", "F");
smap.put("&SO(1", "F");
smap.put("&SO(F", "F");
smap.put("&SO(N", "F");
smap.put("&SO(S", "F");
smap.put("&SO(V", "F");
smap.put("&SO1(", "F");
smap.put("&SO1F", "F");
smap.put("&SO1N", "F");
smap.put("&SO1S", "F");
smap.put("&SO1U", "F");
smap.put("&SO1V", "F");
smap.put("&SOF(", "F");
smap.put("&SON(", "F");
smap.put("&SON1", "F");
smap.put("&SONF", "F");
smap.put("&SONU", "F");
smap.put("&SOS(", "F");
smap.put("&SOS1", "F");
smap.put("&SOSF", "F");
smap.put("&SOSU", "F");
smap.put("&SOSV", "F");
smap.put("&SOV(", "F");
smap.put("&SOVF", "F");
smap.put("&SOVO", "F");
smap.put("&SOVS", "F");
smap.put("&SOVU", "F");
smap.put("&SUE(", "F");
smap.put("&SUE1", "F");
smap.put("&SUEF", "F");
smap.put("&SUEK", "F");
smap.put("&SUEN", "F");
smap.put("&SUES", "F");
smap.put("&SUEV", "F");
smap.put("&VO(1", "F");
smap.put("&VO(F", "F");
smap.put("&VO(N", "F");
smap.put("&VO(S", "F");
smap.put("&VO(V", "F");
smap.put("&VOF(", "F");
smap.put("&VOS(", "F");
smap.put("&VOS1", "F");
smap.put("&VOSF", "F");
smap.put("&VOSU", "F");
smap.put("&VOSV", "F");
smap.put("&VUE(", "F");
smap.put("&VUE1", "F");
smap.put("&VUEF", "F");
smap.put("&VUEK", "F");
smap.put("&VUEN", "F");
smap.put("&VUES", "F");
smap.put("&VUEV", "F");
smap.put(")UE(1", "F");
smap.put(")UE(F", "F");
smap.put(")UE(N", "F");
smap.put(")UE(S", "F");
smap.put(")UE(V", "F");
smap.put(")UE1K", "F");
smap.put(")UE1O", "F");
smap.put(")UEF(", "F");
smap.put(")UEK(", "F");
smap.put(")UEK1", "F");
smap.put(")UEKF", "F");
smap.put(")UEKN", "F");
smap.put(")UEKS", "F");
smap.put(")UEKV", "F");
smap.put(")UENK", "F");
smap.put(")UENO", "F");
smap.put(")UESK", "F");
smap.put(")UESO", "F");
smap.put(")UEVK", "F");
smap.put(")UEVO", "F");
smap.put("1&(1&", "F");
smap.put("1&(1)", "F");
smap.put("1&(1,", "F");
smap.put("1&(1O", "F");
smap.put("1&(E(", "F");
smap.put("1&(E1", "F");
smap.put("1&(EF", "F");
smap.put("1&(EK", "F");
smap.put("1&(EN", "F");
smap.put("1&(EO", "F");
smap.put("1&(ES", "F");
smap.put("1&(EV", "F");
smap.put("1&(F(", "F");
smap.put("1&(N&", "F");
smap.put("1&(N)", "F");
smap.put("1&(N,", "F");
smap.put("1&(NO", "F");
smap.put("1&(S&", "F");
smap.put("1&(S)", "F");
smap.put("1&(S,", "F");
smap.put("1&(SO", "F");
smap.put("1&(V&", "F");
smap.put("1&(V)", "F");
smap.put("1&(V,", "F");
smap.put("1&(VO", "F");
smap.put("1&1", "F");
smap.put("1&1&(", "F");
smap.put("1&1&1", "F");
smap.put("1&1&F", "F");
smap.put("1&1&N", "F");
smap.put("1&1&S", "F");
smap.put("1&1&V", "F");
smap.put("1&1)&", "F");
smap.put("1&1)C", "F");
smap.put("1&1)O", "F");
smap.put("1&1)U", "F");
smap.put("1&1;", "F");
smap.put("1&1;C", "F");
smap.put("1&1;E", "F");
smap.put("1&1;T", "F");
smap.put("1&1B(", "F");
smap.put("1&1B1", "F");
smap.put("1&1BF", "F");
smap.put("1&1BN", "F");
smap.put("1&1BS", "F");
smap.put("1&1BV", "F");
smap.put("1&1C", "F");
smap.put("1&1EK", "F");
smap.put("1&1EN", "F");
smap.put("1&1F(", "F");
smap.put("1&1K(", "F");
smap.put("1&1K1", "F");
smap.put("1&1KF", "F");
smap.put("1&1KN", "F");
smap.put("1&1KS", "F");
smap.put("1&1KV", "F");
smap.put("1&1O(", "F");
smap.put("1&1OF", "F");
smap.put("1&1OO", "F");
smap.put("1&1OS", "F");
smap.put("1&1OV", "F");
smap.put("1&1TN", "F");
smap.put("1&1U", "F");
smap.put("1&1U(", "F");
smap.put("1&1U;", "F");
smap.put("1&1UC", "F");
smap.put("1&1UE", "F");
smap.put("1&E(1", "F");
smap.put("1&E(F", "F");
smap.put("1&E(N", "F");
smap.put("1&E(O", "F");
smap.put("1&E(S", "F");
smap.put("1&E(V", "F");
smap.put("1&E1", "F");
smap.put("1&E1;", "F");
smap.put("1&E1C", "F");
smap.put("1&E1K", "F");
smap.put("1&E1O", "F");
smap.put("1&EF(", "F");
smap.put("1&EK(", "F");
smap.put("1&EK1", "F");
smap.put("1&EKF", "F");
smap.put("1&EKN", "F");
smap.put("1&EKS", "F");
smap.put("1&EKU", "F");
smap.put("1&EKV", "F");
smap.put("1&EN", "F");
smap.put("1&EN;", "F");
smap.put("1&ENC", "F");
smap.put("1&ENK", "F");
smap.put("1&ENO", "F");
smap.put("1&ES", "F");
smap.put("1&ES;", "F");
smap.put("1&ESC", "F");
smap.put("1&ESK", "F");
smap.put("1&ESO", "F");
smap.put("1&EUE", "F");
smap.put("1&EV", "F");
smap.put("1&EV;", "F");
smap.put("1&EVC", "F");
smap.put("1&EVK", "F");
smap.put("1&EVO", "F");
smap.put("1&F()", "F");
smap.put("1&F(1", "F");
smap.put("1&F(E", "F");
smap.put("1&F(F", "F");
smap.put("1&F(N", "F");
smap.put("1&F(S", "F");
smap.put("1&F(V", "F");
smap.put("1&K&(", "F");
smap.put("1&K&1", "F");
smap.put("1&K&F", "F");
smap.put("1&K&N", "F");
smap.put("1&K&S", "F");
smap.put("1&K&V", "F");
smap.put("1&K(1", "F");
smap.put("1&K(F", "F");
smap.put("1&K(N", "F");
smap.put("1&K(S", "F");
smap.put("1&K(V", "F");
smap.put("1&K1O", "F");
smap.put("1&KF(", "F");
smap.put("1&KNK", "F");
smap.put("1&KO(", "F");
smap.put("1&KO1", "F");
smap.put("1&KOF", "F");
smap.put("1&KOK", "F");
smap.put("1&KON", "F");
smap.put("1&KOS", "F");
smap.put("1&KOV", "F");
smap.put("1&KSO", "F");
smap.put("1&KVO", "F");
smap.put("1&N&(", "F");
smap.put("1&N&1", "F");
smap.put("1&N&F", "F");
smap.put("1&N&N", "F");
smap.put("1&N&S", "F");
smap.put("1&N&V", "F");
smap.put("1&N)&", "F");
smap.put("1&N)C", "F");
smap.put("1&N)O", "F");
smap.put("1&N)U", "F");
smap.put("1&N;", "F");
smap.put("1&N;C", "F");
smap.put("1&N;E", "F");
smap.put("1&N;T", "F");
smap.put("1&NB(", "F");
smap.put("1&NB1", "F");
smap.put("1&NBF", "F");
smap.put("1&NBN", "F");
smap.put("1&NBS", "F");
smap.put("1&NBV", "F");
smap.put("1&NC", "F");
smap.put("1&NEN", "F");
smap.put("1&NF(", "F");
smap.put("1&NK(", "F");
smap.put("1&NK1", "F");
smap.put("1&NKF", "F");
smap.put("1&NKN", "F");
smap.put("1&NKS", "F");
smap.put("1&NKV", "F");
smap.put("1&NO(", "F");
smap.put("1&NOF", "F");
smap.put("1&NOS", "F");
smap.put("1&NOV", "F");
smap.put("1&NTN", "F");
smap.put("1&NU", "F");
smap.put("1&NU(", "F");
smap.put("1&NU;", "F");
smap.put("1&NUC", "F");
smap.put("1&NUE", "F");
smap.put("1&S", "F");
smap.put("1&S&(", "F");
smap.put("1&S&1", "F");
smap.put("1&S&F", "F");
smap.put("1&S&N", "F");
smap.put("1&S&S", "F");
smap.put("1&S&V", "F");
smap.put("1&S)&", "F");
smap.put("1&S)C", "F");
smap.put("1&S)O", "F");
smap.put("1&S)U", "F");
smap.put("1&S1", "F");
smap.put("1&S1;", "F");
smap.put("1&S1C", "F");
smap.put("1&S1O", "F");
smap.put("1&S;", "F");
smap.put("1&S;C", "F");
smap.put("1&S;E", "F");
smap.put("1&S;T", "F");
smap.put("1&SB(", "F");
smap.put("1&SB1", "F");
smap.put("1&SBF", "F");
smap.put("1&SBN", "F");
smap.put("1&SBS", "F");
smap.put("1&SBV", "F");
smap.put("1&SC", "F");
smap.put("1&SEK", "F");
smap.put("1&SEN", "F");
smap.put("1&SF(", "F");
smap.put("1&SK(", "F");
smap.put("1&SK1", "F");
smap.put("1&SKF", "F");
smap.put("1&SKN", "F");
smap.put("1&SKS", "F");
smap.put("1&SKV", "F");
smap.put("1&SO(", "F");
smap.put("1&SO1", "F");
smap.put("1&SOF", "F");
smap.put("1&SON", "F");
smap.put("1&SOO", "F");
smap.put("1&SOS", "F");
smap.put("1&SOV", "F");
smap.put("1&STN", "F");
smap.put("1&SU", "F");
smap.put("1&SU(", "F");
smap.put("1&SU;", "F");
smap.put("1&SUC", "F");
smap.put("1&SUE", "F");
smap.put("1&SV", "F");
smap.put("1&SV;", "F");
smap.put("1&SVC", "F");
smap.put("1&SVO", "F");
smap.put("1&V", "F");
smap.put("1&V&(", "F");
smap.put("1&V&1", "F");
smap.put("1&V&F", "F");
smap.put("1&V&N", "F");
smap.put("1&V&S", "F");
smap.put("1&V&V", "F");
smap.put("1&V)&", "F");
smap.put("1&V)C", "F");
smap.put("1&V)O", "F");
smap.put("1&V)U", "F");
smap.put("1&V;", "F");
smap.put("1&V;C", "F");
smap.put("1&V;E", "F");
smap.put("1&V;T", "F");
smap.put("1&VB(", "F");
smap.put("1&VB1", "F");
smap.put("1&VBF", "F");
smap.put("1&VBN", "F");
smap.put("1&VBS", "F");
smap.put("1&VBV", "F");
smap.put("1&VC", "F");
smap.put("1&VEK", "F");
smap.put("1&VEN", "F");
smap.put("1&VF(", "F");
smap.put("1&VK(", "F");
smap.put("1&VK1", "F");
smap.put("1&VKF", "F");
smap.put("1&VKN", "F");
smap.put("1&VKS", "F");
smap.put("1&VKV", "F");
smap.put("1&VO(", "F");
smap.put("1&VOF", "F");
smap.put("1&VOO", "F");
smap.put("1&VOS", "F");
smap.put("1&VS", "F");
smap.put("1&VS;", "F");
smap.put("1&VSC", "F");
smap.put("1&VSO", "F");
smap.put("1&VTN", "F");
smap.put("1&VU", "F");
smap.put("1&VU(", "F");
smap.put("1&VU;", "F");
smap.put("1&VUC", "F");
smap.put("1&VUE", "F");
smap.put("1(EF(", "F");
smap.put("1(EKF", "F");
smap.put("1(EKN", "F");
smap.put("1(ENK", "F");
smap.put("1(U(E", "F");
smap.put("1)&(1", "F");
smap.put("1)&(E", "F");
smap.put("1)&(F", "F");
smap.put("1)&(N", "F");
smap.put("1)&(S", "F");
smap.put("1)&(V", "F");
smap.put("1)&1", "F");
smap.put("1)&1&", "F");
smap.put("1)&1)", "F");
smap.put("1)&1;", "F");
smap.put("1)&1B", "F");
smap.put("1)&1C", "F");
smap.put("1)&1F", "F");
smap.put("1)&1O", "F");
smap.put("1)&1U", "F");
smap.put("1)&F(", "F");
smap.put("1)&N", "F");
smap.put("1)&N&", "F");
smap.put("1)&N)", "F");
smap.put("1)&N;", "F");
smap.put("1)&NB", "F");
smap.put("1)&NC", "F");
smap.put("1)&NF", "F");
smap.put("1)&NO", "F");
smap.put("1)&NU", "F");
smap.put("1)&S", "F");
smap.put("1)&S&", "F");
smap.put("1)&S)", "F");
smap.put("1)&S;", "F");
smap.put("1)&SB", "F");
smap.put("1)&SC", "F");
smap.put("1)&SF", "F");
smap.put("1)&SO", "F");
smap.put("1)&SU", "F");
smap.put("1)&V", "F");
smap.put("1)&V&", "F");
smap.put("1)&V)", "F");
smap.put("1)&V;", "F");
smap.put("1)&VB", "F");
smap.put("1)&VC", "F");
smap.put("1)&VF", "F");
smap.put("1)&VO", "F");
smap.put("1)&VU", "F");
smap.put("1),(1", "F");
smap.put("1),(F", "F");
smap.put("1),(N", "F");
smap.put("1),(S", "F");
smap.put("1),(V", "F");
smap.put("1);E(", "F");
smap.put("1);E1", "F");
smap.put("1);EF", "F");
smap.put("1);EK", "F");
smap.put("1);EN", "F");
smap.put("1);EO", "F");
smap.put("1);ES", "F");
smap.put("1);EV", "F");
smap.put("1);T(", "F");
smap.put("1);T1", "F");
smap.put("1);TF", "F");
smap.put("1);TK", "F");
smap.put("1);TN", "F");
smap.put("1);TO", "F");
smap.put("1);TS", "F");
smap.put("1);TV", "F");
smap.put("1)B(1", "F");
smap.put("1)B(F", "F");
smap.put("1)B(N", "F");
smap.put("1)B(S", "F");
smap.put("1)B(V", "F");
smap.put("1)B1", "F");
smap.put("1)B1&", "F");
smap.put("1)B1;", "F");
smap.put("1)B1C", "F");
smap.put("1)B1K", "F");
smap.put("1)B1N", "F");
smap.put("1)B1O", "F");
smap.put("1)B1U", "F");
smap.put("1)BF(", "F");
smap.put("1)BN", "F");
smap.put("1)BN&", "F");
smap.put("1)BN;", "F");
smap.put("1)BNC", "F");
smap.put("1)BNK", "F");
smap.put("1)BNO", "F");
smap.put("1)BNU", "F");
smap.put("1)BS", "F");
smap.put("1)BS&", "F");
smap.put("1)BS;", "F");
smap.put("1)BSC", "F");
smap.put("1)BSK", "F");
smap.put("1)BSO", "F");
smap.put("1)BSU", "F");
smap.put("1)BV", "F");
smap.put("1)BV&", "F");
smap.put("1)BV;", "F");
smap.put("1)BVC", "F");
smap.put("1)BVK", "F");
smap.put("1)BVO", "F");
smap.put("1)BVU", "F");
smap.put("1)C", "F");
smap.put("1)E(1", "F");
smap.put("1)E(F", "F");
smap.put("1)E(N", "F");
smap.put("1)E(S", "F");
smap.put("1)E(V", "F");
smap.put("1)E1C", "F");
smap.put("1)E1O", "F");
smap.put("1)EF(", "F");
smap.put("1)EK(", "F");
smap.put("1)EK1", "F");
smap.put("1)EKF", "F");
smap.put("1)EKN", "F");
smap.put("1)EKS", "F");
smap.put("1)EKV", "F");
smap.put("1)ENC", "F");
smap.put("1)ENO", "F");
smap.put("1)ESC", "F");
smap.put("1)ESO", "F");
smap.put("1)EVC", "F");
smap.put("1)EVO", "F");
smap.put("1)K(1", "F");
smap.put("1)K(F", "F");
smap.put("1)K(N", "F");
smap.put("1)K(S", "F");
smap.put("1)K(V", "F");
smap.put("1)K1&", "F");
smap.put("1)K1;", "F");
smap.put("1)K1B", "F");
smap.put("1)K1E", "F");
smap.put("1)K1O", "F");
smap.put("1)K1U", "F");
smap.put("1)KB(", "F");
smap.put("1)KB1", "F");
smap.put("1)KBF", "F");
smap.put("1)KBN", "F");
smap.put("1)KBS", "F");
smap.put("1)KBV", "F");
smap.put("1)KF(", "F");
smap.put("1)KN&", "F");
smap.put("1)KN;", "F");
smap.put("1)KNB", "F");
smap.put("1)KNE", "F");
smap.put("1)KNK", "F");
smap.put("1)KNU", "F");
smap.put("1)KS&", "F");
smap.put("1)KS;", "F");
smap.put("1)KSB", "F");
smap.put("1)KSE", "F");
smap.put("1)KSO", "F");
smap.put("1)KSU", "F");
smap.put("1)KUE", "F");
smap.put("1)KV&", "F");
smap.put("1)KV;", "F");
smap.put("1)KVB", "F");
smap.put("1)KVE", "F");
smap.put("1)KVO", "F");
smap.put("1)KVU", "F");
smap.put("1)O(1", "F");
smap.put("1)O(E", "F");
smap.put("1)O(F", "F");
smap.put("1)O(N", "F");
smap.put("1)O(S", "F");
smap.put("1)O(V", "F");
smap.put("1)O1", "F");
smap.put("1)O1&", "F");
smap.put("1)O1)", "F");
smap.put("1)O1;", "F");
smap.put("1)O1B", "F");
smap.put("1)O1C", "F");
smap.put("1)O1K", "F");
smap.put("1)O1U", "F");
smap.put("1)OF(", "F");
smap.put("1)ON&", "F");
smap.put("1)ON)", "F");
smap.put("1)ON;", "F");
smap.put("1)ONB", "F");
smap.put("1)ONC", "F");
smap.put("1)ONK", "F");
smap.put("1)ONU", "F");
smap.put("1)OS", "F");
smap.put("1)OS&", "F");
smap.put("1)OS)", "F");
smap.put("1)OS;", "F");
smap.put("1)OSB", "F");
smap.put("1)OSC", "F");
smap.put("1)OSK", "F");
smap.put("1)OSU", "F");
smap.put("1)OV", "F");
smap.put("1)OV&", "F");
smap.put("1)OV)", "F");
smap.put("1)OV;", "F");
smap.put("1)OVB", "F");
smap.put("1)OVC", "F");
smap.put("1)OVK", "F");
smap.put("1)OVO", "F");
smap.put("1)OVU", "F");
smap.put("1)U(E", "F");
smap.put("1)UE(", "F");
smap.put("1)UE1", "F");
smap.put("1)UEF", "F");
smap.put("1)UEK", "F");
smap.put("1)UEN", "F");
smap.put("1)UES", "F");
smap.put("1)UEV", "F");
smap.put("1,(1)", "F");
smap.put("1,(1O", "F");
smap.put("1,(E(", "F");
smap.put("1,(E1", "F");
smap.put("1,(EF", "F");
smap.put("1,(EK", "F");
smap.put("1,(EN", "F");
smap.put("1,(ES", "F");
smap.put("1,(EV", "F");
smap.put("1,(F(", "F");
smap.put("1,(N)", "F");
smap.put("1,(NO", "F");
smap.put("1,(S)", "F");
smap.put("1,(SO", "F");
smap.put("1,(V)", "F");
smap.put("1,(VO", "F");
smap.put("1,F()", "F");
smap.put("1,F(1", "F");
smap.put("1,F(F", "F");
smap.put("1,F(N", "F");
smap.put("1,F(S", "F");
smap.put("1,F(V", "F");
smap.put("1;E(1", "F");
smap.put("1;E(E", "F");
smap.put("1;E(F", "F");
smap.put("1;E(N", "F");
smap.put("1;E(S", "F");
smap.put("1;E(V", "F");
smap.put("1;E1,", "F");
smap.put("1;E1;", "F");
smap.put("1;E1C", "F");
smap.put("1;E1K", "F");
smap.put("1;E1O", "F");
smap.put("1;E1T", "F");
smap.put("1;EF(", "F");
smap.put("1;EK(", "F");
smap.put("1;EK1", "F");
smap.put("1;EKF", "F");
smap.put("1;EKN", "F");
smap.put("1;EKO", "F");
smap.put("1;EKS", "F");
smap.put("1;EKV", "F");
smap.put("1;EN,", "F");
smap.put("1;EN;", "F");
smap.put("1;ENC", "F");
smap.put("1;ENE", "F");
smap.put("1;ENK", "F");
smap.put("1;ENO", "F");
smap.put("1;ENT", "F");
smap.put("1;ES,", "F");
smap.put("1;ES;", "F");
smap.put("1;ESC", "F");
smap.put("1;ESK", "F");
smap.put("1;ESO", "F");
smap.put("1;EST", "F");
smap.put("1;EV,", "F");
smap.put("1;EV;", "F");
smap.put("1;EVC", "F");
smap.put("1;EVK", "F");
smap.put("1;EVO", "F");
smap.put("1;EVT", "F");
smap.put("1;N:T", "F");
smap.put("1;T(1", "F");
smap.put("1;T(E", "F");
smap.put("1;T(F", "F");
smap.put("1;T(N", "F");
smap.put("1;T(S", "F");
smap.put("1;T(V", "F");
smap.put("1;T1,", "F");
smap.put("1;T1;", "F");
smap.put("1;T1C", "F");
smap.put("1;T1F", "F");
smap.put("1;T1K", "F");
smap.put("1;T1O", "F");
smap.put("1;T1T", "F");
smap.put("1;T;", "F");
smap.put("1;T;C", "F");
smap.put("1;TF(", "F");
smap.put("1;TK(", "F");
smap.put("1;TK1", "F");
smap.put("1;TKF", "F");
smap.put("1;TKK", "F");
smap.put("1;TKN", "F");
smap.put("1;TKO", "F");
smap.put("1;TKS", "F");
smap.put("1;TKV", "F");
smap.put("1;TN(", "F");
smap.put("1;TN,", "F");
smap.put("1;TN1", "F");
smap.put("1;TN;", "F");
smap.put("1;TNC", "F");
smap.put("1;TNF", "F");
smap.put("1;TNK", "F");
smap.put("1;TNN", "F");
smap.put("1;TNO", "F");
smap.put("1;TNS", "F");
smap.put("1;TNT", "F");
smap.put("1;TNV", "F");
smap.put("1;TO(", "F");
smap.put("1;TS,", "F");
smap.put("1;TS;", "F");
smap.put("1;TSC", "F");
smap.put("1;TSF", "F");
smap.put("1;TSK", "F");
smap.put("1;TSO", "F");
smap.put("1;TST", "F");
smap.put("1;TT(", "F");
smap.put("1;TT1", "F");
smap.put("1;TTF", "F");
smap.put("1;TTN", "F");
smap.put("1;TTS", "F");
smap.put("1;TTV", "F");
smap.put("1;TV,", "F");
smap.put("1;TV;", "F");
smap.put("1;TVC", "F");
smap.put("1;TVF", "F");
smap.put("1;TVK", "F");
smap.put("1;TVO", "F");
smap.put("1;TVT", "F");
smap.put("1A(F(", "F");
smap.put("1A(N)", "F");
smap.put("1A(NO", "F");
smap.put("1A(S)", "F");
smap.put("1A(SO", "F");
smap.put("1A(V)", "F");
smap.put("1A(VO", "F");
smap.put("1AF()", "F");
smap.put("1AF(1", "F");
smap.put("1AF(F", "F");
smap.put("1AF(N", "F");
smap.put("1AF(S", "F");
smap.put("1AF(V", "F");
smap.put("1ASO(", "F");
smap.put("1ASO1", "F");
smap.put("1ASOF", "F");
smap.put("1ASON", "F");
smap.put("1ASOS", "F");
smap.put("1ASOV", "F");
smap.put("1ASUE", "F");
smap.put("1ATO(", "F");
smap.put("1ATO1", "F");
smap.put("1ATOF", "F");
smap.put("1ATON", "F");
smap.put("1ATOS", "F");
smap.put("1ATOV", "F");
smap.put("1ATUE", "F");
smap.put("1AVO(", "F");
smap.put("1AVOF", "F");
smap.put("1AVOS", "F");
smap.put("1AVUE", "F");
smap.put("1B(1)", "F");
smap.put("1B(1O", "F");
smap.put("1B(F(", "F");
smap.put("1B(N)", "F");
smap.put("1B(NO", "F");
smap.put("1B(S)", "F");
smap.put("1B(SO", "F");
smap.put("1B(V)", "F");
smap.put("1B(VO", "F");
smap.put("1B1", "F");
smap.put("1B1&(", "F");
smap.put("1B1&1", "F");
smap.put("1B1&F", "F");
smap.put("1B1&N", "F");
smap.put("1B1&S", "F");
smap.put("1B1&V", "F");
smap.put("1B1,(", "F");
smap.put("1B1,F", "F");
smap.put("1B1;", "F");
smap.put("1B1;C", "F");
smap.put("1B1B(", "F");
smap.put("1B1B1", "F");
smap.put("1B1BF", "F");
smap.put("1B1BN", "F");
smap.put("1B1BS", "F");
smap.put("1B1BV", "F");
smap.put("1B1C", "F");
smap.put("1B1K(", "F");
smap.put("1B1K1", "F");
smap.put("1B1KF", "F");
smap.put("1B1KN", "F");
smap.put("1B1KS", "F");
smap.put("1B1KV", "F");
smap.put("1B1O(", "F");
smap.put("1B1OF", "F");
smap.put("1B1OS", "F");
smap.put("1B1OV", "F");
smap.put("1B1U(", "F");
smap.put("1B1UE", "F");
smap.put("1BE(1", "F");
smap.put("1BE(F", "F");
smap.put("1BE(N", "F");
smap.put("1BE(S", "F");
smap.put("1BE(V", "F");
smap.put("1BEK(", "F");
smap.put("1BF()", "F");
smap.put("1BF(1", "F");
smap.put("1BF(F", "F");
smap.put("1BF(N", "F");
smap.put("1BF(S", "F");
smap.put("1BF(V", "F");
smap.put("1BN", "F");
smap.put("1BN&(", "F");
smap.put("1BN&1", "F");
smap.put("1BN&F", "F");
smap.put("1BN&N", "F");
smap.put("1BN&S", "F");
smap.put("1BN&V", "F");
smap.put("1BN,(", "F");
smap.put("1BN,F", "F");
smap.put("1BN;", "F");
smap.put("1BN;C", "F");
smap.put("1BNB(", "F");
smap.put("1BNB1", "F");
smap.put("1BNBF", "F");
smap.put("1BNBN", "F");
smap.put("1BNBS", "F");
smap.put("1BNBV", "F");
smap.put("1BNC", "F");
smap.put("1BNK(", "F");
smap.put("1BNK1", "F");
smap.put("1BNKF", "F");
smap.put("1BNKN", "F");
smap.put("1BNKS", "F");
smap.put("1BNKV", "F");
smap.put("1BNO(", "F");
smap.put("1BNOF", "F");
smap.put("1BNOS", "F");
smap.put("1BNOV", "F");
smap.put("1BNU(", "F");
smap.put("1BNUE", "F");
smap.put("1BS", "F");
smap.put("1BS&(", "F");
smap.put("1BS&1", "F");
smap.put("1BS&F", "F");
smap.put("1BS&N", "F");
smap.put("1BS&S", "F");
smap.put("1BS&V", "F");
smap.put("1BS,(", "F");
smap.put("1BS,F", "F");
smap.put("1BS;", "F");
smap.put("1BS;C", "F");
smap.put("1BSB(", "F");
smap.put("1BSB1", "F");
smap.put("1BSBF", "F");
smap.put("1BSBN", "F");
smap.put("1BSBS", "F");
smap.put("1BSBV", "F");
smap.put("1BSC", "F");
smap.put("1BSK(", "F");
smap.put("1BSK1", "F");
smap.put("1BSKF", "F");
smap.put("1BSKN", "F");
smap.put("1BSKS", "F");
smap.put("1BSKV", "F");
smap.put("1BSO(", "F");
smap.put("1BSO1", "F");
smap.put("1BSOF", "F");
smap.put("1BSON", "F");
smap.put("1BSOS", "F");
smap.put("1BSOV", "F");
smap.put("1BSU(", "F");
smap.put("1BSUE", "F");
smap.put("1BV", "F");
smap.put("1BV&(", "F");
smap.put("1BV&1", "F");
smap.put("1BV&F", "F");
smap.put("1BV&N", "F");
smap.put("1BV&S", "F");
smap.put("1BV&V", "F");
smap.put("1BV,(", "F");
smap.put("1BV,F", "F");
smap.put("1BV;", "F");
smap.put("1BV;C", "F");
smap.put("1BVB(", "F");
smap.put("1BVB1", "F");
smap.put("1BVBF", "F");
smap.put("1BVBN", "F");
smap.put("1BVBS", "F");
smap.put("1BVBV", "F");
smap.put("1BVC", "F");
smap.put("1BVK(", "F");
smap.put("1BVK1", "F");
smap.put("1BVKF", "F");
smap.put("1BVKN", "F");
smap.put("1BVKS", "F");
smap.put("1BVKV", "F");
smap.put("1BVO(", "F");
smap.put("1BVOF", "F");
smap.put("1BVOS", "F");
smap.put("1BVU(", "F");
smap.put("1BVUE", "F");
smap.put("1C", "F");
smap.put("1E(1)", "F");
smap.put("1E(1O", "F");
smap.put("1E(F(", "F");
smap.put("1E(N)", "F");
smap.put("1E(NO", "F");
smap.put("1E(S)", "F");
smap.put("1E(SO", "F");
smap.put("1E(V)", "F");
smap.put("1E(VO", "F");
smap.put("1E1C", "F");
smap.put("1E1O(", "F");
smap.put("1E1OF", "F");
smap.put("1E1OS", "F");
smap.put("1E1OV", "F");
smap.put("1E1UE", "F");
smap.put("1EF()", "F");
smap.put("1EF(1", "F");
smap.put("1EF(F", "F");
smap.put("1EF(N", "F");
smap.put("1EF(S", "F");
smap.put("1EF(V", "F");
smap.put("1EK(1", "F");
smap.put("1EK(E", "F");
smap.put("1EK(F", "F");
smap.put("1EK(N", "F");
smap.put("1EK(S", "F");
smap.put("1EK(V", "F");
smap.put("1EK1C", "F");
smap.put("1EK1O", "F");
smap.put("1EK1U", "F");
smap.put("1EKF(", "F");
smap.put("1EKNC", "F");
smap.put("1EKNE", "F");
smap.put("1EKNU", "F");
smap.put("1EKOK", "F");
smap.put("1EKSC", "F");
smap.put("1EKSO", "F");
smap.put("1EKSU", "F");
smap.put("1EKU(", "F");
smap.put("1EKU1", "F");
smap.put("1EKUE", "F");
smap.put("1EKUF", "F");
smap.put("1EKUN", "F");
smap.put("1EKUS", "F");
smap.put("1EKUV", "F");
smap.put("1EKVC", "F");
smap.put("1EKVO", "F");
smap.put("1EKVU", "F");
smap.put("1ENC", "F");
smap.put("1ENEN", "F");
smap.put("1ENO(", "F");
smap.put("1ENOF", "F");
smap.put("1ENOS", "F");
smap.put("1ENOV", "F");
smap.put("1ENUE", "F");
smap.put("1EOKN", "F");
smap.put("1ESC", "F");
smap.put("1ESO(", "F");
smap.put("1ESO1", "F");
smap.put("1ESOF", "F");
smap.put("1ESON", "F");
smap.put("1ESOS", "F");
smap.put("1ESOV", "F");
smap.put("1ESUE", "F");
smap.put("1EU(1", "F");
smap.put("1EU(F", "F");
smap.put("1EU(N", "F");
smap.put("1EU(S", "F");
smap.put("1EU(V", "F");
smap.put("1EU1,", "F");
smap.put("1EU1C", "F");
smap.put("1EU1O", "F");
smap.put("1EUEF", "F");
smap.put("1EUEK", "F");
smap.put("1EUF(", "F");
smap.put("1EUN,", "F");
smap.put("1EUNC", "F");
smap.put("1EUNO", "F");
smap.put("1EUS,", "F");
smap.put("1EUSC", "F");
smap.put("1EUSO", "F");
smap.put("1EUV,", "F");
smap.put("1EUVC", "F");
smap.put("1EUVO", "F");
smap.put("1EVC", "F");
smap.put("1EVO(", "F");
smap.put("1EVOF", "F");
smap.put("1EVOS", "F");
smap.put("1EVUE", "F");
smap.put("1F()1", "F");
smap.put("1F()F", "F");
smap.put("1F()K", "F");
smap.put("1F()N", "F");
smap.put("1F()O", "F");
smap.put("1F()S", "F");
smap.put("1F()U", "F");
smap.put("1F()V", "F");
smap.put("1F(1)", "F");
smap.put("1F(1N", "F");
smap.put("1F(1O", "F");
smap.put("1F(E(", "F");
smap.put("1F(E1", "F");
smap.put("1F(EF", "F");
smap.put("1F(EK", "F");
smap.put("1F(EN", "F");
smap.put("1F(ES", "F");
smap.put("1F(EV", "F");
smap.put("1F(F(", "F");
smap.put("1F(N)", "F");
smap.put("1F(N,", "F");
smap.put("1F(NO", "F");
smap.put("1F(S)", "F");
smap.put("1F(SO", "F");
smap.put("1F(V)", "F");
smap.put("1F(VO", "F");
smap.put("1K(1O", "F");
smap.put("1K(F(", "F");
smap.put("1K(N)", "F");
smap.put("1K(NO", "F");
smap.put("1K(S)", "F");
smap.put("1K(SO", "F");
smap.put("1K(V)", "F");
smap.put("1K(VO", "F");
smap.put("1K)&(", "F");
smap.put("1K)&1", "F");
smap.put("1K)&F", "F");
smap.put("1K)&N", "F");
smap.put("1K)&S", "F");
smap.put("1K)&V", "F");
smap.put("1K);E", "F");
smap.put("1K);T", "F");
smap.put("1K)B(", "F");
smap.put("1K)B1", "F");
smap.put("1K)BF", "F");
smap.put("1K)BN", "F");
smap.put("1K)BS", "F");
smap.put("1K)BV", "F");
smap.put("1K)E(", "F");
smap.put("1K)E1", "F");
smap.put("1K)EF", "F");
smap.put("1K)EK", "F");
smap.put("1K)EN", "F");
smap.put("1K)ES", "F");
smap.put("1K)EV", "F");
smap.put("1K)OF", "F");
smap.put("1K)UE", "F");
smap.put("1K1", "F");
smap.put("1K1&(", "F");
smap.put("1K1&1", "F");
smap.put("1K1&F", "F");
smap.put("1K1&N", "F");
smap.put("1K1&S", "F");
smap.put("1K1&V", "F");
smap.put("1K1;", "F");
smap.put("1K1;C", "F");
smap.put("1K1;E", "F");
smap.put("1K1;T", "F");
smap.put("1K1B(", "F");
smap.put("1K1B1", "F");
smap.put("1K1BF", "F");
smap.put("1K1BN", "F");
smap.put("1K1BS", "F");
smap.put("1K1BV", "F");
smap.put("1K1C", "F");
smap.put("1K1E(", "F");
smap.put("1K1E1", "F");
smap.put("1K1EF", "F");
smap.put("1K1EK", "F");
smap.put("1K1EN", "F");
smap.put("1K1ES", "F");
smap.put("1K1EV", "F");
smap.put("1K1O(", "F");
smap.put("1K1OF", "F");
smap.put("1K1OS", "F");
smap.put("1K1OV", "F");
smap.put("1K1U(", "F");
smap.put("1K1UE", "F");
smap.put("1KF()", "F");
smap.put("1KF(1", "F");
smap.put("1KF(F", "F");
smap.put("1KF(N", "F");
smap.put("1KF(S", "F");
smap.put("1KF(V", "F");
smap.put("1KN", "F");
smap.put("1KN&(", "F");
smap.put("1KN&1", "F");
smap.put("1KN&F", "F");
smap.put("1KN&N", "F");
smap.put("1KN&S", "F");
smap.put("1KN&V", "F");
smap.put("1KN;", "F");
smap.put("1KN;C", "F");
smap.put("1KN;E", "F");
smap.put("1KN;T", "F");
smap.put("1KNB(", "F");
smap.put("1KNB1", "F");
smap.put("1KNBF", "F");
smap.put("1KNBN", "F");
smap.put("1KNBS", "F");
smap.put("1KNBV", "F");
smap.put("1KNC", "F");
smap.put("1KNE(", "F");
smap.put("1KNE1", "F");
smap.put("1KNEF", "F");
smap.put("1KNEN", "F");
smap.put("1KNES", "F");
smap.put("1KNEV", "F");
smap.put("1KNU(", "F");
smap.put("1KNUE", "F");
smap.put("1KS", "F");
smap.put("1KS&(", "F");
smap.put("1KS&1", "F");
smap.put("1KS&F", "F");
smap.put("1KS&N", "F");
smap.put("1KS&S", "F");
smap.put("1KS&V", "F");
smap.put("1KS;", "F");
smap.put("1KS;C", "F");
smap.put("1KS;E", "F");
smap.put("1KS;T", "F");
smap.put("1KSB(", "F");
smap.put("1KSB1", "F");
smap.put("1KSBF", "F");
smap.put("1KSBN", "F");
smap.put("1KSBS", "F");
smap.put("1KSBV", "F");
smap.put("1KSC", "F");
smap.put("1KSE(", "F");
smap.put("1KSE1", "F");
smap.put("1KSEF", "F");
smap.put("1KSEK", "F");
smap.put("1KSEN", "F");
smap.put("1KSES", "F");
smap.put("1KSEV", "F");
smap.put("1KSO(", "F");
smap.put("1KSO1", "F");
smap.put("1KSOF", "F");
smap.put("1KSON", "F");
smap.put("1KSOS", "F");
smap.put("1KSOV", "F");
smap.put("1KSU(", "F");
smap.put("1KSUE", "F");
smap.put("1KUE(", "F");
smap.put("1KUE1", "F");
smap.put("1KUEF", "F");
smap.put("1KUEK", "F");
smap.put("1KUEN", "F");
smap.put("1KUES", "F");
smap.put("1KUEV", "F");
smap.put("1KV", "F");
smap.put("1KV&(", "F");
smap.put("1KV&1", "F");
smap.put("1KV&F", "F");
smap.put("1KV&N", "F");
smap.put("1KV&S", "F");
smap.put("1KV&V", "F");
smap.put("1KV;", "F");
smap.put("1KV;C", "F");
smap.put("1KV;E", "F");
smap.put("1KV;T", "F");
smap.put("1KVB(", "F");
smap.put("1KVB1", "F");
smap.put("1KVBF", "F");
smap.put("1KVBN", "F");
smap.put("1KVBS", "F");
smap.put("1KVBV", "F");
smap.put("1KVC", "F");
smap.put("1KVE(", "F");
smap.put("1KVE1", "F");
smap.put("1KVEF", "F");
smap.put("1KVEK", "F");
smap.put("1KVEN", "F");
smap.put("1KVES", "F");
smap.put("1KVEV", "F");
smap.put("1KVO(", "F");
smap.put("1KVOF", "F");
smap.put("1KVOS", "F");
smap.put("1KVU(", "F");
smap.put("1KVUE", "F");
smap.put("1N&F(", "F");
smap.put("1N(1)", "F");
smap.put("1N(1O", "F");
smap.put("1N(F(", "F");
smap.put("1N(S)", "F");
smap.put("1N(SO", "F");
smap.put("1N(V)", "F");
smap.put("1N(VO", "F");
smap.put("1N)UE", "F");
smap.put("1N,F(", "F");
smap.put("1NE(1", "F");
smap.put("1NE(F", "F");
smap.put("1NE(N", "F");
smap.put("1NE(S", "F");
smap.put("1NE(V", "F");
smap.put("1NE1C", "F");
smap.put("1NE1O", "F");
smap.put("1NEF(", "F");
smap.put("1NENC", "F");
smap.put("1NENO", "F");
smap.put("1NESC", "F");
smap.put("1NESO", "F");
smap.put("1NEVC", "F");
smap.put("1NEVO", "F");
smap.put("1NF()", "F");
smap.put("1NF(1", "F");
smap.put("1NF(F", "F");
smap.put("1NF(N", "F");
smap.put("1NF(S", "F");
smap.put("1NF(V", "F");
smap.put("1NU(E", "F");
smap.put("1NUE", "F");
smap.put("1NUE(", "F");
smap.put("1NUE1", "F");
smap.put("1NUE;", "F");
smap.put("1NUEC", "F");
smap.put("1NUEF", "F");
smap.put("1NUEK", "F");
smap.put("1NUEN", "F");
smap.put("1NUES", "F");
smap.put("1NUEV", "F");
smap.put("1O(1&", "F");
smap.put("1O(1)", "F");
smap.put("1O(1,", "F");
smap.put("1O(1O", "F");
smap.put("1O(E(", "F");
smap.put("1O(E1", "F");
smap.put("1O(EE", "F");
smap.put("1O(EF", "F");
smap.put("1O(EK", "F");
smap.put("1O(EN", "F");
smap.put("1O(ES", "F");
smap.put("1O(EV", "F");
smap.put("1O(F(", "F");
smap.put("1O(N&", "F");
smap.put("1O(N)", "F");
smap.put("1O(N,", "F");
smap.put("1O(NO", "F");
smap.put("1O(S&", "F");
smap.put("1O(S)", "F");
smap.put("1O(S,", "F");
smap.put("1O(SO", "F");
smap.put("1O(V&", "F");
smap.put("1O(V)", "F");
smap.put("1O(V,", "F");
smap.put("1O(VO", "F");
smap.put("1OF()", "F");
smap.put("1OF(1", "F");
smap.put("1OF(E", "F");
smap.put("1OF(F", "F");
smap.put("1OF(N", "F");
smap.put("1OF(S", "F");
smap.put("1OF(V", "F");
smap.put("1OK&(", "F");
smap.put("1OK&1", "F");
smap.put("1OK&F", "F");
smap.put("1OK&N", "F");
smap.put("1OK&S", "F");
smap.put("1OK&V", "F");
smap.put("1OK(1", "F");
smap.put("1OK(F", "F");
smap.put("1OK(N", "F");
smap.put("1OK(S", "F");
smap.put("1OK(V", "F");
smap.put("1OK1C", "F");
smap.put("1OK1O", "F");
smap.put("1OKF(", "F");
smap.put("1OKNC", "F");
smap.put("1OKO(", "F");
smap.put("1OKO1", "F");
smap.put("1OKOF", "F");
smap.put("1OKON", "F");
smap.put("1OKOS", "F");
smap.put("1OKOV", "F");
smap.put("1OKSC", "F");
smap.put("1OKSO", "F");
smap.put("1OKVC", "F");
smap.put("1OKVO", "F");
smap.put("1ONSU", "F");
smap.put("1OS&(", "F");
smap.put("1OS&1", "F");
smap.put("1OS&E", "F");
smap.put("1OS&F", "F");
smap.put("1OS&K", "F");
smap.put("1OS&N", "F");
smap.put("1OS&S", "F");
smap.put("1OS&U", "F");
smap.put("1OS&V", "F");
smap.put("1OS(E", "F");
smap.put("1OS(U", "F");
smap.put("1OS)&", "F");
smap.put("1OS),", "F");
smap.put("1OS);", "F");
smap.put("1OS)B", "F");
smap.put("1OS)C", "F");
smap.put("1OS)E", "F");
smap.put("1OS)K", "F");
smap.put("1OS)O", "F");
smap.put("1OS)U", "F");
smap.put("1OS,(", "F");
smap.put("1OS,F", "F");
smap.put("1OS1(", "F");
smap.put("1OS1F", "F");
smap.put("1OS1N", "F");
smap.put("1OS1S", "F");
smap.put("1OS1U", "F");
smap.put("1OS1V", "F");
smap.put("1OS;", "F");
smap.put("1OS;C", "F");
smap.put("1OS;E", "F");
smap.put("1OS;N", "F");
smap.put("1OS;T", "F");
smap.put("1OSA(", "F");
smap.put("1OSAF", "F");
smap.put("1OSAS", "F");
smap.put("1OSAT", "F");
smap.put("1OSAV", "F");
smap.put("1OSB(", "F");
smap.put("1OSB1", "F");
smap.put("1OSBE", "F");
smap.put("1OSBF", "F");
smap.put("1OSBN", "F");
smap.put("1OSBS", "F");
smap.put("1OSBV", "F");
smap.put("1OSC", "F");
smap.put("1OSE(", "F");
smap.put("1OSE1", "F");
smap.put("1OSEF", "F");
smap.put("1OSEK", "F");
smap.put("1OSEN", "F");
smap.put("1OSEO", "F");
smap.put("1OSES", "F");
smap.put("1OSEU", "F");
smap.put("1OSEV", "F");
smap.put("1OSF(", "F");
smap.put("1OSK(", "F");
smap.put("1OSK)", "F");
smap.put("1OSK1", "F");
smap.put("1OSKB", "F");
smap.put("1OSKF", "F");
smap.put("1OSKN", "F");
smap.put("1OSKS", "F");
smap.put("1OSKU", "F");
smap.put("1OSKV", "F");
smap.put("1OSU", "F");
smap.put("1OSU(", "F");
smap.put("1OSU1", "F");
smap.put("1OSU;", "F");
smap.put("1OSUC", "F");
smap.put("1OSUE", "F");
smap.put("1OSUF", "F");
smap.put("1OSUK", "F");
smap.put("1OSUN", "F");
smap.put("1OSUO", "F");
smap.put("1OSUS", "F");
smap.put("1OSUT", "F");
smap.put("1OSUV", "F");
smap.put("1OSV(", "F");
smap.put("1OSVF", "F");
smap.put("1OSVO", "F");
smap.put("1OSVS", "F");
smap.put("1OSVU", "F");
smap.put("1OU(E", "F");
smap.put("1OUEK", "F");
smap.put("1OUEN", "F");
smap.put("1OV", "F");
smap.put("1OV&(", "F");
smap.put("1OV&1", "F");
smap.put("1OV&E", "F");
smap.put("1OV&F", "F");
smap.put("1OV&K", "F");
smap.put("1OV&N", "F");
smap.put("1OV&S", "F");
smap.put("1OV&U", "F");
smap.put("1OV&V", "F");
smap.put("1OV(E", "F");
smap.put("1OV(U", "F");
smap.put("1OV)&", "F");
smap.put("1OV),", "F");
smap.put("1OV);", "F");
smap.put("1OV)B", "F");
smap.put("1OV)C", "F");
smap.put("1OV)E", "F");
smap.put("1OV)K", "F");
smap.put("1OV)O", "F");
smap.put("1OV)U", "F");
smap.put("1OV,(", "F");
smap.put("1OV,F", "F");
smap.put("1OV;", "F");
smap.put("1OV;C", "F");
smap.put("1OV;E", "F");
smap.put("1OV;N", "F");
smap.put("1OV;T", "F");
smap.put("1OVA(", "F");
smap.put("1OVAF", "F");
smap.put("1OVAS", "F");
smap.put("1OVAT", "F");
smap.put("1OVAV", "F");
smap.put("1OVB(", "F");
smap.put("1OVB1", "F");
smap.put("1OVBE", "F");
smap.put("1OVBF", "F");
smap.put("1OVBN", "F");
smap.put("1OVBS", "F");
smap.put("1OVBV", "F");
smap.put("1OVC", "F");
smap.put("1OVE(", "F");
smap.put("1OVE1", "F");
smap.put("1OVEF", "F");
smap.put("1OVEK", "F");
smap.put("1OVEN", "F");
smap.put("1OVEO", "F");
smap.put("1OVES", "F");
smap.put("1OVEU", "F");
smap.put("1OVEV", "F");
smap.put("1OVF(", "F");
smap.put("1OVK(", "F");
smap.put("1OVK)", "F");
smap.put("1OVK1", "F");
smap.put("1OVKB", "F");
smap.put("1OVKF", "F");
smap.put("1OVKN", "F");
smap.put("1OVKS", "F");
smap.put("1OVKU", "F");
smap.put("1OVKV", "F");
smap.put("1OVO(", "F");
smap.put("1OVOF", "F");
smap.put("1OVOK", "F");
smap.put("1OVOS", "F");
smap.put("1OVOU", "F");
smap.put("1OVS(", "F");
smap.put("1OVS1", "F");
smap.put("1OVSF", "F");
smap.put("1OVSO", "F");
smap.put("1OVSU", "F");
smap.put("1OVSV", "F");
smap.put("1OVU", "F");
smap.put("1OVU(", "F");
smap.put("1OVU1", "F");
smap.put("1OVU;", "F");
smap.put("1OVUC", "F");
smap.put("1OVUE", "F");
smap.put("1OVUF", "F");
smap.put("1OVUK", "F");
smap.put("1OVUN", "F");
smap.put("1OVUO", "F");
smap.put("1OVUS", "F");
smap.put("1OVUT", "F");
smap.put("1OVUV", "F");
smap.put("1SF()", "F");
smap.put("1SF(1", "F");
smap.put("1SF(F", "F");
smap.put("1SF(N", "F");
smap.put("1SF(S", "F");
smap.put("1SF(V", "F");
smap.put("1SUE", "F");
smap.put("1SUE;", "F");
smap.put("1SUEC", "F");
smap.put("1SUEK", "F");
smap.put("1SV", "F");
smap.put("1SV;", "F");
smap.put("1SV;C", "F");
smap.put("1SVC", "F");
smap.put("1SVO(", "F");
smap.put("1SVOF", "F");
smap.put("1SVOS", "F");
smap.put("1U", "F");
smap.put("1U(1)", "F");
smap.put("1U(1O", "F");
smap.put("1U(E(", "F");
smap.put("1U(E1", "F");
smap.put("1U(EF", "F");
smap.put("1U(EK", "F");
smap.put("1U(EN", "F");
smap.put("1U(ES", "F");
smap.put("1U(EV", "F");
smap.put("1U(F(", "F");
smap.put("1U(N)", "F");
smap.put("1U(NO", "F");
smap.put("1U(S)", "F");
smap.put("1U(SO", "F");
smap.put("1U(V)", "F");
smap.put("1U(VO", "F");
smap.put("1U1,(", "F");
smap.put("1U1,F", "F");
smap.put("1U1C", "F");
smap.put("1U1O(", "F");
smap.put("1U1OF", "F");
smap.put("1U1OS", "F");
smap.put("1U1OV", "F");
smap.put("1U;", "F");
smap.put("1U;C", "F");
smap.put("1UC", "F");
smap.put("1UE", "F");
smap.put("1UE(1", "F");
smap.put("1UE(E", "F");
smap.put("1UE(F", "F");
smap.put("1UE(N", "F");
smap.put("1UE(O", "F");
smap.put("1UE(S", "F");
smap.put("1UE(V", "F");
smap.put("1UE1", "F");
smap.put("1UE1&", "F");
smap.put("1UE1(", "F");
smap.put("1UE1)", "F");
smap.put("1UE1,", "F");
smap.put("1UE1;", "F");
smap.put("1UE1B", "F");
smap.put("1UE1C", "F");
smap.put("1UE1F", "F");
smap.put("1UE1K", "F");
smap.put("1UE1N", "F");
smap.put("1UE1O", "F");
smap.put("1UE1S", "F");
smap.put("1UE1U", "F");
smap.put("1UE1V", "F");
smap.put("1UE;", "F");
smap.put("1UE;C", "F");
smap.put("1UEC", "F");
smap.put("1UEF", "F");
smap.put("1UEF(", "F");
smap.put("1UEF,", "F");
smap.put("1UEF;", "F");
smap.put("1UEFC", "F");
smap.put("1UEK", "F");
smap.put("1UEK(", "F");
smap.put("1UEK1", "F");
smap.put("1UEK;", "F");
smap.put("1UEKC", "F");
smap.put("1UEKF", "F");
smap.put("1UEKN", "F");
smap.put("1UEKO", "F");
smap.put("1UEKS", "F");
smap.put("1UEKV", "F");
smap.put("1UEN", "F");
smap.put("1UEN&", "F");
smap.put("1UEN(", "F");
smap.put("1UEN)", "F");
smap.put("1UEN,", "F");
smap.put("1UEN1", "F");
smap.put("1UEN;", "F");
smap.put("1UENB", "F");
smap.put("1UENC", "F");
smap.put("1UENF", "F");
smap.put("1UENK", "F");
smap.put("1UENN", "F");
smap.put("1UENO", "F");
smap.put("1UENS", "F");
smap.put("1UENU", "F");
smap.put("1UEOK", "F");
smap.put("1UEON", "F");
smap.put("1UEOO", "F");
smap.put("1UES", "F");
smap.put("1UES&", "F");
smap.put("1UES(", "F");
smap.put("1UES)", "F");
smap.put("1UES,", "F");
smap.put("1UES1", "F");
smap.put("1UES;", "F");
smap.put("1UESB", "F");
smap.put("1UESC", "F");
smap.put("1UESF", "F");
smap.put("1UESK", "F");
smap.put("1UESO", "F");
smap.put("1UESU", "F");
smap.put("1UESV", "F");
smap.put("1UEV", "F");
smap.put("1UEV&", "F");
smap.put("1UEV(", "F");
smap.put("1UEV)", "F");
smap.put("1UEV,", "F");
smap.put("1UEV;", "F");
smap.put("1UEVB", "F");
smap.put("1UEVC", "F");
smap.put("1UEVF", "F");
smap.put("1UEVK", "F");
smap.put("1UEVN", "F");
smap.put("1UEVO", "F");
smap.put("1UEVS", "F");
smap.put("1UEVU", "F");
smap.put("1UF()", "F");
smap.put("1UF(1", "F");
smap.put("1UF(F", "F");
smap.put("1UF(N", "F");
smap.put("1UF(S", "F");
smap.put("1UF(V", "F");
smap.put("1UK(E", "F");
smap.put("1UN(1", "F");
smap.put("1UN(F", "F");
smap.put("1UN(S", "F");
smap.put("1UN(V", "F");
smap.put("1UN,(", "F");
smap.put("1UN,F", "F");
smap.put("1UN1(", "F");
smap.put("1UN1,", "F");
smap.put("1UN1O", "F");
smap.put("1UNC", "F");
smap.put("1UNE(", "F");
smap.put("1UNE1", "F");
smap.put("1UNEF", "F");
smap.put("1UNEN", "F");
smap.put("1UNES", "F");
smap.put("1UNEV", "F");
smap.put("1UNF(", "F");
smap.put("1UNO(", "F");
smap.put("1UNOF", "F");
smap.put("1UNOS", "F");
smap.put("1UNOV", "F");
smap.put("1UNS(", "F");
smap.put("1UNS,", "F");
smap.put("1UNSO", "F");
smap.put("1UO(E", "F");
smap.put("1UON(", "F");
smap.put("1UON1", "F");
smap.put("1UONF", "F");
smap.put("1UONS", "F");
smap.put("1US,(", "F");
smap.put("1US,F", "F");
smap.put("1USC", "F");
smap.put("1USO(", "F");
smap.put("1USO1", "F");
smap.put("1USOF", "F");
smap.put("1USON", "F");
smap.put("1USOS", "F");
smap.put("1USOV", "F");
smap.put("1UTN(", "F");
smap.put("1UTN1", "F");
smap.put("1UTNF", "F");
smap.put("1UTNS", "F");
smap.put("1UV,(", "F");
smap.put("1UV,F", "F");
smap.put("1UVC", "F");
smap.put("1UVO(", "F");
smap.put("1UVOF", "F");
smap.put("1UVOS", "F");
smap.put("1VF()", "F");
smap.put("1VF(1", "F");
smap.put("1VF(F", "F");
smap.put("1VF(N", "F");
smap.put("1VF(S", "F");
smap.put("1VF(V", "F");
smap.put("1VO(1", "F");
smap.put("1VO(F", "F");
smap.put("1VO(N", "F");
smap.put("1VO(S", "F");
smap.put("1VO(V", "F");
smap.put("1VOF(", "F");
smap.put("1VOS(", "F");
smap.put("1VOS1", "F");
smap.put("1VOSF", "F");
smap.put("1VOSU", "F");
smap.put("1VOSV", "F");
smap.put("1VS", "F");
smap.put("1VS;", "F");
smap.put("1VS;C", "F");
smap.put("1VSC", "F");
smap.put("1VSO(", "F");
smap.put("1VSO1", "F");
smap.put("1VSOF", "F");
smap.put("1VSON", "F");
smap.put("1VSOS", "F");
smap.put("1VSOV", "F");
smap.put("1VUE", "F");
smap.put("1VUE;", "F");
smap.put("1VUEC", "F");
smap.put("1VUEK", "F");
smap.put(";TKNC", "F");
smap.put("E(1&(", "F");
smap.put("E(1&1", "F");
smap.put("E(1&F", "F");
smap.put("E(1&N", "F");
smap.put("E(1&S", "F");
smap.put("E(1&V", "F");
smap.put("E(1)&", "F");
smap.put("E(1),", "F");
smap.put("E(1)1", "F");
smap.put("E(1);", "F");
smap.put("E(1)B", "F");
smap.put("E(1)C", "F");
smap.put("E(1)F", "F");
smap.put("E(1)K", "F");
smap.put("E(1)N", "F");
smap.put("E(1)O", "F");
smap.put("E(1)S", "F");
smap.put("E(1)U", "F");
smap.put("E(1)V", "F");
smap.put("E(1,F", "F");
smap.put("E(1F(", "F");
smap.put("E(1N)", "F");
smap.put("E(1O(", "F");
smap.put("E(1OF", "F");
smap.put("E(1OS", "F");
smap.put("E(1OV", "F");
smap.put("E(1S)", "F");
smap.put("E(1V)", "F");
smap.put("E(1VO", "F");
smap.put("E(E(1", "F");
smap.put("E(E(E", "F");
smap.put("E(E(F", "F");
smap.put("E(E(N", "F");
smap.put("E(E(S", "F");
smap.put("E(E(V", "F");
smap.put("E(E1&", "F");
smap.put("E(E1)", "F");
smap.put("E(E1O", "F");
smap.put("E(EF(", "F");
smap.put("E(EK(", "F");
smap.put("E(EK1", "F");
smap.put("E(EKF", "F");
smap.put("E(EKN", "F");
smap.put("E(EKS", "F");
smap.put("E(EKV", "F");
smap.put("E(EN&", "F");
smap.put("E(EN)", "F");
smap.put("E(ENO", "F");
smap.put("E(ES&", "F");
smap.put("E(ES)", "F");
smap.put("E(ESO", "F");
smap.put("E(EV&", "F");
smap.put("E(EV)", "F");
smap.put("E(EVO", "F");
smap.put("E(F()", "F");
smap.put("E(F(1", "F");
smap.put("E(F(E", "F");
smap.put("E(F(F", "F");
smap.put("E(F(N", "F");
smap.put("E(F(S", "F");
smap.put("E(F(V", "F");
smap.put("E(N&(", "F");
smap.put("E(N&1", "F");
smap.put("E(N&F", "F");
smap.put("E(N&N", "F");
smap.put("E(N&S", "F");
smap.put("E(N&V", "F");
smap.put("E(N(1", "F");
smap.put("E(N(F", "F");
smap.put("E(N(S", "F");
smap.put("E(N(V", "F");
smap.put("E(N)&", "F");
smap.put("E(N),", "F");
smap.put("E(N)1", "F");
smap.put("E(N);", "F");
smap.put("E(N)B", "F");
smap.put("E(N)C", "F");
smap.put("E(N)F", "F");
smap.put("E(N)K", "F");
smap.put("E(N)N", "F");
smap.put("E(N)O", "F");
smap.put("E(N)S", "F");
smap.put("E(N)U", "F");
smap.put("E(N)V", "F");
smap.put("E(N,F", "F");
smap.put("E(N1)", "F");
smap.put("E(N1O", "F");
smap.put("E(NF(", "F");
smap.put("E(NO(", "F");
smap.put("E(NOF", "F");
smap.put("E(NOS", "F");
smap.put("E(NOV", "F");
smap.put("E(S&(", "F");
smap.put("E(S&1", "F");
smap.put("E(S&F", "F");
smap.put("E(S&N", "F");
smap.put("E(S&S", "F");
smap.put("E(S&V", "F");
smap.put("E(S)&", "F");
smap.put("E(S),", "F");
smap.put("E(S)1", "F");
smap.put("E(S);", "F");
smap.put("E(S)B", "F");
smap.put("E(S)C", "F");
smap.put("E(S)F", "F");
smap.put("E(S)K", "F");
smap.put("E(S)N", "F");
smap.put("E(S)O", "F");
smap.put("E(S)S", "F");
smap.put("E(S)U", "F");
smap.put("E(S)V", "F");
smap.put("E(S,F", "F");
smap.put("E(S1)", "F");
smap.put("E(S1O", "F");
smap.put("E(SF(", "F");
smap.put("E(SO(", "F");
smap.put("E(SO1", "F");
smap.put("E(SOF", "F");
smap.put("E(SON", "F");
smap.put("E(SOS", "F");
smap.put("E(SOV", "F");
smap.put("E(SV)", "F");
smap.put("E(SVO", "F");
smap.put("E(V&(", "F");
smap.put("E(V&1", "F");
smap.put("E(V&F", "F");
smap.put("E(V&N", "F");
smap.put("E(V&S", "F");
smap.put("E(V&V", "F");
smap.put("E(V)&", "F");
smap.put("E(V),", "F");
smap.put("E(V)1", "F");
smap.put("E(V);", "F");
smap.put("E(V)B", "F");
smap.put("E(V)C", "F");
smap.put("E(V)F", "F");
smap.put("E(V)K", "F");
smap.put("E(V)N", "F");
smap.put("E(V)O", "F");
smap.put("E(V)S", "F");
smap.put("E(V)U", "F");
smap.put("E(V)V", "F");
smap.put("E(V,F", "F");
smap.put("E(VF(", "F");
smap.put("E(VO(", "F");
smap.put("E(VOF", "F");
smap.put("E(VOS", "F");
smap.put("E(VS)", "F");
smap.put("E(VSO", "F");
smap.put("E1&(1", "F");
smap.put("E1&(E", "F");
smap.put("E1&(F", "F");
smap.put("E1&(N", "F");
smap.put("E1&(S", "F");
smap.put("E1&(V", "F");
smap.put("E1&1)", "F");
smap.put("E1&1O", "F");
smap.put("E1&F(", "F");
smap.put("E1&N)", "F");
smap.put("E1&NO", "F");
smap.put("E1&S)", "F");
smap.put("E1&SO", "F");
smap.put("E1&V)", "F");
smap.put("E1&VO", "F");
smap.put("E1)", "F");
smap.put("E1)&(", "F");
smap.put("E1)&1", "F");
smap.put("E1)&F", "F");
smap.put("E1)&N", "F");
smap.put("E1)&S", "F");
smap.put("E1)&V", "F");
smap.put("E1);", "F");
smap.put("E1);(", "F");
smap.put("E1);C", "F");
smap.put("E1);E", "F");
smap.put("E1);T", "F");
smap.put("E1)C", "F");
smap.put("E1)KN", "F");
smap.put("E1)O(", "F");
smap.put("E1)O1", "F");
smap.put("E1)OF", "F");
smap.put("E1)ON", "F");
smap.put("E1)OS", "F");
smap.put("E1)OV", "F");
smap.put("E1)UE", "F");
smap.put("E1,(1", "F");
smap.put("E1,(F", "F");
smap.put("E1,(N", "F");
smap.put("E1,(S", "F");
smap.put("E1,(V", "F");
smap.put("E1,F(", "F");
smap.put("E1;(E", "F");
smap.put("E1B(1", "F");
smap.put("E1B(F", "F");
smap.put("E1B(N", "F");
smap.put("E1B(S", "F");
smap.put("E1B(V", "F");
smap.put("E1B1)", "F");
smap.put("E1B1O", "F");
smap.put("E1BF(", "F");
smap.put("E1BN)", "F");
smap.put("E1BNO", "F");
smap.put("E1BS)", "F");
smap.put("E1BSO", "F");
smap.put("E1BV)", "F");
smap.put("E1BVO", "F");
smap.put("E1F()", "F");
smap.put("E1F(1", "F");
smap.put("E1F(F", "F");
smap.put("E1F(N", "F");
smap.put("E1F(S", "F");
smap.put("E1F(V", "F");
smap.put("E1K(1", "F");
smap.put("E1K(E", "F");
smap.put("E1K(F", "F");
smap.put("E1K(N", "F");
smap.put("E1K(S", "F");
smap.put("E1K(V", "F");
smap.put("E1K1)", "F");
smap.put("E1K1K", "F");
smap.put("E1K1O", "F");
smap.put("E1KF(", "F");
smap.put("E1KN", "F");
smap.put("E1KN)", "F");
smap.put("E1KN;", "F");
smap.put("E1KNC", "F");
smap.put("E1KNK", "F");
smap.put("E1KNU", "F");
smap.put("E1KS)", "F");
smap.put("E1KSK", "F");
smap.put("E1KSO", "F");
smap.put("E1KV)", "F");
smap.put("E1KVK", "F");
smap.put("E1KVO", "F");
smap.put("E1N)U", "F");
smap.put("E1N;", "F");
smap.put("E1N;C", "F");
smap.put("E1NC", "F");
smap.put("E1NKN", "F");
smap.put("E1O(1", "F");
smap.put("E1O(E", "F");
smap.put("E1O(F", "F");
smap.put("E1O(N", "F");
smap.put("E1O(S", "F");
smap.put("E1O(V", "F");
smap.put("E1OF(", "F");
smap.put("E1OS&", "F");
smap.put("E1OS(", "F");
smap.put("E1OS)", "F");
smap.put("E1OS,", "F");
smap.put("E1OS1", "F");
smap.put("E1OS;", "F");
smap.put("E1OSB", "F");
smap.put("E1OSF", "F");
smap.put("E1OSK", "F");
smap.put("E1OSU", "F");
smap.put("E1OSV", "F");
smap.put("E1OV&", "F");
smap.put("E1OV(", "F");
smap.put("E1OV)", "F");
smap.put("E1OV,", "F");
smap.put("E1OV;", "F");
smap.put("E1OVB", "F");
smap.put("E1OVF", "F");
smap.put("E1OVK", "F");
smap.put("E1OVO", "F");
smap.put("E1OVS", "F");
smap.put("E1OVU", "F");
smap.put("E1S;", "F");
smap.put("E1S;C", "F");
smap.put("E1SC", "F");
smap.put("E1U(E", "F");
smap.put("E1UE(", "F");
smap.put("E1UE1", "F");
smap.put("E1UEF", "F");
smap.put("E1UEK", "F");
smap.put("E1UEN", "F");
smap.put("E1UES", "F");
smap.put("E1UEV", "F");
smap.put("E1V", "F");
smap.put("E1V;", "F");
smap.put("E1V;C", "F");
smap.put("E1VC", "F");
smap.put("E1VO(", "F");
smap.put("E1VOF", "F");
smap.put("E1VOS", "F");
smap.put("EE(F(", "F");
smap.put("EEK(F", "F");
smap.put("EF()&", "F");
smap.put("EF(),", "F");
smap.put("EF()1", "F");
smap.put("EF();", "F");
smap.put("EF()B", "F");
smap.put("EF()F", "F");
smap.put("EF()K", "F");
smap.put("EF()N", "F");
smap.put("EF()O", "F");
smap.put("EF()S", "F");
smap.put("EF()U", "F");
smap.put("EF()V", "F");
smap.put("EF(1&", "F");
smap.put("EF(1)", "F");
smap.put("EF(1,", "F");
smap.put("EF(1O", "F");
smap.put("EF(E(", "F");
smap.put("EF(E1", "F");
smap.put("EF(EF", "F");
smap.put("EF(EK", "F");
smap.put("EF(EN", "F");
smap.put("EF(ES", "F");
smap.put("EF(EV", "F");
smap.put("EF(F(", "F");
smap.put("EF(N&", "F");
smap.put("EF(N)", "F");
smap.put("EF(N,", "F");
smap.put("EF(NO", "F");
smap.put("EF(O)", "F");
smap.put("EF(S&", "F");
smap.put("EF(S)", "F");
smap.put("EF(S,", "F");
smap.put("EF(SO", "F");
smap.put("EF(V&", "F");
smap.put("EF(V)", "F");
smap.put("EF(V,", "F");
smap.put("EF(VO", "F");
smap.put("EK(1&", "F");
smap.put("EK(1(", "F");
smap.put("EK(1)", "F");
smap.put("EK(1,", "F");
smap.put("EK(1F", "F");
smap.put("EK(1N", "F");
smap.put("EK(1O", "F");
smap.put("EK(1S", "F");
smap.put("EK(1V", "F");
smap.put("EK(E(", "F");
smap.put("EK(E1", "F");
smap.put("EK(EF", "F");
smap.put("EK(EK", "F");
smap.put("EK(EN", "F");
smap.put("EK(ES", "F");
smap.put("EK(EV", "F");
smap.put("EK(F(", "F");
smap.put("EK(N&", "F");
smap.put("EK(N(", "F");
smap.put("EK(N)", "F");
smap.put("EK(N,", "F");
smap.put("EK(N1", "F");
smap.put("EK(NF", "F");
smap.put("EK(NO", "F");
smap.put("EK(S&", "F");
smap.put("EK(S(", "F");
smap.put("EK(S)", "F");
smap.put("EK(S,", "F");
smap.put("EK(S1", "F");
smap.put("EK(SF", "F");
smap.put("EK(SO", "F");
smap.put("EK(SV", "F");
smap.put("EK(V&", "F");
smap.put("EK(V(", "F");
smap.put("EK(V)", "F");
smap.put("EK(V,", "F");
smap.put("EK(VF", "F");
smap.put("EK(VO", "F");
smap.put("EK(VS", "F");
smap.put("EK1&(", "F");
smap.put("EK1&1", "F");
smap.put("EK1&F", "F");
smap.put("EK1&N", "F");
smap.put("EK1&S", "F");
smap.put("EK1&V", "F");
smap.put("EK1)", "F");
smap.put("EK1)&", "F");
smap.put("EK1);", "F");
smap.put("EK1)C", "F");
smap.put("EK1)K", "F");
smap.put("EK1)O", "F");
smap.put("EK1)U", "F");
smap.put("EK1,(", "F");
smap.put("EK1,F", "F");
smap.put("EK1;(", "F");
smap.put("EK1B(", "F");
smap.put("EK1B1", "F");
smap.put("EK1BF", "F");
smap.put("EK1BN", "F");
smap.put("EK1BS", "F");
smap.put("EK1BV", "F");
smap.put("EK1F(", "F");
smap.put("EK1K(", "F");
smap.put("EK1K1", "F");
smap.put("EK1KF", "F");
smap.put("EK1KN", "F");
smap.put("EK1KS", "F");
smap.put("EK1KV", "F");
smap.put("EK1N", "F");
smap.put("EK1N)", "F");
smap.put("EK1N;", "F");
smap.put("EK1NC", "F");
smap.put("EK1NF", "F");
smap.put("EK1NK", "F");
smap.put("EK1O(", "F");
smap.put("EK1OF", "F");
smap.put("EK1OS", "F");
smap.put("EK1OV", "F");
smap.put("EK1S", "F");
smap.put("EK1S;", "F");
smap.put("EK1SC", "F");
smap.put("EK1SF", "F");
smap.put("EK1SK", "F");
smap.put("EK1U(", "F");
smap.put("EK1UE", "F");
smap.put("EK1V", "F");
smap.put("EK1V;", "F");
smap.put("EK1VC", "F");
smap.put("EK1VF", "F");
smap.put("EK1VK", "F");
smap.put("EK1VO", "F");
smap.put("EKE(F", "F");
smap.put("EKEK(", "F");
smap.put("EKF()", "F");
smap.put("EKF(1", "F");
smap.put("EKF(E", "F");
smap.put("EKF(F", "F");
smap.put("EKF(N", "F");
smap.put("EKF(O", "F");
smap.put("EKF(S", "F");
smap.put("EKF(V", "F");
smap.put("EKN&(", "F");
smap.put("EKN&1", "F");
smap.put("EKN&F", "F");
smap.put("EKN&N", "F");
smap.put("EKN&S", "F");
smap.put("EKN&V", "F");
smap.put("EKN(1", "F");
smap.put("EKN(F", "F");
smap.put("EKN(S", "F");
smap.put("EKN(V", "F");
smap.put("EKN)", "F");
smap.put("EKN)&", "F");
smap.put("EKN);", "F");
smap.put("EKN)C", "F");
smap.put("EKN)K", "F");
smap.put("EKN)O", "F");
smap.put("EKN)U", "F");
smap.put("EKN,(", "F");
smap.put("EKN,F", "F");
smap.put("EKN1", "F");
smap.put("EKN1;", "F");
smap.put("EKN1C", "F");
smap.put("EKN1F", "F");
smap.put("EKN1K", "F");
smap.put("EKN1O", "F");
smap.put("EKN;(", "F");
smap.put("EKNB(", "F");
smap.put("EKNB1", "F");
smap.put("EKNBF", "F");
smap.put("EKNBN", "F");
smap.put("EKNBS", "F");
smap.put("EKNBV", "F");
smap.put("EKNF(", "F");
smap.put("EKNK(", "F");
smap.put("EKNK1", "F");
smap.put("EKNKF", "F");
smap.put("EKNKN", "F");
smap.put("EKNKS", "F");
smap.put("EKNKV", "F");
smap.put("EKNU(", "F");
smap.put("EKNUE", "F");
smap.put("EKO(1", "F");
smap.put("EKO(F", "F");
smap.put("EKO(N", "F");
smap.put("EKO(S", "F");
smap.put("EKO(V", "F");
smap.put("EKOK(", "F");
smap.put("EKOKN", "F");
smap.put("EKS&(", "F");
smap.put("EKS&1", "F");
smap.put("EKS&F", "F");
smap.put("EKS&N", "F");
smap.put("EKS&S", "F");
smap.put("EKS&V", "F");
smap.put("EKS)", "F");
smap.put("EKS)&", "F");
smap.put("EKS);", "F");
smap.put("EKS)C", "F");
smap.put("EKS)K", "F");
smap.put("EKS)O", "F");
smap.put("EKS)U", "F");
smap.put("EKS,(", "F");
smap.put("EKS,F", "F");
smap.put("EKS1", "F");
smap.put("EKS1;", "F");
smap.put("EKS1C", "F");
smap.put("EKS1F", "F");
smap.put("EKS1K", "F");
smap.put("EKS1O", "F");
smap.put("EKS;(", "F");
smap.put("EKSB(", "F");
smap.put("EKSB1", "F");
smap.put("EKSBF", "F");
smap.put("EKSBN", "F");
smap.put("EKSBS", "F");
smap.put("EKSBV", "F");
smap.put("EKSF(", "F");
smap.put("EKSK(", "F");
smap.put("EKSK1", "F");
smap.put("EKSKF", "F");
smap.put("EKSKN", "F");
smap.put("EKSKS", "F");
smap.put("EKSKV", "F");
smap.put("EKSO(", "F");
smap.put("EKSO1", "F");
smap.put("EKSOF", "F");
smap.put("EKSON", "F");
smap.put("EKSOS", "F");
smap.put("EKSOV", "F");
smap.put("EKSU(", "F");
smap.put("EKSUE", "F");
smap.put("EKSV", "F");
smap.put("EKSV;", "F");
smap.put("EKSVC", "F");
smap.put("EKSVF", "F");
smap.put("EKSVK", "F");
smap.put("EKSVO", "F");
smap.put("EKV&(", "F");
smap.put("EKV&1", "F");
smap.put("EKV&F", "F");
smap.put("EKV&N", "F");
smap.put("EKV&S", "F");
smap.put("EKV&V", "F");
smap.put("EKV)", "F");
smap.put("EKV)&", "F");
smap.put("EKV);", "F");
smap.put("EKV)C", "F");
smap.put("EKV)K", "F");
smap.put("EKV)O", "F");
smap.put("EKV)U", "F");
smap.put("EKV,(", "F");
smap.put("EKV,F", "F");
smap.put("EKV;(", "F");
smap.put("EKVB(", "F");
smap.put("EKVB1", "F");
smap.put("EKVBF", "F");
smap.put("EKVBN", "F");
smap.put("EKVBS", "F");
smap.put("EKVBV", "F");
smap.put("EKVF(", "F");
smap.put("EKVK(", "F");
smap.put("EKVK1", "F");
smap.put("EKVKF", "F");
smap.put("EKVKN", "F");
smap.put("EKVKS", "F");
smap.put("EKVKV", "F");
smap.put("EKVO(", "F");
smap.put("EKVOF", "F");
smap.put("EKVOS", "F");
smap.put("EKVS", "F");
smap.put("EKVS;", "F");
smap.put("EKVSC", "F");
smap.put("EKVSF", "F");
smap.put("EKVSK", "F");
smap.put("EKVSO", "F");
smap.put("EKVU(", "F");
smap.put("EKVUE", "F");
smap.put("EN&(1", "F");
smap.put("EN&(E", "F");
smap.put("EN&(F", "F");
smap.put("EN&(N", "F");
smap.put("EN&(S", "F");
smap.put("EN&(V", "F");
smap.put("EN&1)", "F");
smap.put("EN&1O", "F");
smap.put("EN&F(", "F");
smap.put("EN&N)", "F");
smap.put("EN&NO", "F");
smap.put("EN&S)", "F");
smap.put("EN&SO", "F");
smap.put("EN&V)", "F");
smap.put("EN&VO", "F");
smap.put("EN(1O", "F");
smap.put("EN(F(", "F");
smap.put("EN(S)", "F");
smap.put("EN(SO", "F");
smap.put("EN(V)", "F");
smap.put("EN(VO", "F");
smap.put("EN)", "F");
smap.put("EN)&(", "F");
smap.put("EN)&1", "F");
smap.put("EN)&F", "F");
smap.put("EN)&N", "F");
smap.put("EN)&S", "F");
smap.put("EN)&V", "F");
smap.put("EN);", "F");
smap.put("EN);(", "F");
smap.put("EN);C", "F");
smap.put("EN);E", "F");
smap.put("EN);T", "F");
smap.put("EN)C", "F");
smap.put("EN)KN", "F");
smap.put("EN)O(", "F");
smap.put("EN)O1", "F");
smap.put("EN)OF", "F");
smap.put("EN)ON", "F");
smap.put("EN)OS", "F");
smap.put("EN)OV", "F");
smap.put("EN)UE", "F");
smap.put("EN,(1", "F");
smap.put("EN,(F", "F");
smap.put("EN,(N", "F");
smap.put("EN,(S", "F");
smap.put("EN,(V", "F");
smap.put("EN,F(", "F");
smap.put("EN1;", "F");
smap.put("EN1;C", "F");
smap.put("EN1C", "F");
smap.put("EN1O(", "F");
smap.put("EN1OF", "F");
smap.put("EN1OS", "F");
smap.put("EN1OV", "F");
smap.put("EN;(E", "F");
smap.put("ENB(1", "F");
smap.put("ENB(F", "F");
smap.put("ENB(N", "F");
smap.put("ENB(S", "F");
smap.put("ENB(V", "F");
smap.put("ENB1)", "F");
smap.put("ENB1O", "F");
smap.put("ENBF(", "F");
smap.put("ENBN)", "F");
smap.put("ENBNO", "F");
smap.put("ENBS)", "F");
smap.put("ENBSO", "F");
smap.put("ENBV)", "F");
smap.put("ENBVO", "F");
smap.put("ENF()", "F");
smap.put("ENF(1", "F");
smap.put("ENF(F", "F");
smap.put("ENF(N", "F");
smap.put("ENF(S", "F");
smap.put("ENF(V", "F");
smap.put("ENK(1", "F");
smap.put("ENK(E", "F");
smap.put("ENK(F", "F");
smap.put("ENK(N", "F");
smap.put("ENK(S", "F");
smap.put("ENK(V", "F");
smap.put("ENK1)", "F");
smap.put("ENK1K", "F");
smap.put("ENK1O", "F");
smap.put("ENKF(", "F");
smap.put("ENKN)", "F");
smap.put("ENKN,", "F");
smap.put("ENKN;", "F");
smap.put("ENKNB", "F");
smap.put("ENKNC", "F");
smap.put("ENKNK", "F");
smap.put("ENKNU", "F");
smap.put("ENKS)", "F");
smap.put("ENKSK", "F");
smap.put("ENKSO", "F");
smap.put("ENKV)", "F");
smap.put("ENKVK", "F");
smap.put("ENKVO", "F");
smap.put("ENO(1", "F");
smap.put("ENO(E", "F");
smap.put("ENO(F", "F");
smap.put("ENO(N", "F");
smap.put("ENO(S", "F");
smap.put("ENO(V", "F");
smap.put("ENOF(", "F");
smap.put("ENOS&", "F");
smap.put("ENOS(", "F");
smap.put("ENOS)", "F");
smap.put("ENOS,", "F");
smap.put("ENOS1", "F");
smap.put("ENOS;", "F");
smap.put("ENOSB", "F");
smap.put("ENOSF", "F");
smap.put("ENOSK", "F");
smap.put("ENOSU", "F");
smap.put("ENOSV", "F");
smap.put("ENOV&", "F");
smap.put("ENOV(", "F");
smap.put("ENOV)", "F");
smap.put("ENOV,", "F");
smap.put("ENOV;", "F");
smap.put("ENOVB", "F");
smap.put("ENOVF", "F");
smap.put("ENOVK", "F");
smap.put("ENOVO", "F");
smap.put("ENOVS", "F");
smap.put("ENOVU", "F");
smap.put("ENU(E", "F");
smap.put("ENUE(", "F");
smap.put("ENUE1", "F");
smap.put("ENUEF", "F");
smap.put("ENUEK", "F");
smap.put("ENUEN", "F");
smap.put("ENUES", "F");
smap.put("ENUEV", "F");
smap.put("EOK(E", "F");
smap.put("EOKNK", "F");
smap.put("ES&(1", "F");
smap.put("ES&(E", "F");
smap.put("ES&(F", "F");
smap.put("ES&(N", "F");
smap.put("ES&(S", "F");
smap.put("ES&(V", "F");
smap.put("ES&1)", "F");
smap.put("ES&1O", "F");
smap.put("ES&F(", "F");
smap.put("ES&N)", "F");
smap.put("ES&NO", "F");
smap.put("ES&S)", "F");
smap.put("ES&SO", "F");
smap.put("ES&V)", "F");
smap.put("ES&VO", "F");
smap.put("ES)", "F");
smap.put("ES)&(", "F");
smap.put("ES)&1", "F");
smap.put("ES)&F", "F");
smap.put("ES)&N", "F");
smap.put("ES)&S", "F");
smap.put("ES)&V", "F");
smap.put("ES);", "F");
smap.put("ES);(", "F");
smap.put("ES);C", "F");
smap.put("ES);E", "F");
smap.put("ES);T", "F");
smap.put("ES)C", "F");
smap.put("ES)KN", "F");
smap.put("ES)O(", "F");
smap.put("ES)O1", "F");
smap.put("ES)OF", "F");
smap.put("ES)ON", "F");
smap.put("ES)OS", "F");
smap.put("ES)OV", "F");
smap.put("ES)UE", "F");
smap.put("ES,(1", "F");
smap.put("ES,(F", "F");
smap.put("ES,(N", "F");
smap.put("ES,(S", "F");
smap.put("ES,(V", "F");
smap.put("ES,F(", "F");
smap.put("ES1", "F");
smap.put("ES1;", "F");
smap.put("ES1;C", "F");
smap.put("ES1C", "F");
smap.put("ES1O(", "F");
smap.put("ES1OF", "F");
smap.put("ES1OS", "F");
smap.put("ES1OV", "F");
smap.put("ES;(E", "F");
smap.put("ESB(1", "F");
smap.put("ESB(F", "F");
smap.put("ESB(N", "F");
smap.put("ESB(S", "F");
smap.put("ESB(V", "F");
smap.put("ESB1)", "F");
smap.put("ESB1O", "F");
smap.put("ESBF(", "F");
smap.put("ESBN)", "F");
smap.put("ESBNO", "F");
smap.put("ESBS)", "F");
smap.put("ESBSO", "F");
smap.put("ESBV)", "F");
smap.put("ESBVO", "F");
smap.put("ESF()", "F");
smap.put("ESF(1", "F");
smap.put("ESF(F", "F");
smap.put("ESF(N", "F");
smap.put("ESF(S", "F");
smap.put("ESF(V", "F");
smap.put("ESK(1", "F");
smap.put("ESK(E", "F");
smap.put("ESK(F", "F");
smap.put("ESK(N", "F");
smap.put("ESK(S", "F");
smap.put("ESK(V", "F");
smap.put("ESK1)", "F");
smap.put("ESK1K", "F");
smap.put("ESK1O", "F");
smap.put("ESKF(", "F");
smap.put("ESKN", "F");
smap.put("ESKN)", "F");
smap.put("ESKN;", "F");
smap.put("ESKNC", "F");
smap.put("ESKNK", "F");
smap.put("ESKNU", "F");
smap.put("ESKS)", "F");
smap.put("ESKSK", "F");
smap.put("ESKSO", "F");
smap.put("ESKV)", "F");
smap.put("ESKVK", "F");
smap.put("ESKVO", "F");
smap.put("ESO(1", "F");
smap.put("ESO(E", "F");
smap.put("ESO(F", "F");
smap.put("ESO(N", "F");
smap.put("ESO(S", "F");
smap.put("ESO(V", "F");
smap.put("ESO1&", "F");
smap.put("ESO1(", "F");
smap.put("ESO1)", "F");
smap.put("ESO1,", "F");
smap.put("ESO1;", "F");
smap.put("ESO1B", "F");
smap.put("ESO1F", "F");
smap.put("ESO1K", "F");
smap.put("ESO1N", "F");
smap.put("ESO1S", "F");
smap.put("ESO1U", "F");
smap.put("ESO1V", "F");
smap.put("ESOF(", "F");
smap.put("ESON&", "F");
smap.put("ESON(", "F");
smap.put("ESON)", "F");
smap.put("ESON,", "F");
smap.put("ESON1", "F");
smap.put("ESON;", "F");
smap.put("ESONB", "F");
smap.put("ESONF", "F");
smap.put("ESONK", "F");
smap.put("ESONU", "F");
smap.put("ESOS&", "F");
smap.put("ESOS(", "F");
smap.put("ESOS)", "F");
smap.put("ESOS,", "F");
smap.put("ESOS1", "F");
smap.put("ESOS;", "F");
smap.put("ESOSB", "F");
smap.put("ESOSF", "F");
smap.put("ESOSK", "F");
smap.put("ESOSU", "F");
smap.put("ESOSV", "F");
smap.put("ESOV&", "F");
smap.put("ESOV(", "F");
smap.put("ESOV)", "F");
smap.put("ESOV,", "F");
smap.put("ESOV;", "F");
smap.put("ESOVB", "F");
smap.put("ESOVF", "F");
smap.put("ESOVK", "F");
smap.put("ESOVO", "F");
smap.put("ESOVS", "F");
smap.put("ESOVU", "F");
smap.put("ESU(E", "F");
smap.put("ESUE(", "F");
smap.put("ESUE1", "F");
smap.put("ESUEF", "F");
smap.put("ESUEK", "F");
smap.put("ESUEN", "F");
smap.put("ESUES", "F");
smap.put("ESUEV", "F");
smap.put("ESV", "F");
smap.put("ESV;", "F");
smap.put("ESV;C", "F");
smap.put("ESVC", "F");
smap.put("ESVO(", "F");
smap.put("ESVOF", "F");
smap.put("ESVOS", "F");
smap.put("EV&(1", "F");
smap.put("EV&(E", "F");
smap.put("EV&(F", "F");
smap.put("EV&(N", "F");
smap.put("EV&(S", "F");
smap.put("EV&(V", "F");
smap.put("EV&1)", "F");
smap.put("EV&1O", "F");
smap.put("EV&F(", "F");
smap.put("EV&N)", "F");
smap.put("EV&NO", "F");
smap.put("EV&S)", "F");
smap.put("EV&SO", "F");
smap.put("EV&V)", "F");
smap.put("EV&VO", "F");
smap.put("EV)", "F");
smap.put("EV)&(", "F");
smap.put("EV)&1", "F");
smap.put("EV)&F", "F");
smap.put("EV)&N", "F");
smap.put("EV)&S", "F");
smap.put("EV)&V", "F");
smap.put("EV);", "F");
smap.put("EV);(", "F");
smap.put("EV);C", "F");
smap.put("EV);E", "F");
smap.put("EV);T", "F");
smap.put("EV)C", "F");
smap.put("EV)KN", "F");
smap.put("EV)O(", "F");
smap.put("EV)O1", "F");
smap.put("EV)OF", "F");
smap.put("EV)ON", "F");
smap.put("EV)OS", "F");
smap.put("EV)OV", "F");
smap.put("EV)UE", "F");
smap.put("EV,(1", "F");
smap.put("EV,(F", "F");
smap.put("EV,(N", "F");
smap.put("EV,(S", "F");
smap.put("EV,(V", "F");
smap.put("EV,F(", "F");
smap.put("EV;(E", "F");
smap.put("EVB(1", "F");
smap.put("EVB(F", "F");
smap.put("EVB(N", "F");
smap.put("EVB(S", "F");
smap.put("EVB(V", "F");
smap.put("EVB1)", "F");
smap.put("EVB1O", "F");
smap.put("EVBF(", "F");
smap.put("EVBN)", "F");
smap.put("EVBNO", "F");
smap.put("EVBS)", "F");
smap.put("EVBSO", "F");
smap.put("EVBV)", "F");
smap.put("EVBVO", "F");
smap.put("EVF()", "F");
smap.put("EVF(1", "F");
smap.put("EVF(F", "F");
smap.put("EVF(N", "F");
smap.put("EVF(S", "F");
smap.put("EVF(V", "F");
smap.put("EVK(1", "F");
smap.put("EVK(E", "F");
smap.put("EVK(F", "F");
smap.put("EVK(N", "F");
smap.put("EVK(S", "F");
smap.put("EVK(V", "F");
smap.put("EVK1)", "F");
smap.put("EVK1K", "F");
smap.put("EVK1O", "F");
smap.put("EVKF(", "F");
smap.put("EVKN", "F");
smap.put("EVKN)", "F");
smap.put("EVKN;", "F");
smap.put("EVKNC", "F");
smap.put("EVKNK", "F");
smap.put("EVKNU", "F");
smap.put("EVKS)", "F");
smap.put("EVKSK", "F");
smap.put("EVKSO", "F");
smap.put("EVKV)", "F");
smap.put("EVKVK", "F");
smap.put("EVKVO", "F");
smap.put("EVN", "F");
smap.put("EVN)U", "F");
smap.put("EVN;", "F");
smap.put("EVN;C", "F");
smap.put("EVNC", "F");
smap.put("EVNKN", "F");
smap.put("EVNO(", "F");
smap.put("EVNOF", "F");
smap.put("EVNOS", "F");
smap.put("EVNOV", "F");
smap.put("EVO(1", "F");
smap.put("EVO(E", "F");
smap.put("EVO(F", "F");
smap.put("EVO(N", "F");
smap.put("EVO(S", "F");
smap.put("EVO(V", "F");
smap.put("EVOF(", "F");
smap.put("EVOS&", "F");
smap.put("EVOS(", "F");
smap.put("EVOS)", "F");
smap.put("EVOS,", "F");
smap.put("EVOS1", "F");
smap.put("EVOS;", "F");
smap.put("EVOSB", "F");
smap.put("EVOSF", "F");
smap.put("EVOSK", "F");
smap.put("EVOSU", "F");
smap.put("EVOSV", "F");
smap.put("EVS", "F");
smap.put("EVS;", "F");
smap.put("EVS;C", "F");
smap.put("EVSC", "F");
smap.put("EVSO(", "F");
smap.put("EVSO1", "F");
smap.put("EVSOF", "F");
smap.put("EVSON", "F");
smap.put("EVSOS", "F");
smap.put("EVSOV", "F");
smap.put("EVU(E", "F");
smap.put("EVUE(", "F");
smap.put("EVUE1", "F");
smap.put("EVUEF", "F");
smap.put("EVUEK", "F");
smap.put("EVUEN", "F");
smap.put("EVUES", "F");
smap.put("EVUEV", "F");
smap.put("F()&(", "F");
smap.put("F()&1", "F");
smap.put("F()&E", "F");
smap.put("F()&F", "F");
smap.put("F()&K", "F");
smap.put("F()&N", "F");
smap.put("F()&S", "F");
smap.put("F()&V", "F");
smap.put("F(),(", "F");
smap.put("F(),1", "F");
smap.put("F(),F", "F");
smap.put("F(),N", "F");
smap.put("F(),S", "F");
smap.put("F(),V", "F");
smap.put("F()1(", "F");
smap.put("F()1F", "F");
smap.put("F()1N", "F");
smap.put("F()1O", "F");
smap.put("F()1S", "F");
smap.put("F()1U", "F");
smap.put("F()1V", "F");
smap.put("F();E", "F");
smap.put("F();N", "F");
smap.put("F();T", "F");
smap.put("F()A(", "F");
smap.put("F()AF", "F");
smap.put("F()AS", "F");
smap.put("F()AT", "F");
smap.put("F()AV", "F");
smap.put("F()B(", "F");
smap.put("F()B1", "F");
smap.put("F()BE", "F");
smap.put("F()BF", "F");
smap.put("F()BN", "F");
smap.put("F()BS", "F");
smap.put("F()BV", "F");
smap.put("F()C", "F");
smap.put("F()E(", "F");
smap.put("F()E1", "F");
smap.put("F()EF", "F");
smap.put("F()EK", "F");
smap.put("F()EN", "F");
smap.put("F()EO", "F");
smap.put("F()ES", "F");
smap.put("F()EU", "F");
smap.put("F()EV", "F");
smap.put("F()F(", "F");
smap.put("F()K(", "F");
smap.put("F()K)", "F");
smap.put("F()K1", "F");
smap.put("F()KF", "F");
smap.put("F()KN", "F");
smap.put("F()KS", "F");
smap.put("F()KU", "F");
smap.put("F()KV", "F");
smap.put("F()N&", "F");
smap.put("F()N(", "F");
smap.put("F()N)", "F");
smap.put("F()N,", "F");
smap.put("F()N1", "F");
smap.put("F()NE", "F");
smap.put("F()NF", "F");
smap.put("F()NO", "F");
smap.put("F()NU", "F");
smap.put("F()O(", "F");
smap.put("F()O1", "F");
smap.put("F()OF", "F");
smap.put("F()OK", "F");
smap.put("F()ON", "F");
smap.put("F()OS", "F");
smap.put("F()OU", "F");
smap.put("F()OV", "F");
smap.put("F()S(", "F");
smap.put("F()S1", "F");
smap.put("F()SF", "F");
smap.put("F()SO", "F");
smap.put("F()SU", "F");
smap.put("F()SV", "F");
smap.put("F()U", "F");
smap.put("F()U(", "F");
smap.put("F()U1", "F");
smap.put("F()U;", "F");
smap.put("F()UC", "F");
smap.put("F()UE", "F");
smap.put("F()UF", "F");
smap.put("F()UK", "F");
smap.put("F()UN", "F");
smap.put("F()UO", "F");
smap.put("F()US", "F");
smap.put("F()UT", "F");
smap.put("F()UV", "F");
smap.put("F()V(", "F");
smap.put("F()VF", "F");
smap.put("F()VO", "F");
smap.put("F()VS", "F");
smap.put("F()VU", "F");
smap.put("F(1&(", "F");
smap.put("F(1&1", "F");
smap.put("F(1&F", "F");
smap.put("F(1&N", "F");
smap.put("F(1&S", "F");
smap.put("F(1&V", "F");
smap.put("F(1)", "F");
smap.put("F(1)&", "F");
smap.put("F(1),", "F");
smap.put("F(1)1", "F");
smap.put("F(1);", "F");
smap.put("F(1)A", "F");
smap.put("F(1)B", "F");
smap.put("F(1)C", "F");
smap.put("F(1)E", "F");
smap.put("F(1)F", "F");
smap.put("F(1)K", "F");
smap.put("F(1)N", "F");
smap.put("F(1)O", "F");
smap.put("F(1)S", "F");
smap.put("F(1)U", "F");
smap.put("F(1)V", "F");
smap.put("F(1,(", "F");
smap.put("F(1,F", "F");
smap.put("F(1O(", "F");
smap.put("F(1OF", "F");
smap.put("F(1OS", "F");
smap.put("F(1OV", "F");
smap.put("F(E(1", "F");
smap.put("F(E(E", "F");
smap.put("F(E(F", "F");
smap.put("F(E(N", "F");
smap.put("F(E(S", "F");
smap.put("F(E(V", "F");
smap.put("F(E1&", "F");
smap.put("F(E1)", "F");
smap.put("F(E1K", "F");
smap.put("F(E1O", "F");
smap.put("F(EF(", "F");
smap.put("F(EK(", "F");
smap.put("F(EK1", "F");
smap.put("F(EKF", "F");
smap.put("F(EKN", "F");
smap.put("F(EKS", "F");
smap.put("F(EKV", "F");
smap.put("F(EN&", "F");
smap.put("F(EN)", "F");
smap.put("F(ENK", "F");
smap.put("F(ENO", "F");
smap.put("F(ES&", "F");
smap.put("F(ES)", "F");
smap.put("F(ESK", "F");
smap.put("F(ESO", "F");
smap.put("F(EV&", "F");
smap.put("F(EV)", "F");
smap.put("F(EVK", "F");
smap.put("F(EVO", "F");
smap.put("F(F()", "F");
smap.put("F(F(1", "F");
smap.put("F(F(E", "F");
smap.put("F(F(F", "F");
smap.put("F(F(N", "F");
smap.put("F(F(S", "F");
smap.put("F(F(V", "F");
smap.put("F(K()", "F");
smap.put("F(K,(", "F");
smap.put("F(K,F", "F");
smap.put("F(N&(", "F");
smap.put("F(N&1", "F");
smap.put("F(N&F", "F");
smap.put("F(N&N", "F");
smap.put("F(N&S", "F");
smap.put("F(N&V", "F");
smap.put("F(N)", "F");
smap.put("F(N)&", "F");
smap.put("F(N),", "F");
smap.put("F(N)1", "F");
smap.put("F(N);", "F");
smap.put("F(N)A", "F");
smap.put("F(N)B", "F");
smap.put("F(N)C", "F");
smap.put("F(N)E", "F");
smap.put("F(N)F", "F");
smap.put("F(N)K", "F");
smap.put("F(N)N", "F");
smap.put("F(N)O", "F");
smap.put("F(N)S", "F");
smap.put("F(N)U", "F");
smap.put("F(N)V", "F");
smap.put("F(N,(", "F");
smap.put("F(N,F", "F");
smap.put("F(NO(", "F");
smap.put("F(NOF", "F");
smap.put("F(NOS", "F");
smap.put("F(NOV", "F");
smap.put("F(S&(", "F");
smap.put("F(S&1", "F");
smap.put("F(S&F", "F");
smap.put("F(S&N", "F");
smap.put("F(S&S", "F");
smap.put("F(S&V", "F");
smap.put("F(S)", "F");
smap.put("F(S)&", "F");
smap.put("F(S),", "F");
smap.put("F(S)1", "F");
smap.put("F(S);", "F");
smap.put("F(S)A", "F");
smap.put("F(S)B", "F");
smap.put("F(S)C", "F");
smap.put("F(S)E", "F");
smap.put("F(S)F", "F");
smap.put("F(S)K", "F");
smap.put("F(S)N", "F");
smap.put("F(S)O", "F");
smap.put("F(S)S", "F");
smap.put("F(S)U", "F");
smap.put("F(S)V", "F");
smap.put("F(S,(", "F");
smap.put("F(S,F", "F");
smap.put("F(SO(", "F");
smap.put("F(SO1", "F");
smap.put("F(SOF", "F");
smap.put("F(SON", "F");
smap.put("F(SOS", "F");
smap.put("F(SOV", "F");
smap.put("F(T,(", "F");
smap.put("F(T,F", "F");
smap.put("F(V&(", "F");
smap.put("F(V&1", "F");
smap.put("F(V&F", "F");
smap.put("F(V&N", "F");
smap.put("F(V&S", "F");
smap.put("F(V&V", "F");
smap.put("F(V)", "F");
smap.put("F(V)&", "F");
smap.put("F(V),", "F");
smap.put("F(V)1", "F");
smap.put("F(V);", "F");
smap.put("F(V)A", "F");
smap.put("F(V)B", "F");
smap.put("F(V)C", "F");
smap.put("F(V)E", "F");
smap.put("F(V)F", "F");
smap.put("F(V)K", "F");
smap.put("F(V)N", "F");
smap.put("F(V)O", "F");
smap.put("F(V)S", "F");
smap.put("F(V)U", "F");
smap.put("F(V)V", "F");
smap.put("F(V,(", "F");
smap.put("F(V,F", "F");
smap.put("F(VO(", "F");
smap.put("F(VOF", "F");
smap.put("F(VOS", "F");
smap.put("K(1),", "F");
smap.put("K(1)A", "F");
smap.put("K(1)K", "F");
smap.put("K(1)O", "F");
smap.put("K(1O(", "F");
smap.put("K(1OF", "F");
smap.put("K(1OS", "F");
smap.put("K(1OV", "F");
smap.put("K(F()", "F");
smap.put("K(F(1", "F");
smap.put("K(F(F", "F");
smap.put("K(F(N", "F");
smap.put("K(F(S", "F");
smap.put("K(F(V", "F");
smap.put("K(N),", "F");
smap.put("K(N)A", "F");
smap.put("K(N)K", "F");
smap.put("K(N)O", "F");
smap.put("K(NO(", "F");
smap.put("K(NOF", "F");
smap.put("K(NOS", "F");
smap.put("K(NOV", "F");
smap.put("K(S),", "F");
smap.put("K(S)A", "F");
smap.put("K(S)K", "F");
smap.put("K(S)O", "F");
smap.put("K(SO(", "F");
smap.put("K(SO1", "F");
smap.put("K(SOF", "F");
smap.put("K(SON", "F");
smap.put("K(SOS", "F");
smap.put("K(SOV", "F");
smap.put("K(V),", "F");
smap.put("K(V)A", "F");
smap.put("K(V)K", "F");
smap.put("K(V)O", "F");
smap.put("K(VO(", "F");
smap.put("K(VOF", "F");
smap.put("K(VOS", "F");
smap.put("K1,(1", "F");
smap.put("K1,(F", "F");
smap.put("K1,(N", "F");
smap.put("K1,(S", "F");
smap.put("K1,(V", "F");
smap.put("K1,F(", "F");
smap.put("K1A(F", "F");
smap.put("K1A(N", "F");
smap.put("K1A(S", "F");
smap.put("K1A(V", "F");
smap.put("K1AF(", "F");
smap.put("K1ASO", "F");
smap.put("K1AVO", "F");
smap.put("K1K(1", "F");
smap.put("K1K(F", "F");
smap.put("K1K(N", "F");
smap.put("K1K(S", "F");
smap.put("K1K(V", "F");
smap.put("K1K1O", "F");
smap.put("K1K1U", "F");
smap.put("K1KF(", "F");
smap.put("K1KNU", "F");
smap.put("K1KSO", "F");
smap.put("K1KSU", "F");
smap.put("K1KVO", "F");
smap.put("K1KVU", "F");
smap.put("K1O(1", "F");
smap.put("K1O(F", "F");
smap.put("K1O(N", "F");
smap.put("K1O(S", "F");
smap.put("K1O(V", "F");
smap.put("K1OF(", "F");
smap.put("K1OS(", "F");
smap.put("K1OS,", "F");
smap.put("K1OS1", "F");
smap.put("K1OSA", "F");
smap.put("K1OSF", "F");
smap.put("K1OSK", "F");
smap.put("K1OSV", "F");
smap.put("K1OV(", "F");
smap.put("K1OV,", "F");
smap.put("K1OVA", "F");
smap.put("K1OVF", "F");
smap.put("K1OVK", "F");
smap.put("K1OVO", "F");
smap.put("K1OVS", "F");
smap.put("KF(),", "F");
smap.put("KF()A", "F");
smap.put("KF()K", "F");
smap.put("KF()O", "F");
smap.put("KF(1)", "F");
smap.put("KF(1O", "F");
smap.put("KF(F(", "F");
smap.put("KF(N)", "F");
smap.put("KF(NO", "F");
smap.put("KF(S)", "F");
smap.put("KF(SO", "F");
smap.put("KF(V)", "F");
smap.put("KF(VO", "F");
smap.put("KN,(1", "F");
smap.put("KN,(F", "F");
smap.put("KN,(N", "F");
smap.put("KN,(S", "F");
smap.put("KN,(V", "F");
smap.put("KN,F(", "F");
smap.put("KNA(F", "F");
smap.put("KNA(N", "F");
smap.put("KNA(S", "F");
smap.put("KNA(V", "F");
smap.put("KNAF(", "F");
smap.put("KNASO", "F");
smap.put("KNAVO", "F");
smap.put("KNK(1", "F");
smap.put("KNK(F", "F");
smap.put("KNK(N", "F");
smap.put("KNK(S", "F");
smap.put("KNK(V", "F");
smap.put("KNK1O", "F");

initialize2();
initialize3();

keywordOrFunctionSet.add( "USER_ID" );
keywordOrFunctionSet.add( "USER_NAME" );
keywordOrFunctionSet.add( "DATABASE" );
keywordOrFunctionSet.add( "PASSWORD" );
keywordOrFunctionSet.add( "USER" );
keywordOrFunctionSet.add( "CURRENT_USER" );
keywordOrFunctionSet.add( "CURRENT_DATE" );
keywordOrFunctionSet.add( "CURRENT_TIME" );
keywordOrFunctionSet.add( "CURRENT_TIMESTAMP" );
keywordOrFunctionSet.add( "LOCALTIME" );
keywordOrFunctionSet.add( "LOCALTIMESTAMP" );

initKeywordMergeMap();
}
//--------------------------------------------------------------------------------
/* initialize map as
 * UNION=[[UNION, ALL, DISTINCT], [UNION, DISTINCT, ALL], [UNION, ALL], [UNION, DISTINCT]], ...
 */
private static void initKeywordMergeMap()
{
Iterator p = map.keySet().iterator();
while( p.hasNext() )
	{
	final String key = ( String )p.next();
	if( key.indexOf( ' ' ) > -1 )
		{
		final String[] array = key.split( "\\s+" );
		final String firstKeyword = array[ 0 ];
		if( firstKeyword.length() > maxMergedKeywordLength )
			{
			maxMergedKeywordLength = firstKeyword.length();
			}
		List list = ( List )keywordMergeMap.get( firstKeyword );
		if( list == null )
			{
			list = new ArrayList();
			}
		list.add( new ArrayList( Arrays.asList( array ) ) );
		Collections.sort( list, new MListSizeComparator() );
		keywordMergeMap.put( firstKeyword, list );
		}
	}
}
//--------------------------------------------------------------------------------
private static void initialize2()
{
smap.put("KNK1U", "F");
smap.put("KNKF(", "F");
smap.put("KNKNU", "F");
smap.put("KNKSO", "F");
smap.put("KNKSU", "F");
smap.put("KNKVO", "F");
smap.put("KNKVU", "F");
smap.put("KS,(1", "F");
smap.put("KS,(F", "F");
smap.put("KS,(N", "F");
smap.put("KS,(S", "F");
smap.put("KS,(V", "F");
smap.put("KS,F(", "F");
smap.put("KSA(F", "F");
smap.put("KSA(N", "F");
smap.put("KSA(S", "F");
smap.put("KSA(V", "F");
smap.put("KSAF(", "F");
smap.put("KSASO", "F");
smap.put("KSAVO", "F");
smap.put("KSK(1", "F");
smap.put("KSK(F", "F");
smap.put("KSK(N", "F");
smap.put("KSK(S", "F");
smap.put("KSK(V", "F");
smap.put("KSK1O", "F");
smap.put("KSK1U", "F");
smap.put("KSKF(", "F");
smap.put("KSKNU", "F");
smap.put("KSKSO", "F");
smap.put("KSKSU", "F");
smap.put("KSKVO", "F");
smap.put("KSKVU", "F");
smap.put("KSO(1", "F");
smap.put("KSO(F", "F");
smap.put("KSO(N", "F");
smap.put("KSO(S", "F");
smap.put("KSO(V", "F");
smap.put("KSO1(", "F");
smap.put("KSO1,", "F");
smap.put("KSO1A", "F");
smap.put("KSO1F", "F");
smap.put("KSO1K", "F");
smap.put("KSO1N", "F");
smap.put("KSO1S", "F");
smap.put("KSO1V", "F");
smap.put("KSOF(", "F");
smap.put("KSON(", "F");
smap.put("KSON,", "F");
smap.put("KSON1", "F");
smap.put("KSONA", "F");
smap.put("KSONF", "F");
smap.put("KSONK", "F");
smap.put("KSOS(", "F");
smap.put("KSOS,", "F");
smap.put("KSOS1", "F");
smap.put("KSOSA", "F");
smap.put("KSOSF", "F");
smap.put("KSOSK", "F");
smap.put("KSOSV", "F");
smap.put("KSOV(", "F");
smap.put("KSOV,", "F");
smap.put("KSOVA", "F");
smap.put("KSOVF", "F");
smap.put("KSOVK", "F");
smap.put("KSOVO", "F");
smap.put("KSOVS", "F");
smap.put("KV,(1", "F");
smap.put("KV,(F", "F");
smap.put("KV,(N", "F");
smap.put("KV,(S", "F");
smap.put("KV,(V", "F");
smap.put("KV,F(", "F");
smap.put("KVA(F", "F");
smap.put("KVA(N", "F");
smap.put("KVA(S", "F");
smap.put("KVA(V", "F");
smap.put("KVAF(", "F");
smap.put("KVASO", "F");
smap.put("KVAVO", "F");
smap.put("KVK(1", "F");
smap.put("KVK(F", "F");
smap.put("KVK(N", "F");
smap.put("KVK(S", "F");
smap.put("KVK(V", "F");
smap.put("KVK1O", "F");
smap.put("KVK1U", "F");
smap.put("KVKF(", "F");
smap.put("KVKNU", "F");
smap.put("KVKSO", "F");
smap.put("KVKSU", "F");
smap.put("KVKVO", "F");
smap.put("KVKVU", "F");
smap.put("KVO(1", "F");
smap.put("KVO(F", "F");
smap.put("KVO(N", "F");
smap.put("KVO(S", "F");
smap.put("KVO(V", "F");
smap.put("KVOF(", "F");
smap.put("KVOS(", "F");
smap.put("KVOS,", "F");
smap.put("KVOS1", "F");
smap.put("KVOSA", "F");
smap.put("KVOSF", "F");
smap.put("KVOSK", "F");
smap.put("KVOSV", "F");
smap.put("N&(1&", "F");
smap.put("N&(1)", "F");
smap.put("N&(1,", "F");
smap.put("N&(1O", "F");
smap.put("N&(E(", "F");
smap.put("N&(E1", "F");
smap.put("N&(EF", "F");
smap.put("N&(EK", "F");
smap.put("N&(EN", "F");
smap.put("N&(EO", "F");
smap.put("N&(ES", "F");
smap.put("N&(EV", "F");
smap.put("N&(F(", "F");
smap.put("N&(N&", "F");
smap.put("N&(N)", "F");
smap.put("N&(N,", "F");
smap.put("N&(NO", "F");
smap.put("N&(S&", "F");
smap.put("N&(S)", "F");
smap.put("N&(S,", "F");
smap.put("N&(SO", "F");
smap.put("N&(V&", "F");
smap.put("N&(V)", "F");
smap.put("N&(V,", "F");
smap.put("N&(VO", "F");
smap.put("N&1", "F");
smap.put("N&1&(", "F");
smap.put("N&1&1", "F");
smap.put("N&1&F", "F");
smap.put("N&1&N", "F");
smap.put("N&1&S", "F");
smap.put("N&1&V", "F");
smap.put("N&1)&", "F");
smap.put("N&1)C", "F");
smap.put("N&1)O", "F");
smap.put("N&1)U", "F");
smap.put("N&1;", "F");
smap.put("N&1;C", "F");
smap.put("N&1;E", "F");
smap.put("N&1;T", "F");
smap.put("N&1B(", "F");
smap.put("N&1B1", "F");
smap.put("N&1BF", "F");
smap.put("N&1BN", "F");
smap.put("N&1BS", "F");
smap.put("N&1BV", "F");
smap.put("N&1C", "F");
smap.put("N&1EK", "F");
smap.put("N&1EN", "F");
smap.put("N&1F(", "F");
smap.put("N&1K(", "F");
smap.put("N&1K1", "F");
smap.put("N&1KF", "F");
smap.put("N&1KN", "F");
smap.put("N&1KS", "F");
smap.put("N&1KV", "F");
smap.put("N&1O(", "F");
smap.put("N&1OF", "F");
smap.put("N&1OO", "F");
smap.put("N&1OS", "F");
smap.put("N&1OV", "F");
smap.put("N&1TN", "F");
smap.put("N&1U", "F");
smap.put("N&1U(", "F");
smap.put("N&1U;", "F");
smap.put("N&1UC", "F");
smap.put("N&1UE", "F");
smap.put("N&E(1", "F");
smap.put("N&E(F", "F");
smap.put("N&E(N", "F");
smap.put("N&E(O", "F");
smap.put("N&E(S", "F");
smap.put("N&E(V", "F");
smap.put("N&E1", "F");
smap.put("N&E1;", "F");
smap.put("N&E1C", "F");
smap.put("N&E1K", "F");
smap.put("N&E1O", "F");
smap.put("N&EF(", "F");
smap.put("N&EK(", "F");
smap.put("N&EK1", "F");
smap.put("N&EKF", "F");
smap.put("N&EKN", "F");
smap.put("N&EKS", "F");
smap.put("N&EKV", "F");
smap.put("N&EN;", "F");
smap.put("N&ENC", "F");
smap.put("N&ENK", "F");
smap.put("N&ENO", "F");
smap.put("N&ES", "F");
smap.put("N&ES;", "F");
smap.put("N&ESC", "F");
smap.put("N&ESK", "F");
smap.put("N&ESO", "F");
smap.put("N&EV", "F");
smap.put("N&EV;", "F");
smap.put("N&EVC", "F");
smap.put("N&EVK", "F");
smap.put("N&EVO", "F");
smap.put("N&F()", "F");
smap.put("N&F(1", "F");
smap.put("N&F(E", "F");
smap.put("N&F(F", "F");
smap.put("N&F(N", "F");
smap.put("N&F(S", "F");
smap.put("N&F(V", "F");
smap.put("N&K&(", "F");
smap.put("N&K&1", "F");
smap.put("N&K&F", "F");
smap.put("N&K&N", "F");
smap.put("N&K&S", "F");
smap.put("N&K&V", "F");
smap.put("N&K(1", "F");
smap.put("N&K(F", "F");
smap.put("N&K(N", "F");
smap.put("N&K(S", "F");
smap.put("N&K(V", "F");
smap.put("N&K1O", "F");
smap.put("N&KF(", "F");
smap.put("N&KNK", "F");
smap.put("N&KO(", "F");
smap.put("N&KO1", "F");
smap.put("N&KOF", "F");
smap.put("N&KOK", "F");
smap.put("N&KON", "F");
smap.put("N&KOS", "F");
smap.put("N&KOV", "F");
smap.put("N&KSO", "F");
smap.put("N&KVO", "F");
smap.put("N&N&(", "F");
smap.put("N&N&1", "F");
smap.put("N&N&F", "F");
smap.put("N&N&S", "F");
smap.put("N&N&V", "F");
smap.put("N&N)&", "F");
smap.put("N&N)C", "F");
smap.put("N&N)O", "F");
smap.put("N&N)U", "F");
smap.put("N&N;C", "F");
smap.put("N&N;E", "F");
smap.put("N&N;T", "F");
smap.put("N&NB(", "F");
smap.put("N&NB1", "F");
smap.put("N&NBF", "F");
smap.put("N&NBN", "F");
smap.put("N&NBS", "F");
smap.put("N&NBV", "F");
smap.put("N&NF(", "F");
smap.put("N&NK(", "F");
smap.put("N&NK1", "F");
smap.put("N&NKF", "F");
smap.put("N&NKS", "F");
smap.put("N&NKV", "F");
smap.put("N&NO(", "F");
smap.put("N&NOF", "F");
smap.put("N&NOS", "F");
smap.put("N&NOV", "F");
smap.put("N&NU", "F");
smap.put("N&NU(", "F");
smap.put("N&NU;", "F");
smap.put("N&NUC", "F");
smap.put("N&NUE", "F");
smap.put("N&S&(", "F");
smap.put("N&S&1", "F");
smap.put("N&S&F", "F");
smap.put("N&S&N", "F");
smap.put("N&S&S", "F");
smap.put("N&S&V", "F");
smap.put("N&S)&", "F");
smap.put("N&S)C", "F");
smap.put("N&S)O", "F");
smap.put("N&S)U", "F");
smap.put("N&S1", "F");
smap.put("N&S1;", "F");
smap.put("N&S1C", "F");
smap.put("N&S1O", "F");
smap.put("N&S;", "F");
smap.put("N&S;C", "F");
smap.put("N&S;E", "F");
smap.put("N&S;T", "F");
smap.put("N&SB(", "F");
smap.put("N&SB1", "F");
smap.put("N&SBF", "F");
smap.put("N&SBN", "F");
smap.put("N&SBS", "F");
smap.put("N&SBV", "F");
smap.put("N&SC", "F");
smap.put("N&SEK", "F");
smap.put("N&SEN", "F");
smap.put("N&SF(", "F");
smap.put("N&SK(", "F");
smap.put("N&SK1", "F");
smap.put("N&SKF", "F");
smap.put("N&SKN", "F");
smap.put("N&SKS", "F");
smap.put("N&SKV", "F");
smap.put("N&SO(", "F");
smap.put("N&SO1", "F");
smap.put("N&SOF", "F");
smap.put("N&SON", "F");
smap.put("N&SOO", "F");
smap.put("N&SOS", "F");
smap.put("N&SOV", "F");
smap.put("N&STN", "F");
smap.put("N&SU", "F");
smap.put("N&SU(", "F");
smap.put("N&SU;", "F");
smap.put("N&SUC", "F");
smap.put("N&SUE", "F");
smap.put("N&SV", "F");
smap.put("N&SV;", "F");
smap.put("N&SVC", "F");
smap.put("N&SVO", "F");
smap.put("N&V", "F");
smap.put("N&V&(", "F");
smap.put("N&V&1", "F");
smap.put("N&V&F", "F");
smap.put("N&V&N", "F");
smap.put("N&V&S", "F");
smap.put("N&V&V", "F");
smap.put("N&V)&", "F");
smap.put("N&V)C", "F");
smap.put("N&V)O", "F");
smap.put("N&V)U", "F");
smap.put("N&V;", "F");
smap.put("N&V;C", "F");
smap.put("N&V;E", "F");
smap.put("N&V;T", "F");
smap.put("N&VB(", "F");
smap.put("N&VB1", "F");
smap.put("N&VBF", "F");
smap.put("N&VBN", "F");
smap.put("N&VBS", "F");
smap.put("N&VBV", "F");
smap.put("N&VC", "F");
smap.put("N&VEK", "F");
smap.put("N&VEN", "F");
smap.put("N&VF(", "F");
smap.put("N&VK(", "F");
smap.put("N&VK1", "F");
smap.put("N&VKF", "F");
smap.put("N&VKN", "F");
smap.put("N&VKS", "F");
smap.put("N&VKV", "F");
smap.put("N&VO(", "F");
smap.put("N&VOF", "F");
smap.put("N&VOO", "F");
smap.put("N&VOS", "F");
smap.put("N&VS", "F");
smap.put("N&VS;", "F");
smap.put("N&VSC", "F");
smap.put("N&VSO", "F");
smap.put("N&VTN", "F");
smap.put("N&VU", "F");
smap.put("N&VU(", "F");
smap.put("N&VU;", "F");
smap.put("N&VUC", "F");
smap.put("N&VUE", "F");
smap.put("N(1)F", "F");
smap.put("N(1)O", "F");
smap.put("N(1)U", "F");
smap.put("N(1)V", "F");
smap.put("N(1O(", "F");
smap.put("N(1OF", "F");
smap.put("N(1OS", "F");
smap.put("N(1OV", "F");
smap.put("N(EF(", "F");
smap.put("N(EKF", "F");
smap.put("N(EKN", "F");
smap.put("N(ENK", "F");
smap.put("N(F()", "F");
smap.put("N(F(1", "F");
smap.put("N(F(F", "F");
smap.put("N(F(N", "F");
smap.put("N(F(S", "F");
smap.put("N(F(V", "F");
smap.put("N(S)1", "F");
smap.put("N(S)F", "F");
smap.put("N(S)N", "F");
smap.put("N(S)O", "F");
smap.put("N(S)S", "F");
smap.put("N(S)U", "F");
smap.put("N(S)V", "F");
smap.put("N(SO(", "F");
smap.put("N(SO1", "F");
smap.put("N(SOF", "F");
smap.put("N(SON", "F");
smap.put("N(SOS", "F");
smap.put("N(SOV", "F");
smap.put("N(U(E", "F");
smap.put("N(V)1", "F");
smap.put("N(V)F", "F");
smap.put("N(V)N", "F");
smap.put("N(V)O", "F");
smap.put("N(V)S", "F");
smap.put("N(V)U", "F");
smap.put("N(V)V", "F");
smap.put("N(VO(", "F");
smap.put("N(VOF", "F");
smap.put("N(VOS", "F");
smap.put("N)&(1", "F");
smap.put("N)&(E", "F");
smap.put("N)&(F", "F");
smap.put("N)&(N", "F");
smap.put("N)&(S", "F");
smap.put("N)&(V", "F");
smap.put("N)&1", "F");
smap.put("N)&1&", "F");
smap.put("N)&1)", "F");
smap.put("N)&1;", "F");
smap.put("N)&1B", "F");
smap.put("N)&1C", "F");
smap.put("N)&1F", "F");
smap.put("N)&1O", "F");
smap.put("N)&1U", "F");
smap.put("N)&F(", "F");
smap.put("N)&N", "F");
smap.put("N)&N&", "F");
smap.put("N)&N)", "F");
smap.put("N)&N;", "F");
smap.put("N)&NB", "F");
smap.put("N)&NC", "F");
smap.put("N)&NF", "F");
smap.put("N)&NO", "F");
smap.put("N)&NU", "F");
smap.put("N)&S", "F");
smap.put("N)&S&", "F");
smap.put("N)&S)", "F");
smap.put("N)&S;", "F");
smap.put("N)&SB", "F");
smap.put("N)&SC", "F");
smap.put("N)&SF", "F");
smap.put("N)&SO", "F");
smap.put("N)&SU", "F");
smap.put("N)&V", "F");
smap.put("N)&V&", "F");
smap.put("N)&V)", "F");
smap.put("N)&V;", "F");
smap.put("N)&VB", "F");
smap.put("N)&VC", "F");
smap.put("N)&VF", "F");
smap.put("N)&VO", "F");
smap.put("N)&VU", "F");
smap.put("N),(1", "F");
smap.put("N),(F", "F");
smap.put("N),(N", "F");
smap.put("N),(S", "F");
smap.put("N),(V", "F");
smap.put("N);E(", "F");
smap.put("N);E1", "F");
smap.put("N);EF", "F");
smap.put("N);EK", "F");
smap.put("N);EN", "F");
smap.put("N);EO", "F");
smap.put("N);ES", "F");
smap.put("N);EV", "F");
smap.put("N);T(", "F");
smap.put("N);T1", "F");
smap.put("N);TF", "F");
smap.put("N);TK", "F");
smap.put("N);TN", "F");
smap.put("N);TO", "F");
smap.put("N);TS", "F");
smap.put("N);TV", "F");
smap.put("N)B(1", "F");
smap.put("N)B(F", "F");
smap.put("N)B(N", "F");
smap.put("N)B(S", "F");
smap.put("N)B(V", "F");
smap.put("N)B1", "F");
smap.put("N)B1&", "F");
smap.put("N)B1;", "F");
smap.put("N)B1C", "F");
smap.put("N)B1K", "F");
smap.put("N)B1N", "F");
smap.put("N)B1O", "F");
smap.put("N)B1U", "F");
smap.put("N)BF(", "F");
smap.put("N)BN", "F");
smap.put("N)BN&", "F");
smap.put("N)BN;", "F");
smap.put("N)BNC", "F");
smap.put("N)BNK", "F");
smap.put("N)BNO", "F");
smap.put("N)BNU", "F");
smap.put("N)BS", "F");
smap.put("N)BS&", "F");
smap.put("N)BS;", "F");
smap.put("N)BSC", "F");
smap.put("N)BSK", "F");
smap.put("N)BSO", "F");
smap.put("N)BSU", "F");
smap.put("N)BV", "F");
smap.put("N)BV&", "F");
smap.put("N)BV;", "F");
smap.put("N)BVC", "F");
smap.put("N)BVK", "F");
smap.put("N)BVO", "F");
smap.put("N)BVU", "F");
smap.put("N)E(1", "F");
smap.put("N)E(F", "F");
smap.put("N)E(N", "F");
smap.put("N)E(S", "F");
smap.put("N)E(V", "F");
smap.put("N)E1C", "F");
smap.put("N)E1O", "F");
smap.put("N)EF(", "F");
smap.put("N)EK(", "F");
smap.put("N)EK1", "F");
smap.put("N)EKF", "F");
smap.put("N)EKN", "F");
smap.put("N)EKS", "F");
smap.put("N)EKV", "F");
smap.put("N)ENC", "F");
smap.put("N)ENO", "F");
smap.put("N)ESC", "F");
smap.put("N)ESO", "F");
smap.put("N)EVC", "F");
smap.put("N)EVO", "F");
smap.put("N)K(1", "F");
smap.put("N)K(F", "F");
smap.put("N)K(N", "F");
smap.put("N)K(S", "F");
smap.put("N)K(V", "F");
smap.put("N)K1&", "F");
smap.put("N)K1;", "F");
smap.put("N)K1B", "F");
smap.put("N)K1E", "F");
smap.put("N)K1O", "F");
smap.put("N)K1U", "F");
smap.put("N)KB(", "F");
smap.put("N)KB1", "F");
smap.put("N)KBF", "F");
smap.put("N)KBN", "F");
smap.put("N)KBS", "F");
smap.put("N)KBV", "F");
smap.put("N)KF(", "F");
smap.put("N)KN&", "F");
smap.put("N)KN;", "F");
smap.put("N)KNB", "F");
smap.put("N)KNE", "F");
smap.put("N)KNK", "F");
smap.put("N)KNU", "F");
smap.put("N)KS&", "F");
smap.put("N)KS;", "F");
smap.put("N)KSB", "F");
smap.put("N)KSE", "F");
smap.put("N)KSO", "F");
smap.put("N)KSU", "F");
smap.put("N)KUE", "F");
smap.put("N)KV&", "F");
smap.put("N)KV;", "F");
smap.put("N)KVB", "F");
smap.put("N)KVE", "F");
smap.put("N)KVO", "F");
smap.put("N)KVU", "F");
smap.put("N)O(1", "F");
smap.put("N)O(E", "F");
smap.put("N)O(F", "F");
smap.put("N)O(N", "F");
smap.put("N)O(S", "F");
smap.put("N)O(V", "F");
smap.put("N)O1&", "F");
smap.put("N)O1)", "F");
smap.put("N)O1;", "F");
smap.put("N)O1B", "F");
smap.put("N)O1C", "F");
smap.put("N)O1K", "F");
smap.put("N)O1U", "F");
smap.put("N)OF(", "F");
smap.put("N)ON", "F");
smap.put("N)ON&", "F");
smap.put("N)ON)", "F");
smap.put("N)ON;", "F");
smap.put("N)ONB", "F");
smap.put("N)ONC", "F");
smap.put("N)ONK", "F");
smap.put("N)ONU", "F");
smap.put("N)OS", "F");
smap.put("N)OS&", "F");
smap.put("N)OS)", "F");
smap.put("N)OS;", "F");
smap.put("N)OSB", "F");
smap.put("N)OSC", "F");
smap.put("N)OSK", "F");
smap.put("N)OSU", "F");
smap.put("N)OV", "F");
smap.put("N)OV&", "F");
smap.put("N)OV)", "F");
smap.put("N)OV;", "F");
smap.put("N)OVB", "F");
smap.put("N)OVC", "F");
smap.put("N)OVK", "F");
smap.put("N)OVO", "F");
smap.put("N)OVU", "F");
smap.put("N)U(E", "F");
smap.put("N)UE(", "F");
smap.put("N)UE1", "F");
smap.put("N)UEF", "F");
smap.put("N)UEK", "F");
smap.put("N)UEN", "F");
smap.put("N)UES", "F");
smap.put("N)UEV", "F");
smap.put("N,(1)", "F");
smap.put("N,(1O", "F");
smap.put("N,(E(", "F");
smap.put("N,(E1", "F");
smap.put("N,(EF", "F");
smap.put("N,(EK", "F");
smap.put("N,(EN", "F");
smap.put("N,(ES", "F");
smap.put("N,(EV", "F");
smap.put("N,(F(", "F");
smap.put("N,(NO", "F");
smap.put("N,(S)", "F");
smap.put("N,(SO", "F");
smap.put("N,(V)", "F");
smap.put("N,(VO", "F");
smap.put("N,F()", "F");
smap.put("N,F(1", "F");
smap.put("N,F(F", "F");
smap.put("N,F(N", "F");
smap.put("N,F(S", "F");
smap.put("N,F(V", "F");
smap.put("N1F()", "F");
smap.put("N1F(1", "F");
smap.put("N1F(F", "F");
smap.put("N1F(N", "F");
smap.put("N1F(S", "F");
smap.put("N1F(V", "F");
smap.put("N1O(1", "F");
smap.put("N1O(F", "F");
smap.put("N1O(N", "F");
smap.put("N1O(S", "F");
smap.put("N1O(V", "F");
smap.put("N1OF(", "F");
smap.put("N1OS(", "F");
smap.put("N1OS1", "F");
smap.put("N1OSF", "F");
smap.put("N1OSU", "F");
smap.put("N1OSV", "F");
smap.put("N1OV(", "F");
smap.put("N1OVF", "F");
smap.put("N1OVO", "F");
smap.put("N1OVS", "F");
smap.put("N1OVU", "F");
smap.put("N1S;", "F");
smap.put("N1S;C", "F");
smap.put("N1SC", "F");
smap.put("N1UE", "F");
smap.put("N1UE;", "F");
smap.put("N1UEC", "F");
smap.put("N1UEK", "F");
smap.put("N1V;", "F");
smap.put("N1V;C", "F");
smap.put("N1VC", "F");
smap.put("N1VO(", "F");
smap.put("N1VOF", "F");
smap.put("N1VOS", "F");
smap.put("N;E(1", "F");
smap.put("N;E(E", "F");
smap.put("N;E(F", "F");
smap.put("N;E(N", "F");
smap.put("N;E(S", "F");
smap.put("N;E(V", "F");
smap.put("N;E1,", "F");
smap.put("N;E1;", "F");
smap.put("N;E1C", "F");
smap.put("N;E1K", "F");
smap.put("N;E1O", "F");
smap.put("N;E1T", "F");
smap.put("N;EF(", "F");
smap.put("N;EK(", "F");
smap.put("N;EK1", "F");
smap.put("N;EKF", "F");
smap.put("N;EKN", "F");
smap.put("N;EKO", "F");
smap.put("N;EKS", "F");
smap.put("N;EKV", "F");
smap.put("N;EN,", "F");
smap.put("N;EN;", "F");
smap.put("N;ENC", "F");
smap.put("N;ENE", "F");
smap.put("N;ENK", "F");
smap.put("N;ENO", "F");
smap.put("N;ENT", "F");
smap.put("N;ES,", "F");
smap.put("N;ES;", "F");
smap.put("N;ESC", "F");
smap.put("N;ESK", "F");
smap.put("N;ESO", "F");
smap.put("N;EST", "F");
smap.put("N;EV,", "F");
smap.put("N;EV;", "F");
smap.put("N;EVC", "F");
smap.put("N;EVK", "F");
smap.put("N;EVO", "F");
smap.put("N;EVT", "F");
smap.put("N;N:T", "F");
smap.put("N;T(1", "F");
smap.put("N;T(E", "F");
smap.put("N;T(F", "F");
smap.put("N;T(N", "F");
smap.put("N;T(S", "F");
smap.put("N;T(V", "F");
smap.put("N;T1,", "F");
smap.put("N;T1;", "F");
smap.put("N;T1C", "F");
smap.put("N;T1F", "F");
smap.put("N;T1K", "F");
smap.put("N;T1O", "F");
smap.put("N;T1T", "F");
smap.put("N;T;", "F");
smap.put("N;T;C", "F");
smap.put("N;TF(", "F");
smap.put("N;TK(", "F");
smap.put("N;TK1", "F");
smap.put("N;TKF", "F");
smap.put("N;TKK", "F");
smap.put("N;TKN", "F");
smap.put("N;TKO", "F");
smap.put("N;TKS", "F");
smap.put("N;TKV", "F");
smap.put("N;TN(", "F");
smap.put("N;TN,", "F");
smap.put("N;TN1", "F");
smap.put("N;TN;", "F");
smap.put("N;TNC", "F");
smap.put("N;TNE", "F");
smap.put("N;TNF", "F");
smap.put("N;TNK", "F");
smap.put("N;TNN", "F");
smap.put("N;TNO", "F");
smap.put("N;TNS", "F");
smap.put("N;TNT", "F");
smap.put("N;TNV", "F");
smap.put("N;TO(", "F");
smap.put("N;TS,", "F");
smap.put("N;TS;", "F");
smap.put("N;TSC", "F");
smap.put("N;TSF", "F");
smap.put("N;TSK", "F");
smap.put("N;TSO", "F");
smap.put("N;TST", "F");
smap.put("N;TT(", "F");
smap.put("N;TT1", "F");
smap.put("N;TTF", "F");
smap.put("N;TTN", "F");
smap.put("N;TTS", "F");
smap.put("N;TTV", "F");
smap.put("N;TV,", "F");
smap.put("N;TV;", "F");
smap.put("N;TVC", "F");
smap.put("N;TVF", "F");
smap.put("N;TVK", "F");
smap.put("N;TVO", "F");
smap.put("N;TVT", "F");
smap.put("NA(F(", "F");
smap.put("NA(N)", "F");
smap.put("NA(NO", "F");
smap.put("NA(S)", "F");
smap.put("NA(SO", "F");
smap.put("NA(V)", "F");
smap.put("NA(VO", "F");
smap.put("NAF()", "F");
smap.put("NAF(1", "F");
smap.put("NAF(F", "F");
smap.put("NAF(N", "F");
smap.put("NAF(S", "F");
smap.put("NAF(V", "F");
smap.put("NASO(", "F");
smap.put("NASO1", "F");
smap.put("NASOF", "F");
smap.put("NASON", "F");
smap.put("NASOS", "F");
smap.put("NASOV", "F");
smap.put("NASUE", "F");
smap.put("NATO(", "F");
smap.put("NATO1", "F");
smap.put("NATOF", "F");
smap.put("NATON", "F");
smap.put("NATOS", "F");
smap.put("NATOV", "F");
smap.put("NATUE", "F");
smap.put("NAVO(", "F");
smap.put("NAVOF", "F");
smap.put("NAVOS", "F");
smap.put("NAVUE", "F");
smap.put("NB(1)", "F");
smap.put("NB(1O", "F");
smap.put("NB(F(", "F");
smap.put("NB(N)", "F");
smap.put("NB(NO", "F");
smap.put("NB(S)", "F");
smap.put("NB(SO", "F");
smap.put("NB(V)", "F");
smap.put("NB(VO", "F");
smap.put("NB1", "F");
smap.put("NB1&(", "F");
smap.put("NB1&1", "F");
smap.put("NB1&F", "F");
smap.put("NB1&N", "F");
smap.put("NB1&S", "F");
smap.put("NB1&V", "F");
smap.put("NB1,(", "F");
smap.put("NB1,F", "F");
smap.put("NB1;", "F");
smap.put("NB1;C", "F");
smap.put("NB1B(", "F");
smap.put("NB1B1", "F");
smap.put("NB1BF", "F");
smap.put("NB1BN", "F");
smap.put("NB1BS", "F");
smap.put("NB1BV", "F");
smap.put("NB1C", "F");
smap.put("NB1K(", "F");
smap.put("NB1K1", "F");
smap.put("NB1KF", "F");
smap.put("NB1KN", "F");
smap.put("NB1KS", "F");
smap.put("NB1KV", "F");
smap.put("NB1O(", "F");
smap.put("NB1OF", "F");
smap.put("NB1OS", "F");
smap.put("NB1OV", "F");
smap.put("NB1U(", "F");
smap.put("NB1UE", "F");
smap.put("NBE(1", "F");
smap.put("NBE(F", "F");
smap.put("NBE(N", "F");
smap.put("NBE(S", "F");
smap.put("NBE(V", "F");
smap.put("NBEK(", "F");
smap.put("NBF()", "F");
smap.put("NBF(1", "F");
smap.put("NBF(F", "F");
smap.put("NBF(N", "F");
smap.put("NBF(S", "F");
smap.put("NBF(V", "F");
smap.put("NBN&(", "F");
smap.put("NBN&1", "F");
smap.put("NBN&F", "F");
smap.put("NBN&N", "F");
smap.put("NBN&S", "F");
smap.put("NBN&V", "F");
smap.put("NBN,(", "F");
smap.put("NBN,F", "F");
smap.put("NBN;", "F");
smap.put("NBN;C", "F");
smap.put("NBNB(", "F");
smap.put("NBNB1", "F");
smap.put("NBNBF", "F");
smap.put("NBNBN", "F");
smap.put("NBNBS", "F");
smap.put("NBNBV", "F");
smap.put("NBNC", "F");
smap.put("NBNK(", "F");
smap.put("NBNK1", "F");
smap.put("NBNKF", "F");
smap.put("NBNKN", "F");
smap.put("NBNKS", "F");
smap.put("NBNKV", "F");
smap.put("NBNO(", "F");
smap.put("NBNOF", "F");
smap.put("NBNOS", "F");
smap.put("NBNOV", "F");
smap.put("NBNU(", "F");
smap.put("NBNUE", "F");
smap.put("NBS", "F");
smap.put("NBS&(", "F");
smap.put("NBS&1", "F");
smap.put("NBS&F", "F");
smap.put("NBS&N", "F");
smap.put("NBS&S", "F");
smap.put("NBS&V", "F");
smap.put("NBS,(", "F");
smap.put("NBS,F", "F");
smap.put("NBS;", "F");
smap.put("NBS;C", "F");
smap.put("NBSB(", "F");
smap.put("NBSB1", "F");
smap.put("NBSBF", "F");
smap.put("NBSBN", "F");
smap.put("NBSBS", "F");
smap.put("NBSBV", "F");
smap.put("NBSC", "F");
smap.put("NBSK(", "F");
smap.put("NBSK1", "F");
smap.put("NBSKF", "F");
smap.put("NBSKN", "F");
smap.put("NBSKS", "F");
smap.put("NBSKV", "F");
smap.put("NBSO(", "F");
smap.put("NBSO1", "F");
smap.put("NBSOF", "F");
smap.put("NBSON", "F");
smap.put("NBSOS", "F");
smap.put("NBSOV", "F");
smap.put("NBSU(", "F");
smap.put("NBSUE", "F");
smap.put("NBV", "F");
smap.put("NBV&(", "F");
smap.put("NBV&1", "F");
smap.put("NBV&F", "F");
smap.put("NBV&N", "F");
smap.put("NBV&S", "F");
smap.put("NBV&V", "F");
smap.put("NBV,(", "F");
smap.put("NBV,F", "F");
smap.put("NBV;", "F");
smap.put("NBV;C", "F");
smap.put("NBVB(", "F");
smap.put("NBVB1", "F");
smap.put("NBVBF", "F");
smap.put("NBVBN", "F");
smap.put("NBVBS", "F");
smap.put("NBVBV", "F");
smap.put("NBVC", "F");
smap.put("NBVK(", "F");
smap.put("NBVK1", "F");
smap.put("NBVKF", "F");
smap.put("NBVKN", "F");
smap.put("NBVKS", "F");
smap.put("NBVKV", "F");
smap.put("NBVO(", "F");
smap.put("NBVOF", "F");
smap.put("NBVOS", "F");
smap.put("NBVU(", "F");
smap.put("NBVUE", "F");
smap.put("NC", "F");
smap.put("NE(1)", "F");
smap.put("NE(1O", "F");
smap.put("NE(F(", "F");
smap.put("NE(N)", "F");
smap.put("NE(NO", "F");
smap.put("NE(S)", "F");
smap.put("NE(SO", "F");
smap.put("NE(V)", "F");
smap.put("NE(VO", "F");
smap.put("NE1C", "F");
smap.put("NE1O(", "F");
smap.put("NE1OF", "F");
smap.put("NE1OS", "F");
smap.put("NE1OV", "F");
smap.put("NE1UE", "F");
smap.put("NEF()", "F");
smap.put("NEF(1", "F");
smap.put("NEF(F", "F");
smap.put("NEF(N", "F");
smap.put("NEF(S", "F");
smap.put("NEF(V", "F");
smap.put("NENC", "F");
smap.put("NENO(", "F");
smap.put("NENOF", "F");
smap.put("NENOS", "F");
smap.put("NENOV", "F");
smap.put("NENUE", "F");
smap.put("NEOKN", "F");
smap.put("NESC", "F");
smap.put("NESO(", "F");
smap.put("NESO1", "F");
smap.put("NESOF", "F");
smap.put("NESON", "F");
smap.put("NESOS", "F");
smap.put("NESOV", "F");
smap.put("NESUE", "F");
smap.put("NEU(1", "F");
smap.put("NEU(F", "F");
smap.put("NEU(N", "F");
smap.put("NEU(S", "F");
smap.put("NEU(V", "F");
smap.put("NEU1,", "F");
smap.put("NEU1C", "F");
smap.put("NEU1O", "F");
smap.put("NEUEF", "F");
smap.put("NEUEK", "F");
smap.put("NEUF(", "F");
smap.put("NEUN,", "F");
smap.put("NEUNC", "F");
smap.put("NEUNO", "F");
smap.put("NEUS,", "F");
smap.put("NEUSC", "F");
smap.put("NEUSO", "F");
smap.put("NEUV,", "F");
smap.put("NEUVC", "F");
smap.put("NEUVO", "F");
smap.put("NEVC", "F");
smap.put("NEVO(", "F");
smap.put("NEVOF", "F");
smap.put("NEVOS", "F");
smap.put("NEVUE", "F");
smap.put("NF()1", "F");
smap.put("NF()F", "F");
smap.put("NF()K", "F");
smap.put("NF()N", "F");
smap.put("NF()O", "F");
smap.put("NF()S", "F");
smap.put("NF()U", "F");
smap.put("NF()V", "F");
smap.put("NF(1)", "F");
smap.put("NF(1N", "F");
smap.put("NF(1O", "F");
smap.put("NF(E(", "F");
smap.put("NF(E1", "F");
smap.put("NF(EF", "F");
smap.put("NF(EK", "F");
smap.put("NF(EN", "F");
smap.put("NF(ES", "F");
smap.put("NF(EV", "F");
smap.put("NF(F(", "F");
smap.put("NF(N)", "F");
smap.put("NF(N,", "F");
smap.put("NF(NO", "F");
smap.put("NF(S)", "F");
smap.put("NF(SO", "F");
smap.put("NF(V)", "F");
smap.put("NF(VO", "F");
smap.put("NK(1)", "F");
smap.put("NK(1O", "F");
smap.put("NK(F(", "F");
smap.put("NK(N)", "F");
smap.put("NK(NO", "F");
smap.put("NK(S)", "F");
smap.put("NK(SO", "F");
smap.put("NK(V)", "F");
smap.put("NK(VO", "F");
smap.put("NK)&(", "F");
smap.put("NK)&1", "F");
smap.put("NK)&F", "F");
smap.put("NK)&N", "F");
smap.put("NK)&S", "F");
smap.put("NK)&V", "F");
smap.put("NK);E", "F");
smap.put("NK);T", "F");
smap.put("NK)B(", "F");
smap.put("NK)B1", "F");
smap.put("NK)BF", "F");
smap.put("NK)BN", "F");
smap.put("NK)BS", "F");
smap.put("NK)BV", "F");
smap.put("NK)E(", "F");
smap.put("NK)E1", "F");
smap.put("NK)EF", "F");
smap.put("NK)EK", "F");
smap.put("NK)EN", "F");
smap.put("NK)ES", "F");
smap.put("NK)EV", "F");
smap.put("NK)OF", "F");
smap.put("NK)UE", "F");
smap.put("NK1", "F");
smap.put("NK1&(", "F");
smap.put("NK1&1", "F");
smap.put("NK1&F", "F");
smap.put("NK1&N", "F");
smap.put("NK1&S", "F");
smap.put("NK1&V", "F");
smap.put("NK1;C", "F");
smap.put("NK1;E", "F");
smap.put("NK1;T", "F");
smap.put("NK1B(", "F");
smap.put("NK1B1", "F");
smap.put("NK1BF", "F");
smap.put("NK1BN", "F");
smap.put("NK1BS", "F");
smap.put("NK1BV", "F");
smap.put("NK1C", "F");
smap.put("NK1E(", "F");
smap.put("NK1E1", "F");
smap.put("NK1EF", "F");
smap.put("NK1EK", "F");
smap.put("NK1EN", "F");
smap.put("NK1ES", "F");
smap.put("NK1EV", "F");
smap.put("NK1O(", "F");
smap.put("NK1OF", "F");
smap.put("NK1OS", "F");
smap.put("NK1OV", "F");
smap.put("NK1U(", "F");
smap.put("NK1UE", "F");
smap.put("NKF()", "F");
smap.put("NKF(1", "F");
smap.put("NKF(F", "F");
smap.put("NKF(N", "F");
smap.put("NKF(S", "F");
smap.put("NKF(V", "F");
smap.put("NKN", "F");
smap.put("NKN&(", "F");
smap.put("NKN&1", "F");
smap.put("NKN&F", "F");
smap.put("NKN&S", "F");
smap.put("NKN&V", "F");
smap.put("NKN;C", "F");
smap.put("NKN;E", "F");
smap.put("NKN;T", "F");
smap.put("NKNB(", "F");
smap.put("NKNB1", "F");
smap.put("NKNBF", "F");
smap.put("NKNBN", "F");
smap.put("NKNBS", "F");
smap.put("NKNBV", "F");
smap.put("NKNC", "F");
smap.put("NKNE(", "F");
smap.put("NKNE1", "F");
smap.put("NKNEF", "F");
smap.put("NKNES", "F");
smap.put("NKNEV", "F");
smap.put("NKNU(", "F");
smap.put("NKNUE", "F");
smap.put("NKS", "F");
smap.put("NKS&(", "F");
smap.put("NKS&1", "F");
smap.put("NKS&F", "F");
smap.put("NKS&N", "F");
smap.put("NKS&S", "F");
smap.put("NKS&V", "F");
smap.put("NKS;", "F");
smap.put("NKS;C", "F");
smap.put("NKS;E", "F");
smap.put("NKS;T", "F");
smap.put("NKSB(", "F");
smap.put("NKSB1", "F");
smap.put("NKSBF", "F");
smap.put("NKSBN", "F");
smap.put("NKSBS", "F");
smap.put("NKSBV", "F");
smap.put("NKSC", "F");
smap.put("NKSE(", "F");
smap.put("NKSE1", "F");
smap.put("NKSEF", "F");
smap.put("NKSEK", "F");
smap.put("NKSEN", "F");
smap.put("NKSES", "F");
smap.put("NKSEV", "F");
smap.put("NKSO(", "F");
smap.put("NKSO1", "F");
smap.put("NKSOF", "F");
smap.put("NKSON", "F");
smap.put("NKSOS", "F");
smap.put("NKSOV", "F");
smap.put("NKSU(", "F");
smap.put("NKSUE", "F");
smap.put("NKUE(", "F");
smap.put("NKUE1", "F");
smap.put("NKUEF", "F");
smap.put("NKUEK", "F");
smap.put("NKUEN", "F");
smap.put("NKUES", "F");
smap.put("NKUEV", "F");
smap.put("NKV", "F");
smap.put("NKV&(", "F");
smap.put("NKV&1", "F");
smap.put("NKV&F", "F");
smap.put("NKV&N", "F");
smap.put("NKV&S", "F");
smap.put("NKV&V", "F");
smap.put("NKV;", "F");
smap.put("NKV;C", "F");
smap.put("NKV;E", "F");
smap.put("NKV;T", "F");
smap.put("NKVB(", "F");
smap.put("NKVB1", "F");
smap.put("NKVBF", "F");
smap.put("NKVBN", "F");
smap.put("NKVBS", "F");
smap.put("NKVBV", "F");
smap.put("NKVC", "F");
smap.put("NKVE(", "F");
smap.put("NKVE1", "F");
smap.put("NKVEF", "F");
smap.put("NKVEK", "F");
smap.put("NKVEN", "F");
smap.put("NKVES", "F");
smap.put("NKVEV", "F");
smap.put("NKVO(", "F");
smap.put("NKVOF", "F");
smap.put("NKVOS", "F");
smap.put("NKVU(", "F");
smap.put("NKVUE", "F");
smap.put("NO(1&", "F");
smap.put("NO(1)", "F");
smap.put("NO(1,", "F");
smap.put("NO(1O", "F");
smap.put("NO(E(", "F");
smap.put("NO(E1", "F");
smap.put("NO(EE", "F");
smap.put("NO(EF", "F");
smap.put("NO(EK", "F");
smap.put("NO(EN", "F");
smap.put("NO(ES", "F");
smap.put("NO(EV", "F");
smap.put("NO(F(", "F");
smap.put("NO(N&", "F");
smap.put("NO(N)", "F");
smap.put("NO(N,", "F");
smap.put("NO(NO", "F");
smap.put("NO(S&", "F");
smap.put("NO(S)", "F");
smap.put("NO(S,", "F");
smap.put("NO(SO", "F");
smap.put("NO(V&", "F");
smap.put("NO(V)", "F");
smap.put("NO(V,", "F");
smap.put("NO(VO", "F");
smap.put("NOF()", "F");
smap.put("NOF(1", "F");
smap.put("NOF(E", "F");
smap.put("NOF(F", "F");
smap.put("NOF(N", "F");
smap.put("NOF(S", "F");
smap.put("NOF(V", "F");
smap.put("NOK&(", "F");
smap.put("NOK&1", "F");
smap.put("NOK&F", "F");
smap.put("NOK&N", "F");
smap.put("NOK&S", "F");
smap.put("NOK&V", "F");
smap.put("NOK(1", "F");
smap.put("NOK(F", "F");
smap.put("NOK(N", "F");
smap.put("NOK(S", "F");
smap.put("NOK(V", "F");
smap.put("NOK1C", "F");
smap.put("NOK1O", "F");
smap.put("NOKF(", "F");
smap.put("NOKNC", "F");
smap.put("NOKO(", "F");
smap.put("NOKO1", "F");
smap.put("NOKOF", "F");
smap.put("NOKON", "F");
smap.put("NOKOS", "F");
smap.put("NOKOV", "F");
smap.put("NOKSC", "F");
smap.put("NOKSO", "F");
smap.put("NOKVC", "F");
smap.put("NOKVO", "F");
smap.put("NONSU", "F");
smap.put("NOS&(", "F");
smap.put("NOS&1", "F");
smap.put("NOS&E", "F");
smap.put("NOS&F", "F");
smap.put("NOS&K", "F");
smap.put("NOS&N", "F");
smap.put("NOS&S", "F");
smap.put("NOS&U", "F");
smap.put("NOS&V", "F");
smap.put("NOS(E", "F");
smap.put("NOS(U", "F");
smap.put("NOS)&", "F");
smap.put("NOS),", "F");
smap.put("NOS);", "F");
smap.put("NOS)B", "F");
smap.put("NOS)C", "F");
smap.put("NOS)E", "F");
smap.put("NOS)K", "F");
smap.put("NOS)O", "F");
smap.put("NOS)U", "F");
smap.put("NOS,(", "F");
smap.put("NOS,F", "F");
smap.put("NOS1(", "F");
smap.put("NOS1F", "F");
smap.put("NOS1N", "F");
smap.put("NOS1O", "F");
smap.put("NOS1S", "F");
smap.put("NOS1U", "F");
smap.put("NOS1V", "F");
smap.put("NOS;", "F");
smap.put("NOS;C", "F");
smap.put("NOS;E", "F");
smap.put("NOS;T", "F");
smap.put("NOSA(", "F");
smap.put("NOSAF", "F");
smap.put("NOSAS", "F");
smap.put("NOSAT", "F");
smap.put("NOSAV", "F");
smap.put("NOSB(", "F");
smap.put("NOSB1", "F");
smap.put("NOSBE", "F");
smap.put("NOSBF", "F");
smap.put("NOSBN", "F");
smap.put("NOSBS", "F");
smap.put("NOSBV", "F");
smap.put("NOSC", "F");
smap.put("NOSE(", "F");
smap.put("NOSE1", "F");
smap.put("NOSEF", "F");
smap.put("NOSEK", "F");
smap.put("NOSEN", "F");
smap.put("NOSEO", "F");
smap.put("NOSES", "F");
smap.put("NOSEU", "F");
smap.put("NOSEV", "F");
smap.put("NOSF(", "F");
smap.put("NOSK(", "F");
smap.put("NOSK)", "F");
smap.put("NOSK1", "F");
smap.put("NOSKB", "F");
smap.put("NOSKF", "F");
smap.put("NOSKN", "F");
smap.put("NOSKS", "F");
smap.put("NOSKU", "F");
smap.put("NOSKV", "F");
smap.put("NOSU", "F");
smap.put("NOSU(", "F");
smap.put("NOSU1", "F");
smap.put("NOSU;", "F");
smap.put("NOSUC", "F");
smap.put("NOSUE", "F");
smap.put("NOSUF", "F");
smap.put("NOSUK", "F");
smap.put("NOSUN", "F");
smap.put("NOSUO", "F");
smap.put("NOSUS", "F");
smap.put("NOSUT", "F");
smap.put("NOSUV", "F");
smap.put("NOSV(", "F");
smap.put("NOSVF", "F");
smap.put("NOSVO", "F");
smap.put("NOSVS", "F");
smap.put("NOSVU", "F");
smap.put("NOU(E", "F");
smap.put("NOUEK", "F");
smap.put("NOUEN", "F");
smap.put("NOV&(", "F");
smap.put("NOV&1", "F");
smap.put("NOV&E", "F");
smap.put("NOV&F", "F");
smap.put("NOV&K", "F");
smap.put("NOV&N", "F");
smap.put("NOV&S", "F");
smap.put("NOV&U", "F");
smap.put("NOV&V", "F");
smap.put("NOV(E", "F");
smap.put("NOV(U", "F");
smap.put("NOV)&", "F");
smap.put("NOV),", "F");
smap.put("NOV);", "F");
smap.put("NOV)B", "F");
smap.put("NOV)C", "F");
smap.put("NOV)E", "F");
smap.put("NOV)K", "F");
smap.put("NOV)O", "F");
smap.put("NOV)U", "F");
smap.put("NOV,(", "F");
smap.put("NOV,F", "F");
smap.put("NOV;", "F");
smap.put("NOV;C", "F");
smap.put("NOV;E", "F");
smap.put("NOV;N", "F");
smap.put("NOV;T", "F");
smap.put("NOVA(", "F");
smap.put("NOVAF", "F");
smap.put("NOVAS", "F");
smap.put("NOVAT", "F");
smap.put("NOVAV", "F");
smap.put("NOVB(", "F");
smap.put("NOVB1", "F");
smap.put("NOVBE", "F");
smap.put("NOVBF", "F");
smap.put("NOVBN", "F");
smap.put("NOVBS", "F");
smap.put("NOVBV", "F");
smap.put("NOVC", "F");
smap.put("NOVE(", "F");
smap.put("NOVE1", "F");
smap.put("NOVEF", "F");
smap.put("NOVEK", "F");
smap.put("NOVEN", "F");
smap.put("NOVEO", "F");
smap.put("NOVES", "F");
smap.put("NOVEU", "F");
smap.put("NOVEV", "F");
smap.put("NOVF(", "F");
smap.put("NOVK(", "F");
smap.put("NOVK)", "F");
smap.put("NOVK1", "F");
smap.put("NOVKB", "F");
smap.put("NOVKF", "F");
smap.put("NOVKN", "F");
smap.put("NOVKS", "F");
smap.put("NOVKU", "F");
smap.put("NOVKV", "F");
smap.put("NOVO(", "F");
smap.put("NOVOF", "F");
smap.put("NOVOK", "F");
smap.put("NOVOS", "F");
smap.put("NOVOU", "F");
smap.put("NOVS(", "F");
smap.put("NOVS1", "F");
smap.put("NOVSF", "F");
smap.put("NOVSO", "F");
smap.put("NOVSU", "F");
smap.put("NOVSV", "F");
smap.put("NOVU", "F");
smap.put("NOVU(", "F");
smap.put("NOVU1", "F");
smap.put("NOVU;", "F");
smap.put("NOVUC", "F");
smap.put("NOVUE", "F");
smap.put("NOVUF", "F");
smap.put("NOVUK", "F");
smap.put("NOVUN", "F");
smap.put("NOVUO", "F");
smap.put("NOVUS", "F");
smap.put("NOVUT", "F");
smap.put("NOVUV", "F");
smap.put("NSO1U", "F");
smap.put("NSONU", "F");
smap.put("NSOSU", "F");
smap.put("NSOVU", "F");
smap.put("NSUE", "F");
smap.put("NSUE;", "F");
smap.put("NSUEC", "F");
smap.put("NSUEK", "F");
smap.put("NU(1)", "F");
smap.put("NU(1O", "F");
smap.put("NU(E(", "F");
smap.put("NU(E1", "F");
smap.put("NU(EF", "F");
smap.put("NU(EK", "F");
smap.put("NU(EN", "F");
smap.put("NU(ES", "F");
smap.put("NU(EV", "F");
smap.put("NU(F(", "F");
smap.put("NU(N)", "F");
smap.put("NU(NO", "F");
smap.put("NU(S)", "F");
smap.put("NU(SO", "F");
smap.put("NU(V)", "F");
smap.put("NU(VO", "F");
smap.put("NU1,(", "F");
smap.put("NU1,F", "F");
smap.put("NU1C", "F");
smap.put("NU1O(", "F");
smap.put("NU1OF", "F");
smap.put("NU1OS", "F");
smap.put("NU1OV", "F");
smap.put("NU;", "F");
smap.put("NU;C", "F");
smap.put("NUC", "F");
smap.put("NUE", "F");
smap.put("NUE(1", "F");
smap.put("NUE(E", "F");
smap.put("NUE(F", "F");
smap.put("NUE(N", "F");
smap.put("NUE(O", "F");
smap.put("NUE(S", "F");
smap.put("NUE(V", "F");
smap.put("NUE1", "F");
smap.put("NUE1&", "F");
smap.put("NUE1(", "F");
smap.put("NUE1)", "F");
smap.put("NUE1,", "F");
smap.put("NUE1;", "F");
smap.put("NUE1B", "F");
smap.put("NUE1C", "F");
smap.put("NUE1F", "F");
smap.put("NUE1K", "F");
smap.put("NUE1N", "F");
smap.put("NUE1O", "F");
smap.put("NUE1S", "F");
smap.put("NUE1U", "F");
smap.put("NUE1V", "F");
smap.put("NUE;", "F");
smap.put("NUE;C", "F");
smap.put("NUEC", "F");
smap.put("NUEF", "F");
smap.put("NUEF(", "F");
smap.put("NUEF,", "F");
smap.put("NUEF;", "F");
smap.put("NUEFC", "F");
smap.put("NUEK", "F");
smap.put("NUEK(", "F");
smap.put("NUEK1", "F");
smap.put("NUEK;", "F");
smap.put("NUEKC", "F");
smap.put("NUEKF", "F");
smap.put("NUEKN", "F");
smap.put("NUEKO", "F");
smap.put("NUEKS", "F");
smap.put("NUEKV", "F");
smap.put("NUEN", "F");
smap.put("NUEN&", "F");
smap.put("NUEN(", "F");
smap.put("NUEN)", "F");
smap.put("NUEN,", "F");
smap.put("NUEN1", "F");
smap.put("NUEN;", "F");
smap.put("NUENB", "F");
smap.put("NUENC", "F");
smap.put("NUENF", "F");
smap.put("NUENK", "F");
smap.put("NUENO", "F");
smap.put("NUENS", "F");
smap.put("NUENU", "F");
smap.put("NUEOK", "F");
smap.put("NUEON", "F");
smap.put("NUEOO", "F");
smap.put("NUES", "F");
smap.put("NUES&", "F");
smap.put("NUES(", "F");
smap.put("NUES)", "F");
smap.put("NUES,", "F");
smap.put("NUES1", "F");
smap.put("NUES;", "F");
smap.put("NUESB", "F");
smap.put("NUESC", "F");
smap.put("NUESF", "F");
smap.put("NUESK", "F");
smap.put("NUESO", "F");
smap.put("NUESU", "F");
smap.put("NUESV", "F");
smap.put("NUEV", "F");
smap.put("NUEV&", "F");
smap.put("NUEV(", "F");
smap.put("NUEV)", "F");
smap.put("NUEV,", "F");
smap.put("NUEV;", "F");
smap.put("NUEVB", "F");
smap.put("NUEVC", "F");
smap.put("NUEVF", "F");
smap.put("NUEVK", "F");
smap.put("NUEVN", "F");
smap.put("NUEVO", "F");
smap.put("NUEVS", "F");
smap.put("NUEVU", "F");
smap.put("NUF()", "F");
smap.put("NUF(1", "F");
smap.put("NUF(F", "F");
smap.put("NUF(N", "F");
smap.put("NUF(S", "F");
smap.put("NUF(V", "F");
smap.put("NUK(E", "F");
smap.put("NUN(1", "F");
smap.put("NUN(F", "F");
smap.put("NUN(S", "F");
smap.put("NUN(V", "F");
smap.put("NUN,(", "F");
smap.put("NUN,F", "F");
smap.put("NUN1(", "F");
smap.put("NUN1,", "F");
smap.put("NUN1O", "F");
smap.put("NUNC", "F");
smap.put("NUNE(", "F");
smap.put("NUNE1", "F");
smap.put("NUNEF", "F");
smap.put("NUNEN", "F");
smap.put("NUNES", "F");
smap.put("NUNEV", "F");
smap.put("NUNF(", "F");
smap.put("NUNO(", "F");
smap.put("NUNOF", "F");
smap.put("NUNOS", "F");
smap.put("NUNOV", "F");
smap.put("NUNS(", "F");
smap.put("NUNS,", "F");
smap.put("NUNSO", "F");
smap.put("NUO(E", "F");
smap.put("NUON(", "F");
smap.put("NUON1", "F");
smap.put("NUONF", "F");
smap.put("NUONS", "F");
smap.put("NUS,(", "F");
smap.put("NUS,F", "F");
smap.put("NUSC", "F");
smap.put("NUSO(", "F");
smap.put("NUSO1", "F");
smap.put("NUSOF", "F");
smap.put("NUSON", "F");
smap.put("NUSOS", "F");
smap.put("NUSOV", "F");
smap.put("NUTN(", "F");
smap.put("NUTN1", "F");
smap.put("NUTNF", "F");
smap.put("NUTNS", "F");
smap.put("NUV,(", "F");
smap.put("NUV,F", "F");
smap.put("NUVC", "F");
smap.put("NUVO(", "F");
smap.put("NUVOF", "F");
smap.put("NUVOS", "F");
smap.put("O(1)O", "F");
smap.put("O(1)U", "F");
smap.put("O(1O(", "F");
smap.put("O(1OF", "F");
smap.put("O(1OS", "F");
smap.put("O(1OV", "F");
smap.put("O(F()", "F");
smap.put("O(F(1", "F");
smap.put("O(F(F", "F");
smap.put("O(F(N", "F");
smap.put("O(F(S", "F");
smap.put("O(F(V", "F");
smap.put("O(N)O", "F");
smap.put("O(N)U", "F");
smap.put("O(NO(", "F");
smap.put("O(NOF", "F");
smap.put("O(NOS", "F");
smap.put("O(NOV", "F");
smap.put("O(S)O", "F");
smap.put("O(S)U", "F");
smap.put("O(SO(", "F");
smap.put("O(SO1", "F");
smap.put("O(SOF", "F");
smap.put("O(SON", "F");
smap.put("O(SOS", "F");
smap.put("O(SOV", "F");
smap.put("O(V)O", "F");
smap.put("O(V)U", "F");
smap.put("O(VO(", "F");
smap.put("O(VOF", "F");
smap.put("O(VOS", "F");
smap.put("O1UE(", "F");
smap.put("O1UE1", "F");
smap.put("O1UEF", "F");
smap.put("O1UEK", "F");
smap.put("O1UEN", "F");
smap.put("O1UES", "F");
smap.put("O1UEV", "F");
smap.put("OF()O", "F");
smap.put("OF()U", "F");
smap.put("OF(1)", "F");
smap.put("OF(1O", "F");
smap.put("OF(F(", "F");
smap.put("OF(N)", "F");
smap.put("OF(NO", "F");
smap.put("OF(S)", "F");
smap.put("OF(SO", "F");
smap.put("OF(V)", "F");
smap.put("OF(VO", "F");
smap.put("ONUE(", "F");
smap.put("ONUE1", "F");
smap.put("ONUEF", "F");
smap.put("ONUEK", "F");
smap.put("ONUEN", "F");
smap.put("ONUES", "F");
smap.put("ONUEV", "F");
smap.put("OSUE(", "F");
smap.put("OSUE1", "F");
smap.put("OSUEF", "F");
smap.put("OSUEK", "F");
smap.put("OSUEN", "F");
smap.put("OSUES", "F");
smap.put("OSUEV", "F");
smap.put("OUE(1", "F");
smap.put("OUE(F", "F");
smap.put("OUE(N", "F");
smap.put("OUE(S", "F");
smap.put("OUE(V", "F");
smap.put("OUE1,", "F");
smap.put("OUE1O", "F");
smap.put("OUEF(", "F");
smap.put("OUEK(", "F");
smap.put("OUEK1", "F");
smap.put("OUEKF", "F");
smap.put("OUEKN", "F");
smap.put("OUEKS", "F");
smap.put("OUEKV", "F");
smap.put("OUEN,", "F");
smap.put("OUENO", "F");
smap.put("OUES,", "F");
smap.put("OUESO", "F");
smap.put("OUEV,", "F");
smap.put("OUEVO", "F");
smap.put("OVO(1", "F");
smap.put("OVO(F", "F");
smap.put("OVO(N", "F");
smap.put("OVO(S", "F");
smap.put("OVO(V", "F");
smap.put("OVOF(", "F");
smap.put("OVOSU", "F");
smap.put("OVUE(", "F");
smap.put("OVUE1", "F");
smap.put("OVUEF", "F");
smap.put("OVUEK", "F");
smap.put("OVUEN", "F");
smap.put("OVUES", "F");
smap.put("OVUEV", "F");
smap.put("S&(1&", "F");
smap.put("S&(1)", "F");
smap.put("S&(1,", "F");
smap.put("S&(1O", "F");
smap.put("S&(E(", "F");
smap.put("S&(E1", "F");
smap.put("S&(EF", "F");
smap.put("S&(EK", "F");
smap.put("S&(EN", "F");
smap.put("S&(EO", "F");
smap.put("S&(ES", "F");
smap.put("S&(EV", "F");
smap.put("S&(F(", "F");
smap.put("S&(N&", "F");
smap.put("S&(N)", "F");
smap.put("S&(N,", "F");
smap.put("S&(NO", "F");
smap.put("S&(S&", "F");
smap.put("S&(S)", "F");
smap.put("S&(S,", "F");
smap.put("S&(SO", "F");
smap.put("S&(V&", "F");
smap.put("S&(V)", "F");
smap.put("S&(V,", "F");
smap.put("S&(VO", "F");
smap.put("S&1", "F");
smap.put("S&1&(", "F");
smap.put("S&1&1", "F");
smap.put("S&1&F", "F");
smap.put("S&1&N", "F");
smap.put("S&1&S", "F");
smap.put("S&1&V", "F");
smap.put("S&1)&", "F");
smap.put("S&1)C", "F");
smap.put("S&1)O", "F");
smap.put("S&1)U", "F");
smap.put("S&1;", "F");
smap.put("S&1;C", "F");
smap.put("S&1;E", "F");
smap.put("S&1;T", "F");
smap.put("S&1B(", "F");
smap.put("S&1B1", "F");
smap.put("S&1BF", "F");
smap.put("S&1BN", "F");
smap.put("S&1BS", "F");
smap.put("S&1BV", "F");
smap.put("S&1C", "F");
smap.put("S&1EK", "F");
smap.put("S&1EN", "F");
smap.put("S&1F(", "F");
smap.put("S&1K(", "F");
smap.put("S&1K1", "F");
smap.put("S&1KF", "F");
smap.put("S&1KN", "F");
smap.put("S&1KS", "F");
smap.put("S&1KV", "F");
smap.put("S&1O(", "F");
smap.put("S&1OF", "F");
smap.put("S&1OO", "F");
smap.put("S&1OS", "F");
smap.put("S&1OV", "F");
smap.put("S&1TN", "F");
smap.put("S&1U", "F");
smap.put("S&1U(", "F");
smap.put("S&1U;", "F");
smap.put("S&1UC", "F");
smap.put("S&1UE", "F");
smap.put("S&E(1", "F");
smap.put("S&E(F", "F");
smap.put("S&E(N", "F");
smap.put("S&E(O", "F");
smap.put("S&E(S", "F");
smap.put("S&E(V", "F");
smap.put("S&E1", "F");
smap.put("S&E1;", "F");
smap.put("S&E1C", "F");
smap.put("S&E1K", "F");
smap.put("S&E1O", "F");
smap.put("S&EF(", "F");
smap.put("S&EK(", "F");
smap.put("S&EK1", "F");
smap.put("S&EKF", "F");
smap.put("S&EKN", "F");
smap.put("S&EKS", "F");
smap.put("S&EKV", "F");
smap.put("S&EN", "F");
smap.put("S&EN;", "F");
smap.put("S&ENC", "F");
smap.put("S&ENK", "F");
smap.put("S&ENO", "F");
smap.put("S&ES", "F");
smap.put("S&ES;", "F");
smap.put("S&ESC", "F");
smap.put("S&ESK", "F");
smap.put("S&ESO", "F");
smap.put("S&EV", "F");
smap.put("S&EV;", "F");
smap.put("S&EVC", "F");
smap.put("S&EVK", "F");
smap.put("S&EVO", "F");
smap.put("S&F()", "F");
smap.put("S&F(1", "F");
smap.put("S&F(E", "F");
smap.put("S&F(F", "F");
smap.put("S&F(N", "F");
smap.put("S&F(S", "F");
smap.put("S&F(V", "F");
smap.put("S&K&(", "F");
smap.put("S&K&1", "F");
smap.put("S&K&F", "F");
smap.put("S&K&N", "F");
smap.put("S&K&S", "F");
smap.put("S&K&V", "F");
smap.put("S&K(1", "F");
smap.put("S&K(F", "F");
smap.put("S&K(N", "F");
smap.put("S&K(S", "F");
smap.put("S&K(V", "F");
smap.put("S&K1O", "F");
smap.put("S&KF(", "F");
smap.put("S&KNK", "F");
smap.put("S&KO(", "F");
smap.put("S&KO1", "F");
smap.put("S&KOF", "F");
smap.put("S&KOK", "F");
smap.put("S&KON", "F");
smap.put("S&KOS", "F");
smap.put("S&KOV", "F");
smap.put("S&KSO", "F");
smap.put("S&KVO", "F");
smap.put("S&N", "F");
smap.put("S&N&(", "F");
smap.put("S&N&1", "F");
smap.put("S&N&F", "F");
smap.put("S&N&N", "F");
smap.put("S&N&S", "F");
smap.put("S&N&V", "F");
smap.put("S&N)&", "F");
smap.put("S&N)C", "F");
smap.put("S&N)O", "F");
smap.put("S&N)U", "F");
smap.put("S&N;", "F");
smap.put("S&N;C", "F");
smap.put("S&N;E", "F");
smap.put("S&N;T", "F");
smap.put("S&NB(", "F");
smap.put("S&NB1", "F");
smap.put("S&NBF", "F");
smap.put("S&NBN", "F");
smap.put("S&NBS", "F");
smap.put("S&NBV", "F");
smap.put("S&NC", "F");
smap.put("S&NEN", "F");
smap.put("S&NF(", "F");
smap.put("S&NK(", "F");
smap.put("S&NK1", "F");
smap.put("S&NKF", "F");
smap.put("S&NKN", "F");
smap.put("S&NKS", "F");
smap.put("S&NKV", "F");
smap.put("S&NO(", "F");
smap.put("S&NOF", "F");
smap.put("S&NOS", "F");
smap.put("S&NOV", "F");
smap.put("S&NTN", "F");
smap.put("S&NU", "F");
smap.put("S&NU(", "F");
smap.put("S&NU;", "F");
smap.put("S&NUC", "F");
smap.put("S&NUE", "F");
smap.put("S&S", "F");
smap.put("S&S&(", "F");
smap.put("S&S&1", "F");
smap.put("S&S&F", "F");
smap.put("S&S&N", "F");
smap.put("S&S&S", "F");
smap.put("S&S&V", "F");
smap.put("S&S)&", "F");
smap.put("S&S)C", "F");
smap.put("S&S)O", "F");
smap.put("S&S)U", "F");
smap.put("S&S1", "F");
smap.put("S&S1;", "F");
smap.put("S&S1C", "F");
smap.put("S&S1O", "F");
smap.put("S&S;", "F");
smap.put("S&S;C", "F");
smap.put("S&S;E", "F");
smap.put("S&S;T", "F");
smap.put("S&SB(", "F");
smap.put("S&SB1", "F");
smap.put("S&SBF", "F");
smap.put("S&SBN", "F");
smap.put("S&SBS", "F");
smap.put("S&SBV", "F");
smap.put("S&SC", "F");
smap.put("S&SEK", "F");
smap.put("S&SEN", "F");
smap.put("S&SF(", "F");
smap.put("S&SK(", "F");
smap.put("S&SK1", "F");
smap.put("S&SKF", "F");
smap.put("S&SKN", "F");
smap.put("S&SKS", "F");
smap.put("S&SKV", "F");
smap.put("S&SO(", "F");
smap.put("S&SO1", "F");
smap.put("S&SOF", "F");
smap.put("S&SON", "F");
smap.put("S&SOO", "F");
smap.put("S&SOS", "F");
smap.put("S&SOV", "F");
smap.put("S&STN", "F");
smap.put("S&SU", "F");
smap.put("S&SU(", "F");
smap.put("S&SU;", "F");
smap.put("S&SUC", "F");
smap.put("S&SUE", "F");
smap.put("S&SV", "F");
smap.put("S&SV;", "F");
smap.put("S&SVC", "F");
smap.put("S&SVO", "F");
smap.put("S&V", "F");
smap.put("S&V&(", "F");
smap.put("S&V&1", "F");
smap.put("S&V&F", "F");
smap.put("S&V&N", "F");
smap.put("S&V&S", "F");
smap.put("S&V&V", "F");
smap.put("S&V)&", "F");
smap.put("S&V)C", "F");
smap.put("S&V)O", "F");
smap.put("S&V)U", "F");
smap.put("S&V;", "F");
smap.put("S&V;C", "F");
smap.put("S&V;E", "F");
smap.put("S&V;T", "F");
smap.put("S&VB(", "F");
smap.put("S&VB1", "F");
smap.put("S&VBF", "F");
smap.put("S&VBN", "F");
smap.put("S&VBS", "F");
smap.put("S&VBV", "F");
smap.put("S&VC", "F");
smap.put("S&VEK", "F");
smap.put("S&VEN", "F");
smap.put("S&VF(", "F");
smap.put("S&VK(", "F");
smap.put("S&VK1", "F");
smap.put("S&VKF", "F");
smap.put("S&VKN", "F");
smap.put("S&VKS", "F");
smap.put("S&VKV", "F");
smap.put("S&VO(", "F");
smap.put("S&VOF", "F");
smap.put("S&VOO", "F");
smap.put("S&VOS", "F");
smap.put("S&VS", "F");
smap.put("S&VS;", "F");
smap.put("S&VSC", "F");
smap.put("S&VSO", "F");
smap.put("S&VTN", "F");
smap.put("S&VU", "F");
smap.put("S&VU(", "F");
smap.put("S&VU;", "F");
smap.put("S&VUC", "F");
smap.put("S&VUE", "F");
smap.put("S(EF(", "F");
smap.put("S(EKF", "F");
smap.put("S(EKN", "F");
smap.put("S(ENK", "F");
smap.put("S(U(E", "F");
smap.put("S)&(1", "F");
smap.put("S)&(E", "F");
smap.put("S)&(F", "F");
smap.put("S)&(N", "F");
smap.put("S)&(S", "F");
smap.put("S)&(V", "F");
smap.put("S)&1", "F");
smap.put("S)&1&", "F");
smap.put("S)&1)", "F");
smap.put("S)&1;", "F");
smap.put("S)&1B", "F");
smap.put("S)&1C", "F");
smap.put("S)&1F", "F");
smap.put("S)&1O", "F");
smap.put("S)&1U", "F");
smap.put("S)&F(", "F");
smap.put("S)&N", "F");
smap.put("S)&N&", "F");
smap.put("S)&N)", "F");
smap.put("S)&N;", "F");
smap.put("S)&NB", "F");
smap.put("S)&NC", "F");
smap.put("S)&NF", "F");
smap.put("S)&NO", "F");
smap.put("S)&NU", "F");
smap.put("S)&S", "F");
smap.put("S)&S&", "F");
smap.put("S)&S)", "F");
smap.put("S)&S;", "F");
smap.put("S)&SB", "F");
smap.put("S)&SC", "F");
smap.put("S)&SF", "F");
smap.put("S)&SO", "F");
smap.put("S)&SU", "F");
smap.put("S)&V", "F");
smap.put("S)&V&", "F");
smap.put("S)&V)", "F");
smap.put("S)&V;", "F");
smap.put("S)&VB", "F");
smap.put("S)&VC", "F");
smap.put("S)&VF", "F");
smap.put("S)&VO", "F");
smap.put("S)&VU", "F");
smap.put("S),(1", "F");
smap.put("S),(F", "F");
smap.put("S),(N", "F");
smap.put("S),(S", "F");
smap.put("S),(V", "F");
smap.put("S);E(", "F");
smap.put("S);E1", "F");
smap.put("S);EF", "F");
smap.put("S);EK", "F");
smap.put("S);EN", "F");
smap.put("S);EO", "F");
smap.put("S);ES", "F");
smap.put("S);EV", "F");
smap.put("S);T(", "F");
smap.put("S);T1", "F");
smap.put("S);TF", "F");
smap.put("S);TK", "F");
smap.put("S);TN", "F");
smap.put("S);TO", "F");
smap.put("S);TS", "F");
smap.put("S);TV", "F");
smap.put("S)B(1", "F");
smap.put("S)B(F", "F");
smap.put("S)B(N", "F");
smap.put("S)B(S", "F");
smap.put("S)B(V", "F");
smap.put("S)B1", "F");
smap.put("S)B1&", "F");
smap.put("S)B1;", "F");
smap.put("S)B1C", "F");
smap.put("S)B1K", "F");
smap.put("S)B1N", "F");
smap.put("S)B1O", "F");
smap.put("S)B1U", "F");
smap.put("S)BF(", "F");
smap.put("S)BN", "F");
smap.put("S)BN&", "F");
smap.put("S)BN;", "F");
smap.put("S)BNC", "F");
smap.put("S)BNK", "F");
smap.put("S)BNO", "F");
smap.put("S)BNU", "F");
smap.put("S)BS", "F");
smap.put("S)BS&", "F");
smap.put("S)BS;", "F");
smap.put("S)BSC", "F");
smap.put("S)BSK", "F");
smap.put("S)BSO", "F");
smap.put("S)BSU", "F");
smap.put("S)BV", "F");
smap.put("S)BV&", "F");
smap.put("S)BV;", "F");
smap.put("S)BVC", "F");
smap.put("S)BVK", "F");
smap.put("S)BVO", "F");
smap.put("S)BVU", "F");
smap.put("S)C", "F");
smap.put("S)E(1", "F");
smap.put("S)E(F", "F");
smap.put("S)E(N", "F");
smap.put("S)E(S", "F");
smap.put("S)E(V", "F");
smap.put("S)E1C", "F");
smap.put("S)E1O", "F");
smap.put("S)EF(", "F");
smap.put("S)EK(", "F");
smap.put("S)EK1", "F");
smap.put("S)EKF", "F");
smap.put("S)EKN", "F");
smap.put("S)EKS", "F");
smap.put("S)EKV", "F");
smap.put("S)ENC", "F");
smap.put("S)ENO", "F");
smap.put("S)ESC", "F");
smap.put("S)ESO", "F");
smap.put("S)EVC", "F");
smap.put("S)EVO", "F");
smap.put("S)K(1", "F");
smap.put("S)K(F", "F");
smap.put("S)K(N", "F");
smap.put("S)K(S", "F");
smap.put("S)K(V", "F");
smap.put("S)K1&", "F");
smap.put("S)K1;", "F");
smap.put("S)K1B", "F");
smap.put("S)K1E", "F");
smap.put("S)K1O", "F");
smap.put("S)K1U", "F");
smap.put("S)KB(", "F");
smap.put("S)KB1", "F");
smap.put("S)KBF", "F");
smap.put("S)KBN", "F");
smap.put("S)KBS", "F");
smap.put("S)KBV", "F");
smap.put("S)KF(", "F");
smap.put("S)KN&", "F");
smap.put("S)KN;", "F");
smap.put("S)KNB", "F");
smap.put("S)KNE", "F");
smap.put("S)KNK", "F");
smap.put("S)KNU", "F");
smap.put("S)KS&", "F");
smap.put("S)KS;", "F");
smap.put("S)KSB", "F");
smap.put("S)KSE", "F");
smap.put("S)KSO", "F");
smap.put("S)KSU", "F");
smap.put("S)KUE", "F");
smap.put("S)KV&", "F");
smap.put("S)KV;", "F");
smap.put("S)KVB", "F");
smap.put("S)KVE", "F");
smap.put("S)KVO", "F");
smap.put("S)KVU", "F");
smap.put("S)O(1", "F");
smap.put("S)O(E", "F");
smap.put("S)O(F", "F");
smap.put("S)O(N", "F");
smap.put("S)O(S", "F");
smap.put("S)O(V", "F");
smap.put("S)O1", "F");
smap.put("S)O1&", "F");
smap.put("S)O1)", "F");
smap.put("S)O1;", "F");
smap.put("S)O1B", "F");
smap.put("S)O1C", "F");
smap.put("S)O1K", "F");
smap.put("S)O1U", "F");
smap.put("S)OF(", "F");
smap.put("S)ON&", "F");
smap.put("S)ON)", "F");
smap.put("S)ON;", "F");
smap.put("S)ONB", "F");
smap.put("S)ONC", "F");
smap.put("S)ONK", "F");
smap.put("S)ONU", "F");
smap.put("S)OS", "F");
smap.put("S)OS&", "F");
smap.put("S)OS)", "F");
smap.put("S)OS;", "F");
smap.put("S)OSB", "F");
smap.put("S)OSC", "F");
smap.put("S)OSK", "F");
smap.put("S)OSU", "F");
smap.put("S)OV", "F");
smap.put("S)OV&", "F");
smap.put("S)OV)", "F");
smap.put("S)OV;", "F");
smap.put("S)OVB", "F");
smap.put("S)OVC", "F");
smap.put("S)OVK", "F");
smap.put("S)OVO", "F");
smap.put("S)OVU", "F");
smap.put("S)U(E", "F");
smap.put("S)UE(", "F");
smap.put("S)UE1", "F");
smap.put("S)UEF", "F");
smap.put("S)UEK", "F");
smap.put("S)UEN", "F");
smap.put("S)UES", "F");
smap.put("S)UEV", "F");
smap.put("S,(1)", "F");
smap.put("S,(1O", "F");
smap.put("S,(E(", "F");
smap.put("S,(E1", "F");
smap.put("S,(EF", "F");
smap.put("S,(EK", "F");
smap.put("S,(EN", "F");
smap.put("S,(ES", "F");
smap.put("S,(EV", "F");
smap.put("S,(F(", "F");
smap.put("S,(N)", "F");
smap.put("S,(NO", "F");
smap.put("S,(S)", "F");
smap.put("S,(SO", "F");
smap.put("S,(V)", "F");
smap.put("S,(VO", "F");
smap.put("S,F()", "F");
smap.put("S,F(1", "F");
smap.put("S,F(F", "F");
smap.put("S,F(N", "F");
smap.put("S,F(S", "F");
smap.put("S,F(V", "F");
smap.put("S1F()", "F");
smap.put("S1F(1", "F");
smap.put("S1F(F", "F");
smap.put("S1F(N", "F");
smap.put("S1F(S", "F");
smap.put("S1F(V", "F");
smap.put("S1NC", "F");
smap.put("S1O(1", "F");
smap.put("S1O(F", "F");
smap.put("S1O(N", "F");
smap.put("S1O(S", "F");
smap.put("S1O(V", "F");
smap.put("S1OF(", "F");
smap.put("S1OS(", "F");
smap.put("S1OS1", "F");
smap.put("S1OSF", "F");
smap.put("S1OSU", "F");
smap.put("S1OSV", "F");
smap.put("S1OV(", "F");
smap.put("S1OVF", "F");
smap.put("S1OVO", "F");
smap.put("S1OVS", "F");
smap.put("S1OVU", "F");
smap.put("S1S;", "F");
smap.put("S1S;C", "F");
smap.put("S1SC", "F");
smap.put("S1UE", "F");
smap.put("S1UE;", "F");
smap.put("S1UEC", "F");
smap.put("S1UEK", "F");
smap.put("S1V", "F");
smap.put("S1V;", "F");
smap.put("S1V;C", "F");
smap.put("S1VC", "F");
smap.put("S1VO(", "F");
smap.put("S1VOF", "F");
smap.put("S1VOS", "F");
smap.put("S;E(1", "F");
smap.put("S;E(E", "F");
smap.put("S;E(F", "F");
smap.put("S;E(N", "F");
smap.put("S;E(S", "F");
smap.put("S;E(V", "F");
smap.put("S;E1,", "F");
smap.put("S;E1;", "F");
smap.put("S;E1C", "F");
smap.put("S;E1K", "F");
smap.put("S;E1O", "F");
smap.put("S;E1T", "F");
smap.put("S;EF(", "F");
smap.put("S;EK(", "F");
smap.put("S;EK1", "F");
smap.put("S;EKF", "F");
smap.put("S;EKN", "F");
smap.put("S;EKO", "F");
smap.put("S;EKS", "F");
smap.put("S;EKV", "F");
smap.put("S;EN,", "F");
smap.put("S;EN;", "F");
smap.put("S;ENC", "F");
smap.put("S;ENE", "F");
smap.put("S;ENK", "F");
smap.put("S;ENO", "F");
smap.put("S;ENT", "F");
smap.put("S;ES,", "F");
smap.put("S;ES;", "F");
smap.put("S;ESC", "F");
smap.put("S;ESK", "F");
smap.put("S;ESO", "F");
smap.put("S;EST", "F");
smap.put("S;EV,", "F");
smap.put("S;EV;", "F");
smap.put("S;EVC", "F");
smap.put("S;EVK", "F");
smap.put("S;EVO", "F");
smap.put("S;EVT", "F");
smap.put("S;N:T", "F");
smap.put("S;T(1", "F");
smap.put("S;T(E", "F");
smap.put("S;T(F", "F");
smap.put("S;T(N", "F");
smap.put("S;T(S", "F");
smap.put("S;T(V", "F");
smap.put("S;T1,", "F");
smap.put("S;T1;", "F");
smap.put("S;T1C", "F");
smap.put("S;T1F", "F");
smap.put("S;T1K", "F");
smap.put("S;T1O", "F");
smap.put("S;T1T", "F");
smap.put("S;T;", "F");
smap.put("S;T;C", "F");
smap.put("S;TF(", "F");
smap.put("S;TK(", "F");
smap.put("S;TK1", "F");
smap.put("S;TKF", "F");
smap.put("S;TKK", "F");
smap.put("S;TKN", "F");
smap.put("S;TKO", "F");
smap.put("S;TKS", "F");
smap.put("S;TKV", "F");
smap.put("S;TN(", "F");
smap.put("S;TN,", "F");
smap.put("S;TN1", "F");
smap.put("S;TN;", "F");
smap.put("S;TNC", "F");
smap.put("S;TNE", "F");
smap.put("S;TNF", "F");
smap.put("S;TNK", "F");
smap.put("S;TNN", "F");
smap.put("S;TNO", "F");
smap.put("S;TNS", "F");
smap.put("S;TNT", "F");
smap.put("S;TNV", "F");
smap.put("S;TO(", "F");
smap.put("S;TS,", "F");
smap.put("S;TS;", "F");
smap.put("S;TSC", "F");
smap.put("S;TSF", "F");
smap.put("S;TSK", "F");
smap.put("S;TSO", "F");
smap.put("S;TST", "F");
smap.put("S;TT(", "F");
smap.put("S;TT1", "F");
smap.put("S;TTF", "F");
smap.put("S;TTN", "F");
smap.put("S;TTS", "F");
smap.put("S;TTV", "F");
smap.put("S;TV,", "F");
smap.put("S;TV;", "F");
smap.put("S;TVC", "F");
smap.put("S;TVF", "F");
smap.put("S;TVK", "F");
smap.put("S;TVO", "F");
smap.put("S;TVT", "F");
smap.put("SA(F(", "F");
smap.put("SA(N)", "F");
smap.put("SA(NO", "F");
smap.put("SA(S)", "F");
smap.put("SA(SO", "F");
smap.put("SA(V)", "F");
smap.put("SA(VO", "F");
smap.put("SAF()", "F");
smap.put("SAF(1", "F");
smap.put("SAF(F", "F");
smap.put("SAF(N", "F");
smap.put("SAF(S", "F");
smap.put("SAF(V", "F");
smap.put("SASO(", "F");
smap.put("SASO1", "F");
smap.put("SASOF", "F");
smap.put("SASON", "F");
smap.put("SASOS", "F");
smap.put("SASOV", "F");
smap.put("SASUE", "F");
smap.put("SATO(", "F");
smap.put("SATO1", "F");
smap.put("SATOF", "F");
smap.put("SATON", "F");
smap.put("SATOS", "F");
smap.put("SATOV", "F");
smap.put("SATUE", "F");
smap.put("SAVO(", "F");
smap.put("SAVOF", "F");
smap.put("SAVOS", "F");
smap.put("SAVUE", "F");
smap.put("SB(1)", "F");
smap.put("SB(1O", "F");
smap.put("SB(F(", "F");
smap.put("SB(N)", "F");
smap.put("SB(NO", "F");
smap.put("SB(S)", "F");
smap.put("SB(SO", "F");
smap.put("SB(V)", "F");
smap.put("SB(VO", "F");
smap.put("SB1", "F");
smap.put("SB1&(", "F");
smap.put("SB1&1", "F");
smap.put("SB1&F", "F");
smap.put("SB1&N", "F");
smap.put("SB1&S", "F");
smap.put("SB1&V", "F");
smap.put("SB1,(", "F");
smap.put("SB1,F", "F");
smap.put("SB1;", "F");
smap.put("SB1;C", "F");
smap.put("SB1B(", "F");
smap.put("SB1B1", "F");
smap.put("SB1BF", "F");
smap.put("SB1BN", "F");
smap.put("SB1BS", "F");
smap.put("SB1BV", "F");
smap.put("SB1C", "F");
smap.put("SB1K(", "F");
smap.put("SB1K1", "F");
smap.put("SB1KF", "F");
smap.put("SB1KN", "F");
smap.put("SB1KS", "F");
smap.put("SB1KV", "F");
smap.put("SB1O(", "F");
smap.put("SB1OF", "F");
smap.put("SB1OS", "F");
smap.put("SB1OV", "F");
smap.put("SB1U(", "F");
smap.put("SB1UE", "F");
smap.put("SBE(1", "F");
smap.put("SBE(F", "F");
smap.put("SBE(N", "F");
smap.put("SBE(S", "F");
smap.put("SBE(V", "F");
smap.put("SBEK(", "F");
smap.put("SBF()", "F");
smap.put("SBF(1", "F");
smap.put("SBF(F", "F");
smap.put("SBF(N", "F");
smap.put("SBF(S", "F");
smap.put("SBF(V", "F");
smap.put("SBN", "F");
smap.put("SBN&(", "F");
smap.put("SBN&1", "F");
smap.put("SBN&F", "F");
smap.put("SBN&N", "F");
smap.put("SBN&S", "F");
smap.put("SBN&V", "F");
smap.put("SBN,(", "F");
smap.put("SBN,F", "F");
smap.put("SBN;", "F");
smap.put("SBN;C", "F");
smap.put("SBNB(", "F");
smap.put("SBNB1", "F");
smap.put("SBNBF", "F");
smap.put("SBNBN", "F");
smap.put("SBNBS", "F");
smap.put("SBNBV", "F");
smap.put("SBNC", "F");
smap.put("SBNK(", "F");
smap.put("SBNK1", "F");
smap.put("SBNKF", "F");
smap.put("SBNKN", "F");
smap.put("SBNKS", "F");
smap.put("SBNKV", "F");
smap.put("SBNO(", "F");
smap.put("SBNOF", "F");
smap.put("SBNOS", "F");
smap.put("SBNOV", "F");
smap.put("SBNU(", "F");
smap.put("SBNUE", "F");
smap.put("SBS", "F");
smap.put("SBS&(", "F");
smap.put("SBS&1", "F");
smap.put("SBS&F", "F");
smap.put("SBS&N", "F");
smap.put("SBS&S", "F");
smap.put("SBS&V", "F");
smap.put("SBS,(", "F");
smap.put("SBS,F", "F");
smap.put("SBS;", "F");
smap.put("SBS;C", "F");
smap.put("SBSB(", "F");
smap.put("SBSB1", "F");
smap.put("SBSBF", "F");
smap.put("SBSBN", "F");
smap.put("SBSBS", "F");
smap.put("SBSBV", "F");
smap.put("SBSC", "F");
smap.put("SBSK(", "F");
smap.put("SBSK1", "F");
smap.put("SBSKF", "F");
smap.put("SBSKN", "F");
smap.put("SBSKS", "F");
smap.put("SBSKV", "F");
smap.put("SBSO(", "F");
smap.put("SBSO1", "F");
smap.put("SBSOF", "F");
smap.put("SBSON", "F");
smap.put("SBSOS", "F");
smap.put("SBSOV", "F");
smap.put("SBSU(", "F");
smap.put("SBSUE", "F");
smap.put("SBV", "F");
smap.put("SBV&(", "F");
smap.put("SBV&1", "F");
smap.put("SBV&F", "F");
smap.put("SBV&N", "F");
smap.put("SBV&S", "F");
smap.put("SBV&V", "F");
smap.put("SBV,(", "F");
smap.put("SBV,F", "F");
smap.put("SBV;", "F");
smap.put("SBV;C", "F");
smap.put("SBVB(", "F");
smap.put("SBVB1", "F");
smap.put("SBVBF", "F");
smap.put("SBVBN", "F");
smap.put("SBVBS", "F");
smap.put("SBVBV", "F");
smap.put("SBVC", "F");
smap.put("SBVK(", "F");
smap.put("SBVK1", "F");
smap.put("SBVKF", "F");
smap.put("SBVKN", "F");
smap.put("SBVKS", "F");
smap.put("SBVKV", "F");
smap.put("SBVO(", "F");
smap.put("SBVOF", "F");
smap.put("SBVOS", "F");
smap.put("SBVU(", "F");
smap.put("SBVUE", "F");
smap.put("SC", "F");
smap.put("SE(1)", "F");
smap.put("SE(1O", "F");
smap.put("SE(F(", "F");
smap.put("SE(N)", "F");
smap.put("SE(NO", "F");
smap.put("SE(S)", "F");
smap.put("SE(SO", "F");
smap.put("SE(V)", "F");
smap.put("SE(VO", "F");
smap.put("SE1C", "F");
smap.put("SE1O(", "F");
smap.put("SE1OF", "F");
smap.put("SE1OS", "F");
smap.put("SE1OV", "F");
smap.put("SE1UE", "F");
smap.put("SEF()", "F");
smap.put("SEF(1", "F");
smap.put("SEF(F", "F");
smap.put("SEF(N", "F");
smap.put("SEF(S", "F");
smap.put("SEF(V", "F");
smap.put("SEK(1", "F");
smap.put("SEK(E", "F");
smap.put("SEK(F", "F");
smap.put("SEK(N", "F");
smap.put("SEK(S", "F");
smap.put("SEK(V", "F");
smap.put("SEK1C", "F");
smap.put("SEK1O", "F");
smap.put("SEK1U", "F");
smap.put("SEKF(", "F");
smap.put("SEKNC", "F");
smap.put("SEKNE", "F");
smap.put("SEKNU", "F");
smap.put("SEKOK", "F");
smap.put("SEKSC", "F");
smap.put("SEKSO", "F");
smap.put("SEKSU", "F");
smap.put("SEKU(", "F");
smap.put("SEKU1", "F");
smap.put("SEKUE", "F");
smap.put("SEKUF", "F");
smap.put("SEKUN", "F");
smap.put("SEKUS", "F");
smap.put("SEKUV", "F");
smap.put("SEKVC", "F");
smap.put("SEKVO", "F");
smap.put("SEKVU", "F");
smap.put("SENC", "F");
smap.put("SENEN", "F");
smap.put("SENO(", "F");
smap.put("SENOF", "F");
smap.put("SENOS", "F");
smap.put("SENOV", "F");
smap.put("SENUE", "F");
smap.put("SEOKN", "F");
smap.put("SESC", "F");
smap.put("SESO(", "F");
smap.put("SESO1", "F");
smap.put("SESOF", "F");
smap.put("SESON", "F");
smap.put("SESOS", "F");
smap.put("SESOV", "F");
smap.put("SESUE", "F");
smap.put("SEU(1", "F");
smap.put("SEU(F", "F");
smap.put("SEU(N", "F");
smap.put("SEU(S", "F");
smap.put("SEU(V", "F");
smap.put("SEU1,", "F");
smap.put("SEU1C", "F");
smap.put("SEU1O", "F");
smap.put("SEUEF", "F");
smap.put("SEUEK", "F");
smap.put("SEUF(", "F");
smap.put("SEUN,", "F");
smap.put("SEUNC", "F");
smap.put("SEUNO", "F");
smap.put("SEUS,", "F");
smap.put("SEUSC", "F");
smap.put("SEUSO", "F");
smap.put("SEUV,", "F");
smap.put("SEUVC", "F");
smap.put("SEUVO", "F");
smap.put("SEVC", "F");
smap.put("SEVO(", "F");
smap.put("SEVOF", "F");
smap.put("SEVOS", "F");
smap.put("SEVUE", "F");
smap.put("SF()1", "F");
smap.put("SF()F", "F");
smap.put("SF()K", "F");
smap.put("SF()N", "F");
smap.put("SF()O", "F");
smap.put("SF()S", "F");
smap.put("SF()U", "F");
smap.put("SF()V", "F");
smap.put("SF(1)", "F");
smap.put("SF(1N", "F");
smap.put("SF(1O", "F");
smap.put("SF(E(", "F");
smap.put("SF(E1", "F");
smap.put("SF(EF", "F");
smap.put("SF(EK", "F");
smap.put("SF(EN", "F");
smap.put("SF(ES", "F");
smap.put("SF(EV", "F");
smap.put("SF(F(", "F");
smap.put("SF(N)", "F");
smap.put("SF(N,", "F");
smap.put("SF(NO", "F");
smap.put("SF(S)", "F");
smap.put("SF(SO", "F");
smap.put("SF(V)", "F");
smap.put("SF(VO", "F");
smap.put("SK(1)", "F");
smap.put("SK(1O", "F");
smap.put("SK(F(", "F");
smap.put("SK(N)", "F");
smap.put("SK(NO", "F");
smap.put("SK(S)", "F");
smap.put("SK(SO", "F");
smap.put("SK(V)", "F");
smap.put("SK(VO", "F");
smap.put("SK)&(", "F");
smap.put("SK)&1", "F");
smap.put("SK)&F", "F");
smap.put("SK)&N", "F");
smap.put("SK)&S", "F");
smap.put("SK)&V", "F");
smap.put("SK);E", "F");
smap.put("SK);T", "F");
smap.put("SK)B(", "F");
smap.put("SK)B1", "F");
smap.put("SK)BF", "F");
smap.put("SK)BN", "F");
smap.put("SK)BS", "F");
smap.put("SK)BV", "F");
smap.put("SK)E(", "F");
smap.put("SK)E1", "F");
smap.put("SK)EF", "F");
smap.put("SK)EK", "F");
smap.put("SK)EN", "F");
smap.put("SK)ES", "F");
smap.put("SK)EV", "F");
smap.put("SK)OF", "F");
smap.put("SK)UE", "F");
smap.put("SK1", "F");
smap.put("SK1&(", "F");
smap.put("SK1&1", "F");
smap.put("SK1&F", "F");
smap.put("SK1&N", "F");
smap.put("SK1&S", "F");
smap.put("SK1&V", "F");
smap.put("SK1;", "F");
smap.put("SK1;C", "F");
smap.put("SK1;E", "F");
smap.put("SK1;T", "F");
smap.put("SK1B(", "F");
smap.put("SK1B1", "F");
smap.put("SK1BF", "F");
smap.put("SK1BN", "F");
smap.put("SK1BS", "F");
smap.put("SK1BV", "F");
smap.put("SK1C", "F");
smap.put("SK1E(", "F");
smap.put("SK1E1", "F");
smap.put("SK1EF", "F");
smap.put("SK1EK", "F");
smap.put("SK1EN", "F");
smap.put("SK1ES", "F");
smap.put("SK1EV", "F");
smap.put("SK1O(", "F");
smap.put("SK1OF", "F");
smap.put("SK1OS", "F");
smap.put("SK1OV", "F");
smap.put("SK1U(", "F");
smap.put("SK1UE", "F");
smap.put("SKF()", "F");
smap.put("SKF(1", "F");
smap.put("SKF(F", "F");
smap.put("SKF(N", "F");
smap.put("SKF(S", "F");
smap.put("SKF(V", "F");
smap.put("SKN", "F");
smap.put("SKN&(", "F");
smap.put("SKN&1", "F");
smap.put("SKN&F", "F");
smap.put("SKN&N", "F");
smap.put("SKN&S", "F");
smap.put("SKN&V", "F");
smap.put("SKN;", "F");
smap.put("SKN;C", "F");
smap.put("SKN;E", "F");
smap.put("SKN;T", "F");
smap.put("SKNB(", "F");
smap.put("SKNB1", "F");
smap.put("SKNBF", "F");
smap.put("SKNBN", "F");
smap.put("SKNBS", "F");
smap.put("SKNBV", "F");
smap.put("SKNC", "F");
smap.put("SKNE(", "F");
smap.put("SKNE1", "F");
smap.put("SKNEF", "F");
smap.put("SKNEN", "F");
smap.put("SKNES", "F");
smap.put("SKNEV", "F");
smap.put("SKNU(", "F");
smap.put("SKNUE", "F");
smap.put("SKS", "F");
smap.put("SKS&(", "F");
smap.put("SKS&1", "F");
smap.put("SKS&F", "F");
smap.put("SKS&N", "F");
smap.put("SKS&S", "F");
smap.put("SKS&V", "F");
smap.put("SKS;", "F");
smap.put("SKS;C", "F");
smap.put("SKS;E", "F");
smap.put("SKS;T", "F");
smap.put("SKSB(", "F");
smap.put("SKSB1", "F");
smap.put("SKSBF", "F");
smap.put("SKSBN", "F");
smap.put("SKSBS", "F");
smap.put("SKSBV", "F");
smap.put("SKSC", "F");
smap.put("SKSE(", "F");
smap.put("SKSE1", "F");
smap.put("SKSEF", "F");
smap.put("SKSEK", "F");
smap.put("SKSEN", "F");
smap.put("SKSES", "F");
smap.put("SKSEV", "F");
smap.put("SKSO(", "F");
smap.put("SKSO1", "F");
smap.put("SKSOF", "F");
smap.put("SKSON", "F");
smap.put("SKSOS", "F");
smap.put("SKSOV", "F");
smap.put("SKSU(", "F");
smap.put("SKSUE", "F");
smap.put("SKUE(", "F");
smap.put("SKUE1", "F");
smap.put("SKUEF", "F");
smap.put("SKUEK", "F");
smap.put("SKUEN", "F");
smap.put("SKUES", "F");
smap.put("SKUEV", "F");
smap.put("SKV", "F");
smap.put("SKV&(", "F");
smap.put("SKV&1", "F");
smap.put("SKV&F", "F");
smap.put("SKV&N", "F");
smap.put("SKV&S", "F");
smap.put("SKV&V", "F");
smap.put("SKV;", "F");
smap.put("SKV;C", "F");
smap.put("SKV;E", "F");
smap.put("SKV;T", "F");
smap.put("SKVB(", "F");
smap.put("SKVB1", "F");
smap.put("SKVBF", "F");
smap.put("SKVBN", "F");
smap.put("SKVBS", "F");
smap.put("SKVBV", "F");
smap.put("SKVC", "F");
smap.put("SKVE(", "F");
smap.put("SKVE1", "F");
smap.put("SKVEF", "F");
smap.put("SKVEK", "F");
smap.put("SKVEN", "F");
smap.put("SKVES", "F");
smap.put("SKVEV", "F");
smap.put("SKVO(", "F");
smap.put("SKVOF", "F");
smap.put("SKVOS", "F");
smap.put("SKVU(", "F");
smap.put("SKVUE", "F");
smap.put("SO(1&", "F");
smap.put("SO(1)", "F");
smap.put("SO(1,", "F");
smap.put("SO(1O", "F");
smap.put("SO(E(", "F");
smap.put("SO(E1", "F");
smap.put("SO(EE", "F");
smap.put("SO(EF", "F");
smap.put("SO(EK", "F");
smap.put("SO(EN", "F");
smap.put("SO(ES", "F");
smap.put("SO(EV", "F");
smap.put("SO(F(", "F");
smap.put("SO(N&", "F");
smap.put("SO(N)", "F");
smap.put("SO(N,", "F");
smap.put("SO(NO", "F");
smap.put("SO(S&", "F");
smap.put("SO(S)", "F");
smap.put("SO(S,", "F");
smap.put("SO(SO", "F");
smap.put("SO(V&", "F");
smap.put("SO(V)", "F");
smap.put("SO(V,", "F");
smap.put("SO(VO", "F");
smap.put("SO1&(", "F");
smap.put("SO1&1", "F");
smap.put("SO1&E", "F");
smap.put("SO1&F", "F");
smap.put("SO1&K", "F");
smap.put("SO1&N", "F");
smap.put("SO1&S", "F");
smap.put("SO1&U", "F");
smap.put("SO1&V", "F");
smap.put("SO1(E", "F");
smap.put("SO1(U", "F");
smap.put("SO1)&", "F");
smap.put("SO1),", "F");
smap.put("SO1);", "F");
smap.put("SO1)B", "F");
smap.put("SO1)C", "F");
smap.put("SO1)E", "F");
smap.put("SO1)K", "F");
smap.put("SO1)O", "F");
smap.put("SO1)U", "F");
smap.put("SO1,(", "F");
smap.put("SO1,F", "F");
smap.put("SO1;", "F");
smap.put("SO1;C", "F");
smap.put("SO1;E", "F");
smap.put("SO1;N", "F");
smap.put("SO1;T", "F");
smap.put("SO1A(", "F");
smap.put("SO1AF", "F");
smap.put("SO1AS", "F");
smap.put("SO1AT", "F");
smap.put("SO1AV", "F");
smap.put("SO1B(", "F");
smap.put("SO1B1", "F");
smap.put("SO1BE", "F");
smap.put("SO1BF", "F");
smap.put("SO1BN", "F");
smap.put("SO1BS", "F");
smap.put("SO1BV", "F");
smap.put("SO1C", "F");
smap.put("SO1E(", "F");
smap.put("SO1E1", "F");
smap.put("SO1EF", "F");
smap.put("SO1EK", "F");
smap.put("SO1EN", "F");
smap.put("SO1EO", "F");
smap.put("SO1ES", "F");
smap.put("SO1EU", "F");
smap.put("SO1EV", "F");
smap.put("SO1F(", "F");
smap.put("SO1K(", "F");
smap.put("SO1K)", "F");
smap.put("SO1K1", "F");
smap.put("SO1KB", "F");
smap.put("SO1KF", "F");
smap.put("SO1KN", "F");
smap.put("SO1KS", "F");
smap.put("SO1KU", "F");
smap.put("SO1KV", "F");
smap.put("SO1N&", "F");
smap.put("SO1N(", "F");
smap.put("SO1N,", "F");
smap.put("SO1NE", "F");
smap.put("SO1NF", "F");
smap.put("SO1NU", "F");
smap.put("SO1S(", "F");
smap.put("SO1SF", "F");
smap.put("SO1SU", "F");
smap.put("SO1SV", "F");
smap.put("SO1U", "F");
smap.put("SO1U(", "F");
smap.put("SO1U1", "F");
smap.put("SO1U;", "F");
smap.put("SO1UC", "F");
smap.put("SO1UE", "F");
smap.put("SO1UF", "F");
smap.put("SO1UK", "F");
smap.put("SO1UN", "F");
smap.put("SO1UO", "F");
smap.put("SO1US", "F");
smap.put("SO1UT", "F");
smap.put("SO1UV", "F");
smap.put("SO1V(", "F");
smap.put("SO1VF", "F");
smap.put("SO1VO", "F");
smap.put("SO1VS", "F");
smap.put("SO1VU", "F");
smap.put("SOF()", "F");
smap.put("SOF(1", "F");
smap.put("SOF(E", "F");
smap.put("SOF(F", "F");
smap.put("SOF(N", "F");
smap.put("SOF(S", "F");
smap.put("SOF(V", "F");
smap.put("SOK&(", "F");
smap.put("SOK&1", "F");
smap.put("SOK&F", "F");
smap.put("SOK&N", "F");
smap.put("SOK&S", "F");
smap.put("SOK&V", "F");
smap.put("SOK(1", "F");
smap.put("SOK(F", "F");
smap.put("SOK(N", "F");
smap.put("SOK(S", "F");
smap.put("SOK(V", "F");
smap.put("SOK1C", "F");
smap.put("SOK1O", "F");
smap.put("SOKF(", "F");
smap.put("SOKNC", "F");
smap.put("SOKO(", "F");
smap.put("SOKO1", "F");
smap.put("SOKOF", "F");
smap.put("SOKON", "F");
smap.put("SOKOS", "F");
smap.put("SOKOV", "F");
smap.put("SOKSC", "F");
smap.put("SOKSO", "F");
smap.put("SOKVC", "F");
smap.put("SOKVO", "F");
smap.put("SON&(", "F");
smap.put("SON&1", "F");
smap.put("SON&E", "F");
smap.put("SON&F", "F");
smap.put("SON&K", "F");
smap.put("SON&N", "F");
smap.put("SON&S", "F");
smap.put("SON&U", "F");
smap.put("SON&V", "F");
smap.put("SON(1", "F");
smap.put("SON(E", "F");
smap.put("SON(F", "F");
smap.put("SON(S", "F");
smap.put("SON(U", "F");
smap.put("SON(V", "F");
smap.put("SON)&", "F");
smap.put("SON),", "F");
smap.put("SON);", "F");
smap.put("SON)B", "F");
smap.put("SON)C", "F");
smap.put("SON)E", "F");
smap.put("SON)K", "F");
smap.put("SON)O", "F");
smap.put("SON)U", "F");
smap.put("SON,(", "F");
smap.put("SON,F", "F");
smap.put("SON1(", "F");
smap.put("SON1F", "F");
smap.put("SON1N", "F");
smap.put("SON1O", "F");
smap.put("SON1S", "F");
smap.put("SON1U", "F");
smap.put("SON1V", "F");
smap.put("SON;", "F");
smap.put("SON;C", "F");
smap.put("SON;E", "F");
smap.put("SON;N", "F");
smap.put("SON;T", "F");
smap.put("SONA(", "F");
smap.put("SONAF", "F");
smap.put("SONAS", "F");
smap.put("SONAT", "F");
smap.put("SONAV", "F");
smap.put("SONB(", "F");
smap.put("SONB1", "F");
smap.put("SONBE", "F");
smap.put("SONBF", "F");
smap.put("SONBN", "F");
smap.put("SONBS", "F");
smap.put("SONBV", "F");
smap.put("SONE(", "F");
smap.put("SONE1", "F");
smap.put("SONEF", "F");
smap.put("SONEN", "F");
smap.put("SONEO", "F");
smap.put("SONES", "F");
smap.put("SONEU", "F");
smap.put("SONEV", "F");
smap.put("SONF(", "F");
smap.put("SONK(", "F");
smap.put("SONK)", "F");
smap.put("SONK1", "F");
smap.put("SONKB", "F");
smap.put("SONKF", "F");
smap.put("SONKS", "F");
smap.put("SONKU", "F");
smap.put("SONKV", "F");
smap.put("SONSU", "F");
smap.put("SONU", "F");
smap.put("SONU(", "F");
smap.put("SONU1", "F");
smap.put("SONU;", "F");
smap.put("SONUC", "F");
smap.put("SONUE", "F");
smap.put("SONUF", "F");
smap.put("SONUK", "F");
smap.put("SONUN", "F");
smap.put("SONUO", "F");
smap.put("SONUS", "F");
smap.put("SONUT", "F");
smap.put("SONUV", "F");
smap.put("SOS", "F");
smap.put("SOS&(", "F");
smap.put("SOS&1", "F");
smap.put("SOS&E", "F");
smap.put("SOS&F", "F");
smap.put("SOS&K", "F");
smap.put("SOS&N", "F");
smap.put("SOS&S", "F");
smap.put("SOS&U", "F");
smap.put("SOS&V", "F");
smap.put("SOS(E", "F");
smap.put("SOS(U", "F");
smap.put("SOS)&", "F");
smap.put("SOS),", "F");
smap.put("SOS);", "F");
smap.put("SOS)B", "F");
smap.put("SOS)C", "F");
smap.put("SOS)E", "F");
smap.put("SOS)K", "F");
smap.put("SOS)O", "F");
smap.put("SOS)U", "F");
smap.put("SOS,(", "F");
smap.put("SOS,F", "F");
smap.put("SOS1(", "F");
smap.put("SOS1F", "F");
smap.put("SOS1N", "F");
smap.put("SOS1O", "F");
smap.put("SOS1S", "F");
smap.put("SOS1U", "F");
smap.put("SOS1V", "F");
smap.put("SOS;", "F");
smap.put("SOS;C", "F");
smap.put("SOS;E", "F");
smap.put("SOS;N", "F");
smap.put("SOS;T", "F");
smap.put("SOSA(", "F");
smap.put("SOSAF", "F");
smap.put("SOSAS", "F");
smap.put("SOSAT", "F");
smap.put("SOSAV", "F");
smap.put("SOSB(", "F");
smap.put("SOSB1", "F");
smap.put("SOSBE", "F");
smap.put("SOSBF", "F");
smap.put("SOSBN", "F");
smap.put("SOSBS", "F");
smap.put("SOSBV", "F");
smap.put("SOSC", "F");
smap.put("SOSE(", "F");
smap.put("SOSE1", "F");
smap.put("SOSEF", "F");
smap.put("SOSEK", "F");
smap.put("SOSEN", "F");
smap.put("SOSEO", "F");
smap.put("SOSES", "F");
smap.put("SOSEU", "F");
smap.put("SOSEV", "F");
smap.put("SOSF(", "F");
smap.put("SOSK(", "F");
smap.put("SOSK)", "F");
smap.put("SOSK1", "F");
smap.put("SOSKB", "F");
smap.put("SOSKF", "F");
smap.put("SOSKN", "F");
smap.put("SOSKS", "F");
smap.put("SOSKU", "F");
smap.put("SOSKV", "F");
smap.put("SOSU", "F");
smap.put("SOSU(", "F");
smap.put("SOSU1", "F");
smap.put("SOSU;", "F");
smap.put("SOSUC", "F");
smap.put("SOSUE", "F");
smap.put("SOSUF", "F");
smap.put("SOSUK", "F");
smap.put("SOSUN", "F");
smap.put("SOSUO", "F");
smap.put("SOSUS", "F");
smap.put("SOSUT", "F");
smap.put("SOSUV", "F");
smap.put("SOSV(", "F");
smap.put("SOSVF", "F");
smap.put("SOSVO", "F");
smap.put("SOSVS", "F");
smap.put("SOSVU", "F");
smap.put("SOU(E", "F");
smap.put("SOUEK", "F");
smap.put("SOUEN", "F");
smap.put("SOV", "F");
smap.put("SOV&(", "F");
smap.put("SOV&1", "F");
smap.put("SOV&E", "F");
smap.put("SOV&F", "F");
smap.put("SOV&K", "F");
smap.put("SOV&N", "F");
smap.put("SOV&S", "F");
smap.put("SOV&U", "F");
smap.put("SOV&V", "F");
smap.put("SOV(E", "F");
smap.put("SOV(U", "F");
smap.put("SOV)&", "F");
smap.put("SOV),", "F");
smap.put("SOV);", "F");
smap.put("SOV)B", "F");
smap.put("SOV)C", "F");
smap.put("SOV)E", "F");
smap.put("SOV)K", "F");
smap.put("SOV)O", "F");
smap.put("SOV)U", "F");
smap.put("SOV,(", "F");
smap.put("SOV,F", "F");
smap.put("SOV;", "F");
smap.put("SOV;C", "F");
smap.put("SOV;E", "F");
}
//--------------------------------------------------------------------------------
private static void initialize3()
{
smap.put("SOV;N", "F");
smap.put("SOV;T", "F");
smap.put("SOVA(", "F");
smap.put("SOVAF", "F");
smap.put("SOVAS", "F");
smap.put("SOVAT", "F");
smap.put("SOVAV", "F");
smap.put("SOVB(", "F");
smap.put("SOVB1", "F");
smap.put("SOVBE", "F");
smap.put("SOVBF", "F");
smap.put("SOVBN", "F");
smap.put("SOVBS", "F");
smap.put("SOVBV", "F");
smap.put("SOVC", "F");
smap.put("SOVE(", "F");
smap.put("SOVE1", "F");
smap.put("SOVEF", "F");
smap.put("SOVEK", "F");
smap.put("SOVEN", "F");
smap.put("SOVEO", "F");
smap.put("SOVES", "F");
smap.put("SOVEU", "F");
smap.put("SOVEV", "F");
smap.put("SOVF(", "F");
smap.put("SOVK(", "F");
smap.put("SOVK)", "F");
smap.put("SOVK1", "F");
smap.put("SOVKB", "F");
smap.put("SOVKF", "F");
smap.put("SOVKN", "F");
smap.put("SOVKS", "F");
smap.put("SOVKU", "F");
smap.put("SOVKV", "F");
smap.put("SOVO(", "F");
smap.put("SOVOF", "F");
smap.put("SOVOK", "F");
smap.put("SOVOS", "F");
smap.put("SOVOU", "F");
smap.put("SOVS(", "F");
smap.put("SOVS1", "F");
smap.put("SOVSF", "F");
smap.put("SOVSO", "F");
smap.put("SOVSU", "F");
smap.put("SOVSV", "F");
smap.put("SOVU", "F");
smap.put("SOVU(", "F");
smap.put("SOVU1", "F");
smap.put("SOVU;", "F");
smap.put("SOVUC", "F");
smap.put("SOVUE", "F");
smap.put("SOVUF", "F");
smap.put("SOVUK", "F");
smap.put("SOVUN", "F");
smap.put("SOVUO", "F");
smap.put("SOVUS", "F");
smap.put("SOVUT", "F");
smap.put("SOVUV", "F");
smap.put("SU(1)", "F");
smap.put("SU(1O", "F");
smap.put("SU(E(", "F");
smap.put("SU(E1", "F");
smap.put("SU(EF", "F");
smap.put("SU(EK", "F");
smap.put("SU(EN", "F");
smap.put("SU(ES", "F");
smap.put("SU(EV", "F");
smap.put("SU(F(", "F");
smap.put("SU(N)", "F");
smap.put("SU(NO", "F");
smap.put("SU(S)", "F");
smap.put("SU(SO", "F");
smap.put("SU(V)", "F");
smap.put("SU(VO", "F");
smap.put("SU1,(", "F");
smap.put("SU1,F", "F");
smap.put("SU1C", "F");
smap.put("SU1O(", "F");
smap.put("SU1OF", "F");
smap.put("SU1OS", "F");
smap.put("SU1OV", "F");
smap.put("SU;", "F");
smap.put("SU;C", "F");
smap.put("SUC", "F");
smap.put("SUE", "F");
smap.put("SUE(1", "F");
smap.put("SUE(E", "F");
smap.put("SUE(F", "F");
smap.put("SUE(N", "F");
smap.put("SUE(O", "F");
smap.put("SUE(S", "F");
smap.put("SUE(V", "F");
smap.put("SUE1", "F");
smap.put("SUE1&", "F");
smap.put("SUE1(", "F");
smap.put("SUE1)", "F");
smap.put("SUE1,", "F");
smap.put("SUE1;", "F");
smap.put("SUE1B", "F");
smap.put("SUE1C", "F");
smap.put("SUE1F", "F");
smap.put("SUE1K", "F");
smap.put("SUE1N", "F");
smap.put("SUE1O", "F");
smap.put("SUE1S", "F");
smap.put("SUE1U", "F");
smap.put("SUE1V", "F");
smap.put("SUE;", "F");
smap.put("SUE;C", "F");
smap.put("SUEC", "F");
smap.put("SUEF", "F");
smap.put("SUEF(", "F");
smap.put("SUEF,", "F");
smap.put("SUEF;", "F");
smap.put("SUEFC", "F");
smap.put("SUEK", "F");
smap.put("SUEK(", "F");
smap.put("SUEK1", "F");
smap.put("SUEK;", "F");
smap.put("SUEKC", "F");
smap.put("SUEKF", "F");
smap.put("SUEKN", "F");
smap.put("SUEKO", "F");
smap.put("SUEKS", "F");
smap.put("SUEKV", "F");
smap.put("SUEN", "F");
smap.put("SUEN&", "F");
smap.put("SUEN(", "F");
smap.put("SUEN)", "F");
smap.put("SUEN,", "F");
smap.put("SUEN1", "F");
smap.put("SUEN;", "F");
smap.put("SUENB", "F");
smap.put("SUENC", "F");
smap.put("SUENF", "F");
smap.put("SUENK", "F");
smap.put("SUENO", "F");
smap.put("SUENS", "F");
smap.put("SUENU", "F");
smap.put("SUEOK", "F");
smap.put("SUEON", "F");
smap.put("SUEOO", "F");
smap.put("SUES", "F");
smap.put("SUES&", "F");
smap.put("SUES(", "F");
smap.put("SUES)", "F");
smap.put("SUES,", "F");
smap.put("SUES1", "F");
smap.put("SUES;", "F");
smap.put("SUESB", "F");
smap.put("SUESC", "F");
smap.put("SUESF", "F");
smap.put("SUESK", "F");
smap.put("SUESO", "F");
smap.put("SUESU", "F");
smap.put("SUESV", "F");
smap.put("SUEV", "F");
smap.put("SUEV&", "F");
smap.put("SUEV(", "F");
smap.put("SUEV)", "F");
smap.put("SUEV,", "F");
smap.put("SUEV;", "F");
smap.put("SUEVB", "F");
smap.put("SUEVC", "F");
smap.put("SUEVF", "F");
smap.put("SUEVK", "F");
smap.put("SUEVN", "F");
smap.put("SUEVO", "F");
smap.put("SUEVS", "F");
smap.put("SUEVU", "F");
smap.put("SUF()", "F");
smap.put("SUF(1", "F");
smap.put("SUF(F", "F");
smap.put("SUF(N", "F");
smap.put("SUF(S", "F");
smap.put("SUF(V", "F");
smap.put("SUK(E", "F");
smap.put("SUN(1", "F");
smap.put("SUN(F", "F");
smap.put("SUN(S", "F");
smap.put("SUN(V", "F");
smap.put("SUN,(", "F");
smap.put("SUN,F", "F");
smap.put("SUN1(", "F");
smap.put("SUN1,", "F");
smap.put("SUN1O", "F");
smap.put("SUNC", "F");
smap.put("SUNE(", "F");
smap.put("SUNE1", "F");
smap.put("SUNEF", "F");
smap.put("SUNEN", "F");
smap.put("SUNES", "F");
smap.put("SUNEV", "F");
smap.put("SUNF(", "F");
smap.put("SUNO(", "F");
smap.put("SUNOF", "F");
smap.put("SUNOS", "F");
smap.put("SUNOV", "F");
smap.put("SUNS(", "F");
smap.put("SUNS,", "F");
smap.put("SUNSO", "F");
smap.put("SUO(E", "F");
smap.put("SUON(", "F");
smap.put("SUON1", "F");
smap.put("SUONF", "F");
smap.put("SUONS", "F");
smap.put("SUS,(", "F");
smap.put("SUS,F", "F");
smap.put("SUSC", "F");
smap.put("SUSO(", "F");
smap.put("SUSO1", "F");
smap.put("SUSOF", "F");
smap.put("SUSON", "F");
smap.put("SUSOS", "F");
smap.put("SUSOV", "F");
smap.put("SUTN(", "F");
smap.put("SUTN1", "F");
smap.put("SUTNF", "F");
smap.put("SUTNS", "F");
smap.put("SUV,(", "F");
smap.put("SUV,F", "F");
smap.put("SUVC", "F");
smap.put("SUVO(", "F");
smap.put("SUVOF", "F");
smap.put("SUVOS", "F");
smap.put("SVF()", "F");
smap.put("SVF(1", "F");
smap.put("SVF(F", "F");
smap.put("SVF(N", "F");
smap.put("SVF(S", "F");
smap.put("SVF(V", "F");
smap.put("SVO(1", "F");
smap.put("SVO(F", "F");
smap.put("SVO(N", "F");
smap.put("SVO(S", "F");
smap.put("SVO(V", "F");
smap.put("SVOF(", "F");
smap.put("SVOS(", "F");
smap.put("SVOS1", "F");
smap.put("SVOSF", "F");
smap.put("SVOSU", "F");
smap.put("SVOSV", "F");
smap.put("SVS", "F");
smap.put("SVS;", "F");
smap.put("SVS;C", "F");
smap.put("SVSC", "F");
smap.put("SVSO(", "F");
smap.put("SVSO1", "F");
smap.put("SVSOF", "F");
smap.put("SVSON", "F");
smap.put("SVSOS", "F");
smap.put("SVSOV", "F");
smap.put("SVUE", "F");
smap.put("SVUE;", "F");
smap.put("SVUEC", "F");
smap.put("SVUEK", "F");
smap.put("T(1)F", "F");
smap.put("T(1)O", "F");
smap.put("T(1F(", "F");
smap.put("T(1N)", "F");
smap.put("T(1O(", "F");
smap.put("T(1OF", "F");
smap.put("T(1OS", "F");
smap.put("T(1OV", "F");
smap.put("T(1S)", "F");
smap.put("T(1V)", "F");
smap.put("T(1VO", "F");
smap.put("T(F()", "F");
smap.put("T(F(1", "F");
smap.put("T(F(F", "F");
smap.put("T(F(N", "F");
smap.put("T(F(S", "F");
smap.put("T(F(V", "F");
smap.put("T(N(1", "F");
smap.put("T(N(F", "F");
smap.put("T(N(S", "F");
smap.put("T(N(V", "F");
smap.put("T(N)F", "F");
smap.put("T(N)O", "F");
smap.put("T(N1)", "F");
smap.put("T(N1O", "F");
smap.put("T(NF(", "F");
smap.put("T(NO(", "F");
smap.put("T(NOF", "F");
smap.put("T(NOS", "F");
smap.put("T(NOV", "F");
smap.put("T(NS)", "F");
smap.put("T(NSO", "F");
smap.put("T(S)F", "F");
smap.put("T(S)O", "F");
smap.put("T(S1)", "F");
smap.put("T(S1O", "F");
smap.put("T(SF(", "F");
smap.put("T(SN)", "F");
smap.put("T(SNO", "F");
smap.put("T(SO(", "F");
smap.put("T(SO1", "F");
smap.put("T(SOF", "F");
smap.put("T(SON", "F");
smap.put("T(SOS", "F");
smap.put("T(SOV", "F");
smap.put("T(SV)", "F");
smap.put("T(SVO", "F");
smap.put("T(V)F", "F");
smap.put("T(V)O", "F");
smap.put("T(VF(", "F");
smap.put("T(VO(", "F");
smap.put("T(VOF", "F");
smap.put("T(VOS", "F");
smap.put("T(VS)", "F");
smap.put("T(VSO", "F");
smap.put("T(VV)", "F");
smap.put("T1F(1", "F");
smap.put("T1F(F", "F");
smap.put("T1F(N", "F");
smap.put("T1F(S", "F");
smap.put("T1F(V", "F");
smap.put("T1O(1", "F");
smap.put("T1O(F", "F");
smap.put("T1O(N", "F");
smap.put("T1O(S", "F");
smap.put("T1O(V", "F");
smap.put("T1OF(", "F");
smap.put("T1OSF", "F");
smap.put("T1OVF", "F");
smap.put("T1OVO", "F");
smap.put("TF()F", "F");
smap.put("TF()O", "F");
smap.put("TF(1)", "F");
smap.put("TF(1O", "F");
smap.put("TF(F(", "F");
smap.put("TF(N)", "F");
smap.put("TF(NO", "F");
smap.put("TF(S)", "F");
smap.put("TF(SO", "F");
smap.put("TF(V)", "F");
smap.put("TF(VO", "F");
smap.put("TN(1)", "F");
smap.put("TN(1O", "F");
smap.put("TN(F(", "F");
smap.put("TN(S)", "F");
smap.put("TN(SO", "F");
smap.put("TN(V)", "F");
smap.put("TN(VO", "F");
smap.put("TN1;", "F");
smap.put("TN1;C", "F");
smap.put("TN1O(", "F");
smap.put("TN1OF", "F");
smap.put("TN1OS", "F");
smap.put("TN1OV", "F");
smap.put("TNF()", "F");
smap.put("TNF(1", "F");
smap.put("TNF(F", "F");
smap.put("TNF(N", "F");
smap.put("TNF(S", "F");
smap.put("TNF(V", "F");
smap.put("TNO(1", "F");
smap.put("TNO(F", "F");
smap.put("TNO(N", "F");
smap.put("TNO(S", "F");
smap.put("TNO(V", "F");
smap.put("TNOF(", "F");
smap.put("TNOSF", "F");
smap.put("TNOVF", "F");
smap.put("TNOVO", "F");
smap.put("TNS;", "F");
smap.put("TNS;C", "F");
smap.put("TNSO(", "F");
smap.put("TNSO1", "F");
smap.put("TNSOF", "F");
smap.put("TNSON", "F");
smap.put("TNSOS", "F");
smap.put("TNSOV", "F");
smap.put("TNV;", "F");
smap.put("TNVOS", "F");
smap.put("TSF(1", "F");
smap.put("TSF(F", "F");
smap.put("TSF(N", "F");
smap.put("TSF(S", "F");
smap.put("TSF(V", "F");
smap.put("TSO(1", "F");
smap.put("TSO(F", "F");
smap.put("TSO(N", "F");
smap.put("TSO(S", "F");
smap.put("TSO(V", "F");
smap.put("TSO1F", "F");
smap.put("TSOF(", "F");
smap.put("TSONF", "F");
smap.put("TSOSF", "F");
smap.put("TSOVF", "F");
smap.put("TSOVO", "F");
smap.put("TVF(1", "F");
smap.put("TVF(F", "F");
smap.put("TVF(N", "F");
smap.put("TVF(S", "F");
smap.put("TVF(V", "F");
smap.put("TVO(1", "F");
smap.put("TVO(F", "F");
smap.put("TVO(N", "F");
smap.put("TVO(S", "F");
smap.put("TVO(V", "F");
smap.put("TVOF(", "F");
smap.put("TVOSF", "F");
smap.put("U(E(1", "F");
smap.put("U(E(F", "F");
smap.put("U(E(K", "F");
smap.put("U(E(N", "F");
smap.put("U(E(S", "F");
smap.put("U(E(V", "F");
smap.put("U(E1)", "F");
smap.put("U(E1O", "F");
smap.put("U(EF(", "F");
smap.put("U(EK(", "F");
smap.put("U(EK1", "F");
smap.put("U(EKF", "F");
smap.put("U(EKN", "F");
smap.put("U(EKO", "F");
smap.put("U(EKS", "F");
smap.put("U(EKV", "F");
smap.put("U(EN)", "F");
smap.put("U(ENK", "F");
smap.put("U(ENO", "F");
smap.put("U(EOK", "F");
smap.put("U(ES)", "F");
smap.put("U(ESO", "F");
smap.put("U(EV)", "F");
smap.put("U(EVO", "F");
smap.put("UE(1)", "F");
smap.put("UE(1,", "F");
smap.put("UE(1O", "F");
smap.put("UE(F(", "F");
smap.put("UE(N)", "F");
smap.put("UE(N,", "F");
smap.put("UE(NO", "F");
smap.put("UE(S)", "F");
smap.put("UE(S,", "F");
smap.put("UE(SO", "F");
smap.put("UE(V)", "F");
smap.put("UE(V,", "F");
smap.put("UE(VO", "F");
smap.put("UE1", "F");
smap.put("UE1,(", "F");
smap.put("UE1,F", "F");
smap.put("UE1;", "F");
smap.put("UE1;C", "F");
smap.put("UE1C", "F");
smap.put("UE1K(", "F");
smap.put("UE1K1", "F");
smap.put("UE1KF", "F");
smap.put("UE1KN", "F");
smap.put("UE1KS", "F");
smap.put("UE1KV", "F");
smap.put("UE1O(", "F");
smap.put("UE1OF", "F");
smap.put("UE1OS", "F");
smap.put("UE1OV", "F");
smap.put("UEF()", "F");
smap.put("UEF(1", "F");
smap.put("UEF(F", "F");
smap.put("UEF(N", "F");
smap.put("UEF(S", "F");
smap.put("UEF(V", "F");
smap.put("UEK(1", "F");
smap.put("UEK(F", "F");
smap.put("UEK(N", "F");
smap.put("UEK(S", "F");
smap.put("UEK(V", "F");
smap.put("UEK1", "F");
smap.put("UEK1,", "F");
smap.put("UEK1;", "F");
smap.put("UEK1C", "F");
smap.put("UEK1K", "F");
smap.put("UEK1O", "F");
smap.put("UEKF(", "F");
smap.put("UEKN", "F");
smap.put("UEKN(", "F");
smap.put("UEKN,", "F");
smap.put("UEKN;", "F");
smap.put("UEKNC", "F");
smap.put("UEKNK", "F");
smap.put("UEKS", "F");
smap.put("UEKS,", "F");
smap.put("UEKS;", "F");
smap.put("UEKSC", "F");
smap.put("UEKSK", "F");
smap.put("UEKSO", "F");
smap.put("UEKV", "F");
smap.put("UEKV,", "F");
smap.put("UEKV;", "F");
smap.put("UEKVC", "F");
smap.put("UEKVK", "F");
smap.put("UEKVO", "F");
smap.put("UEN()", "F");
smap.put("UEN,(", "F");
smap.put("UEN,F", "F");
smap.put("UEN;", "F");
smap.put("UEN;C", "F");
smap.put("UENC", "F");
smap.put("UENK(", "F");
smap.put("UENK1", "F");
smap.put("UENKF", "F");
smap.put("UENKN", "F");
smap.put("UENKS", "F");
smap.put("UENKV", "F");
smap.put("UENO(", "F");
smap.put("UENOF", "F");
smap.put("UENOS", "F");
smap.put("UENOV", "F");
smap.put("UES", "F");
smap.put("UES,(", "F");
smap.put("UES,F", "F");
smap.put("UES;", "F");
smap.put("UES;C", "F");
smap.put("UESC", "F");
smap.put("UESK(", "F");
smap.put("UESK1", "F");
smap.put("UESKF", "F");
smap.put("UESKN", "F");
smap.put("UESKS", "F");
smap.put("UESKV", "F");
smap.put("UESO(", "F");
smap.put("UESO1", "F");
smap.put("UESOF", "F");
smap.put("UESON", "F");
smap.put("UESOS", "F");
smap.put("UESOV", "F");
smap.put("UEV", "F");
smap.put("UEV,(", "F");
smap.put("UEV,F", "F");
smap.put("UEV;", "F");
smap.put("UEV;C", "F");
smap.put("UEVC", "F");
smap.put("UEVK(", "F");
smap.put("UEVK1", "F");
smap.put("UEVKF", "F");
smap.put("UEVKN", "F");
smap.put("UEVKS", "F");
smap.put("UEVKV", "F");
smap.put("UEVO(", "F");
smap.put("UEVOF", "F");
smap.put("UEVOS", "F");
smap.put("UF(1O", "F");
smap.put("UF(F(", "F");
smap.put("UF(NO", "F");
smap.put("UF(SO", "F");
smap.put("UF(VO", "F");
smap.put("V&(1&", "F");
smap.put("V&(1)", "F");
smap.put("V&(1,", "F");
smap.put("V&(1O", "F");
smap.put("V&(E(", "F");
smap.put("V&(E1", "F");
smap.put("V&(EF", "F");
smap.put("V&(EK", "F");
smap.put("V&(EN", "F");
smap.put("V&(EO", "F");
smap.put("V&(ES", "F");
smap.put("V&(EV", "F");
smap.put("V&(F(", "F");
smap.put("V&(N&", "F");
smap.put("V&(N)", "F");
smap.put("V&(N,", "F");
smap.put("V&(NO", "F");
smap.put("V&(S&", "F");
smap.put("V&(S)", "F");
smap.put("V&(S,", "F");
smap.put("V&(SO", "F");
smap.put("V&(V&", "F");
smap.put("V&(V)", "F");
smap.put("V&(V,", "F");
smap.put("V&(VO", "F");
smap.put("V&1", "F");
smap.put("V&1&(", "F");
smap.put("V&1&1", "F");
smap.put("V&1&F", "F");
smap.put("V&1&N", "F");
smap.put("V&1&S", "F");
smap.put("V&1&V", "F");
smap.put("V&1)&", "F");
smap.put("V&1)C", "F");
smap.put("V&1)O", "F");
smap.put("V&1)U", "F");
smap.put("V&1;", "F");
smap.put("V&1;C", "F");
smap.put("V&1;E", "F");
smap.put("V&1;T", "F");
smap.put("V&1B(", "F");
smap.put("V&1B1", "F");
smap.put("V&1BF", "F");
smap.put("V&1BN", "F");
smap.put("V&1BS", "F");
smap.put("V&1BV", "F");
smap.put("V&1C", "F");
smap.put("V&1EK", "F");
smap.put("V&1EN", "F");
smap.put("V&1F(", "F");
smap.put("V&1K(", "F");
smap.put("V&1K1", "F");
smap.put("V&1KF", "F");
smap.put("V&1KN", "F");
smap.put("V&1KS", "F");
smap.put("V&1KV", "F");
smap.put("V&1O(", "F");
smap.put("V&1OF", "F");
smap.put("V&1OO", "F");
smap.put("V&1OS", "F");
smap.put("V&1OV", "F");
smap.put("V&1TN", "F");
smap.put("V&1U", "F");
smap.put("V&1U(", "F");
smap.put("V&1U;", "F");
smap.put("V&1UC", "F");
smap.put("V&1UE", "F");
smap.put("V&E(1", "F");
smap.put("V&E(F", "F");
smap.put("V&E(N", "F");
smap.put("V&E(O", "F");
smap.put("V&E(S", "F");
smap.put("V&E(V", "F");
smap.put("V&E1", "F");
smap.put("V&E1;", "F");
smap.put("V&E1C", "F");
smap.put("V&E1K", "F");
smap.put("V&E1O", "F");
smap.put("V&EF(", "F");
smap.put("V&EK(", "F");
smap.put("V&EK1", "F");
smap.put("V&EKF", "F");
smap.put("V&EKN", "F");
smap.put("V&EKS", "F");
smap.put("V&EKV", "F");
smap.put("V&EN", "F");
smap.put("V&EN;", "F");
smap.put("V&ENC", "F");
smap.put("V&ENK", "F");
smap.put("V&ENO", "F");
smap.put("V&ES", "F");
smap.put("V&ES;", "F");
smap.put("V&ESC", "F");
smap.put("V&ESK", "F");
smap.put("V&ESO", "F");
smap.put("V&EV", "F");
smap.put("V&EV;", "F");
smap.put("V&EVC", "F");
smap.put("V&EVK", "F");
smap.put("V&EVO", "F");
smap.put("V&F()", "F");
smap.put("V&F(1", "F");
smap.put("V&F(E", "F");
smap.put("V&F(F", "F");
smap.put("V&F(N", "F");
smap.put("V&F(S", "F");
smap.put("V&F(V", "F");
smap.put("V&K&(", "F");
smap.put("V&K&1", "F");
smap.put("V&K&F", "F");
smap.put("V&K&N", "F");
smap.put("V&K&S", "F");
smap.put("V&K&V", "F");
smap.put("V&K(1", "F");
smap.put("V&K(F", "F");
smap.put("V&K(N", "F");
smap.put("V&K(S", "F");
smap.put("V&K(V", "F");
smap.put("V&K1O", "F");
smap.put("V&KF(", "F");
smap.put("V&KNK", "F");
smap.put("V&KO(", "F");
smap.put("V&KO1", "F");
smap.put("V&KOF", "F");
smap.put("V&KOK", "F");
smap.put("V&KON", "F");
smap.put("V&KOS", "F");
smap.put("V&KOV", "F");
smap.put("V&KSO", "F");
smap.put("V&KVO", "F");
smap.put("V&N", "F");
smap.put("V&N&(", "F");
smap.put("V&N&1", "F");
smap.put("V&N&F", "F");
smap.put("V&N&N", "F");
smap.put("V&N&S", "F");
smap.put("V&N&V", "F");
smap.put("V&N)&", "F");
smap.put("V&N)C", "F");
smap.put("V&N)O", "F");
smap.put("V&N)U", "F");
smap.put("V&N;", "F");
smap.put("V&N;C", "F");
smap.put("V&N;E", "F");
smap.put("V&N;T", "F");
smap.put("V&NB(", "F");
smap.put("V&NB1", "F");
smap.put("V&NBF", "F");
smap.put("V&NBN", "F");
smap.put("V&NBS", "F");
smap.put("V&NBV", "F");
smap.put("V&NC", "F");
smap.put("V&NEN", "F");
smap.put("V&NF(", "F");
smap.put("V&NK(", "F");
smap.put("V&NK1", "F");
smap.put("V&NKF", "F");
smap.put("V&NKN", "F");
smap.put("V&NKS", "F");
smap.put("V&NKV", "F");
smap.put("V&NO(", "F");
smap.put("V&NOF", "F");
smap.put("V&NOS", "F");
smap.put("V&NOV", "F");
smap.put("V&NTN", "F");
smap.put("V&NU", "F");
smap.put("V&NU(", "F");
smap.put("V&NU;", "F");
smap.put("V&NUC", "F");
smap.put("V&NUE", "F");
smap.put("V&S", "F");
smap.put("V&S&(", "F");
smap.put("V&S&1", "F");
smap.put("V&S&F", "F");
smap.put("V&S&N", "F");
smap.put("V&S&S", "F");
smap.put("V&S&V", "F");
smap.put("V&S)&", "F");
smap.put("V&S)C", "F");
smap.put("V&S)O", "F");
smap.put("V&S)U", "F");
smap.put("V&S1", "F");
smap.put("V&S1;", "F");
smap.put("V&S1C", "F");
smap.put("V&S1O", "F");
smap.put("V&S;", "F");
smap.put("V&S;C", "F");
smap.put("V&S;E", "F");
smap.put("V&S;T", "F");
smap.put("V&SB(", "F");
smap.put("V&SB1", "F");
smap.put("V&SBF", "F");
smap.put("V&SBN", "F");
smap.put("V&SBS", "F");
smap.put("V&SBV", "F");
smap.put("V&SC", "F");
smap.put("V&SEK", "F");
smap.put("V&SEN", "F");
smap.put("V&SF(", "F");
smap.put("V&SK(", "F");
smap.put("V&SK1", "F");
smap.put("V&SKF", "F");
smap.put("V&SKN", "F");
smap.put("V&SKS", "F");
smap.put("V&SKV", "F");
smap.put("V&SO(", "F");
smap.put("V&SO1", "F");
smap.put("V&SOF", "F");
smap.put("V&SON", "F");
smap.put("V&SOO", "F");
smap.put("V&SOS", "F");
smap.put("V&SOV", "F");
smap.put("V&STN", "F");
smap.put("V&SU", "F");
smap.put("V&SU(", "F");
smap.put("V&SU;", "F");
smap.put("V&SUC", "F");
smap.put("V&SUE", "F");
smap.put("V&SV", "F");
smap.put("V&SV;", "F");
smap.put("V&SVC", "F");
smap.put("V&SVO", "F");
smap.put("V&V", "F");
smap.put("V&V&(", "F");
smap.put("V&V&1", "F");
smap.put("V&V&F", "F");
smap.put("V&V&N", "F");
smap.put("V&V&S", "F");
smap.put("V&V&V", "F");
smap.put("V&V)&", "F");
smap.put("V&V)C", "F");
smap.put("V&V)O", "F");
smap.put("V&V)U", "F");
smap.put("V&V;", "F");
smap.put("V&V;C", "F");
smap.put("V&V;E", "F");
smap.put("V&V;T", "F");
smap.put("V&VB(", "F");
smap.put("V&VB1", "F");
smap.put("V&VBF", "F");
smap.put("V&VBN", "F");
smap.put("V&VBS", "F");
smap.put("V&VBV", "F");
smap.put("V&VC", "F");
smap.put("V&VEK", "F");
smap.put("V&VEN", "F");
smap.put("V&VF(", "F");
smap.put("V&VK(", "F");
smap.put("V&VK1", "F");
smap.put("V&VKF", "F");
smap.put("V&VKN", "F");
smap.put("V&VKS", "F");
smap.put("V&VKV", "F");
smap.put("V&VO(", "F");
smap.put("V&VOF", "F");
smap.put("V&VOO", "F");
smap.put("V&VOS", "F");
smap.put("V&VS", "F");
smap.put("V&VS;", "F");
smap.put("V&VSC", "F");
smap.put("V&VSO", "F");
smap.put("V&VTN", "F");
smap.put("V&VU", "F");
smap.put("V&VU(", "F");
smap.put("V&VU;", "F");
smap.put("V&VUC", "F");
smap.put("V&VUE", "F");
smap.put("V(EF(", "F");
smap.put("V(EKF", "F");
smap.put("V(EKN", "F");
smap.put("V(ENK", "F");
smap.put("V(U(E", "F");
smap.put("V)&(1", "F");
smap.put("V)&(E", "F");
smap.put("V)&(F", "F");
smap.put("V)&(N", "F");
smap.put("V)&(S", "F");
smap.put("V)&(V", "F");
smap.put("V)&1", "F");
smap.put("V)&1&", "F");
smap.put("V)&1)", "F");
smap.put("V)&1;", "F");
smap.put("V)&1B", "F");
smap.put("V)&1C", "F");
smap.put("V)&1F", "F");
smap.put("V)&1O", "F");
smap.put("V)&1U", "F");
smap.put("V)&F(", "F");
smap.put("V)&N", "F");
smap.put("V)&N&", "F");
smap.put("V)&N)", "F");
smap.put("V)&N;", "F");
smap.put("V)&NB", "F");
smap.put("V)&NC", "F");
smap.put("V)&NF", "F");
smap.put("V)&NO", "F");
smap.put("V)&NU", "F");
smap.put("V)&S", "F");
smap.put("V)&S&", "F");
smap.put("V)&S)", "F");
smap.put("V)&S;", "F");
smap.put("V)&SB", "F");
smap.put("V)&SC", "F");
smap.put("V)&SF", "F");
smap.put("V)&SO", "F");
smap.put("V)&SU", "F");
smap.put("V)&V", "F");
smap.put("V)&V&", "F");
smap.put("V)&V)", "F");
smap.put("V)&V;", "F");
smap.put("V)&VB", "F");
smap.put("V)&VC", "F");
smap.put("V)&VF", "F");
smap.put("V)&VO", "F");
smap.put("V)&VU", "F");
smap.put("V),(1", "F");
smap.put("V),(F", "F");
smap.put("V),(N", "F");
smap.put("V),(S", "F");
smap.put("V),(V", "F");
smap.put("V);E(", "F");
smap.put("V);E1", "F");
smap.put("V);EF", "F");
smap.put("V);EK", "F");
smap.put("V);EN", "F");
smap.put("V);EO", "F");
smap.put("V);ES", "F");
smap.put("V);EV", "F");
smap.put("V);T(", "F");
smap.put("V);T1", "F");
smap.put("V);TF", "F");
smap.put("V);TK", "F");
smap.put("V);TN", "F");
smap.put("V);TO", "F");
smap.put("V);TS", "F");
smap.put("V);TV", "F");
smap.put("V)B(1", "F");
smap.put("V)B(F", "F");
smap.put("V)B(N", "F");
smap.put("V)B(S", "F");
smap.put("V)B(V", "F");
smap.put("V)B1", "F");
smap.put("V)B1&", "F");
smap.put("V)B1;", "F");
smap.put("V)B1C", "F");
smap.put("V)B1K", "F");
smap.put("V)B1N", "F");
smap.put("V)B1O", "F");
smap.put("V)B1U", "F");
smap.put("V)BF(", "F");
smap.put("V)BN", "F");
smap.put("V)BN&", "F");
smap.put("V)BN;", "F");
smap.put("V)BNC", "F");
smap.put("V)BNK", "F");
smap.put("V)BNO", "F");
smap.put("V)BNU", "F");
smap.put("V)BS", "F");
smap.put("V)BS&", "F");
smap.put("V)BS;", "F");
smap.put("V)BSC", "F");
smap.put("V)BSK", "F");
smap.put("V)BSO", "F");
smap.put("V)BSU", "F");
smap.put("V)BV", "F");
smap.put("V)BV&", "F");
smap.put("V)BV;", "F");
smap.put("V)BVC", "F");
smap.put("V)BVK", "F");
smap.put("V)BVO", "F");
smap.put("V)BVU", "F");
smap.put("V)C", "F");
smap.put("V)E(1", "F");
smap.put("V)E(F", "F");
smap.put("V)E(N", "F");
smap.put("V)E(S", "F");
smap.put("V)E(V", "F");
smap.put("V)E1C", "F");
smap.put("V)E1O", "F");
smap.put("V)EF(", "F");
smap.put("V)EK(", "F");
smap.put("V)EK1", "F");
smap.put("V)EKF", "F");
smap.put("V)EKN", "F");
smap.put("V)EKS", "F");
smap.put("V)EKV", "F");
smap.put("V)ENC", "F");
smap.put("V)ENO", "F");
smap.put("V)ESC", "F");
smap.put("V)ESO", "F");
smap.put("V)EVC", "F");
smap.put("V)EVO", "F");
smap.put("V)K(1", "F");
smap.put("V)K(F", "F");
smap.put("V)K(N", "F");
smap.put("V)K(S", "F");
smap.put("V)K(V", "F");
smap.put("V)K1&", "F");
smap.put("V)K1;", "F");
smap.put("V)K1B", "F");
smap.put("V)K1E", "F");
smap.put("V)K1O", "F");
smap.put("V)K1U", "F");
smap.put("V)KB(", "F");
smap.put("V)KB1", "F");
smap.put("V)KBF", "F");
smap.put("V)KBN", "F");
smap.put("V)KBS", "F");
smap.put("V)KBV", "F");
smap.put("V)KF(", "F");
smap.put("V)KN&", "F");
smap.put("V)KN;", "F");
smap.put("V)KNB", "F");
smap.put("V)KNE", "F");
smap.put("V)KNK", "F");
smap.put("V)KNU", "F");
smap.put("V)KS&", "F");
smap.put("V)KS;", "F");
smap.put("V)KSB", "F");
smap.put("V)KSE", "F");
smap.put("V)KSO", "F");
smap.put("V)KSU", "F");
smap.put("V)KUE", "F");
smap.put("V)KV&", "F");
smap.put("V)KV;", "F");
smap.put("V)KVB", "F");
smap.put("V)KVE", "F");
smap.put("V)KVO", "F");
smap.put("V)KVU", "F");
smap.put("V)O(1", "F");
smap.put("V)O(E", "F");
smap.put("V)O(F", "F");
smap.put("V)O(N", "F");
smap.put("V)O(S", "F");
smap.put("V)O(V", "F");
smap.put("V)O1", "F");
smap.put("V)O1&", "F");
smap.put("V)O1)", "F");
smap.put("V)O1;", "F");
smap.put("V)O1B", "F");
smap.put("V)O1C", "F");
smap.put("V)O1K", "F");
smap.put("V)O1U", "F");
smap.put("V)OF(", "F");
smap.put("V)ON", "F");
smap.put("V)ON&", "F");
smap.put("V)ON)", "F");
smap.put("V)ON;", "F");
smap.put("V)ONB", "F");
smap.put("V)ONC", "F");
smap.put("V)ONK", "F");
smap.put("V)ONU", "F");
smap.put("V)OS", "F");
smap.put("V)OS&", "F");
smap.put("V)OS)", "F");
smap.put("V)OS;", "F");
smap.put("V)OSB", "F");
smap.put("V)OSC", "F");
smap.put("V)OSK", "F");
smap.put("V)OSU", "F");
smap.put("V)OV", "F");
smap.put("V)OV&", "F");
smap.put("V)OV)", "F");
smap.put("V)OV;", "F");
smap.put("V)OVB", "F");
smap.put("V)OVC", "F");
smap.put("V)OVK", "F");
smap.put("V)OVO", "F");
smap.put("V)OVU", "F");
smap.put("V)U(E", "F");
smap.put("V)UE(", "F");
smap.put("V)UE1", "F");
smap.put("V)UEF", "F");
smap.put("V)UEK", "F");
smap.put("V)UEN", "F");
smap.put("V)UES", "F");
smap.put("V)UEV", "F");
smap.put("V,(1)", "F");
smap.put("V,(1O", "F");
smap.put("V,(E(", "F");
smap.put("V,(E1", "F");
smap.put("V,(EF", "F");
smap.put("V,(EK", "F");
smap.put("V,(EN", "F");
smap.put("V,(ES", "F");
smap.put("V,(EV", "F");
smap.put("V,(F(", "F");
smap.put("V,(N)", "F");
smap.put("V,(NO", "F");
smap.put("V,(S)", "F");
smap.put("V,(SO", "F");
smap.put("V,(V)", "F");
smap.put("V,(VO", "F");
smap.put("V,F()", "F");
smap.put("V,F(1", "F");
smap.put("V,F(F", "F");
smap.put("V,F(N", "F");
smap.put("V,F(S", "F");
smap.put("V,F(V", "F");
smap.put("V;E(1", "F");
smap.put("V;E(E", "F");
smap.put("V;E(F", "F");
smap.put("V;E(N", "F");
smap.put("V;E(S", "F");
smap.put("V;E(V", "F");
smap.put("V;E1,", "F");
smap.put("V;E1;", "F");
smap.put("V;E1C", "F");
smap.put("V;E1K", "F");
smap.put("V;E1O", "F");
smap.put("V;E1T", "F");
smap.put("V;EF(", "F");
smap.put("V;EK(", "F");
smap.put("V;EK1", "F");
smap.put("V;EKF", "F");
smap.put("V;EKN", "F");
smap.put("V;EKO", "F");
smap.put("V;EKS", "F");
smap.put("V;EKV", "F");
smap.put("V;EN,", "F");
smap.put("V;EN;", "F");
smap.put("V;ENC", "F");
smap.put("V;ENE", "F");
smap.put("V;ENK", "F");
smap.put("V;ENO", "F");
smap.put("V;ENT", "F");
smap.put("V;ES,", "F");
smap.put("V;ES;", "F");
smap.put("V;ESC", "F");
smap.put("V;ESK", "F");
smap.put("V;ESO", "F");
smap.put("V;EST", "F");
smap.put("V;EV,", "F");
smap.put("V;EV;", "F");
smap.put("V;EVC", "F");
smap.put("V;EVK", "F");
smap.put("V;EVO", "F");
smap.put("V;EVT", "F");
smap.put("V;N:T", "F");
smap.put("V;T(1", "F");
smap.put("V;T(E", "F");
smap.put("V;T(F", "F");
smap.put("V;T(N", "F");
smap.put("V;T(S", "F");
smap.put("V;T(V", "F");
smap.put("V;T1,", "F");
smap.put("V;T1;", "F");
smap.put("V;T1C", "F");
smap.put("V;T1F", "F");
smap.put("V;T1K", "F");
smap.put("V;T1O", "F");
smap.put("V;T1T", "F");
smap.put("V;T;", "F");
smap.put("V;T;C", "F");
smap.put("V;TF(", "F");
smap.put("V;TK(", "F");
smap.put("V;TK1", "F");
smap.put("V;TKF", "F");
smap.put("V;TKK", "F");
smap.put("V;TKN", "F");
smap.put("V;TKO", "F");
smap.put("V;TKS", "F");
smap.put("V;TKV", "F");
smap.put("V;TN(", "F");
smap.put("V;TN,", "F");
smap.put("V;TN1", "F");
smap.put("V;TN;", "F");
smap.put("V;TNC", "F");
smap.put("V;TNE", "F");
smap.put("V;TNF", "F");
smap.put("V;TNK", "F");
smap.put("V;TNN", "F");
smap.put("V;TNO", "F");
smap.put("V;TNS", "F");
smap.put("V;TNT", "F");
smap.put("V;TNV", "F");
smap.put("V;TO(", "F");
smap.put("V;TS,", "F");
smap.put("V;TS;", "F");
smap.put("V;TSC", "F");
smap.put("V;TSF", "F");
smap.put("V;TSK", "F");
smap.put("V;TSO", "F");
smap.put("V;TST", "F");
smap.put("V;TT(", "F");
smap.put("V;TT1", "F");
smap.put("V;TTF", "F");
smap.put("V;TTN", "F");
smap.put("V;TTS", "F");
smap.put("V;TTV", "F");
smap.put("V;TV,", "F");
smap.put("V;TV;", "F");
smap.put("V;TVC", "F");
smap.put("V;TVF", "F");
smap.put("V;TVK", "F");
smap.put("V;TVO", "F");
smap.put("V;TVT", "F");
smap.put("VA(F(", "F");
smap.put("VA(N)", "F");
smap.put("VA(NO", "F");
smap.put("VA(S)", "F");
smap.put("VA(SO", "F");
smap.put("VA(V)", "F");
smap.put("VA(VO", "F");
smap.put("VAF()", "F");
smap.put("VAF(1", "F");
smap.put("VAF(F", "F");
smap.put("VAF(N", "F");
smap.put("VAF(S", "F");
smap.put("VAF(V", "F");
smap.put("VASO(", "F");
smap.put("VASO1", "F");
smap.put("VASOF", "F");
smap.put("VASON", "F");
smap.put("VASOS", "F");
smap.put("VASOV", "F");
smap.put("VASUE", "F");
smap.put("VATO(", "F");
smap.put("VATO1", "F");
smap.put("VATOF", "F");
smap.put("VATON", "F");
smap.put("VATOS", "F");
smap.put("VATOV", "F");
smap.put("VATUE", "F");
smap.put("VAVO(", "F");
smap.put("VAVOF", "F");
smap.put("VAVOS", "F");
smap.put("VAVUE", "F");
smap.put("VB(1)", "F");
smap.put("VB(1O", "F");
smap.put("VB(F(", "F");
smap.put("VB(N)", "F");
smap.put("VB(NO", "F");
smap.put("VB(S)", "F");
smap.put("VB(SO", "F");
smap.put("VB(V)", "F");
smap.put("VB(VO", "F");
smap.put("VB1", "F");
smap.put("VB1&(", "F");
smap.put("VB1&1", "F");
smap.put("VB1&F", "F");
smap.put("VB1&N", "F");
smap.put("VB1&S", "F");
smap.put("VB1&V", "F");
smap.put("VB1,(", "F");
smap.put("VB1,F", "F");
smap.put("VB1;", "F");
smap.put("VB1;C", "F");
smap.put("VB1B(", "F");
smap.put("VB1B1", "F");
smap.put("VB1BF", "F");
smap.put("VB1BN", "F");
smap.put("VB1BS", "F");
smap.put("VB1BV", "F");
smap.put("VB1C", "F");
smap.put("VB1K(", "F");
smap.put("VB1K1", "F");
smap.put("VB1KF", "F");
smap.put("VB1KN", "F");
smap.put("VB1KS", "F");
smap.put("VB1KV", "F");
smap.put("VB1O(", "F");
smap.put("VB1OF", "F");
smap.put("VB1OS", "F");
smap.put("VB1OV", "F");
smap.put("VB1U(", "F");
smap.put("VB1UE", "F");
smap.put("VBE(1", "F");
smap.put("VBE(F", "F");
smap.put("VBE(N", "F");
smap.put("VBE(S", "F");
smap.put("VBE(V", "F");
smap.put("VBEK(", "F");
smap.put("VBF()", "F");
smap.put("VBF(1", "F");
smap.put("VBF(F", "F");
smap.put("VBF(N", "F");
smap.put("VBF(S", "F");
smap.put("VBF(V", "F");
smap.put("VBN", "F");
smap.put("VBN&(", "F");
smap.put("VBN&1", "F");
smap.put("VBN&F", "F");
smap.put("VBN&N", "F");
smap.put("VBN&S", "F");
smap.put("VBN&V", "F");
smap.put("VBN,(", "F");
smap.put("VBN,F", "F");
smap.put("VBN;", "F");
smap.put("VBN;C", "F");
smap.put("VBNB(", "F");
smap.put("VBNB1", "F");
smap.put("VBNBF", "F");
smap.put("VBNBN", "F");
smap.put("VBNBS", "F");
smap.put("VBNBV", "F");
smap.put("VBNC", "F");
smap.put("VBNK(", "F");
smap.put("VBNK1", "F");
smap.put("VBNKF", "F");
smap.put("VBNKN", "F");
smap.put("VBNKS", "F");
smap.put("VBNKV", "F");
smap.put("VBNO(", "F");
smap.put("VBNOF", "F");
smap.put("VBNOS", "F");
smap.put("VBNOV", "F");
smap.put("VBNU(", "F");
smap.put("VBNUE", "F");
smap.put("VBS", "F");
smap.put("VBS&(", "F");
smap.put("VBS&1", "F");
smap.put("VBS&F", "F");
smap.put("VBS&N", "F");
smap.put("VBS&S", "F");
smap.put("VBS&V", "F");
smap.put("VBS,(", "F");
smap.put("VBS,F", "F");
smap.put("VBS;", "F");
smap.put("VBS;C", "F");
smap.put("VBSB(", "F");
smap.put("VBSB1", "F");
smap.put("VBSBF", "F");
smap.put("VBSBN", "F");
smap.put("VBSBS", "F");
smap.put("VBSBV", "F");
smap.put("VBSC", "F");
smap.put("VBSK(", "F");
smap.put("VBSK1", "F");
smap.put("VBSKF", "F");
smap.put("VBSKN", "F");
smap.put("VBSKS", "F");
smap.put("VBSKV", "F");
smap.put("VBSO(", "F");
smap.put("VBSO1", "F");
smap.put("VBSOF", "F");
smap.put("VBSON", "F");
smap.put("VBSOS", "F");
smap.put("VBSOV", "F");
smap.put("VBSU(", "F");
smap.put("VBSUE", "F");
smap.put("VBV", "F");
smap.put("VBV&(", "F");
smap.put("VBV&1", "F");
smap.put("VBV&F", "F");
smap.put("VBV&N", "F");
smap.put("VBV&S", "F");
smap.put("VBV&V", "F");
smap.put("VBV,(", "F");
smap.put("VBV,F", "F");
smap.put("VBV;", "F");
smap.put("VBV;C", "F");
smap.put("VBVB(", "F");
smap.put("VBVB1", "F");
smap.put("VBVBF", "F");
smap.put("VBVBN", "F");
smap.put("VBVBS", "F");
smap.put("VBVBV", "F");
smap.put("VBVC", "F");
smap.put("VBVK(", "F");
smap.put("VBVK1", "F");
smap.put("VBVKF", "F");
smap.put("VBVKN", "F");
smap.put("VBVKS", "F");
smap.put("VBVKV", "F");
smap.put("VBVO(", "F");
smap.put("VBVOF", "F");
smap.put("VBVOS", "F");
smap.put("VBVU(", "F");
smap.put("VBVUE", "F");
smap.put("VC", "F");
smap.put("VE(1)", "F");
smap.put("VE(1O", "F");
smap.put("VE(F(", "F");
smap.put("VE(N)", "F");
smap.put("VE(NO", "F");
smap.put("VE(S)", "F");
smap.put("VE(SO", "F");
smap.put("VE(V)", "F");
smap.put("VE(VO", "F");
smap.put("VE1C", "F");
smap.put("VE1O(", "F");
smap.put("VE1OF", "F");
smap.put("VE1OS", "F");
smap.put("VE1OV", "F");
smap.put("VE1UE", "F");
smap.put("VEF()", "F");
smap.put("VEF(1", "F");
smap.put("VEF(F", "F");
smap.put("VEF(N", "F");
smap.put("VEF(S", "F");
smap.put("VEF(V", "F");
smap.put("VEK(1", "F");
smap.put("VEK(E", "F");
smap.put("VEK(F", "F");
smap.put("VEK(N", "F");
smap.put("VEK(S", "F");
smap.put("VEK(V", "F");
smap.put("VEK1C", "F");
smap.put("VEK1O", "F");
smap.put("VEK1U", "F");
smap.put("VEKF(", "F");
smap.put("VEKNC", "F");
smap.put("VEKNE", "F");
smap.put("VEKNU", "F");
smap.put("VEKOK", "F");
smap.put("VEKSC", "F");
smap.put("VEKSO", "F");
smap.put("VEKSU", "F");
smap.put("VEKU(", "F");
smap.put("VEKU1", "F");
smap.put("VEKUE", "F");
smap.put("VEKUF", "F");
smap.put("VEKUN", "F");
smap.put("VEKUS", "F");
smap.put("VEKUV", "F");
smap.put("VEKVC", "F");
smap.put("VEKVO", "F");
smap.put("VEKVU", "F");
smap.put("VENC", "F");
smap.put("VENEN", "F");
smap.put("VENO(", "F");
smap.put("VENOF", "F");
smap.put("VENOS", "F");
smap.put("VENOV", "F");
smap.put("VENUE", "F");
smap.put("VEOKN", "F");
smap.put("VESC", "F");
smap.put("VESO(", "F");
smap.put("VESO1", "F");
smap.put("VESOF", "F");
smap.put("VESON", "F");
smap.put("VESOS", "F");
smap.put("VESOV", "F");
smap.put("VESUE", "F");
smap.put("VEU(1", "F");
smap.put("VEU(F", "F");
smap.put("VEU(N", "F");
smap.put("VEU(S", "F");
smap.put("VEU(V", "F");
smap.put("VEU1,", "F");
smap.put("VEU1C", "F");
smap.put("VEU1O", "F");
smap.put("VEUEF", "F");
smap.put("VEUEK", "F");
smap.put("VEUF(", "F");
smap.put("VEUN,", "F");
smap.put("VEUNC", "F");
smap.put("VEUNO", "F");
smap.put("VEUS,", "F");
smap.put("VEUSC", "F");
smap.put("VEUSO", "F");
smap.put("VEUV,", "F");
smap.put("VEUVC", "F");
smap.put("VEUVO", "F");
smap.put("VEVC", "F");
smap.put("VEVO(", "F");
smap.put("VEVOF", "F");
smap.put("VEVOS", "F");
smap.put("VEVUE", "F");
smap.put("VF()1", "F");
smap.put("VF()F", "F");
smap.put("VF()K", "F");
smap.put("VF()N", "F");
smap.put("VF()O", "F");
smap.put("VF()S", "F");
smap.put("VF()U", "F");
smap.put("VF()V", "F");
smap.put("VF(1)", "F");
smap.put("VF(1N", "F");
smap.put("VF(1O", "F");
smap.put("VF(E(", "F");
smap.put("VF(E1", "F");
smap.put("VF(EF", "F");
smap.put("VF(EK", "F");
smap.put("VF(EN", "F");
smap.put("VF(ES", "F");
smap.put("VF(EV", "F");
smap.put("VF(F(", "F");
smap.put("VF(N)", "F");
smap.put("VF(N,", "F");
smap.put("VF(NO", "F");
smap.put("VF(S)", "F");
smap.put("VF(SO", "F");
smap.put("VF(V)", "F");
smap.put("VF(VO", "F");
smap.put("VK(1)", "F");
smap.put("VK(1O", "F");
smap.put("VK(F(", "F");
smap.put("VK(N)", "F");
smap.put("VK(NO", "F");
smap.put("VK(S)", "F");
smap.put("VK(SO", "F");
smap.put("VK(V)", "F");
smap.put("VK(VO", "F");
smap.put("VK)&(", "F");
smap.put("VK)&1", "F");
smap.put("VK)&F", "F");
smap.put("VK)&N", "F");
smap.put("VK)&S", "F");
smap.put("VK)&V", "F");
smap.put("VK);E", "F");
smap.put("VK);T", "F");
smap.put("VK)B(", "F");
smap.put("VK)B1", "F");
smap.put("VK)BF", "F");
smap.put("VK)BN", "F");
smap.put("VK)BS", "F");
smap.put("VK)BV", "F");
smap.put("VK)E(", "F");
smap.put("VK)E1", "F");
smap.put("VK)EF", "F");
smap.put("VK)EK", "F");
smap.put("VK)EN", "F");
smap.put("VK)ES", "F");
smap.put("VK)EV", "F");
smap.put("VK)OF", "F");
smap.put("VK)UE", "F");
smap.put("VK1", "F");
smap.put("VK1&(", "F");
smap.put("VK1&1", "F");
smap.put("VK1&F", "F");
smap.put("VK1&N", "F");
smap.put("VK1&S", "F");
smap.put("VK1&V", "F");
smap.put("VK1;", "F");
smap.put("VK1;C", "F");
smap.put("VK1;E", "F");
smap.put("VK1;T", "F");
smap.put("VK1B(", "F");
smap.put("VK1B1", "F");
smap.put("VK1BF", "F");
smap.put("VK1BN", "F");
smap.put("VK1BS", "F");
smap.put("VK1BV", "F");
smap.put("VK1C", "F");
smap.put("VK1E(", "F");
smap.put("VK1E1", "F");
smap.put("VK1EF", "F");
smap.put("VK1EK", "F");
smap.put("VK1EN", "F");
smap.put("VK1ES", "F");
smap.put("VK1EV", "F");
smap.put("VK1O(", "F");
smap.put("VK1OF", "F");
smap.put("VK1OS", "F");
smap.put("VK1OV", "F");
smap.put("VK1U(", "F");
smap.put("VK1UE", "F");
smap.put("VKF()", "F");
smap.put("VKF(1", "F");
smap.put("VKF(F", "F");
smap.put("VKF(N", "F");
smap.put("VKF(S", "F");
smap.put("VKF(V", "F");
smap.put("VKN", "F");
smap.put("VKN&(", "F");
smap.put("VKN&1", "F");
smap.put("VKN&F", "F");
smap.put("VKN&N", "F");
smap.put("VKN&S", "F");
smap.put("VKN&V", "F");
smap.put("VKN;", "F");
smap.put("VKN;C", "F");
smap.put("VKN;E", "F");
smap.put("VKN;T", "F");
smap.put("VKNB(", "F");
smap.put("VKNB1", "F");
smap.put("VKNBF", "F");
smap.put("VKNBN", "F");
smap.put("VKNBS", "F");
smap.put("VKNBV", "F");
smap.put("VKNC", "F");
smap.put("VKNE(", "F");
smap.put("VKNE1", "F");
smap.put("VKNEF", "F");
smap.put("VKNEN", "F");
smap.put("VKNES", "F");
smap.put("VKNEV", "F");
smap.put("VKNU(", "F");
smap.put("VKNUE", "F");
smap.put("VKS", "F");
smap.put("VKS&(", "F");
smap.put("VKS&1", "F");
smap.put("VKS&F", "F");
smap.put("VKS&N", "F");
smap.put("VKS&S", "F");
smap.put("VKS&V", "F");
smap.put("VKS;", "F");
smap.put("VKS;C", "F");
smap.put("VKS;E", "F");
smap.put("VKS;T", "F");
smap.put("VKSB(", "F");
smap.put("VKSB1", "F");
smap.put("VKSBF", "F");
smap.put("VKSBN", "F");
smap.put("VKSBS", "F");
smap.put("VKSBV", "F");
smap.put("VKSC", "F");
smap.put("VKSE(", "F");
smap.put("VKSE1", "F");
smap.put("VKSEF", "F");
smap.put("VKSEK", "F");
smap.put("VKSEN", "F");
smap.put("VKSES", "F");
smap.put("VKSEV", "F");
smap.put("VKSO(", "F");
smap.put("VKSO1", "F");
smap.put("VKSOF", "F");
smap.put("VKSON", "F");
smap.put("VKSOS", "F");
smap.put("VKSOV", "F");
smap.put("VKSU(", "F");
smap.put("VKSUE", "F");
smap.put("VKUE(", "F");
smap.put("VKUE1", "F");
smap.put("VKUEF", "F");
smap.put("VKUEK", "F");
smap.put("VKUEN", "F");
smap.put("VKUES", "F");
smap.put("VKUEV", "F");
smap.put("VKV", "F");
smap.put("VKV&(", "F");
smap.put("VKV&1", "F");
smap.put("VKV&F", "F");
smap.put("VKV&N", "F");
smap.put("VKV&S", "F");
smap.put("VKV&V", "F");
smap.put("VKV;", "F");
smap.put("VKV;C", "F");
smap.put("VKV;E", "F");
smap.put("VKV;T", "F");
smap.put("VKVB(", "F");
smap.put("VKVB1", "F");
smap.put("VKVBF", "F");
smap.put("VKVBN", "F");
smap.put("VKVBS", "F");
smap.put("VKVBV", "F");
smap.put("VKVC", "F");
smap.put("VKVE(", "F");
smap.put("VKVE1", "F");
smap.put("VKVEF", "F");
smap.put("VKVEK", "F");
smap.put("VKVEN", "F");
smap.put("VKVES", "F");
smap.put("VKVEV", "F");
smap.put("VKVO(", "F");
smap.put("VKVOF", "F");
smap.put("VKVOS", "F");
smap.put("VKVU(", "F");
smap.put("VKVUE", "F");
smap.put("VO(1&", "F");
smap.put("VO(1)", "F");
smap.put("VO(1,", "F");
smap.put("VO(1O", "F");
smap.put("VO(E(", "F");
smap.put("VO(E1", "F");
smap.put("VO(EE", "F");
smap.put("VO(EF", "F");
smap.put("VO(EK", "F");
smap.put("VO(EN", "F");
smap.put("VO(ES", "F");
smap.put("VO(EV", "F");
smap.put("VO(F(", "F");
smap.put("VO(N&", "F");
smap.put("VO(N)", "F");
smap.put("VO(N,", "F");
smap.put("VO(NO", "F");
smap.put("VO(S&", "F");
smap.put("VO(S)", "F");
smap.put("VO(S,", "F");
smap.put("VO(SO", "F");
smap.put("VO(V&", "F");
smap.put("VO(V)", "F");
smap.put("VO(V,", "F");
smap.put("VO(VO", "F");
smap.put("VOF()", "F");
smap.put("VOF(1", "F");
smap.put("VOF(E", "F");
smap.put("VOF(F", "F");
smap.put("VOF(N", "F");
smap.put("VOF(S", "F");
smap.put("VOF(V", "F");
smap.put("VOK&(", "F");
smap.put("VOK&1", "F");
smap.put("VOK&F", "F");
smap.put("VOK&N", "F");
smap.put("VOK&S", "F");
smap.put("VOK&V", "F");
smap.put("VOK(1", "F");
smap.put("VOK(F", "F");
smap.put("VOK(N", "F");
smap.put("VOK(S", "F");
smap.put("VOK(V", "F");
smap.put("VOK1C", "F");
smap.put("VOK1O", "F");
smap.put("VOKF(", "F");
smap.put("VOKNC", "F");
smap.put("VOKO(", "F");
smap.put("VOKO1", "F");
smap.put("VOKOF", "F");
smap.put("VOKON", "F");
smap.put("VOKOS", "F");
smap.put("VOKOV", "F");
smap.put("VOKSC", "F");
smap.put("VOKSO", "F");
smap.put("VOKVC", "F");
smap.put("VOKVO", "F");
smap.put("VOS", "F");
smap.put("VOS&(", "F");
smap.put("VOS&1", "F");
smap.put("VOS&E", "F");
smap.put("VOS&F", "F");
smap.put("VOS&K", "F");
smap.put("VOS&N", "F");
smap.put("VOS&S", "F");
smap.put("VOS&U", "F");
smap.put("VOS&V", "F");
smap.put("VOS(E", "F");
smap.put("VOS(U", "F");
smap.put("VOS)&", "F");
smap.put("VOS),", "F");
smap.put("VOS);", "F");
smap.put("VOS)B", "F");
smap.put("VOS)C", "F");
smap.put("VOS)E", "F");
smap.put("VOS)K", "F");
smap.put("VOS)O", "F");
smap.put("VOS)U", "F");
smap.put("VOS,(", "F");
smap.put("VOS,F", "F");
smap.put("VOS1(", "F");
smap.put("VOS1F", "F");
smap.put("VOS1N", "F");
smap.put("VOS1O", "F");
smap.put("VOS1S", "F");
smap.put("VOS1U", "F");
smap.put("VOS1V", "F");
smap.put("VOS;", "F");
smap.put("VOS;C", "F");
smap.put("VOS;E", "F");
smap.put("VOS;N", "F");
smap.put("VOS;T", "F");
smap.put("VOSA(", "F");
smap.put("VOSAF", "F");
smap.put("VOSAS", "F");
smap.put("VOSAT", "F");
smap.put("VOSAV", "F");
smap.put("VOSB(", "F");
smap.put("VOSB1", "F");
smap.put("VOSBE", "F");
smap.put("VOSBF", "F");
smap.put("VOSBN", "F");
smap.put("VOSBS", "F");
smap.put("VOSBV", "F");
smap.put("VOSC", "F");
smap.put("VOSE(", "F");
smap.put("VOSE1", "F");
smap.put("VOSEF", "F");
smap.put("VOSEK", "F");
smap.put("VOSEN", "F");
smap.put("VOSEO", "F");
smap.put("VOSES", "F");
smap.put("VOSEU", "F");
smap.put("VOSEV", "F");
smap.put("VOSF(", "F");
smap.put("VOSK(", "F");
smap.put("VOSK)", "F");
smap.put("VOSK1", "F");
smap.put("VOSKB", "F");
smap.put("VOSKF", "F");
smap.put("VOSKN", "F");
smap.put("VOSKS", "F");
smap.put("VOSKU", "F");
smap.put("VOSKV", "F");
smap.put("VOSU", "F");
smap.put("VOSU(", "F");
smap.put("VOSU1", "F");
smap.put("VOSU;", "F");
smap.put("VOSUC", "F");
smap.put("VOSUE", "F");
smap.put("VOSUF", "F");
smap.put("VOSUK", "F");
smap.put("VOSUN", "F");
smap.put("VOSUO", "F");
smap.put("VOSUS", "F");
smap.put("VOSUT", "F");
smap.put("VOSUV", "F");
smap.put("VOSV(", "F");
smap.put("VOSVF", "F");
smap.put("VOSVO", "F");
smap.put("VOSVS", "F");
smap.put("VOSVU", "F");
smap.put("VOU(E", "F");
smap.put("VOUEK", "F");
smap.put("VOUEN", "F");
smap.put("VU", "F");
smap.put("VU(1)", "F");
smap.put("VU(1O", "F");
smap.put("VU(E(", "F");
smap.put("VU(E1", "F");
smap.put("VU(EF", "F");
smap.put("VU(EK", "F");
smap.put("VU(EN", "F");
smap.put("VU(ES", "F");
smap.put("VU(EV", "F");
smap.put("VU(F(", "F");
smap.put("VU(N)", "F");
smap.put("VU(NO", "F");
smap.put("VU(S)", "F");
smap.put("VU(SO", "F");
smap.put("VU(V)", "F");
smap.put("VU(VO", "F");
smap.put("VU1,(", "F");
smap.put("VU1,F", "F");
smap.put("VU1C", "F");
smap.put("VU1O(", "F");
smap.put("VU1OF", "F");
smap.put("VU1OS", "F");
smap.put("VU1OV", "F");
smap.put("VU;", "F");
smap.put("VU;C", "F");
smap.put("VUC", "F");
smap.put("VUE", "F");
smap.put("VUE(1", "F");
smap.put("VUE(E", "F");
smap.put("VUE(F", "F");
smap.put("VUE(N", "F");
smap.put("VUE(O", "F");
smap.put("VUE(S", "F");
smap.put("VUE(V", "F");
smap.put("VUE1", "F");
smap.put("VUE1&", "F");
smap.put("VUE1(", "F");
smap.put("VUE1)", "F");
smap.put("VUE1,", "F");
smap.put("VUE1;", "F");
smap.put("VUE1B", "F");
smap.put("VUE1C", "F");
smap.put("VUE1F", "F");
smap.put("VUE1K", "F");
smap.put("VUE1N", "F");
smap.put("VUE1O", "F");
smap.put("VUE1S", "F");
smap.put("VUE1U", "F");
smap.put("VUE1V", "F");
smap.put("VUE;", "F");
smap.put("VUE;C", "F");
smap.put("VUEC", "F");
smap.put("VUEF", "F");
smap.put("VUEF(", "F");
smap.put("VUEF,", "F");
smap.put("VUEF;", "F");
smap.put("VUEFC", "F");
smap.put("VUEK", "F");
smap.put("VUEK(", "F");
smap.put("VUEK1", "F");
smap.put("VUEK;", "F");
smap.put("VUEKC", "F");
smap.put("VUEKF", "F");
smap.put("VUEKN", "F");
smap.put("VUEKO", "F");
smap.put("VUEKS", "F");
smap.put("VUEKV", "F");
smap.put("VUEN", "F");
smap.put("VUEN&", "F");
smap.put("VUEN(", "F");
smap.put("VUEN)", "F");
smap.put("VUEN,", "F");
smap.put("VUEN1", "F");
smap.put("VUEN;", "F");
smap.put("VUENB", "F");
smap.put("VUENC", "F");
smap.put("VUENF", "F");
smap.put("VUENK", "F");
smap.put("VUENO", "F");
smap.put("VUENS", "F");
smap.put("VUENU", "F");
smap.put("VUEOK", "F");
smap.put("VUEON", "F");
smap.put("VUEOO", "F");
smap.put("VUES", "F");
smap.put("VUES&", "F");
smap.put("VUES(", "F");
smap.put("VUES)", "F");
smap.put("VUES,", "F");
smap.put("VUES1", "F");
smap.put("VUES;", "F");
smap.put("VUESB", "F");
smap.put("VUESC", "F");
smap.put("VUESF", "F");
smap.put("VUESK", "F");
smap.put("VUESO", "F");
smap.put("VUESU", "F");
smap.put("VUESV", "F");
smap.put("VUEV", "F");
smap.put("VUEV&", "F");
smap.put("VUEV(", "F");
smap.put("VUEV)", "F");
smap.put("VUEV,", "F");
smap.put("VUEV;", "F");
smap.put("VUEVB", "F");
smap.put("VUEVC", "F");
smap.put("VUEVF", "F");
smap.put("VUEVK", "F");
smap.put("VUEVN", "F");
smap.put("VUEVO", "F");
smap.put("VUEVS", "F");
smap.put("VUEVU", "F");
smap.put("VUF()", "F");
smap.put("VUF(1", "F");
smap.put("VUF(F", "F");
smap.put("VUF(N", "F");
smap.put("VUF(S", "F");
smap.put("VUF(V", "F");
smap.put("VUK(E", "F");
smap.put("VUN(1", "F");
smap.put("VUN(F", "F");
smap.put("VUN(S", "F");
smap.put("VUN(V", "F");
smap.put("VUN,(", "F");
smap.put("VUN,F", "F");
smap.put("VUN1(", "F");
smap.put("VUN1,", "F");
smap.put("VUN1O", "F");
smap.put("VUNC", "F");
smap.put("VUNE(", "F");
smap.put("VUNE1", "F");
smap.put("VUNEF", "F");
smap.put("VUNEN", "F");
smap.put("VUNES", "F");
smap.put("VUNEV", "F");
smap.put("VUNF(", "F");
smap.put("VUNO(", "F");
smap.put("VUNOF", "F");
smap.put("VUNOS", "F");
smap.put("VUNOV", "F");
smap.put("VUNS(", "F");
smap.put("VUNS,", "F");
smap.put("VUNSO", "F");
smap.put("VUO(E", "F");
smap.put("VUON(", "F");
smap.put("VUON1", "F");
smap.put("VUONF", "F");
smap.put("VUONS", "F");
smap.put("VUS,(", "F");
smap.put("VUS,F", "F");
smap.put("VUSC", "F");
smap.put("VUSO(", "F");
smap.put("VUSO1", "F");
smap.put("VUSOF", "F");
smap.put("VUSON", "F");
smap.put("VUSOS", "F");
smap.put("VUSOV", "F");
smap.put("VUTN(", "F");
smap.put("VUTN1", "F");
smap.put("VUTNF", "F");
smap.put("VUTNS", "F");
smap.put("VUV,(", "F");
smap.put("VUV,F", "F");
smap.put("VUVC", "F");
smap.put("VUVO(", "F");
smap.put("VUVOF", "F");
smap.put("VUVOS", "F");
smap.put("X", "F");
map.put("::", "o");
map.put(":=", "o");
map.put("<<", "o");
map.put("<=", "o");
map.put("<>", "o");
map.put("<@", "o");
map.put(">=", "o");
map.put(">>", "o");
map.put("@>", "o");
map.put("ABORT", "k");
map.put("ABS", "f");
map.put("ACCESSIBLE", "k");
map.put("ACOS", "f");
map.put("ADD", "k");
map.put("ADDDATE", "f");
map.put("ADDTIME", "f");
map.put("AES_DECRYPT", "f");
map.put("AES_ENCRYPT", "f");
map.put("AGAINST", "k");
map.put("AGE", "f");
map.put("ALL_USERS", "k");
map.put("ALTER", "k");
map.put("ALTER DOMAIN", "k");
map.put("ALTER TABLE", "k");
map.put("ANALYZE", "k");
map.put("AND", "&");
map.put("ANY", "f");
map.put("ANYARRAY", "t");
map.put("ANYELEMENT", "t");
map.put("ANYNONARRY", "t");
map.put("APPLOCK_MODE", "f");
map.put("APPLOCK_TEST", "f");
map.put("APP_NAME", "f");
map.put("ARRAY_AGG", "f");
map.put("ARRAY_CAT", "f");
map.put("ARRAY_DIM", "f");
map.put("ARRAY_FILL", "f");
map.put("ARRAY_LENGTH", "f");
map.put("ARRAY_LOWER", "f");
map.put("ARRAY_NDIMS", "f");
map.put("ARRAY_PREPEND", "f");
map.put("ARRAY_TO_JSON", "f");
map.put("ARRAY_TO_STRING", "f");
map.put("ARRAY_UPPER", "f");
map.put("AS", "k");
map.put("ASC", "k");
map.put("ASCII", "f");
map.put("ASENSITIVE", "k");
map.put("ASIN", "f");
map.put("ASSEMBLYPROPERTY", "f");
map.put("ASYMKEY_ID", "f");
map.put("AT TIME", "n");
map.put("AT TIME ZONE", "k");
map.put("ATAN", "f");
map.put("ATAN2", "f");
map.put("AUTOINCREMENT", "k");
map.put("AVG", "f");
map.put("BEFORE", "k");
map.put("BEGIN", "T");
map.put("BENCHMARK", "f");
map.put("BETWEEN", "o");
map.put("BIGINT", "t");
map.put("BIGSERIAL", "t");
map.put("BIN", "f");
map.put("BINARY", "t");
map.put("BINARY_DOUBLE_INFINITY", "1");
map.put("BINARY_DOUBLE_NAN", "1");
map.put("BINARY_FLOAT_INFINITY", "1");
map.put("BINARY_FLOAT_NAN", "1");
map.put("BINBINARY", "f");
map.put("BIT_AND", "f");
map.put("BIT_COUNT", "f");
map.put("BIT_LENGTH", "f");
map.put("BIT_OR", "f");
map.put("BIT_XOR", "f");
map.put("BLOB", "k");
map.put("BOOLEAN", "t");
map.put("BOOL_AND", "f");
map.put("BOOL_OR", "f");
map.put("BOTH", "k");
map.put("BTRIM", "f");
map.put("BY", "n");
map.put("BYTEA", "t");
map.put("CALL", "T");
map.put("CASCADE", "k");
map.put("CASE", "E");
map.put("CAST", "f");
map.put("CBOOL", "f");
map.put("CBRT", "f");
map.put("CBYTE", "f");
map.put("CCUR", "f");
map.put("CDATE", "f");
map.put("CDBL", "f");
map.put("CEIL", "f");
map.put("CEILING", "f");
map.put("CERTENCODED", "f");
map.put("CERTPRIVATEKEY", "f");
map.put("CERT_ID", "f");
map.put("CERT_PROPERTY", "f");
map.put("CHANGE", "k");
map.put("CHANGES", "f");
map.put("CHAR", "f");
map.put("CHARACTER", "t");
map.put("CHARACTER VARYING", "t");
map.put("CHARACTER_LENGTH", "f");
map.put("CHARINDEX", "f");
map.put("CHARSET", "f");
map.put("CHAR_LENGTH", "f");
map.put("CHDIR", "f");
map.put("CHDRIVE", "f");
map.put("CHECK", "k");
map.put("CHECKSUM_AGG", "f");
map.put("CHOOSE", "f");
map.put("CHR", "f");
map.put("CINT", "f");
map.put("CLNG", "f");
map.put("CLOCK_TIMESTAMP", "f");
map.put("COALESCE", "f");
map.put("COERCIBILITY", "f");
map.put("COLLATE", "A");
map.put("COLLATION", "f");
map.put("COLLATIONPROPERTY", "f");
map.put("COLUMN", "k");
map.put("COLUMNPROPERTY", "f");
map.put("COLUMNS_UPDATED", "f");
map.put("COL_LENGTH", "f");
map.put("COL_NAME", "f");
map.put("COMPRESS", "f");
map.put("CONCAT", "f");
map.put("CONCAT_WS", "f");
map.put("CONDITION", "k");
map.put("CONNECTION_ID", "f");
map.put("CONSTRAINT", "k");
map.put("CONTINUE", "k");
map.put("CONV", "f");
map.put("CONVERT", "f");
map.put("CONVERT_FROM", "f");
map.put("CONVERT_TO", "f");
map.put("CONVERT_TZ", "f");
map.put("COS", "f");
map.put("COT", "f");
map.put("COUNT", "f");
map.put("COUNT_BIG", "k");
map.put("CRC32", "f");
map.put("CREATE", "E");
map.put("CREATE OR", "n");
map.put("CREATE OR REPLACE", "T");
map.put("CROSS", "n");
map.put("CROSS JOIN", "k");
map.put("CSNG", "f");
map.put("CSTRING", "t");
map.put("CTXSYS.DRITHSX.SN", "f");
map.put("CUME_DIST", "f");
map.put("CURDATE", "f");
map.put("CURDIR", "f");
map.put("CURRENT DATE", "v");
map.put("CURRENT DEGREE", "v");
map.put("CURRENT FUNCTION", "v");
map.put("CURRENT FUNCTION PATH", "v");
map.put("CURRENT PATH", "v");
map.put("CURRENT SCHEMA", "v");
map.put("CURRENT SERVER", "v");
map.put("CURRENT TIME", "v");
map.put("CURRENT TIMEZONE", "v");
map.put("CURRENTUSER", "f");
map.put("CURRENT_DATABASE", "f");
map.put("CURRENT_DATE", "v");
map.put("CURRENT_PATH", "v");
map.put("CURRENT_QUERY", "f");
map.put("CURRENT_SCHEMA", "f");
map.put("CURRENT_SCHEMAS", "f");
map.put("CURRENT_SERVER", "v");
map.put("CURRENT_SETTING", "f");
map.put("CURRENT_TIME", "v");
map.put("CURRENT_TIMESTAMP", "v");
map.put("CURRENT_TIMEZONE", "v");
map.put("CURRENT_USER", "v");
map.put("CURRVAL", "f");
map.put("CURSOR", "k");
map.put("CURSOR_STATUS", "f");
map.put("CURTIME", "f");
map.put("CVAR", "f");
map.put("DATABASE", "n");
map.put("DATABASEPROPERTYEX", "f");
map.put("DATABASES", "k");
map.put("DATABASE_PRINCIPAL_ID", "f");
map.put("DATALENGTH", "f");
map.put("DATE", "f");
map.put("DATEADD", "f");
map.put("DATEDIFF", "f");
map.put("DATEFROMPARTS", "f");
map.put("DATENAME", "f");
map.put("DATEPART", "f");
map.put("DATESERIAL", "f");
map.put("DATETIME2FROMPARTS", "f");
map.put("DATETIMEFROMPARTS", "f");
map.put("DATETIMEOFFSETFROMPARTS", "f");
map.put("DATEVALUE", "f");
map.put("DATE_ADD", "f");
map.put("DATE_FORMAT", "f");
map.put("DATE_PART", "f");
map.put("DATE_SUB", "f");
map.put("DATE_TRUNC", "f");
map.put("DAVG", "f");
map.put("DAY", "f");
map.put("DAYNAME", "f");
map.put("DAYOFMONTH", "f");
map.put("DAYOFWEEK", "f");
map.put("DAYOFYEAR", "f");
map.put("DAY_HOUR", "k");
map.put("DAY_MICROSECOND", "k");
map.put("DAY_MINUTE", "k");
map.put("DAY_SECOND", "k");
map.put("DBMS_LOCK.SLEEP", "f");
map.put("DBMS_PIPE.RECEIVE_MESSAGE", "f");
map.put("DB_ID", "f");
map.put("DB_NAME", "f");
map.put("DCOUNT", "f");
map.put("DEC", "k");
map.put("DECIMAL", "t");
map.put("DECLARE", "T");
map.put("DECODE", "f");
map.put("DECRYPTBYASMKEY", "f");
map.put("DECRYPTBYCERT", "f");
map.put("DECRYPTBYKEY", "f");
map.put("DECRYPTBYKEYAUTOCERT", "f");
map.put("DECRYPTBYPASSPHRASE", "f");
map.put("DEFAULT", "k");
map.put("DEGREES", "f");
map.put("DELAY", "k");
map.put("DELAYED", "k");
map.put("DELETE", "T");
map.put("DENSE_RANK", "f");
map.put("DESC", "k");
map.put("DESCRIBE", "k");
map.put("DES_DECRYPT", "f");
map.put("DES_ENCRYPT", "f");
map.put("DETERMINISTIC", "k");
map.put("DFIRST", "f");
map.put("DIFFERENCE", "f");
map.put("DISTINCT", "k");
map.put("DISTINCTROW", "k");
map.put("DIV", "o");
map.put("DLAST", "f");
map.put("DLOOKUP", "f");
map.put("DMAX", "f");
map.put("DMIN", "f");
map.put("DO", "n");
map.put("DOUBLE", "t");
map.put("DOUBLE PRECISION", "t");
map.put("DROP", "T");
map.put("DSUM", "f");
map.put("DUAL", "n");
map.put("EACH", "k");
map.put("ELSE", "k");
map.put("ELSEIF", "k");
map.put("ELT", "f");
map.put("ENCLOSED", "k");
map.put("ENCODE", "f");
map.put("ENCRYPT", "f");
map.put("ENCRYPTBYASMKEY", "f");
map.put("ENCRYPTBYCERT", "f");
map.put("ENCRYPTBYKEY", "f");
map.put("ENCRYPTBYPASSPHRASE", "f");
map.put("ENUM_FIRST", "f");
map.put("ENUM_LAST", "f");
map.put("ENUM_RANGE", "f");
map.put("EOMONTH", "f");
map.put("EQV", "o");
map.put("ESCAPED", "k");
map.put("EVENTDATA", "f");
map.put("EXCEPT", "U");
map.put("EXEC", "T");
map.put("EXECUTE", "T");
map.put("EXECUTE AS", "E");
map.put("EXECUTE AS LOGIN", "E");
map.put("EXISTS", "f");
map.put("EXIT", "k");
map.put("EXP", "f");
map.put("EXPLAIN", "k");
map.put("EXPORT_SET", "f");
map.put("EXTRACT", "f");
map.put("EXTRACTVALUE", "f");
map.put("EXTRACT_VALUE", "f");
map.put("FALSE", "1");
map.put("FETCH", "k");
map.put("FIELD", "f");
map.put("FILEDATETIME", "f");
map.put("FILEGROUPPROPERTY", "f");
map.put("FILEGROUP_ID", "f");
map.put("FILEGROUP_NAME", "f");
map.put("FILELEN", "f");
map.put("FILEPROPERTY", "f");
map.put("FILE_ID", "f");
map.put("FILE_IDEX", "f");
map.put("FILE_NAME", "f");
map.put("FIND_IN_SET", "f");
map.put("FIRST_VALUE", "f");
map.put("FLOAT", "t");
map.put("FLOAT4", "t");
map.put("FLOAT8", "t");
map.put("FLOOR", "f");
map.put("FN_VIRTUALFILESTATS", "f");
map.put("FOR", "n");
map.put("FOR UPDATE", "k");
map.put("FOR UPDATE NOWAIT", "k");
map.put("FOR UPDATE OF", "k");
map.put("FOR UPDATE SKIP", "k");
map.put("FOR UPDATE SKIP LOCKED", "k");
map.put("FOR UPDATE WAIT", "k");
map.put("FORCE", "k");
map.put("FOREIGN", "k");
map.put("FORMAT", "f");
map.put("FOUND_ROWS", "f");
map.put("FROM", "k");
map.put("FROM_BASE64", "f");
map.put("FROM_DAYS", "f");
map.put("FROM_UNIXTIME", "f");
map.put("FULL JOIN", "k");
map.put("FULL OUTER", "k");
map.put("FULL OUTER JOIN", "k");
map.put("FULLTEXT", "k");
map.put("FULLTEXTCATALOGPROPERTY", "f");
map.put("FULLTEXTSERVICEPROPERTY", "f");
map.put("FUNCTION", "k");
map.put("GENERATE_SERIES", "f");
map.put("GENERATE_SUBSCRIPTS", "f");
map.put("GETATTR", "f");
map.put("GETDATE", "f");
map.put("GETUTCDATE", "f");
map.put("GET_BIT", "f");
map.put("GET_BYTE", "f");
map.put("GET_FORMAT", "f");
map.put("GET_LOCK", "f");
map.put("GO", "T");
map.put("GOTO", "T");
map.put("GRANT", "k");
map.put("GREATEST", "f");
map.put("GROUP", "n");
map.put("GROUP BY", "B");
map.put("GROUPING", "f");
map.put("GROUPING_ID", "f");
map.put("GROUP_CONCAT", "f");
map.put("HANDLER", "T");
map.put("HASHBYTES", "f");
map.put("HAS_PERMS_BY_NAME", "f");
map.put("HAVING", "B");
map.put("HEX", "f");
map.put("HIGH_PRIORITY", "k");
map.put("HOST_NAME", "f");
map.put("HOUR", "f");
map.put("HOUR_MICROSECOND", "k");
map.put("HOUR_MINUTE", "k");
map.put("HOUR_SECOND", "k");
map.put("IDENTIFY", "f");
map.put("IDENT_CURRENT", "f");
map.put("IDENT_INCR", "f");
map.put("IDENT_SEED", "f");
map.put("IF", "f");
map.put("IF EXISTS", "f");
map.put("IF NOT", "n");
map.put("IF NOT EXISTS", "f");
map.put("IFF", "f");
map.put("IFNULL", "f");
map.put("IGNORE", "k");
map.put("IIF", "f");
map.put("IN", "k");
map.put("IN BOOLEAN", "n");
map.put("IN BOOLEAN MODE", "k");
map.put("INDEX", "k");
map.put("INDEXKEY_PROPERTY", "f");
map.put("INDEXPROPERTY", "f");
map.put("INDEX_COL", "f");
map.put("INET_ATON", "f");
map.put("INET_NTOA", "f");
map.put("INFILE", "k");
map.put("INITCAP", "f");
map.put("INNER", "k");
map.put("INNER JOIN", "k");
map.put("INOUT", "k");
map.put("INSENSITIVE", "k");
map.put("INSERT", "E");
map.put("INSERT DELAYED", "E");
map.put("INSERT DELAYED INTO", "T");
map.put("INSERT HIGH_PRIORITY", "E");
map.put("INSERT HIGH_PRIORITY INTO", "T");
map.put("INSERT IGNORE", "E");
map.put("INSERT IGNORE INTO", "T");
map.put("INSERT INTO", "T");
map.put("INSERT LOW_PRIORITY", "E");
map.put("INSERT LOW_PRIORITY INTO", "T");
map.put("INSTR", "f");
map.put("INSTRREV", "f");
map.put("INT", "t");
map.put("INT1", "t");
map.put("INT2", "t");
map.put("INT3", "t");
map.put("INT4", "t");
map.put("INT8", "t");
map.put("INTEGER", "t");
map.put("INTERSECT", "U");
map.put("INTERSECT ALL", "U");
map.put("INTERVAL", "k");
map.put("INTO", "k");
map.put("INTO DUMPFILE", "k");
map.put("INTO OUTFILE", "k");
map.put("IS", "o");
map.put("IS DISTINCT", "n");
map.put("IS DISTINCT FROM", "o");
map.put("IS NOT", "o");
map.put("IS NOT DISTINCT", "n");
map.put("IS NOT DISTINCT FROM", "o");
map.put("ISDATE", "f");
map.put("ISEMPTY", "f");
map.put("ISFINITE", "f");
map.put("ISNULL", "f");
map.put("ISNUMERIC", "f");
map.put("IS_FREE_LOCK", "f");
map.put("IS_MEMBER", "f");
map.put("IS_OBJECTSIGNED", "f");
map.put("IS_ROLEMEMBER", "f");
map.put("IS_SRVROLEMEMBER", "f");
map.put("IS_USED_LOCK", "f");
map.put("ITERATE", "k");
map.put("JOIN", "k");
map.put("JULIANDAY", "f");
map.put("JUSTIFY_DAYS", "f");
map.put("JUSTIFY_HOURS", "f");
map.put("JUSTIFY_INTERVAL", "f");
map.put("KEYS", "k");
map.put("KEY_GUID", "f");
map.put("KEY_ID", "f");
map.put("KILL", "k");
map.put("LAG", "f");
map.put("LASTVAL", "f");
map.put("LAST_INSERT_ID", "f");
map.put("LAST_INSERT_ROWID", "f");
map.put("LAST_VALUE", "f");
map.put("LCASE", "f");
map.put("LEAD", "f");
map.put("LEADING", "k");
map.put("LEAST", "f");
map.put("LEAVE", "k");
map.put("LEFT", "n");
map.put("LEFT JOIN", "k");
map.put("LEFT OUTER", "k");
map.put("LEFT OUTER JOIN", "k");
map.put("LENGTH", "f");
map.put("LIKE", "o");
map.put("LIMIT", "B");
map.put("LINEAR", "k");
map.put("LINES", "k");
map.put("LN", "f");
map.put("LOAD", "k");
map.put("LOAD DATA", "T");
map.put("LOAD XML", "T");
map.put("LOAD_EXTENSION", "f");
map.put("LOAD_FILE", "f");
map.put("LOCALTIME", "v");
map.put("LOCALTIMESTAMP", "v");
map.put("LOCATE", "f");
map.put("LOCK", "n");
map.put("LOCK IN", "n");
map.put("LOCK IN SHARE", "n");
map.put("LOCK IN SHARE MODE", "k");
map.put("LOCK TABLE", "k");
map.put("LOCK TABLES", "k");
map.put("LOG", "f");
map.put("LOG10", "f");
map.put("LOG2", "f");
map.put("LONGBLOB", "k");
map.put("LONGTEXT", "k");
map.put("LOOP", "k");
map.put("LOWER", "f");
map.put("LOWER_INC", "f");
map.put("LOWER_INF", "f");
map.put("LOW_PRIORITY", "k");
map.put("LPAD", "f");
map.put("LTRIM", "f");
map.put("MAKEDATE", "f");
map.put("MAKE_SET", "f");
map.put("MASKLEN", "f");
map.put("MASTER_BIND", "k");
map.put("MASTER_POS_WAIT", "f");
map.put("MASTER_SSL_VERIFY_SERVER_CERT", "k");
map.put("MATCH", "k");
map.put("MAX", "f");
map.put("MAXVALUE", "k");
map.put("MD5", "f");
map.put("MEDIUMBLOB", "k");
map.put("MEDIUMINT", "k");
map.put("MEDIUMTEXT", "k");
map.put("MERGE", "k");
map.put("MICROSECOND", "f");
map.put("MID", "f");
map.put("MIDDLEINT", "k");
map.put("MIN", "f");
map.put("MINUTE", "f");
map.put("MINUTE_MICROSECOND", "k");
map.put("MINUTE_SECOND", "k");
map.put("MKDIR", "f");
map.put("MOD", "o");
map.put("MODE", "n");
map.put("MODIFIES", "k");
map.put("MONEY", "t");
map.put("MONTH", "f");
map.put("MONTHNAME", "f");
map.put("NAME_CONST", "f");
map.put("NATURAL", "n");
map.put("NATURAL FULL", "k");
map.put("NATURAL FULL OUTER JOIN", "k");
map.put("NATURAL INNER", "k");
map.put("NATURAL JOIN", "k");
map.put("NATURAL LEFT", "k");
map.put("NATURAL LEFT OUTER", "k");
map.put("NATURAL LEFT OUTER JOIN", "k");
map.put("NATURAL OUTER", "k");
map.put("NATURAL RIGHT", "k");
map.put("NATURAL RIGHT OUTER JOIN", "k");
map.put("NETMASK", "f");
map.put("NEXT VALUE", "n");
map.put("NEXT VALUE FOR", "k");
map.put("NEXTVAL", "f");
map.put("NOT", "o");
map.put("NOT BETWEEN", "o");
map.put("NOT IN", "k");
map.put("NOT LIKE", "o");
map.put("NOT REGEXP", "o");
map.put("NOT RLIKE", "o");
map.put("NOT SIMILAR", "o");
map.put("NOT SIMILAR TO", "o");
map.put("NOTNULL", "k");
map.put("NOW", "f");
map.put("NOWAIT", "k");
map.put("NO_WRITE_TO_BINLOG", "k");
map.put("NTH_VALUE", "f");
map.put("NTILE", "f");
map.put("NULL", "v");
map.put("NULLIF", "f");
map.put("NUMERIC", "t");
map.put("NZ", "f");
map.put("OBJECTPROPERTY", "f");
map.put("OBJECTPROPERTYEX", "f");
map.put("OBJECT_DEFINITION", "f");
map.put("OBJECT_ID", "f");
map.put("OBJECT_NAME", "f");
map.put("OBJECT_SCHEMA_NAME", "f");
map.put("OCT", "f");
map.put("OCTET_LENGTH", "f");
map.put("OFFSET", "k");
map.put("OID", "t");
map.put("OLD_PASSWORD", "f");
map.put("ONE_SHOT", "k");
map.put("OPEN", "k");
map.put("OPENDATASOURCE", "f");
map.put("OPENQUERY", "f");
map.put("OPENROWSET", "f");
map.put("OPENXML", "f");
map.put("OPTIMIZE", "k");
map.put("OPTION", "k");
map.put("OPTIONALLY", "k");
map.put("OR", "&");
map.put("ORD", "f");
map.put("ORDER", "n");
map.put("ORDER BY", "B");
map.put("ORIGINAL_DB_NAME", "f");
map.put("ORIGINAL_LOGIN", "f");
map.put("OUT", "k");
map.put("OUTER", "n");
map.put("OUTFILE", "k");
map.put("OVERLAPS", "f");
map.put("OVERLAY", "f");
//map.put("OWN3D", "k");
//map.put("OWN3D BY", "B");
map.put("PARSENAME", "f");
map.put("PARTITION", "k");
map.put("PARTITION BY", "B");
map.put("PASSWORD", "n");
map.put("PATHINDEX", "f");
map.put("PATINDEX", "f");
map.put("PERCENTILE_COUNT", "f");
map.put("PERCENTILE_DISC", "f");
map.put("PERCENTILE_RANK", "f");
map.put("PERCENT_RANK", "f");
map.put("PERIOD_ADD", "f");
map.put("PERIOD_DIFF", "f");
map.put("PERMISSIONS", "f");
map.put("PG_ADVISORY_LOCK", "f");
map.put("PG_BACKEND_PID", "f");
map.put("PG_CANCEL_BACKEND", "f");
map.put("PG_CLIENT_ENCODING", "f");
map.put("PG_CONF_LOAD_TIME", "f");
map.put("PG_CREATE_RESTORE_POINT", "f");
map.put("PG_HAS_ROLE", "f");
map.put("PG_IS_IN_RECOVERY", "f");
map.put("PG_IS_OTHER_TEMP_SCHEMA", "f");
map.put("PG_LISTENING_CHANNELS", "f");
map.put("PG_LS_DIR", "f");
map.put("PG_MY_TEMP_SCHEMA", "f");
map.put("PG_POSTMASTER_START_TIME", "f");
map.put("PG_READ_BINARY_FILE", "f");
map.put("PG_READ_FILE", "f");
map.put("PG_RELOAD_CONF", "f");
map.put("PG_ROTATE_LOGFILE", "f");
map.put("PG_SLEEP", "f");
map.put("PG_START_BACKUP", "f");
map.put("PG_STAT_FILE", "f");
map.put("PG_STOP_BACKUP", "f");
map.put("PG_SWITCH_XLOG", "f");
map.put("PG_TERMINATE_BACKEND", "f");
map.put("PG_TRIGGER_DEPTH", "f");
map.put("PI", "f");
map.put("POSITION", "f");
map.put("POW", "f");
map.put("POWER", "f");
map.put("PRECISION", "k");
map.put("PREVIOUS VALUE", "n");
map.put("PREVIOUS VALUE FOR", "k");
map.put("PRIMARY", "k");
map.put("PRINT", "T");
map.put("PROCEDURE", "k");
map.put("PROCEDURE ANALYSE", "f");
map.put("PUBLISHINGSERVERNAME", "f");
map.put("PURGE", "k");
map.put("PWDCOMPARE", "f");
map.put("PWDENCRYPT", "f");
map.put("QUARTER", "f");
map.put("QUOTE", "f");
map.put("QUOTENAME", "f");
map.put("QUOTE_IDENT", "f");
map.put("QUOTE_LITERAL", "f");
map.put("QUOTE_NULLABLE", "f");
map.put("RADIANS", "f");
map.put("RAISEERROR", "E");
map.put("RAND", "f");
map.put("RANDOM", "f");
map.put("RANDOMBLOB", "f");
map.put("RANGE", "k");
map.put("RANK", "f");
map.put("READ", "k");
map.put("READ WRITE", "k");
map.put("READS", "k");
map.put("READ_WRITE", "k");
map.put("REAL", "t");
map.put("REFERENCES", "k");
map.put("REGCLASS", "t");
map.put("REGCONFIG", "t");
map.put("REGDICTIONARY", "t");
map.put("REGEXP", "o");
map.put("REGEXP_INSTR", "f");
map.put("REGEXP_MATCHES", "f");
map.put("REGEXP_REPLACE", "f");
map.put("REGEXP_SPLIT_TO_ARRAY", "f");
map.put("REGEXP_SPLIT_TO_TABLE", "f");
map.put("REGEXP_SUBSTR", "f");
map.put("REGOPER", "t");
map.put("REGOPERATOR", "t");
map.put("REGPROC", "t");
map.put("REGPROCEDURE", "t");
map.put("REGTYPE", "t");
map.put("RELEASE", "k");
map.put("RELEASE_LOCK", "f");
map.put("RENAME", "k");
map.put("REPEAT", "k");
map.put("REPLACE", "k");
map.put("REPLICATE", "f");
map.put("REQUIRE", "k");
map.put("RESIGNAL", "k");
map.put("RESTRICT", "k");
map.put("RETURN", "k");
map.put("REVERSE", "f");
map.put("REVOKE", "k");
map.put("RIGHT", "n");
map.put("RIGHT JOIN", "k");
map.put("RIGHT OUTER", "k");
map.put("RIGHT OUTER JOIN", "k");
map.put("RLIKE", "o");
map.put("ROUND", "f");
map.put("ROW", "f");
map.put("ROW_COUNT", "f");
map.put("ROW_NUMBER", "f");
map.put("ROW_TO_JSON", "f");
map.put("RPAD", "f");
map.put("RTRIM", "f");
map.put("SCHAMA_NAME", "f");
map.put("SCHEMA", "k");
map.put("SCHEMAS", "k");
map.put("SCHEMA_ID", "f");
map.put("SCOPE_IDENTITY", "f");
map.put("SECOND_MICROSECOND", "k");
map.put("SEC_TO_TIME", "f");
map.put("SELECT", "E");
map.put("SELECT ALL", "E");
map.put("SELECT DISTINCT", "E");
map.put("SENSITIVE", "k");
map.put("SEPARATOR", "k");
map.put("SERIAL", "t");
map.put("SERIAL2", "t");
map.put("SERIAL4", "t");
map.put("SERIAL8", "t");
map.put("SERVERPROPERTY", "f");
map.put("SESSION_USER", "f");
map.put("SET", "E");
map.put("SETATTR", "f");
map.put("SETSEED", "f");
map.put("SETVAL", "f");
map.put("SET_BIT", "f");
map.put("SET_BYTE", "f");
map.put("SET_CONFIG", "f");
map.put("SET_MASKLEN", "f");
map.put("SHA", "f");
map.put("SHA1", "f");
map.put("SHA2", "f");
map.put("SHOW", "n");
map.put("SHUTDOWN", "T");
map.put("SIGN", "f");
map.put("SIGNAL", "k");
map.put("SIGNBYASMKEY", "f");
map.put("SIGNBYCERT", "f");
map.put("SIMILAR", "k");
map.put("SIMILAR TO", "o");
map.put("SIN", "f");
map.put("SLEEP", "f");
map.put("SMALLDATETIMEFROMPARTS", "f");
map.put("SMALLINT", "t");
map.put("SMALLSERIAL", "t");
map.put("SOME", "f");
map.put("SOUNDEX", "f");
map.put("SOUNDS", "o");
map.put("SOUNDS LIKE", "o");
map.put("SPACE", "f");
map.put("SPATIAL", "k");
map.put("SPECIFIC", "k");
map.put("SPLIT_PART", "f");
map.put("SQL", "k");
map.put("SQLEXCEPTION", "k");
map.put("SQLSTATE", "k");
map.put("SQLWARNING", "k");
map.put("SQL_BIG_RESULT", "k");
map.put("SQL_BUFFER_RESULT", "k");
map.put("SQL_CACHE", "k");
map.put("SQL_CALC_FOUND_ROWS", "k");
map.put("SQL_NO_CACHE", "k");
map.put("SQL_SMALL_RESULT", "k");
map.put("SQL_VARIANT_PROPERTY", "f");
map.put("SQRT", "f");
map.put("SSL", "k");
map.put("STARTING", "k");
map.put("STATEMENT_TIMESTAMP", "f");
map.put("STATS_DATE", "f");
map.put("STDDEV", "f");
map.put("STDDEV_POP", "f");
map.put("STDDEV_SAMP", "f");
map.put("STRAIGHT_JOIN", "k");
map.put("STRCMP", "f");
map.put("STRCOMP", "f");
map.put("STRCONV", "f");
map.put("STRING_AGG", "f");
map.put("STRING_TO_ARRAY", "f");
map.put("STRPOS", "f");
map.put("STR_TO_DATE", "f");
map.put("STUFF", "f");
map.put("SUBDATE", "f");
map.put("SUBSTR", "f");
map.put("SUBSTRING", "f");
map.put("SUBSTRING_INDEX", "f");
map.put("SUBTIME", "f");
map.put("SUM", "f");
map.put("SUSER_ID", "f");
map.put("SUSER_NAME", "f");
map.put("SUSER_SID", "f");
map.put("SUSER_SNAME", "f");
map.put("SWITCHOFFET", "f");
map.put("SYS.DATABASE_NAME", "n");
map.put("SYS.FN_BUILTIN_PERMISSIONS", "f");
map.put("SYS.FN_GET_AUDIT_FILE", "f");
map.put("SYS.FN_MY_PERMISSIONS", "f");
map.put("SYS.STRAGG", "f");
map.put("SYSCOLUMNS", "k");
map.put("SYSDATE", "f");
map.put("SYSDATETIME", "f");
map.put("SYSDATETIMEOFFSET", "f");
map.put("SYSOBJECTS", "k");
map.put("SYSTEM_USER", "f");
map.put("SYSUSERS", "k");
map.put("SYSUTCDATETME", "f");
map.put("TABLE", "k");
map.put("TAN", "f");
map.put("TERMINATED", "k");
map.put("TERTIARY_WEIGHTS", "f");
map.put("TEXT", "t");
map.put("TEXTPOS", "f");
map.put("TEXTPTR", "f");
map.put("TEXTVALID", "f");
map.put("THEN", "k");
map.put("TIME", "k");
map.put("TIMEDIFF", "f");
map.put("TIMEFROMPARTS", "f");
map.put("TIMEOFDAY", "f");
map.put("TIMESERIAL", "f");
map.put("TIMESTAMP", "t");
map.put("TIMESTAMPADD", "f");
map.put("TIMEVALUE", "f");
map.put("TIME_FORMAT", "f");
map.put("TIME_TO_SEC", "f");
map.put("TINYBLOB", "k");
map.put("TINYINT", "k");
map.put("TINYTEXT", "k");
map.put("TODATETIMEOFFSET", "f");
map.put("TOP", "k");
map.put("TOTAL", "f");
map.put("TOTAL_CHANGES", "f");
map.put("TO_ASCII", "f");
map.put("TO_BASE64", "f");
map.put("TO_CHAR", "f");
map.put("TO_DATE", "f");
map.put("TO_DAYS", "f");
map.put("TO_HEX", "f");
map.put("TO_NUMBER", "f");
map.put("TO_SECONDS", "f");
map.put("TO_TIMESTAMP", "f");
map.put("TRAILING", "n");
map.put("TRANSACTION_TIMESTAMP", "f");
map.put("TRANSLATE", "f");
map.put("TRIGGER", "k");
map.put("TRIGGER_NESTLEVEL", "f");
map.put("TRIM", "f");
map.put("TRUE", "1");
map.put("TRUNC", "f");
map.put("TRUNCATE", "f");
map.put("TRY_CAST", "f");
map.put("TRY_CONVERT", "f");
map.put("TRY_PARSE", "f");
map.put("TYPEOF", "f");
map.put("TYPEPROPERTY", "f");
map.put("TYPE_ID", "f");
map.put("TYPE_NAME", "f");
map.put("UCASE", "f");
map.put("UESCAPE", "o");
map.put("UNCOMPRESS", "f");
map.put("UNCOMPRESS_LENGTH", "f");
map.put("UNDO", "k");
map.put("UNHEX", "f");
map.put("UNICODE", "f");
map.put("UNION", "U");
map.put("UNION ALL", "U");
map.put("UNION ALL DISTINCT", "U");
map.put("UNION DISTINCT", "U");
map.put("UNION DISTINCT ALL", "U");
map.put("UNIQUE", "n");
map.put("UNIX_TIMESTAMP", "f");
map.put("UNI_ON", "U");
map.put("UNKNOWN", "v");
map.put("UNLOCK", "k");
map.put("UNNEST", "f");
map.put("UNSIGNED", "k");
map.put("UPDATE", "E");
map.put("UPDATEXML", "f");
map.put("UPPER", "f");
map.put("UPPER_INC", "f");
map.put("UPPER_INF", "f");
map.put("USAGE", "k");
map.put("USE", "T");
map.put("USER", "n");
map.put("USER_ID", "n");
map.put("USER_LOCK.SLEEP", "f");
map.put("USER_NAME", "n");
map.put("USING", "f");
map.put("UTC_DATE", "k");
map.put("UTC_TIME", "k");
map.put("UTC_TIMESTAMP", "k");
map.put("UTL_HTTP.REQUEST", "f");
map.put("UTL_INADDR.GET_HOST_ADDRESS", "f");
map.put("UTL_INADDR.GET_HOST_NAME", "f");
map.put("UUID", "f");
map.put("UUID_SHORT", "f");
map.put("VALUES", "k");
map.put("VAR", "f");
map.put("VARBINARY", "k");
map.put("VARCHAR", "t");
map.put("VARCHARACTER", "k");
map.put("VARIANCE", "f");
map.put("VARP", "f");
map.put("VARYING", "k");
map.put("VAR_POP", "f");
map.put("VAR_SAMP", "f");
map.put("VERIFYSIGNEDBYASMKEY", "f");
map.put("VERIFYSIGNEDBYCERT", "f");
map.put("VERSION", "f");
map.put("VOID", "t");
map.put("WAIT", "k");
map.put("WAITFOR", "n");
map.put("WAITFOR DELAY", "E");
map.put("WAITFOR RECEIVE", "E");
map.put("WAITFOR TIME", "E");
map.put("WEEK", "f");
map.put("WEEKDAY", "f");
map.put("WEEKDAYNAME", "f");
map.put("WEEKOFYEAR", "f");
map.put("WHEN", "k");
map.put("WHERE", "k");
map.put("WHILE", "T");
map.put("WIDTH_BUCKET", "f");
map.put("WITH", "n");
map.put("WITH ROLLUP", "k");
map.put("XMLAGG", "f");
map.put("XMLCOMMENT", "f");
map.put("XMLCONCAT", "f");
map.put("XMLELEMENT", "f");
map.put("XMLEXISTS", "f");
map.put("XMLFOREST", "f");
map.put("XMLFORMAT", "f");
map.put("XMLPI", "f");
map.put("XMLROOT", "f");
map.put("XMLTYPE", "f");
map.put("XML_IS_WELL_FORMED", "f");
map.put("XOR", "&");
map.put("XPATH", "f");
map.put("XPATH_EXISTS", "f");
map.put("XP_EXECRESULTSET", "k");
map.put("YEAR", "f");
map.put("YEARWEEK", "f");
map.put("YEAR_MONTH", "k");
map.put("ZEROBLOB", "f");
map.put("ZEROFILL", "k");
map.put("^=", "o");
map.put("_ARMSCII8", "t");
map.put("_ASCII", "t");
map.put("_BIG5", "t");
map.put("_BINARY", "t");
map.put("_CP1250", "t");
map.put("_CP1251", "t");
map.put("_CP1257", "t");
map.put("_CP850", "t");
map.put("_CP852", "t");
map.put("_CP866", "t");
map.put("_CP932", "t");
map.put("_DEC8", "t");
map.put("_EUCJPMS", "t");
map.put("_EUCKR", "t");
map.put("_GB2312", "t");
map.put("_GBK", "t");
map.put("_GEOSTD8", "t");
map.put("_GREEK", "t");
map.put("_HEBREW", "t");
map.put("_HP8", "t");
map.put("_KEYBCS2", "t");
map.put("_KOI8R", "t");
map.put("_KOI8U", "t");
map.put("_LATIN1", "t");
map.put("_LATIN2", "t");
map.put("_LATIN5", "t");
map.put("_LATIN7", "t");
map.put("_MACCE", "t");
map.put("_MACROMAN", "t");
map.put("_SJIS", "t");
map.put("_SWE7", "t");
map.put("_TIS620", "t");
map.put("_UJIS", "t");
map.put("_USC2", "t");
map.put("_UTF8", "t");
map.put("|/", "o");
map.put("|=", "o");
map.put("||", "&");
map.put("~*", "o");
}
//--------------------------------------------------------------------------------
public static String replaceAll( String target, String from, String to )
{
int len = from.length();
if( len == 0 )
	{
	return target;
	}
StringBuffer buf = new StringBuffer( target.length() );
while( true )
	{
	int pos = target.indexOf( from );
	if( pos == -1 )
		{
		break;
		}
	buf.append( target.substring( 0, pos ) );
	buf.append( to );
	target = target.substring( pos + len );
	}
buf.append( target );
return buf.toString();
}
//--------------------------------------------------------------------------------
public static boolean isSQLi( final String input )
{
try
	{
	return isSQLiImpl1( input );
	}
catch( Exception e )
	{
	e.printStackTrace();
	return false;
	}
}
//--------------------------------------------------------------------------------
public static void tokenize( String input, List valueList, String[] allTokenBuf )
{
tokenize( input, valueList, allTokenBuf, 0, false );
}
//--------------------------------------------------------------------------------
/*
 * return value: size of valueList before folding
 */
public static int tokenize( String input, List valueList, String[] allTokenBuf, int flags, final boolean withFolding )
{
StringBuffer token = new StringBuffer();
final int inputLength = input.length();
int totalProcessedLength = 0;
while( inputLength > totalProcessedLength )
	{
	String[] processed = new String[ 1 ];
	String[] tokenBuf = new String[ 1 ];
	tokenizeOne( input, processed, tokenBuf, flags );
	
	token.append( tokenBuf[ 0 ] );
	totalProcessedLength += processed[ 0 ].length();
	input = input.substring( processed[ 0 ].length() );
	
	if( !tokenBuf[ 0 ].equals( "w" ) )
		{
		valueList.add( processed[ 0 ] );
		}
	
		//fold
	if( withFolding && token.length() >= LIBINJECTION_SQLI_MAX_TOKENS )
		{
		List _list = new ArrayList();
		_list.addAll( valueList );
		final String foldedToken = fold( _list, token.toString().replaceAll( "w+", "" ) );
		if( foldedToken.length() >= LIBINJECTION_SQLI_MAX_TOKENS )
			{
			int valueListSize = valueList.size();
			valueList.clear();
			valueList.addAll( _list );
			allTokenBuf[ 0 ] = foldedToken;
			return valueListSize;
			}
		}
	}
allTokenBuf[ 0 ] = token.toString();


if( withFolding )
	{
	int valueListSize = valueList.size();
	final String foldedToken = fold( valueList, token.toString().replaceAll( "w+", "" ) );
	allTokenBuf[ 0 ] = foldedToken;
	return valueListSize;
	}
else
	{
	return valueList.size();
	}

}
//--------------------------------------------------------------------------------
public static void tokenizeOne( final String input, String[] processed, String[] tokenBuf, int flags )
{
if( input == null || input.length() == 0 )
	{
	return;
	}

//String input = orig.toUpperCase();

	//process white (space)
char firstChar = input.charAt( 0 );
int i = ( int )( ( byte )firstChar );
String firstStr = firstChar + "";

	//white space
if( ( 0 <= i &&  i < 33 )  || i == 127 || isWhiteSpaceChar( firstChar ) )
	{
	processed[ 0 ] = firstStr;
	tokenBuf[ 0 ] = "w";
	return;
	}

	//number
if( firstChar == '.' || ( 48 <= i && i <= 57 ) )
	{
	String numberStr = getMatch( "^[0-9\\.]+", input );
	if( firstChar == '.' && numberStr.length() == 1 )
		{
		processed[ 0 ] = ".";
		tokenBuf[ 0 ] = ".";
		return;
		}
	else
		{
		final StringBuffer buf = new StringBuffer();
		buf.append( numberStr );
		boolean moreCharacter = input.length() > numberStr.length();
		boolean haveExp = false;
		boolean haveE = false;
		if( moreCharacter )
			{
			final char nextChar = input.charAt( numberStr.length() );
			if( input.startsWith( "0x" ) || input.startsWith( "0X" ) )
				{
				final String bStr = getMatchIgnoreCase( "0x[0-9A-F]*", input );
				if( bStr.length() == 2 )
					{
					processed[ 0 ] = bStr;
					tokenBuf[ 0 ] = "n";
					return;
					}
				else
					{
					processed[ 0 ] = bStr;
					tokenBuf[ 0 ] = "1";
					return;
					}				
				}
			else if( nextChar == 'e' || nextChar == 'E' )
				{
				haveE = true;
				buf.append( nextChar );
				if( input.length() > numberStr.length() + 1 )
					{
					final String digitStr = getMatch( "^[-+]?[0-9]*", input.substring( numberStr.length() + 1 ) );
					if( digitStr.length() > 0 )
						{
						buf.append( digitStr );
						haveExp = true;
						}
					}
				}
			else if( input.startsWith( "0b" ) || input.startsWith( "0B" ) )
				{
				final String bStr = getMatchIgnoreCase( "0b[01]*", input );
				if( bStr.length() == 2 )
					{
					processed[ 0 ] = bStr;
					tokenBuf[ 0 ] = "n";
					return;
					}
				else
					{
					processed[ 0 ] = bStr;
					tokenBuf[ 0 ] = "1";
					return;
					}
				}
			}
		
		moreCharacter = input.length() > buf.length();
		if( moreCharacter )
			{
			numberStr = buf.toString();
			final char nextChar = input.charAt( numberStr.length() );
			if( nextChar == 'f' || nextChar == 'F' || nextChar == 'd' || nextChar == 'D' )
				{
					//end of input?
				if( input.length() == numberStr.length() + 1 )
					{
					processed[ 0 ] = input;
					tokenBuf[ 0 ] = "1";
					return;
					}
				else
					{
					final char secondChar = input.charAt( numberStr.length() + 1 );
					if( isWhiteSpaceChar( secondChar ) || secondChar == ';' )
						{
						processed[ 0 ] = input.substring( 0, numberStr.length() + 1 );
						tokenBuf[ 0 ] = "1";
						return;
						}
					else if( secondChar == 'u' || secondChar == 'U' )
						{
						processed[ 0 ] = input.substring( 0, numberStr.length() + 1 );
						tokenBuf[ 0 ] = "1";
						return;							
						}
					}
				}
			}
		
		final String _processedNumberStr = buf.toString();
		boolean havDot = _processedNumberStr.indexOf( "." ) > -1;
		processed[ 0 ] = _processedNumberStr;
		
		if( havDot && haveE && haveExp == false )
			{
			tokenBuf[ 0 ] = "n";
			}
		else
			{
			tokenBuf[ 0 ] = "1";
			}
		return;
		}
	}

if( setOfChar.contains( firstStr ) )
	{
	processed[ 0 ] = firstStr;
	tokenBuf[ 0 ] = firstStr;
	return;
	}
/*
else if( firstChar == ':' )
	{
	if( input.startsWith( ":=" ) )
		{
		processed[ 0 ] = ":=";
		tokenBuf[ 0 ] = "o";
		return;
		}
	else
		{
		processed[ 0 ] = ":";
		tokenBuf[ 0 ] = ":";
		return;
		}
	}
*/
else if( setOfOperator.contains( firstStr ) )
	{
	if( input.startsWith( "<=>" ) )
		{
		processed[ 0 ] = "<=>";
		tokenBuf[ 0 ] = "o";
		return;
		}
	if( input.startsWith( ":=" ) )
		{
		processed[ 0 ] = ":=";
		tokenBuf[ 0 ] = "o";
		return;
		}	
	if( input.length() > 1 )
		{
		String firstTwoStr = input.substring( 0, 2 );
		if( setOf2byteOperator.contains( firstTwoStr ) )
			{
			processed[ 0 ] = firstTwoStr;
			tokenBuf[ 0 ] = "o";
			return;
			}
		else if( map.containsKey( firstTwoStr.toUpperCase() ) )
			{
				// && ||
			processed[ 0 ] = firstTwoStr;
			tokenBuf[ 0 ] = ( String )map.get( firstTwoStr.toUpperCase() );
			return;
			}
		}
	if( firstChar == ':' )
		{
		processed[ 0 ] = ":";
		tokenBuf[ 0 ] = ":";
		return;	
		}
	
	processed[ 0 ] = firstStr;
	tokenBuf[ 0 ] = "o";
	return;
	}

else if( firstChar == '-' )
	{
    /*
     * five cases
     * 1) --[white]  this is always a SQL comment
     * 2) --[EOF]    this is a comment
     * 3) --[notwhite] in MySQL this is NOT a comment but two unary operators
     * 4) --[notwhite] everyone else thinks this is a comment
     * 5) -[not dash]  '-' is a unary operator
     */
	if( input.startsWith( "--" ) )
		{
		boolean commentUntilEndOfLine = false;
		if( input.length() == 2 )
			{
				//input is "--"  case 2
			processed[ 0 ] = input;
			tokenBuf[ 0 ] = "c";
			return;
			}
		else
				//input length > 2
			{
			char tc = input.charAt( 2 );
			if( isWhiteSpaceChar( tc ) )
				{
				commentUntilEndOfLine = true;
				}
			else
				{
				if( ( flags & SQL_MYSQL ) != 0 )
					{
						//case 3
					processed[ 0 ] = "-";
					tokenBuf[ 0 ] = "o";
					return;
					}
				else
					{
					commentUntilEndOfLine = true;
					}
				}
			}
		if( commentUntilEndOfLine )
			{
			int index = input.indexOf( '\n' );
			if( index == -1 )
				{
				processed[ 0 ] = input;
				tokenBuf[ 0 ] = "c";
				return;
				}
			else
				{
				processed[ 0 ] = input.substring( 0, index + 1 );
				tokenBuf[ 0 ] = "c";
				return;
				}
			}
		}
	else
		{
		processed[ 0 ] = "-";
		tokenBuf[ 0 ] = "o";
		return;
		}
	}

else if( firstChar == '/' )
	{
	if( input.startsWith( "/*" ) )
		{
		parseComment( input, processed, tokenBuf );
		return;
		}
	else
		{
		processed[ 0 ] = "/";
		tokenBuf[ 0 ] = "o";
		return;
		}
	}

else if( firstChar == '\''|| firstChar == '"' )
	{
	parseQuoteString( input, processed, tokenBuf, firstChar, flags );
	return;
	}

else if( firstChar == '\\' )
	{
	if( input.startsWith( "\\N" ) )
		{
		processed[ 0 ] = "\\N"; //MySQL weired \N
		tokenBuf[ 0 ] = "1";
		return;
		}
	else
		{
		processed[ 0 ] = "\\";
		tokenBuf[ 0 ] = "\\";
		return;
		}
	}

	//variable like @@version
else if( firstChar == '@' )
	{
	String atmark = "@";
	if( input.startsWith( "@@" ) )
		{
		atmark = "@@";
		}
	if( input.startsWith( atmark + "`" )
	 || input.startsWith( atmark + "'" )
	 || input.startsWith( atmark + "\"" )
	  )
		{
		final char _delimiter = input.charAt( atmark.length() );
		int variableNameLength = parseString( input, _delimiter );
		processed[ 0 ] = input.substring( 0, variableNameLength );
		tokenBuf[ 0 ] = "v";
		return;
		}
	else
		{
		int wordIndex = getWordLength( input.substring( atmark.length() ), true );
		String variable = input.substring( 0, atmark.length() + wordIndex );
		processed[ 0 ] = variable;
		tokenBuf[ 0 ] = "v";
		return;
		}
	}

else if( firstChar == '#' )
	{
	if( ( flags & SQL_MYSQL ) != 0 )
		{
		int index = input.indexOf( '\n' );
		if( index == -1 )
			{
			processed[ 0 ] = input;
			tokenBuf[ 0 ] = "c";
			return;
			}
		else
			{
			processed[ 0 ] = input.substring( 0, index + 1 );
			tokenBuf[ 0 ] = "c";
			return;
			}
		}
	else
		{
		processed[ 0 ] = "#";
		tokenBuf[ 0 ] = "o";
		return;
		}
	}

else if( firstChar == '$' )
	{
	if( input.equals( "$." ) )
		{
		processed[ 0 ] = input;
		tokenBuf[ 0 ] = "n";
		return;
		}
	String match = getMatch( "^\\$[0-9\\.,]+", input );
	if( match.length() > 0 )
		{
		if( match.equals( "$." ) )
			{
			
			}
		else
			{
			processed[ 0 ] = match;
			tokenBuf[ 0 ] = "1";
			return;
			}
		}
	else if( input.startsWith( "$$" ) )
		{
		int nextDollarIndex = input.indexOf( "$$", 2 );
		if( nextDollarIndex > -1 )
			{
			processed[ 0 ] = input.substring( 0, nextDollarIndex + 2 );
			tokenBuf[ 0 ] = "s";
			return;
			}
		else
			{
			processed[ 0 ] = input;
			tokenBuf[ 0 ] = "s";
			return;
			}
		}
	else
		{
		int nextDollarIndex = input.indexOf( "$", 1 );
		if( nextDollarIndex > 1 )
			{
			String quoteStr = input.substring( 0, nextDollarIndex + 1 );
			if( quoteStr.matches( "^\\$[a-zA-Z]+[a-zA-Z0-9]*\\$$" ) )
				{
					//$foo$ found
				int closeQuoteIndex = input.indexOf( quoteStr, quoteStr.length() );
				if( closeQuoteIndex > -1 )
					{
						//$foo$ $foo$ found
					processed[ 0 ] = input.substring( 0, closeQuoteIndex + quoteStr.length() );
					tokenBuf[ 0 ] = "s";
					return;
					}
				else
					{
					processed[ 0 ] = input;
					tokenBuf[ 0 ] = "s";
					return;
					}
				}
			else
				{
				processed[ 0 ] = "$";
				tokenBuf[ 0 ] = "n";
				return;
				}
			}
		else
			{
			processed[ 0 ] = "$";
			tokenBuf[ 0 ] = "n";
			return;
			}
		}
	}

else if( firstChar == '?' || firstChar == ']' )
	{
	processed[ 0 ] = firstStr;
	tokenBuf[ 0 ] = "?";
	return;
	}

else if( firstChar == '`' )
	{
	int index = parseString( input, '`', false );
	String str = input.substring( 0, index );
	String value = ( String )map.get( ( replaceAll( str, "`", "" ) ).toUpperCase() );
	if( value != null && value.equals( "f" ) )
		{
		processed[ 0 ] = str;
		tokenBuf[ 0 ] = "f";
		return;
		}
	else
		{
		processed[ 0 ] = str;
		tokenBuf[ 0 ] = "n";
		return;
		}
	}

else if( input.startsWith( "X'" ) || input.startsWith( "x'" ) )
	{
	String hexMatch = getMatchIgnoreCase( "^X'[0-9a-fA-F]*'", input );
	if( hexMatch.length() >= 3 )
		{
		processed[ 0 ] = hexMatch;
		tokenBuf[ 0 ] = "1";
		return;
		}
	}

	//PostgreSQL Escape String
else if( input.startsWith( "E'" ) || input.startsWith( "e'" ) )
	{
	final int length = parseString( input, '\'' );
	final String estring = input.substring( 0, length );
	processed[ 0 ] = estring;
	tokenBuf[ 0 ] = "s";
	return;
	}

	//Binary literal B'01' PostgreSQL and MySQL?
else if( input.startsWith( "B'" ) || input.startsWith( "b'" ) )
	{
	final String match = getMatchIgnoreCase( "^B'[01]*'", input );
	if( match.length() > 0 )
		{
		processed[ 0 ] = match;
		tokenBuf[ 0 ] = "1";
		return;
		}
	}

	//PostgreSQL U&'123'
else if( input.startsWith( "U&'" ) || input.startsWith( "u&'" ) )
	{
	final int length = parseString( input, '\'' );
	final String estring = input.substring( 0, length );
	processed[ 0 ] = estring;
	tokenBuf[ 0 ] = "s";
	return;	
	}

	//Oracle N|Q String
	//MySQL N String
else if( firstChar == 'Q'
      || firstChar == 'N'
      || firstChar == 'q'
      || firstChar == 'n'
      )
	{
	String prefix = getMatch( "^[nN]?[qQ]{1}'", input ); // nq' or q'
	if( prefix.length() > 0 )
		{
		String leftQuote = getMatch( "^" + prefix + "([\\(\\[\\<\\{]{1})", input );
		String rightQuote = null;
		if( leftQuote.length() == 1 )
			{
				//Oracle
			if(      leftQuote.equals( "(" ) ){ rightQuote = ")"; }
			else if( leftQuote.equals( "[" ) ){ rightQuote = "]"; }
			else if( leftQuote.equals( "{" ) ){ rightQuote = "}"; }
			else if( leftQuote.equals( "<" ) ){ rightQuote = ">"; }
			
			int index = input.indexOf( rightQuote + "'", 2 );
			if( index == -1 )
				{
				processed[ 0 ] = input;
				tokenBuf[ 0 ] = "s";
				return;
				}
			else
				{
				processed[ 0 ] = input.substring( 0, index + 2 );
				tokenBuf[ 0 ] = "s";
				return;
				}
			}
		else
			{
				//nq'X123X'
			if( input.length() > prefix.length() )
				{
				String rest = input.substring( prefix.length() );
				leftQuote = rest.charAt( 0 ) + "";
				int _index = rest.indexOf( leftQuote + "'" );
				if( _index == -1 )
					{
					processed[ 0 ] = input;
					tokenBuf[ 0 ] = "s";
					return;
					}
				else
					{
					processed[ 0 ] = input.substring( 0, prefix.length() + _index + 2 );
					tokenBuf[ 0 ] = "s";
					return;
					}
				}
			}
		}
	else if( input.startsWith( "N'" ) || input.startsWith( "n'" ) )
		{
			//MySQL N'123'
		final int length = parseString( input, '\'' );
		final String nstring = input.substring( 0, length );
		processed[ 0 ] = nstring;
		tokenBuf[ 0 ] = "s";
		return;
		}
	}

else if( firstChar == '[' )
	{
	int index = input.indexOf( ']' );
	if( index > -1 )
		{
		processed[ 0 ] = input.substring( 0, index + 1 );
		tokenBuf[ 0 ] = "n";
		return;
		}
	}

	//process word
String word = input.substring( 0, getWordLength( input ) );
Object value = map.get( word.toUpperCase() );
if( value != null )
	{
	processed[ 0 ] = word;
	tokenBuf[ 0 ] = ( String )value;
	return;
	}
else if( word.length() > 0 )
	{
	processed[ 0 ] = word;
	tokenBuf[ 0 ] = "n";
	return;
	}
}
//--------------------------------------------------------------------------------
public static void parseQuoteString( String input, String[] processed, String[] tokenBuf, char delimiter, int flags )
{
int strLength = 0;

if( delimiter == '"' || ( flags & SQL_MYSQL ) != 0 ) // if MySQL, then allow backslash escape
	{
	strLength = parseString( input, delimiter, true );
	}
else
	{
	strLength = parseString( input, delimiter, false );	
	}

final String str = input.substring( 0, strLength );
processed[ 0 ] = str;
tokenBuf[ 0 ] = "s";
return;
}
//--------------------------------------------------------------------------------
public static int parseSingleQuoteString( final String input )
{
return parseString( input, '\'', true );
}
//--------------------------------------------------------------------------------
public static int parseString( final String input, char delimiter )
{
return parseString( input, delimiter, true );
}
//--------------------------------------------------------------------------------
public static int getWordLength( final String input )
{
return getWordLength( input, false );
}
//--------------------------------------------------------------------------------
public static int getWordLength( final String input, boolean stopOnTick )
{
if( input.length() == 0 )
	{
	return 0;
	}
for( int k = 0; k < input.length(); ++k )
	{	
	boolean found = false;
	char _char = input.charAt( k );
	String _str = _char + "";
	int c = ( int )_char;
	if( ( 0 <= c &&  c < 33 )  || c == 127 || isWhiteSpaceChar( _char ) ){}
	else if( _char == ':' ) {}
	else if( _char == ';' ) {}
	else if( setOfOperator.contains( _str ) ) {}
	else if( _char == '-' ) {}
	else if( _char == '/' ) {}
	else if( _char == '\'' || _char == '"' ) {}
	else if( _char == '\\' ) {}
	else if( _char == '@' ) {}
	else if( _char == '#' ) {}
	else if( _char == '?' ) {}
	else if( _char == '(' || _char == ')' ) {}
	else if( _char == '{' || _char == '}' ) {}
	else if( _char == '[' || _char == ']' )
		{
		if( _char == '[' && k == 0 )
			{
			found = true;
			}
		}
	else if( _char == ',' )	 {}
	else if( _char == '.' )
		{
			//keyword?
		final Object value = map.get( input.substring( 0, k ).toUpperCase() );
		if( value != null && value.equals( "E" ) )
			{
			return k;
			}
		else
			{
			found = true;
			}
		}
	else if( _char == '`' )
		{
		final String key = input.substring( 0, k ).toUpperCase();
		final Object value = map.get( key );
		if( value != null )
			{
			return k;
			}
		
		if( stopOnTick )
			{
			return k;
			}
		else
			{
			found = true;
			}
		}
	else
		{
		found = true;
		}
	
	if( found )
		{
		}
	else
		{
		return k;
		}
	}
return input.length();
}
//--------------------------------------------------------------------------------
public static int parseString( String input, char delimiter, boolean allowBackslashEscape )
{
//allowBackslashEscape = true;
final int MODE_SQL_STRING = 2;
final int MODE_DEFAULT = 3;
int mode = MODE_DEFAULT;

char lastChar = ' ';
final int length = input.length();
for( int i = 0; i < length; ++i )
	{
	boolean notUpdateLastChar = false;
	boolean isLastChar = ( i == input.length() -1 );
	char c = input.charAt( i );
	char nextChar = ' ';
	if( !isLastChar )
		{
		nextChar = input.charAt( i + 1 );
		}
	if( mode == MODE_DEFAULT )
		{
		if( c == delimiter )
			{
			mode = MODE_SQL_STRING;
			}
		else
			{
			}
		}
	else if( mode == MODE_SQL_STRING )
		{
		if( c == delimiter )
			{
			if( allowBackslashEscape && lastChar == '\\' )
				{
					//delimiter is escaped. continue...
				}
			else if( nextChar == delimiter )
				{
					//delimiter is escaped.
				++i;	//move cursor to next delimiter
				}
			else
				{
				if( isLastChar )
					{
						//end of parse
					mode = MODE_DEFAULT;
					}
				else
					{
					if( input.charAt( i + 1 ) == delimiter )
						{
							// ''
						++i;	//skip ''
						}
					else
						{
							//end of string
						mode = MODE_DEFAULT;
						return i + 1;
						}
					}			
				}
			
			}
		else if( c == '\\' )
			{
			if( lastChar == '\\' )
				{
					//double back slash
				lastChar = ' '; //change to space
				notUpdateLastChar = true;
				}
			}
		}
	if( notUpdateLastChar == true )
		{
		}
	else
		{
		lastChar = c;
		}
	}
return input.length();
}
//--------------------------------------------------------------------------------
public static void parseComment( String input, String[] processed, String[] tokenBuf )
{
//input must be starts with '/*'
int commentLength = parseCStyleComment( input, false, null );
String processedStr = input.substring( 0, commentLength );
processed[ 0 ] = processedStr;
if( processedStr.indexOf( "/*", 1 ) > -1 || processedStr.indexOf( "/*!" ) > -1 )
	{
	tokenBuf[ 0 ] = "X"; //PostgreSQL nested comment or MySQL comment
	}
else
	{
	tokenBuf[ 0 ] = "c";
	}
}
//--------------------------------------------------------------------------------
public static int parseCStyleComment( final String input, boolean allowNestedComments, boolean[] containsNestedComment )
{
//input must be starts with '/*'

final int MODE_C_STYLE_COMMENT = 0;
final int MODE_DEFAULT = 3;
int mode = MODE_DEFAULT;
int commentDepth = 0;
if( containsNestedComment == null )
	{
	containsNestedComment = new boolean[ 1 ];
	}
containsNestedComment[ 0 ] = false;

final int length = input.length();
for( int i = 0; i < length; ++i )
	{
	boolean isLastChar = ( i == input.length() -1 );
	char c = input.charAt( i );
	if( mode == MODE_DEFAULT )
		{
		if( c == '/' )
			{
			if( isLastChar )
				{
				}
			else
				{
				if( input.charAt( i + 1 ) == '*' )
					{
					mode = MODE_C_STYLE_COMMENT;
					++commentDepth;
					++i;
					}
				else
					{
					}
				}
			}
		else
			{
			}
		}
	else if( mode == MODE_C_STYLE_COMMENT )
		{
		if( c == '*' )
			{
			if( isLastChar )
				{
				}
			else
				{
				if( input.charAt( i + 1 ) == '/' )
					{
					--commentDepth;
					if( !allowNestedComments || commentDepth == 0 )
						{
						mode = MODE_DEFAULT;
						++i;
						return i + 1;
						}
					}
				}
			}
		else if( allowNestedComments
		     &&  c == '/'
		     && !isLastChar
		     && input.charAt( i + 1 ) == '*'
		     )
			{
			++commentDepth;
			if( containsNestedComment != null )
				{
				containsNestedComment[ 0 ] = true;
				}
			++i;
			}
		}
	}

return input.length();
}
//--------------------------------------------------------------------------------
private static String listToString( final List l )
{
StringBuffer buf = new StringBuffer( 180 );
for( int i = 0; i < l.size(); ++i )
	{
	if( i > 0 )
		{
		buf.append( " " );
		}
	buf.append( l.get( i ) );
	}
return buf.toString();
}
//--------------------------------------------------------------------------------
private static boolean isSqliImpl2( final String input, final String quote, final int flags )
throws Exception
{
final List valueList = new ArrayList();
String[] allTokenBuf = new String[ 1 ];

int valueListSize = tokenize( quote + input, valueList, allTokenBuf, flags, true );
String foldedToken = allTokenBuf[ 0 ];

if( valueList.size() == 0 )
	{
	return false;
	}

	//remove quote from head
String firstValue = ( String )valueList.get( 0 );
if( quote.length() > 0 && firstValue.charAt( 0 ) == quote.charAt( 0 ) )
	{
	valueList.set( 0, firstValue.substring( 1 ) );
	}

if( isSQLiImpl3( valueList, foldedToken, valueListSize ) )
	{
	return true;
	}
else
	{
	return false;
	}
}
//--------------------------------------------------------------------------------
private static boolean isSQLiImpl1( final String input )
throws Exception
{

	// No-Quote no-MySQL
if( isSqliImpl2( input, "", 0 ) )
	{
	return true;
	}

	// No-Quote MySQL
if( isSqliImpl2( input, "", SQL_MYSQL ) )
	{
	return true;
	}

	// Single-Quote no-MySQL
if( isSqliImpl2( input, "'", 0 ) )
	{
	return true;
	}

	// Single-Quote MySQL
if( isSqliImpl2( input, "'", SQL_MYSQL ) )
	{
	return true;
	}

	// Double-Quote MySQL
if( isSqliImpl2( input, "\"", SQL_MYSQL ) )
	{
	return true;
	}

return false;
}
//--------------------------------------------------------------------------------
protected static boolean isSQLiImpl3( final List valueList, final String foldedToken, final int valueListSize )
throws Exception
{
if( foldedToken.indexOf( "X" ) > -1 )
	{
	return true;
	}

if( smap.containsKey( foldedToken.toUpperCase() ) )
	{
	if( maybeFalsePositive( valueList, foldedToken, valueListSize ) )
		{
		return false;
		}
	else
		{
		return true;
		}
	}
else
	{
	return false;
	}
}
//--------------------------------------------------------------------------------
public static boolean maybeFalsePositive( final List valueList, final String token, final int valueListSize )
{
if( token.length() == 2 )
	{
	if( ( ( String )valueList.get( 1 ) ).charAt( 0 ) == '#' )
		{
		return true;
		}
	if( token.equals( "nc" ) )
		{
		if( ( ( String )valueList.get( 1 ) ).startsWith( "/" )  == false )
			{
				// "foo --"
			return true;
			}
		}
	if( valueListSize == 2 && token.charAt( 1 ) == 'U' )
		{
		return true;
		}
	}
else if( token.length() == 3 )
	{
	if( token.equals( "s&s" ) || token.equals( "sos" ) )
		{
		final String firstString = ( String )valueList.get( 0 );
		final String secondString = ( String )valueList.get( 2 );
		boolean firstStringHasOpenQuote;
		boolean secondStringHasCloseQuote;
		char c1open = 'a';
		char c2open = 'b';
		char c1close = 'c';
		char c2close = 'd';
		if( firstString.length() == 1 )
			{
			c1close = firstString.charAt( 0 );
			firstStringHasOpenQuote = false;
			}
		else
			{
			c1open = firstString.charAt( 0 );
			c1close = firstString.charAt( firstString.length() - 1 );
			if( c1open == '"' || c1open == '\'' )
				{
				firstStringHasOpenQuote = true;
				}
			else
				{
				firstStringHasOpenQuote = false;
				}
			}
		
		if( secondString.length() == 1 )
			{
			c2open =  secondString.charAt( 0 );
			secondStringHasCloseQuote = false;
			}
		else
			{
			c2open = secondString.charAt( 0 );
			c2close = secondString.charAt( secondString.length() - 1 );
			if( c2close == '"' || c2close == '\'' )
				{
				secondStringHasCloseQuote = true;
				}
			else
				{
				secondStringHasCloseQuote = false;
				}
			}
		
		if( firstStringHasOpenQuote == false
		 && secondStringHasCloseQuote== false
		 && c1close == c2open 
		  )
			{
				//not false positive
			return false;
			}
		else
			{
				//false positive
			return true;
			}
		}
	else if( token.equals( "s&n" )
	      || token.equals( "n&1" )
	      || token.equals( "1&1" )
	      || token.equals( "1&v" )
	      || token.equals( "1&s" )
	       )
		{
		if( valueListSize == 3 )
			{
			return true;
			}
		}
	else if( token.charAt( 1 ) == 'k' )
		{
		if( !( ( String )valueList.get( 1 ) ).toUpperCase().startsWith( "INTO" ) )
			{
			return true;
			}
		}
	}

return false;
}
//--------------------------------------------------------------------------------
public static boolean isWhiteSpaceChar( char tc )
{
if( tc == '\u0009'
 || tc == '\r'
 || tc == '\n'
 || tc == '\u000b'
 || tc == '\u000c'
 || tc == '\u0000'
 || tc == '\u00a0'
 || tc == ' ' )
	{
return true;
	}
else
	{
	return false;
	}
}
//------------------------------------------------------------------------------------------
public static String getMatch( String patternStr, String target )
{
Pattern pattern = Pattern.compile( patternStr, Pattern.DOTALL );
Matcher matcher = pattern.matcher( target );
if( matcher.find() )
	{
	if( matcher.groupCount() > 0 )
		{
		return matcher.group( 1 );
		}
	else
		{
		return target.substring( matcher.start(), matcher.end() );
		}
	}
else
	{
	return "";
	}
}
//------------------------------------------------------------------------------------------
public static String getMatchIgnoreCase( String patternStr, String target )
{
Pattern pattern = Pattern.compile( patternStr, Pattern.DOTALL | Pattern.CASE_INSENSITIVE );
Matcher matcher = pattern.matcher( target );
if( matcher.find() )
	{
	if( matcher.groupCount() > 0 )
		{
		return matcher.group( 1 );
		}
	else
		{
		return target.substring( matcher.start(), matcher.end() );
		}
	}
else
	{
	return "";
	}
}
//--------------------------------------------------------------------------------
public static boolean isArithmeticOperator( final String s )
{
return arithmeticOperatorSet.contains( s );
}
//--------------------------------------------------------------------------------
public static boolean isUnaryOperator( final String s )
{

if( s.length() > 3 )
	{
	return false;
	}
else
	{
	return unaryOperatorSet.contains( s.toUpperCase() );
	}
}
//--------------------------------------------------------------------------------
public static String fold( List valueList, final String token )
{
String lastToken = token;
while( true )
	{
	String foldedToken = foldImpl( valueList, lastToken );
	if( foldedToken.equals( lastToken ) )
		{
		break;
		}
	else
		{
		lastToken = foldedToken;
		}
	}

if( lastToken.length() > LIBINJECTION_SQLI_MAX_TOKENS )
	{
	lastToken =  lastToken.substring( 0, LIBINJECTION_SQLI_MAX_TOKENS );
	}

	//do some work after folding

	//Check for magic PHP backquote comment
if( lastToken.length() > 1 )
	{
	int lastIndex = lastToken.length() - 1;
	if( lastToken.charAt( lastIndex ) == 'n'
	 && valueList.get( lastIndex ).equals( "`" )
	  )
		{
		lastToken = lastToken.substring( 0, lastIndex ) + "c";
		}
	}

return lastToken;
}
//--------------------------------------------------------------------------------
public static String foldImpl( List valueList, String token )
{
List foldedValueList = new ArrayList();
StringBuffer foldedTokenBuf = new StringBuffer();
loop:
for( int i = 0; i < token.length(); ++i )
	{
	char currentToken = token.charAt( i );
	final String currentValue = ( String )valueList.get( i );
	String nextValue = "dummy";
	boolean hasNext = false;
	char nextToken = ' ';
	if( token.length() > i + 1 )
		{
		hasNext = true;
		nextToken = token.charAt( i + 1 );
		nextValue = ( String )valueList.get( i + 1 );
		}
	
	if( foldedValueList.size() == 0 )
		{
			//initial unary operator
		if( currentToken == 'o' )
			{
			if( isUnaryOperator( currentValue ) )
				{
				continue;
				}
			}
		else if( currentToken == '(' )
			{
			continue;
			}
		else if( currentToken == 'c' )
			{
			continue;
			}
		else if( currentToken == 't' )
			{
			continue;
			}
		}
	
	if( currentToken == '}' )
		{
		continue;
		}
		
	if( currentToken == 'c' && hasNext )
		{
		continue;
		}
	else if( hasNext
		&& ( currentToken == '&' || currentToken == 'o' )
		&& ( isUnaryOperator( nextValue ) || nextToken == 't' )
		)
		{
		foldedTokenBuf.append( currentToken );
		foldedValueList.add( currentValue );
		++i; //skip next unary operator or 't'
		continue;//1
		}
	
	if( hasNext )
		{
			//keyword merge
		final String _key = currentValue.toUpperCase();
		final List _keywordList = ( List )keywordMergeMap.get( _key );
		if( _keywordList != null )
			{
			for( int k = 0; k < _keywordList.size(); ++k )
				{
				final List eachList = ( List )_keywordList.get( k );
				final int _eachListSize = eachList.size();
				if( valueList.size() >= ( i + _eachListSize ) )
					{
					if( valueList.subList( i, i + _eachListSize ).toString().toUpperCase().equals( eachList.toString() ) )
						{
						final String _found = listToString( valueList.subList( i, i + _eachListSize ) );
						foldedValueList.add( _found );
						foldedTokenBuf.append( map.get( _found.toUpperCase() ) );
						i += _eachListSize - 1;
						continue loop;
						}
					}
				}
			}
		if( currentToken == '(' )
			{
			if( isUnaryOperator( nextValue ) )
				{
				++i; //skip operator
				foldedTokenBuf.append( currentToken );	// (
				foldedValueList.add( currentValue );		// (
				continue;	
				}
			else if( nextToken == '(' )
				{
				++i; //skip next (
				foldedTokenBuf.append( currentToken );	// (
				foldedValueList.add( currentValue );	// (
				continue;			
				}
			}
		
		if( currentToken == ')' )
			{
			if( nextToken == ')' )
				{
				++i; //skip next (
				foldedTokenBuf.append( currentToken );	// )
				foldedValueList.add( currentValue );	// )
				continue;							
				}
			}
		
		if( ( currentToken == 'n' || currentToken == 'v' )
		 && nextToken == '('
		  )
			{
			if( keywordOrFunctionSet.contains( currentValue.toUpperCase() ) )
				{
				foldedValueList.add( currentValue );
				foldedTokenBuf.append( 'f' );
				continue;
				}
			}
		if( currentToken == 't' 
		 && ( nextToken == 'n'
		   || nextToken == '1'
		   || nextToken == 't'
		   || nextToken == '('
		   || nextToken == 'f'
		   || nextToken == 'v'
		   || nextToken == 's'
		    )
		  )
			{
			continue;
			}
		
		if( currentToken == 'k'
		 && ( currentValue.toUpperCase().equals( "IN" ) || currentValue.toUpperCase().equals( "NOT IN" ) )
		  )
			{
			if( nextToken == '(' )
				{
				foldedValueList.add( currentValue );
				foldedTokenBuf.append( 'o' );
				continue;			
				}
			else
				{
				foldedValueList.add( currentValue );
				foldedTokenBuf.append( 'n' );
				continue;				
				}
			}
		
		if( currentToken == ';' && nextToken == ';' )
			{
			continue;
			}
		
		if( currentToken == ';' && nextValue.equalsIgnoreCase( "IF" ) )
			{
			foldedValueList.add( currentValue );
			foldedTokenBuf.append( ';' );
			foldedValueList.add( nextValue );
			foldedTokenBuf.append( 'T' );
			i += 1;
			continue;			
			}
		
		if( currentToken == '\\' )
			{
			if( isArithmeticOperator( nextValue ) )
				{
				foldedValueList.add( currentValue );
				foldedTokenBuf.append( '1' );	// \\ to number
				continue;
				}
			else
				{
				continue;
				}
			}
		
		if( currentToken == 'A' && nextToken == 'n' )
			{
			if( nextValue.indexOf( "_" ) > -1 )
				{
				foldedValueList.add( currentValue );
				foldedTokenBuf.append( currentToken );
				foldedValueList.add( nextValue );
				foldedTokenBuf.append( 't' ); // n to t
				i += 1;
				continue;
				}
			}
		
		if( currentToken == 'o'
		 && ( currentValue.equalsIgnoreCase( "LIKE" ) || currentValue.equalsIgnoreCase( "NOT LIKE" ) )
		 && nextToken == '('
		  )
			{
			foldedValueList.add( currentValue );
			foldedTokenBuf.append( 'f' );
			continue;
			}
		
		if( currentToken == '{' && nextToken == 'n' && nextValue.startsWith( "`" ) )
			{
			foldedValueList.add( currentValue );
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( nextValue );
			foldedTokenBuf.append( "X" );
			break;
			}
		
		if( ( currentToken == 'o' || currentToken == '&' )
		 && ( nextToken == 't' || isUnaryOperator( nextValue ) )
		  )
			{
			foldedValueList.add( currentValue );
			foldedTokenBuf.append( currentToken );
			i += 1;
			break;
			}
		}
	
		//three tokens
	if( token.length() > i + 2 )
		{
		char thirdToken = token.charAt( i + 2 );
		String thirdValue = ( String )valueList.get( i + 2 );
		if( currentToken == '1' && nextToken == 'o' && thirdToken == '1' )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 2;
			continue;
			}
		if( ( currentToken == 'n' || currentToken == '1' )
		      && nextToken == 'o'
		      && ( thirdToken == '1' || thirdToken == 'n' )
		       )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 2;
			continue;
			}
		if( currentToken == '&' && thirdToken == '&' )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 2;
			continue;
			}
		if( currentToken == 'n' && nextToken == '.' && thirdToken == 'n' )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 2;
			continue;
			}
		if( isUnaryOperator( currentValue ) && nextToken == 'n' && isUnaryOperator( thirdValue ) )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 2;
			continue;
			}
		if( ( currentToken == 'k' || currentToken == 'E' || currentToken == 'B' )
		      && isUnaryOperator( nextValue )
		      && ( thirdToken == '1' || thirdToken == 'n' || thirdToken == 'v' || thirdToken == 's' || thirdToken == 'f' )
		       )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 1;
			continue;
			}
		if( ( currentToken == 'n' || currentToken == '1' || currentToken == 'v' || currentToken == 's' )
		      && nextToken == 'o'
		      && nextValue.equals( "::" )
		      && thirdToken == 't'
		       )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 2;
			continue;
			}
		if( ( currentToken == 'n' || currentToken == '1' || currentToken == 's' || currentToken == 'v' )
		      && nextToken == ','
		      && ( thirdToken == '1' || thirdToken == 'n' || thirdToken == 's' || thirdToken == 'v' )
		       )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 2;
			continue;
			}
		if( ( currentToken == 'E' || currentToken == 'B' || currentToken == ',' )
		      && isUnaryOperator( nextValue )
		      && thirdToken == '('
		       )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 1;
			continue;		
			}
		
		if( currentToken == ','
		      && isUnaryOperator( nextValue )
		      && ( thirdToken == '1' || thirdToken == 'n' || thirdToken == 'v' || thirdToken == 's' )
		       )
			{
			i += 2;
			continue;			
			}
		if( currentToken == ','
		      && isUnaryOperator( nextValue )
		      && ( thirdToken == 'f' )
		       )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 1;
			continue;			
			}
		
		if( currentToken == 'v'
		 && nextToken == 'o'
		 && ( thirdToken == 'v' || thirdToken == '1' || thirdToken == 'n' )
		  )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 2;
			continue;			
			}
		
		if( currentToken == '{' && nextToken == 'n' )
			{
			i += 1;
			continue;
			}
	
		if( currentToken == 'E' && nextToken == '.' && thirdToken == 'n' )
			{
			foldedTokenBuf.append( currentToken );
			foldedValueList.add( currentValue );
			i += 1;
			continue;			
			}
/*
           
                    */
		}
	
		//ss -> s
	if( currentToken == 's' )
		{
		foldedTokenBuf.append( 's' );
		foldedValueList.add( currentValue );
		boolean found;
		while( true )
			{
			found = false;
			if( token.length() > i + 1 )
				{
				if( token.charAt( i + 1 ) == 's' )
					{
					++i;
					found = true;
					}
				}
			if( !found )
				{
				break;
				}
			}
		continue;
		}

	foldedTokenBuf.append( currentToken );
	foldedValueList.add( currentValue );
	}

	//some special cases for 5 tokens
if( foldedValueList.size() >= LIBINJECTION_SQLI_MAX_TOKENS )
	{
	String foldedToken = foldedTokenBuf.toString();
	if( ( foldedToken.startsWith( "1o(1)" ) || foldedToken.startsWith( "1,(1)" ) )
	 || ( foldedToken.startsWith( "no(n)" ) || foldedToken.startsWith( "no(1)" ) )
	 || ( foldedToken.startsWith( "1),(1" ) )
	  )
		{
		final String _tmpFoldedToken = foldedToken.substring( 5 );
		final List _tmpValueList = new ArrayList();
		_tmpValueList.addAll( foldedValueList.subList( 5, foldedValueList.size() ) );
		
		foldedTokenBuf = new StringBuffer();
		foldedTokenBuf.append( foldedToken.charAt( 0 ) );
		foldedTokenBuf.append( _tmpFoldedToken );
		
		String firstNumber = ( String )foldedValueList.get( 0 );
		foldedValueList.clear();
		foldedValueList.add( firstNumber );
		foldedValueList.addAll( _tmpValueList );		
		}
	}	

valueList.clear();
valueList.addAll( foldedValueList );

return foldedTokenBuf.toString();
}

//--------------------------------------------------------------------------------
}

class MListSizeComparator
implements Comparator, java.io.Serializable
{
private static final long	serialVersionUID	= 5795625486351629911L;
// --------------------------------------------------------------------------------
public int compare( Object o1, Object o2 )
{
List l1 = ( List )o1;
List l2 = ( List )o2;

if( l1.size() > l2.size() )
	{
	return -1;
	}
else if( l1.size() == l2.size() )
	{
	return 0;
	}
else
	{
	return 1;
	}
}

// --------------------------------------------------------------------------------
}
