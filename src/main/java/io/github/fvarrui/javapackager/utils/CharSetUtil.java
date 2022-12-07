package io.github.fvarrui.javapackager.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharSetUtil {

    public static Charset getCommandLineChartSet(){
        try{
            Process p = Runtime.getRuntime().exec("cmd /k chcp");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String res = br.readLine();
            String code = find("\\d+",res);
            switch (code){
                case "936": return Charset.forName("GBK");
                case "65001": return Charset.forName("UTF-8");
            }
        }catch (Exception e){
            return Charset.defaultCharset();
        }
        return Charset.forName("UTF-8");
    }

    private static String find(String pattern,String data){
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(data);
        matcher.find();
        return matcher.group();
    }

}
