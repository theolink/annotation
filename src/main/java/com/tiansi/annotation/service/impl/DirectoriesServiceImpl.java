package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Directories;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.mapper.DirectoriesMapper;
import com.tiansi.annotation.service.DirectoriesService;
import com.tiansi.annotation.util.Props;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class DirectoriesServiceImpl extends ServiceImpl<DirectoriesMapper, Directories> implements DirectoriesService {
    @Autowired
    private Props props;

    @Override
    public ArrayList<String> getDirectories() {
        List<Directories> directories = list(null);
        return getNames(directories);
    }

    @Override
    public ArrayList<String> notScan() throws TiansiException {
        File originHome = new File(props.getOriginVideoHome());
        if (!originHome.isDirectory()) {
            throw new TiansiException(ErrorCode.INVALID_CONFIG, "OriginVideoHome is not a directory!");
        }
        File[] totalDirectories = originHome.listFiles();
        ArrayList<String> totalDirectoriesNames = new ArrayList<>();
        if (totalDirectories == null || totalDirectories.length == 0) {
            return totalDirectoriesNames;
        }
        for (File directory : totalDirectories) {
            totalDirectoriesNames.add(directory.getName());
        }
        totalDirectoriesNames.removeAll(getDirectories());
        return totalDirectoriesNames;
    }

    @Override
    public int addDirectories(ArrayList<String> directories) {
        directories.removeAll(getDirectories());
        directories.forEach(directoryName -> {
            Directories directory = new Directories(directoryName);
            save(directory);
        });
        return directories.size();
    }

    private ArrayList<String> getNames(List<Directories> directories) {
        ArrayList<String> names = new ArrayList<>();
        directories.forEach(directory -> names.add(directory.getName()));
        return names;
    }
}
