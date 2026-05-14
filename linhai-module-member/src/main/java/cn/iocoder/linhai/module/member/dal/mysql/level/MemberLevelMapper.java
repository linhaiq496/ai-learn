package cn.iocoder.linhai.module.member.dal.mysql.level;

import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.member.controller.admin.level.vo.level.MemberLevelListReqVO;
import cn.iocoder.linhai.module.member.dal.dataobject.level.MemberLevelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员等级 Mapper
 *
 * @author owen
 */
@Mapper
public interface MemberLevelMapper extends BaseMapperX<MemberLevelDO> {

    default List<MemberLevelDO> selectList(MemberLevelListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<MemberLevelDO>()
                .likeIfPresent(MemberLevelDO::getName, reqVO.getName())
                .eqIfPresent(MemberLevelDO::getStatus, reqVO.getStatus())
                .orderByAsc(MemberLevelDO::getLevel));
    }


    default List<MemberLevelDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<MemberLevelDO>()
                .eq(MemberLevelDO::getStatus, status)
                .orderByAsc(MemberLevelDO::getLevel));
    }

}
