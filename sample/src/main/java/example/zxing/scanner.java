package example.zxing;
import android.util.Log;

import java.util.*;
public class scanner {
    ArrayList<locCod> scannedCorrect = new ArrayList<locCod>();
    ArrayList<locCod> scannedWrong = new ArrayList<locCod>();
    ArrayList<book> bookList = new ArrayList<book>();
    public class book{
        public locCod libConCode;
        public Boolean correct;
        public book(locCod in1, Boolean in2){
            this.libConCode = in1;
            this.correct = in2;
        }
    }
    public class locCod<X,Y,Z> {
        public String X;
        public int Y;
        public String Z;
        public locCod(String X, int Y, String Z){
            this.X = X;
            this.Y = Y;
            this.Z = Z;
        }
        public locCod(){
            this.X = "";
            this.Y = -1;
            this.Z = "";
        }
        boolean gr(locCod other){
            if(this.X.compareTo(other.X)<0){
                return false;
            }
            if (this.X.compareTo(other.X)==0){
                if(this.Y <other.Y){
                    return false;
                }
                else if(this.Y == other.Y){
                    return this.Z.compareTo(other.Z)>0;
                }
            }
            return true;
        }
    }
    public locCod string_to_locCod(String data){
        String[] code = data.split("\n");
        return new locCod(code[0],Integer.parseInt(code[1]),code[2]);
    }
    public boolean inputOrdered(String data){
        Log.d("input ordered",data);
        locCod input = string_to_locCod(data);

        if(this.scannedCorrect.size()!=0) {
            locCod prev = this.scannedCorrect.get(this.scannedCorrect.size() - 1);
            if (prev.gr(input)) {
                this.scannedWrong.add(input);
                this.bookList.add(new book(input,false));
                return true; // true == Wrong
            }
        }
        this.bookList.add(new book(input,true));
        this.scannedCorrect.add(input);
        return false; //false == correct
    }
    public boolean inputOdd(String data) {
        locCod input = string_to_locCod(data);
        if (this.scannedCorrect.size() != 0) {
            locCod prev = this.scannedCorrect.get(this.scannedCorrect.size() - 1);
            if (input.X.compareTo(prev.X) != 0 || input.Y != prev.Y) {
                this.bookList.add(new book(input,false));
                this.scannedWrong.add(input);
                return true;
            }
        }
        this.scannedCorrect.add(input);
        this.bookList.add(new book(input,true));
        return false;
    }
    public ArrayList<book> getBookList(){
        return this.bookList;
    }
    public void clearLists() {
        this.scannedCorrect.clear();
        this.scannedWrong.clear();
        this.bookList.clear();
    }
}