package com.plansolve.farm.controller.client.common;

import cn.jpush.api.push.PushResult;
import com.plansolve.farm.controller.client.BaseController;
import com.plansolve.farm.exception.*;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.user.MobileDTO;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.UserService;
import com.plansolve.farm.service.common.CaptchaService;
import com.plansolve.farm.util.*;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户无需登录所能进行的操作
 **/

@RestController
@RequestMapping(value = "/farm")
public class UserController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private CaptchaService captchaService;
    private final String adminUser = "17610575626";

    /*************************************************************用户注册*************************************************************/
    /**
     * 获取手机验证码并发送给用户
     *
     * @param mobile 注册手机号码（唯一）
     * @return
     */
    @PostMapping(value = "/getRegistCaptcha")
    public Result getRegistCaptcha(String mobile) throws Exception {
        if (mobile.isEmpty()) {
            logger.error("传入手机号码为空");
            throw new NullParamException("[手机号码不可为空]");
        } else {
            // 判断数据库是否已存在这个号码
            if (userService.checkMobileExist(mobile)) {
                // 该手机号码已注册
                logger.error("该手机号码已被注册");
                throw new ParamErrorException("[该手机号码已注册]");
            } else if (userService.checkMobileExist("F" + mobile)) {
                logger.error("该手机号码已销户6次，无法再次注册");
                throw new ParamErrorException("[该手机号码已被销户6次，系统拒绝您的注册请求]");
            } else {
                Result result = captchaService.sendCaptcha(mobile);

                result.setMsg(AppHttpUtil.getSession().getId());
                return result;
            }
        }
    }

    /**
     * 用户注册
     * 注册完成后，直接登录
     *
     * @param userDTO    传输对象
     * @param androidMAC 设备码
     * @param password   密码设置
     * @return
     */
    @PostMapping(value = "/regist")
    public Result regist(UserDTO userDTO, String androidMAC, String password) {
        if (password.isEmpty()) {
            logger.error("密码为空");
            throw new NullParamException("[密码不可为空]");
        } else if (password.trim().length() < 6) {
            logger.error("密码少于6位长度");
            throw new NullParamException("[请输入至少6位长度的密码]");
        } else if (userDTO.getNickname().isEmpty()) {
            logger.error("用户名为空");
            throw new NullParamException("[用户名不能为空]");
        } else if (androidMAC.isEmpty()) {
            logger.error("设备码为空");
            throw new NullParamException("[用户手机设备码不能为空]");
        } else {
            HttpSession session = AppHttpUtil.getSession();
            // 检查是否经过验证码验证
            String mobile = (String) session.getAttribute(SysConstant.VALIDATION_MOBILE);
            Date expireDate = (Date) session.getAttribute(SysConstant.VALIDATION_EXPIRE_TIME);
            if (mobile == null || mobile.isEmpty() || expireDate == null) {
                logger.error("[该请求未经短信验证，不予通过]");
                throw new PermissionException("[您未进行短信验证]");
            } else if (expireDate.before(new Date())) {
                logger.error("[短信验证超过5分钟时效，需重新验证]");
                throw new PermissionException("[请重新进行短信验证]");
            } else {
                userDTO.setMobile(mobile);
                userDTO = userService.regist(userDTO, password);
                // 注册成功，直接登录
                MobileDTO mobileDTO = new MobileDTO();
                mobileDTO.setMobile(userDTO.getMobile());
                mobileDTO.setAndroidMAC(androidMAC);
                // 登录成功，将手机号码与设备码共同生成唯一标识，并将当前用户存进session中
                User user = userService.findByMobile(mobileDTO.getMobile());
                // 实时更新用户的注册id，确保消息推送到用户最新的手机
                user = userService.changeUserRegistId(user, userDTO.getRegistId());
                user = userService.changeUserAndroidMAC(user.getIdUser(), mobileDTO.getAndroidMAC());
                setCurrentUser(user);
                return ResultUtil.success(userDTO);
            }
        }
    }
    /*************************************************************用户注册*************************************************************/

    /*************************************************************用户登录*************************************************************/
    /**
     * 用户登录（手机/密码登录）
     *
     * @param mobileDTO
     * @param password
     * @return
     */
    @PostMapping(value = "/login/password")
    public Result loginByPassword(@Valid MobileDTO mobileDTO, String password) {
        if (password.isEmpty()) {
            logger.error("密码为空");
            throw new NullParamException("[密码不可为空]");
        } else {
            if (mobileDTO.getMobile().equals(adminUser)) {
                // 校对手机密码
                User user = userService.findByMobile(mobileDTO.getMobile());
                if (EncryptUtil.comparator(password, user.getPassword())) {
                    // 实时更新用户的注册id，确保消息推送到用户最新的手机
                    user = userService.changeUserRegistId(user, mobileDTO.getRegistId());
                    // 登录成功，将手机号码与设备码共同生成唯一标识，并将当前用户存进session中
                    setCurrentUser(user);
                    // 查看是否有系统的推送消息
                    userMessageAfterLogin(user);
                    UserDTO userDTO = userService.findUser(user.getIdUser(), false);

                    Result result = ResultUtil.success(userDTO);
                    result.setMsg(AppHttpUtil.getSession().getId());
                    return result;
                } else {
                    // 登录失败
                    logger.error("用户手机密码不匹配");
                    throw new ParamErrorException("[手机或密码错误]");
                }
            } else {
                String token = AppHttpUtil.getRequest().getHeader("Token");
                String[] cookie = token.split("=");
                if (cookie == null || cookie.length != 2) {
                    // 手机验证码登录
                    logger.error("获取COOKIE失败，需使用手机验证码登录");
                    throw new LoginException("[请使用手机验证码登录]");
                } else {
                    String UID = cookie[1];
                    // 校验是否是原设备上登录
                    if (EncryptUtil.encrypt(mobileDTO.getMobile() + mobileDTO.getAndroidMAC()).equals(UID) == false) {
                        // 手机验证码登录
                        logger.error("COOKIE设备匹配失败，需使用手机验证码登录");
                        logger.error("系统生成为：" + EncryptUtil.encrypt(mobileDTO.getMobile() + mobileDTO.getAndroidMAC()));
                        throw new LoginException("[请使用手机验证码登录]");
                    } else {
                        // 校对手机密码
                        User user = userService.findByMobile(mobileDTO.getMobile());
                        if (UserUtil.checkUserExist(user)){
                            if (EncryptUtil.comparator(password, user.getPassword())) {
                                // 实时更新用户的注册id，确保消息推送到用户最新的手机
                                user = userService.changeUserRegistId(user, mobileDTO.getRegistId());
                                // 登录成功，将手机号码与设备码共同生成唯一标识，并将当前用户存进session中
                                setCurrentUser(user);
                                // 查看是否有系统的推送消息
                                userMessageAfterLogin(user);
                                UserDTO userDTO = userService.findUser(user.getIdUser(), false);

                                Result result = ResultUtil.success(userDTO);
                                result.setMsg(AppHttpUtil.getSession().getId());
                                return result;
                            } else {
                                // 登录失败
                                logger.error("用户手机密码不匹配");
                                throw new ParamErrorException("[手机或密码错误]");
                            }
                        } else {
                            return null;
                        }
                    }
                }
            }
        }
    }

    /**
     * 用户登录（手机/验证码登录）
     *
     * @param mobileDTO
     * @param captcha
     * @return
     */
    @PostMapping(value = "/login/captcha")
    public Result loginByCaptcha(@Valid MobileDTO mobileDTO, String captcha) {
        if (captcha.isEmpty()) {
            // 该手机号码未注册
            logger.error("验证码为空");
            throw new ParamErrorException("[验证码不能为空]");
        } else {
            User user = userService.findByMobile(mobileDTO.getMobile());
            if (UserUtil.checkUserExist(user)) {
                if (captchaService.checkCaptcha(mobileDTO.getMobile(), captcha)) {
                    // 登录成功，将手机号码与设备码共同生成唯一标识，并将当前用户存进session中
                    if (user.getAndroidMAC().equals(mobileDTO.getAndroidMAC()) == false) {
                        user = userService.changeUserAndroidMAC(user.getIdUser(), mobileDTO.getAndroidMAC());
                    }
                    // 实时更新用户的注册id，确保消息推送到用户最新的手机
                    user = userService.changeUserRegistId(user, mobileDTO.getRegistId());
                    setCurrentUser(user);
                    // 查看是否有系统的推送消息
                    userMessageAfterLogin(user);
                    UserDTO userDTO = userService.findUser(user.getIdUser(), false);

                    Result result = ResultUtil.success(userDTO);
                    result.setMsg(AppHttpUtil.getSession().getId());
                    return result;
                } else {
                    logger.error("用户手机验证码不匹配");
                    throw new ParamErrorException("[手机验证码校验失败]");
                }
            } else {
                return null;
            }
        }
    }

    /**
     * 登录成功后，30天免登录
     *
     * @param user
     */
    private void setCurrentUser(User user) {
        String USER_TOKEN = EncryptUtil.encrypt(user.getMobile().trim() + user.getAndroidMAC().trim());
        Cookie cookie = new Cookie(SysConstant.USER_TOKEN, USER_TOKEN);
        cookie.setMaxAge(2592000);
        cookie.setPath("/");
        AppHttpUtil.getResponse().addCookie(cookie);
        HttpSession session = AppHttpUtil.getSession();
        session.setMaxInactiveInterval(60 * 60 * 24 * 30);
        session.setAttribute(SysConstant.CURRENT_USER, user);
    }

    /*public static void main(String[] args) {
        String USER_TOKEN = EncryptUtil.encrypt("15001109276" + "00000000-339f-43e6-ffff-ffffb26bab52");
        System.out.println(USER_TOKEN);
    }*/
    /*************************************************************用户登录*************************************************************/

    /**
     * 获取手机验证码并发送给用户
     *
     * @param mobile 登录手机号码（唯一）
     * @return
     */
    @PostMapping(value = "/captcha")
    public Result getCaptcha(String mobile) throws Exception {
        if (mobile.isEmpty()) {
            logger.error("手机号码为空");
            throw new NullParamException("[手机号码不可为空]");
        } else {
            // 判断数据库是否已存在这个号码
            if (userService.checkMobileExist(mobile)) {
                logger.debug("============================" + AppHttpUtil.getSession().getId() + "============================");
                Result result = captchaService.sendCaptcha(mobile);

                result.setMsg(AppHttpUtil.getSession().getId());
                return result;
            } else {
                // 该手机号码未注册
                logger.error("该手机号码不存在");
                throw new ParamErrorException("[该手机号码未注册]");
            }
        }
    }

    /**
     * 验证用户验证码
     *
     * @param captcha
     * @return
     */
    @PostMapping(value = "/checkCaptcha")
    public Result checkCaptcha(String captcha) {
        HttpSession session = AppHttpUtil.getSession();

        // 获取用户手机号码并进行匹配
        String mobile = (String) session.getAttribute(SysConstant.MOBILE);
        if (mobile == null || mobile.isEmpty()) {
            // 系统未获取到验证码
            logger.error("系统手机验证码获取记录不存在");
            throw new NullParamException("[系统未获取到验证码]");
        } else if (captcha.isEmpty()) {
            logger.error("验证码输入值为空");
            throw new NullParamException("[验证码不可为空]");
        } else {
            if (captchaService.checkCaptcha(mobile, captcha)) {
                // 验证成功，5分钟内有效
                session.setAttribute(SysConstant.VALIDATION_MOBILE, mobile);
                session.setAttribute(SysConstant.VALIDATION_EXPIRE_TIME, DateUtils.getDate_PastOrFuture_Minute(new Date(), 5));
                Result result = ResultUtil.success(null);

                result.setMsg(AppHttpUtil.getSession().getId());
                return result;
            } else {
                logger.error("手机验证码与系统不匹配");
                throw new ParamErrorException("[手机验证码错误]");
            }
        }
    }

    /**
     * 用户更改密码
     *
     * @param password
     * @return
     */
    @PostMapping(value = "/changePassword")
    public Result changePassword(String password, String androidMAC) {
        // 检查是否经过验证码验证
        HttpSession session = AppHttpUtil.getSession();
        String mobile = (String) session.getAttribute(SysConstant.VALIDATION_MOBILE);
        Date expireDate = (Date) session.getAttribute(SysConstant.VALIDATION_EXPIRE_TIME);
        if (mobile == null || mobile.isEmpty() || expireDate == null) {
            logger.error("未进行短信验证");
            throw new PermissionException("[您未进行短信验证]");
        } else if (expireDate.before(new Date())) {
            logger.error("短信验证超过5分钟时效，需重新验证");
            throw new ParamErrorException("[请重新进行短信验证]");
        } else {
            User user = userService.findByMobile(mobile);
            userService.changePassword(user.getIdUser(), password);

            String USER_TOKEN = EncryptUtil.encrypt(mobile.trim() + androidMAC.trim());
            Cookie cookie = new Cookie(SysConstant.USER_TOKEN, USER_TOKEN);
            cookie.setMaxAge(2592000);
            cookie.setPath("/");
            AppHttpUtil.getResponse().addCookie(cookie);

            if (user.getAndroidMAC().equals(androidMAC.trim()) == false) {
                userService.changeUserAndroidMAC(user.getIdUser(), androidMAC);
            }
            return ResultUtil.success(null);
        }
    }

    /**
     * 根据旧手机号码验证更改用户手机号码
     *
     * @param captcha
     * @param androidMAC
     * @return
     */
    @PostMapping(value = "/changeMobile")
    public Result changeMobile(String captcha, String androidMAC) {
        // 检查是否经过验证码验证
        HttpSession session = AppHttpUtil.getSession();
        String mobileNew = (String) session.getAttribute(SysConstant.MOBILE);
        String mobileOld = (String) session.getAttribute(SysConstant.VALIDATION_MOBILE);
        Date expireDate = (Date) session.getAttribute(SysConstant.VALIDATION_EXPIRE_TIME);
        if (mobileOld == null || mobileOld.isEmpty() || expireDate == null) {
            logger.error("未进行原有手机短信验证");
            throw new PermissionException("[未进行原有手机短信验证]");
        } else if (expireDate.before(new Date())) {
            logger.error("原有手机短信验证超过5分钟时效，需重新验证");
            throw new ParamErrorException("[请重新进行短信验证原有手机]");
        } else {
            if (mobileNew == null || mobileNew.isEmpty()) {
                // 系统未获取到验证码
                logger.error("系统新手机号码验证码获取记录不存在");
                throw new NullParamException("[系统未获取到新手机号码验证码]");
            } else if (captcha.isEmpty()) {
                logger.error("验证码输入值为空");
                throw new NullParamException("[验证码不可为空]");
            } else {
                if (captchaService.checkCaptcha(mobileNew, captcha)) {
                    // 验证成功，更改手机号码
                    User user = userService.findByMobile(mobileOld);
                    user = userService.changeUserMobile(user.getIdUser(), mobileNew);
                    if (user.getAndroidMAC().equals(androidMAC.trim()) == false) {
                        user = userService.changeUserAndroidMAC(user.getIdUser(), androidMAC);
                    }
                    setCurrentUser(user);
                    return ResultUtil.success(mobileNew);
                } else {
                    logger.error("新手机号码验证码与系统不匹配");
                    throw new ParamErrorException("[验证失败]");
                }
            }
        }
    }

    private void userMessageAfterLogin(User user) {
        if (null != user) {
            CacheManager cacheManager = CacheManager.getInstance();
            Cache cache = cacheManager.getCache("pushCache");
            if (StringUtils.isNotBlank(user.getMobile())) {
                Element userMap = cache.get(user.getMobile());
                // 如果带推送用户在缓存中
                if (null != userMap) {
                    Element msgMap = cache.get(user.getIdUser());
                    if (null != msgMap) {
                        List<String> messages = (List<String>) msgMap.getObjectValue();
                        if (null != messages && messages.size() > 0) {
                            if (StringUtils.isNotBlank(user.getRegistId())) {
                                List<User> users = new ArrayList<>();
                                users.add(user);
                                for (String msg : messages) {
                                    PushResult pushResult = Jdpush.pushMessageUtil(msg, users);
                                    if (null != pushResult && pushResult.isResultOK()) {
                                        logger.info("用户" + user.getNickname() + "待发送的消息推送成功。");
                                    } else {
                                        logger.error("用户" + user.getNickname() + "待发送的消息推送失败！");
                                    }
                                }
                                // 将用户从缓存中清除
                                cache.remove(user.getIdUser());
                                cache.remove(user.getMobile());
                            }
                        }
                    }
                }
            }
        }
    }

}
