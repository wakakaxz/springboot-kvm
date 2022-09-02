package cn.xfufu.kvm.util;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * @author Ez
 * Md5
 */
@Component
public class Md5Kit {

    public String toMd5(String password) {
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(password.getBytes());
        return md5DigestAsHex;
    }

}
