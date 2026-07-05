package com.likelion_collb.global;

import com.likelion_collb.global.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 배포 확인용 임시 컨트롤러. 이후 정리 예정.
 */
@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public BaseResponse<String> health() {
        return BaseResponse.success("ok");
    }
}
