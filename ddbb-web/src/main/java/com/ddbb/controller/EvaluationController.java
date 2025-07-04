package com.ddbb.controller;


import com.ddbb.controller.request.evaluation.CoachEvalDimensionRequest;
import com.ddbb.controller.request.evaluation.CoachEvalQueryRequest;
import com.ddbb.controller.request.evaluation.HallEvalDimensionRequest;
import com.ddbb.controller.request.evaluation.HallEvalQueryRequest;
import com.ddbb.controller.response.evaluation.CoachEvalQueryResponse;
import com.ddbb.controller.response.evaluation.HallEvalQueryResponse;
import com.ddbb.internal.annotate.DdbbController;
import com.ddbb.internal.domain.PageResult;
import com.ddbb.service.evaluation.CoachEvalQueryService;
import com.ddbb.service.evaluation.CoachEvalUpdateService;
import com.ddbb.service.evaluation.HallEvalQueryService;
import com.ddbb.service.evaluation.HallEvalUpdateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@DdbbController
@RequestMapping("/evaluation")
@Slf4j
@Api(tags = "评论")
public class EvaluationController extends BaseController {

    @Autowired
    private CoachEvalQueryService coachEvalQueryService;

    @Autowired
    private CoachEvalUpdateService coachEvalUpdateService;

    @Autowired
    private HallEvalUpdateService hallEvalUpdateService;

    @Autowired
    private HallEvalQueryService hallEvalQueryService;


    @ApiOperation(value = "查询助教评论")
    @ApiResponses(value = {@ApiResponse(code = 0, message = "ok", response = CoachEvalQueryResponse.class)})
    @RequestMapping(value = "/get_coach", method = {RequestMethod.POST})
    @ResponseBody
    public BaseResult getCoach(@RequestBody CoachEvalQueryRequest queryRequest) {

        if (queryRequest == null || !queryRequest.validate()) {
            return ERROR("参数不合法");
        }

        int pageNum = queryRequest.getPageNum() <= 0 ? 0 : queryRequest.getPageNum() - 1;

        PageResult<CoachEvalQueryResponse> response = coachEvalQueryService.getByCoachUid(queryRequest.getCoachUid(), pageNum, queryRequest.getPageSize());

        return OK(response);
    }

    @ApiOperation(value = "查询球房评论")
    @ApiResponses(value = {@ApiResponse(code = 0, message = "ok", response = HallEvalQueryResponse.class)})
    @RequestMapping(value = "/get_hall", method = {RequestMethod.POST})
    @ResponseBody
    public BaseResult getHall(@RequestBody HallEvalQueryRequest queryRequest) {

        if (queryRequest == null || !queryRequest.validate()) {
            return ERROR("参数不合法");
        }

        int pageNum = queryRequest.getPageNum() <= 0 ? 0 : queryRequest.getPageNum() - 1;

        PageResult<HallEvalQueryResponse> response = hallEvalQueryService.getByHallId(queryRequest.getHallId(), pageNum, queryRequest.getPageSize());

        return OK(response);
    }

    @ApiOperation(value = "助教评论")
    @ApiResponses(value = {@ApiResponse(code = 0, message = "ok", response = BaseResult.class)})
    @RequestMapping(value = "/add_coach", method = {RequestMethod.POST})
    @ResponseBody
    public BaseResult addCoach(@RequestBody CoachEvalDimensionRequest dimensionRequest) {
        if (dimensionRequest == null || !dimensionRequest.validate()) {
            return ERROR("参数不合法");
        }
        if (!dimensionRequest.validateDimension()) {
            return ERROR("评价不能为空");
        }
        if (dimensionRequest.getComment().length() < 10) {
            return ERROR("评价内容不能少于10个字符");
        }

        return coachEvalUpdateService.addEvaluation(dimensionRequest);
    }

    @ApiOperation(value = "球房评论")
    @ApiResponses(value = {@ApiResponse(code = 0, message = "ok", response = BaseResult.class)})
    @RequestMapping(value = "/add_hall", method = {RequestMethod.POST})
    @ResponseBody
    public BaseResult addHall(@RequestBody HallEvalDimensionRequest dimensionRequest) {

        if (dimensionRequest == null || !dimensionRequest.validate()) {
            return ERROR("参数不合法");
        }
        if (!dimensionRequest.validateDimension()) {
            return ERROR("评价不能为空");
        }
        if (dimensionRequest.getComment().length() < 10) {
            return ERROR("评价内容不能少于10个字符");
        }

        return hallEvalUpdateService.addEvaluation(dimensionRequest);
    }
}
