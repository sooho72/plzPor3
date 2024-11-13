package com.lyj.securitydomo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)  // AuditingEntityListener로 수정
@Getter
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "createdAt", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdAt; // 등록 날짜 (처음 저장될 때 자동 설정)

    @LastModifiedDate
    @Column(name = "upDatedAt")
    private Date upDatedAt; // 수정 날짜 (수정 시 자동으로 업데이트됨)
}