package com.ddbb.controller.response;

import java.util.List;

public interface IWithPhotoVO {
    void setPhoto(List<String> photo);
    List<String> getPhoto();
}
