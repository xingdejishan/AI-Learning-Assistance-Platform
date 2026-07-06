import { http } from '@kit.NetworkKit';
import { ApiError, ApiResult } from './ApiResult';
import { AuthStore } from './AuthStore';

type QueryValue = string | number | boolean | undefined | null;

export class HttpClient {
  static async get<T>(path: string, params?: Record<string, QueryValue>): Promise<T> {
    return HttpClient.request<T>('GET', path, undefined, params);
  }

  static async post<T>(path: string, data?: Object): Promise<T> {
    return HttpClient.request<T>('POST', path, data);
  }

  private static async request<T>(method: string, path: string, data?: Object, params?: Record<string, QueryValue>): Promise<T> {
    const url = HttpClient.buildUrl(path, params);
    const request = http.createHttp();
    const token = AuthStore.getToken();
    const header: Record<string, string> = {
      'Content-Type': 'application/json'
    };
    if (token.length > 0) {
      header['Authorization'] = `Bearer ${token}`;
    }

    try {
      const response = await request.request(url, {
        method: method === 'GET' ? http.RequestMethod.GET : http.RequestMethod.POST,
        header,
        extraData: data ? JSON.stringify(data) : undefined,
        expectDataType: http.HttpDataType.STRING,
        connectTimeout: 15000,
        readTimeout: 30000
      });
      const text = typeof response.result === 'string' ? response.result : '';
      const result = JSON.parse(text) as ApiResult<T>;
      if (result.code !== 0) {
        throw new ApiError(result.code, result.msg || '请求失败');
      }
      return result.data as T;
    } catch (err) {
      if (err instanceof ApiError) {
        throw err;
      }
      throw new Error((err as Error).message || '网络请求失败');
    } finally {
      request.destroy();
    }
  }

  private static buildUrl(path: string, params?: Record<string, QueryValue>): string {
    let baseUrl = AuthStore.getBaseUrl();
    const normalizedPath = path.startsWith('/') ? path : `/${path}`;
    let url = `${baseUrl}${normalizedPath}`;
    if (params) {
      const query: string[] = [];
      Object.keys(params).forEach((key: string) => {
        const value = params[key];
        if (value !== undefined && value !== null && `${value}`.length > 0) {
          query.push(`${encodeURIComponent(key)}=${encodeURIComponent(`${value}`)}`);
        }
      });
      if (query.length > 0) {
        url = `${url}?${query.join('&')}`;
      }
    }
    return url;
  }
}
