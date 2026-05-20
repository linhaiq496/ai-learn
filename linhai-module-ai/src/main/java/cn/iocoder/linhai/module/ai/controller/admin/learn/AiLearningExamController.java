package cn.iocoder.linhai.module.ai.controller.admin.learn;

import cn.iocoder.linhai.framework.common.pojo.CommonResult;
import cn.iocoder.linhai.framework.common.util.object.BeanUtils;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam.AiLearningExamGenerateReqVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam.AiLearningExamQuestionRespVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam.AiLearningExamRespVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam.AiLearningExamSubmitReqVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningExamQuestionDO;
import cn.iocoder.linhai.module.ai.service.learn.exam.AiLearningExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.linhai.framework.common.pojo.CommonResult.success;
import static cn.iocoder.linhai.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - AI 学习考试")
@RestController
@RequestMapping("/ai/learning/exam")
@Validated
public class AiLearningExamController {

    @Resource
    private AiLearningExamService learningExamService;

    @GetMapping("/get")
    @Operation(summary = "获得学习考试")
    @Parameter(name = "id", description = "考试编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<AiLearningExamRespVO> getExam(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(learningExamService.getExam(id), AiLearningExamRespVO.class));
    }

    @GetMapping("/question-list")
    @Operation(summary = "获取考试题目列表")
    @Parameter(name = "examId", description = "考试编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<List<AiLearningExamQuestionRespVO>> getQuestionList(@RequestParam("examId") Long examId) {
        List<AiLearningExamQuestionDO> list = learningExamService.getQuestionListByExamId(examId);
        return success(BeanUtils.toBean(list, AiLearningExamQuestionRespVO.class));
    }

    @PostMapping("/generate")
    @Operation(summary = "生成学习考试")
    @PreAuthorize("@ss.hasPermission('ai:learning:create')")
    public CommonResult<Long> createExam(@Valid @RequestBody AiLearningExamGenerateReqVO createReqVO) {
        return success(learningExamService.createExam(getLoginUserId(), createReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交学习考试")
    @PreAuthorize("@ss.hasPermission('ai:learning:update')")
    public CommonResult<Boolean> submitExam(@Valid @RequestBody AiLearningExamSubmitReqVO submitReqVO) {
        learningExamService.submitExam(getLoginUserId(), submitReqVO);
        return success(true);
    }

}
