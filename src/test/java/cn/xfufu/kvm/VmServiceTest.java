package cn.xfufu.kvm;

import cn.xfufu.kvm.service.LibvirtServiceImpl;
import cn.xfufu.kvm.service.UserServiceImpl;
import cn.xfufu.kvm.service.VmServiceImpl;
import cn.xfufu.kvm.util.LibvirtKit;
import cn.xfufu.kvm.util.RandomKit;
import cn.xfufu.kvm.util.SendEmailKit;
import cn.xfufu.kvm.vo.VmVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VmServiceTest {

    @Autowired
    private VmServiceImpl vmService;

    @Autowired
    private LibvirtServiceImpl libvirtService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private SendEmailKit sendEmailKit;

    @Autowired
    private RandomKit randomKit;

    @Test
    public void getHost() {
        System.out.println(libvirtService.getHostInfo());
    }

    @Test
    public void getStoragePoolDefault() {
        System.out.println(libvirtService.getStoragePoolDefalut());
    }

    @Test
    public void getNetList() {
        libvirtService.getNetList().forEach(System.out::println);
    }

    @Test
    public void getVmVoList() {
        vmService.getVmVoList().forEach(System.out::println);
    }


    @Test
    public void startVirtualMachine() {
        Integer machine = vmService.startVirtualMachine(1);
        System.out.println(machine);
    }


    @Test
    public void suspendVirtualMachine() {
        Boolean flag = vmService.suspendVirtualMachine(1);
        System.out.println(flag);
    }

    @Test
    public void resumeVirtualMachine() {
        Boolean flag = vmService.resumeVirtualMachine(1);
        System.out.println(flag);
    }

    @Test
    public void shutdownVirtualMachine() {
        Boolean flag = vmService.shutdownVirtualMachine(1);
        System.out.println(flag);
    }

    @Test
    public void deleteVirtualMachine() {
        vmService.deleteVirtualMachine(1);
    }

    @Test
    public void cloneVirtualMachine() {
//        Boolean flag = vmService.cloneVirtualMachine("myVm1",
//                2,
//                "lubuntu16gui-clone",
//                2,
//                2048,
//                1024 * 1024 * 10);
        Boolean flag = vmService.cloneVirtualMachine("myVm2",
        2,
        "centos7.0-clone",
        1,
        1024,
        1024 * 1024 * 5);

        System.out.println(flag);
    }


    @Test
    public void getVmVoListByUserId() {
        List<VmVo> list = vmService.getVmVoListByUserId(2);
        list.forEach(System.out::println);
    }

    @Test
    public void mailSend() {
        Boolean b = sendEmailKit.sendHTMLEmail("1838255989@qq.com", "测试验证码", randomKit.randomCode());
        System.out.println(b);
    }
}
