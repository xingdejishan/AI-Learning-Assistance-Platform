package cn.iocoder.yudao.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动成功后，输出 AI伴学项目运行提示。
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS); // 延迟 1 秒，保证输出到结尾
                log.info("\n----------------------------------------------------------\n\t" +
                        "AI伴学项目启动成功！\n\t" +
                        "后端服务已就绪，可通过前端登录进入系统。\n" +
                        "----------------------------------------------------------");

            // 数据报表
            if (isNotPresent("cn.iocoder.yudao.module.report.framework.security.config.SecurityConfiguration")) {
                System.out.println("[数据报表模块 - 已禁用]");
            }
            // 工作流
            if (isNotPresent("cn.iocoder.yudao.module.bpm.framework.flowable.config.BpmFlowableConfiguration")) {
                System.out.println("[流程模块 - 已禁用]");
            }
            // 交易模块
            if (isNotPresent("cn.iocoder.yudao.module.trade.framework.web.config.TradeWebConfiguration")) {
                System.out.println("[交易模块 - 已禁用]");
            }
            // 业务管理模块
            if (isNotPresent("cn.iocoder.yudao.module.erp.framework.web.config.ErpWebConfiguration")) {
                System.out.println("[业务管理模块 - 已禁用]");
            }
            // 仓储模块
            if (isNotPresent("cn.iocoder.yudao.module.wms.framework.web.config.WmsWebConfiguration")) {
                System.out.println("[仓储模块 - 已禁用]");
            }
            // 客户管理模块
            if (isNotPresent("cn.iocoder.yudao.module.crm.framework.web.config.CrmWebConfiguration")) {
                System.out.println("[客户管理模块 - 已禁用]");
            }
            // 制造模块
            if (isNotPresent("cn.iocoder.yudao.module.mes.framework.web.config.MesWebConfiguration")) {
                System.out.println("[制造模块 - 已禁用]");
            }
            // 社交账号模块
            if (isNotPresent("cn.iocoder.yudao.module.mp.framework.mp.config.MpConfiguration")) {
                System.out.println("[社交账号模块 - 已禁用]");
            }
            // 支付模块
            if (isNotPresent("cn.iocoder.yudao.module.pay.framework.pay.config.PayConfiguration")) {
                System.out.println("[支付模块 - 已禁用]");
            }
            // AI 模块
            if (isNotPresent("cn.iocoder.yudao.module.ai.framework.web.config.AiWebConfiguration")) {
                System.out.println("[AI 模块 - 已禁用]");
            }
            // 设备接入模块
            if (isNotPresent("cn.iocoder.yudao.module.iot.framework.web.config.IotWebConfiguration")) {
                System.out.println("[设备接入模块 - 已禁用]");
            }
            // 消息通讯模块
            if (isNotPresent("cn.iocoder.yudao.module.im.framework.web.config.ImWebConfiguration")) {
                System.out.println("[消息通讯模块 - 已禁用]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
