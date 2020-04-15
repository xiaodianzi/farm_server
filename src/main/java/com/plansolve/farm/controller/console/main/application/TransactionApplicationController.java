package com.plansolve.farm.controller.console.main.application;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.model.console.account.TransactionApplicationDTO;
import com.plansolve.farm.model.console.account.WithdrawApplicationDetailDTO;
import com.plansolve.farm.model.database.account.ApplicationDetail;
import com.plansolve.farm.model.database.account.TransactionApplication;
import com.plansolve.farm.model.database.console.AdminUser;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.enums.type.TransactionApplicationTypeEnum;
import com.plansolve.farm.repository.account.ApplicationDetailRepository;
import com.plansolve.farm.service.base.order.UserOrderBaseSelectService;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.service.client.TransactionApplicationService;
import com.plansolve.farm.service.console.AdminUserService;
import com.plansolve.farm.service.console.ConsoleTransactionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/12
 * @Description:
 **/

@Controller
@RequestMapping(value = "/manger/application")
public class TransactionApplicationController extends BaseController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private ConsoleTransactionApplicationService applicationService;
    @Autowired
    private TransactionApplicationService transactionApplicationService;
    @Autowired
    private UserOrderBaseSelectService orderBaseSelectService;

    @Autowired
    private ApplicationDetailRepository applicationDetailRepository;

    /**************************************************************提现**************************************************************/
    /**
     * 跳转申请列表页
     *
     * @return
     */
    @GetMapping(value = "/withdraw/listPage")
    public String withdrawListPage() {
        return "console/application/withdraw/list";
    }

    /**
     * 获取分页对象
     *
     * @param limit
     * @param offset
     * @param applicationState
     * @param transactionApplicationNo
     * @return
     */
    @GetMapping(value = "/withdraw/list")
    @ResponseBody
    public PageDTO<TransactionApplicationDTO> withdrawList(@RequestParam(defaultValue = "10") Integer limit,
                                                           @RequestParam(defaultValue = "0") Integer offset,
                                                           @RequestParam(required = false) String applicationState,
                                                           @RequestParam(required = false) String transactionApplicationNo) {
        Integer page = getPage(limit, offset);
        Page<TransactionApplication> applicationPage = applicationService.findAllApplication(TransactionApplicationTypeEnum.WITHDRAW.getType(), applicationState, transactionApplicationNo, page, limit);
        List<TransactionApplicationDTO> applicationDTOS = applicationService.loadDTOS(applicationPage.getContent());
        PageDTO<TransactionApplicationDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(applicationPage.getTotalElements());
        pageDTO.setRows(applicationDTOS);
        return pageDTO;
    }

    /**
     * 提现订单复核
     *
     * @param idTransactionApplication 申请id
     * @param result                   申请结果
     * @param detail                   原因
     * @return
     */
    @RequestMapping("/withdraw/recheck")
    public String withdrawRecheck(Long idTransactionApplication, Boolean result, String detail, Model model) {
        //平台管理员
        AdminUser adminUser = adminUserService.findUser(1);
        //申请详情
        if (null != idTransactionApplication && null != result) {
            applicationService.withdrawRecheck(idTransactionApplication, adminUser, result, detail);
            List<ApplicationDetail> applicationDetails = applicationDetailRepository.findByIdTransactionApplication(idTransactionApplication);
            List<WithdrawApplicationDetailDTO> withdrawApplicationDetailDTOList = applicationService.loadWithdrawDTO(applicationDetails);
            if (withdrawApplicationDetailDTOList.size() > 0) {
                model.addAttribute("applicationDetails", withdrawApplicationDetailDTOList);
            }
        } else {
            throw new ParamErrorException("");
        }
        //主申请
        TransactionApplication transactionApplication = applicationService.findApplicationById(idTransactionApplication);
        if (null != transactionApplication) {
            TransactionApplicationDTO transactionApplicationDTO = applicationService.loadDTO(transactionApplication);
            model.addAttribute("transactionApplication", transactionApplicationDTO);
        }
        return "redirect:/manger/application/detail?idTransactionApplication=" + idTransactionApplication;
    }
    /**************************************************************提现**************************************************************/
    /**************************************************************支付**************************************************************/
    /**
     * 跳转申请列表页
     *
     * @return
     */
    @GetMapping(value = "/payment/listPage")
    public String paymentListPage() {
        return "console/application/pay/list";
    }

    /**
     * 获取分页对象
     *
     * @param limit
     * @param offset
     * @param applicationState
     * @param transactionApplicationNo
     * @return
     */
    @GetMapping(value = "/payment/list")
    @ResponseBody
    public PageDTO<TransactionApplicationDTO> paymentList(@RequestParam(defaultValue = "10") Integer limit,
                                                          @RequestParam(defaultValue = "0") Integer offset,
                                                          @RequestParam(required = false) String applicationState,
                                                          @RequestParam(required = false) String transactionApplicationNo) {
        Integer page = getPage(limit, offset);
        Page<TransactionApplication> applicationPage = applicationService.findAllApplication(TransactionApplicationTypeEnum.PAYMENT.getType(), applicationState, transactionApplicationNo, page, limit);
        List<TransactionApplicationDTO> applicationDTOS = applicationService.loadDTOS(applicationPage.getContent());
        PageDTO<TransactionApplicationDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(applicationPage.getTotalElements());
        pageDTO.setRows(applicationDTOS);
        return pageDTO;
    }
    /**************************************************************支付**************************************************************/
    /**************************************************************微信支付**************************************************************/
    /**
     * 跳转申请列表页
     *
     * @return
     */
    @GetMapping(value = "/wxpay/listPage")
    public String wxpayListPage() {
        return "console/application/wxpay/list";
    }

    /**
     * 获取分页对象
     *
     * @param limit
     * @param offset
     * @param applicationState
     * @param transactionApplicationNo
     * @return
     */
    @GetMapping(value = "/wxpay/list")
    @ResponseBody
    public PageDTO<TransactionApplicationDTO> wxpayList(@RequestParam(defaultValue = "10") Integer limit,
                                                        @RequestParam(defaultValue = "0") Integer offset,
                                                        @RequestParam(required = false) String applicationState,
                                                        @RequestParam(required = false) String transactionApplicationNo) {
        Integer page = getPage(limit, offset);
        Page<TransactionApplication> applicationPage = applicationService.findAllApplication(TransactionApplicationTypeEnum.WX_PAYMENT.getType(), applicationState, transactionApplicationNo, page, limit);
        List<TransactionApplicationDTO> applicationDTOS = applicationService.loadDTOS(applicationPage.getContent());
        PageDTO<TransactionApplicationDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(applicationPage.getTotalElements());
        pageDTO.setRows(applicationDTOS);
        return pageDTO;
    }

    /**
     * 手动审核支付情况
     *
     * @param idTransactionApplication
     * @return
     */
    @GetMapping(value = "/wxpay/check")
    public String check(Long idTransactionApplication) {
        TransactionApplication transactionApplication = applicationService.findApplicationById(idTransactionApplication);
        UserOrder userOrder = orderBaseSelectService.getByUserOrder(transactionApplication.getIdUserOrder());
        ApplicationDetail applicationDetail = transactionApplicationService.paymentCheck(userOrder, TransactionApplicationTypeEnum.WX_PAYMENT.getType(), userOrder.getUserOrderNo());
        if (applicationDetail.getApplicationResult()) {
            transactionApplicationService.paymentRecheck(userOrder, TransactionApplicationTypeEnum.WX_PAYMENT.getType());
        }
        return "redirect:/manger/application/detail?idTransactionApplication=" + idTransactionApplication;
    }
    /**************************************************************微信支付**************************************************************/

    /**
     * 跳转申请列表页
     *
     * @return
     */
    @GetMapping(value = "/listPage")
    public String listPage() {
        return "console/application/list";
    }

    /**
     * 获取分页对象
     *
     * @param limit
     * @param offset
     * @param applicationState
     * @param transactionApplicationNo
     * @return
     */
    @GetMapping(value = "/list")
    @ResponseBody
    public PageDTO<TransactionApplicationDTO> list(@RequestParam(defaultValue = "10") Integer limit,
                                                   @RequestParam(defaultValue = "0") Integer offset,
                                                   @RequestParam(required = false) String applicationType,
                                                   @RequestParam(required = false) String applicationState,
                                                   @RequestParam(required = false) String transactionApplicationNo) {
        Integer page = getPage(limit, offset);
        Page<TransactionApplication> applicationPage = applicationService.findAllApplication(applicationType, applicationState, transactionApplicationNo, page, limit);
        List<TransactionApplicationDTO> applicationDTOS = applicationService.loadDTOS(applicationPage.getContent());
        PageDTO<TransactionApplicationDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(applicationPage.getTotalElements());
        pageDTO.setRows(applicationDTOS);
        return pageDTO;
    }

    /**
     * 获取申请详情
     *
     * @param idTransactionApplication
     * @param model
     * @return
     */
    @GetMapping(value = "/detail")
    public String detail(Long idTransactionApplication, Model model) {
        //主申请
        TransactionApplication transactionApplication = applicationService.findApplicationById(idTransactionApplication);
        if (null != transactionApplication) {
            TransactionApplicationDTO transactionApplicationDTO = applicationService.loadDTO(transactionApplication);
            model.addAttribute("transactionApplication", transactionApplicationDTO);
        }
        //申请详情
        List<ApplicationDetail> applicationDetails = applicationDetailRepository.findByIdTransactionApplication(idTransactionApplication);
        List<WithdrawApplicationDetailDTO> withdrawApplicationDetailDTOList = applicationService.loadWithdrawDTO(applicationDetails);
        if (withdrawApplicationDetailDTOList.size() > 0) {
            model.addAttribute("applicationDetails", withdrawApplicationDetailDTOList);
        }
        if (transactionApplication.getApplicationType().equals(TransactionApplicationTypeEnum.WITHDRAW.getType())) {
            return "console/application/withdraw/detail";
        } else if (transactionApplication.getApplicationType().equals(TransactionApplicationTypeEnum.WX_PAYMENT.getType())) {
            return "console/application/wxpay/detail";
        } else if (transactionApplication.getApplicationType().equals(TransactionApplicationTypeEnum.PAYMENT.getType())) {
            return "console/application/pay/detail";
        } else {
            return "console/application/detail";
        }
    }

}
