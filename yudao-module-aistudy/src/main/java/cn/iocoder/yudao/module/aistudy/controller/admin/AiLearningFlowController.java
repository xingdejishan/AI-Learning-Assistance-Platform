package cn.iocoder.yudao.module.aistudy.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow.*;
import cn.iocoder.yudao.module.aistudy.service.AiLearningFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 学习闭环")
@RestController
@RequestMapping("/aistudy")
@Validated
public class AiLearningFlowController {

    @Resource
    private AiLearningFlowService aiLearningFlowService;

    @GetMapping("/learning-flow/summary")
    @Operation(summary = "学习闭环概览")
    @PreAuthorize("@ss.hasPermission('aistudy:learning-flow:query')")
    public CommonResult<AiLearningFlowSummaryRespVO> getSummary() {
        return success(aiLearningFlowService.getSummary(SecurityFrameworkUtils.getLoginUserId()));
    }

    @GetMapping("/review/today")
    @Operation(summary = "今日复习")
    @PreAuthorize("@ss.hasPermission('aistudy:review:query')")
    public CommonResult<List<AiKnowledgePointRespVO>> getTodayReviews() {
        return success(aiLearningFlowService.getTodayReviews(SecurityFrameworkUtils.getLoginUserId()));
    }

    @PostMapping("/review/submit")
    @Operation(summary = "提交复习结果")
    @PreAuthorize("@ss.hasPermission('aistudy:review:submit')")
    public CommonResult<AiReviewSubmitRespVO> submitReview(@Valid @RequestBody AiReviewSubmitReqVO reqVO) {
        return success(aiLearningFlowService.reviewKnowledgePoint(SecurityFrameworkUtils.getLoginUserId(),
                reqVO.getKnowledgePointId(), reqVO.getRemembered()));
    }

    @GetMapping("/profile")
    @Operation(summary = "学习画像")
    @PreAuthorize("@ss.hasPermission('aistudy:profile:query')")
    public CommonResult<AiUserLearningProfileRespVO> getProfile() {
        return success(aiLearningFlowService.getProfile(SecurityFrameworkUtils.getLoginUserId()));
    }

    @PostMapping("/report/generate")
    @Operation(summary = "生成学习报告")
    @PreAuthorize("@ss.hasPermission('aistudy:report:generate')")
    public CommonResult<AiReportRespVO> generateReport() {
        return success(aiLearningFlowService.generateReport(SecurityFrameworkUtils.getLoginUserId()));
    }
}
