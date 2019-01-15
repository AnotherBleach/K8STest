package cn.edu.bupt.k8sdemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class K8sdemoApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    protected int serverPort;
    @Test
    public void contextLoads() throws MalformedURLException {
        System.out.println(restTemplate);
       String result = restTemplate.getForEntity(new URL("http://www.baidu.com").toString(),String.class,"").getBody();
       System.out.println(result);
    }

}
