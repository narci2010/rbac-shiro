package rbac.web.client.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.rbac.shiro.Menu;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import rbac.dao.AdminUsersDao;
import rbac.dao.repository.AdminAdminNav;
import rbac.dao.repository.AdminUsers;
import rbac.dao.repository.AdminVersionLicense;
import rbac.service.AdminAdminNavService;
import rbac.service.AdminDepartmentService;
import rbac.service.AdminUsersService;
import rbac.utils.BeanUtil;
import rbac.utils.Result;
import rbac.web.client.ClientApiRequestParam;
import rbac.web.client.RestClientApi;

import java.util.*;

/**
 * Created by pandaking on 2017/5/31.
 */
@RestController
public class RestClientAuthApi extends RestClientApi {

    @Autowired
    private AdminUsersService adminUsersService;
    @Autowired
    private AdminDepartmentService adminDepartmentService;
    @Autowired
    private AdminUsersDao adminUsersDao;
    @Autowired
    private AdminAdminNavService adminAdminNavService;

    @RequestMapping(value = "auth/check", method = RequestMethod.POST)
    @ResponseBody
    public Result doLogin(ClientApiRequestParam param) {
        Result<JSONObject> result = checkParam(param);
        if (!result.isSuccess()) {
            return result;
        }
        JSONObject request = result.getData();
        AdminVersionLicense license = request.getObject("license", AdminVersionLicense.class);
        String username = request.getString("username");
        String password = request.getString("password");

        AdminUsers adminUsers = adminUsersService.checkUserAndPassword(username, password);
        {
            Result checkResult = adminUsersService.checkUser(adminUsers);
            if (!checkResult.isSuccess()) {
                return checkResult;
            }
            adminUsersService.userLogin(adminUsers);
        }
        Map<String, Object> profile = new HashMap<>();
        profile.put("uuid", adminUsers.getUuid());
        profile.put("username", adminUsers.getUsername());
        profile.put("avatar", adminUsers.getAvatar());
        return response(license, profile);
    }

    @RequestMapping(value = "auth/permission", method = RequestMethod.POST)
    @ResponseBody
    public Result permission(ClientApiRequestParam param) {
        Result<JSONObject> paramCheckResult = checkParam(param);
        if (!paramCheckResult.isSuccess()) {
            return paramCheckResult;
        }
        JSONObject request = paramCheckResult.getData();
        AdminUsers buildUser = adminUsersDao.findByUuid(param.getUuid());

        String uuid = request.getString("uuid");
        AdminUsers user = adminUsersDao.findByUuid(uuid);
        //  检查用户信息
        {
            Result result = adminUsersService.checkUser(user);
            if (!result.isSuccess()) {
                return result;
            }
        }
        Result<Set<String>> result = adminUsersService.getRulesByUser(user);
        Set<String> rules = new HashSet<>();
        if (result.isSuccess()) {
            rules = result.getData();
        }

        Set<String> userUUids = adminDepartmentService.getManagedUserUuidByUserUuid(uuid);
        userUUids.add(uuid);

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("permission", rules);
        resultMap.put("menus", buildMenu(buildUser));
        resultMap.put("uuid", userUUids);

        return response(request.getObject("license", AdminVersionLicense.class), resultMap);
    }

    private List<Menu> buildMenu(AdminUsers buildUser) {
        List<AdminAdminNav> navList = adminAdminNavService.getClientMenu(buildUser);
        Map<Long, List<AdminAdminNav>> navMap = new HashMap<>();
        for (AdminAdminNav item : navList) {
            List<AdminAdminNav> list = navMap.get(item.getPid());
            if (CollectionUtils.isEmpty(list)) {
                list = new ArrayList<>();
            }
            list.add(item);
            navMap.put(item.getPid(), list);
        }
        List<Menu> menus = new ArrayList<>();
        for (AdminAdminNav item : navMap.get(0L)) {
            Menu menu = BeanUtil.copy(item, Menu.class);
            menu.setChild(processMenu(item, navMap));
            menus.add(menu);
        }
        return menus;
    }

    private List<Menu> processMenu(AdminAdminNav parent, Map<Long, List<AdminAdminNav>> navMap) {
        List<AdminAdminNav> list = navMap.get(parent.getId());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<Menu> menus = new ArrayList<>();
        for (AdminAdminNav item : list) {
            Menu menu = BeanUtil.copy(item, Menu.class);
            menu.setChild(processMenu(item, navMap));
            menus.add(menu);
        }
        return menus;
    }

}
