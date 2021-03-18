package com.usbsdk;

import android.content.Context;

import java.net.InetAddress;

import comon.error.Common;

public class DeviceParameters
{
  public Common.PORT_TYPE PortType;
  public String PortName;
  public int PortNumber;
  public String IPAddress;
  public char DeviceID;
  public String DeviceName;
  public Context ApplicationContext;

  public DeviceParameters()
  {
    this.PortType = Common.PORT_TYPE.ETHERNET;
    this.PortName = "";
    this.PortNumber = 9100;
    this.IPAddress = "192.168.192.168";
    this.DeviceID = '\000';
    this.DeviceName = "";
    this.ApplicationContext = null;
  }

  public Common.ERROR_CODE validateParameters()
  {
    Common.ERROR_CODE retval = Common.ERROR_CODE.SUCCESS;

    switch (this.PortType)
    {
    case SERIAL:
      break;
    case PARALLEL:
      break;
    case USB:
      if (this.ApplicationContext == null)
      {
        retval = Common.ERROR_CODE.INVALID_APPLICATION_CONTEXT;
      }
      break;
    case ETHERNET:
      if (this.PortNumber <= 0)
      {
        retval = Common.ERROR_CODE.INVALID_PORT_NUMBER;
      }
      else if (this.IPAddress.length() != 0)
      {
        try
        {
          InetAddress.getByName(this.IPAddress);
        }
        catch (Exception e)
        {
          retval = Common.ERROR_CODE.INVALID_IP_ADDRESS;
        }

      }
      else
      {
        retval = Common.ERROR_CODE.INVALID_IP_ADDRESS;
      }
      break;
    default:
      retval = Common.ERROR_CODE.INVALID_PORT_TYPE;
    }

    return retval;
  }

  public DeviceParameters copy()
  {
    DeviceParameters dp = new DeviceParameters();
    dp.PortType = this.PortType;
    dp.PortName = this.PortName;
    dp.PortNumber = this.PortNumber;
    dp.IPAddress = this.IPAddress;
    dp.DeviceID = this.DeviceID;
    dp.DeviceName = this.DeviceName;
    dp.ApplicationContext = this.ApplicationContext;

    return dp;
  }
}
