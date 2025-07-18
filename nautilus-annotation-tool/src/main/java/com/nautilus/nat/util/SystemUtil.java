package com.nautilus.nat.util;

public final class SystemUtil {
  public static int OS_UNKNOWN = 0;
  public static int OS_WINDOWS = 1;
  public static int OS_LINUX = 2;
  public static int OS_MACOS = 3;

  public static int detectOS() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) {
      return OS_WINDOWS;
    } else if (osName.contains("mac") || osName.contains("osx")) {
      return OS_MACOS;
    } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
      return OS_LINUX;
    }
    return OS_UNKNOWN;
  }
}
