/*										Project 2

Professor: Roger Eggen
Class:Compilers
Author: Gavin Murray
N#:N01062011
Description:Parser 



*/
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


public class proj2{
	

		
	public static void main(String[] args) {
String lexeme="";
		String S="";
		int n=0;
		int m=0;
		int a=0;
		int max=0;
		String token="";
		String A="+-{}[](),;";
		String B="<=>";
		String[] C;

		char x=' ';
		char y=' ';
		boolean comment=false;

		try{
			File file = new File(args[0]);
			Scanner input= new Scanner(file);
			while(input.hasNext()){
				S=input.nextLine();
				m++;
				n=S.length();
				if(n>max)
					max=n;
			}

			input.close();
			input= new Scanner(file);

			C=new String[max*m];
			for(int i=0;i<max*m;i++)
				C[i]="0";
			while(input.hasNext()){
				S=input.nextLine();
				n=S.length();
			//	System.out.println("Input:"+S);
			//	System.out.println("Output:");
				for(int i=0;i<n;i++){
					lexeme="";
					token="";
					x=S.charAt(i);
					if((i+1)<n){
						y=S.charAt(i+1);
					}
					else{
						y=' ';
					}

					if(x==' '||x=='\t'){
						continue;
					}
					else if(A.indexOf(x)>-1){
						lexeme+=x;
						token="Symbol";
					}
					else if(B.indexOf(x)>-1){
						lexeme+=x;
						token="Symbol";
						if(y=='='){
							lexeme+=y;
							i++; //Prepare to skip next character.
						}
					}
					else if(x=='!'&&y=='='){
						lexeme+=x;
						lexeme+=y;
						token="Symbol";
						i++;
					}
					else if(x=='/'){
						lexeme+=x;
						token="Symbol";
						if(y=='*'||y=='/'){
							lexeme+=y;
							i++;
						}
					}
					else if(x=='*'){
						lexeme+=x;
						token="Symbol";
						if(y=='/'&&comment){
							lexeme+=y;
							i++;
						}
					}
					else if(isNumber(x)){
						lexeme+=x;
						token="Number";
						while((i+1)<n&&(isNumber(S.charAt(i+1)))){
							lexeme+=(S.charAt(++i));
						}
					}
					else if(isLetter(x)){
						lexeme+=x;
						while((i+1)<n&&isLetter(S.charAt(i+1))){
							lexeme+=(S.charAt(++i));
						}
						switch(lexeme){
							case "if":
							case "int":
							case "else":
							case "while":
							case "void":
							case "return":
								token="Keyword";
								break;
							default:
								token="ID";
						}

					}
					else{
						token="Error";
						lexeme+=x;
					}
					if(lexeme.equals("/*")){
						comment=true;
					}
					else if(lexeme.equals("//")){
						break;
					}

					if(!comment){
						if(token.equals("Symbol")||token.equals("Keyword")){
			//				System.out.println(lexeme);
							C[a++]=lexeme;
						}
						else{
			//				System.out.println(token);
							C[a++]=token;
						}
					}
					else if(lexeme.equals("*/")){
						comment=false;
					}



				}
			}
			input.close();
			String[] D= new String[a];
			a=0;
			while(!C[a].equals("0")){
				D[a]=C[a];
				a++;
			}
			Parse parse = new Parse(a,D); //The array needs to be shortened.
			parse.disp();
			if(parse.program()&&parse.X.equals("$"))
				System.out.println("ACCEPT");
			else 
				System.out.println("REJECT");
		}
		catch(FileNotFoundException e){
			System.out.println("Void file");
		}
	
	}



static boolean isLetter(char ch){
	if(ch>=65&&ch<=90||(ch>=97&&ch<=122))
		return true;
	
	return false;
}

static boolean isNumber(char ch){
	if(ch>=48&&ch<=57){
		return true;
	}

	return false;
}


	
	
}

