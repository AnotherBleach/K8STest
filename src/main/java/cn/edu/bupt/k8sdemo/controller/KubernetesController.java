package cn.edu.bupt.k8sdemo.controller;


import cn.edu.bupt.k8sdemo.model.Task;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.JenkinsTriggerHelper;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.auth.ApiKeyAuth;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodBuilder;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.proto.Resource;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;



@RestController
@RequestMapping("/k8s")
public class KubernetesController {

    final static String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi10b2tlbi1zNW14aCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50Lm5hbWUiOiJhZG1pbiIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjFlYzRiYWZmLWY3YTEtMTFlOC05NTBjLWUwZGI1NTAxOWQwOCIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDprdWJlLXN5c3RlbTphZG1pbiJ9.XYv6tSv7rd1cPghPKxjqscvDReYRrltL8YWvFSd1aJP_kFKNp-J7vQGKP3IkogSzi8ZgyN-PlzE8_HE0wGml8eHw9J_X32vTWvKkY_wbilb1xQNazlIjqFDpXWGfi97MK4rCRyd2mNHl4cdMdFj96WTb5jkaRYLzBJ6y4lypQoSTDYeUWnYcxu9Jsp99AdP8-dyUtmLzsqgvhpyxxZRBn9unvHDuBLFgli_HS3_jiPiUrN6pLCYUOmsN94mIX47SRNsFi35eEivd1spwKOhk3zRWuasHctYqggbdV0YAhZtxWl_bQ7LcB4rl2kFj8Mj8fbDsBFMfVoPk4dYnCVrPyA";


    @RequestMapping("/getAddrAndName")
    public Task getAddrAndName(@RequestBody Task task) {
        String task_addr = task.getAddress();
        String task_name = task.getName();
        System.out.println(task_name);
        System.out.println(task_addr);

        return task;
    }

    @RequestMapping("/getXML")
    public String getXML(@RequestBody Task task) throws IOException, DocumentException {

        String jobxml = FileUtils.readFileToString(new File("D:\\ProgramFiles\\bishe\\code\\k8s管理\\k8sdemo\\src\\main\\resources\\config.xml"));
        Document document = DocumentHelper.parseText(jobxml);
        Element project = document.getRootElement();
        project.element("scm")
                .element("userRemoteConfigs")
                .element("hudson.plugins.git.UserRemoteConfig")
                .element("url")
                .setText(task.getAddress());
        jobxml = document.asXML();

        return document.asXML();

    }

    @RequestMapping("/addJenkinsTask")
    public void addJenkinsTask(@RequestBody Task task) throws URISyntaxException, IOException, DocumentException {
        System.out.println(task.getName() + ":" + task.getAddress());
        String jobxml = FileUtils.readFileToString(new File("D:\\ProgramFiles\\bishe\\code\\k8s管理\\k8sdemo\\src\\main\\resources\\config.xml"));
        Document document = DocumentHelper.parseText(jobxml);
        Element project = document.getRootElement();
        project.element("scm")
                .element("userRemoteConfigs")
                .element("hudson.plugins.git.UserRemoteConfig")
                .element("url")
                .setText(task.getAddress());


        String script = "sudo docker ps|grep 10.108.210.227:8000/library/" + task.getName() + ":latest|awk '{print $1}'|xargs docker stop ||true;" +
                "sudo docker ps -a|grep 10.108.210.227:8000/library/" + task.getName() + ":latest|awk '{print $1}'|xargs docker rm ||true;" +
                "sudo docker rmi 10.108.210.227:8000/library/" + task.getName() + ":latest||true;" +
                "sudo docker build -t 10.108.210.227:8000/library/" + task.getName() + ":latest .;" +
                "sudo docker push 10.108.210.227:8000/library/" + task.getName() + ":latest;";


        project.element("builders")
                .element("hudson.tasks.Shell")
                .element("command")
                .setText(script);

        jobxml = document.asXML();

        JenkinsServer jenkins = new JenkinsServer(new URI("http://10.108.210.227:9999"), "", "");
        jenkins.createJob(task.getName(), jobxml);


    }

