package com.plansolve.farm.service.client.Impl;

import com.plansolve.farm.exception.ParamErrorException;
import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.client.CropsDataDTO;
import com.plansolve.farm.model.client.CropsDiseaseDTO;
import com.plansolve.farm.model.client.GrainMarketDTO;
import com.plansolve.farm.model.database.agricultural.CropsData;
import com.plansolve.farm.model.database.agricultural.CropsDisease;
import com.plansolve.farm.model.database.agricultural.GrainMarket;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.enums.type.GrainMarketTypeEnum;
import com.plansolve.farm.repository.CropsDiseaseRepository;
import com.plansolve.farm.repository.agricultural.CropsDataRepository;
import com.plansolve.farm.repository.agricultural.GrainMarketRepository;
import com.plansolve.farm.service.client.GrainMarketService;
import com.plansolve.farm.util.CropsUtil;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.EnumUtil;
import com.plansolve.farm.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 * @Author: Andrew
 * @Date: 2019/3/29
 * @Description:
 */
@Service
public class GrainMarketServiceImpl implements GrainMarketService {

    @Autowired
    private GrainMarketRepository grainMarketRepository;

    @Autowired
    private CropsDataRepository cropsDataRepository;

    @Autowired
    private CropsDiseaseRepository cropsDiseaseRepository;

    @Override
    @Transactional
    public GrainMarket saveGrainMarket(User user, GrainMarketDTO grainMarket) {
        GrainMarket savedGrainMarket = null;
        if (null != user && null != grainMarket) {
            GrainMarket grain = new GrainMarket();
            grain.setIdUser(user.getIdUser());
            grain.setName(grainMarket.getName());
            grain.setMobile(grainMarket.getMobile());
            grain.setGrainType(grainMarket.getGrainType());
            grain.setMarketType(grainMarket.getMarketType());
            if (grainMarket.getMarketType().equals(SysConstant.GRAIN_BUY_INFORMATION_TYPE)) {
                if (StringUtils.isNotBlank(grainMarket.getMinimumBuyPrice()) && StringUtils.isNotBlank(grainMarket.getHighestBuyPrice())) {
                    BigDecimal min = new BigDecimal(grainMarket.getMinimumBuyPrice());
                    BigDecimal max = new BigDecimal(grainMarket.getHighestBuyPrice());
                    grain.setMinimumBuyPrice(min);
                    grain.setHighestBuyPrice(max);
                } else {
                    throw new ParamErrorException("");
                }
            } else {
                if (StringUtils.isNotBlank(grainMarket.getSalePrice())) {
                    BigDecimal salePrice = new BigDecimal(grainMarket.getSalePrice());
                    grain.setSalePrice(salePrice);
                } else {
                    throw new ParamErrorException("");
                }
            }
            grain.setAmount(Float.parseFloat(grainMarket.getAmount()));
            grain.setAddressDetail(grainMarket.getAddressDetail());
            grain.setValidTime(grainMarket.getValidTime());
            grain.setCreateTime(new Date());
            grain.setValidTime(grainMarket.getValidTime());
            switch (grainMarket.getValidTime()) {
                case SysConstant.GRAIN_WEEK_VALID:
                    grain.setInvalidTime(DateUtils.getDate_PastOrFuture_Day(new Date(), 7));
                    break;
                case SysConstant.GRAIN_HALFMONTH_VALID:
                    grain.setInvalidTime(DateUtils.getDate_PastOrFuture_Day(new Date(), 15));
                    break;
                case SysConstant.GRAIN_MONTH_VALID:
                    grain.setInvalidTime(DateUtils.getDate_PastOrFuture_Month(new Date(), 1));
                    break;
                case SysConstant.GRAIN_QUARTER_VALID:
                    grain.setInvalidTime(DateUtils.getDate_PastOrFuture_Month(new Date(), 3));
                    break;
                case SysConstant.GRAIN_HALFYEAR_VALID:
                    grain.setInvalidTime(DateUtils.getDate_PastOrFuture_Month(new Date(), 6));
                    break;
                case SysConstant.GRAIN_YEAR_VALID:
                    grain.setInvalidTime(DateUtils.getDate_PastOrFuture_Year(new Date(), 1));
                    break;
                case SysConstant.GRAIN_LONG_TERM_VALID:
                    grain.setInvalidTime(null);
                    break;
                default:
                    break;
            }
            savedGrainMarket = grainMarketRepository.save(grain);
        }
        return savedGrainMarket;
    }

