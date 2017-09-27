import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.StrUtils;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author brock
 */
public class Test implements Serializable {
    private transient List<Integer> a = new ArrayList<>();
    private transient String b;
    private int c;

    public static void main(String[] args) {
        Test test = new Test();
        try {
            String str = StrUtils.toString(test);
            System.out.println(test);
            Test unser = (Test)StrUtils.fromString(str);
            System.out.println(unser);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return "[Test, a: " + a + ", b: " + b + ", c: " + c + "]";
    }
}
