package example.zxing;
import java.util.*;
public class scanner {
    List<String> scanned = new ArrayList<String>();
    public void inputQR(String data) { scanned.add(data); }
}