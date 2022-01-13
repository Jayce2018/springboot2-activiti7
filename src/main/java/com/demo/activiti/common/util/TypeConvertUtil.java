package com.demo.activiti.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换
 *
 * @author sunjie
 * @date 2021/9/8 5:15 下午
 **/
@ApiModel("类型转换")
public class TypeConvertUtil {
    /*@ApiOperation("分页对象类型转换")
    public static <E, T> PageInfo<T> pageInfo(PageInfo<E> pageInfo, Class<T> clazz) {
        PageInfo<T> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        List<E> list = pageInfo.getList();
        List<T> resultList = listConvertor(list, clazz);
        result.setList(resultList);
        return result;
    }*/

    @ApiOperation("对象类型转换-json序列法")
    public static <T> T objToType(Object obj, Class<T> clazz) {
        return JSONObject.parseObject(JSONObject.toJSONString(obj), clazz);
    }

    @ApiOperation("集合类型转换-json序列法")
    public static <T> List<T> listToType(Object list, Class<T> clazz) {
        return JSONArray.parseArray(JSONArray.toJSONString(list), clazz);
    }

    @ApiOperation("对象类型转换-BeanUtils")
    public static <T> T objConvertor(Object obj, Class<T> clazz) {
        try {
            T newInstance = clazz.newInstance();
            BeanUtils.copyProperties(obj, newInstance);
            return newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ApiOperation("集合类型转换-BeanUtils")
    public static <E, T> List<T> listConvertor(List<E> list, Class<T> clazz) {
        List<T> results = new ArrayList<>();
        list.forEach(fore -> {
            T newItem = objConvertor(fore, clazz);
            results.add(newItem);
        });
        return results;
    }
}
