package com.nautilus.nat.component;

import javafx.scene.control.Alert;

public class AlertDialogUtil {
  public static void showCommonAlert(Alert.AlertType type, String title, String headerText,
                                     String content) {
    Alert alertDlg = new Alert(type);
    alertDlg.setTitle(title);
    alertDlg.setHeaderText(headerText);
    alertDlg.setContentText(content);
    alertDlg.showAndWait();
  }

  public static Alert buildCommonAlert(Alert.AlertType type, String title, String headerText,
                                       String content) {
    Alert alertDlg = new Alert(type);
    alertDlg.setTitle(title);
    alertDlg.setHeaderText(headerText);
    alertDlg.setContentText(content);
    return alertDlg;
  }
}
