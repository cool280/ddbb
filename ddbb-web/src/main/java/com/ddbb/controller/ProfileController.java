package com.ddbb.controller;

import com.ddbb.controller.request.ChangeProfileRequest;
import com.ddbb.controller.request.WorkplaceRequest;
import com.ddbb.internal.annotate.DdbbController;
import com.ddbb.service.profile.ProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@DdbbController
@RequestMapping("/profile")
@Slf4j
@Api(tags = "个人资料")
public class ProfileController extends BaseController{
    @Autowired
    private ProfileService profileService;

    @ResponseBody
    @PostMapping("/addPresentHall")
    @ApiOperation(value = "助教添加可出台球房")
    public BaseResult addPresentHall(@RequestBody WorkplaceRequest workplaceRequest){
        if(workplaceRequest.getHallId() == null || workplaceRequest.getQid() == null){
            return BaseResult.ERROR("参数有毛病");
        }
        try {
            profileService.addPresentHall(workplaceRequest);
            return BaseResult.OK();
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.ERROR(e);
        }
    }

    /**
     * 也可使用Map<String,MultipartFile> files来接受多个文件，
     * key - 文件名，v - 文件
     * files.forEach((key, file) -> {
     *             System.out.println("name = " + key) ;
     *             try {
     *                 //file.transferTo(new File("d:\\upload\\" + file.getOriginalFilename())) ;
     *             }catch (Exception e){}
     *         }) ;
     * @param request
     * @param qid
     * @param imageType a（头像）| p(照片)
     * @param files
     * @return
     */
    @ResponseBody
    @PostMapping("/uploadCoachPhoto")
    @ApiOperation(value = "添加助教照片，此接口参数需要使用form格式，多个图片使用同名参数files")
    public BaseResult uploadCoachPhoto(HttpServletRequest request,
                                       @RequestParam("qid") Long qid,
                                       @RequestParam("imageType") String imageType,
                                       @RequestParam List<MultipartFile> files){
        try {
            if("p".equals(imageType)){
                profileService.uploadCoachPhoto(qid,files);
            }else if("a".equals(imageType)){
                profileService.uploadCoachAvatar(qid,files.get(0));
            }else{
                return BaseResult.ERROR("wrong image type");
            }
            return BaseResult.OK();
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.ERROR();
        }
    }

    @ResponseBody
    @PostMapping("/changeProfile")
    @ApiOperation(value = "修改个人资料，修改啥就在参数里写啥，为null的不会更新")
    public BaseResult changeProfile(@RequestBody ChangeProfileRequest request){
        try {
            profileService.changeProfile(request);
            return BaseResult.OK();
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.ERROR(e);
        }
    }

    @ResponseBody
    @PostMapping("/setMyWorkplace")
    @ApiOperation(value = "助教设置工作球房，同时将工作类型设置成驻场助教")
    public BaseResult setMyWorkplace(@RequestBody WorkplaceRequest request){
        try {
            profileService.setMyWorkplace(request);
            return BaseResult.OK();
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.ERROR(e);
        }
    }

    @ResponseBody
    @PostMapping("/setFreeCoach")
    @ApiOperation(value = "助教设置工作类型为自由职业")
    public BaseResult setFreeCoach(@RequestBody WorkplaceRequest request){
        try {
            profileService.setFreeCoach(request);
            return BaseResult.OK();
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResult.ERROR(e);
        }
    }
}
