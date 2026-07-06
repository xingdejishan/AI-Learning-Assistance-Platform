import { common } from '@kit.AbilityKit';
import { promptAction } from '@kit.ArkUI';
import { LearningFlowApi } from '../api/LearningFlowApi';
import { AuthStore } from '../common/AuthStore';
import { Report } from '../model/LearningFlowModels';

@Entry
@Component
struct ReportPage {
  @State loading: boolean = false;
  @State error: string = '';
  @State report: Report | undefined = undefined;

  aboutToAppear(): void {
    void this.prepare();
  }

  async prepare(): Promise<void> {
    await AuthStore.init(getContext(this) as common.Context);
  }

  async generate(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      this.report = await LearningFlowApi.generateReport();
    } catch (err) {
      this.error = (err as Error).message || '生成报告失败';
      promptAction.showToast({ message: this.error });
    } finally {
      this.loading = false;
    }
  }

  build() {
    Scroll() {
      Column({ space: 12 }) {
        Text('成长报告').fontSize(26).fontWeight(FontWeight.Bold).width('100%')
        Button(this.loading ? '生成中...' : '生成成长报告')
          .width('100%')
          .height(44)
          .enabled(!this.loading)
          .onClick(() => this.generate())
        if (this.loading) {
          Text('加载中...').fontColor('#666666').textAlign(TextAlign.Center).width('100%').padding(24)
        } else if (this.error.length > 0) {
          Text(this.error).fontColor('#D93026').textAlign(TextAlign.Center).width('100%').padding(24)
        } else if (!this.report) {
          Text('点击按钮生成最新成长报告').fontColor('#666666').textAlign(TextAlign.Center).width('100%').padding(24)
        } else {
          Column({ space: 10 }) {
            Text(this.report.title || '学习成长报告').fontSize(20).fontWeight(FontWeight.Bold).width('100%')
            Text(this.report.summary || '').fontSize(15).width('100%')
            this.ListText('优势', this.report.strongPoints || [])
            this.ListText('薄弱点', this.report.weakPoints || [])
            this.ListText('下一步计划', this.report.nextPlan || [])
            if ((this.report.riskReminder || '').length > 0) {
              Text(`风险提醒：${this.report.riskReminder}`).fontSize(14).fontColor('#A15C00').width('100%')
            }
          }
          .padding(16)
          .backgroundColor(Color.White)
          .borderRadius(8)
          .width('100%')
        }
      }
      .padding(20)
      .width('100%')
    }
    .height('100%')
    .backgroundColor('#F7F8FA')
  }

  @Builder
  ListText(title: string, items: string[]) {
    Column({ space: 4 }) {
      Text(title).fontSize(16).fontWeight(FontWeight.Bold).width('100%')
      if (items.length === 0) {
        Text('暂无').fontSize(14).fontColor('#777777').width('100%')
      } else {
        ForEach(items, (item: string) => {
          Text(`· ${item}`).fontSize(14).fontColor('#555555').width('100%')
        }, (item: string) => item)
      }
    }
    .width('100%')
  }
}
