package cn.edu.bupt.k8sdemo.controller;


import cn.edu.bupt.k8sdemo.model.Task;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.JenkinsTriggerHelper;
import com.offbytwo.jenkins.model.Job;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.auth.ApiKeyAuth;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test/k8s")
public class KubernetesController {

    final static String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi10b2tlbi1zNW14aCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJhZG1pbiIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjFlYzRiYWZmLWY3YTEtMTFlOC05NTBjLWUwZGI1NTAxOWQwOCIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDprdWJlLXN5c3RlbTphZG1pbiJ9.XYv6tSv7rd1cPghPKxjqscvDReYRrltL8YWvFSd1aJP_kFKNp-J7vQGKP3IkogSzi8ZgyN-PlzE8_HE0wGml8eHw9J_X32vTWvKkY_wbilb1xQNazlIjqFDpXWGfi97MK4rCRyd2mNHl4cdMdFj96WTb5jkaRYLzBJ6y4lypQoSTDYeUWnYcxu9Jsp99AdP8-dyUtmLzsqgvhpyxxZRBn9unvHDuBLFgli_HS3_jiPiUrN6pLCYUOmsN94mIX47SRNsFi35eEivd1spwKOhk3zRWuasHctYqggbdV0YAhZtxWl_bQ7LcB4rl2kFj8Mj8fbDsBFMfVoPk4dYnCVrPyA";

    @RequestMapping("/showName")
    public String showName(@RequestBody String name) {
        if (name == null || name.equals(""))
            name = "ERROR";
        System.out.println(name);
        return "123";
    }

    @RequestMapping("/submitTask")
    public String submitTask(@RequestBody Task task) throws ApiException {

        String task_addr = task.getAddress();
        String task_name = task.getName();
        System.out.println(task_name);
        System.out.println(task_addr);
        // 1、set name ,addr ,script to build a 镜像。



        // 2、等待镜像构建完成

        // 3、获取镜像地址
        // String image = "";
//
//        //4、createPod
//
//        ApiClient client = Config.fromUrl("http://10.108.210.194:8888");
//        Configuration.setDefaultApiClient(client);
//        HashMap<String, Quantity> hashMap = new HashMap<>();
//        hashMap.put("nvidia.com/gpu", new Quantity(BigDecimal.valueOf(4), Quantity.Format.DECIMAL_SI));
//        V1Pod pod =
//                new V1PodBuilder()
//                        .withApiVersion("v1")
//                        .withKind("Pod")
//                        .withNewMetadata()
//                        .withName("gpu-test")
//                        .endMetadata()
//                        .withNewSpec()
//                        .addNewContainer()
//                        .withName("gpu-test")
//                        .withImage(image)//nvidia/digits:6.0
//                        .withNewResources()
//                        .withLimits(hashMap)
//                        .endResources()
//                        .endContainer()
//                        .endSpec()
//                        .build();
//        System.out.println(pod.getSpec().getContainers().get(0).getResources().getLimits().get("nvidia.com/gpu"));
//        System.out.println(Yaml.dump(pod));
//        CoreV1Api api = new CoreV1Api();
//        api.createNamespacedPod("default", pod, "true");


        return new Date().toLocaleString();

    }


    @RequestMapping("/getAllPods")
    public String[] getAllPods() throws ApiException, IOException {


        ApiClient client = Config.fromUrl("http://10.108.210.194:8888");
        ApiKeyAuth BearerToken = (ApiKeyAuth) client.getAuthentication("BearerToken");
        BearerToken.setApiKey(token);
        Configuration.setDefaultApiClient(client);

        V1Pod pod =
                new V1PodBuilder()
                        .withApiVersion("v1")
                        .withKind("Pod")
                        .withNewMetadata()
                        .withName("nopass")
                        .endMetadata()
                        .withNewSpec()
                        .addNewContainer()
                        .withName("www")
                        .withImage("nginx")
                        .withNewResources()
                        .withLimits(new HashMap<>())
                        .endResources()
                        .endContainer()
                        .endSpec()
                        .build();
        CoreV1Api api = new CoreV1Api();
        api.createNamespacedPod("default", pod, "true");


        return "hello world".split(" ");
    }

    @RequestMapping("/test")
    public String test() throws IOException, URISyntaxException {

        JenkinsServer jenkins = new JenkinsServer(new URI("http://10.108.210.227:9999"), "", "");
        Map<String, Job> jobs = jenkins.getJobs();
        for (String keyname : jobs.keySet()) {
            System.out.println(keyname);
        }
        return "ok";
    }


    @Test
    public void test2() throws URISyntaxException, IOException, InterruptedException {
        JenkinsServer jenkins = new JenkinsServer(new URI("http://10.108.210.227:9999"), "", "");
       // Map<String, Job> jobs = jenkins.getJobs();

    // for (String keyname : jobs.keySet()) {
    //            System.out.println(keyname);
    //        }
    //
       // String jobxml = FileUtils.readFileToString(new File("D:\\ProgramFiles\\bishe\\code\\k8s管理\\k8sdemo\\src\\main\\resources\\config.xml"));
        //System.out.println(jobxml);
        //jenkins.createJob("javaJob",jobxml);
        //jenkins.getJob("javaJob");
        // JobWithDetails zcc = jobs.get("zcc").details();
        //   System.out.println(zcc.getClient().get());
        new JenkinsTriggerHelper(jenkins).triggerJobAndWaitUntilFinished("javaJob");

    }

    @Test
    public void test3() throws ApiException {


        ApiClient client = Config.fromUrl("http://10.108.210.194:8888");
        //ApiKeyAuth BearerToken = (ApiKeyAuth) client.getAuthentication("BearerToken");
        //BearerToken.setApiKey("32");
        Configuration.setDefaultApiClient(client);


        HashMap<String, Quantity> hashMap = new HashMap<>();

        hashMap.put("nvidia.com/gpu", new Quantity(BigDecimal.valueOf(4), Quantity.Format.DECIMAL_SI));
        V1Pod pod =
                new V1PodBuilder()
                        .withApiVersion("v1")
                        .withKind("Pod")
                        .withNewMetadata()
                        .withName("gpu-test")
                        .endMetadata()
                        .withNewSpec()
                        .addNewContainer()
                        .withName("gpu-test")
                        .withImage("nvidia/digits:6.0")//nvidia/digits:6.0
                        .withNewResources()
                        .withLimits(hashMap)
                        .endResources()
                        .endContainer()
                        .endSpec()
                        .build();
        System.out.println(pod.getSpec().getContainers().get(0).getResources().getLimits().get("nvidia.com/gpu"));
        System.out.println(Yaml.dump(pod));
        CoreV1Api api = new CoreV1Api();
        api.createNamespacedPod("default", pod, "true");
        System.out.println("done");
    }

}
