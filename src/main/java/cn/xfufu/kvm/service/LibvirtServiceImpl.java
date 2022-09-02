package cn.xfufu.kvm.service;

import cn.xfufu.kvm.entity.Host;
import cn.xfufu.kvm.entity.Net;
import cn.xfufu.kvm.entity.StoragePool;
import cn.xfufu.kvm.repository.UserRepository;
import cn.xfufu.kvm.repository.VmRepository;
import cn.xfufu.kvm.util.LibvirtKit;
import cn.xfufu.kvm.util.RandomKit;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LibvirtServiceImpl {

    @Autowired
    private LibvirtKit libvirtKit;

    /**
     * @return 宿主机信息
     */
    public Host getHostInfo() {
        return libvirtKit.getHost();
    }

    /**
     * @return 直接获取默认存储池
     */
    public StoragePool getStoragePoolDefalut() {
        return libvirtKit.getStoragePool("default");
    }

    /**
     * @return 存储池信息列表
     */
    public List<StoragePool> getStoragePoolList() {
        List<StoragePool> poolList = new ArrayList<>();
        try {
            String[] pools = libvirtKit.getConnect().listStoragePools();
            for (String name : pools) {
                StoragePool storagePool = libvirtKit.getStoragePool(name);
                poolList.add(storagePool);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return poolList;
    }

    /**
     * @return 网络信息桥接网卡br0
     */
    public Net getBriageNet() {
        Sigar sigar = libvirtKit.getSigar();
        Net net = new Net();
        try {
            String[] interfaceList = sigar.getNetInterfaceList();
            for (String name : interfaceList) {
                if (name.equals("br0")) {
                    net = libvirtKit.getNet(name);
                    break;
                }
            }
        } catch (SigarException e) {
            e.printStackTrace();
            return null;
        }
        return net;
    }


    /**
     * @return 网络信息列表
     */
    public List<Net> getNetList() {
        List<Net> netList = new ArrayList<>();
        Sigar sigar = libvirtKit.getSigar();
        try {
            String[] interfaceList = sigar.getNetInterfaceList();
            for (String name : interfaceList) {
                Net net = libvirtKit.getNet(name);
                netList.add(net);
            }
        } catch (SigarException e) {
            e.printStackTrace();
            return null;
        }
        return netList;
    }

    /**
     * @param webPort web端口
     * @param vncPort vnc端口
     * @return 开启成功或失败
     */
    public Boolean openWebPort(Integer webPort, Integer vncPort) {
        return libvirtKit.openWebPort(webPort, vncPort);
    }
}
