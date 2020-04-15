package com.plansolve.farm.controller.client.main.farmer;

import com.plansolve.farm.controller.client.BaseController;
import com.plansolve.farm.exception.PermissionException;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.client.user.FarmlandDTO;
import com.plansolve.farm.model.client.order.GuideDTO;
import com.plansolve.farm.model.client.order.OrderDTO;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.DictService;
import com.plansolve.farm.service.client.FarmlandService;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.ResultUtil;
import com.plansolve.farm.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/8
 * @Description: 土地管理
 **/
@RestController
@RequestMapping(value = "/farm/user/farmer/farmland")
public class FarmlandController extends BaseController {

    @Autowired
    private FarmlandService farmlandService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private DictService dictService;

    /**
     * 为用户添加农田信息
     *
     * @param farmlandDTO    农田信息
     * @param addressDTO     农田地址信息
     * @param pictures       农田相关图片
     * @param farmlandDetail 农田备注
     * @param addressDetail  地址详情
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/insert")
    public Result farmlandInsert(@Valid FarmlandDTO farmlandDTO,
                                 @Valid AddressDTO addressDTO,
                                 @RequestParam(value = "pics", required = false) List<MultipartFile> pictures,
                                 @RequestParam(required = false) String farmlandDetail,
                                 @RequestParam(required = false) String addressDetail) throws IOException {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        addressDTO.setDetail(addressDetail);
        farmlandDTO.setAddress(addressDTO);
        farmlandDTO.setDetail(farmlandDetail);
        FarmlandDTO savedFarmland = farmlandService.insert(farmlandDTO, pictures, user.getIdUser());

        // 变更用户身份
        if (user.getFarmer() == false) {
            userService.changeToBeFarmer(user.getIdUser(), true);
        }
        return ResultUtil.success(savedFarmland);
    }

    /**
     * 获取用户农田信息
     *
     * @return
     */
    @PostMapping(value = "/list")
    public Result farmlandList() {
        HttpSession session = AppHttpUtil.getSession();
        User user = (User) session.getAttribute(SysConstant.CURRENT_USER);
        List<FarmlandDTO> list = farmlandService.list(user.getIdUser());
        return ResultUtil.success(list);
    }

    /**
     * 用户修改农田信息
     *
     * @param farmlandDTO    农田信息
     * @param addressDTO     农田地址信息
     * @param pictures       农田相关图片
     * @param farmlandDetail 农田备注
     * @param addressDetail  地址详情
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/edit")
    public Result farmlandEdit(@Valid FarmlandDTO farmlandDTO,
                               @Valid AddressDTO addressDTO,
                               @RequestParam(value = "pics", required = false) List<MultipartFile> pictures,
                               @RequestParam(required = false) String farmlandDetail,
                               @RequestParam(required = false) String addressDetail) throws IOException {
        addressDTO.setDetail(addressDetail);
        farmlandDTO.setAddress(addressDTO);
        farmlandDTO.setDetail(farmlandDetail);
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        Farmland farmland = farmlandService.getFarmland(farmlandDTO.getFarmlandNo());
        if (farmland.getIdUser().equals(user.getIdUser())) {
            farmlandDTO = farmlandService.edit(farmlandDTO, pictures);
            return ResultUtil.success(farmlandDTO);
        } else {
            throw new PermissionException("[该用户无权操作此农田]");
        }
    }

    /**
     * 用户删除农田信息
     *
     * @param farmlandNo
     * @return
     */
    @PostMapping(value = "/delete")
    public Result farmlandDelete(String farmlandNo) {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        Farmland farmland = farmlandService.getFarmland(farmlandNo);
        if (farmland.getIdUser().equals(user.getIdUser())) {
            farmlandService.delete(farmlandNo);
            // 用户土地信息为空时，收回用户种植户身份
            List<FarmlandDTO> list = farmlandService.list(user.getIdUser());
            if (list == null || list.size() == 0) {
                userService.changeToBeFarmer(user.getIdUser(), false);
            }
            return ResultUtil.success(null);
        } else {
            throw new PermissionException("[该用户无权操作此农田]");
        }
    }

    /**
     * 获取农作物类型
     *
     * @return
     */
    @PostMapping(value = "/crop")
    public Result crop() {
        String[] crops = dictService.getValue(SysConstant.CROP).split(SysConstant.FARM_STRING_SEPARATOR);
        return ResultUtil.success(crops);
    }

    /**
     * @param farmlandDTO    农田信息
     * @param addressDTO     农田地址信息
     * @param pictures       农田相关图片
     * @param farmlandDetail 农田备注
     * @param addressDetail  地址详情
     * @param orderDTO       订单信息
     * @param guideDTO       领路人信息
     * @param mobile         下单到“用户”时，用户手机号码
     * @return 订单详情
     * @throws IOException
     */
    @PostMapping(value = "/insertAndOrder")
    public Result farmlandInsertAndOrder(@Valid FarmlandDTO farmlandDTO,
                                         @Valid AddressDTO addressDTO,
                                         @Valid OrderDTO orderDTO,
                                         @Valid GuideDTO guideDTO,
                                         @RequestParam(value = "pics", required = false) List<MultipartFile> pictures,
                                         @RequestParam(required = false) String farmlandDetail,
                                         @RequestParam(required = false) String addressDetail,
                                         @RequestParam(required = false) String mobile) throws IOException {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        if (UserUtil.checkUserState(user)) {
            Result result = farmlandInsert(farmlandDTO, addressDTO, pictures, farmlandDetail, addressDetail);
            farmlandDTO = (FarmlandDTO) result.getData();
            orderDTO.setGuideDTO(guideDTO);
            UserOrder order = orderService.createOrder(orderDTO, farmlandDTO.getFarmlandNo(), user, mobile);
            orderDTO = orderService.loadDTO(order);
            return ResultUtil.success(orderDTO);
        }
        return null;
    }

}
