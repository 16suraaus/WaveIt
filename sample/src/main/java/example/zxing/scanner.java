package example.zxing;
import java.util.*;
public class scanner {
    ArrayList<locCod> scanned = new ArrayList<locCod>();
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
        boolean geq(locCod other){
            if(this.X.compareTo(other.X)<0){
                return false;
            }
            if (this.X.compareTo(other.X)==0){
                if(this.Y <other.Y){
                    return false;
                }
                else if(this.Y == other.Y){
                    return this.Z.compareTo(other.Z)>=0;
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
        locCod input = string_to_locCod(data);
        this.scanned.add(input);
        if(this.scanned.size()==0){
            locCod prev = this.scanned.get(this.scanned.size()-1);
            if(input.geq(prev)){
                return false;
            }
        }
        return true;
    }
    public boolean inputOdd(String data){
        locCod input = string_to_locCod(data);
        this.scanned.add(input);
        if(this.scanned.size()==0) {
            locCod prev = this.scanned.get(this.scanned.size() - 1);
            if(input.X.compareTo(prev.X)>=0)
        }
        return true;
    }
}
