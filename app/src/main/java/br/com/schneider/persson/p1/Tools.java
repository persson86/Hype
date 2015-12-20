package br.com.schneider.persson.p1;

/**
 * Created by Persson on 17/12/2015.
 */
public class Tools {

    public class SearchStringEmp{
        public void main(String[] args) {
            String strOrig = "Hello readers";
            int intIndex = strOrig.indexOf("Hello");
            if(intIndex == - 1){
                System.out.println("Hello not found");
            }else{
                System.out.println("Found Hello at index "
                        + intIndex);
            }
        }
    }
}
