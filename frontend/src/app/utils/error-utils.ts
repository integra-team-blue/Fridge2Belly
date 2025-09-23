import { HttpErrorResponse } from '@angular/common/http';

export function buildErrorMessage(e: unknown, fallback: string): string {
  const err = e as HttpErrorResponse;
  const body = err?.error as unknown;
  if (typeof body === 'object' && body !== null) {
    const msg = (body as { message?: unknown }).message;
    if (typeof msg === 'string') {
      return msg;
    }
  }
  return typeof err?.message === 'string' && err.message.length ? err.message : fallback;
}
