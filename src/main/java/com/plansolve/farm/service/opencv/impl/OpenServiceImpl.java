package com.plansolve.farm.service.opencv.impl;

import com.plansolve.farm.exception.PhotoFormatException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.CropsDiseaseDTO;
import com.plansolve.farm.model.client.score.DiagnoseScoreDTO;
import com.plansolve.farm.model.client.score.ScoreTaskDTO;
import com.plansolve.farm.model.database.agricultural.CropsDisease;
import com.plansolve.farm.model.database.agricultural.DiagnoseFeedback;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.type.ScoreTypeEnum;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.repository.CropsDiseaseRepository;
import com.plansolve.farm.repository.agricultural.DiagnoseFeedbackRepository;
import com.plansolve.farm.repository.score.ScoreLogRepository;
import com.plansolve.farm.service.common.FileService;
import com.plansolve.farm.service.console.ScoreManageService;
import com.plansolve.farm.service.opencv.OpenService;
import com.plansolve.farm.util.EncacheUtil;
import com.plansolve.farm.util.ImageUtil;
import com.plansolve.farm.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

/**
 * @Author: Andrew
 * @Date: 2019/2/2
 * @Description:
 */
@Slf4j
@Service
public class OpenServiceImpl implements OpenService {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private CropsDiseaseRepository cropsDiseaseRepository;

    @Autowired
    private ScoreManageService scoreManageService;

    @Autowired
    private ScoreLogRepository scoreLogRepository;

    @Autowired
    private DiagnoseFeedbackRepository diagnoseFeedbackRepository;

