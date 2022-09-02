package cn.xfufu.kvm.service;

import cn.xfufu.kvm.entity.VirtualMachine;
import cn.xfufu.kvm.entity.Vm;
import cn.xfufu.kvm.repository.UserRepository;
import cn.xfufu.kvm.repository.VmRepository;
import cn.xfufu.kvm.util.LibvirtKit;
import cn.xfufu.kvm.util.RandomKit;
import cn.xfufu.kvm.vo.VmVo;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.StorageVol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xz
 * 虚拟机操作， 包含对宿主机操作
 */
@Service
public class VmServiceImpl {

    @Autowired
    private LibvirtKit libvirtKit;

    @Autowired
    private RandomKit randomKit;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VmRepository vmRepository;

//    /**
//     * @return 全部虚拟机信息列表
//     */
//    public List<VirtualMachine> getVirtualMachineList() {
//        List<VirtualMachine> virtualMachineList = new ArrayList<>();
//        try {
//            Connect connect = libvirtKit.getConnect();
//            // 已定义但未运行的虚拟机列表
//            String[] domainArray = connect.listDefinedDomains();
//            for (String name : domainArray) {
//                VirtualMachine machine = libvirtKit.getVirtualMachine(name);
//                virtualMachineList.add(machine);
//            }
//
//            // 正在运行的虚拟机列表
//            int[] ints = connect.listDomains();
//            for (int i : ints) {
//                Domain domain = connect.domainLookupByID(i);
//                VirtualMachine machine = libvirtKit.getVirtualMachine(domain.getName());
//                virtualMachineList.add(machine);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return virtualMachineList;
//    }


    /**
     * @return 虚拟机数据列表根据userId查询
     */
    public List<VmVo> getVmVoListByUserId(Integer userId) {
        List<VmVo> vmVoList = new ArrayList<>();

        List<Vm> list = vmRepository.findAllByUserId(userId);

        list.forEach(item -> vmVoList.add(getVmVoById(item.getId())));
        return vmVoList;
    }


    /**
     * @return 虚拟机视图显示数据列表
     */
    public List<VmVo> getVmVoList() {
        List<VmVo> vmVoList = new ArrayList<>();

        List<Vm> vmList = new ArrayList<>();
        vmRepository.findAll().forEach(item -> vmList.add(item));

        for (Vm vm : vmList) {
            vmVoList.add(getVmVoById(vm.getId()));
        }
        return vmVoList;
    }

    /**
     * @param id 虚拟机id
     * @return 虚拟机视图显示数据
     */
    public VmVo getVmVoById(Integer id) {
        VmVo vo = new VmVo();
        Vm vm = vmRepository.findById(id).get();
        VirtualMachine virtualMachine = libvirtKit.getVirtualMachine(String.valueOf(id));
        vo.setId(vm.getId());
        vo.setUserId(vm.getUserId());
        vo.setName(vm.getName());
        vo.setOsType(vm.getOsType());
        vo.setUuid(virtualMachine.getUuid());
        vo.setState(virtualMachine.getState());
        vo.setMaxMemory(virtualMachine.getMaxMemory());
        vo.setMemory(virtualMachine.getMemory());
        vo.setCpuNum(virtualMachine.getCpuNum());
        vo.setCpuTime(virtualMachine.getCpuTime());
        vo.setCapacity(virtualMachine.getCapacity());
        vo.setVncPort(virtualMachine.getVncPort());
        vo.setWebPort(virtualMachine.getWebPort());
        vo.setIp(virtualMachine.getIp());
        vo.setMac(virtualMachine.getMac());
        return vo;
    }


