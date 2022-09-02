package cn.xfufu.kvm;

import cn.xfufu.kvm.util.LibvirtKit;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.junit.Test;
import org.libvirt.*;

import java.io.Console;
import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class KvmTest {

    private static String SEPARATOR_OF_MAC = ":";

    public String randomMac4Qemu() {
        Random random = new Random();
        String[] mac = {
                String.format("%02x", 0x52),
                String.format("%02x", 0x54),
                String.format("%02x", 0x00),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff))
        };
        return String.join(SEPARATOR_OF_MAC, mac);
    }

    @Test
    public void test1() throws LibvirtException { //连接测试
        Connect connect = new Connect("qemu:///system", true);
        System.out.println("宿主机主机名： " + connect.getHostName());
        System.out.println("Libvirt版本号： " + connect.getLibVirVersion());
        System.out.println("连接的URI： " + connect.getURI());
        System.out.println("宿主机剩余内存： " + connect.getFreeMemory() / 1024 / 1024);
        System.out.println("hyperviser名称： " + connect.getType());
    }

    @Test
    public void test1_1() throws SigarException {
        Sigar sigar = new Sigar();
        System.out.println(sigar.getMem().getRam());
        System.out.println(sigar.getMem().getTotal()/1024/1024);
//        System.out.println(sigar.getMem().getActualFree()/1024/1024);
//        System.out.println(sigar.getMem().getActualUsed()/1024/1024);
//
//        System.out.println(sigar.getMem().getFreePercent());
//        System.out.println(sigar.getMem().getUsedPercent());
//        CpuInfo[] cpuInfos = sigar.getCpuInfoList();
//
//        for (CpuInfo cpuInfo : cpuInfos) {
//            System.out.println(cpuInfo.getModel());
//            System.out.println(cpuInfo.getMhz());
//        }
//        System.out.println(cpuInfos.length);
//        System.out.println(sigar.getCpuInfoList()[0]);
    }

    @Test
    public void test2() throws LibvirtException { //StoragePool
        Connect connect = new Connect("qemu:///system");
//        String[] pools = connect.listStoragePools();
//        for (String pool : pools) {
//            System.out.println(pool);
//        }
        StoragePool storagePool = connect.storagePoolLookupByName("disk");
        StoragePoolInfo info = storagePool.getInfo();

        System.out.println("存储池状态： " + info.state);
        System.out.printf("存储池容量： %f G\n", info.capacity / 1024.00 / 1024.00 / 1024.00);
        System.out.printf("存储池可用容量： %f G\n", info.available / 1024.00 / 1024.00 / 1024.00);
        System.out.printf("存储池已用容量： %f G\n", info.allocation / 1024.00 / 1024.00 / 1024.00);
    }


    @Test
    public void test3() throws LibvirtException { // StorageVolume
        Connect connect = new Connect("qemu:///system");
        StoragePool storagePool = connect.storagePoolLookupByName("default");
        String[] volumes = storagePool.listVolumes();
        for (String volume : volumes) {
            StorageVol storageVol = connect.storageVolLookupByPath("/var/lib/libvirt/images/" + volume);
            StorageVolInfo info = storageVol.getInfo();
            System.out.println(storageVol.storagePoolLookupByVolume().getName());
            System.out.println(storageVol.getKey());
            System.out.println(info.type);
            System.out.println(info.allocation);
            System.out.println(info.capacity);
        }
    }

    @Test
    public void test4() throws LibvirtException, DocumentException {//createStorageVol
        Connect connect = new Connect("qemu:///system");

        SAXReader reader = new SAXReader();
        Document document = reader.read(new File("/usr/module/kvm/xml/storage.xml"));
        String xmlDesc = document.asXML();
        System.out.println("文件描述： " + xmlDesc);

        StoragePool storagePool = connect.storagePoolLookupByName("img");

        //StorageVol genericVol = storagePool.storageVolLookupByName("generic.qcow2");
        //StorageVol storageVol = storagePool.storageVolCreateXMLFrom(xmlDesc, genericVol, 0);

        // 创建vol
        StorageVol storageVol = storagePool.storageVolCreateXML(xmlDesc, 0);

        System.out.println("创建存储卷名： " + storageVol.getName());
        System.out.println("创建存储卷路径： " + storageVol.getPath());

        StorageVolInfo info = storageVol.getInfo();
        System.out.println("存储卷的类型： " + info.type);
        System.out.println("存储卷的容量：" + info.capacity / 1024.00 / 1024.00 / 1024.00);
        System.out.println("存储卷的可用容量： " + (info.capacity - info.allocation) / 1024.00 / 1024.00 / 1024.00);
        System.out.println("存储卷的已用容量： " + info.allocation / 1024.00 / 1024.00 / 1024.00);
        System.out.println("存储卷的描述xml： " + storageVol.getXMLDesc(0));

    }

    @Test
    public void test6() throws LibvirtException, DocumentException {
        Connect connect = new Connect("qemu:///system");

        int[] idsOfDomain = connect.listDomains();
        System.out.println("正在运行的虚拟机个数：" + idsOfDomain.length);
        for (int id : idsOfDomain) {
            Domain domain = connect.domainLookupByID(id);
            System.out.println("虚拟机的id：" + domain.getID());
            System.out.println("虚拟机的uuid：" + domain.getUUIDString());
            System.out.println("虚拟机的名称：" + domain.getName());
            System.out.println("虚拟机的是否自动启动：" + domain.getAutostart());
            System.out.println("虚拟机的状态：" + domain.getInfo().state);
            System.out.println("虚拟机的最大内存：" + domain.getInfo().maxMem);
            System.out.println("虚拟机的CpuTime：" + domain.getInfo().cpuTime);
            System.out.println("虚拟机的virCPU：" + domain.getInfo().nrVirtCpu);
        }

        String[] uuidsOfDefinedDomain = connect.listDefinedDomains();
        System.out.println("已定义未运行的虚拟机个数：" + uuidsOfDefinedDomain.length);
        for (String name : uuidsOfDefinedDomain) {
            System.out.println("虚拟机名称： " + name);
            Domain domain = connect.domainLookupByName(name);
            System.out.println(domain.getOSType());
//            System.out.println(domain.getXMLDesc(0));
//            System.out.println("虚拟机OSType:"+domain.getOSType());
//            System.out.println("虚拟机的id：" + domain.getID());
//            System.out.println("虚拟机的uuid：" + domain.getUUIDString());
//            System.out.println("虚拟机的是否自动启动：" + domain.getAutostart());
//            System.out.println("虚拟机的状态：" + domain.getInfo().state);
//            System.out.println("虚拟机的最大内存：" + domain.getInfo().maxMem);
//            System.out.println("虚拟机的内存：" + domain.getInfo().memory);
//            System.out.println("虚拟机的CpuTime：" + domain.getInfo().cpuTime);
//            System.out.println("虚拟机的virCPU：" + domain.getInfo().nrVirtCpu);

//            Document document = DocumentHelper.parseText(domain.getXMLDesc(0));
//            Element root = document.getRootElement();
//            Attribute vncPort = root.element("devices").element("graphics").attribute("port");
//            Integer text = Integer.valueOf(vncPort.getText());
//            System.out.println(text);
        }
    }

    @Test
    public void test8() {
        // 随机产生UUID
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
        // 随机产生MAC地址
        String randomMac4Qemu = randomMac4Qemu();
        System.out.println(randomMac4Qemu);
    }

    @Test
    public void test9() throws LibvirtException, DocumentException {
        Connect connect = new Connect("qemu:///system");

        SAXReader reader = new SAXReader();
        Document document = reader.read(new File("/usr/module/kvm/xml/start.xml"));
        String xmlDesc = document.asXML();
        System.out.println("创建虚拟机描述： " + xmlDesc);

        Domain domain = connect.domainCreateXML(xmlDesc, 0);

        System.out.println("虚拟机的id：" + domain.getID());
        System.out.println("虚拟机的uuid：" + domain.getUUIDString());
        System.out.println("虚拟机的名称：" + domain.getName());
        System.out.println("虚拟机的是否自动启动：" + domain.getAutostart());
        System.out.println("虚拟机的状态：" + domain.getInfo().state);
        System.out.println("虚拟机的描述xml：" + domain.getXMLDesc(0));
    }

    @Test
    public void test10() throws LibvirtException, DocumentException {
        Connect connect = new Connect("qemu:///system");

        SAXReader reader = new SAXReader();
        Document document = reader.read(new File("/usr/module/kvm/xml/start.xml"));
        String xmlDesc = document.asXML();
        System.out.println("定义虚拟机描述： " + xmlDesc);

        Domain domain = connect.domainDefineXML(xmlDesc);
        domain.setAutostart(false);

        domain.create(); // 定义完后直接启动
        System.out.println("虚拟机的id：" + domain.getID());
        System.out.println("虚拟机的uuid：" + domain.getUUIDString());
        System.out.println("虚拟机的名称：" + domain.getName());
        System.out.println("虚拟机的是否自动启动：" + domain.getAutostart());
        System.out.println("虚拟机的状态：" + domain.getInfo().state);
        System.out.println("虚拟机的描述xml：" + domain.getXMLDesc(0));
    }

    @Test
    public void test11() throws LibvirtException {
        Connect connect = new Connect("qemu:///system");
        // 已定义未开启
//        String[] definedDomains = connect.listDefinedDomains();
//        for (String name : definedDomains) {
//            System.out.println(name);
//            Domain domain = "demo".equals(name) ? connect.domainLookupByName(name) : null;
//            if (domain != null) {
//                domain.create();
//            }
//        }
        // 正在运行
//        int[] domains = connect.listDomains();
//        for (int domain : domains) {
//            System.out.println(domain);
//        }
//        Domain demo = connect.domainLookupByName("demo");
//        System.out.println(Arrays.toString(demo.getVcpusCpuMaps()));
//        System.out.println(demo.getOSType());
//        demo.create();// 开启
//        demo.shutdown();// 关闭
//        demo.reboot(0);// 重启
        // 快照
//        System.out.println(demo.snapshotNum());
//        String[] names = demo.snapshotListNames();
//        for (String name : names) {
//            System.out.println(name);
//        }


        // CPU最大数量不能变？
//        demo.create();
//        demo.setVcpus());
//        demo.setMaxMemory(2097152);
//        System.out.println(demo.getMaxMemory());
//        System.out.println(demo.getMaxVcpus());// 虚拟机必须运行才能操作

    }

    @Test
    public void test12() throws LibvirtException {
        Connect connect = new Connect("qemu:///system");

//        StoragePool storagePool = connect.storagePoolLookupByName("default");
//        StorageVol storageVol = storagePool.storageVolLookupByName("demo.qcow2");
//        System.out.println("存储卷名称：" + storageVol.getName());
//        System.out.println(storageVol.getPath());

        Domain domain = connect.domainLookupByName("demo");
        domain.undefine();

//        domain.shutdown();

//        storageVol.free();
//        storageVol.wipe();
//        storageVol.delete(0);
    }

    @Test
    public void test13() throws LibvirtException {
        Connect connect = new Connect("qemu:///system");

        Domain domain = connect.domainLookupByName("centos7.0");
        // 创建快照需要xml
//        DomainSnapshot domainSnapshot = domain.snapshotCreateXML();

//        String[] names = domain.snapshotListNames();
//        for (String name : names) {
//            System.out.println(name);
//        }
//        System.out.println(kit.getStorageVolDirPath());
    }

}
