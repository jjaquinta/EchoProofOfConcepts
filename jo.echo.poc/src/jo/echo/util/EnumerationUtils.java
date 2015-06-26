package jo.echo.util;

public class EnumerationUtils
{
    public static final String[] ORDINAL =
    {
        "first",
        "second",
        "third",
        "fourth",
        "fifth",
        "sixth",
        "seventh",
        "eighth",
        "ninth",
        "tenth",
        "eleventh",
        "twelfth",
        "thirteenth",
        "fourteenth",
        "fifteenth",
        "sixteenth",
        "seventeenth",
        "eighteenth",
        "nineteenth",
        "twentieth",
        "twenty first",
        "twenty second",
        "twenty third",
        "twenty fourth",
        "twenty fifth",
        "twenty sixth",
        "twenty seventh",
        "twenty eighth",
        "twenty ninth",
        "thirtieth",
        "thirty first",
        "thirty second",
        "thirty third",
        "thirty fourth",
        "thirty fifth",
        "thirty sixth",
        "thirty seventh",
        "thirty eighth",
        "thirty ninth",
    };
    public static final String[] CARDINAL = 
    {
        "one",
        "two",
        "three",
        "four",
        "five",
        "six",
        "seven",
        "eight",
        "nine",
        "ten",
        "eleven",
        "twelve",
        "thirteen",
        "fourteen",
        "fifteen",
        "sixteen",
        "seventeen",
        "eighteen",
        "nineteen",
        "twenty",
        "twenty one",
        "twenty two",
        "twenty three",
        "twenty four",
        "twenty five",
        "twenty six",
        "twenty seven",
        "twenty eight",
        "twenty nine",
    };
    
    public static Integer getOrdinal(String txt)
    {
        for (int i = 0; i < ORDINAL.length; i++)
            if (ORDINAL[i].equalsIgnoreCase(txt))
                return i - 1;
        return null;
    }
    
    public static Integer getCardinal(String txt)
    {
        for (int i = 0; i < CARDINAL.length; i++)
            if (CARDINAL[i].equalsIgnoreCase(txt))
                return i - 1;
        return null;
    }
    
    public static Integer getEnumeration(String txt)
    {
        Integer ret = getOrdinal(txt);
        if (ret == null)
            ret = getCardinal(txt);
        return ret;
    }
}