    @Override
    public String smartCompareImage(String cropType, MultipartFile modelImg) {
        String picture = "";
        //如果图片对象不为空就保存图片到服务器
        try {
            picture = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.OPENCV_PHOTES_IMAGE, modelImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ("" != picture) {
            //opencv中彩色图片转换成灰度图片的方法
            String pictureUrl = FileProperties.fileRealPath + SysConstant.OPENCV_PHOTES_IMAGE + picture;
            double[] size = {80, 80};
            Mat grayImg = ImageUtil.grayAndSize(pictureUrl, size);
            Map<String, Integer> matchImageMap = ImageUtil.smartMatch(cropType, grayImg, picture);
            List<Integer> lists = new ArrayList<>();
            //遍历map获取对比度最高的图片
            for (Map.Entry entry : matchImageMap.entrySet()) {
//                String key = entry.getKey().toString();
                Integer points = (Integer) entry.getValue();
                lists.add(points);
            }
            if (lists.size() > 0) {
                final Integer maxValue = Collections.max(lists);
                if (maxValue > 4) {
                    List<Object> keys = ImageUtil.getKey(matchImageMap, maxValue);
                    String matchImgUrl = (String) keys.get(0);
                    String matchPic = "http:" + FileProperties.fileUrlPath + SysConstant.OPENCV_MODEL_IMAGE + matchImgUrl;
                    log.info("精准匹配结果：" + matchPic);
                    return matchPic;
                }
            }
        }
        return null;
    }

    /**
     * 针对统一格式的图片进行智能匹配
     *
     * @param modelImg
     * @return
     */
    @Override
    public DiagnoseScoreDTO compareImage(User user, String cropType, MultipartFile modelImg) {
        DiagnoseScoreDTO diagnoseScoreDTO = compareImageByPoint(user, cropType, modelImg);
        return diagnoseScoreDTO;
    }

    @Override
    @Transactional
    public DiagnoseFeedback saveDiagnoseFeedback(DiagnoseFeedback diagnoseFeedback) {
        DiagnoseFeedback saved = null;
        if (null != diagnoseFeedback){
            diagnoseFeedback.setCreateTime(new Date());
            saved = diagnoseFeedbackRepository.save(diagnoseFeedback);
        }
        return saved;
    }

    private List<String> getDiseaseList(Map<String, Integer> matchImageMap, List<Integer> lists, List<String> diseaseNames) {
        if (lists.size() > 0) {
            final Integer maxValue = Collections.max(lists);
            List<Object> keys = ImageUtil.getKey(matchImageMap, maxValue);
            for (Object key : keys) {
                log.info("================================识别后的病虫害名称" + key.toString() + "===============================");
                if (diseaseNames.size() < 3) {
                    if (key.toString().lastIndexOf("/") > 0) {
                        diseaseNames.add((String) key.toString().substring(0, key.toString().lastIndexOf("/")));
                    } else {
                        diseaseNames.add((String) key.toString());
                    }
                }
                log.info("添加的病虫害名称：" + key.toString() + "——特征点数：" + maxValue);
            }
            lists.remove(maxValue);
            if (lists.size() > 0 && diseaseNames.size() < 3) {
                getDiseaseList(matchImageMap, lists, diseaseNames);
            }
        }
        return diseaseNames;
    }

    private DiagnoseScoreDTO compareImageByPoint(User user, String cropType, MultipartFile modelImg) {
        DiagnoseScoreDTO diagnoseScoreDTO = new DiagnoseScoreDTO();
        String picture = "";
        //如果图片对象不为空就保存图片到服务器
        try {
            picture = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.OPENCV_PHOTES_IMAGE, modelImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ("" != picture) {
            Mat grayImg = null;
            try {
                //opencv中彩色图片转换成灰度图片的方法
                String pictureUrl = FileProperties.fileRealPath + SysConstant.OPENCV_PHOTES_IMAGE + picture;
                diagnoseScoreDTO.setDiagnosePictureUrl(pictureUrl);
                double[] size = {80, 80};
                grayImg = ImageUtil.grayAndSize(pictureUrl, size);
            } catch (Exception e) {
                throw new PhotoFormatException("");
            }
            if (null != grayImg){
                Map<String, Integer> matchImageMap = ImageUtil.smartMatch(cropType, grayImg, picture);
                if (matchImageMap.size() > 0) {
                    //准备符合条件的集合容器
                    Map<String, Integer> diseaseMaps = new LinkedHashMap<>();
                    List<Integer> lists = new ArrayList<>();
                    //遍历map获取对比度最高的图片
                    for (Map.Entry entry : matchImageMap.entrySet()) {
//                String key = entry.getKey().toString();
                        Integer points = (Integer) entry.getValue();
                        lists.add(points);
                    }
                    //map集合返回病虫害名称和得分
                    Map<String, Integer> diseaseMap = getDiseaseMap(matchImageMap, lists, diseaseMaps);
                    if (diseaseMap != null) {
                        if (diseaseMap.size() > 0) {
                            List<CropsDiseaseDTO> cropsDiseases = new ArrayList<>();
                            for (Map.Entry entry : diseaseMap.entrySet()) {
                                //病虫害的folderName
                                String folderName = entry.getKey().toString();
                                //匹配的分数
                                Integer point = (Integer) entry.getValue();
                                log.info("匹配的病虫害名称是" + folderName+"特征点数是"+point);
                                if (null != folderName) {
                                    CropsDisease cropsDisease = cropsDiseaseRepository.findOneByFolderName(folderName);
                                    if (null != cropsDisease) {
                                        CropsDiseaseDTO cropsDiseaseDTO = new CropsDiseaseDTO();
                                        String diseaseFeature = StringUtil.toString(cropsDisease.getDiseaseFeature());
                                        String regularity = StringUtil.toString(cropsDisease.getRegularity());
                                        String pathogen = StringUtil.toString(cropsDisease.getPathogen());
                                        String treatment = StringUtil.toString(cropsDisease.getTreatment());
                                        cropsDiseaseDTO.setSamples(cropsDisease.getSamples().split(";"));
                                        cropsDiseaseDTO.setDiseaseName(cropsDisease.getDiseaseName());
                                        cropsDiseaseDTO.setDiseaseFeature(diseaseFeature);
                                        cropsDiseaseDTO.setRegularity(regularity);
                                        cropsDiseaseDTO.setPathogen(pathogen);
                                        cropsDiseaseDTO.setTreatment(treatment);
                                        if (point < 40) {
                                            Integer percent = point + 50;
                                            cropsDiseaseDTO.setPercentage(percent + "%");
                                        } else {
                                            cropsDiseaseDTO.setPercentage("90%");
                                        }
                                        cropsDiseases.add(cropsDiseaseDTO);
                                    }
                                }
                            }
                            diagnoseScoreDTO.setCropsDiseases(cropsDiseases);
                            //奖励病虫害诊断积分
//                            EncacheUtil.getOrAddCache(SysConstant.SCORE_CACHE_NAME);
//                            Integer diagnoseTime = scoreLogRepository.countByIdUserAndChangeType(user.getIdUser(), ScoreTypeEnum.DIAGNOSE_SCORE_PLUS.getType());
//                            ScoreTaskDTO scoreTask = null;
//                            if (diagnoseTime < 5) {
//                                scoreTask = scoreManageService.diagnosePointTask(user);
//                            } else {
//                                scoreTask = scoreManageService.getDefaultScoreTask(false, "病虫害诊断积分任务次数已达上限");
//                            }
//                            diagnoseScoreDTO.setDiagnoseScoreData(scoreTask);
                        }
                    }
                }
            }
        }
        return diagnoseScoreDTO;
    }

    private Map<String, Integer> getDiseaseMap(Map<String, Integer> matchImageMap, List<Integer> lists, Map<String, Integer> diseaseMaps) {
        if (lists.size() > 0) {
            final Integer maxValue = Collections.max(lists);
            List<Object> keys = ImageUtil.getKey(matchImageMap, maxValue);
            for (Object key : keys) {
                log.info("================================识别后的病虫害名称" + key.toString() + "===============================");
                if (diseaseMaps.size() < 3) {
                    String floderName = null;
                    if (key.toString().lastIndexOf("/") > 0) {
                        floderName = key.toString().substring(0, key.toString().lastIndexOf("/"));
                    } else {
                        floderName = key.toString();
                    }
                    if (StringUtils.isNotBlank(floderName)){
                        boolean includeed = diseaseMaps.containsKey(floderName);
                        if (includeed){
                            continue;
                        }else {
                            diseaseMaps.put(floderName, maxValue);
                            log.info("添加到map中的病虫害名称：" + key.toString() + "——特征点数：" + maxValue);
                        }
                    }
                }
            }
            lists.remove(maxValue);
            if (lists.size() > 0 && diseaseMaps.size() < 3) {
                getDiseaseMap(matchImageMap, lists, diseaseMaps);
            }
        }
        return diseaseMaps;
    }

}
