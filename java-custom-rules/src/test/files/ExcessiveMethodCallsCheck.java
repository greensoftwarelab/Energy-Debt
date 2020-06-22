class ExcessiveMethodCallsCheck {
    private int count = 0;

    public int getNumber(){
        return 10;
    }

    public void addUp() { count++; }

    public int sum(){
        int a = 0;
        for(int i = 0; i < 10; i++){
            a += getNumber(); // Noncompliant {{Excessive Method Calls}}
        }
        return a;
    }

    public int mult(){
        int a = 2;
        for(int i = 0; i < 10; i++){
            if(a%2 == 0)
                a *= getNumber(); // Noncompliant {{Excessive Method Calls}}
        }
        return a;
    }

    public void add(){
        for(int i = 0; i < 10; i++)
            addUp();
    }



}