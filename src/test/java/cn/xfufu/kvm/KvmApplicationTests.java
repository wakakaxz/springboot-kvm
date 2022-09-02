package cn.xfufu.kvm;

import cn.xfufu.kvm.service.LibvirtServiceImpl;
import cn.xfufu.kvm.service.VmServiceImpl;
import cn.xfufu.kvm.util.LibvirtKit;
import cn.xfufu.kvm.util.RandomKit;
import cn.xfufu.kvm.util.SendEmailKit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.libvirt.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KvmApplicationTests {

}
