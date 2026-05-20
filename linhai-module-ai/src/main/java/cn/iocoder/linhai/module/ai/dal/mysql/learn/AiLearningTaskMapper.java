package cn.iocoder.linhai.module.ai.dal.mysql.learn;

import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningTaskDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 学习任务 Mapper
 *
 * @author qzt
 */
@Mapper
public interface AiLearningTaskMapper extends BaseMapperX<AiLearningTaskDO> {
}
