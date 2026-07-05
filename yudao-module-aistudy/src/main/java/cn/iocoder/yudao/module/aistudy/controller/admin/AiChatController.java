package cn.iocoder.yudao.module.aistudy.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.chat.AiChatRecordRespVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.chat.AiChatReqVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.chat.AiChatRespVO;
import cn.iocoder.yudao.module.aistudy.framework.dify.DifyChatflowException;
import cn.iocoder.yudao.module.aistudy.service.AiStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 智能辅导")
@RestController
@RequestMapping("/aistudy/chat")
@Validated
public class AiChatController {

    @Resource
    private AiStudyService aiStudyService;

    @PostMapping("/ask")
    @Operation(summary = "提交问题")
    @PreAuthorize("@ss.hasPermission('aistudy:chat:use')")
    public CommonResult<AiChatRespVO> ask(@Valid @RequestBody AiChatReqVO reqVO) {
        return doChat(reqVO);
    }

    @PostMapping("/send")
    @Operation(summary = "发送 AI 辅导消息")
    @PreAuthorize("@ss.hasPermission('aistudy:chat:use')")
    public CommonResult<AiChatRespVO> send(@Valid @RequestBody AiChatReqVO reqVO) {
        return doChat(reqVO);
    }

    private CommonResult<AiChatRespVO> doChat(AiChatReqVO reqVO) {
        try {
            return success(aiStudyService.chat(reqVO));
        } catch (DifyChatflowException ex) {
            return CommonResult.error(1_003_001_001, "AI 服务暂时不可用，请稍后再试");
        }
    }

    @GetMapping("/history")
    @Operation(summary = "查询历史对话记录")
    @PreAuthorize("@ss.hasPermission('aistudy:chat:query')")
    public CommonResult<List<AiChatRecordRespVO>> getHistory(@RequestParam(value = "userId", required = false) Long userId,
                                                             @RequestParam(value = "conversationId", required = false) String conversationId) {
        return success(aiStudyService.getChatHistory(userId, conversationId));
    }
}
