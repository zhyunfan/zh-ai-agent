package com.zyf.zhaiagent.tools;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 网页抓取工具
 * 修复：用纯文本而非原始HTML，并限制最大长度
 */
@Slf4j
public class WebScrapingTool {

    /** 工具返回的最大字符数（约 5000 tokens） */
    private static final int MAX_CONTENT_LENGTH = 20000;

    @Tool(description = "Scrape the textual content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            log.info("[WebScrapingTool] 开始抓取: {}", url);
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            // 🔑 关键修复1：用 text() 提取纯文本（去掉所有 HTML 标签）
            String text = document.text();
            log.info("[WebScrapingTool] 抓取完成, 原始文本长度: {} 字符", text.length());
            // 🔑 关键修复2：截断超长内容
            if (text.length() > MAX_CONTENT_LENGTH) {
                text = text.substring(0, MAX_CONTENT_LENGTH)
                        + "\n... [内容已截断，原长度: " + document.text().length() + "]";
                log.info("[WebScrapingTool] 内容已截断到 {} 字符", MAX_CONTENT_LENGTH);
            }
            return text;
        } catch (Exception e) {
            log.error("[WebScrapingTool] 抓取失败: {}", e.getMessage());
            return "Error occurred while scraping the web page: " + e.getMessage();
        }
    }
}
