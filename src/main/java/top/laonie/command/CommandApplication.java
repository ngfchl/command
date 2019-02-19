package top.laonie.command;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.laonie.command.dao.UserDOMapper;
import top.laonie.command.dataobject.UserDO;

@SpringBootApplication(scanBasePackages = {"top.laonie.command"})
@RestController
@MapperScan("top.laonie.command.dao")
public class CommandApplication {

    @Autowired
    private UserDOMapper userDOMapper;

    @RequestMapping("/test.do")
    public String test(@RequestParam(name = "id") Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null) {
            return "用户不存在！";
        } else {
            return userDO.getUsername();
        }
    }

    /**
     * 首页
     *
     * @return
     */
    @RequestMapping("/")
    public String homePage() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SpringApplication.run(CommandApplication.class, args);
    }

}

