package cn.xfufu.kvm.controller;


import cn.xfufu.kvm.entity.User;
import cn.xfufu.kvm.service.LibvirtServiceImpl;
import cn.xfufu.kvm.service.VmServiceImpl;
import cn.xfufu.kvm.vo.VmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 虚拟机操作
 */
@Controller
public class VmController {

    @Autowired
    private VmServiceImpl vmService;

    @Autowired
    private LibvirtServiceImpl libvirtService;

    @RequestMapping("/getMachines")
    @ResponseBody
    public ModelMap getMachines(HttpSession session) {
        ModelMap map = new ModelMap();
        Integer userId = ((User) session.getAttribute("user")).getId();

        map.addAttribute("status", 0);
        map.addAttribute("vmVos", vmService.getVmVoListByUserId(userId));
        return map;
    }

    @RequestMapping("/getMachineInfo")
    public ModelAndView getMachineInfo(Integer id) {
        ModelAndView map = new ModelAndView();
        map.addObject("machine", vmService.getVmVoById(id));
        map.setViewName("info");
        return map;
    }

    @RequestMapping("/startMachine")
    @ResponseBody
    public Boolean startMachine(Integer id) {
        Integer webPort = vmService.startVirtualMachine(id);
        return webPort > 5900;
    }

    @RequestMapping("/startMachineArray")
    @ResponseBody
    public Boolean startMachine(Integer[] ids) {
        for (Integer id : ids) {
            Integer webPort = vmService.startVirtualMachine(id);
            if (webPort < 5900) {
                return false;
            }
        }
        return true;
    }

    @RequestMapping("/stopMachineArray")
    @ResponseBody
    public Boolean stopMachine(Integer[] ids) {
        for (Integer id : ids) {
            if (!vmService.shutdownVirtualMachine(id)) {
                return false;
            }
        }
        return true;
    }

    @RequestMapping("/stopMachine")
    @ResponseBody
    public Boolean stopMachine(Integer id) {
        return vmService.shutdownVirtualMachine(id);
    }

    @RequestMapping("/pauseMachine")
    @ResponseBody
    public Boolean pauseMachine(Integer id) {
        return vmService.suspendVirtualMachine(id);
    }

    @RequestMapping("/resumeMachine")
    @ResponseBody
    public Boolean resumeMachine(Integer id) {
        return vmService.resumeVirtualMachine(id);
    }


    @RequestMapping("/createMachine")
    @ResponseBody
    public Boolean createMachine(HttpSession session, String name, String osType, Integer vcpuNum, Integer memory, Integer diskSize) {
        String os;
        if ("CentOS".equals(osType)) {
            os = "centos7.0-clone";
        } else if ("Ubuntu".equals(osType)) {
            os = "lubuntu16gui-clone";
        } else {
            return false;
        }
        Integer userId = ((User) session.getAttribute("user")).getId();
        return vmService.cloneVirtualMachine(name, userId, os, vcpuNum, memory * 1024, diskSize * 1024);
    }

    @RequestMapping("/deleteMachine")
    @ResponseBody
    public Boolean deleteMachine(Integer id) {
        return vmService.deleteVirtualMachine(id);
    }

    @RequestMapping("/deleteMachineArray")
    @ResponseBody
    public Boolean deleteMachine(Integer[] ids) {
        for (Integer id : ids) {
            if (!vmService.deleteVirtualMachine(id)) {
                return false;
            }
        }
        return true;
    }

    @RequestMapping("/createSnapshot")
    @ResponseBody
    public Boolean createSnapshot(Integer vmId) {
        return false;
    }

    @RequestMapping("/recoverSnapshot")
    @ResponseBody
    public Boolean recoverSnapshot(Integer snapshotId) {
        return false;
    }

    @RequestMapping("/deleteSnapshot")
    @ResponseBody
    public Boolean deleteSnapshot(Integer snapshotId) {
        return false;
    }
}