    /**
     * @param id 虚拟机id
     *           开启虚拟机, 返回一个Web端口， -1 为打开虚拟机错误， -2 为打开端口错误
     */
    public Integer startVirtualMachine(Integer id) {
        try {
            Domain domain = libvirtKit.getConnect().domainLookupByName(String.valueOf(id));
            VirtualMachine machine = libvirtKit.getVirtualMachine(String.valueOf(id));
            //开启虚拟机
            domain.create();
            //打开端口
            if (libvirtKit.openWebPort(machine.getWebPort(), machine.getVncPort())) {
                return machine.getWebPort();
            } else {
                return -2;
            }
        } catch (LibvirtException e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * @param id 虚拟机id
     *           挂起虚拟机
     */
    public Boolean suspendVirtualMachine(Integer id) {
        try {
            Domain domain = libvirtKit.getConnect().domainLookupByName(String.valueOf(id));
            domain.suspend();
            return true;
        } catch (LibvirtException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param id 虚拟机id
     *           还原虚拟机
     */
    public Boolean resumeVirtualMachine(Integer id) {
        try {
            Domain domain = libvirtKit.getConnect().domainLookupByName(String.valueOf(id));
            domain.resume();
            return true;
        } catch (LibvirtException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param id 虚拟机id
     *           关闭虚拟机
     */
    public Boolean shutdownVirtualMachine(Integer id) {
        try {
            Domain domain = libvirtKit.getConnect().domainLookupByName(String.valueOf(id));
            domain.shutdown();
            domain.destroy();
            return true;
        } catch (LibvirtException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param id 虚拟机id
     *           删除虚拟机
     */
    public Boolean deleteVirtualMachine(Integer id) {
        try {
            Connect connect = libvirtKit.getConnect();
            Domain domain = connect.domainLookupByName(String.valueOf(id));
            if ("VIR_DOMAIN_SHUTOFF".equals(String.valueOf(domain.getInfo().state))) {
                domain.undefine();
                StorageVol vol = connect.storageVolLookupByPath(libvirtKit.getStorageVolDirPath() + id + ".qcow2");
                vol.delete(0);

            } else {
                domain.undefine();
                domain.destroy();
                StorageVol vol = connect.storageVolLookupByPath(libvirtKit.getStorageVolDirPath() + id + ".qcow2");
                vol.delete(0);
            }
            return true;
        } catch (LibvirtException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param name 想要克隆后的虚拟机名称
     *             克隆一台虚拟机, 因为速度较快， 故可以代替创建虚拟机， 其中vnc端口可以从数据库获取
     */
    public Boolean cloneVirtualMachine(String name, Integer userId, String osType, Integer vcpuNum, Integer memory, Integer diskSize) {
        Connect connect = libvirtKit.getConnect();
        Vm vm = new Vm();
        vm.setName(name);
        vm.setUserId(userId);
        vm.setOsType(osType);
        Vm save = vmRepository.save(vm);
        Integer id = save.getId();
        try {
            String xmlDesc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "\n" +
                    "<volume type='file'>\n" +
                    "    <name>" + id + ".qcow2</name>\n" +
                    "    <source>\n" +
                    "    </source>\n" +
                    "    <capacity unit='GiB'>" + (diskSize / 1024) + "</capacity>\n" +
                    "    <allocation>0</allocation>\n" +
                    "    <target>\n" +
                    "        <path>" + libvirtKit.getStorageVolDirPath() + id + ".qcow2</path>\n" +
                    "        <format type='qcow2'/>\n" +
                    "        <permissions>\n" +
                    "            <mode>0600</mode>\n" +
                    "            <owner>0</owner>\n" +
                    "            <group>0</group>\n" +
                    "        </permissions>\n" +
                    "    </target>\n" +
                    "</volume>\n";
            // 克隆虚拟磁盘
            StorageVol volClone = connect.storagePoolLookupByName("default").storageVolLookupByName(osType + ".qcow2");
            StorageVol volCreateXMLFrom = connect.storagePoolLookupByName("default").storageVolCreateXMLFrom(xmlDesc, volClone, 0);

            String xml = "<domain type='kvm'>\n" +
                    "  <name>" + id + "</name>\n" +
                    "  <uuid>" + randomKit.randomUUID() + "</uuid>\n" +
                    "  <memory unit='KiB'>" + memory * 1024 + "</memory>\n" +
                    "  <currentMemory unit='KiB'>" + memory * 1024 + "</currentMemory>\n" +
                    "  <vcpu placement='static'>" + vcpuNum + "</vcpu>\n" +
                    "  <os>\n" +
                    "    <type arch='x86_64' machine='pc-i440fx-xenial'>hvm</type>\n" +
                    "    <boot dev='hd'/>\n" +
                    "  </os>\n" +
                    "  <features>\n" +
                    "    <acpi/>\n" +
                    "    <apic/>\n" +
                    "  </features>\n" +
                    "  <cpu mode='custom' match='exact'>\n" +
                    "    <model fallback='allow'>Broadwell-noTSX-IBRS</model>\n" +
                    "  </cpu>\n" +
                    "  <clock offset='utc'>\n" +
                    "    <timer name='rtc' tickpolicy='catchup'/>\n" +
                    "    <timer name='pit' tickpolicy='delay'/>\n" +
                    "    <timer name='hpet' present='no'/>\n" +
                    "  </clock>\n" +
                    "  <on_poweroff>destroy</on_poweroff>\n" +
                    "  <on_reboot>restart</on_reboot>\n" +
                    "  <on_crash>restart</on_crash>\n" +
                    "  <pm>\n" +
                    "    <suspend-to-mem enabled='no'/>\n" +
                    "    <suspend-to-disk enabled='no'/>\n" +
                    "  </pm>\n" +
                    "  <devices>\n" +
                    "    <emulator>/usr/bin/kvm-spice</emulator>\n" +
                    "    <disk type='file' device='disk'>\n" +
                    "      <driver name='qemu' type='qcow2'/>\n" +
                    "      <source file='" + libvirtKit.getStorageVolDirPath() + id + ".qcow2'/>\n" +
                    "      <target dev='hda' bus='ide'/>\n" +
                    "    </disk>\n" +
                    "    <controller type='usb' index='0' model='ich9-ehci1'>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x06' function='0x7'/>\n" +
                    "    </controller>\n" +
                    "    <controller type='usb' index='0' model='ich9-uhci1'>\n" +
                    "      <master startport='0'/>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x06' function='0x0' multifunction='on'/>\n" +
                    "    </controller>\n" +
                    "    <controller type='usb' index='0' model='ich9-uhci2'>\n" +
                    "      <master startport='2'/>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x06' function='0x1'/>\n" +
                    "    </controller>\n" +
                    "    <controller type='usb' index='0' model='ich9-uhci3'>\n" +
                    "      <master startport='4'/>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x06' function='0x2'/>\n" +
                    "    </controller>\n" +
                    "    <controller type='pci' index='0' model='pci-root'/>\n" +
                    "    <controller type='ide' index='0'>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x01' function='0x1'/>\n" +
                    "    </controller>\n" +
                    "    <controller type='virtio-serial' index='0'>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x05' function='0x0'/>\n" +
                    "    </controller>\n" +
                    "    <interface type='bridge'>\n" +
                    "      <mac address='" + randomKit.randomMac() + "'/>\n" +
                    "      <source bridge='br0'/>\n" +
                    "      <model type='virtio'/>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x03' function='0x0'/>\n" +
                    "    </interface>\n" +
                    "    <serial type='pty'>\n" +
                    "      <target port='0'/>\n" +
                    "    </serial>\n" +
                    "    <console type='pty'>\n" +
                    "      <target type='serial' port='0'/>\n" +
                    "    </console>\n" +
                    "    <channel type='unix'>\n" +
                    "      <source mode='bind'/>\n" +
                    "      <target type='virtio' name='org.qemu.guest_agent.0'/>\n" +
                    "      <address type='virtio-serial' controller='0' bus='0' port='1'/>\n" +
                    "    </channel>\n" +
                    "    <channel type='spicevmc'>\n" +
                    "      <target type='virtio' name='com.redhat.spice.0'/>\n" +
                    "      <address type='virtio-serial' controller='0' bus='0' port='2'/>\n" +
                    "    </channel>\n" +
                    "    <input type='tablet' bus='usb'/>\n" +
                    "    <input type='mouse' bus='ps2'/>\n" +
                    "    <input type='keyboard' bus='ps2'/>\n" +
                    "    <graphics type='vnc' port='" + (5900 + id) + "' listen='0.0.0.0'>\n" +
                    "      <listen type='address' address='0.0.0.0'/>\n" +
                    "    </graphics>\n" +
                    "    <sound model='ich6'>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x04' function='0x0'/>\n" +
                    "    </sound>\n" +
                    "    <video>\n" +
                    "      <model type='qxl' ram='65536' vram='65536' vgamem='16384' heads='1'/>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x02' function='0x0'/>\n" +
                    "    </video>\n" +
                    "    <redirdev bus='usb' type='spicevmc'>\n" +
                    "    </redirdev>\n" +
                    "    <redirdev bus='usb' type='spicevmc'>\n" +
                    "    </redirdev>\n" +
                    "    <memballoon model='virtio'>\n" +
                    "      <address type='pci' domain='0x0000' bus='0x00' slot='0x08' function='0x0'/>\n" +
                    "    </memballoon>\n" +
                    "  </devices>\n" +
                    "</domain>\n";
            Domain domain = connect.domainDefineXML(xml);
            domain.setAutostart(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param id   虚拟机id
     * @param name 虚拟机要修改的名称
     * @return
     */
    public Boolean updateVm(Integer id, String name) {
        if (vmRepository.existsById(id)) {
            Vm vm = vmRepository.findById(id).get();
            vm.setName(name);
            vmRepository.save(vm);
            return true;
        }
        return false;
    }

}
