package com.plansolve.farm.repository.user;

import com.plansolve.farm.model.database.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 用户基本信息
 **/

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    public List<User> findByUserStateNotAndIsOperatorAndIdUserNot(String userState, Boolean isOperator, Long idUser);

    public User findByIdUser(Long idUser);

    public User findByMobile(String mobile);

    public User findByMobileAndUserState(String mobile, String userState);

    public List<User> findByMobileLike(String mobile);

    public List<User> findAllByIdCooperation(Integer idCooperation);

    public Long countByIdCooperation(Integer idCooperation);

    public List<User> findAllByIdCooperationNotNull();

    public Page<User> findAllByIdCooperationNotNull(Pageable pageable);

    public Long countByIdCooperationNotNull();

    public List<User> findAllByRegistIdAndIdUserIsNot(String registId, Long idUser);

    public List<User> findByIdCooperationAndIdTeam(Integer idCooperation, Integer idTeam);

    public List<User> findAllByIdCooperationAndUserStateNot(Integer idCooperation, String userState);

    public CopyOnWriteArrayList<User> findAllByIdCooperationAndIdTeamIsNotAndUserStateNot(Integer idCooperation, Integer idTeam, String userState);

    public CopyOnWriteArrayList<User> findAllByIdCooperationAndIsOperatorAndIdTeamIsNotAndUserStateNot(Integer idCooperation, boolean isOperator, Integer idTeam, String userState);

    public CopyOnWriteArrayList<User> findAllByIdCooperationAndIsOperatorAndUserStateNot(Integer idCooperation, boolean isOperator, String userState);

    /**************************************用户数量统计**************************************/
    public Integer countByRegistTimeBefore(Date date);

    public Integer countByRegistTimeBetween(Date dateBegin, Date dateEnd);

    public Integer countByRegistTimeBeforeAndUserStateEquals(Date date, String userState);

    public Integer countByRegistTimeBetweenAndUserStateEquals(Date dateBegin, Date dateEnd, String userState);

    @Query(nativeQuery = true, value = "select count(distinct farm.user.id_user) " +
            "from farm.farmland left join farm.address on farmland.id_address = address.id_address  left join farm.user on farmland.id_user = user.id_user " +
            "where address.city = :city and user.regist_time < :date")
    public Integer placeAllUser(@Param("city") String city, @Param("date") Date date);

    @Query(nativeQuery = true, value = "select count(distinct farm.user.id_user) " +
            "from farm.farmland left join farm.address on farmland.id_address = address.id_address  left join farm.user on farmland.id_user = user.id_user " +
            "where address.city = :city and user.regist_time < :dateEnd and user.regist_time > :dateBegin")
    public Integer placeUser(@Param("city") String city, @Param("dateBegin") Date dateBegin, @Param("dateEnd") Date dateEnd);

    @Query(nativeQuery = true, value = "select count(distinct farm.user.id_user) " +
            "from farm.farmland left join farm.address on farmland.id_address = address.id_address  left join farm.user on farmland.id_user = user.id_user " +
            "where address.city = :city and user.user_state = 'normal' and user.regist_time < :date")
    public Integer placeAllNormalUser(@Param("city") String city, @Param("date") Date date);

    @Query(nativeQuery = true, value = "select count(distinct farm.user.id_user) " +
            "from farm.farmland left join farm.address on farmland.id_address = address.id_address  left join farm.user on farmland.id_user = user.id_user " +
            "where address.city = :city and user.user_state = 'normal' and user.regist_time < :dateEnd and user.regist_time > :dateBegin")
    public Integer placeNormalUser(@Param("city") String city, @Param("dateBegin") Date dateBegin, @Param("dateEnd") Date dateEnd);

    @Query(nativeQuery = true, value = "SELECT u.regist_time from user u GROUP BY SUBSTRING(u.regist_time, 1, 10)")
    public List<Date> queryUserGroupByRegistTime();

}