class Parse{
	String[] S;//Set of tokens to be parsed
	int i=0;
	String X=""; //Current token

	Parse(int n, String[] A){
		S=new String[n];
		S=A;
		X=S[i];
	}

	void disp(){
		System.out.println("Contents of S:");
		for(int i=0;i<S.length;i++){
			System.out.println(S[i]);
		}
	}

	boolean inc(String ... token){
	  if(token.length==0||(token.length>0&&X.equals(token[0]))){	
			i++;
		 if(i<S.length){
		  X=S[i];
		  if(X.equals(" ")||X.equals("\t")||X.equals("\n")||X.equals("")){
			inc();
		  }
		  X=X.trim();
		 }
		 else{
			 X="$";
		 }
		 if(X.equals("Error")){
			System.out.println("REJECT");
			System.exit(1);	
		 }
	  	 return true;
	  }
	  return false;
	}

	
	
	boolean program(){
		if(decList())
			return true;
		else
			return false;
	}

	boolean decList(){
		if(decl()&&decList2())
			return true;
		else
			return false;
	}

	boolean decList2(){
		if(decl()&&decList2())
			return true;
		else
			return true;
	
	}

	
	boolean fDecl(){
		if(inc("(")&&params()&&inc(")")&&compStmt())
			return true;
		else
			return false;
	}

	boolean params(){
		if(inc("void"))
			return true;
		else if(paramList())
			return true;
		else
			return false;
	}

	boolean paramList(){

		if(param()&&paramList2())
			return true;
		else
			return false;
	}

	boolean paramList2(){

		if(inc(",")&&param()&&paramList2())
			return true;
		else{
			return true;
		}
	}

	boolean param(){

		if(type()&&inc("ID")){
			if((inc("[")&&inc("]"))){
				return true;
			}
			else 
				return true;
		}
		else{
			return false;
		}
	}

	boolean decl(){
				if(type()){
			if(inc("ID")){
				if(fDecl())
					return true;
				else if(varDecl())
					return true;
				else
					return false;
			}
			else
				return false;
		}
		else
			return false;
	}

	boolean varDecl(){
		if(inc("[")){
			if(inc("Number")){
				if(inc("]")){
					if(inc(";"))
						return true;
					else
						return false;
				}
				else
					return false;
			}
			else
				return false;
		}
		else{
			if(inc(";"))
				return true;
			else
				return false;
		}
	}

	boolean type(){
		if(inc("int")||inc("void"))
			return true;
		else
			return false;
	}

	boolean compStmt(){
		if(inc("{")&&local()&&stmtList()&&inc("}")){
			return true;
		}
		else{
					return false;
		}
	}
	
	boolean local(){
		if(local2())
			return true;
		else
			return false;

	}

	boolean local2(){
				if(type()){
			if(inc("ID")){
				if(varDecl()&&local2())
					return true;
				else
					return false;
			}
			else return false;
		}
		else
			return true;

	}

	boolean stmtList(){//May need fixing
		stmtList2();
		return true;
	}


	boolean stmtList2(){

		if(stmt()&&stmtList2())
			return true;
		else{
			return true;
		}
	}
	

	boolean stmt(){
		if(exprStmt()||select()||i()||ret()||compStmt()){ 
			return true;
		}
		else{
			return false;
		}
	}

	boolean select(){
				if(inc("if")&&inc("(")&&expr()&&inc(")")&&stmt()&&U())
			return true;
		else
			return false;

	}

	boolean U(){
		if(inc("else")&&stmt())
			return true;
		else
			return true;
	}

	boolean i(){
		if(inc("while")&&inc("(")&&expr()&&inc(")")&&stmt()){
			return true;
		}
		else{
			return false;
		}
	}

	boolean exprStmt(){
		expr();
		if(inc(";"))
			return true;
		else{
			return false;
		}
	}



