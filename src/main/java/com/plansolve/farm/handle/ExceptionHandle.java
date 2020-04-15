package com.plansolve.farm.handle;

import cn.jiguang.common.resp.APIRequestException;
import com.plansolve.farm.exception.*;
import com.plansolve.farm.model.enums.code.ResultEnum;
import com.plansolve.farm.model.Result;
import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description:
 **/

@ControllerAdvice
public class ExceptionHandle {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {
        if (e instanceof UnsupportedEncodingException) {
            logger.error("【字符集异常】", e);
            e.printStackTrace();
            return UnsupportedEncodingExceptionHandle((UnsupportedEncodingException) e);
        } else {
            logger.error("【系统异常】", e);
            return new Result(ResultEnum.UNKNOWN_ERROR.getCode(), ResultEnum.UNKNOWN_ERROR.getMessage(), null);
        }
    }

    /**
     * 抢单失败所抛异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = GetOrderFailException.class)
    @ResponseBody
    public Result GetOrderFailExceptionHandel(GetOrderFailException e) {
        logger.error("【抢单失败】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * 订单出错
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = OrderErrorException.class)
    @ResponseBody
    public Result OrderErrorExceptionHandel(OrderErrorException e) {
        logger.error("【订单出错】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * 订单出错：订单被取消
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = OrderCancelErrorException.class)
    @ResponseBody
    public Result OrderCancleErrorExceptionHandel(OrderCancelErrorException e) {
        logger.error("【订单出错】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * @param e 登录异常
     * @return
     */
    @ExceptionHandler(value = LoginException.class)
    @ResponseBody
    public Result LoginExceptionHandel(LoginException e) {
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * @param e 操作已删除对象时所抛异常
     * @return
     */
    @ExceptionHandler(value = DeletedStateErrorException.class)
    @ResponseBody
    public Result DeletedStateErrorExceptionHandel(DeletedStateErrorException e) {
        logger.error("【该操作对象已删除】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * @param e 操作已冻结对象时所抛异常
     * @return
     */
    @ExceptionHandler(value = FrozenStateErrorException.class)
    @ResponseBody
    public Result FrozenStateErrorExceptionHandel(FrozenStateErrorException e) {
        logger.error("【该操作对象已冻结】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * 处理空参异常
     *
     * @param e 自定义空参异常
     * @return
     */
    @ExceptionHandler(value = NullParamException.class)
    @ResponseBody
    public Result NullParamExceptionHandle(NullParamException e) {
        logger.error("【空指针异常】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * 处理传输对象参数错误异常
     *
     * @param e 框架自带校验规则，校验不通过时抛出的异常
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Result BindExceptionHandel(BindException e) {
        logger.error("【传入参数不符合规则】", e);
        return new Result(ResultEnum.PARAM_ERROR.getCode(), ResultEnum.PARAM_ERROR.getMessage() + "[" + e.getBindingResult().getFieldError().getDefaultMessage() + "]", null);
    }

    /**
     * 处理参数规则错误异常
     *
     * @param e 参数值不符合规则所抛异常
     * @return
     */
    @ExceptionHandler(value = ParamErrorException.class)
    @ResponseBody
    public Result ParamErrorExceptionHandel(ParamErrorException e) {
        logger.error("【传入参数不符合规则】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * 处理数据库保存/更改数据时，数据完整性异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    @ResponseBody
    public Result MySQLIntegrityConstraintViolationExceptionHandle(DataIntegrityViolationException e) {
        logger.error("【数据插入/更新异常】", e);
        return new Result(ResultEnum.PARAM_INTEGRITY_ERROR.getCode(), ResultEnum.PARAM_INTEGRITY_ERROR.getMessage(), null);
    }

    /**
     * 处理字符集编码异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = UnsupportedEncodingException.class)
    @ResponseBody
    public Result UnsupportedEncodingExceptionHandle(UnsupportedEncodingException e) {
        logger.error("【字符集编码异常】", e);
        return new Result(ResultEnum.ENCODEING_ERROR.getCode(), ResultEnum.ENCODEING_ERROR.getMessage(), null);
    }

    /**
     * 处理短信发送异常
     *
     * @param e 短信发送失败时所抛异常
     * @return
     */
    @ExceptionHandler(value = MessageSendErrorException.class)
    @ResponseBody
    public Result MessageSendErrorExceptionHandel(MessageSendErrorException e) {
        logger.error("【短信发送失败】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * IO流异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = IOException.class)
    @ResponseBody
    public Result IOExceptionHandle(IOException e) {
        logger.error("【IO流异常】", e);
        return new Result(ResultEnum.FilE_ERROR.getCode(), ResultEnum.FilE_ERROR.getMessage(), null);
    }

    /**
     * 空指针异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public Result NullPointerExceptionHandle(NullPointerException e) {
        logger.error("【空指针异常】", e);
        return new Result(ResultEnum.UNKNOWN_ERROR.getCode(), ResultEnum.UNKNOWN_ERROR.getMessage() + "[空指针异常]", null);
    }

    /**
     * 处理请求方式异常
     * 请求方式GET/POST
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Result HttpRequestMethodNotSupportedExceptionHandle(HttpRequestMethodNotSupportedException e) {
        throw new ParamErrorException("[您使用了错误的请求方式]");
    }

    /**
     * 权限异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = PermissionException.class)
    @ResponseBody
    public Result PermissionExceptionHandle(PermissionException e) {
        logger.error("【请求者没有权限进行该操作】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * 该对象不存在
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = NotExistException.class)
    @ResponseBody
    public Result NotExistExceptionHandle(NotExistException e) {
        logger.error("【该对象不存在】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * 文件上传失败
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MultipartException.class)
    @ResponseBody
    public Result MultipartExceptionHandle(MultipartException e) {
        logger.error("【文件上传失败】", e);
        return new Result(ResultEnum.FilE_ERROR.getCode(), ResultEnum.FilE_ERROR.getMessage() + "[文件上传失败]", null);
    }

    /**
     * 微信公众号出错
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = WeChatException.class)
    @ResponseBody
    public Result WeChatExceptionHandle(WeChatException e) {
        logger.error("【公众平台服务号】服务器设置错误", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * 微信公众号出错
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = WxErrorException.class)
    public String WxErrorExceptionHandle(WxErrorException e) {
        WxError error = e.getError();
        if (error.getErrorCode() == 40030) {
            logger.error("【公众平台服务号】不合法的 refresh_token", e);
            return "redirect:/wechat/toLogin";
        } else {
            logger.error("【公众平台服务号】", e);
            return new Result(ResultEnum.WE_CHAT_ERROR.getCode(), ResultEnum.WE_CHAT_ERROR.getMessage(), null).toString();
        }
    }

    /**
     * 地址转换错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = AddressException.class)
    @ResponseBody
    public Result AddressExceptionHandle(AddressException e) {
        logger.error("【地址转换错误】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

    /**
     * 极光推送失败
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = APIRequestException.class)
    @ResponseBody
    public Result APIRequestExceptionHandle(APIRequestException e) {
        return new Result(ResultEnum.JDPUSH_ERROR.getCode(), ResultEnum.JDPUSH_ERROR.getMessage(), null);
    }


    /**
     * 微信支付错误
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = WeChatSellException.class)
    @ResponseBody
    public Result WeChatSellExceptionHandle(WeChatSellException e) {
        logger.error("【地址转换错误】", e);
        return new Result(e.getCode(), e.getMessage(), null);
    }

}
