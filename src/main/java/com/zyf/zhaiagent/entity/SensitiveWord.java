package com.zyf.zhaiagent.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SensitiveWord {
    private Long id;
    private String word;
    private String category;
    private Integer severity;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}