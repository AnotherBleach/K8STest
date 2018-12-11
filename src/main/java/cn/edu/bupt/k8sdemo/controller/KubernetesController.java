package cn.edu.bupt.k8sdemo.controller;


import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.auth.ApiKeyAuth;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodBuilder;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/k8s")
public class KubernetesController {

    final static String token = "";


    @RequestMapping("/getAllPods")
    public String[] getAllPods() throws ApiException, IOException {


        ApiClient client = Config.fromUrl("");
        ApiKeyAuth BearerToken = (ApiKeyAuth) client.getAuthentication("BearerToken");
        BearerToken.setApiKey(token);
        Configuration.setDefaultApiClient(client);

        V1Pod pod =
                new V1PodBuilder()
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
    public String test() {

        return "ok";
    }


}
