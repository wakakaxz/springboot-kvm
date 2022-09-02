package cn.xfufu.kvm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xz
 * 网络配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Net {
    /*网络设备信息*/
    private Integer id;
    private String name; //设备名
    private String type; //设备类型
    private String state; // 状态
    private String ip; // ip
    private String netmask; // 子网掩码
    private String broadcast; //广播地址
    private String mac; //mac地址
}
