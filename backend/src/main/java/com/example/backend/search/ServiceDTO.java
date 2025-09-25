package com.example.backend.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDTO {
    private String serviceName; // 프론트에서 읽을 필드

    // 기본 생성자
    public ServiceDTO() {}

    // ✅ ServiceEntity를 DTO로 변환하는 생성자
    public ServiceDTO(ServiceEntity serviceEntity) {
        this.serviceName = serviceEntity.getServiceName(); // 엔티티의 서비스 이름 필드
    }
}