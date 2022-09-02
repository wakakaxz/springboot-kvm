package cn.xfufu.kvm.util;

import cn.xfufu.kvm.entity.Host;
import cn.xfufu.kvm.entity.Net;
import cn.xfufu.kvm.entity.StoragePool;
import cn.xfufu.kvm.entity.VirtualMachine;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.libvirt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 用来生成Connect对象和Sigar对象
 * 以及获取各种路径
 */
@Component
public class LibvirtKit {

    @Value("${connect.url}")
    private String url;
    @Value("${storage.dir.path}")
    private String storageVolDirPath;
    @Value("${vm.xml.dir.path}")
    private String vmXmlDirPath;
    @Value("${iso.dir.path}")
    private String isoDirPath;
    @Value("${novnc.dir.path}")
    private String novncDirPath;

    private Connect connect;
    private Sigar sigar;

    public LibvirtKit() {
    }

    public String getUrl() {
        return url;
    }

    public String getStorageVolDirPath() {
        return storageVolDirPath;
    }

    public String getVmXmlDirPath() {
        return vmXmlDirPath;
    }

    public Connect getConnect() {
        try {
            return connect == null ? new Connect(url) : connect;
        } catch (LibvirtException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Sigar getSigar() {
        return sigar == null ? new Sigar() : sigar;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStorageVolDirPath(String storageVolDirPath) {
        this.storageVolDirPath = storageVolDirPath;
    }

    public void setVmXmlDirPath(String vmXmlDirPath) {
        this.vmXmlDirPath = vmXmlDirPath;
    }

    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    public void setSigar(Sigar sigar) {
        this.sigar = sigar;
    }

    public String getNovncDirPath() {
        return novncDirPath;
    }

    public void setNovncDirPath(String novncDirPath) {
        this.novncDirPath = novncDirPath;
    }

    public String getIsoDirPath() {
        return isoDirPath;
    }

    public void setIsoDirPath(String isoDirPath) {
        this.isoDirPath = isoDirPath;
    }



    public Host getHost() {
        Connect connect = getConnect();
        Sigar sigar = getSigar();
        Host host = new Host();
        try {
            host.setHostName(connect.getHostName());
            host.setUrl(connect.getURI());
            host.setHyperVisor(connect.getType());
            host.setVirVersion(String.valueOf(connect.getLibVirVersion()));

            CpuInfo info = sigar.getCpuInfoList()[0];
            host.setCpuType(info.getModel());
            host.setCpuNum(info.getTotalCores());
            host.setCpuMhz(info.getMhz());

            Mem mem = sigar.getMem();
            host.setTotalMemory(Math.toIntExact(mem.getTotal() / 1024 / 1024));
            host.setUsedMemory(Math.toIntExact(mem.getActualUsed() / 1024 / 1024));
            host.setFreeMemory(Math.toIntExact(mem.getActualFree() / 1024 / 1024));
            host.setUsedMemoryPercent(mem.getUsedPercent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return host;
    }

    public StoragePool getStoragePool(String pool) {
        StoragePool storagePool = new StoragePool();
        Connect connect = getConnect();
        try {
            StoragePoolInfo info = connect.storagePoolLookupByName(pool).getInfo();
            storagePool.setName(pool);
            storagePool.setState(String.valueOf(info.state));
            storagePool.setCapacity(Math.toIntExact(info.capacity / 1024 / 1024));
            storagePool.setAvailable(Math.toIntExact(info.available / 1024 / 1024));
            storagePool.setAllocation(Math.toIntExact(info.allocation / 1024 / 1024));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storagePool;
    }

    public VirtualMachine getVirtualMachine(String name) {
        VirtualMachine virtualMachine = new VirtualMachine();
        try {
            Connect connect = getConnect();
            Domain domain = connect.domainLookupByName(name);
            DomainInfo info = domain.getInfo();
            StorageVol vol = connect.storageVolLookupByPath(getStorageVolDirPath() + domain.getName() + ".qcow2");

            // 读取xml文档
            String xmlDesc = domain.getXMLDesc(0);
            Document document = DocumentHelper.parseText(xmlDesc);
            Element root = document.getRootElement();
            Attribute address = root.element("devices").element("interface").element("mac").attribute("address");
            Attribute vncPort = root.element("devices").element("graphics").attribute("port");

            virtualMachine.setName(domain.getName());
            virtualMachine.setId(domain.getID());
            virtualMachine.setUuid(domain.getUUIDString());
            virtualMachine.setState(String.valueOf(info.state));
            virtualMachine.setMaxMemory(Math.toIntExact(info.maxMem / 1024));
            virtualMachine.setMemory(Math.toIntExact(info.memory / 1024));
            virtualMachine.setCpuNum(info.nrVirtCpu);
            virtualMachine.setCpuTime((int) (info.cpuTime / 1000000));
            virtualMachine.setCapacity(Math.toIntExact(vol.getInfo().capacity / 1024 / 1024));
            virtualMachine.setVncPort(Integer.valueOf(vncPort.getText()));
            virtualMachine.setWebPort(10000 + Integer.valueOf(vncPort.getText()));
            //埋下一个坑（以后获取）
            virtualMachine.setIp(null);
            virtualMachine.setMac(address.getText());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return virtualMachine;
    }

    public Net getNet(String name) {
        Net net = new Net();
        try {
            NetInterfaceConfig ifconfig = getSigar().getNetInterfaceConfig(name);
            net.setId(Math.toIntExact(ifconfig.getFlags()));
            net.setName(name);
            net.setType(ifconfig.getType());
            net.setState("alive");
            net.setIp(ifconfig.getAddress());
            net.setNetmask(ifconfig.getNetmask());
            net.setBroadcast(ifconfig.getBroadcast());
            net.setMac(ifconfig.getHwaddr());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return net;
    }

    /**
     * @param webPort 开启的web端口号
     * @param vncPort 开启的vnc端口号
     * @return 是否开启
     */
    public Boolean openWebPort(Integer webPort, Integer vncPort) {
        // 查看端口是否被占用
//        Boolean flag = false;
//        try {
//            InetAddress theAddress = InetAddress.getByName("localhost");
//            Socket socket = new Socket(theAddress, webPort);
//            socket.close();
//            flag = true;
//        } catch (Exception e) {
//            System.out.println("该端口已被占用");
//        }

        try {
            String[] cmd = new String[]{getNovncDirPath() + "utils/websockify/websockify.py",
                    "--web",
                    getNovncDirPath(),
                    String.valueOf(webPort),
                    "localhost:" + vncPort};
            Process ps = Runtime.getRuntime().exec(cmd);
            System.out.println(ps);
            return ps.isAlive();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
