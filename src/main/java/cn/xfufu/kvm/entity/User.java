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
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "varchar(64) COMMENT '昵称'")
    private String username;

    @Column(nullable = false, columnDefinition = "varchar(64) COMMENT '邮箱Email'")
    private String email;

    @Column(nullable = false, columnDefinition = "varchar(64) COMMENT '密码'")
    private String password;

    @Column(nullable = false, columnDefinition = "int(4) COMMENT '标识, 用户为1， 管理员为0'")
    private Integer flag;

}
