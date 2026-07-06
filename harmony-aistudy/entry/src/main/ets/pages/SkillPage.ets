import { common } from '@kit.AbilityKit';
import { promptAction, router } from '@kit.ArkUI';
import { SkillApi } from '../api/SkillApi';
import { AuthStore } from '../common/AuthStore';
import { Skill, SkillProgress } from '../model/SkillModels';

@Entry
@Component
struct SkillPage {
  @State loading: boolean = false;
  @State error: string = '';
  @State skills: Skill[] = [];

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
      const list = await SkillApi.list();
      const progressList = await SkillApi.progress();
      this.skills = this.mergeProgress(list || [], progressList || []);
    } catch (err) {
      this.error = (err as Error).message || '加载技能失败';
      promptAction.showToast({ message: this.error });
    } finally {
      this.loading = false;
    }
  }

  mergeProgress(list: Skill[], progressList: SkillProgress[]): Skill[] {
    return list.map((item: Skill) => {
      const progress = progressList.find((p: SkillProgress) => p.skillId === item.id);
      item.progress = progress?.progress;
      item.status = progress?.status ?? item.status;
      item.score = progress?.score;
      item.recommendation = progress?.recommendation;
      return item;
    });
  }

  build() {
    Scroll() {
      Column({ space: 12 }) {
        Row() {
          Text('技能树').fontSize(26).fontWeight(FontWeight.Bold)
          Blank()
          Button('刷新')
            .height(36)
            .enabled(!this.loading)
            .onClick(() => this.loadData())
        }
        .width('100%')
        if (this.loading) {
          Text('加载中...').fontColor('#666666').width('100%').textAlign(TextAlign.Center).padding(24)
        } else if (this.error.length > 0) {
          Text(this.error).fontColor('#D93026').width('100%').textAlign(TextAlign.Center).padding(24)
        } else if (this.skills.length === 0) {
          Text('暂无技能数据').fontColor('#666666').width('100%').textAlign(TextAlign.Center).padding(24)
        } else {
          ForEach(this.skills, (item: Skill) => {
            Column({ space: 8 }) {
              Text(item.name || '未命名技能').fontSize(18).fontWeight(FontWeight.Bold).width('100%')
              Text(`掌握度：${item.progress ?? 0}%    状态：${item.status ?? 0}`).fontSize(14).fontColor('#555555').width('100%')
              if ((item.recommendation || '').length > 0) {
                Text(item.recommendation || '').fontSize(13).fontColor('#777777').width('100%')
              }
            }
            .padding(16)
            .width('100%')
            .backgroundColor(Color.White)
            .borderRadius(8)
            .onClick(() => router.pushUrl({
              url: 'pages/ChatPage',
              params: { skillId: item.id || 0, skillName: item.name || '' }
            }))
          }, (item: Skill) => `${item.id}`)
        }
      }
      .padding(20)
      .width('100%')
    }
    .height('100%')
    .backgroundColor('#F7F8FA')
  }
}
