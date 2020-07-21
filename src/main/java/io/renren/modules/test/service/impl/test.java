package io.renren.modules.test.service.impl;
import io.renren.modules.test.utils.SSH2Utils;
import org.slf4j.Logger;

public class test {
    public static void main(String args[]){
        SSH2Utils ssh2Util = new SSH2Utils("10.102.98.239", "vmuser",
                "vmuser@test", 22);
        String psStr = ssh2Util.runCommand("echo 1111");
        System.out.print(psStr);
    }

}


