package com.plansolve.farm.controller.client.main.operator;

import com.plansolve.farm.controller.client.BaseController;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.user.MachineryDTO;
import com.plansolve.farm.model.database.Machinery;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.DictService;
import com.plansolve.farm.service.client.MachineryService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/11
 * @Description:
 **/
@RestController
@RequestMapping(value = "/farm/user/operator/machinery")
public class MachineryController extends BaseController {

    @Autowired
    private MachineryService machineryService;
    @Autowired
    private UserService userService;
    @Autowired
    private DictService dictService;

    /**
     * 保存农机信息
     *
     * @param machineryDTO
     * @param pictures
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/insert")
    public Result machineryInsert(@Valid MachineryDTO machineryDTO, @RequestParam(value = "pics", required = false) List<MultipartFile> pictures) throws IOException {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        MachineryDTO savedMachinery = machineryService.insert(machineryDTO, pictures, user.getIdUser());

        // 变更用户身份
        if (user.getOperator() == false) {
            userService.changeToBeOperator(user.getIdUser(), true);
        }
        return ResultUtil.success(savedMachinery);
    }

    /**
     * 获取用户农机信息
     *
     * @return
     */
    @PostMapping(value = "/list")
    public Result machineryList() {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);

        List<Machinery> list = machineryService.list(user.getIdUser());
        if (list != null && list.size() > 0) {
            List<MachineryDTO> machineryDTOs = new ArrayList<>();
            for (Machinery machinery : list) {
                MachineryDTO machineryDTO = machineryService.loadDTO(machinery);
                machineryDTOs.add(machineryDTO);
            }
            return ResultUtil.success(machineryDTOs);
        } else {
            return ResultUtil.success(null);
        }
    }

    /**
     * 用户修改农机信息
     *
     * @param machineryDTO
     * @param pictures
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/edit")
    public Result machineryEdit(@Valid MachineryDTO machineryDTO, @RequestParam(value = "pics", required = false) List<MultipartFile> pictures) throws IOException {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        Machinery machinery = machineryService.findByMachineryNo(machineryDTO.getMachineryNo());
        if (machinery.getIdUser().equals(user.getIdUser())) {
            machineryDTO = machineryService.edit(machineryDTO, pictures);
            return ResultUtil.success(machineryDTO);
        } else {
            throw new PermissionException("[该用户无权操作此农机]");
        }
    }

    /**
     * 用户删除农机信息
     *
     * @param machineryNo
     * @return
     */
    @PostMapping(value = "/delete")
    public Result machineryDelete(String machineryNo) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        Machinery machinery = machineryService.findByMachineryNo(machineryNo);
        if (machinery.getIdUser().equals(user.getIdUser())) {
            machineryService.delete(machineryNo);
            // 用户土地信息为空时，收回用户农机手身份
            List<Machinery> list = machineryService.list(user.getIdUser());
            if (list == null || list.size() == 0) {
                userService.changeToBeOperator(user.getIdUser(), false);
            }
            return ResultUtil.success(null);
        } else {
            throw new PermissionException("[该用户无权操作此农机]");
        }
    }

    /**
     * 获取农机类型
     *
     * @return
     */
    @PostMapping(value = "/type")
    public Result getMachineryType() {
        String[] types = dictService.getValue(SysConstant.MACHINERY_TYPE).split(SysConstant.MACHINERY_STRING_SEPARATOR);
        return ResultUtil.success(types);
    }

    /**
     * 获取农机拖拽装置
     *
     * @return
     */
    @PostMapping(value = "/dragging")
    public Result getMachineryDragging() {
        String[] dragging = dictService.getValue(SysConstant.MACHINERY_DRAGGING).split(SysConstant.MACHINERY_STRING_SEPARATOR);
        return ResultUtil.success(dragging);
    }

}
