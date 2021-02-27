package com.usbsdk;

import comon.error.Common;

public abstract interface CallbackInterface
{
  public abstract Common.ERROR_CODE CallbackMethod(CallbackInfo paramGpComCallbackInfo);
}
