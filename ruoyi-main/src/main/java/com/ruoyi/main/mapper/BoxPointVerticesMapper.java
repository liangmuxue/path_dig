package com.ruoyi.main.mapper;

import java.util.List;
import com.ruoyi.main.domain.BoxPointVertices;

/**
 * box_point_vertices多边形边框点Mapper接口
 * 
 * @author ruoyi
 * @date 2024-08-27
 */
public interface BoxPointVerticesMapper 
{
    /**
     * 查询box_point_vertices多边形边框点
     * 
     * @param id box_point_vertices多边形边框点主键
     * @return box_point_vertices多边形边框点
     */
    public BoxPointVertices selectBoxPointVerticesById(Long id);

    /**
     * 查询box_point_vertices多边形边框点列表
     * 
     * @param boxPointVertices box_point_vertices多边形边框点
     * @return box_point_vertices多边形边框点集合
     */
    public List<BoxPointVertices> selectBoxPointVerticesList(BoxPointVertices boxPointVertices);

    /**
     * 新增box_point_vertices多边形边框点
     * 
     * @param boxPointVertices box_point_vertices多边形边框点
     * @return 结果
     */
    public int insertBoxPointVertices(BoxPointVertices boxPointVertices);

    /**
     * 修改box_point_vertices多边形边框点
     * 
     * @param boxPointVertices box_point_vertices多边形边框点
     * @return 结果
     */
    public int updateBoxPointVertices(BoxPointVertices boxPointVertices);

    /**
     * 删除box_point_vertices多边形边框点
     * 
     * @param id box_point_vertices多边形边框点主键
     * @return 结果
     */
    public int deleteBoxPointVerticesById(Long id);

    /**
     * 批量删除box_point_vertices多边形边框点
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBoxPointVerticesByIds(Long[] ids);

    List<BoxPointVertices> selectBoxPointVerticesByReportId(Long id);
}
