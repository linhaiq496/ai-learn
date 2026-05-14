package cn.iocoder.linhai.module.mes.dal.mysql.wm.packages;

import cn.iocoder.linhai.framework.common.pojo.PageResult;
import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.mes.controller.admin.wm.packages.vo.line.MesWmPackageLinePageReqVO;
import cn.iocoder.linhai.module.mes.dal.dataobject.wm.packages.MesWmPackageLineDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * MES 装箱明细 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MesWmPackageLineMapper extends BaseMapperX<MesWmPackageLineDO> {

    default PageResult<MesWmPackageLineDO> selectPage(MesWmPackageLinePageReqVO reqVO, java.util.Collection<Long> packageIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MesWmPackageLineDO>()
                .inIfPresent(MesWmPackageLineDO::getPackageId, packageIds)
                .orderByDesc(MesWmPackageLineDO::getId));
    }

    default void deleteByPackageId(Long packageId) {
        delete(MesWmPackageLineDO::getPackageId, packageId);
    }

}
