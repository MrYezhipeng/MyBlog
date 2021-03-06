package com.drye.myblog.demo.controller;

import com.drye.myblog.demo.entity.AdminEntity;
import com.drye.myblog.demo.entity.ArticleEntity;
import com.drye.myblog.demo.service.AdminService;
import com.drye.myblog.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    /**
    * @Description: 设值注入
    * @Author: GeekYe
    * @Date: 2018/3/6
    */
    @Autowired
    private ArticleService articleService;

    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Autowired
    private AdminService adminService;

    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
    * @Description:  登陆验证
    * @Param: [page, adminEntity, model, httpSession]
    * @return: java.lang.String
    * @Author: GeekYe
    * @Date: 2018/2/28
    */
    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public String formVerify(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "category", defaultValue = "default" ) String category,
                             AdminEntity adminEntity,
                             Model model, HttpSession httpSession) {
        //先判断是否为空，防止空指针异常
        if (adminEntity.getAdminName() != null || adminEntity.getAdminPassword() != null) {
            if (adminService.getAdmin(adminEntity.getAdminName(), adminEntity.getAdminPassword()) == null) {
                model.addAttribute("error", "账号密码错误，请重新登陆");
                return "templates/login";
            } else {
                List<ArticleEntity> articleEntityList = articleService.listArticlesAtAdminHome(page-1);
                model.addAttribute("page", page);
                model.addAttribute("articleList", articleEntityList);
                model.addAttribute("pagination", articleService.countPaginationByCategory(category));
                httpSession.setAttribute("SessionJudge", adminEntity.getAdminName());
                return "templates/manage";
            }
        }else {
            model.addAttribute("error","请输入账号密码");
            return "templates/login";
        }
    }

    /**
    * @Description: 管理员页面，根据当前页和时间顺序显示
    * @Param: [page, method]
    * @return: java.lang.String
    * @Author: GeekYe
    * @Date: 2018/3/1
    */
    @RequestMapping(value = {"/admin/pagination"}, method = RequestMethod.GET)
    public String findByTime(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "method", defaultValue = "asc") String method,
                             @RequestParam(value = "category", defaultValue = "default") String category,
                              Model model){

        List<ArticleEntity> articleEntityList = articleService.listArticlesAtAdminByTime(method,page-1, category);

        model.addAttribute("articleList", articleEntityList);
        model.addAttribute("method", method);
        model.addAttribute("page", page);
        model.addAttribute("category", category);
        model.addAttribute("pagination", articleService.countAdminPaginationByCategory(category));

        return "templates/manage";
    }

    /**
    * @Description:  session销毁
    * @Param: [model, httpSession]
    * @return: java.lang.String
    * @Author: GeekYe
    * @Date: 2018/2/28
    */
    @RequestMapping(value = "/destorysession", method = RequestMethod.GET)
    public String destroySession(Model model, HttpSession httpSession){
        if(httpSession != null){
            httpSession.removeAttribute("SessionJudge");
            httpSession.invalidate();
        }
        //重定向到主页
        return "redirect:/";
    }
}