    @RequestMapping("/buildJenkinsTask")
    public void buildJenkinsTask(@RequestBody Task task) throws IOException, URISyntaxException, DocumentException, InterruptedException {


        String jobxml = FileUtils.readFileToString(new File("D:\\ProgramFiles\\bishe\\code\\k8s管理\\k8sdemo\\src\\main\\resources\\config.xml"));
        Document document = DocumentHelper.parseText(jobxml);
        Element project = document.getRootElement();
        project.element("scm")
                .element("userRemoteConfigs")
                .element("hudson.plugins.git.UserRemoteConfig")
                .element("url")
                .setText(task.getAddress());

        String script = "sudo docker ps|grep 10.108.210.227:8000/library/" + task.getName() + ":latest|awk '{print $1}'|xargs docker stop ||true;" +
                "sudo docker ps -a|grep 10.108.210.227:8000/library/" + task.getName() + ":latest|awk '{print $1}'|xargs docker rm ||true;" +
                "sudo docker rmi 10.108.210.227:8000/library/" + task.getName() + ":latest||true;" +
                "sudo docker build -t 10.108.210.227:8000/library/" + task.getName() + ":latest .;" +
                "sudo docker push 10.108.210.227:8000/library/" + task.getName() + ":latest;";


        project.element("builders")
                .element("hudson.tasks.Shell")
                .element("command")
                .setText(script);

        jobxml = document.asXML();

        JenkinsServer jenkins = new JenkinsServer(new URI("http://10.108.210.227:9999"), "", "");
        int x;
        jenkins.createJob(task.getName(), jobxml);
        new Thread(() -> {
            try {
                new JenkinsTriggerHelper(jenkins).triggerJobAndWaitUntilFinished(task.getName());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();


    }


    @RequestMapping("/submitTask")
    public String submitTask(@RequestBody Task task) throws ApiException, URISyntaxException, IOException, DocumentException {

        String jobxml = FileUtils.readFileToString(new File("D:\\ProgramFiles\\bishe\\code\\k8s管理\\k8sdemo\\src\\main\\resources\\config.xml"));
        Document document = DocumentHelper.parseText(jobxml);
        Element project = document.getRootElement();
        project.element("scm")
                .element("userRemoteConfigs")
                .element("hudson.plugins.git.UserRemoteConfig")
                .element("url")
                .setText(task.getAddress());

        String script = "sudo docker ps|grep 10.108.210.227:8000/library/" + task.getName() + ":latest|awk '{print $1}'|xargs docker stop ||true;" +
                "sudo docker ps -a|grep 10.108.210.227:8000/library/" + task.getName() + ":latest|awk '{print $1}'|xargs docker rm ||true;" +
                "sudo docker rmi 10.108.210.227:8000/library/" + task.getName() + ":latest||true;" +
                "sudo docker build -t 10.108.210.227:8000/library/" + task.getName() + ":latest .;" +
                "sudo docker push 10.108.210.227:8000/library/" + task.getName() + ":latest;";


        project.element("builders")
                .element("hudson.tasks.Shell")
                .element("command")
                .setText(script);

        jobxml = document.asXML();

        JenkinsServer jenkins = new JenkinsServer(new URI("http://10.108.210.227:9999"), "", "");
        int x;
        jenkins.createJob(task.getName(), jobxml);
        new Thread(() -> {
            try {
                new JenkinsTriggerHelper(jenkins).triggerJobAndWaitUntilFinished(task.getName());
                ApiClient client = Config.fromUrl("http://10.108.210.194:8888");
                Configuration.setDefaultApiClient(client);
                V1Pod pod =
                        new V1PodBuilder()
                                .withApiVersion("v1")
                                .withKind("Pod")
                                .withNewMetadata()
                                .withName(task.getName())
                                .endMetadata()
                                .withNewSpec()
                                .addNewContainer()
                                .withName("gpu-test")
                                .withImage("10.108.210.227:8000/library/" + task.getName() + ":latest")
                                .withNewResources()
                                .withLimits(new HashMap<>())
                                .endResources()
                                .endContainer()
                                .endSpec()
                                .build();
                CoreV1Api api = new CoreV1Api();
                api.createNamespacedPod("default", pod, "true");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }).start();

        return new Date().toLocaleString();

    }


    @RequestMapping("/submitGpuTask")
    public String submitGpuTask(@RequestBody Task task) throws ApiException, URISyntaxException, IOException, DocumentException {


        //读取jenkins 配置文件，并修改配置.提交jenkins任务
        String jobxml = FileUtils.readFileToString(new File("D:\\ProgramFiles\\bishe\\code\\k8s管理\\k8sdemo\\src\\main\\resources\\config.xml"));
        Document document = DocumentHelper.parseText(jobxml);
        Element project = document.getRootElement();
        project.element("scm")
                .element("userRemoteConfigs")
                .element("hudson.plugins.git.UserRemoteConfig")
                .element("url")
                .setText(task.getAddress());

        String script = "sudo docker ps|grep 10.108.210.227:8000/library/" + task.getName() + ":latest|awk '{print $1}'|xargs docker stop ||true;" +
                "sudo docker ps -a|grep 10.108.210.227:8000/library/" + task.getName() + ":latest|awk '{print $1}'|xargs docker rm ||true;" +
                "sudo docker rmi 10.108.210.227:8000/library/" + task.getName() + ":latest||true;" +
                "sudo docker build -t 10.108.210.227:8000/library/" + task.getName() + ":latest .;" +
                "sudo docker push 10.108.210.227:8000/library/" + task.getName() + ":latest;";


        project.element("builders")
                .element("hudson.tasks.Shell")
                .element("command")
                .setText(script);

        jobxml = document.asXML();

        JenkinsServer jenkins = new JenkinsServer(new URI("http://10.108.210.227:9999"), "", "");
        int x;
        jenkins.createJob(task.getName(), jobxml);


        //拉取build后的镜像，提交pod运行

        new Thread(() -> {

            try {
                HashMap<String,Quantity> limits = new HashMap<String,Quantity>();
                limits.put("nvidia.com/gpu",Quantity.fromString("1"));
                new JenkinsTriggerHelper(jenkins).triggerJobAndWaitUntilFinished(task.getName());
                ApiClient client = Config.fromUrl("http://10.108.210.194:8888");
                Configuration.setDefaultApiClient(client);
                V1Pod pod =
                        new V1PodBuilder()
                                .withApiVersion("v1")
                                .withKind("Pod")
                                .withNewMetadata()
                                .withName(task.getName())
                                .endMetadata()
                                .withNewSpec()
                                .addNewContainer()
                                .withName("digits-container")
                                .withImage("nvidia/digits:6.0")
                                .withNewResources()
                                .withLimits(limits)
                                .endResources()
                                .endContainer()
                                .addNewContainer()
                                .withName("node-container")
                                .withImage("10.108.210.227:8000/library/" + task.getName() + ":latest")
                                .withNewResources()
                                .withLimits(new HashMap<>())
                                .endResources()
                                .endContainer()
                                .endSpec()
                                .build();
                CoreV1Api api = new CoreV1Api();
                api.createNamespacedPod("default", pod, "true");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }).start();

        return new Date().toLocaleString();

    }

}
