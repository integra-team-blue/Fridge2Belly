import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private _active = signal(false);

  show() {
    this._active.set(true);
  }

  hide() {
    this._active.set(false);
  }

  active() {
    return this._active();
  }
}
