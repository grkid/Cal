package com.czy.cal;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class MainActivity extends AppCompatActivity {

    public Calculator myCalculator=new Calculator();
    Button calculateButton;
    TextView answerText;
    TextView expressionText;

    Button buttonLShift,buttonD,buttonE,buttonF,buttonDel;
    Button buttonRShift,buttonA,buttonB,buttonC,buttonDiv;
    Button buttonAnd,button7,button8,button9,buttonMul;
    Button buttonOr,button4,button5,button6,buttonMinus;
    Button buttonNot,button1,button2,button3,buttonPlus;
    Button buttonXor,buttonClear,button0,buttonPoint;

    String expression;
    String answer;
    double answer_double=0;
    private int haveAnswer=0;//目前是否存在答案

    Vibrator vb;

    private Animation anim=null;

    private int anim_seq=0;

    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {


            if(anim_seq==0)
            {
                initAnimate(calculateButton);
                anim_seq=1;
            }
            else if(anim_seq==1)
            {
                initAnimate(buttonPoint);
                initAnimate(buttonPlus);
                anim_seq=2;
            }
            else if(anim_seq==2)
            {
                initAnimate(button0);
                initAnimate(button3);
                initAnimate(buttonMinus);
                anim_seq=3;
            }
            else if(anim_seq==3)
            {

                initAnimate(buttonClear);
                initAnimate(button2);
                initAnimate(button6);
                initAnimate(buttonMul);
                anim_seq=4;
            }
            else if(anim_seq==4)
            {
                initAnimate(buttonXor);
                initAnimate(button1);
                initAnimate(button5);
                initAnimate(button9);
                initAnimate(buttonDiv);

                anim_seq=5;
            }
            else if(anim_seq==5)
            {
                initAnimate(buttonDel);
                initAnimate(buttonC);
                initAnimate(button8);
                initAnimate(button4);
                initAnimate(buttonNot);
                anim_seq=6;
            }
            else if(anim_seq==6)
            {
                initAnimate(buttonOr);
                initAnimate(button7);
                initAnimate(buttonB);
                initAnimate(buttonF);


                anim_seq=7;
            }
            else if(anim_seq==7)
            {
                initAnimate(buttonE);
                initAnimate(buttonA);
                initAnimate(buttonAnd);

                anim_seq=8;
            }
            else if(anim_seq==8)
            {
                initAnimate(buttonD);
                initAnimate(buttonRShift);
                anim_seq=9;

            }
            else if(anim_seq==9)
            {
                initAnimate(buttonLShift);
                anim_seq=-1;
            }


            handler.postDelayed(this,35);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //先对calculator进行测试，最基础的要求是输入正确的算式要得出正确的答案。
        //exception什么的之后再搞

        //选择 只有十进制的时候会显示小数点，其他情况下一律只保留int类型的书数值
        //计算中还是使用double的
        init();
       setClicks();
       handler.postDelayed(runnable,35);


    }


    private void init()
    {
        vb=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        expression="";
        answer="";
        calculateButton=findViewById(R.id.calculateButton);
        answerText=findViewById(R.id.Answer);
        expressionText=findViewById(R.id.Expression);

        buttonLShift=findViewById(R.id.buttonLshift);
        buttonD=findViewById(R.id.buttonD);
        buttonE=findViewById(R.id.buttonE);
        buttonF=findViewById(R.id.buttonF);
        buttonDel=findViewById(R.id.buttonDel);

        buttonRShift=findViewById(R.id.buttonRshift);
        buttonA=findViewById(R.id.buttonA);
        buttonB=findViewById(R.id.buttonB);
        buttonC=findViewById(R.id.buttonC);
        buttonDiv=findViewById(R.id.buttonDiv);

        buttonAnd=findViewById(R.id.buttonAnd);
        button7=findViewById(R.id.button7);
        button8=findViewById(R.id.button8);
        button9=findViewById(R.id.button9);
        buttonMul=findViewById(R.id.buttonMul);

        buttonOr=findViewById(R.id.buttonOr);
        button4=findViewById(R.id.button4);
        button5=findViewById(R.id.button5);
        button6=findViewById(R.id.button6);
        buttonMinus=findViewById(R.id.buttonMinus);

        buttonNot=findViewById(R.id.buttonNot);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        buttonPlus=findViewById(R.id.buttonPlus);

        buttonXor=findViewById(R.id.buttonXor);
        buttonClear=findViewById(R.id.buttonClear);
        button0=findViewById(R.id.button0);
        buttonPoint=findViewById(R.id.buttonPoint);




    }

    private void initAnimate(Button e)
    {
        e.setVisibility(View.INVISIBLE);
        anim = AnimationUtils.loadAnimation(this, R.anim.anims);
        e.startAnimation(anim);
        e.setVisibility(View.VISIBLE);
    }

    //下面两个函数只管返回，不用处理其他事务（不用在意答案是否存在）
    String getBinString(double res)
    {

        long a=(long)res;
        //return String.valueOf(a);
        String return_val="";
        String sign="";
        if(a<0)
        {
            a=~a+1;//负数，取反加一
            sign="-";
        }

        //第一个余数 在最后一位
        while(a>0)
        {
            long temp=a%2;
            a/=2;
            return_val=String.valueOf(temp)+return_val;
        }

        //性能不好，但是暂时没问题（？
        return sign+return_val;

    }

    String getHexString(double res)
    {
        long a=(long)res;
        //return String.valueOf(a);
        String return_val="";
        String sign="";
        if(a<0)
        {
            a=~a+1;//负数，取反加一
            sign="-";
        }

        //第一个余数 在最后一位
        while(a>0)
        {
            long temp=a%16;
            a/=16;
            String c="";
            if(temp<10)
                c=String.valueOf(temp);
            else
            {
               if(temp==10)
                   c="A";
               else if(temp==11)
                   c="B";
               else if(temp==12)
                   c="C";
               else if(temp==13)
                   c="D";
               else if(temp==14)
                   c="E";
               else
                   c="F";

            }
            return_val=c+return_val;
        }

        //性能不好，但是暂时没问题（？
        return sign+return_val;
    }

    private void setAnswer(double res)
    {
        answer_double = res;
        haveAnswer=1;
        if(myCalculator.getMode()==Calculator.BIN)
        {
            answerText.setText(getBinString(res));
        }
        else if(myCalculator.getMode()==Calculator.HEX)
        {
            answerText.setText(getHexString(res));
        }
        else
        {
            answerText.setText(String.valueOf(res));
        }
    }


    private void press_equal()
    {
        try {
            double res = myCalculator.getAnswer(String.valueOf(expressionText.getText()));
            answer_double=res;
            setAnswer(res);
        }
        catch(CalculatorException e)
        {
            haveAnswer=0;
            answerText.setText(e.exp_info());
            answer_double=0;
        }

        vb.vibrate(20);
    }

    private void press_clear()
    {
        expression="";
        answer="";
        answer_double=0;
        expressionText.setText(expression);
        answerText.setText(answer);
        myCalculator.clear();
        vb.vibrate(40);
    }

    private void press_del()
    {
        if(expression.length()!=0)
        {
            if(expression.charAt(expression.length()-1)=='<'||expression.charAt(expression.length()-1)=='>')
                expression = expression.substring(0, expression.length() - 2);
            else
                expression = expression.substring(0, expression.length() - 1);
        }
        expressionText.setText(expression);
        vb.vibrate(40);
    }

    private void press_not()
    {
        if(haveAnswer==1) {
            long a = new Double(answer_double).longValue();
            a = ~a;
            answerText.setText(String.valueOf(a));
        }
    }

    private void setUnclickable(Button b)
    {
        if(!b.isClickable())
            return;
        b.setClickable(false);
        b.setTextColor(0xffc8c8c8);
        anim=AnimationUtils.loadAnimation(this,R.anim.scale_smaller);
        b.startAnimation(anim);

    }
    private void setClickable(Button b)
    {
        if(b.isClickable())
            return;
        b.setClickable(true);
        b.setTextColor(0xff000000);
        anim=AnimationUtils.loadAnimation(this,R.anim.scale_bigger);
        b.startAnimation(anim);
    }

    private void setClicks()
    {
        calculateButton=findViewById(R.id.calculateButton);
        answerText=findViewById(R.id.Answer);
        expressionText=findViewById(R.id.Expression);


        //等于
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               press_equal();
            }
        });

        //清空
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               press_clear();
            }
        });

        //删除（一个字符）
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                press_del();
            }
        });

        buttonNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                press_not();
            }
        });



        //通用的listener


        View.OnClickListener UniversalClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button temp=(Button)view;
                if(temp.isClickable()) {
                    String name = String.valueOf(temp.getText());
                    if (name.equals("÷"))
                        name = "/";
                    if (name.equals("×"))
                        name = "*";
                    expression += name;
                    expressionText.setText(expression);
                }
            }
        };



        View.OnTouchListener UniversalTouchListener=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Button b=(Button)view;
                if(b.isClickable()) {
                    if (motionEvent.getAction() == ACTION_DOWN) {
                        b.setBackgroundColor(0xffe6e6e6);
                        //b.performClick();

                    }
                    if (motionEvent.getAction() == ACTION_UP) {
                        //0xff4081
                        String name = String.valueOf(b.getText());
                        if (myCalculator.isNumber(name) && !name.equals("CE") && !name.equals("Del"))
                            b.setBackgroundColor(0xfffafafa);
                        else
                            b.setBackgroundColor(0xfff0f0f0);
                        return false;
                    }
                }
                return false;

            }
        };






        buttonLShift.setOnClickListener(UniversalClickListener);
        buttonD.setOnClickListener(UniversalClickListener);
        buttonE.setOnClickListener(UniversalClickListener);
        buttonF.setOnClickListener(UniversalClickListener);

        buttonRShift.setOnClickListener(UniversalClickListener);
        buttonA.setOnClickListener(UniversalClickListener);
        buttonB.setOnClickListener(UniversalClickListener);
        buttonC.setOnClickListener(UniversalClickListener);
        buttonDiv.setOnClickListener(UniversalClickListener);

        buttonAnd.setOnClickListener(UniversalClickListener);
        button7.setOnClickListener(UniversalClickListener);
        button8.setOnClickListener(UniversalClickListener);
        button9.setOnClickListener(UniversalClickListener);
        buttonMul.setOnClickListener(UniversalClickListener);

        buttonOr.setOnClickListener(UniversalClickListener);
        button4.setOnClickListener(UniversalClickListener);
        button5.setOnClickListener(UniversalClickListener);
        button6.setOnClickListener(UniversalClickListener);
        buttonMinus.setOnClickListener(UniversalClickListener);

        button1.setOnClickListener(UniversalClickListener);
        button2.setOnClickListener(UniversalClickListener);
        button3.setOnClickListener(UniversalClickListener);
        buttonPlus.setOnClickListener(UniversalClickListener);

        buttonXor.setOnClickListener(UniversalClickListener);
        button0.setOnClickListener(UniversalClickListener);
        buttonPoint.setOnClickListener(UniversalClickListener);


        //========================================================


        buttonLShift.setOnTouchListener(UniversalTouchListener);
        buttonD.setOnTouchListener(UniversalTouchListener);
        buttonE.setOnTouchListener(UniversalTouchListener);
        buttonF.setOnTouchListener(UniversalTouchListener);
        buttonDel.setOnTouchListener(UniversalTouchListener);

        buttonRShift.setOnTouchListener(UniversalTouchListener);
        buttonA.setOnTouchListener(UniversalTouchListener);
        buttonB.setOnTouchListener(UniversalTouchListener);
        buttonC.setOnTouchListener(UniversalTouchListener);
        buttonDiv.setOnTouchListener(UniversalTouchListener);

        buttonAnd.setOnTouchListener(UniversalTouchListener);
        button7.setOnTouchListener(UniversalTouchListener);
        button8.setOnTouchListener(UniversalTouchListener);
        button9.setOnTouchListener(UniversalTouchListener);
        buttonMul.setOnTouchListener(UniversalTouchListener);

        buttonOr.setOnTouchListener(UniversalTouchListener);
        button4.setOnTouchListener(UniversalTouchListener);
        button5.setOnTouchListener(UniversalTouchListener);
        button6.setOnTouchListener(UniversalTouchListener);
        buttonMinus.setOnTouchListener(UniversalTouchListener);

        buttonNot.setOnTouchListener(UniversalTouchListener);
        button1.setOnTouchListener(UniversalTouchListener);
        button2.setOnTouchListener(UniversalTouchListener);
        button3.setOnTouchListener(UniversalTouchListener);
        buttonPlus.setOnTouchListener(UniversalTouchListener);

        buttonXor.setOnTouchListener(UniversalTouchListener);
        buttonClear.setOnTouchListener(UniversalTouchListener);
        button0.setOnTouchListener(UniversalTouchListener);
        buttonPoint.setOnTouchListener(UniversalTouchListener);
        calculateButton.setOnTouchListener(UniversalTouchListener);

        buttonPlus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                myCalculator.setMode(Calculator.BIN);
                //buttonClear.performClick();
                expression="";
                answer="";
                myCalculator.clear();
                setClickable(button0);
                setClickable(button1);
                setUnclickable(button2);
                setUnclickable(button3);
                setUnclickable(button4);
                setUnclickable(button5);
                setUnclickable(button6);
                setUnclickable(button7);
                setUnclickable(button8);
                setUnclickable(button9);
                setUnclickable(buttonA);
                setUnclickable(buttonB);
                setUnclickable(buttonC);
                setUnclickable(buttonD);
                setUnclickable(buttonE);
                setUnclickable(buttonF);
                vb.vibrate(40);
                if(haveAnswer==1)
                    setAnswer(answer_double);
                maketext("BIN");
                return true;//阻断
            }
        });

        buttonMinus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                myCalculator.setMode(Calculator.DEC);
                //buttonClear.performClick();
                expression="";
                answer="";
                myCalculator.clear();

                setClickable(button0);
                setClickable(button1);
                setClickable(button2);
                setClickable(button3);
                setClickable(button4);
                setClickable(button5);
                setClickable(button6);
                setClickable(button7);
                setClickable(button8);
                setClickable(button9);
                setUnclickable(buttonA);
                setUnclickable(buttonB);
                setUnclickable(buttonC);
                setUnclickable(buttonD);
                setUnclickable(buttonE);
                setUnclickable(buttonF);
                vb.vibrate(40);
                if(haveAnswer==1)
                    setAnswer(answer_double);
                maketext("DEC");
                return true;
            }
        });

        buttonMul.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                myCalculator.setMode(Calculator.HEX);
                //buttonClear.performClick();
                expression="";
                answer="";
                myCalculator.clear();
                setClickable(button0);
                setClickable(button1);
                setClickable(button2);
                setClickable(button3);
                setClickable(button4);
                setClickable(button5);
                setClickable(button6);
                setClickable(button7);
                setClickable(button8);
                setClickable(button9);
                setClickable(buttonA);
                setClickable(buttonB);
                setClickable(buttonC);
                setClickable(buttonD);
                setClickable(buttonE);
                setClickable(buttonF);
                vb.vibrate(40);
                if(haveAnswer==1)
                    setAnswer(answer_double);
                maketext("HEX");
                return true;
            }
        });

        buttonLShift.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                expression+="(";
                expressionText.setText(expression);
                vb.vibrate(10);
                return true;
            }
        });

        buttonRShift.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                expression+=")";
                expressionText.setText(expression);
                vb.vibrate(10);
                return true;
            }
        });

        //===============================================
        //之前会改变一些属性
        /*
        setUnclickable(buttonA);
        setUnclickable(buttonB);
        setUnclickable(buttonC);
        setUnclickable(buttonD);
        setUnclickable(buttonE);
        setUnclickable(buttonF);
        */
        buttonA.setClickable(false);
        buttonB.setClickable(false);
        buttonC.setClickable(false);
        buttonD.setClickable(false);
        buttonE.setClickable(false);
        buttonF.setClickable(false);



    }

    private void maketext(String e)
    {
        Toast.makeText(this,e,Toast.LENGTH_SHORT).show();
    }

}
