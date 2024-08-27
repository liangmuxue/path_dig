package com.ruoyi.main.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.main.mapper.BoxPointVerticesMapper;
import com.ruoyi.main.domain.BoxPointVertices;
import com.ruoyi.main.service.IBoxPointVerticesService;

import javax.annotation.Resource;

/**
 * box_point_vertices多边形边框点Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-08-27
 */
@Service
public class BoxPointVerticesServiceImpl implements IBoxPointVerticesService 
{
    @Resource
    private BoxPointVerticesMapper boxPointVerticesMapper;

    /**
     * 查询box_point_vertices多边形边框点
     * 
     * @param id box_point_vertices多边形边框点主键
     * @return box_point_vertices多边形边框点
     */
    @Override
    public BoxPointVertices selectBoxPointVerticesById(Long id)
    {
        return boxPointVerticesMapper.selectBoxPointVerticesById(id);
    }

    /**
     * 查询box_point_vertices多边形边框点列表
     * 
     * @param boxPointVertices box_point_vertices多边形边框点
     * @return box_point_vertices多边形边框点
     */
    @Override
    public List<BoxPointVertices> selectBoxPointVerticesList(BoxPointVertices boxPointVertices)
    {
        return boxPointVerticesMapper.selectBoxPointVerticesList(boxPointVertices);
    }

    /**
     * 新增box_point_vertices多边形边框点
     * 
     * @param boxPointVertices box_point_vertices多边形边框点
     * @return 结果
     */
    @Override
    public int insertBoxPointVertices(BoxPointVertices boxPointVertices)
    {
        return boxPointVerticesMapper.insertBoxPointVertices(boxPointVertices);
    }

    /**
     * 修改box_point_vertices多边形边框点
     * 
     * @param boxPointVertices box_point_vertices多边形边框点
     * @return 结果
     */
    @Override
    public int updateBoxPointVertices(BoxPointVertices boxPointVertices)
    {
        return boxPointVerticesMapper.updateBoxPointVertices(boxPointVertices);
    }

    /**
     * 批量删除box_point_vertices多边形边框点
     * 
     * @param ids 需要删除的box_point_vertices多边形边框点主键
     * @return 结果
     */
    @Override
    public int deleteBoxPointVerticesByIds(Long[] ids)
    {
        return boxPointVerticesMapper.deleteBoxPointVerticesByIds(ids);
    }

    /**
     * 删除box_point_vertices多边形边框点信息
     * 
     * @param id box_point_vertices多边形边框点主键
     * @return 结果
     */
    @Override
    public int deleteBoxPointVerticesById(Long id)
    {
        return boxPointVerticesMapper.deleteBoxPointVerticesById(id);
    }

    @Override
    public List<BoxPointVertices> selectBoxPointVerticesByReportId(Long id) {
        return boxPointVerticesMapper.selectBoxPointVerticesByReportId(id);
    }
}
