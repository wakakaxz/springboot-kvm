package cn.xfufu.kvm.repository;

import cn.xfufu.kvm.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    User findByEmail(String email);
    List<User> findAllByFlag(Integer flag);
}
