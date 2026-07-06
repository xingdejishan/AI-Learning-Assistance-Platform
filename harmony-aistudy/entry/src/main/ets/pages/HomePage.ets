import { router } from '@kit.ArkUI';

@Entry
@Component
struct HomePage {
  build() {
    Scroll() {
      Column({ space: 16 }) {
        Row() {
          Text('AI伴学')
            .fontSize(28)
            .fontWeight(FontWeight.Bold)
          Blank()
          Button('配置')
            .height(36)
            .onClick(() => router.pushUrl({ url: 'pages/LoginPage' }))
        }
        .width('100%')
        this.MenuCard('技能树', '查看技能掌握度，选择技能开始伴学', 'pages/SkillPage')
        this.MenuCard('AI 伴学对话', '直接进入默认技能对话', 'pages/ChatPage')
        this.MenuCard('今日复习', '完成间隔复习并更新记忆阶段', 'pages/ReviewPage')
        this.MenuCard('学习画像 / 成长报告', '查看画像、看板并生成成长报告', 'pages/ProfilePage')
        this.MenuCard('学习闭环看板', '查看 XP、等级、复习数和最近事件', 'pages/DashboardPage')
        this.MenuCard('成长报告', '生成 AI 学习成长报告', 'pages/ReportPage')
      }
      .padding(20)
      .width('100%')
    }
    .height('100%')
    .backgroundColor('#F7F8FA')
  }

  @Builder
  MenuCard(title: string, desc: string, url: string) {
    Column({ space: 8 }) {
      Text(title).fontSize(20).fontWeight(FontWeight.Bold).width('100%')
      Text(desc).fontSize(14).fontColor('#666666').width('100%')
    }
    .padding(16)
    .width('100%')
    .backgroundColor(Color.White)
    .borderRadius(8)
    .onClick(() => router.pushUrl({ url }))
  }
}
