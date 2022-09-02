package cn.xfufu.kvm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xz
 * 视图数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VmVo implements Serializable {

    private Integer id;
    private Integer userId;
    private String name;
    private String osType;

    private String uuid;
    private String state;

    private Integer maxMemory;
    private Integer memory;

    private Integer cpuNum; //cpu数目
    private Integer cpuTime; //cpu运行时间

    private Integer capacity; // 硬盘容量

    private Integer vncPort; // vnc端口
    private Integer webPort; // web端口
    private Integer ip; // ip 暂存
    private String mac; //mac地址

}
