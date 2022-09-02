package cn.xfufu.kvm;

import cn.xfufu.kvm.entity.User;
import cn.xfufu.kvm.service.UserServiceImpl;
import cn.xfufu.kvm.util.Md5Kit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserServiceImpl service;

    @Autowired
    private Md5Kit md5Kit;

    @Test
    public void saveUser() {
        String toMd5 = md5Kit.toMd5("123456");
        Boolean flag = service.saveUser(new User(null, "测试", "nyistxiaozheng@qq.com", toMd5, 1));
        System.out.println(flag);
    }

    @Test
    public void updateUser() {
        String toMd5 = md5Kit.toMd5("12345678");
        service.updateUser(new User(2, "测试2", "nyistxiaozheng@qq.com", toMd5, 1));
    }

    @Test
    public void findUserByEmail() {
        User user = service.findUserByEmail("1838255989@qq.com");
        System.out.println(user);
    }

    @Test
    public void findUserById() {
        User user = service.findUserById(1);
        System.out.println(user);
    }

    @Test
    public void findUserAll() {
        List<User> all = service.findUserAll();
        System.out.println(all);
    }

    @Test
    public void findUserAllNormal() {
        List<User> all = service.findUserAllByFlag(0);
        System.out.println(all);
    }
}
