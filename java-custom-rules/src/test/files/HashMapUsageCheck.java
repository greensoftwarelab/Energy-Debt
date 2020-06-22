import java.util.HashMap; // Noncompliant {{HashMap Usage}}
import java.util.TreeMap;

class HashMapUsageCheck {

    int count // Noncompliant {{HashMap Usage}}
            (HashMap hmap) // Noncompliant {{HashMap Usage}}
    { return 10; }

    void createHashMap(){
        HashMap<Integer, String> hmap = new HashMap<Integer, String>(); // Noncompliant {{HashMap Usage}}
    }

    Map<Integer, String> newMap(){
        return new TreeMap<Integer, String>();
    }

    int foo1(int a) {
        return a;
    }
}