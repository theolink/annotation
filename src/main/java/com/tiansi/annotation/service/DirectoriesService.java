package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Directories;
import com.tiansi.annotation.exception.TiansiException;

import java.util.ArrayList;

public interface DirectoriesService extends IService<Directories> {
    /**
     * 获取所有已扫描目录
     *
     * @return
     */
    ArrayList<String> getDirectories();

    /**
     * 获取所有目录中的未扫描目录
     *
     * @return 未扫描目录
     */
    ArrayList<String> notScan() throws TiansiException;

    /**
     * 添加已扫描目录
     *
     * @param directories 已扫描目录
     * @return 成功添加的目录数
     */
    int addDirectories(ArrayList<String> directories);
}
