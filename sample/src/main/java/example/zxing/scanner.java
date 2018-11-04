package example.zxing;
import java.util.*;
public class scanner {
    ArrayList<String> scanned = new ArrayList<String>();
    public class locCod<X,Y,Z> {
        String X;
        int Y;
        String Z;
    }
    public boolean inputOrdered(String data){
        this.scanned.add(data);
        if(this.scanned.size()==0){
            if(data.compareTo(this.scanned.get(this.scanned.size() - 1)) <0 ){
                return false;
            }
        }
        return true;
    }
    public boolean inpuutOrdered(String data){
        this.scanned.add(data);
        return false;
    }
}
