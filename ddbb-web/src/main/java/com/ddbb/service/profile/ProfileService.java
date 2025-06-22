package com.ddbb.service.profile;

import com.ddbb.controller.request.ChangeProfileRequest;
import com.ddbb.controller.request.WorkplaceRequest;
import com.ddbb.controller.response.ImagePath;
import com.ddbb.infra.data.mongo.entity.CoachWorkplaceEntity;
import com.ddbb.infra.data.mongo.entity.HallEntity;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import com.ddbb.infra.data.mongo.repo.CoachWorkplaceReop;
import com.ddbb.infra.data.mongo.repo.HallRepo;
import com.ddbb.infra.data.mongo.repo.UserRepo;
import com.ddbb.internal.enums.JobType;
import com.ddbb.internal.enums.UserType;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
    public void addPresentHall(WorkplaceRequest workplaceRequest) throws Exception {
        Long uid = workplaceRequest.getUid();
        Long hallId = workplaceRequest.getHallId();
        UserEntity user = userRepo.findByUid(uid);
        if(user.getUserType() == null || UserType.ASSISTANT_COACH.getCode() != user.getUserType()){
            throw new Exception("只有助教才能添加可出台球房");
        }

        boolean b = coachWorkplaceReop.isCoachWorkplace(uid, hallId);
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
        entity.setUid(uid);
        entity.setHallId(hallId);
        entity.setCoordinate(hall.getCoordinate());

        coachWorkplaceReop.insert(entity);
    }

    /**
     * 助教设置工作球房
     * @param workplaceRequest
     */
    public void setMyWorkplace(WorkplaceRequest workplaceRequest) throws Exception {
        Long uid = workplaceRequest.getUid();
        Long hallId = workplaceRequest.getHallId();

        //查球房经纬度
        HallEntity hall = hallRepo.findByHallId(hallId);
        if(hall == null){
            throw new Exception("hall id error");
        }

        UserEntity user = userRepo.findByUid(uid);
        if(user.getUserType() == null || UserType.ASSISTANT_COACH.getCode() != user.getUserType()){
            throw new Exception("只有助教才能设置工作球房");
        }
        //更新user
        user.setJobType(JobType.FIX_HALL.getCode());
        user.setWorkHallId(hallId);
        user.setCoordinate(hall.getCoordinate());

        userRepo.updateByUidWithNull(user);

        //更新可出台球房
        boolean b = coachWorkplaceReop.isCoachWorkplace(uid, hallId);
        if(!b){
            CoachWorkplaceEntity entity = new CoachWorkplaceEntity();
            entity.setAid(SnowflakeIdUtil.getInstance().nextId());
            entity.setUid(uid);
            entity.setHallId(hallId);
            entity.setCoordinate(hall.getCoordinate());

            coachWorkplaceReop.insert(entity);
        }

    }

    /**
     * 助教设置工作类型为自由职业
     * @param workplaceRequest
     */
    public void setFreeCoach(WorkplaceRequest workplaceRequest) throws Exception {
        Long uid = workplaceRequest.getUid();

        UserEntity user = userRepo.findByUid(uid);
        if(user.getUserType() == null || UserType.ASSISTANT_COACH.getCode() != user.getUserType()){
            throw new Exception("只有助教才能设置为自由职业");
        }
        //更新user
        user.setJobType(JobType.FREE_PERSON.getCode());
        user.setWorkHallId(null);
        user.setCoordinate(null);

        userRepo.updateByUidWithNull(user);
    }

    /**
     * 添加助教照片
     * @param uid
     * @param files
     */
    public void uploadCoachPhoto(Long uid,List<MultipartFile> files) throws Exception {
        UserEntity user = userRepo.findByUid(uid);
        if(user == null){
            throw new Exception("uid is wrong");
        }
        //1、获取路径
        ImagePath imgPathObj = imagePathService.getUserImageFolderPath(uid);
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
        userUpdate.setUid(uid);
        userUpdate.setPhoto(photoList);
        userRepo.updateByUidWithoutNull(userUpdate);
    }
    /**
     * 添加助教头像
     * @param uid
     * @param multipartFile
     */
    public void uploadCoachAvatar(Long uid, MultipartFile multipartFile) throws Exception {
        UserEntity user = userRepo.findByUid(uid);
        if(user == null){
            throw new Exception("uid is wrong");
        }
        //1、获取路径
        ImagePath imgPathObj = imagePathService.getUserImageFolderPath(uid);
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
        userUpdate.setUid(uid);
        userUpdate.setAvatar(imgPathObj.getAvatarUrlRelativePath() + multipartFile.getOriginalFilename());
        userRepo.updateByUidWithoutNull(userUpdate);
    }

    public void changeProfile(ChangeProfileRequest request) throws Exception {
        Long uid = request.getUid();
        if(uid == null){
            throw new Exception("uid is null");
        }
        UserEntity user = userRepo.findByUid(uid);
        if(user == null){
            throw new Exception("uid is wrong");
        }

        UserEntity updateUser = new UserEntity();
        BeanUtils.copyProperties(request,updateUser);
        userRepo.updateByUidWithoutNull(updateUser);
    }
}
