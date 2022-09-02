package cn.xfufu.kvm.service;

import cn.xfufu.kvm.entity.User;
import cn.xfufu.kvm.repository.UserRepository;
import cn.xfufu.kvm.util.Md5Kit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xz
 * user
 */
@Service
public class UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Md5Kit md5Kit;

    /**
     * @param id
     * @return 按id查找
     */
    public User findUserById(Integer id) {
        return userRepository.findById(id).get();
    }

    /**
     * @param email
     * @return 根据邮箱查询
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * @param user
     * @return 保存用户
     */
    public Boolean saveUser(User user) {
        User email = userRepository.findByEmail(user.getEmail());
        if (email != null) {
            return false;
        }
        user.setPassword(md5Kit.toMd5(user.getPassword()));
        User save = userRepository.save(user);
        return save == null ? false : true;
    }

    /**
     * @param id
     * @return 根据ID删除
     */
    public Boolean deleteUserById(Integer id) {
        User user = userRepository.findById(id).get();
        userRepository.delete(user);
        User user1 = userRepository.findById(id).get();
        return user1 == null ? true : false;
    }

    /**
     * @param user
     * @return 更新
     */
    public Boolean updateUser(User user) {
        if (userRepository.existsById(user.getId()) == false) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    /**
     * @return 查询全部用户
     */
    public List<User> findUserAll() {
        List<User> list = new ArrayList<>();
        userRepository.findAll().forEach(item -> list.add(item));
        return list;
    }


    /**
     * @return 查询全部普通用户
     */
    public List<User> findUserAllByFlag(Integer flag) {
        List<User> list = new ArrayList<>();
        userRepository.findAllByFlag(flag).forEach(item -> list.add(item));
        return list;
    }

}
