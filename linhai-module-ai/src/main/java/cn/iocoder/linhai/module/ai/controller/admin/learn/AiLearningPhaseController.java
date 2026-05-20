package cn.iocoder.linhai.module.ai.controller.admin.learn;

import cn.iocoder.linhai.framework.common.pojo.CommonResult;
import cn.iocoder.linhai.framework.common.util.object.BeanUtils;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.phase.AiLearningPhaseMaterialRespVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.phase.AiLearningPhaseRespVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningMaterialDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPhaseDO;
import cn.iocoder.linhai.module.ai.service.learn.phase.AiLearningPhaseService;
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

@Tag(name = "管理后台 - AI 学习阶段")
@RestController
@RequestMapping("/ai/learning/phase")
@Validated
public class AiLearningPhaseController {

    @Resource
    private AiLearningPhaseService learningPhaseService;

    @GetMapping("/list")
    @Operation(summary = "获取学习阶段列表")
    @Parameter(name = "planId", description = "学习计划编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<List<AiLearningPhaseRespVO>> getPhaseList(@RequestParam("planId") Long planId) {
        List<AiLearningPhaseDO> list = learningPhaseService.getPhaseListByPlanId(planId);
        return success(BeanUtils.toBean(list, AiLearningPhaseRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得学习阶段")
    @Parameter(name = "id", description = "阶段编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<AiLearningPhaseRespVO> getPhase(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(learningPhaseService.getPhase(id), AiLearningPhaseRespVO.class));
    }

    @GetMapping("/material-list")
    @Operation(summary = "获取阶段资料列表")
    @Parameter(name = "phaseId", description = "阶段编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<List<AiLearningPhaseMaterialRespVO>> getMaterialList(@RequestParam("phaseId") Long phaseId) {
        List<AiLearningMaterialDO> list = learningPhaseService.getMaterialListByPhaseId(phaseId);
        return success(BeanUtils.toBean(list, AiLearningPhaseMaterialRespVO.class));
    }

}
