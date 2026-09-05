package com.zyf.zhaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * HTTP 方式调用AI
 */
public class HttpAiInvoke {

    public static void main(String[] args) {
        String workspaceId = "ws-x9vx8gdgkrgejywt";
        String apiKey = TestApiKey.API_KEY;

        String url = "https://" + workspaceId
                + ".cn-beijing.maas.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";

        JSONObject body = JSONUtil.createObj()
                .set("model", "qwen3.8-max")
                .set("input", JSONUtil.createObj()
                        .set("messages", JSONUtil.createArray()
                                .set(JSONUtil.createObj()
                                        .set("role", "system")
                                        .set("content", JSONUtil.createArray()
                                                .set(JSONUtil.createObj().set("text", "You are a helpful assistant."))))
                                .set(JSONUtil.createObj()
                                        .set("role", "user")
                                        .set("content", JSONUtil.createArray()
                                                .set(JSONUtil.createObj().set("text", "你是谁？"))))))
                .set("parameters", JSONUtil.createObj()
                        .set("result_format", "message"));

        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(60_000)
                .execute();

        System.out.println(response.getStatus());
        System.out.println(response.body());
    }
}