	boolean ret(){ 
		if(inc("return")){
			T(); //check for optional expresiion
			if(inc(";")){
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}

	boolean T(){
		if(expr())
			return true;
		else
			return true;
	}
	
	boolean expr(){

		
		if(inc("ID")){
			if(V())
				return true;
			else return false;
		}
		else if(simpExpr())
			return true;
		else
			return false;

	}

	boolean V(){
		if(inc("(")){
			if(args()){
				if(inc(")")){
					if(W())
						return true;
					else
						return false;
				}
				else
					return false;
			}
			else
				return false;
		}
		else if(inc("[")){
			if(expr()){
				if(inc("]")){
					if(X())
						return true;
					else
						return false;
				}
				else 
					return false;
			}
			else 
				return false;
		}
		else if(X())
			return true;
		else
			return false;
	}

	boolean X(){
		if(inc("=")){
			if(expr())
				return true;
			else
				return false;
		}
		else if(W()){
			return true;
		}
		else
			return false;
	}

	boolean W(){
		if(term2()&&simpExpr2())
			return true;
		else
			return false;
	}

	boolean simpExpr2(){
		if(addExpr2()&&Y())
			return true;
		else
			return false;
	}

	boolean Y(){	
		if(relOp()){
			if(addExpr())
				return true;
			else{
				return false;
			}
		}
		else
			return true;
	}


	boolean simpExpr(){//Good
		
		if(addExpr()){
			if(Y())
				return true;
			else{
				return false;
			}

		}
		else 
		return false;
	}

	boolean var(){

		
		if(inc("[")){
			if(expr()){
				if(inc("]"))
					return true;
				else 
					return false;
			}
			else
				return false;
		}
		else{
			return true;
		}
	}

	boolean relOp(){
		if(X.equals("<=")||X.equals("<")||X.equals(">")||X.equals(">=")||X.equals("==")||X.equals("!=")){
			inc();
			return true;
		}
		else{
			return false;
		}
	}

	boolean addExpr(){
		
		if(term()&&addExpr2()){
			return true;
		}
		else{
			return false;
		}
	}

	boolean addExpr2(){	
		if(addOp()){
			if(term()){ 
				if(addExpr2()){
					return true;
				}
			}
		}
		return true;
	}

	boolean addOp(){
		if(X.equals("+")||X.equals("-")){
			inc();	
			return true;
		}
		else{
			return false;
		}
	}

	boolean term(){//Good
			if(factor()&&term2()){
				return true;
		}
		else{
			return false;
		}
	}

	boolean term2(){
		if(mulOp()){
			if(factor()){//Factor will produce tokens that may need to be put back. Telling how many will be complicated. 
				if(term2()){
					return true;
				}
				else{
					return true;
				}
			}
			return true;
		}
		else{
			return true;
		}
	}

	boolean mulOp(){
		if(X.equals("*")||X.equals("/")){
			inc();
			return true;
		}
		else{
			return false;
		}

	}

	boolean factor(){ 
		if(X.equals("(")){
			inc();
			if(expr()){ 
				if(X.equals(")")){
					inc();
					return true;
				}
			}
			return false;
		} 
		else if(X.equals("Number")){ 
			inc();
			return true;
		}
		else if(inc("ID")){
			if(Z())
				return true;
			else
				return false;
		}
		else{
			return false;
		}
	}

	boolean Z(){
		if(call())
			return true;
		else if((var()))
			return true;
		else
			return false;
			
	}

	boolean call(){
			if(X.equals("(")){
				inc(); 
				args();
				if(X.equals(")")){
					inc();
					return true;
				}
			}
			return false;
	}

	boolean args(){
		argList();
		return true;
	}

	boolean argList(){
		if(expr()){
			if(argList2()){
				return true;
			}
		}
		return false;
	}

	boolean argList2(){
		
		if(X.equals(",")){
			inc();
			if(expr()){ 
				if(argList2()){
					return true;
				}
			}
		}
		return true;
	}






}

