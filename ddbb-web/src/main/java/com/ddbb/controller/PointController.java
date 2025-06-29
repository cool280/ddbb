package com.ddbb.controller;

import com.ddbb.controller.request.point.PointRequest;
import com.ddbb.controller.response.point.PointQueryResponse;
import com.ddbb.controller.response.point.SignInStatusResponse;
import com.ddbb.internal.annotate.DdbbController;
import com.ddbb.service.point.PointUpdateService;
import com.ddbb.service.point.PointQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@DdbbController
@RequestMapping("/points")
@Api(tags = "用户积分")
public class PointController extends BaseController {

    @Autowired
    private PointQueryService pointQueryService;

    @Autowired
    private PointUpdateService pointUpdateService;

    @ApiOperation(value = "我的积分")
    @ApiResponses(value = {@ApiResponse(code = 0, message = "ok", response = PointQueryResponse.class)})
    @RequestMapping(value = "/get", method = {RequestMethod.POST})
    @ResponseBody
    public BaseResult<PointQueryResponse> getPoint(@RequestBody PointRequest queryRequest) {

        if (!queryRequest.validate()) {
            return ERROR("参数不合法");
        }

        PointQueryResponse queryResponse = pointQueryService.getPoint(queryRequest.getUid());

        return OK(queryResponse);
    }

    @ApiOperation(value = "立即签到")
    @ApiResponses(value = {@ApiResponse(code = 0, message = "ok", response = BaseResult.class)})
    @RequestMapping(value = "/update_sign_in", method = {RequestMethod.POST})
    @ResponseBody
    public BaseResult updateSignIn(@RequestBody PointRequest queryRequest) {

        if (!queryRequest.validate()) {
            return ERROR("参数不合法");
        }
        return pointUpdateService.signIn(queryRequest.getUid());
    }

    @ApiOperation(value = "签到记录")
    @ApiResponses(value = {@ApiResponse(code = 0, message = "ok", response = SignInStatusResponse.class)})
    @RequestMapping(value = "/get_sign_in_status", method = {RequestMethod.POST})
    @ResponseBody
    public BaseResult<SignInStatusResponse> getSignInStatus(@RequestBody PointRequest queryRequest) {

        if (!queryRequest.validate()) {
            return ERROR("参数不合法");
        }
        SignInStatusResponse statusResponse = pointQueryService.getSignInStatus(queryRequest.getUid());
        return OK(statusResponse);
    }

}
