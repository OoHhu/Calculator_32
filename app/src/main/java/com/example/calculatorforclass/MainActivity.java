package com.example.calculatorforclass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    StringBuffer disAns;
    StringBuffer disAns2;
    StringBuffer nowEdit;
    char setSymbol;
    Boolean is_disAns2 = false;
    TextView ansText;
    TextView formulaText;
    String answer = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        disAns = new StringBuffer();
        disAns2 = new StringBuffer();
        ansText = findViewById(R.id.Answer);
        formulaText = findViewById(R.id.formula);
        nowEditDisplay0();
    }

    private void nowEditSelect() {
        if (is_disAns2) {
            nowEdit = disAns2;
        } else {
            nowEdit = disAns;
        }
    }

    public void Number_onClick(View v) {
        //https://groups.google.com/g/android-group-japan/c/GqU6NWkw4Dk を参考に実装
        String zero = "0";
        char clickNum = '0';
        nowEditSelect();

        //入力の最大文字数
        if (nowEdit.toString().length() == 14) return;

        String text = (String) ansText.getText();
        int id = v.getId();

        switch (id) {
            case R.id.bt_dtto:
                if (text.contains(".")) {
                    return;
                }
                clickNum = '.';
                break;
            case R.id.bt0:
                //表示が0の時
                if (zero.contentEquals(nowEdit)) {
                    return;
                }
                clickNum = '0';
                break;
            case R.id.bt1:
                clickNum = '1';
                break;
            case R.id.bt2:
                clickNum = '2';
                break;
            case R.id.bt3:
                clickNum = '3';
                break;
            case R.id.bt4:
                clickNum = '4';
                break;
            case R.id.bt5:
                clickNum = '5';
                break;
            case R.id.bt6:
                clickNum = '6';
                break;
            case R.id.bt7:
                clickNum = '7';
                break;
            case R.id.bt8:
                clickNum = '8';
                break;
            case R.id.bt9:
                clickNum = '9';
                break;
            default:
                break;
        }

        //表示が0の時と、エラーの時にリセット
        //’.’が押された場合は"0."と表示したいから、リセットしない
        if ((zero.contentEquals(ansText.getText()) && id != R.id.bt_dtto)
                || getString(R.string.error).contentEquals(ansText.getText())) {
            nowEditReset();
        }

        //nowEditが空の時にドットが押された時
        if(id==R.id.bt_dtto && nowEdit.length()==0){
        nowEdit.append(0).append(clickNum);
        }else{
        nowEdit.append(clickNum);
        }
        ansText.setText(nowEdit);
    }//end of Number_onClick(View v)

    //nowEditを0表示に戻す
    private void nowEditDisplay0() {
        nowEditReset();
        nowEdit.append(0);
        ansText.setText(nowEdit);
    }

    //nowEditをまっさらに
    private void nowEditReset() {
        nowEditSelect();
        nowEdit.setLength(0);
    }

    /*delete 1 letter
     * */
    public void Del1_onClick(View v) {
        nowEditSelect();

        if (nowEdit.length() > 0) nowEdit.deleteCharAt(nowEdit.length() - 1);
        if (nowEdit.length() <= 0) {
            nowEdit.append(0);
        }
        ansText.setText(nowEdit);
    }

    /**
     * thinking about situation that Symbol button is pushed after...
     * 1.numBt : (if)have 2nd num-->Call to equalBt, then Set symbol(continue with calculation procedure)
     * (else if)have a num--> Set symbol
     * 2.AnsBt : Same as above
     * 3.DeleteBt All, 1 : Set symbol
     * 4.DeleteBt c1 : Set symbol...To check that have a least '0'
     * 5.DeleteBt c2 : Up date Symbol
     * 6.Symbol : Up date symbol
     * 7.equal :  Set symbol
     * So,Type of Situation are...
     * 1. Set symbol(1-2(else),3,4,7) -->   base
     * 2. Update symbol(5,6) --> Substitute with 1
     * 3. Call equalBtn and disAns2 set Answer, then to 1 (1-2(if))
     * -->  (disAns, disAns2, symbol != null)&&( formulaText contains '+','-','x','/' )
     * &&(formulaText not contains '=')
     * Variable "symbol" doesn't become mark.
     * It inherits the situation from the previous, Even if it's not set in current time.
     */
    public void Symbol_onClick(View v) {
        if (disAns.toString().length() <= 0) disAns.append(answer);

        if (disAns2.toString().length() > 0 && setSymbol != '\u0000') {
            String nowFormulaText = (String) formulaText.getText();
            String pattern = "[-]?[0-9]+?.?[0-9]+?…?[+|\\-|×|÷]";

            if (nowFormulaText.matches(pattern)) {
                equal_onClick(v);
                disAns.append(answer);
            }
        }

        switch (v.getId()) {
            case R.id.bt_add:
                setSymbol = '+';
                break;
            case R.id.bt_minus:
                setSymbol = '-';
                break;
            case R.id.bt_times:
                setSymbol = '×';
                break;
            case R.id.bt_divide:
                setSymbol = '÷';
                break;
            default:
                break;
        }

        formulaText.setText(setOver11letters_Adjust(disAns.toString()) + setSymbol);
        is_disAns2 = true;
        nowEditReset();
    }

    public void equal_onClick(View v) {
        /**
         * BigDecimal参考
         * https://qiita.com/ota-meshi/items/967304d406d668febe1d
         *https://qiita.com/blendthink/items/b66b0cc960ae59aa6265
         *
         * Attention!!
         * BigDecimal input1 で宣言するとandroid.icu.mathとして認識されてしまう*/
        java.math.BigDecimal input1 = null;
        java.math.BigDecimal input2 = null;
        java.math.BigDecimal ans = null;
        String ansSt = null;

        //disAnsの状態で、symbolがないのに"="が押された時
        if ((!is_disAns2) && (setSymbol == '\u0000')) {
            formulaText.setText(disAns.toString() + "=");
            return;
        }
        if (disAns.toString().length() <= 0) disAns.append(answer);

        is_disAns2 = false;

        //二つ目の値の入力がない場合
        if (disAns2.length() <= 0) disAns2.append(disAns.toString());

        //数値に変換
        input1 = java.math.BigDecimal.valueOf(Double.parseDouble(disAns.toString()));
        input2 = java.math.BigDecimal.valueOf(Double.parseDouble(disAns2.toString()));

        if (input2.doubleValue() == 0 && setSymbol == '÷') {
            AllClear_onClick(v);
            ansText.setText(R.string.error);
            return;
        } else {
            switch (setSymbol) {
                case '+':
                    ans = input1.add(input2);
                    break;
                case '-':
                    ans = input1.subtract(input2);
                    break;
                case '×':
                    ans = input1.multiply(input2);
                    break;
                case '÷':
                    //scale:30は適当。表示桁が14なので倍あれば十分かと
                    ans = input1.divide(input2, 30, BigDecimal.ROUND_HALF_UP);
                    break;
                default:
                    break;
            }
        }
        ansSt = ans.stripTrailingZeros().toPlainString();

        //14桁以上は表示させない
        if (ansSt.length() > 14) {
            ansText.setText(ansSt.substring(0, 14));
        } else {
            ansText.setText(ansSt);
        }
        answer = ansSt;

        formulaText.setText(setOver11letters_Adjust(disAns.toString())
                + setSymbol + setOver11letters_Adjust(disAns2.toString()) + "=");

        //計算が終わったので、answerをセットする
        disAns.setLength(0);
        /**
         * On end of this method's condition
         * disAns = ""
         * symbol, disAns2 != null
         */
    }

    private String setOver11letters_Adjust(String words) {
        if (words.length() > 11) {
            return words.substring(0, 10) + "…";
        }
        return words;
    }

    public void AllClear_onClick(View v) {
        //disAns2がnowEditの場合の初期化
        disAns2.setLength(0);
        is_disAns2 = false;

        //disAnsの初期化
        nowEditDisplay0();
        setSymbol = '\u0000';
        formulaText.setText("");
        answer = "0";
    }

    public void Clear1_onClick(View v) {
        if (is_disAns2) {
            //nowEditがdisAns2の時
            disAns.setLength(0);
            disAns.append(disAns2.toString());
            nowEditReset();

            if (disAns.toString().length() > 0) {
                //二つ目の数字入力がある場合
                formulaText.setText(disAns.toString() + setSymbol);
                ansText.setText(disAns.toString());
            } else {
                is_disAns2 = false;
                nowEditDisplay0();
                setSymbol = '\u0000';
                formulaText.setText("");
            }
        } else {
            //is_disAns=falseのとき、setSymbol != '\u0000'にはなりえない。
            nowEditDisplay0();
        }
    }

    public void Clear2_onClick(View v) {
        if (is_disAns2) nowEditDisplay0();
    }

    public void Ans_onClick(View v) {
        if (answer != null) {
            nowEditReset();
            nowEdit.append(answer);
            ansText.setText(nowEdit);
        }
    }
}//end of class

