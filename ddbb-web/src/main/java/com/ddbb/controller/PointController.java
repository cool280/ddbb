package com.ddbb.controller;

import com.ddbb.controller.request.PointQueryRequest;
import com.ddbb.controller.response.PointQueryResponse;
import com.ddbb.internal.annotate.DdbbController;
import com.ddbb.service.point.PointQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@DdbbController
@RequestMapping("/point")
@Api(tags = "用户积分")
public class PointController extends BaseController {

    @Autowired
    private PointQueryService pointQueryService;

    @ApiOperation(value = "我的积分")
    @ApiResponses(value = {@ApiResponse(code = 0, message = "ok", response = PointQueryResponse.class)})
    @RequestMapping(value = "/get", method = {RequestMethod.POST})
    @ResponseBody
    public BaseResult<PointQueryResponse> getPoint(@RequestBody PointQueryRequest queryRequest) {

        if (!queryRequest.validate()) {
            return ERROR("参数不合法");
        }

        PointQueryResponse queryResponse = pointQueryService.getPoint(queryRequest.getUid());

        return OK(queryRequest);
    }
}
