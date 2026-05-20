package cn.iocoder.linhai.module.ai.dal.mysql.learn;

import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningExamQuestionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 学习考试题目 Mapper
 *
 * @author qzt
 */
@Mapper
public interface AiLearningExamQuestionMapper extends BaseMapperX<AiLearningExamQuestionDO> {

    default List<AiLearningExamQuestionDO> selectListByExamId(Long examId) {
        return selectList(new LambdaQueryWrapperX<AiLearningExamQuestionDO>()
                .eq(AiLearningExamQuestionDO::getExamId, examId)
                .orderByAsc(AiLearningExamQuestionDO::getQuestionNo));
    }

}
