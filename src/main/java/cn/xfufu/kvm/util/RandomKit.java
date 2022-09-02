package cn.xfufu.kvm.util;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class RandomKit {
    /**
     * @return 随机生成MAC地址
     */
    public String randomMac() {
        Random random = new Random();
        String[] mac = {
                String.format("%02x", 0x52),
                String.format("%02x", 0x54),
                String.format("%02x", 0x00),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff))
        };
        String separatorOfMac = ":";
        return String.join(separatorOfMac, mac);
    }

    /**
     * @return 生成随机UUID
     */
    public String randomUUID() {
        return UUID.randomUUID().toString();
    }


    /**
     * @return 返回一个随机验证码
     */
    public String randomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
