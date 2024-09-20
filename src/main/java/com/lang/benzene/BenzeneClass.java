package src.main.java.com.lang.benzene;

public class BenzeneClass implements BenzeneCallable{
    final String name;

    BenzeneClass(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return "<user defined class: " + this.name + ">";
    }
    
}
