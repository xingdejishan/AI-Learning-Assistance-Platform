package cn.iocoder.yudao.module.aistudy.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.dashboard.AiDashboardRespVO;
import cn.iocoder.yudao.module.aistudy.service.AiStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 学习数据看板")
@RestController
@RequestMapping("/aistudy/dashboard")
public class AiDashboardController {

    @Resource
    private AiStudyService aiStudyService;

    @GetMapping("/summary")
    @Operation(summary = "查询学习数据看板")
    @PreAuthorize("@ss.hasPermission('aistudy:dashboard:query')")
    public CommonResult<AiDashboardRespVO> getDashboard() {
        return success(aiStudyService.getDashboard());
    }
}
