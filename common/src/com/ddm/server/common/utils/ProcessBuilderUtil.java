package com.ddm.server.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;

public class ProcessBuilderUtil {

    /**
     * 功能：执行外部命令 
     * @param cmd 待执行的命令
     * @return 执行结果
     */
    public static String shell(String cmd) throws Exception {
        if (StringUtils.isEmpty(cmd)) {
            return null;
        }
        //使用"sh -c 命令字符串"的方式解决管道和重定向的问题
        List<String> cmds = new LinkedList<String>();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add(cmd);
        ProcessBuilder pb = new ProcessBuilder(cmds);
        //重定向到标准输出
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.waitFor(3, TimeUnit.SECONDS);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));  
        StringBuffer sb = new StringBuffer();  
        String line;  
        while ((line = br.readLine()) != null) {  
            sb.append(line).append("\n");  
        }  
        String result = sb.toString();  
        return result;
    }

    public static void main(String[] args){
        try {
            ProcessBuilderUtil.shell("ps -ef | grep java | grep -v grep");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}