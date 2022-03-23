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
 * This file is Created by fankes on 2022/2/8.
 */
package com.fankes.coloros.notify.utils.tool

import android.content.Context
import com.fankes.coloros.notify.utils.factory.execShellSu
import com.fankes.coloros.notify.utils.factory.showDialog
import com.fankes.coloros.notify.utils.factory.snake
import com.fankes.coloros.notify.utils.factory.toast
import com.google.android.material.snackbar.Snackbar
import com.highcapable.yukihookapi.hook.factory.isXposedModuleActive

/**
 * 系统界面工具
 */
object SystemUITool {

    /**
     * 重启系统界面
     * @param context 实例
     */
    fun restartSystemUI(context: Context) =
        context.showDialog {
            title = "重启系统界面"
            msg = "你确定要立即重启系统界面吗？\n\n" +
                    "重启过程会黑屏并等待进入锁屏重新解锁。"
            confirmButton {
                execShellSu(cmd = "pgrep systemui").also { pid ->
                    if (pid.isNotBlank())
                        execShellSu(cmd = "kill -9 $pid")
                    else toast(msg = "ROOT 权限获取失败")
                }
            }
            cancelButton()
        }

    /**
     * 刷新系统界面状态栏与通知图标
     * @param context 实例
     */
    fun refreshSystemUI(context: Context) = IconRuleManagerTool.refreshSystemUI(context)

    /**
     * 显示需要重启系统界面的 [Snackbar]
     * @param context 实例
     */
    fun showNeedRestartSnake(context: Context) =
        if (isXposedModuleActive)
            context.snake(msg = "设置需要重启系统界面才能生效", actionText = "立即重启") { restartSystemUI(context) }
        else context.snake(msg = "模块没有激活，更改不会生效")

    /**
     * 显示更新数据后需要重启系统界面的 [Snackbar]
     * @param context 实例
     */
    fun showNeedUpdateApplySnake(context: Context) =
        if (isXposedModuleActive) context.snake(msg = "通知图标优化名单已完成同步")
        else context.snake(msg = "模块没有激活，更改不会生效")
}