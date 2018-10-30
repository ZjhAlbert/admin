package com.venusz.admin.controller;

import com.venusz.admin.model.SysUser;
import com.venusz.admin.service.SysUserService;
import com.venusz.admin.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: VenusZ
 * \* Date: 2018/10/30
 * \* Time: 22:23
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
@Controller
public class UserController {
    @Autowired
    private SysUserService sysUserService;

    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getSession().invalidate();
        String path = "signin.jsp";
        response.sendRedirect(path);
    }

    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean flag = false;
        SysUser sysUser = sysUserService.findByKeyword(username);
        String errorMsg = "";
        String ret = request.getParameter("ret");

        if (StringUtils.isBlank(username)) {
            errorMsg = "用户名不可以为空";
        } else if (StringUtils.isBlank(password)) {
            errorMsg = "密码不可以为空";
        } else if (sysUser == null) {
            errorMsg = "查询不到指定的用户";
        } else if (!sysUser.getPassword().equals(MD5Util.encrypt(password))) {
            errorMsg = "用户名或密码错误";
        } else if (sysUser.getStatus() != 1) {
            errorMsg = "用户已被冻结，请联系管理员";
        } else {
            // login success
            request.getSession().setAttribute("user", sysUser);
            if (StringUtils.isNotBlank(ret)) {
                response.sendRedirect(ret);
                flag = true;
            } else {
                response.sendRedirect("/admin/index.page");
                flag = true;
                //TODO
            }
        }
        if(!flag){
            request.setAttribute("error", errorMsg);
            request.setAttribute("username", username);
            if (StringUtils.isNotBlank(ret)) {
                request.setAttribute("ret", ret);
            }
            String path = "signin.jsp";
            request.getRequestDispatcher(path).forward(request, response);
        }
    }
}
