package com.nautilus.nat.component;

import com.nautilus.nat.model.BoundingBox;

import java.util.List;

public interface UIActionListener {
  List<BoundingBox> getBoundingBoxes();
  void saveBoundingBoxes();
}
