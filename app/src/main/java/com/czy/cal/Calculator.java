package com.czy.cal;

import java.util.*;
/**
 * Created by CZY on 2019/4/8.
 */

public class Calculator {
    //全过程使用double类型
    public static final int DEC=10;//十进制
    public static final int HEX=16;//十六进制
    public static final int BIN=2;//二进制

    private String expression;//表达式

    private int mode=DEC;//获取数字的模式

    private int pointer=0;//目前的指针，指向expression的某一字符

    private int false_flag=0;//关于表达式是否错误的flag。目前只使用0和1

    Vector<String> expression_out=new Vector<String>();//输出的后缀表达式。double一律转化为string
    Stack<String> sign_stack=new Stack<String>();//对于运算符使用的栈

    private HashMap<String,Integer> privilige_in=new HashMap<String,Integer>();//内部优先级
    private HashMap<String,Integer> privilige_out=new HashMap<String,Integer>();//外部优先级
    //优先级越高越大。
    //加减乘除 左移右移 and or xor 两种括号
    //not不是双目运算符，不考虑。

    Calculator()
    {
        privilige_in.put("(",0);
        privilige_in.put(")",13);

        privilige_out.put(")",0);
        privilige_out.put("(",13);

        privilige_in.put("*",12);
        privilige_in.put("/",12);

        privilige_in.put("+",10);
        privilige_in.put("-",10);

        privilige_in.put("<<",8);
        privilige_in.put(">>",8);
        privilige_in.put("&",6);
        privilige_in.put("^",4);
        privilige_in.put("|",2);

        privilige_out.put("*",11);
        privilige_out.put("/",11);

        privilige_out.put("+",9);
        privilige_out.put("-",9);

        privilige_out.put("<<",7);
        privilige_out.put(">>",7);
        privilige_out.put("&",5);
        privilige_out.put("^",3);
        privilige_out.put("|",1);

        privilige_in.put("#",-1);
        privilige_out.put("#",-1);
        //特殊字符表示栈底部



    }

    public double getAnswer(String exp)
    {
        //给外部调用的接口
        if(exp.equals(""))
            return 0;
        pointer=0;
        false_flag=0;
        expression_out.clear();
        sign_stack.clear();
        //初始化工作

        expression=exp;
        expression+="#";//结束符号
        if(expression.charAt(0)=='+'||expression.charAt(0)=='-')
            expression="0"+expression;

        bracketMatch();
        checkCorrectness();
        return calculate();
    }
    private void bracketMatch()
    {
        //检测括号是否正确
        int len=expression.length();
        int a=0;
        for(int i=0;i<len;i++)
        {
            if(expression.charAt(i)=='(')
                a++;
            if(expression.charAt(i)==')')
                a--;
            if(a<0)
            {
                CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_BRACKET);
                throw e;
            }
        }

