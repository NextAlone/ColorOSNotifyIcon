/*
 * ColorOSNotifyIcon - Optimize notification icons for ColorOS and adapt to native notification icon specifications.
 * Copyright (C) 2019-2022 Fankes Studio(qzmmcn@163.com)
 * https://github.com/fankes/ColorOSNotifyIcon
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version.
 * <p>
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 *
 * This file is Created by fankes on 2022/1/30.
 */
@file:Suppress("UNCHECKED_CAST")

package com.fankes.coloros.notify.ui.activity.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.fankes.coloros.notify.R
import com.fankes.coloros.notify.utils.factory.isNotSystemInDarkMode
import com.fankes.coloros.notify.utils.tool.SystemUITool
import com.gyf.immersionbar.ktx.immersionBar
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    companion object {

        /** 应用是否正在运行 */
        var isMainThreadRunning = false
    }

    /** 获取绑定布局对象 */
    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isMainThreadRunning = true
        javaClass.genericSuperclass.also { type ->
            if (type is ParameterizedType) {
                binding = (type.actualTypeArguments[0] as Class<VB>).method {
                    name = "inflate"
                    param(LayoutInflaterClass)
                }.get().invoke<VB>(layoutInflater) ?: error("binding failed")
                setContentView(binding.root)
            } else error("binding but got wrong type")
        }
        /** 隐藏系统的标题栏 */
        supportActionBar?.hide()
        /** 初始化沉浸状态栏 */
        immersionBar {
            statusBarColor(R.color.colorThemeBackground)
            autoDarkModeEnable(true)
            statusBarDarkFont(isNotSystemInDarkMode)
            navigationBarColor(R.color.colorThemeBackground)
            navigationBarDarkIcon(isNotSystemInDarkMode)
            fitsSystemWindows(true)
        }
        /** 注册 */
        SystemUITool.register(context = this)
        /** 装载子类 */
        onCreate()
    }

    /** 回调 [onCreate] 方法 */
    abstract fun onCreate()

    override fun onDestroy() {
        super.onDestroy()
        /** 解除注册 */
        SystemUITool.unregister(context = this)
    }
}