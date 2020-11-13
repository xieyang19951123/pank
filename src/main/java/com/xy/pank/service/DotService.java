package com.xy.pank.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xy.pank.entity.Dot;

public interface DotService extends IService<Dot> {
    Dot getDotByTag(String tag);
}
