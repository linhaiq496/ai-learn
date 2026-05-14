package cn.iocoder.linhai.module.mes.dal.mysql.cal.team;

import cn.iocoder.linhai.framework.common.pojo.PageResult;
import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.mes.controller.admin.cal.team.vo.MesCalTeamPageReqVO;
import cn.iocoder.linhai.module.mes.dal.dataobject.cal.team.MesCalTeamDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 班组 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesCalTeamMapper extends BaseMapperX<MesCalTeamDO> {

    default PageResult<MesCalTeamDO> selectPage(MesCalTeamPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesCalTeamDO>()
                .likeIfPresent(MesCalTeamDO::getCode, reqVO.getCode())
                .likeIfPresent(MesCalTeamDO::getName, reqVO.getName())
                .eqIfPresent(MesCalTeamDO::getCalendarType, reqVO.getCalendarType())
                .orderByDesc(MesCalTeamDO::getId));
    }

    default MesCalTeamDO selectByCode(String code) {
        return selectOne(MesCalTeamDO::getCode, code);
    }

}
