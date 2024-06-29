package org.jeecg.modules.teaching.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.DictResult;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.util.Ow365Util;
import org.jeecg.modules.common.util.QiniuUtil;
import org.jeecg.modules.system.service.ISysFileService;
import org.jeecg.modules.teaching.entity.TeachingCourseUnit;
import org.jeecg.modules.teaching.model.CourseUnitModel;
import org.jeecg.modules.teaching.model.CourseUnitWorkModel;
import org.jeecg.modules.teaching.service.ITeachingCourseDeptService;
import org.jeecg.modules.teaching.service.ITeachingCourseUnitService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 课程单元
 * @Author: jeecg-boot
 * @Date:   2020-04-14
 * @Version: V1.0
 */
@Api(tags="课程单元")
@RestController
@RequestMapping("/teaching/teachingCourseUnit")
@Slf4j
public class TeachingCourseUnitController extends JeecgController<TeachingCourseUnit, ITeachingCourseUnitService> {
	@Autowired
	private ITeachingCourseUnitService teachingCourseUnitService;
	@Autowired
	private ITeachingCourseDeptService teachingCourseDeptService;
	 @Autowired
	 private ISysFileService sysFileService;
	 @Autowired
	 private Ow365Util ow365Util;



	 @ApiOperation(value="我的课程单元", notes="我的课程单元")
	 @GetMapping(value = "/mineUnit")
	 public Result<?> mineUnit(@RequestParam String courseId,
								@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								HttpServletRequest req) throws Exception {
		 //验证权限
		 if (!teachingCourseDeptService.checkCoursePermission(courseId, getCurrentUser().getId())){
			 return Result.error("无课程权限");
		 }

		 QueryWrapper<CourseUnitModel> queryWrapper = new QueryWrapper<>();
		 queryWrapper.eq("course_id", courseId);
		 queryWrapper.orderByAsc("order_num");
		 Page<CourseUnitModel> page = new Page<CourseUnitModel>(pageNo, pageSize);
		 IPage<CourseUnitModel> pageList = teachingCourseUnitService.getCourseUnitList(page, queryWrapper);

		 for (CourseUnitModel model: pageList.getRecords()){
			 if(StringUtils.isNotBlank(model.getCoursePpt())){
				 model.setCoursePpt(ow365Util.getFileUrlStr(model.getCoursePpt()));
			 }
			 if(StringUtils.isNotBlank(model.getCoursePlan())){
				 model.setCoursePlan(ow365Util.getFileUrlStr(model.getCoursePlan()));
			 }
		 	if (getCurrentUser().getUserIdentity() == null || getCurrentUser().getUserIdentity().equals(1)){
				 model.setCourseVideo(model.getShowCourseVideo()?model.getCourseVideo(): null);
				 model.setCourseCase(model.getShowCourseCase()?model.getCourseCase(): null);
				 model.setCoursePlan(model.getShowCoursePlan()?model.getCoursePlan(): null);
				 model.setCoursePpt(model.getShowCoursePpt()?model.getCoursePpt(): null);
			 }
		 }
		 return Result.ok(pageList);
	 }


	
	/**
	 * 分页列表查询
	 *
	 * @param teachingCourseUnit
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "课程单元-分页列表查询")
	@ApiOperation(value="课程单元-分页列表查询", notes="课程单元-分页列表查询")
	@GetMapping(value = "/list")
	@PermissionData
	public Result<?> queryPageList(CourseUnitModel teachingCourseUnit,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<CourseUnitModel> queryWrapper = QueryGenerator.initQueryWrapper(teachingCourseUnit, req.getParameterMap());
		queryWrapper.orderByAsc("order_num");
		Page<CourseUnitModel> page = new Page<CourseUnitModel>(pageNo, pageSize);
		IPage<CourseUnitModel> pageList = teachingCourseUnitService.getCourseUnitList(page, queryWrapper);
		return Result.ok(pageList);
	}

	 @GetMapping("getUnitWorkInfo")
	 public DictResult<CourseUnitWorkModel> getUnitWorkInfo(@RequestParam String unitId) {
		 DictResult<CourseUnitWorkModel> result = new DictResult<CourseUnitWorkModel>();
		 LoginUser user = getCurrentUser();
		 if (user == null){
			 //未登录
		 }
		 CourseUnitWorkModel teachingCourseUnit = teachingCourseUnitService.getCourseWorkUnit(unitId, user.getId());
		 if(teachingCourseUnit==null) {
			 result.error500("未找到对应实体");
		 }else {
			 result.setResult(teachingCourseUnit);
			 result.setSuccess(true);
		 }
		 return result;
	 }
	
	/**
	 *   添加
	 *
	 * @param teachingCourseUnit
	 * @return
	 */
	@AutoLog(value = "课程单元-添加")
	@ApiOperation(value="课程单元-添加", notes="课程单元-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TeachingCourseUnit teachingCourseUnit) {
		teachingCourseUnitService.save(teachingCourseUnit);
		return Result.ok("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param teachingCourseUnit
	 * @return
	 */
	@AutoLog(value = "课程单元-编辑")
	@ApiOperation(value="课程单元-编辑", notes="课程单元-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TeachingCourseUnit teachingCourseUnit) {
		teachingCourseUnitService.updateById(teachingCourseUnit);
		return Result.ok("编辑成功!");
	}

	 @AutoLog(value = "课程单元地图-编辑")
	 @ApiOperation(value="课程单元地图-编辑", notes="课程单元地图-编辑")
	 @PutMapping(value = "/editBatch")
	 public Result<?> editMap(@RequestBody ArrayList<TeachingCourseUnit> unitList){
		 teachingCourseUnitService.updateBatchById(unitList);
		 return Result.ok("编辑成功!");
	 }

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "课程单元-通过id删除")
	@ApiOperation(value="课程单元-通过id删除", notes="课程单元-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		TeachingCourseUnit unit = teachingCourseUnitService.getById(id);
		if (unit != null){
			sysFileService.deleteByKeyWithFile(unit.getCoursePpt());
			sysFileService.deleteByKeyWithFile(unit.getUnitCover());
			sysFileService.deleteByKeyWithFile(unit.getCourseVideo());
			sysFileService.deleteByKeyWithFile(unit.getCourseWork());
			sysFileService.deleteByKeyWithFile(unit.getCourseWorkAnswer());
			sysFileService.deleteByKeyWithFile(unit.getCourseCase());
			sysFileService.deleteByKeyWithFile(unit.getCoursePlan());
			teachingCourseUnitService.removeById(id);
		}
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "课程单元-批量删除")
	@ApiOperation(value="课程单元-批量删除", notes="课程单元-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		List<String> idList = Arrays.asList(ids.split(","));
		List<TeachingCourseUnit> unitList = teachingCourseUnitService.list(new QueryWrapper<TeachingCourseUnit>().in("id", idList));
		for(TeachingCourseUnit unit: unitList){
			sysFileService.deleteByKeyWithFile(unit.getCoursePpt());
			sysFileService.deleteByKeyWithFile(unit.getUnitCover());
			sysFileService.deleteByKeyWithFile(unit.getCourseVideo());
			sysFileService.deleteByKeyWithFile(unit.getCourseWork());
			sysFileService.deleteByKeyWithFile(unit.getCourseWorkAnswer());
			sysFileService.deleteByKeyWithFile(unit.getCourseCase());
			sysFileService.deleteByKeyWithFile(unit.getCoursePlan());
		}
		this.teachingCourseUnitService.removeByIds(idList);
		return Result.ok("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "课程单元-通过id查询")
	@ApiOperation(value="课程单元-通过id查询", notes="课程单元-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TeachingCourseUnit teachingCourseUnit = teachingCourseUnitService.getById(id);
		if(teachingCourseUnit==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(teachingCourseUnit);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param teachingCourseUnit
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TeachingCourseUnit teachingCourseUnit) {
        return super.exportXls(request, teachingCourseUnit, TeachingCourseUnit.class, "课程单元");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TeachingCourseUnit.class);
    }

}
