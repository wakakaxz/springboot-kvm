package cn.xfufu.kvm.repository;

import cn.xfufu.kvm.entity.Vm;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VmRepository extends CrudRepository<Vm, Integer> {

    List<Vm> findAllByUserId(Integer userId);

}
