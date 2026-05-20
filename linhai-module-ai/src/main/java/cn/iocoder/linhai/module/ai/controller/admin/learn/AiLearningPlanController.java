package cn.iocoder.linhai.module.ai.controller.admin.learn;

import cn.iocoder.linhai.framework.common.pojo.CommonResult;
import cn.iocoder.linhai.framework.common.pojo.PageResult;
import cn.iocoder.linhai.framework.common.util.object.BeanUtils;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan.AiLearningPlanGenerateReqVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan.AiLearningPlanPageReqVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan.AiLearningPlanRespVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan.AiLearningPlanSaveReqVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPlanDO;
import cn.iocoder.linhai.module.ai.service.learn.plan.AiLearningPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.linhai.framework.common.pojo.CommonResult.success;
import static cn.iocoder.linhai.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - AI 学习计划")
@RestController
@RequestMapping("/ai/learning/plan")
@Validated
public class AiLearningPlanController {

    @Resource
    private AiLearningPlanService learningPlanService;

    @GetMapping("/page")
    @Operation(summary = "获取学习计划分页")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<PageResult<AiLearningPlanRespVO>> getPlanPage(@Valid AiLearningPlanPageReqVO pageReqVO) {
        PageResult<AiLearningPlanDO> pageResult = learningPlanService.getPlanPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiLearningPlanRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得学习计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
    public CommonResult<AiLearningPlanRespVO> getPlan(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(learningPlanService.getPlan(id), AiLearningPlanRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "创建学习计划")
    @PreAuthorize("@ss.hasPermission('ai:learning:create')")
    public CommonResult<Long> createPlan(@Valid @RequestBody AiLearningPlanSaveReqVO createReqVO) {
        return success(learningPlanService.createPlan(getLoginUserId(), createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新学习计划")
    @PreAuthorize("@ss.hasPermission('ai:learning:update')")
    public CommonResult<Boolean> updatePlan(@Valid @RequestBody AiLearningPlanSaveReqVO updateReqVO) {
        learningPlanService.updatePlan(getLoginUserId(), updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学习计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:learning:delete')")
    public CommonResult<Boolean> deletePlan(@RequestParam("id") Long id) {
        learningPlanService.deletePlan(id);
        return success(true);
    }

    @PostMapping("/generate")
    @Operation(summary = "生成学习规划")
    @PreAuthorize("@ss.hasPermission('ai:learning:update')")
    public CommonResult<Boolean> generatePlan(@Valid @RequestBody AiLearningPlanGenerateReqVO generateReqVO) {
        learningPlanService.generatePlan(generateReqVO.getId());
        return success(true);
    }

}
