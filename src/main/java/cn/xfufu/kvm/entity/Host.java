package cn.xfufu.kvm.entity;

import cn.xfufu.kvm.util.LibvirtKit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.libvirt.Connect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author xz
 * 宿主机信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Host implements Serializable {
    /**
     * 宿主机连接信息
     * */
    private String hostName;
    private String url;
    private String hyperVisor;
    private String virVersion;

    /**
     * 宿主机CPU信息
     * */
    private String cpuType;
    private Integer cpuMhz;
    private Integer cpuNum;
    /**
     * 宿主机内存信息
     * */
    private Integer totalMemory;
    private Integer usedMemory;
    private Integer freeMemory;
    private Double usedMemoryPercent;
}
