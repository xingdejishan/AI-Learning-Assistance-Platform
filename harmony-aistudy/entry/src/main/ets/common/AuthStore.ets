import { common } from '@kit.AbilityKit';
import { preferences } from '@kit.ArkData';
import { AppConfig } from './AppConfig';

const STORE_NAME = 'aistudy_auth';
const KEY_BASE_URL = 'baseUrl';
const KEY_TOKEN = 'accessToken';

export class AuthStore {
  private static pref?: preferences.Preferences;
  private static baseUrl: string = AppConfig.DEFAULT_BASE_URL;
  private static token: string = '';

  static async init(context: common.Context): Promise<void> {
    if (!AuthStore.pref) {
      AuthStore.pref = await preferences.getPreferences(context, STORE_NAME);
      AuthStore.baseUrl = (await AuthStore.pref.get(KEY_BASE_URL, AppConfig.DEFAULT_BASE_URL)) as string;
      AuthStore.token = (await AuthStore.pref.get(KEY_TOKEN, '')) as string;
    }
  }

  static getBaseUrl(): string {
    return AuthStore.baseUrl;
  }

  static getToken(): string {
    return AuthStore.token;
  }

  static async save(baseUrl: string, token: string): Promise<void> {
    const nextBaseUrl = baseUrl.trim();
    AuthStore.baseUrl = nextBaseUrl.endsWith('/') ? nextBaseUrl.substring(0, nextBaseUrl.length - 1) : nextBaseUrl;
    AuthStore.token = token.trim();
    if (AuthStore.pref) {
      await AuthStore.pref.put(KEY_BASE_URL, AuthStore.baseUrl);
      await AuthStore.pref.put(KEY_TOKEN, AuthStore.token);
      await AuthStore.pref.flush();
    }
  }
}
