package argo.testcamelproject

import java.text.SimpleDateFormat

public class ScratchPad {
    public static void main(String[] args) {
//        System.out.println(returnChar())
//        System.out.println(new Date().toString())
//        printDate()
//        System.out.println(checkRet())

        Map<String, Object> myMap = new HashMap<>();
        myMap.put( "111-hello", '111a' );
        myMap.put( "111-world", '111b' );
        myMap.put( "111-test", '111c' );
        myMap.put( "111-java", '111d' )

        ['111-hello' : '111a',
         '111-world' : '111b',
         '111-test'  : '111c',
         "111-java"  : '111d' ] as Map<String, Object>
    }

    private static testMapPrefix(){
        Map<String, Object> myMap = new TreeMap<String, Object>();
        myMap.put( "111-hello", '111a' );
        myMap.put( "111-world", '111b' );
        myMap.put( "111-test", '111c' );
        myMap.put( "111-java", '111d' )

        myMap.put( "123-one", null )
        myMap.put( "123-two", null )
        myMap.put( "123--three", null )
        myMap.put( "123--four", null )

        myMap.put( "125-hello", null )
        myMap.put( "125--world", null )

        System.out.println( "111 \t" + getByPreffix( myMap, "111" ) )
        System.out.println( "123 \t" + getByPreffix( myMap, "123" ) )
        System.out.println( "123-- \t" + getByPreffix( myMap, "123--" ) )
        System.out.println( "12 \t" + getByPreffix( myMap, "12" ) )
    }

    private static SortedMap<String, Object> getByPreffix(
            NavigableMap<String, Object> myMap,
            String prefix ) {
        return myMap.subMap( prefix, prefix + Character.MAX_VALUE )
    }

    static void printDate() {
        String date = new SimpleDateFormat("_yyyy_MM_dd-HH_mm_ss").format(new Date())
        System.out.println(date)
    }

    private static int checkRet() {
        def a = [1, 2, 3, 4, 5]
        for (int num : a) {
            if (num == 3) {
                return num
            }
        }
        4
    }

}
