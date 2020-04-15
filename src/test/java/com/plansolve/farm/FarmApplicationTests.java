package com.plansolve.farm;

import com.plansolve.farm.model.database.Address;
import com.plansolve.farm.model.database.Farmland;
import com.plansolve.farm.model.database.Machinery;
import com.plansolve.farm.model.database.order.UserOrder;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.database.user.UserInfo;
import com.plansolve.farm.model.enums.state.FarmlandStateEnum;
import com.plansolve.farm.model.enums.state.MachineryStateEnum;
import com.plansolve.farm.model.enums.state.OrderStateEnum;
import com.plansolve.farm.model.enums.state.UserStateEnum;
import com.plansolve.farm.repository.AddressRepository;
import com.plansolve.farm.repository.FarmlandRepository;
import com.plansolve.farm.repository.MachineryRepository;
import com.plansolve.farm.repository.order.UserOrderRepository;
import com.plansolve.farm.repository.user.UserInfoRepository;
import com.plansolve.farm.repository.user.UserRepository;
import com.plansolve.farm.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FarmApplicationTests {

    @Value("${development}")
    private Boolean development;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private FarmlandRepository farmlandRepository;
    @Autowired
    private MachineryRepository machineryRepository;
    @Autowired
    private UserOrderRepository userOrderRepository;
    String[] mobiles = {"17610575626", "18510785802", "13810554269", "15037165971", "18211005247", "13664667007"};

    /**************************************************************数据导出**************************************************************/

    /**
     * 导出测试库中的数据
     */
    @Test
    public void exportData() throws IOException {
        if (development) {
            XSSFWorkbook workbook = new XSSFWorkbook();

            Map<Long, User> userMap = new HashMap<>();
            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                /*if (user.getIdUser() >= min && user.getIdUser() <= max) {
                    userMap.put(user.getIdUser(), user);
                }*/
                for (String mobile : mobiles) {
                    if (!mobile.equals(user.getMobile())) {
                        userMap.put(user.getIdUser(), user);
                    }
                }
            }

            Sheet userSheet = workbook.createSheet("用户数据");
//            userSheet = userSheetData(userSheet, userMap);
            /*Sheet userFarmlandSheet = workbook.createSheet("用户土地数据");
            userFarmlandSheet = userFarmlandSheetData(userFarmlandSheet, userMap);
            Sheet userMachinerySheet = workbook.createSheet("用户农机数据");
            userMachinerySheet = userMachinerySheetData(userMachinerySheet, userMap);
            Sheet orderSheet = workbook.createSheet("用户订单数据");
            orderSheet = orderSheetData(orderSheet, userMap);*/

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream("C:/Users/24677/Desktop/托管之家数据" + new Date().getTime() + ".xlsx");
                workbook.write(outputStream);
                System.out.println("==================成功===================");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    /**
     * 用户数据输出
     *
     * @param userSheet
     * @param userMap
     * @return
     */
    private Sheet userSheetData(Sheet userSheet, Map<Integer, User> userMap) {
        Map<Integer, UserInfo> userInfoMap = new HashMap<>();
        List<UserInfo> allUserInfos = userInfoRepository.findAll();
        /*for (UserInfo userInfo : allUserInfos) {
            userInfoMap.put(userInfo.getIdUser(), userInfo);
        }*/

        int rowIndex = 0;
        for (int i = 1; i <= userMap.size(); i++) {
            Row row = userSheet.createRow(rowIndex);
            User user = userMap.get(i);
            UserInfo userInfo = userInfoMap.get(i);
            row.createCell(0).setCellValue(user.getNickname());
            row.createCell(1).setCellValue(user.getMobile());
            row.createCell(2).setCellValue(user.getPassword());
            row.createCell(3).setCellValue(DateUtils.formatDate(user.getRegistTime(), "yyyy-MM-dd HH:mm:ss"));
            row.createCell(4).setCellValue(DateUtils.formatDate(user.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            row.createCell(5).setCellValue(user.getFarmer());
            row.createCell(6).setCellValue(user.getOperator());
            row.createCell(7).setCellValue(user.getUserState());
            row.createCell(8).setCellValue(user.getAvatar());
            row.createCell(9).setCellValue(user.getAndroidMAC());
            if (userInfo != null) {
                row.createCell(10).setCellValue(userInfo.getRealname());
                row.createCell(11).setCellValue(userInfo.getIdCardNo());
            }
            List<Farmland> farmlands = farmlandRepository.findByIdUserAndFarmlandStateNot(user.getIdUser(), FarmlandStateEnum.DELETED.getState());

            Set<String> addresses = new HashSet<>();
            if (farmlands != null) {
                for (Farmland farmland : farmlands) {
                    Address farmlandAddress = addressRepository.findByIdAddress(farmland.getIdAddress());
                    if (farmlandAddress != null) {
                        addresses.add(farmlandAddress.getCounty());
                    }
                }
            }
            String address = "";
            Iterator<String> iterator = addresses.iterator();
            if (iterator.hasNext()) {
                address = address + iterator.next() + "  ";
            }
            row.createCell(12).setCellValue(address);

            rowIndex++;
        }
        return userSheet;
    }

    /**
     * 用户土地输出
     *
     * @param userFarmlandSheet
     * @param userMap
     * @return
     */
    private Sheet userFarmlandSheetData(Sheet userFarmlandSheet, Map<Integer, User> userMap) {
        int rowIndex = 0;
        for (int i = 1; i <= userMap.size(); i++) {
            Row row = userFarmlandSheet.createRow(rowIndex);
            User user = userMap.get(i);
            List<Farmland> farmlands = farmlandRepository.findByIdUserAndFarmlandStateNot(user.getIdUser(), FarmlandStateEnum.DELETED.getState());
            for (Farmland farmland : farmlands) {
                Address address = addressRepository.findByIdAddress(farmland.getIdAddress());
                row.createCell(0).setCellValue(user.getMobile());
                row.createCell(1).setCellValue(farmland.getFarmlandName());
                row.createCell(2).setCellValue(farmland.getFarmlandAcreage());
                row.createCell(3).setCellValue(farmland.getFarmlandState());
                row.createCell(4).setCellValue(address.getProvince());
                row.createCell(5).setCellValue(address.getCity());
                row.createCell(6).setCellValue(address.getCounty());
                row.createCell(7).setCellValue(address.getTown());
                row.createCell(8).setCellValue(address.getLatitude());
                row.createCell(9).setCellValue(address.getLongitude());
                row.createCell(10).setCellValue(address.getDetail());
                row.createCell(11).setCellValue(DateUtils.formatDate(address.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(12).setCellValue(DateUtils.formatDate(address.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(13).setCellValue(DateUtils.formatDate(farmland.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(14).setCellValue(DateUtils.formatDate(farmland.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(15).setCellValue(farmland.getPictures());
                row.createCell(16).setCellValue(farmland.getDetail());
                row.createCell(17).setCellValue(farmland.getIdFarmland());

                rowIndex++;
            }
        }
        return userFarmlandSheet;
    }

    /**
     * 用户农机输出
     *
     * @param userMachinerySheet
     * @param userMap
     * @return
     */
    private Sheet userMachinerySheetData(Sheet userMachinerySheet, Map<Integer, User> userMap) {
        int rowIndex = 0;
        for (int i = 1; i <= userMap.size(); i++) {
            Row row = userMachinerySheet.createRow(rowIndex);
            User user = userMap.get(i);
            List<Machinery> machineries = machineryRepository.findByIdUserAndMachineryStateNot(user.getIdUser(), MachineryStateEnum.DELETED.getState());
            for (Machinery machinery : machineries) {
                row.createCell(0).setCellValue(user.getMobile());
                row.createCell(1).setCellValue(machinery.getMachineryType());
                row.createCell(2).setCellValue(machinery.getMachineryAbility());
                row.createCell(3).setCellValue(machinery.getCount());
                row.createCell(4).setCellValue(machinery.getMachineryState());
                row.createCell(5).setCellValue(DateUtils.formatDate(machinery.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(6).setCellValue(DateUtils.formatDate(machinery.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(7).setCellValue(machinery.getDraggingDevice());
                row.createCell(8).setCellValue(machinery.getRatedPower() != null ? machinery.getRatedPower() : 0);
                row.createCell(9).setCellValue(machinery.getLicenseNum());
                row.createCell(10).setCellValue(machinery.getPictures());
                row.createCell(11).setCellValue(machinery.getDetail());

                rowIndex++;
            }
        }
        return userMachinerySheet;
    }

    /**
     * 用户订单输出
     *
     * @param orderSheet
     * @param userMap
     * @return
     */
    private Sheet orderSheetData(Sheet orderSheet, Map<Integer, User> userMap) {
        int rowIndex = 0;
        for (int i = 1; i <= userMap.size(); i++) {
            Row row = orderSheet.createRow(rowIndex);
            User user = userMap.get(i);
            List<UserOrder> orders = userOrderRepository.findByCreateByAndUserOrderStateNot(user.getIdUser(), OrderStateEnum.DELETED.getState());
            for (UserOrder order : orders) {
                row.createCell(0).setCellValue(user.getMobile());
                row.createCell(1).setCellValue(order.getUserOrderNo());
                row.createCell(2).setCellValue(order.getUserOrderState());
                row.createCell(3).setCellValue(DateUtils.formatDate(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(4).setCellValue(DateUtils.formatDate(order.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(5).setCellValue(order.getIdFarmland());
                row.createCell(6).setCellValue(order.getTarget());
                row.createCell(7).setCellValue(order.getArce());
                row.createCell(8).setCellValue(order.getCropName());
                row.createCell(9).setCellValue(DateUtils.formatDate(order.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
                row.createCell(10).setCellValue(order.getMachineryType());
                row.createCell(11).setCellValue(order.getMachineryNum());
                row.createCell(12).setCellValue(order.getPeriod());
                row.createCell(13).setCellValue(order.getPrice().toString());
                row.createCell(14).setCellValue(order.getGuideName());
                row.createCell(15).setCellValue(order.getGuideMobile());
                row.createCell(16).setCellValue(order.getAssemblyAddress());
                row.createCell(17).setCellValue(order.getLatitude());
                row.createCell(18).setCellValue(order.getLongitude());
                row.createCell(19).setCellValue(order.getReceiveBy() != null ? userMap.get(order.getReceiveBy()).getMobile() : "");
                row.createCell(20).setCellValue(order.getDetail());

                rowIndex++;
            }
        }
        return orderSheet;
    }

    /**************************************************************数据导出**************************************************************/

    /**************************************************************数据导入**************************************************************/

    private String dataUrl = "C:/Users/24677/Desktop/托管之家数据1540358559353.xlsx";

    @Test
    public void importUserData() throws IOException {
        File file = new File(dataUrl);
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(inputStream);
            /*Sheet userSheet = workbook.getSheet("用户数据");
            userSheet = dealUser(userSheet);*/
            /*Sheet userFarmlandSheet = workbook.getSheet("用户土地数据");
            userFarmlandSheet = dealUserFarmland(userFarmlandSheet);*/
            Sheet userMachinerySheet = workbook.getSheet("用户农机数据");
            Sheet orderSheet = workbook.getSheet("用户订单数据");

            outputStream = new FileOutputStream("C:/Users/24677/Desktop/托管之家数据" + new Date().getTime() + ".xlsx");
            workbook.write(outputStream);
            System.out.println("==================成功===================");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }
    }

    private Sheet dealUser(Sheet userSheet) {
        Iterator<Row> userSheetRowIterator = userSheet.rowIterator();
        while (userSheetRowIterator.hasNext()) {
            Row row = userSheetRowIterator.next();
            User user = new User();
            UserInfo userInfo = new UserInfo();

            Iterator<Cell> cellIterator = row.cellIterator();
            int index = 1;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (index) {
                    case 1:
                        user.setNickname(cell.getStringCellValue());
                        break;
                    case 2:
                        user.setMobile(cell.getStringCellValue());
                        break;
                    case 3:
                        user.setPassword(cell.getStringCellValue());
                        break;
                    case 4:
                        Date registTime = DateUtils.parseDate(cell.getStringCellValue());
                        user.setRegistTime(registTime);
                        break;
                    case 5:
                        Date updateTime = DateUtils.parseDate(cell.getStringCellValue());
                        user.setUpdateTime(updateTime);
                        break;
                    case 6:
                        user.setFarmer(cell.getBooleanCellValue());
                        break;
                    case 7:
                        user.setOperator(cell.getBooleanCellValue());
                        break;
                    case 8:
                        user.setUserState(cell.getStringCellValue());
                        break;
                    case 9:
                        user.setAvatar(cell.getStringCellValue());
                        break;
                    case 10:
                        user.setAndroidMAC(cell.getStringCellValue());
                        break;
                    case 11:
                        userInfo.setRealname(cell.getStringCellValue());
                        break;
                    case 12:
                        userInfo.setIdCardNo(cell.getStringCellValue());
                        break;
                }
                index++;
            }

            User user1 = userRepository.findByMobile(user.getMobile());
            if (user1 == null) {
                user = userRepository.save(user);
            }

            User user2 = userRepository.findByMobile(user.getMobile());
            if (userInfo.getRealname() != null && userInfo.getRealname().length() > 0) {
                userInfo.setIdUser(user2.getIdUser());
                UserInfo userInfo1 = userInfoRepository.findByIdUser(userInfo.getIdUser());
                if (userInfo1 == null) {
                    userInfoRepository.save(userInfo);
                    if (user2.getUserState().equals(UserStateEnum.PENDING.getState())) {
                        user2.setUserState(UserStateEnum.NORMOL.getState());
                        userRepository.save(user2);
                    }
                }
            }
        }
        return userSheet;
    }

    private Sheet dealUserFarmland(Sheet userFarmlandSheet) {
        List<Farmland> farmlands = new ArrayList<>();
        Iterator<Row> userFarmlandSheetRowIterator = userFarmlandSheet.rowIterator();
        while (userFarmlandSheetRowIterator.hasNext()) {
            Row row = userFarmlandSheetRowIterator.next();
            Farmland farmland = new Farmland();
            Address address = new Address();

            Iterator<Cell> cellIterator = row.cellIterator();
            int index = 1;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (index) {
                    case 1:
                        User user = userRepository.findByMobile(cell.getStringCellValue());
                        farmland.setIdUser(user.getIdUser());
                        break;
                    case 2:
                        farmland.setFarmlandName(cell.getStringCellValue());
                        break;
                    case 3:
                        double value = cell.getNumericCellValue();
                        farmland.setFarmlandAcreage((float) value);
                        break;
                    case 4:
                        farmland.setFarmlandState(cell.getStringCellValue());
                        break;
                    case 5:
                        address.setProvince(cell.getStringCellValue());
                        break;
                    case 6:
                        address.setCity(cell.getStringCellValue());
                        break;
                    case 7:
                        address.setCounty(cell.getStringCellValue());
                        break;
                    case 8:
                        address.setTown(cell.getStringCellValue());
                        break;
                    case 9:
                        address.setLatitude(cell.getNumericCellValue());
                        break;
                    case 10:
                        address.setLongitude(cell.getNumericCellValue());
                        break;
                    case 11:
                        address.setDetail(cell.getStringCellValue());
                        break;
                    case 12:
                        Date createTime = DateUtils.parseDate(cell.getStringCellValue());
                        address.setCreateDate(createTime);
                        break;
                    case 13:
                        Date updateTime = DateUtils.parseDate(cell.getStringCellValue());
                        address.setUpdateDate(updateTime);
                        break;
                    case 14:
                        Date createDate = DateUtils.parseDate(cell.getStringCellValue());
                        farmland.setCreateDate(createDate);
                        break;
                    case 15:
                        Date updateDate = DateUtils.parseDate(cell.getStringCellValue());
                        farmland.setUpdateDate(updateDate);
                        break;
                    case 16:
                        farmland.setPictures(cell.getStringCellValue());
                        break;
                    case 17:
                        farmland.setDetail(cell.getStringCellValue());
                        break;
                }
                index++;
            }
            if (farmland.getIdUser() >= 380 && farmland.getIdUser() <= 413) {
                address = addressRepository.save(address);

                farmland.setIdAddress(address.getIdAddress());
                farmland = farmlandRepository.save(farmland);

                row.createCell(19).setCellValue(farmland.getIdFarmland());
            }
        }
        return userFarmlandSheet;
    }

    private boolean isString(Cell cell) {
        return cell.getCellTypeEnum().equals(CellType.STRING);
    }

    /**************************************************************数据导入**************************************************************/
}
