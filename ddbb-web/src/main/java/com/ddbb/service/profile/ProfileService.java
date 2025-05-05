package com.ddbb.service.profile;

import com.ddbb.controller.request.WorkplaceRequest;
import com.ddbb.controller.response.ImagePath;
import com.ddbb.infra.data.mongo.entity.CoachWorkplaceEntity;
import com.ddbb.infra.data.mongo.entity.HallEntity;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import com.ddbb.infra.data.mongo.repo.CoachWorkplaceReop;
import com.ddbb.infra.data.mongo.repo.HallRepo;
import com.ddbb.infra.data.mongo.repo.UserRepo;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProfileService {
    @Autowired
    private CoachWorkplaceReop coachWorkplaceReop;
    @Autowired
    private HallRepo hallRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ImagePathService imagePathService;
    /**
     * 助教添加可出台球房
     * @param workplaceRequest
     */
    public void addWorkplace(WorkplaceRequest workplaceRequest) throws Exception {
        Long qid = workplaceRequest.getQid();
        Long hallId = workplaceRequest.getHallId();

        boolean b = coachWorkplaceReop.isCoachWorkplace(qid, hallId);
        if(b){
            throw new Exception("不可重复添加");
        }
        //查球房经纬度
        HallEntity hall = hallRepo.findByHallId(hallId);
        if(hall == null){
            throw new Exception("hall id error");
        }

        CoachWorkplaceEntity entity = new CoachWorkplaceEntity();
        entity.setAid(SnowflakeIdUtil.getInstance().nextId());
        entity.setQid(qid);
        entity.setHallId(hallId);
        entity.setCoordinate(hall.getCoordinate());
        entity.setWorkHere(workplaceRequest.getWorkHere());

        coachWorkplaceReop.insert(entity);
    }

    /**
     * 添加助教照片
     * @param qid
     * @param files
     */
    public void uploadCoachPhoto(Long qid,List<MultipartFile> files) throws Exception {
        UserEntity user = userRepo.findByQid(qid);
        if(user == null){
            throw new Exception("qid is wrong");
        }
        //1、获取路径
        ImagePath imgPathObj = imagePathService.getUserImageFolderPath(qid);
        String folderPath = imgPathObj.getPhotoHardDiskAbsolutePath();
        //判断文件夹是否存在，不存在则创建
        File imageDir = new File(folderPath);
        if (!imageDir.exists()){
            imageDir.mkdirs();
        }

        List<String> photoList = Optional.ofNullable(user.getPhoto()).orElseGet(ArrayList::new);
        int count = CollectionUtils.isEmpty(photoList)?0: photoList.size();

        //2. 上传文件
        files.forEach(f->{
            //System.out.println(f.getName());
            //System.out.println(f.getOriginalFilename());
            try {
                f.transferTo(new File(folderPath + f.getOriginalFilename()));
                photoList.add(imgPathObj.getPhotoUrlRelativePath() + f.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //3、存库
        UserEntity userUpdate = new UserEntity();
        userUpdate.setQid(qid);
        userUpdate.setPhoto(photoList);
        userRepo.updateByQidWithoutNull(userUpdate);
    }
    /**
     * 添加助教头像
     * @param qid
     * @param multipartFile
     */
    public void uploadCoachAvatar(Long qid, MultipartFile multipartFile) throws Exception {
        UserEntity user = userRepo.findByQid(qid);
        if(user == null){
            throw new Exception("qid is wrong");
        }
        //1、获取路径
        ImagePath imgPathObj = imagePathService.getUserImageFolderPath(qid);
        String folderPath = imgPathObj.getAvatarHardDiskAbsolutePath();
        //判断文件夹是否存在，不存在则创建
        File imageDir = new File(folderPath);
        if (!imageDir.exists()){
            imageDir.mkdirs();
        }

        //头像存在就先删除
        if(StringUtils.isNotBlank(user.getAvatar())){
            String origAvatarFilePath = imagePathService.urlRelativePathToHardDiskAbsoluteFilePath(user.getAvatar());
            File old = new File(origAvatarFilePath);
            if(old.exists()){
                old.delete();
            }
        }


        //2、存盘
        multipartFile.transferTo(new File(folderPath + multipartFile.getOriginalFilename()));

        //3、存库
        UserEntity userUpdate = new UserEntity();
        userUpdate.setQid(qid);
        userUpdate.setAvatar(imgPathObj.getAvatarUrlRelativePath() + multipartFile.getOriginalFilename());
        userRepo.updateByQidWithoutNull(userUpdate);
    }
}
