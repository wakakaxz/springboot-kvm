package cn.xfufu.kvm.entity;

import cn.xfufu.kvm.util.LibvirtKit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePoolInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author xz
 * 存储池信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoragePool implements Serializable {

    /**
     * 存储池信息
     */
    private String name; // 存储池名称
    private String state; // 存储池状态
    private Integer capacity; // 总容量
    private Integer available; // 可用
    private Integer allocation; // 已用
}