    @Override
    public List<GrainMarketDTO> getInformationByUser(Integer pageNumber, Integer pageSize, User user, String infoType) {
        List<GrainMarket> grainMarkets = null;
        Pageable pageable = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "createTime");
        if (SysConstant.GRAIN_ALL_INFORMATION_TYPE.equals(infoType)) {
            grainMarkets = grainMarketRepository.findByIdUser(pageable, user.getIdUser());
        } else {
            grainMarkets = grainMarketRepository.findByIdUserAndMarketType(pageable, user.getIdUser(), infoType);
        }
        List<GrainMarketDTO> grainMarketDTOList = loadGrainMarketDTO(grainMarkets);
        return grainMarketDTOList;
    }

    @Override
    public List<GrainMarketDTO> getInformationByType(Integer pageNumber, Integer pageSize, String infoType) {
        List<GrainMarket> grainMarkets = null;
        Pageable pageable = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "createTime");
        if (SysConstant.GRAIN_ALL_INFORMATION_TYPE.equals(infoType)) {
            grainMarkets = grainMarketRepository.findAll(pageable).getContent();
        } else {
            grainMarkets = grainMarketRepository.findByMarketType(pageable, infoType);
        }
        List<GrainMarketDTO> grainMarketDTOList = loadGrainMarketDTO(grainMarkets);
        return grainMarketDTOList;
    }

    @Override
    @Cacheable(cacheNames = "cacheValues")
    public CropsDataDTO getPlantDataByType(Integer pageNumber, Integer pageSize, String dataType) {
        CropsData cropsData = cropsDataRepository.findOneByCropType(dataType);
        CropsDataDTO cropsDataDTO = new CropsDataDTO();
        if (null != cropsData) {
            cropsDataDTO.setIntro(StringUtil.toString(cropsData.getIntro()));
            cropsDataDTO.setPlantSkill(StringUtil.toString(cropsData.getPlantSkill()));
            List<CropsDisease> cropsDiseasesByIll = null;
            List<CropsDisease> cropsDiseasesByInsect = null;
            switch (dataType) {
                case SysConstant.RICE_TYPE:
                    cropsDiseasesByIll = cropsDiseaseRepository.findByDiseaseType(SysConstant.RICE_ILLNESS_TYPE);
                    cropsDiseasesByInsect = cropsDiseaseRepository.findByDiseaseType(SysConstant.RICE_INSECT_TYPE);
                    if (null != cropsDiseasesByIll) {
                        List<CropsDiseaseDTO> cropsDiseaseByIll = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByIll);
                        cropsDataDTO.setIllnessCrops(cropsDiseaseByIll);
                    }
                    if (null != cropsDiseasesByInsect) {
                        List<CropsDiseaseDTO> cropsDiseaseByInsect = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByInsect);
                        cropsDataDTO.setInsectCrops(cropsDiseaseByInsect);
                    }
                    break;
                case SysConstant.CORN_TYPE:
                    cropsDiseasesByIll = cropsDiseaseRepository.findByDiseaseType(SysConstant.CORN_ILLNESS_TYPE);
                    cropsDiseasesByInsect = cropsDiseaseRepository.findByDiseaseType(SysConstant.CORN_INSECT_TYPE);
                    if (null != cropsDiseasesByIll) {
                        List<CropsDiseaseDTO> cropsDiseaseByIll = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByIll);
                        cropsDataDTO.setIllnessCrops(cropsDiseaseByIll);
                    }
                    if (null != cropsDiseasesByInsect) {
                        List<CropsDiseaseDTO> cropsDiseaseByInsect = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByInsect);
                        cropsDataDTO.setInsectCrops(cropsDiseaseByInsect);
                    }
                    break;
                case SysConstant.SORGHUM_TYPE:
                    cropsDiseasesByIll = cropsDiseaseRepository.findByDiseaseType(SysConstant.SORGHUM_ILLNESS_TYPE);
                    cropsDiseasesByInsect = cropsDiseaseRepository.findByDiseaseType(SysConstant.SORGHUM_INSECT_TYPE);
                    if (null != cropsDiseasesByIll) {
                        List<CropsDiseaseDTO> cropsDiseaseByIll = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByIll);
                        cropsDataDTO.setIllnessCrops(cropsDiseaseByIll);
                    }
                    if (null != cropsDiseasesByInsect) {
                        List<CropsDiseaseDTO> cropsDiseaseByInsect = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByInsect);
                        cropsDataDTO.setInsectCrops(cropsDiseaseByInsect);
                    }
                    break;
                case SysConstant.WHEAT_TYPE:
                    cropsDiseasesByIll = cropsDiseaseRepository.findByDiseaseType(SysConstant.WHEAT_ILLNESS_TYPE);
                    cropsDiseasesByInsect = cropsDiseaseRepository.findByDiseaseType(SysConstant.WHEAT_INSECT_TYPE);
                    if (null != cropsDiseasesByIll) {
                        List<CropsDiseaseDTO> cropsDiseaseByIll = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByIll);
                        cropsDataDTO.setIllnessCrops(cropsDiseaseByIll);
                    }
                    if (null != cropsDiseasesByInsect) {
                        List<CropsDiseaseDTO> cropsDiseaseByInsect = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByInsect);
                        cropsDataDTO.setInsectCrops(cropsDiseaseByInsect);
                    }
                    break;
                case SysConstant.SOYBEAN_TYPE:
                    cropsDiseasesByIll = cropsDiseaseRepository.findByDiseaseType(SysConstant.SOYBEAN_ILLNESS_TYPE);
                    cropsDiseasesByInsect = cropsDiseaseRepository.findByDiseaseType(SysConstant.SOYBEAN_INSECT_TYPE);
                    if (null != cropsDiseasesByIll) {
                        List<CropsDiseaseDTO> cropsDiseaseByIll = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByIll);
                        cropsDataDTO.setIllnessCrops(cropsDiseaseByIll);
                    }
                    if (null != cropsDiseasesByInsect) {
                        List<CropsDiseaseDTO> cropsDiseaseByInsect = CropsUtil.loadCropsDiseaseDTO(cropsDiseasesByInsect);
                        cropsDataDTO.setInsectCrops(cropsDiseaseByInsect);
                    }
                    break;
                default:
                    break;
            }
        }
        return cropsDataDTO;
    }

    private List<GrainMarketDTO> loadGrainMarketDTO(List<GrainMarket> grainMarkets) {
        List<GrainMarketDTO> grainMarketDTOList = new ArrayList<>();
        if (null != grainMarkets) {
            for (GrainMarket grainMarket : grainMarkets) {
                GrainMarketDTO grainMarketDTO = new GrainMarketDTO();
                grainMarketDTO.setGrainType(grainMarket.getGrainType());
                grainMarketDTO.setAmount(grainMarket.getAmount() + "");
                grainMarketDTO.setSalePrice(grainMarket.getSalePrice() + "");
                grainMarketDTO.setMinimumBuyPrice(grainMarket.getMinimumBuyPrice() + "");
                grainMarketDTO.setHighestBuyPrice(grainMarket.getHighestBuyPrice() + "");
                grainMarketDTO.setName(grainMarket.getName());
                grainMarketDTO.setMobile(grainMarket.getMobile());
                grainMarketDTO.setAddressDetail(grainMarket.getAddressDetail());
                grainMarketDTO.setValidTime(grainMarket.getValidTime());
                grainMarketDTO.setCreateTime(DateUtils.formatDateTime(grainMarket.getCreateTime()));
                grainMarketDTOList.add(grainMarketDTO);
            }
        }
        return grainMarketDTOList;
    }

    @Override
    public Page<GrainMarket> getInformationByCondition(Integer pageNumber, Integer pageSize, Long idUser, String marketType, String grainType, String validTime) {
        Pageable pageable = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "idGrainMarket");
        Page<GrainMarket> grainMarketPage;
        if (null != idUser || StringUtils.isNotBlank(marketType) || StringUtils.isNotBlank(grainType) || StringUtils.isNotBlank(validTime)) {
            grainMarketPage = grainMarketRepository.findAll(new Specification<GrainMarket>() {
                @Override
                public Predicate toPredicate(Root<GrainMarket> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<>();
                    Predicate predicate = null;
                    if (null != idUser) {
                        Predicate idUserpredicate = criteriaBuilder.equal(root.get("idUser").as(Integer.class), idUser);
                        if (predicate != null) {
                            predicate = criteriaBuilder.and(predicate, idUserpredicate);
                        } else {
                            predicate = criteriaBuilder.and(idUserpredicate);
                        }
                    }
                    if (!"default".equals(marketType)) {
                        Predicate marketTypePredicate = criteriaBuilder.equal(root.get("marketType").as(String.class), marketType);
                        if (predicate != null) {
                            predicate = criteriaBuilder.and(predicate, marketTypePredicate);
                        } else {
                            predicate = criteriaBuilder.and(marketTypePredicate);
                        }
                    }
                    if (!"default".equals(grainType)) {
                        Predicate grainTypePredicate = criteriaBuilder.equal(root.get("grainType").as(String.class), grainType);
                        if (predicate != null) {
                            predicate = criteriaBuilder.and(predicate, grainTypePredicate);
                        } else {
                            predicate = criteriaBuilder.and(grainTypePredicate);
                        }
                    }
                    if (!"default".equals(validTime)) {
                        Predicate validTypePredicate = criteriaBuilder.equal(root.get("validTime").as(String.class), validTime);
                        if (predicate != null) {
                            predicate = criteriaBuilder.and(predicate, validTypePredicate);
                        } else {
                            predicate = criteriaBuilder.and(validTypePredicate);
                        }
                    }
                    if (null != predicate) {
                        predicates.add(predicate);
                    }
                    if (predicates.size() > 0) {
                        query.where(criteriaBuilder.and(predicates.get(0)));
                    }
                    return query.getRestriction();
                }
            }, pageable);
        } else {
            grainMarketPage = grainMarketRepository.findAll(pageable);
        }
        return grainMarketPage;
    }

    @Override
    public List<GrainMarketDTO> loadGrainMarketWebDTO(List<GrainMarket> grainMarkets) {
        List<GrainMarketDTO> grainMarketDTOList = new ArrayList<>();
        if (null != grainMarkets) {
            for (GrainMarket grainMarket : grainMarkets) {
                GrainMarketDTO grainMarketDTO = new GrainMarketDTO();
                grainMarketDTO.setIdGrainMarket(grainMarket.getIdGrainMarket());
                grainMarketDTO.setIdUser(grainMarket.getIdUser());
                grainMarketDTO.setName(grainMarket.getName());
                if (StringUtils.isNotBlank(grainMarket.getMarketType())) {
                    grainMarketDTO.setMarketType(EnumUtil.getByType(grainMarket.getMarketType(), GrainMarketTypeEnum.class).getMessage());
                }
                if (StringUtils.isNotBlank(grainMarket.getGrainType())) {
                    grainMarketDTO.setGrainType(EnumUtil.getByType(grainMarket.getGrainType(), GrainMarketTypeEnum.class).getMessage());
                }
                grainMarketDTO.setAmount(grainMarket.getAmount() + "");
                if (null != grainMarket.getSalePrice()) {
                    grainMarketDTO.setSalePrice(grainMarket.getSalePrice() + "");
                } else {
                    grainMarketDTO.setSalePrice("");
                }
                if (null != grainMarket.getMinimumBuyPrice()) {
                    grainMarketDTO.setMinimumBuyPrice(grainMarket.getMinimumBuyPrice() + "");
                } else {
                    grainMarketDTO.setMinimumBuyPrice("");
                }
                if (null != grainMarket.getHighestBuyPrice()) {
                    grainMarketDTO.setHighestBuyPrice(grainMarket.getHighestBuyPrice() + "");
                } else {
                    grainMarketDTO.setHighestBuyPrice("");
                }
                grainMarketDTO.setName(grainMarket.getName());
                grainMarketDTO.setMobile(grainMarket.getMobile());
                grainMarketDTO.setAddressDetail(grainMarket.getAddressDetail());
                if (StringUtils.isNotBlank(grainMarket.getValidTime())) {
                    grainMarketDTO.setValidTime(EnumUtil.getByType(grainMarket.getValidTime(), GrainMarketTypeEnum.class).getMessage());
                }
                grainMarketDTO.setCreateTime(DateUtils.formatDateTime(grainMarket.getCreateTime()));
                grainMarketDTOList.add(grainMarketDTO);
            }
        }
        return grainMarketDTOList;
    }

}
