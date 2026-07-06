import { common } from '@kit.AbilityKit';
import { promptAction, router } from '@kit.ArkUI';
import { AppConfig } from '../common/AppConfig';
import { AuthStore } from '../common/AuthStore';

@Entry
@Component
struct LoginPage {
  @State baseUrl: string = AppConfig.DEFAULT_BASE_URL;
  @State token: string = '';
  @State saving: boolean = false;

  aboutToAppear(): void {
    void this.prepare();
  }

  async prepare(): Promise<void> {
    await AuthStore.init(getContext(this) as common.Context);
    this.baseUrl = AuthStore.getBaseUrl();
    this.token = AuthStore.getToken();
  }

  build() {
    Scroll() {
      Column({ space: 18 }) {
        Text('AI伴学配置')
          .fontSize(28)
          .fontWeight(FontWeight.Bold)
          .width('100%')
        Text('请填写电脑局域网 IP 的后端地址，并粘贴管理后台登录后的 accessToken。')
          .fontSize(14)
          .fontColor('#666666')
          .width('100%')
        Column({ space: 12 }) {
          Text('后端 baseUrl').fontSize(14).fontWeight(FontWeight.Medium).width('100%')
          TextInput({ text: this.baseUrl, placeholder: AppConfig.DEFAULT_BASE_URL })
            .onChange((value: string) => this.baseUrl = value)
            .height(48)
          Text('Access Token').fontSize(14).fontWeight(FontWeight.Medium).width('100%')
          TextArea({ text: this.token, placeholder: 'Bearer 后面的 token 内容' })
            .onChange((value: string) => this.token = value)
            .height(130)
        }
        .padding(16)
        .backgroundColor(Color.White)
        .borderRadius(8)
        Button(this.saving ? '保存中...' : '保存并进入')
          .width('100%')
          .height(46)
          .enabled(!this.saving)
          .onClick(async () => {
            if (this.baseUrl.trim().length === 0 || this.token.trim().length === 0) {
              promptAction.showToast({ message: 'baseUrl 和 token 不能为空' });
              return;
            }
            this.saving = true;
            try {
              await AuthStore.save(this.baseUrl, this.token);
              router.replaceUrl({ url: 'pages/HomePage' });
            } catch (err) {
              promptAction.showToast({ message: (err as Error).message || '保存失败' });
            } finally {
              this.saving = false;
            }
          })
      }
      .width('100%')
      .padding(20)
    }
    .backgroundColor('#F7F8FA')
    .height('100%')
  }
}
