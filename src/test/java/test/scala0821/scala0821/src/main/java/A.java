import java.util.ArrayList;

public class A {
    public static void main(String[] args) {

        ArrayList<B> arr1 = new ArrayList<B>();
        arr1.add(new B());
        arr1.add(new B());
        arr1.add(new B());

        ArrayList<B1> arr2 = new ArrayList<B1>();
        arr2.add(new B1());
        arr2.add(new B1());
        arr2.add(new B1());


        //arr1 = arr2

    }


}

 class B{ }

class B1 extends B{}
