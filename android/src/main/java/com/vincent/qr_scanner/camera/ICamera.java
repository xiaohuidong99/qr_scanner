package com.vincent.qr_scanner.camera;

import androidx.annotation.Nullable;
import androidx.camera.core.Camera;

/**
 * @Author: Vincent
 * @CreateAt: 2021/01/11 17:53
 */
public interface ICamera {
    /**
     * 启动相机预览
     */
    void startCamera();

    /**
     * 停止相机预览
     */
    void stopCamera();

    /**
     * 获取{@link Camera}
     * @return
     */
    @Nullable
    Camera getCamera();

    /**
     * 释放
     */
    void release();
}
