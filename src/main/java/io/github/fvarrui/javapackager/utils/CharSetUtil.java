package io.github.fvarrui.javapackager.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
                case "037": return Charset.forName("IBM037");
                case "936": return Charset.forName("gb2312");
                case "950": return Charset.forName("big5");
                case "1145": return Charset.forName("IBM01145");
                case "1200": return StandardCharsets.UTF_16;
                case "51936": return Charset.forName("EUC-CN");
                case "65001": return StandardCharsets.UTF_8;
            }
        }catch (Exception e){
        }
        return Charset.defaultCharset();
    }

    private static String find(String pattern,String data){
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(data);
        matcher.find();
        return matcher.group();
    }

}
