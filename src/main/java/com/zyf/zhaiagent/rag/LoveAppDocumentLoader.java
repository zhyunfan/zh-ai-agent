package com.zyf.zhaiagent.rag;

import com.zyf.zhaiagent.chatmemory.FileBasedChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LoveAppDocumentLoader {
    private final ResourcePatternResolver resourcePatternResolver;

    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }
    public List<Document>loadMarkdowns() throws IOException {
        List<Document>allDocuments=new ArrayList<>();
        Resource[]resources=resourcePatternResolver.getResources("classpath:document/*.md");
        for(Resource resource:resources){
            String fileName=resource.getFilename();
            //提取文档倒数第3和第2个字作为标签
            String status=fileName.substring(fileName.length()-6,fileName.length()-4);
            MarkdownDocumentReaderConfig config=MarkdownDocumentReaderConfig.builder()
                    .withHorizontalRuleCreateDocument(true)
                    .withIncludeCodeBlock(false)
                    .withIncludeBlockquote(false)
                    .withAdditionalMetadata("filename",fileName)
                    .withAdditionalMetadata("status",status)
                    .build();
            MarkdownDocumentReader reader=new MarkdownDocumentReader(resource,config);
            allDocuments.addAll(reader.get());
        }
        return allDocuments;
    }
}




