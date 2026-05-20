package cn.iocoder.linhai.module.ai.controller.admin.learn;

import cn.iocoder.linhai.framework.common.pojo.CommonResult;
import cn.iocoder.linhai.framework.common.util.object.BeanUtils;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.analysis.AiLearningBadgeRespVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.analysis.AiLearningDashboardRespVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.analysis.AiLearningProfileRespVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningBadgeRecordDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningProfileSnapshotDO;
import cn.iocoder.linhai.module.ai.service.learn.analysis.AiLearningAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import static cn.iocoder.linhai.framework.common.pojo.CommonResult.success;
import static cn.iocoder.linhai.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - AI 学习分析")
@RestController
@RequestMapping("/ai/learning/analysis")
@Validated
public class AiLearningAnalysisController {

    @Resource
    private AiLearningAnalysisService learningAnalysisService;

    @GetMapping("/dashboard")
    @Operation(summary = "获取学习总览")
    @Parameter(name = "planId", description = "学习计划编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<AiLearningDashboardRespVO> getDashboard(@RequestParam("planId") Long planId) {
        return success(learningAnalysisService.getDashboard(planId));
    }

    @GetMapping("/profile-list")
    @Operation(summary = "获取学习画像快照列表")
    @Parameter(name = "planId", description = "学习计划编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<List<AiLearningProfileRespVO>> getProfileList(@RequestParam("planId") Long planId) {
        List<AiLearningProfileSnapshotDO> list = learningAnalysisService.getProfileSnapshotList(planId);
        return success(BeanUtils.toBean(list, AiLearningProfileRespVO.class));
    }

    @GetMapping("/badge-list")
    @Operation(summary = "获取学习勋章列表")
    @Parameter(name = "planId", description = "学习计划编号", example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<List<AiLearningBadgeRespVO>> getBadgeList(@RequestParam(value = "planId", required = false) Long planId) {
        List<AiLearningBadgeRecordDO> list = learningAnalysisService.getBadgeRecordList(getLoginUserId(), planId);
        return success(BeanUtils.toBean(list, AiLearningBadgeRespVO.class));
    }

}
