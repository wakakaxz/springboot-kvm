package cn.xfufu.kvm.controller;

import cn.xfufu.kvm.entity.Host;
import cn.xfufu.kvm.entity.Net;
import cn.xfufu.kvm.entity.StoragePool;
import cn.xfufu.kvm.entity.VirtualMachine;
import cn.xfufu.kvm.service.LibvirtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author 主机信息
 */
@Controller
public class LibvirtController {

    @Autowired
    public LibvirtServiceImpl libvirtService;

//    @RequestMapping("/getHost")
//    @ResponseBody
//    public Host getHost() {
//        return libvirtService.getHostInfo();
//    }
//
//    @RequestMapping("/getStoragePoolList")
//    @ResponseBody
//    public List<StoragePool> getStoragePoolList() {
//        return libvirtService.getStoragePoolList();
//    }
//
//    @RequestMapping("/getNetList")
//    @ResponseBody
//    public List<Net> getNetList() {
//        return libvirtService.getNetList();
//    }

    @RequestMapping("/hostInfo")
    public ModelAndView getHostInfo() {
        ModelAndView mav = new ModelAndView("qemuInfo");
        mav.addObject("qemuInfo", libvirtService.getHostInfo());
        mav.addObject("netInfo", libvirtService.getBriageNet());
        mav.addObject("capaInfo", libvirtService.getStoragePoolDefalut());
        return mav;
    }


}
