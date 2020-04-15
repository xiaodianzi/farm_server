package com.plansolve.farm.controller.client.main;

import com.plansolve.farm.controller.client.BaseController;
import com.plansolve.farm.model.Result;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.model.client.StatisticsCooperationDTO;
import com.plansolve.farm.model.client.StatisticsOperatorsDTO;
import com.plansolve.farm.model.client.StatisticsOrderDTO;
import com.plansolve.farm.model.client.user.FarmlandDTO;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.order.CompletionReport;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.service.client.CooperationService;
import com.plansolve.farm.service.client.FarmlandService;
import com.plansolve.farm.service.client.OperatorReportService;
import com.plansolve.farm.service.client.OrderService;
import com.plansolve.farm.util.AppHttpUtil;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.ResultUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author: 高一平
 * @Date: 2018/8/15
 * @Description: 文件相关
 **/

@Controller
@RequestMapping(value = "/farm/file")
public class FileController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OperatorReportService reportService;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private FarmlandService farmlandService;

    /**
     * 生成合作社统计信息文件
     *
     * @param getAll    是否获取所有统计信息
     * @param startTime 若获取月份的统计信息时的月份
     * @param endTime   若获取月份的统计信息时的月份
     */
    @PostMapping(value = "/orderTaskFileDetail")
    public Result orderTaskFileDetail(Boolean getAll, Date startTime, Date endTime) throws IOException {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        // 获取表一、表二、表三信息
        Map<String, List> map = cooperationService.statisticsOrders(user, getAll, startTime, endTime);

        Sheet cooperationSheet = workbook.createSheet("合作社统计1");
        cooperationSheet = cooperationSheet(cooperationSheet, cellStyle, map.get("合作社统计信息表一"));
        Sheet cooperationOperatorSheet = workbook.createSheet("农机手统计2");
        cooperationOperatorSheet = cooperationOperatorSheet(cooperationOperatorSheet, cellStyle, map.get("合作社统计信息表二"));
        Sheet cooperationOrderSheet = workbook.createSheet("订单统计3");
        cooperationOrderSheet = cooperationOrderSheet(cooperationOrderSheet, cellStyle, map.get("合作社统计信息表三"));
        Sheet mineOrderSheet = workbook.createSheet("我的统计4");
        mineOrderSheet = mineOrderSheet(mineOrderSheet, cellStyle, user, getAll, startTime, endTime);
        HttpServletResponse response = AppHttpUtil.getResponse();
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
        }
        return ResultUtil.success(null);
    }

    /**
     * 生成合作社统计
     *
     * @param cooperationSheet
     * @param cellStyle
     * @param list
     * @return
     */
    private Sheet cooperationSheet(Sheet cooperationSheet, XSSFCellStyle cellStyle, List list) {
        Row row = cooperationSheet.createRow(0);
        row.createCell(0).setCellValue("合作社统计 表1");
        CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, 0, 5);
        cooperationSheet.addMergedRegion(rangeAddress);

        row = cooperationSheet.createRow(1);
        row.createCell(0).setCellValue("月份");
        row.createCell(1).setCellValue("队别");
        row.createCell(2).setCellValue("作业单数");
        row.createCell(3).setCellValue("提交单数");
        row.createCell(4).setCellValue("结算单数");
        row.createCell(5).setCellValue("作业收入");

        // 录入数据
        if (list != null && list.size() > 0) {
            List<StatisticsCooperationDTO> statisticsCooperationDTOS = list;

            int rowNum = 2;
            for (StatisticsCooperationDTO dto : statisticsCooperationDTOS) {
                Row cooperationSheetRow = cooperationSheet.createRow(rowNum);
                cooperationSheetRow.createCell(0).setCellValue(dto.getStartTime());
                cooperationSheetRow.createCell(1).setCellValue(dto.getTeamName());
                cooperationSheetRow.createCell(2).setCellValue(dto.getWorkingOrderNum());
                cooperationSheetRow.createCell(3).setCellValue(dto.getCommitOrderNum());
                cooperationSheetRow.createCell(4).setCellValue(dto.getFinishedOrderNum());
                cooperationSheetRow.createCell(5).setCellValue(dto.getIncome().toString());
                rowNum++;
            }
        }

        for (int i = 0; i < 6; i++) {
            cooperationSheet.setDefaultColumnStyle(i, cellStyle);
        }
        return cooperationSheet;
    }

    /**
     * 生成合作社农机手统计
     *
     * @param cooperationOperatorSheet
     * @param cellStyle
     * @param list
     * @return
     */
    private Sheet cooperationOperatorSheet(Sheet cooperationOperatorSheet, XSSFCellStyle cellStyle, List list) {
        Row row = cooperationOperatorSheet.createRow(0);
        row.createCell(0).setCellValue("农机手统计 表2");
        CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, 0, 5);
        cooperationOperatorSheet.addMergedRegion(rangeAddress);

        row = cooperationOperatorSheet.createRow(1);
        row.createCell(0).setCellValue("月份");
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("队别");
        row.createCell(3).setCellValue("单数");
        row.createCell(4).setCellValue("亩数");
        row.createCell(5).setCellValue("收入");

        // 录入数据
        if (list != null && list.size() > 0) {
            List<StatisticsOperatorsDTO> statisticsOperatorsDTOS = list;

            int rowNum = 2;
            for (StatisticsOperatorsDTO dto : statisticsOperatorsDTOS) {
                Row cooperationOperatorSheetRow = cooperationOperatorSheet.createRow(rowNum);
                cooperationOperatorSheetRow.createCell(0).setCellValue(dto.getStartTime());
                cooperationOperatorSheetRow.createCell(1).setCellValue(dto.getOperatorName());
                cooperationOperatorSheetRow.createCell(2).setCellValue(dto.getTeamName());
                cooperationOperatorSheetRow.createCell(3).setCellValue(dto.getFinishedOrderNum());
                if (dto.getFarmlandAcreage() != null) {
                    cooperationOperatorSheetRow.createCell(4).setCellValue(dto.getFarmlandAcreage());
                }
                cooperationOperatorSheetRow.createCell(5).setCellValue(dto.getIncome().toString());
                rowNum++;
            }
        }

        for (int i = 0; i < 6; i++) {
            cooperationOperatorSheet.setDefaultColumnStyle(i, cellStyle);
        }
        return cooperationOperatorSheet;
    }

    /**
     * 生成订单统计
     *
     * @param cooperationOrderSheet
     * @param cellStyle
     * @param list
     * @return
     */
    private Sheet cooperationOrderSheet(Sheet cooperationOrderSheet, XSSFCellStyle cellStyle, List list) {
        Row row = cooperationOrderSheet.createRow(0);
        row.createCell(0).setCellValue("订单明细 表3");
        CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, 0, 9);
        cooperationOrderSheet.addMergedRegion(rangeAddress);

        row = cooperationOrderSheet.createRow(1);
        row.createCell(0).setCellValue("月份");
        row.createCell(1).setCellValue("队别");
        row.createCell(2).setCellValue("农机手");
        row.createCell(3).setCellValue("订单号");
        row.createCell(4).setCellValue("作业时间");
        row.createCell(5).setCellValue("作业周期");
        row.createCell(6).setCellValue("农机类型");
        row.createCell(7).setCellValue("作物名称");
        row.createCell(8).setCellValue("作业亩数");
        row.createCell(9).setCellValue("作业地址");

        // 录入数据
        if (list != null && list.size() > 0) {
            List<StatisticsOrderDTO> statisticsOrderDTOS = list;

            int rowNum = 2;
            for (StatisticsOrderDTO dto : statisticsOrderDTOS) {
                Row cooperationOperatorSheetRow = cooperationOrderSheet.createRow(rowNum);
                cooperationOperatorSheetRow.createCell(0).setCellValue(dto.getMonth());
                cooperationOperatorSheetRow.createCell(1).setCellValue(dto.getTeamName());
                cooperationOperatorSheetRow.createCell(2).setCellValue(dto.getOperatorName());
                cooperationOperatorSheetRow.createCell(3).setCellValue(dto.getUserOrderNo());
                cooperationOperatorSheetRow.createCell(4).setCellValue(dto.getStartTime());
                cooperationOperatorSheetRow.createCell(5).setCellValue(dto.getPeriod());
                cooperationOperatorSheetRow.createCell(6).setCellValue(dto.getMachineType());
                cooperationOperatorSheetRow.createCell(7).setCellValue(dto.getCropName());
                if (dto.getFarmlandAcreage() != null) {
                    cooperationOperatorSheetRow.createCell(8).setCellValue(dto.getFarmlandAcreage());
                }
                cooperationOperatorSheetRow.createCell(9).setCellValue(dto.getAddress());
                rowNum++;
            }
        }

        for (int i = 0; i < 10; i++) {
            cooperationOrderSheet.setDefaultColumnStyle(i, cellStyle);
        }
        return cooperationOrderSheet;
    }

    /**
     * 生成我的统计
     *
     * @param mineOrderSheet
     * @param cellStyle
     * @param user
     * @param getAll
     * @param startTime
     * @param endTime
     * @return
     */
    private Sheet mineOrderSheet(Sheet mineOrderSheet, XSSFCellStyle cellStyle, User user, Boolean getAll, Date startTime, Date endTime) {
        Row row = mineOrderSheet.createRow(0);
        row.createCell(0).setCellValue("我的作业统计 表4");
        CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, 0, 7);
        mineOrderSheet.addMergedRegion(rangeAddress);

        row = mineOrderSheet.createRow(1);
        row.createCell(0).setCellValue("月份");
        row.createCell(1).setCellValue("作业时间");
        row.createCell(2).setCellValue("作业周期");
        row.createCell(3).setCellValue("农机类型");
        row.createCell(4).setCellValue("作物名称");
        row.createCell(5).setCellValue("提交作业亩数");
        row.createCell(6).setCellValue("作业地址");
        row.createCell(7).setCellValue("作业总收入");

        // 准备数据
        /*List<UserOrder> orders = orderService.getCooperationOrder(user, getAll, date);*/
        List<UserOrder> orders = orderService.getCooperationOrder(user, getAll, startTime, endTime);
        List<String> months = new ArrayList<>();
        Map<String, List<UserOrder>> map = new HashMap<>();
        /*if (getAll) {*/
        for (UserOrder order : orders) {
            Date date = DateUtils.getMonthStart(order.getStartTime());
            String month = DateUtils.getYear(date) + "/" + DateUtils.getMonth(date);
            List<UserOrder> userOrders = map.get(month);
            if (userOrders != null && userOrders.size() > 0) {
                userOrders.add(order);
                map.put(month, userOrders);
            } else {
                userOrders = new ArrayList<>();
                userOrders.add(order);
                map.put(month, userOrders);

                if (months.size() > 0) {
                    for (int i = 0; i < months.size(); i++) {
                        Date monthsDate = DateUtils.parseDate(months.get(i) + "1");
                        if (date.after(monthsDate)) {
                            months.add(i, month);
                            break;
                        } else if (months.size() - 1 == i) {
                            months.add(month);
                            break;
                        }
                    }
                } else {
                    months.add(month);
                }
            }
        }
        /*} else {
            String month = DateUtils.getYear(date) + "/" + DateUtils.getMonth(date);
            months.add(month);
            map.put(month, orders);
        }*/

        // 录入数据
        if (months.size() > 0) {
            int rowNum = 2;
            for (String month : months) {
                List<UserOrder> userOrders = map.get(month);
                for (UserOrder userOrder : userOrders) {
                    Row mineOrderSheetRow = mineOrderSheet.createRow(rowNum);
                    mineOrderSheetRow.createCell(0).setCellValue(month);
                    mineOrderSheetRow.createCell(1).setCellValue(DateUtils.formatDate(userOrder.getStartTime(), "yyyy.MM.dd") + "至"
                            + DateUtils.formatDate(DateUtils.getDate_PastOrFuture_Day(userOrder.getStartTime(), userOrder.getPeriod()), "yyyy.MM.dd"));
                    mineOrderSheetRow.createCell(2).setCellValue(userOrder.getPeriod() + "天");
                    mineOrderSheetRow.createCell(3).setCellValue(userOrder.getMachineryType());
                    mineOrderSheetRow.createCell(4).setCellValue(userOrder.getCropName());
                    if (userOrder.getCooperative() == false) {
                        mineOrderSheetRow.createCell(5).setCellValue(userOrder.getArce());
                    } else {
                        CompletionReport operatorReport = reportService.getOperatorReport(userOrder.getIdUserOrder(), user.getIdUser());
                        if (operatorReport != null) {
                            mineOrderSheetRow.createCell(5).setCellValue(operatorReport.getAcre());
                        }
                    }
                    Farmland farmland = farmlandService.getFarmland(userOrder.getIdFarmland());
                    if (farmland != null) {
                        FarmlandDTO farmlandDTO = farmlandService.loadDTO(farmland);
                        AddressDTO address = farmlandDTO.getAddress();
                        if (address.getTown() == null) {
                            address.setTown("");
                        }
                        mineOrderSheetRow.createCell(6).setCellValue(address.getProvince()
                                + address.getCity() + address.getCounty() + address.getTown() + address.getDetail());
                    }
                    if (userOrder.getCooperative() == false) {
                        BigDecimal totalPrice = userOrder.getPrice().multiply(new BigDecimal(userOrder.getPeriod()));
                        mineOrderSheetRow.createCell(7).setCellValue(totalPrice.toString());
                    }
                }
            }
            rowNum++;
        }

        for (int i = 0; i < 8; i++) {
            mineOrderSheet.setDefaultColumnStyle(i, cellStyle);
        }
        return mineOrderSheet;
    }

}
