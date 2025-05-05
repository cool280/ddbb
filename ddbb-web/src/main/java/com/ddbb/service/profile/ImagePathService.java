package com.ddbb.service.profile;

import com.ddbb.controller.response.ImagePath;
import com.ddbb.internal.utils.MD5Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ImagePathService {
    //硬盘上的路径
    @Value("${cbs.imagesPath}")
    private String imagesPath;

    /**
     * 获取用户相关的硬盘路径
     * @param qid
     * @return
     */
    public ImagePath getUserImageFolderPath(Long qid){
        return getImageFolderAbsolutePath("u", MD5Util.encode(qid.toString()));

    }
    /**
     * 获取球房相关的硬盘路径
     * @param hallId    球房id
     * @return
     */
    public ImagePath getHallImageFolderPath(Long hallId){
        return getImageFolderAbsolutePath("h", MD5Util.encode(hallId.toString()));
    }

    /**
     *          D:\\ddbb_images\\u\\2ba8f6124363658463d9f67e3538bab6\\p
     *         D:\\ddbb_images\\u\\2ba8f6124363658463d9f67e3538bab6\\a
     *         D:\\ddbb_images\\h\\2ba8f6124363658463d9f67e3538bab6\\p
     *         D:\\ddbb_images\\h\\2ba8f6124363658463d9f67e3538bab6\\a
     * @param who
     * @param md5Id
     * @return
     */
    private ImagePath getImageFolderAbsolutePath(String who, String md5Id){
        String pathPrefix = getHardDiskPathPrefix();
        String separator = getFileSeparator();

        String avatar = new StringBuffer(100)
                .append(pathPrefix)
                .append(who).append(separator)
                .append(md5Id).append(separator)
                .append("a").append(separator)
                .toString();
        String photo = new StringBuffer(100)
                .append(pathPrefix)
                .append(who).append(separator)
                .append(md5Id).append(separator)
                .append("p").append(separator)
                .toString();
        //u/388be1f7a225e5bcca0cf493eae297a4/a/avatar.jpg
        String avatarUrlRelativePath = new StringBuffer(100)
                .append("/").append(who).append("/").append(md5Id).append("/a/").toString();
        String photoUrlRelativePath = new StringBuffer(100)
                .append("/").append(who).append("/").append(md5Id).append("/p/").toString();

        return ImagePath.builder().avatarHardDiskAbsolutePath(avatar).avatarUrlRelativePath(avatarUrlRelativePath)
                    .photoHardDiskAbsolutePath(photo).photoUrlRelativePath(photoUrlRelativePath).build();

    }
    private String getHardDiskPathPrefix(){
        String separator = getFileSeparator();

        String pathPrefix = imagesPath.replace("file:/","");
        pathPrefix = pathPrefix.replace("/", separator);
        if(!pathPrefix.endsWith(separator)){
            pathPrefix = pathPrefix + separator;
        }
        return pathPrefix;
    }
    private String getFileSeparator(){
        String separator = File.separator;

        if(separator.equals("\\")){
            separator = "\\\\";
        }
        return separator;
    }
    /**
     * url相对路径转硬盘绝对路径
     * @param urlRelativePath
     * /u/388be1f7a225e5bcca0cf493eae297a4/a/avatar.jpg
     * /u/388be1f7a225e5bcca0cf493eae297a4/p/1.jpg
     * @return
     */
    public String urlRelativePathToHardDiskAbsoluteFilePath(String urlRelativePath){
        String pathPrefix = getHardDiskPathPrefix();
        String separator = getFileSeparator();
        if(urlRelativePath.startsWith("/")){
            urlRelativePath = urlRelativePath.substring(1);
        }
        urlRelativePath = urlRelativePath.replace("/",separator);
        return pathPrefix+urlRelativePath;
    }

}
