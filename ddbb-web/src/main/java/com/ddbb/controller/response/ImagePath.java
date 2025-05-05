package com.ddbb.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImagePath {
    /**
     * 头像硬盘绝对路径文件夹
     */
    private String avatarHardDiskAbsolutePath;
    /**
     * 照片硬盘绝对路径文件夹
     */
    private String photoHardDiskAbsolutePath;
    /**
     * 头像url相对路径
     */
    private String avatarUrlRelativePath;
    /**
     * 照片url相对路径
     */
    private String photoUrlRelativePath;
}
