package cn.xfufu.kvm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vm")
public class Vm implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "int(11) COMMENT '该虚拟机拥有人id'")
    private Integer userId;

    @Column(nullable = false, columnDefinition = "varchar(64) COMMENT '虚拟机名称， 前端显示, 实际名称和id进行关联'")
    private String name;

    @Column(nullable = false, columnDefinition = "varchar(64) COMMENT '虚拟机类型'")
    private String osType;

}
