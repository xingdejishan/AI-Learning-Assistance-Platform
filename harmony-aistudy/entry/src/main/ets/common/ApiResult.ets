export interface ApiResult<T> {
  code: number;
  data?: T;
  msg?: string;
}

export class ApiError extends Error {
  code: number;

  constructor(code: number, message: string) {
    super(message);
    this.code = code;
  }
}