        if(a!=0)
        {
            CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_BRACKET);
            throw e;
        }
    }

    private void checkCorrectness()
    {
        //首部不能是运算符
        //不能连续出现两个运算符
        //不能出现()连续

        //运算符的前方不能是（
        //）的前方不能是运算符
        //（）不被视为运算符

        //( 没有要求
        //运算符 前方不能是运算符和(
        //)前方不能是运算符和（
        //数字的前方不能是)
        String a=getNext();
        int signBefore=0;
        int LbracketBefore=0;
        int RbracketBefore=0;
        if(!isNumber(a)&&!a.equals("+")&&!a.equals("-")&&!a.equals("("))
        {
            //首部出现运算符
            CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_GRAMMAR);
            throw e;
        }


        while(!a.equals("#"))
        {
            if(isNumber(a))
            {
                if(RbracketBefore==1)
                {
                    CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_GRAMMAR);
                    throw e;
                }
                signBefore=0;
                LbracketBefore=0;
                RbracketBefore=0;
                a = getNext();
            }
            else
            {
                if(a.equals("("))
                {
                    LbracketBefore=1;
                    signBefore=0;
                    RbracketBefore=0;
                    a=getNext();
                }
                else if(a.equals(")"))
                {
                    if(LbracketBefore==1||signBefore==1)
                    {
                        CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_GRAMMAR);
                        throw e;
                    }
                    RbracketBefore=1;
                    signBefore=0;
                    LbracketBefore=0;
                    a=getNext();
                }
                else
                {
                    //普通运算符
                    if(signBefore==1||LbracketBefore==1)
                    {
                        CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_GRAMMAR);
                        throw e;
                    }

                    signBefore=1;
                    LbracketBefore=0;
                    RbracketBefore=1;
                    a=getNext();
                }
            }
        }


        pointer=0;
    }

    private double calculate()
    {
        //内部调用的计算接口
        //先生成后缀表达式，然后进行计算
        //生成后缀表达式
        generate();

        Stack<String> s=new Stack<String>();
        Vector<String> exp_pre=new Vector<String>();
        for(int i=0;i<expression_out.size();i++)
        {
            if(isNumber(expression_out.get(i)))
            {

                double num = getNum(expression_out.get(i));
                exp_pre.add(String.valueOf(num));

            }
            else
            {
                exp_pre.add(expression_out.get(i));
            }
        }

        int len=exp_pre.size();
        for(int i=0;i<len;i++)
        {
            if(isNumber(exp_pre.get(i)))
                s.push(exp_pre.get(i));
            else
            {
                String op1,op2;
                if(s.size()<2)
                {
                    CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_GRAMMAR);
                    throw e;
                }
                op2=s.pop();
                op1=s.pop();
                if(!isNumber(op2)||!isNumber(op1))
                {
                    CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_GRAMMAR);
                    throw e;
                }
                if(exp_pre.get(i).equals("+"))
                {
                    String temp=String.valueOf(getNum_dec(op1)+getNum_dec(op2));
                    s.push(temp);
                }
                else if(exp_pre.get(i).equals("-"))
                {
                    String temp=String.valueOf(getNum_dec(op1)-getNum_dec(op2));
                    s.push(temp);
                }
                else if(exp_pre.get(i).equals("*"))
                {
                    String temp=String.valueOf(getNum_dec(op1)*getNum_dec(op2));
                    s.push(temp);
                }
                else if(exp_pre.get(i).equals("/"))
                {
                    if(Double.valueOf(op2)==0.0)
                    {
                        CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_DIV_BY_ZERO);
                        throw e;
                    }

                    String temp=String.valueOf(getNum_dec(op1)/getNum_dec(op2));
                    s.push(temp);
                }
                else if(exp_pre.get(i).equals("<<"))
                {
                    Double op1_t,op2_t;
                    op1_t=getNum_dec(op1);
                    op2_t=getNum_dec(op2);
                    int op1_i,op2_i;
                    op1_i=op1_t.intValue();
                    op2_i=op2_t.intValue();
                    String temp=String.valueOf(op1_i<<op2_i);
                    s.push(temp);
                }
                else if(exp_pre.get(i).equals(">>"))
                {
                    Double op1_t,op2_t;
                    op1_t=getNum_dec(op1);
                    op2_t=getNum_dec(op2);
                    int op1_i,op2_i;
                    op1_i=op1_t.intValue();
                    op2_i=op2_t.intValue();
                    String temp=String.valueOf(op1_i>>op2_i);
                    s.push(temp);
                }
                else if(exp_pre.get(i).equals("&"))
                {
                    Double op1_t,op2_t;
                    op1_t=getNum_dec(op1);
                    op2_t=getNum_dec(op2);
                    int op1_i,op2_i;
                    op1_i=op1_t.intValue();
                    op2_i=op2_t.intValue();
                    String temp=String.valueOf(op1_i&op2_i);
                    s.push(temp);
                }
                else if(exp_pre.get(i).equals("|"))
                {
                    Double op1_t,op2_t;
                    op1_t=getNum_dec(op1);
                    op2_t=getNum_dec(op2);
                    int op1_i,op2_i;
                    op1_i=op1_t.intValue();
                    op2_i=op2_t.intValue();
                    String temp=String.valueOf(op1_i|op2_i);
                    s.push(temp);
                }
                else if(exp_pre.get(i).equals("^"))
                {
                    Double op1_t,op2_t;
                    op1_t=getNum_dec(op1);
                    op2_t=getNum_dec(op2);
                    int op1_i,op2_i;
                    op1_i=op1_t.intValue();
                    op2_i=op2_t.intValue();
                    String temp=String.valueOf(op1_i^op2_i);
                    s.push(temp);
                }

            }
        }

        return Double.valueOf(s.pop());
    }

    private boolean isNumber()
    {
        return  ((expression.charAt(pointer)<='9'&&expression.charAt(pointer)>='0')||(expression.charAt(pointer)<='f'&&expression.charAt(pointer)>='a')||(expression.charAt(pointer)<='F'&&expression.charAt(pointer)>='A')||expression.charAt(pointer)=='.');
    }

    public boolean isNumber(String ch)
    {
        if((ch.charAt(0)<='9'&&ch.charAt(0)>='0')||(ch.charAt(0)<='f'&&ch.charAt(0)>='a')||(ch.charAt(0)<='F'&&ch.charAt(0)>='A')||ch.charAt(0)=='.')
            return true;
       else if(ch.length()>=2&&(ch.charAt(0)=='-'||ch.charAt(0)=='+'))
        {
            if((ch.charAt(1)<='9'&&ch.charAt(1)>='0')||(ch.charAt(1)<='f'&&ch.charAt(1)>='a')||(ch.charAt(1)<='F'&&ch.charAt(1)>='A'))
                return true;
        }

        return false;
    }

    private void generate()
     {
        //根据expression生成后缀表达式
        pointer=0;
        sign_stack.clear();
        sign_stack.push("#");//作为栈底

        int len=expression.length();
        expression_out.clear();
        String ch="#";
        String ch1,op;
        ch=getNext();
        while(!sign_stack.isEmpty())
        {
            if(isNumber(ch))
            {
                expression_out.add(ch);
                ch=getNext();
            }
            else
            {

                while(!sign_stack.isEmpty()) {
                    ch1=sign_stack.lastElement();

                    if (privilige_in.get(ch1) < privilige_out.get(ch))
                    {
                        sign_stack.push(ch);
                        ch = getNext();
                        break;
                    }
                    else if (privilige_in.get(ch1) > privilige_out.get(ch))
                    {
                        expression_out.add(sign_stack.pop());
                    }
                    else
                    {
                        op = sign_stack.pop();
                        if (op.equals("("))
                            ch = getNext();
                    }
                }
            }
        }

        /*
        while(pointer<len)
        {
            //如果是一个数字
            if((expression.charAt(pointer)<='9'&&expression.charAt(pointer)>='0')||(expression.charAt(pointer)<='f'&&expression.charAt(pointer)>='a')||(expression.charAt(pointer)<='F'&&expression.charAt(pointer)>='A')||expression.charAt(pointer)=='.')
            {
                expression_out.add(getNum());
            }
            else    //如果是运算符
            {
                //有两个运算符是有两个符号的，特殊处理
                //在这个阶段不去处理错误表达式，在后边进行处理过程
                int start=pointer;
                int end=pointer;
                if(expression.charAt(pointer)=='<'||expression.charAt(pointer)=='<')
                {
                    end=start+2;
                    pointer+=2;
                }
                else
                {
                    end=pointer+1;
                    pointer++;
                }
                //完成了对于pointer的处理，以下都不能动pointer

                String sign_pre=expression.substring(start,end);

                if(privilige_in.get(sign_stack.firstElement())<privilige_out.get(sign_pre))
                {
                    sign_stack.push(sign_pre);

                }
                else if(privilige_in.get(sign_stack.firstElement())>privilige_out.get(sign_pre))
                {
                    expression_out.add(sign_stack.pop());
                }
                else
                {
                   sign_stack.pop();
                }

            }
        }

        */


    }

    private String getNext()
    {
        //获取下一个元素，同时调整pointer的位置
        if(pointer>=expression.length())
        {
            CalculatorException e=new CalculatorException(CalculatorException.CAL_EXP_GRAMMAR);
            throw e;
        }
        if(isNumber())
            return getNum();
        else
        {
            if(expression.charAt(pointer)=='<'||expression.charAt(pointer)=='>')
            {
                pointer+=2;
                return expression.substring(pointer-2,pointer);
            }
            else
            {
                pointer+=1;
                return expression.substring(pointer-1,pointer);
            }
        }
    }
    private double getNum(String e)
    {
        //获取一个数字，十进制，十六进制或二进制
        //用于计算过程中而不是中缀转后缀的过程中

        double before=0;
        double after=0;
        double mode_double=mode;
        double ratio=1.0/mode_double;
        int length=e.length();


        int i;
        for(i=0;i<length;i++)
        {
            if(e.charAt(i)=='.')
            {
                i++;//跳离小数点
                break;
            }
            before*=mode_double;
            if(e.charAt(i)<='9'&&e.charAt(i)>='0')
                before+=e.charAt(i)-'0';
            else if(e.charAt(i)<='F'&&e.charAt(i)>='A')
                before+=e.charAt(i)-'A'+10;
            else if(e.charAt(i)<='f'&&e.charAt(i)>='a')
                before+=e.charAt(i)-'a'+10;

        }

        for(;i<length;i++)
        {
            if(e.charAt(i)<='9'&&e.charAt(i)>='0')
            {
                after+=(e.charAt(i)-'0')*ratio;
            }
            else if(e.charAt(i)<='F'&&e.charAt(i)>='A')
            {
                after+=(e.charAt(i)-'A'+10)*ratio;
            }
            else if(e.charAt(i)<='f'&&e.charAt(i)>='a')
            {
                after+=(e.charAt(i)-'a'+10)*ratio;
            }
            else if(e.charAt(i)=='.')
            {
                CalculatorException b=new CalculatorException(CalculatorException.CAL_EXP_GRAMMAR);
                throw b;
            }

            ratio/=mode_double;

        }

        return before+after;
    }

    private double getNum_dec(String e)
    {
       return Double.valueOf(e);
    }

    private String getNum()
    {
        //从pointer开始获取一个数字的字符串。
        //会修改pointer的值
        //最终停下来的地方应该是一个运算符或者字符串结束符

        int begin=pointer;
        if(mode==BIN)
        {
            while(expression.charAt(pointer)=='0'||expression.charAt(pointer)=='1'||expression.charAt(pointer)=='.')
                pointer++;
            return expression.substring(begin,pointer);
        }
        else if(mode==HEX)
        {
            while((expression.charAt(pointer)<='9'&&expression.charAt(pointer)>='0')||(expression.charAt(pointer)<='f'&&expression.charAt(pointer)>='a')||(expression.charAt(pointer)<='F'&&expression.charAt(pointer)>='A')||expression.charAt(pointer)=='.')
                pointer++;
            return expression.substring(begin,pointer);
        }
        else if(mode==DEC)
        {
            while((expression.charAt(pointer)<='9'&&expression.charAt(pointer)>='0')||expression.charAt(pointer)=='.')
                pointer++;
            return expression.substring(begin,pointer);
        }
        false_flag=1;
        return expression.substring(0,1);//对于错误情况
    }

    public void setMode(int Mode)
    {
        mode=Mode;//设置获取数字的模式
    }

    public void clear()
    {
        expression="";
        pointer=0;
        false_flag=0;
        expression_out.clear();
        sign_stack.clear();
    }

    public int getMode(){return mode;}


}
