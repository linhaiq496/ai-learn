package cn.iocoder.linhai.module.ai.dal.mysql.learn;

import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningMaterialDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 学习资料 Mapper
 *
 * @author qzt
 */
@Mapper
public interface AiLearningMaterialMapper extends BaseMapperX<AiLearningMaterialDO> {

    default List<AiLearningMaterialDO> selectListByPhaseId(Long phaseId) {
        return selectList(new LambdaQueryWrapperX<AiLearningMaterialDO>()
                .eq(AiLearningMaterialDO::getPhaseId, phaseId)
                .orderByAsc(AiLearningMaterialDO::getSort, AiLearningMaterialDO::getId));
    }

}
