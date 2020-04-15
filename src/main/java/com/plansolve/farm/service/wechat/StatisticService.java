package com.plansolve.farm.service.wechat;

import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.client.user.FarmlandDTO;
import com.plansolve.farm.model.database.cooperation.Cooperation;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserInfo;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.repository.cooperation.CooperationRepository;
import com.plansolve.farm.repository.order.UserOrderRepository;
import com.plansolve.farm.repository.user.UserInfoRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.client.FarmlandService;
import com.plansolve.farm.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Author: 高一平
 * @Date: 2018/10/25
 * @Description:
 **/
@Slf4j
@Service
public class StatisticService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private CooperationRepository cooperationRepository;
    @Autowired
    private UserOrderRepository orderRepository;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private FarmlandService farmlandService;
    private static Date STATISTIC_DATE_BEGIN = DateUtils.parseDate("2018-10-01");

    /**
     * 生成用户统计数据文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public void updateUserExcel(String path) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        XSSFSheet userSheet = workbook.createSheet();
        List<User> users = userRepository.findAll();
        Row topRow = userSheet.createRow(0);
        topRow.createCell(0).setCellValue("序号");
        topRow.createCell(1).setCellValue("昵称");
        topRow.createCell(2).setCellValue("真实姓名");
        topRow.createCell(3).setCellValue("注册时间");
        topRow.createCell(4).setCellValue("手机号");
        topRow.createCell(5).setCellValue("是否认证");
        topRow.createCell(6).setCellValue("合作社名称");
        topRow.createCell(7).setCellValue("地址");

        XSSFCellStyle topCellStyle = workbook.createCellStyle();
        topCellStyle.cloneStyleFrom(cellStyle);
        topCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        topCellStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        for (int i = 0; i < 8; i++) {
            topRow.getCell(i).setCellStyle(topCellStyle);
        }

        int rowIndex = 1;
        for (User user : users) {
            UserInfo userInfo = userInfoRepository.findByIdUser(user.getIdUser());

            Row row = userSheet.createRow(rowIndex);
            for (int i = 0; i <= 7; i++) {
                row.createCell(i).setCellStyle(cellStyle);
            }

            row.getCell(0).setCellValue(user.getIdUser());
            row.getCell(1).setCellValue(user.getNickname());
            if (userInfo != null) {
                row.getCell(2).setCellValue(userInfo.getRealname());
            }
            row.getCell(3).setCellValue(DateUtils.formatDate(user.getRegistTime(), "yyyy-MM-dd HH:mm:ss"));
            row.getCell(4).setCellValue(user.getMobile());
            if (user.getUserState().equals(UserStateEnum.NORMOL.getState())) {
                row.getCell(5).setCellValue("已认证");
            } else {
                row.getCell(5).setCellValue("未认证");
            }
            if (user.getIdCooperation() != null && user.getIdCooperation() > 0) {
                Cooperation cooperation = cooperationService.getById(user.getIdCooperation());
                row.getCell(6).setCellValue(cooperation.getCooperationName());
            }
            List<FarmlandDTO> list = farmlandService.list(user.getIdUser());
            if (list != null && list.size() > 0) {
                Set<String> addresses = new HashSet<>();
                for (FarmlandDTO farmlandDTO : list) {
                    AddressDTO address = farmlandDTO.getAddress();
                    addresses.add(address.getProvince() + address.getCity() + address.getCity());
                }
                Iterator<String> iterator = addresses.iterator();
                String addressStr = "";
                while (iterator.hasNext()) {
                    addressStr = addressStr + iterator.next() + "  ";
                }
                row.getCell(7).setCellValue(addressStr);
            }
            rowIndex++;
        }

        userSheet.setColumnWidth(0, 1200);
        userSheet.setColumnWidth(1, 3200);
        userSheet.setColumnWidth(2, 3200);
        userSheet.setColumnWidth(3, 6000);
        userSheet.setColumnWidth(4, 3500);
        userSheet.setColumnWidth(5, 2500);
        userSheet.setColumnWidth(6, 5000);
        userSheet.setColumnWidth(7, 16000);

        path = FileProperties.fileRealPath + path;
        checkDictExist(path);
        write(path, workbook);
    }

    /**
     * 更新用户统计信息
     *
     * @param path      需更新的文件
     * @param origin    文件模板
     * @param reloadAll 是否更新全部
     * @throws IOException
     */
    public void updateUserStatisticExcel(String path, String origin, Boolean reloadAll) throws IOException, InvalidFormatException {
        // 文件需保存地址
        path = FileProperties.fileRealPath + path;
        // 文件模板地址
        origin = FileProperties.fileRealPath + origin;
        // 确保文件夹存在
        checkDictExist(path);

        File file = new File(origin);
        if (!file.exists()) {
            log.error("【统计数据原始表不存在】");
            return;
        }

        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet userStatisticSheet = workbook.getSheetAt(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        // 统计截止时间
        Date date = DateUtils.getDayEnd(DateUtils.getDate_PastOrFuture_Day(new Date(), -1));

        if (reloadAll) {
            int rowIndex = 3;
            Date registTime = STATISTIC_DATE_BEGIN;
            do {
                userStatisticSheet = dealRow(userStatisticSheet, cellStyle, registTime, rowIndex);
                registTime = DateUtils.getDate_PastOrFuture_Day(registTime, 1);
                rowIndex++;
            } while (date.after(registTime));

        } else {
            // 如果不更新全部，须获得表格结尾的行数
            Iterator<Row> rowIterator = userStatisticSheet.iterator();
            int rowIndex = 0;
            while (rowIterator.hasNext()) {
                rowIndex = rowIterator.next().getRowNum();
            }
            rowIndex = rowIndex + 1;

            userStatisticSheet = dealRow(userStatisticSheet, cellStyle, date, rowIndex);
        }
        write(path, workbook);
    }

    /**
     * 处理对应日期数据
     *
     * @param sheet     需填入数据的表格队形
     * @param cellStyle 表格样式
     * @param date      数据日期
     * @param rowIndex  行数
     * @return
     */
    private XSSFSheet dealRow(XSSFSheet sheet, XSSFCellStyle cellStyle, Date date, int rowIndex) {
        if (rowIndex < 3) {
            rowIndex = 3;
        }
        XSSFRow row = sheet.createRow(rowIndex);
        Date beginDate = DateUtils.getDayBegin(date);
        Date endDate = DateUtils.getDayEnd(date);

        for (int i = 0; i <= 24; i++) {
            row.createCell(i).setCellStyle(cellStyle);
        }

        row.getCell(0).setCellValue(DateUtils.formatDate(date, "yyyy-MM-dd"));
        /*************************************************************************总计*************************************************************************/
        // 昨天累计注册人数
        Integer allRegistUserNum = userRepository.countByRegistTimeBefore(endDate);
        row.getCell(1).setCellValue(allRegistUserNum);
        // 当日累计注册人数
        Integer registUserNum = userRepository.countByRegistTimeBetween(beginDate, endDate);
        row.getCell(2).setCellValue(registUserNum);
        // 昨天累计认证人数
        Integer allNormalRegistNum = userRepository.countByRegistTimeBeforeAndUserStateEquals(endDate, UserStateEnum.NORMOL.getState());
        row.getCell(3).setCellValue(allNormalRegistNum);
        // 当日累计认证人数
        Integer normalRegistNum = userRepository.countByRegistTimeBetweenAndUserStateEquals(beginDate, endDate, UserStateEnum.NORMOL.getState());
        row.getCell(4).setCellValue(normalRegistNum);
        // 昨天累计合作社数量
        Integer allRegistCooperationNum = cooperationRepository.countByRegistTimeBefore(endDate);
        row.getCell(5).setCellValue(allRegistCooperationNum);
        // 当日累计合作社数量
        Integer registCooperationNum = cooperationRepository.countByRegistTimeBetween(beginDate, endDate);
        row.getCell(6).setCellValue(registCooperationNum);
        // 昨天累计订单数量
        Integer allOrderNum = orderRepository.countByCreateTimeBefore(endDate);
        row.getCell(7).setCellValue(allOrderNum);
        // 当前累计订单数量
        Integer orderNum = orderRepository.countByCreateTimeBetween(beginDate, endDate);
        row.getCell(8).setCellValue(orderNum);
        /*************************************************************************吉林白城*************************************************************************/
        // 昨天累计注册人数
        Integer JBAllRegistUserNum = userRepository.placeAllUser("白城市", endDate);
        row.getCell(9).setCellValue(JBAllRegistUserNum);
        // 当日累计注册人数
        Integer JBRegistUserNum = userRepository.placeUser("白城市", beginDate, endDate);
        row.getCell(10).setCellValue(JBRegistUserNum);
        // 昨天累计认证人数
        Integer JBAllNormalRegistUserNum = userRepository.placeAllNormalUser("白城市", endDate);
        row.getCell(11).setCellValue(JBAllNormalRegistUserNum);
        // 当日累计认证人数
        Integer JBNormalRegistUserNum = userRepository.placeNormalUser("白城市", beginDate, endDate);
        row.getCell(12).setCellValue(JBNormalRegistUserNum);
        // 昨天累计合作社数量
        Integer JBAllRegistCooperationNum = cooperationRepository.placeAllCooperation("白城市", endDate);
        row.getCell(13).setCellValue(JBAllRegistCooperationNum);
        // 当日累计合作社数量
        Integer JBRegistCooperationNum = cooperationRepository.placeCooperation("白城市", beginDate, endDate);
        row.getCell(14).setCellValue(JBRegistCooperationNum);
        // 昨天累计订单数量
        Integer JBAllOrderNum = orderRepository.countByCreateTimeBeforeAndAndAssemblyAddressLike(endDate, "吉林省白城市%");
        row.getCell(15).setCellValue(JBAllOrderNum);
        // 当前累计订单数量
        Integer JBOrderNum = orderRepository.countByCreateTimeBetweenAndAndAssemblyAddressLike(beginDate, endDate, "吉林省白城市%");
        row.getCell(16).setCellValue(JBOrderNum);
        /*************************************************************************内蒙古兴安盟*************************************************************************/
        // 昨天累计注册人数
        Integer XAMAllRegistUserNum = userRepository.placeAllUser("兴安盟", endDate);
        row.getCell(17).setCellValue(XAMAllRegistUserNum);
        // 当日累计注册人数
        Integer XAMRegistUserNum = userRepository.placeUser("兴安盟", beginDate, endDate);
        row.getCell(18).setCellValue(XAMRegistUserNum);
        // 昨天累计认证人数
        Integer XAMAllNormalRegistUserNum = userRepository.placeAllNormalUser("兴安盟", endDate);
        row.getCell(19).setCellValue(XAMAllNormalRegistUserNum);
        // 当日累计认证人数
        Integer XAMNormalRegistUserNum = userRepository.placeNormalUser("兴安盟", beginDate, endDate);
        row.getCell(20).setCellValue(XAMNormalRegistUserNum);
        // 昨天累计合作社数量
        Integer XAMAllRegistCooperationNum = cooperationRepository.placeAllCooperation("兴安盟", endDate);
        row.getCell(21).setCellValue(XAMAllRegistCooperationNum);
        // 当日累计合作社数量
        Integer XAMRegistCooperationNum = cooperationRepository.placeCooperation("兴安盟", beginDate, endDate);
        row.getCell(22).setCellValue(XAMRegistCooperationNum);
        // 昨天累计订单数量
        Integer XAMAllOrderNum = orderRepository.countByCreateTimeBeforeAndAndAssemblyAddressLike(endDate, "内蒙古自治区兴安盟%");
        row.getCell(23).setCellValue(XAMAllOrderNum);
        // 当前累计订单数量
        Integer XAMOrderNum = orderRepository.countByCreateTimeBetweenAndAndAssemblyAddressLike(beginDate, endDate, "内蒙古自治区兴安盟%");
        row.getCell(24).setCellValue(XAMOrderNum);
        return sheet;
    }

    /**
     * 将Excel保存至硬盘
     *
     * @param path
     * @param workbook
     * @throws IOException
     */
    private void write(String path, XSSFWorkbook workbook) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    /**
     * 检验文件夹是否存在
     * 若不存在，则创建该文件夹及其父文件夹
     *
     * @param path
     */
    private void checkDictExist(String path) {
        File dict = new File(path.substring(0, path.lastIndexOf("/")));
        if (!dict.exists()) {
            dict.mkdirs();
        }
    }

}
