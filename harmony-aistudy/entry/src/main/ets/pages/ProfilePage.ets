import { common } from '@kit.AbilityKit';
import { router } from '@kit.ArkUI';
import { LearningFlowApi } from '../api/LearningFlowApi';
import { AuthStore } from '../common/AuthStore';
import { LearningProfile } from '../model/LearningFlowModels';

@Entry
@Component
struct ProfilePage {
  @State loading: boolean = false;
  @State error: string = '';
  @State profile: LearningProfile | undefined = undefined;

  aboutToAppear(): void {
    void this.prepare();
  }

  async prepare(): Promise<void> {
    await AuthStore.init(getContext(this) as common.Context);
    await this.loadData();
  }

  async loadData(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      this.profile = await LearningFlowApi.profile();
    } catch (err) {
      this.error = (err as Error).message || '加载画像失败';
    } finally {
      this.loading = false;
    }
  }

  build() {
    Scroll() {
      Column({ space: 12 }) {
        Row() {
          Text('学习画像').fontSize(26).fontWeight(FontWeight.Bold)
          Blank()
          Button('报告').height(36).onClick(() => router.pushUrl({ url: 'pages/ReportPage' }))
        }
        .width('100%')
        if (this.loading) {
          Text('加载中...').fontColor('#666666').textAlign(TextAlign.Center).width('100%').padding(24)
        } else if (this.error.length > 0) {
          Text(this.error).fontColor('#D93026').textAlign(TextAlign.Center).width('100%').padding(24)
        } else if (!this.profile) {
          Text('暂无学习画像').fontColor('#666666').textAlign(TextAlign.Center).width('100%').padding(24)
        } else {
          Column({ space: 12 }) {
            this.RowText('总 XP', `${this.profile.totalXp ?? 0}`)
            this.RowText('等级', `${this.profile.levelNo ?? 0}`)
            this.RowText('当前连续学习', `${this.profile.currentStreak ?? 0} 天`)
            this.RowText('最长连续学习', `${this.profile.longestStreak ?? 0} 天`)
            this.RowText('最近活跃', this.profile.lastActiveDate || '-')
            this.RowText('本周 XP', this.profile.weeklyXp || '0')
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
  RowText(label: string, value: string) {
    Row() {
      Text(label).fontSize(15).fontColor('#666666')
      Blank()
      Text(value).fontSize(17).fontWeight(FontWeight.Bold)
    }
    .width('100%')
  }
}
