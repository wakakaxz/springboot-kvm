package cn.xfufu.kvm.entity;

import cn.xfufu.kvm.util.LibvirtKit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.libvirt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xz
 * 虚拟机信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirtualMachine {

    private String name; // 唯一确定
    private Integer id; // 非运行态为-1
    private String uuid; //唯一确定uuid
    private String state; // 状态

    private Integer maxMemory;
    private Integer memory;

    private Integer cpuNum; //cpu数目
    private Integer cpuTime; //cpu运行时间

    private Integer capacity; // 硬盘容量

    private Integer vncPort; // vnc端口
    private Integer webPort; // web端口
    private Integer ip; // ip 暂存，
    private String mac; //mac地址

}
